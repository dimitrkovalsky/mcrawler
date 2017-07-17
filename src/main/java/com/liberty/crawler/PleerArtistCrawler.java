package com.liberty.crawler;

import com.liberty.model.PleerTrack;
import com.liberty.repository.PleerTrackRepository;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
public class PleerArtistCrawler extends WebCrawler {
    private final PleerTrackRepository repository;
    private LinkFetcher linkFetcher = new LinkFetcher();
    private Set<String> fetched = new HashSet<>();

    public PleerArtistCrawler(PleerTrackRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return href.startsWith("http://pleer.net/tracks") || href.startsWith("http://pleer.net/en/tracks") || href.startsWith("http://pleer.net/search") ||
            href.startsWith("http://pleer.net/artists") || href.startsWith("http://pleer.net/en/artists");
    }


// Todo: use virtual scroll
    private void crawlArtist(Page page) {
        System.out.println("Crawl songs from artist page: " + page.getWebURL().getPath());
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            Document document = Jsoup.parse(html);

            Elements trackBlocks = document.select(".track-main.song");
            trackBlocks.forEach(block -> {
                Elements artist = block.select(".artist.inside");
                Elements track = block.select(".title.inside");
                if (artist.size() > 0 && track.size() > 0) {
                    String artistName = artist.first().text();
                    String trackName = track.first().text();
                    String trackRef = track.first().attr("href");
                    String[] split = trackRef.split("/");
                    String trackId = split[split.length - 1];
                    fetched.add(trackId);
                    //save(new Song(trackId, artistName, trackName, null));

                } else if (artist.size() > 0 && track.size() <= 0) {
                    System.err.println("Artist size more than tracks");
                } else if (artist.size() <= 0 && track.size() > 0) {
                    System.err.println("Tracks size more than artists");
                }
            });
            System.out.println("Stored " + trackBlocks.size() + " tracks");
        }
    }

    private void save(PleerTrack song) {
        repository.save(song);
    }

}
