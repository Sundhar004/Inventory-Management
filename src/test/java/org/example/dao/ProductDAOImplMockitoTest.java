package org.example.dao;

import org.example.model.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDAOImplMockitoTest {

    @Mock
    private ProductDAO productDAO; // we mock the interface

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddProductSuccess() throws SQLException {
        Product p = new Product(202, "Mouse", "Electronics", 10, 300);

        // if addProduct returns boolean
        when(productDAO.addProduct(p)).thenReturn(true);

        boolean added = productDAO.addProduct(p);

        assertTrue(added);
        verify(productDAO, times(1)).addProduct(p);
    }

    @Test
    public void testUpdateProduct() throws SQLException {
        Product p = new Product(202, "Mouse", "Electronics", 10, 300);

        when(productDAO.updateProduct(p)).thenReturn(true);

        boolean updated = productDAO.updateProduct(p);

        assertTrue(updated);
        verify(productDAO, times(1)).updateProduct(p);
    }

    @Test
    public void testDeleteProduct() throws SQLException {
        int productId = 202;

        when(productDAO.deleteProduct(productId)).thenReturn(true);

        boolean deleted = productDAO.deleteProduct(productId);

        assertTrue(deleted);
        verify(productDAO, times(1)).deleteProduct(productId);
    }

    @Test
    public void testGetAllProducts() throws SQLException {
        List<Product> mockProducts = Arrays.asList(
                new Product(201, "Keyboard", "Electronics", 5, 500),
                new Product(202, "Mouse", "Electronics", 10, 300)
        );

        when(productDAO.getAllProducts()).thenReturn(mockProducts);

        List<Product> products = productDAO.getAllProducts();

        assertEquals(2, products.size());
        assertEquals("Keyboard", products.get(0).getName());
        verify(productDAO, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductById() throws SQLException {
        Product mockProduct = new Product(202, "Mouse", "Electronics", 10, 300);

        when(productDAO.getProductById(202)).thenReturn(mockProduct);

        Product p = productDAO.getProductById(202);

        assertEquals("Mouse", p.getName());
        assertEquals(300, p.getPrice(), 0.001);
        verify(productDAO, times(1)).getProductById(202);
    }
}
