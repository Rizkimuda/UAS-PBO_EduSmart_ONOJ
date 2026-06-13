package com.edusmart.ui;

import com.edusmart.JavaFXApplication;
import com.edusmart.model.Enrollment;
import com.edusmart.model.User;
import com.edusmart.repository.EnrollmentRepository;
import com.edusmart.repository.UserRepository;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
public class StudentDashboardController {

    private static User sessionUser;

    @FXML
    private Label usernameLabel;

    @FXML
    private HBox homeTabBtn;

    @FXML
    private HBox classListTabBtn;

    @FXML
    private VBox homeView;

    @FXML
    private VBox classListView;

    @FXML
    private VBox profileView;

    @FXML
    private Label avatarFallback;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label largeAvatarFallback;

    @FXML
    private ImageView largeAvatarImageView;

    @FXML
    private Label profileUsernameLabel;

    @FXML
    private Label profileEmailLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label enrolledCountLabel;

    @FXML
    private Label completedCountLabel;

    @FXML
    private VBox courseListContainer;

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public StudentDashboardController(EnrollmentRepository enrollmentRepository, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    public static void setSessionUser(User user) {
        sessionUser = user;
    }

    public static User getSessionUser() {
        return sessionUser;
    }

    @FXML
    public void initialize() {
        if (sessionUser != null) {
            // Load user profile statistics
            String username = sessionUser.getUsername();
            
            usernameLabel.setText(username);
            welcomeLabel.setText("Selamat datang kembali, " + username + "!");

            // Load and render enrolled courses
            List<Enrollment> enrollments = enrollmentRepository.findByUser(sessionUser);
            enrolledCountLabel.setText(String.valueOf(enrollments.size()));

            long completedCount = enrollments.stream()
                    .filter(Enrollment::isCompleted)
                    .count();
            completedCountLabel.setText(String.valueOf(completedCount));

            populateCourseList(enrollments);
            loadProfilePicture();
        }
    }

    private void loadProfilePicture() {
        if (sessionUser != null && sessionUser.getProfilePicture() != null) {
            try {
                File file = new File(sessionUser.getProfilePicture());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    
                    // Small Avatar
                    cropAndSetProfileImage(image, avatarImageView, 36.0);
                    avatarImageView.setVisible(true);
                    avatarImageView.setManaged(true);
                    avatarFallback.setVisible(false);
                    avatarFallback.setManaged(false);
                    
                    // Large Avatar in Profile
                    cropAndSetProfileImage(image, largeAvatarImageView, 90.0);
                    largeAvatarImageView.setVisible(true);
                    largeAvatarImageView.setManaged(true);
                    largeAvatarFallback.setVisible(false);
                    largeAvatarFallback.setManaged(false);
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Failed to load profile picture: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Revert to fallback if null
            avatarImageView.setVisible(false);
            avatarImageView.setManaged(false);
            avatarFallback.setVisible(true);
            avatarFallback.setManaged(true);

            largeAvatarImageView.setVisible(false);
            largeAvatarImageView.setManaged(false);
            largeAvatarFallback.setVisible(true);
            largeAvatarFallback.setManaged(true);
        }
    }

    private void cropAndSetProfileImage(Image image, ImageView imageView, double targetSize) {
        double width = image.getWidth();
        double height = image.getHeight();
        double minSide = Math.min(width, height);
        double x = (width - minSide) / 2.0;
        double y = (height - minSide) / 2.0;
        
        imageView.setViewport(new Rectangle2D(x, y, minSide, minSide));
        imageView.setFitWidth(targetSize);
        imageView.setFitHeight(targetSize);
        
        Circle clip = new Circle(targetSize / 2.0, targetSize / 2.0, targetSize / 2.0);
        imageView.setClip(clip);
        imageView.setImage(image);
    }

    private void populateCourseList(List<Enrollment> enrollments) {
        courseListContainer.getChildren().clear();
        
        if (enrollments.isEmpty()) {
            VBox emptyContainer = new VBox();
            emptyContainer.setSpacing(10.0);
            emptyContainer.setAlignment(javafx.geometry.Pos.CENTER);
            emptyContainer.setPadding(new Insets(30.0));
            emptyContainer.setStyle("-fx-border-color: #cbd5e1; -fx-border-style: dashed; -fx-border-width: 2px; -fx-border-radius: 8px;");

            Label emptyLabel = new Label("📚 Anda belum terdaftar di kelas mana pun.");
            emptyLabel.getStyleClass().add("field-label");
            emptyLabel.setStyle("-fx-font-size: 15px;");

            Label emptySubtitle = new Label("Kursus yang terdaftar akan muncul di sini beserta progres belajar Anda.");
            emptySubtitle.getStyleClass().add("helper-credential");

            emptyContainer.getChildren().addAll(emptyLabel, emptySubtitle);
            courseListContainer.getChildren().add(emptyContainer);
            return;
        }

        for (Enrollment enrollment : enrollments) {
            com.edusmart.model.Course course = enrollment.getCourse();
            
            HBox card = new HBox();
            card.getStyleClass().add("course-list-card");
            card.setSpacing(20.0);
            card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            card.setPadding(new Insets(16.0, 20.0, 16.0, 20.0));

            // Book icon
            Label iconLabel = new Label("📚");
            iconLabel.setStyle("-fx-font-size: 24px;");

            // Info details
            VBox infoBox = new VBox();
            infoBox.setSpacing(4.0);
            HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

            Label titleLabel = new Label(course.getTitle());
            titleLabel.getStyleClass().add("field-label");
            titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

            String instructorName = (course.getInstructor() != null) ? course.getInstructor().getUsername() : "Tidak diketahui";
            Label descLabel = new Label("Kategori: " + course.getCategory() + " | Pengajar: " + instructorName);
            descLabel.getStyleClass().add("helper-credential");

            infoBox.getChildren().addAll(titleLabel, descLabel);

            // Progress bar
            VBox progressBox = new VBox();
            progressBox.setSpacing(6.0);
            progressBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            progressBox.setPrefWidth(180.0);

            Label progressLabel = new Label("Progres: " + enrollment.getProgressPercent() + "%");
            progressLabel.getStyleClass().add("helper-header");

            ProgressBar progressBar = new ProgressBar(enrollment.getProgressPercent() / 100.0);
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.setPrefHeight(6.0);
            progressBar.getStyleClass().add("xp-progress-bar");

            progressBox.getChildren().addAll(progressLabel, progressBar);

            card.getChildren().addAll(iconLabel, infoBox, progressBox);
            courseListContainer.getChildren().add(card);
        }
    }

    @FXML
    public void showHomeView(Event event) {
        homeView.setVisible(true);
        homeView.setManaged(true);
        classListView.setVisible(false);
        classListView.setManaged(false);
        profileView.setVisible(false);
        profileView.setManaged(false);

        // Update tab styles
        updateTabStyles(homeTabBtn, classListTabBtn);
    }

    @FXML
    public void showClassListView(Event event) {
        homeView.setVisible(false);
        homeView.setManaged(false);
        classListView.setVisible(true);
        classListView.setManaged(true);
        profileView.setVisible(false);
        profileView.setManaged(false);

        // Update tab styles
        updateTabStyles(classListTabBtn, homeTabBtn);
    }

    private void updateTabStyles(HBox activeTab, HBox inactiveTab) {
        activeTab.getStyleClass().remove("sidebar-tab");
        if (!activeTab.getStyleClass().contains("active-sidebar-tab")) {
            activeTab.getStyleClass().add("active-sidebar-tab");
        }

        inactiveTab.getStyleClass().remove("active-sidebar-tab");
        if (!inactiveTab.getStyleClass().contains("sidebar-tab")) {
            inactiveTab.getStyleClass().add("sidebar-tab");
        }
    }

    @FXML
    public void showProfileView(Event event) {
        homeView.setVisible(false);
        homeView.setManaged(false);
        classListView.setVisible(false);
        classListView.setManaged(false);
        profileView.setVisible(true);
        profileView.setManaged(true);

        // Remove active class from tabs
        homeTabBtn.getStyleClass().remove("active-sidebar-tab");
        if (!homeTabBtn.getStyleClass().contains("sidebar-tab")) {
            homeTabBtn.getStyleClass().add("sidebar-tab");
        }

        classListTabBtn.getStyleClass().remove("active-sidebar-tab");
        if (!classListTabBtn.getStyleClass().contains("sidebar-tab")) {
            classListTabBtn.getStyleClass().add("sidebar-tab");
        }
        
        // Load user info
        if (sessionUser != null) {
            profileUsernameLabel.setText(sessionUser.getUsername());
            profileEmailLabel.setText(sessionUser.getEmail());
            loadProfilePicture();
        }
    }

    @FXML
    public void handleUploadProfilePicture(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );
        
        String userHome = System.getProperty("user.home");
        File defaultDirectory = new File(userHome);
        if (defaultDirectory.exists()) {
            fileChooser.setInitialDirectory(defaultDirectory);
        }
        
        File selectedFile = fileChooser.showOpenDialog(JavaFXApplication.getPrimaryStage());
        if (selectedFile != null) {
            try {
                String storagePath = "C:/Users/Axioo Pongo/.gemini/antigravity/brain/7dd1fc06-258f-4a3d-87cd-d85949ccc2ca/profile_pics/";
                File dir = new File(storagePath);
                if (!dir.exists()) {
                     dir.mkdirs();
                }
                
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String fileName = sessionUser.getUsername() + "_profile" + extension;
                File targetFile = new File(dir, fileName);
                
                Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                sessionUser.setProfilePicture(targetFile.getAbsolutePath());
                userRepository.save(sessionUser);
                
                loadProfilePicture();
                System.out.println("DEBUG: Profile picture saved successfully: " + targetFile.getAbsolutePath());
            } catch (Exception e) {
                System.out.println("DEBUG: Failed to save profile picture: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleLogout(Event event) {
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
