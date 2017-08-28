package com.rsegeda.thesis;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.location.LocationMapper;
import com.rsegeda.thesis.location.LocationService;
import com.rsegeda.thesis.route.RouteMapper;
import com.rsegeda.thesis.route.RouteService;
import com.rsegeda.thesis.view.HomeTab;
import com.rsegeda.thesis.view.InfoTab;
import com.rsegeda.thesis.view.MainTabSheet;
import com.rsegeda.thesis.view.ResultsTab;
import com.rsegeda.thesis.view.SettingsTab;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;

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

    private static Logger logger = LoggerFactory.getLogger(MyUI.class);

    private Selection selection;

    private final LocationService locationService;
    private final RouteService routeService;
    private final LocationMapper locationMapper;
    private final RouteMapper routeMapper;
    private Label appNameLabel;
    private CssLayout mainLayout;

    private TabSheet tabSheet;

    private final MainTabSheet mainTabSheet;

    private final HomeTab homeTab;
    private final ResultsTab resultsTab;
    private final SettingsTab settingsTab;
    private final InfoTab infoTab;

    @Autowired
    public MyUI(LocationService locationService, RouteService routeService, LocationMapper locationMapper,
                RouteMapper routeMapper, Selection selection, MainTabSheet mainTabSheet, HomeTab homeTab, ResultsTab resultsTab,
                SettingsTab settingsTab, InfoTab infoTab) {
        this.locationService = locationService;
        this.routeService = routeService;
        this.locationMapper = locationMapper;
        this.routeMapper = routeMapper;

        this.selection = selection;

        this.mainTabSheet = mainTabSheet;

        this.homeTab = homeTab;
        this.resultsTab = resultsTab;
        this.settingsTab = settingsTab;
        this.infoTab = infoTab;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setupMainLayout();

        setContent(mainLayout);

        setPollInterval(1000);
        addPollListener((UIEvents.PollListener) event -> logger.info("Polling"));
    }

    private void setupMainLayout() {

        mainLayout = new CssLayout();
        mainLayout.setStyleName("homeMainLayout");

        appNameLabel = new Label("Pathfinder - TSP solver");
        appNameLabel.setStyleName("appLabel");
        appNameLabel.setHeight(8.0f, Unit.PERCENTAGE);
        mainLayout.addComponent(appNameLabel);

        mainTabSheet.init();
        mainLayout.addComponent(mainTabSheet);

        mainLayout.setSizeFull();
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

    }

}
