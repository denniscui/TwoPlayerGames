package connect4;

import java.util.ArrayList;

import utils.Point;

import models.Board;
import models.Game;
import models.Move;
import models.Piece;
import models.Rules;

public class Connect4Game extends Game {

	// Standard position scores obtained by figuring out how many 4-in-a-rows
	// can be made including each position.
	public static final int[][] STANDARD_SCORE = new int[][] {
			{ 3, 4, 5, 7, 5, 4, 3 }, { 4, 6, 8, 10, 8, 6, 4 },
			{ 5, 8, 11, 13, 11, 8, 5 }, { 5, 8, 11, 13, 11, 8, 5 },
			{ 4, 6, 8, 10, 8, 6, 4 }, { 3, 4, 5, 7, 5, 4, 3 } };

	// The number of moves in the opening
	public static int OPENING_DEPTH = 15;

	public Connect4Game(int gameId, Rules rules) {
		super(gameId, rules);
	}

	public Connect4Game(int gameId, Board board, Rules rules, int turn,
			ArrayList<Move> moves) {
		super(gameId, board, rules, turn, moves);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int[] hasEnded() {
		if (this.getMoves().size() == 0)
			return new int[] { Game.NONE };

		Board board = getBoard();
		Move lastMove = this.getMoves().get(getMoves().size() - 1);

		int horizontal = crawl(new int[] { 0, -1 }, lastMove.getEnd(), lastMove
				.getPiece().getType(), board)
				+ crawl(new int[] { 0, 1 }, lastMove.getEnd(), lastMove
						.getPiece().getType(), board);

		int vertical = crawl(new int[] { 1, 0 }, lastMove.getEnd(), lastMove
				.getPiece().getType(), board);

		int leftRightDiag = crawl(new int[] { -1, 1 }, lastMove.getEnd(),
				lastMove.getPiece().getType(), board)
				+ crawl(new int[] { 1, -1 }, lastMove.getEnd(), lastMove
						.getPiece().getType(), board);

		int rightLeftDiag = crawl(new int[] { -1, -1 }, lastMove.getEnd(),
				lastMove.getPiece().getType(), board)
				+ crawl(new int[] { 1, 1 }, lastMove.getEnd(), lastMove
						.getPiece().getType(), board);

		if (horizontal >= 3 || vertical >= 3 || leftRightDiag >= 3
				|| rightLeftDiag >= 3) {
			int[] state = new int[] { Game.WIN, lastMove.getPiece().getPlayer() };
			onGameEnded(state);
			return state;
		} else if (board.isFilled()) {
			int[] state = new int[] { Game.DRAW };
			onGameEnded(state);
			return state;
		} else
			return new int[] { Game.NONE };
	}

	@Override
	public int evaluateState() {
		// If we can search to the end, then it's an endgame. Otherwise, it's an
		// early game.

		if (this.getMoves().size() >= OPENING_DEPTH)
			return Game.IN_OPENING;
		else
			return Game.IN_ENDING;
	}

	@Override
	public int scoreGame() {

		int baseScore = 0;

		// Calculates a base score based on static position scores
		for (Move m : this.getMoves()) {
			int positionScore = STANDARD_SCORE[m.getEnd().getX()][m.getEnd()
					.getY()];
			baseScore += m.getPiece().getPlayer() == Game.MAXIMIZING_PLAYER ? positionScore
					: -1 * positionScore;
		}

		// Adds a certain amount depending on the state of the game
		int[] currentState = this.hasEnded();
		if (currentState[0] == Game.DRAW) {
			// At least we didn't lose.
			baseScore += 250;
		} else if (currentState[0] == Game.WIN) {
			// Great for the maximizing player, bad otherwise.
			baseScore += currentState[1] == Game.MAXIMIZING_PLAYER ? 1000
					: -1000;
		}

		// TODO: Score needs to be modified to include 3-in-a-rows
		return baseScore;
	}

	@Override
	public Game clone() {
		Board clonedBoard = new Connect4Board(getRules().getRows(), getRules()
				.getCols(), (ArrayList<Piece>) getBoard().getPieces().clone());

		String[][] newMatrix = new String[getRules().getRows()][getRules()
				.getCols()];
		for (int r = 0; r < getRules().getRows(); r++) {
			for (int c = 0; c < getRules().getCols(); c++) {
				newMatrix[r][c] = getBoard().getMatrix()[r][c];
			}
		}

		clonedBoard.setMatrix(newMatrix);

		return new Connect4Game(this.getId(), clonedBoard, this.getRules(),
				this.getTurn(), (ArrayList<Move>) this.getMoves().clone());
	}

	@Override
	public ArrayList<Move> getAvailableMoves() {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();

		// Iterate over the columns
		for (int c = 0; c < this.getRules().getCols(); c++) {
			// Iterate over the rows in reverse order
			for (int r = this.getRules().getRows() - 1; r >= 0; r--) {
				// Add the first null position
				if (getBoard().getMatrix()[r][c] == null) {
					Piece p = new Piece(this.getRules().getPieces()
							.get(getTurn()).get(0).getType(), new Point(r, c),
							getTurn());
					possibleMoves.add(new Move(p, null, new Point(r, c)));
					break;
				}
			}
		}

		return possibleMoves;
	}

	@Override
	public ArrayList<Move> getAvailableMovesRestricted() {
		return new ArrayList<Move>();
	}
}
