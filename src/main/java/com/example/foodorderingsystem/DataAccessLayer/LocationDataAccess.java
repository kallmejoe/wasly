package com.example.foodorderingsystem.DataAccessLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.foodorderingsystem.BusinessLayer.Location;

public class LocationDataAccess {

    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;

    public LocationDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Since Location is typically linked to other entities like Customer, Restaurant, etc.
    // This class provides generic location operations

    public List<Location> getAllLocations() throws SQLException {
        String getAllLocationsSQL = "SELECT DISTINCT City, Street_Name, Street_Number FROM " +
                "(SELECT City, Street_Name, Street_Number FROM Cust_Location " +
                "UNION " +
                "SELECT City, Street_Name, Street_Number FROM Rest_Location " +
                "UNION " +
                "SELECT City, Street_Name, Street_Number FROM Del_Location) AS AllLocations";

        List<Location> locations = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getAllLocationsSQL)) {

            while (rs.next()) {
                String city = rs.getString("City");
                String streetName = rs.getString("Street_Name");
                String streetNumber = rs.getString("Street_Number");

                Location location = new Location(city, streetName, streetNumber);
                locations.add(location);
            }
        }

        return locations;
    }

    public List<Location> getLocationsByCity(String city) throws SQLException {
        String getLocationsByCitySQL = "SELECT DISTINCT City, Street_Name, Street_Number FROM " +
                "(SELECT City, Street_Name, Street_Number FROM Cust_Location " +
                "UNION " +
                "SELECT City, Street_Name, Street_Number FROM Rest_Location " +
                "UNION " +
                "SELECT City, Street_Name, Street_Number FROM Del_Location) AS AllLocations " +
                "WHERE City = ?";

        List<Location> locations = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(getLocationsByCitySQL)) {
            stmt.setString(1, city);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String streetName = rs.getString("Street_Name");
                    String streetNumber = rs.getString("Street_Number");

                    Location location = new Location(city, streetName, streetNumber);
                    locations.add(location);
                }
            }
        }

        return locations;
    }

    public List<String> getAllCities() throws SQLException {
        String getAllCitiesSQL = "SELECT DISTINCT City FROM " +
                "(SELECT City FROM Cust_Location " +
                "UNION " +
                "SELECT City FROM Rest_Location " +
                "UNION " +
                "SELECT City FROM Del_Location) AS AllCities " +
                "ORDER BY City";

        List<String> cities = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getAllCitiesSQL)) {

            while (rs.next()) {
                cities.add(rs.getString("City"));
            }
        }

        return cities;
    }

    // Get all customer locations
    public List<Location> getCustomerLocations(int customerId) throws SQLException {
        String getLocationsSQL = "SELECT City, Street_Name, Street_Number FROM Cust_Location WHERE Customer_ID = ?";

        List<Location> locations = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(getLocationsSQL)) {
            stmt.setInt(1, customerId);

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

    // Get all restaurant locations
    public List<Location> getRestaurantLocations(int restaurantId) throws SQLException {
        String getLocationsSQL = "SELECT City, Street_Name, Street_Number FROM Rest_Location WHERE Restaurant_ID = ?";

        List<Location> locations = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(getLocationsSQL)) {
            stmt.setInt(1, restaurantId);

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

    // Get delivery person's location
    public Location getDeliveryLocation(int deliveryId) throws SQLException {
        String getLocationSQL = "SELECT City, Street_Name, Street_Number FROM Del_Location WHERE Delivery_ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(getLocationSQL)) {
            stmt.setInt(1, deliveryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String city = rs.getString("City");
                    String streetName = rs.getString("Street_Name");
                    String streetNumber = rs.getString("Street_Number");

                    return new Location(city, streetName, streetNumber);
                }
            }
        }

        return null;
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
