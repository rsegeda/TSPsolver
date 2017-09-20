package com.rsegeda.thesis.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Roman Segeda on 17/09/2017.
 */
@Getter
@ConfigurationProperties(prefix = "pathfinder")
public class Properties {

    public final Mongo mongo = new Mongo();
    @Setter
    public String apiKey;

    @Data
    public static class Mongo {

        String uri;
        String port;
    }
}
