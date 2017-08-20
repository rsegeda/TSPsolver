package com.rsegeda.thesis.route;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Component
@Mapper(componentModel = "spring")
public interface RouteMapper extends GenericMapper<RouteDto, Route> {

}
