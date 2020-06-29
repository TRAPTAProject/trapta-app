package com.traps.trapta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Match {

	protected Archer[] archerArray = new Archer[2];
	protected int winnerIndex = -1;
	protected int tieBreakWinner = -1;
	protected int matchId;
	protected int matchMode = 0;  // 0 = SET, 1 = POINT
	protected int volleyMax = 5; 
	protected int[][] matchScore = new int[2][5]; 
	
	public Match(int matchId, int matchMode, boolean trispot, int volleyMax, Archer archerA, Archer archerB) {
		this.matchId = matchId;
		this.matchMode = matchMode;
		this.volleyMax = volleyMax;
		archerArray[0] = archerA;
		archerArray[1] = archerB;
		updateScore();
	}
	
	public JSONObject getJson() {
		JSONObject json = new JSONObject();
		JSONArray jsonArray0 = archerArray[0].getHeatList().get(0).getJsonArray();
		JSONArray jsonArray1 = archerArray[1].getHeatList().get(0).getJsonArray();
		JSONArray jsonArrayList = new JSONArray();
		jsonArrayList.put(jsonArray0);
		jsonArrayList.put(jsonArray1);
		try {
			json.put("matchId", matchId);
			json.put("arrowList", jsonArrayList);
			json.put("tieBreakWinner", tieBreakWinner);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	
	public int getMatchId() {
		return matchId;
	}
	
	public int getMatchMode() 	{
		return matchMode;
	}
	
	public int getArcherIndex(Archer archer) {
		if (archer==archerArray[0]) return 0;
		if (archer==archerArray[1]) return 1;
		return -1;
	}
	
	public Archer getArcher(int index) {
		if (index<0 || index>2) return null;
		return archerArray[index];
	}
	
	
	public void setTieBreakWinner(int index) {
		tieBreakWinner = index;
	}
	
	public int getWinner() {
		if (tieBreakWinner>-1) return tieBreakWinner;
		return winnerIndex; 
	}
	
	// return true if a new volley can be added to the archer (the other archer have at least the same number of volleys)
	public boolean isAllowed(int archerIndex) {
		if (archerArray[archerIndex].getHeatList().get(0).getVolleyList().size()>=volleyMax) return false;
		int[] counter = new int[2];
		counter[0] = archerArray[0].getHeatList().get(0).getVolleyList().size();
		counter[1] = archerArray[1].getHeatList().get(0).getVolleyList().size();
		if (counter[1-archerIndex]>=counter[archerIndex]) return true;
		return false;
		
	}
	
	public boolean isAllowed(Archer archer) {
		int index = getArcherIndex(archer);
		if (index<0) return false;
		return isAllowed(index);
	}
	
	
	public abstract void updateScore();
	
	public abstract boolean needArrowBreak();
	
	// returns score at volley index (set score or added score for point mode) 
	public abstract int getScore(int archerIndex, int volleyIndex);
	
	public abstract int getScore(int archerIndex);
	
	
}

