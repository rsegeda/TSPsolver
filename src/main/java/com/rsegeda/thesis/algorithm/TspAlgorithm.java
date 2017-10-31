package com.rsegeda.thesis.algorithm;

import com.google.maps.model.DirectionsResult;
import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Roman Segeda on 25/08/2017.
 * <p>
 * This is the mock-up - default implementation of TSP algorithm.
 * The path is build by rewriting input list of locations to the output and retrieving distances and durations
 * between them.
 */
@Slf4j
public class TspAlgorithm implements Algorithm {

    private final JmsTemplate jmsTemplate;
    private final DirectionsService directionsService;
    Selection selection;

    int[][] distancesArray;
    private int[][] durationsArray;

    int numberOfCities;
    int[] currentPath;

    int optimalDistance = Integer.MAX_VALUE;

    private Map<Integer, Integer> stagesMap;
    private Map<Integer, Integer> durationsMap;

    List<Integer> optimalPath;

    @Getter
    public Thread thread;

    @Getter
    public int progress = 0;
    private List<LocationDto> inputList;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    public TspAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService
            directionsService) {
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
    public void prepareData() {

        int[][] distances = new int[numberOfCities][numberOfCities];
        int[][] durations = new int[numberOfCities][numberOfCities];

        setProgress(0);
        selection.setProgress(progress);
        selection.setState(Constants.PREPARING_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        // Setup distances matrix
        for (int a = 0; a < numberOfCities; a++) {
            for (int b = 0; b < numberOfCities; b++) {
                if (a == b) {
                    distances[a][b] = 0;
                    durations[a][b] = 0;

                } else {
                    DirectionsResult directionsResult = directionsService.getDirection(inputList.get(a).getPlaceName(),
                            inputList.get(b).getPlaceName());
                    Long distance = directionsResult.routes[0].legs[0].distance.inMeters;
                    distances[a][b] = distance.intValue();
                    Long interval = directionsResult.routes[0].legs[0].duration.inSeconds;
                    durations[a][b] = interval.intValue();
                }
            }
            setProgress(a * 100 / this.numberOfCities);
            selection.setProgress(getProgress());
            jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");
        }

        distancesArray = distances;
        durationsArray = durations;
    }

    @Override
    public List<LocationDto> compute() {

        currentPath = getRandomPath();

        return getResult();
    }

    public void run() {

        inputList = selection.getInputList();
        numberOfCities = inputList.size();
        distancesArray = selection.getDistances();
        durationsArray = selection.getDurations();

        if (selection.getChanged()) {
            prepareData();
            selection.setChanged(false);
            selection.setDistances(distancesArray);
            selection.setDurations(durationsArray);
        }

        setProgress(0);
        selection.setProgress(getProgress());
        selection.setState(Constants.COMPUTING_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        optimalDistance = Integer.MAX_VALUE;
        optimalPath = new ArrayList<>();

        if (selection.getMode().equals(Constants.MODE_TIME)) {
            distancesArray = selection.getDurations();
        }

        List<LocationDto> outputList = compute();

        if (selection.getMode().equals(Constants.MODE_TIME)) {
            distancesArray = selection.getDistances();
        }

        for (int i = 0; i < outputList.size(); i++) {
            outputList.get(i).setIndex(i);
        }

        if (!Objects.equals(outputList.get(0).getId(), outputList.get(outputList.size() - 1).getId())) {

            try {
                LocationDto first = outputList.get(0).clone();
                first.setIndex(outputList.size());
                outputList.add(first);

            } catch (CloneNotSupportedException e) {
                log.error(e.getMessage());
            }
        }

        selection.setOutputList(outputList);
        optimalDistance = getCurrentDistance();
        selection.setResultDistance(optimalDistance);

        int optimalDuration = getCurrentDuration();
        selection.setResultDuration(optimalDuration);

        setupStages();
        selection.setDistanceStagesMap(stagesMap);
        selection.setDurationStagesMap(durationsMap);

        setProgress(100);
        selection.setProgress(getProgress());
        selection.setState(Constants.READY_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        selection.setOutputList(outputList);
        jmsTemplate.convertAndSend("algorithmResult", "");
    }

    List<LocationDto> getResult() {

        List<LocationDto> result = new ArrayList<>();

        int[] clone = currentPath.clone();
        List<Integer> optimalWay = new ArrayList<>();

        optimalWay.addAll(Arrays.stream(clone).boxed().collect(Collectors.toList()));

        if (!selection.getAlgorithmName().equals(Constants.THE_HELD_KARP_LOWER_BOUND)) {
            optimalWay.add(optimalWay.get(0));
        }

        for (int i = 0; i < optimalWay.size(); i++) {
            try {
                LocationDto locationDto = inputList.get(optimalWay.get(i)).clone();
                locationDto.setIndex(i);
                result.add(locationDto);
            } catch (CloneNotSupportedException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Generates random path with drunken sailor algorithm
     */
    int[] getRandomPath() {

        int[] randomPath = new int[numberOfCities];

        for (int i = 0; i < numberOfCities; i++) {
            randomPath[i] = i;
        }
        Random random = new Random();

        for (int i = 0; i < numberOfCities; i++) {
            int index = random.nextInt(i + 1);
            // swap elements
            int a = randomPath[index];
            randomPath[index] = randomPath[i];
            randomPath[i] = a;
        }

        return randomPath;
    }

    /**
     * Returns the current distance
     */
    int getCurrentDistance() {
        int sum = 0;

        for (int i = 0; i < this.numberOfCities; i++) {
            int a = currentPath[i];
            int b = currentPath[(i + 1) % this.numberOfCities];
            sum += this.distancesArray[a][b];
        }

        return sum;
    }

    /**
     * Returns the current duration
     */
    private int getCurrentDuration() {
        int sum = 0;

        for (int i = 0; i < this.numberOfCities; i++) {
            int a = currentPath[i];
            int b = currentPath[(i + 1) % this.numberOfCities];
            sum += this.durationsArray[a][b];
        }

        return sum;
    }

    private void setupStages() {
        List<LocationDto> locations = selection.getOutputList();
        stagesMap = new HashMap<>();
        durationsMap = new HashMap<>();

        for (int i = 0; i < locations.size(); i++) {
            long firstId = i == locations.size() - 1 ? locations.get(0).getId() : locations.get(i).getId();
            long secondId = i == locations.size() - 1 ? locations.get(i).getId() : locations.get(i + 1).getId();

            int indexA = 0;
            int indexB = 0;

            for (int j = 0; j < inputList.size(); j++) {
                if (inputList.get(j).getId().equals(firstId)) {
                    indexA = j;
                }
                if (inputList.get(j).getId().equals(secondId)) {
                    indexB = j;
                }
            }

            int dist = distancesArray[indexA][indexB];
            int dur = durationsArray[indexA][indexB];
            stagesMap.put(i, dist);
            durationsMap.put(i, dur);
        }

    }

}
