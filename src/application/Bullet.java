package application;

import javafx.scene.shape.Polygon;

public abstract class Bullet extends Character {
    private static final int SPEED = 10;
    private static final int MAXDIST = 400;

//    x-coordinate
    private int x;
//    y-coordinate
    private int y;
//    direction of travel
    private int dir;
//    distance traveled
    private int dist;

    public Bullet(int x, int y, int dir) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.dist = 0;
    }

    public static int getMaxDist() {
        return MAXDIST;
    }


}
