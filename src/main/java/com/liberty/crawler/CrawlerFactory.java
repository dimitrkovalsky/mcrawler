package com.liberty.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
@Component
public class CrawlerFactory implements CrawlController.WebCrawlerFactory {

//    @Autowired
//    private SongRepository repository;

//    public CrawlerFactory( SongRepository repository) {
//        this.repository = repository;
//    }

    @Override
    public WebCrawler newInstance() {
        return null;
//        return new PleerCrawler(repository);
    }
}