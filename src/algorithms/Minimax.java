package algorithms;

import java.util.ArrayList;

import models.*;
import engine.*;

/**
 * The class that implements GameEngine using a naive minimax search (no
 * alpha-beta pruning)
 * 
 * @author Marcus
 * 
 */
public class Minimax extends GameEngine {

	private int depth;

	public Minimax(int depth) {
		this.depth = depth;
	}

	/**
	 * Method to get a move for the given game, using a naive minimax search
	 * with NO alpha-beta pruning.
	 * 
	 */
	public Move getMove(Game g) {
		int playerID = g.getTurn();
		ArrayList<Move> possibleMoves = g.getAvailableMoves();

		if (possibleMoves.size() == 1)
			return possibleMoves.get(0);

		ArrayList<Game> possibleGames = getPossibleGames(g, possibleMoves);
		Game bestGame = possibleGames.get(0);
		int bestScore = minimaxScore(bestGame, depth, bestGame.getTurn());
		for (int i = 1; i < possibleGames.size(); i++) {
			Game testGame = possibleGames.get(i);
			int testScore = minimaxScore(testGame, depth, testGame.getTurn());
			System.out.println(testScore);
			if ((playerID == Game.MAXIMIZING_PLAYER && testScore > bestScore)
					|| (playerID == Game.MINIMIZING_PLAYER && testScore < bestScore)) {
				bestGame = testGame;
				bestScore = testScore;
			}
		}
		ArrayList<Move> gameMoves = bestGame.getMoves();
		return gameMoves.get(gameMoves.size() - 1);
	}

	private int minimaxScore(Game start, int depthToGo, int playerID) {
		ArrayList<Move> possibleMoves = start.getAvailableMoves();
		if (start.hasEnded()[0] == Game.WIN || possibleMoves.size() == 0
				|| depthToGo == 0) {
			return start.scoreGame();
		}

		ArrayList<Game> possibleGames = getPossibleGames(start, possibleMoves);
		int bestScore = minimaxScore(possibleGames.get(0), depthToGo - 1,
				possibleGames.get(0).getTurn());
		for (int i = 1; i < possibleGames.size(); i++) {
			Game testGame = possibleGames.get(i);
			int testScore = minimaxScore(testGame, depthToGo - 1,
					testGame.getTurn());

			if ((playerID == Game.MAXIMIZING_PLAYER && testScore > bestScore)
					|| (playerID == Game.MINIMIZING_PLAYER && testScore < bestScore)) {
				bestScore = testScore;
			}
		}
		return bestScore;
	}

	private ArrayList<Game> getPossibleGames(Game start, ArrayList<Move> moves) {
		ArrayList<Game> result = new ArrayList<Game>();
		for (Move m : moves) {
			Game tempGame = start.clone();
			if (tempGame.addMove(m)) {
				// System.out
				// .println("MoveX: " + m.getEnd().getX() + " MoveY: "
				// + m.getEnd().getY() + " Score: "
				// + tempGame.scoreGame());
				result.add(tempGame);
			}
		}
		return result;
	}

	private int flipID(int id) {
		return 1 - id;
	}

}
