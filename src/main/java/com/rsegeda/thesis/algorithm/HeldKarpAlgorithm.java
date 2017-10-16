package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Roman Segeda on 01/08/2017.
 */
@Slf4j
public class HeldKarpAlgorithm extends TspAlgorithm {

    public HeldKarpAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService
            directionsService) {
        super(selection, jmsTemplate, directionsService);
    }

    @Override
    public List<LocationDto> compute() {

        int size = selection.getLocationDtos().size();
        // Initial variables to start the algorithm
        int[] vertices = new int[size - 1];

        for (int i = 1; i < size; i++) {
            vertices[i - 1] = i;
        }

        heldKarpProcedure(0, vertices, new ArrayList<>(), 0);


        List<LocationDto> result = new ArrayList<>();
        selection.setDistanceStagesMap(new HashMap<>());

        for (int i = 0; i < optimalPath.size() - 1; i++) {
            result.add(selection.getLocationDtos().get(optimalPath.get(i)));

            selection.getDistanceStagesMap().put(selection.getLocationDtos().get(optimalPath.get(i)).getId(),
                    selection.getDistances()[optimalPath.get(i)][optimalPath.get(i + 1)]);
        }

        return result;
    }

    private int heldKarpProcedure(int initial, int vertices[], List<Integer> path, int distance) {

        // Adding vertex to existing path
        path = new ArrayList<>(path);
        path.add(initial);

        int newDistance;
        int size = vertices.length;

        // Exit - last node case
        if (size == 0) {
            newDistance = distance + selection.getDistances()[initial][0];

            // Update shortest route if not exceeded the optimal
            // Add first location to route and change optimalDistance
            if (newDistance < optimalDistance) {
                optimalDistance = newDistance;
                path.add(0);
                optimalPath = path;
            }

            return (selection.getDistances()[initial][0]);
        }

        // If temporary distance is higher than current optimal - skip this calculation
        else if (distance > optimalDistance) {
            return 0;
        }

        // Common case
        else {

            int[][] subPath = new int[size][(size - 1)];
            int optimal = Integer.MAX_VALUE;

            // For each location
            for (int a = 0; a < size; a++) {

                // Each recursion new vertices list is constructed
                for (int b = 0, k = 0; b < size; b++, k++) {

                    // The same location is being skipped
                    if (b == a) {
                        k--;
                        continue;
                    }
                    subPath[a][k] = vertices[b];
                }

                // Distance between parent and current node
                int currentDistance = selection.getDistances()[initial][vertices[a]];

                // Current calculated distance including last node
                newDistance = currentDistance + distance;

                int childrenDistance = heldKarpProcedure(vertices[a], subPath[a], path, newDistance);

                int totalDistance = childrenDistance + currentDistance;

                if (totalDistance < optimal) {
                    optimal = totalDistance;
                }
            }

            return (optimal);
        }
    }

}
