package com.edusmart;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFXApplication extends Application {

    private static ConfigurableApplicationContext context;
    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }

    @Override
    public void init() throws Exception {
        context = new SpringApplicationBuilder()
                .sources(EdusmartApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        
        primaryStage.setTitle("EduSmart - E-Learning Platform");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (context != null) {
            context.close();
        }
    }
}
