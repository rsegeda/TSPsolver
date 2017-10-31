package com.rsegeda.thesis.view;

import com.rsegeda.thesis.algorithm.AntColonyAlgorithm;
import com.rsegeda.thesis.algorithm.HeldKarpAlgorithm;
import com.rsegeda.thesis.algorithm.LinKernighanAlgorithm;
import com.rsegeda.thesis.algorithm.TspAlgorithm;
import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.config.Properties;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.utils.DirectionsService;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.rsegeda.thesis.config.Constants.ALGORITHMS;

/**
 * Created by Roman Segeda on 09/04/2017.
 */
@Slf4j
@Component
public class ResultsTab extends HorizontalLayout {

    private final transient Properties properties;
    private transient Selection selection;
    private transient JmsTemplate jmsTemplate;
    private transient DirectionsService directionsService;

    private transient TspAlgorithm tspAlgorithm;
    private String selectedAlgorithm = "";
    private Label selectedAlgorithmLabel;
    private Label progressLabel;
    private Label distanceLabel;
    private Label durationLabel;

    @SuppressWarnings("FieldCanBeLocal")
    private HorizontalLayout infoPanel;

    private VerticalLayout leftPanel;
    private HorizontalLayout progressPanel;

    /*
    Right panel
     */
    private VerticalLayout rightPanel;
    private GoogleMap googleMap;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<GoogleMapMarker> googleMapMarkers = new ArrayList<>();
    private List<GoogleMapPolyline> polylines = new ArrayList<>();

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

        if (ALGORITHMS.contains(selectedAlgorithm)) {
            tspAlgorithm.start();
        }
    }

    private void clearOldResults() {
        locationGrid.setItems(Collections.emptyList());
        googleMap.clearMarkers();
        polylines.forEach(googleMapPolyline -> googleMap.removePolyline(googleMapPolyline));


        if (distanceLabel != null && distanceLabel.isAttached()) {
            progressPanel.removeComponent(distanceLabel);
        }
        if (durationLabel != null && durationLabel.isAttached()) {
            progressPanel.removeComponent(durationLabel);
        }

        if (progressLabel == null) {
            progressLabel = new Label();
            progressLabel.setStyleName("resultsProgressLabel");
        } else if (progressLabel.isAttached()) {
            progressPanel.removeComponent(progressLabel);
            progressPanel.addComponent(progressLabel);
        }

        if (!progressLabel.isAttached()) {
            progressPanel.addComponent(progressLabel);
        }
    }

    private void setupObservers() {

        switch (selectedAlgorithm) {

            case Constants.DRUNKEN_SAILOR_ALGORITHM:
                tspAlgorithm = new TspAlgorithm(selection, jmsTemplate, directionsService);
                break;

            case Constants.THE_HELD_KARP_LOWER_BOUND:
                tspAlgorithm = new HeldKarpAlgorithm(selection, jmsTemplate, directionsService);
                break;

            case Constants.ANT_COLONY_OPTIMIZATION:
                tspAlgorithm = new AntColonyAlgorithm(selection, jmsTemplate, directionsService);
                break;

            case Constants.LIN_KERNIGHAN:
                tspAlgorithm = new LinKernighanAlgorithm(selection, jmsTemplate, directionsService);
                break;

            default:
                log.error("Incorrect algorithm selection");
                break;
        }
    }


    void init() {
        if (!created) {
            googleMap = new GoogleMap(properties.getApiKey(),
                    null, "english");

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
        locationGrid.addColumn(locationDto -> locationDto.getIndex() + 1).setCaption("Order").setExpandRatio(2);
        locationGrid.addColumn(LocationDto::getPlaceName).setCaption("Name").setExpandRatio(58);

        locationGrid.addColumn(locationDto -> selection.getDistanceStagesMap()
                .get(locationDto.getIndex()) / 1000 + "km").setCaption("Distance").setExpandRatio(20);
        locationGrid.addColumn(locationDto -> {
            int s = selection.getDurationStagesMap().get(locationDto.getIndex
                    ());
            return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
        }).setCaption("Duration").setExpandRatio(20);

        // Allow column hiding
        locationGrid.getColumns().forEach(column -> column.setHidable(true));
        locationGrid.setId("resultsGrid");
        locationGrid.setStyleName("resultsGrid");

        leftPanel.setSizeFull();
        leftPanel.addStyleName("resultsLeftPanel");
        leftPanel.addComponent(locationGrid);

        progressPanel = new HorizontalLayout();
        leftPanel.addComponent(progressPanel);
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
    public void showResults() {

        locationGrid.setItems(selection.getOutputList());
        pinMarkers();
        drawLines();
        String prefixDist = selection.getMode().equalsIgnoreCase(Constants.MODE_DISTANCE) ? "Optimal" : "Calculated";
        String prefixTime = selection.getMode().equalsIgnoreCase(Constants.MODE_TIME) ? "Optimal" : "Calculated";
        distanceLabel = new Label(prefixDist + " Distance: " + selection.getResultDistance() / 1000 + "km");
        Integer resultDuration = selection.getResultDuration();
        durationLabel = new Label(prefixTime + " Time: " + String.format("%dh:%02dm:%02ds", resultDuration / 3600,
                (resultDuration % 3600) / 60, (resultDuration % 60)));
        progressPanel.addComponents(distanceLabel, durationLabel);
    }

    private void drawLines() {
        List<LocationDto> locationDtos = selection.getOutputList();

        for (int i = 0; i < locationDtos.size(); i++) {
            if (i == locationDtos.size() - 1) {
                GoogleMapPolyline polyline = new GoogleMapPolyline(
                        Arrays.asList(locationDtos.get(0).getLatLon(), locationDtos.get(i).getLatLon()),
                        String.format("#%02x%02x%02x", 100,
                                i * 100 / locationDtos.size(),
                                i * 100 / locationDtos.size()),
                        0.8, 5);
                polylines.add(polyline);
                googleMap.addPolyline(polyline);
            } else {
                GoogleMapPolyline polyline = new GoogleMapPolyline(
                        Arrays.asList(locationDtos.get(i).getLatLon(), locationDtos.get(i + 1).getLatLon()),
                        String.format("#%02x%02x%02x",
                                10, i * 255 / locationDtos.size(),
                                i * 255 / locationDtos.size()),
                        0.8, 5);
                polylines.add(polyline);
                googleMap.addPolyline(polyline);
            }
        }
    }

    @JmsListener(destination = "stateUpdate", containerFactory = "jmsListenerFactory")
    public void updateInfo() {

        progressLabel.setValue("Current state is: " + selection.getState());

        if (selection.getState().equals(Constants.PREPARING_STATE)) {
            progressLabel.setValue("Current state is: "
                    + selection.getState() + ": " + selection.getProgress() + "%");
            log.warn("Progress is: " + selection.getProgress());

        }
    }

    private void pinMarkers() {
        selection.getOutputList().forEach(locationDto -> createMarker(locationDto.getPlaceName(), locationDto
                .getLatLon()));
    }

    private void createMarker(String name, LatLon latLon) {

        GoogleMapMarker newMarker = new GoogleMapMarker(name,
                new LatLon(latLon.getLat(), latLon.getLon()), false, null);
        newMarker.setIconUrl("VAADIN/themes/mytheme/img/pin.png");
        googleMapMarkers.add(newMarker);
        googleMap.addMarker(newMarker);
    }
}
