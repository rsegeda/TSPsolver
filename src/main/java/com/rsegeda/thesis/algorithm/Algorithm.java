package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.location.LocationDto;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 20/08/2017.
 */
public interface Algorithm extends Runnable{

    int getProgress();

    void setProgress(int x);

    Thread getThread();

    void start();

    Map<Pair<LocationDto, LocationDto>, Long> prepareData(List<LocationDto> locationDtoList);

    List<LocationDto> compute();

    void run();

    void stop();
}
