package GameLogic;

import java.io.Serializable;

public class Team implements Serializable {
	private static final long serialVersionUID = 6512517830077857944L; // auto-generated
	String name;
	int points;
	boolean answeredThisRound;
	
	public Team (String name) {
		this.name = name;
		this.points = 0;
		this.answeredThisRound = false;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getPoints() {
		return this.points;
	}
	
	public void addPoints(int pts) {
		this.points += pts;
	}
	
	public void subPoints(int pts) {
		this.points -= pts;
	}
	
	public void resetPoints() {
		this.points = 0;
	}
	
	public void setAnsweredThisRound() {
		this.answeredThisRound = true;
	}
	
	public boolean hasAnswered() {
		return this.answeredThisRound;
	}
	
	// will get reset at the beginning of each round
	public void clearSetAnsweredThisRound() {
		this.answeredThisRound = false;
	}
}
