package com.rsegeda.thesis;

import com.rsegeda.thesis.config.Selection;
import com.rsegeda.thesis.location.LocationMapper;
import com.rsegeda.thesis.location.LocationService;
import com.rsegeda.thesis.route.RouteMapper;
import com.rsegeda.thesis.route.RouteService;
import com.rsegeda.thesis.view.HomeTab;
import com.rsegeda.thesis.view.InfoTab;
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

    private Selection selection;

    private final LocationService locationService;
    private final RouteService routeService;
    private final LocationMapper locationMapper;
    private final RouteMapper routeMapper;
    private Label appNameLabel;
    private CssLayout mainLayout;

    private TabSheet tabSheet;

    private final HomeTab homeTab;
    private final ResultsTab resultsTab;
    private final SettingsTab settingsTab;
    private final InfoTab infoTab;

    @Autowired
    public MyUI(LocationService locationService, RouteService routeService, LocationMapper locationMapper,
                RouteMapper routeMapper, Selection selection, HomeTab homeTab, ResultsTab resultsTab,
                SettingsTab settingsTab, InfoTab infoTab) {
        this.locationService = locationService;
        this.routeService = routeService;
        this.locationMapper = locationMapper;
        this.routeMapper = routeMapper;
        this.selection = selection;
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
        addPollListener((UIEvents.PollListener) event -> log.info("Polling"));
    }

    private void setupMainLayout() {
        mainLayout = new CssLayout();
        mainLayout.setStyleName("homeMainLayout");

        appNameLabel = new Label("Pathfinder - TSP solver");
        appNameLabel.setStyleName("appLabel");
        appNameLabel.setHeight(8.0f, Unit.PERCENTAGE);
        mainLayout.addComponent(appNameLabel);

        tabSheet = new TabSheet();

        tabSheet.setWidth(100.0f, Unit.PERCENTAGE);
        tabSheet.setHeight(90.0f, Unit.PERCENTAGE);

        homeTab.setTabSheet(tabSheet);
        homeTab.init();
        tabSheet.addTab(homeTab, "Home");

        resultsTab.init();
        tabSheet.addTab(resultsTab, "Results");

        settingsTab.init();
        tabSheet.addTab(settingsTab, "Settings");

        infoTab.init();
        tabSheet.addTab(infoTab, "Info");

        mainLayout.addComponent(tabSheet);

        homeTab.setTabSheet(tabSheet);

        mainLayout.setSizeFull();

    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

    }

}
