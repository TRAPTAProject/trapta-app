package com.trapta.app;

import android.app.AlertDialog;
import android.content.Context;


public class Utility {
 
	public static void alert(Context context, int title, int message) {
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(context.getResources().getString(message))
		.setNeutralButton("OK",null)
		.create()
		.show();
	} 
	
	public static void alert(Context context, String title, String message) {
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setNeutralButton("OK",null)
		.create()
		.show();
	}

}
