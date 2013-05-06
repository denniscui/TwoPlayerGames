package ui_views;

import java.util.ArrayList;

import models.Game;
import ui_models.Cell;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public abstract class BoardView extends View {
	// the human player identifier
	private int player;

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
	private boolean mShowPossibleMoves = false;

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

	public BoardView(Context context) {
		super(context);
	}

	public BoardView(Context context, AttributeSet attrs, int player) {
		super(context, attrs);

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
		mBackgroundColorSecondary.setColor(Color.WHITE);
		mBackgroundColorSelected.setColor(0xaa3aa6d0);

		// Smooth out the edges
		mLinePaint.setAntiAlias(true);
		mCellValuePaint.setAntiAlias(true);

		initGame();
		initCells();
	}

	/**
	 * Initialize the game based on the type of board.
	 */
	protected abstract void initGame();

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
	public void setOnCellSelectedListener(OnCellSelectedListener l) {
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

				// This logic will not work for checkers/chess
				if (mSelectedCell != null && mSelectedCell.equals(selectedCell)) {
					// Need to think about this, won't work for checkers/chess
					setCellValue(mSelectedCell, 1);

					// Also need to encode game logic here
					makeGameMove(null, mSelectedCell);

					mSelectedCell = null;
				} else if (selectedCell.isFilled())
					mSelectedCell = null;
				else
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
	public abstract void makeGameMove(Cell begin, Cell end);

	// ----------------- Actual drawing done below -------------------

	// Sets the images to reduce overhead
	protected abstract void setImages(float width, float height);

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
			canvas.drawRect(0, 0, 3 * mCellWidth, 3 * mCellWidth,
					mBackgroundColorSecondary);
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
						canvas.drawRect(cellLeft, cellTop, cellLeft
								+ mCellWidth, cellTop + mCellHeight,
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
			if (!mReadOnly && mSelectedCell != null) {
				cellLeft = Math.round(mSelectedCell.getCol() * mCellWidth)
						+ paddingLeft;
				cellTop = Math.round(mSelectedCell.getRow() * mCellHeight)
						+ paddingTop;

				canvas.drawRect(cellLeft, cellTop, cellLeft + mCellWidth,
						cellTop + mCellHeight, mBackgroundColorSelected);
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
