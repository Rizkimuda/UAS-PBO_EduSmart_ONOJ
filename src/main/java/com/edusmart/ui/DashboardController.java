package com.edusmart.ui;

import com.edusmart.JavaFXApplication;
import com.edusmart.model.*;
import com.edusmart.repository.*;
import com.edusmart.service.*;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Component
public class DashboardController {

    private static User sessionUser;
    private Transition sidebarTransition;

    // Active state variables
    private Course activeCourse;
    private Material activeMaterial;
    private Quiz activeQuiz;
    private Question activeQuestion;

    // FXML fields
    @FXML
    private HBox rootPane;
    @FXML
    private VBox sidebar;
    @FXML
    private VBox activeInstructorCard;
    @FXML
    private StackPane avatarContainer;
    @FXML
    private Label avatarFallback;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private Label usernameLabel;

    // Tabs navigation buttons
    @FXML
    private HBox dashboardTabBtn;
    @FXML
    private HBox myCoursesTabBtn;
    @FXML
    private HBox createCourseTabBtn;

    // View panels
    @FXML
    private VBox dashboardView;
    @FXML
    private VBox myCoursesView;
    @FXML
    private VBox createCourseView;
    @FXML
    private VBox profileView;
    @FXML
    private VBox manageContentView;

    // Tab 1 fields
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalCoursesLabel;
    @FXML
    private Label totalStudentsLabel;

    // Tab 2 fields
    @FXML
    private FlowPane courseListContainer;

    // Tab 3 fields
    @FXML
    private TextField newCourseTitleField;
    @FXML
    private ComboBox<String> newCourseCategoryCombo;
    @FXML
    private TextArea newCourseDescField;
    @FXML
    private Label createCourseErrorLabel;

    // Tab 5 fields
    @FXML
    private StackPane largeAvatarContainer;
    @FXML
    private Label largeAvatarFallback;
    @FXML
    private ImageView largeAvatarImageView;
    @FXML
    private Label profileUsernameLabel;
    @FXML
    private TextField profileEmailField;
    @FXML
    private PasswordField profilePasswordField;
    @FXML
    private PasswordField profileConfirmPasswordField;
    @FXML
    private Label profileErrorLabel;

    // Classroom Content Manager fields
    @FXML
    private Label classroomCourseTitle;
    @FXML
    private Label classroomCourseCategory;
    @FXML
    private VBox materialsContainer;
    @FXML
    private Button manageQuizBtn;
    @FXML
    private StackPane editorWorkspace;

    // Material Editor
    @FXML
    private VBox materialEditorArea;
    @FXML
    private Label materialEditorTitleLabel;
    @FXML
    private TextField materialTitleField;
    @FXML
    private ComboBox<String> materialTypeCombo;
    @FXML
    private TextArea materialContentField;
    @FXML
    private Label materialErrorLabel;
    @FXML
    private Button deleteMaterialBtn;

    // Quiz Editor
    @FXML
    private VBox quizEditorArea;
    @FXML
    private TextField quizTimeLimitField;
    @FXML
    private TextField quizPassingScoreField;
    @FXML
    private TextField quizMaxAttemptsField;
    @FXML
    private Label quizErrorLabel;
    @FXML
    private VBox questionsContainer;

    // Question Editor
    @FXML
    private VBox questionEditorArea;
    @FXML
    private Label questionEditorTitleLabel;
    @FXML
    private ComboBox<String> questionTypeCombo;
    @FXML
    private TextField questionPointsField;
    @FXML
    private TextArea questionTextField;
    @FXML
    private VBox mcOptionsContainer;
    @FXML
    private TextField optionAField;
    @FXML
    private TextField optionBField;
    @FXML
    private TextField optionCField;
    @FXML
    private TextField optionDField;
    @FXML
    private TextField questionAnswerField;
    @FXML
    private TextArea questionExplanationField;
    @FXML
    private Label questionErrorLabel;

    // Spring dependencies
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final CourseService courseService;
    private final QuizService quizService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DashboardController(CourseRepository courseRepository,
                               EnrollmentRepository enrollmentRepository,
                               UserRepository userRepository,
                               MaterialRepository materialRepository,
                               QuestionRepository questionRepository,
                               QuizRepository quizRepository,
                               CourseService courseService,
                               QuizService quizService,
                               UserService userService,
                               PasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.materialRepository = materialRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.courseService = courseService;
        this.quizService = quizService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
            String username = sessionUser.getUsername();
            usernameLabel.setText(username);
            profileUsernameLabel.setText(username);
            profileEmailField.setText(sessionUser.getEmail());
            welcomeLabel.setText("Selamat datang kembali, " + username + "!");

            // Initialize ComboBoxes
            newCourseCategoryCombo.getItems().addAll("Data Science", "Matematika", "IPA", "Manajemen");
            materialTypeCombo.getItems().addAll("TEXT", "VIDEO", "DOCUMENT");
            materialTypeCombo.setValue("TEXT");
            questionTypeCombo.getItems().addAll("Pilihan Ganda", "Essay");

            // Setup Question Type Listener
            questionTypeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                boolean isMC = "Pilihan Ganda".equals(newVal);
                mcOptionsContainer.setVisible(isMC);
                mcOptionsContainer.setManaged(isMC);
            });

            loadProfilePicture();
            showDashboardView(null);
        }
    }

    private void loadProfilePicture() {
        if (sessionUser != null && sessionUser.getProfilePicture() != null) {
            try {
                File file = new File(sessionUser.getProfilePicture());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    
                    cropAndSetProfileImage(image, avatarImageView, 36.0);
                    avatarImageView.setVisible(true);
                    avatarImageView.setManaged(true);
                    avatarFallback.setVisible(false);
                    avatarFallback.setManaged(false);
                    
                    cropAndSetProfileImage(image, largeAvatarImageView, 90.0);
                    largeAvatarImageView.setVisible(true);
                    largeAvatarImageView.setManaged(true);
                    largeAvatarFallback.setVisible(false);
                    largeAvatarFallback.setManaged(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
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

    @FXML
    public void showDashboardView(Event event) {
        if (sessionUser != null) {
            List<Course> courses = courseRepository.findByInstructor(sessionUser);
            totalCoursesLabel.setText(String.valueOf(courses.size()));

            long totalStudents = 0;
            for (Course course : courses) {
                totalStudents += enrollmentRepository.findByCourse(course).size();
            }
            totalStudentsLabel.setText(String.valueOf(totalStudents));
        }
        switchTab(dashboardTabBtn, dashboardView);
    }

    @FXML
    public void showMyCoursesView(Event event) {
        populateMyCourses();
        switchTab(myCoursesTabBtn, myCoursesView);
    }

    @FXML
    public void showCreateCourseView(Event event) {
        newCourseTitleField.clear();
        newCourseCategoryCombo.getSelectionModel().clearSelection();
        newCourseDescField.clear();
        createCourseErrorLabel.setVisible(false);
        createCourseErrorLabel.setManaged(false);
        switchTab(createCourseTabBtn, createCourseView);
    }

    @FXML
    public void showProfileView(Event event) {
        profilePasswordField.clear();
        profileConfirmPasswordField.clear();
        profileErrorLabel.setVisible(false);
        profileErrorLabel.setManaged(false);
        switchTab(null, profileView);
    }

    private void switchTab(HBox activeTab, VBox activeView) {
        // Reset navigation button styles
        resetTabStyle(dashboardTabBtn);
        resetTabStyle(myCoursesTabBtn);
        resetTabStyle(createCourseTabBtn);
        
        if (activeTab != null) {
            activeTab.getStyleClass().remove("sidebar-tab");
            if (!activeTab.getStyleClass().contains("active-sidebar-tab")) {
                activeTab.getStyleClass().add("active-sidebar-tab");
            }
        }
        
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        myCoursesView.setVisible(false);
        myCoursesView.setManaged(false);
        createCourseView.setVisible(false);
        createCourseView.setManaged(false);
        profileView.setVisible(false);
        profileView.setManaged(false);
        manageContentView.setVisible(false);
        manageContentView.setManaged(false);
        
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
    public void handleToggleSidebar(ActionEvent event) {
        if (sidebar != null) {
            if (sidebarTransition != null) {
                sidebarTransition.stop();
            }

            boolean isCurrentlyExpanded = sidebar.isManaged() && sidebar.getPrefWidth() > 0;
            
            double startWidth = isCurrentlyExpanded ? 280.0 : 0.0;
            double endWidth = isCurrentlyExpanded ? 0.0 : 280.0;
            
            if (!isCurrentlyExpanded) {
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
                    sidebar.setVisible(false);
                    sidebar.setManaged(false);
                    sidebar.setClip(null);
                } else {
                    sidebar.setClip(null);
                }
                sidebarTransition = null;
            });
            
            sidebarTransition.play();
        }
    }

    private void populateMyCourses() {
        courseListContainer.getChildren().clear();
        List<Course> courses = courseRepository.findByInstructor(sessionUser);

        if (courses.isEmpty()) {
            VBox emptyContainer = new VBox();
            emptyContainer.setSpacing(10.0);
            emptyContainer.setAlignment(javafx.geometry.Pos.CENTER);
            emptyContainer.setPadding(new Insets(30.0));
            emptyContainer.setStyle("-fx-border-color: #cbd5e1; -fx-border-style: dashed; -fx-border-width: 2px; -fx-border-radius: 8px;");

            Label emptyLabel = new Label("📚 Anda belum membuat kelas apa pun.");
            emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #475569;");
            emptyContainer.getChildren().add(emptyLabel);
            courseListContainer.getChildren().add(emptyContainer);
            return;
        }

        for (Course course : courses) {
            VBox card = new VBox();
            card.getStyleClass().add("course-grid-card");

            // Banner style based on category
            StackPane banner = new StackPane();
            banner.getStyleClass().add("course-card-banner");
            banner.setStyle(getBannerStyle(course.getCategory()));

            VBox body = new VBox();
            body.getStyleClass().add("course-card-body");
            body.setSpacing(8.0);

            Label titleLabel = new Label(course.getTitle());
            titleLabel.getStyleClass().add("course-card-title");

            Label categoryLabel = new Label(course.getCategory());
            categoryLabel.getStyleClass().add("course-card-category-badge");

            Label statusLabel = new Label("Status: " + course.getStatus());
            statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + 
                    (course.getStatus() == CourseStatus.PUBLISHED ? "#10b981;" : "#f59e0b;") + 
                    " -fx-font-weight: bold;");

            // Buttons
            HBox btnContainer = new HBox(8.0);
            btnContainer.setAlignment(javafx.geometry.Pos.CENTER);

            Button manageBtn = new Button("Kelola Konten");
            manageBtn.setStyle("-fx-background-color: #7c3aed; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 6px 12px; -fx-background-radius: 4px;");
            manageBtn.setOnAction(e -> manageCourseContent(course));

            Button togglePublishBtn = new Button(course.getStatus() == CourseStatus.PUBLISHED ? "Unpublish" : "Publish");
            togglePublishBtn.setStyle("-fx-background-color: " + (course.getStatus() == CourseStatus.PUBLISHED ? "#64748b;" : "#10b981;") + " -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 6px 12px; -fx-background-radius: 4px;");
            togglePublishBtn.setOnAction(e -> {
                course.setStatus(course.getStatus() == CourseStatus.PUBLISHED ? CourseStatus.DRAFT : CourseStatus.PUBLISHED);
                courseRepository.save(course);
                populateMyCourses();
            });

            btnContainer.getChildren().addAll(manageBtn, togglePublishBtn);

            body.getChildren().addAll(titleLabel, categoryLabel, statusLabel, btnContainer);
            card.getChildren().addAll(banner, body);

            courseListContainer.getChildren().add(card);
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

    @FXML
    public void handleCreateCourse(ActionEvent event) {
        String title = newCourseTitleField.getText().trim();
        String category = newCourseCategoryCombo.getValue();
        String description = newCourseDescField.getText().trim();

        createCourseErrorLabel.setVisible(false);
        createCourseErrorLabel.setManaged(false);

        if (title.isEmpty() || category == null || description.isEmpty()) {
            createCourseErrorLabel.setText("Semua kolom input wajib diisi!");
            createCourseErrorLabel.setVisible(true);
            createCourseErrorLabel.setManaged(true);
            return;
        }

        try {
            Course newCourse = new Course();
            newCourse.setTitle(title);
            newCourse.setCategory(category);
            newCourse.setDescription(description);
            newCourse.setInstructor(sessionUser);
            newCourse.setStatus(CourseStatus.DRAFT);
            newCourse.setCreatedAt(java.time.LocalDateTime.now());
            newCourse.setUpdatedAt(java.time.LocalDateTime.now());

            courseRepository.save(newCourse);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText("Kelas Berhasil Dibuat");
            alert.setContentText("Kelas baru dengan judul '" + title + "' berhasil dibuat dalam status DRAFT. Anda dapat mengelola kontennya di tab 'Kursus Saya'.");
            alert.showAndWait();

            showMyCoursesView(null);
        } catch (Exception e) {
            createCourseErrorLabel.setText("Gagal membuat kelas: " + e.getMessage());
            createCourseErrorLabel.setVisible(true);
            createCourseErrorLabel.setManaged(true);
            e.printStackTrace();
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
                String storagePath = System.getProperty("user.home") + "/.edusmart/profile_pics/";
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleUpdateProfile(ActionEvent event) {
        String email = profileEmailField.getText().trim();
        String password = profilePasswordField.getText();
        String confirmPassword = profileConfirmPasswordField.getText();

        profileErrorLabel.setVisible(false);
        profileErrorLabel.setManaged(false);

        if (email.isEmpty()) {
            showProfileError("Email tidak boleh kosong!");
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showProfileError("Format email tidak valid!");
            return;
        }

        if (!password.isEmpty()) {
            if (password.length() < 6) {
                showProfileError("Kata sandi minimal 6 karakter!");
                return;
            }
            if (!password.equals(confirmPassword)) {
                showProfileError("Konfirmasi kata sandi tidak cocok!");
                return;
            }
        }

        try {
            userService.updateProfile(sessionUser.getId(), email, password);
            
            // Refresh local session user reference
            userRepository.findById(sessionUser.getId()).ifPresent(updatedUser -> sessionUser = updatedUser);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Profil Diperbarui");
            alert.setHeaderText("Perubahan Disimpan");
            alert.setContentText("Email dan kata sandi profil Anda telah berhasil diperbarui.");
            alert.showAndWait();

            loadProfilePicture();
        } catch (Exception e) {
            showProfileError("Gagal memperbarui profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showProfileError(String message) {
        profileErrorLabel.setText(message);
        profileErrorLabel.setVisible(true);
        profileErrorLabel.setManaged(true);
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

    // Classroom Content Manager implementation
    private void manageCourseContent(Course course) {
        this.activeCourse = course;
        classroomCourseTitle.setText(course.getTitle());
        classroomCourseCategory.setText(course.getCategory());
        
        refreshMaterialsList();
        handleClearMaterialForm();
        
        // Find Quiz
        this.activeQuiz = quizService.getQuizByCourseId(course.getId()).orElse(null);
        
        switchTab(null, manageContentView);
    }

    @FXML
    public void handleBackToCourseList(ActionEvent event) {
        showMyCoursesView(null);
    }

    private void refreshMaterialsList() {
        materialsContainer.getChildren().clear();
        List<Material> materials = materialRepository.findByCourseOrderByOrderIndexAsc(activeCourse);

        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            
            HBox item = new HBox();
            item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            item.setSpacing(10.0);
            item.getStyleClass().add("classroom-material-item");
            item.setStyle("-fx-padding: 8px 12px; -fx-cursor: hand;");

            Label titleLbl = new Label((i + 1) + ". " + material.getTitle());
            titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e293b;");
            titleLbl.setWrapText(true);
            HBox.setHgrow(titleLbl, javafx.scene.layout.Priority.ALWAYS);
            
            Label typeBadge = new Label(material.getType().name());
            typeBadge.setStyle("-fx-font-size: 9px; -fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-padding: 2px 6px; -fx-background-radius: 4px;");

            item.getChildren().addAll(titleLbl, typeBadge);
            item.setOnMouseClicked(e -> editMaterial(material));

            materialsContainer.getChildren().add(item);
        }
    }

    private void editMaterial(Material material) {
        this.activeMaterial = material;
        materialEditorTitleLabel.setText("Edit Materi: " + material.getTitle());
        materialTitleField.setText(material.getTitle());
        materialTypeCombo.setValue(material.getType().name());
        materialContentField.setText(material.getContent());
        
        deleteMaterialBtn.setVisible(true);
        deleteMaterialBtn.setManaged(true);
        materialErrorLabel.setVisible(false);
        materialErrorLabel.setManaged(false);

        switchEditorArea(materialEditorArea);
    }

    @FXML
    public void handleShowAddMaterialForm(ActionEvent event) {
        handleClearMaterialForm();
    }

    @FXML
    public void handleClearMaterialForm() {
        this.activeMaterial = null;
        materialEditorTitleLabel.setText("Tambah Materi Baru");
        materialTitleField.clear();
        materialTypeCombo.setValue("TEXT");
        materialContentField.clear();
        
        deleteMaterialBtn.setVisible(false);
        deleteMaterialBtn.setManaged(false);
        materialErrorLabel.setVisible(false);
        materialErrorLabel.setManaged(false);

        switchEditorArea(materialEditorArea);
    }

    @FXML
    public void handleSaveMaterial(ActionEvent event) {
        String title = materialTitleField.getText().trim();
        String typeStr = materialTypeCombo.getValue();
        String content = materialContentField.getText().trim();

        materialErrorLabel.setVisible(false);
        materialErrorLabel.setManaged(false);

        if (title.isEmpty() || typeStr == null || content.isEmpty()) {
            showMaterialError("Semua kolom input wajib diisi!");
            return;
        }

        try {
            MaterialType type = MaterialType.valueOf(typeStr);
            if (activeMaterial == null) {
                // Create new material
                List<Material> materials = materialRepository.findByCourseOrderByOrderIndexAsc(activeCourse);
                int orderIndex = materials.size() + 1;
                
                Material newMaterial = new Material(title, type, content, orderIndex, activeCourse);
                materialRepository.save(newMaterial);
            } else {
                // Edit existing material
                activeMaterial.setTitle(title);
                activeMaterial.setType(type);
                activeMaterial.setContent(content);
                materialRepository.save(activeMaterial);
            }

            refreshMaterialsList();
            handleClearMaterialForm();
        } catch (Exception e) {
            showMaterialError("Gagal menyimpan materi: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteMaterial(ActionEvent event) {
        if (activeMaterial != null) {
            try {
                materialRepository.delete(activeMaterial);
                
                // Re-sequence remaining materials
                List<Material> materials = materialRepository.findByCourseOrderByOrderIndexAsc(activeCourse);
                for (int i = 0; i < materials.size(); i++) {
                    materials.get(i).setOrderIndex(i + 1);
                    materialRepository.save(materials.get(i));
                }

                refreshMaterialsList();
                handleClearMaterialForm();
            } catch (Exception e) {
                showMaterialError("Gagal menghapus materi: " + e.getMessage());
            }
        }
    }

    private void showMaterialError(String message) {
        materialErrorLabel.setText(message);
        materialErrorLabel.setVisible(true);
        materialErrorLabel.setManaged(true);
    }

    @FXML
    public void handleShowQuizEditor(ActionEvent event) {
        this.activeQuiz = quizService.getQuizByCourseId(activeCourse.getId()).orElse(null);
        
        quizErrorLabel.setVisible(false);
        quizErrorLabel.setManaged(false);

        if (activeQuiz == null) {
            quizTimeLimitField.setText("15");
            quizPassingScoreField.setText("70");
            quizMaxAttemptsField.setText("3");
        } else {
            quizTimeLimitField.setText(String.valueOf(activeQuiz.getTimeLimit()));
            quizPassingScoreField.setText(String.valueOf(activeQuiz.getPassingScore()));
            quizMaxAttemptsField.setText(String.valueOf(activeQuiz.getMaxAttempts()));
        }

        refreshQuestionsList();
        switchEditorArea(quizEditorArea);
    }

    @FXML
    public void handleSaveQuizSettings(ActionEvent event) {
        String limitStr = quizTimeLimitField.getText().trim();
        String scoreStr = quizPassingScoreField.getText().trim();
        String attemptsStr = quizMaxAttemptsField.getText().trim();

        quizErrorLabel.setVisible(false);
        quizErrorLabel.setManaged(false);

        if (limitStr.isEmpty() || scoreStr.isEmpty() || attemptsStr.isEmpty()) {
            showQuizError("Semua kolom parameter kuis wajib diisi!");
            return;
        }

        try {
            int limit = Integer.parseInt(limitStr);
            int score = Integer.parseInt(scoreStr);
            int attempts = Integer.parseInt(attemptsStr);

            if (activeQuiz == null) {
                // Create new Quiz
                activeQuiz = new Quiz("Kuis Evaluasi " + activeCourse.getTitle(), limit, score, attempts, activeCourse);
            } else {
                activeQuiz.setTimeLimit(limit);
                activeQuiz.setPassingScore(score);
                activeQuiz.setMaxAttempts(attempts);
            }
            activeQuiz = quizRepository.save(activeQuiz);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText("Pengaturan Kuis Disimpan");
            alert.setContentText("Parameter kuis evaluasi berhasil disimpan.");
            alert.showAndWait();
            
            refreshQuestionsList();
        } catch (NumberFormatException e) {
            showQuizError("Batas Waktu, Nilai Kelulusan, dan Batas Percobaan harus berupa angka!");
        } catch (Exception e) {
            showQuizError("Gagal menyimpan kuis: " + e.getMessage());
        }
    }

    private void showQuizError(String message) {
        quizErrorLabel.setText(message);
        quizErrorLabel.setVisible(true);
        quizErrorLabel.setManaged(true);
    }

    private void refreshQuestionsList() {
        questionsContainer.getChildren().clear();
        if (activeQuiz == null) {
            Label placeholder = new Label("Kuis belum disetting. Silakan simpan kuis di atas terlebih dahulu.");
            placeholder.setStyle("-fx-font-style: italic; -fx-text-fill: #64748b; -fx-font-size: 11px;");
            questionsContainer.getChildren().add(placeholder);
            return;
        }

        List<Question> questions = questionRepository.findByQuiz(activeQuiz);
        if (questions.isEmpty()) {
            Label placeholder = new Label("Belum ada soal kuis yang ditambahkan.");
            placeholder.setStyle("-fx-font-style: italic; -fx-text-fill: #64748b; -fx-font-size: 11px;");
            questionsContainer.getChildren().add(placeholder);
            return;
        }

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            
            HBox item = new HBox();
            item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            item.setSpacing(10.0);
            item.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 4px; -fx-padding: 8px;");

            Label qLbl = new Label((i + 1) + ". [" + question.getType().name() + "] " + question.getQuestionText() + " (" + question.getPoints() + " Poin)");
            qLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e293b;");
            qLbl.setWrapText(true);
            HBox.setHgrow(qLbl, javafx.scene.layout.Priority.ALWAYS);

            Button editBtn = new Button("✏️");
            editBtn.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #0369a1; -fx-font-size: 11px; -fx-padding: 6px 10px; -fx-cursor: hand; -fx-background-radius: 4px;");
            editBtn.setTooltip(new Tooltip("Edit Soal"));
            editBtn.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
            editBtn.setOnAction(e -> editQuestion(question));

            Button delBtn = new Button("🗑️");
            delBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-font-size: 11px; -fx-padding: 6px 10px; -fx-cursor: hand; -fx-background-radius: 4px;");
            delBtn.setTooltip(new Tooltip("Hapus Soal"));
            delBtn.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
            delBtn.setOnAction(e -> deleteQuestion(question));

            item.getChildren().addAll(qLbl, editBtn, delBtn);
            questionsContainer.getChildren().add(item);
        }
    }

    private void editQuestion(Question question) {
        this.activeQuestion = question;
        questionEditorTitleLabel.setText("Edit Pertanyaan");
        questionTypeCombo.setValue(question.getType() == QuestionType.MULTIPLE_CHOICE ? "Pilihan Ganda" : "Essay");
        questionPointsField.setText(String.valueOf(question.getPoints()));
        questionTextField.setText(question.getQuestionText());
        questionAnswerField.setText(question.getCorrectAnswer());
        questionExplanationField.setText(question.getExplanation());

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mc = (MultipleChoiceQuestion) question;
            optionAField.setText(mc.getOptionA());
            optionBField.setText(mc.getOptionB());
            optionCField.setText(mc.getOptionC());
            optionDField.setText(mc.getOptionD());
            
            mcOptionsContainer.setVisible(true);
            mcOptionsContainer.setManaged(true);
        } else {
            optionAField.clear();
            optionBField.clear();
            optionCField.clear();
            optionDField.clear();
            
            mcOptionsContainer.setVisible(false);
            mcOptionsContainer.setManaged(false);
        }

        questionErrorLabel.setVisible(false);
        questionErrorLabel.setManaged(false);
        switchEditorArea(questionEditorArea);
    }

    @FXML
    public void handleShowAddQuestionForm(ActionEvent event) {
        this.activeQuestion = null;
        questionEditorTitleLabel.setText("Tambah Pertanyaan Baru");
        questionTypeCombo.getSelectionModel().clearSelection();
        questionPointsField.setText("10");
        questionTextField.clear();
        optionAField.clear();
        optionBField.clear();
        optionCField.clear();
        optionDField.clear();
        questionAnswerField.clear();
        questionExplanationField.clear();
        
        mcOptionsContainer.setVisible(false);
        mcOptionsContainer.setManaged(false);
        
        questionErrorLabel.setVisible(false);
        questionErrorLabel.setManaged(false);
        
        switchEditorArea(questionEditorArea);
    }

    @FXML
    public void handleCancelQuestionEdit(ActionEvent event) {
        switchEditorArea(quizEditorArea);
    }

    @FXML
    public void handleSaveQuestion(ActionEvent event) {
        String typeStr = questionTypeCombo.getValue();
        String pointsStr = questionPointsField.getText().trim();
        String text = questionTextField.getText().trim();
        String answer = questionAnswerField.getText().trim();
        String explanation = questionExplanationField.getText().trim();

        questionErrorLabel.setVisible(false);
        questionErrorLabel.setManaged(false);

        if (typeStr == null || pointsStr.isEmpty() || text.isEmpty() || answer.isEmpty()) {
            showQuestionError("Tipe, Poin, Pertanyaan, dan Kunci Jawaban wajib diisi!");
            return;
        }

        int points;
        try {
            points = Integer.parseInt(pointsStr);
        } catch (NumberFormatException e) {
            showQuestionError("Poin harus berupa angka bulat!");
            return;
        }

        boolean isMC = "Pilihan Ganda".equals(typeStr);
        String optA = optionAField.getText().trim();
        String optB = optionBField.getText().trim();
        String optC = optionCField.getText().trim();
        String optD = optionDField.getText().trim();

        if (isMC && (optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty())) {
            showQuestionError("Untuk Pilihan Ganda, semua kolom Opsi (A, B, C, D) harus diisi!");
            return;
        }

        if (isMC && !answer.matches("^[A-Da-d]$")) {
            showQuestionError("Kunci Jawaban untuk Pilihan Ganda harus salah satu huruf dari A, B, C, atau D!");
            return;
        }

        try {
            // Save quiz first if null
            if (activeQuiz == null) {
                activeQuiz = new Quiz("Kuis Evaluasi " + activeCourse.getTitle(), 15, 70, 3, activeCourse);
                activeQuiz = quizRepository.save(activeQuiz);
            }

            if (activeQuestion == null) {
                // Add new question
                Question newQ;
                if (isMC) {
                    newQ = new MultipleChoiceQuestion(text, points, answer.toUpperCase(), explanation, activeQuiz, optA, optB, optC, optD);
                } else {
                    newQ = new EssayQuestion(text, points, answer, explanation, activeQuiz);
                }
                questionRepository.save(newQ);
            } else {
                // Edit existing question
                activeQuestion.setQuestionText(text);
                activeQuestion.setPoints(points);
                activeQuestion.setCorrectAnswer(isMC ? answer.toUpperCase() : answer);
                activeQuestion.setExplanation(explanation);
                
                if (isMC) {
                    if (activeQuestion instanceof MultipleChoiceQuestion) {
                        MultipleChoiceQuestion mcQ = (MultipleChoiceQuestion) activeQuestion;
                        mcQ.setOptionA(optA);
                        mcQ.setOptionB(optB);
                        mcQ.setOptionC(optC);
                        mcQ.setOptionD(optD);
                    } else {
                        // Type changed from Essay to MC - delete old, save new
                        questionRepository.delete(activeQuestion);
                        Question newMC = new MultipleChoiceQuestion(text, points, answer.toUpperCase(), explanation, activeQuiz, optA, optB, optC, optD);
                        questionRepository.save(newMC);
                    }
                } else {
                    if (activeQuestion instanceof EssayQuestion) {
                        // Keep as Essay
                    } else {
                        // Type changed from MC to Essay - delete old, save new
                        questionRepository.delete(activeQuestion);
                        Question newEssay = new EssayQuestion(text, points, answer, explanation, activeQuiz);
                        questionRepository.save(newEssay);
                    }
                }
                questionRepository.save(activeQuestion);
            }

            refreshQuestionsList();
            switchEditorArea(quizEditorArea);
        } catch (Exception e) {
            showQuestionError("Gagal menyimpan pertanyaan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteQuestion(Question question) {
        try {
            questionRepository.delete(question);
            refreshQuestionsList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showQuestionError(String message) {
        questionErrorLabel.setText(message);
        questionErrorLabel.setVisible(true);
        questionErrorLabel.setManaged(true);
    }

    private void switchEditorArea(VBox activeArea) {
        materialEditorArea.setVisible(false);
        materialEditorArea.setManaged(false);
        quizEditorArea.setVisible(false);
        quizEditorArea.setManaged(false);
        questionEditorArea.setVisible(false);
        questionEditorArea.setManaged(false);

        if (activeArea != null) {
            activeArea.setVisible(true);
            activeArea.setManaged(true);
        }
    }
}
