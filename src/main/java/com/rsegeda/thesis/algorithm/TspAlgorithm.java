package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import javafx.util.Pair;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 25/08/2017.
 */
@Slf4j
public class TspAlgorithm implements Algorithm {

    public final Selection selection;
    public final JmsTemplate jmsTemplate;
    public final DirectionsService directionsService;
    @Getter
    public Thread thread;
    @Getter
    public int progress = 0;
    public boolean stopAlgorithm = false;

    @Autowired
    public TspAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService directionsService) {
        this.selection = selection;
        this.jmsTemplate = jmsTemplate;
        this.directionsService = directionsService;
    }

    public void setProgress(int x) {
        this.progress = x;
    }

    public void start() {
        progress = 0;
        thread = new Thread(this);
        thread.start();
        log.info("Algorithm started");
    }

    @Override
    public Map<Pair<LocationDto, LocationDto>, Long> prepareData(List<LocationDto> locationDtoList) {
        setProgress(0);
        selection.setProgress(progress);
        selection.setState(Constants.PREPARING_STATE);
        jmsTemplate.convertAndSend("stateUpdate", "");

        Map<Pair<LocationDto, LocationDto>, Long> distancesMap;

        distancesMap = new HashMap<>();

        for (int i = 0; i < locationDtoList.size(); i++) {

            LocationDto locationDto = locationDtoList.get(i);

            List<LocationDto> others = new ArrayList<>();
            others.addAll(locationDtoList);
            others.remove(locationDto);

            for (LocationDto other : others) {

                Long distance = directionsService.getDirection(locationDto.getPlaceName(), other.getPlaceName()).routes[0].legs[0].distance.inMeters;
                distancesMap.put(new Pair<>(locationDto, other), distance);
            }
            setProgress(i * 100 / locationDtoList.size());
            selection.setProgress(getProgress());
            jmsTemplate.convertAndSend("stateUpdate", "");
        }

        return distancesMap;
    }

    @Override
    public List<LocationDto> compute() {
        setProgress(0);
        selection.setProgress(getProgress());
        selection.setState(Constants.COMPUTING_STATE);
        jmsTemplate.convertAndSend("stateUpdate", "");

        while (progress < 100 && !stopAlgorithm) {

            setProgress(getProgress() + 10);
            selection.setProgress(getProgress());

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { log.error("Cannot call sleep on thread.", e); }
        }
        selection.setResult(selection.getLocationDtos());
        return selection.getLocationDtos();
    }

    public void run() {

        List<LocationDto> locationDtoList = new ArrayList<>();
        locationDtoList.addAll(selection.getLocationDtos());

        selection.setDistancesMap(prepareData(locationDtoList));

        List<LocationDto> result = compute();

        setProgress(100);
        selection.setProgress(getProgress());
        selection.setState(Constants.READY_STATE);
        jmsTemplate.convertAndSend("stateUpdate", "");

        int index = 1;
        for (LocationDto locationDto : result) {
            locationDto.setIndex(index);
            index++;
        }
        selection.setResult(result);
        jmsTemplate.convertAndSend("algorithmResult", "");
    }

    @Override
    public void stop() {
        setStopAlgorithm(true);
    }

    public void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }


}
