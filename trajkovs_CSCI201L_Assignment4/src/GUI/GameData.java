package GUI;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import GameLogic.Question;
import GameLogic.Team;

public class GameData implements Serializable {
	private static final long serialVersionUID = -6502628326317732979L; // auto-generated
	
	private ArrayList<Integer> fileRanking = new ArrayList<Integer>();
	private String [] Categories = new String[5]; // holds all the categories for the game
	private int [] Points = new int[5];	// holds the point values for the game
	private HashMap<String, ArrayList<Question>> Questions = new HashMap<String, ArrayList<Question>>();	// holds all the questions
	private Question FJQuestion = null;	// holds the Final Jeopardy question
	private ArrayList<Team> Teams = new ArrayList<Team>(0); // holds all the teams
	private int numTeams, nextTeam, currTeam, qsAnswered;
	private boolean quickPlay;
	
	private int [] FJBets = new int[4]; // FJBets
	private String [] FJAnswers = new String[4];
	
	// background images
	private String qBtnEnabledPath, qBtnDisabledPath, categoryPath;
	// used to track number of lines in files (used for reading)
	private int linesInFile;
	private File gameFile;
	private boolean changePanel, correctAnswer, wrongAnswer, teamBet, inFJ;
	private int lastTeamBet; // holds the id of the last team that bet
	private String currPanel;
	private String buzzIn = null;
	private int selectedQCat, selectedQPtVal, teamJustBet;
	private int fileRatedByNumOfTeams;
	private boolean restartGame;
	private int playersWaiting;
	private String terminatedBy;
	private String gameServer;

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
	
	public String getCategoryPath() {
		return categoryPath;
	}
	
	public String getqBtnEnabledPath() {
		return qBtnEnabledPath;
	}
	
	public String getqBtnDisabledPath() {
		return qBtnDisabledPath;
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
	
	public void createFJQuestion(String q, String a) {
		FJQuestion = new Question(q, a);
	}
	
	public String getFJQuestion() {
		return FJQuestion.getQuestion();
	}
	
	public String getFJAnswer() {
		return FJQuestion.getAnswer();
	}
	
	public Question getQuestion(String category, int PointValue) {
		ArrayList<Question> qs = Questions.get(category.toLowerCase());
		for (Question q: qs) {
			if (q.getPointValue() == PointValue)
				return q;
		}
		return null;
	}
	
	// Check if users answer is correct
	public boolean checkAnswer(String [] userAns, String answer) {
		String [] actual = answer.split("\\s+");
		
		// make sure that varying length works
		for (int i = 0; i < actual.length; ++i) {
			if (!actual[i].toLowerCase().trim().equals(userAns[i+2].toLowerCase()))
				return false;
		}
		return true;
	}
	
	public int getNextTeam() {
		return nextTeam;
	}
	
	public int getCurrentTeam() {
		return currTeam;
	}
	
	public void setNextTeam(int setTo) {
		nextTeam = setTo;
	}
	
	public void updateCurrentTeam() {
		// Update current team
		++currTeam;
		if (currTeam >= Teams.size())
			currTeam = 0;
		
//		nextTeam = currTeam;
	}
	
	public void updateNextTeam() {
		// Update current team
		++nextTeam;
		if (nextTeam >= Teams.size())
			nextTeam = 0;
	}
	
	public void setNumberOfQuestions(boolean quickPlay) {
		if (quickPlay)
			qsAnswered = 20;
		else
			qsAnswered = 0;
		
		this.quickPlay = quickPlay;
	}
	
	public int getQsAnswered() {
		return qsAnswered;
	}
	
	public void qsAnsweredIncrement() {
		qsAnswered++;
	}
	
	// Checks if the point value exists
	public boolean pointsExist(String cat, int pts) {
		ArrayList<Question> qs = Questions.get(cat.toLowerCase());
		for (Question q : qs) {
			if (q.getPointValue() == pts)
				return true;
		}
		return false;
	}
	
	public int [] getAllPoints() {
		return Points;
	}
	
	public String [] getAllCategories() {
		return Categories;
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
	
	public ArrayList<Team> getAllTeams() {
		 return Teams;
	}
	
	public void setBetForTeam(int teamID, int betValue) {
		FJBets[teamID] = betValue;
		Teams.get(teamID).setBet(betValue);
	}
	
	public void setFJAnswerForTeam(int teamID, String answer) {
		FJAnswers[teamID] = answer;
	}
	
	public String [] getAllFJAnswers() {
		return FJAnswers;
	}
	
	public int [] getAllFJBets() {
		return FJBets;
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
	
	
	public boolean questionExists(String question, String category) {
		for (Question q : Questions.get(category.toLowerCase())) {
			if (q.getQuestion().toLowerCase().equals(question.toLowerCase()))
				return true;
		}
		return false;
	}
	
	public void addQuestion(String category, int pointValue, String question, String answer) throws Exception {
    // if first time adding key
    if (Questions.get(category.toLowerCase()) == null)
      Questions.put(category.toLowerCase(), new ArrayList<Question>());
    
    // checks for questions with duplicate point values
    if (pointsExist(category, pointValue)) {
      throwException("Duplicate point value!\nExiting...");
    }
    
    // Checks if the question exists. Only checks for same question not answer, since a question can't have 2 answers.
    // Only checks within the same category
    if (questionExists(question, category))
    	throwException("Duplicate question!\nExiting...");
		
		Questions.get(category.toLowerCase()).add(new Question(category.toLowerCase(), pointValue, question.trim(), answer.trim()));
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
	
	public void setFileRanking(int totalRanking, int timesRanked) {
		fileRanking.add(totalRanking);
		fileRanking.add(timesRanked);
	}
	
	public void updateFileRanking(int updateBy) {
		fileRatedByNumOfTeams++;
		fileRanking.set(0, fileRanking.get(0) + updateBy);
		fileRanking.set(1, fileRanking.get(1) + 1);
	}
	
	public ArrayList<Integer> getFileRankingItems() {
		return fileRanking;
	}
	
	public File getGameFile() {
		return gameFile;
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
	public void InitGame() {
		for (Team team : Teams)
			team.resetPoints();
		
		for (String key: Questions.keySet()) {
			// Get all the Questions in a Category
			ArrayList<Question> qs = Questions.get(key);
			for (Question q: qs) {
				q.setUnanswered();
			}			
		}
		generateStartingTeam();
		// Set the number of answered questions to 0
		if (quickPlay)
			qsAnswered = 20;
		else
			qsAnswered = 0;
		
		// Reset bets fr all teams
		Arrays.fill(FJBets, 0);
		Arrays.fill(FJAnswers, null);
		fileRatedByNumOfTeams = 0;
	}
	
	public void generateStartingTeam() {
		// Generate the starting team
		currTeam = (int)(Math.random() * Teams.size());
		nextTeam = currTeam;
	}
	
	// Checks if all teams have negative scores. Used before Final Jeopardy
	public boolean teamsAllNegative() {
		boolean teamPos = false;
		for (Team team : Teams) {
			if (team.getPoints() > 0)
				teamPos = true;
		}
		return !teamPos;
	}
	
	public ArrayList<Integer> findWinner() { // add exceptions for when empty array

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
	
	public int getPlayersWaiting() {
		return playersWaiting;
	}
	
	public void updatePlayersWaiting(int newValue) {
		playersWaiting = newValue;
	}
	
	public void updateSwitchingLogic(boolean changePanel, String currPanel) {
		this.changePanel = changePanel;
		this.currPanel = currPanel;
	}
	
	public void updateSwitchingLogic(boolean changePanel) {
		this.changePanel = changePanel;
	}
	
	public void updateSwitchingLogic(String currPanel) {
		this.currPanel = currPanel;
	}
	
	public String getCurrPanel() {
		return this.currPanel;
	}
	
	public boolean changePanel() {
		return this.changePanel;
	}
	
	public void setSelectedQuestion(int cat, int pt) {
		selectedQCat = cat;
		selectedQPtVal = pt;
	}
	
	public int getSelectedQuestionCat() {
		return selectedQCat;
	}
	
	public int getSelectedQuestionPtValue() {
		return selectedQPtVal;
	}
	
	public void setCorrectAnswer(boolean qAnswered) {
		correctAnswer = qAnswered;
	}
	
	public boolean correctAnswer() {
		return correctAnswer;
	}
	
	public void setWrongAnswer(boolean qAnswered) {
		wrongAnswer = qAnswered;
	}
	
	public boolean wrongAnswer() {
		return wrongAnswer;
	}
	
	public void buzzInTeam(String myTeamName) {
		buzzIn = myTeamName;
	}
	
	public String buzzedInTeam() {
		return buzzIn;
	}
	
	// runs before going back to the Question List panel
	public void resetTeamAnsweredFlag() {
		for (Team t : Teams)
			t.clearSetAnsweredThisRound();
	}
	
	public Team getTeam(int teamID) {
		return Teams.get(teamID);
	}
	
	public int findTeamID(String teamName) {
		for (int i = 0; i < Teams.size(); ++i) {
			if (Teams.get(i).getName().equals(teamName))
				return i;
		}
		return -1;
	}
	
	public boolean checkFJEligible(String teamName) {
		for (Team t : Teams) {
			if (t.getName().equals(teamName)) {
				if (t.getPoints() > 0)
					return true;
			}
		}
		return false;
	}
	
	public void setTeamJustBet(int id) {
		teamJustBet = id;
	}
	
	public int getTeamJustBet() {
		return teamJustBet;
	}
	
	public boolean teamBet() {
		return teamBet;
	}
	
	public void setLastTeamBet(int lastTeamBet) {
		this.lastTeamBet = lastTeamBet;
	}

	public int getLastTeamBet() {
		return this.lastTeamBet;
	}
	
	public void teamBet(boolean flag) {
		teamBet = flag;
	}
	
	public boolean inFinalJeopardy() {
		return inFJ;
	}

	public void inFinalJeopardy(boolean flag) {
		inFJ = flag;
	}
	
	public boolean allTeamsRatedFile() {
		return Teams.size() == fileRatedByNumOfTeams;
	}
	
	public void restartGame(boolean flag) {
		this.restartGame = flag;
	}
	
	public boolean restartGame() {
		return this.restartGame;
	}
	
	public void gameTerminatedBy(String user) {
		this.terminatedBy = user;
	}
	
	public String gameTerminatedBy() {
		return this.terminatedBy;
	}
	
	public void setGameServer(String gs) {
		this.gameServer = gs;
	}
	
	public String gameServerIs() {
		return this.gameServer;
	}
	
	private void throwException(String message) throws Exception {
//	clearData();
		throw new Exception(message);
	}
}
