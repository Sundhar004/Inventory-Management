package org.example.dao;

import org.example.model.Product;
import org.example.product.java.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public boolean addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            st.setInt(1, product.getId());
            st.setString(2, product.getName());
            st.setString(3, product.getCategory());
            st.setInt(4, product.getQuantity());
            st.setDouble(5, product.getPrice());

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate key
                throw new SQLException("❌ Product with ID " + product.getId() + " already exists. Please use a unique ID.");
            } else if (e.getErrorCode() == 1048) { // Null value in NOT NULL column
                throw new SQLException("⚠️ One of the required fields (ID, Name, Category, Quantity, Price) is missing.");
            } else {
                throw new SQLException("💥 Failed to add product. Reason: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            throw new SQLException("💥 Could not fetch product list. Please try again. (" + e.getMessage() + ")");
        }
        return products;
    }

    @Override
    public Product getProductById(int id) throws SQLException {
        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    );
                } else {
                    throw new SQLException("🔍 No product found with ID " + id);
                }
            }

        } catch (SQLException e) {
            throw new SQLException("💥 Failed to search for product ID " + id + ". Reason: " + e.getMessage());
        }
    }

    @Override
    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, price = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, product.getName());
            st.setString(2, product.getCategory());
            st.setInt(3, product.getQuantity());
            st.setDouble(4, product.getPrice());
            st.setInt(5, product.getId());

            int rows = st.executeUpdate();
            if (rows == 0) {
                throw new SQLException("⚠️ Update failed. No product found with ID " + product.getId());
            }
            return true;

        } catch (SQLException e) {
            throw new SQLException("💥 Could not update product ID " + product.getId() + ". Reason: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteProduct(int id) throws SQLException {
        String query = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            st.setInt(1, id);
            int rows = st.executeUpdate();
            if (rows == 0) {
                throw new SQLException("⚠️ Cannot delete. No product found with ID " + id);
            }
            return true;

        } catch (SQLException e) {
            throw new SQLException("💥 Could not delete product ID " + id + ". Reason: " + e.getMessage());
        }
    }
}
