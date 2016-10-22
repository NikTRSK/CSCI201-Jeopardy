package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import other.Helpers;
import other.userDB;

public class LoginScreen extends JFrame {
	private static final long serialVersionUID = 1L;
	// GUI Elements
	
	private JLabel loginLbl, jeopardyLbl, errorLbl;
	private JButton loginBtn, createAccountBtn;
	private JTextField usernameInputField, passwordInputField;
	
	// Create Border
  Border line = new LineBorder(Color.DARK_GRAY);
  Border margin = new EmptyBorder(5, 15, 5, 15);
  Border compound = new CompoundBorder(line, margin);
	
  // UserDatabase
  userDB db;
  
	public LoginScreen() {
		super("Welcome to Jeopardy");
		db = new userDB(); // create database
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeComponents() {
		
		// Create welcome Label
		loginLbl = new JLabel("login or create an account to play", SwingConstants.CENTER);
		loginLbl.setAlignmentX(CENTER_ALIGNMENT);
		loginLbl.setFont(new Font("Cambria", Font.BOLD, 30));
		loginLbl.setForeground(Color.WHITE);
		loginLbl.setBorder(new EmptyBorder(20,0,0,0));
		
		// Create prompt Label
		jeopardyLbl = new JLabel("Jeopardy", SwingConstants.CENTER);
		jeopardyLbl.setAlignmentX(CENTER_ALIGNMENT);
		jeopardyLbl.setFont(new Font("Cambria", Font.BOLD, 45));
		jeopardyLbl.setForeground(Color.WHITE);
		jeopardyLbl.setBorder(new EmptyBorder(20,0,0,0));
		
		// Error message
		errorLbl = new JLabel("\n", SwingConstants.CENTER);
		errorLbl.setAlignmentX(CENTER_ALIGNMENT);
		errorLbl.setFont(new Font("Cambria", Font.BOLD, 20));
		errorLbl.setForeground(Color.BLACK);
		errorLbl.setBorder(new EmptyBorder(20,0,0,0));
		
		// Login fields
  	usernameInputField = new JTextField();
  	Helpers.styleComponentFlat(usernameInputField, Color.BLACK, Color.WHITE, Color.WHITE, 19, true);
  	usernameInputField.setText("Username");
  	usernameInputField.setForeground(Color.LIGHT_GRAY);
  	usernameInputField.setPreferredSize(new Dimension(450, 60));
  	usernameInputField.setMinimumSize(new Dimension(60, 30));

  	passwordInputField = new JTextField();
  	Helpers.styleComponentFlat(passwordInputField, Color.BLACK, Color.WHITE, Color.WHITE, 19, true);
  	passwordInputField.setText("Password");
  	passwordInputField.setForeground(Color.LIGHT_GRAY);
  	passwordInputField.setPreferredSize(new Dimension(450, 60));
  	passwordInputField.setMinimumSize(new Dimension(60, 30));
  	
		/////////////////////
		// Navigation bar buttons //
		/////////////////////
		loginBtn = new JButton("Login");
		Helpers.styleComponentFlat(loginBtn, Color.WHITE, Color.DARK_GRAY, Color.DARK_GRAY, 22, true);
		loginBtn.setPreferredSize(new Dimension(230, 60));
		loginBtn.setMinimumSize(new Dimension(60, 30));
		loginBtn.setEnabled(false);
		
		createAccountBtn = new JButton("Create Account");
		Helpers.styleComponentFlat(createAccountBtn, Color.WHITE, Color.DARK_GRAY, Color.DARK_GRAY, 22, true);
		createAccountBtn.setPreferredSize(new Dimension(230, 60));
		createAccountBtn.setMinimumSize(new Dimension(60, 30));
		createAccountBtn.setEnabled(false);
	}
	
	private void createGUI() {
		setSize(800, 825);
		setLocation(100, 100);
		setVisible(true);
		
		// Create Grid Bag Constraints
		GridBagConstraints gbc = new GridBagConstraints();
		
		////////////////////////////////
		// Top part of Welcome Screen //
		////////////////////////////////
		JPanel welcomePanel = new JPanel(); welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
		welcomePanel.setBackground(new Color(0,137,123));
		welcomePanel.add(loginLbl);
		welcomePanel.add(jeopardyLbl);
		
		add(welcomePanel, BorderLayout.NORTH);
		
		//////////////////
		// CENTER PANEL //
		//////////////////
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setBackground(new Color(0,137,123));
		
		gbc.gridx = 1; gbc.gridy = 0; gbc.insets = new Insets(0,0,100,0); gbc.gridwidth = 2;
		mainPanel.add(errorLbl, gbc);
		gbc.gridx = 1; gbc.gridy = 2; gbc.insets = new Insets(0,0,30,0);
		mainPanel.add(usernameInputField, gbc);
		gbc.gridx = 1; gbc.gridy = 3;  gbc.insets = new Insets(0,0,100,0);
		mainPanel.add(passwordInputField, gbc);
		
		////////////////////
		// Navigation Bar //
		////////////////////
		JPanel navigationPanel = new JPanel(); navigationPanel.setLayout(new GridBagLayout());
		// cahnge this to just empty border
		navigationPanel.setBackground(new Color(0,137,123));
		navigationPanel.setBorder(new MatteBorder(0,0,60,0, new Color(0,0,0,0)));
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.fill = GridBagConstraints.HORIZONTAL; gbc2.insets = new Insets(0,0,0,5);//gbc2.weightx = 0.5;// gbc2.ipadx = 5;
		navigationPanel.add(loginBtn, gbc2);
		navigationPanel.add(createAccountBtn, gbc2);
		//		navigationPanel.add(loginBtn);
//		navigationPanel.add(createAccountBtn);
		add(navigationPanel, BorderLayout.SOUTH);
	}
	
	private void addEvents() {		
		// Listener for Window close
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// X Button Listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
//				Jeopardy.Users.saveUsers();	
				System.exit(0);
			}
		});
		
		// Listeners for Input fields
		usernameInputField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (((JTextField)e.getSource()).getText().equals("Username")) {
					((JTextField)e.getSource()).setText("");
					((JTextField)e.getSource()).setForeground(Color.BLACK);
				}
			}

			public void focusLost(FocusEvent e) {
				if (((JTextField)e.getSource()).getText().equals("")) {
					((JTextField)e.getSource()).setText("Username");
					((JTextField)e.getSource()).setForeground(Color.LIGHT_GRAY);
				}
			}
		});
		
		passwordInputField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (((JTextField)e.getSource()).getText().equals("Password")) {
					((JTextField)e.getSource()).setText("");
					((JTextField)e.getSource()).setForeground(Color.BLACK);
				}
			}

			public void focusLost(FocusEvent e) {
				if (((JTextField)e.getSource()).getText().equals("")) {
					((JTextField)e.getSource()).setText("Password");		
					((JTextField)e.getSource()).setForeground(Color.LIGHT_GRAY);
				}
			}
		});
		
		// enable buttons
		usernameInputField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				validInput();
			}

			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				validInput();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				validInput();
			}
		});
		
		passwordInputField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				validInput();
			}

			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				validInput();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				validInput();
			}
		});
		
		loginBtn.addActionListener((ActionEvent event) -> { // playing around with instant instantiation
			errorLbl.setText("\n");
			try {
				loginUser(usernameInputField.getText(), passwordInputField.getText());
				usernameInputField.setText("");
				passwordInputField.setText("");
			} catch (Exception e) {
				errorLbl.setText(e.getMessage());
			}
		});
		
		createAccountBtn.addActionListener((ActionEvent event) -> { // playing around with instant instantiation
			errorLbl.setText("\n");
			try {
				if (!db.createUser(usernameInputField.getText(), passwordInputField.getText()))
					errorLbl.setText("The username already exists");
			} catch (SQLException sqle) {
				System.out.println("SQLException: " + sqle);
			}
		});
	}
	
	private void validInput() {
		loginBtn.setEnabled(false);
		createAccountBtn.setEnabled(false);
		// check if any of the text boxes are empty
		if (!usernameInputField.getText().equalsIgnoreCase("username") && !usernameInputField.getText().equalsIgnoreCase("")) {
			if (!passwordInputField.getText().equalsIgnoreCase("password") && !passwordInputField.getText().equalsIgnoreCase("")) {
				loginBtn.setEnabled(true);
				createAccountBtn.setEnabled(true);
			}
		}
	}
	
	class dialogBox extends JOptionPane {
		private static final long serialVersionUID = 1L;
		public dialogBox() {
			UIManager.put("OptionPane.background", new Color(0,150,136));
			UIManager.put("Panel.background", new Color(0,150,136));
			UIManager.put("Panel.foreground", Color.WHITE);
			UIManager.put("Button.background", new Color(39,40,34));
			UIManager.put("Button.foreground", Color.WHITE);
		}
	}
	
	private void loginUser(String username, String password) {
		try {
			if (db.loginUser(username, password)) {
				new FileChooser(username).setVisible(true);
				dispose();
			} else
				errorLbl.setText("This password and username combintion does not exist");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("SQLException: " + e.getMessage());
		}
	}
}
