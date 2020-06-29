package com.traps.trapta;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class Heat {
	
	private List<Volley> volleyList = new ArrayList<Volley>();
	
	public List<Volley> getVolleyList() {
		return volleyList;
	} 

	// return the number of arrow
	public int getArrowCount() {
		int sum = 0;
		for (Volley volley: volleyList) {
			sum += volley.getArrowList().size();
		}
		return sum;
	}
	
	public float getAverage() {
		return (float)getScore() / getArrowCount();
	}
	
	public JSONArray getJsonArray() {
		JSONArray json = new JSONArray();
		for (Volley volley:volleyList) json.put(volley.getJsonArray());
		return json;
	}
	
	public void setJsonArrowList(JSONArray array) throws JSONException {
		volleyList.clear();
		for (int index=0; index<array.length(); index++) {
			JSONArray arrowList = array.getJSONArray(index);
			if ((arrowList==null) || (arrowList.length()==0)) return;
			volleyList.add(new Volley(arrowList));
		}
	}
	
	public int getScore() { 
		return getScore(volleyList.size());
	}
	
	// return score as sum up to volley index position
	public int getScore(int index) {
		int score = 0;
		for (int i=0; i<=index; i++) {
			if (i>=volleyList.size()) break;
			Volley volley = volleyList.get(i);
			score += volley.getScore();
		}
		return score; 
		
	}

	public int getCodeCount(int code) {
		int sum = 0;
		for (Volley volley: volleyList) sum += volley.getCodeCount(code);
		return sum;
	}
	
	public void clear() {
		volleyList.clear();
	}
	

}
