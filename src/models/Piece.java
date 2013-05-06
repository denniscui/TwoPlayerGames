package models;

import utils.Point;

public class Piece {
	// Identifier for a piece. We may change the type later.
	private String pieceType;

	// Player that this piece belongs to
	private int piecePlayer;

	// True if the piece is in play, false otherwise
	private boolean onBoard;

	// The position of the piece on the board
	private Point position;

	/**
	 * Constructs a new piece from an id and position. Default piece is not in
	 * play.
	 * 
	 * @param pieceId
	 * @param position
	 * @param piecePlayer
	 */
	public Piece(String pieceType, Point position, int piecePlayer) {
		this.pieceType = pieceType;
		this.position = position;
		this.onBoard = false;
		this.piecePlayer = piecePlayer;
	}

	/**
	 * Constructs a new piece from an id, position, and state.
	 * 
	 * @param pieceId
	 * @param position
	 * @param onBoard
	 * @param piecePlayer
	 */
	public Piece(String pieceType, Point position, boolean onBoard,
			int piecePlayer) {
		this.pieceType = pieceType;
		this.position = position;
		this.onBoard = onBoard;
		this.piecePlayer = piecePlayer;
	}

	/**
	 * Return the identifier for this piece.
	 * 
	 * @return pieceId
	 */
	public String getType() {
		return pieceType;
	}

	/**
	 * Returns the state of the piece.
	 * 
	 * @return true if the piece is on the board.
	 */
	public boolean inPlay() {
		return onBoard;
	}

	/**
	 * Update the state of the piece.
	 * 
	 * @param inPlay
	 */
	public void updateState(boolean inPlay) {
		this.onBoard = inPlay;
	}

	/**
	 * Returns the position of the piece.
	 * 
	 * @return position
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Returns the player this piece belongs to
	 * 
	 * @return piecePlayer
	 */
	public int getPlayer() {
		return piecePlayer;
	}

	/**
	 * Sets the position of the piece.
	 * 
	 * @param p
	 */
	public void setPosition(Point p) {
		this.position = p;
	}

	/**
	 * Returns true iff the ids are the same and the positions are the same.
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals(Object other) {
		Piece p = (Piece) other;

		if (p.getPosition() == null && position == null)
			return p.getType().equals(getType());

		return getType().equals(p.getType())
				&& getPosition().equals(p.getPosition());
	}

	/**
	 * Returns true iff the ids are the same.
	 * 
	 * @param other
	 * @return
	 */
	public boolean equalsIgnorePosition(Object other) {
		Piece p = (Piece) other;
		return getType().equals(p.getType());
	}

	@Override
	public int hashCode() {
		return getType().toCharArray()[0];
	}

	/**
	 * Sets the type of the piece. Mainly used for promotions.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.pieceType = type;
	}

}
