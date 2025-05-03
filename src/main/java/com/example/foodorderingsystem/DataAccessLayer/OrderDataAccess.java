package com.example.foodorderingsystem.DataAccessLayer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Delivery;
import com.example.foodorderingsystem.BusinessLayer.Order;
import com.example.foodorderingsystem.BusinessLayer.Payment;
import com.example.foodorderingsystem.BusinessLayer.Product;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;

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

    public int placeOrder(Order order) throws SQLException {
        // Now 6 "?": 5 IN, 1 OUT
        String sql = "{call PlaceOrder(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setTimestamp(1, new Timestamp(order.getOrderDate().getTime()));
            stmt.setInt(2, order.getCustomer().getCustomerId());
            stmt.setInt(3, order.getRestaurant().getRestaurantId());
            stmt.setInt(4, order.getDelivery().getDeliveryId());
            stmt.setInt(5, order.getPayment().getPaymentId());

            // now register parameter #6 as your OUTPUT
            stmt.registerOutParameter(6, Types.INTEGER);

            stmt.execute();

            int orderId = stmt.getInt(6);
            System.out.println("Order placed successfully with ID: " + orderId);
            return orderId;
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

            // Create a new delivery - now using proper fields to match the stored procedure
            Delivery delivery = new Delivery(null, null, "Delivery", "Person", "Agent", 0, 1000.0, new ArrayList<>(), new ArrayList<>());
            delivery.setLocation(deliveryLocation);

            // Store the delivery - the ID will be generated and returned
            int deliveryId = deliveryDataAccess.createDelivery(delivery);
            delivery.setDeliveryId(deliveryId);

            // Create a new payment and set restaurant and delivery IDs
            Payment payment = new Payment();
            payment.setMethod(paymentMethod);
            payment.setStatus(paymentStatus); // Payment status is used instead of order status
            payment.setAmount(totalAmount.doubleValue());
            payment.setRestaurantId(restaurantId);
            payment.setDeliveryId(deliveryId);

            int paymentId = paymentDataAccess.createPayment(payment);
            System.out.println("Payment created successfully with ID: " + paymentId);
            if(paymentId == 0) {
                throw new SQLException("Failed to create payment");
            }
            payment.setPaymentId(paymentId);

            // Create the order
            Date now = new Date();
            Order order = new Order(now, customer, restaurant, delivery, payment); // ID will be set by the database

            // Place the order
            int orderId = placeOrder(order);
            order.setOrderId(orderId);

            saveOrderItems(orderId, items);

            return order;
        } catch (SQLException e) {
            System.err.println("Error creating and placing order: " + e.getMessage());
            throw e;
        }
    }

    private void saveOrderItems(int orderId, java.util.Map<com.example.foodorderingsystem.BusinessLayer.Product, Integer> items)
            throws SQLException {
        String callSaveOrderItem = "{call AddProductToOrder(?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callSaveOrderItem)) {
            for (java.util.Map.Entry<com.example.foodorderingsystem.BusinessLayer.Product, Integer> entry : items.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();

                // Fixed parameter order to match the stored procedure's expected order
                stmt.setInt(1, product.getProductId());
                stmt.setInt(2, orderId);
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
                    Date orderDate = rs.getTimestamp("Order_Date");
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
                    Payment payment = new Payment();
                    payment.setStatus(paymentStatus);
                    payment.setMethod(paymentMethod);

                    Order order = new Order(orderDate, customer, restaurant, delivery, payment);
                    order.setOrderId(orderId);

                    // Calculate and set the total amount from the has_Product_Order table
                    double totalAmount = calculateOrderTotal(orderId);
                    order.setTotalAmount(totalAmount);

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer orders: " + e.getMessage());
            throw e;
        }

        return orders;
    }

    /**
     * Calculates the total amount of an order from the has_Product_Order table
     * @param orderId The ID of the order
     * @return The total amount of the order
     * @throws SQLException if a database error occurs
     */
    public double calculateOrderTotal(int orderId) throws SQLException {
        String sql = "SELECT p.Product_ID, p.Product_Unit_Price, hpo.Quantity " +
                     "FROM has_Product_Order hpo " +
                     "JOIN Products p ON hpo.Product_ID = p.Product_ID " +
                     "WHERE hpo.Order_ID = ?";

        double total = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double price = rs.getDouble("Product_Unit_Price");
                    int quantity = rs.getInt("Quantity");
                    total += price * quantity;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating order total: " + e.getMessage());
            throw e;
        }

        return total;
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
                    Date orderDate = rs.getTimestamp("Order_Date");
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

                    order = new Order(orderDate, customer, restaurant, delivery, payment);
                    order.setOrderId(orderId);

                    // Calculate and set the total amount from the has_Product_Order table
                    double totalAmount = calculateOrderTotal(orderId);
                    order.setTotalAmount(totalAmount);
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

    public List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        return getCustomerOrders(customerId);
    }

    /**
     * Filters orders for a customer based on status and date period
     * @param customerId The ID of the customer
     * @param status The order status to filter by (e.g., "Delivered", "Processing", "Cancelled", "All")
     * @param datePeriod The time period to filter by (e.g., "Last Week", "Last Month", "All Time")
     * @return List of filtered orders
     * @throws SQLException if a database error occurs
     */
    public List<Order> getFilteredOrders(int customerId, String status, String datePeriod) throws SQLException {
        List<Order> allOrders = getCustomerOrders(customerId);
        List<Order> filteredOrders = new ArrayList<>();

        Date now = new Date();
        Date fromDate = null;

        // Determine date filter
        if (datePeriod != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);

            switch (datePeriod) {
                case "Last Week":
                    cal.add(Calendar.DAY_OF_YEAR, -7);
                    fromDate = cal.getTime();
                    break;
                case "Last Month":
                    cal.add(Calendar.MONTH, -1);
                    fromDate = cal.getTime();
                    break;
                case "Last 3 Months":
                    cal.add(Calendar.MONTH, -3);
                    fromDate = cal.getTime();
                    break;
                case "Last 6 Months":
                    cal.add(Calendar.MONTH, -6);
                    fromDate = cal.getTime();
                    break;
                case "Last Year":
                    cal.add(Calendar.YEAR, -1);
                    fromDate = cal.getTime();
                    break;
                case "All Time":
                default:
                    // No date filtering needed
                    fromDate = null;
                    break;
            }
        }

        // Apply filters
        for (Order order : allOrders) {
            // Since delivery no longer has status, we only use payment status
            String paymentStatus = order.getPayment() != null ? order.getPayment().getStatus() : null;

            // Check if status matches payment status
            boolean matchesStatus = "All".equals(status) ||
                                   (paymentStatus != null && paymentStatus.equals(status));

            // Check if date matches
            boolean matchesDate = fromDate == null ||
                                 (order.getOrderDate() != null && order.getOrderDate().after(fromDate));

            if (matchesStatus && matchesDate) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }

    /**
     * Gets all orders in the system, used mainly by administrative interfaces
     * @return List of all orders
     * @throws SQLException if a database error occurs
     */
    public List<Order> getAllOrders() throws SQLException {
        // Using GetAllOrders stored procedure
        String callGetAllOrders = "{call GetAllOrders()}";
        List<Order> orders = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetAllOrders);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("Order_ID");
                int customerId = rs.getInt("Customer_ID");
                Date orderDate = rs.getDate("Order_Date");
                int restaurantId = rs.getInt("Restaurant_ID");
                int deliveryId = rs.getInt("Delivery_ID");
                int paymentId = rs.getInt("Payment_ID");

                // Load related entities
                Customer customer = customerDataAccess.getCustomerById(customerId);
                Restaurant restaurant = restaurantDataAccess.getRestaurant(restaurantId);
                Delivery delivery = deliveryDataAccess.getDeliveryById(deliveryId);
                Payment payment = paymentDataAccess.getPaymentById(paymentId);

                Order order = new Order(orderDate, customer, restaurant, delivery, payment);
                order.setOrderId(orderId); // Set the order ID

                // Calculate and set the total amount from the has_Product_Order table
                double totalAmount = calculateOrderTotal(orderId);
                order.setTotalAmount(totalAmount);

                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
            throw e;
        }

        return orders;
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
