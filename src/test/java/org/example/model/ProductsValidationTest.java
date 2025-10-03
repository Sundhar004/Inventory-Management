package org.example.model;

import org.example.exception.InvalidInputException;
import org.junit.Assert;
import org.junit.Test;

public class ProductsValidationTest {

    @Test
    public void testValidateProductCreation() {
        Product p = new Product(1, "Mouse", "Electronics", 10, 300);
        Assert.assertEquals(1, p.getId());
        Assert.assertEquals("Mouse", p.getName());
        Assert.assertEquals("Electronics", p.getCategory());
        Assert.assertEquals(10, p.getQuantity());
        Assert.assertEquals(300, p.getPrice(), 0.001);
    }

    @Test
    public void testInvalidPriceThrowsException() {
        try {
            new Product(1, "Mouse", "Electronics", 10, -300);
            Assert.fail("Expected InvalidInputException was not thrown");
        } catch (InvalidInputException ex) {
            Assert.assertEquals("🚫 Price must be greater than 0", ex.getMessage());
        }
    }

    @Test
    public void testInvalidQuantityThrowsException() {
        try {
            new Product(2, "Keyboard", "Electronics", -5, 500);
            Assert.fail("Expected InvalidInputException was not thrown");
        } catch (InvalidInputException ex) {
            Assert.assertEquals("🚫 Quantity cannot be negative!", ex.getMessage());
        }
    }

    @Test
    public void testInvalidNameThrowsException() {
        try {
            new Product(3, "", "Electronics", 5, 100);
            Assert.fail("Expected InvalidInputException was not thrown");
        } catch (InvalidInputException ex) {
            Assert.assertEquals("🚫 Name cannot be empty!", ex.getMessage());
        }
    }

    @Test
    public void testInvalidCategoryThrowsException() {
        try {
            new Product(4, "Laptop", "", 2, 2000);
            Assert.fail("Expected InvalidInputException was not thrown");
        } catch (InvalidInputException ex) {
            Assert.assertEquals("🚫 Category cannot be empty!", ex.getMessage());
        }
    }

    @Test
    public void testInvalidIdThrowsException() {
        try {
            new Product(0, "Monitor", "Electronics", 3, 1500);
            Assert.fail("Expected InvalidInputException was not thrown");
        } catch (InvalidInputException ex) {
            Assert.assertEquals("🚫 ID must be positive!", ex.getMessage());
        }
    }
}
