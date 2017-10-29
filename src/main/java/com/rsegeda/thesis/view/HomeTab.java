package com.rsegeda.thesis.view;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.config.Constants;
import com.rsegeda.thesis.config.Properties;
import com.rsegeda.thesis.location.LocationDto;
import com.rsegeda.thesis.location.LocationService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ImageRenderer;
import lombok.extern.slf4j.Slf4j;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.vaadin.addons.autocomplete.AutocompleteExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Roman Segeda on 09/04/2017.
 */
@Slf4j
@Component
public class HomeTab extends HorizontalLayout {

    private final transient Properties properties;
    private transient Selection selection;
    private transient LocationService locationService;
    /*
    Right panel
     */
    private final VerticalLayout rightPanel = new VerticalLayout();
    private transient JmsTemplate jmsTemplate;
    private GoogleMap googleMap;
    private List<GoogleMapMarker> googleMapMarkers = new ArrayList<>();
    /*
    Left panel
     */
    private transient Place.Prediction selectedPrediction = null;
    private transient List<LocationDto> locationList;
    private transient List<Place.Prediction> predictions = new ArrayList<>();

    private VerticalLayout leftPanel = new VerticalLayout();
    private HorizontalLayout basicSetupLayout = new HorizontalLayout();
    private List<String> algorithms = new ArrayList<>();
    private List<String> goals = new ArrayList<>();
    private HorizontalLayout addLocationLayout = new HorizontalLayout();
    private HorizontalLayout locationGridLayout = new HorizontalLayout();
    private Grid<LocationDto> locationGrid = new Grid<>();
    private Label addLocationLabel = new Label("New location");
    private TextField addLocationTextField = new TextField();
    private ComboBox<Place.Prediction> addLocationComboBox = new ComboBox<>();
    private Button addLocationButton = new Button();

    private boolean created = false;

    @Autowired
    public HomeTab(Selection selection, LocationService locationService, JmsTemplate jmsTemplate,
                   Properties properties) {
        this.selection = selection;
        this.locationService = locationService;
        this.jmsTemplate = jmsTemplate;
        this.properties = properties;
    }

    private void setupRightPanel() {
        rightPanel.setSizeFull();

        googleMap.setCenter(new LatLon(52.0690115, 19.4790478));
        googleMap.setZoom(6);
        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);
        googleMap.setWidth(100.0f, Unit.PERCENTAGE);
        googleMap.setHeight(95.0f, Unit.PERCENTAGE);
        googleMap.setVisible(true);
        rightPanel.addComponent(googleMap);
    }

    private void setupLeftPanel() {
        leftPanel.setSpacing(true);

        setupBasicSetupLayout();
        leftPanel.addComponent(basicSetupLayout);

        setupAddLocationLayout();
        leftPanel.addComponent(addLocationLayout);

        setupLocationGridLayout();
        leftPanel.addComponent(locationGridLayout);
    }

    private void setupBasicSetupLayout() {
        RadioButtonGroup<String> algorithmRadioButtonGroup;
        algorithmRadioButtonGroup = new RadioButtonGroup<>("Algorithm", algorithms);
        algorithmRadioButtonGroup.setSelectedItem(algorithms.get(0));

        algorithmRadioButtonGroup.addStyleName("algorithmOption");
        basicSetupLayout.addComponent(algorithmRadioButtonGroup);

        RadioButtonGroup<String> goalRadioButtonGroup = new RadioButtonGroup<>("Goal", goals);
        goalRadioButtonGroup.setSelectedItem(goals.get(0));
        basicSetupLayout.addComponent(goalRadioButtonGroup);

        Button runButton = new Button("Run");
        runButton.setIcon(VaadinIcons.PLAY);

        runButton.addClickListener((Button.ClickListener) clickEvent -> {
            Optional<String> selectedItem = algorithmRadioButtonGroup.getSelectedItem();
            selectedItem.ifPresent(this::runAlgorithm);
        });
        basicSetupLayout.addComponent(runButton);

        basicSetupLayout.setSizeFull();
    }

    private void runAlgorithm(String algorithm) {

        if (locationList.size() < 2) {
            new Notification("Pathfinder needs more than 1 location to work",
                    "You have chosen 1 or less places to visit",
                    Notification.Type.WARNING_MESSAGE, true)
                    .show(Page.getCurrent());
            return;
        }
        selection.setAlgorithmName(algorithm);
        selection.setLocationDtos(new ArrayList<>());
        locationList.forEach(locationDto -> selection.getLocationDtos().add(locationDto));

        notifyMainTabSheet();
    }

    private void notifyMainTabSheet() {
        jmsTemplate.convertAndSend("runAlgorithm", "");
    }

    private void setupAddLocationLayout() {
        loadData();

        addLocationComboBox.setItemCaptionGenerator(Place.Prediction::getDescription);

        addLocationLayout.setStyleName("newLocation");
        addLocationTextField.setStyleName("addLocationTextField");

        AutocompleteExtension<String> placesExtension = new AutocompleteExtension<>(
                addLocationTextField);
        placesExtension.setSuggestionGenerator(this::suggestPrediction);
        placesExtension.setSuggestionDelay(500);

        placesExtension.addSuggestionSelectListener(event -> event.getSelectedItem()
                .ifPresent(selected -> predictions.stream()
                        .filter(prediction -> prediction.getDescription() != null
                                && prediction.getDescription().equalsIgnoreCase(selected))
                        .findFirst().ifPresent(prediction -> selectedPrediction = prediction)));

        addLocationButton.setIcon(VaadinIcons.PLUS);
        addLocationButton.addClickListener((Button.ClickListener) clickEvent -> {
            Place newPlace = null;

            try {

                if (selectedPrediction.getPlaceId() == null) {
                    return;
                }

                Places.Response<Place> searchPlaceResponse =
                        Places.details(Places.Params.create().placeId(selectedPrediction.getPlaceId().getId()));

                if (searchPlaceResponse.getStatus() != null
                        && searchPlaceResponse.getStatus().equals("OVER_QUERY_LIMIT")) {
                    Notification.show("Google Places Web API has reached 1k requests limit :(",
                            Notification.Type.TRAY_NOTIFICATION);
                    return;
                }

                newPlace = searchPlaceResponse.getResult();

            } catch (IOException e) {
                log.error("Error while adding location", e);
            }

            if (newPlace == null) {
                return;
            }

            LocationDto locationDto = LocationDto.builder()
                    .id(new Random().nextLong())
                    .placeId(String.valueOf(newPlace.getPlaceId()))
                    .placeName(newPlace.getName())
                    .latLon(new LatLon(newPlace.getLatitude(), newPlace.getLongitude()))
                    .build();

            locationList.add(locationDto);

            locationService.saveLocation(locationDto);

            createMarker(locationDto.getPlaceName(), locationDto.getLatLon());

            locationGrid.setItems(locationList);
        });

        addLocationLayout.addComponents(addLocationLabel, addLocationTextField, addLocationButton);
    }

    private void loadData() {

        locationList = locationService.getLocationList();
        locationGrid.setItems(locationList);

        restoreMarkers();

    }

    private void restoreMarkers() {

        locationList.forEach(locationDto -> createMarker(locationDto.getPlaceName(), locationDto.getLatLon()));
    }

    private void createMarker(String name, LatLon latLon) {

        GoogleMapMarker newMarker = new GoogleMapMarker(name,
                new LatLon(latLon.getLat(), latLon.getLon()), false, null);
        googleMapMarkers.add(newMarker);
        googleMap.addMarker(newMarker);
    }

    private void setupLocationGridLayout() {

        locationGridLayout.setSizeFull();
        locationGridLayout.setStyleName("locationGridLayout");

        setupLocationGrid();
        locationGridLayout.addComponent(locationGrid);
    }

    private List<String> suggestPrediction(String query, int cap) {

        try {
            Places.Response<List<Place.Prediction>> autocompleteResponse =
                    Places.autocomplete(Places.Params.create().query(String.valueOf(query)));

            if (autocompleteResponse.getStatus() != null
                    && autocompleteResponse.getStatus().equals("OVER_QUERY_LIMIT")) {

                Notification.show("Google Places Web API has reached 1k requests limit :(",
                        Notification.Type.TRAY_NOTIFICATION);
            }

            predictions = autocompleteResponse.getResult();
        } catch (IOException e) {
            log.error("Cannot suggest a prediction", e);
        }

        if (predictions == null || predictions.isEmpty()) {
            return Collections.emptyList();
        } else {
            return predictions.stream().filter(
                    prediction -> (prediction.getDescription() != null) && prediction.getDescription().contains(query)
            ).limit(cap).map(Place.Prediction::getDescription).collect(Collectors.toList());
        }
    }

    private void setupLocationGrid() {

        locationGrid.setSizeFull();
        locationGrid.addStyleName("locationGrid");
        locationGrid.addColumn(LocationDto::getPlaceName).setCaption("Name");
        locationGrid.addColumn(location -> new ThemeResource("img/trash.png"), new ImageRenderer<>(
                clickEvent -> {
                    LocationDto selectedLocation = clickEvent.getItem();
                    locationList.remove(selectedLocation);

                    Optional<LocationDto> locationDtoOptional = locationService.getLocation(selectedLocation.getId());
                    locationDtoOptional.ifPresent(locationDto -> locationService.deleteLocation(locationDto.getId()));
                    locationGrid.setItems(locationList);
                    Optional<GoogleMapMarker> markerToRemove = googleMapMarkers.stream()
                            .filter(googleMapMarker -> selectedLocation.getPlaceName() != null
                                    && selectedLocation.getPlaceName().equalsIgnoreCase(googleMapMarker.getCaption()))
                            .findAny();
                    markerToRemove.ifPresent(googleMapMarker -> googleMap.removeMarker(googleMapMarker));

                    if (locationList.isEmpty()) {
                        googleMap.clearMarkers();
                    }

                })).setCaption("Delete");

        // Allow column reordering
        locationGrid.setColumnReorderingAllowed(true);

        // Allow column hiding
        locationGrid.getColumns().forEach(column -> column.setHidable(true));
    }

    void init() {

        if (!created) {
            googleMap = new GoogleMap(properties.getApiKey(),
                    null, "english");

            locationList = new ArrayList<>();
            algorithms.addAll(Constants.ALGORITHMS);
            goals.addAll(Constants.GOALS);

            setupLeftPanel();
            setupRightPanel();

            addComponents(leftPanel, rightPanel);
            setSizeFull();
            created = true;
        }

    }
}
