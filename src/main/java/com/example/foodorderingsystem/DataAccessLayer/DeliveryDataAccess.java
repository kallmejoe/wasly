package com.example.foodorderingsystem.DataAccessLayer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.example.foodorderingsystem.BusinessLayer.Delivery;
import com.example.foodorderingsystem.BusinessLayer.Location;

public class DeliveryDataAccess {
    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;

    public DeliveryDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to MSSQL successfully!");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public void CreateDelivery(Delivery delivery) throws SQLException {
        // Using CreateDelivery stored procedure for basic delivery info
        String callCreateDelivery = "{call CreateDelivery(?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callCreateDelivery)) {
            stmt.setString(1, delivery.getFirstName());
            stmt.setString(2, delivery.getMiddleName());
            stmt.setString(3, delivery.getLastName());
            stmt.setDouble(4, delivery.getSalary());

            stmt.execute();

            // Get the ID of the newly created delivery person (using SCOPE_IDENTITY() from SQL Server)
            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT SCOPE_IDENTITY() AS ID")) {
                if (rs.next()) {
                    int deliveryId = rs.getInt("ID");
                    delivery.setDeliveryId(deliveryId);

                    // Insert phone numbers using for loop
                    // for (String phone : delivery.getPhoneNumbers()) {
                    //     addDeliveryPhone(deliveryId, phone);
                    // }

                    // // Insert locations using for loop
                    // for (Location location : delivery.getLocations()) {
                    //     addDeliveryLocation(deliveryId, location);
                    // }

                    System.out.println("Delivery created successfully with ID: " + deliveryId);
                }
            }
        }
    }

    /**
     * Creates a new delivery record in the database and returns the generated ID
     *
     * @param delivery The delivery object to create
     * @return The generated delivery ID
     * @throws SQLException if a database error occurs
     */
    public int createDelivery(Delivery delivery) throws SQLException {
        // Using the CreateDelivery stored procedure with ResultSet return pattern
        String callCreateDelivery = "{call CreateDelivery(?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callCreateDelivery)) {
            stmt.setString(1, delivery.getFirstName());
            stmt.setString(2, delivery.getMiddleName());
            stmt.setString(3, delivery.getLastName());
            stmt.setDouble(4, delivery.getSalary());

            // Execute the stored procedure and get the ResultSet with the ID
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int deliveryId = rs.getInt("Delivery_ID"); // Column name from stored procedure
                    delivery.setDeliveryId(deliveryId);

                    System.out.println("Delivery created successfully with ID: " + deliveryId);
                    return deliveryId;
                } else {
                    throw new SQLException("Failed to retrieve DeliveryID after creation");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating delivery: " + e.getMessage());
            throw e;
        }
    }

    private void addDeliveryPhone(int deliveryId, String phoneNumber) throws SQLException {
        // Using AddDeliveryPhone stored procedure
        String callAddPhone = "{call AddDeliveryPhone(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callAddPhone)) {
            stmt.setInt(1, deliveryId);
            stmt.setString(2, phoneNumber);
            stmt.execute();
        }
    }

    private void addDeliveryLocation(int deliveryId, Location location) throws SQLException {
        // Using UpsertDeliveryLocation stored procedure
        String callUpsertLocation = "{call UpsertDeliveryLocation(?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callUpsertLocation)) {
            stmt.setInt(1, deliveryId);
            stmt.setString(2, location.getCity());
            stmt.setString(3, location.getStreetName());
            stmt.setString(4, location.getStreetNumber());
            stmt.execute();
        }
    }

    public void updateDelivery(Delivery delivery) throws SQLException {
        // Using UpdateDelivery stored procedure for basic delivery info
        String callUpdateDelivery = "{call UpdateDelivery(?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callUpdateDelivery)) {
            stmt.setInt(1, delivery.getDeliveryId());
            stmt.setString(2, delivery.getFirstName());
            stmt.setString(3, delivery.getMiddleName());
            stmt.setString(4, delivery.getLastName());
            stmt.setDouble(5, delivery.getSalary());

            stmt.execute();

            int deliveryId = delivery.getDeliveryId();

            // Update phone numbers (delete all and re-insert)
            deleteAllDeliveryPhones(deliveryId);
            for (String phone : delivery.getPhoneNumbers()) {
                addDeliveryPhone(deliveryId, phone);
            }

            // Update locations (delete all and re-insert)
            deleteDeliveryLocations(deliveryId);
            for (Location location : delivery.getLocations()) {
                addDeliveryLocation(deliveryId, location);
            }

            System.out.println("Delivery updated successfully.");
        }
    }

    private void deleteAllDeliveryPhones(int deliveryId) throws SQLException {
        // Get all phone numbers and delete them individually
        List<String> phoneNumbers = getDeliveryPhones(deliveryId);
        for (String phone : phoneNumbers) {
            deleteDeliveryPhone(deliveryId, phone);
        }
    }

    private void deleteDeliveryPhone(int deliveryId, String phoneNumber) throws SQLException {
        // Using DeleteDeliveryPhone stored procedure
        String callDeletePhone = "{call DeleteDeliveryPhone(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeletePhone)) {
            stmt.setInt(1, deliveryId);
            stmt.setString(2, phoneNumber);
            stmt.execute();
        }
    }

    private void deleteDeliveryLocations(int deliveryId) throws SQLException {
        // Using DeleteDeliveryLocation stored procedure
        String callDeleteLocation = "{call DeleteDeliveryLocation(?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeleteLocation)) {
            stmt.setInt(1, deliveryId);
            stmt.execute();
        }
    }

    public void DeleteDelivery(int deliveryId) throws SQLException {
        // First delete all phone numbers
        deleteAllDeliveryPhones(deliveryId);

        // Then delete all locations
        deleteDeliveryLocations(deliveryId);

        // Finally delete the delivery using DeleteDelivery stored procedure
        String callDeleteDelivery = "{call DeleteDelivery(?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeleteDelivery)) {
            stmt.setInt(1, deliveryId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " delivery record(s) deleted along with related data.");
        }
    }

    public Delivery getDeliveryById(int deliveryId) throws SQLException {
        // Get basic delivery info
        String callGetDelivery = "{call GetDeliveryById(?)}";
        Delivery delivery = null;

        try (CallableStatement stmt = connection.prepareCall(callGetDelivery)) {
            stmt.setInt(1, deliveryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("First_Name");
                    String middleName = rs.getString("Middle_Name");
                    String lastName = rs.getString("Last_Name");
                    double salary = rs.getDouble("Salary");

                    // Get phone numbers
                    List<String> phoneNumbers = getDeliveryPhones(deliveryId);

                    // Get locations
                    List<Location> locations = getDeliveryLocations(deliveryId);

                    // Create delivery object
                    delivery = new Delivery(null, null, firstName, middleName, lastName, deliveryId, salary, phoneNumbers, locations);
                }
            }
        }

        return delivery;
    }

    private List<String> getDeliveryPhones(int deliveryId) throws SQLException {
        // Using GetDeliveryPhones stored procedure
        String callGetPhones = "{call GetDeliveryPhoneNo(?)}";
        List<String> phoneNumbers = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetPhones)) {
            stmt.setInt(1, deliveryId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phoneNumbers.add(rs.getString("DelPhoneNo"));
                }
            }
        }

        return phoneNumbers;
    }

    private List<Location> getDeliveryLocations(int deliveryId) throws SQLException {
        // Using GetDeliveryLocation stored procedure
        String callGetLocation = "{call GetDeliveryLocation(?)}";
        List<Location> locations = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetLocation)) {
            stmt.setInt(1, deliveryId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String city = rs.getString("City");
                    String streetName = rs.getString("Street_Name");
                    String streetNumber = rs.getString("Street_Number");
                    locations.add(new Location(city, streetName, streetNumber));
                }
            }
        }

        return locations;
    }

    public List<Delivery> getAllDeliveries() throws SQLException {
        String callGetAllDeliveries = "{call GetAllDeliveries}";
        List<Delivery> deliveries = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetAllDeliveries);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int deliveryId = rs.getInt("Delivery_ID");
                String firstName = rs.getString("First_Name");
                String middleName = rs.getString("Middle_Name");
                String lastName = rs.getString("Last_Name");
                double salary = rs.getDouble("Salary");

                // Get phone numbers
                List<String> phoneNumbers = getDeliveryPhones(deliveryId);

                // Get locations
                List<Location> locations = getDeliveryLocations(deliveryId);

                // Create delivery object
                Delivery delivery = new Delivery(null, null, firstName, middleName, lastName, deliveryId, salary, phoneNumbers, locations);
                deliveries.add(delivery);
            }
        }

        return deliveries;
    }

    // For backward compatibility with existing code
    public Delivery GetDeliveryFull(int deliveryId) throws SQLException {
        return getDeliveryById(deliveryId);
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
