package ui_views;

import java.util.ArrayList;

import com.dcmobile.tpgames.R;

import connect4.Connect4Game;
import connect4.Connect4Rules;

import models.Game;
import models.Move;
import models.Piece;
import models.Game.OnGameEndedListener;
import models.Game.OnTurnChangedListener;
import ui_models.Cell;
import utils.Point;
import algorithms.AlphaBeta;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class Connect4BoardView extends View {
	// Progress dialog
	private ProgressDialog mProgressDialog;

	// the human player identifier
	private int player;

	// default size of the board
	public static final int DEFAULT_BOARD_SIZE = 100;

	// cell dimensions
	private float mCellWidth;
	private float mCellHeight;

	// keep track of selected cell
	private int mSelectedCol;

	// read only when it's not your turn
	private boolean mReadOnly = false;

	// should show possible moves?
	private boolean mShowPossibleMoves = false;

	// the game
	private Game mGame;

	// the cells
	private Cell[][] mCells;

	// the default possible bitmap values
	private ArrayList<Bitmap> images;

	// Listeners
	private OnColumnSelectedListener mOnColSelectedListener;

	// Default no color transparent
	private static final int NO_COLOR = 0;

	// Different paints
	private Paint mLinePaint;
	private Paint mCellValuePaint;
	private Paint mBackgroundColorSecondary;
	private Paint mBackgroundColorReadOnly;
	private Paint mBackgroundColorSelected;

	// Background resources
	private Bitmap whiteDummy;

	public Connect4BoardView(Context context) {
		super(context);
	}

	public Connect4BoardView(Context context, AttributeSet attrs, int player) {
		super(context, attrs);
		// Initialize progress dialogue
		mProgressDialog = new ProgressDialog(getContext());

		// set first col
		mSelectedCol = -1;

		// setup the player
		this.player = player;

		// This view needs to be focusable
		setFocusable(true);
		setFocusableInTouchMode(true);

		// Initialize the paints
		mLinePaint = new Paint();
		mCellValuePaint = new Paint();
		mBackgroundColorSecondary = new Paint();
		mBackgroundColorReadOnly = new Paint();
		mBackgroundColorSelected = new Paint();

		mBackgroundColorReadOnly.setColor(Color.WHITE);
		mLinePaint.setColor(Color.BLACK);
		mCellValuePaint.setColor(Color.WHITE);
		mBackgroundColorSecondary.setColor(0xff3333ff);
		mBackgroundColorSelected.setColor(0xaa3aa6d0);

		// Smooth out the edges
		mLinePaint.setAntiAlias(true);
		mCellValuePaint.setAntiAlias(true);

		initGame();
		initCells();
	}

	/**
	 * Initialize the game.
	 */
	protected void initGame() {
		final Game g = new Connect4Game(Game.CONNECT_FOUR, new Connect4Rules());
		final AlphaBeta m = new AlphaBeta(5);
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

	/**
	 * Makes the move asynchronously.
	 * 
	 * @author denniscui
	 * 
	 */
	private class MakeMove extends AsyncTask<AlphaBeta, Object, Move> {

		@Override
		protected Move doInBackground(AlphaBeta... params) {

			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND
							+ android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
			Game g = getGame();
			AlphaBeta m = (AlphaBeta) params[0];
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

	public Game getGame() {
		return mGame;
	}

	public void setGame(Game g) {
		mGame = g;
	}

	public Cell[][] getCells() {
		return mCells;
	}

	public void setReadOnly(boolean readOnly) {
		mReadOnly = readOnly;
	}

	public boolean getReadOnly() {
		return mReadOnly;
	}

	public int getPlayer() {
		return player;
	}

	/**
	 * Initialize the cells for this board.
	 */
	private void initCells() {
		int rows = mGame.getRules().getRows();
		int cols = mGame.getRules().getCols();

		mCells = new Cell[rows][cols];

		// Initialize each of the cells to default
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Cell cell = new Cell(r, c);
				mCells[r][c] = cell;
			}
		}
	}

	/**
	 * Registers callback which will be invoked when a cell is selected.
	 * 
	 * @param l
	 */
	public void setOnColumnSelectedListener(OnColumnSelectedListener l) {
		mOnColSelectedListener = l;
	}

	protected void onColumnSelected(int col) {
		if (mOnColSelectedListener != null) {
			mOnColSelectedListener.onColumnSelected(col);
			postInvalidate();
		}
	}

	/**
	 * Listener for cell selection.
	 * 
	 * @author denniscui
	 * 
	 */
	public interface OnColumnSelectedListener {
		void onColumnSelected(int col);
	}

	/**
	 * Set the cell value.
	 * 
	 * @param cell
	 * @param value
	 */
	public void setCellValue(Cell cell, int value) {
		if (!cell.isFilled()) {
			cell.setValue(value);
			invalidate();
		}
	}

	/**
	 * Get the cell at a given (x, y) position.
	 * 
	 * @param x
	 * @param y
	 * @return cell at the given (x, y) position
	 */
	private int getColAtPoint(int x, int y) {
		// take into account padding
		int lx = x - getPaddingLeft();

		int col = (int) (lx / mCellWidth);

		return col;
	}

	/**
	 * Gets the first empty cell in the column starting from the bottom.
	 * 
	 * @param col
	 * @return first empty cell or null if full
	 */
	private Cell getFirstEmptyInCol(int col) {
		for (int r = mCells.length - 1; r >= 0; r--) {
			if (!mCells[r][col].isFilled())
				return mCells[r][col];
		}

		return null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mReadOnly) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				int selectedCol = getColAtPoint(x, y);
				Cell firstEmpty = getFirstEmptyInCol(selectedCol);

				// This logic will not work for checkers/chess
				if (mSelectedCol == selectedCol && firstEmpty != null) {

					// Need to think about this, won't work for checkers/chess
					setCellValue(firstEmpty, 1);

					// Also need to encode game logic here
					makeGameMove(null, firstEmpty);

					mSelectedCol = -1;
				} else if (firstEmpty == null)
					mSelectedCol = -1;
				else
					mSelectedCol = selectedCol;

				// update the board asap because selected cell changed
				invalidate();

				if (mSelectedCol != -1)
					onColumnSelected(mSelectedCol);
			}
		}

		return true;
	}

	/**
	 * Makes a game on cell selection
	 * 
	 * @param begin
	 * @param end
	 */
	public void makeGameMove(Cell begin, Cell end) {
		String pieceType = getPlayer() == Game.MINIMIZING_PLAYER ? "R" : "Y";
		Piece piece = new Piece(pieceType,
				new Point(end.getRow(), end.getCol()), getPlayer());

		getGame().addMove(
				new Move(piece, null, new Point(end.getRow(), end.getCol())));
	}

	// ----------------- Actual drawing done below -------------------

	// Sets the images to reduce overhead
	protected void setImages(float width, float height) {
		// Set up background resource
		whiteDummy = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.c4_w),
				(int) width, (int) height, true);

		ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();

		Bitmap r = BitmapFactory
				.decodeResource(getResources(), R.drawable.c4_r);
		Bitmap resizedr = Bitmap.createScaledBitmap(r, (int) width,
				(int) height, true);

		Bitmap y = BitmapFactory
				.decodeResource(getResources(), R.drawable.c4_y);

		Bitmap resizedy = Bitmap.createScaledBitmap(y, (int) width,
				(int) height, true);

		if (getPlayer() == Game.MINIMIZING_PLAYER) {

			bmps.add(resizedr);
			bmps.add(resizedy);
		} else {

			bmps.add(resizedy);
			bmps.add(resizedr);
		}

		setImages(bmps);
	}

	public void setImages(ArrayList<Bitmap> bmps) {
		images = bmps;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width = -1, height = -1;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = DEFAULT_BOARD_SIZE;
			if (widthMode == MeasureSpec.AT_MOST && width > widthSize) {
				width = widthSize;
			}
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = DEFAULT_BOARD_SIZE;
			if (heightMode == MeasureSpec.AT_MOST && height > heightSize) {
				height = heightSize;
			}
		}

		if (widthMode != MeasureSpec.EXACTLY)
			width = height;

		if (heightMode != MeasureSpec.EXACTLY)
			height = width;

		if (widthMode == MeasureSpec.AT_MOST && width > widthSize)
			width = widthSize;

		if (heightMode == MeasureSpec.AT_MOST && height > heightSize)
			height = heightSize;

		mCellWidth = (width - getPaddingLeft() - getPaddingRight())
				/ ((float) mGame.getRules().getCols());

		mCellHeight = (height - getPaddingLeft() - getPaddingRight())
				/ ((float) mGame.getRules().getRows());

		// After we have these values, set the default images
		setImages(mCellWidth, mCellHeight);

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Why do we only subtract half the padding?
		int width = getWidth() - getPaddingRight();
		int height = getHeight() - getPaddingBottom();

		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();

		int rows = mGame.getRules().getRows();
		int cols = mGame.getRules().getCols();

		if (mBackgroundColorSecondary.getColor() != NO_COLOR) {
			canvas.drawRect(0, 0, width, height, mBackgroundColorSecondary);
		}

		// draw the cells
		int cellLeft, cellTop;
		if (mCells != null) {
			boolean hasBackgroundColorReadOnly = mBackgroundColorReadOnly
					.getColor() != NO_COLOR;

			// Draw all of the cells
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					Cell cell = mCells[r][c];

					cellLeft = Math.round((c * mCellWidth) + paddingLeft);
					cellTop = Math.round((r * mCellHeight) + paddingTop);

					// draw read only background
					if (!cell.isFilled() && hasBackgroundColorReadOnly) {
						canvas.drawBitmap(whiteDummy, cellLeft, cellTop,
								mBackgroundColorReadOnly);
					}

					// draw cell image
					int value = cell.getValue();

					if (value != 0) {
						canvas.drawBitmap(images.get(value - 1), cellLeft,
								cellTop, mCellValuePaint);

					}
				}
			}

			// highlight selected cell
			if (!mReadOnly && mSelectedCol != -1) {
				cellLeft = Math.round(mSelectedCol * mCellWidth) + paddingLeft;
				cellTop = paddingTop;

				canvas.drawRect(cellLeft, cellTop, cellLeft + mCellWidth,
						cellTop + mCellHeight * mGame.getRules().getRows(),
						mBackgroundColorSelected);
			}

			// // draw vertical lines
			// for (int c = 0; c < cols; c++) {
			// float x = (c * mCellWidth) + paddingLeft;
			// canvas.drawLine(x, paddingTop, x, height, mLinePaint);
			// }
			//
			// // draw horizontal lines
			// for (int r = 0; r < rows; r++) {
			// float y = (r * mCellHeight) + paddingTop;
			// canvas.drawLine(paddingLeft, y, width, y, mLinePaint);
			// }
		}
	}
}
