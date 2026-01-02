package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.dao.ProductDAO;
import org.example.dao.ProductDAOImpl;
import org.example.model.Product;

public class AdminDashboardController {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> thresholdColumn;

    private ProductDAO productDAO;
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            productDAO = new ProductDAOImpl();
        } catch (Exception e) {
            showAlert("Initialization Error",
                    "Failed to connect to database. check DBLink/DBUSER/PASSWORD env vars.\nError: " + e.getMessage());
        }

        // Create columns programmatically if not in FXML
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> thrCol = new TableColumn<>("Threshold");
        thrCol.setCellValueFactory(new PropertyValueFactory<>("threshold"));

        productTable.getColumns().clear();
        productTable.getColumns().addAll(idCol, nameCol, catCol, qtyCol, priceCol, thrCol);
        productTable.setItems(productList);

        loadProducts();
    }

    private void loadProducts() {
        if (productDAO == null)
            return;
        try {
            productList.setAll(productDAO.getAllProducts());
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProduct() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details");

        ButtonType addType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField category = new TextField();
        category.setPromptText("Category");
        TextField quantity = new TextField();
        quantity.setPromptText("Quantity");
        TextField price = new TextField();
        price.setPromptText("Price");
        TextField threshold = new TextField();
        threshold.setPromptText("Threshold");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(category, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantity, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(price, 1, 3);
        grid.add(new Label("Threshold:"), 0, 4);
        grid.add(threshold, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addType) {
                try {
                    Product p = new Product(
                            name.getText(),
                            category.getText(),
                            Integer.parseInt(quantity.getText()),
                            Double.parseDouble(price.getText()));
                    p.setThreshold(Integer.parseInt(threshold.getText()));
                    return p;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(product -> {
            try {
                if (productDAO.addProduct(product)) {
                    loadProducts();
                    showAlert("Success", "Product added successfully!");
                } else {
                    showAlert("Error", "Failed to add product");
                }
            } catch (Exception e) {
                showAlert("Error", "Database error: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleViewProducts() {
        loadProducts();
    }

    @FXML
    private void handleUpdateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a product to update");
            return;
        }

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Update Product");
        dialog.setHeaderText("Update details for " + selected.getName());

        ButtonType updateType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField(selected.getName());
        TextField category = new TextField(selected.getCategory());
        TextField quantity = new TextField(String.valueOf(selected.getQuantity()));
        TextField price = new TextField(String.valueOf(selected.getPrice()));
        TextField threshold = new TextField(String.valueOf(selected.getThreshold()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(category, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantity, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(price, 1, 3);
        grid.add(new Label("Threshold:"), 0, 4);
        grid.add(threshold, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateType) {
                try {
                    selected.setName(name.getText());
                    selected.setCategory(category.getText());
                    selected.setQuantity(Integer.parseInt(quantity.getText()));
                    selected.setPrice(Double.parseDouble(price.getText()));
                    selected.setThreshold(Integer.parseInt(threshold.getText()));
                    return selected;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(product -> {
            try {
                if (productDAO.updateProduct(product)) {
                    loadProducts();
                    showAlert("Success", "Product updated successfully!");
                } else {
                    showAlert("Error", "Failed to update product");
                }
            } catch (Exception e) {
                showAlert("Error", "Database error: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a product to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (productDAO.deleteProduct(selected.getId())) {
                        loadProducts();
                    } else {
                        showAlert("Error", "Failed to delete product");
                    }
                } catch (Exception e) {
                    showAlert("Error", "Database error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleSendAlert() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Send Threshold Alert");
        dialog.setHeaderText("Enter recipient email for stock alerts:");
        dialog.setContentText("Email:");

        dialog.showAndWait().ifPresent(email -> {
            if (email != null && !email.trim().isEmpty()) {
                org.example.service.StockAlertService.sendLowStockAlerts(email);
                showAlert("Success", "Stock alerts sent to " + email);
            } else {
                showAlert("Warning", "Email cannot be empty");
            }
        });
    }

    @FXML
    private void handleSendReport() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Send Product Report");
        dialog.setHeaderText("Enter recipient email for product report:");
        dialog.setContentText("Email:");

        dialog.showAndWait().ifPresent(email -> {
            if (email != null && !email.trim().isEmpty()) {
                org.example.service.EmailService.sendProductReport(
                        email,
                        "📦 Inventory Product Report",
                        "Attached is the latest inventory report.");
                showAlert("Success", "Report sent to " + email);
            } else {
                showAlert("Warning", "Email cannot be empty");
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void logout() throws Exception {
        Stage stage = (Stage) productTable.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        stage.setScene(new Scene(loader.load()));
    }
}
