package com.rsegeda.thesis.view;

import com.vaadin.ui.TabSheet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 27/08/2017.
 */
@Slf4j
@Component
public class MainTabSheet extends TabSheet {

    private final HomeTab homeTab;
    private final ResultsTab resultsTab;
    private final SettingsTab settingsTab;
    private final InfoTab infoTab;

    @Autowired
    public MainTabSheet(HomeTab homeTab, ResultsTab resultsTab, SettingsTab settingsTab, InfoTab infoTab) {
        this.homeTab = homeTab;
        this.resultsTab = resultsTab;
        this.settingsTab = settingsTab;
        this.infoTab = infoTab;
    }

    public void init() {

        this.setWidth(100.0f, Unit.PERCENTAGE);
        this.setHeight(90.0f, Unit.PERCENTAGE);

        homeTab.init();
        resultsTab.init();
        settingsTab.init();
        infoTab.init();

        this.addTab(homeTab, "Home");
        this.addTab(resultsTab, "Results");
        this.addTab(settingsTab, "Settings");
        this.addTab(infoTab, "Info");
    }

    @JmsListener(destination = "runAlgorithm", containerFactory = "jmsListenerFactory")
    public void startAlgorithm() {
        this.setSelectedTab(resultsTab);
        resultsTab.run();
    }
}
