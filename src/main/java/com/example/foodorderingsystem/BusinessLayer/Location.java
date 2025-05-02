package com.example.foodorderingsystem.BusinessLayer;


public class Location {
    private String city;
    private String streetName;
    private String streetNumber;
    private String state;
    private String zipCode;
    private String description;
    private double latitude;
    private double longitude;

    // No-argument constructor
    public Location() {
    }

    public Location(String city, String streetName, String streetNumber) {
        this.city = city;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
    }

    public Location(String city, String streetName, String streetNumber, String state, String zipCode) {
        this.city = city;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.state = state;
        this.zipCode = zipCode;
    }

    // Getters and setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Method to get the full address as a string
    public String getAddress() {
        StringBuilder address = new StringBuilder();
        address.append(streetNumber).append(" ").append(streetName);
        if (city != null && !city.isEmpty()) {
            address.append(", ").append(city);
        }
        if (state != null && !state.isEmpty()) {
            address.append(", ").append(state);
        }
        if (zipCode != null && !zipCode.isEmpty()) {
            address.append(" ").append(zipCode);
        }
        return address.toString();
    }

    // Method for setting full address as a single string
    public void setAddress(String address) {
        if (address == null || address.isEmpty()) {
            return;
        }

        // Simple parsing logic - this could be improved based on address format
        String[] parts = address.split(",");
        if (parts.length >= 1) {
            String streetPart = parts[0].trim();
            int spaceIndex = streetPart.indexOf(' ');
            if (spaceIndex > 0) {
                this.streetNumber = streetPart.substring(0, spaceIndex).trim();
                this.streetName = streetPart.substring(spaceIndex).trim();
            } else {
                this.streetName = streetPart;
            }
        }

        if (parts.length >= 2) {
            this.city = parts[1].trim();
        }

        if (parts.length >= 3) {
            String stateZip = parts[2].trim();
            int spaceIndex = stateZip.lastIndexOf(' ');
            if (spaceIndex > 0) {
                this.state = stateZip.substring(0, spaceIndex).trim();
                this.zipCode = stateZip.substring(spaceIndex).trim();
            } else {
                this.state = stateZip;
            }
        }
    }

    // For compatibility with code that expects getLocation()
    public Location getLocation() {
        return this;
    }

    @Override
    public String toString() {
        return getAddress();
    }
}
