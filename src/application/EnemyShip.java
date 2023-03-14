package application;

import java.util.Random;
//import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public class EnemyShip extends Ship {
    public EnemyShip(int x, int y) {
        super(new Polygon(-30, 0, -20, 10, 20, 10, 30, 0, 20, -10, 20, -10, 10, -17, -10, -17, -15, -10, -20, -10), x, y);
        Random rnd = new Random();
        int accelerationAmount = 1 + rnd.nextInt(10);
        for (int i = 0; i < accelerationAmount; i++) {
            accelerate();
        }
    }

    @Override
    public void move() {
        super.move();
        super.getCharacter().setRotate(0);
    }
}
