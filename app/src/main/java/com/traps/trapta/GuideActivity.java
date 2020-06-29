package com.traps.trapta;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GuideActivity extends Activity implements BlinkListener {

	private Button startButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		SharedPreferences pref = getSharedPreferences("TRAPTA_PREF", 0);
		int targetId = pref.getInt("target", 0);

		startButton = (Button) findViewById(R.id.buttonStartGuide);
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();

			}
		});
		boolean match = getIntent().getExtras().getBoolean("match");

		String title = "CIBLE " + targetId;
		if (match)
			title += " : MATCH";
		TextView titleView = (TextView) findViewById(R.id.titleGuide);
		LinearLayout layout = (LinearLayout) findViewById(R.id.bgStartGuide);
		ImageView image = (ImageView) findViewById(R.id.imageViewGuide);
		if (StaticParam.colorInverted) {
			titleView.setTextColor(Color.BLACK);
			layout.setBackgroundColor(Color.WHITE);
			image.setImageResource(R.drawable.startarrow_inverted);
		} else {
			titleView.setTextColor(Color.WHITE);
			layout.setBackgroundColor(Color.BLACK);
			image.setImageResource(R.drawable.startarrow);
		}
		titleView.setText(title);

		Blinker blinker = new Blinker(this, "START_BUTTON", 0);
		blinker.start(250, 2, 1);
	}

	@Override
	public void blinkOn(String id, int index) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (StaticParam.colorInverted) {
					startButton
							.setBackgroundResource(R.drawable.okbuttonlight_inverted);
					startButton.setTextColor(Color.BLACK);
				} else {
					startButton.setBackgroundResource(R.drawable.okbuttonlight);
					startButton.setTextColor(Color.BLACK);
				}
			}
		});

	}

	@Override
	public void blinkOff(String id, int index) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (StaticParam.colorInverted) {
					startButton
							.setBackgroundResource(R.drawable.okbuttondark_inverted);
					startButton.setTextColor(Color.WHITE);
				} else {
					startButton.setBackgroundResource(R.drawable.okbuttondark);
					startButton.setTextColor(Color.WHITE);
				}
			}
		});
	}

}
