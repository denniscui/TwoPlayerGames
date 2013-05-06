package connect4;

import java.util.ArrayList;

import models.Board;
import models.Move;
import models.Piece;

/**
 * Model for a Connect Four board.
 * 
 * @author denniscui
 * 
 */
public class Connect4Board extends Board {

	public Connect4Board(int rows, int cols, ArrayList<Piece> pieces) {
		super(rows, cols, pieces);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean makeMove(Move move) {
		Piece p = move.getPiece();

		this.addPiece(p);

		// int newX = move.getEnd().getX();
		// int newY = move.getEnd().getY();

		// We might be able to do this in one line if it's pass by reference
		// getMatrix()[newX][newY] = p.getType();

		return true;
	}

}
