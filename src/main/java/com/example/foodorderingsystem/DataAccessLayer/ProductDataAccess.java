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

import com.example.foodorderingsystem.BusinessLayer.Category;
import com.example.foodorderingsystem.BusinessLayer.Product;
import com.example.foodorderingsystem.BusinessLayer.ProductImage;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;

public class ProductDataAccess {

    private final String url = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=Wasly;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "user=sa;"
            + "password=admin123;";

    private Connection connection;
    private RestaurantDataAccess restaurantDataAccess;
    private CategoryDataAccess categoryDataAccess;

    public ProductDataAccess() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to MSSQL successfully!");
            restaurantDataAccess = new RestaurantDataAccess();
            categoryDataAccess = new CategoryDataAccess();
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
            if (restaurantDataAccess != null) {
                restaurantDataAccess.closeConnection();
            }
            if (categoryDataAccess != null) {
                categoryDataAccess.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // INSERT operation using stored procedure
    public void insertProduct(Product product) throws SQLException {
        String callProc = "{call CreateProduct(?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            // Set parameters for the stored procedure
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getUnitPrice());
            stmt.setInt(3, product.getAmountInStock());
            stmt.setInt(4, product.getRestaurantId());
            stmt.setInt(5, product.getCategoryId());

            // Execute the stored procedure and get the result set
            try (ResultSet rs = stmt.executeQuery()) {
                // Retrieve the generated product ID from the result set
                if (rs.next()) {
                    int productId = rs.getInt("ProductID"); // Column name should match what's returned by the stored procedure
                    product.setProductId(productId);

                    // Add product images if present
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        for (ProductImage image : product.getImages()) {
                            image.setProductId(productId); // Ensure the image has the correct product ID
                            addProductImage(image);
                        }
                    }

                    System.out.println("Product inserted successfully with ID: " + productId);
                } else {

                    throw new SQLException("Failed to retrieve the product ID after insertion");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed  insertion" + product.getRestaurantId());
            e.printStackTrace();
            throw new SQLException("Error inserting product via stored procedure: " + e.getMessage(), e);
        }
    }

    // UPDATE operation using stored procedure
    public void updateProduct(Product product) throws SQLException {
        String callProc = "{call UpdateFullProduct(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            int productId = product.getProductId();

            // Set parameters for the stored procedure
            stmt.setInt(1, productId);
            stmt.setString(2, product.getName());
            stmt.setDouble(3, product.getUnitPrice());
            stmt.setInt(4, product.getAmountInStock());
            stmt.setInt(5, product.getCategoryId());
            stmt.setInt(6, product.getRestaurantId());
            stmt.execute();

            // Update product images (delete all and re-insert)
            deleteProductImages(productId);
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                for (ProductImage image : product.getImages()) {
                    image.setProductId(productId); // Ensure the image has the correct product ID
                    addProductImage(image);
                }
            }

            System.out.println("Product updated successfully via stored procedures.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating product via stored procedures: " + e.getMessage(), e);
        }
    }

    // DELETE operation using stored procedure
    public void deleteProduct(int productId) throws SQLException {
        String callProc = "{call DeleteProduct(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, productId);
            stmt.execute();
            System.out.println("Product deleted successfully via stored procedure.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error deleting product via stored procedure: " + e.getMessage(), e);
        }
    }

    // GET BY ID operation using stored procedure
    public Product getProductById(int productId) throws SQLException {
        String callProc = "{call GetProductWithImageById(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, productId);
            boolean hasResults = stmt.execute();

            if (!hasResults) {
                return null; // No product found
            }

            // Process first result set - Product details
            ResultSet productRs = stmt.getResultSet();
            if (!productRs.next()) {
                return null; // No product found
            }

            String name = productRs.getString("PName");
            double unitPrice = productRs.getDouble("Product_Unit_Price");
            int amountInStock = productRs.getInt("Product_Amount_Stock");
            int restaurantId = productRs.getInt("Restaurant_ID");
            int categoryId = productRs.getInt("Category_ID");

            // Fetch complete Restaurant and Category objects
            Restaurant restaurant = restaurantDataAccess.getRestaurant(restaurantId);
            Category category = categoryDataAccess.getCategoryById(categoryId);

            Product product = new Product(
                    productId,
                    name,
                    unitPrice,
                    amountInStock,
                    restaurant,
                    category
            );

            // Process second result set - Product Images
            if (stmt.getMoreResults()) {
                ResultSet imageRs = stmt.getResultSet();
                while (imageRs.next()) {
                    String imageName = imageRs.getString("Product_Image_Name");
                    String alt = imageRs.getString("ALT");
                    product.addImage(new ProductImage(imageName, productId, alt));
                }
            }

            return product;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving product by ID via stored procedure: " + e.getMessage(), e);
        }
    }

    // GET ALL operation
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String getProductsSQL = "SELECT Product_ID FROM Products";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getProductsSQL)) {

            while (rs.next()) {
                int productId = rs.getInt("Product_ID");
                Product product = getProductById(productId);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving all products: " + e.getMessage(), e);
        }

        return products;
    }

    // Get products by restaurant ID
    public List<Product> getProductsByRestaurant(int restaurantId) throws SQLException {
        String callProc = "{call GetAllProductsWithImages(?)}";
        List<Product> products = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, restaurantId);
            boolean hasResults = stmt.execute();

            if (hasResults) {
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    int productId = rs.getInt("Product_ID");
                    Product product = getProductById(productId);
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving products by restaurant ID: " + e.getMessage(), e);
        }

        return products;
    }

    // Helper method to delete all images for a product
    private void deleteProductImages(int productId) throws SQLException {
        String callProc = "{call DeleteProductImageC(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, productId);
            stmt.execute();
        }
    }

    // Helper method to add an image for a product
    private void addProductImage(ProductImage image) throws SQLException {
        String callProc = "{call CreateProductImage(?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setString(1, image.getImageName() + ".jpg");
            stmt.setInt(2, image.getProductId());
            stmt.setString(3, image.getAlt());

            // Execute the stored procedure and get the result set
            stmt.execute();
        }
    }

    // Add product to order with quantity
    public void addProductToOrder(int productId, int orderId, int quantity) throws SQLException {
        String callProc = "{call AddProductToOrder(?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, orderId);
            stmt.setInt(3, quantity);
            stmt.execute();
            System.out.println("Product added to order successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error adding product to order: " + e.getMessage(), e);
        }
    }

    // Add addProduct method to fix the error
    public void addProduct(Product product) throws SQLException {
        insertProduct(product);
    }
}
