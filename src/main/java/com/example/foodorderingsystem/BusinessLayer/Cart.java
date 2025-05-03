package com.example.foodorderingsystem.BusinessLayer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    private Map<Product, Integer> items; // Product with quantity
    private int customerId;
    private int restaurantId; // Ensuring orders are from a single restaurant

    public Cart(int customerId) {
        this.customerId = customerId;
        this.items = new HashMap<>();
        this.restaurantId = -1; // -1 means no restaurant selected yet
    }

    /**
     * Adds a product to the cart
     * @param product The product to add
     * @param quantity Quantity to add
     * @return true if added successfully, false if from different restaurant
     */
    public boolean addProduct(Product product, int quantity) {
        // Check if cart is empty or product is from the same restaurant
        if (restaurantId == -1 || restaurantId == product.getRestaurantId()) {
            // Set restaurant ID if this is the first item
            if (restaurantId == -1) {
                restaurantId = product.getRestaurantId();
            }

            // If product already exists in cart, update quantity
            if (items.containsKey(product)) {
                items.put(product, items.get(product) + quantity);
            } else {
                items.put(product, quantity);
            }
            return true;
        }
        return false; // Product is from a different restaurant
    }

    /**
     * Removes a product from the cart
     * @param product The product to remove
     */
    public void removeProduct(Product product) {
        items.remove(product);

        // If cart is empty, reset restaurant ID
        if (items.isEmpty()) {
            restaurantId = -1;
        }
    }

    /**
     * Updates the quantity of a product in the cart
     * @param product The product to update
     * @param quantity New quantity
     */
    public void updateQuantity(Product product, int quantity) {
        if (quantity <= 0) {
            removeProduct(product);
        } else if (items.containsKey(product)) {
            items.put(product, quantity);
        }
    }

    /**
     * Clears the cart
     */
    public void clear() {
        items.clear();
        restaurantId = -1;
    }

    /**
     * Calculates the total price of items in the cart
     * @return Total price
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            // Convert the double to BigDecimal before multiplying
            BigDecimal itemPrice = BigDecimal.valueOf(entry.getKey().getUnitPrice())
                    .multiply(BigDecimal.valueOf(entry.getValue()));
            total = total.add(itemPrice);
        }
        return total;
    }

    /**
     * Get the current cart items
     * @return Map of products with their quantities
     */
    public Map<Product, Integer> getItems() {
        return items;
    }

    /**
     * Get the customer ID associated with this cart
     * @return Customer ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Get the restaurant ID for this cart
     * @return Restaurant ID
     */
    public int getRestaurantId() {
        return restaurantId;
    }

    /**
     * Check if the cart is empty
     * @return true if cart is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Get the total number of items in the cart
     * @return Total item count
     */
    public int getItemCount() {
        int count = 0;
        for (int quantity : items.values()) {
            count += quantity;
        }
        return count;
    }

    /**
     * Get cart items as a list of CartItems (for UI display)
     * @return List of CartItem objects
     */
    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            cartItems.add(new CartItem(entry.getKey(), entry.getValue()));
        }
        return cartItems;
    }

    /**
     * CartItem class for UI display purposes
     */
    public static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getItemTotal() {
            // Convert the double to BigDecimal before multiplying
            return BigDecimal.valueOf(product.getUnitPrice())
                   .multiply(BigDecimal.valueOf(quantity));
        }
    }
}
