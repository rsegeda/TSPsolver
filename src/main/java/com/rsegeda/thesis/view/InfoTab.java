package com.rsegeda.thesis.view;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.springframework.stereotype.Component;

/**
 * Created by Roman Segeda on 26/08/2017.
 */
@Component
public class InfoTab extends HorizontalLayout {

    void init() {
        Label infoLabel = new Label("Created by Roman Segeda");
        infoLabel.setSizeFull();
        this.addComponent(infoLabel);
    }
}
