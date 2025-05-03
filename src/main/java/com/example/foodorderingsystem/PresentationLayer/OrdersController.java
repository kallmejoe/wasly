package com.example.foodorderingsystem.PresentationLayer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import com.example.foodorderingsystem.BusinessLayer.Order;
import com.example.foodorderingsystem.DataAccessLayer.OrderDataAccess;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class OrdersController implements Initializable {

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ComboBox<String> dateFilter;

    @FXML
    private Button applyFilterButton;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, String> orderIdColumn;

    @FXML
    private TableColumn<Order, String> dateColumn;

    @FXML
    private TableColumn<Order, String> restaurantColumn;

    @FXML
    private TableColumn<Order, String> totalColumn;

    @FXML
    private StackPane noOrdersPane;

    @FXML
    private Label cartItemCount;

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

    private OrderDataAccess orderDataAccess;
    private ObservableList<Order> orders;

    public OrdersController() {
        orderDataAccess = new OrderDataAccess();
        orders = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadOrders();
        updateCartCount();

        // Setup default filter values
        statusFilter.getSelectionModel().selectFirst();
        dateFilter.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {
        // Order ID column
        orderIdColumn.setCellValueFactory(data ->
            new SimpleStringProperty(String.valueOf(data.getValue().getOrderId())));

        // Date column
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a");
        dateColumn.setCellValueFactory(data -> {
            if (data.getValue().getOrderDate() != null) {
                return new SimpleStringProperty(dateFormat.format(data.getValue().getOrderDate()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Restaurant column
        restaurantColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getRestaurant().getName()));

        // Total column - now showing calculated total from has_Product_Order table
        totalColumn.setCellValueFactory(data ->
            new SimpleStringProperty(String.format("$%.2f", data.getValue().getTotalAmount())));
    }

    private void loadOrders() {
        try {
            int customerId = ((SessionManager.getInstance().getCurrentUser() instanceof com.example.foodorderingsystem.BusinessLayer.Customer) ?
                    ((com.example.foodorderingsystem.BusinessLayer.Customer) SessionManager.getInstance().getCurrentUser()).getCustomerId() : 0);

            if (customerId > 0) {
                List<Order> customerOrders = orderDataAccess.getOrdersByCustomerId(customerId);

                // Clear and add new orders
                orders.clear();
                orders.addAll(customerOrders);
                ordersTable.setItems(orders);

                // Show or hide the no orders message
                boolean hasOrders = !customerOrders.isEmpty();
                ordersTable.setVisible(hasOrders);
                ordersTable.setManaged(hasOrders);
                noOrdersPane.setVisible(!hasOrders);
                noOrdersPane.setManaged(!hasOrders);
            } else {
                ordersTable.setVisible(false);
                ordersTable.setManaged(false);
                noOrdersPane.setVisible(true);
                noOrdersPane.setManaged(true);
            }
        } catch (SQLException e) {
            System.err.println("Error loading orders: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load orders", e.getMessage());
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
    protected void handleApplyFilter(ActionEvent event) {
        String status = statusFilter.getValue();
        String datePeriod = dateFilter.getValue();

        try {
            int customerId = ((SessionManager.getInstance().getCurrentUser() instanceof com.example.foodorderingsystem.BusinessLayer.Customer) ?
                    ((com.example.foodorderingsystem.BusinessLayer.Customer) SessionManager.getInstance().getCurrentUser()).getCustomerId() : 0);

            if (customerId > 0) {
                List<Order> filteredOrders;

                // Apply filters based on selections
                if ("All".equals(status) && "All Time".equals(datePeriod)) {
                    filteredOrders = orderDataAccess.getOrdersByCustomerId(customerId);
                } else {
                    filteredOrders = orderDataAccess.getFilteredOrders(customerId, status, datePeriod);
                }

                orders.clear();
                orders.addAll(filteredOrders);

                // Show or hide the no orders message
                boolean hasOrders = !filteredOrders.isEmpty();
                ordersTable.setVisible(hasOrders);
                ordersTable.setManaged(hasOrders);
                noOrdersPane.setVisible(!hasOrders);
                noOrdersPane.setManaged(!hasOrders);
            }
        } catch (SQLException e) {
            System.err.println("Error filtering orders: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Could not filter orders", e.getMessage());
        }
    }

    private void handleViewOrderDetails(Order order) {
        // Store the selected order in the session
        SessionManager.getInstance().setSelectedOrder(order);

        // Navigate to order details view
        try {
            /*
            // If you have an order-details-view.fxml, uncomment this code
            Parent orderDetailsView = FXMLLoader.load(getClass().getResource("/com/example/foodorderingsystem/order-details-view.fxml"));
            Scene orderDetailsScene = new Scene(orderDetailsView);
            Stage currentStage = (Stage) ordersTable.getScene().getWindow();
            currentStage.setScene(orderDetailsScene);
            currentStage.show();
            */

            // For now, we'll just show order details in an alert
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a");
            String formattedDate = order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "N/A";

            showAlert(Alert.AlertType.INFORMATION,
                      "Order Details",
                      "Order #" + order.getOrderId(),
                      "Restaurant: " + order.getRestaurant().getName() + "\n" +
                      "Total: $" + String.format("%.2f", order.getTotalAmount()) + "\n" +
                      "Date: " + formattedDate);
        } catch (Exception e) {
            System.err.println("Error showing order details: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Could not show order details", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigation handlers
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to Home", e.getMessage());
        }
    }

    @FXML
    protected void handleRestaurantsButton(ActionEvent event) {
        // Navigate to restaurants view (dashboard has restaurants)
        handleHomeButton(event);
    }

    @FXML
    protected void handleOrdersButton(ActionEvent event) {
        // Instead of showing the "under development" message, just refresh the orders
        loadOrders();
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to Cart", e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to Profile", e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to Login", e.getMessage());
        }
    }
}
