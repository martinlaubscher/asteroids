package model;

import java.util.Random;

import application.GameController;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public class EnemyShip extends Ship {

    private long lastDirectionChange = 0;
    private final static long DIRECTION_CHANGE_INTERVAL = 5_000_000_000L;

    public EnemyShip(int x, int y) {
        super(new Polygon(-30, 0, -20, 10, 20, 10, 30, 0, 20, -10, 20, -10, 10, -17, -10, -17, -15, -10, -20, -10), x, y);
        setRandomMovement();
    }

    @Override
    public void move() {
        long now = System.nanoTime();
        // check if DIRECTION_CHANGE_INTERVAL time has passed since last direction change
        if (now - lastDirectionChange > DIRECTION_CHANGE_INTERVAL) {
            Random random = new Random();
            double randomX = (random.nextDouble() * 2 - 1) * 2; // Generates a random number between -2 and 2
            double randomY = (random.nextDouble() * 2 - 1) * 2; // Generates a random number between -2 and 2

            setMovement(new Point2D(randomX, randomY));
            lastDirectionChange = now;
        }

        double newX = getCharacter().getTranslateX() + movement.getX();
        double newY = getCharacter().getTranslateY() + movement.getY();

        // handle the ship going off the screen and appearing on the opposite side
        if (newX < -40) {
            newX = GameController.WIDTH + 40;
        } else if (newX > GameController.WIDTH + 40) {
            newX = -40;
        }

        if (newY < -40) {
            newY = GameController.HEIGHT + 40;
        } else if (newY > GameController.HEIGHT + 40) {
            newY = -40;
        }

        getCharacter().setTranslateX(newX);
        getCharacter().setTranslateY(newY);
    }

    public void setRandomMovement() {
        Random rnd = new Random();
        double randomX = -0.5 + rnd.nextDouble();
        double randomY = -0.5 + rnd.nextDouble();
        setMovement(new Point2D(randomX, randomY));
    }

    public long getLastDirectionChange() {
        return lastDirectionChange;
    }

    public void setLastDirectionChange(long lastDirectionChange) {
        this.lastDirectionChange = lastDirectionChange;
    }
}