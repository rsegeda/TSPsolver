package com.rsegeda.thesis.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Configuration
@EnableMongoRepositories(basePackages = {"com.rsegeda.thesis.location", "com.rsegeda.thesis.route"})
@EnableConfigurationProperties({Properties.class})
public class MongoConfig extends AbstractMongoConfiguration {

    private final Properties properties;

    @Autowired
    public MongoConfig(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected String getDatabaseName() {
        return "pathfinder";
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(properties.getMongo().getUri(), Integer.parseInt(properties.getMongo().getPort()));
    }

}
