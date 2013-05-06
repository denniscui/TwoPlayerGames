package chess;

import java.util.ArrayList;
import java.util.HashMap;

import checkers.CheckersBoard;
import checkers.CheckersGame;

import utils.Point;
import utils.Vector;

import models.Board;
import models.Game;
import models.Move;
import models.Piece;
import models.Rules;

/**
 * In this variation of chess, you're allowed to put yourself in check. The game
 * ends when the king is taken.
 * 
 * The scoring tables and board evaluation function were obtained from
 * www.chessbincom/post/Chess-Board-Evaluation.aspx
 * 
 * @author denniscui
 * 
 */
public class ChessGame extends Game {

	// Checks if it can castle
	private boolean wCanCastleQueenSide = true;
	private boolean wCanCastleKingSide = true;
	private boolean bCanCastleQueenSide = true;
	private boolean bCanCastleKingSide = true;

	private boolean wCastled = false;
	private boolean bCastled = false;

	// Necessary constants for board evaluation
	private static final int PAWN_VALUE = 100;
	private static final int KNIGHT_VALUE = 320;
	private static final int BISHOP_VALUE = 325;
	private static final int ROOK_VALUE = 500;
	private static final int QUEEN_VALUE = 975;
	private static final int KING_VALUE = 32767;

	// Scoring tables for each piece based on position
	private static final int[][] WHITE_PAWN_TABLE = new int[][] {
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 50, 50, 50, 50, 50, 50, 50, 50 },
			{ 10, 10, 20, 30, 30, 20, 10, 10 }, { 5, 5, 10, 25, 25, 10, 5, 5 },
			{ 0, 0, 0, 20, 20, 0, 0, 0 }, { 5, -5, -10, 0, 0, -10, -5, 5 },
			{ 5, 10, 10, -20, -20, 10, 10, 5 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };

	private static final int[][] BLACK_PAWN_TABLE = new int[][] {
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 5, 10, 10, -20, -20, 10, 10, 5 },
			{ 5, -5, -10, 0, 0, -10, -5, 5 }, { 0, 0, 0, 20, 20, 0, 0, 0 },
			{ 5, 5, 10, 25, 25, 10, 5, 5 }, { 10, 10, 20, 30, 30, 20, 10, 10 },
			{ 50, 50, 50, 50, 50, 50, 50, 50 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };

	private static final int[][] WHITE_KNIGHT_TABLE = new int[][] {
			{ -50, -40, -30, -30, -30, -30, -40, -50 },
			{ -40, -20, 0, 0, 0, 0, -20, -40 },
			{ -30, 0, 10, 15, 15, 10, 0, -30 },
			{ -30, 5, 15, 20, 20, 15, 5, -30 },
			{ -30, 0, 15, 20, 20, 15, 0, -30 },
			{ -30, 5, 10, 15, 15, 10, 5, -30 },
			{ -40, -20, 0, 5, 5, 0, -20, -40 },
			{ -50, -40, -30, -30, -30, -30, -40, -50 } };

	private static final int[][] BLACK_KNIGHT_TABLE = new int[][] {
			{ -50, -40, -30, -30, -30, -30, -40, -50 },
			{ -40, -20, 0, 5, 5, 0, -20, -40 },
			{ -30, 5, 10, 15, 15, 10, 5, -30 },
			{ -30, 0, 15, 20, 20, 15, 0, -30 },
			{ -30, 5, 15, 20, 20, 15, 5, -30 },
			{ -30, 0, 10, 15, 15, 10, 0, -30 },
			{ -40, -20, 0, 0, 0, 0, -20, -40 },
			{ -50, -40, -30, -30, -30, -30, -40, -50 } };

	private static final int[][] WHITE_BISHOP_TABLE = new int[][] {
			{ -20, -10, -10, -10, -10, -10, -10, -20 },
			{ -10, 0, 0, 0, 0, 0, 0, -10 }, { -10, 0, 5, 10, 10, 5, 0, -10 },
			{ -10, 5, 5, 10, 10, 5, 5, -10 },
			{ -10, 0, 10, 10, 10, 10, 0, -10 },
			{ -10, 10, 10, 10, 10, 10, 10, -10 },
			{ -10, 5, 0, 0, 0, 0, 5, -10 },
			{ -20, -10, -10, -10, -10, -10, -10, -20 } };

	private static final int[][] BLACK_BISHOP_TABLE = new int[][] {
			{ -20, -10, -10, -10, -10, -10, -10, -20 },
			{ -10, 5, 0, 0, 0, 0, 5, -10 },
			{ -10, 10, 10, 10, 10, 10, 10, -10 },
			{ -10, 0, 10, 10, 10, 10, 0, -10 },
			{ -10, 5, 5, 10, 10, 5, 5, -10 }, { -10, 0, 5, 10, 10, 5, 0, -10 },
			{ -10, 0, 0, 0, 0, 0, 0, -10 },
			{ -20, -10, -10, -10, -10, -10, -10, -20 } };

	private static final int[][] WHITE_ROOK_TABLE = new int[][] {
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 5, 10, 10, 10, 10, 10, 10, 5 },
			{ -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 },
			{ -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 },
			{ -5, 0, 0, 0, 0, 0, 0, -5 }, { 0, 0, 0, 5, 5, 0, 0, 0 } };

	private static final int[][] BLACK_ROOK_TABLE = new int[][] {
			{ 0, 0, 0, 5, 5, 0, 0, 0 }, { -5, 0, 0, 0, 0, 0, 0, -5 },
			{ -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 },
			{ -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 },
			{ 5, 10, 10, 10, 10, 10, 10, 5 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };

	private static final int[][] WHITE_QUEEN_TABLE = new int[][] {
			{ -20, -10, -10, -5, -5, -10, -10, -20 },
			{ -10, 0, 0, 0, 0, 0, 0, -10 }, { -10, 0, 5, 5, 5, 5, 0, -10 },
			{ -5, 0, 5, 5, 5, 5, 0, -5 }, { 0, 0, 5, 5, 5, 5, 0, -5 },
			{ -10, 5, 5, 5, 5, 5, 0, -10 }, { -10, 0, 5, 0, 0, 0, 0, -10 },
			{ -20, -10, -10, -5, -5, -10, -10, -20 } };

	private static final int[][] BLACK_QUEEN_TABLE = new int[][] {
			{ -20, -10, -10, -5, -5, -10, -10, -20 },
			{ -10, 0, 5, 0, 0, 0, 0, -10 }, { -10, 5, 5, 5, 5, 5, 0, -10 },
			{ 0, 0, 5, 5, 5, 5, 0, -5 }, { -5, 0, 5, 5, 5, 5, 0, -5 },
			{ -10, 0, 5, 5, 5, 5, 0, -10 }, { -10, 0, 0, 0, 0, 0, 0, -10 },
			{ -20, -10, -10, -5, -5, -10, -10, -20 } };

	// In the early/mid game it's good to keep the king near the back in a
	// protected position
	private static final int[][] WHITE_KING_TABLE = new int[][] {
			{ -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -20, -30, -30, -40, -40, -30, -30, -20 },
			{ -10, -20, -20, -20, -20, -20, -20, -10 },
			{ 20, 20, 0, 0, 0, 0, 20, 20 }, { 20, 30, 10, 0, 0, 10, 30, 20 } };

	private static final int[][] BLACK_KING_TABLE = new int[][] {
			{ 20, 30, 10, 0, 0, 10, 30, 20 }, { 20, 20, 0, 0, 0, 0, 20, 20 },
			{ -10, -20, -20, -20, -20, -20, -20, -10 },
			{ -20, -30, -30, -40, -40, -30, -30, -20 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 },
			{ -30, -40, -40, -50, -50, -40, -40, -30 } };

	// Near the end game, the king should move more freely
	private static final int[][] WHITE_KING_TABLE_END = new int[][] {
			{ -50, -40, -30, -20, -20, -30, -40, -50 },
			{ -30, -20, -10, 0, 0, -10, -20, -30 },
			{ -30, -10, 20, 30, 30, 20, -10, -30 },
			{ -30, -10, 30, 40, 40, 30, -10, -30 },
			{ -30, -10, 30, 40, 40, 30, -10, -30 },
			{ -30, -10, 20, 30, 30, 20, -10, -30 },
			{ -30, -30, 0, 0, 0, 0, -30, -30 },
			{ -50, -30, -30, -30, -30, -30, -30, -50 } };

	private static final int[][] BLACK_KING_TABLE_END = new int[][] {
			{ -50, -30, -30, -30, -30, -30, -30, -50 },
			{ -30, -30, 0, 0, 0, 0, -30, -30 },
			{ -30, -10, 20, 30, 30, 20, -10, -30 },
			{ -30, -10, 30, 40, 40, 30, -10, -30 },
			{ -30, -10, 30, 40, 40, 30, -10, -30 },
			{ -30, -10, 20, 30, 30, 20, -10, -30 },
			{ -30, -20, -10, 0, 0, -10, -20, -30 },
			{ -50, -40, -30, -20, -20, -30, -40, -50 } };

	public ChessGame(int gameId, Board board, Rules rules, int turn,
			ArrayList<Move> moves) {
		super(gameId, board, rules, turn, moves);
		// TODO Auto-generated constructor stub
	}

	public ChessGame(int gameId, Rules rules) {
		super(gameId, rules);
	}

	@Override
	public int[] hasEnded() {
		// First check for a win
		boolean wKing = false;
		boolean bKing = false;

		for (Piece p : getBoard().getPieces()) {
			if (p.getType().equals("k"))
				wKing = true;

			if (p.getType().equals("K"))
				bKing = true;
		}

		if (bKing == false) {
			int[] state = new int[] { Game.WIN, Game.MINIMIZING_PLAYER };
			onGameEnded(state);
			return state;
		} else if (wKing == false) {
			int[] state = new int[] { Game.WIN, Game.MAXIMIZING_PLAYER };
			onGameEnded(state);
			return state;
		}

		// Now check for tie
		if (getAvailableMoves().size() == 0) {
			int[] state = new int[] { Game.DRAW };
			onGameEnded(state);
			return state;
		}

		// TODO: Later do other checks like same 3 moves

		// Otherwise keep playing
		return new int[] { Game.NONE };
	}

	@Override
	public boolean makeMove(Move m) {

		// Check for castling ability
		if (m.getPiece().getType().equals("k")) {
			int deltaY = m.getStart().getY() - m.getEnd().getY();

			// If this is a castle
			if (Math.abs(deltaY) == 2)
				wCastled = true;

			wCanCastleQueenSide = false;
			wCanCastleKingSide = false;
		} else if (m.getPiece().getType().equals("K")) {
			int deltaY = m.getStart().getY() - m.getEnd().getY();

			// If this is a castle
			if (Math.abs(deltaY) == 2)
				bCastled = true;

			bCanCastleQueenSide = false;
			bCanCastleKingSide = false;
		} else if (m.getPiece().getType().equals("r")) {
			if (m.getPiece().getPosition().equals(new Point(7, 0)))
				wCanCastleQueenSide = false;
			else if (m.getPiece().getPosition().equals(new Point(7, 7)))
				wCanCastleKingSide = false;
		} else if (m.getPiece().getType().equals("R")) {
			if (m.getPiece().getPosition().equals(new Point(0, 0)))
				bCanCastleQueenSide = false;
			else if (m.getPiece().getPosition().equals(new Point(0, 7)))
				bCanCastleKingSide = false;
		}

		return super.makeMove(m);
	}

	@Override
	public int evaluateState() {
		// First 20 moves are opening
		if (getMoves().size() < 20)
			return Game.IN_OPENING;

		// Less than 6 pieces left is ending
		if (getBoard().getPieces().size() < 6)
			return Game.IN_ENDING;

		return Game.IN_MIDDLE;
	}

	@Override
	public Game clone() {
		Board clonedBoard = new CheckersBoard(getRules().getRows(), getRules()
				.getCols(), (ArrayList<Piece>) getBoard().getPieces().clone());

		String[][] newMatrix = new String[getRules().getRows()][getRules()
				.getCols()];
		for (int r = 0; r < getRules().getRows(); r++) {
			for (int c = 0; c < getRules().getCols(); c++) {
				newMatrix[r][c] = getBoard().getMatrix()[r][c];
			}
		}

		clonedBoard.setMatrix(newMatrix);

		return new ChessGame(this.getId(), clonedBoard, this.getRules(),
				this.getTurn(), (ArrayList<Move>) this.getMoves().clone());
	}

	@Override
	public int scoreGame() {
		int baseScore = 0;

		int wBishopCount = 0;
		int bBishopCount = 0;
		int wKnightCount = 0;
		int bKnightCount = 0;

		for (Piece p : getBoard().getPieces()) {
			Point pos = p.getPosition();
			int tempScore = 0;
			// If it's a pawn
			if (p.getType().equals("p") || p.getType().equals("P")) {
				tempScore += PAWN_VALUE;

				// Pawns on the sides are worth less because they can only
				// attack in 1 direction
				if (p.getPosition().getY() == 0 || p.getPosition().getY() == 7) {
					tempScore -= 15;
				}
				if (p.getType().equals("p"))
					tempScore += WHITE_PAWN_TABLE[pos.getX()][pos.getY()];
				else
					tempScore += BLACK_PAWN_TABLE[pos.getX()][pos.getY()];
			}
			// If it's a knight
			else if (p.getType().equals("n") || p.getType().equals("N")) {
				tempScore += KNIGHT_VALUE;

				if (p.getType().equals("n"))
					tempScore += WHITE_KNIGHT_TABLE[pos.getX()][pos.getY()];
				else
					tempScore += BLACK_KNIGHT_TABLE[pos.getX()][pos.getY()];

				// Count the number of knights
				if (p.getType().equals("n"))
					wKnightCount++;
				else
					bKnightCount++;
			}
			// If it's a bishop
			else if (p.getType().equals("b") || p.getType().equals("B")) {
				tempScore += BISHOP_VALUE;

				if (p.getType().equals("b"))
					tempScore += WHITE_BISHOP_TABLE[pos.getX()][pos.getY()];
				else
					tempScore += BLACK_BISHOP_TABLE[pos.getX()][pos.getY()];

				// Count the number of bishops
				if (p.getType().equals("b"))
					wBishopCount++;
				else
					bBishopCount++;

				// Bishops are better in end phase
				if (evaluateState() == Game.IN_ENDING)
					tempScore += 10;
			}
			// If it's a rook
			else if (p.getType().equals("r") || p.getType().equals("R")) {
				tempScore += ROOK_VALUE;

				if (p.getType().equals("r"))
					tempScore += WHITE_ROOK_TABLE[pos.getX()][pos.getY()];
				else
					tempScore += BLACK_ROOK_TABLE[pos.getX()][pos.getY()];
			}
			// If it's a queen
			else if (p.getType().equals("q") || p.getType().equals("Q")) {
				tempScore += QUEEN_VALUE;

				if (p.getType().equals("q"))
					tempScore += WHITE_QUEEN_TABLE[pos.getX()][pos.getY()];
				else
					tempScore += BLACK_QUEEN_TABLE[pos.getX()][pos.getY()];
			} else {
				tempScore += KING_VALUE;

				// Two different tables depending on how far the game has
				// progressed
				if (evaluateState() == Game.IN_ENDING) {
					if (p.getType().equals("k"))
						tempScore += WHITE_KING_TABLE_END[pos.getX()][pos
								.getY()];
					else
						tempScore += BLACK_KING_TABLE_END[pos.getX()][pos
								.getY()];
				} else {
					if (p.getType().equals("k"))
						tempScore += WHITE_KING_TABLE[pos.getX()][pos.getY()];
					else
						tempScore += BLACK_KING_TABLE[pos.getX()][pos.getY()];
				}
			}

			// Update the basescore based on piece score
			baseScore += p.getPlayer() == Game.MAXIMIZING_PLAYER ? tempScore
					: -1 * tempScore;
		}

		// Penalize for not being able to castle
		if ((!wCanCastleQueenSide && !wCanCastleKingSide) && !wCastled) {
			baseScore += 50;
		} else if ((!wCanCastleQueenSide || !wCanCastleKingSide) && !wCastled) {
			baseScore += 20;
		}

		if ((!bCanCastleQueenSide && !bCanCastleKingSide) && !bCastled) {
			baseScore -= 50;
		} else if ((!bCanCastleQueenSide || !bCanCastleKingSide) && !bCastled) {
			baseScore -= 20;
		}

		// Reward for keeping 2 bishops and 2 knights
		if (wBishopCount >= 2)
			baseScore -= 10;

		if (bBishopCount >= 2)
			baseScore += 10;

		if (wKnightCount >= 2)
			baseScore -= 10;

		if (bKnightCount >= 2)
			baseScore += 10;

		return baseScore;
	}

	@Override
	public ArrayList<Move> getAvailableMoves() {
		ArrayList<Move> availableMoves = new ArrayList<Move>();

		// Iterate through the pieces
		for (Piece p : getBoard().getPieces()) {

			// Make sure the piece corresponds to the current turn
			if (p.getPlayer() == getTurn()) {

				// Get the key version of this piece by setting position = null
				Piece key = new Piece(p.getType(), null, p.getPlayer());
				HashMap<Piece, ArrayList<Vector>> moveMap = this.getRules()
						.getMoveMap();
				ArrayList<Vector> moves = moveMap.get(key);

				// If it's a knight
				if (p.getType().equals("n") || p.getType().equals("N")) {
					// Iterate through all of the moves
					for (Vector v : moves) {
						int startX = p.getPosition().getX();
						int startY = p.getPosition().getY();

						int endX = startX + v.getDeltaX();
						int endY = startY + v.getDeltaY();

						Piece piece = null;
						try {
							piece = getPieceInDir(p.getPosition(), v);
						} catch (IndexOutOfBoundsException e) {
							continue;
						}

						// Can't jump on our own pieces
						if (piece == null
								|| (piece != null && piece.getPlayer() != getTurn())) {

							// Update the piece with its new location
							Piece newPiece = new Piece(p.getType(), new Point(
									endX, endY), p.getPlayer());

							// Get the affected pieces
							ArrayList<Piece> affected = new ArrayList<Piece>();
							if (piece != null)
								affected.add(piece);

							Move m = new Move(newPiece, p.getPosition(),
									newPiece.getPosition(), affected, true,
									false);
							availableMoves.add(m);
						}
					}
				}
				// If it's a pawn
				else if (p.getType().equals("p") || p.getType().equals("P")) {
					for (Vector v : moves) {
						int startX = p.getPosition().getX();
						int startY = p.getPosition().getY();

						int deltaX = v.getDeltaX();
						int deltaY = v.getDeltaY();

						int endX = startX + deltaX;
						int endY = startY + deltaY;

						switch (deltaX) {
						case 2:
							// Won't throw an exception because we stay on board
							if (p.getPosition().getX() == 1
									&& crawl(p.getPosition(), new Vector(1, 0),
											2) == null) {
								// Set the new position
								Piece newPiece = new Piece(p.getType(),
										new Point(endX, endY), p.getPlayer());
								Move m = new Move(newPiece, p.getPosition(),
										newPiece.getPosition());
								availableMoves.add(m);
							}
							break;
						case -2:
							// Won't throw an exception because we stay on board
							if (p.getPosition().getX() == 6
									&& crawl(p.getPosition(),
											new Vector(-1, 0), 2) == null) {
								// Update the piece with its new location
								Piece newPiece = new Piece(p.getType(),
										new Point(endX, endY), p.getPlayer());
								Move m = new Move(newPiece, p.getPosition(),
										newPiece.getPosition());
								availableMoves.add(m);
							}
							break;
						case 1:
						case -1:
							// Now check how we're moving in the col direction
							switch (deltaY) {
							case 0:
								// We're moving vertically
								if (getPieceInDir(p.getPosition(), v) == null) {
									// Update the piece with its new location
									Piece newPiece = new Piece(p.getType(),
											new Point(endX, endY),
											p.getPlayer());

									// Check if the pawn should be promoted
									boolean isPromotion = (endX == 0 || endX == 7) ? true
											: false;

									Move m = new Move(newPiece,
											p.getPosition(),
											newPiece.getPosition(),
											new ArrayList<Piece>(), true,
											isPromotion);
									availableMoves.add(m);
								}
								break;
							case 1:
							case -1:
								// Get the piece to capture
								Piece capture = null;

								try {

									capture = getPieceInDir(p.getPosition(), v);
								} catch (IndexOutOfBoundsException e) {
									continue;
								}

								// It must be of another type
								if (capture != null
										&& capture.getPlayer() != getTurn()) {
									// Update the piece with its new location
									Piece newPiece = new Piece(p.getType(),
											new Point(endX, endY),
											p.getPlayer());

									// Get the affected piece
									ArrayList<Piece> affected = new ArrayList<Piece>();
									affected.add(capture);

									// Check if the pawn should be promoted
									boolean isPromotion = (endY == 0 || endY == 7) ? true
											: false;

									Move m = new Move(newPiece,
											p.getPosition(),
											newPiece.getPosition(), affected,
											true, isPromotion);
									availableMoves.add(m);
								}
							}
						}
					}
				}
				// If it's a king
				else if (p.getType().equals("k") || p.getType().equals("K")) {
					for (Vector v : moves) {
						int startX = p.getPosition().getX();
						int startY = p.getPosition().getY();

						int endX = startX + v.getDeltaX();
						int endY = startY + v.getDeltaY();

						Piece piece = null;
						try {
							piece = getPieceInDir(p.getPosition(), v);
						} catch (IndexOutOfBoundsException e) {
							continue;
						}

						if (piece == null) {
							// Update the piece with its new location
							Piece newPiece = new Piece(p.getType(), new Point(
									endX, endY), p.getPlayer());
							Move m = new Move(newPiece, p.getPosition(),
									newPiece.getPosition());
							availableMoves.add(m);
						} else if (piece.getPlayer() != getTurn()) {
							// Update the piece with its new location
							Piece newPiece = new Piece(p.getType(), new Point(
									endX, endY), p.getPlayer());

							// Get the affected piece
							ArrayList<Piece> affected = new ArrayList<Piece>();
							affected.add(piece);

							Move m = new Move(newPiece, p.getPosition(),
									newPiece.getPosition(), affected, true,
									false);
							availableMoves.add(m);
						}
					}

					// Now to add in castling
					switch (getTurn()) {
					case 0:
						// First castling must be legal
						if (wCanCastleKingSide) {
							// Then the spaces inbetween must be empty
							if (getBoard().getMatrix()[7][5] == null
									&& getBoard().getMatrix()[7][6] == null) {
								// Finally we can't castle through check
								if (!isInCheck(p.getPosition())
										&& !isInCheck(new Point(7, 5))
										&& !isInCheck(new Point(7, 6))) {
									// Name the new castled piece
									Piece castle = new Piece(p.getType(),
											new Point(7, 6), p.getPlayer());

									// Get the affected piece
									ArrayList<Piece> affected = new ArrayList<Piece>();
									affected.add(new Piece("r",
											new Point(7, 7), p.getPlayer()));

									Move m = new Move(castle, p.getPosition(),
											castle.getPosition(), affected,
											true, false);
									availableMoves.add(m);
								}
							}
						}

						if (wCanCastleQueenSide) {
							if (getBoard().getMatrix()[7][1] == null
									&& getBoard().getMatrix()[7][2] == null
									&& getBoard().getMatrix()[7][3] == null) {
								if (!isInCheck(p.getPosition())
										&& !isInCheck(new Point(7, 2))
										&& !isInCheck(new Point(7, 3))) {
									// Name the new castled piece
									Piece castle = new Piece(p.getType(),
											new Point(7, 2), p.getPlayer());

									// Get the affected piece
									ArrayList<Piece> affected = new ArrayList<Piece>();
									affected.add(new Piece("r",
											new Point(7, 0), p.getPlayer()));

									Move m = new Move(castle, p.getPosition(),
											castle.getPosition(), affected,
											true, false);
									availableMoves.add(m);
								}

							}
						}
						break;
					case 1:
						if (bCanCastleKingSide) {
							if (getBoard().getMatrix()[0][5] == null
									&& getBoard().getMatrix()[0][6] == null) {
								if (!isInCheck(p.getPosition())
										&& !isInCheck(new Point(0, 5))
										&& !isInCheck(new Point(0, 6))) {
									// Name the new castled piece
									Piece castle = new Piece(p.getType(),
											new Point(0, 6), p.getPlayer());

									// Get the affected piece
									ArrayList<Piece> affected = new ArrayList<Piece>();
									affected.add(new Piece("R",
											new Point(0, 7), p.getPlayer()));

									Move m = new Move(castle, p.getPosition(),
											castle.getPosition(), affected,
											true, false);
									availableMoves.add(m);
								}
							}
						}

						if (bCanCastleQueenSide) {
							if (getBoard().getMatrix()[0][1] == null
									&& getBoard().getMatrix()[0][2] == null
									&& getBoard().getMatrix()[0][3] == null) {
								if (!isInCheck(p.getPosition())
										&& !isInCheck(new Point(0, 2))
										&& !isInCheck(new Point(0, 3))) {
									// Name the new castled piece
									Piece castle = new Piece(p.getType(),
											new Point(0, 2), p.getPlayer());

									// Get the affected piece
									ArrayList<Piece> affected = new ArrayList<Piece>();
									affected.add(new Piece("R",
											new Point(0, 0), p.getPlayer()));

									Move m = new Move(castle, p.getPosition(),
											castle.getPosition(), affected,
											true, false);
									availableMoves.add(m);
								}

							}
						}
						break;
					}
				}
				// If it's a queen, bishop, or rook
				else {
					for (Vector v : moves) {
						// Add all of the available moves!
						availableMoves.addAll(this.getAllMoves(p,
								p.getPosition(), v));
					}
				}
			}
		}

		return availableMoves;
	}

	@Override
	public ArrayList<Move> getAvailableMovesRestricted() {
		// TODO Auto-generated method stub
		return new ArrayList<Move>();
	}

	/**
	 * Evaluates whether the current player is in check.
	 * 
	 * @param player
	 * @return arraylist of pieces checking
	 */
	public boolean isInCheck(Point pos) {
		// King should never be null
		Point start = pos;
		Piece knight = new Piece("n", null, 0);
		ArrayList<Vector> knightMoves = getRules().getMoveMap().get(knight);

		for (Vector v : knightMoves) {
			Piece p = null;
			try {
				p = getPieceInDir(start, v);
			} catch (IndexOutOfBoundsException e) {
				continue;
			}

			if (p != null && p.getPlayer() != getTurn()
					&& (p.getType().equals("k") || p.getType().equals("K")))
				return true;
		}

		Piece diagLeftUp = crawl(start, new Vector(-1, -1));
		Piece diagLeftDown = crawl(start, new Vector(1, -1));
		Piece diagRightUp = crawl(start, new Vector(-1, 1));
		Piece diagRightDown = crawl(start, new Vector(1, 1));
		Piece up = crawl(start, new Vector(-1, 0));
		Piece down = crawl(start, new Vector(1, 0));
		Piece left = crawl(start, new Vector(0, -1));
		Piece right = crawl(start, new Vector(0, 1));

		// Now we check each of them... :(
		if (diagLeftUp != null
				&& diagLeftUp.getPlayer() != getTurn()
				&& (diagLeftUp.getType().equals("q")
						|| diagLeftUp.getType().equals("Q")
						|| diagLeftUp.getType().equals("b")
						|| diagLeftUp.getType().equals("B")
						|| diagLeftUp.getType().equals("p") || diagLeftUp
						.getType().equals("P"))) {
			return true;

		}

		if (diagLeftDown != null
				&& diagLeftDown.getPlayer() != getTurn()
				&& (diagLeftDown.getType().equals("q")
						|| diagLeftDown.getType().equals("Q")
						|| diagLeftDown.getType().equals("b") || diagLeftDown
						.getType().equals("B"))) {
			return true;

		}

		if (diagRightUp != null
				&& diagRightUp.getPlayer() != getTurn()
				&& (diagRightUp.getType().equals("q")
						|| diagRightUp.getType().equals("Q")
						|| diagRightUp.getType().equals("b")
						|| diagRightUp.getType().equals("B")
						|| diagRightUp.getType().equals("p") || diagRightUp
						.getType().equals("P"))) {
			return true;

		}

		if (diagRightDown != null
				&& diagRightDown.getPlayer() != getTurn()
				&& (diagRightDown.getType().equals("q")
						|| diagRightDown.getType().equals("Q")
						|| diagRightDown.getType().equals("b")
						|| diagRightDown.getType().equals("B")
						|| diagRightDown.getType().equals("p") || diagRightDown
						.getType().equals("P"))) {
			return true;

		}

		if (up != null
				&& up.getPlayer() != getTurn()
				&& (up.getType().equals("q") || up.getType().equals("Q")
						|| up.getType().equals("r") || up.getType().equals("R"))) {
			return true;

		}

		if (down != null
				&& down.getPlayer() != getTurn()
				&& (down.getType().equals("q") || down.getType().equals("Q")
						|| down.getType().equals("r") || down.getType().equals(
						"R"))) {
			return true;

		}

		if (right != null
				&& right.getPlayer() != getTurn()
				&& (right.getType().equals("q") || right.getType().equals("Q")
						|| right.getType().equals("r") || right.getType()
						.equals("R"))) {
			return true;

		}

		if (left != null
				&& left.getPlayer() != getTurn()
				&& (left.getType().equals("q") || left.getType().equals("Q")
						|| left.getType().equals("r") || left.getType().equals(
						"R"))) {
			return true;

		}

		return false;
	}

	/**
	 * Crawls in a given direction from a starting point until a piece is
	 * reached.
	 * 
	 * @param start
	 * @param dir
	 * @return the piece we reached or null if there is none
	 */
	private Piece crawl(Point start, Vector dir) {
		Piece p = null;
		Point currentPos = new Point(start.getX(), start.getY());

		try {
			// While we haven't reached a piece and end of board
			while (p == null) {

				// Get the piece in the direction
				p = getPieceInDir(currentPos, dir);

				// Increment the current pos
				currentPos = new Point(currentPos.getX() + dir.getDeltaX(),
						currentPos.getY() + dir.getDeltaY());
			}
		} catch (IndexOutOfBoundsException e) {
			// So we went off the board without finding anything
			return p;
		}

		return p;
	}

	/**
	 * Crawls in a given direction from a starting point until a piece is
	 * reached or the end of the board is reached. Logs all of the available
	 * moves in the middle.
	 * 
	 * @param piece
	 * @param start
	 * @param dir
	 * @return
	 */
	private ArrayList<Move> getAllMoves(Piece piece, Point start, Vector dir) {
		ArrayList<Move> moves = new ArrayList<Move>();

		Piece p = null;
		Point currentPos = new Point(start.getX(), start.getY());

		try {
			// While we haven't reached a piece
			while (p == null) {

				// Get the piece in the direction
				p = getPieceInDir(currentPos, dir);

				// Increment the current pos
				currentPos = new Point(currentPos.getX() + dir.getDeltaX(),
						currentPos.getY() + dir.getDeltaY());

				// If the piece is null we can move there
				if (p == null) {
					Piece newPiece = new Piece(piece.getType(), currentPos,
							piece.getPlayer());

					moves.add(new Move(newPiece, start, currentPos));
				}
				// Else if it's an enemy we can take it
				else if (p.getPlayer() != piece.getPlayer()) {
					Piece newPiece = new Piece(piece.getType(), currentPos,
							piece.getPlayer());

					// Grab affected pieces
					ArrayList<Piece> affected = new ArrayList<Piece>();
					affected.add(p);

					moves.add(new Move(newPiece, start, currentPos, affected,
							true, false));
				}
			}
		} catch (IndexOutOfBoundsException e) {
			return moves;
		}

		return moves;
	}

	/**
	 * Crawls in a given direction from a starting point for a certain number of
	 * steps or if a piece was found.
	 * 
	 * @param start
	 * @param dir
	 * @return the piece we reached or null if there is none
	 */
	private Piece crawl(Point start, Vector dir, int steps)
			throws IndexOutOfBoundsException {
		Piece p = null;
		Point currentPos = new Point(start.getX(), start.getY());
		int count = steps;

		// While we haven't reached a piece and count is > 0
		while (p == null && count > 0) {

			// Get the piece in the direction
			p = getPieceInDir(currentPos, dir);

			// Increment the current pos
			currentPos = new Point(currentPos.getX() + dir.getDeltaX(),
					currentPos.getY() + dir.getDeltaY());

			count--;
		}

		return p;
	}

	/**
	 * Gets the piece in a given direction adjacent to a starting position.
	 * 
	 * @param start
	 * @param dir
	 * @return the piece at the end coordinates, null if empty
	 */
	private Piece getPieceInDir(Point start, Vector dir)
			throws IndexOutOfBoundsException {
		int endX = start.getX() + dir.getDeltaX();
		int endY = start.getY() + dir.getDeltaY();

		if (!getBoard().isInBounds(endX, endY))
			throw new IndexOutOfBoundsException();

		String type = getBoard().getMatrix()[endX][endY];
		if (type == null)
			return null;
		else {
			Point end = new Point(endX, endY);
			return type.charAt(0) > 95 ? new Piece(type, end,
					Game.MINIMIZING_PLAYER) : new Piece(type, end,
					Game.MAXIMIZING_PLAYER);
		}
	}
}
