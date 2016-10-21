package other;

import java.io.File;
import java.util.ArrayList;

public class GameConstants {
//	protected static userDB Users = new userDB();
//	protected static GameBoardUI GameBoard;
//	protected static FileChooser fileChooser = new FileChooser(); 
//	protected static LoginScreenUI loginScreen = new LoginScreenUI();
	public static ArrayList<Integer> fileRanking = new ArrayList<Integer>();
	
	// background images
	protected static String qBtnEnabledPath, qBtnDisabledPath, categoryPath;
	
	// used to track number of lines in files (used for reading)
	protected static int linesInFile;
	protected static File gameFile;
}
