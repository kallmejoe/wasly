package com.example.foodorderingsystem.BusinessLayer;

import java.util.List;

public class Customer extends Account {
    private int customerId;
    private List<String> phoneNumbers;
    private List<Location> location;

    public Customer( String email, String password, String firstName,
                    String middleName, String lastName, List<String> phoneNumbers, List<Location> location) {
        super(email, password, firstName, middleName, lastName);
        this.phoneNumbers = phoneNumbers;
        this.location = location;
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
