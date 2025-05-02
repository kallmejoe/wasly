package com.example.foodorderingsystem.BusinessLayer;

import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private LocalDateTime orderDate;
    private Customer customer;
    private Restaurant restaurant;
    private Delivery delivery;
    private Payment payment;
    private double totalAmount;
    private String status;

    // No-argument constructor
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = "Pending"; // Default status
    }

    public Order(int orderId, LocalDateTime orderDate, Customer customer,
                 Restaurant restaurant, Delivery delivery, Payment payment) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customer = customer;
        this.restaurant = restaurant;
        this.delivery = delivery;
        this.payment = payment;
        this.status = "Pending"; // Default status
        this.totalAmount = payment != null ? payment.getAmount() : 0.0;
    }

    public Order(int orderId, LocalDateTime now, Customer customer, Restaurant restaurant, Payment payment) {
        this.orderId = orderId;
        this.orderDate = now;
        this.customer = customer;
        this.restaurant = restaurant;
        this.delivery = null; // Delivery is not set in this constructor
        this.payment = payment;
        this.status = "Pending"; // Default status
        this.totalAmount = payment != null ? payment.getAmount() : 0.0;
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
