package model;

import application.GameController;
import javafx.scene.shape.Polygon;

public class Bullet extends Character {
	private static final int SPEED = 4;
	private static final double MAX_DISTANCE = GameController.HEIGHT * 0.8;
	// flag indicating if player bullet
	private final boolean friendly;
	// distance traveled
	private double distance;

	// constructor for a bullet
	private Bullet(int x, int y, boolean friendly) {
		super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
		this.distance = 0;
		this.friendly = friendly;
	}

	// factory method to "spawn" a bullet (create -> rotate -> accelerate)
	static Bullet spawnBullet(int x, int y, boolean friendly, double rotation) {
		Bullet bullet = new Bullet(x, y, friendly);
		bullet.getCharacter().setRotate(rotation);
		bullet.accelerate();
		return bullet;
	}

	// getter function to get the MAX_DISTANCE value of the Bullet class
	public static double getMaxDistance() {
		return MAX_DISTANCE;
	}

	// getter function to get the SPEED value of the Bullet class
	public static int getSpeed() {
		return SPEED;
	}

	// setter function to set the distance the bullet has traveled
	public void setDistance() {
		this.distance += SPEED;
	}

	// getter function to get the distance the bullet has traveled
	public double getDistance() {
		return distance;
	}

	// getter function to get value of the friendly flag
	public boolean isFriendly() {
		return friendly;
	}

}