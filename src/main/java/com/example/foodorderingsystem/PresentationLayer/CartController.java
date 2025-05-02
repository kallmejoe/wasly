package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Cart;
import com.example.foodorderingsystem.BusinessLayer.Cart.CartItem;
import com.example.foodorderingsystem.BusinessLayer.Product;
import com.example.foodorderingsystem.DataAccessLayer.ProductDataAccess;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    // FXML components
    @FXML
    private TableView<CartItem> cartTableView;
    @FXML
    private TableColumn<CartItem, ImageView> imageColumn;
    @FXML
    private TableColumn<CartItem, String> nameColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> priceColumn;
    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> totalColumn;
    @FXML
    private TableColumn<CartItem, Void> actionColumn;
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
    @FXML
    private Button checkoutButton;

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
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("5.00");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get cart from session manager instead of creating a new one
        cart = SessionManager.getInstance().getUserCart();

        // Initialize data access
        productDataAccess = new ProductDataAccess();

        // Initialize table columns
        setupTableColumns();

        // Load cart data
        loadCartItems();

        // Update cart summary (subtotal, total, etc.)
        updateCartSummary();

        // Update cart count in the navbar
        updateCartCountInNavbar();
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
        navigateTo("restaurant-view.fxml");
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
            CartItem item = param.getValue();
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

        priceColumn.setCellFactory(column -> new TableCell<CartItem, BigDecimal>() {
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

        totalColumn.setCellFactory(column -> new TableCell<CartItem, BigDecimal>() {
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

    private Callback<TableColumn<CartItem, Void>, TableCell<CartItem, Void>> createActionCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<CartItem, Void> call(final TableColumn<CartItem, Void> param) {
                return new TableCell<>() {
                    private final Button removeButton = new Button("Remove");
                    {
                        removeButton.getStyleClass().add("danger-button");
                        removeButton.setOnAction(event -> {
                            CartItem item = getTableView().getItems().get(getIndex());
                            cart.removeProduct(item.getProduct());
                            loadCartItems();
                            updateCartSummary();
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
        ObservableList<CartItem> cartItems = FXCollections.observableArrayList(cart.getCartItems());
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

        // Disable checkout button if cart is empty
        boolean isEmpty = cart.isEmpty();
        checkoutButton.setDisable(isEmpty);
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
        }

        // After clearing cart, update the navbar count too
        updateCartCountInNavbar();
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

        // Use our navigation method
        navigateTo("checkout-view.fxml");
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
