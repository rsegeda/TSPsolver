package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Roman Segeda on 03/10/2017.
 */

public class AntColonyAlgorithm extends TspAlgorithm implements Algorithm {

    private int numberOfLocations;
    private int numberOfAnts;

    private int[][] distances;
    private double trails[][];
    private double probabilities[];
    private int[] bestTourOrder;

    private List<Ant> ants = new ArrayList<>();
    private Random random = new Random();

    private int currentLocationIndex;

    public AntColonyAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService
            directionsService) {
        super(selection, jmsTemplate, directionsService);
    }

    @Override
    public List<LocationDto> compute() {

        //         Initialization
        distances = selection.getDistances();
        numberOfLocations = distances.length;
        numberOfAnts = (int) (numberOfLocations * selection.getSettings().getAotAntGroupSize());

        trails = new double[numberOfLocations][numberOfLocations];
        probabilities = new double[numberOfLocations];

        IntStream.range(0, numberOfAnts).forEach(i -> ants.add(new Ant(numberOfLocations)));

        //         Set starting index of locations to begin with foremost element
        IntStream.range(0, numberOfAnts)
                .forEach(i -> ants.forEach(ant -> {
                    ant.reset();
                    ant.visitLocation(-1, random.nextInt(numberOfLocations));
                }));
        currentLocationIndex = 0;

        IntStream.range(0, numberOfLocations)
                .forEach(i -> IntStream.range(0, numberOfLocations)
                        .forEach(j -> trails[i][j] = selection.getSettings().getAotNumberOfTrails()));

        //        Main loop is executed N times. Setting is retrieved from settings tab and may be adjusted.
        IntStream.range(0, selection.getSettings().getAotNumberOfIterations())
                .forEach(i -> {
                    moveAnts();
                    updateTrails();
                    updateOptimalPath();
                });

        //        Prepare results and setup each stage distance
        List<LocationDto> result = new ArrayList<>();

        int[] clone = bestTourOrder.clone();
        List<Integer> optimalPath = new ArrayList<>();

        optimalPath.addAll(Arrays.stream(clone).boxed().collect(Collectors.toList()));
        optimalPath.add(optimalPath.get(0));

        selection.setDistanceStagesMap(new HashMap<>());

        for (int i = 0; i < optimalPath.size() - 1; i++) {
            result.add(selection.getLocationDtos().get(optimalPath.get(i)));

            selection.getDistanceStagesMap().put(selection.getLocationDtos().get(optimalPath.get(i)).getId(),
                    selection.getDistances()[optimalPath.get(i)][optimalPath.get(i + 1)]);
        }

        return result;
    }

    private void moveAnts() {
        IntStream.range(currentLocationIndex, numberOfLocations - 1)
                .forEach(i -> {
                    ants.forEach(ant -> ant.visitLocation(currentLocationIndex, selectNextLocation(ant)));
                    currentLocationIndex++;
                });
    }

    private int selectNextLocation(Ant ant) throws AotException {
        double randNum = 0.01;
        int randomInt = random.nextInt(numberOfLocations - currentLocationIndex);

        if (random.nextDouble() < randNum) {
            OptionalInt cityIndex = IntStream.range(0, numberOfLocations)
                    .filter(i -> (i == randomInt) && !ant.visited(i))
                    .findFirst();
            if (cityIndex.isPresent()) {
                return cityIndex.getAsInt();
            }
        }

        calculateProbabilities(ant);

        double randomDouble = random.nextDouble();
        double totalProbability = 0;

        for (int i = 0; i < numberOfLocations; i++) {
            totalProbability += probabilities[i];

            if (totalProbability >= randomDouble) {
                return i;
            }
        }

        throw new AotException("Next location not found");
    }

    private void calculateProbabilities(Ant ant) {
        int i = ant.trail[currentLocationIndex];
        double pheromone = 0.0;

        for (int l = 0; l < numberOfLocations; l++) {

            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], selection.getSettings().getAotBeta()) * Math.pow(1.0 /
                        distances[i][l], selection.getSettings()
                        .getAotBeta());
            }
        }

        for (int j = 0; j < numberOfLocations; j++) {

            if (ant.visited(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(trails[i][j], selection.getSettings().getAotAlpha()) * Math.pow(1.0 /
                                distances[i][j],
                        selection.getSettings().getAotBeta());
                probabilities[j] = numerator / pheromone;
            }
        }
    }

    private void updateTrails() {
        for (int i = 0; i < numberOfLocations; i++) {
            for (int j = 0; j < numberOfLocations; j++) {

                trails[i][j] *= selection.getSettings().getAotEvaporation();
            }
        }

        for (Ant a : ants) {
            double pheromoneLeft = 500;
            double contribution = pheromoneLeft / a.trailLength(distances);

            for (int i = 0; i < numberOfLocations - 1; i++) {
                trails[a.trail[i]][a.trail[i + 1]] += contribution;
            }
            trails[a.trail[numberOfLocations - 1]][a.trail[0]] += contribution;
        }
    }

    private void updateOptimalPath() {
        if (bestTourOrder == null) {
            bestTourOrder = ants.get(0).trail;
            optimalDistance = ants.get(0)
                    .trailLength(distances);
        }

        for (Ant a : ants) {
            if (a.trailLength(distances) < optimalDistance) {
                optimalDistance = a.trailLength(distances);
                bestTourOrder = a.trail.clone();
            }
        }
    }

    public static class Ant {

        private int trailLength;
        private int trail[];
        private boolean visited[];

        Ant(int numberOfCities) {
            this.trailLength = numberOfCities;
            this.trail = new int[numberOfCities];
            this.visited = new boolean[numberOfCities];
        }

        void visitLocation(int currentIndex, int city) {
            trail[currentIndex + 1] = city;
            visited[city] = true;
        }

        boolean visited(int i) {
            return visited[i];
        }

        int trailLength(int[][] graph) {
            int length = graph[trail[trailLength - 1]][trail[0]];

            for (int i = 0; i < trailLength - 1; i++) {
                length += graph[trail[i]][trail[i + 1]];
            }

            return length;
        }

        void reset() {
            for (int i = 0; i < trailLength; i++) { visited[i] = false; }
        }

    }

    private class AotException extends RuntimeException {

        public AotException(String reason) {
        }
    }
}
