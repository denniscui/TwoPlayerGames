package checkers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import utils.Point;
import utils.Vector;

import models.Board;
import models.Game;
import models.Move;
import models.Piece;
import models.Rules;

public class CheckersRules extends Rules {

	private ArrayList<Piece> starters;

	public CheckersRules() {
		super();
		setupBoardDimensions();
		setupPieces();
		setupPieceMap();
	}

	/**
	 * Helper function called by the constructor to setup the board dimensions.
	 */
	private void setupBoardDimensions() {
		this.setRows(8);
		this.setCols(8);
	}

	/**
	 * Helper function called by the constructor to setup the piece map. Capture
	 * moves are added to the arraylist first to speed up processing.
	 */
	private void setupPieceMap() {
		// Red player's pieces, red will start on top
		Piece rPiece = new Piece("r", null, 0);

		ArrayList<Vector> rPieceMoves = new ArrayList<Vector>();

		// Capture moves
		rPieceMoves.add(new Vector(2, -2));
		rPieceMoves.add(new Vector(2, 2));

		// Normal Moves
		rPieceMoves.add(new Vector(1, -1));
		rPieceMoves.add(new Vector(1, 1));

		Piece rPieceK = new Piece("R", null, 0);

		ArrayList<Vector> rPieceKMoves = new ArrayList<Vector>();
		// Capture Moves
		rPieceKMoves.add(new Vector(2, -2));
		rPieceKMoves.add(new Vector(2, 2));
		rPieceKMoves.add(new Vector(-2, 2));
		rPieceKMoves.add(new Vector(-2, -2));

		// Normal Moves
		rPieceKMoves.add(new Vector(1, -1));
		rPieceKMoves.add(new Vector(1, 1));
		rPieceKMoves.add(new Vector(-1, 1));
		rPieceKMoves.add(new Vector(-1, -1));

		// Black player's pieces, black will start on bottom
		Piece bPiece = new Piece("b", null, 1);

		ArrayList<Vector> bPieceMoves = new ArrayList<Vector>();

		// Capture moves
		bPieceMoves.add(new Vector(-2, -2));
		bPieceMoves.add(new Vector(-2, 2));

		// Normal Moves
		bPieceMoves.add(new Vector(-1, -1));
		bPieceMoves.add(new Vector(-1, 1));

		Piece bPieceK = new Piece("B", null, 1);

		// Kings move the same way!
		HashMap<Piece, ArrayList<Vector>> moveMap = new HashMap<Piece, ArrayList<Vector>>();
		moveMap.put(rPiece, rPieceMoves);
		moveMap.put(rPieceK, rPieceKMoves);
		moveMap.put(bPiece, bPieceMoves);
		moveMap.put(bPieceK, rPieceKMoves);

		this.setPieceRules(moveMap);
	}

	/**
	 * Helper function called by the constructor to setup the pieces.
	 */
	private void setupPieces() {
		// Red player's pieces
		Piece rPiece = new Piece("r", null, 0);
		Piece rPieceK = new Piece("R", null, 0);

		// Black player's pieces
		Piece bPiece = new Piece("b", null, 1);
		Piece bPieceK = new Piece("B", null, 1);

		ArrayList<Piece> rPieces = new ArrayList<Piece>();
		rPieces.add(rPiece);
		rPieces.add(rPieceK);

		ArrayList<Piece> bPieces = new ArrayList<Piece>();
		bPieces.add(bPiece);
		bPieces.add(bPieceK);

		// These are piece "blueprints"
		HashMap<Integer, ArrayList<Piece>> playerToPieces = new HashMap<Integer, ArrayList<Piece>>();
		playerToPieces.put(Game.MINIMIZING_PLAYER, rPieces);
		playerToPieces.put(Game.MAXIMIZING_PLAYER, bPieces);
		this.setPieces(playerToPieces);
	}

	@Override
	public Board defaultBoard() {
		starters = new ArrayList<Piece>();

		// For the red player
		starters.add(new Piece("r", new Point(0, 0), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(0, 2), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(0, 4), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(0, 6), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(1, 1), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(1, 3), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(1, 5), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(1, 7), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(2, 0), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(2, 2), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(2, 4), Game.MINIMIZING_PLAYER));
		starters.add(new Piece("r", new Point(2, 6), Game.MINIMIZING_PLAYER));

		// For the black player
		starters.add(new Piece("b", new Point(5, 1), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(5, 3), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(5, 5), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(5, 7), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(6, 0), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(6, 2), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(6, 4), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(6, 6), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(7, 1), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(7, 3), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(7, 5), Game.MAXIMIZING_PLAYER));
		starters.add(new Piece("b", new Point(7, 7), Game.MAXIMIZING_PLAYER));
		return new CheckersBoard(this.getRows(), this.getCols(),
				(ArrayList<Piece>) starters.clone());
	}

	@Override
	public boolean isValidMove(Move m, Board b) {
		Piece moving = m.getPiece();

		int newX = m.getEnd().getX();
		int newY = m.getEnd().getY();

		int oldX = m.getStart().getX();
		int oldY = m.getStart().getY();

		// If the new pos isn't in bounds, not valid
		if (!b.isInBounds(newX, newY))
			return false;

		// Must be a diagonal move
		if (Math.abs(oldX - newX) != Math.abs(oldY - newY))
			return false;

		switch (Math.abs(oldX - newX)) {
		case 1:
			// It's a single move, so new position must be empty.
			return b.getMatrix()[newX][newY] == null;
		case 2:
			// Get the affected piece
			String midType = m.getAffected().get(0).getType();

			// Midtype can't be empty or have the same type as the current
			// player's piece
			if (midType == null
					|| midType
							.equals(CheckersGame.TYPES[moving.getPlayer()][0])
					|| midType
							.equals(CheckersGame.TYPES[moving.getPlayer()][1]))
				return false;

			// NOTE: If this doesn't work then setting up affected failed
			// somewhere
			return true;
		}

		return false;
	}

}
