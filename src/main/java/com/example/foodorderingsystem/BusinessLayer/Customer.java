package com.example.foodorderingsystem.BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Account {
    private int customerId;
    private List<String> phoneNumbers;
    private List<Location> location;
    private String phone; // For compatibility

    // No-arguments constructor
    public Customer() {
        super();
        this.phoneNumbers = new ArrayList<>();
        this.location = new ArrayList<>();
    }

    // Main constructor
    public Customer(String email, String password, String firstName,
                    String middleName, String lastName, List<String> phoneNumbers, List<Location> location) {
        super(email, password, firstName, middleName, lastName);
        this.phoneNumbers = phoneNumbers != null ? phoneNumbers : new ArrayList<>();
        this.location = location != null ? location : new ArrayList<>();
        if (this.phoneNumbers != null && !this.phoneNumbers.isEmpty()) {
            this.phone = this.phoneNumbers.get(0);
        }
    }

    // Simplified constructor with single phone number
    public Customer(String email, String password, String firstName, String middleName,
                    String lastName, String phoneNumber) {
        super(email, password, firstName, middleName, lastName);
        this.phoneNumbers = new ArrayList<>();
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            this.phoneNumbers.add(phoneNumber);
            this.phone = phoneNumber;
        }
        this.location = new ArrayList<>();
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
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

    // Get/set single phone number for compatibility
    public String getPhoneNumber() {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            return phoneNumbers.get(0);
        }
        return phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (this.phoneNumbers == null) {
            this.phoneNumbers = new ArrayList<>();
        }
        if (this.phoneNumbers.isEmpty()) {
            this.phoneNumbers.add(phoneNumber);
        } else {
            this.phoneNumbers.set(0, phoneNumber);
        }
        this.phone = phoneNumber;
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

    // For compatibility with code expecting getPhoneNo()
    public List<String> getPhoneNo() {
        return phoneNumbers;
    }

    // For compatibility with code expecting setPhoneNo()
    public void setPhoneNo(List<String> phoneNo) {
        this.phoneNumbers = phoneNo;
        if (phoneNo != null && !phoneNo.isEmpty()) {
            this.phone = phoneNo.get(0);
        }
    }

    // For compatibility with code expecting getAddress()
    public String getAddress() {
        if (location != null && !location.isEmpty() && location.get(0) != null) {
            return location.get(0).getAddress();
        }
        return "";
    }

    // For compatibility with code expecting getLocations()
    public List<Location> getLocations() {
        return location;
    }

    // For compatibility with code expecting setLocations()
    public void setLocations(List<Location> locations) {
        this.location = locations;
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
