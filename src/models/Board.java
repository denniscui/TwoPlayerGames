package models;

import java.util.ArrayList;

import utils.Point;

public abstract class Board {

	// List of the pieces in the game
	private ArrayList<Piece> pieces;

	// We may also want to have a two-dimensional interpretation of the board to
	// make isEmpty more efficient.

	// Number of rows on this board
	private int rows;

	// Number of columns on this board
	private int cols;

	// Two dimensional board representation
	private String[][] boardMatrix;

	/**
	 * Constructs a new board with rows, cols, and places the pieces on the
	 * board.
	 * 
	 * @param rows
	 * @param cols
	 * @param pieces
	 */
	public Board(int rows, int cols, ArrayList<Piece> pieces) {
		this.rows = rows;
		this.cols = cols;
		this.pieces = new ArrayList<Piece>();
		this.boardMatrix = new String[rows][cols];

		for (Piece p : pieces)
			this.addPiece(p);
	}

	/**
	 * Constructs a default board with rows and cols.
	 * 
	 * @param rows
	 * @param cols
	 */
	public Board(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.pieces = new ArrayList<Piece>();
		this.boardMatrix = new String[rows][cols];

	}

	/**
	 * Gets a piece at the given position
	 * 
	 * @param row
	 * @param col
	 * @return piece if the piece exists, else null
	 */
	public Piece getPieceAtPos(int row, int col) {
		for (Piece p : pieces) {
			if (p.getPosition().equals(new Point(row, col)))
				return p;
		}

		return null;
	}

	/**
	 * Given a position, checks to see if the space is empty
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public boolean isEmpty(Point pos) {
		return boardMatrix[pos.getX()][pos.getY()] == null;
	}

	/**
	 * Checks whether the board is filled.
	 * 
	 * @return true iff board is full
	 */
	public boolean isFilled() {
		return rows * cols == pieces.size();
	}

	/**
	 * Changes the state of this board by making the move.
	 * 
	 * @param move
	 * @return true if the move can be made
	 */
	public abstract boolean makeMove(Move move);

	/**
	 * Removes all pieces from the board
	 */
	public void clear() {
		pieces = new ArrayList<Piece>();
	}

	/**
	 * Gets the matrix representation of this board.
	 * 
	 * @return boardMatrix
	 */
	public String[][] getMatrix() {
		return boardMatrix;
	}

	/**
	 * Updates the current board matrix.
	 * 
	 * @param newMatrix
	 */
	public void setMatrix(String[][] newMatrix) {
		this.boardMatrix = newMatrix;
	}

	/**
	 * Get the array of pieces on the board.
	 * 
	 * @return pieces
	 */
	public ArrayList<Piece> getPieces() {
		return this.pieces;
	}

	/**
	 * Adds a piece to the board's list of pieces. The piece must be unique.
	 * 
	 * @param p
	 * @return true if the piece was added
	 */
	public boolean addPiece(Piece p) {
		if (pieces.contains(p))
			return false;
		else {

			// Update the matrix
			getMatrix()[p.getPosition().getX()][p.getPosition().getY()] = p
					.getType();

			// add the piee
			return pieces.add(p);
		}
	}

	/**
	 * Removes a piece from the board.
	 * 
	 * @param p
	 * @return true if the piece was removed.
	 */
	public boolean removePiece(Piece p) {
		int posX = p.getPosition().getX();
		int posY = p.getPosition().getY();

		if (this.getMatrix()[posX][posY] != null)
			this.getMatrix()[posX][posY] = null;

		return pieces.remove(p);
	}

	/**
	 * Checks if the position is in the bounds of this board.
	 * 
	 * @param r
	 * @param c
	 * @return true iff the position is on the board
	 */
	public boolean isInBounds(int r, int c) {
		return r >= 0 && r < rows && c >= 0 && c < cols;
	}

	/**
	 * Generates a string representation of the board.
	 * 
	 * @return string
	 */
	public String toString() {
		String thisBoard = "";
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (this.boardMatrix[r][c] == null) {
					thisBoard += "_ ";
				} else
					thisBoard += this.boardMatrix[r][c] + " ";
			}

			thisBoard += "\n";
		}

		return thisBoard;
	}
}
