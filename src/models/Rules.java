package models;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Vector;

/**
 * 
 * @author Derek
 * 
 *         Defines the rules for a game. Functions as a person who tells you the
 *         rules since we create instances of this.
 */
public abstract class Rules {
	// A map from each piece to possible moves
	private HashMap<Piece, ArrayList<Vector>> pieceToMoveMap;

	// A map from players to all of the possible pieces.
	private HashMap<Integer, ArrayList<Piece>> allPieces;

	// Dimensions of the board for this game
	private int rows;
	private int cols;
	
	public Rules()
	{
		rows = 0;
		cols = 0;
		
		pieceToMoveMap = new HashMap<Piece, ArrayList<Vector>>();
		allPieces = new HashMap<Integer, ArrayList<Piece>>();
	}
	/**
	 * Returns the number of rows in the board.
	 * 
	 * @return rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Returns the number of columns in the board.
	 * 
	 * @return cols
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * Sets the number of rows on the board. Should only be called by the
	 * constructor.
	 * 
	 * @param rows
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * Sets the number of columns in the board. Should only be called by the
	 * constructor.
	 * 
	 * @param cols
	 */
	public void setCols(int cols) {
		this.cols = cols;
	}

	/**
	 * Generates a default board given the game.
	 * 
	 * @return the default board
	 */
	public abstract Board defaultBoard();

	/**
	 * Checks if a move is valid.
	 * 
	 * @param m
	 * @param b
	 * @return true iff the move is valid
	 */
	public abstract boolean isValidMove(Move m, Board b);
	
	/**
	 * Defines the rules for each defined piece
	 * 
	 * @param h
	 */
	public void setPieceRules(HashMap<Piece, ArrayList<Vector>> h) {
		this.pieceToMoveMap = h;
	}

	/**
	 * Returns the map from pieces to rules.
	 * 
	 * @return pieceToMoveMap
	 */
	public HashMap<Piece, ArrayList<Vector>> getMoveMap() {
		return this.pieceToMoveMap;
	}

	/**
	 * Set the list of all the possible pieces.
	 * 
	 * @param pieces
	 */
	public void setPieces(HashMap<Integer, ArrayList<Piece>> pieces) {
		this.allPieces = pieces;
	}

	/**
	 * Get the list of all possible pieces
	 * 
	 * @return allPieces
	 */
	public HashMap<Integer, ArrayList<Piece>> getPieces() {
		return this.allPieces;
	}
}
