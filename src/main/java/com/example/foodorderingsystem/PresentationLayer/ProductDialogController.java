package com.example.foodorderingsystem.PresentationLayer;

import java.sql.SQLException;
import java.util.List;

import com.example.foodorderingsystem.BusinessLayer.Category;
import com.example.foodorderingsystem.BusinessLayer.Product;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.DataAccessLayer.CategoryDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.ProductDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.RestaurantDataAccess;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ProductDialogController {

    @FXML
    private DialogPane dialogPane;
    @FXML
    private Label lblProductId;
    @FXML
    private TextField txtProductName;
    @FXML
    private TextField txtProductPrice;
    @FXML
    private TextArea txtDescription;
    @FXML
    private ComboBox<Restaurant> cmbRestaurant;
    @FXML
    private ComboBox<Category> cmbCategory;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtAmountInStock;

    private Product product;
    private ProductDataAccess productDataAccess;
    private RestaurantDataAccess restaurantDataAccess;
    private CategoryDataAccess categoryDataAccess;
    private boolean isNewProduct = false;
    private Dialog<Product> dialog;

    public void setDialog(Dialog<Product> dialog) {
        this.dialog = dialog;

        // Set up the result converter for the dialog
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (validateInputs()) {
                    updateProductFromInputs();
                    if (saveProduct()) {
                        return product;
                    }
                }
                return null;
            }
            return null;
        });
    }

    @FXML
    private void initialize() {
        productDataAccess = new ProductDataAccess();
        restaurantDataAccess = new RestaurantDataAccess();
        categoryDataAccess = new CategoryDataAccess();

        // Set default amount in stock
        txtAmountInStock.setText("0");

        // Load restaurants and categories
        loadRestaurants();
        loadCategories();
    }

    public void setProduct(Product product) {
        if (product == null) {
            this.product = new Product();
            isNewProduct = true;
            lblProductId.setText("New");
            return;
        }

        this.product = product;
        isNewProduct = (product.getProductId() == 0);

        if (!isNewProduct) {
            // Fill form with product data
            lblProductId.setText(String.valueOf(product.getProductId()));
            txtProductName.setText(product.getName());
            txtDescription.setText(product.getDescription() != null ? product.getDescription() : "");
            txtAmountInStock.setText(String.valueOf(product.getAmountInStock()));

            // Display price as formatted string
            txtProductPrice.setText(String.format("%.2f", product.getPrice()));

            // Select restaurant and category
            try {
                Restaurant restaurant = restaurantDataAccess.getRestaurantByID(product.getRestaurantId());
                if (restaurant != null) {
                    for (Restaurant r : cmbRestaurant.getItems()) {
                        if (r.getRestaurantId() == restaurant.getRestaurantId()) {
                            cmbRestaurant.setValue(r);
                            break;
                        }
                    }
                }

                Category category = categoryDataAccess.getCategoryById(product.getCategoryId());
                if (category != null) {
                    for (Category c : cmbCategory.getItems()) {
                        if (c.getCategoryId() == category.getCategoryId()) {
                            cmbCategory.setValue(c);
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                showError("Could not load product details: " + e.getMessage());
            }
        } else {
            lblProductId.setText("New");
        }
    }

    private void loadRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantDataAccess.getAllRestaurants();
            cmbRestaurant.setItems(FXCollections.observableArrayList(restaurants));
            cmbRestaurant.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Restaurant item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getName());
                }
            });
            cmbRestaurant.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Restaurant item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getName());
                }
            });
        } catch (SQLException e) {
            showError("Could not load restaurants: " + e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDataAccess.getAllCategories();
            cmbCategory.setItems(FXCollections.observableArrayList(categories));
            cmbCategory.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getName());
                }
            });
            cmbCategory.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getName());
                }
            });
        } catch (SQLException e) {
            showError("Could not load categories: " + e.getMessage());
        }
    }

    private void updateProductFromInputs() {
        System.out.println("Updating product from inputs...");

        // Update product object with form data
        String productName = txtProductName.getText().trim();
        String description = txtDescription.getText().trim();
        String priceString = txtProductPrice.getText().trim();
        String stockString = txtAmountInStock.getText().trim();

        // Set basic product info
        product.setName(productName);
        product.setDescription(description);

        // Set price
        try {
            double price = Double.parseDouble(priceString);
            product.setPrice(price);
        } catch (NumberFormatException e) {
            product.setPrice(0.0);
        }

        // Set amount in stock
        try {
            int stock = Integer.parseInt(stockString);
            product.setAmountInStock(stock);
        } catch (NumberFormatException e) {
            product.setAmountInStock(0);
        }

        // Set restaurant and category
        Restaurant selectedRestaurant = cmbRestaurant.getValue();
        if (selectedRestaurant != null) {
            product.setRestaurant(selectedRestaurant);
        }

        Category selectedCategory = cmbCategory.getValue();
        if (selectedCategory != null) {
            product.setCategory(selectedCategory);
        }
    }

    public boolean saveProduct() {
        try {
            if (isNewProduct) {
                System.out.println("Inserting new product: " + product.getName());
                productDataAccess.insertProduct(product);
            } else {
                System.out.println("Updating existing product with ID: " + product.getProductId());
                productDataAccess.updateProduct(product);
            }

            System.out.println("Product saved successfully.");
            return true;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        lblStatus.setText("");

        if (txtProductName.getText().trim().isEmpty()) {
            errorMessage.append("Product name is required\n");
        }

        if (txtProductPrice.getText().trim().isEmpty()) {
            errorMessage.append("Product price is required\n");
        } else {
            try {
                double price = Double.parseDouble(txtProductPrice.getText().trim());
                if (price <= 0) {
                    errorMessage.append("Price must be greater than zero\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Price must be a valid number\n");
            }
        }

        if (cmbRestaurant.getValue() == null) {
            errorMessage.append("Restaurant selection is required\n");
        }

        if (cmbCategory.getValue() == null) {
            errorMessage.append("Category selection is required\n");
        }

        if (errorMessage.length() > 0) {
            lblStatus.setText(errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showError(String message) {
        lblStatus.setText(message);

        if (message != null && !message.isEmpty() && lblStatus.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save product");
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    @FXML
    public void handleOk() {
        if (validateInputs()) {
            updateProductFromInputs();
            if (saveProduct()) {
                dialog.setResult(product);
                dialog.close();
            }
        }
    }

    @FXML
    public void handleSave() {
        handleOk();
    }

    @FXML
    public void handleCancel() {
        dialog.setResult(null);
        dialog.close();
    }
}
