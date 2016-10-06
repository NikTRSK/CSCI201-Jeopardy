package trajkovs_CSCI201L_Assignment1;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Helpers {
	public static String capitalize(String toCapitalize) {
		return toCapitalize.substring(0, 1).toUpperCase() + toCapitalize.substring(1);
	}
	
	public static boolean elementExists(String [] array, String value) {
		for (String toTest: array) {
			if ((toTest.toLowerCase()).equals(value.toLowerCase()))
				return true;
		}
		return false;
	}
	
	public static boolean elementExists(int [] array, int value) {
		for (int toTest: array) {
			if (toTest == value)
				return true;
		}
		return false;
	}
	
	// Reference of parseInt https://docs.oracle.com/javase/7/docs/api/java/lang/Integer.html#parseInt(java.lang.String,%20int)
	public static boolean isNumber(String input) {
		// if ParseInt throws exception we know it's not a digit
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException nfe) {}
		return false;
	}
	
	public static boolean allNumbers(String [] vals) {
		for (String val : vals) {
			if (!isNumber(val.trim()))
				return false;
		}
		return true;
	}
	
	// additionally checks for empty string
	public static boolean hasDuplicates(String [] in) {
		for (int i = 0; i < in.length; ++i) {
			//check if empty string
			if (in[i].trim().isEmpty())
				return true;
			for (int j = i + 1; j < in.length; ++j) {
				if (in[i].trim().equals(in[j].trim()))
					return true;
			}
		}
		return false;
	}
	
	// Checks if there are any empty elements in an array
	public static boolean checkArrayElements(String [] words) {
		for (int i = 0; i < words.length; ++i) {
			//check if empty string
			if (words[i].trim().isEmpty())
				return true;
		}
		return false;
	}
	
	public static String [] appendToArray(String [] original, String [] toAdd) {
		String [] newArray = new String[original.length+toAdd.length-1];
		for (int i = 0; i < original.length; ++i)
			newArray[i] = original[i];
		
		for (int i = 0; i < toAdd.length; ++i) {
//			System.out.println(original.length-1+i);
			if (newArray[original.length-1 + i] == null || newArray[original.length-1 + i].isEmpty())
				newArray[original.length-1 + i] = toAdd[i];
			else {
				String temp = newArray[original.length-1 + i] + toAdd[i];
				newArray[original.length-1 + i] = temp;
			}
		}
		
//		System.out.println("appendToArray: " + Arrays.toString(newArray));
		return newArray;
	}
	
	public static boolean arrayEmpty(String [] array, int startIdx, int endIdx) {
		for (int i = startIdx; i <= endIdx; ++i) {
			if (array[i].isEmpty())
				return true;
		}
		return false;
	}
	
	public static boolean questionExists(String question, ArrayList<Question> qlist) {
		for (Question q : qlist) {
			if (q.getQuestion().toLowerCase().equals(question.toLowerCase()))
				return true;
		}
		return false;
	}

	public static void styleComponent(JComponent item, Color backgroundColor, Color borderColor, int fontSize) {
		item.setBackground(backgroundColor);
		item.setOpaque(true);
		item.setBorder(new LineBorder(borderColor));
		item.setFont(new Font("Cambria", Font.BOLD, fontSize));
	}
	
	public static void styleComponentFlat(JComponent item, Color textColor, Color backgroundColor, Color borderColor, int fontSize, boolean opaque) {
		item.setFont(new Font("Cambria", Font.BOLD, fontSize));
		item.setForeground(textColor);
    // Make it look flat
    item.setBackground(backgroundColor);
		// Create Border
	  Border line = new LineBorder(borderColor);
	  Border margin = new EmptyBorder(5, 15, 5, 15);
	  Border compound = new CompoundBorder(line, margin);
    item.setBorder(compound);
    item.setOpaque(opaque);
	}
	
	// True - error; False - sucess
  public static void ParseFile(File input) {  
    String currLine; // holds the current line
    int questionCount = 0; // holds the number of questions loaded
    BufferedReader br = null;
    
    if (input == null) {
    	throw new RuntimeException("Error opening file.");
    }
    
    try {
      // Create the file stream
      br = new LineNumberReader(new FileReader(input));
      
      // Parse in the Categories
      currLine = br.readLine();
      String [] line = currLine.split("::", -1);
      // Check if there are duplicate categories
      if (line.length != 5 || Helpers.hasDuplicates(line)) {
      	throw new RuntimeException("Wrong number of categories or duplicate categories");
      }
      else
        Jeopardy.setCategories(line);

      // Parse in the Point values for questions
      currLine = br.readLine().trim();
      line = currLine.split("::", -1);
      // Check if there are duplicate point values or if they are all numbers
      if (line.length != 5 || Helpers.hasDuplicates(line) || !Helpers.allNumbers(line)) {
      	throw new RuntimeException("Wrong number of point values or duplicate point values");
      }
      else
        Jeopardy.setPoints(line);
    
      // Sort the point values (for convenience)
      Arrays.sort(GamePlay.Points);
      
      // Parse in the questions
      while ((currLine = br.readLine()) != null) {
        currLine.trim();
        // check if the line starts with ::
        if (!currLine.startsWith("::")) {
        	throw new RuntimeException("Wrong question format");
        } else {
          line = currLine.split("::", -1);
          // lookahed to see if the quesiton is on 2 lines
          try { br.mark(10000); }
          catch (IOException ioe) { throw new RuntimeException(ioe.getMessage()); }
          currLine = br.readLine();
          // Handles the last line of file
          if (currLine != null) {
            currLine.trim();
            // if the question has 2nd line
            if (!currLine.startsWith("::")) {
              line = Helpers.appendToArray(line, currLine.split("::"));
            } else {  // if it's not go back  
              try { br.reset(); }
              catch (IOException ioe) { throw new RuntimeException(ioe.getMessage()); } 
            }
          }
          if (Helpers.arrayEmpty(line, 1, line.length-1)) {
          	throw new RuntimeException("Wrong question format");
          }
          // error checking for valid category and, values
          String cat = line[1].trim();
          String question = "", answer = "";
          int pts = 0;

          // Check for FINAL JEOPARDY Question
          if (cat.toLowerCase().equals("fj")) {
            if (GamePlay.FJQuestion != null) {
            	throw new RuntimeException("Final Jeopardy question already exists! Exiting...");
            }
            if (line.length != 4) {
            	throw new RuntimeException("Wrong format for Final Jeopardy question!");
            }
            GamePlay.FJQuestion = new Question(line[2].trim(), line[3].trim());
          } else {  // Regular questions
            if (Helpers.isNumber(line[2]))
              pts = Integer.parseInt(line[2]);
            else {
            	throw new RuntimeException("Wrong question format1");
            }
            question = line[3].trim();
            answer = line[4].trim();
            
            // Create a new question and add it to the Question list
            if (Helpers.elementExists(GamePlay.Categories, cat) && Helpers.elementExists(GamePlay.Points, pts)) {
              // if first time adding key
              if (GamePlay.Questions.get(cat.toLowerCase()) == null)
                GamePlay.Questions.put(cat.toLowerCase(), new ArrayList<Question>());
              
              // checks for questions with duplicate point values
              if (Jeopardy.pointsExist(cat, pts)) {
              	throw new RuntimeException("Duplicate point value!\nExiting...");
              }
              
              // Checks if the question exists. Only checks for same question not answer, since a question can't have 2 answers.
              // Only checks within the same category
              if (Helpers.questionExists(question, GamePlay.Questions.get(cat.toLowerCase()))) {
              	throw new RuntimeException("Duplicate question!\nExiting...");
              }
              
              GamePlay.Questions.get(cat.toLowerCase()).add(new Question(cat.toLowerCase(), pts, question, answer));
              if (!cat.toLowerCase().equals("fj"))
                questionCount++;
            } else{
            	throw new RuntimeException("Category or Points Value invalid");
            }
          }
        }
      }
    } catch (FileNotFoundException fnfe) { throw new RuntimeException("FileNotFoundException: " + fnfe.getMessage()); }
      catch (IOException ioe) { throw new RuntimeException("IOException: " + ioe.getMessage()); }
      finally {
      // Close the file stream
      if (br != null) {
        try {
          br.close();
        } catch (IOException ioe) {
        	throw new RuntimeException(ioe.getMessage());
        }
      }
    }
    // See if everything was loaded correctly
    Jeopardy.checkValidGame(questionCount);
  }
  
}

