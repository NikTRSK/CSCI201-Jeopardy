package trajkovs_CSCI201L_Assignment1;

import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
//import Question

public class Jeopardy {
	
	static private int numTeams;

	public void ParseFile(String input) {	
		String currLine; // holds the current line
		int questionCount = 0; // holds the number of questions loaded
		BufferedReader br = null;
		
		try {
			// Create the file stream
			br = new LineNumberReader(new FileReader(input));
			
			// Parse in the Categories
			currLine = br.readLine();
			String [] line = currLine.split("::", -1);
			// Check if there are duplicate categories
			if (line.length != 5 || Helpers.hasDuplicates(line)) {
				System.out.println("Wrong number of categories or duplicate categories");
				System.exit(1);
			}
			else
				setCategories(line);

			// Parse in the Point values for questions
			currLine = br.readLine().trim();
			line = currLine.split("::", -1);
			// Check if there are duplicate point values or if they are all numbers
			if (line.length != 5 || Helpers.hasDuplicates(line) || !Helpers.allNumbers(line)) {
				System.out.println("Wrong number of point values or duplicate point values");
				System.exit(1);
			}
			else
				setPoints(line);
		
			// Sort the point values (for convenience)
			Arrays.sort(GamePlay.Points);
			
			// Parse in the questions
			while ((currLine = br.readLine()) != null) {
				currLine.trim();
				// check if the line starts with ::
				if (!currLine.startsWith("::")) {
					System.out.println("Wrong question format");
					System.exit(1);
				} else {
					line = currLine.split("::", -1);
					// lookahed to see if the quesiton is on 2 lines
					try { br.mark(10000); }
					catch (IOException ioe) { System.out.println(ioe.getMessage()); }
					currLine = br.readLine();
					// Handles the last line of file
					if (currLine != null) {
						currLine.trim();
						// if the question has 2nd line
						if (!currLine.startsWith("::")) {
							line = Helpers.appendToArray(line, currLine.split("::"));
						} else {	// if it's not go back  
							try { br.reset(); }
							catch (IOException ioe) { System.out.println(ioe.getMessage()); } 
						}
					}
					if (Helpers.arrayEmpty(line, 1, line.length-1)) {
						System.out.println("Wrong question format");
						System.exit(1);						
					}
					// error checking for valid category and, values
					String cat = line[1].trim();
					String question = "", answer = "";
					int pts = 0;

					// Check for FINAL JEOPARDY Question
					if (cat.toLowerCase().equals("fj")) {
						if (GamePlay.FJQuestion != null) {
							System.out.println("Final Jeopardy question already exists! Exiting...");
							System.exit(1);
						}
						if (line.length != 4) {
							System.out.println("Wrong format for Final Jeopardy question!");
							System.exit(1);
						}
						GamePlay.FJQuestion = new Question(line[2].trim(), line[3].trim());
					} else {	// Regular questions
						if (Helpers.isNumber(line[2]))
							pts = Integer.parseInt(line[2]);
						else {
							System.out.println("Wrong question format1");
							System.exit(1);
						}
						question = line[3].trim();
						answer = line[4].trim();
						
						// Create a new question and add it to the Question list
						if (Helpers.elementExists(GamePlay.Categories, cat) && Helpers.elementExists(GamePlay.Points, pts)) {
							// if first time adding key
							if (GamePlay.Questions.get(cat.toLowerCase()) == null)
								GamePlay.Questions.put(cat.toLowerCase(), new ArrayList<Question>());
							
							// checks for questions with duplicate point values
							if (pointsExist(cat, pts)) {
								System.out.println("Duplicate point value!\nExiting...");
								System.exit(1);
							}
							
							// Checks if the question exists. Only checks for same question not answer, since a question can't have 2 answers.
							// Only checks within the same category
							if (Helpers.questionExists(question, GamePlay.Questions.get(cat.toLowerCase()))) {
								System.out.println("Duplicate question!\nExiting...");
								System.exit(1);
							}
							
							GamePlay.Questions.get(cat.toLowerCase()).add(new Question(cat.toLowerCase(), pts, question, answer));
							if (!cat.toLowerCase().equals("fj"))
								questionCount++;
						} else{
							System.out.println("Category or Points Value invalid");
							System.exit(1);;
						}
					}
				}
			}
		} catch (FileNotFoundException fnfe) { System.out.println("FileNotFoundException: " + fnfe.getMessage()); }
			catch (IOException ioe) { System.out.println("IOException: " + ioe.getMessage());	}
			finally {
			// Close the file stream
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			}
		}
		// See if everything was loaded correctly
		checkValidGame(questionCount);
	}
		
	private void GenerateTeams(Scanner userIn) {
//		Scanner userIn = new Scanner(System.in);
		numTeams = 0;

		System.out.print("Please enter the number of teams that will be playing the game (1-4): ");
		while (numTeams < 1 || numTeams > 4) {
			String in = userIn.nextLine();
			if (!in.isEmpty()) {
				if (Helpers.isNumber(in)) {
					numTeams = Integer.parseInt(in);
	
					if (numTeams < 1 || numTeams > 4)
						System.out.println("Invalid number of teams! Please try again!");
				} else
					System.out.println("Not a number. Please input a number between 1 and 4!");
			} else
				System.out.println("Not a number. Please input a number between 1 and 4!");
		}
		
		for (int i = 1; i <= numTeams; ++i) {
			System.out.print("Enter name for team " + i + " (Deafult 'Team " + i + "'): ");
			String teamName = (userIn.nextLine()).trim();
			if (teamName.isEmpty())
				teamName = "Team " + i;
			GamePlay.Teams.add(new Team(teamName));
		}
	}
	
	// Adds the categories to the Categories variable
	private void setCategories(String [] cat) {
		GamePlay.Categories = cat.clone();
	}
	
	// Adds the point values to the Points variable
	private void setPoints(String [] pts) {
		for (int i = 0; i < pts.length; ++i) {
			GamePlay.Points[i] = Integer.parseInt(pts[i].trim());
		}
	}
	
	// Checks if the point value exists
	private boolean pointsExist(String cat, int pts) {
		ArrayList<Question> qs = GamePlay.Questions.get(cat.toLowerCase());
		for (Question q : qs) {
			if (q.getPointValue() == pts)
				return true;
		}
		return false;
	}
	
	// Checks if the game is loaded correctly
	private void checkValidGame(int qCount) {
		if ( !(qCount == 25 && GamePlay.FJQuestion != null) ) {	// when both a question and FJ missing
			System.out.println("Wrong number of questions");
			System.exit(1);
		}
		
		if (GamePlay.Questions.size() != 5) {
			System.out.println("Incorrect number of categories\n Terminating...");
			System.exit(1);
		}
		
		for (String key: GamePlay.Questions.keySet()) {
			if (GamePlay.Questions.get(key).size() != 5) {
				System.out.println("Incorrect number of point values\n Terminating...");
				System.exit(1);
			}
		}
	}
	
	public static void main(String [] args) {

		// Init the game and parse in input file
		Jeopardy PlayGame = new Jeopardy();
		PlayGame.ParseFile(args[0]);
		
		Scanner userInput = new Scanner(System.in);
		PlayGame.GenerateTeams(userInput);
		
		System.out.println("====================================\n======= Welcome to Jeopardy! =======\n====================================");
		System.out.println("Thank you! Setting up the game for you...");
		System.out.println("Ready to play!");
		
		System.out.println("The Game will autoplay now...\n");
		///////////////
		// PLAY GAME //
		///////////////
		GamePlay.PlayGame(userInput);
		
		if (GamePlay.teamsAllNegative())
			System.out.println("All teams have a score of 0 or less. There are no winners");
		else {
			GamePlay.FinalJeopardy(userInput);
			// After final jeopardy check again if there are teams that have a score of 0
			if (GamePlay.teamsAllNegative())
				System.out.println("All teams have a score of 0. There are no winners");
			else
				GamePlay.showWinner();
		}

		userInput.close();
		System.out.println("\n--Game Finished--\nThank you for playing.");		
	}
}