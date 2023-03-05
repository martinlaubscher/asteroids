package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
//        Scene scene = new Scene(root, 1920, 1080, Color.web("0x00001c"));
        stage.setTitle("Asteroids");
        stage.setResizable(true);

        stage.setScene(scene);
        stage.show();
        ;
    }

    public static void main(String[] args) {
        launch();
    }
}