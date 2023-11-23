package com.trapta.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import com.trapta.app.R;
import android.widget.Button;

public class ArrowView implements BlinkListener {
	
	private static final String ARROW_LABEL = "Flèche ";
	
	private Button arrowButton;
	private Arrow arrow; 
	private Button valueButton;
	private Blinker blinker;
	private Activity activity; 
	
	public ArrowView(Activity activity, Button arrowButton, Button button, Arrow arrow) {
		this.arrowButton = arrowButton;
		this.arrow = arrow;
		this.valueButton = button;
		this.activity = activity; 
		updateView();
	}
	
	
	public void updateView() {
		valueButton.setVisibility(View.VISIBLE);
		valueButton.setText(arrow.toString());
		valueButton.setTextColor(Color.BLACK);
		if (StaticParam.colorInverted) {
			valueButton.setBackgroundResource(R.drawable.buttona_inverted);
			valueButton.setTextColor(Color.BLACK);
		}
		else switch (arrow.getCode()) {
		
			case 0:
			case 1:
			case 2:
				valueButton.setBackgroundResource(R.drawable.drawbuttonwhite);
				break;
			case 3:
			case 4:
				valueButton.setTextColor(Color.WHITE);
				valueButton.setBackgroundResource(R.drawable.drawbuttonblack);
				break;
			case 5:
			case 6:
				valueButton.setBackgroundResource(R.drawable.drawbuttonblue);
				break;
			case 7:
			case 8:
				valueButton.setTextColor(Color.WHITE);
				valueButton.setBackgroundResource(R.drawable.drawbuttonred);
				break;
			case 9:
			case 10:
			case 11:
				valueButton.setBackgroundResource(R.drawable.drawbuttonyellow);
				break;
		
			default:
				
				if (StaticParam.colorInverted) {
					valueButton.setBackgroundResource(R.drawable.drawbuttonvoid_inverted);
					valueButton.setTextColor(Color.BLACK);
				}
				else {
					valueButton.setBackgroundResource(R.drawable.drawbuttonvoid);
					valueButton.setTextColor(Color.WHITE);
				}
				break;
		}
		
		
	}
	
	
	public boolean isDefined() {
		return arrow.isDefined();
	}
	
	public void setCode(int code) {
		arrow.setCode(code);
		updateView();
	}

	public Arrow getArrow() {
		return arrow;
	} 
	
	public Button getArrowButton() {
		return arrowButton;
	}
	
	public Button getValueButton() {
		return valueButton;
	}

	public void setChecked(boolean value, boolean blink) {
	
		if (value) {
			if (StaticParam.colorInverted) {
				arrowButton.setBackgroundResource(R.drawable.targetletter_inverted);
				arrowButton.setTextColor(Color.WHITE);
			}
			else {
				arrowButton.setBackgroundResource(R.drawable.targetletter);
				arrowButton.setTextColor(Color.WHITE);
			}
			blinker = new Blinker(this, null, 0);
			if (blink) blinker.start(200, 2, 1);
			valueButton.setEnabled(false);
		}
		else {
			valueButton.setEnabled(true);
			if (blinker!=null) blinker.stop();
			updateView();
			if (StaticParam.colorInverted) {
				arrowButton.setBackgroundColor(Color.WHITE);
				arrowButton.setTextColor(Color.BLACK);
			}
			else {
				arrowButton.setBackgroundColor(Color.BLACK);
				arrowButton.setTextColor(Color.WHITE);
			}
			
		}
	}
	
	@Override
	public void blinkOn(String id, int index) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				valueButton.setVisibility(View.VISIBLE);
				
			}
		});
		
	}

	@Override
	public void blinkOff(String id, int index) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				valueButton.setVisibility(View.INVISIBLE);
				
			}
		});
		
	}
	
}
