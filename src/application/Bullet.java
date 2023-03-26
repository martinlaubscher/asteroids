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

    public Bullet(int x, int y, boolean friendly) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        this.xSpawn = x;
        this.ySpawn = y;
        this.dist = 0;
        this.friendly = friendly;
    }

    public void setDist() {
        // double currentX = this.getCharacter().getTranslateX();
        // double currentY = this.getCharacter().getTranslateY();
        // this.dist = Math.sqrt(Math.pow(currentX - xSpawn, 2) + Math.pow(currentY - ySpawn, 2));
        this.dist += SPEED;
    }

    public double getDist() {
        return dist;
    }

    public static double getMAXDIST() {
        return MAXDIST;
    }
}
