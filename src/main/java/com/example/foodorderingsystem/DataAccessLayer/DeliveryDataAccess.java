package com.example.foodorderingsystem.DataAccessLayer;

import com.example.foodorderingsystem.BusinessLayer.Delivery;
import com.example.foodorderingsystem.BusinessLayer.Location;

import java.sql.*;
import java.util.*;

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
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // INSERT
    public void insertDelivery(Delivery delivery) throws SQLException {
        String insertDeliverySQL = "INSERT INTO Delivery (Delivery_ID, First_Name, Middle_Name, Last_Name, Salary) VALUES (?, ?, ?, ?, ?)";
        String insertPhoneSQL = "INSERT INTO Del_PhoneNo (Delivery_ID, DelPhoneNo) VALUES (?, ?)";
        String insertLocationSQL = "INSERT INTO Del_Location (Delivery_ID, City, Street_Name, Street_Number) VALUES (?, ?, ?, ?)";

        try (PreparedStatement deliveryStmt = connection.prepareStatement(insertDeliverySQL);
             PreparedStatement phoneStmt = connection.prepareStatement(insertPhoneSQL);
             PreparedStatement locationStmt = connection.prepareStatement(insertLocationSQL)) {

            deliveryStmt.setInt(1, delivery.getDeliveryId());
            deliveryStmt.setString(2, delivery.getFirstName());
            deliveryStmt.setString(3, delivery.getMiddleName());
            deliveryStmt.setString(4, delivery.getLastName());
            deliveryStmt.setDouble(5, delivery.getSalary());
            deliveryStmt.executeUpdate();

            for (String phone : delivery.getPhoneNumbers()) {
                phoneStmt.setInt(1, delivery.getDeliveryId());
                phoneStmt.setString(2, phone);
                phoneStmt.executeUpdate();
            }

            // Only one location allowed per Delivery_ID (due to PK)
            if (!delivery.getLocations().isEmpty()) {
                Location loc = delivery.getLocations().get(0);
                locationStmt.setInt(1, delivery.getDeliveryId());
                locationStmt.setString(2, loc.getCity());
                locationStmt.setString(3, loc.getStreetName());
                locationStmt.setString(4, loc.getStreetNumber());
                locationStmt.executeUpdate();
            }

            System.out.println("Delivery record inserted successfully.");
        }
    }

    public void updateDelivery(Delivery delivery) throws SQLException {
        String updateDeliverySQL = "UPDATE Delivery SET First_Name = ?, Middle_Name = ?, Last_Name = ?, Salary = ? WHERE Delivery_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateDeliverySQL)) {
            stmt.setString(1, delivery.getFirstName());
            stmt.setString(2, delivery.getMiddleName());
            stmt.setString(3, delivery.getLastName());
            stmt.setDouble(4, delivery.getSalary());
            stmt.setInt(5, delivery.getDeliveryId());
            stmt.executeUpdate();
        }

        deletePhones(delivery.getDeliveryId());
        insertPhones(delivery);

        deleteLocation(delivery.getDeliveryId());
        insertLocation(delivery);

        System.out.println("Delivery record updated (phones and location reinserted).");
    }

    private void deletePhones(int deliveryId) throws SQLException {
        String deleteSQL = "DELETE FROM Del_PhoneNo WHERE Delivery_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, deliveryId);
            stmt.executeUpdate();
        }
    }

    private void insertPhones(Delivery delivery) throws SQLException {
        String insertSQL = "INSERT INTO Del_PhoneNo (Delivery_ID, DelPhoneNo) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            for (String phone : delivery.getPhoneNumbers()) {
                stmt.setInt(1, delivery.getDeliveryId());
                stmt.setString(2, phone);
                stmt.executeUpdate();
            }
        }
    }

    private void deleteLocation(int deliveryId) throws SQLException {
        String deleteSQL = "DELETE FROM Del_Location WHERE Delivery_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, deliveryId);
            stmt.executeUpdate();
        }
    }

    private void insertLocation(Delivery delivery) throws SQLException {
        if (delivery.getLocations() == null || delivery.getLocations().isEmpty()) return;

        Location loc = delivery.getLocations().get(0); // single location per delivery
        String insertSQL = "INSERT INTO Del_Location (Delivery_ID, City, Street_Name, Street_Number) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setInt(1, delivery.getDeliveryId());
            stmt.setString(2, loc.getCity());
            stmt.setString(3, loc.getStreetName());
            stmt.setString(4, loc.getStreetNumber());
            stmt.executeUpdate();
        }
    }

}
