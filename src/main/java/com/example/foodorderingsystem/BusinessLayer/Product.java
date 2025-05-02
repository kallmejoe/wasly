package com.example.foodorderingsystem.BusinessLayer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productId;
    private String name;
    private BigDecimal unitPrice;
    private int amountInStock;
    private Restaurant restaurant; // Changed from restaurantId to Restaurant object
    private Category category;     // Changed from categoryId to Category object
    private List<ProductImage> images;
    private String description;

    public Product() {
        this.images = new ArrayList<>();
    }

    public Product(int productId, String name, BigDecimal unitPrice, int amountInStock,
                   Restaurant restaurant, Category category) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.amountInStock = amountInStock;
        this.restaurant = restaurant;
        this.category = category;
        this.images = new ArrayList<>();
    }

    // Constructor with description
    public Product(int productId, String name, String description, BigDecimal unitPrice, int amountInStock,
                   Restaurant restaurant, Category category) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.amountInStock = amountInStock;
        this.restaurant = restaurant;
        this.category = category;
        this.images = new ArrayList<>();
    }

    // Constructor without ID for new products
    public Product(String name, BigDecimal unitPrice, int amountInStock,
                   Restaurant restaurant, Category category) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.amountInStock = amountInStock;
        this.restaurant = restaurant;
        this.category = category;
        this.images = new ArrayList<>();
    }

    // Constructor that accepts IDs instead of objects (for backward compatibility)
    public Product(int productId, String name, BigDecimal unitPrice, int amountInStock,
                   int restaurantId, int categoryId) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.amountInStock = amountInStock;
        // Restaurant and Category will be set later
        this.images = new ArrayList<>();
    }

    // Constructor without ID that accepts IDs instead of objects (for backward compatibility)
    public Product(String name, BigDecimal unitPrice, int amountInStock,
                   int restaurantId, int categoryId) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.amountInStock = amountInStock;
        // Restaurant and Category will be set later
        this.images = new ArrayList<>();
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getAmountInStock() {
        return amountInStock;
    }

    public void setAmountInStock(int amountInStock) {
        this.amountInStock = amountInStock;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // For backward compatibility
    public int getRestaurantId() {
        return restaurant != null ? restaurant.getRestaurantId() : 0;
    }

    public void setRestaurantId(int restaurantId) {
        // This will be replaced by setRestaurant with actual Restaurant object
    }

    public int getCategoryId() {
        return category != null ? category.getCategoryId() : 0;
    }

    public void setCategoryId(int categoryId) {
        // This will be replaced by setCategory with actual Category object
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public void addImage(ProductImage image) {
        this.images.add(image);
    }

    @Override
    public String toString() {
        return "Product [productId=" + productId +
               ", name=" + name +
               (description != null ? ", description=" + description : "") +
               ", unitPrice=" + unitPrice +
               ", amountInStock=" + amountInStock +
               ", restaurant=" + (restaurant != null ? restaurant.getName() : "null") +
               ", category=" + (category != null ? category.getName() : "null") +
               ", images=" + images + "]";
    }
}
