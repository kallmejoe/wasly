package com.example.foodorderingsystem.BusinessLayer;

public class Category {
    private int categoryId;
    private String name;

    // Constructor with ID for retrieving from database
    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    // Constructor without ID for creating new categories
    public Category(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Category [categoryId=" + categoryId + ", name=" + name + "]";
    }
}
