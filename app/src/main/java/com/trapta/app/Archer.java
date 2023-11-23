package com.trapta.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Archer implements Comparable<Archer> {
 
	static private int heatCount = 4;
	static private int idSeed = 0;
	  
	
	private String name;
	private String license; 
	private String category;
	private int id;  
	private boolean trispot;
	private char letter;
	private List<Heat> heatList = new ArrayList<Heat>();
	
	     
	public Archer() {
		name = "Undefined";
		license = "Undefined";
		category = "Undefined";
		id = 0;
		letter = 'F';
		for (int i=0; i<heatCount; i++) {
			heatList.add(new Heat());
		} 
	} 
	
	public Archer(int id, String license, String category, String name, char letter, boolean trispot) {
		this.id = id;
		idSeed = id+1;
		this.name = name;
		this.category = category;
		this.license = license;
		this.letter = letter;
		this.trispot = trispot;
		for (int i=0; i<heatCount; i++) {
			heatList.add(new Heat());
		}
	}
	 
	public Archer(Archer archer) {
		this.id = archer.id;
		this.name = archer.name;
		this.category = archer.category;
		this.license = archer.license;
		this.letter = archer.letter;
		this.trispot = archer.trispot;
		for (int i=0; i<heatCount; i++) {
			heatList.add(new Heat());
		}
	}
	
	public Archer(JSONObject json) throws JSONException {
		license = "license";
		category = "category";
		name = "name";
		id = idSeed++;
		letter = 'A';
		trispot = false;
		if (json.has("license")) this.license = json.getString("license");
		if (json.has("category")) this.category = json.getString("category");
		if (json.has("name")) this.name = json.getString("name");
		if (json.has("id")) this.id = json.getInt("id");
		if (json.has("letter")) this.letter = (char)json.getInt("letter");
		if (json.has("trispot")) this.trispot = json.getBoolean("trispot");
		for (int i=0; i<heatCount; i++) {
			heatList.add(new Heat());
		}
	}
	
	public void setJsonArrowList(JSONArray array) throws JSONException {
		for (int index=0; index<array.length(); index++) {
			JSONArray jsonArrowList = array.getJSONArray(index);
			Heat heat = heatList.get(index);
			if (heat!=null) heat.setJsonArrowList(jsonArrowList);
		}
		  
	}
	
	public void setJsonMatchArrow(JSONArray array) throws JSONException {
		Heat heat = heatList.get(0);
		if (heat!=null) heat.setJsonArrowList(array);
		
		  
	}

	public JSONArray getJsonArray() {
		JSONArray json = new JSONArray();
		for (Heat heat:heatList) json.put(heat.getJsonArray());
		return json;
	}
	
	public String toString() {
		return letter+": "+name+"\n"+license+" "+category;
	}
	
	public String toCSV() {
		return letter+";"+name+";"+license+";"+category;
	}
	
	public char getLetter() {
		return letter;
	}
	
	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	} 
	public boolean isTrispot() {
		return trispot;
	}
	
	public String getLicense() {
		return license;
	} 
	
	public List<Heat> getHeatList() {
		return heatList;
	}
	
	public int getArrowCount() {
		int sum = 0;
		for (Heat heat: heatList) {
			sum += heat.getArrowCount();
		}
		return sum;
	}
	    
	public float getAverage() {
		return (float)getScore() / getArrowCount();
	}
	
	public int getScore() {
		int score = 0;
		for (Heat heat: heatList) score += heat.getScore();
		return score;
	}
	 
	public int getCriteria1() {
		if (StaticParam.x10) return getCodeCount(10)+getCodeCount(11);
		return getCodeCount(10);
	}
	
	public int getCriteria2() {
		if (StaticParam.x10) return getCodeCount(11);
		return getCodeCount(9);
	}

	     
	public int getCodeCount(int code) {
		int sum = 0;
		for (Heat heat: heatList) sum += heat.getCodeCount(code);
		return sum;
	}
	
	public void clear() {
		heatList.clear();
		for (int i=0; i<heatCount; i++) {
			heatList.add(new Heat());
		}
	}

	@Override
	public int compareTo(Archer arg0) {
		if (arg0.getLetter()>getLetter()) return -1;
		if (arg0.getLetter()<getLetter()) return 1;
		return 0;
	}
	
	
}
