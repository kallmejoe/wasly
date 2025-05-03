package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Product;
import com.example.foodorderingsystem.BusinessLayer.Restaurant;
import com.example.foodorderingsystem.BusinessLayer.Category;
import com.example.foodorderingsystem.DataAccessLayer.ProductDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.RestaurantDataAccess;
import com.example.foodorderingsystem.DataAccessLayer.CategoryDataAccess;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ProductDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private Label lblProductId;
    @FXML private TextField txtProductName;
    @FXML private TextField txtProductPrice;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<Restaurant> cmbRestaurant;
    @FXML private ComboBox<Category> cmbCategory;
    @FXML private Label lblStatus;

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
                    return product;
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

        // Load restaurants and categories
        loadRestaurants();
        loadCategories();
    }

    public void setProduct(Product product) {
        this.product = product;
        isNewProduct = (product.getProductId() == 0);

        if (!isNewProduct) {
            // Fill form with product data
            lblProductId.setText(String.valueOf(product.getProductId()));
            txtProductName.setText(product.getName());
            txtDescription.setText(product.getDescription() != null ? product.getDescription() : "");

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
        // Update product object with form data
        product.setName(txtProductName.getText().trim());
        product.setDescription(txtDescription.getText().trim());

        try {
            double price = Double.parseDouble(txtProductPrice.getText().trim());
            product.setPrice(price);
        } catch (NumberFormatException e) {
            product.setPrice(0.0);
        }

        Restaurant selectedRestaurant = cmbRestaurant.getValue();
        Category selectedCategory = cmbCategory.getValue();

        if (selectedRestaurant != null) {
            product.setRestaurantId(selectedRestaurant.getRestaurantId());
        }

        if (selectedCategory != null) {
            product.setCategoryId(selectedCategory.getCategoryId());
        }
    }

    public boolean saveProduct() {
        try {
            if (isNewProduct) {
                productDataAccess.insertProduct(product);
            } else {
                productDataAccess.updateProduct(product);
            }
            return true;
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            return false;
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        lblStatus.setText("");

        if (txtProductName.getText() == null || txtProductName.getText().trim().isEmpty()) {
            errorMessage.append("Product name is required\n");
        }

        if (txtProductPrice.getText() == null || txtProductPrice.getText().trim().isEmpty()) {
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

        if (lblStatus.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save product");
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    public void handleOk() {
        if (validateInputs()) {
            updateProductFromInputs();
            if (saveProduct()) {
                dialog.setResult(product);
                dialog.close();
            }
        }
    }

    // Add handleSave method as an alias for handleOk for consistency with AdminDashboardController
    public void handleSave() {
        handleOk();
    }

    public void handleCancel() {
        dialog.setResult(null);
        dialog.close();
    }
}
