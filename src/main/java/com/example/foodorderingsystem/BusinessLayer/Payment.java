package com.example.foodorderingsystem.BusinessLayer;

public class Payment {
    private int paymentId;
    private String status;
    private String method;
    private int orderId;
    private int customerId;
    private Delivery delivery;
    private Restaurant restaurant;
    private double amount; // Added amount field

    // Default constructor for creating empty payment objects
    public Payment() {
    }

    public Payment(int paymentId, String status, String method, Delivery delivery, Restaurant restaurant) {
        this.paymentId = paymentId;
        this.status = status;
        this.method = method;
        this.delivery = delivery;
        this.restaurant = restaurant;
    }

    // New constructor with orderId and customerId
    public Payment(int paymentId, String status, String method, int orderId, int customerId, Delivery delivery, Restaurant restaurant) {
        this.paymentId = paymentId;
        this.status = status;
        this.method = method;
        this.orderId = orderId;
        this.customerId = customerId;
        this.delivery = delivery;
        this.restaurant = restaurant;
    }

    // Constructor with amount
    public Payment(int paymentId, String status, String method, double amount, Delivery delivery, Restaurant restaurant) {
        this.paymentId = paymentId;
        this.status = status;
        this.method = method;
        this.amount = amount;
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

    // Alias for setMethod to support setPaymentMethod calls
    public void setPaymentMethod(String method) {
        this.method = method;
    }

    // Alias for getMethod to support getPaymentMethod calls
    public String getPaymentMethod() {
        return this.method;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
