package utils;

/**
 * 
 * @author denniscui
 * 
 *         Special point operations specific to 2D board games.
 */
public class Point {

	private int pX;
	private int pY;

	/**
	 * Constructs a point with the given coordinates. Coordinates in a board
	 * game are zero indexed.
	 * 
	 * @param pX
	 *            x coordinate.
	 * @param pY
	 *            y coordinate.
	 */
	public Point(int pX, int pY) {
		this.pX = pX;
		this.pY = pY;
	}

	/**
	 * Returns the distance between two points as a vector.
	 * 
	 * @param other
	 *            the other point.
	 * @return a vector from this point to the other point.
	 */
	public Vector distance(Point other) {
		int xDiff = this.getX() - other.getX();
		int yDiff = this.getY() - other.getY();

		return new Vector(xDiff, yDiff);
	}

	/**
	 * Get the x coordinate of the point.
	 * 
	 * @return pX
	 */
	public int getX() {
		return pX;
	}

	/**
	 * Get the y coordinate of the point.
	 * 
	 * @return pY
	 */
	public int getY() {
		return pY;
	}

	/**
	 * Returns true iff the x and y coordinates are equal
	 */
	public boolean equals(Object other) {
		Point p = (Point) other;
		return (getX() == p.getX()) && (getY() == p.getY());
	}
}
