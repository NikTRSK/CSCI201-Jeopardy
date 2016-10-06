package trajkovs_CSCI201L_Assignment1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class GamePlay {
	static protected String [] Categories = new String[5]; // holds all the categories for the game
	static protected int [] Points = new int[5];	// holds the point values for the game
	static protected HashMap<String, ArrayList<Question>> Questions = new HashMap<String, ArrayList<Question>>();	// holds all the questions
	static protected Question FJQuestion = null;	// holds the Final Jeopardy question
	static protected ArrayList<Team> Teams = new ArrayList<Team>(0); // holds all the teams
	
	static protected String [][] Answers = { {"what", "who", "where", "when", "how"}, {"is", "are"} };	// used to check for question form of the answer
	
	static protected int numTeams; 
	static protected int currTeam;
	static protected int qsAnswered;
	static protected Question currQuestion = null;
	static protected int numTries = 0;
	static protected int [] FJBets = new int[4]; // FJBets
	static protected String [] FJAnswers = new String[4];
	//////////////////
	// LIST METHODS //
	//////////////////
	
	// Lists all the available point values for a category (Questions that are unanswered)
	protected static void listPointValues(String cat) {
		ArrayList<Question> qs = Questions.get(cat.toLowerCase());
		for (int pts: Points) {
			for (Question q: qs) {
				if (q.getPointValue() == pts && !q.isAnswered())
					System.out.println(pts);
			}
		}
	}
	
	// Returns teh list of categories that have unanswered categories
	private static ArrayList<String> listCategories() {
		ArrayList<String> availCats = new ArrayList<String>(0);
		ArrayList<Integer> availPts = new ArrayList<Integer>(0);
		
		for (String cat: Categories) {
			ArrayList<Question> qs = Questions.get(cat.toLowerCase());
			
			for (Question q : qs) {
				if (!q.isAnswered()) {
					availPts.add(q.getPointValue());
				}
			}
			// If category has unanswered questions
			if (!availPts.isEmpty()) {
				System.out.println(cat);
				availCats.add(cat.toLowerCase());
			}
			availPts.clear();
		}
		return availCats; // return the categories with unanswered questions
	}
	
	/////////////////////////////////
	// GETTERS FOR CLASS VARIABLES //
	/////////////////////////////////
	
	// Returns the whole question object
	protected static Question getQuestion(String cat, int ptValue) {
		ArrayList<Question> qs = Questions.get(cat.toLowerCase());
		for (Question q: qs) {
			if (q.getPointValue() == ptValue)
				return q;
		}
		return null;
	}
	
	////////////////////
	// HELPER METHODS //
	////////////////////
	
	// Check if users answer is correct
	static protected boolean checkAnswer(String [] userAns, String answer) {
		String [] actual = answer.split("\\s+");
		
		// make sure that varying length works
		for (int i = 0; i < actual.length; ++i) {
			if (!actual[i].toLowerCase().trim().equals(userAns[i+2].toLowerCase()))
				return false;
		}
		return true;
	}
	
	// Prints all the stored questions
	protected static void displayAllQuestions() {
		for (String key: Questions.keySet()) {
			// Get all the Questions in a Category
			ArrayList<Question> qs = Questions.get(key);
			for (Question q: qs) {
				System.out.println(q.getCategory());
				System.out.println(q.getPointValue());
				System.out.println(q.getQuestion());
				System.out.println(q.getAnswer() + "\n");
			}
		}
	}
		
	// Show all the scores for all the teams
	protected static void showScores() { // add exceptions for when empty array
		for (Team team : Teams)
			System.out.println("Team: " + team.getName() + ", Points: " + team.getPoints());
	}

	// Checks if all teams have negative scores. Used before Final Jeopardy
	protected static boolean teamsAllNegative() {
		boolean teamPos = false;
		for (Team team : GamePlay.Teams) {
			if (team.getPoints() > 0)
				teamPos = true;
		}
		return !teamPos;
	}
	
	// Finds the winner of the game
	protected static ArrayList<Integer> findWinner() { // add exceptions for when empty array

		ArrayList<Integer> winner = new ArrayList<Integer>(0); 
		winner.add(0);
		for (int i = 1; i < Teams.size(); ++i) {
			if (Teams.get(i).getPoints() > Teams.get(winner.get(0)).getPoints()) {
				winner.clear();
				winner.add(i);
			} else if (Teams.get(i).getPoints() == Teams.get(winner.get(0)).getPoints())
				winner.add(i);
		}
		return winner;
	}
	
	// Check for Exit and Replay signals
	private static boolean checkGame(String input, Scanner userInput) {
		if (input.split("\\s+").length == 1) {
			if (input.toLowerCase().equals("replay")) {
				System.out.println("Restarting...\n");
				InitGame();
				PlayGame(userInput);
				return true;
			} else if (input.toLowerCase().equals("exit")) {
				System.out.println("Exiting...");
				System.exit(0);
			}
		}
		return false;
	}

	// Gameplay for regular questions
	public static void PlayGame(Scanner userInput) {
		// Create instance variables
		int currTeam = (int)(Math.random() * Teams.size());	// Randomly choose starting team
		int qsAnswered = 0;
		String catChoice;
		int ptChoice;
		
		while (qsAnswered < 25) {
			System.out.println("\n*** Team " + Teams.get(currTeam).getName() + " turn ***\n");
			
			////////////////////////
			// Category selection //
			////////////////////////
			while (true) {
				System.out.println("Choose a category:");
				ArrayList<String> cats = listCategories();
				catChoice = userInput.nextLine().trim().toLowerCase();
				if (checkGame(catChoice, userInput))	// check for replay or exit
					return; // If true means restart so don't continue this method
				if (!cats.contains(catChoice)) {
					System.out.println("Wrong Category choice!!!");
				} else if (Helpers.elementExists(Categories, catChoice))
					break;
			}

			//////////////////
			// Point choice //
			//////////////////
			while (true) {
				System.out.println("Choose a point value:");
				listPointValues(catChoice);
				String in = userInput.nextLine();
				if (checkGame(in, userInput))	// check for replay or exit
					return; // If true means restart so don't continue this method
				if (Helpers.isNumber(in)) {
					ptChoice = Integer.parseInt(in);
					// check if question is valid and not answered
					if (Helpers.elementExists(Points, ptChoice) && !getQuestion(catChoice, ptChoice).isAnswered())
						break;
					System.out.println("Wrong Point Value choice!!!");
				} else
					System.out.println("Wrong Point Value choice!!!");
			}
			
			//////////////////
			// Ask question //
			//////////////////
			Question currQuestion = getQuestion(catChoice, ptChoice);
			System.out.println("Question: " + currQuestion.getQuestion());
			int numTries = 0;	// holds the number of tries
			// Give the player a 2 tries
			while (numTries<= 2) {
				if (numTries == 2) {
					currQuestion.setAnswered();
					System.out.println("Wrong answer!!! " + ptChoice + " will be subtracted from your score.");
					Teams.get(currTeam).subPoints(ptChoice);
					System.out.println("The answer to the Question is: " + currQuestion.getAnswer());
					break;
				}
				else {
					System.out.print("Answer: ");
					String ans = userInput.nextLine();
					if (checkGame(ans, userInput))	// check for replay or exit
						return; // If true means restart so don't continue this method
		
					String [] ansBeginning = ans.split("\\s+");
					// Check if the user's answer begins with the proper format 
					if ( Helpers.elementExists(Answers[0], ansBeginning[0]) && Helpers.elementExists(Answers[1], ansBeginning[1]) ) {
						if (checkAnswer(ansBeginning, currQuestion.getAnswer())) { // if the answer is correct add points to the user
							currQuestion.setAnswered();
							System.out.println("Correct answer!!! " + ptChoice + " will be added to your score.");
							Teams.get(currTeam).addPoints(ptChoice);
						} else { // if not correct subtract points
							currQuestion.setAnswered();
							System.out.println("Wrong answer!!! " + ptChoice + " will be subtracted from your score.");
							Teams.get(currTeam).subPoints(ptChoice);
							System.out.println("The answer to the Question is: " + currQuestion.getAnswer());
						}
						break;
					} else { // If it doesn't give the user a second chance
						System.out.println("The answer needs to be posed as a question. Try again!");
						++numTries;
					}
				}
			}
			// Show current scores
			System.out.println("The current scores are: ");
			showScores();
			
			// Update current team
			updateCurrentTeam();
			// Answer the answer question count
			++qsAnswered;
		}
	}
	
	
	protected static void FinalJeopardy(Scanner userInput) {
		System.out.println("********************************");
		System.out.println("******** Final Jeopardy ********");
		System.out.println("********************************");
		
		System.out.println("Teams place your bets!!!");
		
		String bet;
		ArrayList<Integer> Bets = new ArrayList<Integer>();
		
		// Ask all teams to place bets
		for (Team team : Teams) {
			if (team.getPoints() <= 0) {
				System.out.println(team.getName() + " doesn't have enough funds to bet. Skipping...");
				Bets.add(0);
			} else {
				boolean correct = false;
				while (!correct) {
					System.out.print(team.getName() + ": ");
					bet = userInput.nextLine();
					if (checkGame(bet, userInput))
						return; // If true means restart so don't continue this method
					while (!Helpers.isNumber(bet.trim())) {
						System.out.println("Not a number. Please input a number for your bet.");
						bet = userInput.nextLine();
						if (checkGame(bet, userInput))	// check for replay or exit
							return; // If true means restart so don't continue this method
					}
					if (Integer.parseInt(bet.trim()) > team.getPoints() || Integer.parseInt(bet.trim()) <= 0)
						System.out.println("Invalid bed. Please enter a bet withing range.");
					else {
						Bets.add(Integer.parseInt(bet.trim()));
						correct = true;
					}
				}
			}
		}
		
		// Ask all the valid teams to answer the Final Jeoprady quesiton
		System.out.println(FJQuestion.getQuestion());
		for (int i = 0; i < Teams.size(); ++i) {
			// The team can answer only if it has positive balance
			if (Teams.get(i).getPoints() > 0) {
				System.out.print(Teams.get(i).getName() + " answer: ");
				String ans = userInput.nextLine();
				if (checkGame(ans, userInput))	// check for replay or exit
					return; // If true means restart so don't continue this method
	
				String [] ansBeginning = ans.split("\\s+");
				// Check if the answer is in a question form
				if ( Helpers.elementExists(Answers[0], ansBeginning[0]) && Helpers.elementExists(Answers[1], ansBeginning[1]) ) {
					if (checkAnswer(ansBeginning, FJQuestion.getAnswer())) {
						System.out.println("Correct answer!!! " + Bets.get(i) + " will be added to your score.");
						Teams.get(i).addPoints(Bets.get(i));
					} else {	// no second change in Final Jeopardy
						System.out.println("Wrong answer!!! " + Bets.get(i) + " will be subtracted from your score.");
						Teams.get(i).subPoints(Bets.get(i));
					}
				}
			}
		}
		System.out.println("The answer to the Final Jeopardy is: " + FJQuestion.getAnswer());
	}
	
	// Reinitializes the game after replay/exit is called
	protected static void InitGame() {
		for (Team team : Teams)
			team.points = 0;
		
		for (String key: Questions.keySet()) {
			// Get all the Questions in a Category
			ArrayList<Question> qs = Questions.get(key);
			for (Question q: qs) {
				q.setUnanswered();
			}			
		}
		// Generate the starting team
		currTeam = (int)(Math.random() * Teams.size());
		// Set the number of answered questions to 0
		qsAnswered = 0;
		
		// Reset bets fr all teams
		Arrays.fill(FJBets, 0);
		Arrays.fill(FJAnswers, null);
	}
	
	static protected void updateCurrentTeam() {
		// Update current team
		++currTeam;
		if (currTeam >= Teams.size())
			currTeam = 0;
	}
	
	static protected void resetVariables() {
		Categories = null;
		Categories = new String[5]; // holds all the categories for the game
		Points = null;
		Points = new int[5];	// holds the point values for the game
		Questions = null;
		Questions = new HashMap<String, ArrayList<Question>>();	// holds all the questions
		FJQuestion = null;	// holds the Final Jeopardy question
		Teams = null;
		Teams = new ArrayList<Team>(0); // holds all the teams
	}
}
