package application;

import javafx.scene.shape.Polygon;

public class PlayerShip extends Ship {
	private int lives;
	private Boolean safelySpawned;
	
    public PlayerShip(int x, int y) {
        super(new Polygon(-5, -5, 10, 0, -5, 5), x, y);
        this.setLives(3);
        this.setSafelySpawned(true);
    }
    
    public int getLives() {
    	return this.lives;
    }
    
    private final void setLives(int lives) {
    	this.lives = lives;
    }
    
    public void decrementLives() {
    	this.lives -= 1;
    }
    
    public Boolean isSafelySpawned() {
    	return this.safelySpawned;
    }
    
    public final void setSafelySpawned(Boolean safelySpawned) {
    	this.safelySpawned = safelySpawned;
    }
}