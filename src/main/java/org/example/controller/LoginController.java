package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.service.UserService;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        User user = userService.login(
                usernameField.getText(),
                passwordField.getText());

        if (user == null) {
            errorLabel.setText("❌ Invalid credentials");
            return;
        }

        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            if ("admin".equalsIgnoreCase(user.getRole())) {
                loadScene(stage, "/fxml/AdminDashboard.fxml");
            } else {
                loadScene(stage, "/fxml/UserDashboard.fxml");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            errorLabel.setText("Err: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            errorLabel.setTooltip(new Tooltip(e.getMessage()));
        }
    }

    @FXML
    private void openRegister() throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        loadScene(stage, "/fxml/RegisterView.fxml");
    }

    private void loadScene(Stage stage, String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        stage.setScene(new Scene(loader.load(), 500, 400));
    }
}
