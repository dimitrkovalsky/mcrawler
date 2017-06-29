package com.liberty.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.common.RequestHelper;
import com.liberty.model.ZaycevArtist;
import com.liberty.model.ZaycevTrack;
import com.liberty.repository.ZaycevArtistRepository;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by dkovalskyi on 27.06.2017.
 */
@Component
public class ZaycevCrawler {
    private String ARTIST_SONGS_URL = "http://zaycev.net/artist/%s?page=%s";
    private String STREAM_LINK_URL = "http://zaycev.net%s";
    private String ARTIST_LETTER_LINK_URL = "http://zaycev.net/artist/letter-%s-more.html?page=%s&_=%s";
    private String ARTIST_RUS_LETTER_LINK_URL = "http://zaycev.net/artist/letter-rus-%s-more.html?page=%s&_=%s";
    private String ENGLISH_LETTERS = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
    private String RUSSIAN_LETTERS = "a b v g d e zh z i k l m n o p r s t u f h c ch sh sch ee yu ya";

    @Autowired
    private ZaycevArtistRepository artistRepository;

    private Map<Long, ZaycevTrack> crawlArtistSongs(long artistId, String artistName) {
        Map<Long, ZaycevTrack> allLinks = new HashMap<>();
        int page = 1;
        boolean completed = false;
        while (!completed) {
            Map<Long, ZaycevTrack> retrieved = toMap(crawlArtistSongs(artistId, artistName, page));

            if (retrieved.size() < 50 || !areNewElements(retrieved, allLinks)) {
                completed = true;
            }
            allLinks.putAll(retrieved);
            page++;
        }
        System.out.println(String.format("Fetched %s raw songs for %s", allLinks.size(), artistName));
        allLinks.values().parallelStream().forEach(l -> {
            String streamLink = getStreamLink(l);
            l.setStreamLink(streamLink);
        });
        System.out.println(String.format("Fetched %s stream links for %s", allLinks.size(), artistName));
        return allLinks;
    }

    private String getStreamLink(ZaycevTrack link) {
        String url = null;
        try {
            String result = RequestHelper.executeRequestAndGetResult(String.format(STREAM_LINK_URL, link.getDataUrl()));
            ObjectMapper mapper = new ObjectMapper();
            TrackUrl trackUrl = mapper.readValue(result, TrackUrl.class);
            url = trackUrl.url;
            System.out.println(url);
        } catch (Exception e) {
            System.err.println("Can not find stream for : " + link.getTrackName());
        }
        return url;
    }

    private <T> boolean areNewElements(Map<Long, T> retrieved, Map<Long, T> allLinks) {
        return retrieved.keySet().stream().anyMatch(id -> !allLinks.containsKey(id));
    }

    private Map<Long, ZaycevTrack> toMap(List<ZaycevTrack> zaycevTracks) {
        Map<Long, ZaycevTrack> allLinks = new HashMap<>();
        zaycevTracks.forEach(l -> allLinks.put(l.getZaycevId(), l));
        return allLinks;
    }

    private Map<Long, ZaycevArtist> toArtistMap(List<ZaycevArtist> trackLinks) {
        Map<Long, ZaycevArtist> allLinks = new HashMap<>();
        trackLinks.forEach(l -> allLinks.put(l.getZaycevArtistId(), l));
        return allLinks;
    }

    private List<ZaycevTrack> crawlArtistSongs(long artistId, String artistName, int page) {
        List<ZaycevTrack> zaycevTracks = new ArrayList<>();
        System.out.println(String.format("Trying crawl songs for %s page : %s", artistName, page));
        try {
            String url = String.format(ARTIST_SONGS_URL, artistId, page);
            String result = RequestHelper.executeRequestAndGetResult(url);
            Document document = Jsoup.parse(result);
            Elements elements = document.select(".musicset-track-list.musicset-autoplayed");
            Elements tags = elements.select("[data-url]");
            for (Element tag : tags) {
                ZaycevTrack link = new ZaycevTrack();
                link.setDataUrl(tag.attr("data-url"));
                link.setDuration(Integer.parseInt(tag.attr("data-duration")));
                link.setZaycevId(Long.parseLong(tag.attr("data-id")));
                link.setZaycevArtistId(artistId);
                link.setZaycevArtistName(artistName);
                String name = tag.select(".musicset-track__track-name").first().select("a").text();
                System.out.println(name);
                link.setTrackName(name);
                zaycevTracks.add(link);
            }
        } catch (Exception e) {
            System.err.println("Can not crawl data for " + artistName + " page: " + page);
        }
        return zaycevTracks;
    }

    public void crawlArtists() {
        crawlEnglishArtists();
        crawlRussianArtists();
    }

    private List<ZaycevArtist> crawlRussianArtists() {
        String[] letters = RUSSIAN_LETTERS.toLowerCase().split(" ");
        return crawlArtists(letters, true);
    }

    private List<ZaycevArtist> crawlEnglishArtists() {
        String[] letters = ENGLISH_LETTERS.toLowerCase().split(" ");
        return crawlArtists(letters, false);
    }

    private List<ZaycevArtist> crawlArtists(String[] letters, boolean russian) {
        List<ZaycevArtist> allArtists = new ArrayList<>();
        for (String letter : letters) {
            int page = 1;
            boolean completed = false;
            Map<Long, ZaycevArtist> allLinks = new HashMap<>();
            while (!completed) {
                Map<Long, ZaycevArtist> retrieved = toArtistMap(crawlArtist(letter, page, russian));

                if (retrieved.size() < 50 || !areNewElements(retrieved, allLinks)) {
                    completed = true;
                }
                allLinks.putAll(retrieved);
                page++;
            }
            Collection<ZaycevArtist> values = allLinks.values();
            artistRepository.save(values);
            System.out.println(String.format("Crawled %s artists for letter %s", values.size(), letter));
            allArtists.addAll(values);
            System.out.println(String.format("Crawled %s %s artists", allArtists.size(), russian ? "russian" : "english"));
        }

        return allArtists;
    }

    private List<ZaycevArtist> crawlArtist(String letter, int page, boolean rus) {
        List<ZaycevArtist> artists = new ArrayList<>();
        System.out.println(String.format("Trying crawl artists for %s letter and page : %s", letter, page));
        Random random = new Random();
        try {
            String url;
            if (rus) {
                url = String.format(ARTIST_RUS_LETTER_LINK_URL, letter, page, random.nextInt());
            } else {
                url = String.format(ARTIST_LETTER_LINK_URL, letter, page, random.nextInt());
            }
            String result = RequestHelper.executeRequestAndGetResult(url);
            System.out.println(result);
            Document document = Jsoup.parse(result);
            document.select(".artist-item__pic-link").forEach(e -> {
                String href = e.attr("href");
                Long artistId = Long.valueOf(href.substring(href.indexOf("/artist/") + 8));
                String name = e.child(0).attr("alt");
                System.out.println(e);
                artists.add(new ZaycevArtist(artistId, name));
            });

        } catch (Exception e) {
            System.err.println("Can not crawl data for letter " + letter + " page: " + page);
        }
        return artists;
    }

    @Data
    private static class TrackUrl {
        private String url;
    }

    public static void main(String[] args) {
        ZaycevCrawler crawler = new ZaycevCrawler();
//        Map<Long, TrackLink> trackLinks = crawler.crawlArtistSongs(120434, "");
//        System.out.println(trackLinks);
        crawler.crawlEnglishArtists();
    }
}
