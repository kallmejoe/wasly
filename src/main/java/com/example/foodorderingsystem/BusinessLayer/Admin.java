package com.example.foodorderingsystem.BusinessLayer;
import java.util.List;
import java.util.ArrayList;

public class Admin extends Account {
    private int adminId;
    private double salary;
    private List<String> phoneNumbers;
    private List<Integer> RestaurantsManaged;
    private static int adminCounter = 0;

    // No-arguments constructor
    public Admin() {
        super();
        this.adminId = ++adminCounter;
        this.salary = 0.0;
        this.phoneNumbers = new ArrayList<>();
        this.RestaurantsManaged = new ArrayList<>();
    }

    // Full constructor
    public Admin(String email, String password, String firstName, String middleName, String lastName,
                 int adminId, double salary, List<String> phoneNumbers) {
        super(email, password, firstName, middleName, lastName);
        this.adminId = adminId;
        this.salary = salary;
        this.phoneNumbers = phoneNumbers != null ? phoneNumbers : new ArrayList<>();
        this.RestaurantsManaged = new ArrayList<>();
    }

    // Simplified constructor
    public Admin(String email, String password, String firstName, String middleName, String lastName) {
        super(email, password, firstName, middleName, lastName);
        this.adminId = ++adminCounter;
        this.salary = 0.0;
        this.phoneNumbers = new ArrayList<>();
        this.RestaurantsManaged = new ArrayList<>();
    }

    // Getters and Setters
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public List<String> getPhoneNumbers() { return phoneNumbers; }
    public void setPhoneNumbers(List<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; }

    // For compatibility with code expecting getPhoneNo()
    public List<String> getPhoneNo() { return phoneNumbers; }
    // For compatibility with code expecting setPhoneNo()
    public void setPhoneNo(List<String> phoneNo) { this.phoneNumbers = phoneNo; }

    public List<Integer> getRestaurantsManaged() {return RestaurantsManaged;}
    public void setRestaurantsManaged(List<Integer> restaurantsManaged) {this.RestaurantsManaged = restaurantsManaged;}
}
