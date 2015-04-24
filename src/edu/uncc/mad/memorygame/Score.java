package edu.uncc.mad.memorygame;

public class Score {
	public static long calcSinglePlayerScore(long timeTaken, long maxTime, long maxScore) {
		return maxScore - (timeTaken / 1000);
	}

	public static long calcOneOnOneScore(long timeTaken, long maxScore) {
		long score = maxScore - (timeTaken / 1000);
		if (score < 10) {
			return 10;
		} else {
			return score;
		}
	}
}
