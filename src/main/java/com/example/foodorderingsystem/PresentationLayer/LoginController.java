package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Admin;
import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.DataAccessLayer.AdminDataAccess;
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
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMessage;

    private CustomerDataAccess customerDataAccess;
    private AdminDataAccess adminDataAccess;

    public void initialize() {
        try {
            customerDataAccess = new CustomerDataAccess();
            adminDataAccess = new AdminDataAccess();
            errorMessage.setVisible(false);
        } catch (Exception e) {
            showError("Failed to initialize database connections: " + e.getMessage());
        }
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
            // First, try to validate as a customer
            Customer customer = customerDataAccess.validateCustomer(email, password);

            if (customer != null) {
                // Customer login successful
                SessionManager.getInstance().setCurrentUser(customer);
                SessionManager.getInstance().setUserType("customer");

                // Navigate to customer dashboard
                navigateToDashboard(event);
                return;
            }

            // If not a customer, try to validate as an admin
            Admin admin = adminDataAccess.validateAdmin(email, password);

            if (admin != null) {
                // Admin login successful
                SessionManager.getInstance().setCurrentUser(admin);
                SessionManager.getInstance().setUserType("admin");

                // Navigate to admin dashboard
                navigateToAdminDashboard(event);
                return;
            }

            // If we got here, neither customer nor admin authentication succeeded
            showError("Invalid email or password");

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
        }
    }

    private void navigateToDashboard(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/dashboard-view.fxml"));
            Scene dashboardScene = new Scene(dashboardView);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(dashboardScene);
            window.show();
        } catch (IOException e) {
            showError("Error navigating to dashboard: " + e.getMessage());
        }
    }

    private void navigateToAdminDashboard(ActionEvent event) {
        try {
            Parent adminDashboardView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/admin-dashboard.fxml"));
            Scene adminDashboardScene = new Scene(adminDashboardView);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(adminDashboardScene);
            window.show();
        } catch (IOException e) {
            showError("Error navigating to admin dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }

    @FXML
    protected void handleRegisterLink(ActionEvent event) {
        try {
            Parent registerView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/register-view.fxml"));
            Scene registerScene = new Scene(registerView);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(registerScene);
            window.show();
        } catch (IOException e) {
            showError("Error navigating to registration: " + e.getMessage());
        }
    }
}