package com.example.foodorderingsystem.PresentationLayer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.example.foodorderingsystem.BusinessLayer.Account;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.RestaurantDataAccess;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        card.getStyleClass().add("modern-restaurant-card");
        card.setPadding(new Insets(0));

        // Restaurant placeholder with initial letter
        StackPane placeholder = new StackPane();
        placeholder.getStyleClass().add("restaurant-placeholder");

        // Use first letter of restaurant name as placeholder
        String firstLetter = restaurant.getName().substring(0, 1).toUpperCase();
        Label initialLabel = new Label(firstLetter);
        initialLabel.getStyleClass().add("restaurant-initial");
        placeholder.getChildren().add(initialLabel);

        // Restaurant info container
        VBox infoContainer = new VBox(6);
        infoContainer.setPadding(new Insets(12, 0, 8, 0));

        // Restaurant name
        Label nameLabel = new Label(restaurant.getName());
        nameLabel.getStyleClass().add("restaurant-name");

        // Restaurant rating
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER_LEFT);

        // Fake rating (sample data)
        Label ratingLabel = new Label("★★★★☆ 4.0");
        ratingLabel.getStyleClass().add("restaurant-rating");

        // Category (hardcoded for demonstration)
        Label categoryLabel = new Label("Restaurant");
        categoryLabel.getStyleClass().add("restaurant-category");
        ratingBox.getChildren().addAll(ratingLabel, categoryLabel);

        // Restaurant address
        String address = restaurant.getLocations().isEmpty() ? "No address available" :
                         restaurant.getLocations().get(0).getCity() + ", " +
                         restaurant.getLocations().get(0).getStreetName();
        Label addressLabel = new Label(address);
        addressLabel.getStyleClass().add("restaurant-address");

        // Button to view restaurant
        Button viewButton = new Button("View Menu");
        viewButton.getStyleClass().add("view-menu-button");
        viewButton.setPrefWidth(Double.MAX_VALUE);
        viewButton.setOnAction(event -> handleViewRestaurant(restaurant));

        // Add all to info container
        infoContainer.getChildren().addAll(nameLabel, ratingBox, addressLabel);

        // Add all components to the card
        card.getChildren().addAll(placeholder, infoContainer, viewButton);

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

                if (searchResults.isEmpty()) {
                    // Show a message when no results are found
                    Label noResultsLabel = new Label("No restaurants found matching \"" + searchTerm + "\"");
                    noResultsLabel.getStyleClass().add("no-results-message");
                    noResultsLabel.setStyle("-fx-font-size: 16px; -fx-padding: 20px;");
                    allRestaurantsContainer.getChildren().add(noResultsLabel);
                } else {
                    // Display search results
                    for (Restaurant restaurant : searchResults) {
                        allRestaurantsContainer.getChildren().add(createRestaurantCard(restaurant));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error searching restaurants: " + e.getMessage());

                // Show error alert
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Search Error");
                alert.setHeaderText("Could not search restaurants");
                alert.setContentText("An error occurred: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            // If search field is empty, reload all restaurants
            loadRestaurants();
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
