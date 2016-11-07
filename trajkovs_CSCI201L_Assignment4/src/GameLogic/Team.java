package GameLogic;

import java.io.Serializable;

public class Team implements Serializable {
	private static final long serialVersionUID = 6512517830077857944L; // auto-generated
	String name;
	int points;
	Integer FJBet;
	boolean answeredThisRound;
	String FJAnswer;
	
	public Team (String name) {
		this.name = name;
		this.points = 0;
		this.answeredThisRound = false;
		this.FJBet = null;
		this.FJAnswer = null;
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
	
	public void setBet(int pts) {
		this.FJBet = pts;
	}
	
	public Integer getBet() {
		return this.FJBet;
	}
	
	public boolean isFJEligible() {
		return this.points > 0;
	}
	
	public boolean hasSetBet() {
		return this.FJBet != null;
	}
	
	public void setFinalJeopardyAnswer(String answer) {
		this.FJAnswer = answer;
	}
	
	public String getFinalJeopardyAnswer() {
		return this.FJAnswer;
	}
}
