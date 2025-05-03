package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Account;
import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.RestaurantDataAccess;
import com.example.foodorderingsystem.FoodOrderingApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button restaurantsButton;

    @FXML
    private Button ordersButton;

    @FXML
    private Button cartButton;

    @FXML
    private Label cartItemCount;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private FlowPane featuredRestaurantsContainer;

    @FXML
    private FlowPane allRestaurantsContainer;

    private RestaurantDataAccess restaurantDataAccess;

    public DashboardController() {
        restaurantDataAccess = new RestaurantDataAccess();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set user name in welcome message
        Account currentUser = (Account) SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        }

        // Update cart count
        updateCartCount();

        // Load restaurants
        loadRestaurants();
    }

    private void loadRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantDataAccess.getAllRestaurants();

            // Clear existing containers
            featuredRestaurantsContainer.getChildren().clear();
            allRestaurantsContainer.getChildren().clear();

            // Display all restaurants
            for (Restaurant restaurant : restaurants) {
                allRestaurantsContainer.getChildren().add(createRestaurantCard(restaurant));
            }

            // Display featured restaurants (for this example, just showing the first 3)
            List<Restaurant> featuredRestaurants = restaurants.stream()
                    .limit(3)
                    .collect(Collectors.toList());

            for (Restaurant restaurant : featuredRestaurants) {
                featuredRestaurantsContainer.getChildren().add(createRestaurantCard(restaurant));
            }
        } catch (SQLException e) {
            System.err.println("Error loading restaurants: " + e.getMessage());
        }
    }

    private Node createRestaurantCard(Restaurant restaurant) {
        VBox card = new VBox(10);
        card.getStyleClass().add("restaurant-card");
        card.setPrefWidth(200);
        card.setPadding(new Insets(15));

        // Restaurant image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(170);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        // Use a placeholder image or actual restaurant image if available
        try {
            imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/foodorderingsystem/images/restaurant-placeholder.png")));
        } catch (Exception e) {
            System.err.println("Could not load restaurant image: " + e.getMessage());
        }

        // Restaurant name
        Label nameLabel = new Label(restaurant.getName());
        nameLabel.getStyleClass().add("restaurant-name");

        // Restaurant address
        String address = restaurant.getLocations().isEmpty() ? "No address available" :
                         restaurant.getLocations().get(0).getCity() + ", " +
                         restaurant.getLocations().get(0).getStreetName();
        Label addressLabel = new Label(address);

        // Button to view restaurant
        Button viewButton = new Button("View Menu");
        viewButton.setPrefWidth(Double.MAX_VALUE);
        viewButton.setOnAction(event -> handleViewRestaurant(restaurant));

        // Add all components to the card
        card.getChildren().addAll(imageView, nameLabel, addressLabel, viewButton);

        return card;
    }

    private void handleViewRestaurant(Restaurant restaurant) {
        try {
            // Store selected restaurant in session for access on the restaurant view
            SessionManager.getInstance().setSelectedRestaurant(restaurant);

            // Load the restaurantView.fxml resource
            Parent restaurantView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/restaurantView.fxml"));
            Scene scene = new Scene(restaurantView);
            Stage stage = (Stage) allRestaurantsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading restaurant view: " + e.getMessage());

            // Show error dialog
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not load restaurant view");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
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
    protected void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            try {
                List<Restaurant> searchResults = restaurantDataAccess.searchRestaurants(searchTerm);

                // Update UI with search results
                allRestaurantsContainer.getChildren().clear();
                for (Restaurant restaurant : searchResults) {
                    allRestaurantsContainer.getChildren().add(createRestaurantCard(restaurant));
                }
            } catch (SQLException e) {
                System.err.println("Error searching restaurants: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void handleHomeButton(ActionEvent event) {
        // We're already on the home/dashboard page
        // Refresh if needed
        loadRestaurants();
    }

    @FXML
    protected void handleRestaurantsButton(ActionEvent event) {
        // For now, this just refreshes the restaurants list
        loadRestaurants();
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
}
