package com.example.foodorderingsystem;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Delivery;
import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.DataAccessLayer.CustomerDataAccess;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.*;
import java.util.List;
import java.util.Arrays;
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws SQLException {
        // Create some sample phone numbers and locations
        List<String> phoneNumbers = List.of("0123456789", "0987654321");
        Delivery delivery = new Delivery(
                "delivery@example.com",           // email
                "secure123",                      // password
                "Ali",                            // firstName
                "H.",                             // middleName
                "Saleh",                          // lastName
                1,                                // deliveryId
                3500.0,                           // salary
                Arrays.asList("0501234567", "0569876543"),                 // phoneNumbers
                List.of(new Location("Jeddah", "King Fahd Street", "12B")) // locations
        );
        // Database connection details
        String connectionUrl = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=Wasly;"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "user=sa;"
                + "password=admin123;";

        // Try to insert the customer data into the database

    }

    public static void main(String[] args) {
        launch();
    }
}