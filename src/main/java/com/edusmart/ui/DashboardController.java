package com.edusmart.ui;

import com.edusmart.JavaFXApplication;
import com.edusmart.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class DashboardController {

    private static User sessionUser;

    @FXML
    private Label headerUsernameLabel;

    @FXML
    private Label headerRoleLabel;

    @FXML
    private Circle avatarCircle;

    @FXML
    private Button logoutButton;

    @FXML
    private Label profileUsernameLabel;

    @FXML
    private Label profileEmailLabel;

    @FXML
    private Label profileIdLabel;

    @FXML
    private Label profileCreatedLabel;

    @FXML
    private Label rawRoleLabel;

    @FXML
    private Label polymorphicRoleLabel;

    public static void setSessionUser(User user) {
        sessionUser = user;
    }

    public static User getSessionUser() {
        return sessionUser;
    }

    @FXML
    public void initialize() {
        if (sessionUser != null) {
            headerUsernameLabel.setText(sessionUser.getUsername());
            headerRoleLabel.setText(sessionUser.getRole());
            profileUsernameLabel.setText(sessionUser.getUsername());
            profileEmailLabel.setText(sessionUser.getEmail());
            profileIdLabel.setText(sessionUser.getId() != null ? String.valueOf(sessionUser.getId()) : "N/A");
            profileCreatedLabel.setText(sessionUser.getCreatedAt() != null ? sessionUser.getCreatedAt().toString() : "N/A");
            rawRoleLabel.setText(sessionUser.getRole());
            polymorphicRoleLabel.setText(sessionUser.getRoleName());
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        sessionUser = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(JavaFXApplication.getContext()::getBean);
            Parent root = loader.load();
            JavaFXApplication.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
