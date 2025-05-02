package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Cart;
import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.BusinessLayer.Order;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.OrderDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.RestaurantDataAccess;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CheckoutController implements Initializable {

    @FXML
    private Label cartItemCount;

    @FXML
    private ComboBox<Location> addressComboBox;

    @FXML
    private TextArea deliveryInstructionsField;

    @FXML
    private RadioButton cashRadioButton;

    @FXML
    private RadioButton creditCardRadioButton;

    @FXML
    private VBox creditCardForm;

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField cardNameField;

    @FXML
    private TextField expiryDateField;

    @FXML
    private TextField cvvField;

    @FXML
    private Label restaurantNameLabel;

    @FXML
    private TableView<Cart.CartItem> orderItemsTable;

    @FXML
    private TableColumn<Cart.CartItem, String> productNameColumn;

    @FXML
    private TableColumn<Cart.CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<Cart.CartItem, String> priceColumn;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Button placeOrderButton;

    private Cart cart;
    private ToggleGroup paymentToggleGroup;
    private RestaurantDataAccess restaurantDataAccess;
    private OrderDataAccess orderDataAccess;
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("5.00"); // Fixed delivery fee
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public CheckoutController() {
        restaurantDataAccess = new RestaurantDataAccess();
        orderDataAccess = new OrderDataAccess();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get cart from session
        cart = SessionManager.getInstance().getUserCart();

        if (cart == null || cart.isEmpty()) {
            showEmptyCartAlert();
            return;
        }

        // Set up payment toggle group
        setupPaymentMethods();

        // Set up address combo box
        setupAddressComboBox();

        // Set up order summary table
        setupOrderSummaryTable();

        // Load restaurant information
        loadRestaurantInfo();

        // Update cart count in navbar
        cartItemCount.setText(Integer.toString(cart.getItemCount()));

        // Update order summary
        updateOrderSummary();
    }

    private void setupPaymentMethods() {
        paymentToggleGroup = new ToggleGroup();
        cashRadioButton.setToggleGroup(paymentToggleGroup);
        creditCardRadioButton.setToggleGroup(paymentToggleGroup);

        // Show/hide credit card form based on selection
        paymentToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            // Corrected unused lambda parameters
            creditCardForm.setVisible(newValue == creditCardRadioButton);
        });
    }

    private void setupAddressComboBox() {
        Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
        if (customer != null && customer.getLocation() != null) {
            List<Location> locations = customer.getLocation();
            addressComboBox.setItems(FXCollections.observableArrayList(locations));

            // Set display for the location objects
            addressComboBox.setConverter(new StringConverter<Location>() {
                @Override
                public String toString(Location location) {
                    if (location == null) {
                        return null;
                    }
                    return location.getCity() + ", " + location.getStreetName() + ", " + location.getStreetNumber();
                }

                @Override
                public Location fromString(String string) {
                    return null; // Not used for combo box
                }
            });

            // Select first address by default if available
            if (!locations.isEmpty()) {
                addressComboBox.setValue(locations.get(0));
            }
        }
    }

    private void setupOrderSummaryTable() {
        // Product Name Column
        productNameColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        // Quantity Column
        quantityColumn.setCellValueFactory(cellData
                -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        // Price Column
        priceColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(currencyFormat.format(cellData.getValue().getItemTotal())));

        // Add items to the table
        ObservableList<Cart.CartItem> cartItems = FXCollections.observableArrayList(cart.getCartItems());
        orderItemsTable.setItems(cartItems);
    }

    private void loadRestaurantInfo() {
        try {
            Restaurant restaurant = restaurantDataAccess.getRestaurantByID(cart.getRestaurantId());
            if (restaurant != null) {
                restaurantNameLabel.setText(restaurant.getName());
            } else {
                restaurantNameLabel.setText("Unknown Restaurant");
            }
        } catch (SQLException e) {
            System.err.println("Error loading restaurant information: " + e.getMessage());
            restaurantNameLabel.setText("Unknown Restaurant");
        }
    }

    private void updateOrderSummary() {
        BigDecimal subtotal = cart.calculateTotal();
        subtotalLabel.setText(currencyFormat.format(subtotal));
        deliveryFeeLabel.setText(currencyFormat.format(DELIVERY_FEE));
        BigDecimal total = subtotal.add(DELIVERY_FEE);
        totalLabel.setText(currencyFormat.format(total));
    }

    private void showEmptyCartAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Empty Cart");
        alert.setHeaderText(null);
        alert.setContentText("Your cart is empty. Please add items to your cart before proceeding to checkout.");
        alert.showAndWait();

        // Navigate back to the dashboard
        try {
            Parent dashboardView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/dashboard-view.fxml"));
            Scene dashboardScene = new Scene(dashboardView);
            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(dashboardScene);
        } catch (IOException e) {
            System.err.println("Error navigating to dashboard: " + e.getMessage());
        }
    }

    @FXML
    protected void handleAddNewAddress(ActionEvent event) {
        // Create a dialog for adding a new address
        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle("Add New Address");
        dialog.setHeaderText("Enter your new delivery address");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the address form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField cityField = new TextField();
        cityField.setPromptText("City");
        TextField streetNameField = new TextField();
        streetNameField.setPromptText("Street Name");
        TextField streetNumberField = new TextField();
        streetNumberField.setPromptText("Street Number/Building No.");

        grid.add(new Label("City:"), 0, 0);
        grid.add(cityField, 1, 0);
        grid.add(new Label("Street Name:"), 0, 1);
        grid.add(streetNameField, 1, 1);
        grid.add(new Label("Street Number:"), 0, 2);
        grid.add(streetNumberField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable save button depending on form validation
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validate all fields
        cityField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty()
                    || streetNameField.getText().trim().isEmpty()
                    || streetNumberField.getText().trim().isEmpty());
        });
        streetNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty()
                    || cityField.getText().trim().isEmpty()
                    || streetNumberField.getText().trim().isEmpty());
        });
        streetNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty()
                    || cityField.getText().trim().isEmpty()
                    || streetNameField.getText().trim().isEmpty());
        });

        // Convert the result to a location when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Location(
                        cityField.getText().trim(),
                        streetNameField.getText().trim(),
                        streetNumberField.getText().trim()
                );
            }
            return null;
        });

        Optional<Location> result = dialog.showAndWait();

        result.ifPresent(newLocation -> {
            // Add to the combo box
            addressComboBox.getItems().add(newLocation);
            addressComboBox.setValue(newLocation);

            // Add to the customer's location list (this would typically be saved to the database)
            Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
            if (customer != null) {
                customer.getLocation().add(newLocation);
                // In a real application, you would also call a method to update the customer in the database
            }
        });
    }

    @FXML
    protected void handleBackToCart(ActionEvent event) {
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
    protected void handlePlaceOrder(ActionEvent event) {
        // Validate form
        if (!validateForm()) {
            return;
        }

        // Confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Order");
        confirmDialog.setHeaderText("Please confirm your order");
        confirmDialog.setContentText("Are you sure you want to place this order?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            placeOrder();
        }
    }

    private boolean validateForm() {
        StringBuilder errorMessages = new StringBuilder();

        // Check if an address is selected
        if (addressComboBox.getValue() == null) {
            errorMessages.append("Please select a delivery address.\n");
        }

        // If credit card is selected, validate card details
        if (creditCardRadioButton.isSelected()) {
            if (cardNumberField.getText().trim().isEmpty()) {
                errorMessages.append("Please enter your card number.\n");
            } else if (!cardNumberField.getText().trim().matches("\\d{16}")) {
                errorMessages.append("Please enter a valid 16-digit card number.\n");
            }

            if (cardNameField.getText().trim().isEmpty()) {
                errorMessages.append("Please enter the cardholder name.\n");
            }

            if (expiryDateField.getText().trim().isEmpty()) {
                errorMessages.append("Please enter the card expiry date.\n");
            } else if (!expiryDateField.getText().trim().matches("\\d{2}/\\d{2}")) {
                errorMessages.append("Please enter expiry date in MM/YY format.\n");
            }

            if (cvvField.getText().trim().isEmpty()) {
                errorMessages.append("Please enter the CVV.\n");
            } else if (!cvvField.getText().trim().matches("\\d{3}")) {
                errorMessages.append("Please enter a valid 3-digit CVV.\n");
            }
        }

        // If there are validation errors, show them
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Validation Error");
            alert.setHeaderText(null);
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private void placeOrder() {
        try {
            Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
            if (customer == null) {
                showErrorAlert("User session error. Please login again.");
                return;
            }

            Location deliveryAddress = addressComboBox.getValue();
            String paymentMethod = cashRadioButton.isSelected() ? "Cash on Delivery" : "Credit Card";

            // Convert List<Cart.CartItem> to Map<Product, Integer> as required by OrderDataAccess
            java.util.Map<com.example.foodorderingsystem.BusinessLayer.Product, Integer> itemsMap =
                new java.util.HashMap<>();

            for (Cart.CartItem item : cart.getCartItems()) {
                itemsMap.put(item.getProduct(), item.getQuantity());
            }

            Order order = orderDataAccess.placeOrder(
                    customer.getCustomerId(),
                    cart.getRestaurantId(),
                    deliveryAddress,
                    paymentMethod,
                    "Pending",
                    itemsMap, // Pass the converted map instead of cart.getCartItems()
                    cart.calculateTotal().add(DELIVERY_FEE)
            );

            if (order != null) {
                showOrderSuccessDialog();
                cart.clear();
                SessionManager.getInstance().setUserCart(cart);

                Parent ordersView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/orders-view.fxml"));
                Scene ordersScene = new Scene(ordersView);
                Stage currentStage = (Stage) placeOrderButton.getScene().getWindow();
                currentStage.setScene(ordersScene);
                currentStage.show();
            } else {
                showErrorAlert("Failed to place order. Please try again later.");
            }
        } catch (SQLException e) {
            showErrorAlert("Error placing order: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showErrorAlert("Error navigating to orders view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showOrderSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed");
        alert.setHeaderText("Thank you for your order!");
        alert.setContentText("Your order has been successfully placed. You can track its status in the Orders section.");
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
        // Navigate to dashboard which shows restaurants
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
        try {
            Parent profileView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/profile-view.fxml"));
            Scene profileScene = new Scene(profileView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(profileScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error loading profile view: " + e.getMessage());
        }
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
