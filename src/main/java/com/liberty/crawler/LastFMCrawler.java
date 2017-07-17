package com.liberty.crawler;

import com.liberty.model.ArtistData;
import com.liberty.model.MbAlbum;
import com.liberty.model.MbArtist;
import com.liberty.model.SimilarArtist;
import com.liberty.repository.MbAlbumRepository;
import com.liberty.repository.MbArtistRepository;
import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author dkovalskyi
 * @since 22.05.2017
 */
@Component
@Slf4j
public class LastFMCrawler {
    public static final int DELAY = 300;
    private static String API_KEY = "a586b0293c79d3b089b94099ea3944e2";
    private static String SHARED_SECRET = "11df3173137f5cde73d6b8260ae50c31";
    private static String USER = "dimitrkovalsky";
    private static String APPLICATION_NAME = "test";

    @Autowired
    private MbArtistRepository artistRepository;
    @Autowired
    private MbAlbumRepository albumRepository;

    private void crawl(Consumer<MbArtist> crawlOperation, long delay) {
        List<MbArtist> all = artistRepository.findAll();
        Collections.reverse(all);
        AtomicInteger counter = new AtomicInteger();
        all.forEach(a -> {
            crawlOperation.accept(a);
            try {
                Thread.sleep(delay);
            }
            catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            log.info("Fetched {} / {} artist from lastfm", counter.incrementAndGet(), all.size());
        });
    }

    private void fetchArtistData(MbArtist artist) {
        String artistName = artist.getName();
        Locale locale;
        if (Pattern.matches(".*\\p{InCyrillic}.*", artistName)) {
            locale = new Locale("ru");
            System.out.println("Used russian language for artist: " + artistName);
        }
        else {
            locale = new Locale("en");
            System.out.println("Used English language for artist: " + artistName);
        }

        Artist info = Artist.getInfo(artist.getMbid(), locale, "test", API_KEY);
        if (info == null) {
            log.warn("Could not find {} artist on lastfm", artistName);
            return;
        }
        ArtistData data = toInternalArtist(artist, info);
        Collection<Artist> similar = info.getSimilar();
        data.setSimilar(toSimilarInternal(similar));
        artist.setData(data);
        artistRepository.save(artist);
    }

    private List<SimilarArtist> toSimilarInternal(Collection<Artist> similar) {
        return similar.stream().map(sa -> {
            SimilarArtist artist = new SimilarArtist();
            artist.setName(sa.getName());
            artist.setMbid(sa.getMbid());
            artist.setMatch(sa.getSimilarityMatch());
            saveImages(sa, artist.getImages());
            return artist;
        }).collect(Collectors.toList());
    }

    private ArtistData toInternalArtist(MbArtist artist, Artist info) {
        ArtistData data = new ArtistData();
        data.setMbid(info.getMbid());
        data.setTags(new HashSet<>(info.getTags()));
        data.setWikiDescpiption(cleanText(info.getWikiText()));
        data.setWikiSummary(cleanText(info.getWikiSummary()));
        data.setName(artist.getName());
        data.setFullName(info.getName());
        saveImages(info, data.getImages());
        return data;
    }

    private void saveImages(Artist source, Map<ImageSize, String> toStore) {
        for (ImageSize imageSize : ImageSize.values()) {
            String imageURL = source.getImageURL(imageSize);
            if (imageURL != null) {
                toStore.put(imageSize, imageURL);
            }
        }
    }

    public void crawlArtistImages() {
        crawl(this::fetchArtistData, DELAY);
    }

    public void crawlAlbumImages() {
        List<MbAlbum> all = albumRepository.findAll();
        AtomicInteger counter = new AtomicInteger();
        all.forEach(a -> {
            crawlAlbums(a);
            try {
                Thread.sleep(DELAY);
            }
            catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            log.info("Fetched {} / {} albums from lastfm", counter.incrementAndGet(), all.size());
        });
    }

    private void crawlAlbums(MbAlbum album) {
        Album info = Album.getInfo(album.getArtistMbib(), album.getMbid(), API_KEY);
        if (info == null) {
            System.err.println("Can not find albums for : " + album.getName());
            return;
        }
        System.out.println("Fetched album for : " + info.getArtist() + " " + album.getName());
        if (info.getTags() != null)
            album.setTags(new HashSet<>(info.getTags()));
        album.setWikiSummary(info.getWikiSummary());
        album.setWikiText(info.getWikiText());
        album.setReleaseDate(info.getReleaseDate());
        saveImages(album, info);
        albumRepository.save(album);
    }

    private void saveImages(MbAlbum album, Album retrieved) {
        for (ImageSize imageSize : ImageSize.values()) {
            String imageURL = retrieved.getImageURL(imageSize);
            if (imageURL != null) {
                album.getImages().put(imageSize, imageURL);
            }
        }
    }

    private String cleanText(String wikiText) {
        if (StringUtils.isEmpty(wikiText))
            return "";
        int index = wikiText.indexOf("<a href=\"https://www.last");
        if (index != -1)
            return wikiText.substring(0, index);
        return wikiText;
    }

}
