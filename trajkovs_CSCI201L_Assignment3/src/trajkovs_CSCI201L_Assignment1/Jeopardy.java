package trajkovs_CSCI201L_Assignment1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import Question

public class Jeopardy {
	protected static userDB Users = new userDB();
	protected static GameBoardUI GameBoard;
	protected static FileChooser fileChooser = new FileChooser(); 
	protected static LoginScreenUI loginScreen = new LoginScreenUI();
	protected static ArrayList<Integer> fileRanking = new ArrayList<Integer>();
	
	// background images
	protected static String qBtnEnabledPath, qBtnDisabledPath, categoryPath;
	
	// used to track number of lines in files (used for reading)
	protected static int linesInFile;
	protected static File gameFile;
	
	// Adds the categories to the Categories variable
	protected static void setCategories(String [] cat) {
		GamePlay.Categories = cat.clone();
	}
	
	// Adds the point values to the Points variable
	protected static void setPoints(String [] pts) {
		for (int i = 0; i < pts.length; ++i) {
			GamePlay.Points[i] = Integer.parseInt(pts[i].trim());
		}
	}
	
	// Checks if the point value exists
	protected static boolean pointsExist(String cat, int pts) {
		ArrayList<Question> qs = GamePlay.Questions.get(cat.toLowerCase());
		for (Question q : qs) {
			if (q.getPointValue() == pts)
				return true;
		}
		return false;
	}
	
	// Checks if the game is loaded correctly
	protected static void checkValidGame(int qCount) {
		if ( !(qCount == 25 && GamePlay.FJQuestion != null) ) {	// when both a question and FJ missing
			throw new RuntimeException("Wrong number of questions");
		}
		
		if (GamePlay.Questions.size() != 5)
			throw new RuntimeException("Incorrect number of categories\n Terminating...");
		
		for (String key: GamePlay.Questions.keySet()) {
			if (GamePlay.Questions.get(key).size() != 5) {
				throw new RuntimeException("Incorrect number of point values\n Terminating...");
			}
		}
		if (Jeopardy.fileRanking.size() != 2)
			throw new RuntimeException("Missing ranking values\n Terminating...");
	}
	
	public static void createGameBoard() {
		fileChooser.setVisible(false);
		GameBoard = new GameBoardUI();
		GameBoard.setVisible(true);
	}
	
	public static void main(String [] args) {
		loginScreen.setVisible(true);
	}
	
	public static void saveFile() {
		BufferedReader br;
		FileReader fr;
		
		try {
			fr = new FileReader(gameFile);
			br = new BufferedReader(fr);
			String line;
			String input = "";
			while((line = br.readLine()) != null) input += line + '\n';
			
			br.close();
			System.out.println("*****************");
			System.out.println(input);
			System.out.println(input.split("\n").length);
			System.out.println(input.split("\n")[28]);
			
			String [] output = input.split("\n");
			output[output.length-2] = Integer.toString(Jeopardy.fileRanking.get(1)); // number of people
			output[output.length-1] = Integer.toString(Jeopardy.fileRanking.get(0)); // total score
			
			FileOutputStream outFile = new FileOutputStream(gameFile);
			for (int i = 0; i < output.length; ++i)
				outFile.write((output[i] + "\n").getBytes());
			
			outFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}