package com.example.foodorderingsystem.BusinessLayer;

public class Category {
    private int categoryId;
    private String name;
    private String description;

    // Default constructor for no arguments
    public Category() {
    }

    // Constructor with ID for retrieving from database
    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    // Constructor with ID and description
    public Category(int categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    // Constructor without ID for creating new categories
    public Category(String name) {
        this.name = name;
    }

    // Constructor with name and description
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Alias for getName() - resolves the getCategoryName() errors
    public String getCategoryName() {
        return name;
    }

    // Alias for setName() - resolves the setCategoryName() errors
    public void setCategoryName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Category [categoryId=" + categoryId + ", name=" + name +
               (description != null ? ", description=" + description : "") + "]";
    }
}
