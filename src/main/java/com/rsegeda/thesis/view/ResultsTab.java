package com.rsegeda.thesis.view;

import com.rsegeda.thesis.algorithm.HeldKarpAlgorithm;
import com.rsegeda.thesis.algorithm.TspAlgorithm;
import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.location.LocationMapper;
import com.rsegeda.thesis.location.LocationService;
import com.rsegeda.thesis.route.RouteMapper;
import com.rsegeda.thesis.route.RouteService;
import com.rsegeda.thesis.utils.DirectionsService;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.rsegeda.thesis.config.Constants.THE_HELD_KARP_LOWER_BOUND;

/**
 * Created by Roman Segeda on 09/04/2017.
 */
@Slf4j
@Component
public class ResultsTab extends VerticalLayout {

    private final Selection selection;
    private final JmsTemplate jmsTemplate;
    private final DirectionsService directionsService;

    private final LocationService locationService;
    private final RouteService routeService;

    private final LocationMapper locationMapper;
    private final RouteMapper routeMapper;
    private TspAlgorithm tspAlgorithm;
    private List<LocationDto> locationList;
    private String selectedAlgorithm = "";
    private Label selectedAlgorithmLabel;
    private Label progressLabel;

    private Grid<LocationDto> locationGrid;
    private boolean created = false;

    @Autowired
    public ResultsTab(LocationService locationService, RouteService routeService, LocationMapper locationMapper,
                      RouteMapper routeMapper, Selection selection, JmsTemplate jmsTemplate, DirectionsService directionsService) {

        this.locationService = locationService;
        this.routeService = routeService;
        this.locationMapper = locationMapper;
        this.routeMapper = routeMapper;
        this.selection = selection;
        this.jmsTemplate = jmsTemplate;
        this.directionsService = directionsService;
    }

    public void run() {
        clearOldResults();
        selectedAlgorithm = selection.getAlgorithmName();
        selectedAlgorithmLabel.setValue("Algorithm: " + selectedAlgorithm);
        setupObservers();

        if (selectedAlgorithm.equals(THE_HELD_KARP_LOWER_BOUND)) {
            tspAlgorithm.start();

        } else if (tspAlgorithm != null) {
            tspAlgorithm.stop();
        }
    }

    private void clearOldResults() {
        locationGrid.setItems(Collections.emptyList());
    }

    private void setupObservers() {

        if (progressLabel != null && progressLabel.isAttached()) {
            removeComponent(progressLabel);
        }

        switch (selectedAlgorithm) {

            case Constants.THE_HELD_KARP_LOWER_BOUND:
                tspAlgorithm = new HeldKarpAlgorithm(selection, jmsTemplate, directionsService);
                break;

            default:
                log.error("Incorrect algorithm selection");
                break;
        }

        if (tspAlgorithm == null) {
            return;
        }
        progressLabel = new Label();

        addComponent(progressLabel);

    }


    public void init() {

        if (!created) {
            this.locationList = new ArrayList<>();

            selectedAlgorithmLabel = new Label("");

            HorizontalLayout infoPanel = new HorizontalLayout();
            infoPanel.addComponent(selectedAlgorithmLabel);
            addComponent(infoPanel);
            HorizontalLayout resultsPanel = new HorizontalLayout();

            locationGrid = new Grid<>();

            locationGrid.setSizeFull();
            locationGrid.addStyleName("locationGrid");
            locationGrid.addColumn(LocationDto::getIndex).setCaption("Order").setExpandRatio(2);
            locationGrid.addColumn(LocationDto::getPlaceName).setCaption("Name").setExpandRatio(98);

            // Allow column hiding
            locationGrid.getColumns().forEach(column -> column.setHidable(true));
            locationGrid.setId("resultsGrid");
            locationGrid.setStyleName("resultsGrid");
            resultsPanel.setSizeFull();
            resultsPanel.addComponent(locationGrid);
            resultsPanel.addComponent(new Label("Other data"));
            addComponent(resultsPanel);

            created = true;
        }

    }

    @JmsListener(destination = "algorithmResult", containerFactory = "jmsListenerFactory")
    public void startAlgorithm() {
        locationList = selection.getResult();
        locationGrid.setItems(locationList);
    }

    @JmsListener(destination = "stateUpdate", containerFactory = "jmsListenerFactory")
    public void updateInfo() {

        if (selection.getState().equals(Constants.PREPARING_STATE)) {
            progressLabel.setValue("Current state is: " + selection.getState() + ": " + selection.getProgress() + "%");
            log.warn("Progress is: " + selection.getProgress());
        } else {
            progressLabel.setValue("Current state is: " + selection.getState());
        }
    }
}
