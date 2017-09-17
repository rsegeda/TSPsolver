package com.rsegeda.thesis.view;

import com.rsegeda.thesis.algorithm.HeldKarpAlgorithm;
import com.rsegeda.thesis.algorithm.TspAlgorithm;
import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
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

    private HorizontalLayout resultsPanel;
    private VerticalLayout leftPanel;
    private VerticalLayout rightPanel;

    private Grid<LocationDto> locationGrid;
    private boolean created = false;

    @Autowired
    public ResultsTab(Selection selection, JmsTemplate jmsTemplate,
                      DirectionsService directionsService) {

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

        addComponent(progressLabel);
    }


    void init() {

        if (!created) {
            this.locationList = new ArrayList<>();

            selectedAlgorithmLabel = new Label("");

            infoPanel = new HorizontalLayout();
            infoPanel.addComponent(selectedAlgorithmLabel);
            addComponent(infoPanel);

            setupResultsPanel();
            addComponent(resultsPanel);

            created = true;
        }
    }

    private void setupResultsPanel() {
        resultsPanel = new HorizontalLayout();
        resultsPanel.setStyleName("resultsTabPanel");
        setupLeftPanel();
        setupRightPanel();
        resultsPanel.addComponents(leftPanel, rightPanel);
    }

    private void setupLeftPanel() {
        leftPanel = new VerticalLayout();
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
        leftPanel.setSizeFull();
        leftPanel.addStyleName("ResultsLeftPanel");
        leftPanel.addComponent(locationGrid);
    }

    private void setupRightPanel() {
        rightPanel = new VerticalLayout();
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
