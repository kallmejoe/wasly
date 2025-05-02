package com.example.foodorderingsystem.BusinessLayer;

import java.util.List;

public class Restaurant {

    private int restaurantId;
    private List<Location> locations;
    private String name;
    private static int restaurantCounter = 0;
    private List<String> phoneNo;


    public Restaurant(String name, List<Location> locations, List<String> phoneNo) {
        this.restaurantId = ++restaurantCounter;
        this.name = name;
        this.locations = locations;
        this.phoneNo = phoneNo;
    }


    public List<String> getPhoneNo() { return phoneNo; }
    public void setPhoneNo(List<String> phoneNo) { this.phoneNo = phoneNo; }
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Location> getLocations() { return locations; }
    public void setLocations(List<Location> locations) { this.locations = locations; }


}
