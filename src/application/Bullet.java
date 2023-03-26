package application;

import javafx.scene.shape.Polygon;

public class Bullet extends Character {
    private static final int SPEED = 10;
    private static final int MAXDIST = 400;

//    x-coordinate
    private int x;
//    y-coordinate
    private int y;
//    distance traveled
    private int dist;
//    flag indicating if player's bullet
    private boolean friendly;

    public Bullet(int x, int y, boolean friendly) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        this.x = x;
        this.y = y;
        this.dist = 0;
        this.friendly = friendly;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public int getDist() {
        return dist;
    }
}
