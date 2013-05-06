package ui_views;

import java.util.ArrayList;

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
import android.view.View.MeasureSpec;
import android.widget.Toast;

import checkers.CheckersGame;
import checkers.CheckersRules;

import com.dcmobile.tpgames.R;

public class CheckersBoardView extends View {
	// Progress dialog
	private ProgressDialog mProgressDialog;

	// the human player identifier
	private int player;

	// available moves for the given cell
	private ArrayList<Move> mAvailableMoves;
	private ArrayList<Cell> mAvailableCells;
	private ArrayList<Move> mAllAvailableMoves;

	// default size of the board
	public static final int DEFAULT_BOARD_SIZE = 100;

	// cell dimensions
	private float mCellWidth;
	private float mCellHeight;

	// keep track of selected cell
	private Cell mSelectedCell;

	// read only when it's not your turn
	private boolean mReadOnly = false;

	// should show possible moves?
	private boolean mShowPossibleMoves = true;

	// the game
	private Game mGame;

	// the cells
	private Cell[][] mCells;

	// the default possible bitmap values
	private ArrayList<Bitmap> images;

	// Listeners
	private OnCellSelectedListener mOnCellSelectedListener;

	// Default no color transparent
	private static final int NO_COLOR = 0;

	// Different paints
	private Paint mLinePaint;
	private Paint mCellValuePaint;
	private Paint mBackgroundColorSecondary;
	private Paint mBackgroundColorReadOnly;
	private Paint mBackgroundColorSelected;
	private Paint mPossibleMovesPaint;

	// Background
	private Bitmap woodenBG;

	public CheckersBoardView(Context context) {
		super(context);
	}

	public CheckersBoardView(Context context, AttributeSet attrs, int player) {
		super(context, attrs);
		// Initialize progress dialogue
		mProgressDialog = new ProgressDialog(getContext());

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
		mPossibleMovesPaint = new Paint();

		mBackgroundColorReadOnly.setColor(0xaaf1dcb9);
		mLinePaint.setColor(Color.BLACK);
		mCellValuePaint.setColor(Color.WHITE);
		mBackgroundColorSecondary.setColor(Color.WHITE);
		mBackgroundColorSelected.setColor(0xaa3aa6d0);
		mPossibleMovesPaint.setColor(Color.GREEN);

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
		final Game g = new CheckersGame(Game.CHECKERS, new CheckersRules());
		final AlphaBeta m = new AlphaBeta(5);
		g.setOnGameEndedListener(new OnGameEndedListener() {
			@Override
			public void onGameEnded(int[] state) {
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
					if (g.isRestricted())
						mAllAvailableMoves = mGame
								.getAvailableMovesRestricted();
					else
						mAllAvailableMoves = g.getAvailableMoves();
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

		mAllAvailableMoves = g.getAvailableMoves();
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

			Point end = result.getPiece().getPosition();
			System.out.println("EndRow: " + end.getX() + " EndCol: "
					+ end.getY());
			int beginRow = result.getStart().getX();
			int beginCol = result.getStart().getY();

			int row = result.getEnd().getX();
			int col = result.getEnd().getY();
			Log.v("POSITION", "StartRow: " + beginRow + " StartCol: "
					+ beginCol + " EndRow: " + row + " EnCol: " + col);
			Cell beginCell = mCells[beginRow][beginCol];
			setCellValue(beginCell, 0);

			Cell cell = mCells[row][col];
			setCellValue(cell, 1);

			Log.v("Board", mGame.getBoard().toString());
			makeGameMove(result);

			invalidate();
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
				if (mGame.getBoard().getMatrix()[r][c] != null)
					cell.setValue(1);

				mCells[r][c] = cell;
			}
		}
	}

	/**
	 * Registers callback which will be invoked when a cell is selected.
	 * 
	 * @param l
	 */
	public void setOnColumnSelectedListener(OnCellSelectedListener l) {
		mOnCellSelectedListener = l;
	}

	protected void onCellSelected(Cell cell) {
		if (mOnCellSelectedListener != null) {
			mOnCellSelectedListener.onCellSelected(cell);
			postInvalidate();
		}
	}

	/**
	 * Listener for cell selection.
	 * 
	 * @author denniscui
	 * 
	 */
	public interface OnCellSelectedListener {
		void onCellSelected(Cell cell);
	}

	/**
	 * Set the cell value.
	 * 
	 * @param cell
	 * @param value
	 */
	public void setCellValue(Cell cell, int value) {
		cell.setValue(value);
	}

	/**
	 * Get the cell at a given (x, y) position.
	 * 
	 * @param x
	 * @param y
	 * @return cell at the given (x, y) position
	 */
	private Cell getCellAtPoint(int x, int y) {
		// take into account padding
		int lx = x - getPaddingLeft();
		int ly = y - getPaddingRight();

		int row = (int) (ly / mCellHeight);
		int col = (int) (lx / mCellWidth);

		return mCells[row][col];
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!mReadOnly) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				Cell selectedCell = getCellAtPoint(x, y);
				if (selectedCell != null)
					Log.v("TouchPosition", "(" + selectedCell.getRow() + ", "
							+ selectedCell.getCol() + ")");
				int index = mAvailableCells != null ? mAvailableCells
						.indexOf(selectedCell) : -1;
				if (mSelectedCell != null && index != -1) {

					// Remove the piece from the current selected cell
					setCellValue(mSelectedCell, 0);

					// Add the piece to the new cell
					setCellValue(selectedCell, 1);

					// Also need to encode game logic here
					makeGameMove(mAvailableMoves.get(index));

					mSelectedCell = null;
					mAvailableCells = null;
					mAvailableMoves = null;
				} else if (selectedCell != null && selectedCell.isFilled()) {
					Log.v("TouchPosition",
							"Should enter here when making a king.");
					Piece p = mGame.getBoard().getPieceAtPos(
							selectedCell.getRow(), selectedCell.getCol());
					if (p.getPlayer() == player) {
						Log.v("TouchPosition",
								"Should enter here because players are the same.");
						mSelectedCell = selectedCell;
						updateAvailable(p);
					} else
						mSelectedCell = null;
				} else if (selectedCell != null) {
					mSelectedCell = null;
				} else
					mSelectedCell = selectedCell;

				// update the board asap because selected cell changed
				invalidate();

				if (mSelectedCell != null)
					onCellSelected(mSelectedCell);
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
	public void makeGameMove(Move move) {
		ArrayList<Piece> affected = move.getAffected();

		for (Piece p : affected) {
			int pX = p.getPosition().getX();
			int pY = p.getPosition().getY();

			mCells[pX][pY].setValue(0);
		}

		mGame.addMove(move);

		// if (mGame.isRestricted() && !mReadOnly) {
		// Piece restricted = mGame.getRestrictedPiece();
		// mSelectedCell = mCells[restricted.getPosition().getX()][restricted
		// .getPosition().getY()];
		// updateAvailable(restricted);
		// invalidate();
		// }

		// Debug
		Log.v("Board state", mGame.getBoard().toString());
	}

	/**
	 * Updates the available moves for a given piece. The piece should be
	 * selected.
	 * 
	 * @param p
	 */
	private void updateAvailable(Piece p) {
		if (mAvailableMoves == null)
			mAvailableMoves = new ArrayList<Move>();
		else
			mAvailableMoves.clear();

		// Avoid cheating, only pick from available moves
		for (Move m : mAllAvailableMoves) {
			Point start = m.getStart();
			Piece piece = m.getPiece();

			// If the move is a promote move
			if (m.isPromoted()) {
				// Compare the current piece to the demoted piece
				if (start.equals(p.getPosition())
						&& demote(piece).equals(p.getType())
						&& piece.getPlayer() == p.getPlayer()) {
					mAvailableMoves.add(m);
				}
			} else if (start.equals(p.getPosition())
					&& piece.getType().equals(p.getType())
					&& piece.getPlayer() == p.getPlayer()) {
				mAvailableMoves.add(m);
			}
		}

		// If there are no available moves from that point, deselect it
		if (mAvailableMoves.size() == 0) {
			mSelectedCell = null;
			mAvailableMoves = null;
			mAvailableCells = null;
			return;
		}

		// Pull the cells from the available moves
		if (mAvailableCells == null)
			mAvailableCells = new ArrayList<Cell>();
		else
			mAvailableCells.clear();

		for (Move m : mAvailableMoves) {
			int r = m.getEnd().getX();
			int c = m.getEnd().getY();

			mAvailableCells.add(mCells[r][c]);
		}
	}

	/**
	 * Demotes a piece.
	 * 
	 * @param piece
	 * @return the demoted type
	 */
	private String demote(Piece piece) {
		if (piece.getType().equals("r") || piece.getType().equals("b"))
			return piece.getType();

		return piece.getType().equals("R") ? "r" : "b";
	}

	// ----------------- Actual drawing done below -------------------

	// Sets the images to reduce overhead
	protected void setImages(float width, float height, int w, int h) {

		// Generate the wooden BG
		woodenBG = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.wood),
				w, h, true);

		ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();

		Bitmap r = BitmapFactory.decodeResource(getResources(), R.drawable.c_r);
		Bitmap resizedr = Bitmap.createScaledBitmap(r, (int) width,
				(int) height, true);

		Bitmap rk = BitmapFactory.decodeResource(getResources(),
				R.drawable.c_rk);
		Bitmap resizedrk = Bitmap.createScaledBitmap(rk, (int) width,
				(int) height, true);

		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.c_b);
		Bitmap resizedb = Bitmap.createScaledBitmap(b, (int) width,
				(int) height, true);

		Bitmap bk = BitmapFactory.decodeResource(getResources(),
				R.drawable.c_bk);

		Bitmap resizedbk = Bitmap.createScaledBitmap(bk, (int) width,
				(int) height, true);

		bmps.add(resizedr);
		bmps.add(resizedrk);
		bmps.add(resizedb);
		bmps.add(resizedbk);

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
		setImages(mCellWidth, mCellHeight, width, height);

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
			canvas.drawBitmap(woodenBG, 0, 0, mBackgroundColorSecondary);
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
					if ((r + c) % 2 == 0 && hasBackgroundColorReadOnly) {
						canvas.drawRect(cellLeft, cellTop, cellLeft
								+ mCellWidth, cellTop + mCellHeight,
								mBackgroundColorReadOnly);
					}

					// draw cell image
					int value = cell.getValue();

					if (value != 0) {
						Piece p = mGame.getBoard().getPieceAtPos(r, c);

						if (p == null)
							Log.v("POSITION", "Row: " + r + " Col: " + c);
						if (p.getType().equals("r"))
							canvas.drawBitmap(images.get(0), cellLeft, cellTop,
									mCellValuePaint);
						else if (p.getType().equals("R"))
							canvas.drawBitmap(images.get(1), cellLeft, cellTop,
									mCellValuePaint);
						else if (p.getType().equals("b"))
							canvas.drawBitmap(images.get(2), cellLeft, cellTop,
									mCellValuePaint);
						else
							canvas.drawBitmap(images.get(3), cellLeft, cellTop,
									mCellValuePaint);

					}
				}
			}

			// highlight selected cell
			if (!mReadOnly && mSelectedCell != null) {
				cellLeft = Math.round(mSelectedCell.getCol() * mCellWidth)
						+ paddingLeft;
				cellTop = Math.round(mSelectedCell.getRow() * mCellHeight)
						+ paddingTop;

				canvas.drawRect(cellLeft, cellTop, cellLeft + mCellWidth,
						cellTop + mCellHeight, mBackgroundColorSelected);
			}

			// highlight possible moves
			if (!mReadOnly && mSelectedCell != null) {
				for (Cell cell : mAvailableCells) {
					cellLeft = Math.round(cell.getCol() * mCellWidth)
							+ paddingLeft;
					cellTop = Math.round(cell.getRow() * mCellHeight)
							+ paddingTop;

					canvas.drawRect(cellLeft, cellTop, cellLeft + mCellWidth,
							cellTop + mCellHeight, mPossibleMovesPaint);
				}
			}

			// draw vertical lines
			for (int c = 0; c < cols; c++) {
				float x = (c * mCellWidth) + paddingLeft;
				canvas.drawLine(x, paddingTop, x, height, mLinePaint);
			}

			// draw horizontal lines
			for (int r = 0; r < rows; r++) {
				float y = (r * mCellHeight) + paddingTop;
				canvas.drawLine(paddingLeft, y, width, y, mLinePaint);
			}
		}
	}
}
