package com.rsegeda.thesis.view;

import com.rsegeda.thesis.algorithm.Settings;
import com.rsegeda.thesis.config.Constants;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roman Segeda on 26/08/2017.
 */
@Component
public class SettingsTab extends HorizontalLayout {

    private final Settings settings;

    private Label headerLabel = new Label("Settings tab content");
    private VerticalLayout runConfigurationContainerLayout;
    private Map<String, HorizontalLayout> runConfigurationsLayout;


    @Autowired
    public SettingsTab(Settings settings) {
        this.settings = settings;
    }

    void init() {
        if (headerLabel.isAttached()) {
            removeAllComponents();
        }
        this.addComponent(headerLabel);

        runConfigurationContainerLayout = new VerticalLayout();
        runConfigurationsLayout = new HashMap<>();

        Constants.ALGORITHMS.forEach(s -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setCaption(s);
            setupAlgorithmSettingsLayout(s, horizontalLayout);
            runConfigurationsLayout.put(s, horizontalLayout);
            runConfigurationContainerLayout.addComponent(horizontalLayout);
        });

        addComponent(runConfigurationContainerLayout);


    }

    private void setupAlgorithmSettingsLayout(String s, HorizontalLayout horizontalLayout) {
        switch (s) {
            case Constants.MOCKUP_ALGORITHM:
                break;
            case Constants.THE_HELD_KARP_LOWER_BOUND:
                break;
            case Constants.ANT_COLONY_OPTIMIZATION:
                Label label = new Label("Number of iterations");
                TextField textField = new TextField("Now: " + settings.getAOT_NUMBER_OF_ITERATIONS());
                textField.setValue("");
                Button button = new Button("Save");
                button.addClickListener(event -> {
                    settings.setAOT_NUMBER_OF_ITERATIONS(Integer.valueOf(textField.getValue()));
                    textField.setCaption("Now: " + settings.getAOT_NUMBER_OF_ITERATIONS());
                });
                horizontalLayout.addComponents(label, textField, button);
                break;
            case Constants.LIN_KERNIGHAN:
                break;
            default:
                break;

        }
    }
}
