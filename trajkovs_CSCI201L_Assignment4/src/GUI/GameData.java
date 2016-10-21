package GUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import GameLogic.Question;
import GameLogic.Team;
import trajkovs_CSCI201L_Assignment1.Jeopardy;

public class GameData {
//	protected userDB Users = new userDB();
//	protected GameBoardUI GameBoard;
//	protected FileChooser fileChooser = new FileChooser(); 
//	protected LoginScreenUI loginScreen = new LoginScreenUI();
	protected ArrayList<Integer> fileRanking = new ArrayList<Integer>();
	protected String [] Categories = new String[5]; // holds all the categories for the game
	protected int [] Points = new int[5];	// holds the point values for the game
	protected HashMap<String, ArrayList<Question>> Questions = new HashMap<String, ArrayList<Question>>();	// holds all the questions
	protected Question FJQuestion = null;	// holds the Final Jeopardy question
	protected ArrayList<Team> Teams = new ArrayList<Team>(0); // holds all the teams
	protected int numTeams;
	int qsAnswered;
	
	// background images
	protected static String qBtnEnabledPath, qBtnDisabledPath, categoryPath;
	
	// used to track number of lines in files (used for reading)
	protected int linesInFile;
	protected File gameFile;
	
	// Adds the categories to the Categories variable
	public void setCategories(String [] cat) {
		Categories = cat.clone();
	}
	
	public void setButtonPath(String enabled, String disabled) {
		qBtnEnabledPath = enabled;
		qBtnDisabledPath = disabled;
	}
	
	public void setCategoryPath(String catPath) {
		categoryPath = catPath;
	}
	
	// Adds the point values to the Points variable
	public void setPoints(String [] pts) {
		for (int i = 0; i < pts.length; ++i) {
			Points[i] = Integer.parseInt(pts[i].trim());
		}
	// Sort the point values (for convenience). Also could have used an ordered map
		Arrays.sort(Points);
	}
	
	// returns true if FJQuestion exists
	public boolean FJExists() {
		return (FJQuestion != null);
	}
	
	public void setNumberOfQuestions(boolean quickPlay) {
		if (quickPlay)
			qsAnswered = 20;
		else
			qsAnswered = 0;
	}
	
	// Checks if the point value exists
	protected boolean pointsExist(String cat, int pts) {
		ArrayList<Question> qs = Questions.get(cat.toLowerCase());
		for (Question q : qs) {
			if (q.getPointValue() == pts)
				return true;
		}
		return false;
	}
	
	public int getNumTeams() {
		return numTeams;
	}
	
	public void initLines() {
		linesInFile = 0;
	}
	
	public void addLine() {
		linesInFile++;
	}
	
	public void setGameFile(File inFile) {
		gameFile = inFile;
	}
	
	public void setNumTeams(int nTeams) {
		numTeams = nTeams;
	}
	
	public void addTeam(String teamName) {
		Teams.add(new Team(teamName));
	}
	
	// Checks if the game is loaded correctly
	protected void checkValidGame(int qCount) {
		if ( !(qCount == 25 && FJQuestion != null) ) {	// when both a question and FJ missing
			throw new RuntimeException("Wrong number of questions");
		}
		
		if (Questions.size() != 5)
			throw new RuntimeException("Incorrect number of categories\n Terminating...");
		
		for (String key: Questions.keySet()) {
			if (Questions.get(key).size() != 5) {
				throw new RuntimeException("Incorrect number of point values\n Terminating...");
			}
		}
		if (fileRanking.size() != 2)
			throw new RuntimeException("Missing ranking values\n Terminating...");
	}
	
//	public static void createGameBoard() {
//		fileChooser.setVisible(false);
//		GameBoard = new GameBoardUI();
//		GameBoard.setVisible(true);
//	}
	
	// returns an in of the score. If no score returns -1
	public int getFileRanking() {
		if (fileRanking.size() != 2)
			return -1;
		else {
			return (fileRanking.get(0)/fileRanking.get(1));
		}
	}
	
	public void resetVariables() {
		Categories = null;
		Categories = new String[5]; // holds all the categories for the game
		Points = null;
		Points = new int[5];	// holds the point values for the game
		Questions = null;
		Questions = new HashMap<String, ArrayList<Question>>();	// holds all the questions
		FJQuestion = null;	// holds the Final Jeopardy question
		Teams = null;
		Teams = new ArrayList<Team>(0); // holds all the teams
		qBtnEnabledPath = null;
		qBtnDisabledPath = null;
		categoryPath = null;
		fileRanking = new ArrayList<Integer>();
		numTeams = 0;
	}
	
	// Reinitializes the game after replay/exit is called
/*	protected static void InitGame() {
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
		nextTeam = currTeam;
		// Set the number of answered questions to 0
		qsAnswered = 0;
		
		// Reset bets fr all teams
		Arrays.fill(FJBets, 0);
		Arrays.fill(FJAnswers, null);
	}*/
	
//	public static void main(String [] args) {
//		loginScreen.setVisible(true);
//	}
	
//	public static void saveFile() {
//		BufferedReader br;
//		FileReader fr;
//		
//		try {
//			fr = new FileReader(gameFile);
//			br = new BufferedReader(fr);
//			String line;
//			String input = "";
//			while((line = br.readLine()) != null) input += line + '\n';
//			
//			br.close();
//			
//			String [] output = input.split("\n");
//			output[output.length-2] = Integer.toString(Jeopardy.fileRanking.get(1)); // number of people
//			output[output.length-1] = Integer.toString(Jeopardy.fileRanking.get(0)); // total score
//			
//			FileOutputStream outFile = new FileOutputStream(gameFile);
//			for (int i = 0; i < output.length; ++i)
//				outFile.write((output[i] + "\n").getBytes());
//			
//			outFile.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
}
