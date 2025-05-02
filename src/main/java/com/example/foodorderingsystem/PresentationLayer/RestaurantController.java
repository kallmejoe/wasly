package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Cart;
import com.example.foodorderingsystem.BusinessLayer.Category;
import com.example.foodorderingsystem.BusinessLayer.Product;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.CategoryDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.ProductDataAccess;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RestaurantController implements Initializable {

    @FXML
    private Label restaurantNameLabel;

    @FXML
    private Label restaurantAddressLabel;

    @FXML
    private Label restaurantPhoneLabel;

    @FXML
    private ImageView restaurantImage;

    @FXML
    private TabPane categoriesTabPane;

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label emptyCartLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Button checkoutButton;

    @FXML
    private Label cartItemCount;

    private ProductDataAccess productDataAccess;
    private Restaurant restaurant;
    private Cart cart;
    private Map<Integer, Tab> categoryTabs = new HashMap<>();
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("5.00"); // Fixed delivery fee
    private CategoryDataAccess categoryDataAccess;

    public RestaurantController() {
        productDataAccess = new ProductDataAccess();
        categoryDataAccess = new CategoryDataAccess();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get selected restaurant from session
        restaurant = SessionManager.getInstance().getSelectedRestaurant();
        cart = SessionManager.getInstance().getUserCart();

        if (restaurant == null) {
            System.err.println("No restaurant selected!");
            return;
        }

        // Set restaurant information
        updateRestaurantInfo();

        // Load restaurant products
        loadRestaurantProducts();

        // Update cart display
        updateCartDisplay();
    }

    private void updateRestaurantInfo() {
        restaurantNameLabel.setText(restaurant.getName());

        // Set address if available
        if (!restaurant.getLocations().isEmpty()) {
            String address = restaurant.getLocations().get(0).getCity() + ", " +
                             restaurant.getLocations().get(0).getStreetName();
            restaurantAddressLabel.setText(address);
        } else {
            restaurantAddressLabel.setText("Address not available");
        }

        // Set phone if available
        if (!restaurant.getPhoneNo().isEmpty()) {
            restaurantPhoneLabel.setText("Phone: " + restaurant.getPhoneNo().get(0));
        } else {
            restaurantPhoneLabel.setText("Phone: Not available");
        }

        // Try to load restaurant image
        try {
            // In a real application, you would load the image from a database or file system
            // For now, we'll use a placeholder
            restaurantImage.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/restaurant-placeholder.svg")));
        } catch (Exception e) {
            System.err.println("Could not load restaurant image: " + e.getMessage());
        }
    }

    private void loadRestaurantProducts() {
        try {
            // Get all products for this restaurant
            List<Product> products = productDataAccess.getProductsByRestaurant(restaurant.getRestaurantId());

            // Group products by category
            Map<Integer, List<Product>> productsByCategory = products.stream()
                    .collect(Collectors.groupingBy(Product::getCategoryId));

            // Create a tab for each category
            for (Map.Entry<Integer, List<Product>> entry : productsByCategory.entrySet()) {
                int categoryId = entry.getKey();
                List<Product> categoryProducts = entry.getValue();

                // Create tab for this category
                Tab categoryTab = createCategoryTab(categoryId, categoryProducts);
                categoriesTabPane.getTabs().add(categoryTab);
                categoryTabs.put(categoryId, categoryTab);
            }

            // Select first tab by default
            if (!categoriesTabPane.getTabs().isEmpty()) {
                categoriesTabPane.getSelectionModel().select(0);
            }

        } catch (SQLException e) {
            System.err.println("Error loading restaurant products: " + e.getMessage());

            // Show error to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load menu");
            alert.setContentText("An error occurred while loading the restaurant menu: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private Tab createCategoryTab(int categoryId, List<Product> products) {
        // Get the category name from the database
        String categoryName = "Category " + categoryId;
        try {
            Category category = categoryDataAccess.getCategoryById(categoryId);
            if (category != null) {
                categoryName = category.getName();
            }
        } catch (SQLException e) {
            System.err.println("Error getting category name: " + e.getMessage());
        }

        // Create a tab for this category
        Tab tab = new Tab(categoryName);
        tab.setClosable(false);

        // Container for products
        FlowPane productsContainer = new FlowPane();
        productsContainer.setHgap(15);
        productsContainer.setVgap(15);
        productsContainer.setPadding(new Insets(15));

        // Add product cards
        for (Product product : products) {
            productsContainer.getChildren().add(createProductCard(product));
        }

        // Wrap in a scroll pane
        ScrollPane scrollPane = new ScrollPane(productsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        tab.setContent(scrollPane);
        return tab;
    }

    private Node createProductCard(Product product) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(200);
        card.setPadding(new Insets(15));

        // Product image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(170);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        // Use a placeholder image or product image if available
        try {
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                // In a real app, you would load the actual image from a file or database
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.svg")));
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.svg")));
            }
        } catch (Exception e) {
            System.err.println("Could not load product image: " + e.getMessage());
        }

        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-title");
        nameLabel.setWrapText(true);

        // Product description (if available)
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            Label descLabel = new Label(product.getDescription());
            descLabel.setWrapText(true);
            descLabel.getStyleClass().add("product-description");
            descLabel.setMaxHeight(60);
            card.getChildren().add(descLabel);
        }

        // Product price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        Label priceLabel = new Label(currencyFormat.format(product.getUnitPrice()));
        priceLabel.getStyleClass().add("product-price");

        // Quantity controls
        HBox quantityControls = new HBox(5);
        quantityControls.setAlignment(Pos.CENTER);

        Button decreaseButton = new Button("-");
        decreaseButton.setMinWidth(30);

        Label quantityLabel = new Label("1");
        quantityLabel.setMinWidth(30);
        quantityLabel.setAlignment(Pos.CENTER);

        Button increaseButton = new Button("+");
        increaseButton.setMinWidth(30);

        // Set up quantity control actions
        decreaseButton.setOnAction(e -> {
            int currentQuantity = Integer.parseInt(quantityLabel.getText());
            if (currentQuantity > 1) {
                quantityLabel.setText(String.valueOf(currentQuantity - 1));
            }
        });

        increaseButton.setOnAction(e -> {
            int currentQuantity = Integer.parseInt(quantityLabel.getText());
            quantityLabel.setText(String.valueOf(currentQuantity + 1));
        });

        quantityControls.getChildren().addAll(decreaseButton, quantityLabel, increaseButton);

        // Add to cart button
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setPrefWidth(Double.MAX_VALUE);
        addToCartButton.setOnAction(event -> {
            int quantity = Integer.parseInt(quantityLabel.getText());
            handleAddToCart(product, quantity);
        });

        // Add all components to the card
        card.getChildren().addAll(imageView, nameLabel, priceLabel, quantityControls, addToCartButton);

        return card;
    }

    private void handleAddToCart(Product product, int quantity) {
        if (cart == null) {
            // Initialize cart if needed
            System.err.println("Cart is null, initializing...");
            if (SessionManager.getInstance().getCurrentUser() != null) {
                cart = new Cart(1); // Using default customer ID
                SessionManager.getInstance().setUserCart(cart);
            } else {
                System.err.println("User not logged in!");
                // Show login message
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Login Required");
                alert.setHeaderText(null);
                alert.setContentText("Please log in to add items to your cart.");
                alert.showAndWait();
                return;
            }
        }

        // Check if product is from a different restaurant
        if (!cart.isEmpty() && cart.getRestaurantId() != product.getRestaurantId()) {
            // Show confirmation dialog for clearing cart
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Different Restaurant");
            confirmDialog.setHeaderText("Your cart contains items from another restaurant");
            confirmDialog.setContentText("You can only order from one restaurant at a time. Would you like to clear your current cart and add this item instead?");

            // Create custom buttons
            ButtonType clearAndAddButton = new ButtonType("Clear Cart & Add", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmDialog.getButtonTypes().setAll(clearAndAddButton, cancelButton);

            // Apply some styling to the dialog
            DialogPane dialogPane = confirmDialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/com/example/foodorderingsystem/styles/main-styles.css").toExternalForm());
            dialogPane.getStyleClass().add("modern-dialog");

            // Show dialog and wait for user response
            confirmDialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType == clearAndAddButton) {
                    // Clear the cart
                    cart.clear();

                    // Now add the new product
                    cart.addProduct(product, quantity);

                    // Update cart display
                    updateCartDisplay();

                    // Show success message
                    showSuccessToast(quantity + " x " + product.getName() + " added to your cart");
                }
            });
        } else {
            // Add product to cart (either cart is empty or from same restaurant)
            boolean added = cart.addProduct(product, quantity);
            if (added) {
                // Update cart display
                updateCartDisplay();

                // Show success message
                showSuccessToast(quantity + " x " + product.getName() + " added to your cart");
            }
        }
    }

    /**
     * Shows a success toast message that automatically disappears after a few seconds
     * @param message The message to display
     */
    private void showSuccessToast(String message) {
        // Create a stylish toast notification
        VBox toast = new VBox();
        toast.setStyle("-fx-background-color: #4CAF50; -fx-padding: 10 15; -fx-background-radius: 5; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        toast.getChildren().add(messageLabel);

        // Add to scene but position outside initially
        StackPane overlay = new StackPane(toast);
        overlay.setAlignment(Pos.BOTTOM_CENTER);
        overlay.setPadding(new Insets(0, 0, 30, 0));
        overlay.setMouseTransparent(true);

        // Get the main content from BorderPane
        BorderPane rootPane = (BorderPane) categoriesTabPane.getScene().getRoot();

        // Add overlay as top layer
        rootPane.getChildren().add(overlay);
        StackPane.setAlignment(overlay, Pos.BOTTOM_CENTER);

        // Animation to slide in and fade out
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(overlay));

        // Play animations
        fadeIn.play();
        fadeIn.setOnFinished(e -> fadeOut.play());
    }

    private void updateCartDisplay() {
        if (cart == null || cart.isEmpty()) {
            emptyCartLabel.setVisible(true);
            cartItemsContainer.getChildren().clear();
            cartItemsContainer.getChildren().add(emptyCartLabel);
            subtotalLabel.setText("$0.00");
            totalLabel.setText("$0.00");
            deliveryFeeLabel.setText("$0.00");
            checkoutButton.setDisable(true);
            cartItemCount.setText("0");
            return;
        }

        // Update cart count in navbar
        cartItemCount.setText(Integer.toString(cart.getItemCount()));

        // Hide empty cart label
        emptyCartLabel.setVisible(false);

        // Clear existing items
        cartItemsContainer.getChildren().clear();

        // Add each cart item
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        for (Cart.CartItem item : cart.getCartItems()) {
            HBox itemRow = new HBox(10);
            itemRow.getStyleClass().add("cart-item");
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.setPadding(new Insets(5, 0, 5, 0));

            // Item quantity
            Label quantityLabel = new Label(Integer.toString(item.getQuantity()));
            quantityLabel.setPrefWidth(30);

            // Item name
            Label nameLabel = new Label(item.getProduct().getName());
            nameLabel.setWrapText(true);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            // Item price
            Label priceLabel = new Label(currencyFormat.format(item.getItemTotal()));

            // Quantity controls for cart items
            HBox quantityControls = new HBox(5);
            quantityControls.setAlignment(Pos.CENTER);

            Button decreaseButton = new Button("-");
            decreaseButton.setMinWidth(24);
            decreaseButton.setPrefHeight(24);
            decreaseButton.setOnAction(e -> {
                if (item.getQuantity() > 1) {
                    cart.updateQuantity(item.getProduct(), item.getQuantity() - 1);
                    updateCartDisplay();
                } else {
                    // If quantity would go to 0, remove the item
                    cart.removeProduct(item.getProduct());
                    updateCartDisplay();
                }
            });

            Button increaseButton = new Button("+");
            increaseButton.setMinWidth(24);
            increaseButton.setPrefHeight(24);
            increaseButton.setOnAction(e -> {
                cart.updateQuantity(item.getProduct(), item.getQuantity() + 1);
                updateCartDisplay();
            });

            quantityControls.getChildren().addAll(decreaseButton, quantityLabel, increaseButton);

            // Remove button
            Button removeButton = new Button("✕");
            removeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
            removeButton.setOnAction(event -> {
                cart.removeProduct(item.getProduct());
                updateCartDisplay();
            });

            // Add all components to row
            VBox itemDetails = new VBox(2);
            itemDetails.getChildren().addAll(nameLabel, priceLabel);
            HBox.setHgrow(itemDetails, Priority.ALWAYS);

            itemRow.getChildren().addAll(quantityLabel, itemDetails, quantityControls, removeButton);
            cartItemsContainer.getChildren().add(itemRow);

            // Add separator after each item except the last one
            if (item != cart.getCartItems().get(cart.getCartItems().size() - 1)) {
                cartItemsContainer.getChildren().add(new Separator());
            }
        }

        // Update totals
        BigDecimal subtotal = cart.calculateTotal();
        subtotalLabel.setText(currencyFormat.format(subtotal));

        // Only show delivery fee if cart has items
        BigDecimal deliveryFee = cart.isEmpty() ? BigDecimal.ZERO : DELIVERY_FEE;
        deliveryFeeLabel.setText(currencyFormat.format(deliveryFee));

        // Calculate total
        BigDecimal total = subtotal.add(deliveryFee);
        totalLabel.setText(currencyFormat.format(total));

        // Enable checkout button
        checkoutButton.setDisable(false);
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
        // Navigate back to dashboard which shows restaurants
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
            Parent cartView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/cartView.fxml"));
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

    @FXML
    protected void handleCheckout(ActionEvent event) {
        try {
            Parent checkoutView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/checkout-view.fxml"));
            Scene checkoutScene = new Scene(checkoutView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(checkoutScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error loading checkout view: " + e.getMessage());
        }
    }
}
