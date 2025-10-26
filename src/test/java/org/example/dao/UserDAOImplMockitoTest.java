package org.example.dao;

import org.example.model.User;
import org.example.product.java.util.DBConnection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserDAOImplMockitoTest {

    private UserDAOImpl userDAO;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @Before
    public void setUp() throws Exception {
        userDAO = new UserDAOImpl();
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    // ✅ Test addUser() method
    @Test
    public void testAddUser() throws Exception {
        User user = new User(1, "ramesh", "secure123", "admin");

        try (MockedStatic<DBConnection> dbMock = Mockito.mockStatic(DBConnection.class)) {
            dbMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1);

            boolean result = userDAO.addUser(user);
            assertTrue("User should be added successfully", result);

            verify(mockStatement, times(1)).executeUpdate();
        }
    }

    // ✅ Test getUserById() method
    @Test
    public void testGetUserById() throws Exception {
        try (MockedStatic<DBConnection> dbMock = Mockito.mockStatic(DBConnection.class)) {
            dbMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("username")).thenReturn("ramesh");
            when(mockResultSet.getString("password")).thenReturn("secure123");
            when(mockResultSet.getString("role")).thenReturn("admin");

            User fetched = userDAO.getUserById(1);

            assertNotNull("User should not be null", fetched);
            assertEquals("ramesh", fetched.getUsername());
            assertEquals("admin", fetched.getRole());
        }
    }

    // ✅ Test updateUser() method
    @Test
    public void testUpdateUser() throws Exception {
        User user = new User(2, "kumar", "updatedPass", "manager");

        try (MockedStatic<DBConnection> dbMock = Mockito.mockStatic(DBConnection.class)) {
            dbMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1);

            boolean result = userDAO.updateUser(user);
            assertTrue("Update should return true", result);

            verify(mockStatement, times(1)).executeUpdate();
        }
    }

    // ✅ Test deleteUser() method
    @Test
    public void testDeleteUser() throws Exception {
        try (MockedStatic<DBConnection> dbMock = Mockito.mockStatic(DBConnection.class)) {
            dbMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeUpdate()).thenReturn(1);

            boolean result = userDAO.deleteUser(3);
            assertTrue("Delete should return true", result);

            verify(mockStatement, times(1)).executeUpdate();
        }
    }
}
