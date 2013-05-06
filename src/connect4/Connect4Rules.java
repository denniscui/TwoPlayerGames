package connect4;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Point;
import utils.Vector;

import models.Board;
import models.Game;
import models.Move;
import models.Piece;
import models.Rules;

public class Connect4Rules extends Rules {

	public Connect4Rules() {
		super();
		setupBoardDimensions();
		setupPieceMap();
		setupPieces();
	}

	/**
	 * Helper function called by the constructor to setup the board dimensions.
	 */
	private void setupBoardDimensions() {
		this.setRows(6);
		this.setCols(7);
	}

	/**
	 * Helper function called by the constructor to setup the piece map.
	 */
	private void setupPieceMap() {
		// Connect 4 doesn't need this.
	}

	/**
	 * Helper function called by the constructor to setup the pieces.
	 */
	private void setupPieces() {
		// Red player's piece
		Piece rPiece = new Piece("R", null, 0);

		// Yellow player's piece
		Piece yPiece = new Piece("Y", null, 1);

		ArrayList<Piece> rPieces = new ArrayList<Piece>();
		rPieces.add(rPiece);

		ArrayList<Piece> yPieces = new ArrayList<Piece>();
		yPieces.add(yPiece);

		HashMap<Integer, ArrayList<Piece>> playerToPieces = new HashMap<Integer, ArrayList<Piece>>();
		playerToPieces.put(0, rPieces);
		playerToPieces.put(1, yPieces);
		this.setPieces(playerToPieces);
	}

	@Override
	public Board defaultBoard() {
		// Generates an empty board
		return new Connect4Board(this.getRows(), this.getCols(),
				new ArrayList<Piece>());
	}

	@Override
	public boolean isValidMove(Move m, Board b) {
		int endX = m.getEnd().getX();
		int endY = m.getEnd().getY();

		// If any of the lower pieces are null, return false
		for (int i = this.getRows() - 1; i > endX; i--) {
			if (b.getMatrix()[i][endY] == null)
				return false;
		}

		return true;
	}
}
