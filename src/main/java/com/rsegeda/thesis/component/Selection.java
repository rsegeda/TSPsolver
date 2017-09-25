package com.rsegeda.thesis.component;

import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Roman Segeda on 26/08/2017.
 */
@Data
@Component
public class Selection {

    private String state = Constants.READY_STATE;
    private Integer progress = 0;

    private String algorithmName;

    private List<LocationDto> locationDtos;

    private List<LocationDto> resultList;

    private Map<Long, Map<Long, Long>> distancesMap;

    private Long resultDistance;

    private Map<Long, Long> distanceStagesMap;

}
