package com.example.foodorderingsystem.PresentationLayer;

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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

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
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, Order> actionsColumn;

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
        dateColumn.setCellValueFactory(data -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a");
            return new SimpleStringProperty(dateFormat.format(data.getValue().getOrderDate()));
        });

        // Restaurant column
        restaurantColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getRestaurant().getName()));

        // Total column
        totalColumn.setCellValueFactory(data ->
            new SimpleStringProperty(String.format("$%.2f", data.getValue().getTotalAmount())));

        // Status column
        statusColumn.setCellValueFactory(data -> {
            String status = data.getValue().getStatus();
            return new SimpleStringProperty(status);
        });

        // Status column styling
        statusColumn.setCellFactory(column -> {
            return new TableCell<Order, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);

                        switch (item.toLowerCase()) {
                            case "pending":
                                setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 4;");
                                break;
                            case "processing":
                                setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 4;");
                                break;
                            case "delivered":
                                setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 4;");
                                break;
                            case "cancelled":
                                setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 4;");
                                break;
                            default:
                                setStyle("");
                                break;
                        }
                    }
                }
            };
        });

        // Actions column with view details button
        actionsColumn.setCellFactory(new Callback<TableColumn<Order, Order>, TableCell<Order, Order>>() {
            @Override
            public TableCell<Order, Order> call(TableColumn<Order, Order> param) {
                return new TableCell<Order, Order>() {
                    private final Button viewButton = new Button("View Details");

                    {
                        viewButton.setOnAction(event -> {
                            Order order = getTableView().getItems().get(getIndex());
                            handleViewOrderDetails(order);
                        });
                    }

                    @Override
                    protected void updateItem(Order order, boolean empty) {
                        super.updateItem(order, empty);

                        if (order == null || empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(viewButton);
                        }
                    }
                };
            }
        });
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
            showAlert(Alert.AlertType.INFORMATION,
                      "Order Details",
                      "Order #" + order.getOrderId(),
                      "Restaurant: " + order.getRestaurant().getName() + "\n" +
                      "Status: " + order.getStatus() + "\n" +
                      "Total: $" + String.format("%.2f", order.getTotalAmount()) + "\n" +
                      "Date: " + new SimpleDateFormat("MMM d, yyyy h:mm a").format(order.getOrderDate()));
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
