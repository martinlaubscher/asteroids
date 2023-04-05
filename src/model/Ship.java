package model;

import javafx.scene.shape.Polygon;
import model.Character;

public abstract class Ship extends Character {
    public Ship(Polygon character, int x, int y) {
        super(character, x, y);
    }
}