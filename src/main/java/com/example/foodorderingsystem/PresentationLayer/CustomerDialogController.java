package com.example.foodorderingsystem.PresentationLayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.DataAccessLayer.CustomerDataAccess;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CustomerDialogController {

    @FXML private Label lblCustomerId;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtMiddleName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPhone;
    @FXML private TextField txtCity;
    @FXML private TextField txtStreet;
    @FXML private TextField txtStreetNumber;
    @FXML private Label lblStatus;

    private Customer customer;
    private CustomerDataAccess customerDataAccess;
    private boolean isNewCustomer = false;
    private Dialog<ButtonType> dialog;
    private Runnable onSaveCallback;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;

        // Set up proper event handling for the dialog buttons
        dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            // Prevent dialog from closing if validation fails
            if (!validateInputs() || !saveCustomer()) {
                event.consume();
            } else {
                // If saving was successful, run the callback if provided
                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }
            }
        });
    }

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    @FXML
    public void initialize() {
        customerDataAccess = new CustomerDataAccess();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        isNewCustomer = (customer != null && customer.getCustomerId() == 0);

        // Skip all initialization if customer is null
        if (customer == null) return;

        if (!isNewCustomer) {
            // Set fields from existing customer data
            lblCustomerId.setText(String.valueOf(customer.getCustomerId()));

            if (customer.getFirstName() != null) {
                txtFirstName.setText(customer.getFirstName());
            }

            // Safely handle middle name which might be null
            if (txtMiddleName != null && customer.getMiddleName() != null) {
                txtMiddleName.setText(customer.getMiddleName());
            } else if (txtMiddleName != null) {
                txtMiddleName.setText("");
            }

            if (customer.getLastName() != null) {
                txtLastName.setText(customer.getLastName());
            }

            if (customer.getEmail() != null) {
                txtEmail.setText(customer.getEmail());
            }

            txtPassword.clear(); // password hidden for edit mode

            if (customer.getPhoneNo() != null && !customer.getPhoneNo().isEmpty()) {
                txtPhone.setText(customer.getPhoneNo().get(0));
            } else {
                txtPhone.clear();
            }

            if (customer.getLocation() != null && !customer.getLocation().isEmpty()) {
                Location location = customer.getLocation().get(0);
                txtCity.setText(location.getCity() != null ? location.getCity() : "");
                txtStreet.setText(location.getStreetName() != null ? location.getStreetName() : "");
                txtStreetNumber.setText(location.getStreetNumber() != null ? location.getStreetNumber() : "");
            } else {
                txtCity.clear();
                txtStreet.clear();
                txtStreetNumber.clear();
            }

        } else {
            // Initialize for new customer
            lblCustomerId.setText("New");
            txtFirstName.clear();
            if (txtMiddleName != null) {
                txtMiddleName.clear();
            }
            txtLastName.clear();
            txtEmail.clear();
            txtPassword.clear();
            txtPhone.clear();
            txtCity.clear();
            txtStreet.clear();
            txtStreetNumber.clear();
        }
    }

    private void updateCustomerFromInputs() {
        if (customer == null) {
            customer = new Customer();
        }

        customer.setFirstName(txtFirstName.getText().trim());

        // Handle middle name if the field exists
        if (txtMiddleName != null) {
            customer.setMiddleName(txtMiddleName.getText().trim());
        }

        customer.setLastName(txtLastName.getText().trim());
        customer.setEmail(txtEmail.getText().trim());

        String password = txtPassword.getText().trim();
        if (!password.isEmpty()) {
            customer.setPassword(password);
        }

        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty()) {
            customer.setPhoneNo(List.of(phone));
        } else {
            customer.setPhoneNo(new ArrayList<>());
        }

        String city = txtCity.getText().trim();
        String street = txtStreet.getText().trim();
        String streetNumber = txtStreetNumber.getText().trim();
        if (!city.isEmpty() && !street.isEmpty()) {
            customer.setLocation(List.of(new Location(city, street, streetNumber)));
        } else {
            customer.setLocation(new ArrayList<>());
        }
    }

    public boolean saveCustomer() {
        try {
            updateCustomerFromInputs();

            if (isNewCustomer) {
                customerDataAccess.insertCustomer(customer);
            } else {
                customerDataAccess.updateCustomer(customer);
            }
            return true;
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            return false;
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        lblStatus.setText("");

        if (txtFirstName.getText().trim().isEmpty()) {
            errorMessage.append("First name is required.\n");
        }

        if (txtLastName.getText().trim().isEmpty()) {
            errorMessage.append("Last name is required.\n");
        }

        // Fix email validation to be more accurate
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            errorMessage.append("Email is required.\n");
        } else if (!isValidEmail(email)) {
            errorMessage.append("Email format is invalid.\n");
        }

        // Phone validation
        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty() && !isValidPhone(phone)) {
            errorMessage.append("Phone number format is invalid.\n");
        }

        // Password validation for new customers
        if (isNewCustomer) {
            String password = txtPassword.getText().trim();
            if (password.isEmpty()) {
                errorMessage.append("Password is required for new customers.\n");
            } else if (password.length() < 6) {
                errorMessage.append("Password must be at least 6 characters long.\n");
            }
        }

        if (txtCity.getText().trim().isEmpty() || txtStreet.getText().trim().isEmpty()) {
            errorMessage.append("City and Street are required for address.\n");
        }

        if (errorMessage.length() > 0) {
            lblStatus.setText(errorMessage.toString());
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\+?[0-9]{10,15}");
    }

    private void showError(String message) {
        lblStatus.setText(message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not save customer");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean handleOk() {
        // if (validateInputs()) {
            updateCustomerFromInputs();
            boolean saved = saveCustomer();
            if (saved) {
                // If it's a new customer, update the ID from the database
                if (isNewCustomer) {
                    isNewCustomer = false;
                }

                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }

                if (dialog != null) {
                    dialog.setResult(ButtonType.OK);
                    dialog.close();
                }
                return true;
            }

        return false;
    }

    public void handleCancel() {
        if (dialog != null) {
            dialog.setResult(ButtonType.CANCEL);
            dialog.close();
        }
    }
}
