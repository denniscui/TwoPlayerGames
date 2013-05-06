package com.dcmobile.tpgames;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SplashActivity extends Activity {

	private Animation fade_out;

	private View splashView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Get the layout inflator
		LayoutInflater inflator = SplashActivity.this.getLayoutInflater();

		// Inflate the games view
		splashView = inflator.inflate(R.layout.activity_splash, null);

		// Set the fade out animation
		fade_out = AnimationUtils.loadAnimation(SplashActivity.this,
				android.R.anim.fade_out);

		// Set it as the new view
		setContentView(splashView);

		Handler handler = new Handler();
		handler.postDelayed(r, 5000);
	}

	/**
	 * Lets the splash load for 5 seconds.
	 */
	private Runnable r = new Runnable() {

		@Override
		public void run() {
			// Make the old view fade out
			splashView.startAnimation(fade_out);

			// Get the layout inflator
			LayoutInflater inflator = SplashActivity.this.getLayoutInflater();

			// Inflate the games view
			View gameView = inflator.inflate(R.layout.activity_games, null);

			// Start the fade in animation
			gameView.startAnimation(AnimationUtils.loadAnimation(
					SplashActivity.this, android.R.anim.fade_in));

			// Set it as the new view
			setContentView(gameView);

		}

	};

	/**
	 * Loads the game of choice.
	 * 
	 * @param v
	 */
	public void loadGame(View v) {
		switch (v.getId()) {
		case R.id.ttt:
			Intent ttt = new Intent(SplashActivity.this, TicTacToe.class);
			startActivity(ttt);
			break;
		case R.id.connect4:
			Intent c4 = new Intent(SplashActivity.this, Connect4.class);
			startActivity(c4);
			break;
		case R.id.checkers:
			Intent c = new Intent(SplashActivity.this, Checkers.class);
			startActivity(c);
			break;
		case R.id.chess:
			Intent chess = new Intent(SplashActivity.this, Chess.class);
			startActivity(chess);
			break;
		}
	}
}
