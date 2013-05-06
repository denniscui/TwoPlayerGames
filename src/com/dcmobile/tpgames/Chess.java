package com.dcmobile.tpgames;

import ui_views.ChessBoardView;
import android.app.Activity;
import android.os.Bundle;

public class Chess extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ChessBoardView v = new ChessBoardView(this, null, 0);
		setContentView(v);

	}
}
