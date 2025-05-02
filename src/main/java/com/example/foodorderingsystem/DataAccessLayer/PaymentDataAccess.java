package com.example.foodorderingsystem.DataAccessLayer;

import com.example.foodorderingsystem.BusinessLayer.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentDataAccess {

    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;

    public PaymentDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertPayment(Payment payment) throws SQLException {
        String callProc = "{call PlaceOrderAndPayment(?, ?, ?, ?, ?, ?, ?, ?)}";
        int generatedPaymentId = -1;

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setTimestamp(2, new Timestamp(new Date().getTime())); // Current date/time for Order_Date
            stmt.setInt(3, payment.getCustomerId());
            stmt.setInt(4, payment.getRestaurant().getRestaurantId());
            stmt.setInt(5, payment.getDelivery() != null ? payment.getDelivery().getDeliveryId() : null);
            stmt.setString(6, payment.getMethod());
            stmt.setString(7, payment.getStatus());

            // Register the output parameter
            stmt.registerOutParameter(8, Types.INTEGER);

            stmt.execute();

            // Get the output parameter value
            generatedPaymentId = stmt.getInt(8);
            payment.setPaymentId(generatedPaymentId);

            System.out.println("Payment inserted successfully with ID: " + generatedPaymentId);
            return generatedPaymentId;
        }
    }


    public Payment getPaymentById(int paymentId) throws SQLException {
        // Using a more general query since no specific stored procedure provided
        String callProc = "SELECT p.*, o.Customer_ID, o.Restaurant_ID, o.Delivery_ID FROM Payment p " +
                         "JOIN Order_Table o ON p.Order_ID = o.Order_ID " +
                         "WHERE p.Payment_ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(callProc)) {
            stmt.setInt(1, paymentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                } else {
                    return null;
                }
            }
        }
    }

    // For backward compatibility
    public Payment getPayment(int paymentId) throws SQLException {
        return getPaymentById(paymentId);
    }

    public Payment getPaymentByOrder(int orderId) throws SQLException {
        String callProc = "SELECT p.*, o.Customer_ID, o.Restaurant_ID, o.Delivery_ID FROM Payment p " +
                         "JOIN Order_Table o ON p.Order_ID = o.Order_ID " +
                         "WHERE p.Order_ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(callProc)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public List<Payment> getAllPaymentsForCustomer(int customerId) throws SQLException {
        String callProc = "{call GetCustomerOrdersWithPaymentStatus(?)}";
        List<Payment> payments = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setOrderId(rs.getInt("Order_ID"));
                    payment.setStatus(rs.getString("PaymentStatus"));
                    payment.setMethod(rs.getString("PaymentMethod"));
                    // Order date is also returned but would need to add to Payment class if needed

                    payments.add(payment);
                }
            }
        }

        return payments;
    }

    public List<Payment> getAllPayments() throws SQLException {
        // Since no specific stored procedure was provided for getting all payments
        String getAllPaymentsSQL = "SELECT p.*, o.Customer_ID, o.Restaurant_ID, o.Delivery_ID FROM Payment p " +
                                  "JOIN Order_Table o ON p.Order_ID = o.Order_ID " +
                                  "ORDER BY p.Payment_ID DESC";
        List<Payment> payments = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getAllPaymentsSQL)) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        }

        return payments;
    }

    public void updatePaymentStatus(int orderId, String status, String method) throws SQLException {
        String callProc = "{call UpdatePaymentStatus(?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, orderId);
            stmt.setString(2, status);
            stmt.setString(3, method);

            stmt.execute();
            System.out.println("Payment status updated for order ID: " + orderId);
        }
    }

    public void updatePayment(Payment payment) throws SQLException {
        // Use the updatePaymentStatus method since that's what the provided stored procedure does
        updatePaymentStatus(payment.getOrderId(), payment.getStatus(), payment.getMethod());
    }

    public boolean isPaymentCompletedForOrder(int orderId) throws SQLException {
        String callProc = "{? = call IsPaymentCompletedForOrder(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.registerOutParameter(1, Types.BIT);
            stmt.setInt(2, orderId);

            stmt.execute();

            return stmt.getBoolean(1);
        }
    }

    public void deletePayment(int paymentId) throws SQLException {
        // Since no specific stored procedure was provided for deleting payments
        String deletePaymentSQL = "DELETE FROM Payment WHERE Payment_ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(deletePaymentSQL)) {
            stmt.setInt(1, paymentId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " payment record(s) deleted");
        }
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("Payment_ID"));
        payment.setOrderId(rs.getInt("Order_ID"));
        payment.setStatus(rs.getString("Status"));
        payment.setMethod(rs.getString("Method"));

        // These would require extra logic to fetch the actual objects
        // We'd need to call DeliveryDataAccess and RestaurantDataAccess to get the full objects
        int restaurantId = rs.getInt("Restaurant_ID");
        int deliveryId = rs.getInt("Delivery_ID");
        int customerId = rs.getInt("Customer_ID");

        // Set the IDs in the payment object
        payment.setCustomerId(customerId);

        // We would need to fetch the actual objects if required
        // RestaurantDataAccess restaurantDA = new RestaurantDataAccess();
        // DeliveryDataAccess deliveryDA = new DeliveryDataAccess();
        // payment.setRestaurant(restaurantDA.getRestaurantById(restaurantId));
        // payment.setDelivery(deliveryDA.getDeliveryById(deliveryId));

        return payment;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
