package com.example.foodorderingsystem.DataAccessLayer;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Delivery;
import com.example.foodorderingsystem.BusinessLayer.Order;
import com.example.foodorderingsystem.BusinessLayer.Payment;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.BusinessLayer.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class OrderDataAccess {

    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;
    private CustomerDataAccess customerDataAccess;
    private RestaurantDataAccess restaurantDataAccess;
    private DeliveryDataAccess deliveryDataAccess;
    private PaymentDataAccess paymentDataAccess;

    public OrderDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database successfully!");
            // Initialize related data access objects
            customerDataAccess = new CustomerDataAccess();
            restaurantDataAccess = new RestaurantDataAccess();
            deliveryDataAccess = new DeliveryDataAccess();
            paymentDataAccess = new PaymentDataAccess();
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public void placeOrder(Order order) throws SQLException {
        // Using PlaceOrder stored procedure
        String callPlaceOrder = "{call PlaceOrder(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callPlaceOrder)) {
            stmt.setInt(1, order.getOrderId());
            stmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
            stmt.setInt(3, order.getCustomer().getCustomerId());
            stmt.setInt(4, order.getRestaurant().getRestaurantId());
            stmt.setInt(5, order.getDelivery().getDeliveryId());
            stmt.setInt(6, order.getPayment().getPaymentId());

            stmt.execute();
            System.out.println("Order placed successfully with ID: " + order.getOrderId());
        } catch (SQLException e) {
            System.err.println("Error placing order: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates and places an order using individual parameters
     * @param customerId The ID of the customer placing the order
     * @param restaurantId The ID of the restaurant for this order
     * @param deliveryLocation The delivery location
     * @param paymentMethod The payment method (e.g., "Credit Card", "Cash")
     * @param paymentStatus The payment status (e.g., "Pending", "Completed")
     * @param items Map of products and their quantities
     * @param totalAmount The total order amount
     * @return The created Order object
     * @throws SQLException if a database error occurs
     */
    public Order placeOrder(int customerId, int restaurantId,
                           com.example.foodorderingsystem.BusinessLayer.Location deliveryLocation,
                           String paymentMethod, String paymentStatus,
                           java.util.Map<com.example.foodorderingsystem.BusinessLayer.Product, Integer> items,
                           java.math.BigDecimal totalAmount) throws SQLException {

        try {
            // Get the customer and restaurant objects
            Customer customer = customerDataAccess.getCustomerById(customerId);
            Restaurant restaurant = restaurantDataAccess.getRestaurant(restaurantId);

            if (customer == null || restaurant == null) {
                throw new SQLException("Customer or restaurant not found");
            }

            // Create a new payment
            Payment payment = new Payment();
            payment.setMethod(paymentMethod);
            payment.setStatus(paymentStatus);
            payment.setAmount(totalAmount.doubleValue());

            // Store the payment
            int paymentId = paymentDataAccess.createPayment(payment);
            payment.setPaymentId(paymentId);

//            // Create a new delivery
//            Delivery delivery = new Delivery();
//            delivery.setLocation(deliveryLocation);
//            delivery.setStatus("Pending"); // Initial status
//
//            // Store the delivery
//            int deliveryId = deliveryDataAccess.createDelivery(delivery);
//            delivery.setDeliveryId(deliveryId);

            // Create the order
            LocalDateTime now = LocalDateTime.now();
            Order order = new Order(0, now, customer, restaurant, payment); // ID will be set by the database

            // Place the order
            placeOrder(order);

            // Save order items (this would need a method for storing order items)
            saveOrderItems(order.getOrderId(), items);

            return order;
        } catch (SQLException e) {
            System.err.println("Error creating and placing order: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Saves the order items to the database
     * @param orderId The ID of the order
     * @param items Map of products and their quantities
     * @throws SQLException if a database error occurs
     */
    private void saveOrderItems(int orderId, java.util.Map<com.example.foodorderingsystem.BusinessLayer.Product, Integer> items)
            throws SQLException {
        String callSaveOrderItem = "{call AddProductToOrder(?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callSaveOrderItem)) {
            for (java.util.Map.Entry<com.example.foodorderingsystem.BusinessLayer.Product, Integer> entry : items.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();

                stmt.setInt(1, orderId);
                stmt.setInt(2, product.getProductId());
                stmt.setInt(3, quantity);

                stmt.execute();
            }
            System.out.println("Order items saved successfully for order ID: " + orderId);
        } catch (SQLException e) {
            System.err.println("Error saving order items: " + e.getMessage());
            throw e;
        }
    }

    public List<Order> getCustomerOrders(int customerId) throws SQLException {
        // Using GetCustomerOrders stored procedure
        String callGetCustomerOrders = "{call GetCustomerOrders(?)}";
        List<Order> orders = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetCustomerOrders)) {
            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("Order_ID");
                    LocalDateTime orderDate = rs.getTimestamp("Order_Date").toLocalDateTime();
                    int restaurantId = rs.getInt("Restaurant_ID");
                    int deliveryId = rs.getInt("Delivery_ID");
                    String paymentStatus = rs.getString("Payment_Status");
                    String paymentMethod = rs.getString("Payment_Method");

                    // Load related entities
                    Customer customer = customerDataAccess.getCustomerById(customerId);
                    Restaurant restaurant = restaurantDataAccess.getRestaurant(restaurantId);
                    Delivery delivery = deliveryDataAccess.getDeliveryById(deliveryId);

                    // Since we don't have the actual paymentId in the result set,
                    // we'll create a dummy Payment object with the available info
                    // In a real application, you might want to fetch the real Payment object
                    Payment payment = new Payment();
                    payment.setStatus(paymentStatus);
                    payment.setMethod(paymentMethod);

                    Order order = new Order(orderId, orderDate, customer, restaurant, delivery, payment);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer orders: " + e.getMessage());
            throw e;
        }

        return orders;
    }

    public Order getCustomerOrderById(int customerId, int orderId) throws SQLException {
        // Using GetCustomerOrderByID stored procedure
        String callGetCustomerOrderById = "{call GetCustomerOrderByID(?, ?)}";
        Order order = null;

        try (CallableStatement stmt = connection.prepareCall(callGetCustomerOrderById)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime orderDate = rs.getTimestamp("Order_Date").toLocalDateTime();
                    int restaurantId = rs.getInt("Restaurant_ID");
                    int deliveryId = rs.getInt("Delivery_ID");
                    String paymentStatus = rs.getString("Payment_Status");
                    String paymentMethod = rs.getString("Payment_Method");

                    // Load related entities
                    Customer customer = customerDataAccess.getCustomerById(customerId);
                    Restaurant restaurant = restaurantDataAccess.getRestaurant(restaurantId);
                    Delivery delivery = deliveryDataAccess.getDeliveryById(deliveryId);

                    // Create a dummy Payment object with the available info
                    Payment payment = new Payment();
                    payment.setStatus(paymentStatus);
                    payment.setMethod(paymentMethod);

                    order = new Order(orderId, orderDate, customer, restaurant, delivery, payment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer order by ID: " + e.getMessage());
            throw e;
        }

        return order;
    }

    public boolean cancelOrder(int customerId, int orderId) throws SQLException {
        // Using CancelOrder stored procedure
        String callCancelOrder = "{call CancelOrder(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callCancelOrder)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, orderId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " order(s) cancelled successfully");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            throw e;
        }
    }

    public Payment getOrderPaymentStatus(int customerId, int orderId) throws SQLException {
        // Using GetOrderPaymentStatus stored procedure
        String callGetOrderPaymentStatus = "{call GetOrderPaymentStatus(?, ?)}";
        Payment payment = null;

        try (CallableStatement stmt = connection.prepareCall(callGetOrderPaymentStatus)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int paymentId = rs.getInt("Payment_ID");
                    String status = rs.getString("Status");
                    double amount = rs.getDouble("Amount");
                    String method = rs.getString("Payment_Method");

                    payment = new Payment();
                    payment.setPaymentId(paymentId);
                    payment.setStatus(status);
                    payment.setMethod(method);

                    // You can set other payment properties as needed
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order payment status: " + e.getMessage());
            throw e;
        }

        return payment;
    }

    public int getCustomerOrderCount(int customerId) throws SQLException {
        // Using GetCustomerOrderCount function
        String callGetCustomerOrderCount = "{? = call GetCustomerOrderCount(?)}";

        try (CallableStatement stmt = connection.prepareCall(callGetCustomerOrderCount)) {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, customerId);

            stmt.execute();
            return stmt.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting customer order count: " + e.getMessage());
            throw e;
        }
    }

    public Delivery getOrderDeliveryDetails(int customerId, int orderId) throws SQLException {
        // Using GetOrderDeliveryDetails stored procedure
        String callGetOrderDeliveryDetails = "{call GetOrderDeliveryDetails(?, ?)}";
        Delivery delivery = null;

        try (CallableStatement stmt = connection.prepareCall(callGetOrderDeliveryDetails)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int deliveryId = rs.getInt("Delivery_ID");

                    // Fetch the delivery object using the DeliveryDataAccess class
                    delivery = deliveryDataAccess.getDeliveryById(deliveryId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order delivery details: " + e.getMessage());
            throw e;
        }

        return delivery;
    }

    /**
     * Gets all orders for a specific customer
     * @param customerId The customer ID
     * @return List of orders for the customer
     * @throws SQLException if a database error occurs
     */
    public List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        return getCustomerOrders(customerId);
    }

    /**
     * Gets filtered orders for a specific customer based on status and date period
     * @param customerId The customer ID
     * @param status The order status to filter by (or "All" for no filter)
     * @param datePeriod The date period to filter by (e.g., "Last Week", "Last Month", or "All Time")
     * @return List of filtered orders
     * @throws SQLException if a database error occurs
     */
    public List<Order> getFilteredOrders(int customerId, String status, String datePeriod) throws SQLException {
        List<Order> allOrders = getCustomerOrders(customerId);
        List<Order> filteredOrders = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate = null;

        // Determine date filter
        if ("Last Week".equals(datePeriod)) {
            fromDate = now.minus(7, ChronoUnit.DAYS);
        } else if ("Last Month".equals(datePeriod)) {
            fromDate = now.minus(30, ChronoUnit.DAYS);
        }

        // Apply filters
        for (Order order : allOrders) {
            boolean matchesStatus = "All".equals(status) || (order.getStatus() != null && order.getStatus().equals(status));
            boolean matchesDate = "All Time".equals(datePeriod) ||
                                 (fromDate != null && order.getOrderDate().isAfter(fromDate));

            if (matchesStatus && matchesDate) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
            // Close related data access connections
            customerDataAccess.closeConnection();
            restaurantDataAccess.closeConnection();
            deliveryDataAccess.closeConnection();
            paymentDataAccess.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
