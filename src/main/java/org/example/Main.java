package org.example;

import org.example.dao.ProductDAO;
import org.example.dao.ProductDAOImpl;
import org.example.exception.DatabaseException;
import org.example.exception.InvalidInputException;
import org.example.exception.ProductNotFoundException;
import org.example.model.Product;
import org.example.util.CSVHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final ProductDAO PRODUCT_DAO = new ProductDAOImpl();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("\n===== Inventory Menu =====");
                System.out.println("1. Add Item");
                System.out.println("2. Delete Item");
                System.out.println("3. Update Item");
                System.out.println("4. Search Item");
                System.out.println("5. View All Items");
                System.out.println("6. Export to CSV");
                System.out.println("7. Exit Inventory");
                int choice = readInt("Enter your choice: ");

                switch (choice) {
                    case 1 -> addItem();
                    case 2 -> deleteItem();
                    case 3 -> updateItem();
                    case 4 -> searchItem();
                    case 5 -> viewAllItems();
                    case 6 -> exportToCSV();
                    case 7 -> {
                        System.out.println("Exiting... Goodbye!");
                        sc.close();
                        return;
                    }
                    default -> throw new InvalidInputException("Invalid menu choice! Try again.");
                }

            } catch (InvalidInputException | ProductNotFoundException | DatabaseException e) {
                System.err.println("Error: " + e.getMessage());
            } catch (SQLException | IOException e) {
                System.err.println("System Error: " + e.getMessage());
            }
        }
    }

    private static void addItem() throws SQLException, DatabaseException {
        int id = readInt("Enter ID: ");
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Category: ");
        String category = sc.nextLine();
        int quantity = readInt("Enter Quantity: ");
        double price = readDouble("Enter Price: ");

        Product p = new Product(id, name, category, quantity, price);
        PRODUCT_DAO.addProduct(p);
        System.out.println("Product added successfully!");
    }

    private static void deleteItem() throws SQLException, ProductNotFoundException {
        int id = readInt("Enter Product ID to delete: ");
        boolean deleted = PRODUCT_DAO.deleteProduct(id);
        if (deleted) {
            System.out.println("Product deleted successfully!");
        } else {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
    }

    private static void updateItem() throws SQLException, ProductNotFoundException {
        int id = readInt("Enter Product ID to update: ");
        int newQuantity = readInt("Enter new Quantity: ");
        boolean updated = PRODUCT_DAO.updateProduct(id, newQuantity);
        if (updated) {
            System.out.println("Product updated successfully!");
        } else {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
    }

    private static void searchItem() throws SQLException, ProductNotFoundException {
        System.out.println("\n===== Search Menu =====");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.println("3. Get All Products");
        int choice = readInt("Enter your choice: ");

        List<Product> matched = new ArrayList<>();

        switch (choice) {
            case 1 -> {
                int id = readInt("Enter Product ID: ");
                Product p = PRODUCT_DAO.getProductById(id);
                if (p != null) {
                    matched.add(p);
                } else {
                    throw new ProductNotFoundException("Product with ID " + id + " not found.");
                }
            }

            case 2 -> {
                System.out.print("Enter Product Name: ");
                String name = sc.nextLine().trim();
                List<Product> products = PRODUCT_DAO.getAllProducts();
                matched = products.stream()
                        .filter(p -> p.getName().equalsIgnoreCase(name))
                        .toList();
                if (matched.isEmpty()) {
                    throw new ProductNotFoundException("No product found with name: " + name);
                }
            }

            case 3 -> {
                matched = PRODUCT_DAO.getAllProducts();
                if (matched.isEmpty()) {
                    throw new ProductNotFoundException("No products available in the inventory.");
                }
            }

            default -> throw new InvalidInputException("Invalid search option!");
        }

        // 🟢 Print results in table format
        System.out.println("\n===== Search Results =====");
        System.out.printf("%-5s | %-20s | %-15s | %-10s%n", "ID", "Name", "Category", "Price");
        System.out.println("---------------------------------------------------------------");
        for (Product p : matched) {
            System.out.printf("%-5d | %-20s | %-15s | %-10.2f%n",
                    p.getId(), p.getName(), p.getCategory(), p.getPrice());
        }
        System.out.println("---------------------------------------------------------------");
    }



    private static void viewAllItems() throws SQLException {
        List<Product> products = PRODUCT_DAO.getAllProducts();
        System.out.println("\n===== Inventory Items =====");
        System.out.printf("%-5s | %-15s | %-10s | %-8s | %-10s%n",
                "ID", "Name", "Category", "Qty", "Price");
        System.out.println("------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-5d | %-15s | %-10s | %-8d | %-10.2f%n",
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
        }
    }

    private static void exportToCSV() throws SQLException, IOException {
        List<Product> products = PRODUCT_DAO.getAllProducts();
        CSVHelper.saveProducts(products);
        System.out.println("Data exported to products.csv successfully!");
    }

    // ================== SAFE INPUT HELPERS ==================

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine(); // consume leftover newline
                return value;
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // clear bad input
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                double value = sc.nextDouble();
                sc.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Please enter a decimal number.");
                sc.nextLine(); // clear bad input
            }
        }
    }
}
