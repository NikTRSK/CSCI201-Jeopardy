package other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class userDB {
//	ArrayList<User> users = new ArrayList<User>();
	
	// SQL Connection
	Connection conn = null;
	Statement st = null;
	PreparedStatement loginStatement = null;
	PreparedStatement createStatement = null;
	ResultSet rs = null;
	
	public userDB() {
		initDB();
	}

	// returns true if user valid; false otherwise
	public boolean loginUser(String username, String password) throws SQLException {
		loginStatement.setString(1, username);
		rs = loginStatement.executeQuery();
		
		if (!rs.next()) {
			return false;
		}
		else {
			String uname = rs.getString("username");
			String pword = rs.getString("password");
			
			return true;
		}
	}
	
	// return false if duplicate user; true otherwise
	public boolean createUser(String username, String password) throws SQLException {
		// check if user exits
		loginStatement.setString(1, username);
		rs = loginStatement.executeQuery();
		if (rs.next())
			return false;
		else {
			// if valid insert user
			createStatement.setString(1, username);
			createStatement.setString(2, password);
			createStatement.executeUpdate();
			return true;
		}
	}
	
	private void initDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/UsersDB?user=root&password=root&useSSL=false");
			loginStatement = conn.prepareStatement("SELECT * FROM users WHERE username=?");
			createStatement = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
		} catch (SQLException sql) {
			System.out.println("sqle: " + sql.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		}
		
	}
}
