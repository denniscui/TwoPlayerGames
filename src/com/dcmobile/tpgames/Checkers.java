package com.dcmobile.tpgames;

import ui_views.CheckersBoardView;
import ui_views.Connect4BoardView;
import android.app.Activity;
import android.os.Bundle;

public class Checkers extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckersBoardView v = new CheckersBoardView(this, null, 0);
		setContentView(v);

	}
}
