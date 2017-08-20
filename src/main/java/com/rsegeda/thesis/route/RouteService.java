package com.rsegeda.thesis.route;

import java.util.Optional;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
public interface RouteService {

    Optional<RouteDto> getRoute(String id);

    Optional<RouteDto> saveRoute(RouteDto routeDto);
}
