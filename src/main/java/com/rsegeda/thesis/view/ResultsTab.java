package com.rsegeda.thesis.view;

import com.rsegeda.thesis.algorithm.HeldKarpAlgorithm;
import com.rsegeda.thesis.algorithm.TspAlgorithm;
import com.rsegeda.thesis.component.LabelWithObserver;
import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.location.LocationMapper;
import com.rsegeda.thesis.location.LocationService;
import com.rsegeda.thesis.route.RouteMapper;
import com.rsegeda.thesis.route.RouteService;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rsegeda.thesis.config.Constants.THE_HELD_KARP_LOWER_BOUND;

/**
 * Created by Roman Segeda on 09/04/2017.
 */

@Component
public class ResultsTab extends HorizontalLayout {


    private static Logger logger = LoggerFactory.getLogger(HomeTab.class);

    private final Selection selection;

    private final LocationService locationService;
    private final RouteService routeService;

    private final LocationMapper locationMapper;
    private final RouteMapper routeMapper;
    TspAlgorithm tspAlgorithm;
    private List<LocationDto> locationList;
    private String selectedAlgorithm = "";
    private Label selectedAlgorithmLabel;
    private LabelWithObserver currentAlgorithmResult;

    @Autowired
    public ResultsTab(LocationService locationService, RouteService routeService, LocationMapper locationMapper,
                      RouteMapper routeMapper, Selection selection) {

        this.locationService = locationService;
        this.routeService = routeService;
        this.locationMapper = locationMapper;
        this.routeMapper = routeMapper;
        this.selection = selection;
    }

    public void run(String algorithmName) {

        selectedAlgorithm = algorithmName;
        selectedAlgorithmLabel.setValue("You have selected: " + selectedAlgorithm);

        showProgressPane();

        if (selectedAlgorithm.equals(THE_HELD_KARP_LOWER_BOUND)) {
            tspAlgorithm.start();

        } else if (tspAlgorithm != null) {
            tspAlgorithm.stop();
        }
    }

    private void showProgressPane() {

        if (currentAlgorithmResult != null && currentAlgorithmResult.isAttached()) {
            this.removeComponent(currentAlgorithmResult);
        }

        switch (selectedAlgorithm) {

            case Constants.THE_HELD_KARP_LOWER_BOUND:
                this.tspAlgorithm = new HeldKarpAlgorithm();
                break;

            default:
                logger.error("Incorrect algorithm selection");
                break;
        }

        if (tspAlgorithm == null) {
            return;
        }
        currentAlgorithmResult = new LabelWithObserver(this.tspAlgorithm);

        this.addComponent(currentAlgorithmResult);

        this.tspAlgorithm.addObserver(currentAlgorithmResult);
    }


    public void init() {

        this.locationList = new ArrayList<>();

        selectedAlgorithmLabel = new Label("");
        this.addComponent(selectedAlgorithmLabel);

        this.setSizeFull();

        this.addComponent(new Label("Results tab content"));
    }
}
