package com.edusmart.ui;

import com.edusmart.JavaFXApplication;
import com.edusmart.model.Enrollment;
import com.edusmart.model.User;
import com.edusmart.model.CourseStatus;
import com.edusmart.repository.EnrollmentRepository;
import com.edusmart.repository.UserRepository;
import com.edusmart.repository.CourseRepository;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    private FlowPane courseListContainer;

    @FXML
    private HBox enrolledClassTabBtn;

    @FXML
    private VBox enrolledClassListView;

    @FXML
    private FlowPane enrolledCourseListContainer;

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public StudentDashboardController(EnrollmentRepository enrollmentRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
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

            populateAvailableCourses();
            populateEnrolledCourses(enrollments);
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

    private void populateAvailableCourses() {
        courseListContainer.getChildren().clear();
        
        List<com.edusmart.model.Course> courses = courseRepository.findByStatus(com.edusmart.model.CourseStatus.PUBLISHED);
        
        if (courses.isEmpty()) {
            VBox emptyContainer = new VBox();
            emptyContainer.setSpacing(10.0);
            emptyContainer.setAlignment(javafx.geometry.Pos.CENTER);
            emptyContainer.setPadding(new Insets(30.0));
            emptyContainer.setStyle("-fx-border-color: #cbd5e1; -fx-border-style: dashed; -fx-border-width: 2px; -fx-border-radius: 8px;");

            Label emptyLabel = new Label("📚 Tidak ada kelas yang tersedia saat ini.");
            emptyLabel.getStyleClass().add("field-label");
            emptyLabel.setStyle("-fx-font-size: 15px;");

            emptyContainer.getChildren().add(emptyLabel);
            courseListContainer.getChildren().add(emptyContainer);
            return;
        }

        // Get student enrolled course IDs to check enrollment status
        List<Enrollment> enrollments = enrollmentRepository.findByUser(sessionUser);
        List<Long> enrolledCourseIds = enrollments.stream()
                .map(e -> e.getCourse().getId())
                .toList();

        for (com.edusmart.model.Course course : courses) {
            VBox card = new VBox();
            card.getStyleClass().add("course-grid-card");
            
            // 1. Banner
            StackPane banner = new StackPane();
            banner.getStyleClass().add("course-card-banner");
            banner.setStyle(getBannerStyle(course.getCategory()));
            
            // 2. Card Body
            VBox body = new VBox();
            body.getStyleClass().add("course-card-body");
            
            // Title
            Label titleLabel = new Label(course.getTitle());
            titleLabel.getStyleClass().add("course-card-title");
            
            // Category Badge
            Label categoryLabel = new Label(course.getCategory());
            categoryLabel.getStyleClass().add("course-card-category-badge");
            
            // Instructor Row
            HBox instructorRow = new HBox();
            instructorRow.getStyleClass().add("course-card-instructor");
            
            // Circular Avatar Container for Instructor
            StackPane avatarCircle = new StackPane();
            avatarCircle.getStyleClass().add("course-card-instructor-avatar");
            
            Label avatarTxt = new Label("👤");
            avatarTxt.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            avatarCircle.getChildren().add(avatarTxt);
            
            if (course.getInstructor() != null && course.getInstructor().getProfilePicture() != null) {
                try {
                    File picFile = new File(course.getInstructor().getProfilePicture());
                    if (picFile.exists()) {
                        ImageView iv = new ImageView(new Image(picFile.toURI().toString()));
                        double w = iv.getImage().getWidth();
                        double h = iv.getImage().getHeight();
                        double side = Math.min(w, h);
                        iv.setViewport(new Rectangle2D((w - side)/2.0, (h - side)/2.0, side, side));
                        iv.setFitWidth(28.0);
                        iv.setFitHeight(28.0);
                        
                        Circle c = new Circle(14.0, 14.0, 14.0);
                        iv.setClip(c);
                        
                        avatarTxt.setVisible(false);
                        avatarCircle.getChildren().add(iv);
                    }
                } catch (Exception ex) {
                    // Ignore
                }
            }
            
            String instructorName = (course.getInstructor() != null) ? course.getInstructor().getUsername() : "Tidak diketahui";
            Label nameLabel = new Label(instructorName);
            nameLabel.getStyleClass().add("course-card-instructor-name");
            
            instructorRow.getChildren().addAll(avatarCircle, nameLabel);
            
            // Action Button
            HBox btn = new HBox();
            boolean isEnrolled = enrolledCourseIds.contains(course.getId());
            
            if (isEnrolled) {
                Label btnTxt = new Label("Sudah Terdaftar");
                btnTxt.setStyle("-fx-text-fill: #065f46; -fx-font-weight: bold;");
                btn.getChildren().add(btnTxt);
                btn.setStyle("-fx-background-color: #d1fae5; -fx-border-color: #a7f3d0; -fx-border-width: 1px; -fx-border-style: solid; -fx-background-radius: 6px; -fx-padding: 8px 16px;");
            } else {
                btn.getStyleClass().add("course-card-button");
                Label btnTxt = new Label("Enroll Kelas");
                btnTxt.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                Label btnArrow = new Label(" →");
                btnArrow.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                btn.getChildren().addAll(btnTxt, btnArrow);
                
                // Add click event for enrollment
                btn.setOnMouseClicked(event -> handleEnrollCourse(course));
            }
            btn.setAlignment(javafx.geometry.Pos.CENTER);
            
            // Assemble Card
            body.getChildren().addAll(titleLabel, categoryLabel, instructorRow, btn);
            card.getChildren().addAll(banner, body);
            
            courseListContainer.getChildren().add(card);
        }
    }

    private void populateEnrolledCourses(List<Enrollment> enrollments) {
        enrolledCourseListContainer.getChildren().clear();
        
        if (enrollments.isEmpty()) {
            VBox emptyContainer = new VBox();
            emptyContainer.setSpacing(10.0);
            emptyContainer.setAlignment(javafx.geometry.Pos.CENTER);
            emptyContainer.setPadding(new Insets(30.0));
            emptyContainer.setStyle("-fx-border-color: #cbd5e1; -fx-border-style: dashed; -fx-border-width: 2px; -fx-border-radius: 8px;");

            Label emptyLabel = new Label("📚 Anda belum mengambil kelas apa pun.");
            emptyLabel.getStyleClass().add("field-label");
            emptyLabel.setStyle("-fx-font-size: 15px;");

            Label emptySubtitle = new Label("Daftar kelas yang Anda ikuti akan muncul di sini.");
            emptySubtitle.getStyleClass().add("helper-credential");

            emptyContainer.getChildren().addAll(emptyLabel, emptySubtitle);
            enrolledCourseListContainer.getChildren().add(emptyContainer);
            return;
        }

        for (Enrollment enrollment : enrollments) {
            com.edusmart.model.Course course = enrollment.getCourse();
            
            VBox card = new VBox();
            card.getStyleClass().add("course-grid-card");
            
            // 1. Banner
            StackPane banner = new StackPane();
            banner.getStyleClass().add("course-card-banner");
            banner.setStyle(getBannerStyle(course.getCategory()));
            
            // 2. Card Body
            VBox body = new VBox();
            body.getStyleClass().add("course-card-body");
            
            // Title
            Label titleLabel = new Label(course.getTitle());
            titleLabel.getStyleClass().add("course-card-title");
            
            // Category Badge
            Label categoryLabel = new Label(course.getCategory());
            categoryLabel.getStyleClass().add("course-card-category-badge");
            
            // Instructor Row
            HBox instructorRow = new HBox();
            instructorRow.getStyleClass().add("course-card-instructor");
            
            // Circular Avatar Container for Instructor
            StackPane avatarCircle = new StackPane();
            avatarCircle.getStyleClass().add("course-card-instructor-avatar");
            
            Label avatarTxt = new Label("👤");
            avatarTxt.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            avatarCircle.getChildren().add(avatarTxt);
            
            if (course.getInstructor() != null && course.getInstructor().getProfilePicture() != null) {
                try {
                    File picFile = new File(course.getInstructor().getProfilePicture());
                    if (picFile.exists()) {
                        ImageView iv = new ImageView(new Image(picFile.toURI().toString()));
                        double w = iv.getImage().getWidth();
                        double h = iv.getImage().getHeight();
                        double side = Math.min(w, h);
                        iv.setViewport(new Rectangle2D((w - side)/2.0, (h - side)/2.0, side, side));
                        iv.setFitWidth(28.0);
                        iv.setFitHeight(28.0);
                        
                        Circle c = new Circle(14.0, 14.0, 14.0);
                        iv.setClip(c);
                        
                        avatarTxt.setVisible(false);
                        avatarCircle.getChildren().add(iv);
                    }
                } catch (Exception ex) {
                    // Ignore
                }
            }
            
            String instructorName = (course.getInstructor() != null) ? course.getInstructor().getUsername() : "Tidak diketahui";
            Label nameLabel = new Label(instructorName);
            nameLabel.getStyleClass().add("course-card-instructor-name");
            
            instructorRow.getChildren().addAll(avatarCircle, nameLabel);
            
            // Progress Bar & Percentage
            VBox progressBox = new VBox();
            progressBox.setSpacing(6.0);
            
            HBox progressLabelRow = new HBox();
            progressLabelRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label progressTitle = new Label("Progres belajar");
            progressTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");
            Label progressPercent = new Label(enrollment.getProgressPercent() + "%");
            progressPercent.setStyle("-fx-font-size: 10px; -fx-text-fill: #2563eb; -fx-font-weight: bold;");
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            progressLabelRow.getChildren().addAll(progressTitle, spacer, progressPercent);
            
            ProgressBar progressBar = new ProgressBar(enrollment.getProgressPercent() / 100.0);
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.setPrefHeight(6.0);
            progressBar.getStyleClass().add("xp-progress-bar");
            
            progressBox.getChildren().addAll(progressLabelRow, progressBar);
            
            // Action Button
            HBox btn = new HBox();
            btn.getStyleClass().add("course-card-button");
            
            Label btnTxt = new Label("Buka Kelas");
            btnTxt.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label btnArrow = new Label(" →");
            btnArrow.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            
            btn.getChildren().addAll(btnTxt, btnArrow);
            btn.setAlignment(javafx.geometry.Pos.CENTER);
            
            // Assemble Card
            body.getChildren().addAll(titleLabel, categoryLabel, instructorRow, progressBox, btn);
            card.getChildren().addAll(banner, body);
            
            enrolledCourseListContainer.getChildren().add(card);
        }
    }

    private void handleEnrollCourse(com.edusmart.model.Course course) {
        try {
            Enrollment newEnrollment = new Enrollment(sessionUser, course);
            newEnrollment.setProgressPercent(0);
            newEnrollment.setCreatedAt(java.time.LocalDateTime.now());
            newEnrollment.setUpdatedAt(java.time.LocalDateTime.now());
            
            enrollmentRepository.save(newEnrollment);
            
            // Refresh views
            List<Enrollment> enrollments = enrollmentRepository.findByUser(sessionUser);
            enrolledCountLabel.setText(String.valueOf(enrollments.size()));
            
            populateAvailableCourses();
            populateEnrolledCourses(enrollments);
            
            System.out.println("DEBUG: Enrolled in course: " + course.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBannerStyle(String category) {
        if (category == null) return "-fx-background-color: linear-gradient(to bottom right, #64748b, #475569); -fx-background-radius: 10px 10px 0px 0px;";
        switch (category.toLowerCase()) {
            case "data science":
                return "-fx-background-color: linear-gradient(to bottom right, #a855f7, #6366f1); -fx-background-radius: 10px 10px 0px 0px;";
            case "matematika":
                return "-fx-background-color: linear-gradient(to bottom right, #3b82f6, #1d4ed8); -fx-background-radius: 10px 10px 0px 0px;";
            case "ipa":
                return "-fx-background-color: linear-gradient(to bottom right, #ec4899, #be185d); -fx-background-radius: 10px 10px 0px 0px;";
            case "manajemen":
                return "-fx-background-color: linear-gradient(to bottom right, #10b981, #047857); -fx-background-radius: 10px 10px 0px 0px;";
            default:
                return "-fx-background-color: linear-gradient(to bottom right, #64748b, #475569); -fx-background-radius: 10px 10px 0px 0px;";
        }
    }

    private void switchTab(HBox activeTab, VBox activeView) {
        // Reset all tabs
        resetTabStyle(homeTabBtn);
        resetTabStyle(enrolledClassTabBtn);
        resetTabStyle(classListTabBtn);
        
        // Set active tab
        if (activeTab != null) {
            activeTab.getStyleClass().remove("sidebar-tab");
            if (!activeTab.getStyleClass().contains("active-sidebar-tab")) {
                activeTab.getStyleClass().add("active-sidebar-tab");
            }
        }
        
        // Hide all views
        homeView.setVisible(false);
        homeView.setManaged(false);
        enrolledClassListView.setVisible(false);
        enrolledClassListView.setManaged(false);
        classListView.setVisible(false);
        classListView.setManaged(false);
        profileView.setVisible(false);
        profileView.setManaged(false);
        
        // Show active view
        if (activeView != null) {
            activeView.setVisible(true);
            activeView.setManaged(true);
        }
    }

    private void resetTabStyle(HBox tabBtn) {
        tabBtn.getStyleClass().remove("active-sidebar-tab");
        if (!tabBtn.getStyleClass().contains("sidebar-tab")) {
            tabBtn.getStyleClass().add("sidebar-tab");
        }
    }

    @FXML
    public void showHomeView(Event event) {
        switchTab(homeTabBtn, homeView);
    }

    @FXML
    public void showEnrolledClassListView(Event event) {
        switchTab(enrolledClassTabBtn, enrolledClassListView);
    }

    @FXML
    public void showClassListView(Event event) {
        switchTab(classListTabBtn, classListView);
    }

    @FXML
    public void showProfileView(Event event) {
        switchTab(null, profileView);
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
