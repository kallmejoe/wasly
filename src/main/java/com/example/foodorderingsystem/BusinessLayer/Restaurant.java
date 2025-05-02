package com.example.foodorderingsystem.BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    private int restaurantId;
    private List<Location> locations;
    private String name;
    private static int restaurantCounter = 0;
    private List<String> phoneNo;
    private String phone; // Single phone for compatibility

    // Default constructor
    public Restaurant() {
        this.locations = new ArrayList<>();
        this.phoneNo = new ArrayList<>();
    }

    public Restaurant(String name, List<Location> locations, List<String> phoneNo) {
        this.restaurantId = ++restaurantCounter;
        this.name = name;
        this.locations = locations;
        this.phoneNo = phoneNo;
        if (phoneNo != null && !phoneNo.isEmpty()) {
            this.phone = phoneNo.get(0);
        }
    }

    // Constructor with ID
    public Restaurant(int restaurantId, String name, List<Location> locations, List<String> phoneNo) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.locations = locations;
        this.phoneNo = phoneNo;
        if (phoneNo != null && !phoneNo.isEmpty()) {
            this.phone = phoneNo.get(0);
        }
    }

    public List<String> getPhoneNo() { return phoneNo; }
    public void setPhoneNo(List<String> phoneNo) {
        this.phoneNo = phoneNo;
        if (phoneNo != null && !phoneNo.isEmpty()) {
            this.phone = phoneNo.get(0);
        }
    }

    // For compatibility with code expecting getPhone()
    public String getPhone() {
        if (phoneNo != null && !phoneNo.isEmpty()) {
            return phoneNo.get(0);
        }
        return phone;
    }

    // For compatibility with code expecting setPhone()
    public void setPhone(String phone) {
        this.phone = phone;
        if (this.phoneNo == null) {
            this.phoneNo = new ArrayList<>();
        }
        if (this.phoneNo.isEmpty()) {
            this.phoneNo.add(phone);
        } else {
            this.phoneNo.set(0, phone);
        }
    }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Location> getLocations() { return locations; }
    public void setLocations(List<Location> locations) { this.locations = locations; }

    // For compatibility with code expecting getLocation()
    public Location getLocation() {
        if (locations != null && !locations.isEmpty()) {
            return locations.get(0);
        }
        return null;
    }

    // For compatibility with code expecting setLocation()
    public void setLocation(Location location) {
        if (this.locations == null) {
            this.locations = new ArrayList<>();
        }
        if (this.locations.isEmpty()) {
            this.locations.add(location);
        } else {
            this.locations.set(0, location);
        }
    }

    // For compatibility to handle location operations
    public String getAddress() {
        if (getLocation() != null) {
            return getLocation().getAddress();
        }
        return "";
    }

    // Get ID for compatibility with some operations
    public int getId() {
        return restaurantId;
    }
}
