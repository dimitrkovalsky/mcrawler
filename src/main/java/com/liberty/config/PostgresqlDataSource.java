package com.liberty.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "com.liberty.jpa"
})
public class PostgresqlDataSource {

}
