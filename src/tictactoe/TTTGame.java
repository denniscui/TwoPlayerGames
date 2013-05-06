package tictactoe;

import java.util.ArrayList;

import tictactoe.TTTBoard;
import tictactoe.TTTGame;
import utils.Point;
import models.Board;
import models.Game;
import models.Move;
import models.Piece;
import models.Rules;

public class TTTGame extends Game {

	public static final int[][] STANDARD_SCORE = new int[][] { { 3, 2, 3 },
			{ 2, 4, 2 }, { 3, 2, 3 } };

	public static int OPENING_DEPTH = 0;

	public TTTGame(int gameId, Rules rules) {
		super(gameId, rules);
	}

	public TTTGame(int gameId, Board board, Rules rules, int turn,
			ArrayList<Move> moves) {
		super(gameId, board, rules, turn, moves);

	}

	@Override
	public int[] hasEnded() {
		if (this.getMoves().size() == 0)
			return new int[] { Game.NONE };

		Board board = getBoard();
		Move lastMove = getMoves().get(getMoves().size() - 1);

		int horizontal = crawl(new int[] { 0, -1 }, lastMove.getEnd(), lastMove
				.getPiece().getType(), board)
				+ crawl(new int[] { 0, 1 }, lastMove.getEnd(), lastMove
						.getPiece().getType(), board);

		int vertical = crawl(new int[] { 1, 0 }, lastMove.getEnd(), lastMove
				.getPiece().getType(), board)
				+ crawl(new int[] { -1, 0 }, lastMove.getEnd(), lastMove
						.getPiece().getType(), board);

		int leftRightDiag = crawl(new int[] { -1, 1 }, lastMove.getEnd(),
				lastMove.getPiece().getType(), board)
				+ crawl(new int[] { 1, -1 }, lastMove.getEnd(), lastMove
						.getPiece().getType(), board);

		int rightLeftDiag = crawl(new int[] { -1, -1 }, lastMove.getEnd(),
				lastMove.getPiece().getType(), board)
				+ crawl(new int[] { 1, 1 }, lastMove.getEnd(), lastMove
						.getPiece().getType(), board);

		if (horizontal == 2 || vertical == 2 || leftRightDiag == 2
				|| rightLeftDiag == 2) {
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

		return baseScore;
	}

	@Override
	public Game clone() {
		Board clonedBoard = new TTTBoard(getRules().getRows(), getRules()
				.getCols(), (ArrayList<Piece>) getBoard().getPieces().clone());

		String[][] newMatrix = new String[getRules().getRows()][getRules()
				.getCols()];
		for (int r = 0; r < getRules().getRows(); r++) {
			for (int c = 0; c < getRules().getCols(); c++) {
				newMatrix[r][c] = getBoard().getMatrix()[r][c];
			}
		}

		clonedBoard.setMatrix(newMatrix);

		return new TTTGame(this.getId(), clonedBoard, this.getRules(),
				this.getTurn(), (ArrayList<Move>) this.getMoves().clone());
	}

	@Override
	public ArrayList<Move> getAvailableMoves() {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();

		// Iterate over the columns
		for (int c = 0; c < this.getRules().getCols(); c++) {
			// Iterate over the rows
			for (int r = 0; r < this.getRules().getRows(); r++) {
				// Add the first null position
				if (getBoard().getMatrix()[r][c] == null) {
					Piece p = new Piece(this.getRules().getPieces()
							.get(getTurn()).get(0).getType(), new Point(r, c),
							getTurn());
					possibleMoves.add(new Move(p, null, new Point(r, c)));
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
