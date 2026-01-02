package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.service.OTPService;
import org.example.service.UserService;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> roleCombo;
    @FXML
    private TextField otpField;
    @FXML
    private Label statusLabel;

    private final UserService userService = new UserService();
    private boolean otpSent = false;

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("user", "admin");
    }

    @FXML
    private void sendOtp() {
        String email = emailField.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            statusLabel.setText("⚠️ Enter a valid email.");
            return;
        }
        OTPService.generateOTP(email);
        otpSent = true;
        statusLabel.setText("✅ OTP sent to your email.");
    }

    @FXML
    private void handleRegister() {
        if (!otpSent) {
            statusLabel.setText("⚠️ Please request OTP first.");
            return;
        }

        String email = emailField.getText().trim();
        String enteredOTP = otpField.getText().trim();
        if (!OTPService.validateOTP(email, enteredOTP)) {
            statusLabel.setText("❌ Invalid OTP.");
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleCombo.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            statusLabel.setText("⚠️ All fields are required.");
            return;
        }

        userService.register(username, password, role);
        statusLabel.setText("✅ Registration successful! Go back to Login.");
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml")));
            stage.setScene(scene);
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}
