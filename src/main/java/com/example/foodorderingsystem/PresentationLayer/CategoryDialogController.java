package com.example.foodorderingsystem.PresentationLayer;

import java.sql.SQLException;

import com.example.foodorderingsystem.BusinessLayer.Category;
import com.example.foodorderingsystem.DataAccessLayer.CategoryDataAccess;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CategoryDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private Label lblCategoryId;
    @FXML private TextField txtCategoryName;
    @FXML private Label lblStatus;

    private Category category;
    private CategoryDataAccess categoryDataAccess;
    private boolean isNewCategory = true;
    private Dialog<Category> dialog;

    @FXML
    private void initialize() {
        categoryDataAccess = new CategoryDataAccess();
    }

    public void setDialog(Dialog<Category> dialog) {
        this.dialog = dialog;

        // Set up the result converter for the dialog
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (validateInput()) {
                    updateCategoryFromForm();
                    if (saveCategory()) {
                        return category;
                    }
                }
                return null;
            }
            return null;
        });
    }

    public void setCategory(Category category) {
        this.category = category;
        this.isNewCategory = (category == null || category.getCategoryId() == 0);

        if (!isNewCategory) {
            // Populate the form fields with the category data
            lblCategoryId.setText(String.valueOf(category.getCategoryId()));
            txtCategoryName.setText(category.getName());
        } else {
            // Creating a new category
            this.category = new Category();
        }
    }

    private void updateCategoryFromForm() {
        category.setName(txtCategoryName.getText().trim());
    }

    public boolean saveCategory() {
        try {
            if (isNewCategory) {
                boolean success = categoryDataAccess.insertCategory(category);
                return success;
            } else {
                boolean success = categoryDataAccess.updateCategory(category);
                return success;
            }
        } catch (SQLException e) {
            showError("Error saving category: " + e.getMessage());
            return false;
        }
    }

    private boolean validateInput() {
        lblStatus.setText("");

        if (txtCategoryName.getText().trim().isEmpty()) {
            showError("Category name is required");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        lblStatus.setText(message);
    }

    // Method called when the OK button is clicked
    public void handleOk() {
        if (validateInput()) {
            updateCategoryFromForm();
            saveCategory();
        }
    }
}
