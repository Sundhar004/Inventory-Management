package org.example;

import org.example.dao.ProductDAO;
import org.example.dao.ProductDAOImpl;
import org.example.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class AppTest {

    private ProductDAO dao;

    @Before
    public void setUp() throws SQLException {
        dao = new ProductDAOImpl();

        // Clean up test IDs before running
        try { dao.deleteProduct(100); } catch (Exception ignored) {}
        try { dao.deleteProduct(101); } catch (Exception ignored) {}
        try { dao.deleteProduct(102); } catch (Exception ignored) {}
    }

    @Test
    public void testAddAndGetProduct() throws SQLException {
        Product p = new Product(100, "Test Mouse", "Electronics", 5, 250);
        boolean added = dao.addProduct(p);

        Assert.assertTrue("Product should be added", added);

        Product fetched = dao.getProductById(100);
        Assert.assertNotNull("Product should be found", fetched);
        Assert.assertEquals("Test Mouse", fetched.getName());
        Assert.assertEquals(250, fetched.getPrice(), 0.001);
    }

    @Test
    public void testUpdateProduct() throws SQLException {
        Product p = new Product(101, "Keyboard", "Electronics", 3, 500);
        dao.addProduct(p);

        // Modify and update
        p.setPrice(600);
        boolean updated = dao.updateProduct(p);

        Assert.assertTrue("Update should succeed", updated);

        Product updatedProduct = dao.getProductById(101);
        Assert.assertEquals(600, updatedProduct.getPrice(), 0.001);
    }

    @Test
    public void testDeleteProduct() throws SQLException {
        Product p = new Product(102, "Monitor", "Electronics", 2, 3000);
        dao.addProduct(p);

        boolean deleted = dao.deleteProduct(102);
        Assert.assertTrue("Delete should succeed", deleted);

        try {
            dao.getProductById(102);
            Assert.fail("Expected SQLException after deleting product");
        } catch (SQLException ex) {
            Assert.assertTrue("Exception message should mention product not found",
                    ex.getMessage().contains("No product found"));
        }
    }
    @Test
    public void testSearchProductByPriceRange() throws SQLException {
        // Add sample data
        Product p1 = new Product(103, "Earphones", "Electronics", 10, 150);
        Product p2 = new Product(104, "Headphones", "Electronics", 8, 800);
        dao.addProduct(p1);
        dao.addProduct(p2);

        // Search products within price range 100 to 500
        List<Product> products = dao.getProductsByPriceRange(100, 500);

        Assert.assertNotNull("Search result should not be null", products);
        Assert.assertFalse("Search result should not be empty", products.isEmpty());

        for (Product p : products) {
            Assert.assertTrue(
                    "Product price should be within range 100-500",
                    p.getPrice() >= 100 && p.getPrice() <= 500
            );
        }
    }


    @Test
    public void testGetAllProducts() throws SQLException {
        List<Product> products = dao.getAllProducts();
        Assert.assertNotNull("Product list should not be null", products);
        Assert.assertTrue("Product list size should be >= 0", products.size() >= 0);
    }
}
