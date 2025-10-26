package org.example.dao;

import org.example.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {

    boolean addUser(User user) throws SQLException;
    User getUserById(int id) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean deleteUser(int id) throws SQLException;
    User getUserByCredentials(String username, String password) throws SQLException;
}
