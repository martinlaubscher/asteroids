package model;

import model.Ship;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Label;
import javafx.geometry.Point2D;

public class PlayerShip extends Ship {
    private int lives;
    private Label livesLabel;
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
        if (this.lives < 0) {
            this.lives = 0;
        }
        livesLabel.setText("Lives: " + this.lives);
    }

    public Boolean isSafelySpawned() {
        return this.safelySpawned;
    }

    public final void setSafelySpawned(Boolean safelySpawned) {
        this.safelySpawned = safelySpawned;
    }

    public void respawn(double x, double y) {
        this.getCharacter().setTranslateX(x);
        this.getCharacter().setTranslateY(y);
        this.setMovement(new Point2D(0, 0));
        this.getCharacter().setRotate(0);
    }

    // Add the isRespawning() method
    public boolean isRespawning() {
        return !this.safelySpawned;
    }
}