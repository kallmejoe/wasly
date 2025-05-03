package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.*;
import com.example.foodorderingsystem.DataAccessLayer.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    // UI Components from FXML
    @FXML private BorderPane contentArea;
    @FXML private Label contentTitle;
    @FXML private Label statusLabel;
    @FXML private Label lblItemCount;
    @FXML private HBox actionButtons;
    @FXML private Button btnRefresh;
    @FXML private Button btnAdd;

    // Navigation Buttons
    @FXML private Button btnRestaurants;
    @FXML private Button btnProducts;
    @FXML private Button btnCategories;
    @FXML private Button btnCustomers;
    @FXML private Button btnOrders;
    @FXML private Button btnDeliveries;
    @FXML private Button btnPayments;
    @FXML private Button btnAdmins;
    @FXML private Button btnLogout;

    // Content Containers
    @FXML private VBox welcomeScreen;
    @FXML private ScrollPane restaurantsContainer;
    @FXML private ScrollPane productsContainer;
    @FXML private ScrollPane categoriesContainer;
    @FXML private ScrollPane customersContainer;
    @FXML private ScrollPane ordersContainer;
    @FXML private ScrollPane deliveriesContainer;
    @FXML private ScrollPane paymentsContainer;
    @FXML private ScrollPane adminsContainer;

    // FlowPanes for card layouts
    @FXML private FlowPane restaurantsPane;
    @FXML private FlowPane productsPane;
    @FXML private FlowPane categoriesPane;
    @FXML private FlowPane customersPane;
    @FXML private FlowPane ordersPane;
    @FXML private FlowPane deliveriesPane;
    @FXML private FlowPane paymentsPane;
    @FXML private FlowPane adminsPane;

    // Data Access Objects
    private RestaurantDataAccess restaurantDataAccess;
    private ProductDataAccess productDataAccess;
    private CategoryDataAccess categoryDataAccess;
    private CustomerDataAccess customerDataAccess;
    private OrderDataAccess orderDataAccess;
    private DeliveryDataAccess deliveryDataAccess;
    private PaymentDataAccess paymentDataAccess;
    private AdminDataAccess adminDataAccess;

    // Current section being viewed
    private String currentSection = "";

    // Formatter for dates
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize data access objects
        restaurantDataAccess = new RestaurantDataAccess();
        productDataAccess = new ProductDataAccess();
        categoryDataAccess = new CategoryDataAccess();
        customerDataAccess = new CustomerDataAccess();
        orderDataAccess = new OrderDataAccess();
        deliveryDataAccess = new DeliveryDataAccess();
        paymentDataAccess = new PaymentDataAccess();
        adminDataAccess = new AdminDataAccess();

        // Set welcome screen visible initially
        showWelcomeScreen();
    }

    // Navigation handlers
    @FXML
    private void handleRestaurants(ActionEvent event) {
        setCurrentSection("Restaurants");
        loadRestaurants();
    }

    @FXML
    private void handleProducts(ActionEvent event) {
        setCurrentSection("Products");
        loadProducts();
    }

    @FXML
    private void handleCategories(ActionEvent event) {
        setCurrentSection("Categories");
        loadCategories();
    }

    @FXML
    private void handleCustomers(ActionEvent event) {
        setCurrentSection("Customers");
        loadCustomers();
    }

    @FXML
    private void handleOrders(ActionEvent event) {
        setCurrentSection("Orders");
        loadOrders();
    }

    @FXML
    private void handleDeliveries(ActionEvent event) {
        setCurrentSection("Deliveries");
        loadDeliveries();
    }

    @FXML
    private void handlePayments(ActionEvent event) {
        setCurrentSection("Payments");
        loadPayments();
    }

    @FXML
    private void handleAdmins(ActionEvent event) {
        setCurrentSection("Admins");
        loadAdmins();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/foodorderingsystem/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Error loading login page: " + e.getMessage());
        }
    }

    // Action Handlers
    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshCurrentSection();
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        switch (currentSection) {
            case "Restaurants" -> openRestaurantDialog(new Restaurant());
            case "Products" -> openProductDialog(new Product());
            case "Categories" -> openCategoryDialog(new Category());
            case "Customers" -> openCustomerDialog(new Customer());
            case "Admins" -> openAdminDialog(new Admin());
        }
    }

    // Data loading methods
    protected void loadRestaurants() {
        restaurantsPane.getChildren().clear();
        statusLabel.setText("Loading restaurants...");

        try {
            List<Restaurant> restaurants = restaurantDataAccess.getAllRestaurants();
            for (Restaurant restaurant : restaurants) {
                restaurantsPane.getChildren().add(createRestaurantCard(restaurant));
            }
            lblItemCount.setText("Total Restaurants: " + restaurants.size());
            statusLabel.setText("Restaurants loaded successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading restaurants: " + e.getMessage());
            showErrorAlert("Database Error", "Failed to load restaurants: " + e.getMessage());
        }
    }

    protected void loadProducts() {
        productsPane.getChildren().clear();
        statusLabel.setText("Loading products...");

        try {
            List<Product> products = productDataAccess.getAllProducts();
            for (Product product : products) {
                productsPane.getChildren().add(createProductCard(product));
            }
            lblItemCount.setText("Total Products: " + products.size());
            statusLabel.setText("Products loaded successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading products: " + e.getMessage());
            showErrorAlert("Database Error", "Failed to load products: " + e.getMessage());
        }
    }

    protected void loadCategories() {
        categoriesPane.getChildren().clear();
        statusLabel.setText("Loading categories...");

        try {
            List<Category> categories = categoryDataAccess.getAllCategories();
            for (Category category : categories) {
                categoriesPane.getChildren().add(createCategoryCard(category));
            }
            lblItemCount.setText("Total Categories: " + categories.size());
            statusLabel.setText("Categories loaded successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading categories: " + e.getMessage());
            showErrorAlert("Database Error", "Failed to load categories: " + e.getMessage());
        }
    }

    protected void loadCustomers() {
        customersPane.getChildren().clear();
        statusLabel.setText("Loading customers...");

        try {
            List<Customer> customers = customerDataAccess.getAllCustomers();
            for (Customer customer : customers) {
                customersPane.getChildren().add(createCustomerCard(customer));
            }
            lblItemCount.setText("Total Customers: " + customers.size());
            statusLabel.setText("Customers loaded successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading customers: " + e.getMessage());
            showErrorAlert("Database Error", "Failed to load customers: " + e.getMessage());
        }
    }

    protected void loadOrders() {
        ordersPane.getChildren().clear();
        statusLabel.setText("Loading orders...");

        try {
            List<Order> orders = orderDataAccess.getAllOrders();
            for (Order order : orders) {
                ordersPane.getChildren().add(createOrderCard(order));
            }
            lblItemCount.setText("Total Orders: " + orders.size());
            statusLabel.setText("Orders loaded successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading orders: " + e.getMessage());
            showErrorAlert("Database Error", "Failed to load orders: " + e.getMessage());
        }
    }

    protected void loadDeliveries() {
        deliveriesPane.getChildren().clear();
        statusLabel.setText("Loading deliveries...");

        try {
            List<Delivery> deliveries = deliveryDataAccess.getAllDeliveries();
            for (Delivery delivery : deliveries) {
                deliveriesPane.getChildren().add(createDeliveryCard(delivery));
            }
            lblItemCount.setText("Total Deliveries: " + deliveries.size());
            statusLabel.setText("Deliveries loaded successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading deliveries: " + e.getMessage());
            showErrorAlert("Database Error", "Failed to load deliveries: " + e.getMessage());
        }
    }

    protected void loadPayments() {
        paymentsPane.getChildren().clear();
        statusLabel.setText("Loading payments...");

        try {
            List<Payment> payments = paymentDataAccess.getAllPayments();
            for (Payment payment : payments) {
                paymentsPane.getChildren().add(createPaymentCard(payment));
            }
            lblItemCount.setText("Total Payments: " + payments.size());
            statusLabel.setText("Payments loaded successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading payments: " + e.getMessage());
            showErrorAlert("Database Error", "Failed to load payments: " + e.getMessage());
        }
    }

    protected void loadAdmins() {
        adminsPane.getChildren().clear();
        statusLabel.setText("Loading admins...");

        try {
            List<Admin> admins = adminDataAccess.getAllAdmins();
            for (Admin admin : admins) {
                adminsPane.getChildren().add(createAdminCard(admin));
            }
            lblItemCount.setText("Total Admins: " + admins.size());
            statusLabel.setText("Admins loaded successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading admins: " + e.getMessage());
            showErrorAlert("Database Error", "Failed to load admins: " + e.getMessage());
        }
    }

    // Card creation methods
    private VBox createRestaurantCard(Restaurant restaurant) {
        VBox card = createBaseCard();

        Label nameLabel = new Label(restaurant.getName());
        nameLabel.getStyleClass().add("card-title");

        HBox phoneBox = new HBox(5);
        phoneBox.setAlignment(Pos.CENTER_LEFT);
        if (restaurant.getPhoneNo() != null && !restaurant.getPhoneNo().isEmpty()) {
            Label phoneLabel = new Label("Phone: " + restaurant.getPhoneNo().get(0));
            phoneBox.getChildren().add(phoneLabel);
        }

        TextFlow descriptionFlow = new TextFlow();
        Text descText = new Text(restaurant.getDescription());
        descriptionFlow.getChildren().add(descText);
        descriptionFlow.setPrefHeight(60);

        HBox locationBox = new HBox(5);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        if (restaurant.getLocations() != null && !restaurant.getLocations().isEmpty()) {
            Location location = restaurant.getLocations().get(0);
            Label locationLabel = new Label(location.getAddress());
            locationBox.getChildren().add(locationLabel);
        }

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("card-button");
        editButton.setOnAction(e -> openRestaurantDialog(restaurant));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("card-button", "delete-button");
        deleteButton.setOnAction(e -> handleDeleteRestaurant(restaurant));

        buttonsBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(nameLabel, phoneBox, descriptionFlow, locationBox, new Separator(), buttonsBox);

        return card;
    }

    private VBox createProductCard(Product product) {
        VBox card = createBaseCard();

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("card-title");

        Label priceLabel = new Label(String.format("Price: $%.2f", product.getPrice()));

        TextFlow descriptionFlow = new TextFlow();
        Text descText = new Text(product.getDescription());
        descriptionFlow.getChildren().add(descText);
        descriptionFlow.setPrefHeight(60);

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("card-button");
        editButton.setOnAction(e -> openProductDialog(product));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("card-button", "delete-button");
        deleteButton.setOnAction(e -> handleDeleteProduct(product));

        buttonsBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(nameLabel, priceLabel, descriptionFlow, new Separator(), buttonsBox);

        return card;
    }

    private VBox createCategoryCard(Category category) {
        VBox card = createBaseCard();

        Label nameLabel = new Label(category.getName());
        nameLabel.getStyleClass().add("card-title");

        TextFlow descriptionFlow = new TextFlow();
        Text descText = new Text(category.getDescription());
        descriptionFlow.getChildren().add(descText);
        descriptionFlow.setPrefHeight(60);

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("card-button");
        editButton.setOnAction(e -> openCategoryDialog(category));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("card-button", "delete-button");
        deleteButton.setOnAction(e -> handleDeleteCategory(category));

        buttonsBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(nameLabel, descriptionFlow, new Separator(), buttonsBox);

        return card;
    }

    private VBox createCustomerCard(Customer customer) {
        VBox card = createBaseCard();

        Label nameLabel = new Label(customer.getName());
        nameLabel.getStyleClass().add("card-title");

        Label emailLabel = new Label("Email: " + customer.getEmail());

        HBox phoneBox = new HBox(5);
        phoneBox.setAlignment(Pos.CENTER_LEFT);
        if (customer.getPhoneNo() != null && !customer.getPhoneNo().isEmpty()) {
            Label phoneLabel = new Label("Phone: " + customer.getPhoneNo().get(0));
            phoneBox.getChildren().add(phoneLabel);
        }

        HBox addressBox = new HBox(5);
        addressBox.setAlignment(Pos.CENTER_LEFT);
        List<Location> locations = customer.getLocation();
        if (locations != null && !locations.isEmpty()) {
            Location location = locations.get(0);
            Label addressLabel = new Label(location.getAddress());
            addressBox.getChildren().add(addressLabel);
        }

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("card-button");
        editButton.setOnAction(e -> openCustomerDialog(customer));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("card-button", "delete-button");
        deleteButton.setOnAction(e -> handleDeleteCustomer(customer));

        buttonsBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(nameLabel, emailLabel, phoneBox, addressBox, new Separator(), buttonsBox);

        return card;
    }

    private VBox createOrderCard(Order order) {
        VBox card = createBaseCard();

        Label orderIdLabel = new Label("Order #" + order.getOrderId());
        orderIdLabel.getStyleClass().add("card-title");

        Label customerLabel = new Label("Customer: " + order.getCustomer().getName());

        // Add status label - with null check in case getStatus() is not implemented


        // Fix: Convert java.util.Date to LocalDateTime properly using try-catch for safety
        Label dateLabel;
        if (order.getOrderDate() != null) {
            try {
                if (order.getOrderDate() instanceof java.util.Date) {
                    // Properly convert java.util.Date to LocalDateTime
                    java.util.Date utilDate = (java.util.Date) order.getOrderDate();
                    java.time.Instant instant = utilDate.toInstant();
                    java.time.LocalDateTime localDateTime = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                    dateLabel = new Label("Date: " + localDateTime.format(dateFormatter));
                } else {
                    dateLabel = new Label("Date: " + order.getOrderDate().toString());
                }
            } catch (Exception e) {
                dateLabel = new Label("Date: " + order.getOrderDate().toString());
            }
        } else {
            dateLabel = new Label("Date: N/A");
        }

        Label totalLabel = new Label(String.format("Total: $%.2f", order.getTotalAmount()));
        totalLabel.getStyleClass().add("price");

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("View Details");
        viewButton.getStyleClass().add("card-button");
        viewButton.setOnAction(e -> viewOrderDetails(order));

        buttonsBox.getChildren().add(viewButton);

        card.getChildren().addAll(orderIdLabel, customerLabel, statusLabel, dateLabel, totalLabel, new Separator(), buttonsBox);

        return card;
    }

    private VBox createDeliveryCard(Delivery delivery) {
        VBox card = createBaseCard();

        Label deliveryIdLabel = new Label("Delivery #" + delivery.getDeliveryId());
        deliveryIdLabel.getStyleClass().add("card-title");

        Label orderLabel = new Label("Order #" + delivery.getOrder().getOrderId());

        Label statusLabel = new Label("Status: " + delivery.getStatus());
        statusLabel.getStyleClass().add("order-status");

        // Fix: Convert java.util.Date to LocalDateTime properly using try-catch for safety
        Label dateLabel;
        if (delivery.getDeliveryDate() != null) {
            try {
                if (delivery.getDeliveryDate() instanceof java.util.Date) {
                    // Properly convert java.util.Date to LocalDateTime
                    java.util.Date utilDate = (java.util.Date) delivery.getDeliveryDate();
                    java.time.Instant instant = utilDate.toInstant();
                    java.time.LocalDateTime localDateTime = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                    dateLabel = new Label("Date: " + localDateTime.format(dateFormatter));
                } else {
                    dateLabel = new Label("Date: " + delivery.getDeliveryDate().toString());
                }
            } catch (Exception e) {
                dateLabel = new Label("Date: " + delivery.getDeliveryDate().toString());
            }
        } else {
            dateLabel = new Label("Date: N/A");
        }

        TextFlow addressFlow = new TextFlow();
        Text addressText = new Text("Address: " + delivery.getLocation().getAddress());
        addressFlow.getChildren().add(addressText);

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("View Details");
        viewButton.getStyleClass().add("card-button");
        viewButton.setOnAction(e -> viewDeliveryDetails(delivery));

        buttonsBox.getChildren().add(viewButton);

        card.getChildren().addAll(deliveryIdLabel, orderLabel, statusLabel, dateLabel, addressFlow, new Separator(), buttonsBox);

        return card;
    }

    private VBox createPaymentCard(Payment payment) {
        VBox card = createBaseCard();

        Label paymentIdLabel = new Label("Payment #" + payment.getPaymentId());
        paymentIdLabel.getStyleClass().add("card-title");

        Label orderLabel = new Label("Order #" + payment.getOrderId());

        Label amountLabel = new Label(String.format("Amount: $%.2f", payment.getAmount()));
        amountLabel.getStyleClass().add("price");

        Label methodLabel = new Label("Method: " + payment.getPaymentMethod());

        Label statusLabel = new Label("Status: " + payment.getStatus());
        statusLabel.getStyleClass().add("order-status");

        // Fix: Convert java.util.Date to LocalDateTime properly using try-catch for safety
        Label dateLabel;
        if (payment.getPaymentDate() != null) {
            try {
                if (payment.getPaymentDate() instanceof java.util.Date) {
                    // Properly convert java.util.Date to LocalDateTime
                    java.util.Date utilDate = (java.util.Date) payment.getPaymentDate();
                    java.time.Instant instant = utilDate.toInstant();
                    java.time.LocalDateTime localDateTime = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                    dateLabel = new Label("Date: " + localDateTime.format(dateFormatter));
                } else {
                    dateLabel = new Label("Date: " + payment.getPaymentDate().toString());
                }
            } catch (Exception e) {
                dateLabel = new Label("Date: " + payment.getPaymentDate().toString());
            }
        } else {
            dateLabel = new Label("Date: N/A");
        }

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("View Details");
        viewButton.getStyleClass().add("card-button");
        viewButton.setOnAction(e -> viewPaymentDetails(payment));

        buttonsBox.getChildren().add(viewButton);

        card.getChildren().addAll(paymentIdLabel, orderLabel, amountLabel, methodLabel, statusLabel, dateLabel, new Separator(), buttonsBox);

        return card;
    }

    private VBox createAdminCard(Admin admin) {
        VBox card = createBaseCard();

        Label nameLabel = new Label(admin.getName());
        nameLabel.getStyleClass().add("card-title");

        Label emailLabel = new Label("Email: " + admin.getEmail());

        Label roleLabel = new Label("Role: " + (admin.getRole() != null ? admin.getRole() : "Administrator"));

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("card-button");
        editButton.setOnAction(e -> openAdminDialog(admin));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("card-button", "delete-button");
        deleteButton.setOnAction(e -> handleDeleteAdmin(admin));

        buttonsBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(nameLabel, emailLabel, roleLabel, new Separator(), buttonsBox);

        return card;
    }

    // Helper method to create base card layout
    private VBox createBaseCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setMinHeight(200);
        return card;
    }

    // Dialog methods
    private void openRestaurantDialog(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/foodorderingsystem/restaurant-dialog.fxml"));
            DialogPane dialogPane = loader.load();

            RestaurantDialogController controller = loader.getController();
            controller.setRestaurant(restaurant);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(restaurant.getRestaurantId() == 0 ? "Add Restaurant" : "Edit Restaurant");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(contentArea.getScene().getWindow());

            // Set result converter to handle OK button action
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    controller.handleOk();
                }
                return buttonType;
            });

            dialog.showAndWait();

            // Refresh after dialog closes
            loadRestaurants();

        } catch (IOException e) {
            showErrorAlert("Dialog Error", "Error opening restaurant dialog: " + e.getMessage());
        }
    }

    private void openProductDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/foodorderingsystem/product-dialog.fxml"));
            DialogPane dialogPane = loader.load();

            ProductDialogController controller = loader.getController();
            controller.setProduct(product);

            Dialog<Product> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(product.getProductId() == 0 ? "Add Product" : "Edit Product");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(contentArea.getScene().getWindow());

            // Connect the controller with the dialog
            controller.setDialog(dialog);

            dialog.showAndWait();

            // Refresh after dialog closes
            loadProducts();

        } catch (IOException e) {
            showErrorAlert("Dialog Error", "Error opening product dialog: " + e.getMessage());
        }
    }

    private void openCategoryDialog(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/foodorderingsystem/category-dialog.fxml"));
            DialogPane dialogPane = loader.load();

            CategoryDialogController controller = loader.getController();
            controller.setCategory(category);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(category.getCategoryId() == 0 ? "Add Category" : "Edit Category");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(contentArea.getScene().getWindow());

            // Set result converter to handle OK button action
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    controller.handleOk();
                }
                return buttonType;
            });

            dialog.showAndWait();

            // Refresh after dialog closes
            loadCategories();

        } catch (IOException e) {
            showErrorAlert("Dialog Error", "Error opening category dialog: " + e.getMessage());
        }
    }

    private void openCustomerDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/foodorderingsystem/customer-dialog.fxml"));
            DialogPane dialogPane = loader.load();

            CustomerDialogController controller = loader.getController();
            controller.setCustomer(customer);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(customer.getCustomerId() == 0 ? "Add Customer" : "Edit Customer");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(contentArea.getScene().getWindow());

            // Set result converter to handle OK button action
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    controller.handleOk();
                }
                return buttonType;
            });

            dialog.showAndWait();

            // Refresh after dialog closes
            loadCustomers();

        } catch (IOException e) {
            showErrorAlert("Dialog Error", "Error opening customer dialog: " + e.getMessage());
        }
    }

    private void openAdminDialog(Admin admin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/foodorderingsystem/admin-dialog.fxml"));
            DialogPane dialogPane = loader.load();

            AdminDialogController controller = loader.getController();
            controller.setAdmin(admin);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(admin.getAdminId() == 0 ? "Add Admin" : "Edit Admin");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(contentArea.getScene().getWindow());

            // Set result converter to handle OK button action
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    controller.handleOk();
                }
                return buttonType;
            });

            dialog.showAndWait();

            // Refresh after dialog closes
            loadAdmins();

        } catch (IOException e) {
            showErrorAlert("Dialog Error", "Error opening admin dialog: " + e.getMessage());
        }
    }

    // Delete handlers
    private void handleDeleteRestaurant(Restaurant restaurant) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Restaurant");
        confirmAlert.setContentText("Are you sure you want to delete " + restaurant.getName() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                restaurantDataAccess.deleteRestaurant(restaurant.getRestaurantId());
                statusLabel.setText("Restaurant deleted successfully");
                loadRestaurants();
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Error deleting restaurant: " + e.getMessage());
            }
        }
    }

    private void handleDeleteProduct(Product product) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Product");
        confirmAlert.setContentText("Are you sure you want to delete " + product.getName() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                productDataAccess.deleteProduct(product.getProductId());
                statusLabel.setText("Restaurant deleted successfully");
                loadProducts();
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Error deleting product: " + e.getMessage());
            }
        }
    }

    private void handleDeleteCategory(Category category) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Category");
        confirmAlert.setContentText("Are you sure you want to delete " + category.getName() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                categoryDataAccess.deleteCategory(category.getCategoryId());
                statusLabel.setText("Restaurant deleted successfully");
                loadCategories();
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Error deleting category: " + e.getMessage());
            }
        }
    }

    private void handleDeleteCustomer(Customer customer) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Customer");
        confirmAlert.setContentText("Are you sure you want to delete " + customer.getName() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                customerDataAccess.deleteCustomer(customer.getCustomerId());
                statusLabel.setText("Restaurant deleted successfully");
                loadCustomers();
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Error deleting customer: " + e.getMessage());
            }
        }
    }

    private void handleDeleteAdmin(Admin admin) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Admin");
        confirmAlert.setContentText("Are you sure you want to delete " + admin.getName() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                adminDataAccess.deleteAdmin(admin.getAdminId());
                statusLabel.setText("Restaurant deleted successfully");
                loadAdmins();
            } catch (SQLException e) {
                showErrorAlert("Database Error", "Error deleting admin: " + e.getMessage());
            }
        }
    }

    // View details methods
    private void viewOrderDetails(Order order) {
        // Show order details in a dialog
        showInfoAlert("Coming Soon", "Order details view will be implemented soon");
    }

    private void viewDeliveryDetails(Delivery delivery) {
        // Show delivery details in a dialog
        showInfoAlert("Coming Soon", "Delivery details view will be implemented soon");
    }

    private void viewPaymentDetails(Payment payment) {
        // Show payment details in a dialog
        showInfoAlert("Coming Soon", "Payment details view will be implemented soon");
    }

    // Utility methods
    private void showWelcomeScreen() {
        hideAllContainers();
        welcomeScreen.setVisible(true);
        welcomeScreen.setManaged(true);
        contentTitle.setText("Dashboard");
        actionButtons.setVisible(false);
        lblItemCount.setText("");
    }

    private void setCurrentSection(String section) {
        currentSection = section;
        contentTitle.setText(section);
        hideAllContainers();
        actionButtons.setVisible(true);

        // Show Add button for entities that can be added
        btnAdd.setVisible(currentSection.equals("Restaurants") ||
                          currentSection.equals("Products") ||
                          currentSection.equals("Categories") ||
                          currentSection.equals("Customers") ||
                          currentSection.equals("Admins"));

        switch (section) {
            case "Restaurants" -> {
                restaurantsContainer.setVisible(true);
                restaurantsContainer.setManaged(true);
            }
            case "Products" -> {
                productsContainer.setVisible(true);
                productsContainer.setManaged(true);
            }
            case "Categories" -> {
                categoriesContainer.setVisible(true);
                categoriesContainer.setManaged(true);
            }
            case "Customers" -> {
                customersContainer.setVisible(true);
                customersContainer.setManaged(true);
            }
            case "Orders" -> {
                ordersContainer.setVisible(true);
                ordersContainer.setManaged(true);
            }
            case "Deliveries" -> {
                deliveriesContainer.setVisible(true);
                deliveriesContainer.setManaged(true);
            }
            case "Payments" -> {
                paymentsContainer.setVisible(true);
                paymentsContainer.setManaged(true);
            }
            case "Admins" -> {
                adminsContainer.setVisible(true);
                adminsContainer.setManaged(true);
            }
        }
    }

    private void hideAllContainers() {
        welcomeScreen.setVisible(false);
        welcomeScreen.setManaged(false);
        restaurantsContainer.setVisible(false);
        restaurantsContainer.setManaged(false);
        productsContainer.setVisible(false);
        productsContainer.setManaged(false);
        categoriesContainer.setVisible(false);
        categoriesContainer.setManaged(false);
        customersContainer.setVisible(false);
        customersContainer.setManaged(false);
        ordersContainer.setVisible(false);
        ordersContainer.setManaged(false);
        deliveriesContainer.setVisible(false);
        deliveriesContainer.setManaged(false);
        paymentsContainer.setVisible(false);
        paymentsContainer.setManaged(false);
        adminsContainer.setVisible(false);
        adminsContainer.setManaged(false);
    }

    private void refreshCurrentSection() {
        switch (currentSection) {
            case "Restaurants" -> loadRestaurants();
            case "Products" -> loadProducts();
            case "Categories" -> loadCategories();
            case "Customers" -> loadCustomers();
            case "Orders" -> loadOrders();
            case "Deliveries" -> loadDeliveries();
            case "Payments" -> loadPayments();
            case "Admins" -> loadAdmins();
        }
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
