package com.rsegeda.thesis.view;

import com.rsegeda.thesis.component.Selection;
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

    private transient final Selection selection;

    private Label headerLabel = new Label("Settings tab content");
    private VerticalLayout runConfigurationContainerLayout;
    private Map<String, VerticalLayout> runConfigurationsLayout;


    @Autowired
    public SettingsTab(Selection selection) {
        this.selection = selection;
    }

    void init() {
        if (headerLabel.isAttached()) {
            removeAllComponents();
        }
        this.addComponent(headerLabel);

        runConfigurationContainerLayout = new VerticalLayout();
        runConfigurationsLayout = new HashMap<>();

        Constants.ALGORITHMS.forEach(s -> {
            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setCaption(s);
            setupAlgorithmSettingsLayout(s, verticalLayout);
            runConfigurationsLayout.put(s, verticalLayout);
            runConfigurationContainerLayout.addComponent(verticalLayout);
        });

        addComponent(runConfigurationContainerLayout);


    }

    private void setupAlgorithmSettingsLayout(String s, VerticalLayout verticalLayout) {
        switch (s) {
            case Constants.MOCKUP_ALGORITHM:
                break;
            case Constants.THE_HELD_KARP_LOWER_BOUND:
                break;
            case Constants.ANT_COLONY_OPTIMIZATION:
                setupAntSettings(verticalLayout);
                break;
            case Constants.LIN_KERNIGHAN:
                break;
            default:
                break;

        }
    }

    private void setupAntSettings(VerticalLayout verticalLayout) {
        addNumberOfIterationsSetup(verticalLayout);
        addNumberOfTrailsSetup(verticalLayout);
        addAlphaFactorSetup(verticalLayout);
        addBetaFactorSetup(verticalLayout);
        addEvaporationSetup(verticalLayout);
        addGroupSizeSetup(verticalLayout);
    }

    private void addGroupSizeSetup(VerticalLayout verticalLayout) {
        Label label = new Label("Ant group size");
        label.setStyleName("settingsProperty");

        TextField textField = new TextField("Now: " + selection.getSettings().getAotAntGroupSize());
        textField.setValue("");

        Button button = new Button("Save");
        button.setStyleName("settingsProperty");
        button.addClickListener(event -> {
            selection.getSettings().setAotAntGroupSize(Double.valueOf(textField.getValue()));
            textField.setCaption("Now: " + selection.getSettings().getAotAntGroupSize());
        });

        HorizontalLayout propertyLayout = new HorizontalLayout();
        propertyLayout.addComponents(label, textField, button);
        verticalLayout.addComponents(propertyLayout);
    }

    private void addEvaporationSetup(VerticalLayout verticalLayout) {
        Label label = new Label("Evaporation factor");
        label.setStyleName("settingsProperty");

        TextField textField = new TextField("Now: " + selection.getSettings().getAotEvaporation());
        textField.setValue("");

        Button button = new Button("Save");
        button.setStyleName("settingsProperty");
        button.addClickListener(event -> {
            selection.getSettings().setAotEvaporation(Double.valueOf(textField.getValue()));
            textField.setCaption("Now: " + selection.getSettings().getAotEvaporation());
        });

        HorizontalLayout propertyLayout = new HorizontalLayout();
        propertyLayout.addComponents(label, textField, button);
        verticalLayout.addComponents(propertyLayout);
    }

    private void addBetaFactorSetup(VerticalLayout verticalLayout) {
        Label label = new Label("Beta factor");
        label.setStyleName("settingsProperty");

        TextField textField = new TextField("Now: " + selection.getSettings().getAotBeta());
        textField.setValue("");

        Button button = new Button("Save");
        button.setStyleName("settingsProperty");
        button.addClickListener(event -> {
            selection.getSettings().setAotBeta(Integer.valueOf(textField.getValue()));
            textField.setCaption("Now: " + selection.getSettings().getAotBeta());
        });

        HorizontalLayout propertyLayout = new HorizontalLayout();
        propertyLayout.addComponents(label, textField, button);
        verticalLayout.addComponents(propertyLayout);
    }

    private void addAlphaFactorSetup(VerticalLayout verticalLayout) {
        Label label = new Label("Alpha factor");
        label.setStyleName("settingsProperty");

        TextField textField = new TextField("Now: " + selection.getSettings().getAotAlpha());
        textField.setValue("");

        Button button = new Button("Save");
        button.setStyleName("settingsProperty");
        button.addClickListener(event -> {
            selection.getSettings().setAotAlpha(Integer.valueOf(textField.getValue()));
            textField.setCaption("Now: " + selection.getSettings().getAotAlpha());
        });

        HorizontalLayout propertyLayout = new HorizontalLayout();
        propertyLayout.addComponents(label, textField, button);
        verticalLayout.addComponents(propertyLayout);
    }

    private void addNumberOfTrailsSetup(VerticalLayout verticalLayout) {
        Label label = new Label("Number of trails");
        label.setStyleName("settingsProperty");

        TextField textField = new TextField("Now: " + selection.getSettings().getAotNumberOfTrails());
        textField.setValue("");

        Button button = new Button("Save");
        button.setStyleName("settingsProperty");
        button.addClickListener(event -> {
            selection.getSettings().setAotNumberOfTrails(Double.valueOf(textField.getValue()));
            textField.setCaption("Now: " + selection.getSettings().getAotNumberOfTrails());
        });

        HorizontalLayout propertyLayout = new HorizontalLayout();
        propertyLayout.addComponents(label, textField, button);
        verticalLayout.addComponents(propertyLayout);
    }

    private void addNumberOfIterationsSetup(VerticalLayout verticalLayout) {
        Label label = new Label("Number of iterations");
        label.setStyleName("settingsProperty");

        TextField textField = new TextField("Now: " + selection.getSettings().getAotNumberOfIterations());
        textField.setValue("");

        Button button = new Button("Save");
        button.setStyleName("settingsProperty");
        button.addClickListener(event -> {
            selection.getSettings().setAotNumberOfIterations(Integer.valueOf(textField.getValue()));
            textField.setCaption("Now: " + selection.getSettings().getAotNumberOfIterations());
        });

        HorizontalLayout propertyLayout = new HorizontalLayout();
        propertyLayout.addComponents(label, textField, button);
        verticalLayout.addComponents(propertyLayout);
    }
}
