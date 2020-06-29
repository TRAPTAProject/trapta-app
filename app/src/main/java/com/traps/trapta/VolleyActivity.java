package com.traps.trapta;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VolleyActivity extends Activity implements OnClickListener, BlinkListener {

	 
	static final private String VOLLEY_LABEL = "Volée ";
	static final private String LABEL_ARROW = "  Flèche ";
	static final private int MAX_ARROW = 6;
	
	static final private float COLUMN1_WIDTH = 0.7f;
	static final private float SIZE_OK_BUTTON = 0.06f;
	static final private float SIZE_ABORT_BUTTON = 0.05f;
	static final private float SIZE_MARGIN = 0.07f;
	
	static final private float SIZE_VALUE_BUTTON= 0.07f;
	static final private float SIZE_ARROW_BUTTON= 0.06f;
	static final private float SIZE_LABEL= 0.07f;
	
	static private boolean playSound = true;
	
	//private ArrayList<Arrow> 
	private Button[] valueButtonArray = new Button[12];
	private int selectedArrow = 0; 
	private Button okButton;
	private ArrayList<ArrowView> arrowList = new ArrayList<ArrowView>();
	private Blinker okButtonBlinker = null;;
	private String name;
	private int archerId;
	private boolean trispot;
	private int volleyIndex;
    private boolean modifyingMode = false;  
    
    private SoundPool soundPool;
    private int explanation2;
    private int explanation1;

	private RelativeLayout rootLayout;
	private ImageView handImage1;
	private RelativeLayout.LayoutParams handLayout1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("VolleyActivity", "onCreate");
		
		rootLayout = new RelativeLayout(this);
		getLayoutInflater().inflate(R.layout.activity_volley, rootLayout);
		
		handImage1 = new ImageView(this);
		handImage1.setImageResource(R.drawable.handright);
		handLayout1 = new RelativeLayout.LayoutParams(StaticParam.screenWidth/4, (int)(0.87*(StaticParam.screenWidth/4)));
		handLayout1.leftMargin = StaticParam.screenWidth-(handLayout1.width*2);
		handLayout1.topMargin = StaticParam.screenHeight-handLayout1.height;
		handImage1.setLayoutParams(handLayout1);
		rootLayout.addView(handImage1);
		handImage1.setVisibility(View.INVISIBLE);
		
		setContentView(rootLayout);      
		getWindow().setFlags(          
				WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);   
		getActionBar().hide();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Button abortButton = (Button)findViewById(R.id.abortButton);
		if (StaticParam.colorInverted) {
			abortButton.setBackgroundResource(R.drawable.buttonb_inverted);
			abortButton.setTextColor(Color.BLACK);
		}
		else {
			abortButton.setBackgroundResource(R.drawable.buttona);
			abortButton.setTextColor(Color.WHITE); 
		}
		abortButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
		});
		 
		LinearLayout mainVerticalLayout = (LinearLayout)findViewById(R.id.mainVerticalLayout);
		LayoutParams params = new LayoutParams((int)(StaticParam.screenWidth*COLUMN1_WIDTH), LayoutParams.MATCH_PARENT); 
		params.setMargins(0, 0, (int)(StaticParam.screenWidth*SIZE_MARGIN), 0);
		mainVerticalLayout.setLayoutParams(params);
		if (StaticParam.colorInverted) {
			LinearLayout layout = (LinearLayout)findViewById(R.id.LinearLayout10);
			layout.setBackgroundColor(Color.WHITE);
		}
		
		archerId = getIntent().getExtras().getInt("id");
		name = getIntent().getExtras().getString("name");
		trispot = getIntent().getExtras().getBoolean("trispot");
		if (name!=null) {
			TextView nameView = (TextView)findViewById(R.id.volleyName);
			if (StaticParam.colorInverted) nameView.setTextColor(Color.BLACK); else nameView.setTextColor(Color.WHITE);
			nameView.setText(name);
		}
		volleyIndex = getIntent().getExtras().getInt("volleyIndex");
		TextView volleyView = (TextView)findViewById(R.id.volleyId);
		if (StaticParam.colorInverted) volleyView.setTextColor(Color.BLACK); else volleyView.setTextColor(Color.WHITE);
		volleyView.setText(VOLLEY_LABEL+(volleyIndex+1));
		
		valueButtonArray[0] = (Button)findViewById(R.id.buttonM);
		valueButtonArray[1] = (Button)findViewById(R.id.okButtonSettings);
		valueButtonArray[2] = (Button)findViewById(R.id.button2);
		valueButtonArray[3] = (Button)findViewById(R.id.button3);
		valueButtonArray[4] = (Button)findViewById(R.id.button4);
		valueButtonArray[5] = (Button)findViewById(R.id.button5);
		valueButtonArray[6] = (Button)findViewById(R.id.button6);
		valueButtonArray[7] = (Button)findViewById(R.id.button7);
		valueButtonArray[8] = (Button)findViewById(R.id.button8);
		valueButtonArray[9] = (Button)findViewById(R.id.button9);
		valueButtonArray[10] = (Button)findViewById(R.id.button10);
		valueButtonArray[11] = (Button)findViewById(R.id.button11);

		 
		for (int i=0; i<valueButtonArray.length; i++) {
			if (StaticParam.colorInverted) {
				valueButtonArray[i].setBackgroundResource(R.drawable.buttonb_inverted);
				valueButtonArray[i].setTextColor(Color.BLACK);
			}
			if (i==11 && !StaticParam.x10) valueButtonArray[i].setVisibility(View.GONE);
			if (i>0 && i<5 && trispot) valueButtonArray[i].setVisibility(View.GONE);
			if (i==5 && trispot && !StaticParam.x10) valueButtonArray[i].setVisibility(View.GONE);
			valueButtonArray[i].setOnClickListener(this);
		}

		Button[] arrowButtonArray = new Button[MAX_ARROW];
		arrowButtonArray[0] = (Button)findViewById(R.id.arrowLabel1);
		arrowButtonArray[1] = (Button)findViewById(R.id.arrowLabel2);
		arrowButtonArray[2] = (Button)findViewById(R.id.arrowLabel3);
		arrowButtonArray[3] = (Button)findViewById(R.id.arrowLabel4);
		arrowButtonArray[4] = (Button)findViewById(R.id.arrowLabel5);
		arrowButtonArray[5] = (Button)findViewById(R.id.arrowLabel6);
		 
		View[] arrowLayout = new View[MAX_ARROW];
		arrowLayout[0] = findViewById(R.id.arrowLayout1);
		arrowLayout[1] = findViewById(R.id.arrowLayout2);
		arrowLayout[2] = findViewById(R.id.arrowLayout3);
		arrowLayout[3] = findViewById(R.id.arrowLayout4);
		arrowLayout[4] = findViewById(R.id.arrowLayout5);
		arrowLayout[5] = findViewById(R.id.arrowLayout6);
		
		Button[] arrowValue = new Button[MAX_ARROW];
		arrowValue[0] = (Button)findViewById(R.id.arrowValue1);
		arrowValue[1] = (Button)findViewById(R.id.arrowValue2);
		arrowValue[2] = (Button)findViewById(R.id.arrowValue3);
		arrowValue[3] = (Button)findViewById(R.id.arrowValue4);
		arrowValue[4] = (Button)findViewById(R.id.arrowValue5);
		arrowValue[5] = (Button)findViewById(R.id.arrowValue6);
		
		
		int[] codeArray = getIntent().getExtras().getIntArray("codeList");
		if (codeArray!=null) {
			int arrowCount = StaticParam.arrowCount;
			for (int i=0; i<arrowCount; i++) {
				if (codeArray[i]>-1) modifyingMode = true;
				arrowButtonArray[i].setText(LABEL_ARROW+(i+1)+" :   ");
				arrowButtonArray[i].setOnClickListener(this);
				arrowValue[i].setOnClickListener(this);
				if (StaticParam.colorInverted) {
					arrowValue[i].setBackgroundResource(R.drawable.buttonb_inverted);
					arrowValue[i].setTextColor(Color.BLACK);
				}
				ArrowView arrow = new ArrowView(this, arrowButtonArray[i], arrowValue[i], new Arrow(codeArray[i]));
				if (i==0) {
					// if no defined, to not blink
					if (codeArray[i]==-1) arrow.setChecked(true, false);
					// if already define, then blink
					else arrow.setChecked(true, true);
				}
				else arrow.setChecked(false, false); 
				arrowList.add(arrow);
			}
			
		}
		
		
		// hide unused arrow
		
		for (int i=arrowList.size(); i<MAX_ARROW; i++) {
			arrowLayout[i].setVisibility(View.INVISIBLE);
		}
		
		okButton = (Button)findViewById(R.id.okButton);

		okButton.setEnabled(false);
		//_okButtonBlinker = new Blinker(this, "OK_BUTTON", 0);
		okButton.setBackgroundResource(R.drawable.okbuttondisabled);
		okButton.setTextColor(Color.DKGRAY);
		okButton.setOnClickListener(this);
		//_okButtonBlinker.start(250, 1, 4);
		
		if (modifyingMode) {
			okButton.setEnabled(true);
			if (StaticParam.colorInverted) {
				okButton.setTextColor(Color.BLACK);
				okButton.setBackgroundResource(R.drawable.okbuttonlight_inverted);				
			}
			else {
				okButton.setTextColor(Color.BLACK);
				okButton.setBackgroundResource(R.drawable.okbuttonlight);
			}
		}
		
		soundPool = null;
			
		 // init sound
		if (getIntent().getExtras().getBoolean("play")) {
	        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	        if (soundPool!=null) {
	        	explanation1 = soundPool.load(this, R.raw.usesound, 1);
	        	explanation2 = soundPool.load(this, R.raw.validsound, 1);
	        }
			
	        if (volleyIndex==0) {
	        	 soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	        	      @Override
	        	      public void onLoadComplete(SoundPool soundPool, int sampleId,
	        	          int status) {
	        	    	  if ((sampleId==explanation1) && (StaticParam.speakerEnabled)) {
	        	    		  	if (playSound) {
	        	    		  		playSound = false;
	        	    		  		soundPool.play(explanation1, 1, 1, 0, 0, 1);
	        	    		  		
	        	    		  	}
	        	    		  	handImage1.setVisibility(View.VISIBLE);
	        	    		  	TranslateAnimation anim = new TranslateAnimation(0,0,0,-StaticParam.screenHeight+handLayout1.height);
	        	  				anim.setDuration(5000);
	        	  				anim.setAnimationListener(new AnimationListener() {
									public void onAnimationStart(Animation animation) {}
									public void onAnimationRepeat(Animation animation) {}
									public void onAnimationEnd(Animation animation) {
										handImage1.setVisibility(View.INVISIBLE);
									}
								});
	        	  				handImage1.setAnimation(anim);
	        	  				anim.start();
	        	    	  }
	        	    	  
	        	      }
	        	    });        	
	        }
		} 
	}


	public void onBackPressed() {
		if (soundPool!=null) {
			soundPool.stop(explanation1);
			soundPool.stop(explanation2);
		}
		playSound = true;
		finish();
	}

	@Override
	public void onClick(View v) {
		
		
		// check the value buttons
		for (int i=0; i<12; i++) {
			if (v==valueButtonArray[i]) {
				if ((selectedArrow>-1) && (selectedArrow<arrowList.size())) {
					
					// set the code to tha arrow
					ArrowView arrowView = arrowList.get(selectedArrow);
					arrowView.setChecked(false, false);
					arrowView.setCode(i);
					
					// go to the next empty arrow:
					int nextArrow = selectedArrow+1;
					if (nextArrow==arrowList.size()) nextArrow=0;
					while ((nextArrow!=selectedArrow) && (arrowList.get(nextArrow).getArrow().getCode()>-1)) {
						nextArrow++;
						if (nextArrow==arrowList.size()) nextArrow=0;
					}
					// if all arrow have a code 
					if (nextArrow==selectedArrow) {
						
						if ((modifyingMode) && (selectedArrow<arrowList.size()-1)) {
							selectedArrow++;
							arrowView = arrowList.get(selectedArrow);
							arrowView.setChecked(true, true);
						} 
						else {
							selectedArrow=-1;
							okButton.setEnabled(true);
							okButton.setTextColor(Color.WHITE);
							okButtonBlinker = new Blinker(this, "OK_BUTTON", 0);
							okButtonBlinker.start(200, 1, 1);
							if ((volleyIndex==0) && (soundPool!=null) && (StaticParam.speakerEnabled)) {
								soundPool.stop(explanation1);
								soundPool.stop(explanation2);
								soundPool.play(explanation2, 1, 1, 0, 0, 1);
							}
						}
						return;
					
					}
					selectedArrow = nextArrow;
					arrowView = arrowList.get(selectedArrow);
					arrowView.setChecked(true, false);
					
				}
				
				return;
			}
		}
		
		
		// check the OK button
		if (okButton==v) {
			
			Intent data = new Intent();
			int[] array = new int[arrowList.size()];
			int index=0;
			for (ArrowView arrow: arrowList) 
				array[index++] = arrow.getArrow().getCode();
			
			data.putExtra("codeList", array);
			data.putExtra("id", archerId);
			data.putExtra("name", name);
			data.putExtra("volleyIndex", volleyIndex);
			setResult(RESULT_OK, data);
			if ((volleyIndex==0) && (soundPool!=null)) {
				soundPool.stop(explanation1);
				soundPool.stop(explanation2);
			}
			playSound = true;
			finish();
			return;
		}
		
		// if none of the above, then it is the arrow button
		int index = 0;
		for (ArrowView arrowView: arrowList) {
			if (okButtonBlinker!=null) {
				okButtonBlinker.stop();
				okButton.setBackgroundResource(R.drawable.okbuttonlight);
			}
			
			if ((arrowView.getArrowButton()!=v) && (arrowView.getValueButton()!=v)) {
				arrowView.setChecked(false, false);
			}
			else {
				arrowView.setChecked(true, true);
				selectedArrow = index;
			}
			index++;
		}
		
	}


	@Override
	public void blinkOn(String id, int index) {
	
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (StaticParam.colorInverted) {
						okButton.setBackgroundResource(R.drawable.okbuttonlight_inverted);
						okButton.setTextColor(Color.BLACK);
					}
					else {
						okButton.setBackgroundResource(R.drawable.okbuttonlight);
						okButton.setTextColor(Color.BLACK);
						
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
						okButton.setBackgroundResource(R.drawable.okbuttondark_inverted);
						okButton.setTextColor(Color.WHITE);
					}
					else {
						okButton.setBackgroundResource(R.drawable.okbuttondark);
						okButton.setTextColor(Color.WHITE);
					}
				}
			});
		}
	
	
	
}
