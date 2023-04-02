package application;

import javafx.scene.shape.Polygon;

public class Bullet extends Character {
    private static final int SPEED = 3;
    private static final double MAXDIST = 350;
    // flag indicating if player bullet
    private final boolean friendly;
    //    distance traveled
    private double dist;

    // constructor for a bullet
    public Bullet(int x, int y, boolean friendly) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        this.dist = 0;
        this.friendly = friendly;
    }

    // getter function to get the MAXDISTANCE value of the Bullet class
    public static double getMAXDIST() {
        return MAXDIST;
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
