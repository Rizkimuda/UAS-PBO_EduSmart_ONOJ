package com.edusmart.ui;

import com.edusmart.JavaFXApplication;
import com.edusmart.model.User;
import com.edusmart.service.UserService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RegisterController {

    private final UserService userService;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    private RadioButton studentRadio;

    @FXML
    private RadioButton instructorRadio;

    @FXML
    private Label errorLabel;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Semua kolom input wajib diisi!");
            return;
        }

        if (username.length() < 3) {
            showError("Username minimal harus 3 karakter!");
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError("Format alamat email tidak valid!");
            return;
        }

        if (password.length() < 6) {
            showError("Kata sandi minimal harus 6 karakter!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Konfirmasi kata sandi tidak cocok!");
            return;
        }

        String role = instructorRadio.isSelected() ? "ROLE_INSTRUCTOR" : "ROLE_STUDENT";

        try {
            User newUser = new User(username, email, password, role);
            userService.registerUser(newUser);

            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registrasi Berhasil");
            alert.setHeaderText("Akun EduSmart Berhasil Dibuat");
            alert.setContentText("Akun Anda dengan username '" + username + "' telah berhasil didaftarkan. Silakan masuk untuk memulai.");
            alert.showAndWait();

            // Navigate back to login
            navigateToLogin(null);

        } catch (IllegalArgumentException e) {
            showError(e.getMessage().equals("Username already exists") ? "Username sudah terdaftar! Gunakan username lain." : e.getMessage());
        } catch (Exception e) {
            showError("Terjadi kesalahan sistem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void navigateToLogin(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(JavaFXApplication.getContext()::getBean);
            Parent root = loader.load();
            JavaFXApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            showError("Gagal memuat halaman masuk: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
