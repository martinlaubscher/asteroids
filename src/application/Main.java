package application;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;

public class Main extends Application {
    public static int WIDTH = 600;
    public static int HEIGHT = 400;
	public Pane pane = new Pane();
	// Adds currentLevel to implement level progression
	private int currentLevel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    
    public void start(Stage stage) throws Exception{
    	
    	// Initializes currentLevel as 1
    	currentLevel = 1;
    	
        pane.setPrefSize(600, 400);

        PlayerShip playerShip = new PlayerShip(WIDTH / 2, HEIGHT / 2);
        pane.getChildren().add(playerShip.getCharacter());
        List<EnemyShip> EnemyShips = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
           Random rnd = new Random();
           EnemyShip EnemyShip = new EnemyShip((rnd.nextInt(WIDTH)), (rnd.nextInt(HEIGHT)));
           EnemyShips.add(EnemyShip);
        }
        pane.getChildren().add(playerShip.getCharacter());
        EnemyShips.forEach(EnemyShip -> pane.getChildren().add(EnemyShip.getCharacter()));
        
        // Creates arrays to store asteroids of different sizes
        List<Asteroid> largeAsteroids = new ArrayList<>();
		List<Asteroid> medAsteroids = new ArrayList<>();
		List<Asteroid> smallAsteroids = new ArrayList<>();
		
		// Creates an array to store playerBullets
		List<Bullet> bullets = new ArrayList<>();
        
		// For level i, creates i large asteroids at the beginning
		for (int i = 0; i < currentLevel; i++) {
			Random rnd = new Random();
			Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH), rnd.nextInt(HEIGHT), Size.LARGE);
			largeAsteroids.add(asteroid);
			
		}
        
        // Adds large asteroids to the pane
        largeAsteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));

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
                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    playerShip.turnLeft();
                }

                if(pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    playerShip.turnRight();
                }

                if(pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    playerShip.accelerate();
                }

                if(pressedKeys.getOrDefault(KeyCode.DOWN, false)) {
                    playerShip.decelerate();
                }
                playerShip.move();
                bullets.forEach(bullet -> bullet.move());
                largeAsteroids.forEach(asteroid -> asteroid.move());
                medAsteroids.forEach(asteroid -> asteroid.move());
		        smallAsteroids.forEach(asteroid -> asteroid.move());
                
                EnemyShips.forEach(EnemyShip -> EnemyShip.accelerate());

                // enemy ship behaviour
                EnemyShips.forEach(EnemyShip -> {
                    if (playerShip.collide(EnemyShip)) {
                        // stop();
                        // TODO on collision, both ships are destroyed (player ship: need to deduct life, respawn etc.)
                        playerShip.decrementLives();
                    }
					// if the enemy ship is alive, shoot a bullet every 10 seconds
                    if (EnemyShip.isAlive() && now - lastEnemyBullet > 10_000_000_000L) {
                        Bullet bullet = new Bullet((int) EnemyShip.getCharacter().getTranslateX(), (int) EnemyShip.getCharacter().getTranslateY(), false);
                        // use player ship's location to determine direction of fire
                        bullets.add(bullet);

                        // send bullet on its path
                        bullet.accelerate();
                        bullet.setMovement(bullet.getMovement().normalize().multiply(3));

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
                    Bullet bullet = new Bullet((int) playerShip.getCharacter().getTranslateX(), (int) playerShip.getCharacter().getTranslateY(), true);
                    bullet.getCharacter().setRotate(playerShip.getCharacter().getRotate());
                    bullets.add(bullet);

                    // send bullet on its path
                    bullet.accelerate();
                    bullet.setMovement(bullet.getMovement().normalize().multiply(3));

                    pane.getChildren().add(bullet.getCharacter());

                    // update timestamp when last bullet was fired
                    lastPlayerBullet = now;
                }
				bullets.forEach(bullet -> {
	                // When the bullet hits a large asteroid, two medium asteroids are created
	                largeAsteroids.forEach(asteroid -> {
	                	if(bullet.collide(asteroid)) {
	                		bullet.setAlive(false);
	                		asteroid.setAlive(false);
	                		splitAsteroids(asteroid, Size.MEDIUM, medAsteroids);
	                	}
	                });
	                
	                // When the bullet hits a medium asteroid, two small asteroids are created
	                medAsteroids.forEach(asteroid -> {
	                	if(bullet.collide(asteroid)) {
	                		bullet.setAlive(false);
	                		asteroid.setAlive(false);
	                		splitAsteroids(asteroid, Size.SMALL, smallAsteroids);
	                	}
	                });
	             
	                // When the bullet hits a small asteroid, no asteroids are created
	                smallAsteroids.forEach(asteroid -> {
	                	if(bullet.collide(asteroid)) {
	                		bullet.setAlive(false);
	                		asteroid.setAlive(false);
	                	}
	                });
	                
                });
                
             // When the playerShip hits a large asteroid, two asteroids are created
                largeAsteroids.forEach(asteroid -> {
                	if(playerShip.collide(asteroid)) {
                		asteroid.setAlive(false);
                		splitAsteroids(asteroid, Size.MEDIUM, medAsteroids);
                	}
                });
                
                // When the playerShip hits a medium asteroid, two small asteroids are created
                medAsteroids.forEach(asteroid -> {
                	if(playerShip.collide(asteroid)) {
                		asteroid.setAlive(false);
                		splitAsteroids(asteroid, Size.SMALL, smallAsteroids);
                	}
                });
         
                
                // When the playerShip hits a small asteroid, no asteroids are created
                smallAsteroids.forEach(asteroid -> {
                	if(playerShip.collide(asteroid)) {
                		asteroid.setAlive(false);
                	}
                });
                
                // Remove dead items
                removeDeadAsteroids(largeAsteroids);
		        removeDeadAsteroids(medAsteroids);
		        removeDeadAsteroids(smallAsteroids);
            }
        }.start();
    }
	public void removeDeadBullets(List<Bullet> bullets) {
		// Isolates bullets that have hit an asteroid
		bullets.stream()
			.filter(bullet -> !bullet.isAlive())
			.forEach(bullet -> pane.getChildren().remove(bullet.getCharacter()));
	
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
	
	// Method for removing dead asteroids
	public void removeDeadAsteroids(List<Asteroid> asteroids) {
		asteroids.stream()
				.filter(asteroid -> !asteroid.isAlive())
				.forEach(asteroid -> pane.getChildren().remove(asteroid.getCharacter()));
		asteroids.removeAll(asteroids.stream()
				.filter(asteroid -> !asteroid.isAlive())
				.collect(Collectors.toList()));
	}
	
	
 // Method for splitting a asteroid into two asteroids
 public void splitAsteroids(Asteroid asteroid, Size newSize, List<Asteroid> asteroids) {
	 for (int i = 0; i < 2; i++) {
		 Asteroid newAsteroid = new Asteroid((int) asteroid.getCharacter().getTranslateX(), (int) asteroid.getCharacter().getTranslateY(), newSize);
		 asteroids.add(newAsteroid);
		 pane.getChildren().add(newAsteroid.getCharacter());
	 }
 }
   
}