package com.example.foodorderingsystem.PresentationLayer;

import com.example.foodorderingsystem.BusinessLayer.Category;
import com.example.foodorderingsystem.DataAccessLayer.CategoryDataAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.List;

public class CategoryDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private Label lblCategoryId;
    @FXML private TextField txtCategoryName;
    @FXML private ComboBox<Category> cmbParentCategory;
    @FXML private Label lblStatus;

    private Category category;
    private CategoryDataAccess categoryDataAccess;
    private boolean isNewCategory = true;
    private Dialog<Category> dialog;
    private ObservableList<Category> availableCategories;
    // Since Category doesn't have parent category ID field, we'll track it here
    private int parentCategoryId = 0;

    @FXML
    private void initialize() {
        categoryDataAccess = new CategoryDataAccess();
        availableCategories = FXCollections.observableArrayList();

        // Configure the parent category combo box
        cmbParentCategory.setItems(availableCategories);
        cmbParentCategory.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category == null ? "None" : category.getName();
            }

            @Override
            public Category fromString(String string) {
                return null; // Not needed for combo box
            }
        });

        // Add a "None" option for no parent category
        Category noneCategory = new Category();
        noneCategory.setCategoryId(0);
        noneCategory.setName("None");
        availableCategories.add(noneCategory);
        cmbParentCategory.getSelectionModel().selectFirst();

        // Load available categories
        loadCategories();
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDataAccess.getAllCategories();
            availableCategories.addAll(categories);
        } catch (SQLException e) {
            showError("Error loading categories: " + e.getMessage());
        }
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

            // Parent category is stored separately since it's not in the Category class
            // We would need to retrieve it from the database or receive it as input

            // Remove this category from available parents (to prevent circular references)
            availableCategories.removeIf(c -> c.getCategoryId() == category.getCategoryId());
        } else {
            // Creating a new category
            this.category = new Category();
            cmbParentCategory.getSelectionModel().selectFirst(); // Select "None" by default
        }
    }

    // Set the parent category ID (used when editing an existing category)
    public void setParentCategoryId(int parentId) {
        this.parentCategoryId = parentId;

        // Set the selected item in the combobox
        if (parentId > 0) {
            for (Category parentCategory : availableCategories) {
                if (parentCategory.getCategoryId() == parentId) {
                    cmbParentCategory.getSelectionModel().select(parentCategory);
                    break;
                }
            }
        } else {
            cmbParentCategory.getSelectionModel().selectFirst(); // Select "None"
        }
    }

    // Get the parent category ID (used when saving the category)
    public int getParentCategoryId() {
        return parentCategoryId;
    }

    private void updateCategoryFromForm() {
        category.setName(txtCategoryName.getText().trim());

        // Set parent category
        Category selectedParent = cmbParentCategory.getSelectionModel().getSelectedItem();
        if (selectedParent != null && selectedParent.getCategoryId() > 0) {
            parentCategoryId = selectedParent.getCategoryId();
        } else {
            parentCategoryId = 0; // No parent
        }
    }

    public boolean saveCategory() {
        try {
            if (isNewCategory) {
                boolean success = categoryDataAccess.insertCategory(category, parentCategoryId);
                return success;
            } else {
                boolean success = categoryDataAccess.updateCategory(category, parentCategoryId);
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
