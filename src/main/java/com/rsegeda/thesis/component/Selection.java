package com.rsegeda.thesis.component;

import com.rsegeda.thesis.location.LocationDto;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 26/08/2017.
 */
@Data
@Component
public class Selection {

    private Integer value = 0;

    private String algorithmName;

    private List<LocationDto> locationDtos;

}
