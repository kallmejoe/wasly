package com.example.foodorderingsystem.DataAccessLayer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;

public class RestaurantDataAccess {

    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;

    public RestaurantDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database successfully!");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public void insertRestaurant(Restaurant restaurant) throws SQLException {
        // Set auto-commit to false to start a transaction
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try {
            // Using AddRestaurant stored procedure
            String callAddRestaurant = "{call CreateRestaurant(?)}";

            try (CallableStatement stmt = connection.prepareCall(callAddRestaurant)) {
                // Set restaurant name parameter
                stmt.setString(1, restaurant.getName());

                // Execute the stored procedure
                stmt.execute();

                // Commit the restaurant creation immediately to ensure it's in the database
                connection.commit();

                // Instead of using getGeneratedKeys() which doesn't work with stored procedures,
                // we'll get the last inserted ID with a separate query
                try (java.sql.Statement idStmt = connection.createStatement();
                     ResultSet rs = idStmt.executeQuery("SELECT SCOPE_IDENTITY() AS Restaurant_ID")) {

                    if (rs.next()) {
                        int restaurantId = rs.getInt("Restaurant_ID");
                        restaurant.setRestaurantId(restaurantId);

                        // Verify the restaurant was actually created
                        try (java.sql.Statement verifyStmt = connection.createStatement();
                             ResultSet verifyRs = verifyStmt.executeQuery("SELECT COUNT(*) FROM Restaurant WHERE Restaurant_ID = " + restaurantId)) {

                            if (verifyRs.next() && verifyRs.getInt(1) > 0) {
                                // Restaurant exists, proceed with adding locations only
                                // Phone numbers are no longer added during restaurant creation

                                // Insert locations
                                for (Location location : restaurant.getLocations()) {
                                    addRestaurantLocation(restaurantId, location);
                                }

                                // Commit the entire transaction
                                connection.commit();
                                System.out.println("Restaurant inserted successfully with ID: " + restaurantId);
                            } else {
                                // Restaurant not found, roll back
                                connection.rollback();
                                throw new SQLException("Restaurant creation verification failed. No restaurant with ID " + restaurantId + " found.");
                            }
                        }
                    } else {
                        // No ID returned, roll back
                        connection.rollback();
                        throw new SQLException("Creating restaurant failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            // Roll back on any exception
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            throw new SQLException("Error saving restaurant: " + e.getMessage(), e);
        } finally {
            // Restore original auto-commit setting
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private void addRestaurantPhone(int restaurantId, String phoneNumber) throws SQLException {
        // Using AddRestaurantPhone stored procedure
        String callAddRestaurantPhone = "{call CreateRestPhoneNo(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callAddRestaurantPhone)) {
            stmt.setInt(1, restaurantId);
            stmt.setString(2, phoneNumber);
            stmt.execute();
        }
    }

    private void addRestaurantLocation(int restaurantId, Location location) throws SQLException {
        // Using UpsertRestaurantLocation stored procedure
        String callUpsertLocation = "{call CreateRestLocation(?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callUpsertLocation)) {
            stmt.setInt(1, restaurantId);
            stmt.setString(2, location.getCity());
            stmt.setString(3, location.getStreetName());
            stmt.setString(4, location.getStreetNumber());
            stmt.execute();
        }
    }

    public Restaurant getRestaurant(int restaurantId) throws SQLException {
        // First get the restaurant basic info
        String callGetAllRestaurants = "{call GetAllRestaurants()}";
        Restaurant restaurant = null;

        try (CallableStatement stmt = connection.prepareCall(callGetAllRestaurants);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Restaurant_ID");
                if (id == restaurantId) {
                    String name = rs.getString("Name");

                    // Get phone numbers
                    List<String> phoneNumbers = getRestaurantPhones(restaurantId);

                    // Get locations
                    List<Location> locations = getRestaurantLocations(restaurantId);

                    // Create restaurant object
                    restaurant = new Restaurant(name, locations, phoneNumbers);
                    restaurant.setRestaurantId(restaurantId);
                    break;
                }
            }
        }

        return restaurant;
    }

    private List<String> getRestaurantPhones(int restaurantId) throws SQLException {
        // Using GetRestaurantPhoneNumbers stored procedure
        String callGetPhones = "{call GetRestaurantPhoneNumbers(?)}";
        List<String> phoneNumbers = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetPhones)) {
            stmt.setInt(1, restaurantId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phoneNumbers.add(rs.getString("RestPhoneNo"));
                }
            }
        }

        return phoneNumbers;
    }

    private List<Location> getRestaurantLocations(int restaurantId) throws SQLException {
        // Using GetRestaurantLocation stored procedure
        String callGetLocation = "{call GetRestaurantLocations(?)}";
        List<Location> locations = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetLocation)) {
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

    public List<Restaurant> getAllRestaurants() throws SQLException {
        // Using GetAllRestaurants stored procedure
        String callGetAllRestaurants = "{call GetallRestaurants}";
        List<Restaurant> restaurants = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetAllRestaurants);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int restaurantId = rs.getInt("Restaurant_ID");
                String name = rs.getString("Name");

                // Get phone numbers
                List<String> phoneNumbers = getRestaurantPhones(restaurantId);

                // Get locations
                List<Location> locations = getRestaurantLocations(restaurantId);

                // Create restaurant object
                Restaurant restaurant = new Restaurant(name, locations, phoneNumbers);
                restaurant.setRestaurantId(restaurantId);
                restaurants.add(restaurant);
            }
        }

        return restaurants;
    }

    public List<Restaurant> searchRestaurants(String searchTerm) throws SQLException {
        List<Restaurant> allRestaurants = getAllRestaurants();
        List<Restaurant> matchingRestaurants = new ArrayList<>();

        // Case-insensitive search in restaurant names
        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                matchingRestaurants.add(restaurant);
                continue;
            }

            // Search in locations (cities and streets)
            for (Location location : restaurant.getLocations()) {
                if (location.getCity().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    location.getStreetName().toLowerCase().contains(searchTerm.toLowerCase())) {
                    matchingRestaurants.add(restaurant);
                    break;
                }
            }
        }

        return matchingRestaurants;
    }

    public Restaurant getRestaurantByID(int restaurantId) throws SQLException {
        return getRestaurant(restaurantId);
    }

    public boolean updateRestaurant(Restaurant restaurant) throws SQLException {
        // Using UpdateRestaurantName stored procedure
        String callUpdateRestaurantName = "{call UpdateRestaurantName(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callUpdateRestaurantName)) {
            stmt.setInt(1, restaurant.getRestaurantId());
            stmt.setString(2, restaurant.getName());
            stmt.execute();

            int restaurantId = restaurant.getRestaurantId();

            // Update phone numbers (delete all and re-insert)
            deleteAllRestaurantPhones(restaurantId);
            for (String phone : restaurant.getPhoneNo()) {
                addRestaurantPhone(restaurantId, phone);
            }

            // Update locations (delete all and re-insert)
            deleteRestaurantLocation(restaurantId);
            for (Location location : restaurant.getLocations()) {
                addRestaurantLocation(restaurantId, location);
            }

            System.out.println("Restaurant updated successfully.");
            return true;
        }
    }

    private void deleteAllRestaurantPhones(int restaurantId) throws SQLException {
        // We'll delete each phone number individually since there's no procedure to delete all at once
        List<String> phoneNumbers = getRestaurantPhones(restaurantId);
        for (String phone : phoneNumbers) {
            deleteRestaurantPhone(restaurantId, phone);
        }
    }

    private void deleteRestaurantPhone(int restaurantId, String phoneNumber) throws SQLException {
        // Using DeleteRestaurantPhone stored procedure
        String callDeletePhone = "{call DeleteRestaurantPhone(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeletePhone)) {
            stmt.setInt(1, restaurantId);
            stmt.setString(2, phoneNumber);
            stmt.execute();
        }
    }

    private void deleteRestaurantLocation(int restaurantId) throws SQLException {
        // Using DeleteRestaurantLocation stored procedure
        String callDeleteLocation = "{call DeleteRestaurantLocation(?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeleteLocation)) {
            stmt.setInt(1, restaurantId);
            stmt.execute();
        }
    }

    public void deleteRestaurant(int restaurantId) throws SQLException {
        // First delete all phone numbers
        deleteAllRestaurantPhones(restaurantId);

        // Then delete all locations
        deleteRestaurantLocation(restaurantId);

        // Finally delete the restaurant using DeleteRestaurant stored procedure
        String callDeleteRestaurant = "{call DeleteRestaurant(?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeleteRestaurant)) {
            stmt.setInt(1, restaurantId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " restaurant record(s) deleted along with related data.");
        }
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
