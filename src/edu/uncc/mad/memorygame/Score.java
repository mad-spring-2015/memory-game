package edu.uncc.mad.memorygame;

public class Score {
	public static long calcScore(long timeTaken, long maxTime, long maxScore){
		return maxScore - (timeTaken/1000);
	}
}
