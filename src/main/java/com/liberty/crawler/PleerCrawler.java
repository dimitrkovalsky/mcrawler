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
import java.util.Optional;
import java.util.Set;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
public class PleerCrawler extends WebCrawler {
    private final PleerTrackRepository repository;
    private LinkFetcher linkFetcher = new LinkFetcher();
    private Set<String> fetched = new HashSet<>();

    public PleerCrawler(PleerTrackRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return href.startsWith("http://pleer.net/tracks") || href.startsWith("http://pleer.net/en/tracks") || href.startsWith("http://pleer.net/search");
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);
        if (url.startsWith("http://pleer.net/search?q=artist")) {
            crawlArtist(page);
            return;
        }
        if (!url.startsWith("http://pleer.net/tracks") && !url.startsWith("http://pleer.net/en/tracks")) {
            return;
        }
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Document document = Jsoup.parse(html);
            Elements artist = document.select(".artist.inside");
            Elements track = document.select(".title.inside");
            if (artist.size() > 0 && track.size() > 0) {
                String artistName = artist.first().text();
                String trackName = track.first().text();
                String trackRef = track.first().attr("href");
                String[] split = trackRef.split("/");
                String trackId = split[split.length - 1];
                if (fetched.contains(trackId)) {
                    System.out.println("Track " + trackId + " was fetched before");
                    return;
                }
                System.out.println("Track id: " + trackId);
                System.out.println("Fetched artist: " + artistName);
                System.out.println("Fetched track: " + trackName);
                try {
                    Optional<String> link = linkFetcher.fetchLink(trackId);
                    System.out.println(link);
                    fetched.add(trackId);
                    if (link.isPresent()) {
                      //  save(new Song(trackId, artistName, trackName, link.get()));
                    } else {
                        System.err.println("Cannot fetch link");
                        //save(new Song(trackId, artistName, trackName, null));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (artist.size() > 0 && track.size() <= 0) {
                System.err.println("Artist size more than tracks");
            } else if (artist.size() <= 0 && track.size() > 0) {
                System.err.println("Tracks size more than artists");
            }
            //Set<WebURL> links = htmlParseData.getOutgoingUrls();
//            System.out.println("Text length: " + text.length());
//            System.out.println("Html length: " + html.length());
//            System.out.println("Number of outgoing links: " + links.size());
        }
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
//                    save(new Song(trackId, artistName, trackName, null));

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
