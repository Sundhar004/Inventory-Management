 package org.example;

import org.example.dao.ProductDAOImpl;
import org.example.model.Product;
import org.example.model.User;
import org.example.service.EmailService;
import org.example.service.OTPService;
import org.example.service.StockAlertService;
import org.example.service.UserService;
import org.example.util.CSVHelper;

import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner SC = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ProductDAOImpl productDAO = new ProductDAOImpl();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== 🔐 Authentication Menu ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = readIntSafe("Enter choice: ");
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    User loggedUser = loginUser();
                    if (loggedUser != null) {
                        System.out.println("✅ Login successful! Welcome, " + loggedUser.getUsername());
                        if ("admin".equalsIgnoreCase(loggedUser.getRole())) adminMenu();
                        else userMenu();
                    }
                    break;
                case 3:
                    System.out.println("👋 Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("⚠️ Invalid choice. Enter 1, 2 or 3.");
            }
        }
    }

    // =================== REGISTER / LOGIN ===================

    private static void registerUser() {
        try {
            System.out.println("\n=== 🧾 User Registration ===");

            System.out.print("👤 Username: ");
            String username = SC.nextLine().trim();

            System.out.print("🔒 Password: ");
            String password = SC.nextLine().trim();

            System.out.print("📧 Email: ");
            String email = SC.nextLine().trim();

            System.out.print("🧩 Role (admin / user): ");
            String role = SC.nextLine().trim();

            // === Input validation ===
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role.isEmpty()) {
                System.out.println("⚠️ All fields are required. Please try again.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("⚠️ Invalid email format. Please provide a valid email.");
                return;
            }

            // === OTP generation & verification ===
            System.out.println("\n📨 Sending OTP to " + email + "...");
            OTPService.generateOTP(email);

            System.out.print("🔐 Enter the OTP sent to your email: ");
            String enteredOTP = SC.nextLine().trim();

            if (!OTPService.validateOTP(email, enteredOTP)) {
                System.out.println("❌ Registration failed: Invalid or expired OTP.");
                return;
            }

            // === Proceed with registration ===
            userService.register(username, password, role);
            System.out.println("✅ Registration successful! Welcome, " + username + " 🎉");

        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Validation error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("💥 Unexpected error during registration: " + e.getMessage());
        }
    }


    private static User loginUser() {
        try {
            System.out.print("👤 Username: ");
            String username = SC.nextLine().trim();

            System.out.print("🔒 Password: ");
            String password = SC.nextLine().trim();

            User user = userService.login(username, password);
            if (user == null) {
                System.out.println("❌ Login failed: Invalid username or password.");
            }
            return user;
        } catch (Exception e) {
            System.err.println("💥 Error during login: " + e.getMessage());
        }
        return null;
    }

    // =================== ADMIN MENU ===================
    private static void adminMenu() {
        while (true) {
            System.out.println("\n=== 🛠 ADMIN MENU ===");
            System.out.println("1. ➕ Add Product");
            System.out.println("2. 📋 View All Products");
            System.out.println("3. 🔎 Search Product by ID");
            System.out.println("4. 🔤 Search Product by Name");
            System.out.println("5. 🗂 Search Product by Category");
            System.out.println("6. 🗑 Delete Product");
            System.out.println("7. 💲 Filter Products by Price");
            System.out.println("8. 📄 Generate CSV Report");
            System.out.println("9. ✉️ Send Email Report");
            System.out.println("10. ⚠️ Send Threshold Stock Alerts");
            System.out.println("11. 🚪 Exit to Main Menu");

            int choice = readIntSafe("Enter choice: ");

            try {
                switch (choice) {
                    case 1 -> addProduct();
                    case 2 -> viewProducts();
                    case 3 -> searchProductById();
                    case 4 -> searchProductByName();
                    case 5 -> searchProductByCategory();
                    case 6 -> deleteProduct();
                    case 7 -> filterProductsByPrice();
                    case 8 -> generateCsvReport();
                    case 9 -> sendEmailReport();
                    case 10 -> sendStockAlerts();
                    case 11 -> {
                        System.out.println("🔙 Returning to main menu...");
                        return;
                    }
                    default -> System.out.println("⚠️ Invalid choice. Enter a number between 1–11.");
                }
            } catch (Exception e) {
                System.err.println("💥 Unexpected error: " + e.getMessage());
            }
        }
    }



    // =================== USER MENU ===================
    private static void userMenu() {
        while (true) {
            System.out.println("\n=== 🛒 USER MENU ===");
            System.out.println("1. 📋 View All Products");
            System.out.println("2. 🔎 Search Product by ID");
            System.out.println("3. 🔤 Search Product by Name");
            System.out.println("4. 🗂 Search Product by Category");
            System.out.println("5. 💲 Filter Products by Price");
            System.out.println("6. 📄 Generate CSV Report");
            System.out.println("7. 🚪 Exit to Main Menu");

            int choice = readIntSafe("Enter choice: ");

            try {
                switch (choice) {
                    case 1 -> viewProducts();
                    case 2 -> searchProductById();
                    case 3 -> searchProductByName();
                    case 4 -> searchProductByCategory();
                    case 5 -> filterProductsByPrice();
                    case 6 -> generateCsvReport();
                    case 7 -> {
                        System.out.println("🔙 Returning to main menu...");
                        return;
                    }
                    default -> System.out.println("⚠️ Invalid choice. Enter a number between 1–7.");
                }
            } catch (Exception e) {
                System.err.println("💥 Unexpected error: " + e.getMessage());
            }
        }
    }


    // =================== CORE ACTIONS ===================

    private static void addProduct() {
        try {
            System.out.println("\n🆕 Add New Product");

            System.out.print("🏷️ Enter Name: ");
            String name = SC.nextLine().trim();

            System.out.print("📂 Enter Category: ");
            String category = SC.nextLine().trim();

            int quantity = readIntSafe("📦 Enter Quantity: ");
            double price = readDoubleSafe("💲 Enter Price: ");
            int threshold = readIntSafe("⚠️ Enter Stock Threshold: ");

            // ✅ Input Validation
            if (name.isEmpty() || category.isEmpty()) {
                System.out.println("⚠️ Name and Category cannot be empty.");
                return;
            }
            if (quantity < 0 || price < 0 || threshold < 0) {
                System.out.println("⚠️ Quantity, Price, and Threshold must be positive numbers.");
                return;
            }

            // ✅ Create product and add to database
            Product product = new Product(name, category, quantity, price);
            product.setThreshold(threshold);

            boolean success = productDAO.addProduct(product);
            if (success) {
                System.out.println("✅ Product added successfully!");
            } else {
                System.out.println("❌ Failed to add product. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("💥 Error adding product: " + e.getMessage());
        }
    }


    private static void viewProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            printProductsTable(products);
        } catch (Exception e) {
            System.err.println("💥 Failed to view products: " + e.getMessage());
        }
    }

    // 🔍 Search by Product ID
    private static void searchProductById() {
        int id = readIntSafe("Enter product ID: ");
        try {
            Product product = productDAO.getProductById(id);
            if (product != null) {
                printProductsTable(List.of(product));
            } else {
                System.out.println("⚠️ No product found with ID " + id);
            }
        } catch (Exception e) {
            System.err.println("💥 Error searching product: " + e.getMessage());
        }
    }

    // 🔤 Search by Product Name
    private static void searchProductByName() {
        System.out.print("Enter product name: ");
        String name = SC.nextLine().trim();
        try {
            List<Product> all = productDAO.getAllProducts();
            List<Product> matched = all.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(name))
                    .collect(java.util.stream.Collectors.toList());

            if (matched.isEmpty()) {
                System.out.println("⚠️ No products found with name: " + name);
            } else {
                printProductsTable(matched);
            }
        } catch (Exception e) {
            System.err.println("💥 Error searching by name: " + e.getMessage());
        }
    }

    // 🗂 Search by Category
    private static void searchProductByCategory() {
        System.out.print("Enter category: ");
        String category = SC.nextLine().trim();
        try {
            List<Product> all = productDAO.getAllProducts();
            List<Product> matched = all.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .collect(java.util.stream.Collectors.toList());

            if (matched.isEmpty()) {
                System.out.println("⚠️ No products found in category: " + category);
            } else {
                printProductsTable(matched);
            }
        } catch (Exception e) {
            System.err.println("💥 Error searching by category: " + e.getMessage());
        }
    }


    private static void deleteProduct() {
        int id = readIntSafe("Enter product ID to delete: ");
        try {
            boolean deleted = productDAO.deleteProduct(id);
            System.out.println(deleted ? "✅ Product deleted successfully." : "⚠️ Product not found.");
        } catch (Exception e) {
            System.err.println("💥 Error deleting product: " + e.getMessage());
        }
    }

    private static void filterProductsByPrice() {
        double min = readDoubleSafe("Enter minimum price: ");
        double max = readDoubleSafe("Enter maximum price: ");
        try {
            List<Product> products = productDAO.getAllProducts()
                    .stream()
                    .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                    .collect(java.util.stream.Collectors.toList());
            printProductsTable(products);
        } catch (Exception e) {
            System.err.println("💥 Error filtering products: " + e.getMessage());
        }
    }

    private static void generateCsvReport() {
        try {
            List<Product> products = productDAO.getAllProducts();
            CSVHelper.saveProducts(products);
            System.out.println("✅ CSV report generated successfully: products.csv");
            printProductsTable(products);
        } catch (Exception e) {
            System.err.println("💥 Error generating CSV: " + e.getMessage());
        }
    }

    private static void sendEmailReport() {
        try {
            System.out.print("Enter recipient email: ");
            String toEmail = SC.nextLine().trim();

            EmailService.sendProductReport(
                    toEmail,
                    "📦 Product Report",
                    "Attached is the latest product report."
            );

            System.out.println("✅ Email report sent successfully!");
        } catch (Exception e) {
            System.err.println("💥 Failed to send email: " + e.getMessage());
        }
    }


    private static void sendStockAlerts() {
        System.out.print("Enter recipient email: ");
        String email = SC.nextLine().trim();
        StockAlertService.sendLowStockAlerts(email);
    }

    // =================== UTILITIES ===================

    private static int readIntSafe(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SC.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Please enter a valid number.");
            }
        }
    }

    private static double readDoubleSafe(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SC.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Please enter a valid decimal number.");
            }
        }
    }

    private static void printProductsTable(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("⚠️ No products found.");
            return;
        }

        System.out.printf("%-5s %-20s %-15s %-10s %-10s%n", "ID", "Name", "Category", "Qty", "Price");
        System.out.println("------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-5d %-20s %-15s %-10d %-10.2f%n",
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
        }
    }
}
