package com.rsegeda.thesis.view;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.springframework.stereotype.Component;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 26/08/2017.
 */
@Component
public class SettingsTab extends HorizontalLayout {

    public void init(){
        this.addComponent(new Label("Settings tab content"));
    }
}
