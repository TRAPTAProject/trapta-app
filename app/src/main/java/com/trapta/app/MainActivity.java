package com.trapta.app;

import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

// 37a4ce // android blue

public class MainActivity extends Activity implements OnItemClickListener, Watchdog {

	private final static int CODE_VOLLEY_ACTIVITY = 0;
	private final static int CODE_WELCOME_ACTIVITY = 1;
	private final static int CODE_GUIDE_ACTIVITY = 2;
	private final static int CODE_SETTINGS_ACTIVITY = 3;
	private final static int CODE_MENU_ACTIVITY = 4;

	private final static int HEAT_COUNT = 4;

	private List<Archer> archerList = new ArrayList<Archer>();
	private int currentArcher = 0;
	private int currentHeat = 0;
	private VolleyListAdapter listAdapter;
	private TextView archerNameView;
	private TextView archerInfoView;
	private TextView runView;
	private TextView targetView;
	private TextView scoreMatchText;
	private Button[] archerButton = new Button[4];
	private ListView listView;
	private SoundPool soundPool = null;
	private int audioGuide1;
	private int audioGuide3;
	private boolean audioGuide1Playing = false;
	private boolean audioGuide3Playing = false;
	private Handler visualGuideHandler = new Handler();
	private ImageView networkFlag;
	private Button menuButton;
	private RelativeLayout rootLayout;
	private TextView titleView;
	private View separator1;
	private View separator2;
	private ImageView handImage;
	private LayoutParams handLayout;
	private Context mainContext;
	private ImageView broadcastImage;

	private JSONObject jsonTargetList = null;
	private boolean goneToVolley = false;
	private int latestArcherUpdate = -1;

	private boolean guideEnabled = false;
	private Match match = null;

	private WifiManager wifiManager = null;
	private WifiManager.WifiLock wifiLock = null;
	private VisualGuide1 visualGuide1;
	private VisualGuide3 visualGuide3;

	private SharedPreferences pref;

	private Database database;
	IntentFilter batteryIntentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

	private List<Archer> getDefaultArcherList() {
		List<Archer> archerList = new ArrayList<Archer>();
		archerList.add(new Archer(-1, "123456H", "CHCL", "ARCHER A", 'A', false));
		archerList.add(new Archer(-2, "789951L", "CFCL", "ARCHER B", 'B', false));
		archerList.add(new Archer(-3, "496378J", "CHCL", "ARCHER C", 'C', false));
		archerList.add(new Archer(-4, "713645Y", "CHCL", "ARCHER D", 'D', false));
		return archerList;
	}

	private static String getMacAddr() {
		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (!nif.getName().equalsIgnoreCase("wlan0"))
					continue;

				byte[] macBytes = nif.getHardwareAddress();
				if (macBytes == null) {
					return "";
				}

				StringBuilder res1 = new StringBuilder();
				for (byte b : macBytes) {
					res1.append(Integer.toHexString(b & 0xFF) + ":");
				}

				if (res1.length() > 0) {
					res1.deleteCharAt(res1.length() - 1);
				}
				return res1.toString();
			}
		} catch (Exception ex) {
		}
		return "02:00:00:00:00:00";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mainContext = this;
		rootLayout = new RelativeLayout(this);
		getLayoutInflater().inflate(R.layout.activity_main, rootLayout);

		handImage = new ImageView(this);
		handImage.setImageResource(R.drawable.hand);
		handLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rootLayout.addView(handImage, handLayout);

		setContentView(rootLayout);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		pref = getSharedPreferences("TRAPTA_PREF", 0);
		int targetId = pref.getInt("target", 1);
		StaticParam.targetId = targetId;
		StaticParam.heatVolleyCount = pref.getInt("heatVolleyCount", 10);
		StaticParam.matchVolleyMax = pref.getInt("matchVolleyCount", 5);
		StaticParam.arrowCount = pref.getInt("arrowCount", 3);
		StaticParam.roundCount = pref.getInt("roundCount", 2);
		StaticParam.x10 = pref.getBoolean("x10", false);
		StaticParam.standAloneMode = pref.getBoolean("standAloneMode", false);

		database = new Database(this);
		if (archerList.size() == 0) {
			archerList = database.getArcherList();
			if (archerList.size() == 0) {
				archerList = getDefaultArcherList();
				database.setArcherList(archerList);
			}
		}
		Collections.sort(archerList);
		Point screenSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(screenSize);
		StaticParam.screenWidth = screenSize.x;
		StaticParam.screenHeight = screenSize.y;
		Log.i("Screen height", "" + screenSize.y);
		Log.i("Screen width", "" + screenSize.x);

		float ratio = handImage.getLayoutParams().height / handImage.getLayoutParams().width;
		handImage.getLayoutParams().width = StaticParam.screenWidth / 4;
		handImage.getLayoutParams().height = (int) (handImage.getLayoutParams().width * ratio);
		handImage.setVisibility(View.GONE);

		networkFlag = findViewById(R.id.networkflag);
		broadcastImage = findViewById(R.id.broadcastflag);
		broadcastImage.setVisibility(View.INVISIBLE);

		archerNameView = findViewById(R.id.textName);
		archerInfoView = findViewById(R.id.infoArcher);
		targetView = findViewById(R.id.textTarget);
		runView = findViewById(R.id.textRun);
		runView.setBackgroundColor(Color.GRAY);
		runView.setTextColor(Color.WHITE);

		targetView.setText("CIBLE " + targetId);

		listView = findViewById(R.id.listView);
		listView.setDrawSelectorOnTop(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(this);

		archerButton[0] = findViewById(R.id.buttonA);
		archerButton[1] = findViewById(R.id.buttonB);
		archerButton[2] = findViewById(R.id.buttonC);
		archerButton[3] = findViewById(R.id.buttonD);

		scoreMatchText = findViewById(R.id.scoreMatchText);

		separator1 = findViewById(R.id.separator1);
		separator2 = findViewById(R.id.separator2);
		// menu stuff
		menuButton = findViewById(R.id.menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mainContext, MenuActivity.class);
				// lock, match, hest
				intent.putExtra("match", match != null);
				intent.putExtra("currentHeat", currentHeat);
				startActivityForResult(intent, CODE_MENU_ACTIVITY);
			}
		});

		// check if match
		int matchId = pref.getInt("matchId", -1);
		int matchMode = pref.getInt("matchmode", 0);
		if (matchId > -1 && archerList.size() > 1) {
			if (matchMode == 0)
				match = new MatchSet(matchId, pref.getBoolean("trispot", false), StaticParam.matchVolleyMax,
						archerList.get(0), archerList.get(1));
			else
				match = new MatchPoint(matchId, pref.getBoolean("trispot", false), StaticParam.matchVolleyMax,
						archerList.get(0), archerList.get(1));
			match.setTieBreakWinner(pref.getInt("tieBreakWinner", -1));
			updateListAdapter();
			setCurrentHeat(0);
		} else {
			match = null;
			updateListAdapter();
			setCurrentHeat(pref.getInt("heatIndex", 0));

		}

		configureArcherButton();
		setCurrentArcher(currentArcher);
		int ipAddress = 0;
		wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			WifiInfo info = wifiManager.getConnectionInfo();
			ipAddress = info.getIpAddress();

			// WIFI lock
			if (wifiLock == null)
				wifiLock = wifiManager.createWifiLock("TRAPTAWIFI");
			if (wifiLock != null) {
				wifiLock.setReferenceCounted(false);
				wifiLock.acquire();
			}
		} else {
			QuestionDialog questionDialog = new QuestionDialog(this,
					"Impossible d'accéder à la fonction WIFI de l'appareil. Veuillez activer le WIFI et redémarrer.",
					"OK", null);
			questionDialog.setOnButtonClickListener(new QuestionButtonListener() {

				@Override
				public void onButton2Click() {

				}

				@Override
				public void onButton1Click() {
					finish();

				}
			});
			questionDialog.show();

		}
		if (ipAddress == 0) {
			QuestionDialog questionDialog = new QuestionDialog(this,
					"L'appareil n'est connecté à aucun réseau WIFI. La synchronisation avec le serveur TRAPTA ne pourra se faire que si la connexion est établie.",
					"OK", null);
			questionDialog.show();

		}
		Log.i("UDP", "Starting UPD listener...");
		UDPListener.getInstance().reset();
		UDPListener.getInstance().register(this);
		Log.i("TRAPTA", "Starting TRAPTA Socket...");
		titleView = (TextView) findViewById(R.id.title);

		titleView.setText(BuildConfig.VERSION_NAME);

		StaticParam.colorInverted = pref.getBoolean("colorInverted", false);
		checkColors();
		TRAPTASocket.getInstance().setReference(this, wifiManager);
		showNetworkFlag(false);
		updateMatchScore();

	}

	private void updateListAdapter() {
		if (StaticParam.arrowCount == 6) {
			int layoutId = R.layout.cellview_volley6;
			if (StaticParam.colorInverted)
				layoutId = R.layout.cellview_volley6_inverted;
			if (match == null)
				listAdapter = new Volley6ListAdapter(this, layoutId);
			else
				listAdapter = new Volley6MatchAdapter(this, layoutId, match);
		} else {
			int layoutId = R.layout.cellview_volley3;
			if (StaticParam.colorInverted)
				layoutId = R.layout.cellview_volley3_inverted;
			if (match == null)
				listAdapter = new Volley3ListAdapter(this, layoutId);
			else
				listAdapter = new Volley3MatchAdapter(this, layoutId, match);
		}
		listView.setAdapter(listAdapter);
	}

	private void checkColors() {

		if (StaticParam.colorInverted) {
			archerNameView.setTextColor(Color.BLACK);
			archerInfoView.setTextColor(Color.BLACK);
			LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout1);
			layout.setBackgroundColor(Color.WHITE);
			scoreMatchText.setTextColor(Color.BLACK);
			for (int i = 0; i < 4; i++) {
				archerButton[i].setBackgroundResource(R.drawable.buttona_inverted);
				archerButton[i].setTextColor(Color.BLACK);
			}
			titleView.setTextColor(Color.BLACK);
			titleView.setBackgroundColor(Color.WHITE);
			menuButton.setTextColor(Color.BLACK);
			menuButton.setBackgroundResource(R.drawable.buttonb_inverted);
			separator1.setBackgroundColor(Color.BLACK);
			separator2.setBackgroundColor(Color.BLACK);
			broadcastImage.setImageResource(R.drawable.ic_wifi_black);
		} else {
			archerNameView.setTextColor(Color.WHITE);
			archerInfoView.setTextColor(Color.WHITE);
			LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout1);
			layout.setBackgroundColor(Color.BLACK);
			scoreMatchText.setTextColor(Color.WHITE);
			for (int i = 0; i < 4; i++) {
				archerButton[i].setBackgroundResource(R.drawable.buttona);
				archerButton[i].setTextColor(Color.WHITE);
			}
			titleView.setTextColor(Color.WHITE);
			titleView.setBackgroundColor(Color.BLACK);
			menuButton.setTextColor(Color.WHITE);
			menuButton.setBackgroundResource(R.drawable.buttona);
			separator1.setBackgroundColor(Color.WHITE);
			separator2.setBackgroundColor(Color.WHITE);
			broadcastImage.setImageResource(R.drawable.ic_wifi_white);

		}
		targetView.setTextColor(Color.WHITE);
		if (StaticParam.colorInverted)
			targetView.setTextColor(Color.BLACK);
		updateListAdapter();
		setCurrentHeat(currentHeat);
		setCurrentArcher(0);
	}

	public void showNetworkFlag(boolean value) {
		final boolean showValue = value;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (showValue)
					networkFlag.setVisibility(View.VISIBLE);
				else
					networkFlag.setVisibility(View.INVISIBLE);
			}
		});
	}

	public int getBatteryLevel() {
		Intent batteryStatus = registerReceiver(null, batteryIntentfilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int batt = (level * 100) / scale;
		Log.i("Battery", "" + batt);
		return batt;

	}

	private void setCurrentHeat(int run) {
		if (run < 0 || run >= HEAT_COUNT)
			return;
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("heatIndex", run);
		editor.apply();
		currentHeat = run;
		runView.setVisibility(View.VISIBLE);
		runView.setText("SÉRIE " + (run + 1));
		if (match != null)
			runView.setText("MATCH");

		if (StaticParam.colorInverted) {
			runView.setTextColor(Color.BLACK);
			runView.setBackgroundColor(Color.WHITE);
		} else {
			runView.setTextColor(Color.WHITE);
			runView.setBackgroundColor(Color.BLACK);
		}

		setCurrentArcher(0);
	}

	private void setCurrentArcher(int index) {
		if (index >= archerList.size())
			return;
		currentArcher = index;

		Archer archer = archerList.get(currentArcher);
		List<Volley> volleyList = archer.getHeatList().get(currentHeat).getVolleyList();

		archerNameView.setText(archer.getName());
		archerInfoView.setText("Licence : " + archer.getLicense() + " [ " + archer.getCategory() + " ]");
		listAdapter.updateArcher(archer, currentHeat);

		int buttonIndex = archer.getLetter() - 'A';
		for (int i = 0; i < archerButton.length; i++) {
			if (i == buttonIndex) {
				if (StaticParam.colorInverted)
					archerButton[i].setBackgroundResource(R.drawable.buttonb_inverted);
				else
					archerButton[i].setBackgroundResource(R.drawable.buttonb);
			} else {
				if (StaticParam.colorInverted)
					archerButton[i].setBackgroundResource(R.drawable.buttona_inverted);
				else
					archerButton[i].setBackgroundResource(R.drawable.buttona);
			}
		}

		listView.smoothScrollToPosition(volleyList.size());

	}

	private void processMenuChoice(int index) {

		switch (index) {
			// restart
			case 0: {
				initWelcome();
				break;
			}
			// go to next round (heat)
			case 1: {
				setCurrentHeat(currentHeat + 1);
				break;
			}
			// go back to previous run
			case 2: {
				setCurrentHeat(currentHeat - 1);
				break;
			}
			// send scorecard again
			case 3: {
				// post heat in queue
				if (!StaticParam.standAloneMode) {
					if (match != null)
						TRAPTASocket.getInstance().sendMatch(match.getMatchId(), match);
					else {
						Archer archer = archerList.get(currentArcher);
						TRAPTASocket.getInstance().sendHeat(archer.getId(), currentHeat,
								archer.getHeatList().get(currentHeat));
					}
				}
				break;
			}
			case 4: {
				startWelcome(true);
				break;
			}
			// indoor / outdoor colors
			case 5: {
				StaticParam.colorInverted = !StaticParam.colorInverted;
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean("colorInverted", StaticParam.colorInverted);
				editor.apply();
				checkColors();
				break;
			}

		}
	}

	private void initWelcome() {

		if (UDPListener.getInstance().isInit()) {
			initWelcome2();
		} else {
			(new WaitingForServerTask(this) {
				@Override
				protected void onPostExecute(Boolean value) {
					super.onPostExecute(value);
					Log.i("MainActivity", "Done with Waiting task");
					initWelcome2();
				}
			}).execute();
		}

	}

	private void initWelcome2() {
		if (!UDPListener.getInstance().isInit()) {

			QuestionDialog questionDialog = new QuestionDialog(
					this,
					"L'application serveur TRAPTA n'a pas été détectée. Vérifiez qu'elle est démarrée sur un PC connecté au même réseau que cet appareil. Vérifiez que cet appareil est connecté au réseau WIFI.",
					"OK", null);
			questionDialog.setOnButtonClickListener(new QuestionButtonListener() {

				@Override
				public void onButton1Click() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onButton2Click() {

				}
			});
			questionDialog.show();

		} else
			startWelcome(false);
	}

	// get target list and start welcome activity
	private void startWelcome(boolean standaloneMode) {

		StaticParam.standAloneMode = standaloneMode;

		if (!standaloneMode) {
			jsonTargetList = null;
			// request for list of archer
			JSONObject request = new JSONObject();
			try {
				request.put("command", 1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InetSocketAddress socketAddress = UDPListener.getInstance().getAddress();
			(new JsonRequestTask(this, socketAddress, "Récupération liste des archers") {

				@Override
				protected void onPostExecute(JSONObject jsonObject) {
					super.onPostExecute(jsonObject);
					int response;

					try {

						StaticParam.heatVolleyCount = pref.getInt("heatVolleyCount", 10);
						StaticParam.matchVolleyMax = pref.getInt("matchVolleyCount", 5);
						StaticParam.arrowCount = pref.getInt("arrowCount", 3);
						StaticParam.roundCount = pref.getInt("roundCount", 2);
						StaticParam.x10 = pref.getBoolean("x10", false);

						if (jsonObject.has("heatVolleyCount")) {
							int volleyCount = jsonObject.getInt("heatVolleyCount");
							if (volleyCount > 0 && volleyCount < 11)
								StaticParam.heatVolleyCount = volleyCount;
							SharedPreferences.Editor editor = pref.edit();
							editor.putInt("heatVolleyCount", volleyCount);
							editor.apply();

						}
						if (jsonObject.has("matchVolleyMax")) {
							int matchVolleyCount = jsonObject.getInt("matchVolleyMax");
							if (matchVolleyCount > 0 && matchVolleyCount < 11)
								StaticParam.matchVolleyMax = matchVolleyCount;
							SharedPreferences.Editor editor = pref.edit();
							editor.putInt("matchVolleyCount", matchVolleyCount);
							editor.apply();

						}
						if (jsonObject.has("arrowCount")) {
							int arrowCount = jsonObject.getInt("arrowCount");
							if (arrowCount == 3 || arrowCount == 6)
								StaticParam.arrowCount = arrowCount;
							SharedPreferences.Editor editor = pref.edit();
							editor.putInt("arrowCount", arrowCount);
							editor.apply();

						}
						if (jsonObject.has("roundCount")) {
							int roundCount = jsonObject.getInt("roundCount");
							if (roundCount == 2 || roundCount == 4)
								StaticParam.roundCount = roundCount;
							SharedPreferences.Editor editor = pref.edit();
							editor.putInt("roundCount", roundCount);
							editor.apply();

						}
						if (jsonObject.has("x10")) {
							StaticParam.x10 = jsonObject.getBoolean("x10");
							SharedPreferences.Editor editor = pref.edit();
							editor.putBoolean("x10", StaticParam.x10);
							editor.apply();

						}

						response = jsonObject.getInt("response");
						// wrong start on TRAPTA side
						if (response == -1) {
							QuestionDialog dialog = new QuestionDialog(mainContext,
									"ERREUR: Sélectionnez un départ dans le serveur TRAPTA",
									"OK",
									null);
							dialog.show();

							return;
						}
						if (response == -2) {
							QuestionDialog dialog = new QuestionDialog(mainContext,
									"ERREUR: Une cible n'est pas définie dans le serveur TRAPTA",
									"OK",
									null);
							dialog.show();
							return;
						}
						if (response == -3) {
							QuestionDialog dialog = new QuestionDialog(mainContext,
									"ERREUR: Erreur -3 retournée par TRAPTA",
									"OK",
									null);
							dialog.show();

							return;
						}

						if ((response != 0 && response != 1) || jsonObject.isNull("data")) {
							throw new JSONException("JSON Object invalid");
						} else {
							jsonTargetList = jsonObject.getJSONObject("data");
							Intent intent = new Intent(mainContext, WelcomeActivity.class);
							intent.putExtra("targetList", jsonTargetList.toString());
							if (response == 1)
								intent.putExtra("match", true);
							else
								intent.putExtra("match", false);
							startActivityForResult(intent, CODE_WELCOME_ACTIVITY);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						QuestionDialog dialog = new QuestionDialog(mainContext,
								"Impossible de récupérer la liste des archers, le serveur TRAPTA est injoignable ou a retourné une erreur.",
								"OK",
								null);
						dialog.setOnButtonClickListener(new QuestionButtonListener() {

							@Override
							public void onButton1Click() {
								// TODO Auto-generated method stub

							}

							@Override
							public void onButton2Click() {

							}
						});
						dialog.show();
					}
				}

			}).execute(request);
		} else {
			Intent intent = new Intent(mainContext, WelcomeActivity.class);
			intent.putExtra("archerList", "");
			startActivityForResult(intent, CODE_WELCOME_ACTIVITY);
		}

	}

	private void initMatchWithResults(JSONObject jsonMatchResult) {
		try {
			int tieBreakWinner = jsonMatchResult.getInt("tieBreakWinner");
			match.setTieBreakWinner(jsonMatchResult.getInt("tieBreakWinner"));
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt("tieBreakWinner", tieBreakWinner);
			editor.apply();
			JSONArray jsonArray = jsonMatchResult.getJSONArray("arrowList");
			if (jsonArray == null)
				return;
			for (int index = 0; index < 2; index++) {
				archerList.get(index).setJsonMatchArrow(jsonArray.getJSONArray(index));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		database.setArcherList(archerList);
		updateListAdapter();
		currentHeat = 0;
		setCurrentHeat(0);
		updateMatchScore();
	}

	private void initArcherListWithResults(JSONObject jsonArrowList) {

		for (Archer archer : archerList) {
			try {
				JSONArray array = jsonArrowList.getJSONArray("" + archer.getId());
				if (array != null)
					archer.setJsonArrowList(array);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		database.setArcherList(archerList);
		latestArcherUpdate = -1;
		updateListAdapter();
		setCurrentHeat(0);
		if (guideEnabled)
			startGuideActivity();

	}

	private void configureArcherButton() {

		if (match == null)
			scoreMatchText.setVisibility(View.GONE);
		else
			scoreMatchText.setVisibility(View.VISIBLE);
		for (int index = 0; index < 4; index++)
			archerButton[index].setVisibility(View.GONE);

		for (int index = 0; index < archerList.size(); index++) {
			Archer archer = archerList.get(index);
			int buttonIndex = archer.getLetter() - 'A';
			final int archerIndex = index;
			archerButton[buttonIndex].setVisibility(View.VISIBLE);
			archerButton[buttonIndex].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					setCurrentArcher(archerIndex);
				}
			});
		}

	}

	private void loadMatchResults(int targetId) throws JSONException {
		JSONObject json = jsonTargetList.getJSONObject("" + targetId);
		if (json == null)
			throw new JSONException("Target " + targetId + " not found in JSON Target list");
		int matchId = json.getInt("matchId");
		boolean matchTrispot = json.getBoolean("trispot");
		int matchMode = 0;
		if (json.has("matchmode"))
			matchMode = json.getInt("matchmode");
		JSONArray jsonArray = json.getJSONArray("archerList");
		if (jsonArray.length() < 2)
			return;
		JSONObject jsonArcher = jsonArray.getJSONObject(0);
		Archer archerA = new Archer(jsonArcher);
		jsonArcher = jsonArray.getJSONObject(1);
		Archer archerB = new Archer(jsonArcher);
		archerA.setLetter('A');
		archerB.setLetter('B');
		archerList.add(archerA);
		archerList.add(archerB);
		if (matchMode == 0)
			match = new MatchSet(matchId, matchTrispot, StaticParam.matchVolleyMax, archerA, archerB);
		else
			match = new MatchPoint(matchId, matchTrispot, StaticParam.matchVolleyMax, archerA, archerB);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("matchId", matchId);
		editor.putBoolean("trispot", matchTrispot);
		editor.putInt("matchmode", matchMode);
		editor.apply();
		configureArcherButton();
		InetSocketAddress socketAddress = UDPListener.getInstance().getAddress();
		// request for list of archer
		JSONObject request = new JSONObject();
		request.put("command", 5);
		request.put("targetId", StaticParam.targetId);
		request.put("batteryLevel", getBatteryLevel());
		request.put("matchId", matchId);
		(new JsonRequestTask(this, socketAddress, "Synchronisation des résultats...") {

			@Override
			protected void onPostExecute(JSONObject jsonObject) {
				super.onPostExecute(jsonObject);
				int response;
				try {
					response = jsonObject.getInt("response");
					if ((response != 0) || (jsonObject.isNull("data"))) {
						throw new JSONException("JSON Object invalid");
					} else {
						JSONObject jsonMatchResult = jsonObject.getJSONObject("data");
						// System.out.println("List of results: "+jsonArrowList.toString(4));
						initMatchWithResults(jsonMatchResult);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				setCurrentArcher(0);

			}

		}).execute(request);

	}

	private void loadArcherResults(int targetId) throws JSONException {

		JSONArray json = jsonTargetList.getJSONArray("" + targetId);
		if ((json == null || (json.length() == 0)))
			throw new JSONException("Target " + targetId + " not found in JSON Target list");
		JSONArray jsonArray = new JSONArray();
		for (int index = 0; index < json.length(); index++) {
			JSONObject jsonArcher = json.getJSONObject(index);
			Archer archer = new Archer(jsonArcher);
			jsonArray.put(archer.getId());
			archerList.add(archer);
		}
		match = null;
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("matchId", -1);
		editor.apply();
		Collections.sort(archerList);
		configureArcherButton();
		InetSocketAddress socketAddress = UDPListener.getInstance().getAddress();
		// request for list of archer
		JSONObject request = new JSONObject();
		request.put("command", 2);
		request.put("targetId", StaticParam.targetId);
		request.put("batteryLevel", getBatteryLevel());
		request.put("data", jsonArray);

		(new JsonRequestTask(this, socketAddress, "Synchronisation des résultats...") {

			@Override
			protected void onPostExecute(JSONObject jsonObject) {
				super.onPostExecute(jsonObject);
				int response;
				try {
					response = jsonObject.getInt("response");
					if ((response != 0) || (jsonObject.isNull("data"))) {
						throw new JSONException("JSON Object invalid");
					} else {
						JSONObject jsonArrowList = jsonObject.getJSONObject("data");
						// System.out.println("List of results: "+jsonArrowList.toString(4));
						initArcherListWithResults(jsonArrowList);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				setCurrentArcher(0);

			}

		}).execute(request);
	}

	private void startVisualGuide1() {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		audioGuide1Playing = true;
		if (soundPool != null) {
			audioGuide1 = soundPool.load(this, R.raw.archersound, 1);
			soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId,
						int status) {
					if ((sampleId == audioGuide1) && (StaticParam.speakerEnabled)) {
						soundPool.play(audioGuide1, 1, 1, 0, 0, 1);
						visualGuide1 = new VisualGuide1(0);
						visualGuideHandler.postDelayed(visualGuide1, 0);
					}

				}
			});

		}
	}

	private void startVisualGuide3() {
		guideEnabled = false;
		audioGuide3Playing = true;
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		if (soundPool != null) {
			audioGuide3 = soundPool.load(this, R.raw.tablesound, 1);
			soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId,
						int status) {
					if ((sampleId == audioGuide3) && (StaticParam.speakerEnabled)) {
						soundPool.play(audioGuide3, 1, 1, 0, 0, 1);
						visualGuide3 = new VisualGuide3(0);
						visualGuideHandler.postDelayed(visualGuide3, 0);
					}

				}
			});

		}
	}

	private void startGuideActivity() {
		Intent intent = new Intent(mainContext, GuideActivity.class);
		String[] data = new String[archerList.size()];
		for (int i = 0; i < data.length; i++) {
			Archer archer = archerList.get(i);
			data[i] = archer.toCSV();
		}
		intent.putExtra("archerArray", data);
		intent.putExtra("match", false);
		startActivityForResult(intent, CODE_GUIDE_ACTIVITY);
	}

	private void updateMatchScore() {
		if (match != null) {
			int scoreA = match.getScore(0);
			int scoreB = match.getScore(1);
			String strA = "?";
			String strB = "?";
			if (scoreA > -1)
				strA = "" + scoreA;
			if (scoreB > -1)
				strB = "" + scoreB;
			scoreMatchText.setText(strA + " - " + strB);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CODE_MENU_ACTIVITY && resultCode == RESULT_OK) {
			processMenuChoice(data.getExtras().getInt("menuIndex", 0));

		}

		titleView.setText(BuildConfig.VERSION_NAME);

		if (requestCode == CODE_GUIDE_ACTIVITY && resultCode == RESULT_OK)
			startVisualGuide1();

		if (requestCode == CODE_VOLLEY_ACTIVITY && resultCode == RESULT_OK) {

			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			Archer archer = archerList.get(currentArcher);
			int id = data.getExtras().getInt("id");

			// check if archer is correct
			if (id != archer.getId())
				return;

			latestArcherUpdate = currentArcher;
			int volleyIndex = data.getExtras().getInt("volleyIndex");
			int[] array = data.getExtras().getIntArray("codeList");
			if ((array == null) || (array.length != StaticParam.arrowCount)) {
				Log.e("Main", "No data returned from VolleyActivity");
				return;
			}
			Volley volley = new Volley(array);
			// archer volley list
			List<Volley> volleyList = archer.getHeatList().get(currentHeat).getVolleyList();
			// if new volley
			if (volleyIndex >= volleyList.size()) {
				database.addVolley(archer, currentHeat, volleyIndex, volley);
				volleyList.add(volley);

			} else {
				volleyList.set(volleyIndex, volley);
				database.updateVolley(archer, currentHeat, volleyIndex, volley);
			}

			listAdapter.updateArcher(archer, currentHeat);
			listView.setSelection(volleyList.size());

			// if match, update score
			updateMatchScore();

			// post heat in queue
			if (!StaticParam.standAloneMode) {
				if (match != null)
					TRAPTASocket.getInstance().sendMatch(match.getMatchId(), match);
				else
					TRAPTASocket.getInstance().sendHeat(id, currentHeat, archer.getHeatList().get(currentHeat));
			}

			if (volleyIndex == 0 && currentArcher == 0 && match == null && archerList.size() > 1) {
				int[] coord = new int[2];
				archerButton[0].getLocationInWindow(coord);
				int y = coord[1] + archerButton[0].getLayoutParams().height / 2;

			}
			if (guideEnabled)
				startVisualGuide3();

			// if 5th volley and tie break needed for match
			if (volleyIndex == StaticParam.matchVolleyMax - 1 && match != null) {
				if (match.needArrowBreak())
					whoWins();

			}

		}
		if (requestCode == CODE_WELCOME_ACTIVITY && resultCode == RESULT_OK) {

			boolean standAloneMode = data.getExtras().getBoolean("standAloneMode");
			guideEnabled = data.getExtras().getBoolean("guide");
			int targetId = data.getExtras().getInt("targetId");
			boolean isMatch = data.getExtras().getBoolean("match");
			targetView.setText("  CIBLE " + targetId);
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt("target", targetId);
			editor.putInt("heatVolleyCount", StaticParam.heatVolleyCount);
			editor.putInt("arrowCount", StaticParam.arrowCount);
			editor.putBoolean("x10", StaticParam.x10);
			editor.putBoolean("standAloneMode", standAloneMode);
			editor.apply();
			StaticParam.targetId = targetId;
			archerList.clear();
			setCurrentHeat(0);
			latestArcherUpdate = -1;
			if ((standAloneMode) || (jsonTargetList == null)) {
				StaticParam.standAloneMode = true;
				match = null;
				editor = pref.edit();
				editor.putInt("matchId", -1);
				editor.apply();
				for (int index = 0; index < StaticParam.standAloneArcherList.get(targetId - 1).size(); index++) {
					archerList.add(new Archer(StaticParam.standAloneArcherList.get(targetId - 1).get(index)));

				}
				// write archerList to DB
				database.setArcherList(archerList);
				latestArcherUpdate = -1;
				updateListAdapter();
				setCurrentHeat(0);
				configureArcherButton();
				setCurrentArcher(0);
				if (guideEnabled)
					startGuideActivity();

			} else {
				StaticParam.standAloneMode = false;
				try {
					if (isMatch)
						loadMatchResults(targetId);
					else
						loadArcherResults(targetId);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}

	}

	private void startVolleyActivity(int volleyIndex, int[] array) {

		Intent intent = new Intent(this, VolleyActivity.class);
		intent.putExtra("codeList", array);
		intent.putExtra("id", archerList.get(currentArcher).getId());
		intent.putExtra("name", archerList.get(currentArcher).getName());
		intent.putExtra("trispot", archerList.get(currentArcher).isTrispot());
		intent.putExtra("volleyIndex", volleyIndex);
		if (guideEnabled)
			intent.putExtra("play", true);
		else
			intent.putExtra("play", false);
		goneToVolley = true;
		startActivityForResult(intent, CODE_VOLLEY_ACTIVITY);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void prepareVolleyActivity(List<Volley> volleyList) {
		int[] array = new int[StaticParam.arrowCount];
		for (int i = 0; i < StaticParam.arrowCount; i++)
			array[i] = -1;
		startVolleyActivity(volleyList.size(), array);
	}

	private void setMatchWinner(int archerIndex) {
		match.setTieBreakWinner(archerIndex);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("tieBreakWinner", archerIndex);
		editor.apply();
		listAdapter.notifyDataSetChanged();
		// post winner in queue
		TRAPTASocket.getInstance().sendMatch(match.getMatchId(), match);

	}

	private void whoWins() {
		final String archerA = archerList.get(0).getName();
		final String archerB = archerList.get(1).getName();

		QuestionDialog dialog = new QuestionDialog(mainContext,
				"Veuillez tirer la flèche de barrage et indiquer qui la gagne.",
				archerA,
				archerB);
		dialog.setOnButtonClickListener(new QuestionButtonListener() {

			@Override
			public void onButton2Click() {
				setMatchWinner(1);
			}

			@Override
			public void onButton1Click() {
				setMatchWinner(0);

			}
		});
		dialog.show();
	}

	// user clicked on one of the rows
	@Override
	public void onItemClick(AdapterView adapterView, View view, int position, long id) {

		if ((audioGuide1Playing || audioGuide3Playing) && soundPool != null) {
			Log.i("SOUND", "STOPPING SOUND !");
			audioGuide1Playing = false;
			audioGuide3Playing = false;
			soundPool.release();
		}
		final List<Volley> volleyList = archerList.get(currentArcher).getHeatList().get(currentHeat).getVolleyList();

		// if not new volley button
		if (position != volleyList.size()) {

			final int volleyIndex = position;
			// ask for confirmation
			String archerName = archerList.get(currentArcher).getName();
			QuestionDialog dialog = new QuestionDialog(mainContext,
					"Êtes-vous sûrs de vouloir modifier la volée " + (position + 1) + " pour " + archerName,
					"Annuler",
					"Modifier");
			dialog.setOnButtonClickListener(new QuestionButtonListener() {

				@Override
				public void onButton2Click() {
					Volley volley = volleyList.get(volleyIndex);
					int[] array = volley.getArrowArray();
					startVolleyActivity(volleyIndex, array);

				}

				@Override
				public void onButton1Click() {

				}
			});
			dialog.show();
			return;

		}

		if (match == null) {

			// if already 10 volleys, do nothing
			if (position == StaticParam.heatVolleyCount)
				return;

			// ask for confirmation if same archer
			if (currentArcher == latestArcherUpdate && archerList.size() > 1 && !StaticParam.standAloneMode) {
				QuestionDialog dialog = new QuestionDialog(this,
						"Voulez-vous vraiment entrer une nouvelle volée pour ce même archer ?", "Annuler",
						"Oui,\nencore une volée");
				dialog.setOnButtonClickListener(new QuestionButtonListener() {

					@Override
					public void onButton2Click() {
						prepareVolleyActivity(volleyList);

					}

					@Override
					public void onButton1Click() {
						// TODO Auto-generated method stub

					}
				});
				dialog.show();

			} else {
				prepareVolleyActivity(volleyList);
			}

		} else {

			if (position == StaticParam.matchVolleyMax && match.needArrowBreak())
				whoWins();
			else if (position < StaticParam.matchVolleyMax && match.getWinner() < 0) {
				if (match.isAllowed(currentArcher)) {
					prepareVolleyActivity(volleyList);
				} else {
					QuestionDialog dialog = new QuestionDialog(this, "Entrez d'abord la volée adverse", "OK", null);
					dialog.show();
				}
			}

		}

	}

	public void onBackPressed() {

	}

	@Override
	public void onWatchdog(String str) {
		Log.d("BROADCAST", str);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				broadcastImage.setVisibility(View.VISIBLE);
				broadcastImage.postDelayed(new Runnable() {
					@Override
					public void run() {
						broadcastImage.setVisibility(View.INVISIBLE);
					}
				}, 2500);

			}
		});
	}

	/**********************************************************************************************
	 * VisualGuide
	 */

	private Blinker blinker;

	private class VisualGuide1 implements Runnable {

		private int step;
		private int[] coord = new int[2];
		private TranslateAnimation anim;

		public VisualGuide1(int step) {
			this.step = step;

		}

		private void stop() {
			if (blinker != null)
				blinker.stop();
			handImage.setVisibility(View.INVISIBLE);
			audioGuide1Playing = false;
		}

		@Override
		public void run() {

			switch (step) {
				case 0: {
					if (!audioGuide1Playing) {
						stop();
						return;
					}
					setCurrentArcher(0);
					archerButton[0].getLocationInWindow(coord);
					int targetX = coord[0];
					int targetY = (int) (coord[1] + archerButton[0].getHeight() * 0.75);

					handImage.setVisibility(View.VISIBLE);
					handLayout.leftMargin = targetX;
					handLayout.topMargin = targetY;
					anim = new TranslateAnimation(StaticParam.screenWidth / 2 - targetX, 0,
							(int) (StaticParam.screenHeight * 0.7) - targetY, 0);
					anim.setDuration(4000);
					handImage.setAnimation(anim);
					anim.start();
					visualGuideHandler.postDelayed(new VisualGuide1(1), 4800);
					return;
				}
				case 1:
				case 2:
				case 3: {
					if (!audioGuide1Playing) {
						stop();
						return;
					}
					setCurrentArcher(currentArcher);
					archerButton[step].getLocationInWindow(coord);
					int targetX = coord[0];
					anim = new TranslateAnimation(handLayout.leftMargin - targetX, 0, 0, 0);
					handLayout.leftMargin = targetX;
					anim.setDuration(500);
					handImage.setAnimation(anim);
					anim.start();
					visualGuideHandler.postDelayed(new VisualGuide1(step + 1), 800);
					return;
				}
				case 4: {
					if (!audioGuide1Playing) {
						stop();
						return;
					}
					blinker = new Blinker(listAdapter, "", 0);
					blinker.start(200, 1, 1);
					listView.getLocationInWindow(coord);
					int targetX = StaticParam.screenWidth / 2 - handImage.getWidth();
					int targetY = coord[1] + archerButton[0].getHeight();
					anim = new TranslateAnimation(handLayout.leftMargin - targetX, 0, handLayout.topMargin - targetY,
							0);
					handLayout.leftMargin = targetX;
					handLayout.topMargin = targetY;
					anim.setDuration(1000);
					handImage.setAnimation(anim);
					anim.start();
					visualGuideHandler.postDelayed(new VisualGuide1(5), 3500);
					return;
				}
				case 5: {
					stop();
					return;
				}

			}
		}

	}

	private class VisualGuide3 implements Runnable {

		private int step;
		private int[] coord = new int[2];
		private TranslateAnimation anim;

		public VisualGuide3(int step) {
			this.step = step;

		}

		public void stop() {
			audioGuide3Playing = false;
			handImage.setVisibility(View.INVISIBLE);
		}

		@Override
		public void run() {

			switch (step) {

				case 0: {
					if (!audioGuide3Playing) {
						stop();
						return;
					}
					setCurrentArcher(currentArcher);
					if ((soundPool != null) && (StaticParam.speakerEnabled)) {
						soundPool.play(audioGuide3, 1, 1, 0, 0, 1);

					}
					listView.getLocationInWindow(coord);
					int targetX = coord[0];
					int targetY = coord[1] + archerInfoView.getHeight();

					handImage.setVisibility(View.VISIBLE);
					handLayout.leftMargin = targetX;
					handLayout.topMargin = targetY;
					anim = new TranslateAnimation(StaticParam.screenWidth / 2 - targetX, 0,
							StaticParam.screenHeight / 2 - targetY, 0);
					anim.setDuration(1000);
					handImage.setAnimation(anim);
					anim.start();
					visualGuideHandler.postDelayed(new VisualGuide3(1), 2000);
					return;
				}

				case 1: {
					if (!audioGuide3Playing) {
						stop();
						return;
					}
					setCurrentArcher(currentArcher);
					listView.getLocationInWindow(coord);
					int targetX = StaticParam.screenWidth - handImage.getLayoutParams().width;
					anim = new TranslateAnimation(handLayout.leftMargin - targetX, 0, 0, 0);
					handLayout.leftMargin = targetX;
					anim.setDuration(5000);
					handImage.setAnimation(anim);
					anim.start();
					visualGuideHandler.postDelayed(new VisualGuide3(2), 6000);
					return;
				}
				case 2: {
					stop();
					return;
				}

			}

		}

	}

	protected void onResume() {
		Log.i("MainActivity", "onResume");
		super.onResume();

		if (!goneToVolley && (currentArcher == archerList.size() - 1)) {
			Log.i("", "Go to archer A");
			latestArcherUpdate = -1;
			setCurrentArcher(0);
		}
		goneToVolley = false;

	}

}
