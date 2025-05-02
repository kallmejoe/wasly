package com.example.foodorderingsystem.DataAccessLayer;

import java.math.BigDecimal;
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
        String callProc = "{call CreateProductWithImage(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            // Set parameters for the stored procedure
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getUnitPrice());
            stmt.setInt(3, product.getAmountInStock());

            // Use getRestaurantId and getCategoryId which now extract IDs from objects
            stmt.setInt(4, product.getRestaurantId());
            stmt.setInt(5, product.getCategoryId());

            // Register output parameter for the generated product ID
            stmt.registerOutParameter(6, Types.INTEGER);

            // Execute the stored procedure
            stmt.execute();

            // Get the generated product ID
            int productId = stmt.getInt(6);
            product.setProductId(productId);

            // Add product images if present
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                for (ProductImage image : product.getImages()) {
                    image.setProductId(productId); // Ensure the image has the correct product ID
                    addProductImage(image);
                }
            }

            System.out.println("Product inserted successfully via stored procedure with ID: " + productId);
        } catch (SQLException e) {
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
            stmt.setBigDecimal(3, product.getUnitPrice());
            stmt.setInt(4, product.getAmountInStock());

            // Use getRestaurantId and getCategoryId which now extract IDs from objects
            stmt.setInt(5, product.getRestaurantId());
            stmt.setInt(6, product.getCategoryId());

            // Execute the stored procedure for basic product info
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
        String callProc = "{call GetProductFullDetails(?)}";

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
            BigDecimal unitPrice = productRs.getBigDecimal("Product_Unit_Price");
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
        String callProc = "{call GetProductsByRestaurant(?)}";
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

    // Get products by category ID
    public List<Product> getProductsByCategory(int categoryId) throws SQLException {
        String callProc = "{call GetProductsByCategory(?)}";
        List<Product> products = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, categoryId);
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
            throw new SQLException("Error retrieving products by category ID: " + e.getMessage(), e);
        }

        return products;
    }

    // Helper method to delete all images for a product
    private void deleteProductImages(int productId) throws SQLException {
        String callProc = "{call DeleteProductImages(?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, productId);
            stmt.execute();
        }
    }

    // Helper method to add an image for a product
    private void addProductImage(ProductImage image) throws SQLException {
        String callProc = "{call AddProductImage(?, ?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setString(1, image.getImageName());
            stmt.setInt(2, image.getProductId());
            stmt.setString(3, image.getAlt());
            stmt.execute();
        }
    }

    // Update product stock
    public void updateProductStock(int productId, int newStockAmount) throws SQLException {
        String callProc = "{call UpdateProductStock(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, newStockAmount);
            stmt.execute();
            System.out.println("Product stock updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating product stock: " + e.getMessage(), e);
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

    // Remove product from order
    public void removeProductFromOrder(int productId, int orderId) throws SQLException {
        String callProc = "{call RemoveProductFromOrder(?, ?)}";

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, orderId);
            stmt.execute();
            System.out.println("Product removed from order successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error removing product from order: " + e.getMessage(), e);
        }
    }

    // Get products by order ID
    public List<Product> getProductsByOrder(int orderId) throws SQLException {
        String callProc = "{call GetProductsByOrder(?)}";
        List<Product> products = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(callProc)) {
            stmt.setInt(1, orderId);
            boolean hasResults = stmt.execute();

            if (hasResults) {
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    int productId = rs.getInt("Product_ID");
                    int quantity = rs.getInt("Quantity");

                    Product product = getProductById(productId);
                    if (product != null) {
                        // You might want to add the quantity information to the product
                        // For example, you could add a transient field to the Product class
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving products by order ID: " + e.getMessage(), e);
        }

        return products;
    }
}
