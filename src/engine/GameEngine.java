package engine;

import models.*;
import tictactoe.*;

/**
 * The abstract class that represents a GameEngine. Will be extended by
 * different engines that implement getMove in different ways.
 * 
 * @author Marcus
 * 
 */
public abstract class GameEngine {

	/**
	 * The abstract method to get the engine to determine the best move given
	 * its implementation. Implemented by subclasses in various ways.
	 * 
	 * @param g
	 *            The current Game
	 * @return The Move the GameEngine recommends you should make.
	 */
	public abstract Move getMove(Game g);

}
