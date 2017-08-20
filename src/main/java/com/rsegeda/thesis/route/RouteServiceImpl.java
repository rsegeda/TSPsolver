package com.rsegeda.thesis.route;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Service
public class RouteServiceImpl implements RouteService {

    private RouteMapper routeMapper;
    private RouteRepository routeRepository;


    public RouteServiceImpl(RouteRepository routeRepository, RouteMapper routeMapper) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
    }

    @Override
    public Optional<RouteDto> getRoute(String id) {
        Optional<Route> locationDtoOptional = routeRepository.findById(id);
        return locationDtoOptional.map(route -> routeMapper.toDto(route));
    }

    @Override
    public Optional<RouteDto> saveRoute(RouteDto routeDto) {
        return null;
    }
}
