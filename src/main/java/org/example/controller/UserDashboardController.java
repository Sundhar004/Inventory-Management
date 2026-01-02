package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.dao.ProductDAO;
import org.example.dao.ProductDAOImpl;
import org.example.model.Product;
import java.sql.SQLException;
import java.util.List;

public class UserDashboardController {

    @FXML
    private TableView<Product> productTable;
    private ProductDAO productDAO;

    @FXML
    public void initialize() {
        try {
            productDAO = new ProductDAOImpl();
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to connect to database: " + e.getMessage());
            return;
        }

        // Create columns programmatically
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        productTable.getColumns().addAll(idCol, nameCol, categoryCol, qtyCol, priceCol);

        loadProducts();
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            productTable.getItems().setAll(products);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load products");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() throws Exception {
        Stage stage = (Stage) productTable.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        stage.setScene(new Scene(loader.load()));
    }
}
