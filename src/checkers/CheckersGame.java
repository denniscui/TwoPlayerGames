package checkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import connect4.Connect4Board;
import connect4.Connect4Game;

import utils.Point;
import utils.Vector;

import models.Board;
import models.Game;
import models.Move;
import models.Piece;
import models.Rules;

/**
 * The game of checkers following rules at
 * http://www.jimloy.com/checkers/rules2.htm
 * 
 * @author denniscui
 * 
 */
public class CheckersGame extends Game {

	public static final String[][] TYPES = new String[][] { { "r", "R" },
			{ "b", "B" } };

	// Score for having a pawn
	public static final int PAWN_SCORE = 15;

	// Score for having a king
	public static final int KING_SCORE = 30;

	// Score for advancing pawns forward
	public static final int ADVANCE_SCORE_MULTIPLIER = 2;

	public CheckersGame(int gameId, Board board, Rules rules, int turn,
			ArrayList<Move> moves) {
		super(gameId, board, rules, turn, moves);
		// TODO Auto-generated constructor stub
	}

	public CheckersGame(int gameId, Rules rules) {
		super(gameId, rules);
	}

	public CheckersGame(int gameId, Rules rules, ArrayList<Move> moves) {
		super(gameId, rules, moves);
	}

	@Override
	public int evaluateState() {
		// Leave it at early game for now
		return Game.IN_OPENING;
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

		return new CheckersGame(this.getId(), clonedBoard, this.getRules(),
				this.getTurn(), (ArrayList<Move>) this.getMoves().clone());
	}

	@Override
	public int scoreGame() {
		int baseScore = 0;

		// Adds a certain amount depending on the state of the game
		int[] currentState = this.hasEnded();
		if (currentState[0] == Game.WIN) {
			// Great for the maximizing player, bad otherwise.
			baseScore += currentState[1] == Game.MAXIMIZING_PLAYER ? 1000
					: -1000;
		}

		// Iterate over all the pieces on the board
		for (Piece p : getBoard().getPieces()) {
			// Pawns
			switch (p.getType().charAt(0)) {
			case 'r':
				baseScore -= PAWN_SCORE;

				// It's good to move pawns forward
				if (p.getPosition().getX() > 3)
					baseScore -= ADVANCE_SCORE_MULTIPLIER
							* (p.getPosition().getX() - 3);

				break;
			case 'b':
				baseScore += 5;

				// It's good to move pawns forward
				if (p.getPosition().getX() < 4)
					baseScore += ADVANCE_SCORE_MULTIPLIER
							* (4 - p.getPosition().getX());
				break;
			case 'R':
				baseScore -= KING_SCORE;
				break;
			case 'B':
				baseScore += KING_SCORE;
				break;
			}
		}

		return baseScore;
	}

	@Override
	public ArrayList<Move> getAvailableMoves() {

		if (isRestricted())
			return getAvailableMovesRestricted();

		// One list for jumps, one for singles
		ArrayList<Move> jumpMoves = new ArrayList<Move>();
		ArrayList<Move> singleMoves = new ArrayList<Move>();

		// For each piece on the board
		for (Piece p : this.getBoard().getPieces()) {
			// If the piece belongs to the player, check it
			if (p.getPlayer() == this.getTurn()) {
				// Get the key version of this piece by setting position = null
				Piece key = new Piece(p.getType(), null, p.getPlayer());
				HashMap<Piece, ArrayList<Vector>> moveMap = this.getRules()
						.getMoveMap();
				ArrayList<Vector> moves = moveMap.get(key);

				// For each vector in the move map
				for (Vector v : moves) {
					int dirX = v.getDeltaX();
					int dirY = v.getDeltaY();

					int curX = p.getPosition().getX();
					int curY = p.getPosition().getY();

					int newX = dirX + curX;
					int newY = dirY + curY;

					// Check if the new position is in bounds and empty
					if (getBoard().isInBounds(newX, newY)
							&& getBoard().getMatrix()[newX][newY] == null) {

						// Deal with promotions
						boolean isPromotion = false;
						String newType = promote(p, newX);

						if (!newType.equals(p.getType()))
							isPromotion = true;

						// Make sure the piece contains the new position
						Piece newPiece = new Piece(newType, new Point(newX,
								newY), p.getPlayer());

						switch (Math.abs(dirX)) {
						case 1:
							// Make sure there are no capture moves and
							// restriction isn't on
							if (!isRestricted() && jumpMoves.size() == 0) {
								singleMoves.add(new Move(newPiece, p
										.getPosition(), new Point(newX, newY),
										new ArrayList<Piece>(), true,
										isPromotion));
							}
							break;
						case 2:
							int midX = (newX + curX) / 2;
							int midY = (newY + curY) / 2;

							String midVal = this.getBoard().getMatrix()[midX][midY];

							// Make sure the middle value is of the other type
							if (midVal != null
									&& (midVal.equals(TYPES[1 - getTurn()][0]) || midVal
											.equals(TYPES[1 - getTurn()][1]))) {
								// The middle piece is affected
								ArrayList<Piece> affected = new ArrayList<Piece>();
								affected.add(new Piece(midVal, new Point(midX,
										midY), 1 - this.getTurn()));

								jumpMoves.add(new Move(newPiece, p
										.getPosition(), new Point(newX, newY),
										affected, false, isPromotion));
							}

							break;
						}
					}
				}
			}
		}

		if (jumpMoves.size() > 0)
			return jumpMoves;
		else {
			return singleMoves;
		}
	}

	/**
	 * Helper function to calculate whether a pawn should promote itself.
	 * 
	 * @param piece
	 * @param endX
	 * @return new type
	 */
	private String promote(Piece piece, int endX) {
		if (piece.getType().equals("B") || piece.getType().equals("R"))
			return piece.getType();

		if (piece.getPlayer() == Game.MAXIMIZING_PLAYER) {
			return endX == 0 ? "B" : "b";
		} else {
			return endX == 7 ? "R" : "r";
		}

	}

	@Override
	public int[] hasEnded() {
		// If this player has no more moves, the other guy won
		if (getAvailableMoves().size() == 0) {
			int[] state = new int[] { Game.WIN, 1 - this.getTurn() };
			onGameEnded(state);
			return state;
		}

		if (getBoard().getPieces().size() == 2) {
			Piece p1 = getBoard().getPieces().get(0);
			Piece p2 = getBoard().getPieces().get(1);

			// If both are uppercase then they're both kings
			if (p1.getType().charAt(0) < 95 && p2.getType().charAt(0) < 95)
				return new int[] { Game.DRAW };
		}

		// Otherwise, keep playing
		return new int[] { Game.NONE };
	}

	@Override
	public ArrayList<Move> getAvailableMovesRestricted() {
		// This is the piece we care about
		Piece piece = getRestrictedPiece();

		// Only jumps allowed in restricted mode
		ArrayList<Move> jumpMoves = new ArrayList<Move>();

		// Get the key version of this piece by setting position = null
		Piece key = new Piece(piece.getType(), null, piece.getPlayer());
		HashMap<Piece, ArrayList<Vector>> moveMap = this.getRules()
				.getMoveMap();
		ArrayList<Vector> moves = moveMap.get(key);

		// For each vector in the move map
		for (Vector v : moves) {
			int dirX = v.getDeltaX();
			int dirY = v.getDeltaY();

			int curX = piece.getPosition().getX();
			int curY = piece.getPosition().getY();

			int newX = dirX + curX;
			int newY = dirY + curY;

			// Check if the new position is in bounds and empty
			if (getBoard().isInBounds(newX, newY)
					&& getBoard().getMatrix()[newX][newY] == null) {

				// Deal with promotions
				boolean isPromotion = false;
				String newType = promote(piece, newX);

				if (!newType.equals(piece.getType()))
					isPromotion = true;

				// Make sure the piece contains the new position
				Piece newPiece = new Piece(newType, new Point(newX, newY),
						piece.getPlayer());

				switch (Math.abs(dirX)) {
				case 2:
					int midX = (newX + curX) / 2;
					int midY = (newY + curY) / 2;

					String midVal = this.getBoard().getMatrix()[midX][midY];

					// Make sure the middle value is of the other type
					if (midVal != null
							&& (midVal.equals(TYPES[1 - getTurn()][0]) || midVal
									.equals(TYPES[1 - getTurn()][1]))) {
						// The middle piece is affected
						ArrayList<Piece> affected = new ArrayList<Piece>();
						affected.add(new Piece(midVal, new Point(midX, midY),
								1 - this.getTurn()));

						jumpMoves.add(new Move(newPiece, piece.getPosition(),
								new Point(newX, newY), affected, false,
								isPromotion));
					}

					break;
				}
			}
		}

		return jumpMoves;
	}

	@Override
	public ArrayList<Move> getAvailableMoves(Piece p) {

		// One list for jumps, one for singles
		ArrayList<Move> jumpMoves = new ArrayList<Move>();
		ArrayList<Move> singleMoves = new ArrayList<Move>();

		// Get the key version of this piece by setting position = null
		Piece key = new Piece(p.getType(), null, p.getPlayer());
		HashMap<Piece, ArrayList<Vector>> moveMap = this.getRules()
				.getMoveMap();
		ArrayList<Vector> moves = moveMap.get(key);

		// For each vector in the move map
		for (Vector v : moves) {
			int dirX = v.getDeltaX();
			int dirY = v.getDeltaY();

			int curX = p.getPosition().getX();
			int curY = p.getPosition().getY();

			int newX = dirX + curX;
			int newY = dirY + curY;

			// Check if the new position is in bounds and empty
			if (getBoard().isInBounds(newX, newY)
					&& getBoard().getMatrix()[newX][newY] == null) {

				// Deal with promotions
				boolean isPromotion = false;
				String newType = promote(p, newX);

				if (!newType.equals(p.getType()))
					isPromotion = true;

				// Make sure the piece contains the new position
				Piece newPiece = new Piece(newType, new Point(newX, newY),
						p.getPlayer());

				switch (Math.abs(dirX)) {
				case 1:
					// Make sure there are no capture moves and
					// restriction isn't on
					if (!isRestricted() && jumpMoves.size() == 0) {
						singleMoves.add(new Move(newPiece, p.getPosition(),
								new Point(newX, newY), new ArrayList<Piece>(),
								true, isPromotion));
					}
					break;
				case 2:
					int midX = (newX + curX) / 2;
					int midY = (newY + curY) / 2;

					String midVal = this.getBoard().getMatrix()[midX][midY];

					// Make sure the middle value is of the other type
					if (midVal != null
							&& (midVal.equals(TYPES[1 - getTurn()][0]) || midVal
									.equals(TYPES[1 - getTurn()][1]))) {
						// The middle piece is affected
						ArrayList<Piece> affected = new ArrayList<Piece>();
						affected.add(new Piece(midVal, new Point(midX, midY),
								1 - this.getTurn()));

						jumpMoves.add(new Move(newPiece, p.getPosition(),
								new Point(newX, newY), affected, false,
								isPromotion));
					}

					break;
				}
			}
		}

		if (jumpMoves.size() > 0)
			return jumpMoves;
		else {
			return singleMoves;
		}
	}
}
