package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Cart;
import com.example.foodorderingsystem.BusinessLayer.Category;
import com.example.foodorderingsystem.BusinessLayer.Product;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.CategoryDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.ProductDataAccess;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MenuViewController implements Initializable {

    // FXML Components - Navigation
    @FXML private Button homeButton;
    @FXML private Button restaurantsButton;
    @FXML private Button ordersButton;
    @FXML private Button cartButton;
    @FXML private Button profileButton;
    @FXML private Button logoutButton;
    @FXML private Button backButton;
    @FXML private Label cartItemCount;

    // FXML Components - Restaurant Info
    @FXML private ImageView restaurantImage;
    @FXML private Label restaurantNameLabel;
    @FXML private Label restaurantLocationLabel;
    @FXML private Label restaurantPhoneLabel;

    // FXML Components - Menu and Categories
    @FXML private TabPane categoryTabPane;

    // FXML Components - Cart
    @FXML private VBox cartItemsContainer;
    @FXML private VBox emptyCartPlaceholder;
    @FXML private Label subtotalLabel;
    @FXML private Label deliveryFeeLabel;
    @FXML private Label totalLabel;
    @FXML private Button clearCartButton;
    @FXML private Button checkoutButton;

    // Class variables
    private Restaurant restaurant;
    private Cart cart;
    private ProductDataAccess productDataAccess;
    private CategoryDataAccess categoryDataAccess;
    private Map<Integer, Tab> categoryTabs = new HashMap<>();
    private Map<Integer, Spinner<Integer>> productQuantitySpinners = new HashMap<>();
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("5.00");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the selected restaurant and current user's cart from the session
        restaurant = SessionManager.getInstance().getSelectedRestaurant();
        cart = SessionManager.getInstance().getUserCart();

        // Initialize data access objects
        productDataAccess = new ProductDataAccess();
        categoryDataAccess = new CategoryDataAccess();

        // Initialize the UI
        if (restaurant != null) {
            loadRestaurantDetails();
            loadProductsByCategory();
            updateCartView();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No restaurant selected",
                    "Please select a restaurant to view the menu.");
            handleBackButton();
        }
    }

    private void loadRestaurantDetails() {
        // Set restaurant name and details
        restaurantNameLabel.setText(restaurant.getName());

        // Set restaurant location (using first location if available)
        if (restaurant.getLocations() != null && !restaurant.getLocations().isEmpty()) {
            restaurantLocationLabel.setText(restaurant.getLocations().get(0).toString());
        } else {
            restaurantLocationLabel.setText("No location available");
        }

        // Set restaurant phone (using first phone if available)
        if (restaurant.getPhoneNo() != null && !restaurant.getPhoneNo().isEmpty()) {
            restaurantPhoneLabel.setText("Phone: " + restaurant.getPhoneNo().get(0));
        } else {
            restaurantPhoneLabel.setText("Phone: N/A");
        }

        // Update cart count
        updateCartCount();
    }

    private void loadProductsByCategory() {
        try {
            // Get all products for this restaurant
            List<Product> products = productDataAccess.getProductsByRestaurant(restaurant.getRestaurantId());

            // Get all categories
            List<Category> categories = categoryDataAccess.getAllCategories();

            // Group products by category
            Map<Integer, List<Product>> productsByCategory = products.stream()
                    .collect(Collectors.groupingBy(Product::getCategoryId));

            // Create a tab for each category that has products
            for (Category category : categories) {
                int categoryId = category.getCategoryId();
                List<Product> categoryProducts = productsByCategory.getOrDefault(categoryId, Collections.emptyList());

                // Skip categories with no products
                if (categoryProducts.isEmpty()) {
                    continue;
                }

                // Create new tab for this category
                Tab categoryTab = new Tab(category.getName());
                categoryTab.setClosable(false);

                // Create scrollable content for tab
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToWidth(true);
                scrollPane.getStyleClass().add("transparent-scroll-pane");

                // Create grid for products (3 columns)
                GridPane productGrid = new GridPane();
                productGrid.setHgap(20);
                productGrid.setVgap(20);
                productGrid.setPadding(new Insets(20));

                // Populate the grid with product cards
                int row = 0;
                int col = 0;
                for (Product product : categoryProducts) {
                    VBox productCard = createProductCard(product);
                    productGrid.add(productCard, col, row);

                    // Move to next column or row
                    col++;
                    if (col >= 3) {
                        col = 0;
                        row++;
                    }
                }

                scrollPane.setContent(productGrid);
                categoryTab.setContent(scrollPane);

                // Store tab for future reference
                categoryTabs.put(categoryId, categoryTab);

                // Add tab to tab pane
                categoryTabPane.getTabs().add(categoryTab);
            }

            // Select first tab if available
            if (!categoryTabPane.getTabs().isEmpty()) {
                categoryTabPane.getSelectionModel().selectFirst();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load products",
                    "There was an error loading products: " + e.getMessage());
        }
    }

    private VBox createProductCard(Product product) {
        // Main card container
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("product-card");
        card.setMaxWidth(220);
        card.setMinWidth(220);

        // Drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.3));
        card.setEffect(dropShadow);

        // Product image
        ImageView productImage = new ImageView();
        productImage.setFitWidth(180);
        productImage.setFitHeight(120);
        productImage.setPreserveRatio(true);

        // Use product image if available, otherwise placeholder
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            try {
                String imagePath = "/com/example/foodorderingsystem/images/" + product.getImages().get(0).getImageName();
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                productImage.setImage(image);
            } catch (Exception e) {
                // Use placeholder if image loading fails
                productImage.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.svg")));
            }
        } else {
            productImage.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.svg")));
        }

        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-title");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(200);

        // Product price
        Label priceLabel = new Label(currencyFormatter.format(product.getUnitPrice()));
        priceLabel.getStyleClass().add("product-price");

        // Quantity control
        HBox quantityBox = new HBox();
        quantityBox.setSpacing(10);
        quantityBox.setAlignment(Pos.CENTER);

        // Create spinner for quantity selection (0-10)
        Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, 0);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(80);

        // Store spinner for future reference
        productQuantitySpinners.put(product.getProductId(), quantitySpinner);

        // Check if product is already in cart
        for (Cart.CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProduct().getProductId() == product.getProductId()) {
                quantitySpinner.getValueFactory().setValue(cartItem.getQuantity());
                break;
            }
        }

        // Add to cart button
        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.getStyleClass().add("button");
        addToCartBtn.setPrefWidth(120);

        // Enable/disable button based on quantity
        addToCartBtn.disableProperty().bind(quantitySpinner.valueProperty().isEqualTo(0));

        // Add to cart action
        addToCartBtn.setOnAction(event -> {
            int quantity = quantitySpinner.getValue();
            if (quantity > 0) {
                // Add product to cart
                if (cart.addProduct(product, quantity)) {
                    updateCartView();
                } else {
                    // Show error message if adding product failed (e.g., from different restaurant)
                    showAlert(Alert.AlertType.WARNING, "Cart Error",
                            "Cannot add item from different restaurant",
                            "Your cart contains items from another restaurant. Please clear your cart first or finish your current order.");
                }
            }
        });

        quantityBox.getChildren().addAll(quantitySpinner, addToCartBtn);

        // Add all components to card
        card.getChildren().addAll(productImage, nameLabel, priceLabel, quantityBox);

        return card;
    }

    private void updateCartView() {
        // Get cart items
        List<Cart.CartItem> cartItems = cart.getCartItems();
        boolean isEmpty = cartItems.isEmpty();

        // Show/hide empty cart placeholder
        emptyCartPlaceholder.setVisible(isEmpty);
        emptyCartPlaceholder.setManaged(isEmpty);

        // Clear and update cart items container
        cartItemsContainer.getChildren().clear();

        if (!isEmpty) {
            for (Cart.CartItem item : cartItems) {
                cartItemsContainer.getChildren().add(createCartItemView(item));
            }
        }

        // Update totals
        BigDecimal subtotal = cart.calculateTotal();
        BigDecimal deliveryFee = isEmpty ? BigDecimal.ZERO : DELIVERY_FEE;
        BigDecimal total = subtotal.add(deliveryFee);

        subtotalLabel.setText(currencyFormatter.format(subtotal));
        deliveryFeeLabel.setText(currencyFormatter.format(deliveryFee));
        totalLabel.setText(currencyFormatter.format(total));

        // Enable/disable checkout and clear buttons
        checkoutButton.setDisable(isEmpty);
        clearCartButton.setDisable(isEmpty);

        // Update cart count in navbar
        updateCartCount();

        // Update quantity spinners to match cart quantities
        for (Cart.CartItem item : cartItems) {
            int productId = item.getProduct().getProductId();
            Spinner<Integer> spinner = productQuantitySpinners.get(productId);
            if (spinner != null) {
                spinner.getValueFactory().setValue(item.getQuantity());
            }
        }
    }

    private HBox createCartItemView(Cart.CartItem item) {
        // Main container for cart item
        HBox cartItem = new HBox();
        cartItem.setSpacing(10);
        cartItem.setPadding(new Insets(5));
        cartItem.setAlignment(Pos.CENTER_LEFT);
        cartItem.getStyleClass().add("cart-item");

        // Product image (small thumbnail)
        ImageView itemImage = new ImageView();
        itemImage.setFitWidth(40);
        itemImage.setFitHeight(40);
        itemImage.setPreserveRatio(true);

        // Use product image if available, otherwise placeholder
        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            try {
                String imagePath = "/com/example/foodorderingsystem/images/" + item.getProduct().getImages().get(0).getImageName();
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                itemImage.setImage(image);
            } catch (Exception e) {
                itemImage.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.svg")));
            }
        } else {
            itemImage.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/food-placeholder.svg")));
        }

        // Item details (name, price, quantity)
        VBox itemDetails = new VBox();
        itemDetails.setSpacing(3);
        itemDetails.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(itemDetails, Priority.ALWAYS);

        Label itemName = new Label(item.getProduct().getName());
        itemName.setStyle("-fx-font-weight: bold;");

        HBox priceQtyBox = new HBox();
        priceQtyBox.setSpacing(10);

        Label itemPrice = new Label(currencyFormatter.format(item.getProduct().getUnitPrice()));
        Label itemQuantity = new Label("x" + item.getQuantity());

        priceQtyBox.getChildren().addAll(itemPrice, itemQuantity);

        Label itemTotal = new Label(currencyFormatter.format(item.getItemTotal()));
        itemTotal.getStyleClass().add("summary-value");

        itemDetails.getChildren().addAll(itemName, priceQtyBox, itemTotal);

        // Remove button
        Button removeButton = new Button("✕");
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(event -> {
            cart.removeProduct(item.getProduct());
            updateCartView();

            // Reset the spinner for this product
            Spinner<Integer> spinner = productQuantitySpinners.get(item.getProduct().getProductId());
            if (spinner != null) {
                spinner.getValueFactory().setValue(0);
            }
        });

        // Add all components to cart item
        cartItem.getChildren().addAll(itemImage, itemDetails, removeButton);

        return cartItem;
    }

    private void updateCartCount() {
        int count = cart.getItemCount();
        cartItemCount.setText(String.valueOf(count));
    }

    @FXML
    private void handleHomeButton() {
        navigateTo("dashboard-view.fxml");
    }

    @FXML
    private void handleRestaurantsButton() {
        navigateTo("restaurant-view.fxml");
    }

    @FXML
    private void handleOrdersButton() {
        navigateTo("orders-view.fxml");
    }

    @FXML
    private void handleCartButton() {
        navigateTo("cartView.fxml");
    }

    @FXML
    private void handleProfileButton() {
        navigateTo("profile-view.fxml");
    }

    @FXML
    private void handleLogoutButton() {
        SessionManager.getInstance().logout();
        navigateTo("login-view.fxml");
    }

    @FXML
    private void handleBackButton() {
        navigateTo("restaurant-view.fxml");
    }

    @FXML
    private void handleClearCart() {
        if (cart.isEmpty()) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Cart");
        alert.setHeaderText("Clear all items from cart");
        alert.setContentText("Are you sure you want to remove all items from your cart?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cart.clear();

            // Reset all spinners
            for (Spinner<Integer> spinner : productQuantitySpinners.values()) {
                spinner.getValueFactory().setValue(0);
            }

            updateCartView();
        }
    }

    @FXML
    private void handleCheckout() {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Empty Cart",
                    "Your cart is empty", "Please add some items to your cart before checking out.");
            return;
        }

        navigateTo("checkout-view.fxml");
    }

    private void navigateTo(String fxmlFile) {
        try {
            String fullPath = "/com/example/foodorderingsystem/" + fxmlFile;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fullPath));
            Parent root = loader.load();

            Stage stage = (Stage) categoryTabPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to the requested page.", e.getMessage());
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
