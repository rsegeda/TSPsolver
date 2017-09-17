package com.rsegeda.thesis.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Roman Segeda on 17/09/2017.
 */
@ConfigurationProperties(prefix = "pathfinder")
public class Properties {

    @Getter
    public final Mongo mongo = new Mongo();

    @Data
    public static class Mongo {

        String uri;
        String port;
    }
}
