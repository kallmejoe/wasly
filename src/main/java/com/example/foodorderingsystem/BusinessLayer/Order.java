package com.example.foodorderingsystem.BusinessLayer;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Order {
    private int orderId;
    private LocalDateTime orderDate;
    private Customer customer;
    private Restaurant restaurant;
    private Delivery delivery;
    private Payment payment;
    private double totalAmount;
    private static int orderCounter = 0; // Static counter for unique order IDs

    // No-argument constructor
    public Order() {
        this.orderDate = LocalDateTime.now();
    }


    public Order( LocalDateTime orderDate, Customer customer,
                 Restaurant restaurant, Delivery delivery, Payment payment) {
        this.orderId = ++orderCounter;
        this.orderDate = orderDate;
        this.customer = customer;
        this.restaurant = restaurant;
        this.delivery = delivery;
        this.payment = payment;
        this.totalAmount = payment != null ? payment.getAmount() : 0.0;
    }

    public Order( LocalDateTime now, Customer customer, Restaurant restaurant, Payment payment) {
        this.orderId = ++orderCounter;
        this.orderDate = now;
        this.customer = customer;
        this.restaurant = restaurant;
        this.delivery = null; // Delivery is not set in this constructor
        this.payment = payment;
        this.totalAmount = payment != null ? payment.getAmount() : 0.0;
    }

    // Constructor for use with checkout
    public Order(int customerId, int restaurantId, Location deliveryLocation,
                String paymentMethod,
                java.util.Map<Product, Integer> items, BigDecimal totalAmount) {
        this.orderDate = LocalDateTime.now();
        this.totalAmount = totalAmount.doubleValue();
        // Note: Customer, Restaurant, Delivery and Payment objects will be populated later
        // by the OrderDataAccess class
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

    // Additional getters to support direct access to related entity IDs
    public int getCustomerId() {
        return customer != null ? customer.getCustomerId() : 0;
    }

    public int getRestaurantId() {
        return restaurant != null ? restaurant.getRestaurantId() : 0;
    }

    // Alias for getOrderDate to support getOrderTime calls
    public LocalDateTime getOrderTime() {
        return orderDate;
    }

    // Added methods for status



    // For compatibility with code expecting getOrder()
    public Order getOrder() {
        return this;
    }
}
