package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.RestaurantDataAccess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RestaurantDialogController implements Initializable {

    @FXML private DialogPane dialogPane;
    @FXML private Label lblRestaurantId;
    @FXML private TextField txtName;
    @FXML private TextArea txtAddress;
    @FXML private TextField txtPhone;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtLatitude;
    @FXML private TextField txtLongitude;
    @FXML private Label lblStatus;

    private Restaurant restaurant;
    private RestaurantDataAccess restaurantDataAccess;
    private boolean isNewRestaurant = true;
    private Dialog<Restaurant> dialog;

    public void setDialog(Dialog<Restaurant> dialog) {
        this.dialog = dialog;

        // Set up the result converter for the dialog
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (validateInput()) {
                    updateRestaurantFromForm();
                    if (saveRestaurant()) {
                        return restaurant;
                    }
                }
                return null;
            }
            return null;
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        restaurantDataAccess = new RestaurantDataAccess();

        // Set default values for numeric fields
        txtLatitude.setText("0.0");
        txtLongitude.setText("0.0");
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        this.isNewRestaurant = (restaurant == null || restaurant.getRestaurantId() == 0);

        if (!isNewRestaurant) {
            // Populate the form fields with the restaurant data
            lblRestaurantId.setText(String.valueOf(restaurant.getRestaurantId()));
            txtName.setText(restaurant.getName());
            txtDescription.setText(restaurant.getDescription() != null ? restaurant.getDescription() : "");

            if (restaurant.getPhoneNo() != null && !restaurant.getPhoneNo().isEmpty()) {
                txtPhone.setText(restaurant.getPhoneNo().get(0));
            }

            if (restaurant.getLocations() != null && !restaurant.getLocations().isEmpty()) {
                Location location = restaurant.getLocations().get(0);
                txtAddress.setText(location.getAddress() != null ? location.getAddress() : "");
                txtLatitude.setText(String.valueOf(location.getLatitude()));
                txtLongitude.setText(String.valueOf(location.getLongitude()));
            }
        } else {
            // Creating a new restaurant
            this.restaurant = new Restaurant();
        }
    }

    private void updateRestaurantFromForm() {
        restaurant.setName(txtName.getText().trim());
        restaurant.setDescription(txtDescription.getText().trim());

        // Handle phone numbers
        List<String> phoneNumbers = new ArrayList<>();
        if (!txtPhone.getText().trim().isEmpty()) {
            phoneNumbers.add(txtPhone.getText().trim());
        }
        restaurant.setPhoneNo(phoneNumbers);

        // Handle location
        List<Location> locations = new ArrayList<>();
        Location location = new Location();
        location.setAddress(txtAddress.getText().trim());

        try {
            if (!txtLatitude.getText().trim().isEmpty()) {
                location.setLatitude(Double.parseDouble(txtLatitude.getText().trim()));
            }

            if (!txtLongitude.getText().trim().isEmpty()) {
                location.setLongitude(Double.parseDouble(txtLongitude.getText().trim()));
            }

            locations.add(location);
            restaurant.setLocations(locations);
        } catch (NumberFormatException e) {
            // Use default values for latitude and longitude
            location.setLatitude(0.0);
            location.setLongitude(0.0);
            locations.add(location);
            restaurant.setLocations(locations);
        }
    }

    public boolean saveRestaurant() {
        try {
            if (isNewRestaurant) {
                restaurantDataAccess.insertRestaurant(restaurant);
                // The method doesn't return an ID, so we rely on the method setting the ID in the restaurant object
                return restaurant.getRestaurantId() > 0;
            } else {
                return restaurantDataAccess.updateRestaurant(restaurant);
            }
        } catch (SQLException e) {
            showError("Error saving restaurant: " + e.getMessage());
            return false;
        }
    }

    private boolean validateInput() {
        lblStatus.setText("");

        if (txtName.getText().trim().isEmpty()) {
            showError("Restaurant name is required");
            return false;
        }

        if (txtPhone.getText().trim().isEmpty()) {
            showError("Phone number is required");
            return false;
        }

        if (txtAddress.getText().trim().isEmpty()) {
            showError("Address is required");
            return false;
        }

        // Validate latitude and longitude if provided
        try {
            if (!txtLatitude.getText().trim().isEmpty()) {
                Double.parseDouble(txtLatitude.getText().trim());
            }
            if (!txtLongitude.getText().trim().isEmpty()) {
                Double.parseDouble(txtLongitude.getText().trim());
            }
        } catch (NumberFormatException e) {
            showError("Latitude and longitude must be valid numbers");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        lblStatus.setText(message);
    }

    // Method called when the OK button is clicked
    public void handleOk() {
        if (validateInput()) {
            updateRestaurantFromForm();
            saveRestaurant();
        }
    }
}
