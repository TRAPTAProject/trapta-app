package com.traps.trapta;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class WelcomeActivity extends Activity implements OnItemSelectedListener, OnClickListener {


	private boolean guide = true;

	private TargetInfo[] targetInfoArray;
	private Spinner spinner;
	private TextView[] letterTextView = new TextView[4];
	private TextView[] nameTextView = new TextView[4];
	private ImageView[] trispotImage = new ImageView[4];
	private CheckBox guideBox;
	private boolean match;
	
	private boolean standAloneMode = false;
    private SharedPreferences pref;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		getWindow().setFlags(          
				WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);   
		getActionBar().hide();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		LinearLayout rootLayout = findViewById(R.id.LinearLayoutWelcome);

		letterTextView[0] = findViewById(R.id.letterView1);
		letterTextView[1] = findViewById(R.id.letterView2);
		letterTextView[2] = findViewById(R.id.letterView3);
		letterTextView[3] = findViewById(R.id.letterView4);
				
		nameTextView[0] = findViewById(R.id.nameView1);
		nameTextView[1] = findViewById(R.id.nameView2);
		nameTextView[2] = findViewById(R.id.nameView3);
		nameTextView[3] = findViewById(R.id.nameView4);
		
		trispotImage[0] = findViewById(R.id.imageView1);
		trispotImage[1] = findViewById(R.id.imageView2);
		trispotImage[2] = findViewById(R.id.imageView3);
		trispotImage[3] = findViewById(R.id.imageView4);

        pref = getSharedPreferences("TRAPTA_PREF",0);
        guide = pref.getBoolean("guide", true);

		guideBox = findViewById(R.id.guideBox1);
		guideBox.setChecked(guide);
		
		TextView label = findViewById(R.id.textMatch);
		label.setVisibility(View.GONE);
		
		Button startButton = findViewById(R.id.button1select);
		startButton.setOnClickListener(this);

		spinner = findViewById(R.id.spinnerTarget);
		spinner.setOnItemSelectedListener(this);
		ArrayAdapter<String> arrayAdapter;

		if (StaticParam.colorInverted) {
			startButton.setBackgroundResource(R.drawable.buttonb_inverted);
			startButton.setTextColor(Color.BLACK);
			guideBox.setTextColor(Color.BLACK);
			rootLayout.setBackgroundColor(Color.WHITE);
			for (int i=0; i<4; i++) {
				nameTextView[i].setTextColor(Color.BLACK);
				letterTextView[i].setBackgroundResource(R.drawable.targetletter_inverted);
			}
			spinner.setBackgroundResource(R.drawable.buttonb_inverted);
			arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinnerlayout_inverted);
		}
		else {
			rootLayout.setBackgroundColor(Color.BLACK);
			startButton.setBackgroundResource(R.drawable.buttonb);
			startButton.setTextColor(Color.WHITE);
			guideBox.setTextColor(Color.WHITE);
			for (int i=0; i<4; i++) {
				nameTextView[i].setTextColor(Color.WHITE);
				letterTextView[i].setBackgroundResource(R.drawable.targetletter);
			}
			spinner.setBackgroundResource(R.drawable.buttonb);
			arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinnerlayout);
		}
		spinner.setAdapter(arrayAdapter);
		match = getIntent().getExtras().getBoolean("match", false);
		String targetList = getIntent().getExtras().getString("targetList");
		if ((targetList==null) || (targetList.isEmpty())) {
			standAloneMode = true;
			targetInfoArray = StaticParam.standAloneTargetInfo;
			arrayAdapter.add("CIBLE 1");
			arrayAdapter.add("CIBLE 2");
			arrayAdapter.add("CIBLE 3");
			StaticParam.heatVolleyCount = 10;
			StaticParam.arrowCount = 3;
			StaticParam.x10 = false;
			 QuestionDialog dialog = new QuestionDialog(this, 
					 "Combien de flèches par volée ?", 
					 "3", 
					 "6");
			 dialog.setOnButtonClickListener(new QuestionButtonListener() {
				
				@Override
				public void onButton2Click() {
					StaticParam.arrowCount = 6;
					StaticParam.heatVolleyCount = 6;
					StaticParam.x10 = true;
				
				}
				
				@Override
				public void onButton1Click() {
					StaticParam.arrowCount = 3;
					StaticParam.heatVolleyCount = 10;
					StaticParam.x10 = false;

					
				}
			});
			dialog.show();
			return;
		}
		JSONObject jsonList = new JSONObject();
		try {
			jsonList = new JSONObject(targetList);
		 
			if (match) initForMatch(jsonList, arrayAdapter); 
			else initForQualif(jsonList, arrayAdapter);
			
		
		} catch (JSONException e) {

			 QuestionDialog dialog = new QuestionDialog(this, 
					 "Erreur lors de la récupération des matches. Vérifiez que tous les matches ont des archers attribués.", 
					 "OK", 
					 null);
			 dialog.setOnButtonClickListener(new QuestionButtonListener() {
				 
				@Override
				public void onButton2Click() {
				
				}
				
				@Override
				public void onButton1Click() {
					finish();					
				}
			});
			dialog.show();
			
		} 
		
		 
	}
	
	
	
	private void initForMatch(JSONObject jsonList, ArrayAdapter<String> arrayAdapter) throws JSONException {
		TextView textView = findViewById(R.id.textMatch);
		textView.setText("MATCH");
		textView.setVisibility(View.VISIBLE);
		guideBox.setVisibility(View.GONE);
		JSONArray jsonTargetArray = jsonList.names();
        int targetCount = jsonTargetArray.length();

        targetInfoArray = new TargetInfo[targetCount];
		for (int index=0; index<targetCount; index++) {
			String targetId = jsonTargetArray.getString(index);
			int id = Integer.parseInt(targetId);
			TargetInfo targetInfo = new TargetInfo(id);
			JSONObject jsonMatch =  jsonList.getJSONObject(targetId);
			JSONArray jsonArray = jsonMatch.getJSONArray("archerList");
			int maxArcher = jsonArray.length();
			if (maxArcher>2) maxArcher =2;
			for (int counter=0; counter<maxArcher; counter++) {
				JSONObject jsonArcher = jsonArray.getJSONObject(counter);
				targetInfo.addArcher((char)('A'+counter), jsonArcher.getString("name"), jsonArcher.getBoolean("trispot"));
			}

			targetInfoArray[index] = targetInfo;
		}
		Arrays.sort(targetInfoArray);
		for (int index=0; index<targetInfoArray.length; index++) {
			arrayAdapter.add("CIBLE "+targetInfoArray[index].getId());
		}
	}
	
	private void initForQualif(JSONObject jsonList, ArrayAdapter<String> arrayAdapter) throws JSONException {
		JSONArray jsonTargetArray = jsonList.names();
        int targetCount = jsonTargetArray.length();

        targetInfoArray = new TargetInfo[targetCount];
		for (int index=0; index<targetCount; index++) {
			String targetId = jsonTargetArray.getString(index);
			int id = Integer.parseInt(targetId);
			TargetInfo targetInfo = new TargetInfo(id);
			JSONArray jsonArcherArray =  jsonList.getJSONArray(targetId);
			int maxArcher = jsonArcherArray.length();
			if (maxArcher>4) maxArcher =4;
			for (int counter=0; counter<maxArcher; counter++) {
				JSONObject jsonArcher = jsonArcherArray.getJSONObject(counter);
				targetInfo.addArcher((char)jsonArcher.getInt("letter"), jsonArcher.getString("name"), jsonArcher.getBoolean("trispot"));
			}
			//Arrays.sort(targetInfo.archerArray);
			targetInfoArray[index] = targetInfo;
		}
		Arrays.sort(targetInfoArray);
		for (int index=0; index<targetInfoArray.length; index++) {
			arrayAdapter.add("CIBLE "+targetInfoArray[index].getId());
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		
		TargetInfo targetInfo = targetInfoArray[position];
		for (int index=0; index<4; index++) {
			if (index<targetInfo.getArcherCount()) {
				nameTextView[index].setText(targetInfo.getName(index));
				nameTextView[index].setVisibility(View.VISIBLE);
				letterTextView[index].setText("  "+targetInfo.getLetter(index)+"  ");
				letterTextView[index].setVisibility(View.VISIBLE);
				trispotImage[index].setVisibility(targetInfo.isTrispot(index)?View.VISIBLE:View.INVISIBLE);
			}
			else {
				nameTextView[index].setVisibility(View.GONE);
				letterTextView[index].setVisibility(View.GONE);
				trispotImage[index].setVisibility(View.GONE);
			}
		}
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		
		guide = guideBox.isChecked();
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("guide", guide);
        editor.commit();
        Intent data = new Intent();
		data.putExtra("standAloneMode", standAloneMode);
		if (match) {
			data.putExtra("match", true);
			guide = false;
		}
		else data.putExtra("match", false);
		data.putExtra("guide", guide);
		int targetId = targetInfoArray[spinner.getSelectedItemPosition()].getId();
		data.putExtra("targetId", targetId);
		setResult(RESULT_OK, data);
		finish();
		
	}

	
}
