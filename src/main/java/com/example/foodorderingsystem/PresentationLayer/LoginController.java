package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Customer;
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

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private Label errorMessage;

    private CustomerDataAccess customerDataAccess;

    public LoginController() {
        customerDataAccess = new CustomerDataAccess();
    }

    @FXML
    protected void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        try {
            // Validate credentials - this method would need to be added to CustomerDataAccess
            Customer customer = customerDataAccess.validateCustomer(email, password);

            if (customer != null) {
                // Store user session
                SessionManager.getInstance().setCurrentUser(customer);

                // Navigate to main dashboard
                navigateToDashboard(event);
            } else {
                showError("Invalid email or password");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
        }
    }

    @FXML
    protected void handleRegisterLink(ActionEvent event) {
        try {
            Parent registerView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/register-view.fxml"));
            Scene registerScene = new Scene(registerView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(registerScene);
            currentStage.show();
        } catch (IOException e) {
            showError("Error loading registration page: " + e.getMessage());
        }
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

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }
}
