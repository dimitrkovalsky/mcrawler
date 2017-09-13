package com.liberty;

import com.liberty.crawler.CrawlerFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by dkovalskyi on 27.06.2017.
 */
@SpringBootApplication
public class PleerRunner {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(PleerRunner.class).web(false).run(args);
        CrawlerFactory crawlerService = context.getBean(CrawlerFactory.class);

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder("D:\\dump\\crawl");
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("http://pleer.net/artists");
        controller.startNonBlocking(crawlerService, 1);
    }
}
