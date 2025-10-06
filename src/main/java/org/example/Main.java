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
                System.out.println("\n✨===== Inventory Menu =====✨");
                System.out.println("1️. Add Item");
                System.out.println("2️. Delete Item");
                System.out.println("3️. Update Item");
                System.out.println("4️. Search Item");
                System.out.println("5️. View All Items");
                System.out.println("6️. Export to CSV");
                System.out.println("7️. Exit Inventory");
                int choice = readInt("👉 Enter your choice: ");

                switch (choice) {
                    case 1 -> addItem();
                    case 2 -> deleteItem();
                    case 3 -> updateItem();
                    case 4 -> searchItem();
                    case 5 -> viewAllItems();
                    case 6 -> exportToCSV();
                    case 7 -> {
                        System.out.println("👋 Exiting... Goodbye!");
                        sc.close();
                        return;
                    }
                    default -> throw new InvalidInputException("⚠️ Invalid menu choice! Try again.");
                }

            } catch (InvalidInputException | ProductNotFoundException | DatabaseException e) {
                System.err.println("❌ Error: " + e.getMessage());
            } catch (SQLException | IOException e) {
                System.err.println("💥 System Error: " + e.getMessage());
            }
        }
    }

    // ============ ADD ITEM ============
    private static void addItem() throws SQLException, DatabaseException {
        System.out.println("\n🆕 Add New Product");

        int id = readInt("🔑 Enter ID: ");
        System.out.print("🏷️ Enter Name: ");
        String name = sc.nextLine().trim();
        System.out.print("📂 Enter Category: ");
        String category = sc.nextLine().trim();
        int quantity = readInt("📦 Enter Quantity: ");
        double price = readDouble("💲 Enter Price: ");

        validateInputs(id, name, category, quantity, price);

        Product p = new Product(id, name, category, quantity, price);
        PRODUCT_DAO.addProduct(p);
        System.out.println("✅ Product added successfully!");
    }

    // ============ DELETE ITEM ============
    private static void deleteItem() throws SQLException, ProductNotFoundException {
        System.out.println("\n🗑️===== Delete Menu =====");
        System.out.println("1️. Delete by ID");
        System.out.println("2️. Delete by Name");
        System.out.println("3️. Delete by Category");
        int choice = readInt("👉 Enter your choice: ");

        switch (choice) {
            case 1 -> {
                int id = readInt("🔑 Enter Product ID: ");
                boolean deleted = PRODUCT_DAO.deleteProduct(id);
                if (deleted) {
                    System.out.println("✅ Product with ID " + id + " deleted successfully.");
                } else {
                    throw new ProductNotFoundException("⚠️ Product with ID " + id + " not found.");
                }
            }
            case 2 -> {
                System.out.print("🏷️ Enter Product Name: ");
                String name = sc.nextLine().trim();
                List<Product> products = PRODUCT_DAO.getAllProducts();
                List<Product> toDelete = products.stream()
                        .filter(p -> p.getName().equalsIgnoreCase(name))
                        .toList();

                if (toDelete.isEmpty()) {
                    throw new ProductNotFoundException("⚠️ No product found with name: " + name);
                } else {
                    System.out.print("❓ Are you sure you want to delete ALL products named '" + name + "'? (y/n): ");
                    String confirm = sc.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                        for (Product p : toDelete) {
                            PRODUCT_DAO.deleteProduct(p.getId());
                        }
                        System.out.println("✅ Deleted all products with name '" + name + "'.");
                    } else {
                        System.out.println("❎ Deletion cancelled.");
                    }
                }
            }
            case 3 -> {
                System.out.print("📂 Enter Product Category: ");
                String category = sc.nextLine().trim();
                List<Product> products = PRODUCT_DAO.getAllProducts();
                List<Product> toDelete = products.stream()
                        .filter(p -> p.getCategory().equalsIgnoreCase(category))
                        .toList();

                if (toDelete.isEmpty()) {
                    throw new ProductNotFoundException("⚠️ No products found in category: " + category);
                } else {
                    System.out.print("❓ Are you sure you want to delete ALL products in category '" + category + "'? (y/n): ");
                    String confirm = sc.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                        for (Product p : toDelete) {
                            PRODUCT_DAO.deleteProduct(p.getId());
                        }
                        System.out.println("✅ Deleted all products in category '" + category + "'.");
                    } else {
                        System.out.println("❎ Deletion cancelled.");
                    }
                }
            }
            default -> throw new InvalidInputException("⚠️ Invalid delete option!");
        }
    }


    // ============ UPDATE ITEM ============
    private static void updateItem() throws SQLException, IOException, ProductNotFoundException {
        int id = readInt("🔑 Enter Product ID to update: ");
        Product existing = PRODUCT_DAO.getProductById(id);
        if (existing == null) {
            throw new ProductNotFoundException("⚠️ Product with ID " + id + " not found.");
        }

        System.out.println("\n✏️===== Update Menu =====");
        System.out.println("1️. Update Name");
        System.out.println("2️. Update Category");
        System.out.println("3️. Update Quantity");
        System.out.println("4️. Update Price");
        System.out.println("5️. Update All Fields");
        int choice = readInt("👉 Enter your choice: ");

        switch (choice) {
            case 1 -> {
                System.out.print("🏷️ Enter new name: ");
                String newName = sc.nextLine().trim();
                existing.setName(newName);
            }
            case 2 -> {
                System.out.print("📂 Enter new category: ");
                String newCategory = sc.nextLine().trim();
                existing.setCategory(newCategory);
            }
            case 3 -> {
                int newQty = readInt("📦 Enter new quantity: ");
                existing.setQuantity(newQty);
            }
            case 4 -> {
                double newPrice = readDouble("💲 Enter new price: ");
                existing.setPrice(newPrice);
            }
            case 5 -> {
                System.out.print("🏷️ Enter new name: ");
                String newName = sc.nextLine().trim();
                System.out.print("📂 Enter new category: ");
                String newCategory = sc.nextLine().trim();
                int newQty = readInt("📦 Enter new quantity: ");
                double newPrice = readDouble("💲 Enter new price: ");
                existing = new Product(id, newName, newCategory, newQty, newPrice);
            }
            default -> throw new InvalidInputException("⚠️ Invalid update option!");
        }

        validateInputs(existing.getId(), existing.getName(),
                existing.getCategory(), existing.getQuantity(), existing.getPrice());

        PRODUCT_DAO.updateProduct(existing);
        CSVHelper.saveProducts(PRODUCT_DAO.getAllProducts());
        System.out.println("✅ Product updated successfully!");
    }

    // ============ SEARCH ITEM ============
    private static void searchItem() throws SQLException, ProductNotFoundException {
        System.out.println("\n🔍===== Search Menu =====");
        System.out.println("1️. Search by ID");
        System.out.println("2️. Search by Name");
        System.out.println("3️. Search by Category");
        System.out.println("4. Search by Price Range");
        System.out.println("5. Get All Products");
        int choice = readInt("👉 Enter your choice: ");

        List<Product> matched = new ArrayList<>();

        switch (choice) {
            case 1 -> {
                int id = readInt("🔑 Enter Product ID: ");
                Product p = PRODUCT_DAO.getProductById(id);
                if (p != null) {
                    matched.add(p);
                } else {
                    throw new ProductNotFoundException("⚠️ Product with ID " + id + " not found.");
                }
            }
            case 2 -> {
                System.out.print("🏷️ Enter Product Name: ");
                String name = sc.nextLine().trim();
                List<Product> products = PRODUCT_DAO.getAllProducts();
                matched = products.stream()
                        .filter(p -> p.getName().equalsIgnoreCase(name))
                        .toList();
                if (matched.isEmpty()) {
                    throw new ProductNotFoundException("⚠️ No product found with name: " + name);
                }
            }
            case 3 -> {
                System.out.print("📂 Enter Product Category: ");
                String category = sc.nextLine().trim();
                List<Product> products = PRODUCT_DAO.getAllProducts();
                matched = products.stream()
                        .filter(p -> p.getCategory().equalsIgnoreCase(category))
                        .toList();
                if (matched.isEmpty()) {
                    throw new ProductNotFoundException("⚠️ No products found in category: " + category);
                }
            }

            case 4 -> {
                double minPrice = readDouble("💲 Enter minimum price: ");
                double maxPrice = readDouble("💲 Enter maximum price: ");
                matched = PRODUCT_DAO.getAllProducts().stream()
                        .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                        .toList();
            }

            case 5 -> {
                matched = PRODUCT_DAO.getAllProducts();
                if (matched.isEmpty()) {
                    throw new ProductNotFoundException("⚠️ No products available in the inventory.");
                }
            }
            default -> throw new InvalidInputException("⚠️ Invalid search option!");
        }

        // Print results in table format
        System.out.println("\n📊===== Search Results =====");
        System.out.printf("%-5s | %-20s | %-15s | %-8s | %-10s%n",
                "ID", "Name", "Category", "Qty", "Price");
        System.out.println("---------------------------------------------------------------");
        for (Product p : matched) {
            System.out.printf("%-5d | %-20s | %-15s | %-8d | %-10.2f%n",
                    p.getId(), p.getName(), p.getCategory(),
                    p.getQuantity(), p.getPrice());
        }
        System.out.println("---------------------------------------------------------------");
    }

    // ============ VIEW ALL ============
    private static void viewAllItems() throws SQLException {
        List<Product> products = PRODUCT_DAO.getAllProducts();
        System.out.println("\n📦===== Inventory Items =====");
        System.out.printf("%-5s | %-15s | %-10s | %-8s | %-10s%n",
                "ID", "Name", "Category", "Qty", "Price");
        System.out.println("------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-5d | %-15s | %-10s | %-8d | %-10.2f%n",
                    p.getId(), p.getName(), p.getCategory(),
                    p.getQuantity(), p.getPrice());
        }
    }

    // ============ EXPORT ============
    private static void exportToCSV() throws SQLException, IOException {
        List<Product> products = PRODUCT_DAO.getAllProducts();
        CSVHelper.saveProducts(products);
        System.out.println("📂 Data exported to products.csv successfully!");
    }

    // ============ VALIDATION ============
    private static void validateInputs(int id, String name, String category, int qty, double price) {
        if (id <= 0) throw new InvalidInputException("🚫 ID must be positive!");
        if (name == null || name.isBlank()) throw new InvalidInputException("🚫 Name cannot be empty!");
        if (category == null || category.isBlank()) throw new InvalidInputException("🚫 Category cannot be empty!");
        if (qty < 0) throw new InvalidInputException("🚫 Quantity cannot be negative!");
        if (price < 0) throw new InvalidInputException("🚫 Price cannot be negative!");

    }

    // ============ SAFE INPUT HELPERS ============
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine(); // consume newline
                return value;
            } else {
                System.out.println("⚠️ Invalid input. Please enter a number.");
                sc.nextLine(); // clear invalid input
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                double value = sc.nextDouble();
                sc.nextLine(); // consume newline
                return value;
            } else {
                System.out.println("⚠️ Invalid input. Please enter a decimal number.");
                sc.nextLine(); // clear invalid input
            }
        }
    }
}


