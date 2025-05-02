package com.example.foodorderingsystem.DataAccessLayer;

import com.example.foodorderingsystem.BusinessLayer.Payment;
import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.BusinessLayer.Delivery;
import com.example.foodorderingsystem.BusinessLayer.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentDataAccess {

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

    public PaymentDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database successfully for PaymentDataAccess!");

            // Initialize related data access objects for relationships
            customerDataAccess = new CustomerDataAccess();
            restaurantDataAccess = new RestaurantDataAccess();
            deliveryDataAccess = new DeliveryDataAccess();
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a new payment using the PlacePayment stored procedure
     * @param payment The payment object to create
     * @return The generated payment ID
     * @throws SQLException if a database error occurs
     */
    public int createPayment(Payment payment) throws SQLException {
        // Using PlacePayment stored procedure
        String callProc = "{call addPayment(?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            // Set parameters for the stored procedure
            stmt.setString(1, payment.getMethod());
            stmt.setString(2, payment.getStatus());
            stmt.setInt(3, payment.getDeliveryId());
            stmt.setInt(4, payment.getRestaurantId());

            stmt.execute();

            // Get the payment ID (assuming it's auto-generated)
            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT MAX(Payment_ID) AS Payment_ID FROM Payment")) {
                if (rs.next()) {
                    int paymentId = rs.getInt("Payment_ID");
                    payment.setPaymentId(paymentId);
                    System.out.println("Payment created successfully with ID: " + paymentId);
                    return paymentId;
                } else {
                    throw new SQLException("Payment was created but ID could not be retrieved");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating payment: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Gets a payment by payment ID
     * @param paymentId The ID of the payment to retrieve
     * @return The Payment object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Payment getPaymentById(int paymentId) throws SQLException {
        String callProc = "{call GetOrderByPaymentID(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, paymentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(paymentId);
                    payment.setStatus(rs.getString("Payment_Status"));
                    payment.setMethod(rs.getString("Payment_Method"));

                    // Set related information
                    payment.setOrderId(rs.getInt("Order_ID"));
                    payment.setCustomerId(rs.getInt("Customer_ID"));
                    payment.setRestaurantId(rs.getInt("Restaurant_ID"));
                    payment.setDeliveryId(rs.getInt("Delivery_ID"));

                    // Optionally load the full related objects if needed
                    // This can be enabled if detailed information is required
                    /*
                    int customerId = rs.getInt("Customer_ID");
                    int restaurantId = rs.getInt("Restaurant_ID");
                    int deliveryId = rs.getInt("Delivery_ID");

                    payment.setCustomer(customerDataAccess.getCustomerById(customerId));
                    payment.setRestaurant(restaurantDataAccess.getRestaurant(restaurantId));
                    payment.setDelivery(deliveryDataAccess.getDeliveryById(deliveryId));
                    */

                    return payment;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment by ID: " + e.getMessage());
            throw e;
        }

        return null; // Payment not found
    }

    /**
     * Gets a payment by order ID using the GetPaymentByOrderID stored procedure
     * @param orderId The ID of the order
     * @return The Payment object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Payment getPaymentByOrder(int orderId) throws SQLException {
        String callProc = "{call GetPaymentByOrderID(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("Payment_ID"));
                    payment.setStatus(rs.getString("Payment_Status"));
                    payment.setMethod(rs.getString("Payment_Method"));
                    payment.setOrderId(orderId);

                    // Set related IDs
                    payment.setDeliveryId(rs.getInt("Delivery_ID"));
                    payment.setRestaurantId(rs.getInt("Restaurant_ID"));
                    payment.setCustomerId(rs.getInt("Customer_ID"));

                    // Get order date if needed
                    Timestamp orderDateTimestamp = rs.getTimestamp("Order_Date");
                    if (orderDateTimestamp != null) {
                        payment.setOrderDate(orderDateTimestamp.toLocalDateTime());
                    }

                    return payment;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment by order ID: " + e.getMessage());
            throw e;
        }

        return null; // Payment not found
    }

    /**
     * Gets all payments associated with a customer using the GetPaymentByCustomerID stored procedure
     * @param customerId The ID of the customer
     * @return List of Payment objects
     * @throws SQLException if a database error occurs
     */
    public List<Payment> getPaymentsByCustomer(int customerId) throws SQLException {
        String callProc = "{call GetPaymentByCustomerID(?)}";
        List<Payment> payments = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("Payment_ID"));
                    payment.setStatus(rs.getString("Payment_Status"));
                    payment.setMethod(rs.getString("Payment_Method"));
                    payment.setOrderId(rs.getInt("Order_ID"));
                    payment.setDeliveryId(rs.getInt("Delivery_ID"));
                    payment.setRestaurantId(rs.getInt("Restaurant_ID"));
                    payment.setCustomerId(customerId);

                    // Get order date if needed
                    Timestamp orderDateTimestamp = rs.getTimestamp("Order_Date");
                    if (orderDateTimestamp != null) {
                        payment.setOrderDate(orderDateTimestamp.toLocalDateTime());
                    }

                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments by customer ID: " + e.getMessage());
            throw e;
        }

        return payments;
    }

    /**
     * Updates a payment's status and method
     * @param payment The payment to update
     * @throws SQLException if a database error occurs
     */
    public void updatePayment(Payment payment) throws SQLException {
        String updateSQL = "UPDATE Payment SET Payment_Status = ?, Payment_Method = ? WHERE Payment_ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, payment.getStatus());
            stmt.setString(2, payment.getMethod());
            stmt.setInt(3, payment.getPaymentId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Payment updated successfully with ID: " + payment.getPaymentId());
            } else {
                throw new SQLException("Payment update failed, no matching payment found");
            }
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Gets all payments in the system
     * @return List of all Payment objects
     * @throws SQLException if a database error occurs
     */
    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String getAllPaymentsSQL = "SELECT p.*, o.Customer_ID, o.Order_Date " +
                                  "FROM Payment p " +
                                  "JOIN Order_Table o ON p.Payment_ID = o.Payment_ID " +
                                  "ORDER BY p.Payment_ID DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getAllPaymentsSQL)) {

            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("Payment_ID"));
                payment.setStatus(rs.getString("Payment_Status"));
                payment.setMethod(rs.getString("Payment_Method"));
                payment.setOrderId(rs.getInt("Order_ID"));
                payment.setDeliveryId(rs.getInt("Delivery_ID"));
                payment.setRestaurantId(rs.getInt("Restaurant_ID"));
                payment.setCustomerId(rs.getInt("Customer_ID"));

                // Get order date
                Timestamp orderDateTimestamp = rs.getTimestamp("Order_Date");
                if (orderDateTimestamp != null) {
                    payment.setOrderDate(orderDateTimestamp.toLocalDateTime());
                }

                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all payments: " + e.getMessage());
            throw e;
        }

        return payments;
    }

    /**
     * Checks if payment is completed for a given order
     * @param orderId The order ID to check
     * @return true if payment is completed, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isPaymentCompletedForOrder(int orderId) throws SQLException {
        Payment payment = getPaymentByOrder(orderId);
        if (payment == null) {
            return false;
        }

        return "Completed".equalsIgnoreCase(payment.getStatus());
    }

    /**
     * Deletes a payment from the database
     * @param paymentId The ID of the payment to delete
     * @throws SQLException if a database error occurs
     */
    public void deletePayment(int paymentId) throws SQLException {
        String deleteSQL = "DELETE FROM Payment WHERE Payment_ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, paymentId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Payment deleted successfully with ID: " + paymentId);
            } else {
                System.out.println("No payment found with ID: " + paymentId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Maps a ResultSet to a Payment object
     * @param rs The ResultSet to map
     * @return A Payment object
     * @throws SQLException if a database error occurs
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("Payment_ID"));
        payment.setStatus(rs.getString("Payment_Status"));
        payment.setMethod(rs.getString("Payment_Method"));

        // Set related entity IDs
        payment.setOrderId(rs.getInt("Order_ID"));
        payment.setCustomerId(rs.getInt("Customer_ID"));
        payment.setRestaurantId(rs.getInt("Restaurant_ID"));
        payment.setDeliveryId(rs.getInt("Delivery_ID"));

        // Set related objects if needed
        // This can be enabled if detailed information is required
        /*
        int customerId = rs.getInt("Customer_ID");
        int restaurantId = rs.getInt("Restaurant_ID");
        int deliveryId = rs.getInt("Delivery_ID");

        payment.setCustomer(customerDataAccess.getCustomerById(customerId));
        payment.setRestaurant(restaurantDataAccess.getRestaurant(restaurantId));
        payment.setDelivery(deliveryDataAccess.getDeliveryById(deliveryId));
        */

        return payment;
    }

    /**
     * Closes the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed for PaymentDataAccess.");
            }

            // Close related connections
            if (customerDataAccess != null) {
                customerDataAccess.closeConnection();
            }
            if (restaurantDataAccess != null) {
                restaurantDataAccess.closeConnection();
            }
            if (deliveryDataAccess != null) {
                deliveryDataAccess.closeConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
