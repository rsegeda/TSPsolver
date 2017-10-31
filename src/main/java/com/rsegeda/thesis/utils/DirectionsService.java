package com.rsegeda.thesis.utils;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.rsegeda.thesis.config.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Roman Segeda on 02/09/2017.
 */
@Slf4j
@Component
public class DirectionsService {

    private GeoApiContext geoApiContext;

    @Autowired
    public DirectionsService(Properties properties) {
        this.geoApiContext = new GeoApiContext().setApiKey(properties.getApiKey());
    }

    public DirectionsResult getDirection(String a, String b) {

        DirectionsResult directionsResult = null;
        try {
            directionsResult = DirectionsApi.getDirections(geoApiContext, a, b)
                    .mode(TravelMode.DRIVING)
                    .units(Unit.METRIC)
                    .region("pl")
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            log.error("DirectionsService: ", e);
        }

        return directionsResult;
    }
}
