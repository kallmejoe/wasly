package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.*;
import com.example.foodorderingsystem.DataAccessLayer.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.geometry.Insets;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController {

    @FXML private Label sectionTitleLabel;
    @FXML private Label statusLabel;
    @FXML private Label lblItemCount;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
//    @FXML private Button logoutButton;
//    @FXML private Pagination pagination;
//
//    // Restaurant table elements
//    @FXML private TableView<Restaurant> restaurantsTable;
//    @FXML private TableColumn<Restaurant, Integer> restaurantIdColumn;
//    @FXML private TableColumn<Restaurant, String> restaurantNameColumn;
//    @FXML private TableColumn<Restaurant, String> restaurantAddressColumn;
//    @FXML private TableColumn<Restaurant, String> restaurantContactColumn;
//    @FXML private TableColumn<Restaurant, Void> restaurantActionsColumn;
//
//    // Customer table elements
//    @FXML private TableView<Customer> customersTable;
//    @FXML private TableColumn<Customer, Integer> customerIdColumn;
//    @FXML private TableColumn<Customer, String> customerNameColumn;
//    @FXML private TableColumn<Customer, String> customerEmailColumn;
//    @FXML private TableColumn<Customer, String> customerPhoneColumn;
//    @FXML private TableColumn<Customer, Void> customerActionsColumn;
//
//    // Product table elements
//    @FXML private TableView<Product> productsTable;
//    @FXML private TableColumn<Product, Integer> productIdColumn;
//    @FXML private TableColumn<Product, String> productNameColumn;
//    @FXML private TableColumn<Product, Double> productPriceColumn;
//    @FXML private TableColumn<Product, String> productCategoryColumn;
//    @FXML private TableColumn<Product, String> productRestaurantColumn;
//    @FXML private TableColumn<Product, Integer> productStockColumn;
//    @FXML private TableColumn<Product, Void> productActionsColumn;
//
//    // Category table elements
//    @FXML private TableView<Category> categoriesTable;
//    @FXML private TableColumn<Category, Integer> categoryIdColumn;
//    @FXML private TableColumn<Category, String> categoryNameColumn;
//    @FXML private TableColumn<Category, String> categoryDescriptionColumn;
//    @FXML private TableColumn<Category, Void> categoryActionsColumn;
//
//    // Order table elements
//    @FXML private TableView<Order> ordersTable;
//    @FXML private TableColumn<Order, Integer> orderIdColumn;
//    @FXML private TableColumn<Order, String> orderDateColumn;
//    @FXML private TableColumn<Order, String> orderCustomerColumn;
//    @FXML private TableColumn<Order, String> orderRestaurantColumn;
//    @FXML private TableColumn<Order, String> orderStatusColumn;
//    @FXML private TableColumn<Order, Double> orderTotalColumn;
//    @FXML private TableColumn<Order, Void> orderActionsColumn;
//
//    // Payment table elements
//    @FXML private TableView<Payment> paymentsTable;
//    @FXML private TableColumn<Payment, Integer> paymentIdColumn;
//    @FXML private TableColumn<Payment, Integer> paymentOrderColumn;
//    @FXML private TableColumn<Payment, String> paymentMethodColumn;
//    @FXML private TableColumn<Payment, String> paymentStatusColumn;
//    @FXML private TableColumn<Payment, Double> paymentAmountColumn;
//    @FXML private TableColumn<Payment, Void> paymentActionsColumn;
//
//    // Delivery table elements
//    @FXML private TableView<Delivery> deliveriesTable;
//    @FXML private TableColumn<Delivery, Integer> deliveryIdColumn;
//    @FXML private TableColumn<Delivery, String> deliveryNameColumn;
//    @FXML private TableColumn<Delivery, Integer> deliveryOrderColumn;
//    @FXML private TableColumn<Delivery, String> deliveryStatusColumn;
//    @FXML private TableColumn<Delivery, String> deliveryLocationColumn;
//    @FXML private TableColumn<Delivery, Void> deliveryActionsColumn;
//
//    // Admin table elements
//    @FXML private TableView<Admin> adminsTable;
//    @FXML private TableColumn<Admin, Integer> adminIdColumn;
//    @FXML private TableColumn<Admin, String> adminNameColumn;
//    @FXML private TableColumn<Admin, String> adminEmailColumn;
//    @FXML private TableColumn<Admin, String> adminPhoneColumn;
//    @FXML private TableColumn<Admin, Void> adminActionsColumn;
//
//    // Data Access Objects
//    private RestaurantDataAccess restaurantDataAccess;
//    private CustomerDataAccess customerDataAccess;
//    private ProductDataAccess productDataAccess;
//    private CategoryDataAccess categoryDataAccess;
//    private OrderDataAccess orderDataAccess;
//    private PaymentDataAccess paymentDataAccess;
//    private DeliveryDataAccess deliveryDataAccess;
//    private AdminDataAccess adminDataAccess;
//
//    private static final int ITEMS_PER_PAGE = 10;
//    private int totalItems;
//
//    // Track current view
//    private enum CurrentView {
//        RESTAURANTS, CUSTOMERS, PRODUCTS, CATEGORIES, ORDERS, PAYMENTS, DELIVERIES, ADMINS
//    }
//    private CurrentView currentView;
//
//    @FXML
//    public void initialize() {
//        // Initialize data access objects
//        try {
//            restaurantDataAccess = new RestaurantDataAccess();
//            customerDataAccess = new CustomerDataAccess();
//            productDataAccess = new ProductDataAccess();
//            categoryDataAccess = new CategoryDataAccess();
//            orderDataAccess = new OrderDataAccess();
//            paymentDataAccess = new PaymentDataAccess();
//            deliveryDataAccess = new DeliveryDataAccess();
//            adminDataAccess = new AdminDataAccess();
//        } catch (Exception e) {
//            showAlert(Alert.AlertType.ERROR, "Initialization Error",
//                      "Failed to initialize data access layer", e.getMessage());
//        }
//
//        // Setup table columns
//        setupRestaurantTable();
//        setupCustomerTable();
//        setupProductTable();
//        setupCategoryTable();
//        setupOrderTable();
//        setupPaymentTable();
//        setupDeliveryTable();
//        setupAdminTable();
//
//        // Show restaurants by default
//        showRestaurants();
//    }
//
//    // Navigation methods
//    @FXML
//    public void showRestaurants() {
//        hideAllTables();
//        restaurantsTable.setVisible(true);
//        sectionTitleLabel.setText("Restaurants");
//        currentView = CurrentView.RESTAURANTS;
//        loadRestaurants();
//    }
//
//    @FXML
//    public void showCustomers() {
//        hideAllTables();
//        customersTable.setVisible(true);
//        sectionTitleLabel.setText("Customers");
//        currentView = CurrentView.CUSTOMERS;
//        loadCustomers();
//    }
//
//    @FXML
//    public void showProducts() {
//        hideAllTables();
//        productsTable.setVisible(true);
//        sectionTitleLabel.setText("Products");
//        currentView = CurrentView.PRODUCTS;
//        loadProducts();
//    }
//
//    @FXML
//    public void showCategories() {
//        hideAllTables();
//        categoriesTable.setVisible(true);
//        sectionTitleLabel.setText("Categories");
//        currentView = CurrentView.CATEGORIES;
//        loadCategories();
//    }
//
//    @FXML
//    public void showOrders() {
//        hideAllTables();
//        ordersTable.setVisible(true);
//        sectionTitleLabel.setText("Orders");
//        currentView = CurrentView.ORDERS;
//        loadOrders();
//    }
//
//    @FXML
//    public void showPayments() {
//        hideAllTables();
//        paymentsTable.setVisible(true);
//        sectionTitleLabel.setText("Payments");
//        currentView = CurrentView.PAYMENTS;
//        loadPayments();
//    }
//
//    @FXML
//    public void showDeliveries() {
//        hideAllTables();
//        deliveriesTable.setVisible(true);
//        sectionTitleLabel.setText("Deliveries");
//        currentView = CurrentView.DELIVERIES;
//        loadDeliveries();
//    }
//
//    @FXML
//    public void showAdmins() {
//        hideAllTables();
//        adminsTable.setVisible(true);
//        sectionTitleLabel.setText("Admins");
//        currentView = CurrentView.ADMINS;
//        loadAdmins();
//    }
//
//    private void hideAllTables() {
//        restaurantsTable.setVisible(false);
//        customersTable.setVisible(false);
//        productsTable.setVisible(false);
//        categoriesTable.setVisible(false);
//        ordersTable.setVisible(false);
//        paymentsTable.setVisible(false);
//        deliveriesTable.setVisible(false);
//        adminsTable.setVisible(false);
//    }
//
//    // Handle Add button clicks
//    @FXML
//    public void handleAdd() {
//        switch (currentView) {
//            case RESTAURANTS:
//                showRestaurantDialog(null);
//                break;
//            case CUSTOMERS:
//                showCustomerDialog(null);
//                break;
//            case PRODUCTS:
//                showAddProductDialog();
//                break;
//            case CATEGORIES:
//                showAddCategoryDialog();
//                break;
//            case ORDERS:
//                showOrderDialog(null);
//                break;
//            case PAYMENTS:
//                showPaymentDialog(null);
//                break;
//            case DELIVERIES:
//                showDeliveryDialog(null);
//                break;
//            case ADMINS:
//                showAdminDialog(null);
//                break;
//        }
//    }
//
//    // Handle Refresh button clicks
//    @FXML
//    public void handleRefresh() {
//        switch (currentView) {
//            case RESTAURANTS:
//                loadRestaurants();
//                break;
//            case CUSTOMERS:
//                loadCustomers();
//                break;
//            case PRODUCTS:
//                loadProducts();
//                break;
//            case CATEGORIES:
//                loadCategories();
//                break;
//            case ORDERS:
//                loadOrders();
//                break;
//            case PAYMENTS:
//                loadPayments();
//                break;
//            case DELIVERIES:
//                loadDeliveries();
//                break;
//            case ADMINS:
//                loadAdmins();
//                break;
//        }
//    }
//
//    // Handle Logout button click
//    @FXML
//    public void handleLogout() {
//        try {
//            // Close all data access connections
//            closeConnections();
//
//            // Load login view
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/foodorderingsystem/login-view.fxml"));
//            Scene scene = new Scene(loader.load());
//
//            // Get current stage
//            Stage stage = (Stage) logoutButton.getScene().getWindow();
//            stage.setScene(scene);
//            stage.show();
//        } catch (IOException e) {
//            showAlert(Alert.AlertType.ERROR, "Navigation Error",
//                     "Failed to load login view", e.getMessage());
//        }
//    }
//
//    // Setup table methods
//    private void setupRestaurantTable() {
//        restaurantIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        restaurantNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        restaurantAddressColumn.setCellValueFactory(cellData -> {
//            Location location = cellData.getValue().getLocation();
//            return new SimpleStringProperty(location != null ? location.getAddress() : "N/A");
//        });
//        restaurantContactColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
//        addRestaurantActionButtons();
//    }
//
//    private void setupCustomerTable() {
//        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        customerNameColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
//        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
//        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
//        addCustomerActionButtons();
//    }
//
//    private void setupProductTable() {
//        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
//        productCategoryColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getCategory() != null ?
//                                     cellData.getValue().getCategory().getName() : "N/A"));
//        productRestaurantColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getRestaurant() != null ?
//                                     cellData.getValue().getRestaurant().getName() : "N/A"));
//        productStockColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
//        addProductActionButtons();
//    }
//
//    private void setupCategoryTable() {
//        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        categoryDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
//        addCategoryActionButtons();
//    }
//
//    private void setupOrderTable() {
//        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        orderDateColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getOrderDate() != null ?
//                                    cellData.getValue().getOrderDate().toString() : "N/A"));
//        orderCustomerColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getCustomer() != null ?
//                                    cellData.getValue().getCustomer().getFirstName() + " " +
//                                    cellData.getValue().getCustomer().getLastName() : "N/A"));
//        orderRestaurantColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getRestaurant() != null ?
//                                    cellData.getValue().getRestaurant().getName() : "N/A"));
//        orderStatusColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getStatus()));
//        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
//        addOrderActionButtons();
//    }
//
//    private void setupPaymentTable() {
//        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        paymentOrderColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getOrder() != null ?
//                                    String.valueOf(cellData.getValue().getOrder().getId()) : "N/A"));
//        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
//        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
//        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
//        addPaymentActionButtons();
//    }
//
//    private void setupDeliveryTable() {
//        deliveryIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        deliveryNameColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getDeliveryPerson()));
//        deliveryOrderColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getOrder() != null ?
//                                    String.valueOf(cellData.getValue().getOrder().getId()) : "N/A"));
//        deliveryStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
//        deliveryLocationColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getLocation() != null ?
//                                    cellData.getValue().getLocation().getAddress() : "N/A"));
//        addDeliveryActionButtons();
//    }
//
//    private void setupAdminTable() {
//        adminIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        adminNameColumn.setCellValueFactory(cellData ->
//            new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
//        adminEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
//        adminPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
//        addAdminActionButtons();
//    }
//
//    // Action button methods
//    private void addRestaurantActionButtons() {
//        Callback<TableColumn<Restaurant, Void>, TableCell<Restaurant, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Restaurant, Void> call(final TableColumn<Restaurant, Void> param) {
//                return new TableCell<>() {
//                    private final Button editButton = new Button("Edit");
//                    private final Button deleteButton = new Button("Delete");
//                    private final HBox pane = new HBox(5, editButton, deleteButton);
//
//                    {
//                        editButton.setOnAction(event -> {
//                            Restaurant restaurant = getTableRow().getItem();
//                            if (restaurant != null) {
//                                showRestaurantDialog(restaurant);
//                            }
//                        });
//
//                        deleteButton.setOnAction(event -> {
//                            Restaurant restaurant = getTableRow().getItem();
//                            if (restaurant != null) {
//                                deleteRestaurant(restaurant);
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : pane);
//                    }
//                };
//            }
//        };
//        restaurantActionsColumn.setCellFactory(cellFactory);
//    }
//
//    private void addCustomerActionButtons() {
//        Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {
//                return new TableCell<>() {
//                    private final Button editButton = new Button("Edit");
//                    private final Button deleteButton = new Button("Delete");
//                    private final HBox pane = new HBox(5, editButton, deleteButton);
//
//                    {
//                        editButton.setOnAction(event -> {
//                            Customer customer = getTableRow().getItem();
//                            if (customer != null) {
//                                showCustomerDialog(customer);
//                            }
//                        });
//
//                        deleteButton.setOnAction(event -> {
//                            Customer customer = getTableRow().getItem();
//                            if (customer != null) {
//                                deleteCustomer(customer);
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : pane);
//                    }
//                };
//            }
//        };
//        customerActionsColumn.setCellFactory(cellFactory);
//    }
//
//    private void addProductActionButtons() {
//        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
//                return new TableCell<>() {
//                    private final Button editButton = new Button("Edit");
//                    private final Button deleteButton = new Button("Delete");
//                    private final HBox pane = new HBox(5, editButton, deleteButton);
//
//                    {
//                        editButton.setOnAction(event -> {
//                            Product product = getTableRow().getItem();
//                            if (product != null) {
//                                editProduct(product);
//                            }
//                        });
//
//                        deleteButton.setOnAction(event -> {
//                            Product product = getTableRow().getItem();
//                            if (product != null) {
//                                deleteProduct(product);
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : pane);
//                    }
//                };
//            }
//        };
//        productActionsColumn.setCellFactory(cellFactory);
//    }
//
//    private void addCategoryActionButtons() {
//        Callback<TableColumn<Category, Void>, TableCell<Category, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Category, Void> call(final TableColumn<Category, Void> param) {
//                return new TableCell<>() {
//                    private final Button editButton = new Button("Edit");
//                    private final Button deleteButton = new Button("Delete");
//                    private final HBox pane = new HBox(5, editButton, deleteButton);
//
//                    {
//                        editButton.setOnAction(event -> {
//                            Category category = getTableRow().getItem();
//                            if (category != null) {
//                                editCategory(category);
//                            }
//                        });
//
//                        deleteButton.setOnAction(event -> {
//                            Category category = getTableRow().getItem();
//                            if (category != null) {
//                                deleteCategory(category);
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : pane);
//                    }
//                };
//            }
//        };
//        categoryActionsColumn.setCellFactory(cellFactory);
//    }
//
//    private void addOrderActionButtons() {
//        Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
//                return new TableCell<>() {
//                    private final Button viewButton = new Button("View");
//                    private final Button editButton = new Button("Edit");
//                    private final HBox pane = new HBox(5, viewButton, editButton);
//
//                    {
//                        viewButton.setOnAction(event -> {
//                            Order order = getTableRow().getItem();
//                            if (order != null) {
//                                showOrderDetails(order);
//                            }
//                        });
//
//                        editButton.setOnAction(event -> {
//                            Order order = getTableRow().getItem();
//                            if (order != null) {
//                                showOrderDialog(order);
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : pane);
//                    }
//                };
//            }
//        };
//        orderActionsColumn.setCellFactory(cellFactory);
//    }
//
//    private void addPaymentActionButtons() {
//        Callback<TableColumn<Payment, Void>, TableCell<Payment, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Payment, Void> call(final TableColumn<Payment, Void> param) {
//                return new TableCell<>() {
//                    private final Button editButton = new Button("Edit");
//                    private final Button deleteButton = new Button("Delete");
//                    private final HBox pane = new HBox(5, editButton, deleteButton);
//
//                    {
//                        editButton.setOnAction(event -> {
//                            Payment payment = getTableRow().getItem();
//                            if (payment != null) {
//                                showPaymentDialog(payment);
//                            }
//                        });
//
//                        deleteButton.setOnAction(event -> {
//                            Payment payment = getTableRow().getItem();
//                            if (payment != null) {
//                                deletePayment(payment);
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : pane);
//                    }
//                };
//            }
//        };
//        paymentActionsColumn.setCellFactory(cellFactory);
//    }
//
//    private void addDeliveryActionButtons() {
//        Callback<TableColumn<Delivery, Void>, TableCell<Delivery, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Delivery, Void> call(final TableColumn<Delivery, Void> param) {
//                return new TableCell<>() {
//                    private final Button editButton = new Button("Edit");
//                    private final Button deleteButton = new Button("Delete");
//                    private final HBox pane = new HBox(5, editButton, deleteButton);
//
//                    {
//                        editButton.setOnAction(event -> {
//                            Delivery delivery = getTableRow().getItem();
//                            if (delivery != null) {
//                                showDeliveryDialog(delivery);
//                            }
//                        });
//
//                        deleteButton.setOnAction(event -> {
//                            Delivery delivery = getTableRow().getItem();
//                            if (delivery != null) {
//                                deleteDelivery(delivery);
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : pane);
//                    }
//                };
//            }
//        };
//        deliveryActionsColumn.setCellFactory(cellFactory);
//    }
//
//    private void addAdminActionButtons() {
//        Callback<TableColumn<Admin, Void>, TableCell<Admin, Void>> cellFactory = new Callback<>() {
//            @Override
//            public TableCell<Admin, Void> call(final TableColumn<Admin, Void> param) {
//                return new TableCell<>() {
//                    private final Button editButton = new Button("Edit");
//                    private final Button deleteButton = new Button("Delete");
//                    private final HBox pane = new HBox(5, editButton, deleteButton);
//
//                    {
//                        editButton.setOnAction(event -> {
//                            Admin admin = getTableRow().getItem();
//                            if (admin != null) {
//                                showAdminDialog(admin);
//                            }
//                        });
//
//                        deleteButton.setOnAction(event -> {
//                            Admin admin = getTableRow().getItem();
//                            if (admin != null) {
//                                deleteAdmin(admin);
//                            }
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(empty ? null : pane);
//                    }
//                };
//            }
//        };
//        adminActionsColumn.setCellFactory(cellFactory);
//    }
//
//    // Load data methods
//    private void loadRestaurants() {
//        try {
//            List<Restaurant> restaurants = restaurantDataAccess.getAllRestaurants();
//            restaurantsTable.setItems(FXCollections.observableArrayList(restaurants));
//            statusLabel.setText("Loaded " + restaurants.size() + " restaurants");
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Data Error",
//                     "Failed to load restaurants", e.getMessage());
//            statusLabel.setText("Error loading restaurants");
//        }
//    }
//
//    private void loadCustomers() {
//        try {
//            List<Customer> customers = customerDataAccess.getAllCustomers();
//            customersTable.setItems(FXCollections.observableArrayList(customers));
//            statusLabel.setText("Loaded " + customers.size() + " customers");
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Data Error",
//                     "Failed to load customers", e.getMessage());
//            statusLabel.setText("Error loading customers");
//        }
//    }
//
//    private void loadProducts() {
//        try {
//            List<Product> products = productDataAccess.getAllProducts();
//            productsTable.setItems(FXCollections.observableArrayList(products));
//            statusLabel.setText("Loaded " + products.size() + " products");
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Data Error",
//                     "Failed to load products", e.getMessage());
//            statusLabel.setText("Error loading products");
//        }
//    }
//
//    private void loadCategories() {
//        try {
//            List<Category> categories = categoryDataAccess.getAllCategories();
//            categoriesTable.setItems(FXCollections.observableArrayList(categories));
//            statusLabel.setText("Loaded " + categories.size() + " categories");
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Data Error",
//                     "Failed to load categories", e.getMessage());
//            statusLabel.setText("Error loading categories");
//        }
//    }
//
//    private void loadOrders() {
//        try {
//            // Get all orders
//            List<Order> allOrders = orderDataAccess.getAllOrders();
//            totalItems = allOrders.size();
//
//            // Update pagination
//            int pageCount = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
//            pagination.setPageCount(Math.max(1, pageCount));
//
//            // Calculate paging
//            int currentPage = pagination.getCurrentPageIndex();
//            int startIndex = currentPage * ITEMS_PER_PAGE;
//            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
//
//            // Get subset for current page
//            List<Order> pageOrders;
//            if (startIndex < totalItems) {
//                pageOrders = allOrders.subList(startIndex, endIndex);
//            } else {
//                pageOrders = allOrders.isEmpty() ? allOrders : allOrders.subList(0, 1);
//            }
//
//            // Update table
//            ordersTable.setItems(FXCollections.observableArrayList(pageOrders));
//
//            // Update item count label
//            lblItemCount.setText("Total Items: " + totalItems);
//
//            // Update status
//            statusLabel.setText("Loaded " + pageOrders.size() + " orders");
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Data Error",
//                     "Failed to load orders", e.getMessage());
//            statusLabel.setText("Error loading orders");
//        }
//    }
//
//    private void loadPayments() {
//        try {
//            // Get all payments
//            List<Payment> allPayments = paymentDataAccess.getAllPayments();
//            totalItems = allPayments.size();
//
//            // Update pagination
//            int pageCount = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
//            pagination.setPageCount(Math.max(1, pageCount));
//
//            // Calculate paging
//            int currentPage = pagination.getCurrentPageIndex();
//            int startIndex = currentPage * ITEMS_PER_PAGE;
//            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
//
//            // Get subset for current page
//            List<Payment> pagePayments;
//            if (startIndex < totalItems) {
//                pagePayments = allPayments.subList(startIndex, endIndex);
//            } else {
//                pagePayments = allPayments.isEmpty() ? allPayments : allPayments.subList(0, 1);
//            }
//
//            // Update table
//            paymentsTable.setItems(FXCollections.observableArrayList(pagePayments));
//
//            // Update item count label
//            lblItemCount.setText("Total Items: " + totalItems);
//
//            // Update status
//            statusLabel.setText("Loaded " + pagePayments.size() + " payments");
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Data Error",
//                     "Failed to load payments", e.getMessage());
//            statusLabel.setText("Error loading payments");
//        }
//    }
//
//    private void loadDeliveries() {
//        try {
//            // Get all deliveries
//            List<Delivery> allDeliveries = deliveryDataAccess.getAllDeliveries();
//            totalItems = allDeliveries.size();
//
//            // Update pagination
//            int pageCount = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
//            pagination.setPageCount(Math.max(1, pageCount));
//
//            // Calculate paging
//            int currentPage = pagination.getCurrentPageIndex();
//            int startIndex = currentPage * ITEMS_PER_PAGE;
//            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
//
//            // Get subset for current page
//            List<Delivery> pageDeliveries;
//            if (startIndex < totalItems) {
//                pageDeliveries = allDeliveries.subList(startIndex, endIndex);
//            } else {
//                pageDeliveries = allDeliveries.isEmpty() ? allDeliveries : allDeliveries.subList(0, 1);
//            }
//
//            // Update table
//            deliveriesTable.setItems(FXCollections.observableArrayList(pageDeliveries));
//
//            // Update item count label
//            lblItemCount.setText("Total Items: " + totalItems);
//
//            // Update status
//            statusLabel.setText("Loaded " + pageDeliveries.size() + " deliveries");
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Data Error",
//                     "Failed to load deliveries", e.getMessage());
//            statusLabel.setText("Error loading deliveries");
//        }
//    }
//
//    private void loadAdmins() {
//        try {
//            // Get all admins
//            List<Admin> allAdmins = adminDataAccess.getAllAdmins();
//            totalItems = allAdmins.size();
//
//            // Update pagination
//            int pageCount = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
//            pagination.setPageCount(Math.max(1, pageCount));
//
//            // Calculate paging
//            int currentPage = pagination.getCurrentPageIndex();
//            int startIndex = currentPage * ITEMS_PER_PAGE;
//            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
//
//            // Get subset for current page
//            List<Admin> pageAdmins;
//            if (startIndex < totalItems) {
//                pageAdmins = allAdmins.subList(startIndex, endIndex);
//            } else {
//                pageAdmins = allAdmins.isEmpty() ? allAdmins : allAdmins.subList(0, 1);
//            }
//
//            // Update table
//            adminsTable.setItems(FXCollections.observableArrayList(pageAdmins));
//
//            // Update item count label
//            lblItemCount.setText("Total Items: " + totalItems);
//
//            // Update status
//            statusLabel.setText("Loaded " + pageAdmins.size() + " admin users");
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Data Error",
//                     "Failed to load admins", e.getMessage());
//            statusLabel.setText("Error loading admins");
//        }
//    }
//
//    // Dialog methods
//    private void showRestaurantDialog(Restaurant restaurant) {
//        // Create dialog
//        Dialog<Restaurant> dialog = new Dialog<>();
//        dialog.setTitle(restaurant == null ? "Add Restaurant" : "Edit Restaurant");
//        dialog.setHeaderText(restaurant == null ? "Create a new restaurant" : "Edit restaurant information");
//
//        // Set buttons
//        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
//
//        // Create form layout
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 150, 10, 10));
//
//        TextField nameField = new TextField();
//        nameField.setPromptText("Restaurant name");
//        TextField phoneField = new TextField();
//        phoneField.setPromptText("Phone number");
//        TextField addressField = new TextField();
//        addressField.setPromptText("Address");
//        TextField cityField = new TextField();
//        cityField.setPromptText("City");
//        TextField stateField = new TextField();
//        stateField.setPromptText("State");
//        TextField zipField = new TextField();
//        zipField.setPromptText("Zip code");
//
//        // Pre-fill fields if editing
//        if (restaurant != null) {
//            nameField.setText(restaurant.getName());
//            phoneField.setText(restaurant.getPhone());
//            if (restaurant.getLocation() != null) {
//                addressField.setText(restaurant.getLocation().getAddress());
//                cityField.setText(restaurant.getLocation().getCity());
//                stateField.setText(restaurant.getLocation().getState());
//                zipField.setText(restaurant.getLocation().getZipCode());
//            }
//        }
//
//        // Add form elements to grid
//        grid.add(new Label("Name:"), 0, 0);
//        grid.add(nameField, 1, 0);
//        grid.add(new Label("Phone:"), 0, 1);
//        grid.add(phoneField, 1, 1);
//        grid.add(new Label("Address:"), 0, 2);
//        grid.add(addressField, 1, 2);
//        grid.add(new Label("City:"), 0, 3);
//        grid.add(cityField, 1, 3);
//        grid.add(new Label("State:"), 0, 4);
//        grid.add(stateField, 1, 4);
//        grid.add(new Label("ZIP:"), 0, 5);
//        grid.add(zipField, 1, 5);
//
//        dialog.getDialogPane().setContent(grid);
//
//        // Focus first field
//        nameField.requestFocus();
//
//        // Convert result to restaurant
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == saveButtonType) {
//                Restaurant result = restaurant == null ? new Restaurant() : restaurant;
//                result.setName(nameField.getText());
//                result.setPhone(phoneField.getText());
//
//                Location location = result.getLocation();
//                if (location == null) {
//                    location = new Location();
//                    result.setLocation(location);
//                }
//
//                location.setAddress(addressField.getText());
//                location.setCity(cityField.getText());
//                location.setState(stateField.getText());
//                location.setZipCode(zipField.getText());
//
//                return result;
//            }
//            return null;
//        });
//
//        // Show dialog and process result
//        Optional<Restaurant> result = dialog.showAndWait();
//        result.ifPresent(r -> {
//            try {
//                if (r.getId() == 0) {
//                    restaurantDataAccess.insertRestaurant(r);
//                    statusLabel.setText("Restaurant added successfully");
//                } else {
//                    restaurantDataAccess.updateRestaurant(r);
//                    statusLabel.setText("Restaurant updated successfully");
//                }
//                loadRestaurants();
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Data Error",
//                         "Failed to save restaurant", e.getMessage());
//                statusLabel.setText("Error saving restaurant");
//            }
//        });
//    }
//
//    private void showCustomerDialog(Customer customer) {
//        // Similar implementation to showRestaurantDialog
//        // Create dialog, form fields, and handle saving
//        Dialog<Customer> dialog = new Dialog<>();
//        dialog.setTitle(customer == null ? "Add Customer" : "Edit Customer");
//        dialog.setHeaderText(customer == null ? "Create a new customer" : "Edit customer information");
//
//        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
//
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 150, 10, 10));
//
//        TextField firstNameField = new TextField();
//        firstNameField.setPromptText("First name");
//        TextField lastNameField = new TextField();
//        lastNameField.setPromptText("Last name");
//        TextField emailField = new TextField();
//        emailField.setPromptText("Email");
//        TextField phoneField = new TextField();
//        phoneField.setPromptText("Phone");
//        PasswordField passwordField = new PasswordField();
//        passwordField.setPromptText("Password");
//
//        if (customer != null) {
//            firstNameField.setText(customer.getFirstName());
//            lastNameField.setText(customer.getLastName());
//            emailField.setText(customer.getEmail());
//            phoneField.setText(customer.getPhone());
//            // Password field left empty for security
//        }
//
//        grid.add(new Label("First Name:"), 0, 0);
//        grid.add(firstNameField, 1, 0);
//        grid.add(new Label("Last Name:"), 0, 1);
//        grid.add(lastNameField, 1, 1);
//        grid.add(new Label("Email:"), 0, 2);
//        grid.add(emailField, 1, 2);
//        grid.add(new Label("Phone:"), 0, 3);
//        grid.add(phoneField, 1, 3);
//        grid.add(new Label("Password:"), 0, 4);
//        grid.add(passwordField, 1, 4);
//
//        dialog.getDialogPane().setContent(grid);
//        firstNameField.requestFocus();
//
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == saveButtonType) {
//                Customer result = customer == null ? new Customer() : customer;
//                result.setFirstName(firstNameField.getText());
//                result.setLastName(lastNameField.getText());
//                result.setEmail(emailField.getText());
//                result.setPhone(phoneField.getText());
//
//                // Only set password if provided (not empty)
//                if (!passwordField.getText().isEmpty()) {
//                    result.setPassword(passwordField.getText());
//                }
//
//                return result;
//            }
//            return null;
//        });
//
//        Optional<Customer> result = dialog.showAndWait();
//        result.ifPresent(c -> {
//            try {
//                if (c.getId() == 0) {
//                    customerDataAccess.insertCustomer(c);
//                    statusLabel.setText("Customer added successfully");
//                } else {
//                    customerDataAccess.updateCustomer(c);
//                    statusLabel.setText("Customer updated successfully");
//                }
//                loadCustomers();
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Data Error",
//                         "Failed to save customer", e.getMessage());
//                statusLabel.setText("Error saving customer");
//            }
//        });
//    }
//
//    private void showAddProductDialog() {
//        Dialog<Product> dialog = new Dialog<>();
//        dialog.setTitle("Add New Product");
//        dialog.setHeaderText("Enter product details");
//
//        // Set the button types
//        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
//
//        // Create form fields
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 150, 10, 10));
//
//        TextField nameField = new TextField();
//        nameField.setPromptText("Product Name");
//
//        TextField priceField = new TextField();
//        priceField.setPromptText("Price");
//
//        TextField descriptionField = new TextField();
//        descriptionField.setPromptText("Description");
//
//        ComboBox<Restaurant> restaurantComboBox = new ComboBox<>();
//        ComboBox<Category> categoryComboBox = new ComboBox<>();
//
//        try {
//            // Load restaurants for dropdown
//            List<Restaurant> restaurants = restaurantDataAccess.getAllRestaurants();
//            restaurantComboBox.setItems(FXCollections.observableArrayList(restaurants));
//            restaurantComboBox.setConverter(new StringConverter<Restaurant>() {
//                @Override
//                public String toString(Restaurant restaurant) {
//                    return restaurant != null ? restaurant.getName() : "";
//                }
//
//                @Override
//                public Restaurant fromString(String string) {
//                    return null;
//                }
//            });
//
//            // Load categories for dropdown
//            List<Category> categories = categoryDataAccess.getAllCategories();
//            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
//            categoryComboBox.setConverter(new StringConverter<Category>() {
//                @Override
//                public String toString(Category category) {
//                    return category != null ? category.getCategoryName() : "";
//                }
//
//                @Override
//                public Category fromString(String string) {
//                    return null;
//                }
//            });
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading data for dropdowns: " + e.getMessage());
//        }
//
//        grid.add(new Label("Name:"), 0, 0);
//        grid.add(nameField, 1, 0);
//        grid.add(new Label("Price:"), 0, 1);
//        grid.add(priceField, 1, 1);
//        grid.add(new Label("Description:"), 0, 2);
//        grid.add(descriptionField, 1, 2);
//        grid.add(new Label("Restaurant:"), 0, 3);
//        grid.add(restaurantComboBox, 1, 3);
//        grid.add(new Label("Category:"), 0, 4);
//        grid.add(categoryComboBox, 1, 4);
//
//        dialog.getDialogPane().setContent(grid);
//
//        // Request focus on the name field
//        nameField.requestFocus();
//
//        // Convert the result to a product when the save button is clicked
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == saveButtonType) {
//                if (nameField.getText().isEmpty()) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Product name cannot be empty", null);
//                    return null;
//                }
//
//                if (restaurantComboBox.getValue() == null) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a restaurant", null);
//                    return null;
//                }
//
//                if (categoryComboBox.getValue() == null) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a category", null);
//                    return null;
//                }
//
//                try {
//                    BigDecimal price = new BigDecimal(priceField.getText());
//
//                    // Create product
//                    Product product = new Product();
//                    product.setName(nameField.getText());
//                    product.setUnitPrice(price);
//                    product.setDescription(descriptionField.getText());
//                    product.setRestaurantId(restaurantComboBox.getValue().getRestaurantId());
//                    product.setCategoryId(categoryComboBox.getValue().getCategoryId());
//
//                    return product;
//                } catch (NumberFormatException e) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Price must be a valid number", null);
//                    return null;
//                }
//            }
//            return null;
//        });
//
//        Optional<Product> result = dialog.showAndWait();
//
//        result.ifPresent(product -> {
//            try {
//                // Save to database
//                productDataAccess.insertProduct(product);
//
//                // Refresh data
//                loadProducts();
//
//                // Show success message
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully", null);
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding product: " + e.getMessage(), null);
//            }
//        });
//    }
//
//    private void editProduct(Product product) {
//        Dialog<Product> dialog = new Dialog<>();
//        dialog.setTitle("Edit Product");
//        dialog.setHeaderText("Edit product details");
//
//        // Set the button types
//        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
//
//        // Create form fields
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 150, 10, 10));
//
//        TextField nameField = new TextField(product.getName());
//        TextField priceField = new TextField(product.getUnitPrice().toString());
//        TextField descriptionField = new TextField(product.getDescription());
//
//        ComboBox<Restaurant> restaurantComboBox = new ComboBox<>();
//        ComboBox<Category> categoryComboBox = new ComboBox<>();
//
//        try {
//            // Load restaurants for dropdown
//            List<Restaurant> restaurants = restaurantDataAccess.getAllRestaurants();
//            restaurantComboBox.setItems(FXCollections.observableArrayList(restaurants));
//            restaurantComboBox.setConverter(new StringConverter<Restaurant>() {
//                @Override
//                public String toString(Restaurant restaurant) {
//                    return restaurant != null ? restaurant.getName() : "";
//                }
//
//                @Override
//                public Restaurant fromString(String string) {
//                    return null;
//                }
//            });
//
//            // Set selected restaurant
//            for (Restaurant r : restaurants) {
//                if (r.getRestaurantId() == product.getRestaurantId()) {
//                    restaurantComboBox.setValue(r);
//                    break;
//                }
//            }
//
//            // Load categories for dropdown
//            List<Category> categories = categoryDataAccess.getAllCategories();
//            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
//            categoryComboBox.setConverter(new StringConverter<Category>() {
//                @Override
//                public String toString(Category category) {
//                    return category != null ? category.getCategoryName() : "";
//                }
//
//                @Override
//                public Category fromString(String string) {
//                    return null;
//                }
//            });
//
//            // Set selected category
//            for (Category c : categories) {
//                if (c.getCategoryId() == product.getCategoryId()) {
//                    categoryComboBox.setValue(c);
//                    break;
//                }
//            }
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading data for dropdowns: " + e.getMessage(), null);
//        }
//
//        grid.add(new Label("Name:"), 0, 0);
//        grid.add(nameField, 1, 0);
//        grid.add(new Label("Price:"), 0, 1);
//        grid.add(priceField, 1, 1);
//        grid.add(new Label("Description:"), 0, 2);
//        grid.add(descriptionField, 1, 2);
//        grid.add(new Label("Restaurant:"), 0, 3);
//        grid.add(restaurantComboBox, 1, 3);
//        grid.add(new Label("Category:"), 0, 4);
//        grid.add(categoryComboBox, 1, 4);
//
//        dialog.getDialogPane().setContent(grid);
//
//        // Convert the result to a product when the save button is clicked
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == saveButtonType) {
//                if (nameField.getText().isEmpty()) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Product name cannot be empty", null);
//                    return null;
//                }
//
//                if (restaurantComboBox.getValue() == null) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a restaurant", null);
//                    return null;
//                }
//
//                if (categoryComboBox.getValue() == null) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a category", null);
//                    return null;
//                }
//
//                try {
//                    BigDecimal price = new BigDecimal(priceField.getText());
//
//                    // Update product
//                    product.setName(nameField.getText());
//                    product.setUnitPrice(price);
//                    product.setDescription(descriptionField.getText());
//                    product.setRestaurantId(restaurantComboBox.getValue().getRestaurantId());
//                    product.setCategoryId(categoryComboBox.getValue().getCategoryId());
//
//                    return product;
//                } catch (NumberFormatException e) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Price must be a valid number", null);
//                    return null;
//                }
//            }
//            return null;
//        });
//
//        Optional<Product> result = dialog.showAndWait();
//
//        result.ifPresent(updatedProduct -> {
//            try {
//                // Update in database
//                productDataAccess.updateProduct(updatedProduct);
//
//                // Refresh data
//                loadProducts();
//
//                // Show success message
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully", null);
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating product: " + e.getMessage(), null);
//            }
//        });
//    }
//
//    private void showAddCategoryDialog() {
//        Dialog<Category> dialog = new Dialog<>();
//        dialog.setTitle("Add New Category");
//        dialog.setHeaderText("Enter category details");
//
//        // Set the button types
//        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
//
//        // Create form fields
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 150, 10, 10));
//
//        TextField nameField = new TextField();
//        nameField.setPromptText("Category Name");
//
//        TextArea descriptionArea = new TextArea();
//        descriptionArea.setPromptText("Category Description");
//        descriptionArea.setPrefRowCount(4);
//        descriptionArea.setPrefColumnCount(30);
//        descriptionArea.setWrapText(true);
//
//        grid.add(new Label("Name:"), 0, 0);
//        grid.add(nameField, 1, 0);
//        grid.add(new Label("Description:"), 0, 1);
//        grid.add(descriptionArea, 1, 1);
//
//        dialog.getDialogPane().setContent(grid);
//
//        // Request focus on the name field
//        nameField.requestFocus();
//
//        // Convert the result to a category when the save button is clicked
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == saveButtonType) {
//                if (nameField.getText().isEmpty()) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty", null);
//                    return null;
//                }
//
//                // Create category
//                Category category = new Category();
//                category.setCategoryName(nameField.getText());
//                category.setDescription(descriptionArea.getText());
//
//                return category;
//            }
//            return null;
//        });
//
//        Optional<Category> result = dialog.showAndWait();
//
//        result.ifPresent(category -> {
//            try {
//                // Save to database
//                categoryDataAccess.insertCategory(category);
//
//                // Refresh data
//                loadCategories();
//
//                // Show success message
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully", null);
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding category: " + e.getMessage(), null);
//            }
//        });
//    }
//
//    private void editCategory(Category category) {
//        Dialog<Category> dialog = new Dialog<>();
//        dialog.setTitle("Edit Category");
//        dialog.setHeaderText("Edit category details");
//
//        // Set the button types
//        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
//
//        // Create form fields
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 150, 10, 10));
//
//        TextField nameField = new TextField(category.getCategoryName());
//
//        TextArea descriptionArea = new TextArea(category.getDescription());
//        descriptionArea.setPrefRowCount(4);
//        descriptionArea.setPrefColumnCount(30);
//        descriptionArea.setWrapText(true);
//
//        grid.add(new Label("Name:"), 0, 0);
//        grid.add(nameField, 1, 0);
//        grid.add(new Label("Description:"), 0, 1);
//        grid.add(descriptionArea, 1, 1);
//
//        dialog.getDialogPane().setContent(grid);
//
//        // Convert the result to a category when the save button is clicked
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == saveButtonType) {
//                if (nameField.getText().isEmpty()) {
//                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty", null);
//                    return null;
//                }
//
//                // Update category
//                category.setCategoryName(nameField.getText());
//                category.setDescription(descriptionArea.getText());
//
//                return category;
//            }
//            return null;
//        });
//
//        Optional<Category> result = dialog.showAndWait();
//
//        result.ifPresent(updatedCategory -> {
//            try {
//                // Update in database
//                categoryDataAccess.updateCategory(updatedCategory);
//
//                // Refresh data
//                loadCategories();
//
//                // Show success message
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Category updated successfully", null);
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating category: " + e.getMessage(), null);
//            }
//        });
//    }
//
//    private void deleteProduct(Product product) {
//        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmDelete.setTitle("Confirm Delete");
//        confirmDelete.setHeaderText("Delete Product");
//        confirmDelete.setContentText("Are you sure you want to delete the product: " + product.getName() + "?");
//
//        Optional<ButtonType> result = confirmDelete.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            try {
//                // Delete from database
//                productDataAccess.deleteProduct(product.getProductId());
//
//                // Refresh data
//                loadProducts();
//
//                // Show success message
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully", null);
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting product: " + e.getMessage(), null);
//            }
//        }
//    }
//
//    private void deleteCategory(Category category) {
//        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmDelete.setTitle("Confirm Delete");
//        confirmDelete.setHeaderText("Delete Category");
//        confirmDelete.setContentText("Are you sure you want to delete the category: " + category.getCategoryName() + "?\n\nThis may affect products that belong to this category.");
//
//        Optional<ButtonType> result = confirmDelete.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            try {
//                // Delete from database
//                categoryDataAccess.deleteCategory(category.getCategoryId());
//
//                // Refresh data
//                loadCategories();
//
//                // Show success message
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted successfully", null);
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting category: " + e.getMessage(), null);
//            }
//        }
//    }
//
//    private void deleteRestaurant(Restaurant restaurant) {
//        if (confirmDelete("restaurant", restaurant.getName())) {
//            try {
//                restaurantDataAccess.deleteRestaurant(restaurant.getId());
//                loadRestaurants();
//                statusLabel.setText("Restaurant deleted successfully");
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Data Error",
//                         "Failed to delete restaurant", e.getMessage());
//                statusLabel.setText("Error deleting restaurant");
//            }
//        }
//    }
//
//    private void deleteCustomer(Customer customer) {
//        if (confirmDelete("customer", customer.getFirstName() + " " + customer.getLastName())) {
//            try {
//                customerDataAccess.deleteCustomer(customer.getId());
//                loadCustomers();
//                statusLabel.setText("Customer deleted successfully");
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Data Error",
//                         "Failed to delete customer", e.getMessage());
//                statusLabel.setText("Error deleting customer");
//            }
//        }
//    }
//
//    private void deletePayment(Payment payment) {
//        if (confirmDelete("payment", "Payment #" + payment.getId())) {
//            try {
//                paymentDataAccess.deletePayment(payment.getId());
//                loadPayments();
//                statusLabel.setText("Payment deleted successfully");
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Data Error",
//                         "Failed to delete payment", e.getMessage());
//                statusLabel.setText("Error deleting payment");
//            }
//        }
//    }
//
//    private void deleteDelivery(Delivery delivery) {
//        if (confirmDelete("delivery", "Delivery #" + delivery.getId())) {
//            try {
//                deliveryDataAccess.deleteDelivery(delivery.getId());
//                loadDeliveries();
//                statusLabel.setText("Delivery deleted successfully");
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Data Error",
//                         "Failed to delete delivery", e.getMessage());
//                statusLabel.setText("Error deleting delivery");
//            }
//        }
//    }
//
//    private void deleteAdmin(Admin admin) {
//        if (confirmDelete("admin", admin.getFirstName() + " " + admin.getLastName())) {
//            try {
//                adminDataAccess.deleteAdmin(admin.getId());
//                loadAdmins();
//                statusLabel.setText("Admin deleted successfully");
//            } catch (SQLException e) {
//                showAlert(Alert.AlertType.ERROR, "Data Error",
//                         "Failed to delete admin", e.getMessage());
//                statusLabel.setText("Error deleting admin");
//            }
//        }
//    }
//
//    // Utility methods
//    private boolean confirmDelete(String entityType, String entityName) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirm Delete");
//        alert.setHeaderText("Delete " + entityType + ": " + entityName);
//        alert.setContentText("Are you sure you want to delete this " + entityType + "? This action cannot be undone.");
//
//        Optional<ButtonType> result = alert.showAndWait();
//        return result.isPresent() && result.get() == ButtonType.OK;
//    }
//
//    private void showAlert(Alert.AlertType type, String title, String header, String content) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(header);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
//
//    private void closeConnections() {
//        try {
//            if (restaurantDataAccess != null) restaurantDataAccess.closeConnection();
//            if (customerDataAccess != null) customerDataAccess.closeConnection();
//            if (productDataAccess != null) productDataAccess.closeConnection();
//            if (categoryDataAccess != null) categoryDataAccess.closeConnection();
//            if (orderDataAccess != null) orderDataAccess.closeConnection();
//            if (paymentDataAccess != null) paymentDataAccess.closeConnection();
//            if (deliveryDataAccess != null) deliveryDataAccess.closeConnection();
//            if (adminDataAccess != null) adminDataAccess.closeConnection();
//        } catch (Exception e) {
//            System.err.println("Error closing connections: " + e.getMessage());
//        }
//    }
//
//    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
//    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
}
