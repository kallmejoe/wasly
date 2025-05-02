package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.*;
import com.example.foodorderingsystem.DataAccessLayer.*;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    // FXML components
    @FXML
    private TableView<Cart.CartItem> cartTableView;
    @FXML
    private TableColumn<Cart.CartItem, ImageView> imageColumn;
    @FXML
    private TableColumn<Cart.CartItem, String> nameColumn;
    @FXML
    private TableColumn<Cart.CartItem, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Cart.CartItem, Integer> quantityColumn;
    @FXML
    private TableColumn<Cart.CartItem, BigDecimal> totalColumn;
    @FXML
    private TableColumn<Cart.CartItem, Void> actionColumn;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label deliveryFeeLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label cartItemsCountLabel;
    @FXML
    private Label cartItemCount; // For navbar indicator
    @FXML
    private Label emptyCartLabel;
    @FXML
    private Button clearCartButton;
    @FXML
    private Button continueShoppingButton;

    // Checkout related components
    @FXML
    private ComboBox<Location> addressComboBox;
    @FXML
    private TextArea deliveryInstructionsField;
    @FXML
    private RadioButton cashRadioButton;
    @FXML
    private RadioButton creditCardRadioButton;
    @FXML
    private ToggleGroup paymentToggleGroup;
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
    private Button placeOrderButton;
    @FXML
    private Button addAddressButton;

    // Navigation buttons
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

    // Services and data access
    private Cart cart;
    private ProductDataAccess productDataAccess;
    private CustomerDataAccess customerDataAccess;
    private OrderDataAccess orderDataAccess;
    private RestaurantDataAccess restaurantDataAccess;
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("5.00");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get cart from session manager instead of creating a new one
        cart = SessionManager.getInstance().getUserCart();

        // Initialize data access
        productDataAccess = new ProductDataAccess();
        customerDataAccess = new CustomerDataAccess();
        orderDataAccess = new OrderDataAccess();
        restaurantDataAccess = new RestaurantDataAccess();

        // Initialize table columns
        setupTableColumns();

        // Set up payment radio button listener
        if (paymentToggleGroup != null) {
            paymentToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (creditCardRadioButton != null && creditCardForm != null) {
                    creditCardForm.setVisible(creditCardRadioButton.isSelected());
                }
            });
        }

        // Load customer addresses
        loadCustomerAddresses();

        // Load cart data
        loadCartItems();

        // Update cart summary (subtotal, total, etc.)
        updateCartSummary();

        // Update cart count in the navbar
        updateCartCountInNavbar();
    }

    private void loadCustomerAddresses() {
        try {
            Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
            if (customer != null && addressComboBox != null) {
                List<Location> addresses = customer.getLocation();
                if (addresses != null && !addresses.isEmpty()) {
                    addressComboBox.setItems(FXCollections.observableArrayList(addresses));
                    addressComboBox.getSelectionModel().selectFirst();
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading customer addresses: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddAddress() {
        // Create a dialog to add a new address
        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle("Add New Address");
        dialog.setHeaderText("Enter your new address details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create fields for address input
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
        TextField stateField = new TextField();
        stateField.setPromptText("State");
        TextField zipCodeField = new TextField();
        zipCodeField.setPromptText("Zip Code");

        grid.add(new Label("City:"), 0, 0);
        grid.add(cityField, 1, 0);
        grid.add(new Label("Street Name:"), 0, 1);
        grid.add(streetNameField, 1, 1);
        grid.add(new Label("Street Number:"), 0, 2);
        grid.add(streetNumberField, 1, 2);
        grid.add(new Label("State:"), 0, 3);
        grid.add(stateField, 1, 3);
        grid.add(new Label("Zip Code:"), 0, 4);
        grid.add(zipCodeField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        cityField.requestFocus();

        // Convert the result when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Location(
                        cityField.getText(),
                        streetNameField.getText(),
                        streetNumberField.getText(),
                        stateField.getText(),
                        zipCodeField.getText()
                );
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Location> result = dialog.showAndWait();
        result.ifPresent(location -> {
            try {
                Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
                if (customer != null) {
                    // Add the new address to the customer
                    customer.getLocation().add(location);
                    // Update the customer in the database
                    customerDataAccess.updateCustomer(customer);
                    // Refresh the address combo box
                    addressComboBox.getItems().add(location);
                    addressComboBox.getSelectionModel().select(location);
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not save address",
                        "An error occurred while saving your address: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handlePlaceOrder() {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Empty Cart",
                    "Your cart is empty", "Please add some items to your cart before placing an order.");
            return;
        }

        // Validate delivery address
        Location selectedLocation = addressComboBox.getValue();
        if (selectedLocation == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Information",
                    "Please select a delivery address", "You must select a delivery address to proceed.");
            return;
        }

        // Create Payment object
        Payment payment = createPayment();
        if (payment == null) {
            // Payment creation failed (validation error happened)
            return;
        }

        try {
            // Get the current customer from session
            Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
            // Get the restaurant from the cart
            Restaurant restaurant = cart.getRestaurantId() > 0 ?
                    restaurantDataAccess.getRestaurantByID(cart.getRestaurantId()) : null;

            if (customer == null || restaurant == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Missing information",
                        "Customer or restaurant information is missing.");
                return;
            }

            // Create order with initial status "Pending"
            Order order = new Order();
            order.setOrderDate(LocalDateTime.now());
            order.setCustomer(customer);
            order.setRestaurant(restaurant);
            order.setStatus("Pending");
            order.setTotalAmount(cart.calculateTotal().add(DELIVERY_FEE).doubleValue());
            order.setPayment(payment);

            // Add delivery instructions if provided
            String instructions = deliveryInstructionsField.getText();
            if (instructions != null && !instructions.trim().isEmpty()) {
                // In a real app, you would store this with the order
                System.out.println("Delivery instructions: " + instructions);
            }

            // Create the order in the database
            int orderId = orderDataAccess.placeOrder(order);
            if (orderId > 0) {
                // Order created successfully
                order.setOrderId(orderId);

                // Add each cart item to the order
                for (Cart.CartItem item : cart.getCartItems()) {
                    productDataAccess.addProductToOrder(
                            item.getProduct().getProductId(),
                            orderId,
                            item.getQuantity()
                    );
                }

                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Order Placed");
                successAlert.setHeaderText("Your order was placed successfully!");
                successAlert.setContentText("Your order #" + orderId + " has been placed and will be delivered soon.");
                successAlert.showAndWait();

                // Clear the cart after successful order
                cart.clear();
                loadCartItems();
                updateCartSummary();
                updateCartCountInNavbar();

                // Navigate to orders view
                navigateTo("orders-view.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Order Error",
                        "Failed to place order", "There was an error processing your order. Please try again.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to place order", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Payment createPayment() {
        // Validate payment information
        if (creditCardRadioButton.isSelected()) {
            // Credit card payment selected
            String cardNumber = cardNumberField.getText();
            String cardName = cardNameField.getText();
            String expiryDate = expiryDateField.getText();
            String cvv = cvvField.getText();

            // Basic validation
            if (cardNumber == null || cardNumber.trim().isEmpty() ||
                cardName == null || cardName.trim().isEmpty() ||
                expiryDate == null || expiryDate.trim().isEmpty() ||
                cvv == null || cvv.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Missing Information",
                        "Please fill in all credit card details", "All credit card fields are required.");
                return null;
            }

            // Create a credit card payment
            Payment payment = new Payment();
            payment.setAmount(cart.calculateTotal().add(DELIVERY_FEE).doubleValue());
            payment.setPaymentMethod("Credit Card");
            payment.setStatus("Completed");
            // In a real app, you would securely process the credit card here
            return payment;
        } else {
            // Cash on delivery payment
            Payment payment = new Payment();
            payment.setAmount(cart.calculateTotal().add(DELIVERY_FEE).doubleValue());
            payment.setPaymentMethod("Cash on Delivery");
            payment.setStatus("Pending");
            return payment;
        }
    }

    private void updateCartCountInNavbar() {
        if (cartItemCount != null) {
            cartItemCount.setText(String.valueOf(cart.getItemCount()));
        }
    }

    // Navigation methods
    @FXML
    private void handleHomeButton(ActionEvent event) {
        navigateTo("dashboard-view.fxml");
    }

    @FXML
    private void handleRestaurantsButton(ActionEvent event) {
        navigateTo("dashboard-view.fxml");
    }

    @FXML
    private void handleOrdersButton(ActionEvent event) {
        navigateTo("orders-view.fxml");
    }

    @FXML
    private void handleCartButton(ActionEvent event) {
        // We're already in the cart view, no need to navigate
    }

    @FXML
    private void handleProfileButton(ActionEvent event) {
        navigateTo("profile-view.fxml");
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        // Logout the user and navigate to login screen
        SessionManager.getInstance().logout();
        navigateTo("login-view.fxml");
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/foodorderingsystem/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) homeButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to the requested page.", e.getMessage());
        }
    }

    private void setupTableColumns() {
        // Set up image column
        imageColumn.setCellValueFactory(param -> {
            Cart.CartItem item = param.getValue();
            Product product = item.getProduct();
            ImageView imageView = new ImageView();

            // Use first product image if available, otherwise use a placeholder
            if (product != null && product.getImages() != null && !product.getImages().isEmpty()) {
                try {
                    String imagePath = "/com/example/foodorderingsystem/images/" + product.getImages().get(0).getImageName();
                    Image image = new Image(getClass().getResourceAsStream(imagePath));
                    imageView.setImage(image);
                } catch (Exception e) {
                    // Use placeholder if image can't be loaded
                    imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.svg")));
                }
            } else {
                // Use placeholder if no image
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.svg")));
            }

            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);

            return new SimpleObjectProperty<>(imageView);
        });

        // Set up name column
        nameColumn.setCellValueFactory(param ->
            new SimpleStringProperty(param.getValue().getProduct().getName()));

        // Set up price column with currency formatting
        priceColumn.setCellValueFactory(param ->
            new SimpleObjectProperty<>(param.getValue().getProduct().getUnitPrice()));

        priceColumn.setCellFactory(column -> new TableCell<Cart.CartItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(price));
                }
            }
        });

        // Set up quantity column
        quantityColumn.setCellValueFactory(param ->
            new SimpleIntegerProperty(param.getValue().getQuantity()).asObject());

        // Set up total column with currency formatting
        totalColumn.setCellValueFactory(param ->
            new SimpleObjectProperty<>(param.getValue().getItemTotal()));

        totalColumn.setCellFactory(column -> new TableCell<Cart.CartItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(total));
                }
            }
        });

        // Set up action column with remove button
        actionColumn.setCellFactory(createActionCellFactory());
    }

    private Callback<TableColumn<Cart.CartItem, Void>, TableCell<Cart.CartItem, Void>> createActionCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Cart.CartItem, Void> call(final TableColumn<Cart.CartItem, Void> param) {
                return new TableCell<>() {
                    private final Button removeButton = new Button("Remove");
                    {
                        removeButton.getStyleClass().add("danger-button");
                        removeButton.setOnAction(event -> {
                            Cart.CartItem item = getTableView().getItems().get(getIndex());
                            cart.removeProduct(item.getProduct());
                            loadCartItems();
                            updateCartSummary();
                            updateCartCountInNavbar();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5);
                            hbox.getChildren().add(removeButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };
    }

    private void loadCartItems() {
        ObservableList<Cart.CartItem> cartItems = FXCollections.observableArrayList(cart.getCartItems());
        cartTableView.setItems(cartItems);

        // Show/hide empty cart message
        boolean isEmpty = cartItems.isEmpty();
        emptyCartLabel.setVisible(isEmpty);
        cartTableView.setVisible(!isEmpty);

        // Update cart items count
        int itemCount = cart.getItemCount();
        cartItemsCountLabel.setText(itemCount + (itemCount == 1 ? " item" : " items") + " in your cart");

        // Update cart count in navbar too
        updateCartCountInNavbar();
    }

    private void updateCartSummary() {
        BigDecimal subtotal = cart.calculateTotal();
        BigDecimal deliveryFee = cart.isEmpty() ? BigDecimal.ZERO : DELIVERY_FEE;
        BigDecimal total = subtotal.add(deliveryFee);

        subtotalLabel.setText(currencyFormatter.format(subtotal));
        deliveryFeeLabel.setText(currencyFormatter.format(deliveryFee));
        totalLabel.setText(currencyFormatter.format(total));

        // Disable buttons if cart is empty
        boolean isEmpty = cart.isEmpty();
        placeOrderButton.setDisable(isEmpty);
        clearCartButton.setDisable(isEmpty);
    }

    @FXML
    private void onClearCartClicked(ActionEvent event) {
        if (cart.isEmpty()) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Cart");
        alert.setHeaderText("Clear all items from cart");
        alert.setContentText("Are you sure you want to remove all items from your cart?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cart.clear();
            loadCartItems();
            updateCartSummary();
            updateCartCountInNavbar();
        }
    }

    @FXML
    private void onContinueShoppingClicked(ActionEvent event) {
        // Use our navigation method instead of duplicating code
        navigateTo("dashboard-view.fxml");
    }

    @FXML
    private void onCheckoutClicked(ActionEvent event) {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Empty Cart",
                    "Your cart is empty", "Please add some items to your cart before checking out.");
            return;
        }

        // Instead of navigating to checkout view, focus on the address field
        if (addressComboBox != null) {
            addressComboBox.requestFocus();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
