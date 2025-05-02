package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Cart;
import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Order;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;

/**
 * Singleton class to manage user session data across the application
 */
public class SessionManager {
    private static SessionManager instance;
    private int currentCustomerId = 1; // Default customer ID (would be set after login)
    private String currentCustomerName = "";
    private boolean isLoggedIn = false;
    private Customer currentUser = null;
    private Cart userCart = null;
    private Order selectedOrder = null;
    private Restaurant selectedRestaurant = null;

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
     * Get the ID of the currently logged-in customer
     * @return The customer ID
     */
    public int getCurrentCustomerId() {
        return currentCustomerId;
    }

    /**
     * Set the ID of the currently logged-in customer
     * @param customerId The customer ID to set
     */
    public void setCurrentCustomerId(int customerId) {
        this.currentCustomerId = customerId;
    }

    /**
     * Get the name of the currently logged-in customer
     * @return The customer name
     */
    public String getCurrentCustomerName() {
        return currentCustomerName;
    }

    /**
     * Set the name of the currently logged-in customer
     * @param customerName The customer name to set
     */
    public void setCurrentCustomerName(String customerName) {
        this.currentCustomerName = customerName;
    }

    /**
     * Check if a user is currently logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Set the logged-in status
     * @param loggedIn The logged-in status to set
     */
    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    /**
     * Get the currently logged-in user
     * @return The current Customer object
     */
    public Customer getCurrentUser() {
        return currentUser;
    }

    /**
     * Set the currently logged-in user
     * @param user The Customer object to set as current user
     */
    public void setCurrentUser(Customer user) {
        this.currentUser = user;
        if (user != null) {
            this.currentCustomerId = user.getCustomerId();
            this.currentCustomerName = user.getFirstName() + " " + user.getLastName();
            this.isLoggedIn = true;

            // Initialize user cart if it doesn't exist
            if (this.userCart == null) {
                this.userCart = new Cart(user.getCustomerId());
            }
        } else {
            clearSession();
        }
    }

    /**
     * Get the user's shopping cart
     * @return The Cart object for the current user
     */
    public Cart getUserCart() {
        if (userCart == null && isLoggedIn) {
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
        currentCustomerId = -1;
        currentCustomerName = "";
        currentUser = null;
        userCart = null;
        selectedOrder = null;
        selectedRestaurant = null;
        isLoggedIn = false;
    }
}
