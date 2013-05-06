package models;

import java.util.ArrayList;

import utils.Point;

/**
 * Model that encodes an abstract move.
 * 
 * @author denniscui
 * 
 */
public class Move {

	// The piece to move
	private Piece piece;

	// The start and end points of the move
	private Point start;
	private Point end;

	// Other pieces this move affects
	private ArrayList<Piece> affected;

	// Flag for whether this move marks the end of the turn
	private boolean endTurn;

	// Flag for whether the piece has been promoted
	private boolean promotion;

	/**
	 * Creates a new move object.
	 * 
	 * @param piece
	 * @param start
	 *            null if the piece was just placed on the board
	 * @param end
	 */
	public Move(Piece piece, Point start, Point end) {
		this.piece = piece;
		this.start = start;
		this.end = end;

		this.endTurn = true;

		affected = new ArrayList<Piece>();
	}

	/**
	 * Creates a new move object.
	 * 
	 * @param piece
	 * @param start
	 *            null if the piece was just placed on the board
	 * @param end
	 */
	public Move(Piece piece, Point start, Point end, ArrayList<Piece> affected,
			boolean isEnd, boolean promotion) {
		this.piece = piece;
		this.start = start;
		this.end = end;

		this.endTurn = isEnd;
		this.affected = affected;
		this.promotion = promotion;
	}

	/**
	 * Returns the piece executing the move.
	 * 
	 * @return piece
	 */
	public Piece getPiece() {
		return piece;
	}

	/**
	 * Returns the starting position of the main piece. start will be null if
	 * the piece was just placed on the board.
	 * 
	 * @return start
	 */
	public Point getStart() {
		return start;
	}

	/**
	 * Returns the end position of the main piece. end should never be null.
	 * 
	 * @return end
	 */
	public Point getEnd() {
		return end;
	}

	/**
	 * Returns a list of other pieces that this move interacts with.
	 * 
	 * @return affected
	 */
	public ArrayList<Piece> getAffected() {
		return affected;
	}

	/**
	 * Adds a piece to the list of affected pieces.
	 * 
	 * @param piece
	 * @return false if the piece was already contained in the list. true if the
	 *         piece was successfully added to the list.
	 */
	public boolean addAffectedPiece(Piece piece) {
		return affected.contains(piece) ? false : affected.add(piece);
	}

	/**
	 * Checks whether this move ends the turn.
	 * 
	 * @return true iff this is the last move in the turn
	 */
	public boolean isEnd() {
		return endTurn;
	}

	/**
	 * Checks whether the piece has been promoted.
	 * 
	 * @return true iff the piece has been promoted
	 */
	public boolean isPromoted() {
		return promotion;
	}

	@Override
	public String toString() {
		if (start != null) {
			if (affected.size() == 0)
				return piece.getType() + " Start: (" + start.getX() + ", "
						+ start.getY() + ") End: (" + end.getX() + ", "
						+ end.getY() + ") ";
			else
				return piece.getType() + " Start: (" + start.getX() + ", "
						+ start.getY() + ") End: (" + end.getX() + ", "
						+ end.getY() + ") " + affected.get(0).getType();
		} else {
			return "";
		}
	}
}
