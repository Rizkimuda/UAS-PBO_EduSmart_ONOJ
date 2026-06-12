package com.edusmart.ui;

import com.edusmart.JavaFXApplication;
import com.edusmart.model.User;
import com.edusmart.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showError("Username and password fields cannot be empty");
            return;
        }

        Optional<User> optionalUser = userRepository.findByUsername(username.trim());

        if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            User user = optionalUser.get();
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            DashboardController.setSessionUser(user);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                loader.setControllerFactory(JavaFXApplication.getContext()::getBean);
                Parent root = loader.load();
                JavaFXApplication.getPrimaryStage().getScene().setRoot(root);
            } catch (IOException e) {
                showError("Failed to load dashboard scene: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Invalid username or password");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
