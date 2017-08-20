package com.rsegeda.thesis.location;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Repository
public interface LocationRepository
        extends org.springframework.data.repository.Repository<Location, String> {

    List<Location> findAll();

    Optional<Location> findById(Long id);

    Optional<Location> findByPlaceId(String placeId);

    Optional<Location> save(Location location);

    void deleteById(Long id);


}
