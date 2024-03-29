package com.trapta.app;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Volley3MatchAdapter extends VolleyListAdapter {

	private boolean buttonHighlighted = false;

	
	public Volley3MatchAdapter(Activity activity,int layoutId, Match match) {
		super(activity, layoutId, match);
		
		
	}
	
    public void updateArcher(Archer archer, int heatIndex) {
        this.archer = archer;
        this.heatIndex = heatIndex;
        match.updateScore();
        notifyDataSetChanged();
    }

	@Override
	public View getView(int position, View view, ViewGroup parent) {
				
		if (view == null) {
			view = LayoutInflater.from(activity).inflate(layoutId, parent, false);
		}
		TextView msg = (TextView) view.findViewById(R.id.enterVolley);
		TextView summary = (TextView) view.findViewById(R.id.summary);
		TextView id = (TextView) view.findViewById(R.id.volleyId);
        TextView volley1 = (TextView) view.findViewById(R.id.volley1);
        TextView volley2 = (TextView) view.findViewById(R.id.volley2);
        TextView volley3 = (TextView) view.findViewById(R.id.volley3);
        TextView volleySum = (TextView) view.findViewById(R.id.volleySum);
        if (layoutId==R.layout.cellview_volley3_inverted) volleySum.setBackgroundColor(Color.BLACK);
        TextView volleySet = (TextView) view.findViewById(R.id.volleyCumulSum);

	        
        Heat heat = archer.getHeatList().get(heatIndex);
        // if last position (button enter new volley)
	    if (position==heat.getVolleyList().size()) {
	    	int drawableId = R.drawable.buttonb;
	    	if (buttonHighlighted && StaticParam.colorInverted) drawableId = R.drawable.buttona_inverted;
	    	if (buttonHighlighted && !StaticParam.colorInverted) drawableId = R.drawable.buttona;
	    	if (!buttonHighlighted && StaticParam.colorInverted) drawableId = R.drawable.buttonb_inverted;
	    	if (!buttonHighlighted && !StaticParam.colorInverted) drawableId = R.drawable.buttonb;
	    	msg.setBackgroundResource(drawableId);
	    	if (StaticParam.colorInverted) msg.setTextColor(Color.BLACK);
	    	else msg.setTextColor(Color.WHITE);
	    	// there is a winner
	    	if (match.getWinner()>-1) {
	    		Archer archer = match.getArcher(match.getWinner());
	    		if (archer!=null) {
	    			if (match.needArrowBreak()) 
	    				msg.setText("Barrage remporté par "+archer.getName());
	    			else msg.setText("Match remporté par "+archer.getName());
	    			
	    		}
	    	}
	    	// if tie break is needed
	    	else if (match.needArrowBreak()) {
	    		msg.setText("Entrer la flèche de barrage");
	    	}
	    	// if archer is allowed
	    	else if (match.isAllowed(archer)) {
	    		msg.setText("Entrer la volée "+(position+1));
	    	}
	    	// if archer not allowed
	    	else {
	    		msg.setText("En attente de la vollée adverse...");
	    	}
	    	
	        volley1.setVisibility(View.GONE);
	        volley2.setVisibility(View.GONE);
	        volley3.setVisibility(View.GONE);
	        volleySum.setVisibility(View.GONE);
	        volleySet.setVisibility(View.GONE);
            id.setVisibility(View.GONE);
            summary.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);
	    } else {
	    	Volley volley = heat.getVolleyList().get(position);
	    	int archerIndex = match.getArcherIndex(archer);
	    	msg.setVisibility(View.GONE);
            summary.setVisibility(View.GONE);

	    	if (StaticParam.colorInverted) id.setBackgroundResource(R.drawable.volleycount_inverted);
	    	else id.setBackgroundResource(R.drawable.volleycount);
	    	msg.setTextColor(Color.WHITE);
	        volley1.setVisibility(View.VISIBLE);
	        volley2.setVisibility(View.VISIBLE);
	        volley3.setVisibility(View.VISIBLE);
	        volleySum.setVisibility(View.VISIBLE);
	        volleySet.setVisibility(View.VISIBLE);
            id.setVisibility(View.VISIBLE);
	        id.setText(""+(position+1)+".");
	        volley1.setText(getPaddedString(volley.getArrowList().get(0).toString(), 2));
	        volley2.setText(getPaddedString(volley.getArrowList().get(1).toString(), 2));
	        volley3.setText(getPaddedString(volley.getArrowList().get(2).toString(), 2));
            if (!StaticParam.colorInverted) {
                volley1.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(0).getValue()));
                volley2.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(1).getValue()));
                volley3.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(2).getValue()));
                volley1.setTextColor(getScoreTextColor(volley.getArrowList().get(0).getValue()));
                volley2.setTextColor(getScoreTextColor(volley.getArrowList().get(1).getValue()));
                volley3.setTextColor(getScoreTextColor(volley.getArrowList().get(2).getValue()));
            }

            volleySum.setText(""+volley.getScore());
	        String valueStr = "?";
	        if (match.getScore(archerIndex, position)>-1) valueStr = ""+match.getScore(archerIndex, position);
	        volleySet.setText(valueStr);
	        	        
	    }

	    return view;
		
	}

	private void update() {
		activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                notifyDataSetChanged();

            }
        });
	}

	
	@Override
	public void blinkOn(String id, int index) {
		buttonHighlighted = true;
		update();
		
		
	}

	@Override
	public void blinkOff(String id, int index) {
		buttonHighlighted = false;
		update();
	}

}
