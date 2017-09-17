package com.rsegeda.thesis.location;

import java.util.List;
import java.util.Optional;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
public interface LocationService {

    List<LocationDto> getLocationList();

    Optional<LocationDto> getLocation(Long id);

    @SuppressWarnings("unused")
    Optional<LocationDto> getLocationByPlaceId(String placeId);

    @SuppressWarnings("UnusedReturnValue")
    Optional<LocationDto> saveLocation(LocationDto routeDto);

    void deleteLocation(Long id);
}
