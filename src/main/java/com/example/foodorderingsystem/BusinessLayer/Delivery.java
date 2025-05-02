package com.example.foodorderingsystem.BusinessLayer;
import java.util.ArrayList;
import java.util.List;

public class Delivery extends Account {
    private int deliveryId;
    private double salary;
    private List<String> phoneNumbers;
    private List<Location> locations;
    private Location location; // Single location for a delivery
    // Status field has been removed

    // Constructor for Delivery class
//    public Delivery(){
//        super();
//        this.deliveryId = 0;
//        this.salary = 0.0;
//        this.phoneNumbers = new ArrayList<>();
//        this.locations = new ArrayList<>();
//        this.location = null; // Initialize to null
//        this.status = ""; // Initialize to empty string
//    }

    public Delivery(String email, String password, String firstName, String middleName, String lastName,
                    int deliveryId, double salary, List<String> phoneNumbers, List<Location> locations) {
        super(email, password, firstName, middleName, lastName);
        this.deliveryId = deliveryId;
        this.salary = salary;
        this.phoneNumbers = phoneNumbers;
        this.locations = locations;
        if (locations != null && !locations.isEmpty()) {
            this.location = locations.get(0);
        }
    }

    public int getDeliveryId() { return deliveryId; }
    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public List<String> getPhoneNumbers() { return phoneNumbers; }
    public void setPhoneNumbers(List<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; }

    public List<Location> getLocations() { return locations; }
    public void setLocations(List<Location> locations) {
        this.locations = locations;
        if (locations != null && !locations.isEmpty()) {
            this.location = locations.get(0);
        }
    }

    // Added methods for location handling
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    // Removed getStatus and setStatus methods

    // For compatibility with errors about getDeliveryPerson()
    public Delivery getDeliveryPerson() {
        return this;
    }

    // For ID compatibility
    public int getId() {
        return deliveryId;
    }
}
