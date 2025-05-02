package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.DataAccessLayer.CustomerDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.OrderDataAccess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    private Label userFullNameLabel;

    @FXML
    private Label userEmailLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label middleNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label createdDateLabel;

    @FXML
    private VBox addressesContainer;

    @FXML
    private VBox phonesContainer;

    @FXML
    private VBox paymentsContainer;

    @FXML
    private Label cartItemCount;

    @FXML
    private Button homeButton;

    @FXML
    private Button restaurantsButton;

    @FXML
    private Button ordersButton;

    @FXML
    private Button cartButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    private CustomerDataAccess customerDataAccess;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the data access object for database operations
        customerDataAccess = new CustomerDataAccess();

        // Load user data
        loadUserProfile();
        updateCartCount();
    }

    private void loadUserProfile() {
        Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
        if (customer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Not logged in", "You must be logged in to view the profile.");
            return;
        }

        // Set user information in UI
        String fullName = customer.getFirstName();
        if (customer.getMiddleName() != null && !customer.getMiddleName().isEmpty()) {
            fullName += " " + customer.getMiddleName();
        }
        fullName += " " + customer.getLastName();

        userFullNameLabel.setText(fullName);
        userEmailLabel.setText(customer.getEmail());

        firstNameLabel.setText(customer.getFirstName());
        middleNameLabel.setText(customer.getMiddleName() != null ? customer.getMiddleName() : "N/A");
        lastNameLabel.setText(customer.getLastName());
        emailLabel.setText(customer.getEmail());

        // Use current date for demo purposes (in a real app, this would come from the database)
        createdDateLabel.setText(new SimpleDateFormat("MMMM d, yyyy").format(new Date()));

        // Load addresses
        loadAddresses(customer);

        // Load phone numbers
        loadPhoneNumbers(customer);

        // Load payment methods
        loadPaymentMethods(customer);
    }

    private void loadAddresses(Customer customer) {
        addressesContainer.getChildren().clear();

        if (customer.getLocation() == null || customer.getLocation().isEmpty()) {
            Label noAddressLabel = new Label("No addresses found. Add your first address.");
            noAddressLabel.setStyle("-fx-text-fill: #757575;");
            addressesContainer.getChildren().add(noAddressLabel);
            return;
        }

        for (Location location : customer.getLocation()) {
            VBox addressCard = createAddressCard(location);
            addressesContainer.getChildren().add(addressCard);
        }
    }

    private VBox createAddressCard(Location location) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");

        Label cityLabel = new Label("City: " + location.getCity());
        Label streetLabel = new Label("Street: " + location.getStreetName() + " " + location.getStreetNumber());

        HBox actions = new HBox(10);
        actions.setPadding(new Insets(5, 0, 0, 0));

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> handleEditAddress(location));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #f44336;");
        deleteButton.setOnAction(e -> handleDeleteAddress(location));

        actions.getChildren().addAll(editButton, deleteButton);
        card.getChildren().addAll(cityLabel, streetLabel, actions);

        return card;
    }

    private void loadPhoneNumbers(Customer customer) {
        phonesContainer.getChildren().clear();

        if (customer.getPhoneNumbers() == null || customer.getPhoneNumbers().isEmpty()) {
            Label noPhoneLabel = new Label("No phone numbers found. Add your first phone number.");
            noPhoneLabel.setStyle("-fx-text-fill: #757575;");
            phonesContainer.getChildren().add(noPhoneLabel);
            return;
        }

        for (String phone : customer.getPhoneNumbers()) {
            HBox phoneEntry = createPhoneEntry(phone);
            phonesContainer.getChildren().add(phoneEntry);
        }
    }

    private HBox createPhoneEntry(String phone) {
        HBox entry = new HBox(10);
        entry.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");

        Label phoneLabel = new Label(phone);
        phoneLabel.setPrefWidth(200);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> handleEditPhone(phone));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #f44336;");
        deleteButton.setOnAction(e -> handleDeletePhone(phone));

        entry.getChildren().addAll(phoneLabel, editButton, deleteButton);

        return entry;
    }

    private void loadPaymentMethods(Customer customer) {
        paymentsContainer.getChildren().clear();

        // For demo purposes, show a placeholder
        Label noPaymentLabel = new Label("No payment methods found. Add your first payment method.");
        noPaymentLabel.setStyle("-fx-text-fill: #757575;");
        paymentsContainer.getChildren().add(noPaymentLabel);
    }

    private void updateCartCount() {
        if (SessionManager.getInstance().getUserCart() != null) {
            int count = SessionManager.getInstance().getUserCart().getItemCount();
            cartItemCount.setText(String.valueOf(count));
        } else {
            cartItemCount.setText("0");
        }
    }

    @FXML
    protected void handleEditProfile() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();

        TextField firstNameField = new TextField(customer.getFirstName());
        TextField middleNameField = new TextField(customer.getMiddleName() != null ? customer.getMiddleName() : "");
        TextField lastNameField = new TextField(customer.getLastName());
        TextField emailField = new TextField(customer.getEmail());

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Middle Name (optional):"), 0, 1);
        grid.add(middleNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                customer.setFirstName(firstNameField.getText());
                customer.setMiddleName(middleNameField.getText());
                customer.setLastName(lastNameField.getText());
                customer.setEmail(emailField.getText());
                return customer;
            }
            return null;
        });

        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(updatedCustomer -> {
            try {
                // Update the database with the modified profile
                customerDataAccess.updateCustomer(updatedCustomer);

                // Reload the profile information
                loadUserProfile();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Updated",
                        "Your profile has been updated successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                        "Failed to update profile: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    protected void handleChangePassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current password and new password");

        ButtonType confirmButtonType = new ButtonType("Change Password", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 150, 10, 10));

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");

        content.getChildren().addAll(
                new Label("Current Password:"), currentPasswordField,
                new Label("New Password:"), newPasswordField,
                new Label("Confirm New Password:"), confirmPasswordField
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                if (newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    return newPasswordField.getText();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Passwords don't match",
                            "The new password and confirmation password do not match.");
                    return null;
                }
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            // Here you would update the password in the database
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password Changed",
                    "Your password has been changed successfully!");
        });
    }

    @FXML
    protected void handleAddAddress() {
        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle("Add New Address");
        dialog.setHeaderText("Enter address details");

        ButtonType addButtonType = new ButtonType("Add Address", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cityField = new TextField();
        cityField.setPromptText("City");

        TextField streetNameField = new TextField();
        streetNameField.setPromptText("Street Name");

        TextField streetNumberField = new TextField();
        streetNumberField.setPromptText("Street Number");

        grid.add(new Label("City:"), 0, 0);
        grid.add(cityField, 1, 0);
        grid.add(new Label("Street Name:"), 0, 1);
        grid.add(streetNameField, 1, 1);
        grid.add(new Label("Street Number:"), 0, 2);
        grid.add(streetNumberField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Location(
                        cityField.getText(),
                        streetNameField.getText(),
                        streetNumberField.getText()
                );
            }
            return null;
        });

        Optional<Location> result = dialog.showAndWait();
        result.ifPresent(location -> {
            try {
                // Get the current customer
                Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
                int customerId = customer.getCustomerId();

                // Add the location directly using addCustomerLocation
                customerDataAccess.addCustomerLocation(customerId, location);

                // Add to customer's local list for UI update
                customer.getLocation().add(location);

                // Refresh the UI
                loadAddresses(customer);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Address Added",
                        "Your new address has been added successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                        "Failed to save address: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleEditAddress(Location location) {
        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle("Edit Address");
        dialog.setHeaderText("Edit address details");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cityField = new TextField(location.getCity());
        TextField streetNameField = new TextField(location.getStreetName());
        TextField streetNumberField = new TextField(location.getStreetNumber());

        grid.add(new Label("City:"), 0, 0);
        grid.add(cityField, 1, 0);
        grid.add(new Label("Street Name:"), 0, 1);
        grid.add(streetNameField, 1, 1);
        grid.add(new Label("Street Number:"), 0, 2);
        grid.add(streetNumberField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                location.setCity(cityField.getText());
                location.setStreetName(streetNameField.getText());
                location.setStreetNumber(streetNumberField.getText());
                return location;
            }
            return null;
        });

        Optional<Location> result = dialog.showAndWait();
        result.ifPresent(updatedLocation -> {
            try {
                Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();

                // Update the database with the modified address
                customerDataAccess.updateCustomer(customer);

                loadAddresses(customer);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Address Updated",
                        "Your address has been updated successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                        "Failed to update address: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleDeleteAddress(Location location) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Address");
        confirmAlert.setContentText("Are you sure you want to delete this address?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
                customer.getLocation().remove(location);

                // Update the database after removing the address
                customerDataAccess.updateCustomer(customer);

                loadAddresses(customer);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Address Deleted",
                        "Your address has been deleted successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                        "Failed to delete address: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void handleAddPhone() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Phone Number");
        dialog.setHeaderText("Enter your phone number");
        dialog.setContentText("Phone Number:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(phone -> {
            try {
                Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
                customer.getPhoneNumbers().add(phone);

                // Update the database with the new phone number
                customerDataAccess.updateCustomer(customer);

                loadPhoneNumbers(customer);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Phone Number Added",
                        "Your new phone number has been added successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                        "Failed to save phone number: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleEditPhone(String oldPhone) {
        TextInputDialog dialog = new TextInputDialog(oldPhone);
        dialog.setTitle("Edit Phone Number");
        dialog.setHeaderText("Update your phone number");
        dialog.setContentText("Phone Number:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPhone -> {
            try {
                Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
                int index = customer.getPhoneNumbers().indexOf(oldPhone);
                if (index >= 0) {
                    customer.getPhoneNumbers().set(index, newPhone);

                    // Update the database with the modified phone number
                    customerDataAccess.updateCustomer(customer);

                    loadPhoneNumbers(customer);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Phone Number Updated",
                            "Your phone number has been updated successfully!");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                        "Failed to update phone number: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleDeletePhone(String phone) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Phone Number");
        confirmAlert.setContentText("Are you sure you want to delete this phone number?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
                customer.getPhoneNumbers().remove(phone);

                // Update the database after removing the phone number
                customerDataAccess.updateCustomer(customer);

                loadPhoneNumbers(customer);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Phone Number Deleted",
                        "Your phone number has been deleted successfully!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database Error",
                        "Failed to delete phone number: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void handleAddPayment() {
        showAlert(Alert.AlertType.INFORMATION, "Coming Soon", "Payment Methods",
                "Adding payment methods will be available in a future update!");
    }

    @FXML
    protected void handleDeleteAccount() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Account Deletion");
        confirmAlert.setHeaderText("Delete Your Account");
        confirmAlert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Here you would call a method to delete the account from the database

            // For now, just log the user out
            SessionManager.getInstance().logout();

            try {
                Parent loginView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/login-view.fxml"));
                Scene loginScene = new Scene(loginView);
                Stage currentStage = (Stage) userFullNameLabel.getScene().getWindow();
                currentStage.setScene(loginScene);
                currentStage.show();
            } catch (IOException e) {
                System.err.println("Error loading login view: " + e.getMessage());
            }
        }
    }

    private void showInputDialog(String title, String header) {
        // Simple placeholder for edit functionality
        showAlert(Alert.AlertType.INFORMATION, title, header,
                "This feature will be implemented in a future update.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigation handlers
    @FXML
    protected void handleHomeButton(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/dashboard-view.fxml"));
            Scene dashboardScene = new Scene(dashboardView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(dashboardScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error loading dashboard view: " + e.getMessage());
        }
    }

    @FXML
    protected void handleRestaurantsButton(ActionEvent event) {
        // Navigate to restaurants view (dashboard has restaurants)
        handleHomeButton(event);
    }

    @FXML
    protected void handleOrdersButton(ActionEvent event) {
        try {
            Parent ordersView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/orders-view.fxml"));
            Scene ordersScene = new Scene(ordersView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(ordersScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error loading orders view: " + e.getMessage());
        }
    }

    @FXML
    protected void handleCartButton(ActionEvent event) {
        try {
            Parent cartView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/cart-view.fxml"));
            Scene cartScene = new Scene(cartView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(cartScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error loading cart view: " + e.getMessage());
        }
    }

    @FXML
    protected void handleProfileButton(ActionEvent event) {
        // Instead of showing the "under development" message, just refresh the profile data
        loadUserProfile();
    }

    @FXML
    protected void handleLogoutButton(ActionEvent event) {
        // Clear user session
        SessionManager.getInstance().logout();

        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/login-view.fxml"));
            Scene loginScene = new Scene(loginView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error loading login view: " + e.getMessage());
        }
    }
}
