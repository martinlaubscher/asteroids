package application;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.application.Platform;
import model.Asteroid;
import model.Bullet;
import model.Character;
import model.EnemyShip;
import model.HighScoresList;
import model.PlayerShip;
import model.Score;
import model.Size;

public class GameController {

    // All constant declaration
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public final static String SCORE_FILE_PATH = "/resources/scores.txt";
    private final static String BACKGROUND_IMAGE = "/resources/space.gif";
    private final static String MUSICPATH = "src/resources/music.mp3";
    HighScoresList scorelist = new HighScoresList();
    MenuScene menuScene = new MenuScene();
    Label levelLabel;
    Label scoreText;
    Label livesLabel;
    Label invulnerabilityLabel;
    Pane pane = new Pane();
    private Stage stage;
    private AnchorPane gamePane;
    private Scene scene;
    private MediaPlayer mediaPlayer;
    private int level = 0;
    private int score = 0;
    private String name = "";
    Score championScore = new Score(name, score);
    // All instanstiation
    private List<Asteroid> asteroids = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<EnemyShip> enemyShips = new ArrayList<>();
    private List<Character> characters = new ArrayList<>();
    private PlayerShip playerShip;
    private AnimationTimer animationTimer;

    // Parameterized constructor with parameter name
    public GameController(String name) {
        initializeStage();
        this.name = name;

    }

    public static int randomX() {
        return (int) (Math.random() * GameController.WIDTH);
    }

    public static int randomY() {
        return (int) (Math.random() * GameController.HEIGHT);
    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        scene = new Scene(gamePane, WIDTH, HEIGHT);
        stage = new Stage();
        stage.setScene(scene);

    }

    private void spawnAsteroidsIfNone() {
        if (asteroids.stream().filter(Asteroid::isAlive).count() == 0) {
            level++; // Increment level
            levelLabel.setText("Level: " + level); // Update level text
            spawnAsteroids(level);
        }
    }

    private void spawnAsteroids(int level) {
        for (int i = 0; i < level; i++) {
            int x = randomX();
            int y = randomY();
            Asteroid asteroid = new Asteroid(x, y, Size.LARGE);
            asteroids.add(asteroid);
            pane.getChildren().add(asteroid.getCharacter());
        }
    }

    private void showGameOverScreen() {
        stage.close();
        mediaPlayer.stop();
        createGameOverStage();
    }

    private void spawnEnemyShips() {
        Timeline enemyShipSpawner = new Timeline(new KeyFrame(Duration.seconds(10)), // wait 10 seconds before spawning
                // first enemy ship
                new KeyFrame(Duration.seconds(20), event -> {
                    Random rnd = new Random();
                    EnemyShip enemyShip = new EnemyShip(rnd.nextInt(WIDTH), rnd.nextInt(HEIGHT));
                    pane.getChildren().add(enemyShip.getCharacter());
                    enemyShips.add(enemyShip);
                }));
        enemyShipSpawner.setCycleCount(Timeline.INDEFINITE);
        enemyShipSpawner.play();
    }

    private void hyperspaceJump(PlayerShip playerShip, List<Asteroid> asteroids, List<EnemyShip> enemyShips) {
        Random random = new Random();
        Point2D newPosition;
        boolean isSafe;

        do {
            newPosition = new Point2D(random.nextInt(WIDTH), random.nextInt(HEIGHT));
            isSafe = checkSafePosition(newPosition, playerShip, asteroids, enemyShips);
        } while (!isSafe);

        playerShip.getCharacter().setTranslateX(newPosition.getX());
        playerShip.getCharacter().setTranslateY(newPosition.getY());
    }

    private boolean checkSafePosition(Point2D newPosition, PlayerShip playerShip, List<Asteroid> asteroids,
                                      List<EnemyShip> enemyShips) {
        final double safeDistance = 50;

        for (Asteroid asteroid : asteroids) {
            Point2D asteroidPosition = new Point2D(asteroid.getCharacter().getTranslateX(),
                    asteroid.getCharacter().getTranslateY());
            if (newPosition.distance(asteroidPosition) < safeDistance) {
                return false;
            }
        }

        for (EnemyShip enemyShip : enemyShips) {
            Point2D enemyShipPosition = new Point2D(enemyShip.getCharacter().getTranslateX(),
                    enemyShip.getCharacter().getTranslateY());
            if (newPosition.distance(enemyShipPosition) < safeDistance) {
                return false;
            }
        }

        return true;
    }

    /**
     * Combines the various list containing active characters into list
     * 'characters'.
     */
    private void getCharacters() {
        // reset the list
        characters.clear();

        // add characters stored in various lists
        characters.addAll(bullets);
        characters.addAll(asteroids);
        characters.addAll(enemyShips);
        characters.add(playerShip);
    }

    /**
     * Moves the active bullets, updates their distance travelled, and removes them
     * if they have travelled too far.
     */
    private void updateBullets() {
        // move the bullets
        bullets.forEach(Character::move);

        // update distance travelled
        bullets.forEach(Bullet::setDist);

        // removing bullets that have exceeded MAXDIST
        bullets.stream().filter(bullet -> bullet.getDist() > Bullet.getMaxdist())
                .forEach(bullet -> pane.getChildren().remove(bullet.getCharacter()));
        bullets.removeAll(bullets.stream().filter(bullet -> bullet.getDist() > Bullet.getMaxdist()).toList());
    }

    /**
     * Takes a list of characters and removes the characters that aren't alive -
     * much like a hearse.
     *
     * @param list The list of characters to check.
     */
    private <T extends Character> void hearse(List<T> list) {
        list.stream().filter(character -> !character.isAlive())
                .forEach(character -> pane.getChildren().remove(character.getCharacter()));

        list.removeAll(list.stream().filter(character -> !character.isAlive()).toList());
    }

    /**
     * Helper method for 'hearse'. Takes multiple lists and calls 'hearse' for each
     * of them.
     *
     * @param lists The list of lists to check.
     */
    @SafeVarargs
    private void hearseMultiple(List<? extends Character>... lists) {
        for (List<? extends Character> list : lists) {
            hearse(list);
        }
    }

    /**
     * Method for splitting an asteroid into two asteroids and increasing score if
     * appropriate
     *
     * @param asteroid       The asteroid that might need to be split.
     * @param otherCharacter the character colliding with the asteroid
     */
    private void splitAsteroids(Asteroid asteroid, Character otherCharacter) {

        if (otherCharacter instanceof Bullet && ((Bullet) otherCharacter).isFriendly()) {
            // Add points to the score based on asteroid size
            score += asteroid.getSize().points();
            scoreText.setText("Score: " + score); // Update score text
        }

        if (asteroid.getSize().ordinal() < Size.values().length - 1) {
            for (int i = 0; i < 2; i++) {
                Asteroid newAsteroid = new Asteroid((int) asteroid.getCharacter().getTranslateX(),
                        (int) asteroid.getCharacter().getTranslateY(),
                        Size.values()[asteroid.getSize().ordinal() + 1]);
                asteroids.add(newAsteroid);
                pane.getChildren().add(newAsteroid.getCharacter());
            }
        }
    }

    /**
     * Checks and handles collisions between characters.
     */
    private void collisions() {
        characters.forEach(character -> {
            for (Character otherCharacter : characters) {
                // Check that collision happens with other character & not with itself
                if (otherCharacter != character && character.collide(otherCharacter)) {
                    switch (character.getClass().getSimpleName()) {
                        // Collision handling for player ship
                        case "PlayerShip" -> {
                            if (!playerShip.isInvulnerable() && (!(otherCharacter instanceof Bullet)
                                    || !(((Bullet) otherCharacter).isFriendly()))) {
                                playerShip.decrementLives();

                                // Respawn the player ship in the middle of the screen
                                playerShip.respawn(GameController.WIDTH / 2, GameController.HEIGHT / 2);

                                // Set the invulnerability end time (3 seconds)
                                playerShip.setInvulnerabilityEndTime(System.nanoTime() + 3_000_000_000L);

                                // Update the lives label
                                livesLabel.setText("Lives: " + Integer.toString(playerShip.getLives()));
                                if (playerShip.getLives() <= 0) {
                                    showGameOverScreen();
                                    stopGame();
                                } else {
                                    livesLabel.setText("Lives: " + playerShip.getLives());
                                }

                                invulnerabilityLabel.setText("Invulnerability Countdown:" + playerShip.getInvulnerabilityTimeLeft() + "s");
                                pane.getChildren().add(invulnerabilityLabel);

                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    public void run() {
                                        if (!playerShip.isInvulnerable()) {
                                            Platform.runLater(() -> pane.getChildren().remove(invulnerabilityLabel));
                                            timer.cancel();
                                        } else {
                                            Platform.runLater(() -> invulnerabilityLabel.setText("Invulnerability Countdown: " + playerShip.getInvulnerabilityTimeLeft() + "s"));
                                        }
                                    }
                                }, 0, 1000);
                            }
                        }
                        // Collision handling for enemy ships
                        case "EnemyShip" -> {
                            // Ignoring own bullets
                            if (!(otherCharacter instanceof Bullet) || ((Bullet) otherCharacter).isFriendly()) {
                                character.setAlive(false);
                            }
                        }
                        // Collision handling for asteroids
                        case "Asteroid" -> {
                            // Ignoring collisions with other asteroids
                            if (!(otherCharacter instanceof Asteroid)) {
                                character.setAlive(false);
                                splitAsteroids((Asteroid) character, otherCharacter);
                            }
                        }
                        // Collision handling for bullets
                        case "Bullet" -> {
                            // Collision handling for friendly bullets
                            if (((Bullet) character).isFriendly()) {
                                // Ignoring friendly fire
                                if (otherCharacter != playerShip) {
                                    character.setAlive(false);
                                }
                                // Collision handling for enemy bullets
                            } else {
                                // Ignoring friendly fire
                                if (!(otherCharacter instanceof EnemyShip)) {
                                    character.setAlive(false);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Method launching and running the game.
     */
    public void startGame() {
        this.stage.hide();
        createBackground();
        playBackgroundSound();
        // Stop the music when the window is closed
        stage.setOnCloseRequest(event -> {
            mediaPlayer.stop();
        });
        // Initializes currentLevel as 1 to implement level progression
        int currentLevel = level;

        pane.setPrefSize(1024, 768);

        playerShip = new PlayerShip(WIDTH / 2, HEIGHT / 2);
        pane.getChildren().add(playerShip.getCharacter());

        livesLabel = new Label("Lives: " + playerShip.getLives());
        livesLabel.setTextFill(Color.WHITE);
        livesLabel.setFont(Font.font("Chiller", FontWeight.BOLD, 24));
        livesLabel.setTranslateX(10);
        livesLabel.setTranslateY(10);
        pane.getChildren().add(livesLabel);

        levelLabel = new Label("Level: " + currentLevel);
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setFont(Font.font("Chiller", FontWeight.BOLD, 24));
        levelLabel.setTranslateX(10);
        levelLabel.setTranslateY(50);
        pane.getChildren().add(levelLabel);

        scoreText = new Label("Score: " + score);
        scoreText.setTextFill(Color.WHITE);
        scoreText.setFont(Font.font("Chiller", FontWeight.BOLD, 24));
        scoreText.setTranslateX(10);
        scoreText.setTranslateY(90);
        pane.getChildren().add(scoreText);

        invulnerabilityLabel = new Label("");
        invulnerabilityLabel.setTextFill(Color.WHITE);
        invulnerabilityLabel.setFont(Font.font("Chiller", FontWeight.BOLD, 24));
        invulnerabilityLabel.setTranslateX(WIDTH / 2 - 150);
        invulnerabilityLabel.setTranslateY(10);

        // For level i, creates i large asteroids at the beginning
        for (int i = 0; i < currentLevel; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH), rnd.nextInt(HEIGHT), Size.LARGE);
            this.asteroids.add(asteroid);
        }

        // Adds initial asteroids to the pane
        this.asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));
        Timeline initialEnemyShipSpawnDelay = new Timeline(new KeyFrame(Duration.seconds(10)));
        initialEnemyShipSpawnDelay.setOnFinished(event -> spawnEnemyShips());
        initialEnemyShipSpawnDelay.play();

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

        animationTimer = new AnimationTimer() {
            // timestamps recording when the last bullet was fired - used to avoid fire rate
            // being too high
            private long lastPlayerBullet = 0;
            private long lastEnemyBullet = 0;

            @Override
            public void handle(long now) {
                if (pressedKeys.getOrDefault(KeyCode.H, false)) {
                    hyperspaceJump(playerShip, asteroids, enemyShips);
                }

                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    playerShip.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    playerShip.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    playerShip.accelerate();
                }
                playerShip.move();

                asteroids.forEach(Asteroid::move);
                enemyShips.forEach(enemyShip -> {
                    long currentTime = System.nanoTime();
                    if (currentTime - enemyShip.getLastDirectionChange() > 1000000000000L) {
                        enemyShip.setRandomMovement();
                        enemyShip.setLastDirectionChange(currentTime);
                    }
                    enemyShip.move();
                });

                // enemy ship fire
                enemyShips.forEach(EnemyShip -> {
                    // if the enemy ship is alive, shoot a bullet every 10 seconds
                    if (EnemyShip.isAlive() && now - lastEnemyBullet > 10_000_000_000L) {
                        Bullet bullet = new Bullet((int) EnemyShip.getCharacter().getTranslateX(),
                                (int) EnemyShip.getCharacter().getTranslateY(), false);

                        // calculate the direction to fire in - 'target' represents a vector pointing
                        // from the enemy ship to the player ship
                        double targetX = playerShip.getCharacter().getTranslateX()
                                - EnemyShip.getCharacter().getTranslateX();
                        double targetY = playerShip.getCharacter().getTranslateY()
                                - EnemyShip.getCharacter().getTranslateY();
                        Point2D target = new Point2D(targetX, targetY);

                        bullets.add(bullet);

                        // send bullet on its path
                        bullet.accelerate();
                        bullet.setMovement(target.normalize().multiply(Bullet.getSpeed()));

                        pane.getChildren().add(bullet.getCharacter());

                        // update timestamp when last bullet was fired
                        lastEnemyBullet = now;
                    }
                });

                // if space is pressed and enough time has passed since the last bullet was
                // fired, spawn new bullet based on player ship's location/rotation
                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) && now - lastPlayerBullet > 200_000_000) {
                    Bullet bullet = new Bullet((int) playerShip.getCharacter().getTranslateX(),
                            (int) playerShip.getCharacter().getTranslateY(), true);
                    bullet.getCharacter().setRotate(playerShip.getCharacter().getRotate());
                    bullets.add(bullet);

                    // accelerate bullet
                    bullet.accelerate();
                    // move bullet based on ship's rotation
                    bullet.setMovement(bullet.getMovement().normalize().multiply(Bullet.getSpeed()));
                    // add ship's momentum to bullet trajectory
                    bullet.setMovement(bullet.getMovement().add(playerShip.getMovement()));

                    pane.getChildren().add(bullet.getCharacter());

                    // update timestamp when last bullet was fired
                    lastPlayerBullet = now;
                }

                // calling method to update bullet position, distance travelled, and remove
                // bullets if exceeding MAXDIST
                updateBullets();

                // calling method to repopulate list with active characters
                getCharacters();

                // calling method checking collisions
                collisions();

                // removing characters marked as dead by the collisions method
                hearseMultiple(bullets, asteroids, enemyShips);

                // Check if all asteroids are destroyed and spawn new ones if needed
                spawnAsteroidsIfNone();

            }
        };
        animationTimer.start();
    }

    public void stopGame() {
        animationTimer.stop();
    }

    public void playBackgroundSound() {
        Media sound = new Media(Paths.get(MUSICPATH).toUri().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();

    }

    private void createBackground() {
        Image backgroundImage = new Image(BACKGROUND_IMAGE, 256, 256, false, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
        pane.setBackground(new Background(background));
    }

    private void createGameOverStage() {
        StackPane pane = new StackPane();
        Image backgroundImage = new Image(BACKGROUND_IMAGE, 256, 256, false, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
        pane.setBackground(new Background(background));

        Label gameOverLabel = new Label("Game Over!!! Well Played " + name + " ");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setFont(Font.font("Chiller", FontWeight.BOLD, 58));

        Label scoreLabel = new Label("Total Score: " + score);
        scoreLabel.setTextFill(Color.RED);
        scoreLabel.setFont(Font.font("Chiller", FontWeight.BOLD, 40));

        Label instruction = new Label("Press Enter to continue");
        instruction.setTextFill(Color.WHITE);
        instruction.setFont(Font.font("Chiller", FontWeight.BOLD, 40));

        championScore.setName(name);
        championScore.setScore(score);

        scorelist.addScore(championScore);
        System.out.println("Score is: " + scorelist);

        VBox vbox = new VBox(20, gameOverLabel, scoreLabel, instruction);
        vbox.setAlignment(Pos.CENTER);
        pane.getChildren().add(vbox);

        Scene scene = new Scene(pane, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                stage.close();

            }
        });
    }
}
