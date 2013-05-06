package algorithms;

import java.util.ArrayList;

import models.*;
import engine.*;

/**
 * The class that implements GameEngine using a minimax approach with alpha/beta
 * pruning.
 * 
 * @author Marcus
 * 
 */
public class AlphaBeta extends GameEngine {

	private int depth;

	public AlphaBeta(int depth) {
		this.depth = depth;
	}

	/**
	 * Method to get a move for the given game, using a minimax search with
	 * alpha-beta pruning optimization.
	 * 
	 */
	public Move getMove(Game g) {
		int playerID = g.getTurn();
		ArrayList<Move> possibleMoves = g.getAvailableMoves();

		if (possibleMoves.size() == 1)
			return possibleMoves.get(0);

		ArrayList<Game> possibleGames = getPossibleGames(g, possibleMoves);
		Game bestGame = null;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		if (playerID == Game.MAXIMIZING_PLAYER) {
			for (Game testGame : possibleGames) {
				int testScore = alphaBetaScore(testGame, depth, alpha, beta,
						testGame.getTurn());
				if (testScore > alpha) {
					alpha = testScore;
					bestGame = testGame;
				}
			}
		} else {
			for (Game testGame : possibleGames) {
				int testScore = alphaBetaScore(testGame, depth, alpha, beta,
						testGame.getTurn());
				if (testScore < beta) {
					beta = testScore;
					bestGame = testGame;
				}
			}
		}

		ArrayList<Move> gameMoves = bestGame.getMoves();
		return gameMoves.get(gameMoves.size() - 1);
	}

	private int alphaBetaScore(Game start, int depthToGo, int alpha, int beta,
			int playerID) {
		ArrayList<Move> possibleMoves = start.getAvailableMoves();
		if (start.hasEnded()[0] == Game.WIN || possibleMoves.size() == 0
				|| depthToGo == 0) {
			return start.scoreGame();
		}
		ArrayList<Game> possibleGames = getPossibleGames(start, possibleMoves);
		if (playerID == Game.MAXIMIZING_PLAYER) {
			for (Game testGame : possibleGames) {
				alpha = Math.max(
						alpha,
						alphaBetaScore(testGame, depthToGo - 1, alpha, beta,
								testGame.getTurn()));
				if (alpha >= beta) {
					break;
				}
			}
			return alpha;
		} else {
			for (Game testGame : possibleGames) {
				beta = Math.min(
						beta,
						alphaBetaScore(testGame, depthToGo - 1, alpha, beta,
								testGame.getTurn()));
				if (alpha >= beta) {
					break;
				}
			}
			return beta;
		}
	}

	private ArrayList<Game> getPossibleGames(Game start, ArrayList<Move> moves) {
		ArrayList<Game> result = new ArrayList<Game>();
		for (Move m : moves) {
			Game tempGame = start.clone();
			if (tempGame.addMove(m)) {
				result.add(tempGame);
			}
		}
		return result;
	}
}
