package trajkovs_CSCI201L_Assignment1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class userDB {
	ArrayList<User> users = new ArrayList<User>();
	
	public userDB() {
		try {
			loadUsers();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void loginUser(String username, String password) throws Exception {
		if (!validateCredentials(username, password)) {
			throw new Exception("This password and username combintion does not exist");
		} else {
			Jeopardy.loginScreen.setVisible(false);
			Jeopardy.fileChooser = new FileChooser();
			Jeopardy.fileChooser.setVisible(true);
			saveUsers();
		}
	}
	
	public static void logoutUser() {
		Jeopardy.loginScreen.setVisible(true);
		if (Jeopardy.GameBoard != null)
			Jeopardy.GameBoard.dispose();
		if (Jeopardy.fileChooser != null)
			Jeopardy.fileChooser.dispose();
		
		// clear variables
		GamePlay.resetVariables();
	}
	
	protected void createUser(String username, String password) throws Exception {
		if (!validateUser(username)) {
			throw new Exception("This username already exists");
		} else {
			users.add(new User(username, password));
			saveUsers();
		}
	}
	
	// checks if the user exists. False if it exists, True otherwise
	// ignores case
	private boolean validateUser(String username) {
		for (User user : users) {
			if (user.getUsername().equalsIgnoreCase(username))
				return false;
		}
		return true;
	}
	
	// Used for login. True if valid credentials
	private boolean validateCredentials(String username, String password) {
		for (User user : users) {
			if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password))
				return true;
		}
		return false;
	}
	
	protected void saveUsers() {
		ObjectOutputStream oos = null;
		try {
			File userFile = new File("./data/userDB.txt");
			try {
				userFile.createNewFile();
			} catch (IOException e) {/*nothing to do if file exists*/}
			oos = new ObjectOutputStream(new FileOutputStream(userFile, false));
			for (User user : users) {
				oos.writeObject(user);
				oos.flush();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error Reading file");
		} catch (IOException e) {
			System.out.println("Error Reading file");
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				System.out.println("Error Closing File");
			}
		}
	}
	
	protected void loadUsers() throws ClassNotFoundException {
		ObjectInputStream ois = null;
		File userFile = new File("./data/userDB.txt");
		if (userFile.exists()) {
			try {
				ois = new ObjectInputStream(new FileInputStream("./data/userDB.txt"));
				User user = (User)ois.readObject();
				while (user != null) {
					users.add(user);
					user = (User)ois.readObject();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();	// send 
			} catch (IOException e) { // throws exception at the end of file. nothing to process just keep going
			} finally {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void printAllUsers() {
		for(User u : users)
			System.out.println("Username: " + u.getUsername() + " || Password: " + u.getPassword());
	}
}
