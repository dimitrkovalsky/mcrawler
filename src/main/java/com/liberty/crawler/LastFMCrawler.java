package com.liberty.crawler;

import com.liberty.jpa.ArtistRepository;
import com.liberty.model.ArtistData;
import com.liberty.model.MbAlbum;
import com.liberty.model.MbArtist;
import com.liberty.model.SimilarArtist;
import de.umass.lastfm.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author dkovalskyi
 * @since 22.05.2017
 */
@Component
@Slf4j
public class LastFMCrawler {
    public static final int DELAY = 300;
    @Autowired
    private ArtistRepository artistRepository;

    private static String API_KEY = "a586b0293c79d3b089b94099ea3944e2";
    private static String SHARED_SECRET = "11df3173137f5cde73d6b8260ae50c31";
    private static String USER = "dimitrkovalsky";
    private static String APPLICATION_NAME = "test";

    private void crawl(Consumer<com.liberty.accord.model.Artist> crawlOperation, long delay) {
        List<com.liberty.accord.model.Artist> all = artistRepository.findAll();
        Collections.reverse(all);
        AtomicInteger counter = new AtomicInteger();
        all.forEach(a -> {
            crawlOperation.accept(a);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            log.info("Fetched {} / {} artist from lastfm", counter.incrementAndGet(), all.size());
        });
    }


      private void saveImages(Artist source, Map<ImageSize, String> toStore) {
        for (ImageSize imageSize : ImageSize.values()) {
            String imageURL = source.getImageURL(imageSize);
            if (imageURL != null) {
                toStore.put(imageSize, imageURL);
            }
        }
    }

    private void getAlbums(MbArtist artist) {
        Artist info = Artist.getInfo(artist.getMbid(), API_KEY);
    }

    private com.liberty.accord.model.Album toAlbum(ArtistData data, Album fromLastFm) {
        com.liberty.accord.model.Album album = new com.liberty.accord.model.Album(new ObjectId(), data.getId().toString(), fromLastFm.getArtist());
        Collection<Track> tracks = fromLastFm.getTracks();
        if (!CollectionUtils.isEmpty(tracks)) {
            System.out.println(tracks);
        }
        if (fromLastFm.getId() != null) {
            System.out.printf(fromLastFm.getId());
        }
        album.setName(fromLastFm.getName());
        Collection<String> tags = fromLastFm.getTags();
        album.getTags().addAll(tags);
        album.setWikiSummary(fromLastFm.getWikiSummary());
        album.setWikiText(fromLastFm.getWikiText());
        saveImages(album, fromLastFm);
        album.setReleaseDate(fromLastFm.getReleaseDate());
        return album;
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

    public static void main(String[] args) {
        LastFMCrawler crawler = new LastFMCrawler();
    }
}
