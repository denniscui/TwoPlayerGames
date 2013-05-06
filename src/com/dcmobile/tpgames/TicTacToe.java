package com.dcmobile.tpgames;

import ui_views.TTTBoardView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TicTacToe extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TTTBoardView v = new TTTBoardView(this, null, 0);
		setContentView(v);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tic_tac_toe, menu);
		return true;
	}

}
