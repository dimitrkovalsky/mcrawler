package com.liberty.config;

import com.liberty.soundcloud.repo.SoundCloudRepository;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
@Configuration
//@EnableMongoRepositories(basePackages = "com.liberty.repository")
public class MongoConfiguration extends AbstractMongoConfiguration {

    private static final String DB_NAME = "accord-db2";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 27017;

    @Override
    protected String getDatabaseName() {
        return DB_NAME;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(HOST, PORT);
    }
}
