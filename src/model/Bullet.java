package model;

import application.GameController;
import javafx.scene.shape.Polygon;

public class Bullet extends Character {
	private static final int SPEED = 4;
	private static final double MAXDIST = GameController.HEIGHT * 0.8;
	// flag indicating if player bullet
	private final boolean friendly;
	// distance traveled
	private double dist;

	// constructor for a bullet
	public Bullet(int x, int y, boolean friendly) {
		super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
		this.dist = 0;
		this.friendly = friendly;
	}

	// getter function to get the MAXDIST value of the Bullet class
	public static double getMaxdist() {
		return MAXDIST;
	}

	// getter function to get the SPEED value of the Bullet class
	public static int getSpeed() {
		return SPEED;
	}

	// setter function to set the distance the bullet has traveled
	public void setDist() {
		this.dist += SPEED;
	}

	// getter function to get the distance the bullet has traveled
	public double getDist() {
		return dist;
	}

	// getter function to get value of the friendly flag
	public boolean isFriendly() {
		return friendly;
	}

}