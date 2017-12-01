package com.rsegeda.thesis;

import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.view.MainTabSheet;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.UIEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@StyleSheet({"http://fonts.googleapis.com/css?family=Alegreya+Sans+SC&subset=latin,latin-ext"})
@Slf4j
@Theme("mytheme")
@SpringUI
public class MyUI extends UI {

    private CssLayout mainLayout;
    private final MainTabSheet mainTabSheet;

    @Autowired
    public MyUI(MainTabSheet mainTabSheet) {
        this.mainTabSheet = mainTabSheet;
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

        Label appNameLabel = new Label(Constants.APP_NAME);
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
