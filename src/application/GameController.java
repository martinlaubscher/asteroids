package application;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.Asteroid;
import model.Bullet;
import model.EnemyShip;
import model.PlayerShip;
import model.Size;
import model.Character;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class GameController {
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;
	private Stage stage;
	private Pane pane = new Pane();
	private AnchorPane gamePane;
	private Scene scene;
	private MediaPlayer mediaPlayer;
	private final static String BACKGROUND_IMAGE = "/resources/deep_blue.png";

	public GameController() {
		initializeStage();
	}

	private void initializeStage() {
		gamePane = new AnchorPane();
		scene = new Scene(gamePane, WIDTH, HEIGHT);
		stage = new Stage();
		stage.setScene(scene);

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
		int currentLevel = 1;

		pane.setPrefSize(1024, 768);

		PlayerShip playerShip = new PlayerShip(WIDTH / 2, HEIGHT / 2);
		pane.getChildren().add(playerShip.getCharacter());
		List<EnemyShip> enemyShips = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Random rnd = new Random();
			EnemyShip EnemyShip = new EnemyShip((rnd.nextInt(WIDTH)), (rnd.nextInt(HEIGHT)));
			enemyShips.add(EnemyShip);
		}

		enemyShips.forEach(EnemyShip -> pane.getChildren().add(EnemyShip.getCharacter()));

		// list storing all (active) asteroids
		List<Asteroid> asteroids = new ArrayList<>();

		// list storing all (active) bullets
		List<Bullet> bullets = new ArrayList<>();

		// list storing all (active) characters
		List<Character> characters = new ArrayList<>();

		// For level i, creates i large asteroids at the beginning
		for (int i = 0; i < currentLevel; i++) {
			Random rnd = new Random();
			Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH), rnd.nextInt(HEIGHT), Size.LARGE);
			asteroids.add(asteroid);
		}

		// Adds initial asteroids to the pane
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
			// timestamps recording when the last bullet was fired - used to avoid fire rate
			// being too high
			private long lastPlayerBullet = 0;
			private long lastEnemyBullet = 0;

			@Override
			public void handle(long now) {
				if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
					playerShip.turnLeft();
				}

				if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
					playerShip.turnRight();
				}

				if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
					playerShip.accelerate();
				}

				if (pressedKeys.getOrDefault(KeyCode.DOWN, false)) {
					playerShip.decelerate();
				}
				playerShip.move();

				asteroids.forEach(Asteroid::move);
				enemyShips.forEach(Character::accelerate);

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
						bullet.setMovement(target.normalize().multiply(4));

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
					bullet.setMovement(bullet.getMovement().normalize().multiply(4));
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
				bullets.stream().filter(bullet -> bullet.getDist() > Bullet.getMAXDIST())
						.forEach(bullet -> pane.getChildren().remove(bullet.getCharacter()));
				bullets.removeAll(bullets.stream().filter(bullet -> bullet.getDist() > Bullet.getMAXDIST()).toList());
			}

			/**
			 * Takes a list of characters and removes the characters that aren't alive -
			 * much like a hearse.
			 * 
			 * @param list The list of characters to check.
			 */
			private <Temp extends Character> void hearse(List<Temp> list) {
				list.stream().filter(character -> !character.isAlive())
						.forEach(character -> pane.getChildren().remove(character.getCharacter()));

				list.removeAll(list.stream().filter(character -> !character.isAlive()).toList());
			}

			/**
			 * Helper method for 'hearse'. Takes a list of lists and calls 'hearse' for each
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
			 * Method for splitting an asteroid into two asteroids
			 * 
			 * @param asteroid The asteroid that might need to be split.
			 */
			public void splitAsteroids(Asteroid asteroid) {
				if (asteroid.getSize() == Size.LARGE) {
					for (int i = 0; i < 2; i++) {
						Asteroid newAsteroid = new Asteroid((int) asteroid.getCharacter().getTranslateX(),
								(int) asteroid.getCharacter().getTranslateY(), Size.MEDIUM);
						asteroids.add(newAsteroid);
						pane.getChildren().add(newAsteroid.getCharacter());
					}
				} else if (asteroid.getSize() == Size.MEDIUM) {
					for (int i = 0; i < 2; i++) {
						Asteroid newAsteroid = new Asteroid((int) asteroid.getCharacter().getTranslateX(),
								(int) asteroid.getCharacter().getTranslateY(), Size.SMALL);
						asteroids.add(newAsteroid);
						pane.getChildren().add(newAsteroid.getCharacter());
					}
				}
				asteroid.setAlive(false);
			}

			/**
			 * Checks and handles collisions between characters.
			 */
			public void collisions() {
				characters.forEach(character -> {
					for (Character otherCharacter : characters) {
						// check that collision happens with other character & not with itself
						if (otherCharacter != character && character.collide(otherCharacter)) {
							switch (character.getClass().getSimpleName()) {
							// collision handling for player ship
							case "PlayerShip" -> {
								// ignoring friendly fire
								if (!(otherCharacter instanceof Bullet) || !(((Bullet) otherCharacter).isFriendly())) {
									((PlayerShip) character).decrementLives();
									// TODO implement respawn/game over
								}
							}
							// collision handling for enemy ships
							case "EnemyShip" -> {
								// ignoring friendly fire
								if (!(otherCharacter instanceof Bullet) || ((Bullet) otherCharacter).isFriendly()) {
									character.setAlive(false);
								}
							}
							// collision handling for asteroids
							case "Asteroid" -> {
								// ignoring collisions with other asteroids
								if (!(otherCharacter instanceof Asteroid)) {
									character.setAlive(false);
									splitAsteroids((Asteroid) character);
								}
							}
							// collision handling for bullets
							case "Bullet" -> {
								// collision handling for friendly bullets
								if (((Bullet) character).isFriendly()) {
									// ignoring friendly fire
									if (otherCharacter != playerShip) {
										character.setAlive(false);
									}
									// collision handling for enemy bullets
								} else {
									// ignoring friendly fire
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
		}.start();
	}
	
	
	public void playBackgroundSound() {
		String musicpath = "C:/Users/ridhi/eclipse-workspace/Asteroids/src/resources/music.mp3";
		Media sound = new Media(Paths.get(musicpath).toUri().toString());
		mediaPlayer = new MediaPlayer(sound);
		mediaPlayer.play();
		
	}

	private void createBackground() {
		Image backgroundImage = new Image(BACKGROUND_IMAGE, 256, 256, false, false);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		pane.setBackground(new Background(background));
}
	
}
