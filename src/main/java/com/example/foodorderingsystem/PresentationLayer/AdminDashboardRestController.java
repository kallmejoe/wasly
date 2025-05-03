package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.RestaurantDataAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminDashboardRestController implements Initializable {

    @FXML
    private TableView<Restaurant> tableView;

    @FXML
    private TableColumn<Restaurant, Integer> idColumn;

    @FXML
    private TableColumn<Restaurant, String> nameColumn;

    @FXML
    private TableColumn<Restaurant, String> phoneNumberColumn;

    @FXML
    private TableColumn<Restaurant, String> descriptionColumn;

    @FXML
    private TableColumn<Restaurant, String> cityColumn;

    @FXML
    private TableColumn<Restaurant, String> streetNameColumn;

    @FXML
    private TableColumn<Restaurant, String> streetNumberColumn;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField cityTextField;

    @FXML
    private TextField streetNameTextField;

    @FXML
    private TextField streetNumberTextField;

    @FXML
    private Button applyButton;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    private RestaurantDataAccess restaurantDataAccess;
    private ObservableList<Restaurant> restaurantList;
    private Restaurant selectedRestaurant;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the data access object
        restaurantDataAccess = new RestaurantDataAccess();
        restaurantList = FXCollections.observableArrayList();

        // Configure the table columns (link table columns to Restaurant properties)
        idColumn.setCellValueFactory(new PropertyValueFactory<>("restaurantId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // For location-related columns, use custom cell factories
        cityColumn.setCellValueFactory(cellData -> {
            Restaurant restaurant = cellData.getValue();
            if (restaurant != null && restaurant.getLocation() != null) {
                return new javafx.beans.property.SimpleStringProperty(restaurant.getLocation().getCity());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        streetNameColumn.setCellValueFactory(cellData -> {
            Restaurant restaurant = cellData.getValue();
            if (restaurant != null && restaurant.getLocation() != null) {
                return new javafx.beans.property.SimpleStringProperty(restaurant.getLocation().getStreetName());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        streetNumberColumn.setCellValueFactory(cellData -> {
            Restaurant restaurant = cellData.getValue();
            if (restaurant != null && restaurant.getLocation() != null) {
                return new javafx.beans.property.SimpleStringProperty(restaurant.getLocation().getStreetNumber());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // Set up button actions
        applyButton.setOnAction(this::handleApply);
        addButton.setOnAction(this::handleAdd);
        deleteButton.setOnAction(this::handleDelete);

        // Set table selection listener to populate form fields when a row is selected
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedRestaurant = newSelection; // Store the selected restaurant
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });

        // Load all restaurants from the database when the view is initialized
        loadRestaurants();
    }

    // Method to load restaurants from the database
    private void loadRestaurants() {
        try {
            // Query the database for all restaurants
            List<Restaurant> restaurants = restaurantDataAccess.getAllRestaurants();

            // Clear existing items and add all fetched restaurants
            restaurantList.clear();
            restaurantList.addAll(restaurants);

            // Set the items to the table view
            tableView.setItems(restaurantList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load restaurants: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to populate form fields when a restaurant is selected
    private void populateFields(Restaurant restaurant) {
        nameTextField.setText(restaurant.getName());
        phoneTextField.setText(restaurant.getPhone());
        descriptionTextField.setText(restaurant.getDescription());

        Location location = restaurant.getLocation();
        if (location != null) {
            cityTextField.setText(location.getCity());
            streetNameTextField.setText(location.getStreetName());
            streetNumberTextField.setText(location.getStreetNumber());
        } else {
            // Clear location fields if no location data
            cityTextField.clear();
            streetNameTextField.clear();
            streetNumberTextField.clear();
        }
    }

    private void clearFields() {
        nameTextField.clear();
        phoneTextField.clear();
        descriptionTextField.clear();
        cityTextField.clear();
        streetNameTextField.clear();
        streetNumberTextField.clear();
    }

    @FXML
    private void handleApply(ActionEvent event) {
        if (selectedRestaurant == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a restaurant to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        // Update restaurant properties
        selectedRestaurant.setName(nameTextField.getText().trim());
        selectedRestaurant.setPhone(phoneTextField.getText().trim());
        selectedRestaurant.setDescription(descriptionTextField.getText().trim());

        // Update location
        Location location = selectedRestaurant.getLocation();
        if (location == null) {
            location = new Location();
            List<Location> locations = new ArrayList<>();
            locations.add(location);
            selectedRestaurant.setLocations(locations);
        }

        location.setCity(cityTextField.getText().trim());
        location.setStreetName(streetNameTextField.getText().trim());
        location.setStreetNumber(streetNumberTextField.getText().trim());

        try {
            boolean success = restaurantDataAccess.updateRestaurant(selectedRestaurant);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Restaurant updated successfully.");
                loadRestaurants();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update restaurant.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating restaurant: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        // Create new restaurant
        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setName(nameTextField.getText().trim());
        newRestaurant.setPhone(phoneTextField.getText().trim());
        newRestaurant.setDescription(descriptionTextField.getText().trim());

        // Create location
        Location location = new Location();
        location.setCity(cityTextField.getText().trim());
        location.setStreetName(streetNameTextField.getText().trim());
        location.setStreetNumber(streetNumberTextField.getText().trim());

        List<Location> locations = new ArrayList<>();
        locations.add(location);
        newRestaurant.setLocations(locations);

        // Add phone to list
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(phoneTextField.getText().trim());
        newRestaurant.setPhoneNo(phoneNumbers);

        try {
            restaurantDataAccess.insertRestaurant(newRestaurant);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Restaurant added successfully.");
            clearFields();
            loadRestaurants();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding restaurant: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedRestaurant == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a restaurant to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Restaurant");
        confirmAlert.setContentText("Are you sure you want to delete the restaurant: " + selectedRestaurant.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                restaurantDataAccess.deleteRestaurant(selectedRestaurant.getRestaurantId());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Restaurant deleted successfully.");
                clearFields();
                loadRestaurants();
                selectedRestaurant = null;
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting restaurant: " + e.getMessage());
            }
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameTextField.getText().trim().isEmpty()) {
            errorMessage.append("Name cannot be empty.\n");
        }

        if (phoneTextField.getText().trim().isEmpty()) {
            errorMessage.append("Phone number cannot be empty.\n");
        }

        if (cityTextField.getText().trim().isEmpty()) {
            errorMessage.append("City cannot be empty.\n");
        }

        if (streetNameTextField.getText().trim().isEmpty()) {
            errorMessage.append("Street name cannot be empty.\n");
        }

        if (streetNumberTextField.getText().trim().isEmpty()) {
            errorMessage.append("Street number cannot be empty.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
