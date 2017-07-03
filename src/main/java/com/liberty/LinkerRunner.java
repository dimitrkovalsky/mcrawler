package com.liberty;

import com.liberty.crawler.ZaycevCrawler;
import com.liberty.service.TrackLinker;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by dkovalskyi on 27.06.2017.
 */
@SpringBootApplication
public class LinkerRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(LinkerRunner.class).web(false).run(args);
        TrackLinker linker = context.getBean(TrackLinker.class);
        linker.linkTracks();
    }
}
