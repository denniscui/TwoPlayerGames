package connect4;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import utils.Point;

import algorithms.AlphaBeta;
import algorithms.Greedy;
import algorithms.Minimax;
import models.Game;
import models.Move;
import models.Piece;

public class Connect4Simulation {
	public static void main(String[] args) {
		// Input states
		int WAIT = '0';
		int NEXT = '1';
		int BREAK = '2';

		Game g = new Connect4Game(Game.CONNECT_FOUR, new Connect4Rules());
		AlphaBeta m = new AlphaBeta(7);

		while (g.hasEnded()[0] == Game.NONE) {
			if (g.getTurn() == 1) {
				int x = -1;
				int y = -1;

				try {
					System.out.println("Please enter coordinates: ");
					BufferedReader bufferRead = new BufferedReader(
							new InputStreamReader(System.in));
					x = bufferRead.read() - '0';
					y = bufferRead.read() - '0';
				} catch (Exception e) {

				}

				if (x < 0 || x > 5 || y < 0 || y > 6)
					continue;
				else {
					Piece p = new Piece("Y", new Point(x, y), g.getTurn());
					g.addMove(new Move(p, null, new Point(x, y)));
					System.out.println(g.getBoard().toString());
					System.out.println(g.scoreGame());
				}
			} else {
				int n = WAIT;
				try {
					BufferedReader bufferRead = new BufferedReader(
							new InputStreamReader(System.in));
					n = bufferRead.read();
				} catch (Exception e) {

				}

				if (n == NEXT) {
					Move bestMove = m.getMove(g);
					System.out.println(bestMove.getEnd().getX() + ", "
							+ bestMove.getEnd().getY());
					g.addMove(bestMove);
					System.out.println(g.getBoard().toString());
				} else if (n == BREAK)
					break;
			}

		}
	}
}
