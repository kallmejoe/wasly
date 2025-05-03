package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Admin;
import com.example.foodorderingsystem.BusinessLayer.Cart;
import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Order;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;

/**
 * Singleton class to manage user session data across the application
 */
public class SessionManager {
    private static SessionManager instance;

    // Common session properties
    private boolean isLoggedIn = false;
    private String userType = null; // "customer" or "admin"

    // Customer specific properties
    private Customer currentCustomer = null;
    private int currentCustomerId = -1;
    private String currentCustomerName = "";
    private Cart userCart = null;
    private Order selectedOrder = null;
    private Restaurant selectedRestaurant = null;

    // Admin specific properties
    private Admin currentAdmin = null;
    private int currentAdminId = -1;
    private String currentAdminName = "";

    private SessionManager() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Get the singleton instance of SessionManager
     * @return The SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Check if a user is currently logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Check if the current user is an admin
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return isLoggedIn && "admin".equals(userType);
    }

    /**
     * Check if the current user is a customer
     * @return true if the user is a customer, false otherwise
     */
    public boolean isCustomer() {
        return isLoggedIn && "customer".equals(userType);
    }

    /**
     * Get the type of the currently logged-in user
     * @return "customer" or "admin" or null if not logged in
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Set the type of the currently logged-in user
     * @param type "customer" or "admin"
     */
    public void setUserType(String type) {
        if ("customer".equals(type) || "admin".equals(type)) {
            this.userType = type;
        }
    }

    /**
     * Get the ID of the currently logged-in customer
     * @return The customer ID or -1 if not logged in as customer
     */
    public int getCurrentCustomerId() {
        return isCustomer() ? currentCustomerId : -1;
    }

    /**
     * Get the name of the currently logged-in customer
     * @return The customer name or empty string if not logged in as customer
     */
    public String getCurrentCustomerName() {
        return isCustomer() ? currentCustomerName : "";
    }

    /**
     * Get the ID of the currently logged-in admin
     * @return The admin ID or -1 if not logged in as admin
     */
    public int getCurrentAdminId() {
        return isAdmin() ? currentAdminId : -1;
    }

    /**
     * Get the name of the currently logged-in admin
     * @return The admin name or empty string if not logged in as admin
     */
    public String getCurrentAdminName() {
        return isAdmin() ? currentAdminName : "";
    }

    /**
     * Get the currently logged-in customer
     * @return The current Customer object or null if not logged in as customer
     */
    public Customer getCurrentCustomer() {
        return isCustomer() ? currentCustomer : null;
    }

    /**
     * Get the currently logged-in admin
     * @return The current Admin object or null if not logged in as admin
     */
    public Admin getCurrentAdmin() {
        return isAdmin() ? currentAdmin : null;
    }

    /**
     * Get the current user (generic)
     * @return The current user object (either Customer or Admin)
     */
    public Object getCurrentUser() {
        if (isCustomer()) {
            return currentCustomer;
        } else if (isAdmin()) {
            return currentAdmin;
        }
        return null;
    }

    /**
     * Set the currently logged-in customer
     * @param customer The Customer object to set as current user
     */
    public void setCurrentUser(Customer customer) {
        this.userType = "customer";
        this.currentCustomer = customer;
        this.currentAdmin = null;

        if (customer != null) {
            this.currentCustomerId = customer.getCustomerId();
            this.currentCustomerName = customer.getFirstName() + " " + customer.getLastName();
            this.isLoggedIn = true;

            // Initialize user cart if it doesn't exist
            if (this.userCart == null) {
                this.userCart = new Cart(customer.getCustomerId());
            }
        } else {
            clearSession();
        }
    }

    /**
     * Set the currently logged-in admin
     * @param admin The Admin object to set as current user
     */
    public void setCurrentUser(Admin admin) {
        this.userType = "admin";
        this.currentAdmin = admin;
        this.currentCustomer = null;

        if (admin != null) {
            this.currentAdminId = admin.getAdminId();
            this.currentAdminName = admin.getFirstName() + " " + admin.getLastName();
            this.isLoggedIn = true;

            // Clear customer-specific data when logging in as admin
            this.userCart = null;
            this.selectedOrder = null;
            this.selectedRestaurant = null;
        } else {
            clearSession();
        }
    }

    /**
     * Get the user's shopping cart (only applicable for customers)
     * @return The Cart object for the current customer or null if not a customer
     */
    public Cart getUserCart() {
        if (isCustomer() && userCart == null) {
            userCart = new Cart(currentCustomerId);
        }
        return userCart;
    }

    /**
     * Set the user's shopping cart
     * @param cart The Cart object to set
     */
    public void setUserCart(Cart cart) {
        this.userCart = cart;
    }

    /**
     * Get the currently selected order (for order details view)
     * @return The selected Order object
     */
    public Order getSelectedOrder() {
        return selectedOrder;
    }

    /**
     * Set the currently selected order (for order details view)
     * @param order The Order object to set as selected
     */
    public void setSelectedOrder(Order order) {
        this.selectedOrder = order;
    }

    /**
     * Get the currently selected restaurant (for restaurant menu view)
     * @return The selected Restaurant object
     */
    public Restaurant getSelectedRestaurant() {
        return selectedRestaurant;
    }

    /**
     * Set the currently selected restaurant (for restaurant menu view)
     * @param restaurant The Restaurant object to set as selected
     */
    public void setSelectedRestaurant(Restaurant restaurant) {
        this.selectedRestaurant = restaurant;
    }

    /**
     * Log out the current user, clearing all session data
     */
    public void logout() {
        clearSession();
    }

    /**
     * Clear all session data (for logout)
     */
    public void clearSession() {
        // Reset customer data
        currentCustomerId = -1;
        currentCustomerName = "";
        currentCustomer = null;
        userCart = null;
        selectedOrder = null;
        selectedRestaurant = null;

        // Reset admin data
        currentAdminId = -1;
        currentAdminName = "";
        currentAdmin = null;

        // Reset common data
        userType = null;
        isLoggedIn = false;
    }
}