package com.example.foodorderingsystem.BusinessLayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Delivery extends Account {
    private int deliveryId;
    private double salary;
    private List<String> phoneNumbers;
    private List<Location> locations;
    private Location location; // Single location for a delivery
    private String status; // Added status field back
    private Date deliveryDate; // Changed from LocalDateTime to Date
    private Order order; // Added order reference

    // Default constructor
    public Delivery() {
        super();
        this.deliveryId = 0;
        this.salary = 0.0;
        this.phoneNumbers = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.location = null;
        this.status = "Pending"; // Default status
        this.deliveryDate = null;
    }

    public Delivery(String email, String password, String firstName, String middleName, String lastName,
                    int deliveryId, double salary, List<String> phoneNumbers, List<Location> locations) {
        super(email, password, firstName, middleName, lastName);
        this.deliveryId = deliveryId;
        this.salary = salary;
        this.phoneNumbers = phoneNumbers;
        this.locations = locations;
        this.status = "Pending"; // Default status
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
    public void setLocation(Location location) {
        this.location = location;

        // Also update the locations list
        if (location != null) {
            if (this.locations == null) {
                this.locations = new ArrayList<>();
            }

            if (this.locations.isEmpty()) {
                this.locations.add(location);
            } else {
                this.locations.set(0, location);
            }
        }
    }

    // Added status methods back
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // For compatibility with errors about getDeliveryPerson()
    public Delivery getDeliveryPerson() {
        return this;
    }

    // For ID compatibility
    public int getId() {
        return deliveryId;
    }

    // Added methods for Order
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // Added methods for delivery date
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    // For compatibility with getPhoneNo method that may be expected
    public List<String> getPhoneNo() {
        return phoneNumbers;
    }

    // For compatibility with setPhoneNo method that may be expected
    public void setPhoneNo(List<String> phoneNo) {
        this.phoneNumbers = phoneNo;
    }
}
