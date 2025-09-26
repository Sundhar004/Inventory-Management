package org.example.dao;

import org.example.model.Product;
import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    void addProduct(Product product) throws SQLException;
    List<Product> getAllProducts() throws SQLException;
    Product getProductById(int id) throws SQLException;

    // Update options
    boolean updateProduct(Product product) throws SQLException;

    boolean deleteProduct(int id) throws SQLException;

}
