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
import java.util.stream.Collectors;

public class Main extends Application {
    public static int WIDTH = 600;
    public static int HEIGHT = 400;
    public Pane pane = new Pane();
    
    // Adds currentLevel to implement level progression
    private int currentLevel;
    
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
        EnemyShips.forEach(EnemyShip -> pane.getChildren().add(EnemyShip.getCharacter()));
        
        // Creates arrays to store asteroids of different sizes
        List<Asteroid> largeAsteroids = new ArrayList<>();
		List<Asteroid> medAsteroids = new ArrayList<>();
		List<Asteroid> smallAsteroids = new ArrayList<>();
		
		// Creates an array to store playerBullets
		List<PlayerBullet> bullets = new ArrayList<>();
        
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
        stage.show();

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });

        new AnimationTimer() {
        	
        	// Records lastBulletUpdate time
        	private long lastBulletUpdate = 0;
        	
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
                
                // Shoot bullets
                if(pressedKeys.getOrDefault(KeyCode.SPACE,false)&& now - lastBulletUpdate >= 280_000_000) {
                	PlayerBullet bullet = new PlayerBullet((int) playerShip.getCharacter().getTranslateX(), (int) playerShip.getCharacter().getTranslateY());
				    bullet.getCharacter().setRotate(playerShip.getCharacter().getRotate());
				    bullets.add(bullet);
				    
				    bullet.accelerate();
				    bullet.setMovement(bullet.getMovement().normalize().multiply(3));
				    pane.getChildren().add(bullet.getCharacter());
				    
				    lastBulletUpdate = now;
                }
                
                // Enable items to move
                playerShip.move();
                bullets.forEach(bullet -> bullet.move());
                largeAsteroids.forEach(asteroid -> asteroid.move());
                medAsteroids.forEach(asteroid -> asteroid.move());
		        smallAsteroids.forEach(asteroid -> asteroid.move());
               
                
                EnemyShips.forEach(EnemyShip -> EnemyShip.accelerate());
                EnemyShips.forEach(EnemyShip -> {
                    if (playerShip.collide(EnemyShip)) {
                        stop();
                    }
                });
                
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
                removeDeadBullets(bullets);
                removeDeadAsteroids(largeAsteroids);
		        removeDeadAsteroids(medAsteroids);
		        removeDeadAsteroids(smallAsteroids);
            }
        }.start();
    }
    	public void removeDeadBullets(List<PlayerBullet> bullets) {
    		// Isolates bullets that have hit an asteroid
    		bullets.stream()
				.filter(bullet -> !bullet.isAlive())
				.forEach(bullet -> pane.getChildren().remove(bullet.getCharacter()));
		
    		// Removes bullets that have hit an asteroid
    		bullets.removeAll(bullets.stream()
    			.filter(bullet -> !bullet.isAlive())
    			.collect(Collectors.toList()));
		
    		// Isolates bullets that have travelled too far
    		bullets.stream()
        		.filter(bullet -> bullet.getDistanceTravelled() > bullet.getMaxDistance())
        		.forEach(bullet -> pane.getChildren().remove(bullet.getCharacter()));
        
    		// Removes bullets that have travelled too far
    		bullets.removeAll(bullets.stream()
    				.filter(bullet -> bullet.getDistanceTravelled() > bullet.getMaxDistance())
    				.collect(Collectors.toList()));
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
    		 Asteroid newAsteroid = new Asteroid(asteroid.getCharacter().getTranslateX(), asteroid.getCharacter().getTranslateY(), newSize);
             asteroids.add(newAsteroid);
             pane.getChildren().add(newAsteroid.getCharacter());
    	 }
     }
    
    public static void main(String[] args) {
        launch(args);
    }
}