package utils;

/**
 * 
 * @author denniscui
 * 
 *         Defines a vector in 2D by its delta x and delta y.
 */
public class Vector {

	private int deltaX;
	private int deltaY;

	/**
	 * Constructs a new vector with the given metrics.
	 * 
	 * @param deltaX
	 *            change in x.
	 * @param deltaY
	 *            change in y.
	 */
	public Vector(int deltaX, int deltaY) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	/**
	 * Get the x coordinate of this vector.
	 * 
	 * @return deltaX
	 */
	public int getDeltaX() {
		return deltaX;
	}

	/**
	 * Get the y coordinate of this vector.
	 * 
	 * @return deltaY
	 */
	public int getDeltaY() {
		return deltaY;
	}

	/**
	 * Get the slope of this vector
	 * 
	 * @return deltaY / deltaX
	 */
	public double slope() {
		return ((double) getDeltaY()) / getDeltaY();
	}
}
