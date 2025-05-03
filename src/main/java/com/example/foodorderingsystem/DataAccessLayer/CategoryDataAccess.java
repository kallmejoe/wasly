package com.example.foodorderingsystem.DataAccessLayer;

import com.example.foodorderingsystem.BusinessLayer.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    // New version of createCategory that includes parent category
    public boolean insertCategory(Category category, int parentCategoryId) throws SQLException {
        String callProc = "{call CreateCategory(?)}";
        boolean success = false;

        try {
            connection.setAutoCommit(false);

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

                        // If a parent category was specified, create the relationship
                        if (parentCategoryId > 0) {
                            setParentCategory(categoryId, parentCategoryId);
                        }

                        connection.commit();
                        success = true;
                    }
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error creating category with parent: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        return success;
    }

    // Alias for createCategory to match the naming convention expected in error messages
    public void insertCategory(Category category) throws SQLException {
        createCategory(category);
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
    public void updateCategory(Category category) throws SQLException {
        String callProc = "{call UpdateCategory(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, category.getCategoryId());
            stmt.setString(2, category.getName());

            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " category updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            throw e;
        }
    }

    // New version of updateCategory that includes parent category
    public boolean updateCategory(Category category, int parentCategoryId) throws SQLException {
        boolean success = false;

        try {
            connection.setAutoCommit(false);

            // Update the basic category information (name)
            String callProc = "{call UpdateCategory(?, ?)}";
            try (CallableStatement stmt = connection.prepareCall(callProc)) {
                stmt.setInt(1, category.getCategoryId());
                stmt.setString(2, category.getName());
                stmt.executeUpdate();
            }

            // Update the parent category if needed
            int currentParentId = getParentCategoryId(category.getCategoryId());
            if (currentParentId != parentCategoryId) {
                // Remove existing parent relationship if any
                if (currentParentId > 0) {
                    removeParentCategory(category.getCategoryId());
                }

                // Set new parent if not 0
                if (parentCategoryId > 0) {
                    setParentCategory(category.getCategoryId(), parentCategoryId);
                }
            }

            connection.commit();
            success = true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error updating category with parent: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        return success;
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

    // Helper method to set a category's description
    private void updateCategoryDescription(int categoryId, String description) throws SQLException {
        String sql = "UPDATE Category SET Description = ? WHERE Category_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, description);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    // Helper method to set parent-child relationship between categories
    private void setParentCategory(int childCategoryId, int parentCategoryId) throws SQLException {
        String sql = "INSERT INTO CategoryHierarchy (Parent_Category_ID, Child_Category_ID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, parentCategoryId);
            stmt.setInt(2, childCategoryId);
            stmt.executeUpdate();
        }
    }

    // Helper method to remove parent-child relationship
    private void removeParentCategory(int childCategoryId) throws SQLException {
        String sql = "DELETE FROM CategoryHierarchy WHERE Child_Category_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, childCategoryId);
            stmt.executeUpdate();
        }
    }

    // Helper method to get the parent category ID for a child category
    public int getParentCategoryId(int childCategoryId) throws SQLException {
        String sql = "SELECT Parent_Category_ID FROM CategoryHierarchy WHERE Child_Category_ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, childCategoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Parent_Category_ID");
            }
        }
        return 0; // No parent category found
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
