package com.traps.trapta;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Volley6MatchAdapter extends VolleyListAdapter {

	private boolean buttonHighlighted = false;

	
	public Volley6MatchAdapter(Activity activity, int layoutId, Match match) {
		super(activity,  layoutId, match);
		
		
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
		TextView id = (TextView) view.findViewById(R.id.volley6Id);
        TextView volley61 = (TextView) view.findViewById(R.id.volley61);
        TextView volley62 = (TextView) view.findViewById(R.id.volley62);
        TextView volley63 = (TextView) view.findViewById(R.id.volley63);
        TextView volley64 = (TextView) view.findViewById(R.id.volley64);
        TextView volley65 = (TextView) view.findViewById(R.id.volley65);
        TextView volley66 = (TextView) view.findViewById(R.id.volley66);        
        TextView volleySum = (TextView) view.findViewById(R.id.volley6Sum);
        if (layoutId==R.layout.cellview_volley6_inverted) volleySum.setBackgroundColor(Color.BLACK);
        TextView volleySet = (TextView) view.findViewById(R.id.volley6CumulSum);

        Heat heat = archer.getHeatList().get(heatIndex);
        // if last position (button enter new volley)
	    if (position==heat.getVolleyList().size()) {
            msg.setVisibility(View.VISIBLE);
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
	    	
	        volley61.setVisibility(View.GONE);
	        volley62.setVisibility(View.GONE);
	        volley63.setVisibility(View.GONE);
	        volley64.setVisibility(View.GONE);
	        volley65.setVisibility(View.GONE);
	        volley66.setVisibility(View.GONE);
	        volleySum.setVisibility(View.GONE);
	        volleySet.setVisibility(View.GONE);
			id.setVisibility(View.GONE);
			summary.setVisibility(View.GONE);
	    } else {
	    	Volley volley = heat.getVolleyList().get(position);
	    	int archerIndex = match.getArcherIndex(archer);
	    	if (StaticParam.colorInverted) id.setBackgroundResource(R.drawable.volleycount_inverted);
	    	else id.setBackgroundResource(R.drawable.volleycount);
	    	id.setTextColor(Color.WHITE);
            id.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
            summary.setVisibility(View.GONE);
	        volley61.setVisibility(View.VISIBLE);
	        volley62.setVisibility(View.VISIBLE);
	        volley63.setVisibility(View.VISIBLE);
	        volley64.setVisibility(View.VISIBLE);
	        volley65.setVisibility(View.VISIBLE);
	        volley66.setVisibility(View.VISIBLE);
	        volleySum.setVisibility(View.VISIBLE);
	        volleySet.setVisibility(View.VISIBLE);
	        id.setText(""+(position+1)+".");
	        volley61.setText(getPaddedString(volley.getArrowList().get(0).toString(), 2));
	        volley62.setText(getPaddedString(volley.getArrowList().get(1).toString(), 2));
	        volley63.setText(getPaddedString(volley.getArrowList().get(2).toString(), 2));
	        if (volley.getArrowList().size()>3) {
	        	volley64.setText(getPaddedString(volley.getArrowList().get(3).toString(), 2));
	        	volley65.setText(getPaddedString(volley.getArrowList().get(4).toString(), 2));
	        	volley66.setText(getPaddedString(volley.getArrowList().get(5).toString(), 2));
	        }
            if (!StaticParam.colorInverted) {
                volley61.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(0).getValue()));
                volley62.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(1).getValue()));
                volley63.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(2).getValue()));
                volley61.setTextColor(getScoreTextColor(volley.getArrowList().get(0).getValue()));
                volley62.setTextColor(getScoreTextColor(volley.getArrowList().get(1).getValue()));
                volley63.setTextColor(getScoreTextColor(volley.getArrowList().get(2).getValue()));

                if (volley.getArrowList().size()>3) {
                    volley64.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(3).getValue()));
                    volley65.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(4).getValue()));
                    volley66.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(5).getValue()));
                    volley64.setTextColor(getScoreTextColor(volley.getArrowList().get(3).getValue()));
                    volley65.setTextColor(getScoreTextColor(volley.getArrowList().get(4).getValue()));
                    volley66.setTextColor(getScoreTextColor(volley.getArrowList().get(5).getValue()));
                }

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
