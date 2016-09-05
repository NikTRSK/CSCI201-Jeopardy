package trajkovs_CSCI201L_Assignment1;

public class Team {
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
}
