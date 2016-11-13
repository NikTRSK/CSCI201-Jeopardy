package other;

public class GameConstants {
	static private String [][] Answers = { {"what", "who", "where", "when", "how"}, {"is", "are"} };	// used to check for question form of the answer
	
	static public boolean checkValidBeginningOfQuestion(String p1, String p2) {
		return Helpers.elementExists(Answers[0], p1) && Helpers.elementExists(Answers[1], p2);
	}
}
