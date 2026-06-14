package com.edusmart.ui;

import com.edusmart.JavaFXApplication;
import com.edusmart.model.User;
import com.edusmart.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

@Component
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private SVGPath eyeIconPath;

    @FXML
    private StackPane togglePasswordButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean isPasswordVisible = false;

    private static final String EYE_OPEN_PATH = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";
    private static final String EYE_CLOSED_PATH = "M12 17c-2.76 0-5-2.24-5-5 0-.77.18-1.5.49-2.14l1.89 1.89c-.23.43-.38.92-.38 1.48 0 1.66 1.34 3 3 3 .56 0 1.05-.15 1.48-.38l1.89 1.89c-.64.31-1.37.49-2.14.49zm6.98-3.02l1.43 1.43C21.84 14.39 23 13.21 24 12c-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l1.63 1.63C11.16 6.3 11.58 6.25 12 6.25c4.08 0 7.64 2.52 9.17 6.25-.6 1.45-1.52 2.68-2.65 3.53zm-13.62-11L4.12 1.74 2.85 3.01l2.42 2.42C3.83 6.9 2.43 8.87 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l2.84 2.84 1.27-1.27-16.71-16.71zM12 8.75c1.66 0 3 1.34 3 3 0 .56-.15 1.05-.38 1.48L10.52 9.13c.43-.23.92-.38 1.48-.38z";

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @FXML
    public void initialize() {
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
    }

    @FXML
    public void togglePasswordVisibility(Event event) {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            eyeIconPath.setContent(EYE_CLOSED_PATH);
            passwordTextField.requestFocus();
            passwordTextField.positionCaret(passwordTextField.getText().length());
        } else {
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            eyeIconPath.setContent(EYE_OPEN_PATH);
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = isPasswordVisible ? passwordTextField.getText() : passwordField.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showError("Username dan kata sandi tidak boleh kosong");
            return;
        }

        Optional<User> optionalUser = userRepository.findByUsername(username.trim());
        System.out.println("DEBUG: Username entered: '" + username + "'");
        System.out.println("DEBUG: Password entered: '" + password + "'");
        System.out.flush();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("DEBUG: User found in DB: " + user.getUsername() + ", Role: " + user.getRole() + ", Hash: " + user.getPassword());
            System.out.println("DEBUG: Password matches: " + passwordMatches);
            System.out.flush();
        } else {
            System.out.println("DEBUG: User NOT found in DB!");
            try {
                System.out.println("DEBUG: Listing all users in database:");
                for (User u : userRepository.findAll()) {
                    System.out.println("  - Username: " + u.getUsername() + ", Role: " + u.getRole());
                }
            } catch (Exception ex) {
                System.out.println("DEBUG: Failed to list users: " + ex.getMessage());
            }
            System.out.flush();
        }

        if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            User user = optionalUser.get();
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            try {
                String fxmlPath;
                if ("ROLE_STUDENT".equals(user.getRole())) {
                    StudentDashboardController.setSessionUser(user);
                    fxmlPath = "/fxml/student_dashboard.fxml";
                } else {
                    DashboardController.setSessionUser(user);
                    fxmlPath = "/fxml/dashboard.fxml";
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                loader.setControllerFactory(JavaFXApplication.getContext()::getBean);
                Parent root = loader.load();
                JavaFXApplication.getPrimaryStage().getScene().setRoot(root);
            } catch (IOException e) {
                showError("Gagal memuat halaman dasbor: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Username atau kata sandi salah");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    @FXML
    public void navigateToRegister(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            loader.setControllerFactory(JavaFXApplication.getContext()::getBean);
            Parent root = loader.load();
            JavaFXApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            showError("Gagal memuat halaman registrasi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
