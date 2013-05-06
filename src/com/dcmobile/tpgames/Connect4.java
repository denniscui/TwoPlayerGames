package com.dcmobile.tpgames;

import ui_views.Connect4BoardView;
import ui_views.TTTBoardView;
import android.app.Activity;
import android.os.Bundle;

public class Connect4 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Connect4BoardView v = new Connect4BoardView(this, null, 0);
		setContentView(v);

	}
}
