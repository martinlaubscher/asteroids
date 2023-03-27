package application;

import javafx.scene.shape.Polygon;

public class Bullet extends Character {
    private static final int SPEED = 10;
    private static final double MAXDIST = 1000;

//    x-coordinate of spawn point
    private int xSpawn;
//    y-coordinate of spawn point
    private int ySpawn;
//    distance traveled
    private double dist;
//    flag indicating if player's bullet
    private boolean friendly;

    // constructor for a bullet
    public Bullet(int x, int y, boolean friendly) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        this.xSpawn = x;
        this.ySpawn = y;
        this.dist = 0;
        this.friendly = friendly;
    }


    // setter function to set the distance the bullet has traveled
    public void setDist() {
        this.dist += SPEED;
    }

    // getter function to get the distance the bullet has traveled
    public double getDist() {
        return dist;
    }

    // getter function to get the MAXDISTANCE value of the Bullet class
    public static double getMAXDIST() {
        return MAXDIST;
    }
}
