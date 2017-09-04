package com.rsegeda.thesis.component;

import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import javafx.util.Pair;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 26/08/2017.
 */
@Data
@Component
public class Selection {

    private String state = Constants.READY_STATE;
    private Integer progress = 0;

    private String algorithmName;

    private List<LocationDto> locationDtos;

    private List<LocationDto> result;

    private Map<Pair<LocationDto, LocationDto>, Long> distancesMap;

}
