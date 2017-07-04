package com.liberty.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

    private static final String DB_NAME = "accord-db";
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
