package trajkovs_CSCI201L_Assignment1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// Liked the parser from the solution code so I used it
public class FileParser {

	private BufferedReader br;
	private FileReader fr;
	private File inputFile;

	protected String finalJeopardyQuestion;
	protected String finalJeopardyAnswer;
	
	//add the appropriate verbs and nouns to our sets
		
	//PARSING METHODS
	
	//filename is no longer passed in the GameData constructor like it did in Assignment 1 solution
	//it now is passed in a class method
	public void parseFile(File inputFile) throws Exception {
		Jeopardy.linesInFile = 0; // used for writting
		// reset before parsing
		GamePlay.resetVariables();
		this.inputFile = inputFile;
		openFile();
		
		// store the filename for later writting
		Jeopardy.gameFile = inputFile;
		Jeopardy.saveFile();
	}
	
	//private methods
	//must close our BufferedReader and FileReader!
	private void close() throws IOException{
		
		if (fr != null) fr.close();
		if (br != null) br.close();
	}
	
	//returns a String containing an error message, or, if no error, the empty string
	private void openFile() throws Exception{
		
		try {
			fr = new FileReader(inputFile);
			br = new BufferedReader(fr);
			//returns a possible error from parsing the categories and point values
			parseCategoriesAndPoints();
			
			//now error contains the error string returned from parsing all the questions from the file, including final jeopardy
			parseQuestions();
			
			// parse rating
		}
		catch (FileNotFoundException e) {
			throwException("File not found");
		}
		catch (IOException ioe){
			throwException("IOException");
		}
		finally{
			try {
				close();
			} catch (IOException e) {
				throwException("Issue with Closing the File");
			}
			
		}
	}
	
	private void throwException(String message) throws Exception{
//		clearData();
		throw new Exception(message);
	}
	
	//parse the categories and point values from the file
	private void parseCategoriesAndPoints() throws IOException, Exception{
		
		String categories = br.readLine();
		Jeopardy.linesInFile++;
		String[] parsedCategories = categories.split("::");
		//if the split string has an array of size other than 5, too much or not enough info on categories
		if (parsedCategories.length != 6){
			throwException("Too many or too few categories provided.");
		}

		//iterate through each category and make sure it is not whitespace
		for (String str : parsedCategories){
			
			if (str.trim().equals("")){
				throwException("One of the categories is whitespace.");
			}
		}
		
		String pointValues = br.readLine();
		Jeopardy.linesInFile++;
		String[] parsedPointValues = pointValues.split("::");
		
		//if the split string has an array of size other than 5, too much or not enough info on point values
		if (parsedPointValues.length != 7){
			throwException("Too many or too few dollar values provided.");
		}
		// check for duplicates
		if (Helpers.hasDuplicates(Arrays.copyOfRange(parsedPointValues, 0, 5)) || !Helpers.allNumbers(Arrays.copyOfRange(parsedPointValues, 0, 5))) {
      throwException("Wrong number of point values or duplicate point values");
    } 
		
		// Load categories
		Jeopardy.setCategories(Arrays.copyOfRange(parsedCategories, 0, 5));
		// Load the category image
		Jeopardy.categoryPath = parsedCategories[5];
		
		// Load point values
    Jeopardy.setPoints(Arrays.copyOfRange(parsedPointValues, 0, 5));
    // Load button labels
    Jeopardy.qBtnEnabledPath = parsedPointValues[5];
    Jeopardy.qBtnDisabledPath = parsedPointValues[6];
    // Sort the point values (for convenience)
    Arrays.sort(GamePlay.Points);
	}
	
	//this parses all the questions from the file, including final jeopardy, and return an error message if needed
	private void parseQuestions() throws IOException, Exception{
		
		//stores the current line we have read
		String currentLine = "";
		//stores the current concatenated question information
		String fullQuestion = "";
		int questionCount = 0;

		while(questionCount != 26){
			
			currentLine = br.readLine();
			Jeopardy.linesInFile++;
			//we have not reached 26 questions, so if currentLine is null, there are not enough questions
			if (currentLine == null){
				throwException("Not enough questions in the file");
			}
			//if the line does not start with '::', it is a continuation of the previous question and
			//should be concatenated onto fullQuestion
			if (!currentLine.startsWith("::")){
				fullQuestion += currentLine;
			}
			else{
				//if it does start with '::', then we must first parse the fullQuestion, and reset it to this new question
				//note that if the questionCount is 0, this is the first question in the file, so fullQuestion equals null
				//and does not need to be parsed
				if (questionCount != 0){
					//returns an error message associated with the parsing of this question
					parseQuestion(fullQuestion);
				}
				//substring currentLine so it does not start with '::' anymore
				fullQuestion = currentLine.substring(2);
				questionCount++;
			}
		}
		//after the loop, the question stored in fullQuestion has not been parsed. If we skipped this next line
		//we would be missing the last question
		parseQuestion(fullQuestion);

		// Parse file ranking
		String numPeople = br.readLine();
		Jeopardy.linesInFile++;
		String totalScore = br.readLine();
		Jeopardy.linesInFile++;
		try {
			int timesRanked = Integer.parseInt(numPeople);
			int score = Integer.parseInt(totalScore);
			parseRanking(timesRanked, score);
		} catch (NumberFormatException nfe) {
			if (numPeople.startsWith("::") || totalScore.startsWith("::"))
				throwException("Too many questions provided.");
			else throwException("Missing ranking values");
		}
		//if the next line is not null, there are too many questions/lines in the file
		if (br.readLine() != null){
			throwException("Too many questions provided.");
		}
		
		//if we never found the final jeopardy question, it is missing!
		if (GamePlay.FJQuestion == null){
			throwException("This game file does not have a final jeopardy question.");
		}
	}
	
	//parse a particular question into either a normal question, or the final jeopardy question
	private void parseQuestion(String question) throws Exception{
		
		if(question.toLowerCase().startsWith("fj")){
			//if we already have a final jeopardy question, we cannot have another and this must be an error
			if (GamePlay.FJQuestion != null){
				throwException ("Cannot have more than one final jeopardy question.");
			}
			else{
				parseFinalJeopardy(question);
			}
			
		}
		else{
			parseNormalQuestion(question);
		}
	}
	
	//parse the final jeopardy question data and return an error message if needed
	private void parseFinalJeopardy(String finalJeopardyString) throws Exception{
		
		String [] questionData = finalJeopardyString.split("::");
		
		//error checking for null values, and for too much data
		if (questionData.length != 3) throwException( "Too much or not enough data provided for the final jeopardy question.");
		
		if (questionData[1].trim().equals("")) throwException( "The Final Jeopardy question cannot be whitespace");
		
		if (questionData[2].trim().equals("")) throwException( "The Final Jeopardy answer cannot be whitespace");
		
		GamePlay.FJQuestion = new Question(questionData[1].trim(), questionData[2].trim());
	}
	
	//does not check whether there is a duplicate category/point value question
	private void parseNormalQuestion(String question) throws Exception{
		
		String [] questionData = question.split("::");
		
		if (questionData.length != 4) throwException( "Too much or not enough data provided for this question");
		//if we were not provided with too much data, we parse the data in the string
		else{
			String category = questionData[0].trim();
			
			//check if a category exist
			if (!hasCategory(category)) throwException("This category does not exist: " + category);
			
			Integer pointValue = -1;
			
			//try to parse the point value, surround with try catch in case it is a string
			try{
				pointValue = Integer.parseInt(questionData[1].trim());	
			}
			catch (NumberFormatException nfe){
				throwException("The point value cannot be a String.");
			}
			
			//check if a point exists
			if (!hasPoints(pointValue)) throwException("This point value does not exist: " + pointValue);
			
			//make sure the question and answer are not empty/white space
			if (questionData[2].trim().isEmpty()) throwException("The question cannot be whitespace.");
			
			if (questionData[3].trim().isEmpty()) throwException("The answer cannot be whitespace.");
			
      // if first time adding key
      if (GamePlay.Questions.get(category.toLowerCase()) == null)
        GamePlay.Questions.put(category.toLowerCase(), new ArrayList<Question>());
      
      // checks for questions with duplicate point values
      if (Jeopardy.pointsExist(category, pointValue)) {
        throwException("Duplicate point value!\nExiting...");
      }
      
      // Checks if the question exists. Only checks for same question not answer, since a question can't have 2 answers.
      // Only checks within the same category
      if (Helpers.questionExists(question, GamePlay.Questions.get(category.toLowerCase()))) {
        throwException("Duplicate question!\nExiting...");
      }
      
      GamePlay.Questions.get(category.toLowerCase()).add(new Question(category.toLowerCase(), pointValue, questionData[2].trim(), questionData[3].trim()));
		}
	}
	
	private void parseRanking(int timesRanked, int totalRanking) {
		Jeopardy.fileRanking.add(totalRanking);
		Jeopardy.fileRanking.add(timesRanked);
	}
	
	private boolean hasCategory(String cat) {
		return Helpers.elementExists(GamePlay.Categories, cat);
	}
	
	private boolean hasPoints(int pts) {
		return Helpers.elementExists(GamePlay.Points, pts);
	}
}
