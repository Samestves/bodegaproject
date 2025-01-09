package com.example.bodegaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        System.setProperty("prism.graphics", "fast");
        Image icon = new Image("/shop.png");
        primaryStage.getIcons().add(icon);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 980, 660);

        // Deshabilitar el redimensionamiento
        primaryStage.setResizable(false);

        primaryStage.setTitle("Bodega Project");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}