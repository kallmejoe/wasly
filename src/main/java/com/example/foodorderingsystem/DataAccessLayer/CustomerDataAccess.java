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

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Location;

public class CustomerDataAccess {

    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;

    public CustomerDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to MSSQL successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE operation using stored procedure
    public void deleteCustomer(int customerId) throws SQLException {
        String callProc = "{call DeleteCustomer(?)}";

        String deletePhoneProc = "{call DeleteCustomerPhones(?)}";
        String deleteLocationProc = "{call DeleteCustomerLocations(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, customerId);
            // First delete all phone numbers
            deleteCustomerPhones(customerId);
            deleteCustomerLocations(customerId);
            stmt.execute();
            System.out.println("Customer and related data deleted successfully via stored procedure.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error deleting customer via stored procedure: " + e.getMessage(), e);
        }
    }

    // GET BY ID operation using stored procedure
    public Customer getCustomerById(int customerId) throws SQLException {
        String callProc = "{call GetCustomerByID(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, customerId);
            boolean hasResults = stmt.execute();

            if (!hasResults) {
                return null; // No customer found
            }

            // Process first result set - Customer details
            ResultSet customerRs = stmt.getResultSet();
            if (!customerRs.next()) {
                return null; // No customer found
            }

            String email = customerRs.getString("Email");
            String password = customerRs.getString("Password");
            String firstName = customerRs.getString("First_Name");
            String middleName = customerRs.getString("Middle_Name");
            String lastName = customerRs.getString("Last_Name");

            // Process second result set - Phone numbers
            List<String> phoneNumbers = new ArrayList<>();
            if (stmt.getMoreResults()) {
                ResultSet phoneRs = stmt.getResultSet();
                while (phoneRs.next()) {
                    phoneNumbers.add(phoneRs.getString("CPhoneNo"));
                }
            }

            // Process third result set - Locations
            List<Location> locations = new ArrayList<>();
            if (stmt.getMoreResults()) {
                ResultSet locationRs = stmt.getResultSet();
                while (locationRs.next()) {
                    String city = locationRs.getString("City");
                    String streetName = locationRs.getString("Street_Name");
                    String streetNumber = locationRs.getString("Street_Number");
                    locations.add(new Location(city, streetName, streetNumber));
                }
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
            customer.setCustomerId(customerId);
            return customer;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving customer by ID via stored procedure: " + e.getMessage(), e);
        }
    }

    // For backward compatibility
    public Customer getCustomer(int id) throws SQLException {
        return getCustomerById(id);
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

    // INSERT operation using stored procedure
    public void insertCustomer(Customer customer) throws SQLException {
        String callProc = "{call InsertFullCustomer(?, ?, ?, ?, ?)}";
        int customerId = -1;

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            // Set parameters for the stored procedure
            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getFirstName());

            // Middle name can be null
            if (customer.getMiddleName() == null || customer.getMiddleName().isEmpty()) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, customer.getMiddleName());
            }

            stmt.setString(5, customer.getLastName());

            // Execute the stored procedure
            stmt.execute();

            System.out.println("Customer basic info inserted successfully via stored procedure.");

            // Get the customer ID of the newly inserted customer
            String getIdSQL = "SELECT Customer_ID FROM Customer WHERE Email = ?";
            try (CallableStatement idStmt = connection.prepareCall(getIdSQL)) {
                idStmt.setString(1, customer.getEmail());
                ResultSet rs = idStmt.executeQuery();
                if (rs.next()) {
                    customerId = rs.getInt("Customer_ID");
                    customer.setCustomerId(customerId); // Update the customer object with the new ID
                } else {
                    throw new SQLException("Customer was inserted but ID could not be retrieved");
                }
            }

            // Now add phone numbers
            if (customerId > 0 && customer.getPhoneNumbers() != null && !customer.getPhoneNumbers().isEmpty()) {
                for (String phoneNumber : customer.getPhoneNumbers()) {
                    addCustomerPhone(customerId, phoneNumber);
                }
                System.out.println("Customer phone numbers added successfully.");
            }

            // Now add locations
            if (customerId > 0 && customer.getLocation() != null && !customer.getLocation().isEmpty()) {
                for (Location location : customer.getLocation()) {
                    addCustomerLocation(customerId, location);
                }
                System.out.println("Customer locations added successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting customer via stored procedure: " + e.getMessage(), e);
        }
    }

    // For backward compatibility
    public void insertCustomerWithDetails(Connection conn, Customer customer) throws SQLException {
        insertCustomer(customer);
    }

    // UPDATE operation using stored procedure
    public void updateCustomer(Customer customer) throws SQLException {
        String callProc = "{call UpdateCustomerInfo(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            int customerId = customer.getCustomerId();

            // Set parameters for the stored procedure
            stmt.setInt(1, customerId);
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            stmt.setString(4, customer.getFirstName());

            // Middle name can be null
            if (customer.getMiddleName() == null || customer.getMiddleName().isEmpty()) {
                stmt.setNull(5, Types.VARCHAR);
            } else {
                stmt.setString(5, customer.getMiddleName());
            }

            stmt.setString(6, customer.getLastName());

            // Execute the stored procedure for basic customer info
            stmt.execute();

            // Update phone numbers (delete all and re-insert)
            deleteCustomerPhones(customerId);
            for (String phoneNumber : customer.getPhoneNumbers()) {
                addCustomerPhone(customerId, phoneNumber);
            }

            // Update locations (delete all and re-insert)
            deleteCustomerLocations(customerId);
            for (Location location : customer.getLocation()) {
                addCustomerLocation(customerId, location);
            }

            System.out.println("Customer updated successfully via stored procedures.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating customer via stored procedures: " + e.getMessage(), e);
        }
    }

    // Helper method to delete all phone numbers for a customer
    private void deleteCustomerPhones(int customerId) throws SQLException {
        String callProc = "{call DeleteCustomerPhones(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, customerId);
            stmt.execute();
        }
    }

    // Helper method to add a phone number for a customer
    private void addCustomerPhone(int customerId, String phoneNumber) throws SQLException {
        String callProc = "{call AddCustomerPhone(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, phoneNumber);
            stmt.execute();
        }
    }

    // Helper method to delete all locations for a customer
    private void deleteCustomerLocations(int customerId) throws SQLException {
        String callProc = "{call DeleteCustomerLocations(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, customerId);
            stmt.execute();
        }
    }

    // Helper method to add a location for a customer
    public void addCustomerLocation(int customerId, Location location) throws SQLException {
        String callProc = "{call SetCustomerLocationI(?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, location.getCity());
            stmt.setString(3, location.getStreetName());
            stmt.setString(4, location.getStreetNumber());
            stmt.execute();
        }
    }

    // GET ALL operation - no stored procedure provided, using existing implementation
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String getCustomersSQL = "SELECT Customer_ID FROM Customer";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getCustomersSQL)) {

            while (rs.next()) {
                int customerId = rs.getInt("Customer_ID");
                Customer customer = getCustomerById(customerId);
                if (customer != null) {
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving all customers: " + e.getMessage(), e);
        }

        return customers;
    }

    // For backward compatibility
    public List<Customer> getAllCustomers(Connection conn) throws SQLException {
        return getAllCustomers();
    }

    /**
     * Validates a customer's login credentials
     * @param email The customer's email
     * @param password The customer's password
     * @return The Customer object if valid, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Customer validateCustomer(String email, String password) throws SQLException {
        String callProc = "{call ValidateLogin(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            boolean hasResults = stmt.execute();

            if (!hasResults) {
                return null; // No matching customer found
            }

            ResultSet rs = stmt.getResultSet();
            if (!rs.next()) {
                return null; // No matching customer found
            }

            int customerId = rs.getInt("Customer_ID");
            return getCustomerById(customerId);

        } catch (SQLException e) {
            System.err.println("Error validating customer login: " + e.getMessage());
            throw new SQLException("Error validating customer login: " + e.getMessage(), e);
        }
    }

    /**
     * Registers a new customer in the system
     * @param customer The customer to register
     * @return true if registration was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean registerCustomer(Customer customer) throws SQLException {
        try {
            insertCustomer(customer);
            return true;
        } catch (SQLException e) {
            System.err.println("Error registering customer: " + e.getMessage());
            return false;
        }
    }
}
