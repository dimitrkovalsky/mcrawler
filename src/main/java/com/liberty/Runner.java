package com.liberty;

import com.liberty.crawler.ZaycevCrawler;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by dkovalskyi on 27.06.2017.
 */
@SpringBootApplication
public class Runner {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Runner.class).web(false).run(args);
        ZaycevCrawler crawlerService = context.getBean(ZaycevCrawler.class);
        crawlerService.crawlArtists();
    }
}
