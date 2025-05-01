package com.example.foodorderingsystem.BusinessLayer;

public class Payment {
    private int paymentId;
    private String status;
    private String method;
    private Delivery delivery;
    private Restaurant restaurant;

    public Payment(int paymentId, String status, String method, Delivery delivery, Restaurant restaurant) {
        this.paymentId = paymentId;
        this.status = status;
        this.method = method;
        this.delivery = delivery;
        this.restaurant = restaurant;
    }

    // Getters and setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}