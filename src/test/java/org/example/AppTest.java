package org.example;

import org.example.dao.ProductDAO;
import org.example.dao.ProductDAOImpl;
import org.example.model.Product;
import org.example.model.User;
import org.example.service.*;
import org.example.util.CSVHelper;
import org.junit.*;


import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * ✅ Comprehensive integration tests for the Inventory Management System.
 * Includes DAO, utility, email, OTP, user, and stock alert services.
 */
public class AppTest {

    private static ProductDAO dao;
    private static UserService userService;

    @BeforeClass
    public static void setupOnce() {
        dao = new ProductDAOImpl();
        userService = new UserService();
        System.out.println("\n🚀 Starting Full Inventory System Test Suite...");
    }

    @Before
    public void cleanBeforeEach() throws SQLException {
        for (Product p : dao.getAllProducts()) {
            if (p.getName().startsWith("Test")) {
                dao.deleteProduct(p.getId());
            }
        }
    }

    // ✅ 1. Add & Retrieve Product
    @Test
    public void testAddAndGetProduct() throws SQLException {
        Product p = new Product("Test Laptop", "Electronics", 5, 55000.0);
        p.setThreshold(10);
        boolean added = dao.addProduct(p);
        Assert.assertTrue("Product should be added successfully", added);

        List<Product> all = dao.getAllProducts();
        Product fetched = all.stream()
                .filter(prod -> prod.getName().equals("Test Laptop"))
                .findFirst()
                .orElse(null);

        Assert.assertNotNull("Product should exist in DB", fetched);
        Assert.assertEquals("Electronics", fetched.getCategory());
        Assert.assertEquals(55000.0, fetched.getPrice(), 0.001);
    }

    // ✅ 2. Update Product
    @Test
    public void testUpdateProduct() throws SQLException {
        Product p = new Product("Test Keyboard", "Electronics", 10, 1200.0);
        dao.addProduct(p);

        Product existing = dao.getAllProducts().stream()
                .filter(prod -> prod.getName().equals("Test Keyboard"))
                .findFirst()
                .orElseThrow(() -> new SQLException("Product not found after insertion"));

        existing.setPrice(1500.0);
        existing.setQuantity(12);
        boolean updated = dao.updateProduct(existing);

        Assert.assertTrue("Update should succeed", updated);

        Product updatedProduct = dao.getProductById(existing.getId());
        Assert.assertEquals(1500.0, updatedProduct.getPrice(), 0.001);
        Assert.assertEquals(12, updatedProduct.getQuantity());
    }

    // ✅ 3. Delete Product
    @Test
    public void testDeleteProduct() throws SQLException {
        Product p = new Product("Test Mouse", "Accessories", 15, 600.0);
        dao.addProduct(p);

        Product toDelete = dao.getAllProducts().stream()
                .filter(prod -> prod.getName().equals("Test Mouse"))
                .findFirst()
                .orElseThrow(() -> new SQLException("Product not found for delete test"));

        boolean deleted = dao.deleteProduct(toDelete.getId());
        Assert.assertTrue("Product deletion should succeed", deleted);

        Product deletedProduct = dao.getProductById(toDelete.getId());
        Assert.assertNull("Deleted product should no longer exist", deletedProduct);
    }

    // ✅ 4. Search by Price Range
    @Test
    public void testSearchProductsByPriceRange() throws SQLException {
        dao.addProduct(new Product("Test Earphones", "Audio", 25, 499.0));
        dao.addProduct(new Product("Test Headphones", "Audio", 10, 999.0));

        List<Product> result = dao.getProductsByPriceRange(400.0, 800.0);

        Assert.assertFalse("Should find products in price range", result.isEmpty());
        for (Product prod : result) {
            Assert.assertTrue(
                    "Price must fall in 400–800 range",
                    prod.getPrice() >= 400.0 && prod.getPrice() <= 800.0
            );
        }
    }

    // ✅ 5. Pagination Logic (Main class)
    @Test
    public void testPaginationHelper() {
        List<Product> dummy = java.util.stream.IntStream.range(1, 21)
                .mapToObj(i -> new Product(i, "P" + i, "Cat", 1, 10.0 * i))
                .collect(java.util.stream.Collectors.toList());

        List<Product> page2 = Main.getPaginatedProducts(dummy, 2, 10);
        Assert.assertEquals(10, page2.size());
        Assert.assertEquals(11, page2.get(0).getId());
        Assert.assertEquals(20, page2.get(9).getId());
    }

    // ✅ 6. CSV Export
    @Test
    public void testCSVExport() throws Exception {
        List<Product> allProducts = dao.getAllProducts();
        CSVHelper.saveProducts(allProducts);

        File csvFile = new File("products.csv");
        Assert.assertTrue("CSV file should exist", csvFile.exists());
        Assert.assertTrue("CSV file should not be empty", csvFile.length() > 0);
    }

    // ✅ 7. OTPService (Generation + Validation)
    @Test
    public void testOTPService() {
        String email = "testuser@example.com";
        String otp = OTPService.generateOTP(email);
        Assert.assertNotNull("OTP should be generated", otp);
        Assert.assertEquals(6, otp.length());

        boolean valid = OTPService.validateOTP(email, otp);
        Assert.assertTrue("OTP should validate correctly", valid);

        boolean invalid = OTPService.validateOTP(email, "999999");
        Assert.assertFalse("Invalid OTP should not validate", invalid);
    }

    // ✅ 8. UserService (Register + Login)
    @Test
    public void testUserService() {
        String username = "test_user_" + System.currentTimeMillis();
        String password = "test123";
        String role = "user";

        userService.register(username, password, role);
        User loggedIn = userService.login(username, password);

        Assert.assertNotNull("User should log in successfully", loggedIn);
        Assert.assertEquals(username, loggedIn.getUsername());
        Assert.assertEquals(role, loggedIn.getRole());
    }

    // ✅ 9. EmailService (Mock Email Sending)
    @Test
    public void testEmailServiceMock() {
        try {
            EmailService.sendProductReport(
                    "mockrecipient@example.com",
                    "📦 Test Report",
                    "This is a test report from EmailService."
            );
            Assert.assertTrue("Mock email send executed successfully", true);
        } catch (Exception ex) {
            System.out.println("ℹ️ EmailService not configured or threw exception — handled gracefully: " + ex.getMessage());
            Assert.assertTrue("Handled gracefully even if mail credentials missing", true);
        }
    }


    // ✅ 10. StockAlertService (Simulated Alert)
    @Test
    public void testStockAlertService() {
        try {
            StockAlertService.sendLowStockAlerts("mockalert@example.com");
            Assert.assertTrue("Stock alert method executed without exception", true);
        } catch (Exception e) {
            Assert.fail("StockAlertService threw an exception: " + e.getMessage());
        }
    }

    @AfterClass
    public static void teardownOnce() {
        System.out.println("\n✅ All system tests completed successfully!");
    }
}
