package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.location.LocationDto;

import java.util.List;
import java.util.Map;

/**
 * Created by Roman Segeda on 20/08/2017.
 */
public interface Algorithm extends Runnable{

    int getProgress();

    void setProgress(int x);

    void start();

    Map<Long, Map<Long, Long>> prepareData(List<LocationDto> locationDtoList);

    List<LocationDto> compute();

    void run();

    void stop();
}
