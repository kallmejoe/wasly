package com.example.foodorderingsystem.DataAccessLayer;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDataAccess {

    String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;

    public CustomerDataAccess() {
        try {
            // Use only the connection string since Windows Authentication does not need a username/password
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to MSSQL successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(Connection conn, int id) throws SQLException {
        String deletePhones = "DELETE FROM Cust_PhoneNo WHERE Customer_ID = ?";
        String deleteLocations = "DELETE FROM Cust_Location WHERE Customer_ID = ?";
        String deleteCustomer = "DELETE FROM Customer WHERE Customer_ID = ?";

        try (PreparedStatement phoneStmt = conn.prepareStatement(deletePhones);
             PreparedStatement locationStmt = conn.prepareStatement(deleteLocations);
             PreparedStatement customerStmt = conn.prepareStatement(deleteCustomer)) {

            phoneStmt.setInt(1, id);
            phoneStmt.executeUpdate();

            locationStmt.setInt(1, id);
            locationStmt.executeUpdate();

            customerStmt.setInt(1, id);
            customerStmt.executeUpdate();

            System.out.println("Customer and related data deleted successfully.");
        }
    }

    public Customer getCustomer(int id) throws SQLException {
        String getCustomerSQL = "SELECT * FROM Customer WHERE Customer_ID = ?";
        String getPhoneNumbersSQL = "SELECT CPhoneNo FROM Cust_PhoneNo WHERE Customer_ID = ?";
        String getLocationsSQL = "SELECT City, Street_Name, Street_Number FROM Cust_Location WHERE Customer_ID = ?";

        try (PreparedStatement customerStmt = connection.prepareStatement(getCustomerSQL);
             PreparedStatement phoneStmt = connection.prepareStatement(getPhoneNumbersSQL);
             PreparedStatement locationStmt = connection.prepareStatement(getLocationsSQL)) {

            customerStmt.setInt(1, id);
            ResultSet customerRs = customerStmt.executeQuery();

            if (!customerRs.next()) {
                return null; // No customer found
            }

            String email = customerRs.getString("Email");
            String password = customerRs.getString("Password");
            String firstName = customerRs.getString("First_Name");
            String middleName = customerRs.getString("Middle_Name");
            String lastName = customerRs.getString("Last_Name");

            // Get phone numbers
            phoneStmt.setInt(1, id);
            ResultSet phoneRs = phoneStmt.executeQuery();
            List<String> phoneNumbers = new ArrayList<>();
            while (phoneRs.next()) {
                phoneNumbers.add(phoneRs.getString("CPhoneNo"));
            }

            // Get locations
            locationStmt.setInt(1, id);
            ResultSet locationRs = locationStmt.executeQuery();
            List<Location> locations = new ArrayList<>();
            while (locationRs.next()) {
                String city = locationRs.getString("City");
                String streetName = locationRs.getString("Street_Name");
                String streetNumber = locationRs.getString("Street_Number");
                locations.add(new Location(city, streetName, streetNumber));
            }

            Customer customer = new Customer(
                    email,
                    password,
                    firstName,
                    middleName,
                    lastName,
                    phoneNumbers,
                    locations
            );
            customer.setCustomerId(id);

            return customer;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving customer by ID: " + e.getMessage(), e);
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
    public void insertCustomerWithDetails(Connection conn, Customer customer) throws SQLException {
        String insertCustomerSQL = "INSERT INTO Customer (Email, Password, First_Name, Middle_Name, Last_Name) " +
                "VALUES (?, ?, ?, ?, ?)";
        String insertPhoneSQL = "INSERT INTO Cust_PhoneNo (Customer_ID, CPhoneNo) VALUES (?, ?)";
        String insertLocationSQL = "INSERT INTO Cust_Location (Customer_ID, City, Street_Name, Street_Number) VALUES (?, ?, ?, ?)";

        try (PreparedStatement customerStmt = conn.prepareStatement(insertCustomerSQL, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement phoneStmt = conn.prepareStatement(insertPhoneSQL);
             PreparedStatement locationStmt = conn.prepareStatement(insertLocationSQL)) {

            // Insert Customer
            customerStmt.setString(1, customer.getEmail());
            customerStmt.setString(2, customer.getPassword());
            customerStmt.setString(3, customer.getFirstName());
            customerStmt.setString(4, customer.getMiddleName());
            customerStmt.setString(5, customer.getLastName());
            int affectedRows = customerStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Inserting customer failed, no rows affected.");
            }

            // Get the generated customerId
            ResultSet generatedKeys = customerStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int customerId = generatedKeys.getInt(1); // Get the generated Customer_ID

                // Insert Phone Numbers
                for (String phoneNo : customer.getPhoneNumbers()) {
                    phoneStmt.setInt(1, customerId);
                    phoneStmt.setString(2, phoneNo);
                    phoneStmt.executeUpdate();
                }

                // Insert Locations
                for (Location loc : customer.getLocation()) {
                    locationStmt.setInt(1, customerId);
                    locationStmt.setString(2, loc.getCity());
                    locationStmt.setString(3, loc.getStreetName());
                    locationStmt.setString(4, loc.getStreetNumber());
                    locationStmt.executeUpdate();
                }

                System.out.println("Customer and associated data inserted successfully!");
            } else {
                throw new SQLException("Inserting customer failed, no ID obtained.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting customer and details: " + e.getMessage(), e);
        }
    }

    public void updateCustomer(Customer customer) {
        String updateCustomerSQL = "UPDATE Customer SET First_Name = ?, Middle_Name = ?, Last_Name = ?, Email = ? WHERE Customer_ID = ?";
        String deletePhonesSQL = "DELETE FROM Cust_PhoneNo WHERE Customer_ID = ?";
        String insertPhoneSQL = "INSERT INTO Cust_PhoneNo (Customer_ID, CPhoneNo) VALUES (?, ?)";
        String deleteLocationsSQL = "DELETE FROM Cust_Location WHERE Customer_ID = ?";
        String insertLocationSQL = "INSERT INTO Cust_Location (Customer_ID, City, Street_Name, Street_Number) VALUES (?, ?, ?, ?)";

        try (
                PreparedStatement updateCustomerStmt = connection.prepareStatement(updateCustomerSQL);
                PreparedStatement deletePhonesStmt = connection.prepareStatement(deletePhonesSQL);
                PreparedStatement insertPhoneStmt = connection.prepareStatement(insertPhoneSQL);
                PreparedStatement deleteLocationsStmt = connection.prepareStatement(deleteLocationsSQL);
                PreparedStatement insertLocationStmt = connection.prepareStatement(insertLocationSQL)
        ) {
            int customerId = customer.getCustomerId(); // Assuming this getter exists

            // Update Customer base info
            updateCustomerStmt.setString(1, customer.getFirstName());
            updateCustomerStmt.setString(2, customer.getMiddleName());
            updateCustomerStmt.setString(3, customer.getLastName());
            updateCustomerStmt.setString(4, customer.getEmail());
            updateCustomerStmt.setInt(5, customerId);
            updateCustomerStmt.executeUpdate();

            // Replace Phone Numbers
            deletePhonesStmt.setInt(1, customerId);
            deletePhonesStmt.executeUpdate();
            for (String phone : customer.getPhoneNumbers()) {
                insertPhoneStmt.setInt(1, customerId);
                insertPhoneStmt.setString(2, phone);
                insertPhoneStmt.executeUpdate();
            }

            // Replace Locations
            deleteLocationsStmt.setInt(1, customerId);
            deleteLocationsStmt.executeUpdate();
            for (Location loc : customer.getLocation()) {
                insertLocationStmt.setInt(1, customerId);
                insertLocationStmt.setString(2, loc.getCity());
                insertLocationStmt.setString(3, loc.getStreetName());
                insertLocationStmt.setString(4, loc.getStreetNumber());
                insertLocationStmt.executeUpdate();
            }

            System.out.println("Customer, phone numbers, and all locations updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public List<Customer> getAllCustomers(Connection conn) throws SQLException {
        String getCustomerSQL = "SELECT * FROM Customer";
        String getPhoneNumbersSQL = "SELECT CPhoneNo FROM Cust_PhoneNo WHERE Customer_ID = ?";
        String getLocationsSQL = "SELECT City, Street_Name, Street_Number FROM Cust_Location WHERE Customer_ID = ?";

        List<Customer> customers = new ArrayList<>();

        try (Statement customerStmt = conn.createStatement();
             PreparedStatement phoneStmt = conn.prepareStatement(getPhoneNumbersSQL);
             PreparedStatement locationStmt = conn.prepareStatement(getLocationsSQL);
             ResultSet customerRs = customerStmt.executeQuery(getCustomerSQL)) {

            // Loop through each customer
            while (customerRs.next()) {
                int customerId = customerRs.getInt("Customer_ID");
                String email = customerRs.getString("Email");
                String password = customerRs.getString("Password");
                String firstName = customerRs.getString("First_Name");
                String middleName = customerRs.getString("Middle_Name");
                String lastName = customerRs.getString("Last_Name");

                // Get phone numbers for the customer
                phoneStmt.setInt(1, customerId);
                ResultSet phoneRs = phoneStmt.executeQuery();
                List<String> phoneNumbers = new ArrayList<>();
                while (phoneRs.next()) {
                    phoneNumbers.add(phoneRs.getString("CPhoneNo"));
                }

                // Get locations for the customer
                locationStmt.setInt(1, customerId);
                ResultSet locationRs = locationStmt.executeQuery();
                List<Location> locations = new ArrayList<>();
                while (locationRs.next()) {
                    String city = locationRs.getString("City");
                    String streetName = locationRs.getString("Street_Name");
                    String streetNumber = locationRs.getString("Street_Number");
                    locations.add(new Location(city, streetName, streetNumber));
                }

                // Create a new Customer object with the retrieved data
                Customer customer = new Customer(
                        email,
                        password,
                        firstName,
                        middleName,
                        lastName,
                        phoneNumbers,
                        locations
                );
                customer.setCustomerId(customerId);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving customers: " + e.getMessage(), e);
        }

        return customers;
    }
}
