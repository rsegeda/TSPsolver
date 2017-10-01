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

    public HeldKarpAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService directionsService) {
        super(selection, jmsTemplate, directionsService);
    }

    @Override
    public List<LocationDto> compute() {

        /* ------------------------- ALGORITHM INITIALIZATION ----------------------- */

        int size = selection.getLocationDtos().size();
        // Initial variables to start the algorithm
        int[] vertices = new int[size - 1];

        // Filling the initial vertices array with the proper values
        for (int i = 1; i < size; i++) {
            vertices[i - 1] = i;
        }

        // FIRST CALL TO THE RECURSIVE FUNCTION
        procedure(0, vertices, new ArrayList<>(), 0);

        System.out.print("Path: " + optimalPath + ". Distance = " + optimalDistance);

        List<LocationDto> result = new ArrayList<>();
        selection.setDistanceStagesMap(new HashMap<>());

        for (int i = 0; i < optimalPath.size() - 1; i++) {
            result.add(selection.getLocationDtos().get(optimalPath.get(i)));

            selection.getDistanceStagesMap().put(selection.getLocationDtos().get(optimalPath.get(i)).getId(),
                    selection.getDistances()[optimalPath.get(i)][optimalPath.get(i + 1)]);
        }

        return result;
    }

    /* ------------------------------- RECURSIVE FUNCTION ---------------------------- */

    private int procedure(int initial, int vertices[], List<Integer> path, int costUntilHere) {

        // We concatenate the current path and the vertex taken as initial
        path = new ArrayList<>(path);
        path.add(initial);

        int length = vertices.length;
        int newCostUntilHere;

        // Exit case, if there are no more options to evaluate (last node)
        if (length == 0) {
            newCostUntilHere = costUntilHere + selection.getDistances()[initial][0];

            // If its cost is lower than the stored one
            if (newCostUntilHere < optimalDistance) {
                optimalDistance = newCostUntilHere;
                path.add(0);
                optimalPath = path;
            }

            return (selection.getDistances()[initial][0]);
        }

        // If the current branch has higher cost than the stored one: stop traversing
        else if (costUntilHere > optimalDistance) {
            return 0;
        }

        // Common case, when there are several nodes in the list
        else {

            int[][] newVertices = new int[length][(length - 1)];
            int costCurrentNode, costChild;
            int bestCost = Integer.MAX_VALUE;

            // For each of the nodes of the list
            for (int i = 0; i < length; i++) {

                // Each recursion new vertices list is constructed
                for (int j = 0, k = 0; j < length; j++, k++) {

                    // The current child is not stored in the new vertices array
                    if (j == i) {
                        k--;
                        continue;
                    }
                    newVertices[i][k] = vertices[j];
                }

                // Cost of arriving the current node from its parent
                costCurrentNode = selection.getDistances()[initial][vertices[i]];

                // Here the cost to be passed to the recursive function is computed
                newCostUntilHere = costCurrentNode + costUntilHere;

                // RECURSIVE CALLS TO THE FUNCTION IN ORDER TO COMPUTE THE COSTS
                costChild = procedure(vertices[i], newVertices[i], path, newCostUntilHere);

                // The cost of every child + the current node cost is computed
                int totalCost = costChild + costCurrentNode;

                // Finally we select from the minimum from all possible children costs
                if (totalCost < bestCost) {
                    bestCost = totalCost;
                }
            }

            return (bestCost);
        }
    }

    @Override
    public void stop() {
        setStopAlgorithm(true);
    }

    @Override
    public void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }

}
