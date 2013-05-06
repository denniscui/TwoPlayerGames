package chess;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import algorithms.AlphaBeta;

import models.Game;
import models.Move;
import checkers.CheckersGame;
import checkers.CheckersRules;
import engine.GameEngine;

public class ChessSimulation {
	public static void main(String[] args) {
		// Input states
		int WAIT = '0';
		int NEXT = '1';
		int BREAK = '2';

		Game g = new ChessGame(Game.CHESS, new ChessRules());
		GameEngine m = new AlphaBeta(3);

		while (g.hasEnded()[0] == Game.NONE) {

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
