package com.liberty;

import com.liberty.crawler.LastFMCrawler;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by dkovalskyi on 27.06.2017.
 */
@SpringBootApplication
public class LastFmRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(LastFmRunner.class).web(false).run(args);
        LastFMCrawler crawler = context.getBean(LastFMCrawler.class);
//        crawler.crawlArtistImages();
        crawler.crawlAlbumImages();
    }
}
