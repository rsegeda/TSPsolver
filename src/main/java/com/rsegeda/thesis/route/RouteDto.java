package com.rsegeda.thesis.route;

import com.rsegeda.thesis.location.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
final class RouteDto {

    Long id;
    List<LocationDto> locations;
}
