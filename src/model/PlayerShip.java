package model;

import model.Ship;
import javafx.scene.shape.Polygon;
import javafx.geometry.Point2D;

public class PlayerShip extends Ship {
    private static int lives;
    private Boolean safelySpawned;
    private long invulnerabilityEndTime; // Add invulnerabilityEndTime variable
    

    public static void resetLives() {
        lives = 3;
    }

    public PlayerShip(int x, int y) {
        super(new Polygon(-5, -5, 10, 0, -5, 5), x, y);
        this.setLives(3);
        this.setSafelySpawned(true);
    }

    public int getLives() {
        return lives;
    }

    private final void setLives(int lives) {
        PlayerShip.lives = lives;
    }

    public void decrementLives() {
        lives -= 1;
        if (lives < 0) {
            lives = 0;
        }
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
        setInvulnerabilityEndTime(System.nanoTime() + 3_000_000_000L); // Set invulnerability end time to 3 seconds after the current time
    }

    public boolean isRespawning() {
        return !this.safelySpawned;
    }

    public void setInvulnerabilityEndTime(long invulnerabilityEndTime) {
        this.invulnerabilityEndTime = invulnerabilityEndTime;
    }

    public boolean isInvulnerable() {
        return System.nanoTime() <= invulnerabilityEndTime;
    }
}
