package org.example.dao;

import org.example.model.Product;
import org.example.product.java.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductDAOImpl implements ProductDAO {

    private final Scanner sc = new Scanner(System.in);

    // === CORE DAO METHODS ===

    @Override
    public boolean addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, category, quantity, price, threshold) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getThreshold());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        }
        return list;
    }

    @Override
    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateProduct(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, category=?, quantity=?, price=?, threshold=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getThreshold());
            ps.setInt(6, p.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProduct(rs));
                }
            }
        }
        return list;
    }

    // === INTERACTIVE METHODS (USED BY APP) ===

    public void addProductFromInput() throws SQLException {
        System.out.print("📝 Product Name: ");
        String name = sc.nextLine();

        System.out.print("📂 Category: ");
        String category = sc.nextLine();

        System.out.print("🔢 Quantity: ");
        int qty = sc.nextInt();

        System.out.print("💰 Price: ");
        double price = sc.nextDouble();

        System.out.print("⚠️ Threshold: ");
        int threshold = sc.nextInt();
        sc.nextLine();

        Product p = new Product(name, category, qty, price);
        p.setThreshold(threshold);

        if (addProduct(p)) {
            System.out.println("✅ Product added successfully!");
        } else {
            System.out.println("❌ Failed to add product.");
        }
    }

    public void searchProductById() throws SQLException {
        System.out.print("🔍 Enter Product ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        Product p = getProductById(id);
        if (p != null) {
            printHeader();
            printProduct(p);
        } else {
            System.out.println("⚠️ Product not found.");
        }
    }

    public void updateProductById() throws SQLException {
        System.out.print("🔄 Enter Product ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();
        Product p = getProductById(id);
        if (p == null) {
            System.out.println("⚠️ Product not found.");
            return;
        }

        System.out.print("📝 New Name (" + p.getName() + "): ");
        String name = sc.nextLine();
        System.out.print("📂 New Category (" + p.getCategory() + "): ");
        String category = sc.nextLine();
        System.out.print("🔢 New Quantity (" + p.getQuantity() + "): ");
        int qty = sc.nextInt();
        System.out.print("💰 New Price (" + p.getPrice() + "): ");
        double price = sc.nextDouble();
        System.out.print("⚠️ New Threshold (" + p.getThreshold() + "): ");
        int threshold = sc.nextInt();
        sc.nextLine();

        p.setName(name.isEmpty() ? p.getName() : name);
        p.setCategory(category.isEmpty() ? p.getCategory() : category);
        p.setQuantity(qty);
        p.setPrice(price);
        p.setThreshold(threshold);

        if (updateProduct(p)) {
            System.out.println("✅ Product updated successfully!");
        } else {
            System.out.println("❌ Failed to update product.");
        }
    }

    public void deleteProductById() throws SQLException {
        System.out.print("🗑 Enter Product ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();
        if (deleteProduct(id)) {
            System.out.println("✅ Product deleted successfully!");
        } else {
            System.out.println("❌ Product not found or deletion failed.");
        }
    }

    public void filterByPriceRange() throws SQLException {
        System.out.print("💲 Enter minimum price: ");
        double min = sc.nextDouble();
        System.out.print("💲 Enter maximum price: ");
        double max = sc.nextDouble();
        sc.nextLine();

        List<Product> filtered = getProductsByPriceRange(min, max);
        if (filtered.isEmpty()) {
            System.out.println("⚠️ No products found in this price range.");
            return;
        }

        printHeader();
        filtered.forEach(this::printProduct);
    }

    // === HELPER METHODS ===

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("price")
        );
        p.setThreshold(rs.getInt("threshold")); // ✅ Add this line
        return p;
    }

    private void printHeader() {
        System.out.printf("%-5s %-15s %-15s %-10s %-10s %-10s%n",
                "ID", "Name", "Category", "Quantity", "Price", "Threshold");
        System.out.println("-----------------------------------------------------------------");
    }

    private void printProduct(Product p) {
        System.out.printf("%-5d %-15s %-15s %-10d %-10.2f %-10d%n",
                p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice(), p.getThreshold());
    }
}
