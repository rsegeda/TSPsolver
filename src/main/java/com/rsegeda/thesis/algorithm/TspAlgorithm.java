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
    int[][] durationsArray;

    int numberOfCities;
    int[] currentPath;

    int optimalDistance = Integer.MAX_VALUE;
    int optimalDuration = Integer.MAX_VALUE;

    Map<Integer, Integer> stagesMap;
    Map<Integer, Integer> durationsMap;

    List<Integer> optimalPath;
    private boolean stopAlgorithm = false;

    @Getter
    public Thread thread;
    @Getter
    public int progress = 0;

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

        int size = selection.getLocationDtos().size();

        int[][] distances = new int[size][size];
        int[][] durations = new int[size][size];

        setProgress(0);
        selection.setProgress(progress);
        selection.setState(Constants.PREPARING_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        // Setup distances matrix
        for (int a = 0; a < size; a++) {
            for (int b = 0; b < size; b++) {
                if (a == b) {
                    distances[a][b] = 0;
                    durations[a][b] = 0;

                } else {
                    DirectionsResult directionsResult = directionsService.getDirection(selection.getLocationDtos()
                                    .get(a)
                                    .getPlaceName(),
                            selection.getLocationDtos().get(b).getPlaceName());
                    Long distance = directionsResult.routes[0].legs[0].distance.inMeters;
                    distances[a][b] = distance.intValue();
                    Long interval = directionsResult.routes[0].legs[0].duration.inSeconds;
                    durations[a][b] = interval.intValue();
                }
            }
            setProgress(a * 100 / selection.getLocationDtos().size());
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

        numberOfCities = selection.getLocationDtos().size();
        distancesArray = selection.getDistances();
        durationsArray = selection.getDurations();

        prepareData();
        selection.setDistances(distancesArray);
        selection.setDurations(durationsArray);

        setProgress(0);
        selection.setProgress(getProgress());
        selection.setState(Constants.COMPUTING_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        optimalDistance = Integer.MAX_VALUE;
        optimalPath = new ArrayList<>();

        List<LocationDto> result = compute();

        for (int i = 0; i < result.size(); i++) {
            result.get(i).setIndex(i);
        }

        if (!Objects.equals(result.get(0).getId(), result.get(result.size() - 1).getId())) {

            try {
                LocationDto first = result.get(0).clone();
                first.setIndex(result.size());
                result.add(first);

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        selection.setResultList(result);
        optimalDistance = getCurrentDistance();
        optimalDuration = getCurrentDuration();
        selection.setResultDistance(optimalDistance);
        selection.setResultDuration(optimalDuration);

        setupStages();
        selection.setDistanceStagesMap(stagesMap);
        selection.setDurationStagesMap(durationsMap);


        setProgress(100);
        selection.setProgress(getProgress());
        selection.setState(Constants.READY_STATE);
        jmsTemplate.convertAndSend(Constants.STATE_UPDATE_JMS, "");

        selection.setResultList(result);
        jmsTemplate.convertAndSend("algorithmResult", "");
    }

    List<LocationDto> getResult() {

        List<LocationDto> result = new ArrayList<>();

        int[] clone = currentPath.clone();
        List<Integer> optimalPath = new ArrayList<>();

        optimalPath.addAll(Arrays.stream(clone).boxed().collect(Collectors.toList()));

        if (!selection.getAlgorithmName().equals(Constants.THE_HELD_KARP_LOWER_BOUND)) {
            optimalPath.add(optimalPath.get(0));
        }

        for (int i = 0; i < optimalPath.size(); i++) {
            try {
                LocationDto locationDto = selection.getLocationDtos().get(optimalPath.get(i)).clone();
                locationDto.setIndex(i);
                result.add(locationDto);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
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
        ;

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
    int getCurrentDuration() {
        int sum = 0;

        for (int i = 0; i < this.numberOfCities; i++) {
            int a = currentPath[i];
            int b = currentPath[(i + 1) % this.numberOfCities];
            sum += this.durationsArray[a][b];
        }

        return sum;
    }

    private void setupStages() {
        List<LocationDto> locations = selection.getResultList();
        stagesMap = new HashMap<>();
        durationsMap = new HashMap<>();

        for (int i = 0; i < locations.size(); i++) {
            int firstIndex = i == locations.size() - 1 ? locations.get(0).getIndex() : locations.get(i).getIndex();
            int secondIndex = i == locations.size() - 1 ? locations.get(i).getIndex() : locations.get(i + 1).getIndex();

            if (secondIndex == locations.size() - 1) {
                secondIndex = locations.get(0).getIndex();
            }

            int dist = distancesArray[firstIndex][secondIndex];
            int dur = durationsArray[firstIndex][secondIndex];
            stagesMap.put(i, dist);
            durationsMap.put(i, dur);
        }

    }

    @Override
    public void stop() {
        setStopAlgorithm(true);
    }

    private void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }

}
