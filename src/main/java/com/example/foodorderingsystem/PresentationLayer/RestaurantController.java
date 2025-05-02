package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Cart;
import com.example.foodorderingsystem.BusinessLayer.Product;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.ProductDataAccess;
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

    public RestaurantController() {
        productDataAccess = new ProductDataAccess();
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
                             restaurant.getLocations().get(0).getStreetName() + ", " +
                             restaurant.getLocations().get(0).getCity();
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

        // Try to load restaurant image (this would be a placeholder or from database)
        try {
            restaurantImage.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/restaurant-placeholder.png")));
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

        } catch (SQLException e) {
            System.err.println("Error loading restaurant products: " + e.getMessage());
        }
    }

    private Tab createCategoryTab(int categoryId, List<Product> products) {
        // In a real app, you would get the category name from the database
        String categoryName = "Category " + categoryId;

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
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.png")));
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.png")));
            }
        } catch (Exception e) {
            System.err.println("Could not load product image: " + e.getMessage());
        }

        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-title");

        // Product price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        Label priceLabel = new Label(currencyFormat.format(product.getUnitPrice()));
        priceLabel.getStyleClass().add("product-price");

        // Add to cart button
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setPrefWidth(Double.MAX_VALUE);
        addToCartButton.setOnAction(event -> handleAddToCart(product));

        // Add all components to the card
        card.getChildren().addAll(imageView, nameLabel, priceLabel, addToCartButton);

        return card;
    }

    private void handleAddToCart(Product product) {
        if (cart == null) {
            // Initialize cart if needed
            System.err.println("Cart is null, initializing...");
            if (SessionManager.getInstance().getCurrentUser() != null) {
                cart = new Cart(1); // Assuming default customer ID is 1
                SessionManager.getInstance().setUserCart(cart);
            } else {
                System.err.println("User not logged in!");
                return;
            }
        }

        // Add product to cart
        boolean added = cart.addProduct(product, 1);
        if (added) {
            // Update cart display
            updateCartDisplay();
        } else {
            // Show error if product is from different restaurant
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cart Error");
            alert.setHeaderText(null);
            alert.setContentText("You can only add items from a single restaurant to your cart. " +
                    "Please empty your cart or finish your current order first.");
            alert.showAndWait();
        }
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

            // Item quantity
            Label quantityLabel = new Label(Integer.toString(item.getQuantity()));
            quantityLabel.setPrefWidth(30);

            // Item name
            Label nameLabel = new Label(item.getProduct().getName());
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            // Item price
            Label priceLabel = new Label(currencyFormat.format(item.getItemTotal()));

            // Remove button
            Button removeButton = new Button("X");
            removeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
            removeButton.setOnAction(event -> {
                cart.removeProduct(item.getProduct());
                updateCartDisplay();
            });

            // Add all components to row
            itemRow.getChildren().addAll(quantityLabel, nameLabel, priceLabel, removeButton);
            cartItemsContainer.getChildren().add(itemRow);
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
