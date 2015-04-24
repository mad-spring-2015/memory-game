package edu.uncc.mad.memorygame.oneonone;

public enum OneOnOneGameStatus {
	PLAYING("Playing"), EXITED("Exited"), DONE("Done");
	private String text;

	private OneOnOneGameStatus(String text){
		this.text = text;
	}

	@Override
	public String toString() {
		return this.text;
	}
	
}
