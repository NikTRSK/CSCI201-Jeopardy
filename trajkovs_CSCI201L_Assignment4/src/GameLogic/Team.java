package GameLogic;

import java.io.Serializable;

public class Team implements Serializable {
	private static final long serialVersionUID = 6512517830077857944L; // auto-generated
	String name;
	int points;
	
	public Team (String name) {
		this.name = name;
		this.points = 0;
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
}
