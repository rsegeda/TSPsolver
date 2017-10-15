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

/**
 * Created by Roman Segeda on 25/08/2017.
 * <p>
 * This is the mock-up - default implementation of TSP algorithm.
 */
@Slf4j
public class TspAlgorithm implements Algorithm {

    private final JmsTemplate jmsTemplate;
    private final DirectionsService directionsService;
    Selection selection;
    Settings settings;
    int optimalDistance = Integer.MAX_VALUE;
    List<Integer> optimalPath;
    private boolean stopAlgorithm = false;

    @Getter
    public Thread thread;
    @Getter
    public int progress = 0;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    public TspAlgorithm(Selection selection, Settings settings, JmsTemplate jmsTemplate, DirectionsService
            directionsService) {
        this.selection = selection;
        this.settings = settings;
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
    public int[][] prepareData() {

        int size = selection.getLocationDtos().size();

        int[][] distances = new int[size][size];

        setProgress(0);
        selection.setProgress(progress);
        selection.setState(Constants.PREPARING_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        // Setup distances matrix
        for (int a = 0; a < size; a++) {
            for (int b = 0; b < size; b++) {
                if (a == b) {
                    distances[a][b] = 0;

                } else {
                    Long distance = directionsService.getDirection(selection.getLocationDtos().get(a).getPlaceName(),
                            selection.getLocationDtos().get(b).getPlaceName()).routes[0].legs[0].distance.inMeters;
                    distances[a][b] = distance.intValue();
                }
            }
            setProgress(a * 100 / selection.getLocationDtos().size());
            selection.setProgress(getProgress());
            jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");
        }

        return distances;
    }

    @Override
    public List<LocationDto> compute() {

        Integer sum = 0;

        List<LocationDto> locations = selection.getLocationDtos();

        selection.setDistanceStagesMap(new HashMap<>());

        for (int i = 0; i < locations.size(); i++) {
            int distOther;

            if (i == locations.size() - 1) {
                distOther = selection.getDistances()[i][0];
            } else {
                distOther = selection.getDistances()[i][i + 1];
            }

            sum += distOther;
            selection.getDistanceStagesMap().put(locations.get(i).getId(), distOther);

            selection.setProgress(i / locations.size());
        }

        optimalDistance = sum;
        return selection.getLocationDtos();
    }

    public void run() {

        selection.setDistances(prepareData());

        setProgress(0);
        selection.setProgress(getProgress());
        selection.setState(Constants.COMPUTING_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        optimalDistance = Integer.MAX_VALUE;
        optimalPath = new ArrayList<>();

        List<LocationDto> result = compute();

        selection.setResultList(result);
        selection.setResultDistance(optimalDistance);

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

    private void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }


}
