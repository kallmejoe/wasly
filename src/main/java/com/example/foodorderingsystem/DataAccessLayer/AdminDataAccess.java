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

import com.example.foodorderingsystem.BusinessLayer.Admin;

public class AdminDataAccess {

    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;

    public AdminDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to MSSQL successfully!");
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

    // INSERT operation using stored procedure
    public void insertAdmin(Admin admin) throws SQLException {
        // Check if at least one phone number exists
        if (admin.getPhoneNumbers().isEmpty()) {
            throw new SQLException("Admin must have at least one phone number");
        }

        String callProc = "{call InsertFullAdmin(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            // Set parameters for the stored procedure
            stmt.setString(1, admin.getEmail());
            stmt.setString(2, admin.getPassword());
            stmt.setString(3, admin.getFirstName());

            // Middle name can be null
            if (admin.getMiddleName() == null || admin.getMiddleName().isEmpty()) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, admin.getMiddleName());
            }

            stmt.setString(5, admin.getLastName());
            stmt.setDouble(6, admin.getSalary());

            // Use the first phone number
            stmt.setString(7, admin.getPhoneNumbers().get(0));

            // Execute the stored procedure
            stmt.execute();

            // Get the generated admin ID
            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT MAX(Admin_ID) AS Admin_ID FROM Admin")) {
                if (rs.next()) {
                    int adminId = rs.getInt("Admin_ID");
                    admin.setAdminId(adminId);

                    // Add additional phone numbers if present
                    for (int i = 1; i < admin.getPhoneNumbers().size(); i++) {
                        addAdminPhone(adminId, admin.getPhoneNumbers().get(i));
                    }

                    // Add restaurant associations if present
                    if (admin.getRestaurantsManaged() != null && !admin.getRestaurantsManaged().isEmpty()) {
                        for (Integer restaurantId : admin.getRestaurantsManaged()) {
                            addAdminRestaurant(adminId, restaurantId);
                        }
                    }
                }
            }

            System.out.println("Admin inserted successfully via stored procedure.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting admin via stored procedure: " + e.getMessage(), e);
        }
    }

    // UPDATE operation using stored procedure
    public void updateAdmin(Admin admin) throws SQLException {
        String callProc = "{call UpdateAdmin(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            int adminId = admin.getAdminId();

            // Set parameters for the stored procedure
            stmt.setInt(1, adminId);
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, admin.getPassword());
            stmt.setString(4, admin.getFirstName());

            // Middle name can be null
            if (admin.getMiddleName() == null || admin.getMiddleName().isEmpty()) {
                stmt.setNull(5, Types.VARCHAR);
            } else {
                stmt.setString(5, admin.getMiddleName());
            }

            stmt.setString(6, admin.getLastName());
            stmt.setDouble(7, admin.getSalary());

            // Execute the stored procedure for basic admin info
            stmt.execute();

            // Update phone numbers (delete all and re-insert)
            deleteAdminPhones(adminId);
            for (String phoneNumber : admin.getPhoneNumbers()) {
                addAdminPhone(adminId, phoneNumber);
            }

            // Update restaurant associations (delete all and re-insert)
            deleteAdminRestaurants(adminId);
            if (admin.getRestaurantsManaged() != null && !admin.getRestaurantsManaged().isEmpty()) {
                for (Integer restaurantId : admin.getRestaurantsManaged()) {
                    addAdminRestaurant(adminId, restaurantId);
                }
            }

            System.out.println("Admin updated successfully via stored procedures.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating admin via stored procedures: " + e.getMessage(), e);
        }
    }

    // DELETE operation using stored procedure
    public void deleteAdmin(int adminId) throws SQLException {
        String callProc = "{call DeleteFullAdmin(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, adminId);
            stmt.execute();
            System.out.println("Admin and related data deleted successfully via stored procedure.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error deleting admin via stored procedure: " + e.getMessage(), e);
        }
    }

    // GET BY ID operation using stored procedure
    public Admin getAdminById(int adminId) throws SQLException {
        String callProc = "{call GetAdminFullDetails(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, adminId);
            boolean hasResults = stmt.execute();

            if (!hasResults) {
                return null; // No admin found
            }

            // Process first result set - Admin details
            ResultSet adminRs = stmt.getResultSet();
            if (!adminRs.next()) {
                return null; // No admin found
            }

            String email = adminRs.getString("Email");
            String password = adminRs.getString("Password");
            String firstName = adminRs.getString("First_Name");
            String middleName = adminRs.getString("Middle_Name");
            String lastName = adminRs.getString("Last_Name");
            double salary = adminRs.getDouble("Salary");

            // Process second result set - Phone numbers
            List<String> phoneNumbers = new ArrayList<>();
            if (stmt.getMoreResults()) {
                ResultSet phoneRs = stmt.getResultSet();
                while (phoneRs.next()) {
                    phoneNumbers.add(phoneRs.getString("AdminPhoneNo"));
                }
            }

            // Process third result set - Restaurants managed
            List<Integer> restaurantsManaged = new ArrayList<>();
            if (stmt.getMoreResults()) {
                ResultSet restaurantRs = stmt.getResultSet();
                while (restaurantRs.next()) {
                    restaurantsManaged.add(restaurantRs.getInt("Restaurant_ID"));
                }
            }

            Admin admin = new Admin(
                    email,
                    password,
                    firstName,
                    middleName,
                    lastName,
                    adminId,
                    salary,
                    phoneNumbers
            );
            admin.setRestaurantsManaged(restaurantsManaged);
            return admin;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving admin by ID via stored procedure: " + e.getMessage(), e);
        }
    }

    // GET ALL operation
    public List<Admin> getAllAdmins() throws SQLException {
        List<Admin> admins = new ArrayList<>();
        String getAdminsSQL = "SELECT Admin_ID FROM Admin";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getAdminsSQL)) {

            while (rs.next()) {
                int adminId = rs.getInt("Admin_ID");
                Admin admin = getAdminById(adminId);
                if (admin != null) {
                    admins.add(admin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving all admins: " + e.getMessage(), e);
        }

        return admins;
    }

    // Helper method to delete all phone numbers for an admin
    private void deleteAdminPhones(int adminId) throws SQLException {
        String callProc = "{call DeleteAdminPhones(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, adminId);
            stmt.execute();
        }
    }

    // Helper method to add a phone number for an admin
    private void addAdminPhone(int adminId, String phoneNumber) throws SQLException {
        String callProc = "{call AddAdminPhone(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, adminId);
            stmt.setString(2, phoneNumber);
            stmt.execute();
        }
    }

    // Helper method to delete all restaurant associations for an admin
    private void deleteAdminRestaurants(int adminId) throws SQLException {
        String callProc = "{call DeleteAdminRestaurants(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, adminId);
            stmt.execute();
        }
    }

    // Helper method to add a restaurant association for an admin
    private void addAdminRestaurant(int adminId, int restaurantId) throws SQLException {
        String callProc = "{call AddAdminRestaurant(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, adminId);
            stmt.setInt(2, restaurantId);
            stmt.execute();
        }
    }
}
