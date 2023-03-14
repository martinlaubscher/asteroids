package application;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;

public class Main extends Application {
    public static int WIDTH = 600;
    public static int HEIGHT = 400;
    @Override
    public void start(Stage stage) throws Exception{

        Pane pane = new Pane();
        pane.setPrefSize(600, 400);

        PlayerShip PlayerShip = new PlayerShip(WIDTH / 2, HEIGHT / 2);
        List<EnemyShip> EnemyShips = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            EnemyShip EnemyShip = new EnemyShip((rnd.nextInt(WIDTH)), (rnd.nextInt(HEIGHT)));
            EnemyShips.add(EnemyShip);
        }
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(100), rnd.nextInt(100));
            asteroids.add(asteroid);
        }

        pane.getChildren().add(PlayerShip.getCharacter());
        EnemyShips.forEach(EnemyShip -> pane.getChildren().add(EnemyShip.getCharacter()));
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));

        Scene scene = new Scene(pane);
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    PlayerShip.turnLeft();
                }

                if(pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    PlayerShip.turnRight();
                }

                if(pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    PlayerShip.accelerate();
                }

                if(pressedKeys.getOrDefault(KeyCode.DOWN, false)) {
                    PlayerShip.decelerate();
                }
                PlayerShip.move();
                asteroids.forEach(asteroid -> asteroid.move());
                asteroids.forEach(asteroid -> {
                    if (PlayerShip.collide(asteroid)) {
                        stop();
                    }
                });
                EnemyShips.forEach(EnemyShip -> EnemyShip.accelerate());
                EnemyShips.forEach(EnemyShip -> {
                    if (PlayerShip.collide(EnemyShip)) {
                        stop();
                    }
                });
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}