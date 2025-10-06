package org.example.dao;

import org.example.model.Product;
import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    boolean addProduct(Product product) throws SQLException;
    boolean updateProduct(Product product) throws SQLException;
    boolean deleteProduct(int id) throws SQLException;
    Product getProductById(int id) throws SQLException;
    List<Product> getAllProducts() throws SQLException;

    // ✅ New method
    List<Product> getProductsByPriceRange(double minPrice, double maxPrice) throws SQLException;
}
