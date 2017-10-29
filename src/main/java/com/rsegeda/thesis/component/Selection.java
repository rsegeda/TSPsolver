package com.rsegeda.thesis.component;

import com.rsegeda.thesis.algorithm.Settings;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Roman Segeda on 26/08/2017.
 * <p>
 * Component below is a singleton which is used to share values across application. The results and settings are
 * stored here. This is the container for values to be transferred across different views.
 */
@Data
@Component
public class Selection {

    private String state = Constants.READY_STATE;
    private Integer progress = 0;

    private String algorithmName;

    /**
     * List of locations on input
     */
    private List<LocationDto> locationDtos;

    /**
     * List of locations on output
     */
    private List<LocationDto> resultList;

    /**
     * Results of algorithms computation.
     */
    private Integer resultDistance;

    private Integer resultDuration;

    /**
     * Maps that have locationDtos' Ids and values of distance/duration to the next node.
     * Used to display on Results grid as an additional columns.
     */
    private Map<Integer, Integer> distanceStagesMap;

    private Map<Integer, Integer> durationStagesMap;

    /**
     * 2D Arrays that contain durations and distances between all nodes.
     */
    private int[][] distances;

    private int[][] durations;

    /**
     * Settings for each algorithm that can be updated in the SettingsTab view.
     */
    private Settings settings;

    public Selection() {
        settings = new Settings();
    }
}
