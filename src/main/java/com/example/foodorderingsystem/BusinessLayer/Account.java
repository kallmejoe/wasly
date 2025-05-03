package com.example.foodorderingsystem.BusinessLayer;

public abstract class Account {
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String role; // Added role field for user type

    // Add a no-argument constructor
    public Account() {
        this.email = "";
        this.password = "";
        this.firstName = "";
        this.middleName = "";
        this.lastName = "";
        this.role = "";
    }

    public Account(String email, String password, String firstName, String middleName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Added role methods
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Get full name utility method
    public String getName() {
        StringBuilder fullName = new StringBuilder();
        fullName.append(firstName);

        if (middleName != null && !middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            fullName.append(" ").append(lastName);
        }

        return fullName.toString();
    }
}
