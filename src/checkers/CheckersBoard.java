package checkers;

import java.util.ArrayList;

import models.Board;
import models.Game;
import models.Move;
import models.Piece;

public class CheckersBoard extends Board {

	public CheckersBoard(int rows, int cols, ArrayList<Piece> pieces) {
		super(rows, cols, pieces);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean makeMove(Move move) {
		// Make sure the piece contains the end position
		Piece p = move.getPiece();

		this.addPiece(p);

		// Removes the old piece from the baord
		if (move.isPromoted())
			this.removePiece(new Piece(demote(p), move.getStart(), false, p
					.getPlayer()));
		else
			this.removePiece(new Piece(p.getType(), move.getStart(), false, p
					.getPlayer()));

		// Remove all affected pieces
		for (Piece piece : move.getAffected())
			this.removePiece(piece);

		return true;
	}

	/**
	 * Helper function to demote a King in order to remove the right piece.
	 * 
	 * @param piece
	 * @return new type
	 */
	private String demote(Piece piece) {
		if (piece.getPlayer() == Game.MAXIMIZING_PLAYER) {
			return "b";
		} else {
			return "r";
		}

	}
}
