package com.example.foodorderingsystem.DataAccessLayer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.foodorderingsystem.BusinessLayer.Category;

public class CategoryDataAccess {

    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;

    public CategoryDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database successfully for CategoryDataAccess!");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    // CREATE operation using CreateCategory stored procedure
    public void createCategory(Category category) throws SQLException {
        String callProc = "{call CreateCategory(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setString(1, category.getName());
            stmt.execute();

            // Get the generated ID for the new category
            String getIdSQL = "SELECT Category_ID FROM Category WHERE Cat_Name = ?";
            try (PreparedStatement idStmt = connection.prepareStatement(getIdSQL)) {
                idStmt.setString(1, category.getName());
                ResultSet rs = idStmt.executeQuery();
                if (rs.next()) {
                    int categoryId = rs.getInt("Category_ID");
                    category.setCategoryId(categoryId);
                    System.out.println("Category created successfully with ID: " + categoryId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating category: " + e.getMessage());
            throw e;
        }
    }

    // Simplified insertCategory method without parent category relationship
    public boolean insertCategory(Category category) throws SQLException {
        try {
            createCategory(category);
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting category: " + e.getMessage());
            throw e;
        }
    }

    // Add addCategory method to fix the error
    public void addCategory(Category category) throws SQLException {
        createCategory(category);
    }

    // READ operation using GetAllCategories stored procedure
    public List<Category> getAllCategories() throws SQLException {
        String callProc = "{call GetAllCategories}";
        List<Category> categories = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callProc);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int categoryId = rs.getInt("Category_ID");
                String name = rs.getString("Cat_Name");

                Category category = new Category(categoryId, name);
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all categories: " + e.getMessage());
            throw e;
        }

        return categories;
    }

    // READ operation by ID
    public Category getCategoryById(int categoryId) throws SQLException {
        // Since there's no GetCategoryById stored procedure, we'll filter from GetAllCategories
        String callProc = "{call GetAllCategories}";

        try (CallableStatement stmt = connection.prepareCall(callProc);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Category_ID");
                if (id == categoryId) {
                    String name = rs.getString("Cat_Name");
                    return new Category(categoryId, name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving category by ID: " + e.getMessage());
            throw e;
        }

        return null; // Category not found
    }

    // UPDATE operation using UpdateCategory stored procedure
    public boolean updateCategory(Category category) throws SQLException {
        String callProc = "{call UpdateCategory(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, category.getCategoryId());
            stmt.setString(2, category.getName());

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " category updated successfully.");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            throw e;
        }
    }

    // DELETE operation using DeleteCategory stored procedure
    public void deleteCategory(int categoryId) throws SQLException {
        String callProc = "{call DeleteCategory(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, categoryId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " category deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            throw e;
        }
    }

    // Close database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed for CategoryDataAccess.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
