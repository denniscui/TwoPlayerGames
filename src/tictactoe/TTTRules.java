package tictactoe;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Point;

import tictactoe.TTTBoard;

import models.Board;
import models.Game;
import models.Move;
import models.Piece;

public class TTTRules extends models.Rules {

	public TTTRules() {
		super();
		setupBoardDimensions();
		setupPieceMap();
		setupPieces();
	}

	/**
	 * Helper function called by the constructor to setup the board dimensions.
	 */
	private void setupBoardDimensions() {
		this.setRows(3);
		this.setCols(3);
	}

	/**
	 * Helper function called by the constructor to setup the piece map.
	 */
	private void setupPieceMap() {
		// Tic-tac-toe doesn't need this.
	}

	private void setupPieces() {
		// X player's piece
		Piece xPiece = new Piece("X", null, 0);

		// O player's piece
		Piece oPiece = new Piece("O", null, 1);

		ArrayList<Piece> xPieces = new ArrayList<Piece>();
		xPieces.add(xPiece);

		ArrayList<Piece> oPieces = new ArrayList<Piece>();
		oPieces.add(oPiece);

		HashMap<Integer, ArrayList<Piece>> playerToPieces = new HashMap<Integer, ArrayList<Piece>>();
		playerToPieces.put(0, xPieces);
		playerToPieces.put(1, oPieces);
		this.setPieces(playerToPieces);
	}

	@Override
	public Board defaultBoard() {
		// Generates an empty board
		return new TTTBoard(this.getRows(), this.getCols(),
				new ArrayList<Piece>());
	}

	@Override
	public boolean isValidMove(Move m, Board b) {
		int endX = m.getEnd().getX();
		int endY = m.getEnd().getY();

		return b.getMatrix()[endX][endY] == null;
	}
}
