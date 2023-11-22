package com.traps.trapta;

import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Volley6ListAdapter extends VolleyListAdapter {

	private boolean buttonHighlighted = false;
	
	public Volley6ListAdapter(Activity activity, int layoutId) {
		super(activity, layoutId, null);
		
	}

	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
				
		if (view == null) {
			view = LayoutInflater.from(activity).inflate(layoutId, parent, false);
		}
		TextView msg = (TextView) view.findViewById(R.id.enterVolley);
		TextView summary = (TextView) view.findViewById(R.id.summary);
		TextView id = (TextView) view.findViewById(R.id.volley6Id);
        TextView volley1 = (TextView) view.findViewById(R.id.volley61);
        TextView volley2 = (TextView) view.findViewById(R.id.volley62);
        TextView volley3 = (TextView) view.findViewById(R.id.volley63);
        TextView volley4 = (TextView) view.findViewById(R.id.volley64);
        TextView volley5 = (TextView) view.findViewById(R.id.volley65);
        TextView volley6 = (TextView) view.findViewById(R.id.volley66);
        TextView volleySum = (TextView) view.findViewById(R.id.volley6Sum);
        if (layoutId==R.layout.cellview_volley6_inverted) volleySum.setBackgroundColor(Color.BLACK);
        TextView volleyCumulSum = (TextView) view.findViewById(R.id.volley6CumulSum);

        Heat heat = archer.getHeatList().get(heatIndex);
        // if last position (button enter new volley)
	    if (position==heat.getVolleyList().size()) { 

	    	if (position<StaticParam.heatVolleyCount) {
				msg.setVisibility(View.VISIBLE);
				summary.setVisibility(View.GONE);
				msg.setText("Entrer la volée " + (position + 1));
	    	}
	    	else {
	    		StringBuilder builder = new StringBuilder();

                Heat currentRun = archer.getHeatList().get(heatIndex);
                builder.append(    "Cette série  : "+currentRun.getScore()); builder.append("\n");
                if (StaticParam.x10) {
                    builder.append("Nb 10+10X    : "+(currentRun.getCodeCount(11)+currentRun.getCodeCount(10))); builder.append("\n");
                    builder.append("Nb 10X       : "+currentRun.getCodeCount(11)); builder.append("\n"); builder.append("\n");
                }
                else {
                    builder.append("Nb 10        : "+currentRun.getCodeCount(10)); builder.append("\n");
                    builder.append("Nb  9        : "+currentRun.getCodeCount(9)); builder.append("\n"); builder.append("\n");
                }

                builder.append(    "Total séries : "+archer.getScore()); builder.append("\n");
                if (StaticParam.x10) {
                    builder.append("Nb 10+10X    : "+(archer.getCodeCount(11)+archer.getCodeCount(10))); builder.append("\n");
                    builder.append("Nb 10X       : "+archer.getCodeCount(11)); builder.append("\n");
                }
                else {
                    builder.append("Nb 10        : "+archer.getCodeCount(10)); builder.append("\n");
                    builder.append("Nb  9        : "+archer.getCodeCount(9)); builder.append("\n");
                }
                builder.append(    "Moyenne      : "+String.format(Locale.getDefault(), "%.2f", archer.getAverage()));
                summary.setMaxLines(7);

				msg.setVisibility(View.GONE);
				summary.setVisibility(View.VISIBLE);
				summary.setText(builder.toString());
				summary.setGravity(Gravity.LEFT);

	    	}
	    	int drawableId = R.drawable.buttonb;
	    	if (buttonHighlighted && StaticParam.colorInverted) drawableId = R.drawable.buttona_inverted;
	    	if (buttonHighlighted && !StaticParam.colorInverted) drawableId = R.drawable.buttona;
	    	if (!buttonHighlighted && StaticParam.colorInverted) drawableId = R.drawable.buttonb_inverted;
	    	if (!buttonHighlighted && !StaticParam.colorInverted) drawableId = R.drawable.buttonb;
			msg.setBackgroundResource(drawableId);
	    	if (StaticParam.colorInverted) {
				msg.setTextColor(Color.BLACK);
				summary.setTextColor(Color.BLACK);
				summary.setBackgroundColor(Color.WHITE);
			}
	    	else {
				msg.setTextColor(Color.WHITE);
				summary.setTextColor(Color.WHITE);
				summary.setBackgroundColor(Color.BLACK);
			}
			id.setVisibility(View.GONE);
	    	volley1.setVisibility(View.GONE);
	    	volley2.setVisibility(View.GONE);
	    	volley3.setVisibility(View.GONE);
	    	volley4.setVisibility(View.GONE);
	    	volley5.setVisibility(View.GONE);
	    	volley6.setVisibility(View.GONE);
	    	volleySum.setVisibility(View.GONE);
	        volleyCumulSum.setVisibility(View.GONE);
	    	
	    } else {
	    	Volley volley = heat.getVolleyList().get(position);
			msg.setVisibility(View.GONE);
			summary.setVisibility(View.GONE);
	    	id.setGravity(Gravity.CENTER);
	    	if (StaticParam.colorInverted) id.setBackgroundResource(R.drawable.volleycount_inverted);
	    	else id.setBackgroundResource(R.drawable.volleycount);
			id.setTextColor(Color.WHITE);
			id.setVisibility(View.VISIBLE);
	    	volley1.setVisibility(View.VISIBLE);
	    	volley2.setVisibility(View.VISIBLE);
	    	volley3.setVisibility(View.VISIBLE);
	    	volley4.setVisibility(View.VISIBLE);
	    	volley5.setVisibility(View.VISIBLE);
	    	volley6.setVisibility(View.VISIBLE);
	    	volleySum.setVisibility(View.VISIBLE);
	        volleyCumulSum.setVisibility(View.VISIBLE);

			String indexStr = ""+(position+1);
			if (position<9) indexStr = indexStr+".";
			id.setText(indexStr);
	        if (volley.getArrowList().size()==6) {
				volley1.setText(getPaddedString(volley.getArrowList().get(0).toString(), 2));
				volley2.setText(getPaddedString(volley.getArrowList().get(1).toString(), 2));
				volley3.setText(getPaddedString(volley.getArrowList().get(2).toString(), 2));
				volley4.setText(getPaddedString(volley.getArrowList().get(3).toString(), 2));
				volley5.setText(getPaddedString(volley.getArrowList().get(4).toString(), 2));
				volley6.setText(getPaddedString(volley.getArrowList().get(5).toString(), 2));
				if (!StaticParam.colorInverted) {
					volley1.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(0).getValue()));
					volley2.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(1).getValue()));
					volley3.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(2).getValue()));
					volley4.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(3).getValue()));
					volley5.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(4).getValue()));
					volley6.setBackgroundResource(getBackgroundResource(volley.getArrowList().get(5).getValue()));
					volley1.setTextColor(getScoreTextColor(volley.getArrowList().get(0).getValue()));
					volley2.setTextColor(getScoreTextColor(volley.getArrowList().get(1).getValue()));
					volley3.setTextColor(getScoreTextColor(volley.getArrowList().get(2).getValue()));
					volley4.setTextColor(getScoreTextColor(volley.getArrowList().get(3).getValue()));
					volley5.setTextColor(getScoreTextColor(volley.getArrowList().get(4).getValue()));
					volley6.setTextColor(getScoreTextColor(volley.getArrowList().get(5).getValue()));
				}

			}

			volleySum.setText(getPaddedString(""+volley.getScore(), 2));
			volleyCumulSum.setText(getPaddedString(""+heat.getScore(position), 3));
	        	        
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
