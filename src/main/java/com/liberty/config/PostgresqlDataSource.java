package com.liberty.config;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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

}
