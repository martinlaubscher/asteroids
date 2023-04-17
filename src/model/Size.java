package model;

// if adding new sizes, make sure to maintain the descending order, otherwise the splitAsteroids method in GameController won't work properly!
public enum Size {
	LARGE(20), MEDIUM(50), SMALL(100);

	private final int points;

	Size(int points) {
		this.points = points;
	}

	public int points() {
		return points;
	}
}