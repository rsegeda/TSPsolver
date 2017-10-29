package com.rsegeda.thesis.location;

import com.vaadin.tapio.googlemaps.client.LatLon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Roman Segeda on 02/04/2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public final class LocationDto implements Cloneable {

    Long id;
    String placeId;
    String placeName;
    LatLon latLon;
    Integer index;

    public LocationDto clone() throws CloneNotSupportedException {
        return (LocationDto) super.clone();
    }
}


