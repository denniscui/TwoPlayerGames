package ui_models;

/**
 * Auxiliary information for a game cell.
 * 
 * @author denniscui
 * 
 */
public class Cell {
	// The value for a cell
	private int value;

	// The row
	private int row;

	// The column
	private int col;

	/**
	 * Produces a default cell with a given row and column.
	 * 
	 * @param row
	 *            the row this cell is in
	 * @param col
	 *            the column this cell is in
	 */
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		this.value = 0;
	}

	/**
	 * Produces a cell with a given row, column, and value.
	 * 
	 * @param row
	 *            the row this cell is in
	 * @param col
	 *            the column this cell is in
	 * @param value
	 *            the value attributed to this cell
	 */
	public Cell(int row, int col, int value) {
		this.row = row;
		this.col = col;
		this.value = value;
	}

	/**
	 * Setter for value.
	 * 
	 * @param value
	 *            the new value to put in this cell
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Getter for value.
	 * 
	 * @return the current value of this cell
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Getter for row.
	 * 
	 * @return the cell's row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Getter for column.
	 * 
	 * @return the cell's column
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Determines whether this cell already has a value.
	 * 
	 * @return true iff value != 0
	 */
	public boolean isFilled() {
		return value != 0;
	}
}
