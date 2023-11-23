package com.trapta.app;

public class MatchPoint extends Match {

	public MatchPoint(int matchId,  boolean trispot, int volleyMax, Archer archerA, Archer archerB) {
		super(matchId, 1, trispot, volleyMax, archerA, archerB);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateScore() {
		// determine winner
		int score0 = getScore(0);
		int score1 = getScore(1);
		if (archerArray[0].getHeatList().get(0).getVolleyList().size()==volleyMax &&
				archerArray[1].getHeatList().get(0).getVolleyList().size()==volleyMax) {
			if (score0>score1) {
				tieBreakWinner = -1;
				winnerIndex = 0;
			}
			else if (score1>score0) {
				tieBreakWinner = -1;
				winnerIndex = 1;
			}
			else {
				winnerIndex = -1;
			}
		}
		
	}

	@Override
	public boolean needArrowBreak() {
		if (archerArray[0].getHeatList().get(0).getVolleyList().size()<volleyMax) return false;
		if (archerArray[1].getHeatList().get(0).getVolleyList().size()<volleyMax) return false;
		if (archerArray[0].getScore()!=archerArray[1].getScore()) return false;
		return true;
	}


	@Override
	public int getScore(int archerIndex, int volleyIndex) {
		if (archerIndex<0 || archerIndex>1) return 0;
		int score = archerArray[archerIndex].getHeatList().get(0).getScore(volleyIndex);
		return score;
	}

	@Override
	public int getScore(int archerIndex) {
		return archerArray[archerIndex].getHeatList().get(0).getScore();
	}

}
