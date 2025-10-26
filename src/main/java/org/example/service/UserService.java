package org.example.service;

import org.example.dao.UserDAO;
import org.example.dao.UserDAOImpl;
import org.example.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAOImpl();

    // ✅ Register new user
    public void register(String username, String password, String role) {
        try {
            User user = new User(username, password, role);
            boolean success = userDAO.addUser(user);

            if (success)
                System.out.print("");
            else
                System.out.println("⚠️ Registration failed. Try again.");

        } catch (SQLException e) {
            System.out.println("💥 Database Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Validation Error: " + e.getMessage());
        }
    }

    // ✅ Login user
    public User login(String username, String password) {
        try {
            User user = userDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
        } catch (SQLException e) {
            System.out.println("💥 Database Error: " + e.getMessage());
        }
        return null;
    }

    // ✅ View all users
    public void viewAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("⚠️ No users found.");
            } else {
                System.out.printf("%-5s %-15s %-15s%n", "ID", "Username", "Role");
                System.out.println("--------------------------------------");
                for (User user : users) {
                    System.out.printf("%-5d %-15s %-15s%n", user.getId(), user.getUsername(), user.getRole());
                }
            }
        } catch (SQLException e) {
            System.out.println("💥 Error fetching users: " + e.getMessage());
        }
    }
}
