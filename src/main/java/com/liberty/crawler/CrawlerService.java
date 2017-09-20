package com.liberty.crawler;

import com.google.common.collect.Lists;
import com.liberty.common.RequestHelper;
import com.liberty.model.GenericTrack;
import com.liberty.model.MbArtist;
import com.liberty.model.PleerArtist;
import com.liberty.model.PleerTrack;
import com.liberty.repository.GenericTrackRepository;
import com.liberty.repository.MbArtistRepository;
import com.liberty.repository.PleerArtistRepository;
import com.liberty.repository.PleerTrackRepository;
import com.liberty.service.TrackLinker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
@Slf4j
@Service
public class CrawlerService {
    @Autowired
    private PleerTrackRepository songRepository;
    @Autowired
    private PleerArtistRepository pleerArtistRepository;
    @Autowired
    private GenericTrackRepository trackRepository;
    @Autowired
    private MbArtistRepository mbArtistRepository;

    @Autowired
    private TrackLinker trackLinker;
    //    @Autowired
    //    private FlickrCrawler flickrCrawler;
    //    private CrawlController controller;
    private String toSearch = "йцукенгшщзхъэждлорпавыфячсмитьбюёqwertyuiopasdfghjklzxcvbnm";
    private String ARTIST_SEARCH_URL = "http://pleer.net/artists/%s/0/0?rand=%s";
    private String SONG_SEARCH_URL = "http://pleer.net/search?page=%s&q=artist:%s&rand=%s";

    //
    //    @Autowired
    //    private CrawlerFactory crawlerFactory;
    //
    //    public void run() throws Exception {
    //        String crawlStorageFolder = "D://download/dump";
    //        int numberOfCrawlers = 1;
    //
    //        CrawlConfig config = new CrawlConfig();
    //        config.setPolitenessDelay(1000);
    //        config.setCrawlStorageFolder(crawlStorageFolder);
    //        /*
    //         * Instantiate the controller for this crawl.
    //         */
    //        PageFetcher pageFetcher = new PageFetcher(config);
    //        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    //        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    //        controller = new CrawlController(config, pageFetcher, robotstxtServer);
    //
    //        controller.addSeed("http://pleer.net/artists");
    //        controller.addSeed("http://pleer.net/search?q=artist%3AАрия");
    //        controller.addSeed("http://pleer.net/tracks/5112302npj5");
    //
    //        controller.startNonBlocking(crawlerFactory, numberOfCrawlers);
    //    }
    //
    public void crawlArtists() throws InterruptedException {
        List<PleerArtist> all = pleerArtistRepository.findAll();
        Set<String> crawled = new HashSet<>();
        if (all != null) {
            all.forEach(a -> crawled.add(a.getPleerArtistName()));
        }
        char[] chars = toSearch.toCharArray();
        Random random = new Random();
        for (char symbol : chars) {
            String encoded = URLEncoder.encode("" + symbol);
            float r = random.nextFloat();
            String url = String.format(ARTIST_SEARCH_URL, encoded, r);
            String result = RequestHelper.executeRequestAndGetResult(url);
            String[] split = result.split("artist:");
            List<String> artists = new ArrayList<>();
            if (split.length > 1) {
                for (int i = 1; i < split.length; i++) {
                    artists.add(split[i].substring(0, split[i].indexOf("\"") - 1));
                }
            }
            if (!artists.isEmpty()) {
                artists.forEach(a -> {
                    if (!crawled.contains(a)) {
                        // pleerArtistRepository.save(new PleerArtist(new ObjectId(), a));
                        crawled.add(a);
                    } else {
                        log.warn("{} Artist already crawled", a);
                    }
                });
                log.error("Stored {} artists for : {}", artists.size(), symbol);
            } else {
                log.error("Not found artists for : " + symbol);
            }
            Thread.sleep(1000);
        }
    }


    public void updateStreamLinks() {
        List<GenericTrack> tracks = trackRepository.findAll();
        Map<String, GenericTrack> genericTrackMap = tracks.stream().collect(Collectors.toMap(GenericTrack::getMbid, identity()));
        List<String> artistMbids = tracks.stream().map(GenericTrack::getArtistMbib).collect(Collectors.toList());
        ArrayList<MbArtist> artists = Lists.newArrayList(mbArtistRepository.findAll(artistMbids));
        log.info("Found {} artists in database", artists.size());
        AtomicInteger counter = new AtomicInteger(0);
        Map<String, List<PleerTrack>> pleerArtistTracks = new HashMap<>();
        artists.forEach(a -> {
            List<PleerTrack> pleerTracks = findPleerTracks(a.getName());
            pleerArtistTracks.put(a.getMbid(), pleerTracks);
            log.info("Fetched {}/{} artists", counter.incrementAndGet(), artists.size());
        });
        trackLinker.relinkTracks(genericTrackMap, pleerArtistTracks);
    }

    //
    //todo: not crawled from 4650 / 5979 (id=591f305e31d8a63a14d4c127)
    public void crawlSongs(String fromId) {
        List<PleerArtist> artists = pleerArtistRepository.findAll();
        int fromIndex = 0;
        if (fromId != null) {
            for (int i = 0; i < artists.size(); i++) {
                if (artists.get(i).getId().toString().equals(fromId)) {
                    fromIndex = i;
                    log.info("Starting crawl from index {} / {}", fromIndex, artists.size());
                }
            }
        }
        Random random = new Random();
        for (int i = fromIndex; i < artists.size(); i++) {
            boolean shouldStop = false;
            AtomicInteger counter = new AtomicInteger(0);
            PleerArtist artist = artists.get(i);
            while (!shouldStop) {
                String name = artist.getPleerArtistName();
                String url = String.format(SONG_SEARCH_URL, i, URLEncoder.encode(name), random.nextFloat());
                String result = RequestHelper.executeRequestAndGetResult(url);
                String[] split = result.split("<li ");
                List<String> tags = new ArrayList<>();
                for (String s : split) {
                    if (s.startsWith("duration")) {
                        tags.add(s.substring(0, s.indexOf(">") - 1));
                    }
                }

                tags.forEach(tag -> {
                    PleerTrack toSave = getSong(tag);
                    songRepository.save(toSave);
                    counter.incrementAndGet();
                });
                System.out.println("Stored " + tags.size() + " songs");
                if (tags.size() < 20) {
                    shouldStop = true;
                }
                i++;
                delay(250);
            }
            System.out.println("Stored " + counter.get() + " songs for artist : " + artist);
            log.info("Crawled songs for {} / {} artists ", i, artists.size());
        }

    }

    private List<PleerTrack> findPleerTracks(String artistName) {
        boolean shouldStop = false;
        List<PleerTrack> tracks = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);
        int page = 0;
        Random random = new Random();
        while (!shouldStop) {
            String url = String
                .format(SONG_SEARCH_URL, page, URLEncoder.encode(artistName.toLowerCase()), random.nextFloat());
            String result = RequestHelper.executeRequestAndGetResult(url);
            String[] split = result.split("<li ");
            List<String> tags = new ArrayList<>();
            for (String s : split) {
                if (s.startsWith("duration")) {
                    tags.add(s.substring(0, s.indexOf(">") - 1));
                }
            }

            tags.forEach(tag -> {
                PleerTrack toSave = getSong(tag);
                tracks.add(toSave);
                counter.incrementAndGet();
            });
            System.out.println("Fetched " + tags.size() + " tracks");
            System.out.println("Overall fetched " + tracks.size() + " tracks");
            if (tags.size() < 20) {
                shouldStop = true;
            }
            page++;
            delay(250);
        }
        System.out.println("Stored " + counter.get() + " songs for artist : " + artistName);
        return tracks;
    }

    private void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private PleerTrack getSong(String tag) {
        Integer duration = Integer.valueOf(findAttribute("duration", tag));
        String fileId = findAttribute("file_id", tag);
        String singer = findAttribute("singer", tag);
        String song = findAttribute("song", tag);
        String link = findAttribute("link", tag);
        String rate = findAttribute("rate", tag);
        String size = findAttribute("size", tag);
        String source = findAttribute("source", tag);
        return new PleerTrack(link, duration, fileId, singer, song, rate, size, source, 0f);
    }

    private String findAttribute(String attribute, String tag) {
        int from = tag.indexOf(attribute);
        String attr = tag.substring(from);
        int valueStartIndex = attr.indexOf("\"");
        int end = attr.indexOf("\"", valueStartIndex + 1);
        if (end == -1) {
            end = attr.indexOf("\\", valueStartIndex);
            return attr.substring(valueStartIndex + 1, end);
        }
        return attr.substring(valueStartIndex + 1, end - 1);
    }
    //
    //    public void crawlSongs() {
    //        crawlSongs(null);
    //    }
    //
    //
    //    public void crawlImages() {
    //        List<Artist> all = pleerArtistRepository.findAll();
    //        Collections.reverse(all);
    //        AtomicInteger counter = new AtomicInteger();
    //        all.parallelStream().forEach(artist -> {
    //            log.info("Trying to crawl images for {}", artist.getPleerArtistName());
    //            int count = flickrCrawler.crawlPhoto(artist.getPleerArtistName(), artist.getId());
    //            log.info("Stored {} photos for {}", count, artist.getPleerArtistName());
    //            log.info("Crawled images for {}/{} artists", counter.incrementAndGet(), all.size());
    //        });
    //    }
    //
    //
    //    public void parseGenres() throws IOException {
    //        ClassLoader classLoader = getClass().getClassLoader();
    //        File file = new File(classLoader.getResource("genres.html").getFile());
    //        Document document = Jsoup.parse(file, "UTF-8");
    //        Elements genreTags = document.select(".genre");
    //        List<Genre> genres = new ArrayList<>();
    //        genreTags.forEach(t -> {
    //            genres.add(parseSingle(t));
    //        });
    //        genreRepository.save(genres);
    //    }
    //
    //    private Genre parseSingle(Element tag) {
    //        Integer genreId = Integer.valueOf(tag.attr("data-genre-id"));
    //        String genreName = tag.select(".genre-txt").toString();
    //        int beginIndex = genreName.indexOf(">") + 1;
    //        genreName = genreName.substring(beginIndex, genreName.indexOf("<", beginIndex)).trim();
    //        String description = tag.select(".data-genre-descrb").first().text();
    //        Genre genre = new Genre();
    //        genre.setId(genreId);
    //        genre.setDescription(description);
    //        genre.setName(genreName);
    //        return genre;
    //    }
}
