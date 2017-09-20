package com.liberty;

import com.liberty.crawler.CrawlerService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by dkovalskyi on 27.06.2017.
 */
@SpringBootApplication
public class PleerRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(PleerRunner.class).web(false).run(args);
        CrawlerService crawlerService = context.getBean(CrawlerService.class);
        crawlerService.updateStreamLinks();
    //    crawlerService.crawlArtistSongs();
    }
}
