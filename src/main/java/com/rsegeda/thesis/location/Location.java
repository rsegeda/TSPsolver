package com.rsegeda.thesis.location;

import com.vaadin.tapio.googlemaps.client.LatLon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Location {

    @Id
    Long id;
    String placeId;
    String placeName;
    LatLon latLon;
}
