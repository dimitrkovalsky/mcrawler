package com.liberty.crawler;

import com.liberty.repository.PleerTrackRepository;
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

    @Autowired
    private PleerTrackRepository repository;

    public CrawlerFactory( PleerTrackRepository repository) {
        this.repository = repository;
    }

    @Override
    public WebCrawler newInstance() {
        return new PleerCrawler(repository);
    }
}