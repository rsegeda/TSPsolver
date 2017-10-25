package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.location.LocationDto;

import java.util.List;

/**
 * Created by Roman Segeda on 20/08/2017.
 */
public interface Algorithm extends Runnable {

    /**
     * Getter for progress of computation
     *
     * @return progress of computation
     */
    int getProgress();

    /**
     * Setter for progress of computation
     *
     * @param x new progress value
     */
    void setProgress(int x);

    /**
     * Starts an algorithm by creating a thread
     */
    void start();

    /**
     * This method purpose is to calculate distances between all the locations. It uses Google Directions API.
     */
    void prepareData();

    /**
     * Method that has to be overridden for each implementation of a different algorithm.
     * In its scope calculation should be done as well as setting up stages' distances for UI.
     *
     * @return list of locations to visit
     */
    List<LocationDto> compute();

    /**
     * Thread's run method
     */
    void run();

    /**
     * Stops an algorithm thread
     */
    void stop();
}
