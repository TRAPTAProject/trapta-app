package com.traps.trapta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class MenuActivity extends Activity implements View.OnClickListener {

    private Button[] menuButton = new Button[7];


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		getWindow().setFlags(          
				WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);   
		getActionBar().hide();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        menuButton[0] = findViewById(R.id.MenuButton00); // redemarrer mode connecte
        menuButton[1] = findViewById(R.id.MenuButton01); // bascule serie
        menuButton[2] = findViewById(R.id.MenuButton02); // retour serie
        menuButton[3] = findViewById(R.id.MenuButton03); // renvoyer
        menuButton[4] = findViewById(R.id.MenuButton07); // redemarrer mode tout seul
        menuButton[5] = findViewById(R.id.MenuButton05); // couleurs
        menuButton[6] = findViewById(R.id.MenuButton08); // retour


        boolean match = getIntent().getExtras().getBoolean("match", false);
        int currentHeat = getIntent().getExtras().getInt("currentHeat", 0);
        if (match) menuButton[1].setVisibility(View.GONE);
        if (StaticParam.colorInverted) menuButton[5].setText("Contraste salle");
        else menuButton[5].setText("Contraste extérieur");

        switch (currentHeat) {
            case 0 : {
                menuButton[1].setVisibility(View.VISIBLE);
                menuButton[1].setText("Basculer en série 2");
                menuButton[2].setVisibility(View.GONE);
                break;
            }
            case 1 : {
                    if (StaticParam.roundCount==4) {
                        menuButton[1].setVisibility(View.VISIBLE);
                        menuButton[1].setText("Basculer en série 3");
                    } else {
                        menuButton[1].setVisibility(View.GONE);
                    }
                    menuButton[2].setVisibility(View.VISIBLE);
                    menuButton[2].setText("Revenir en série 1");
                break;
            }
            case 2 : {
                if (StaticParam.roundCount==4) {
                    menuButton[1].setVisibility(View.VISIBLE);
                    menuButton[2].setVisibility(View.VISIBLE);
                    menuButton[1].setText("Basculer en série 4");
                    menuButton[2].setText("Revenir en série 2");
                }
                break;
            }
            case 3 : {
                if (StaticParam.roundCount==4) {
                    menuButton[1].setVisibility(View.GONE);
                    menuButton[2].setVisibility(View.VISIBLE);
                    menuButton[2].setText("Revenir en série 3");
                }
                break;
            }
        }

        LinearLayout layout = findViewById(R.id.MenuLayout1);
        if (StaticParam.colorInverted) {
            layout.setBackgroundColor(Color.WHITE);
            for (int i=0; i<menuButton.length; i++) {
                menuButton[i].setTextColor(Color.BLACK);
                menuButton[i].setBackgroundResource(R.drawable.buttonb_inverted);
            }
        }
        else {
            layout.setBackgroundColor(Color.BLACK);
            for (int i=0; i<menuButton.length; i++) {
                menuButton[i].setTextColor(Color.WHITE);
                menuButton[i].setBackgroundResource(R.drawable.buttona);
            }

        }

        for (int i=0; i<menuButton.length; i++) {
            menuButton[i].setOnClickListener(this);
        }

	}


    @Override
    public void onClick(View view) {
        // look for button
        int index = 0;
        for (int i=0; i<menuButton.length; i++) if (view==menuButton[i]) index = i;
        Intent data = new Intent();
        data.putExtra("menuIndex", index);
        setResult(RESULT_OK, data);
        finish();
    }
}
