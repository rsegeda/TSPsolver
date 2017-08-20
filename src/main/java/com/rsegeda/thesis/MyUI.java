package com.rsegeda.thesis;

import com.rsegeda.thesis.location.LocationMapper;
import com.rsegeda.thesis.location.LocationService;
import com.rsegeda.thesis.route.RouteMapper;
import com.rsegeda.thesis.route.RouteService;
import com.rsegeda.thesis.view.HomeTab;
import com.rsegeda.thesis.view.ResultsTab;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.UIEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import java.util.logging.Logger;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@SpringUI
public class MyUI extends UI {

    private final static Logger log =
            Logger.getLogger(MyUI.class.getName());
    private final LocationService locationService;
    private final RouteService routeService;
    private final LocationMapper locationMapper;
    private final RouteMapper routeMapper;
    private Label appNameLabel;
    private CssLayout mainLayout;
    private TabSheet tabsheet;
    private HomeTab homeTab;
    private ResultsTab resultsTab;

    @Autowired
    public MyUI(LocationService locationService, RouteService routeService, LocationMapper locationMapper, RouteMapper routeMapper) {
        this.locationService = locationService;
        this.routeService = routeService;
        this.locationMapper = locationMapper;
        this.routeMapper = routeMapper;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setupMainLayout();

        // Home tab
        homeTab = new HomeTab(tabsheet, locationService);
        tabsheet.addTab(homeTab, "Home");

        // Results tab
        resultsTab = new ResultsTab(locationService, routeService, locationMapper, routeMapper);
        resultsTab.addComponent(new Label("Results tab content"));
        tabsheet.addTab(resultsTab, "Results");

        // Settings tab
        VerticalLayout settingsTab = new VerticalLayout();
        settingsTab.addComponent(new Label("Settings tab content"));
        tabsheet.addTab(settingsTab, "Settings");

        // Info tab
        VerticalLayout infoTab = new VerticalLayout();
        Label infoLabel = new Label("Created by Roman Segeda");
        infoLabel.setSizeFull();
        infoTab.addComponent(infoLabel);
        tabsheet.addTab(infoTab, "Info");

        mainLayout.addComponent(tabsheet);
        mainLayout.setSizeFull();
        setContent(mainLayout);

        setPollInterval(1000);
        addPollListener(new UIEvents.PollListener() {
            @Override
            public void poll(UIEvents.PollEvent event) {
                log.info("Polling");
            }
        });
    }

    private void setupMainLayout() {
        mainLayout = new CssLayout();
        mainLayout.setStyleName("homeMainLayout");

        appNameLabel = new Label("Pathfinder - TSP solver");
        appNameLabel.setStyleName("appLabel");
        appNameLabel.setHeight(8.0f, Unit.PERCENTAGE);
        mainLayout.addComponent(appNameLabel);

        tabsheet = new TabSheet();
        tabsheet.setWidth(100.0f, Unit.PERCENTAGE);
        tabsheet.setHeight(90.0f, Unit.PERCENTAGE);
        mainLayout.addComponent(tabsheet);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

    }

}
