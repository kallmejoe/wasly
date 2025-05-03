package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Admin;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.AdminDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.RestaurantDataAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private Label lblAdminId;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private TextField txtPhone;
    @FXML private TextField txtSalary;
    @FXML private ComboBox<Restaurant> cmbRestaurant;
    @FXML private ListView<Restaurant> lstAssignedRestaurants;
    @FXML private Button btnAddRestaurant;
    @FXML private Button btnRemoveRestaurant;
    @FXML private Label lblStatus;
    @FXML private ButtonType btnOk;
    @FXML private ButtonType btnCancel;

    private Admin admin;
    private AdminDataAccess adminDataAccess;
    private RestaurantDataAccess restaurantDataAccess;
    private ObservableList<Restaurant> availableRestaurants;
    private ObservableList<Restaurant> assignedRestaurants;
    private boolean isNewAdmin = true;
    private Dialog<Admin> dialog;

    public void setDialog(Dialog<Admin> dialog) {
        this.dialog = dialog;

        // Set up dialog result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (validateInputs()) {
                    updateAdminFromInputs();
                    return admin;
                }
                return null;
            }
            return null;
        });
    }

    @FXML
    public void initialize() {
        // Initialize data access objects
        adminDataAccess = new AdminDataAccess();
        restaurantDataAccess = new RestaurantDataAccess();

        // Initialize collections
        availableRestaurants = FXCollections.observableArrayList();
        assignedRestaurants = FXCollections.observableArrayList();

        // Set up role combo box
        cmbRole.setItems(FXCollections.observableArrayList("Admin", "Super Admin"));
        cmbRole.getSelectionModel().selectFirst();

        // Set up restaurant combo box
        cmbRestaurant.setItems(availableRestaurants);
        cmbRestaurant.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        cmbRestaurant.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        // Set up assigned restaurants list view
        lstAssignedRestaurants.setItems(assignedRestaurants);
        lstAssignedRestaurants.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        // Set up button handlers
        btnAddRestaurant.setOnAction(event -> handleAddRestaurant());
        btnRemoveRestaurant.setOnAction(event -> handleRemoveRestaurant());

        // Load available restaurants
        loadAvailableRestaurants();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        isNewAdmin = admin.getAdminId() <= 0;

        if (!isNewAdmin) {
            // Existing admin - populate fields
            lblAdminId.setText(String.valueOf(admin.getAdminId()));
            txtFirstName.setText(admin.getFirstName());
            txtLastName.setText(admin.getLastName());
            txtEmail.setText(admin.getEmail());
            txtPassword.setText(""); // Don't display actual password for security reasons

            // Set phone if available
            if (admin.getPhoneNumbers() != null && !admin.getPhoneNumbers().isEmpty()) {
                txtPhone.setText(admin.getPhoneNumbers().get(0));
            }

            // Set salary
            txtSalary.setText(String.format("%.2f", admin.getSalary()));

            // Set role
//            String role = admin.isSuperAdmin() ? "Super Admin" : "Admin";
//            cmbRole.getSelectionModel().select(role);

            // Load assigned restaurants
            loadAssignedRestaurants();
        } else {
            // New admin - set defaults
            lblAdminId.setText("New");
            txtSalary.setText("0.00");
        }
    }

    private void loadAvailableRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantDataAccess.getAllRestaurants();
            availableRestaurants.setAll(restaurants);

            // Filter out already assigned restaurants if editing an existing admin
            if (!isNewAdmin && admin.getRestaurantsManaged() != null && !admin.getRestaurantsManaged().isEmpty()) {
                availableRestaurants.removeAll(assignedRestaurants);
            }
        } catch (SQLException e) {
            showError("Error loading restaurants: " + e.getMessage());
        }
    }

    private void loadAssignedRestaurants() {
        if (isNewAdmin || admin.getRestaurantsManaged() == null || admin.getRestaurantsManaged().isEmpty()) {
            return;
        }

        try {
            // Get assigned restaurant IDs from the admin object
            List<Integer> restaurantIds = admin.getRestaurantsManaged();

            // Fetch complete restaurant objects
            List<Restaurant> restaurants = new ArrayList<>();
            for (Integer id : restaurantIds) {
                Restaurant restaurant = restaurantDataAccess.getRestaurantByID(id);
                if (restaurant != null) {
                    restaurants.add(restaurant);
                }
            }

            // Update the list
            assignedRestaurants.setAll(restaurants);

            // Remove assigned restaurants from available restaurants
            availableRestaurants.removeAll(assignedRestaurants);
        } catch (SQLException e) {
            showError("Error loading assigned restaurants: " + e.getMessage());
        }
    }

    private void handleAddRestaurant() {
        Restaurant selected = cmbRestaurant.getValue();
        if (selected != null) {
            assignedRestaurants.add(selected);
            availableRestaurants.remove(selected);
            cmbRestaurant.getSelectionModel().clearSelection();
        }
    }

    private void handleRemoveRestaurant() {
        Restaurant selected = lstAssignedRestaurants.getSelectionModel().getSelectedItem();
        if (selected != null) {
            assignedRestaurants.remove(selected);
            availableRestaurants.add(selected);
            lstAssignedRestaurants.getSelectionModel().clearSelection();
        }
    }

    private void updateAdminFromInputs() {
        // Update admin object with form values
        admin.setFirstName(txtFirstName.getText().trim());
        admin.setLastName(txtLastName.getText().trim());
        admin.setEmail(txtEmail.getText().trim());

        // Only update password if provided (for existing admins)
        if (!txtPassword.getText().isEmpty()) {
            admin.setPassword(txtPassword.getText());
        }

        // Set phone number
        List<String> phones = new ArrayList<>();
        if (!txtPhone.getText().trim().isEmpty()) {
            phones.add(txtPhone.getText().trim());
        }
        admin.setPhoneNumbers(phones);

        // Set salary
        try {
            double salary = Double.parseDouble(txtSalary.getText().trim());
            admin.setSalary(salary);
        } catch (NumberFormatException e) {
            admin.setSalary(0.0);
        }

        // Set role
//        admin.setSuperAdmin(cmbRole.getValue().equals("Super Admin"));

        // Set managed restaurants
        List<Integer> restaurantIds = assignedRestaurants.stream()
                .map(Restaurant::getRestaurantId)
                .collect(Collectors.toList());
        admin.setRestaurantsManaged(restaurantIds);
    }

    private boolean validateInputs() {
        // Clear previous status
        lblStatus.setText("");

        // Check required fields
        if (txtFirstName.getText().trim().isEmpty() ||
            txtLastName.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() ||
            (isNewAdmin && txtPassword.getText().isEmpty())) {

            showError("Please fill in all required fields");
            return false;
        }

        // Validate email format
        if (!txtEmail.getText().trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError("Please enter a valid email address");
            return false;
        }

        // Validate salary
        try {
            double salary = Double.parseDouble(txtSalary.getText().trim());
            if (salary < 0) {
                showError("Salary cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid salary amount");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        lblStatus.setText(message);
    }

    public boolean saveAdmin() {
        try {
            if (isNewAdmin) {
                adminDataAccess.addAdmin(admin);
            } else {
                adminDataAccess.updateAdmin(admin);
            }
            return true;
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            return false;
        }
    }

    // Handler for OK button
    public void handleOk() {
        if (validateInputs()) {
            updateAdminFromInputs();
            if (saveAdmin()) {
                if (dialog != null) {
                    dialog.setResult(admin);
                }
            }
        }
    }
}
