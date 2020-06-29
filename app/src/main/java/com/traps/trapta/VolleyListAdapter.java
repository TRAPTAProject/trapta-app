package com.traps.trapta;

import android.app.Activity;
import android.graphics.Color;
import android.widget.BaseAdapter;
 
public abstract class VolleyListAdapter extends BaseAdapter implements BlinkListener {

	protected Archer archer = new Archer();
	protected Volley paddingVolley = new Volley();
	protected int heatIndex = 0;
	protected Match match = null;
	protected int layoutId;
 
    protected final Activity activity;
  

    // the context is needed to inflate views in getView()
    public VolleyListAdapter(Activity activity, int layoutId, Match match) {
        this.activity = activity;
        this.layoutId = layoutId;
        this.match = match;
    }

    
    public void updateArcher(Archer archer, int heatIndex) {
        this.archer = archer;
        this.heatIndex = heatIndex;
        notifyDataSetChanged();
    }
    
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }
    
	@Override
	public int getCount() {
		Heat heat = archer.getHeatList().get(heatIndex);
		if (heat==null) return 0;
		return heat.getVolleyList().size()+1;
	}

	@Override
	public Object getItem(int arg0) {
		Heat heat = archer.getHeatList().get(heatIndex);
		if (heat==null) return null;
		if (arg0<heat.getVolleyList().size()) return heat.getVolleyList().get(arg0);
		return paddingVolley;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	protected String getPaddedString(String str, int length)
	{
		for (int index=str.length(); index<length; index++) str = " "+str;
		return str;

	}

	protected int getBackgroundResource(int score) {
		if (score>8) return R.drawable.bgyellow;
		if (score>6) return R.drawable.bgred;
		if (score>4) return R.drawable.bgblue;
		if (score>2) return R.drawable.bgblack;
		return R.drawable.bgwhite;
	}

	protected int getScoreTextColor(int score) {
		if (score>8) return Color.BLACK;
		if (score>6) return Color.WHITE;
		if (score>4) return Color.BLACK;
		if (score>2) return Color.WHITE;
		return Color.BLACK;

	}

}
