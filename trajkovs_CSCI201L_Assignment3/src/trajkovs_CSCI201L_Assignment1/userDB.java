package trajkovs_CSCI201L_Assignment1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class userDB /*implements Serializable*/ {
//	private static final long serialVersionUID = 2411285846743253752L;
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
		if (validateUser(username, password)) {
			throw new Exception("This password and username combintion does not exist");
		} else {
			Jeopardy.loginScreen.setVisible(false);
			Jeopardy.fileChooser.setVisible(true);
			saveUsers();
		}
	}
	
	protected void createUser(String username, String password) throws Exception {
		if (!validateUser(username, password)) {
			throw new Exception("This username already exists");
		} else {
			users.add(new User(username, password));
		}
	}
	
/*	private void addUser() {
		
	}*/
	
	// checks if the user exists. False if it exists, True otherwise
	// ignores case
	private boolean validateUser(String username, String password) {
		for (User user : users) {
			if (user.getUsername().equalsIgnoreCase(username))
				return false;
		}
		return true;
	}
	
	protected void saveUsers() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./data/userDB.txt"));
			for (User user : users) {
				oos.writeObject(user);
				oos.flush();
				oos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();// send 
		} catch (IOException e) {
			e.printStackTrace();// send
		}
	}
	
	protected void loadUsers() throws ClassNotFoundException {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./data/userDB.txt"));
			User user;
			while ((user = (User)ois.readObject()) != null)
				users.add(user);
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();	// send 
		} catch (IOException e) {
			e.printStackTrace(); // send
		}
	}
}
