package com.example.foodorderingsystem.BusinessLayer;

public class ProductImage {
    private String imageName;
    private int productId;
    private String alt;

    public ProductImage(String imageName, int productId, String alt) {
        this.imageName = imageName;
        this.productId = productId;
        this.alt = alt;
    }

    // Getters and Setters
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public String toString() {
        return "ProductImage [imageName=" + imageName + ", productId=" + productId + ", alt=" + alt + "]";
    }
}
