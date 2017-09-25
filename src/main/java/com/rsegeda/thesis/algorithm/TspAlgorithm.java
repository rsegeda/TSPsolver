package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Roman Segeda on 25/08/2017.
 */
@Slf4j
public class TspAlgorithm implements Algorithm {

    private final Selection selection;
    private final JmsTemplate jmsTemplate;
    private final DirectionsService directionsService;
    @Getter
    public Thread thread;
    @Getter
    public int progress = 0;
    boolean stopAlgorithm = false;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
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
    public Map<Long, Map<Long, Long>> prepareData(List<LocationDto> locationDtoList) {
        setProgress(0);
        selection.setProgress(progress);
        selection.setState(Constants.PREPARING_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        Map<Long, Map<Long, Long>> distancesMap;

        distancesMap = new HashMap<>();

        for (int i = 0; i < locationDtoList.size(); i++) {

            LocationDto locationDto = locationDtoList.get(i);

            List<LocationDto> others = new ArrayList<>();
            others.addAll(locationDtoList);
            others.remove(locationDto);

            Map<Long, Long> map = new HashMap<>();

            for (LocationDto other : others) {

                Long distance = directionsService.getDirection(locationDto.getPlaceName(),
                        other.getPlaceName()).routes[0].legs[0].distance.inMeters;
                map.put(other.getId(), distance);
            }

            distancesMap.put(locationDto.getId(), map);

            setProgress(i * 100 / locationDtoList.size());
            selection.setProgress(getProgress());
            jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");
        }

        return distancesMap;
    }

    @Override
    public List<LocationDto> compute() {
        setProgress(0);
        selection.setProgress(getProgress());
        selection.setState(Constants.COMPUTING_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        Long sum = 0L;

        List<LocationDto> locations = selection.getLocationDtos();

        selection.setDistanceStagesMap(new HashMap<>());

        for (int i = 0; i < locations.size(); i++) {
            Long distOther;

            if (i == locations.size() - 1) {
                distOther = selection.getDistancesMap().get(locations.get(i).getId()).get(locations.get(0).getId());
            } else {
                distOther = selection.getDistancesMap().get(locations.get(i).getId()).get(locations.get(i + 1).getId());
            }

            sum += distOther;
            selection.getDistanceStagesMap().put(locations.get(i).getId(), distOther);

            selection.setProgress(i / locations.size());
        }

        selection.setResultDistance(sum);

        selection.setResultList(selection.getLocationDtos());
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
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        int index = 1;
        for (LocationDto locationDto : result) {
            locationDto.setIndex(index);
            index++;
        }

        selection.setResultList(result);
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
