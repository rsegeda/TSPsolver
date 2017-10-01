package com.rsegeda.thesis.view;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.springframework.stereotype.Component;

/**
 * Created by Roman Segeda on 26/08/2017.
 */
@Component
public class SettingsTab extends HorizontalLayout {

    private Label headerLabel = new Label("Settings tab content");
    void init() {
        if (headerLabel.isAttached()) {
            removeComponent(headerLabel);
        }
        this.addComponent(headerLabel);
    }
}
