package chess;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Point;
import utils.Vector;
import models.Board;
import models.Game;
import models.Move;
import models.Piece;
import models.Rules;

public class ChessRules extends Rules {

	public ChessRules() {
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
		// ----- White starts at the "bottom" of the board -----

		// Pawn
		Piece wPawn = new Piece("p", null, 0);
		Piece bPawn = new Piece("P", null, 1);

		ArrayList<Vector> wPawnMoves = new ArrayList<Vector>();
		ArrayList<Vector> bPawnMoves = new ArrayList<Vector>();

		Vector wForwardOne = new Vector(-1, 0);
		Vector wForwardTwo = new Vector(-2, 0);
		Vector wCapture1 = new Vector(-1, -1);
		Vector wCapture2 = new Vector(-1, 1);

		wPawnMoves.add(wForwardOne);
		wPawnMoves.add(wForwardTwo);
		wPawnMoves.add(wCapture1);
		wPawnMoves.add(wCapture2);

		Vector bForwardOne = new Vector(1, 0);
		Vector bForwardTwo = new Vector(2, 0);
		Vector bCapture1 = new Vector(1, -1);
		Vector bCapture2 = new Vector(1, 1);

		bPawnMoves.add(bForwardOne);
		bPawnMoves.add(bForwardTwo);
		bPawnMoves.add(bCapture1);
		bPawnMoves.add(bCapture2);

		// Rook
		Piece wRook = new Piece("r", null, 0);
		Piece bRook = new Piece("R", null, 1);

		ArrayList<Vector> rookMoves = new ArrayList<Vector>();

		Vector left = new Vector(0, -1);
		Vector right = new Vector(0, 1);
		Vector up = new Vector(-1, 0);
		Vector down = new Vector(1, 0);

		rookMoves.add(left);
		rookMoves.add(right);
		rookMoves.add(up);
		rookMoves.add(down);

		// Knight
		Piece wKnight = new Piece("n", null, 0);
		Piece bKnight = new Piece("N", null, 1);

		ArrayList<Vector> knightMoves = new ArrayList<Vector>();

		Vector m1 = new Vector(-2, 1);
		Vector m2 = new Vector(-1, 2);
		Vector m3 = new Vector(1, 2);
		Vector m4 = new Vector(2, 1);
		Vector m5 = new Vector(2, -1);
		Vector m6 = new Vector(1, -2);
		Vector m7 = new Vector(-1, -2);
		Vector m8 = new Vector(-2, -1);

		knightMoves.add(m1);
		knightMoves.add(m2);
		knightMoves.add(m3);
		knightMoves.add(m4);
		knightMoves.add(m5);
		knightMoves.add(m6);
		knightMoves.add(m7);
		knightMoves.add(m8);

		// Bishop
		Piece wBishop = new Piece("b", null, 0);
		Piece bBishop = new Piece("B", null, 1);

		ArrayList<Vector> bishopMoves = new ArrayList<Vector>();

		Vector diagLeftUp = new Vector(-1, -1);
		Vector diagRightUp = new Vector(-1, 1);
		Vector diagRightDown = new Vector(1, 1);
		Vector diagLeftDown = new Vector(1, -1);

		bishopMoves.add(diagLeftUp);
		bishopMoves.add(diagLeftDown);
		bishopMoves.add(diagRightUp);
		bishopMoves.add(diagRightDown);

		// King and queen
		Piece wKing = new Piece("k", null, 0);
		Piece bKing = new Piece("K", null, 1);
		Piece wQueen = new Piece("q", null, 0);
		Piece bQueen = new Piece("Q", null, 1);

		ArrayList<Vector> allDirections = new ArrayList<Vector>();
		allDirections.addAll(bishopMoves);
		allDirections.addAll(rookMoves);

		// All of the move maps
		HashMap<Piece, ArrayList<Vector>> moveMap = new HashMap<Piece, ArrayList<Vector>>();
		moveMap.put(wPawn, wPawnMoves);
		moveMap.put(bPawn, bPawnMoves);
		moveMap.put(wRook, rookMoves);
		moveMap.put(bRook, rookMoves);
		moveMap.put(wKnight, knightMoves);
		moveMap.put(bKnight, knightMoves);
		moveMap.put(wBishop, bishopMoves);
		moveMap.put(bBishop, bishopMoves);
		moveMap.put(wKing, allDirections);
		moveMap.put(bKing, allDirections);
		moveMap.put(wQueen, allDirections);
		moveMap.put(bQueen, allDirections);
		this.setPieceRules(moveMap);
	}

	/**
	 * Helper function called by the constructor to setup the pieces.
	 */
	private void setupPieces() {
		// White player's pieces
		Piece wPawn = new Piece("p", null, 0);
		Piece wRook = new Piece("r", null, 0);
		Piece wKnight = new Piece("n", null, 0);
		Piece wBishop = new Piece("b", null, 0);
		Piece wKing = new Piece("k", null, 0);
		Piece wQueen = new Piece("q", null, 0);

		// Black player's pieces
		Piece bPawn = new Piece("P", null, 1);
		Piece bRook = new Piece("R", null, 1);
		Piece bKnight = new Piece("N", null, 1);
		Piece bBishop = new Piece("B", null, 1);
		Piece bKing = new Piece("K", null, 1);
		Piece bQueen = new Piece("Q", null, 1);

		ArrayList<Piece> wPieces = new ArrayList<Piece>();
		wPieces.add(wPawn);
		wPieces.add(wRook);
		wPieces.add(wKnight);
		wPieces.add(wBishop);
		wPieces.add(wKing);
		wPieces.add(wQueen);

		ArrayList<Piece> bPieces = new ArrayList<Piece>();
		bPieces.add(bPawn);
		bPieces.add(bRook);
		bPieces.add(bKnight);
		bPieces.add(bBishop);
		bPieces.add(bKing);
		bPieces.add(bQueen);

		// These are piece "blueprints"
		HashMap<Integer, ArrayList<Piece>> playerToPieces = new HashMap<Integer, ArrayList<Piece>>();
		playerToPieces.put(Game.MINIMIZING_PLAYER, wPieces);
		playerToPieces.put(Game.MAXIMIZING_PLAYER, bPieces);
		this.setPieces(playerToPieces);
	}

	@Override
	public boolean isValidMove(Move m, Board b) {
		Piece p = m.getPiece();
		Piece bp = new Piece(p.getType(), null, p.getPlayer());
		Point start = m.getStart();
		Point end = m.getEnd();
		ArrayList<Piece> affected = m.getAffected();

		// If the piece is not in bounds...
		if (!b.isInBounds(end.getX(), end.getY()))
			return false;

		// If the affected piece is of the same type...
		if (affected.size() != 0
				&& affected.get(0).getPlayer() == p.getPlayer())
			return false;

		int deltaX = end.getX() - start.getX();
		int deltaY = end.getY() - start.getY();
		int gcd = gcd(deltaX, deltaY);

		// If the move map doesn't contain the move...
		if (!getMoveMap().get(bp).contains(new Vector(deltaX, deltaY))
				&& !getMoveMap().get(bp).contains(
						new Vector(deltaX / gcd, deltaY / gcd)))
			return false;

		return true;
	}

	/**
	 * Finds the GCD of two integers.
	 * 
	 * @param x
	 * @param y
	 * @return gcd of the two integers
	 */
	private int gcd(int x, int y) {
		x = Math.abs(x);
		y = Math.abs(y);

		if (x == 0)
			return y;

		if (y == 0)
			return x;

		return x > y ? gcd(x - y, y) : gcd(y - x, x);
	}

	@Override
	public Board defaultBoard() {
		ArrayList<Piece> starters = new ArrayList<Piece>();

		// Generate the pawns
		for (int col = 0; col < getCols(); col++) {
			Piece wPawn = new Piece("p", new Point(6, col), 0);
			Piece bPawn = new Piece("P", new Point(1, col), 1);

			starters.add(wPawn);
			starters.add(bPawn);
		}

		// Generate rest of pieces
		// White first
		Piece wRook1 = new Piece("r", new Point(7, 0), 0);
		Piece wRook2 = new Piece("r", new Point(7, 7), 0);
		Piece wKnight1 = new Piece("n", new Point(7, 1), 0);
		Piece wKnight2 = new Piece("n", new Point(7, 6), 0);
		Piece wBishop1 = new Piece("b", new Point(7, 2), 0);
		Piece wBishop2 = new Piece("b", new Point(7, 5), 0);
		Piece wKing = new Piece("k", new Point(7, 4), 0);
		Piece wQueen = new Piece("q", new Point(7, 3), 0);

		starters.add(wRook1);
		starters.add(wRook2);
		starters.add(wKnight1);
		starters.add(wKnight2);
		starters.add(wBishop1);
		starters.add(wBishop2);
		starters.add(wKing);
		starters.add(wQueen);

		// Then black
		Piece bRook1 = new Piece("R", new Point(0, 0), 1);
		Piece bRook2 = new Piece("R", new Point(0, 7), 1);
		Piece bKnight1 = new Piece("N", new Point(0, 1), 1);
		Piece bKnight2 = new Piece("N", new Point(0, 6), 1);
		Piece bBishop1 = new Piece("B", new Point(0, 2), 1);
		Piece bBishop2 = new Piece("B", new Point(0, 5), 1);
		Piece bKing = new Piece("K", new Point(0, 4), 1);
		Piece bQueen = new Piece("Q", new Point(0, 3), 1);

		starters.add(bRook1);
		starters.add(bRook2);
		starters.add(bKnight1);
		starters.add(bKnight2);
		starters.add(bBishop1);
		starters.add(bBishop2);
		starters.add(bKing);
		starters.add(bQueen);

		return new ChessBoard(this.getRows(), this.getCols(), starters);
	}

}
