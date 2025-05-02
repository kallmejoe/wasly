package com.example.foodorderingsystem.BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Account {
    private int customerId;
    private List<String> phoneNumbers;
    private List<Location> location;
    private String phone; // For compatibility

//    // Default constructor
//    public Customer() {
////        super();
//        this.phoneNumbers = new ArrayList<>();
//        this.location = new ArrayList<>();
//    }

    public Customer(String email, String password, String firstName,
                    String middleName, String lastName, List<String> phoneNumbers, List<Location> location) {
        super(email, password, firstName, middleName, lastName);
        this.phoneNumbers = phoneNumbers;
        this.location = location;
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            this.phone = phoneNumbers.get(0);
        }
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            this.phone = phoneNumbers.get(0);
        }
    }

    // For compatibility with code expecting getId()
    public int getId() {
        return customerId;
    }

    // For compatibility with code expecting getPhone()
    public String getPhone() {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            return phoneNumbers.get(0);
        }
        return phone;
    }

    // For compatibility with code expecting setPhone()
    public void setPhone(String phone) {
        this.phone = phone;
        if (this.phoneNumbers == null) {
            this.phoneNumbers = new ArrayList<>();
        }
        if (this.phoneNumbers.isEmpty()) {
            this.phoneNumbers.add(phone);
        } else {
            this.phoneNumbers.set(0, phone);
        }
    }

    // For compatibility with code expecting getAddress()
    public String getAddress() {
        if (location != null && !location.isEmpty() && location.get(0) != null) {
            return location.get(0).getAddress();
        }
        return "";
    }

    // Methods for order handling
    public Order getOrder() {
        // This is a compatibility method - in a real implementation,
        // you would retrieve the customer's current order
        return null;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", phoneNumbers=" + phoneNumbers +
                ", location=" + location +
                '}';
    }
}
