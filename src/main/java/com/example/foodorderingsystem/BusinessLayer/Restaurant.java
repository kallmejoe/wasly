package com.example.foodorderingsystem.BusinessLayer;

import java.util.List;

public class Restaurant {

    private int restaurantId;
    private String name;

    private String city;
    private String streetName;
    private String streetNumber;

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }

    public String getStreetNumber() { return streetNumber; }
    public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
    private List<String> phoneNo;

    public List<String> getPhoneNo() { return phoneNo; }
    public void setPhoneNo(List<String> phoneNo) { this.phoneNo = phoneNo; }
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


}
