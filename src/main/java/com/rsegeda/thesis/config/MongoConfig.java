package com.rsegeda.thesis.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Configuration
@EnableMongoRepositories(basePackages = {"com.rsegeda.thesis.location", "com.rsegeda.thesis.route"})
public class MongoConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "pathfinder";
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient("127.0.0.1", 27017);
    }

}
