package algorithms;

import models.*;
import engine.*;
import java.util.*;

/**
 * The class that implements GameEngine using a simple heuristic that makes the
 * simplest best move. Should be outperformed.
 * 
 * @author Marcus
 * 
 */
public class Greedy extends GameEngine {



	/**
	 * Method to get a move for the given game, using the move that maximizes
	 * the determined value of the possible Boards that can be moved to.
	 * 
	 */
	@Override
	public Move getMove(Game g) {
		ArrayList<Move> possibleMoves = g.getAvailableMoves();
		ArrayList<Game> possibleGames = getGames(g, possibleMoves);
		Game bestGame = possibleGames.get(0);
		int bestScore = bestGame.evaluateState();
		for (int i = 1; i < possibleGames.size(); i++) {
			if ((g.getTurn() == Game.MAXIMIZING_PLAYER && possibleGames.get(i)
					.scoreGame() > bestScore)
					|| (g.getTurn() == Game.MINIMIZING_PLAYER && possibleGames
							.get(i).scoreGame() < bestScore)) {
				bestGame = possibleGames.get(i);
				bestScore = bestGame.scoreGame();
			}
		}
		ArrayList<Move> gameMoves = bestGame.getMoves();
		return gameMoves.get(gameMoves.size() - 1);

	}

	private ArrayList<Game> getGames(Game start, ArrayList<Move> moves) {
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
