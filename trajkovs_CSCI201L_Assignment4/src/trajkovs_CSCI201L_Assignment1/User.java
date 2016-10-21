package trajkovs_CSCI201L_Assignment1;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 8334692572999921837L;
	private String username;
	private String password;
	
	public User (String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	protected String getUsername() {
		return username;
	}
	protected void setUsername(String username) {
		this.username = username;
	}
	
	protected String getPassword() {
		return password;
	}
	protected void setPassword(String password) {
		this.password = password;
	}
}
