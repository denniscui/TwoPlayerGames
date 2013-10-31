package ui_views;

import java.util.ArrayList;

import com.dcmobile.tpgames.R;

import models.Game;
import models.Game.OnGameEndedListener;
import models.Game.OnTurnChangedListener;
import models.Move;
import models.Piece;
import algorithms.Minimax;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import tictactoe.TTTGame;
import tictactoe.TTTRules;
import ui_models.Cell;
import utils.Point;

public class TTTBoardView extends BoardView {

	private ProgressDialog mProgressDialog;

	public TTTBoardView(Context context) {
		super(context);

		mProgressDialog = new ProgressDialog(getContext());
	}

	public TTTBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mProgressDialog = new ProgressDialog(getContext());
		// TODO Auto-generated constructor stub
	}

	public TTTBoardView(Context context, AttributeSet attrs, int player) {
		super(context, attrs, player);

		mProgressDialog = new ProgressDialog(getContext());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initGame() {
		final Game g = new TTTGame(Game.TICTACTOE, new TTTRules());
		final Minimax m = new Minimax(5);
		g.setOnGameEndedListener(new OnGameEndedListener() {
			@Override
			public void onGameEnded(int[] state) {
				Log.v("Game", "Game Ended");
				setReadOnly(true);
				g.unregisterListeners();
				setFocusable(false);
				setFocusableInTouchMode(false);

				switch (state[0]) {
				case Game.WIN:
					if (state[1] == getPlayer())
						Toast.makeText(getContext(), "YOU WON!",
								Toast.LENGTH_LONG).show();
					else
						Toast.makeText(getContext(), "YOU LOST!",
								Toast.LENGTH_LONG).show();
					break;
				case Game.DRAW:
					Toast.makeText(getContext(), "It was a tie!",
							Toast.LENGTH_LONG).show();
					break;
				}
			}

		});

		g.setOnTurnChangedListener(new OnTurnChangedListener() {

			@Override
			public void onTurnChanged(int turn) {
				if (turn == getPlayer()) {
					setReadOnly(false);
				} else {
					setReadOnly(true);
					mProgressDialog.setTitle("Thinking...");
					mProgressDialog.setCancelable(false);
					mProgressDialog.show();
					new MakeMove().execute(m);
				}
			}

		});
		setGame(g);
	}

	@Override
	public void makeGameMove(Cell begin, Cell end) {
		String pieceType = getPlayer() == Game.MINIMIZING_PLAYER ? "X" : "O";
		Piece piece = new Piece(pieceType,
				new Point(end.getRow(), end.getCol()), getPlayer());

		getGame().addMove(
				new Move(piece, null, new Point(end.getRow(), end.getCol())));

	}

	@Override
	protected void setImages(float width, float height) {
		ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();

		Bitmap x = BitmapFactory.decodeResource(getResources(),
				R.drawable.ttt_x);
		Bitmap resizedx = Bitmap.createScaledBitmap(x, (int) width,
				(int) height, true);

		Bitmap o = BitmapFactory.decodeResource(getResources(),
				R.drawable.ttt_o);

		Bitmap resizedo = Bitmap.createScaledBitmap(o, (int) width,
				(int) height, true);

		if (getPlayer() == Game.MINIMIZING_PLAYER) {

			bmps.add(resizedx);
			bmps.add(resizedo);
		} else {

			bmps.add(resizedo);
			bmps.add(resizedx);
		}

		setImages(bmps);

	}

	private class MakeMove extends AsyncTask<Minimax, Object, Move> {

		@Override
		protected Move doInBackground(Minimax... params) {

			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND
							+ android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
			Game g = getGame();
			Minimax m = (Minimax) params[0];
			return m.getMove(g);
		}

		@Override
		protected void onPostExecute(Move result) {
			mProgressDialog.dismiss();

			int row = result.getEnd().getX();
			int col = result.getEnd().getY();

			Cell cell = getCells()[row][col];
			setCellValue(cell, 2);

			getGame().addMove(result);
		}

	}
}
