package com.example.foodorderingsystem.BusinessLayer;
import java.util.List;

public class Admin extends Account {
    private int adminId;
    private double salary;
    private List<String> phoneNumbers;
    private List<Integer> RestaurantsManaged;
    private static int adminCounter = 0;

    public Admin(String email, String password, String firstName, String middleName, String lastName,
                 int adminId, double salary, List<String> phoneNumbers) {
        super(email, password, firstName, middleName, lastName);
        this.adminId = ++adminCounter;
        this.adminId = adminId;
        this.salary = salary;
        this.phoneNumbers = phoneNumbers;
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public List<String> getPhoneNumbers() { return phoneNumbers; }
    public void setPhoneNumbers(List<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; }

    public List<Integer> getRestaurantsManaged() {return RestaurantsManaged;}
    public void setRestaurantsManaged(List<Integer> restaurantsManaged) {this.RestaurantsManaged = restaurantsManaged;}
}
