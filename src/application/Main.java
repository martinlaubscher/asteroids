package application;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;

public class Main extends Application {
    public static int WIDTH = 600;
    public static int HEIGHT = 400;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

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

        // list storing all (active) bullets
        List<Bullet> bullets = new ArrayList<>();

        pane.getChildren().add(PlayerShip.getCharacter());
        EnemyShips.forEach(EnemyShip -> pane.getChildren().add(EnemyShip.getCharacter()));
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));

        Scene scene = new Scene(pane);
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });

        new AnimationTimer() {
            // timestamps recording when the last bullet was fired - used to avoid fire rate being too high
            private long lastPlayerBullet = 0;
            private long lastEnemyBullet = 0;

            @Override
            public void handle(long now) {
                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    PlayerShip.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    PlayerShip.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    PlayerShip.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.DOWN, false)) {
                    PlayerShip.decelerate();
                }
                PlayerShip.move();
                asteroids.forEach(asteroid -> asteroid.move());
                asteroids.forEach(asteroid -> {
                    if (PlayerShip.collide(asteroid)) {
                        // stop();
                        // TODO on collision with asteroid, player ship is destroyed (need to deduct life, respawn etc.). asteroid needs to behave as if hit by bullet.
                        PlayerShip.decrementLives();
                    }
                });
                EnemyShips.forEach(EnemyShip -> EnemyShip.accelerate());

                // enemy ship behaviour
                EnemyShips.forEach(EnemyShip -> {
                    if (PlayerShip.collide(EnemyShip)) {
                        // stop();
                        // TODO on collision, both ships are destroyed (player ship: need to deduct life, respawn etc.)
                        PlayerShip.decrementLives();

                    }
                    // if the enemy ship is alive, shoot a bullet every 10 seconds
                    if (EnemyShip.isAlive() && now - lastEnemyBullet > 10_000_000_000L) {
                        Bullet bullet = new Bullet((int) EnemyShip.getCharacter().getTranslateX(), (int) EnemyShip.getCharacter().getTranslateY(), false);

                        // calculate the direction to fire in - 'target' represents a vector pointing from the enemy ship to the player ship
                        double targetX = PlayerShip.getCharacter().getTranslateX() - EnemyShip.getCharacter().getTranslateX();
                        double targetY = PlayerShip.getCharacter().getTranslateY() - EnemyShip.getCharacter().getTranslateY();
                        Point2D target = new Point2D(targetX, targetY);

                        bullets.add(bullet);

                        // send bullet on its path
                        bullet.accelerate();
                        bullet.setMovement(target.normalize().multiply(3));

                        pane.getChildren().add(bullet.getCharacter());

                        // update timestamp when last bullet was fired
                        lastEnemyBullet = now;
                    }
                });

                // move the bullets
                bullets.forEach(Character::move);

                // update distance travelled
                bullets.forEach(Bullet::setDist);

                // if space is pressed and enough time has passed since the last bullet was fired, spawn new bullet based on player ship's location/rotation
                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) && now - lastPlayerBullet > 200_000_000) {
                    Bullet bullet = new Bullet((int) PlayerShip.getCharacter().getTranslateX(), (int) PlayerShip.getCharacter().getTranslateY(), true);
                    bullet.getCharacter().setRotate(PlayerShip.getCharacter().getRotate());
                    bullets.add(bullet);

                    // send bullet on its path
                    bullet.accelerate();
                    bullet.setMovement(bullet.getMovement().normalize().multiply(3));

                    pane.getChildren().add(bullet.getCharacter());

                    // update timestamp when last bullet was fired
                    lastPlayerBullet = now;
                }

                bullets.forEach(bullet -> {

                    List<Asteroid> collisions = asteroids.stream().filter(asteroid -> asteroid.collide(bullet)).toList();


                    collisions.stream().forEach(collided -> {
                        asteroids.remove(collided);
                        pane.getChildren().remove(collided.getCharacter());
                    });
                });

                bullets.stream()
                        .filter(bullet -> !bullet.isAlive())
                        .forEach(bullet -> pane.getChildren().remove(bullet.getCharacter()));

                bullets.removeAll(bullets.stream()
                        .filter(bullet -> !bullet.isAlive())
                        .toList());

                bullets.stream()
                        .filter(bullet -> bullet.getDist() > Bullet.getMAXDIST())
                        .forEach(bullet -> pane.getChildren().remove(bullet.getCharacter()));

                bullets.removeAll(bullets.stream()
                        .filter(bullet -> bullet.getDist() > Bullet.getMAXDIST())
                        .toList());
            }
        }.start();
    }
}