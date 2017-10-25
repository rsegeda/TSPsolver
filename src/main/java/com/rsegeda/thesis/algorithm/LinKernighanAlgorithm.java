package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Roman Segeda on 18/10/2017.
 *
 * This is the Lin-Kernighan algorithm implementation. It is based on original paper:
 * "An Effective Heuristic Algorithm for the Traveling-Salesman Problem"
 * @author Lin, Shen; Kernighan, B. W.
 *
 * @link https://eng.ucmerced.edu/people/yzhang/papers/Heuristic/Lin_Kernighan
 */

public class LinKernighanAlgorithm extends TspAlgorithm {

    public LinKernighanAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService directionsService) {
        super(selection, jmsTemplate, directionsService);
    }

    @Override
    public List<LocationDto> compute() {

        currentPath = getRandomPath();

        startLinKernighan();

        return getResult();
    }

    /**
     * Main function
     */
    private void startLinKernighan() {
        double oldSolution;

        do {
            oldSolution = getCurrentDistance();
            IntStream.range(0, numberOfCities - 1).forEach(i -> improve(i, false));
        } while (getCurrentDistance() < oldSolution);
    }

    /**
     * Improves the currentPath by starting from a particular node and checking the new gain
     *
     * @param i a reference to the city which to start with.
     */
    private void improve(int i, boolean previous) {
        int indexA = previous ? getPreviousIndex(i) : getNextIndex(i);
        int indexB = getNearestNeighbor(indexA);

        if (indexB != -1 && getDistanceBetween(indexA, indexB) < getDistanceBetween(i, indexA)) {
            startAlgorithm(i, indexA, indexB);
        } else if (!previous) {
            improve(i, true);
        }
    }

    /**
     * Returns the previous index for the currentPath.
     *
     * @param index of the given node
     */
    private int getPreviousIndex(int index) {
        return index == 0 ? numberOfCities - 1 : index - 1;
    }

    /**
     * Returns the next index for the currentPath.
     *
     * @param index of the given node
     */
    private int getNextIndex(int index) {
        return (index + 1) % numberOfCities;
    }

    /**
     * Returns the nearest neighbor for an specific node
     */
    private int getNearestNeighbor(int index) {
        int minDistance = Integer.MAX_VALUE;
        int nearestNeighborIndex = -1;
        int currentIndex = currentPath[index];

        for (int i = 0; i < numberOfCities; ++i) {
            if (i != currentIndex) {
                int distance = this.distancesArray[i][currentIndex];

                if (distance < minDistance) {
                    nearestNeighborIndex = getIndex(i);
                    minDistance = distance;
                }
            }
        }

        return nearestNeighborIndex;
    }

    /**
     * Returns the distance between two nodes given its indexes
     *
     * @param a is the index of the first location
     * @param b is the index of the second location
     */
    private int getDistanceBetween(int a, int b) {
        return distancesArray[currentPath[a]][currentPath[b]];
    }

    /**
     * Fourth step of the Lin-Kernighan - main method
     *
     * @param indexA the index chosen in the currentPath
     * @param indexB the index chosen in the currentPath
     * @param indexC the index chosen in the currentPath
     */
    private void startAlgorithm(int indexA, int indexB, int indexC) {
        List<Integer> indexArray = new ArrayList<>();

        indexArray.add(0, -1);
        indexArray.add(1, indexA);
        indexArray.add(2, indexB);
        indexArray.add(3, indexC);

        int initialGain = getDistanceBetween(indexB, indexA) - getDistanceBetween(indexC, indexB); // |x1| - |y1|
        double gStar = 0;
        double gI = initialGain;
        int k = 3;

        for (int i = 4; ; i += 2) {
            int newPathIndex = selectNewPathIndex(indexArray);

            if (newPathIndex == -1) {
                break;
            }
            indexArray.add(i, newPathIndex);
            int newPathIndexWithNextNode = getNextPossibleY(indexArray);

            if (newPathIndexWithNextNode == -1) {
                break;
            }

            // Step 4.F
            gI += getDistanceBetween(indexArray.get(indexArray.size() - 2), newPathIndex);

            if (gI - getDistanceBetween(newPathIndex, indexA) > gStar) {
                gStar = gI - getDistanceBetween(newPathIndex, indexA);
                k = i;
            }

            indexArray.add(newPathIndexWithNextNode);
            gI -= getDistanceBetween(newPathIndex, newPathIndexWithNextNode);
        }

        if (gStar > 0) {
            indexArray.set(k + 1, indexArray.get(1));
            currentPath = getPrimePath(indexArray, k);
        }
    }

    /**
     * Gets all the ys that fit the criterion - step 4
     *
     * @param pathIndexList the list of pathIndexes
     */
    private int getNextPossibleY(List<Integer> pathIndexList) {
        int lastPathIndex = pathIndexList.get(pathIndexList.size() - 1);
        List<Integer> ys = new ArrayList<>();

        for (int i = 0; i < numberOfCities; ++i) {
            // Disjunctive criterion
            if (!checkDisjunctivity(pathIndexList, i, lastPathIndex)) {
                continue;
            }
            // Gain criterion
            if (!isPositiveGain(pathIndexList, i)) {
                continue;
            }
            // Step 4.F
            if (!nextXPossible(pathIndexList, i)) {
                continue;
            }
            ys.add(i);
        }

        // Get closest y
        int minDistance = Integer.MAX_VALUE;
        int minNode = -1;

        for (int i : ys) {
            if (getDistanceBetween(lastPathIndex, i) < minDistance) {
                minNode = i;
                minDistance = getDistanceBetween(lastPathIndex, i);
            }
        }

        return minNode;

    }

    private boolean nextXPossible(List<Integer> pathIndexList, int i) {
        return isConnected(pathIndexList, i, getNextIndex(i)) || isConnected(pathIndexList, i, getPreviousIndex(i));
    }

    private boolean isConnected(List<Integer> pathIndexList, int x, int y) {
        if (x == y) { return false; }

        for (int i = 1; i < pathIndexList.size() - 1; i += 2) {
            if (pathIndexList.get(i) == x && pathIndexList.get(i + 1) == y) { return false; }
            if (pathIndexList.get(i) == y && pathIndexList.get(i + 1) == x) { return false; }
        }

        return true;
    }

    private boolean isPositiveGain(List<Integer> pathIndexList, int pathIndex) {
        int gain = 0;

        for (int i = 1; i < pathIndexList.size() - 2; ++i) {
            int pA = pathIndexList.get(i);
            int pB = pathIndexList.get(i + 1);
            int pC = i == pathIndexList.size() - 3 ? pathIndex : pathIndexList.get(i + 2);

            gain += getDistanceBetween(pB, pC) - getDistanceBetween(pA, pB);
        }

        return gain > 0;
    }

    /**
     * Gets a new pathIndexList -  step 4.A
     */
    private int selectNewPathIndex(List<Integer> pathIndexList) {
        int option1 = getPreviousIndex(pathIndexList.get(pathIndexList.size() - 1));
        int option2 = getNextIndex(pathIndexList.get(pathIndexList.size() - 1));

        int[] firstPath = buildPath(currentPath, pathIndexList, option1);

        if (compareToCurrentPath(firstPath)) {
            return option1;
        } else {
            int[] secondPath = buildPath(currentPath, pathIndexList, option2);

            if (compareToCurrentPath(secondPath)) {
                return option2;
            }
        }

        return -1;
    }

    private int[] buildPath(int[] newPath, List<Integer> pathIndexList, int newIndex) {
        List<Integer> changes = new ArrayList<>(pathIndexList);

        changes.add(newIndex);
        changes.add(changes.get(1));
        return buildPath(newPath, changes);
    }

    /**
     * Compares given path to the currentPath
     */
    private boolean compareToCurrentPath(int[] path) {

        if (path.length != numberOfCities) {
            return false;
        }

        for (int i = 0; i < numberOfCities - 1; ++i) {
            for (int j = i + 1; j < numberOfCities; ++j) {
                if (path[i] == path[j]) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Builds a prime path
     */
    private int[] getPrimePath(List<Integer> pathIndexList, int k) {
        List<Integer> al2 = new ArrayList<>(pathIndexList.subList(0, k + 2));
        return buildPath(currentPath, al2);
    }

    /**
     * Builds a new path by removing the X sets and adding the Y sets
     *
     * @param path    the currentPath
     * @param changes the list of paths to derive the X and Y sets
     * @return an array with the node numbers
     */
    private int[] buildPath(int[] path, List<Integer> changes) {
        List<Edge> currentEdges = deriveEdgesFromTour(path);

        List<Edge> X = deriveX(changes);
        List<Edge> Y = deriveY(changes);
        int s = currentEdges.size();

        // Remove Xs
        for (Edge e : X) {
            for (int j = 0; j < currentEdges.size(); ++j) {
                Edge m = currentEdges.get(j);

                if (e.equals(m)) {
                    s--;
                    currentEdges.set(j, null);
                    break;
                }
            }
        }

        // Add Ys
        for (Edge e : Y) {
            s++;
            currentEdges.add(e);
        }

        return createTourFromEdges(currentEdges, s);
    }

    /**
     * Converts the edges into a currentPath
     */
    private int[] createTourFromEdges(List<Edge> currentEdges, int s) {
        int[] path = new int[s];

        int i = 0;
        int last = -1;

        for (; i < currentEdges.size(); ++i) {
            if (currentEdges.get(i) != null) {
                path[0] = currentEdges.get(i).getA();
                path[1] = currentEdges.get(i).getB();
                last = path[1];
                break;
            }
        }

        currentEdges.set(i, null);

        int k = 2;

        while (true) {
            int j = 0;

            for (; j < currentEdges.size(); ++j) {
                Edge e = currentEdges.get(j);

                if (e != null && e.getA() == last) {
                    last = e.getB();
                    break;
                } else if (e != null && e.getB() == last) {
                    last = e.getA();
                    break;
                }
            }
            if (j == currentEdges.size()) { break; }

            currentEdges.set(j, null);

            if (k >= s) { break; }

            path[k] = last;
            k++;
        }

        return path;
    }

    private List<Edge> deriveX(List<Integer> changes) {
        List<Edge> es = new ArrayList<>();

        for (int i = 1; i < changes.size() - 2; i += 2) {
            Edge e = new Edge(currentPath[changes.get(i)], currentPath[changes.get(i + 1)]);
            es.add(e);
        }

        return es;
    }

    private List<Edge> deriveY(List<Integer> changes) {
        List<Edge> es = new ArrayList<>();

        for (int i = 2; i < changes.size() - 1; i += 2) {
            Edge e = new Edge(currentPath[changes.get(i)], currentPath[changes.get(i + 1)]);
            es.add(e);
        }

        return es;
    }

    /**
     * Converts the currentPath to the edge list
     */
    private List<Edge> deriveEdgesFromTour(int[] path) {
        List<Edge> edgesList = new ArrayList<>();

        for (int i = 0; i < path.length; ++i) {
            Edge e = new Edge(path[i], path[(i + 1) % path.length]);
            edgesList.add(e);
        }

        return edgesList;
    }

    /**
     * Checks if an edge is already on either X or Y (disjunctivity criterion)
     *
     * @param pathIndexList of the nodes in the currentPath
     * @param x             an index of one of the endpoints
     * @param y             an index of one of the endpoints
     */
    private boolean checkDisjunctivity(List<Integer> pathIndexList, int x, int y) {
        if (x == y) { return false; }

        for (int i = 0; i < pathIndexList.size() - 1; i++) {
            if (pathIndexList.get(i) == x && pathIndexList.get(i + 1) == y) { return false; }
            if (pathIndexList.get(i) == y && pathIndexList.get(i + 1) == x) { return false; }
        }

        return true;
    }

    /**
     * Gets the index of the node given the actual number of the node in the currentPath
     */
    private int getIndex(int node) {
        int i = 0;

        for (int t : currentPath) {
            if (node == t) {
                return i;
            }
            i++;
        }

        return -1;
    }

    @EqualsAndHashCode
    @ToString
    @Getter
    public static class Edge implements Comparable<Edge> {

        // node A
        private int a;

        // node B
        private int b;

        Edge(int a, int b) {
            this.a = a > b ? a : b;
            this.b = a > b ? b : a;
        }

        @SuppressWarnings("NullableProblems")
        public int compareTo(Edge edge) {
            if (this.getA() < edge.getA() || this.getA() == edge.getA() && this.getB() < edge.getB()) {
                return -1;
            } else if (this.equals(edge)) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
