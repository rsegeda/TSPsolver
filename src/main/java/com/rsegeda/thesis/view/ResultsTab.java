package com.rsegeda.thesis.view;

import com.rsegeda.thesis.algorithm.HeldKarpAlgorithm;
import com.rsegeda.thesis.algorithm.TspAlgorithm;
import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.config.Properties;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
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
public class ResultsTab extends HorizontalLayout {

    private Properties properties;
    private transient Selection selection;
    private transient JmsTemplate jmsTemplate;
    private transient DirectionsService directionsService;

    private transient TspAlgorithm tspAlgorithm;
    private transient List<LocationDto> locationList;
    private String selectedAlgorithm = "";
    private Label selectedAlgorithmLabel;
    private Label progressLabel;

    @SuppressWarnings("FieldCanBeLocal")
    private HorizontalLayout infoPanel;

    private VerticalLayout leftPanel;

    /*
    Right panel
     */
    private VerticalLayout rightPanel;
    private GoogleMap googleMap;
    private List<GoogleMapMarker> googleMapMarkers = new ArrayList<>();

    private Grid<LocationDto> locationGrid;
    private boolean created = false;

    @Autowired
    public ResultsTab(Properties properties, Selection selection, JmsTemplate jmsTemplate,
                      DirectionsService directionsService) {
        this.properties = properties;
        this.selection = selection;
        this.jmsTemplate = jmsTemplate;
        this.directionsService = directionsService;
    }

    void run() {
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
        progressLabel.setStyleName("resultsProgressLabel");
        leftPanel.addComponent(progressLabel);
    }


    void init() {
        if (!created) {
            googleMap = new GoogleMap(properties.getApiKey(),
                    null, "english");

            this.locationList = new ArrayList<>();

            setupResultsPanel();
            setSizeFull();

            created = true;
        }
    }

    private void setupResultsPanel() {
        setSizeFull();
        setStyleName("resultsTabPanel");
        setupLeftPanel();
        setupRightPanel();
        addComponents(leftPanel, rightPanel);
    }

    private void setupLeftPanel() {
        leftPanel = new VerticalLayout();

        selectedAlgorithmLabel = new Label("");

        infoPanel = new HorizontalLayout();
        infoPanel.addComponent(selectedAlgorithmLabel);
        infoPanel.setStyleName("resultsInfoPanel");
        leftPanel.addComponent(infoPanel);

        locationGrid = new Grid<>();

        locationGrid.setSizeFull();
        locationGrid.addStyleName("locationGrid");
        locationGrid.addColumn(LocationDto::getIndex).setCaption("Order").setExpandRatio(2);
        locationGrid.addColumn(LocationDto::getPlaceName).setCaption("Name").setExpandRatio(98);

        // Allow column hiding
        locationGrid.getColumns().forEach(column -> column.setHidable(true));
        locationGrid.setId("resultsGrid");
        locationGrid.setStyleName("resultsGrid");
        leftPanel.setSizeFull();
        leftPanel.addStyleName("resultsLeftPanel");
        leftPanel.addComponent(locationGrid);
    }

    private void setupRightPanel() {
        rightPanel = new VerticalLayout();
        rightPanel.setSizeFull();
        rightPanel.addStyleName("resultsRightPanel");

        googleMap.setCenter(new LatLon(52.0690115, 19.4790478));
        googleMap.setZoom(6);
        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);
        googleMap.setWidth(100.0f, Unit.PERCENTAGE);
        googleMap.setHeight(95.0f, Unit.PERCENTAGE);
        googleMap.setVisible(true);
        rightPanel.addComponent(googleMap);
    }

    @JmsListener(destination = "algorithmResult", containerFactory = "jmsListenerFactory")
    public void startAlgorithm() {
        locationList = selection.getResult();
        locationGrid.setItems(locationList);
    }

    @JmsListener(destination = "stateUpdate", containerFactory = "jmsListenerFactory")
    public void updateInfo() {

        switch (selection.getState()) {
            case Constants.PREPARING_STATE:
                progressLabel.setValue("Current state is: "
                        + selection.getState() + ": " + selection.getProgress() + "%");
                log.warn("Progress is: " + selection.getProgress());
                break;
            case Constants.READY_STATE:
                pinMarkers();
                break;
            default:
                progressLabel.setValue("Current state is: " + selection.getState());
                break;
        }
    }

    private void pinMarkers() {

        selection.getResult().forEach(locationDto -> createMarker(locationDto.getPlaceName(), locationDto.getLatLon()));
    }

    private void createMarker(String name, LatLon latLon) {

        GoogleMapMarker newMarker = new GoogleMapMarker(name,
                new LatLon(latLon.getLat(), latLon.getLon()), false, null);
        googleMapMarkers.add(newMarker);
        googleMap.addMarker(newMarker);
    }
}
