package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.DataAccessLayer.CustomerDataAccess;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private Label lblCustomerId;
    @FXML private TextField txtFirstName;
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
    private Dialog<Customer> dialog;

    public void setDialog(Dialog<Customer> dialog) {
        this.dialog = dialog;

        // Set up the result converter for the dialog
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (validateInputs()) {
                    updateCustomerFromInputs();
                    return customer;
                }
                return null;
            }
            return null;
        });
    }

    @FXML
    private void initialize() {
        customerDataAccess = new CustomerDataAccess();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        isNewCustomer = (customer.getCustomerId() == 0);

        if (!isNewCustomer) {
            // Fill form with customer data
            lblCustomerId.setText(String.valueOf(customer.getCustomerId()));
            txtFirstName.setText(customer.getFirstName());
            txtLastName.setText(customer.getLastName());
            txtEmail.setText(customer.getEmail());
            txtPassword.setText(""); // Don't show existing password

            // Load phone number (first one if available)
            if (customer.getPhoneNo() != null && !customer.getPhoneNo().isEmpty()) {
                txtPhone.setText(customer.getPhoneNo().get(0));
            }

            // Load address (first one if available)
            if (customer.getLocation() != null && !customer.getLocation().isEmpty()) {
                Location location = customer.getLocation().get(0);
                txtCity.setText(location.getCity() != null ? location.getCity() : "");
                txtStreet.setText(location.getStreet() != null ? location.getStreet() : "");
                txtStreetNumber.setText(location.getStreetNumber() != null ? location.getStreetNumber() : "");
            }
        } else {
            lblCustomerId.setText("New");
        }
    }

    private void updateCustomerFromInputs() {
        // Update customer object with form data
        customer.setFirstName(txtFirstName.getText().trim());
        customer.setLastName(txtLastName.getText().trim());
        customer.setEmail(txtEmail.getText().trim());

        // Only update password if it's provided
        String password = txtPassword.getText().trim();
        if (!password.isEmpty()) {
            customer.setPassword(password);
        }

        // Update phone numbers
        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty()) {
            customer.setPhoneNo(Arrays.asList(phone));
        } else {
            customer.setPhoneNo(new ArrayList<>());
        }

        // Update address
        String city = txtCity.getText().trim();
        String street = txtStreet.getText().trim();
        String streetNumber = txtStreetNumber.getText().trim();

        if (!city.isEmpty() && !street.isEmpty()) {
            Location location = new Location(city, street, streetNumber);
            customer.setLocation(Arrays.asList(location));
        } else {
            customer.setLocation(new ArrayList<>());
        }
    }

    public boolean saveCustomer() {
        try {
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

        if (txtFirstName.getText() == null || txtFirstName.getText().trim().isEmpty()) {
            errorMessage.append("First name is required\n");
        }

        if (txtLastName.getText() == null || txtLastName.getText().trim().isEmpty()) {
            errorMessage.append("Last name is required\n");
        }

        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            errorMessage.append("Email is required\n");
        } else if (!txtEmail.getText().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {
            errorMessage.append("Email format is invalid\n");
        }

        if (isNewCustomer && (txtPassword.getText() == null || txtPassword.getText().trim().isEmpty())) {
            errorMessage.append("Password is required for new customers\n");
        }

        if (txtCity.getText().trim().isEmpty() || txtStreet.getText().trim().isEmpty()) {
            errorMessage.append("City and street are required for the address\n");
        }

        if (errorMessage.length() > 0) {
            lblStatus.setText(errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showError(String message) {
        lblStatus.setText(message);

        if (lblStatus.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save customer");
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    public void handleOk() {
        if (validateInputs()) {
            updateCustomerFromInputs();
            saveCustomer();
        }
    }
}
