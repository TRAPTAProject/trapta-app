package com.traps.trapta;

public class MatchSet extends Match {
 
	
	public MatchSet(int matchId, boolean trispot, int volleyMax, Archer archerA, Archer archerB) {
		super(matchId, 0, trispot, volleyMax, archerA, archerB);		
		
	}

	
	public boolean needArrowBreak() {
		if (getScore(0)==volleyMax && getScore(1)==volleyMax) return true;
		return false;
	}
	
	// returns score at volley index (set score or added score for point mode) 
	public int getScore(int archerIndex, int volleyIndex) {
		if (archerIndex<0 || archerIndex>1 || volleyIndex<0 || volleyIndex>volleyMax-1) return -1;
		return matchScore[archerIndex][volleyIndex];
	}
	
	// added sum (set or point)
	public int getScore(int archerIndex) {
		int sum = 0;
		if (archerIndex<0 || archerIndex>1) return 0;
		for (int index=0; index<volleyMax; index++) {
			if (matchScore[archerIndex][index]>0) sum+=matchScore[archerIndex][index];
		}
		return sum;
		
	}

	
	public void updateScore() {
		for (int index=0; index<volleyMax; index++) {
			int scoreSet0 = computeScoreSet(index);
			matchScore[0][index] = scoreSet0;
			switch (scoreSet0) {
				case 0: matchScore[1][index] = 2; break;
				case 1: matchScore[1][index] = 1; break;
				case 2: matchScore[1][index] = 0; break;
				default: matchScore[1][index] = -1;
			}
		}
		int sum0 = getScore(0);
		int sum1 = getScore(1);
		
		if (sum0>volleyMax) {
			tieBreakWinner = -1;
			winnerIndex = 0;
		}
		else if (sum1>volleyMax) {
			tieBreakWinner = -1;
			winnerIndex = 1;
		}
		else winnerIndex = -1;
		
	}
	
	
		
	// returns the score set for the first archer. if score is 0 then second archer is 2,
	// if it is 1, second is 1, if it is 2, second is 0.
	// return -1 if the score cannot be determined (volley not yet entered)
	private int computeScoreSet(int volleyIndex) {
		Heat run0 = archerArray[0].getHeatList().get(0);
		Heat run1 = archerArray[1].getHeatList().get(0);
		if (run0.getVolleyList().size()<volleyIndex+1 || run1.getVolleyList().size()<volleyIndex+1) return -1;
		int score0 = run0.getVolleyList().get(volleyIndex).getScore();
		int score1 = run1.getVolleyList().get(volleyIndex).getScore();
		if (score0>score1) return 2;
		if (score0<score1) return 0;
		return 1;
	}
}
