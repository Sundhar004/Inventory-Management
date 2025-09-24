package org.example.product.java.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/inventoryDB";
    private static final String USER = "root";
    private static final String PASSWORD = "Sundu@232004";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

//        public void getConnect() {
//            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
//                if (conn != null) {
//                    System.out.println("Database connected successfully!");
//                } else {
//                    System.out.println("Failed to make connection!");
//                }
//            } catch (SQLException e) {
//                System.err.println("Connection error: " + e.getMessage());
//            }
//        }
    }

