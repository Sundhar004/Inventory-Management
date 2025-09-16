package org.example.service;

import org.example.dao.ProductDAO;
import org.example.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class InventoryManager {
    private ProductDAO productDAO = new ProductDAO();
    private Scanner sc = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n=== Inventory Menu ===");
            System.out.println("1. Add Product");
            System.out.println("2. View All Products");
            System.out.println("3. Get Product by ID");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            try {
                switch (choice) {
                    case 1:
                        addProduct();
                        break;
                    case 2:
                        viewAllProducts();
                        break;
                    case 3:
                        getProductById();
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addProduct() throws SQLException {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Category: ");
        String category = sc.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();
        System.out.print("Enter Price: ");
        double price = sc.nextDouble();

        Product product = new Product(id, name, category, quantity, price);
        productDAO.addProduct(product);
        System.out.println("Product added successfully!");
    }

    private void viewAllProducts() throws SQLException {
        List<Product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found!");
        } else {
            products.forEach(System.out::println);
        }
    }

    private void getProductById() throws SQLException {
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        Product product = productDAO.getProductById(id);
        if (product != null) {
            System.out.println(product);
        } else {
            System.out.println(" Product not found!");
        }
    }
}
