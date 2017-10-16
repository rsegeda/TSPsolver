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

    private int numberOfCities;
    private int numberOfAnts;

    private int[][] distances;
    private double trails[][];
    private double probabilities[];
    private int[] bestTourOrder;

    private List<Ant> ants = new ArrayList<>();
    private Random random = new Random();

    private int currentIndex;

    public AntColonyAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService
            directionsService) {
        super(selection, jmsTemplate, directionsService);
    }

    @Override
    public List<LocationDto> compute() {

        // Initialization
        distances = selection.getDistances();
        numberOfCities = distances.length;
        numberOfAnts = (int) (numberOfCities * selection.getSettings().getAotAntGroupSize());

        trails = new double[numberOfCities][numberOfCities];
        probabilities = new double[numberOfCities];
        IntStream.range(0, numberOfAnts)
                .forEach(i -> ants.add(new Ant(numberOfCities)));

        // Computing
        setupAnts();
        clearTrails();
        IntStream.range(0, selection.getSettings().getAotNumberOfIterations())
                .forEach(i -> {
                    moveAnts();
                    updateTrails();
                    updateBest();
                });

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


    /**
     * Prepare ants for the simulation
     */
    private void setupAnts() {
        IntStream.range(0, numberOfAnts)
                .forEach(i -> ants.forEach(ant -> {
                    ant.clear();
                    ant.visitCity(-1, random.nextInt(numberOfCities));
                }));
        currentIndex = 0;
    }

    /**
     * At each iteration, move ants
     */
    private void moveAnts() {
        IntStream.range(currentIndex, numberOfCities - 1)
                .forEach(i -> {
                    ants.forEach(ant -> ant.visitCity(currentIndex, selectNextCity(ant)));
                    currentIndex++;
                });
    }

    /**
     * Select next city for each ant
     */
    private int selectNextCity(Ant ant) {
        int t = random.nextInt(numberOfCities - currentIndex);
        double randNum = 0.01;
        if (random.nextDouble() < randNum) {
            OptionalInt cityIndex = IntStream.range(0, numberOfCities)
                    .filter(i -> i == t && !ant.visited(i))
                    .findFirst();
            if (cityIndex.isPresent()) {
                return cityIndex.getAsInt();
            }
        }
        calculateProbabilities(ant);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < numberOfCities; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }

        throw new RuntimeException("There are no other cities");
    }

    /**
     * Calculate the next city picks probabilities
     */
    private void calculateProbabilities(Ant ant) {
        int i = ant.trail[currentIndex];
        double pheromone = 0.0;

        for (int l = 0; l < numberOfCities; l++) {
            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], selection.getSettings().getAotBeta()) * Math.pow(1.0 /
                        distances[i][l], selection.getSettings()
                        .getAotBeta());
            }
        }

        for (int j = 0; j < numberOfCities; j++) {
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

    /**
     * Update trails that ants used
     */
    private void updateTrails() {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                trails[i][j] *= selection.getSettings().getAotEvaporation();
            }
        }
        for (Ant a : ants) {
            double pheromoneLeft = 500;
            double contribution = pheromoneLeft / a.trailLength(distances);
            for (int i = 0; i < numberOfCities - 1; i++) {
                trails[a.trail[i]][a.trail[i + 1]] += contribution;
            }
            trails[a.trail[numberOfCities - 1]][a.trail[0]] += contribution;
        }
    }

    /**
     * Update the best solution
     */
    private void updateBest() {
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

    /**
     * Clear trails after simulation
     */
    private void clearTrails() {
        IntStream.range(0, numberOfCities)
                .forEach(i -> IntStream.range(0, numberOfCities)
                        .forEach(j -> trails[i][j] = selection.getSettings().getAotNumberOfTrails()));
    }

    public static class Ant {

        private int trailSize;
        private int trail[];
        private boolean visited[];

        Ant(int tourSize) {
            this.trailSize = tourSize;
            this.trail = new int[tourSize];
            this.visited = new boolean[tourSize];
        }

        void visitCity(int currentIndex, int city) {
            trail[currentIndex + 1] = city;
            visited[city] = true;
        }

        boolean visited(int i) {
            return visited[i];
        }

        int trailLength(int[][] graph) {
            int length = graph[trail[trailSize - 1]][trail[0]];
            for (int i = 0; i < trailSize - 1; i++) {
                length += graph[trail[i]][trail[i + 1]];
            }
            return length;
        }

        void clear() {
            for (int i = 0; i < trailSize; i++) { visited[i] = false; }
        }

    }
}
