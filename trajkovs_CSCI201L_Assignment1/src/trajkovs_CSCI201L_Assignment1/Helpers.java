package trajkovs_CSCI201L_Assignment1;

import java.util.ArrayList;

public class Helpers {
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
}

