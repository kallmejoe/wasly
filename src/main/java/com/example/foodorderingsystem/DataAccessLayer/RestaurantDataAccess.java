package com.example.foodorderingsystem.DataAccessLayer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not established.");
        }

        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try {
            String callAddRestaurant = "{call CreateRestaurant(?)}";

            try (CallableStatement stmt = connection.prepareCall(callAddRestaurant)) {
                stmt.setString(1, restaurant.getName());
                stmt.execute();
            }

            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT SCOPE_IDENTITY() AS Restaurant_ID")) {

                if (rs.next()) {
                    int restaurantId = rs.getInt("Restaurant_ID");
                    restaurant.setRestaurantId(restaurantId);

                    try (Statement verifyStmt = connection.createStatement();
                         ResultSet verifyRs = verifyStmt.executeQuery(
                                 "SELECT COUNT(*) FROM Restaurant WHERE Restaurant_ID = " + restaurantId)) {

                        if (verifyRs.next() && verifyRs.getInt(1) > 0) {
                            for (Location location : restaurant.getLocations()) {
                                addRestaurantLocation(restaurantId, location);
                            }
                            connection.commit();
                            System.out.println("Restaurant inserted successfully with ID: " + restaurantId);
                        } else {
                            connection.rollback();
                            throw new SQLException("Verification failed. No restaurant with ID " + restaurantId);
                        }
                    }
                } else {
                    connection.rollback();
                    throw new SQLException("Creating restaurant failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error saving restaurant: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private void addRestaurantPhone(int restaurantId, String phoneNumber) throws SQLException {
        String callAddRestaurantPhone = "{call CreateRestPhoneNo(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callAddRestaurantPhone)) {
            stmt.setInt(1, restaurantId);
            stmt.setString(2, phoneNumber);
            stmt.execute();
        }
    }

    private void addRestaurantLocation(int restaurantId, Location location) throws SQLException {
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
        String callGetAllRestaurants = "{call GetAllRestaurants()}";
        Restaurant restaurant = null;

        try (CallableStatement stmt = connection.prepareCall(callGetAllRestaurants);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                if (rs.getInt("Restaurant_ID") == restaurantId) {
                    String name = rs.getString("Name");
                    List<String> phones = getRestaurantPhones(restaurantId);
                    List<Location> locations = getRestaurantLocations(restaurantId);
                    restaurant = new Restaurant(name, locations, phones);
                    restaurant.setRestaurantId(restaurantId);
                    break;
                }
            }
        }

        return restaurant;
    }

    private List<String> getRestaurantPhones(int restaurantId) throws SQLException {
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
        String callGetLocation = "{call GetRestaurantLocations(?)}";
        List<Location> locations = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetLocation)) {
            stmt.setInt(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    locations.add(new Location(
                            rs.getString("City"),
                            rs.getString("Street_Name"),
                            rs.getString("Street_Number")
                    ));
                }
            }
        }

        return locations;
    }

    public List<Restaurant> getAllRestaurants() throws SQLException {
        String callGetAllRestaurants = "{call GetAllRestaurants()}"; // fixed typo
        List<Restaurant> restaurants = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callGetAllRestaurants);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Restaurant_ID");
                String name = rs.getString("Name");
                List<String> phones = getRestaurantPhones(id);
                List<Location> locations = getRestaurantLocations(id);
                Restaurant restaurant = new Restaurant(name, locations, phones);
                restaurant.setRestaurantId(id);
                restaurants.add(restaurant);
            }
        }

        return restaurants;
    }

    public List<Restaurant> searchRestaurants(String searchTerm) throws SQLException {
        List<Restaurant> allRestaurants = getAllRestaurants();
        List<Restaurant> result = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();

        for (Restaurant restaurant : allRestaurants) {
            // Check restaurant name
            if (restaurant.getName() != null &&
                restaurant.getName().toLowerCase().contains(lowerSearchTerm)) {
                result.add(restaurant);
                continue;
            }

            // Check restaurant locations
            if (restaurant.getLocations() != null) {
                boolean locationMatch = false;

                for (Location loc : restaurant.getLocations()) {
                    // Safely check city and street name for null before calling toLowerCase()
                    if ((loc.getCity() != null &&
                         loc.getCity().toLowerCase().contains(lowerSearchTerm)) ||
                        (loc.getStreetName() != null &&
                         loc.getStreetName().toLowerCase().contains(lowerSearchTerm))) {
                        locationMatch = true;
                        break;
                    }
                }

                if (locationMatch) {
                    result.add(restaurant);
                }
            }
        }

        return result;
    }

    public Restaurant getRestaurantByID(int restaurantId) throws SQLException {
        return getRestaurant(restaurantId);
    }

    public boolean updateRestaurant(Restaurant restaurant) throws SQLException {
        String callUpdateRestaurantName = "{call UpdateRestaurant(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callUpdateRestaurantName)) {
            stmt.setInt(1, restaurant.getRestaurantId());
            stmt.setString(2, restaurant.getName());
            stmt.execute();

            int id = restaurant.getRestaurantId();

            deleteAllRestaurantPhones(id);
            for (String phone : restaurant.getPhoneNo()) {
                addRestaurantPhone(id, phone);
            }

            deleteRestaurantLocation(id);
            for (Location location : restaurant.getLocations()) {
                addRestaurantLocation(id, location);
            }

            System.out.println("Restaurant updated successfully.");
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating restaurant: " + e.getMessage());
            return false;
        }
    }

    private void deleteAllRestaurantPhones(int restaurantId) throws SQLException {
        String callDeletePhones = "{call DeleteRestaurantPhoneNumber(?)}"; // Assumes procedure deletes all for ID

        try (CallableStatement stmt = connection.prepareCall(callDeletePhones)) {
            stmt.setInt(1, restaurantId);
            stmt.execute();
        }
    }

    private void deleteRestaurantPhone(int restaurantId, String phoneNumber) throws SQLException {
        String callDeletePhone = "{call DeleteRestaurantPhone(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeletePhone)) {
            stmt.setInt(1, restaurantId);
            stmt.setString(2, phoneNumber);
            stmt.execute();
        }
    }

    private void deleteRestaurantLocation(int restaurantId) throws SQLException {
        String callDeleteLocation = "{call DeleteRestaurantLocations(?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeleteLocation)) {
            stmt.setInt(1, restaurantId);
            stmt.execute();
        }
    }

    public void deleteRestaurant(int restaurantId) throws SQLException {
        deleteAllRestaurantPhones(restaurantId);
        deleteRestaurantLocation(restaurantId);

        String callDeleteRestaurant = "{call DeleteRestaurant(?)}";

        try (CallableStatement stmt = connection.prepareCall(callDeleteRestaurant)) {
            stmt.setInt(1, restaurantId);
            int rows = stmt.executeUpdate();
            System.out.println(rows + " restaurant record(s) deleted.");
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
