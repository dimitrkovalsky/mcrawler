package com.liberty.config;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "com.liberty.jpa"
})
@EntityScan(basePackages = {
    "com.liberty.entity"
})
public class PostgresqlDataSource {
//    @Bean
//    DataSource dataSource() {
//        PGSimpleDataSource dataSourceConfig = new PGSimpleDataSource();
//        dataSourceConfig.setUrl("jdbc:postgresql://127.0.0.1:5432/musicbrainz");
//        dataSourceConfig.setUser("musicbrainz");
//        dataSourceConfig.setPassword("musicbrainz");
//
//        return dataSourceConfig;
//    }
}
