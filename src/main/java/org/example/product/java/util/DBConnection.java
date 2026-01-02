package org.example.product.java.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ✅ Single reusable connection instance
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {

                // ✅ Load environment variables
                String url = System.getenv("DBLink");
                String user = System.getenv("DBUSER");
                String pass = System.getenv("PASSWORD");

                // ✅ Validate environment variables
                if (url == null || user == null || pass == null) {
                    throw new IllegalStateException(
                            "❌ Missing DB environment variables. Please set DBLink, USERNAME, and PASSWORD."
                    );
                }

                // ✅ Load MySQL driver explicitly (optional for newer JDBC versions)
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("⚠️ MySQL JDBC Driver not found. Add it to your classpath.");
                }

                // ✅ Establish database connection
                connection = DriverManager.getConnection(url, user, pass);
                // System.out.println("✅ Database connected successfully!");
            }
        } catch (SQLException e) {
            System.err.println("💥 Database connection failed: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    /**
     * Safely closes the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error closing connection: " + e.getMessage());
        }
    }
}
