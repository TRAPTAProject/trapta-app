package com.trapta.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class Volley {
	 
	private List<Arrow> arrowList = new ArrayList<Arrow>();
 	
	public Volley() {
	 
	} 
	 
	public Volley(List<Arrow> list) {
		arrowList = list;
	}
	  
	public Volley(int[] array) {

		Arrays.sort(array);
		if (array!=null) 
			for (int i=array.length-1; i>-1; i--) 
				if (array[i]>-1 && array[i]<12) 
					arrowList.add(new Arrow(array[i]));

	}
	 
	public Volley(JSONArray array) throws JSONException {
		for (int index=0; index<array.length(); index++) {
			int value = array.getInt(index);
			if (value>-1 && value<12) arrowList.add(new Arrow(value));
		}
	}
	
	public List<Arrow> getArrowList() {
		return arrowList;
	}
	
	public JSONArray getJsonArray() {
		JSONArray json = new JSONArray();
		for (Arrow arrow:arrowList) json.put(arrow.getCode());
		return json;
	}
	
	public List<Integer> getCodeList() {
		List<Integer> list = new ArrayList<Integer>();
		for (Arrow arrow: arrowList) list.add(arrow.getCode());
		return list;
	}
	
	public int getScore() {

		int sum = 0;
		for (Arrow arrow: arrowList) {
			if (!arrow.isDefined()) return -1;
			sum += arrow.getValue();
		}
		return sum;
	}
	
	
	public int getCodeCount(int code) {
		int sum = 0;
		for (Arrow arrow: arrowList) if (arrow.getCode()==code) sum++;
		return sum;
	}
		
	
	public int[] getArrowArray() {
		int[] array = new int[6];
		for (int i=0; i<6; i++) array[i] = -1;
		int index = 0;
		for (Arrow arrow: arrowList) array[index++] = arrow.getCode();
		return array;
	}

}
