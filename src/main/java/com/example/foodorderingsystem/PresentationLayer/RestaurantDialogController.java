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
                // Call handleOk to save the restaurant
                handleOk();
                // Return the restaurant if it was saved successfully
                if (restaurant.getRestaurantId() > 0 || !isNewRestaurant) {
                    return restaurant;
                }
            }
            return null;
        });

        // Set up button listeners
        dialogPane.lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            // We need to consume the event here to prevent the dialog from closing if validation fails
            if (!validateInput() || !saveRestaurant()) {
                event.consume();
            }
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
        restaurant.setName(txtName != null && txtName.getText() != null ? txtName.getText().trim() : "");
        restaurant.setDescription(txtDescription != null && txtDescription.getText() != null ? txtDescription.getText().trim() : "");

        // Handle phone numbers
        List<String> phoneNumbers = new ArrayList<>();
        if (txtPhone != null && txtPhone.getText() != null && !txtPhone.getText().trim().isEmpty()) {
            phoneNumbers.add(txtPhone.getText().trim());
        }
        restaurant.setPhoneNo(phoneNumbers);

        // Handle location
        List<Location> locations = new ArrayList<>();
        Location location = new Location();

        String addressInput = txtAddress != null && txtAddress.getText() != null ? txtAddress.getText().trim() : "";
        String city = "";
        String streetName = "";
        String streetNumber = "";

        if (!addressInput.isEmpty()) {
            String[] parts = addressInput.split(",");
            if (parts.length >= 3) {
                city = parts[0].trim();
                streetName = parts[1].trim();
                streetNumber = parts[2].trim();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Address Format");
                alert.setHeaderText("Address Parsing Failed");
                alert.setContentText("Please enter address in the format:\nCity, Street Name, Street Number");
                alert.showAndWait();
                return; // exit early if format is invalid
            }
        }

        location.setCity(city);
        location.setStreetName(streetName);
        location.setStreetNumber(streetNumber);

        try {
            if (txtLatitude != null && txtLatitude.getText() != null && !txtLatitude.getText().trim().isEmpty()) {
                location.setLatitude(Double.parseDouble(txtLatitude.getText().trim()));
            } else {
                location.setLatitude(0.0);
            }

            if (txtLongitude != null && txtLongitude.getText() != null && !txtLongitude.getText().trim().isEmpty()) {
                location.setLongitude(Double.parseDouble(txtLongitude.getText().trim()));
            } else {
                location.setLongitude(0.0);
            }
        } catch (NumberFormatException e) {
            location.setLatitude(0.0);
            location.setLongitude(0.0);
        }

        locations.add(location);
        restaurant.setLocations(locations);
    }




    public boolean saveRestaurant() {
        try {
            boolean success;
            if (isNewRestaurant) {
                restaurantDataAccess.insertRestaurant(restaurant);
                success = restaurant.getRestaurantId() > 0;
                if (success) {
                    lblStatus.setText("Restaurant created successfully!");
                }
            } else {
                success = restaurantDataAccess.updateRestaurant(restaurant);
                if (success) {
                    lblStatus.setText("Restaurant updated successfully!");
                }
            }

            if (success) {
                // Update the form fields with the saved data
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
            }

            return success;
        } catch (SQLException e) {
            showError("Error saving restaurant: " + e.getMessage());
            return false;
        }
    }

    private boolean validateInput() {
        lblStatus.setText("");

        // Check if txtName is null or empty
        if (txtName == null || txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            showError("Restaurant name is required");
            return false;
        }

        // Check if txtPhone is null or empty
        if (txtPhone == null || txtPhone.getText() == null || txtPhone.getText().trim().isEmpty()) {
            showError("Phone number is required");
            return false;
        }

        // Check if txtAddress is null or empty
        if (txtAddress == null || txtAddress.getText() == null || txtAddress.getText().trim().isEmpty()) {
            showError("Address is required");
            return false;
        }

        // Validate latitude and longitude if provided
        try {
            if (txtLatitude != null && txtLatitude.getText() != null && !txtLatitude.getText().trim().isEmpty()) {
                Double.parseDouble(txtLatitude.getText().trim());
            }
            if (txtLongitude != null && txtLongitude.getText() != null && !txtLongitude.getText().trim().isEmpty()) {
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
    @FXML
    public void handleOk() {
//        if (validateInput()) {
            updateRestaurantFromForm();
            saveRestaurant();
//        }
    }
}
