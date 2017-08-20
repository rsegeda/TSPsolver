package com.rsegeda.thesis.route;

import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Repository
public interface RouteRepository
        extends org.springframework.data.repository.Repository<Route, String> {

    Optional<Route> findById(String id);
}
