package models;

import java.util.ArrayList;

import utils.Point;

/**
 * 
 * @author denniscui
 * 
 *         This class describes the parameters of a general game. Should be
 *         subclassed to define the state of a specific game.
 */
public abstract class Game {
	// Types of games
	public static final int CONNECT_FOUR = 0;
	public static final int TICTACTOE = 1;
	public static final int CHECKERS = 2;
	public static final int CHESS = 3;

	// Game states
	public static final int IN_OPENING = 0;
	public static final int IN_MIDDLE = 1;
	public static final int IN_ENDING = 2;

	// Win/loss/continue constants
	public static final int DRAW = 0;
	public static final int WIN = 1;
	public static final int NONE = 2;

	// Player IDs
	public static final int MINIMIZING_PLAYER = 0;
	public static final int MAXIMIZING_PLAYER = 1;

	// Keeps track of the game state
	private int gameState;

	// True means the current turn has restricted moves
	private boolean restricted = false;

	// This is the restricted piece
	private Piece restrictedPiece = null;

	// The id for this game.
	private int gameId;

	// The current turn, either 0 or 1.
	private int turn;

	// For testing purposes. the players.
	private boolean[] players;

	// The current state of the board.
	private Board board;

	// The rules of the game.
	private Rules rules;

	// The list of moves made
	private ArrayList<Move> moves;

	// Listeners
	private OnTurnChangedListener turnChangedListener;
	private OnGameEndedListener gameEndedListener;

	/**
	 * Create a new game.
	 * 
	 * @param gameId
	 *            the name of the game
	 * @param board
	 *            the current board
	 * @param rules
	 *            the rules of the game
	 */
	public Game(int gameId, Board board, Rules rules, int turn,
			ArrayList<Move> moves) {
		this.gameId = gameId;
		this.turn = turn;
		this.board = board;

		this.rules = rules;
		this.moves = moves;

		evaluateState();
	}

	/**
	 * Creates a game from an arraylist of moves.
	 * 
	 * @param gameId
	 * @param rules
	 * @param turn
	 * @param moves
	 */
	public Game(int gameId, Rules rules, ArrayList<Move> moves) {
		this.gameId = gameId;
		this.turn = moves.size() % 2;
		this.board = rules.defaultBoard();
		this.rules = rules;

		this.moves = new ArrayList<Move>();

		for (Move m : moves) {
			this.addMove(m);
		}

		evaluateState();
	}

	/**
	 * Create a new game.
	 * 
	 * @param gameId
	 *            the name of the game
	 * @param rules
	 *            the rules of the game
	 */
	public Game(int gameId, Rules rules) {
		this.gameId = gameId;
		this.turn = 0;

		// Generates a default board
		this.board = rules.defaultBoard();

		this.rules = rules;
		this.moves = new ArrayList<Move>();

		this.gameState = evaluateState();
	}

	/**
	 * Get the game's ID.
	 * 
	 * @return gameId
	 */
	public int getId() {
		return gameId;
	}

	/**
	 * Get the current turn.
	 * 
	 * @return turn
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * Get the state of the current board.
	 * 
	 * @return board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Get the rules for the current game.
	 * 
	 * @return rules
	 */
	public Rules getRules() {
		return rules;
	}

	/**
	 * Get the moves made so far in this game.
	 * 
	 * @return moves
	 */
	public ArrayList<Move> getMoves() {
		return moves;
	}

	/**
	 * Adds a move to the list of moves.
	 * 
	 * @param m
	 * @param only
	 *            adds the move if it could be made
	 */
	public boolean addMove(Move m) {
		if (makeMove(m)) {
			moves.add(m);
			hasEnded();
			onTurnChanged(turn);
			return true;
		}
		return false;
	}

	/**
	 * Updates the state of the board using a new board.
	 * 
	 * @param board
	 */
	public void updateBoard(Board board) {
		this.board = board;
	}

	/**
	 * Updates the state of the board using a move.
	 * 
	 * @param move
	 * @return true iff the move can be made
	 */
	public boolean makeMove(Move move) {
		// Make the move
		board.makeMove(move);

		// If it's the last move of the turn or there are no restricted
		// moves
		if (move.isEnd()) {
			// Change the turn when the move is made
			turn = 1 - turn;
			restricted = false;
			// No restricted piece when turn ends
			setRestrictedPiece(null);
		} else {
			restricted = true;

			// Next turn is restricted
			setRestrictedPiece(move.getPiece());

			// If there are no more moves, end the turn
			if (getAvailableMovesRestricted().size() == 0) {
				restricted = false;
				turn = 1 - turn;
			}
		}

		return true;
	}

	/**
	 * Returns the stage of the game.
	 * 
	 * @return IN_OPENING if the game is in its opening stage; IN_MIDDLE if the
	 *         game is in mid-game; IN_ENDING if the game is at its end stage
	 */
	public int getGameState() {
		return gameState;
	}

	/**
	 * Finds out if the game has ended yet.
	 * 
	 * @return true if the game is over
	 */
	public abstract int[] hasEnded();

	/**
	 * Getter for the restriction status.
	 * 
	 * @return restricted
	 */
	public boolean isRestricted() {
		return restricted;
	}

	/**
	 * Sets the restriction status.
	 * 
	 * @param b
	 */
	public void setRestriction(boolean b) {
		this.restricted = b;
	}

	/**
	 * Returns the restricted piece.
	 * 
	 * @return restrictedPiece
	 */
	public Piece getRestrictedPiece() {
		return restrictedPiece;
	}

	/**
	 * Sets the restricted piece.
	 * 
	 * @param p
	 */
	public void setRestrictedPiece(Piece p) {
		restrictedPiece = p;
	}

	/**
	 * Subclasses of Game should define an evaluation function for the different
	 * states of the game. This should return the state of the game and update
	 * the game state.
	 * 
	 * @return IN_OPENING if the game is in its opening stage; IN_MIDDLE if the
	 *         game is in mid-game; IN_ENDING if the game is at its end stage
	 */
	public abstract int evaluateState();

	/**
	 * Does a deep clone of the game.
	 */
	public abstract Game clone();

	/**
	 * Generates a score for the current game state.
	 * 
	 * @return score
	 */
	public abstract int scoreGame();

	/**
	 * Get a list of the available moves!
	 * 
	 * @return available moves
	 */
	public abstract ArrayList<Move> getAvailableMoves();

	/**
	 * Get a list of the available moves restricted to a certain piece.
	 * 
	 * @param piece
	 * @return available moves
	 */
	public abstract ArrayList<Move> getAvailableMovesRestricted();

	/**
	 * Crawls from a given point in a given direction until the piece type
	 * changes.
	 * 
	 * @param dir
	 * @param startP
	 * @param startType
	 * @param board
	 * @return distance crawled, not including the starting piece
	 */
	public int crawl(int[] dir, Point startP, String startType, Board board) {
		int xDir = dir[0];
		int yDir = dir[1];

		int curX = startP.getX();
		int curY = startP.getY();

		int newX = curX + xDir;
		int newY = curY + yDir;

		try {
			if (startType.equals(board.getMatrix()[newX][newY])) {
				return 1 + crawl(dir, new Point(newX, newY), startType, board);
			} else
				return 0;
		} catch (IndexOutOfBoundsException e) {
			// On IndexOutOfBoundsException
			return 0;
		}
	}

	// ----- Setting up listeners --------

	public interface OnTurnChangedListener {
		void onTurnChanged(int turn);
	}

	public interface OnGameEndedListener {
		void onGameEnded(int[] state);
	}

	public void setOnTurnChangedListener(OnTurnChangedListener l) {
		turnChangedListener = l;
	}

	public void setOnGameEndedListener(OnGameEndedListener l) {
		gameEndedListener = l;
	}

	protected void onTurnChanged(int turn) {
		if (turnChangedListener != null) {
			turnChangedListener.onTurnChanged(turn);
		}
	}

	protected void onGameEnded(int[] state) {
		if (gameEndedListener != null) {
			gameEndedListener.onGameEnded(state);
		}
	}

	public void unregisterListeners() {
		turnChangedListener = null;
		gameEndedListener = null;
	}

	public ArrayList<Move> getAvailableMoves(Piece p) {
		return null;
	}
}
