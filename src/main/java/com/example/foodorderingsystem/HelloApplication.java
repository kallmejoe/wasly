package com.example.foodorderingsystem;

import com.example.foodorderingsystem.BusinessLayer.Customer;
import com.example.foodorderingsystem.BusinessLayer.Delivery;
import com.example.foodorderingsystem.BusinessLayer.Location;
import com.example.foodorderingsystem.DataAccessLayer.CustomerDataAccess;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.*;
import java.util.List;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws SQLException {
        // Create some sample phone numbers and locations
        List<String> phoneNumbers = List.of("0123456789", "0987654321");
        
        // Database connection details
        String connectionUrl = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=Wasly;"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "user=sa;"
                + "password=admin123;";

        CustomerDataAccess customer = new CustomerDataAccess();
        try{
            Customer cust = customer.getCustomer(1);
            System.out.println(cust);
        }catch (SQLException s){
            System.out.print(s);
        }

        // Try to insert the customer data into the database

    }

    public static void main(String[] args) {
        launch();
    }
}