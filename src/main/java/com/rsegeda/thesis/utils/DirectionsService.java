package com.rsegeda.thesis.utils;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Roman Segeda on 02/09/2017.
 */
@Slf4j
@Component
public class DirectionsService {

    private GeoApiContext geoApiContext;

    public DirectionsService() {
        this.geoApiContext = new GeoApiContext().setApiKey("AIzaSyBmonDj8j48U0snyCoRn8SgnqMOc7t6QtA");
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
