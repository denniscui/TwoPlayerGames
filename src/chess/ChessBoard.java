package chess;

import java.util.ArrayList;

import utils.Point;

import models.Board;
import models.Move;
import models.Piece;

public class ChessBoard extends Board {

	public ChessBoard(int rows, int cols, ArrayList<Piece> pieces) {
		super(rows, cols, pieces);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean makeMove(Move move) {
		// Make sure the piece contains the end position
		Piece p = move.getPiece();

		int deltaY = move.getStart().getY() - p.getPosition().getY();

		if ((p.getType().equals("k") || p.getType().equals("K"))
				&& Math.abs(deltaY) == 2)
			return handleCastle(move);

		// Remove all affected pieces
		// TODO: Update for the castle case and enpassant
		for (Piece piece : move.getAffected())
			this.removePiece(piece);

		this.addPiece(p);

		// Removes the old piece from the baord
		this.removePiece(new Piece(p.getType(), move.getStart(), false, p
				.getPlayer()));

		return true;
	}

	/**
	 * Handles the special case of castling
	 * 
	 * @param m
	 * @return true
	 */
	private boolean handleCastle(Move move) {
		Piece p = move.getPiece();
		int deltaY = move.getStart().getY() - p.getPosition().getY();

		// This means we did a queen side castle
		if (deltaY > 0) {
			// Remove the rook
			for (Piece piece : move.getAffected()) {
				this.removePiece(piece);

				// And add it back in the new position
				// Rook moves 3 spaces to the right
				Point newPos = new Point(piece.getPosition().getX(), piece
						.getPosition().getY() + 3);
				this.addPiece(new Piece(piece.getType(), newPos, piece
						.getPlayer()));
			}
			// Remove the king
			this.removePiece(new Piece(p.getType(), move.getStart(), false, p
					.getPlayer()));

			// Add the new king
			this.addPiece(p);
		}
		// King side castle
		else {
			// Remove the rook
			for (Piece piece : move.getAffected()) {
				this.removePiece(piece);

				// And add it back in the new position
				// Rook moves 2 spaces to the left
				Point newPos = new Point(piece.getPosition().getX(), piece
						.getPosition().getY() - 2);
				this.addPiece(new Piece(piece.getType(), newPos, piece
						.getPlayer()));
			}
			// Remove the king
			this.removePiece(new Piece(p.getType(), move.getStart(), false, p
					.getPlayer()));

			// Add the new king
			this.addPiece(p);
		}
		return true;
	}

}
