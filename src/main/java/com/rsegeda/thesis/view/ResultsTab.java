package com.rsegeda.thesis.view;

import com.rsegeda.thesis.algorithm.HeldKarpAlgorithm;
import com.rsegeda.thesis.component.LabelWithObserver;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.location.LocationMapper;
import com.rsegeda.thesis.location.LocationService;
import com.rsegeda.thesis.route.RouteMapper;
import com.rsegeda.thesis.route.RouteService;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman Segeda on 09/04/2017.
 */

public class ResultsTab extends HorizontalLayout {

    private final LocationService locationService;
    private final RouteService routeService;

    private final LocationMapper locationMapper;
    private final RouteMapper routeMapper;
    HeldKarpAlgorithm heldKarpAlgorithm;
    private List<LocationDto> locationList;
    private String selectedAlgorithm = "";
    private Label selectedAlgorithmLabel;
    private LabelWithObserver currentAlgorithmResult;

    @Autowired
    public ResultsTab(LocationService locationService, RouteService routeService, LocationMapper locationMapper, RouteMapper routeMapper) {

        this.locationService = locationService;
        this.routeService = routeService;
        this.locationMapper = locationMapper;
        this.routeMapper = routeMapper;

        this.locationList = new ArrayList<>();


        selectedAlgorithmLabel = new Label("");
        this.addComponent(selectedAlgorithmLabel);

        this.heldKarpAlgorithm = new HeldKarpAlgorithm(0);
        currentAlgorithmResult = new LabelWithObserver(this.heldKarpAlgorithm);

        this.addComponent(currentAlgorithmResult);

        this.heldKarpAlgorithm.addObserver(currentAlgorithmResult);

        this.setSizeFull();
    }

    public void run(String algorithmName) {

        selectedAlgorithm = algorithmName;
        selectedAlgorithmLabel.setValue("You have selected: " + selectedAlgorithm);

        if (selectedAlgorithm.equals(Constants.THE_HELD_KARP_LOWER_BOUND)) {
            heldKarpAlgorithm.start();

        } else if (heldKarpAlgorithm != null) {
            heldKarpAlgorithm.getThread().stop();
        }
    }


}
