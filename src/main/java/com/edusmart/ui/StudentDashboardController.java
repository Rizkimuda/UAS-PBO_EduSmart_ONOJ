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
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.edusmart.model.Material;
import com.edusmart.model.Quiz;
import com.edusmart.model.QuizAttempt;
import com.edusmart.model.Question;
import com.edusmart.model.MultipleChoiceQuestion;
import com.edusmart.model.EssayQuestion;
import com.edusmart.repository.MaterialRepository;
import com.edusmart.repository.MaterialCompletionRepository;
import com.edusmart.repository.QuestionRepository;
import com.edusmart.service.CourseService;
import com.edusmart.service.QuizService;
import com.edusmart.service.EnrollmentService;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Toggle;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.animation.Transition;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;

@Component
public class StudentDashboardController {

    private static User sessionUser;
    private Transition sidebarTransition;

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

    // Study View FXML Fields
    @FXML
    private VBox sidebar;
    @FXML
    private VBox studyView;
    @FXML
    private Label classroomCourseTitle;
    @FXML
    private Label classroomCourseCategory;
    @FXML
    private Label classroomProgressText;
    @FXML
    private ProgressBar classroomProgressBar;
    @FXML
    private VBox classroomMaterialsContainer;
    @FXML
    private Button classroomQuizBtn;
    @FXML
    private StackPane classroomWorkspace;
    
    // Material Area
    @FXML
    private VBox classroomMaterialArea;
    @FXML
    private Label classroomMaterialTitle;
    @FXML
    private Label classroomMaterialContent;
    @FXML
    private Button classroomPrevMatBtn;
    @FXML
    private Button classroomMarkCompleteBtn;
    @FXML
    private Button classroomNextMatBtn;

    // Quiz Area
    @FXML
    private VBox classroomQuizArea;
    @FXML
    private Label classroomQuizTitle;
    @FXML
    private Label classroomQuizTimeLimit;
    @FXML
    private Label classroomQuizPassingScore;
    @FXML
    private Label classroomQuizMaxAttempts;
    @FXML
    private VBox classroomQuizQuestionsContainer;

    // Quiz Result Area
    @FXML
    private VBox classroomQuizResultArea;
    @FXML
    private VBox classroomResultBanner;
    @FXML
    private Label classroomResultIcon;
    @FXML
    private Label classroomResultTitle;
    @FXML
    private Label classroomResultScoreText;
    @FXML
    private VBox classroomResultExplanationsContainer;

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final MaterialRepository materialRepository;
    private final MaterialCompletionRepository materialCompletionRepository;
    private final QuestionRepository questionRepository;
    private final CourseService courseService;
    private final QuizService quizService;
    private final EnrollmentService enrollmentService;

    // Active state variables
    private com.edusmart.model.Course activeCourse;
    private List<Material> activeMaterials;
    private Material activeMaterial;
    private int activeMaterialIndex = 0;
    private Quiz activeQuiz;
    private java.util.Map<Long, ToggleGroup> quizMcGroups = new java.util.HashMap<>();
    private java.util.Map<Long, TextField> quizEssayFields = new java.util.HashMap<>();

    @Autowired
    public StudentDashboardController(EnrollmentRepository enrollmentRepository,
                                     UserRepository userRepository,
                                     CourseRepository courseRepository,
                                     MaterialRepository materialRepository,
                                     MaterialCompletionRepository materialCompletionRepository,
                                     QuestionRepository questionRepository,
                                     CourseService courseService,
                                     QuizService quizService,
                                     EnrollmentService enrollmentService) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.materialRepository = materialRepository;
        this.materialCompletionRepository = materialCompletionRepository;
        this.questionRepository = questionRepository;
        this.courseService = courseService;
        this.quizService = quizService;
        this.enrollmentService = enrollmentService;
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
            btn.setStyle("-fx-cursor: hand;");
            
            Label btnTxt = new Label("Buka Kelas");
            btnTxt.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Label btnArrow = new Label(" →");
            btnArrow.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            
            btn.getChildren().addAll(btnTxt, btnArrow);
            btn.setAlignment(javafx.geometry.Pos.CENTER);
            btn.setOnMouseClicked(e -> openClassroom(enrollment.getCourse()));
            
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
        studyView.setVisible(false);
        studyView.setManaged(false);
        
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

    private void openClassroom(com.edusmart.model.Course course) {
        this.activeCourse = course;
        this.activeMaterials = materialRepository.findByCourseOrderByOrderIndexAsc(course);
        
        // Find Quiz
        this.activeQuiz = quizService.getQuizByCourseId(course.getId()).orElse(null);
        if (this.activeQuiz == null) {
            classroomQuizBtn.setVisible(false);
            classroomQuizBtn.setManaged(false);
        } else {
            classroomQuizBtn.setVisible(true);
            classroomQuizBtn.setManaged(true);
        }
        
        updateClassroomProgress();
        renderMaterialsSidebar();
        
        if (activeMaterials != null && !activeMaterials.isEmpty()) {
            activeMaterialIndex = 0;
            activeMaterial = activeMaterials.get(0);
            showMaterial(activeMaterial);
        } else {
            activeMaterial = null;
            activeMaterialIndex = -1;
            classroomMaterialTitle.setText("Tidak ada materi");
            classroomMaterialContent.setText("Belum ada materi pelajaran di kelas ini.");
            classroomPrevMatBtn.setDisable(true);
            classroomNextMatBtn.setDisable(true);
            classroomMarkCompleteBtn.setDisable(true);
            switchWorkspaceArea(classroomMaterialArea);
        }
        
        switchTab(null, studyView);
    }

    private void updateClassroomProgress() {
        if (activeCourse == null || sessionUser == null) return;
        
        enrollmentRepository.findByUserAndCourse(sessionUser, activeCourse).ifPresent(enrollment -> {
            int progress = enrollment.getProgressPercent();
            classroomProgressText.setText(progress + "%");
            classroomProgressBar.setProgress(progress / 100.0);
        });
    }

    private void renderMaterialsSidebar() {
        classroomMaterialsContainer.getChildren().clear();
        if (activeMaterials == null) return;
        
        for (int i = 0; i < activeMaterials.size(); i++) {
            Material material = activeMaterials.get(i);
            final int index = i;
            
            HBox item = new HBox();
            item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            item.setSpacing(10.0);
            
            // Check if active
            boolean isActive = (activeMaterial != null && activeMaterial.getId().equals(material.getId()));
            if (isActive) {
                item.getStyleClass().add("classroom-material-item-active");
            } else {
                item.getStyleClass().add("classroom-material-item");
            }
            
            Label titleLbl = new Label((i + 1) + ". " + material.getTitle());
            titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (isActive ? "#2563eb;" : "#1e293b;") + " -fx-font-weight: " + (isActive ? "bold;" : "normal;"));
            titleLbl.setWrapText(true);
            
            HBox.setHgrow(titleLbl, javafx.scene.layout.Priority.ALWAYS);
            item.getChildren().add(titleLbl);
            
            boolean isCompleted = materialCompletionRepository.existsByUserAndMaterial(sessionUser, material);
            if (isCompleted) {
                Label checkLbl = new Label("✅");
                checkLbl.setStyle("-fx-font-size: 11px;");
                item.getChildren().add(checkLbl);
            }
            
            item.setOnMouseClicked(event -> {
                activeMaterialIndex = index;
                activeMaterial = material;
                showMaterial(material);
            });
            
            classroomMaterialsContainer.getChildren().add(item);
        }
    }

    private void showMaterial(Material material) {
        if (material == null) return;
        this.activeMaterial = material;
        this.activeMaterialIndex = activeMaterials.indexOf(material);
        
        switchWorkspaceArea(classroomMaterialArea);
        renderMaterialsSidebar();
        
        classroomMaterialTitle.setText(material.getTitle());
        classroomMaterialContent.setText(material.getContent());
        
        classroomPrevMatBtn.setDisable(activeMaterialIndex <= 0);
        classroomNextMatBtn.setDisable(activeMaterialIndex >= activeMaterials.size() - 1);
        
        boolean isCompleted = materialCompletionRepository.existsByUserAndMaterial(sessionUser, material);
        if (isCompleted) {
            classroomMarkCompleteBtn.setDisable(true);
            classroomMarkCompleteBtn.setText("✓ Selesai");
            classroomMarkCompleteBtn.setStyle("-fx-background-color: #64748b;");
        } else {
            classroomMarkCompleteBtn.setDisable(false);
            classroomMarkCompleteBtn.setText("✓ Tandai Selesai");
            classroomMarkCompleteBtn.setStyle("-fx-background-color: #10b981;");
        }
    }

    private void switchWorkspaceArea(VBox activeArea) {
        classroomMaterialArea.setVisible(false);
        classroomMaterialArea.setManaged(false);
        classroomQuizArea.setVisible(false);
        classroomQuizArea.setManaged(false);
        classroomQuizResultArea.setVisible(false);
        classroomQuizResultArea.setManaged(false);
        
        if (activeArea != null) {
            activeArea.setVisible(true);
            activeArea.setManaged(true);
        }
    }

    @FXML
    public void handlePrevMaterial(ActionEvent event) {
        if (activeMaterials != null && activeMaterialIndex > 0) {
            activeMaterialIndex--;
            showMaterial(activeMaterials.get(activeMaterialIndex));
        }
    }

    @FXML
    public void handleNextMaterial(ActionEvent event) {
        if (activeMaterials != null && activeMaterialIndex < activeMaterials.size() - 1) {
            activeMaterialIndex++;
            showMaterial(activeMaterials.get(activeMaterialIndex));
        }
    }

    @FXML
    public void handleMarkComplete(ActionEvent event) {
        if (activeMaterial == null || sessionUser == null) return;
        try {
            courseService.markMaterialAsComplete(sessionUser.getId(), activeMaterial.getId());
            
            // Refresh user session points in db
            userRepository.findById(sessionUser.getId()).ifPresent(updatedUser -> {
                sessionUser = updatedUser;
            });
            
            updateClassroomProgress();
            showMaterial(activeMaterial);
            renderMaterialsSidebar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleShowQuiz(ActionEvent event) {
        if (activeQuiz != null) {
            showQuiz();
        }
    }

    private void showQuiz() {
        switchWorkspaceArea(classroomQuizArea);
        
        quizMcGroups.clear();
        quizEssayFields.clear();
        classroomQuizQuestionsContainer.getChildren().clear();
        
        classroomQuizTitle.setText(activeQuiz.getTitle());
        classroomQuizTimeLimit.setText("Waktu: " + activeQuiz.getTimeLimit() + " menit");
        classroomQuizPassingScore.setText("Nilai Kelulusan: " + activeQuiz.getPassingScore());
        classroomQuizMaxAttempts.setText("Batas Percobaan: " + activeQuiz.getMaxAttempts() + " kali");
        
        List<Question> questions = questionRepository.findByQuiz(activeQuiz);
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            
            VBox questionBox = new VBox();
            questionBox.getStyleClass().add("quiz-question-box");
            questionBox.setSpacing(10.0);
            
            Label qText = new Label("Pertanyaan " + (i + 1) + ": " + question.getQuestionText() + " (" + question.getPoints() + " Poin)");
            qText.setWrapText(true);
            qText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1e293b;");
            questionBox.getChildren().add(qText);
            
            if (question instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion mcQ = (MultipleChoiceQuestion) question;
                ToggleGroup group = new ToggleGroup();
                
                RadioButton optA = new RadioButton("A. " + mcQ.getOptionA());
                RadioButton optB = new RadioButton("B. " + mcQ.getOptionB());
                RadioButton optC = new RadioButton("C. " + mcQ.getOptionC());
                RadioButton optD = new RadioButton("D. " + mcQ.getOptionD());
                
                RadioButton[] options = {optA, optB, optC, optD};
                String[] letters = {"A", "B", "C", "D"};
                
                VBox optionsBox = new VBox(8.0);
                optionsBox.setPadding(new Insets(8.0, 0.0, 0.0, 16.0));
                
                for (int j = 0; j < 4; j++) {
                    options[j].setToggleGroup(group);
                    options[j].setUserData(letters[j]);
                    options[j].setWrapText(true);
                    options[j].setStyle("-fx-font-size: 12px; -fx-text-fill: #334155;");
                    optionsBox.getChildren().add(options[j]);
                }
                
                questionBox.getChildren().add(optionsBox);
                quizMcGroups.put(question.getId(), group);
                
            } else if (question instanceof EssayQuestion) {
                TextField essayField = new TextField();
                essayField.setPromptText("Tulis jawaban Anda di sini...");
                essayField.setStyle("-fx-font-size: 12px; -fx-padding: 8px; -fx-border-color: #cbd5e1; -fx-border-radius: 4px;");
                
                VBox essayBox = new VBox(8.0);
                essayBox.setPadding(new Insets(8.0, 0.0, 0.0, 16.0));
                essayBox.getChildren().add(essayField);
                
                questionBox.getChildren().add(essayBox);
                quizEssayFields.put(question.getId(), essayField);
            }
            
            classroomQuizQuestionsContainer.getChildren().add(questionBox);
        }
    }

    @FXML
    public void handleSubmitQuiz(ActionEvent event) {
        if (activeQuiz == null || sessionUser == null) return;
        
        // Gather answers
        java.util.Map<Long, String> answers = new java.util.HashMap<>();
        List<Question> questions = questionRepository.findByQuiz(activeQuiz);
        
        for (Question question : questions) {
            if (question instanceof MultipleChoiceQuestion) {
                ToggleGroup group = quizMcGroups.get(question.getId());
                if (group != null) {
                    Toggle selectedToggle = group.getSelectedToggle();
                    if (selectedToggle != null) {
                        answers.put(question.getId(), (String) selectedToggle.getUserData());
                    } else {
                        answers.put(question.getId(), "");
                    }
                }
            } else if (question instanceof EssayQuestion) {
                TextField field = quizEssayFields.get(question.getId());
                if (field != null) {
                    answers.put(question.getId(), field.getText().trim());
                } else {
                    answers.put(question.getId(), "");
                }
            }
        }
        
        try {
            QuizAttempt attempt = quizService.submitQuizAttempt(sessionUser.getId(), activeQuiz.getId(), answers);
            
            // Refresh user points
            userRepository.findById(sessionUser.getId()).ifPresent(updatedUser -> {
                sessionUser = updatedUser;
            });
            
            showQuizResult(attempt, answers);
        } catch (IllegalStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Batas Percobaan Tercapai");
            alert.setHeaderText("Gagal Mengirim Jawaban Kuis");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Terjadi Kesalahan");
            alert.setContentText("Terjadi kesalahan saat memproses kuis Anda: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showQuizResult(QuizAttempt attempt, java.util.Map<Long, String> answers) {
        switchWorkspaceArea(classroomQuizResultArea);
        
        classroomResultScoreText.setText("Skor Anda: " + attempt.getScore() + "/100");
        
        if (attempt.isPassed()) {
            classroomResultBanner.setStyle("-fx-background-color: #d1fae5; -fx-padding: 16px; -fx-background-radius: 8px;");
            classroomResultIcon.setText("🎉");
            classroomResultTitle.setText("Anda Lulus Kuis!");
            classroomResultTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #065f46;");
        } else {
            classroomResultBanner.setStyle("-fx-background-color: #fee2e2; -fx-padding: 16px; -fx-background-radius: 8px;");
            classroomResultIcon.setText("❌");
            classroomResultTitle.setText("Anda Belum Lulus.");
            classroomResultTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #991b1b;");
        }
        
        classroomResultExplanationsContainer.getChildren().clear();
        List<Question> questions = questionRepository.findByQuiz(activeQuiz);
        
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            
            VBox box = new VBox();
            box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 8px; -fx-padding: 16px;");
            box.setSpacing(8.0);
            
            Label qText = new Label("Pertanyaan " + (i + 1) + ": " + question.getQuestionText());
            qText.setWrapText(true);
            qText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1e293b;");
            box.getChildren().add(qText);
            
            String studentAnswer = answers.get(question.getId());
            if (studentAnswer == null || studentAnswer.isEmpty()) {
                studentAnswer = "(Tidak dijawab)";
            }
            
            boolean isCorrect = question.grade(studentAnswer);
            
            Label sAnsLabel = new Label();
            Label cAnsLabel = new Label();
            
            if (question instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion mc = (MultipleChoiceQuestion) question;
                String correctKey = question.getCorrectAnswer();
                String correctText = "";
                if ("A".equalsIgnoreCase(correctKey)) correctText = mc.getOptionA();
                else if ("B".equalsIgnoreCase(correctKey)) correctText = mc.getOptionB();
                else if ("C".equalsIgnoreCase(correctKey)) correctText = mc.getOptionC();
                else if ("D".equalsIgnoreCase(correctKey)) correctText = mc.getOptionD();
                cAnsLabel.setText("Jawaban Benar: " + correctKey + ". " + correctText);
                
                String studText = "";
                if ("A".equalsIgnoreCase(studentAnswer)) studText = mc.getOptionA();
                else if ("B".equalsIgnoreCase(studentAnswer)) studText = mc.getOptionB();
                else if ("C".equalsIgnoreCase(studentAnswer)) studText = mc.getOptionC();
                else if ("D".equalsIgnoreCase(studentAnswer)) studText = mc.getOptionD();
                
                if (studText.isEmpty()) {
                    sAnsLabel.setText("Jawaban Anda: " + studentAnswer);
                } else {
                    sAnsLabel.setText("Jawaban Anda: " + studentAnswer + ". " + studText);
                }
            } else {
                cAnsLabel.setText("Jawaban Benar (Kunci Kata): " + question.getCorrectAnswer());
                sAnsLabel.setText("Jawaban Anda: " + studentAnswer);
            }
            
            sAnsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (isCorrect ? "#059669;" : "#dc2626;") + " -fx-font-weight: bold;");
            cAnsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #059669; -fx-font-weight: bold;");
            
            box.getChildren().addAll(sAnsLabel, cAnsLabel);
            
            String expl = question.getExplanation();
            if (expl == null || expl.isEmpty()) {
                expl = "Tidak ada pembahasan.";
            }
            Label explLabel = new Label("Pembahasan: " + expl);
            explLabel.setWrapText(true);
            explLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569; -fx-font-style: italic; -fx-padding: 4px 0 0 0;");
            box.getChildren().add(explLabel);
            
            classroomResultExplanationsContainer.getChildren().add(box);
        }
    }

    @FXML
    public void handleCancelQuiz(ActionEvent event) {
        switchWorkspaceArea(classroomMaterialArea);
    }

    @FXML
    public void handleCloseQuizResult(ActionEvent event) {
        switchWorkspaceArea(classroomMaterialArea);
        updateClassroomProgress();
        
        List<Enrollment> enrollments = enrollmentRepository.findByUser(sessionUser);
        populateEnrolledCourses(enrollments);
    }

    @FXML
    public void handleBackToEnrolledCourses(ActionEvent event) {
        switchTab(enrolledClassTabBtn, enrolledClassListView);
        
        List<Enrollment> enrollments = enrollmentRepository.findByUser(sessionUser);
        populateEnrolledCourses(enrollments);
    }

    @FXML
    public void handleToggleSidebar(ActionEvent event) {
        if (sidebar != null) {
            if (sidebarTransition != null) {
                sidebarTransition.stop();
            }

            boolean isCurrentlyExpanded = sidebar.isManaged() && sidebar.getPrefWidth() > 0;
            
            double startWidth = isCurrentlyExpanded ? 280.0 : 0.0;
            double endWidth = isCurrentlyExpanded ? 0.0 : 280.0;
            
            if (!isCurrentlyExpanded) {
                // Expanding
                sidebar.setVisible(true);
                sidebar.setManaged(true);
            }
            
            Rectangle clip = new Rectangle();
            clip.heightProperty().bind(sidebar.heightProperty());
            sidebar.setClip(clip);
            
            sidebarTransition = new Transition() {
                {
                    setCycleDuration(Duration.millis(250));
                }
                
                @Override
                protected void interpolate(double fraction) {
                    double currentWidth = startWidth + (endWidth - startWidth) * fraction;
                    sidebar.setPrefWidth(currentWidth);
                    sidebar.setMinWidth(currentWidth);
                    sidebar.setMaxWidth(currentWidth);
                    clip.setWidth(currentWidth);
                }
            };
            
            sidebarTransition.setOnFinished(e -> {
                if (isCurrentlyExpanded) {
                    // Collapsed
                    sidebar.setVisible(false);
                    sidebar.setManaged(false);
                    sidebar.setClip(null);
                } else {
                    // Fully expanded, remove clip
                    sidebar.setClip(null);
                }
                sidebarTransition = null;
            });
            
            sidebarTransition.play();
        }
    }
}
