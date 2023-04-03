package application;

import java.util.Random;

public class Asteroid extends Character {

    private double rotationalMovement;
    private Size size;
    private double scaler;
    private double accelerationAmount;

    public Asteroid(int x, int y, Size size) {
        super(new PolygonFactory().createPolygon(), x, y);

        Random rnd = new Random();

        setDirection(rnd);
        setSpeed(rnd);
        setRotationalMovement(rnd);

        super.getCharacter().setRotate(rnd.nextInt(360));

        accelerationAmount = 1 + rnd.nextInt(10);
        for (int i = 0; i < accelerationAmount; i++) {
            accelerate();
        }

        this.rotationalMovement = 0.5 - rnd.nextDouble();
    }
    public Size getSize() {
    	return this.size;
    }

    private final void setSize() {
    	if (this.size == Size.LARGE) {
    		this.scaler = 3.0;
    	} else if (this.size == Size.MEDIUM) {
    		this.scaler = 2.0;
    	} else if (this.size == Size.SMALL) {
    		this.scaler = 1;
    	} else {
    		this.scaler = 0;
    	}
    	for (int i = 0; i< this.getCharacter().getPoints().size(); i++) {
    		this.getCharacter().getPoints().set(i, this.getCharacter().getPoints().get(i) * scaler);
    	}
    }

    private void setSpeed(Random rnd) {
    	if (this.size == Size.LARGE) {
    		this.accelerationAmount = 20.0 + rnd.nextDouble() * 10.0;
    	} else if (this.size == Size.MEDIUM) {
    		this.accelerationAmount = 45.0 +rnd.nextDouble() * 10.0;
    	} else if (this.size == Size.SMALL) {
    		this.accelerationAmount = 60.0 +rnd.nextDouble() * 5.0;
    	} else {
    		this.accelerationAmount = 0.0;
    	}
    	
    	for (double i = 0; i < this.accelerationAmount; i++) {
    		accelerate();
    	}
    }
    
    private final void setDirection(Random rnd) {
    	super.getCharacter().setRotate(rnd.nextInt(360));
    }
    
    private final void setRotationalMovement(Random rnd) {
    	this.rotationalMovement = 1.0 - rnd.nextDouble();
    }
    
    @Override
    public void move() {
        super.move();
        super.getCharacter().setRotate(super.getCharacter().getRotate() + rotationalMovement);
    }
}