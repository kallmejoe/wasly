package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.DataAccessLayer.CustomerDataAccess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField middleNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField buildingField;

    @FXML
    private Button registerButton;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private Label errorMessage;

    private CustomerDataAccess customerDataAccess;

    public RegisterController() {
        customerDataAccess = new CustomerDataAccess();
    }

    @FXML
    protected void handleRegister(ActionEvent event) {
        // Clear any previous error messages
        errorMessage.setVisible(false);

        // Validate form inputs
        if (!validateInputs()) {
            return;
        }

        try {
            // Create the customer object
            String firstName = firstNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String phone = phoneField.getText().trim();

            // Create location
            String city = cityField.getText().trim();
            String street = streetField.getText().trim();
            String building = buildingField.getText().trim();
            Location location = new Location(city, street, building);

            // Create customer with the provided information
            Customer newCustomer = new Customer(
                email,
                password,
                firstName,
                middleName,
                lastName,
                Arrays.asList(phone),
                Arrays.asList(location)
            );

            // Register the customer in the database
            boolean success = customerDataAccess.registerCustomer(newCustomer);

            if (success) {
                // Login the new user
                Customer registeredCustomer = customerDataAccess.validateCustomer(email, password);
                SessionManager.getInstance().setCurrentUser(registeredCustomer);

                // Navigate to the dashboard
                navigateToDashboard(event);
            } else {
                showError("Failed to register. Please try again later.");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
        }
    }

    @FXML
    protected void handleLoginLink(ActionEvent event) {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/login-view.fxml"));
            Scene loginScene = new Scene(loginView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            showError("Error loading login page: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Check required fields
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            passwordField.getText().isEmpty() ||
            confirmPasswordField.getText().isEmpty() ||
            phoneField.getText().trim().isEmpty() ||
            cityField.getText().trim().isEmpty() ||
            streetField.getText().trim().isEmpty() ||
            buildingField.getText().trim().isEmpty()) {

            showError("Please fill in all required fields");
            return false;
        }

        // Validate email format
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address");
            return false;
        }

        // Check password length
        if (passwordField.getText().length() < 6) {
            showError("Password must be at least 6 characters long");
            return false;
        }

        // Check if passwords match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return false;
        }

        // Validate phone number (simple validation)
        String phone = phoneField.getText().trim();
        if (!phone.matches("\\d+")) {
            showError("Please enter a valid phone number (digits only)");
            return false;
        }

        return true;
    }

    private void navigateToDashboard(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/dashboard-view.fxml"));
            Scene dashboardScene = new Scene(dashboardView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(dashboardScene);
            currentStage.show();
        } catch (IOException e) {
            showError("Error loading dashboard: " + e.getMessage());
        }
    }
    @FXML
    protected void handleAdminRegisterLink(ActionEvent event) {
        try {
            Parent adminRegisterView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/admin-register-view.fxml"));
            Scene adminRegisterScene = new Scene(adminRegisterView);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(adminRegisterScene);
            window.show();
        } catch (IOException e) {
            showError("Error navigating to admin registration: " + e.getMessage());
        }
    }
    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }
}
