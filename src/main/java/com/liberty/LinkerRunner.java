package com.liberty;

import com.liberty.config.MongoConfiguration;
import com.liberty.config.PostgresqlDataSource;
import com.liberty.service.TrackLinker;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Created by dkovalskyi on 27.06.2017.
 */
@SpringBootApplication
@EnableCaching
@Import( {PostgresqlDataSource.class, MongoConfiguration.class})
public class LinkerRunner {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(LinkerRunner.class).web(false).run(args);
        TrackLinker linker = context.getBean(TrackLinker.class);
//        linker.linkTracks();
        linker.fetchPleerStreams();
    }
}
