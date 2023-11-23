package com.trapta.app;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestionDialog implements OnClickListener {

	private Dialog dialog;
	private Button button1;
	private Button button2;
	private QuestionButtonClick listener = null;
	
	public QuestionDialog(Context context, String questionLabel, String button1Label, String button2Label) {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.question_layout);
		dialog.setTitle("Title");
		dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		LinearLayout layout = dialog.findViewById(R.id.askLayout);
		TextView labelView = dialog.findViewById(R.id.askLabel);
		labelView.setText(questionLabel);
		button1 = dialog.findViewById(R.id.askbutton1);
		button1.setOnClickListener(this);
		button2 = dialog.findViewById(R.id.askbutton2);
		button2.setOnClickListener(this);
	
		if (button1Label!=null) button1.setText(button1Label);
		if (button2Label!=null) button2.setText(button2Label);
		
		if (button1Label==null) button1.setVisibility(View.GONE);
		if (button2Label==null) button2.setVisibility(View.GONE);

		
		if (StaticParam.colorInverted) {
			layout.setBackgroundColor(Color.WHITE);
			labelView.setTextColor(Color.BLACK);
			button1.setBackgroundResource(R.drawable.buttonb_inverted);
			button2.setBackgroundResource(R.drawable.buttonb_inverted);
			button1.setTextColor(Color.BLACK);
			button2.setTextColor(Color.BLACK);
			
		}
		else {
			layout.setBackgroundColor(Color.BLACK);
			labelView.setTextColor(Color.WHITE);
			button1.setBackgroundResource(R.drawable.buttonb);
			button2.setBackgroundResource(R.drawable.buttonb);
			button1.setTextColor(Color.WHITE);
			button2.setTextColor(Color.WHITE);

		}
		
	}

	public void show() {
		dialog.show();
	}
	
	public void setOnButtonClickListener(QuestionButtonClick listener) {
		this.listener = listener;
	}
	
	@Override
	public void onClick(View arg0) {
		dialog.dismiss();
		if (listener==null) return;
		if  (arg0==button1) listener.onButton1Click();
		if  (arg0==button2) listener.onButton2Click();
		
	}
	
}
