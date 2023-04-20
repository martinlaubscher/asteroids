package model;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public abstract class Ship extends Character {
    private long lastBullet;

    public Ship(Polygon character, int x, int y) {
        super(character, x, y);
        this.lastBullet = 0;
    }

    public long getLastBullet() {
        return lastBullet;
    }

    // fire untargeted bullet, e.g. for player ship
    public Bullet fire(long now) {
        Bullet bullet = Bullet.spawnBullet(
                (int) this.getCharacter().getTranslateX(),
                (int) this.getCharacter().getTranslateY(),
                true,
                this.getCharacter().getRotate());

        // move bullet
        bullet.setMovement(bullet.getMovement().normalize().multiply(Bullet.getSpeed()));
        // add ship's momentum to bullet trajectory
        bullet.setMovement(bullet.getMovement().add(this.getMovement()));

        // update timestamp when last bullet was fired
        this.lastBullet = now;

        return bullet;
    }

    // fire targeted bullet, e.g. for enemy ship
    public Bullet fire(Character targetedCharacter, long now) {
        Bullet bullet = Bullet.spawnBullet(
                (int) this.getCharacter().getTranslateX(),
                (int) this.getCharacter().getTranslateY(),
                false,
                0);

        // calculate the direction to fire in - 'target' represents a vector pointing
        // from the ship firing to the character targeted
        double targetX = targetedCharacter.getCharacter().getTranslateX()
                - this.getCharacter().getTranslateX();
        double targetY = targetedCharacter.getCharacter().getTranslateY()
                - this.getCharacter().getTranslateY();
        Point2D target = new Point2D(targetX, targetY);

        // move bullet
        bullet.setMovement(target.normalize().multiply(Bullet.getSpeed()));

        // update timestamp when last bullet was fired
        this.lastBullet = now;

        return bullet;
    }
}