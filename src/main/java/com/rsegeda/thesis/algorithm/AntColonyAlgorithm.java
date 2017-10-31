package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by Roman Segeda on 03/10/2017.
 * <p>
 * Implementation based on the paper called "Ant colony optimization theory: A survey"
 *
 * @author Marco Dorigoa, Christian Blumb
 * @link http://www.sciencedirect.com/science/article/pii/S0304397505003798
 */
@Slf4j
public class AntColonyAlgorithm extends TspAlgorithm implements Algorithm {

    private double[][] trails;
    private double[] probabilities;

    private List<Ant> ants = new ArrayList<>();
    private Random random = new Random();

    private int currentLocationIndex;

    public AntColonyAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService
            directionsService) {
        super(selection, jmsTemplate, directionsService);
    }

    @Override
    public List<LocationDto> compute() {

        int numberOfAnts = (int) (numberOfCities * selection.getSettings().getAotAntGroupSize());

        trails = new double[numberOfCities][numberOfCities];
        probabilities = new double[numberOfCities];

        IntStream.range(0, numberOfAnts).forEach(i -> ants.add(new Ant(numberOfCities)));

        //         Set starting index of locations to begin with foremost element
        IntStream.range(0, numberOfAnts)
                .forEach(i -> ants.forEach(ant -> {
                    ant.reset();
                    ant.visitLocation(-1, random.nextInt(numberOfCities));
                }));
        currentLocationIndex = 0;

        IntStream.range(0, numberOfCities)
                .forEach(i -> IntStream.range(0, numberOfCities)
                        .forEach(j -> trails[i][j] = selection.getSettings().getAotNumberOfTrails()));

        //        Main loop is executed N times. Setting is retrieved from settings tab and may be adjusted.
        IntStream.range(0, selection.getSettings().getAotNumberOfIterations())
                .forEach(i -> {
                    moveAnts();
                    updateTrails();
                    updateOptimalPath();
                });

        return getResult();
    }

    private void moveAnts() {
        IntStream.range(currentLocationIndex, numberOfCities - 1)
                .forEach(i -> {
                    ants.forEach(ant -> {
                        try {
                            ant.visitLocation(currentLocationIndex, selectNextLocation(ant));
                        } catch (AotException e) {
                            log.error(e.getMessage());

                        }
                    });
                    currentLocationIndex++;
                });
    }

    private int selectNextLocation(Ant ant) throws AotException {
        double randNum = 0.01;
        int randomInt = random.nextInt(numberOfCities - currentLocationIndex);

        if (random.nextDouble() < randNum) {
            OptionalInt cityIndex = IntStream.range(0, numberOfCities)
                    .filter(i -> (i == randomInt) && !ant.visited(i))
                    .findFirst();
            if (cityIndex.isPresent()) {
                return cityIndex.getAsInt();
            }
        }

        calculateProbabilities(ant);

        double randomDouble = random.nextDouble();
        double totalProbability = 0;

        for (int i = 0; i < numberOfCities; i++) {
            totalProbability += probabilities[i];

            if (totalProbability >= randomDouble) {
                return i;
            }
        }

        throw new AotException("Next location not found");
    }

    private void calculateProbabilities(Ant ant) throws AotException {
        int i = ant.trail[currentLocationIndex];
        double pheromone = 0.0;

        for (int l = 0; l < numberOfCities; l++) {

            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], selection.getSettings().getAotBeta()) * Math.pow(1.0 /
                        distancesArray[i][l], selection.getSettings()
                        .getAotBeta());
            }
        }

        for (int j = 0; j < numberOfCities; j++) {

            if (ant.visited(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(trails[i][j], selection.getSettings().getAotAlpha()) * Math.pow(1.0 /
                                distancesArray[i][j],
                        selection.getSettings().getAotBeta());

                if (pheromone == 0) {
                    throw new AotException("Pheromonone is zero");
                }
                probabilities[j] = numerator / pheromone;
            }
        }
    }

    private void updateTrails() {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {

                trails[i][j] *= selection.getSettings().getAotEvaporation();
            }
        }

        for (Ant a : ants) {
            double pheromoneLeft = 500;
            double contribution = pheromoneLeft / a.trailLength(distancesArray);

            for (int i = 0; i < numberOfCities - 1; i++) {
                trails[a.trail[i]][a.trail[i + 1]] += contribution;
            }
            trails[a.trail[numberOfCities - 1]][a.trail[0]] += contribution;
        }
    }

    private void updateOptimalPath() {
        if (currentPath == null) {
            currentPath = ants.get(0).trail;
            optimalDistance = ants.get(0)
                    .trailLength(distancesArray);
        }

        for (Ant a : ants) {
            if (a.trailLength(distancesArray) < optimalDistance) {
                optimalDistance = a.trailLength(distancesArray);
                currentPath = a.trail.clone();
            }
        }
    }

    public static class Ant {

        private int trailLength;
        private int[] trail;
        private boolean[] visited;

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

    private class AotException extends Exception {

        AotException(@SuppressWarnings("unused") String reason) {
        }
    }
}
