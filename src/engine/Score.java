package engine;

import models.Game;

/**
 * Defines a scoring heuristic for game states.
 * 
 * @author denniscui
 * 
 */
public abstract class Score {

	// The game to apply this scoring system to
	private Game game;

	/**
	 * Constructs a new scoring system for a game
	 * 
	 * @param game
	 */
	public Score(Game game) {
		this.game = game;
	}

	/**
	 * Subclasses should implement this to score a game.
	 * 
	 * @return score
	 */
	public abstract int scoreGame();

	/**
	 * Getter for the game this score operates on.
	 * 
	 * @return game
	 */
	public Game getGame() {
		return game;
	}
}
