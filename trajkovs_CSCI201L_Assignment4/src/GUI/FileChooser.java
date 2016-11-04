package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import GameLogic.GameClient;
import networking.GameServer;
import other.FileParser;
import other.Helpers;

public class FileChooser extends JFrame {
	private static final long serialVersionUID = 1L;
	private GameData gameData;
	
	// GUI Elements
	private JLabel welcomeLbl, promptLbl, fileChooserLbl, fileNameLbl, teamPromptLbl, avgRatingLbl, waitingLbl;
	private JLabel [] teamLbls = new JLabel[4];
	private JTextField [] teamTxtBoxes = new JTextField[4];
	private JButton chooseFileBtn, startBtn, clearBtn, logoutBtn, exitBtn;
	private JSlider teamSelectSlider;
	private JFileChooser chooseFile;
	static protected JCheckBox quickPlay;
	private File inputFile;
	static private JPanel selectTeamPanel; // used with error popup
	private JPanel connectionSettingsPanel;
	
	private JRadioButton notNetworkedRadio, hostGameRadio, joinGameRadio;
	private JTextField portArea, ipArea;
	
	String loggedInUser;
	transient GameServer gs = null;
	transient GameClient gc = null;
	
	// Create Border
  Border line = new LineBorder(Color.DARK_GRAY);
  Border margin = new EmptyBorder(5, 15, 5, 15);
  Border compound = new CompoundBorder(line, margin);
	
	public FileChooser(String loggedInUser) {
		super("Welcome to Jeopardy");
		this.loggedInUser = loggedInUser;
		gameData = new GameData();
		initializeComponents();
		createGUI();
		addEvents();
		notNetworkedRadio.setSelected(true);
		setupNotNetworkedUI();
	}
	
	private void initializeComponents() {
		
		// Create welcome Label
		welcomeLbl = new JLabel("Welcome to Jeopardy!");
		welcomeLbl.setFont(new Font("Cambria", Font.BOLD, 45));
		welcomeLbl.setForeground(Color.WHITE);
		welcomeLbl.setBorder(new EmptyBorder(0,100,0,0));
		
		// Create Quick Play checkbox
		quickPlay = new JCheckBox("Quick Play");
		quickPlay.setBackground(new Color(0,137,123));
		quickPlay.setForeground(Color.WHITE);
		quickPlay.setFont(new Font("Cambria", Font.BOLD, 15));
		
		// Create prompt Label
		promptLbl = new JLabel("Choose the game file, number of teams, and team names before starting the game", SwingConstants.CENTER);
		promptLbl.setAlignmentX(CENTER_ALIGNMENT);
		promptLbl.setFont(new Font("Cambria", Font.BOLD, 18));
		promptLbl.setForeground(Color.WHITE);
		promptLbl.setBorder(new EmptyBorder(20,0,0,0));
		
		//////////////////
		// Select Game //
		/////////////////
		notNetworkedRadio = new JRadioButton("Not Networked");
		notNetworkedRadio.setOpaque(false);
		notNetworkedRadio.setFont(new Font("Cambria", Font.BOLD, 18));
		notNetworkedRadio.setForeground(Color.WHITE);
		
		hostGameRadio = new JRadioButton("Host Game");
		hostGameRadio.setOpaque(false);
		hostGameRadio.setFont(new Font("Cambria", Font.BOLD, 18));
		hostGameRadio.setForeground(Color.WHITE);
		
		joinGameRadio = new JRadioButton("Join Game");
		joinGameRadio.setOpaque(false);
		joinGameRadio.setFont(new Font("Cambria", Font.BOLD, 18));
		joinGameRadio.setForeground(Color.WHITE);
		
		ButtonGroup gameOptBtnGroup = new ButtonGroup();
		gameOptBtnGroup.add(notNetworkedRadio);
		gameOptBtnGroup.add(hostGameRadio);
		gameOptBtnGroup.add(joinGameRadio);
		
		// Connection components
		 portArea = new JTextField();
		 Helpers.styleComponentFlat(portArea, Color.BLACK, Color.WHITE, Color.DARK_GRAY, 17, true);
		 portArea.setPreferredSize(new Dimension(200, 30));
		 ipArea = new JTextField();
		 Helpers.styleComponentFlat(ipArea, Color.BLACK, Color.WHITE, Color.DARK_GRAY, 17, true);
		 ipArea.setPreferredSize(new Dimension(200, 30));
		
		//////////////////
		// File Chooser //
		//////////////////
		fileChooserLbl = new JLabel("Please choose a game file.");
		fileChooserLbl.setFont(new Font("Cambria", Font.BOLD, 17));
		fileChooserLbl.setForeground(Color.WHITE);
		
		chooseFileBtn = new JButton("Choose File");
		Helpers.styleComponentFlat(chooseFileBtn, Color.WHITE, Color.DARK_GRAY, Color.DARK_GRAY, 17, true);
    
		fileNameLbl = new JLabel("", SwingConstants.CENTER);
		fileNameLbl.setPreferredSize(new Dimension(100, 20));
		fileNameLbl.setFont(new Font("Cambria", Font.BOLD, 17));
		fileNameLbl.setForeground(Color.WHITE);
		
		avgRatingLbl = new JLabel("", SwingConstants.CENTER);
//		avgRatingLbl.setPreferredSize(new Dimension(100, 20));
		avgRatingLbl.setFont(new Font("Cambria", Font.BOLD, 17));
		avgRatingLbl.setForeground(Color.WHITE);
		
    //////////////////
    // Team Select //
    /////////////////
		teamPromptLbl = new JLabel("Please choose the number of teams that will be playing on the slider below.");
		teamPromptLbl.setFont(new Font("Cambria", Font.BOLD, 17));
		teamPromptLbl.setForeground(Color.WHITE);
		teamPromptLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		teamPromptLbl.setBorder(new EmptyBorder(0,0,5,0));
		
		createTeamSlider();
		
		///////////////
		// Team List //
		///////////////
		for (int teamLbl = 0; teamLbl < 4; ++teamLbl) {
			teamLbls[teamLbl] = new JLabel("Please name Team " + (teamLbl+1));
			Helpers.styleComponentFlat(teamLbls[teamLbl], Color.WHITE, Color.DARK_GRAY, Color.DARK_GRAY, 22, true);
			teamLbls[teamLbl].setVisible(false);
		}
		
		for (int team = 0; team < 4; ++team) {
			teamTxtBoxes[team] = new JTextField("");
			Helpers.styleComponentFlat(teamTxtBoxes[team], Color.WHITE, Color.GRAY, Color.DARK_GRAY, 22, true);
			teamTxtBoxes[team].setPreferredSize(new Dimension(teamLbls[0].getPreferredSize()));
			teamTxtBoxes[team].setVisible(false);
		}

		// Waiting label
		waitingLbl = new JLabel("\n", SwingConstants.CENTER);
		waitingLbl.setFont(new Font("Cambria", Font.BOLD, 18));
		waitingLbl.setForeground(Color.WHITE);
		
		/////////////////////
		// Navigation bar buttons //
		/////////////////////
		startBtn = new JButton("Start Jeopardy");
		Helpers.styleComponentFlat(startBtn, Color.WHITE, Color.DARK_GRAY, Color.DARK_GRAY, 22, true);
		startBtn.setPreferredSize(new Dimension(180, 45));
		startBtn.setEnabled(false);
		
		clearBtn = new JButton("Clear Choices");
		Helpers.styleComponentFlat(clearBtn, Color.WHITE, Color.DARK_GRAY, Color.DARK_GRAY, 22, true);
		clearBtn.setPreferredSize(new Dimension(180, 45));
		
		logoutBtn = new JButton("Logout");
		Helpers.styleComponentFlat(logoutBtn, Color.WHITE, Color.DARK_GRAY, Color.DARK_GRAY, 22, true);
		logoutBtn.setPreferredSize(new Dimension(180, 45));
		
		exitBtn = new JButton("Exit");
		Helpers.styleComponentFlat(exitBtn, Color.WHITE, Color.DARK_GRAY, Color.DARK_GRAY, 22, true);
		exitBtn.setPreferredSize(new Dimension(180, 45));		
	}
	
	private void createGUI() {
		setSize(800, 825);
		setLocation(100, 100);
		
		// Create Grid Bag Constraints
		GridBagConstraints gbc = new GridBagConstraints();
		
		////////////////////////////////
		// Top part of Welcome Screen //
		////////////////////////////////
		JPanel welcomePanelTop = new JPanel(); welcomePanelTop.setLayout(new BoxLayout(welcomePanelTop, BoxLayout.X_AXIS));
		welcomePanelTop.setBackground(new Color(0,137,123));
		JPanel welcomeLblPanel = new JPanel();
		welcomeLblPanel.add(welcomeLbl); welcomeLblPanel.setBackground(new Color(0,137,123));
		welcomePanelTop.add(welcomeLblPanel);
		welcomePanelTop.add(quickPlay);
		
		JPanel welcomePanel = new JPanel(); welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
		welcomePanel.setBackground(new Color(0,137,123));
		welcomePanel.add(welcomePanelTop);
		welcomePanel.add(promptLbl);
		
		JPanel gameOptPanel = new JPanel();
		gameOptPanel.setOpaque(false);
		gameOptPanel.add(notNetworkedRadio);
		gameOptPanel.add(hostGameRadio);
		gameOptPanel.add(joinGameRadio);
		
		welcomePanel.add(gameOptPanel);
		
		add(welcomePanel, BorderLayout.NORTH);
		
		//////////////////
		// CENTER PANEL //
		//////////////////
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setBackground(new Color(9,204,185));
		
		//////////////////////
		// CONNECTION PANEL //
		//////////////////////
		connectionSettingsPanel = new JPanel();
		connectionSettingsPanel.setOpaque(false);
		connectionSettingsPanel.add(portArea);
		connectionSettingsPanel.add(ipArea);
		
		JPanel dummyConnPanel = new JPanel();
		dummyConnPanel.setOpaque(false);
		dummyConnPanel.setPreferredSize(new Dimension((int)mainPanel.getPreferredSize().getWidth(), 40));
		dummyConnPanel.add(connectionSettingsPanel);
		
		mainPanel.add(dummyConnPanel);
		//////////////////////////
		// File / Team chooser //
		/////////////////////////
		JPanel filePanel = new JPanel();
		filePanel.setBackground(new Color(9,204,185));
		FlowLayout filePanelLayout = new FlowLayout();
		filePanelLayout.setHgap(5);
		filePanel.setLayout(filePanelLayout);
		filePanel.add(fileChooserLbl);
		filePanel.add(chooseFileBtn);
		filePanel.add(fileNameLbl);
		filePanel.add(avgRatingLbl);
		mainPanel.add(filePanel);
		
		// Select number of teams
		selectTeamPanel = new JPanel();
		selectTeamPanel.setLayout(new BoxLayout(selectTeamPanel, BoxLayout.Y_AXIS));
		selectTeamPanel.setBackground(new Color(9,204,185));
		selectTeamPanel.add(teamPromptLbl);
		selectTeamPanel.add(teamSelectSlider);
		mainPanel.add(selectTeamPanel);
		
		// Team list
		JPanel teamListPanel = new JPanel();
		teamListPanel.setLayout(new GridBagLayout());
		teamListPanel.setBackground(new Color(9,204,185));
		mainPanel.add(teamListPanel);
		gbc.weightx = 1;
		gbc.gridx = 1; gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 2, 0);
		teamListPanel.add(teamLbls[0], gbc);
		gbc.gridx = 1; gbc.gridy = 2;
		gbc.insets = new Insets(0, 0, 25, 0);
		teamListPanel.add(teamTxtBoxes[0], gbc);
		
		gbc.gridx = 1; gbc.gridy = 4;
		gbc.insets = new Insets(0, 0, 2, 0);
		teamListPanel.add(teamLbls[1], gbc);
		gbc.gridx = 1; gbc.gridy = 5;
		gbc.insets = new Insets(0, 0, 25, 0);
		teamListPanel.add(teamTxtBoxes[1], gbc);
		
		gbc.gridx = 2; gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 2, 0);
		teamListPanel.add(teamLbls[2], gbc);
		gbc.gridx = 2; gbc.gridy = 2;
		gbc.insets = new Insets(0, 0, 25, 0);
		teamListPanel.add(teamTxtBoxes[2], gbc);
		
		gbc.gridx = 2; gbc.gridy = 4;
		gbc.insets = new Insets(0, 0, 2, 0);
		teamListPanel.add(teamLbls[3], gbc);
		gbc.gridx = 2; gbc.gridy = 5;
		gbc.insets = new Insets(0, 0, 25, 0);
		teamListPanel.add(teamTxtBoxes[3], gbc);		
		// Default state show only Team 1
		teamLbls[0].setVisible(true);
		teamTxtBoxes[0].setVisible(true);
		
		
		mainPanel.add(waitingLbl);
		////////////////////
		// Navigation Bar //
		////////////////////
		JPanel navigationPanel = new JPanel();
		// cahnge this to just empty border
		navigationPanel.setBackground(new Color(9,204,185));
	  Border navCompound = new CompoundBorder(new LineBorder(new Color(9,204,185)), new EmptyBorder(30, 0, 30, 0));
		navigationPanel.setBorder(navCompound);
		navigationPanel.add(startBtn);
		navigationPanel.add(clearBtn);
		navigationPanel.add(logoutBtn);
		navigationPanel.add(exitBtn);
		add(navigationPanel, BorderLayout.SOUTH);
	}
	
	private void addEvents() {		
		// Listener for Window close
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// X Button Listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				String Btns[] = {"Yes", "No"};
				new dialogBox();
				int result = dialogBox.showOptionDialog(null, "Are you sure you want to quit?", "Quit Program", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, Btns, Btns[1]);
				if (result == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});
		
		// Choose input file
		chooseFileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		    chooseFile = new JFileChooser();
		    chooseFile.setCurrentDirectory(new File(System.getProperty("user.dir")));
		    chooseFile.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
		    
		    int returnVal = chooseFile.showOpenDialog(null);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						inputFile = chooseFile.getSelectedFile();
						// parse input file
						FileParser parse = new FileParser();
						parse.parseFile(inputFile, gameData);
						
						fileNameLbl.setText(inputFile.getName());
						validInput();
						int ranking = gameData.getFileRanking();
						if (ranking == -1)
							avgRatingLbl.setText("average rating: N/A");
						else
							avgRatingLbl.setText("average rating: " + gameData.getFileRanking() + "/5");
					} catch (Exception rte) {
						System.out.println(rte.getMessage());
						displayPopup(rte.getMessage());
						gameData.resetVariables();		
					}
		    }
			}
		});
		
		// GAME OPTION LISTENERS
		notNetworkedRadio.addActionListener((ActionEvent e) -> {
			setupNotNetworkedUI();
			validInput();
		});
		hostGameRadio.addActionListener((ActionEvent e) -> {
			setupHostGameUI();
			validInput();
		});
		joinGameRadio.addActionListener((ActionEvent e) -> {
			setupJoinGameUI();
			validInput();
		});
		
		// Select teams
		teamSelectSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = teamSelectSlider.getValue();
				
				// Hide all of the teams
				for(int i = 0; i < 4; ++i) {
					teamLbls[i].setVisible(false);
					teamTxtBoxes[i].setVisible(false);
				}
				if (notNetworkedRadio.isSelected()) {
					// Show only the chosen teams
					for(int i = 0; i < value; ++i) {
						teamLbls[i].setVisible(true);
						teamTxtBoxes[i].setVisible(true);
					}
				} else { // other modes only show one option for seelect team
					teamLbls[0].setVisible(true);
					teamTxtBoxes[0].setVisible(true);
				}
				validInput();
			}
		});
		
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				gameData.setNumTeams(teamSelectSlider.getValue());
				validInput();
//				GenerateTeams(teamSelectSlider.getValue());
				// networked game condition
//				gameData.setNumTeams(nTeams);;
				if (hostGameRadio.isSelected() || joinGameRadio.isSelected())
					startClientAndWait();
				else
//					startGame();
//				gameData.InitGame();
//				// Check for Quick Play
//				gameData.setNumberOfQuestions(quickPlay.isSelected());
//				new GameBoardUI(gameData, loggedInUser).setVisible(true);
//				
//				dispose();
				startBtn.setEnabled(false);
			}
			
//			private void GenerateTeams(int numTeams) {
//				for (int i = 1; i <= numTeams; ++i) {
//					String teamName = teamTxtBoxes[i-1].getText().trim();
//					if (teamName.isEmpty())
//						teamName = "Team " + i;
//					gameData.addTeam(teamName);
//				}
//			}
			
		});
		
		clearBtn.addActionListener((ActionEvent event) -> { // playing around with instant instantiation
      // Clear file
			inputFile = null;
			fileNameLbl.setText("");
			gameData.resetVariables();
			
			// Clear teams
			for (int team = 0; team < 4; ++team)
				teamTxtBoxes[team].setText("");
			teamSelectSlider.setValue(1);
			
			waitingLbl.setText("\n");
		});
		
		logoutBtn.addActionListener((ActionEvent event) -> {
//			userDB.logoutUser();
		});
		
		exitBtn.addActionListener((ActionEvent event) -> {
      System.exit(0);
		});
		
		portArea.getDocument().addDocumentListener(new DocumentListener() {
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
		
		ipArea.getDocument().addDocumentListener(new DocumentListener() {
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
		
		for(int i = 0; i < 4; ++i) {
			teamTxtBoxes[i].getDocument().addDocumentListener(new DocumentListener() {
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
	
	private void createTeamSlider() {
		teamSelectSlider = new JSlider(JSlider.HORIZONTAL);
		teamSelectSlider.setMaximum(4);
		teamSelectSlider.setMinimum(1);
		teamSelectSlider.setValue(1);
		teamSelectSlider.setPaintLabels(true);
		teamSelectSlider.setPaintTicks(true);
		teamSelectSlider.setMajorTickSpacing(1);
		teamSelectSlider.setMinorTickSpacing(1);
		teamSelectSlider.setFont(new Font("Cambria", Font.BOLD, 15));
		teamSelectSlider.setBackground(Color.DARK_GRAY);
		teamSelectSlider.setForeground(Color.WHITE);
		teamSelectSlider.setBorder(compound);
	}
	
	private void validInput() {
		startBtn.setEnabled(false);
		// check if any of the text boxes are empty
		for(int i = 0; i < teamSelectSlider.getValue(); ++i) {
			// only check if visible
			if(teamTxtBoxes[i].getText().trim().equals("") && teamTxtBoxes[i].isVisible())
				return;
		}
		// check if an input file has been chosen
		if(inputFile == null && !joinGameRadio.isSelected())
			return;

		// check for game modes
		if (hostGameRadio.isSelected() && portArea.getText().equals(""))
			return;
		if (joinGameRadio.isSelected() && portArea.getText().equals(""))
			return;
		
		
		
		// if all selected enable startButton
		startBtn.setEnabled(true);
	}
	
	static void displayPopup(String text) {
		JDialog popup = new JDialog();
		popup.setTitle("Error Parsing File!!!");
		popup.setPreferredSize(new Dimension(300, 150));

		JPanel Panel = new JPanel();
		Panel.setBackground(new Color(0,150,136));
		
		// Create message
		SimpleAttributeSet paneStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(paneStyle, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(paneStyle, "Cambria Bold");
		StyleConstants.setFontSize(paneStyle, 16);

		JTextPane msg = new JTextPane();
		msg.setEditable(false);
		msg.setText(text);
		msg.setBackground(new Color(0,150,136));
		msg.setForeground(Color.WHITE);
		StyledDocument doc = msg.getStyledDocument();
		doc.setParagraphAttributes(0, 104, paneStyle, false);
		Panel.add(msg);
		popup.add(Panel);
		
		JPanel buttonPanel = new JPanel(); buttonPanel.setBackground(new Color(0,150,136));
		JButton OKBtn = new JButton("Okay");
		OKBtn.setBorder(new MatteBorder(5,10,5,10, Color.WHITE));
		OKBtn.setBackground(Color.WHITE);
		OKBtn.setForeground(new Color(0,150,136));
		OKBtn.setFont(new Font("Cambria", Font.BOLD, 18));
//		// style button
		buttonPanel.add(OKBtn);
		popup.setAlwaysOnTop(true);
		popup.setLocationRelativeTo(selectTeamPanel);
		popup.setVisible(true);
		OKBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				popup.dispose();
			}
		});
		popup.add(buttonPanel, BorderLayout.SOUTH);
		popup.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		popup.pack();
	}
	
	private void setupNotNetworkedUI() {
		connectionSettingsPanel.setVisible(false);
		teamSelectSlider.setMinimum(1);
		
		int numTeams = teamSelectSlider.getValue();
		// Hide all of the teams
		for(int i = 0; i < numTeams; ++i) {
			teamLbls[i].setVisible(true);
			teamTxtBoxes[i].setVisible(true);
		}
		teamTxtBoxes[0].setText("");
		fileChooserLbl.setVisible(true);
		fileNameLbl.setVisible(true);
		teamPromptLbl.setVisible(true);
		avgRatingLbl.setVisible(true);
		teamSelectSlider.setVisible(true);
		chooseFileBtn.setVisible(true);
	}
	
	private void setupHostGameUI() {
		connectionSettingsPanel.setVisible(true);
		ipArea.setVisible(false);
		teamSelectSlider.setMinimum(2);

		// show only one team label
		// Hide all of the teams
		for(int i = 0; i < 4; ++i) {
			teamLbls[i].setVisible(false);
			teamTxtBoxes[i].setVisible(false);
		}
		teamLbls[0].setVisible(true);
		teamTxtBoxes[0].setVisible(true);
		teamTxtBoxes[0].setText(loggedInUser);
		fileChooserLbl.setVisible(true);
		fileNameLbl.setVisible(true);
		teamPromptLbl.setVisible(true);
		avgRatingLbl.setVisible(true);
		teamSelectSlider.setVisible(true);
		chooseFileBtn.setVisible(true);
		
		startBtn.setEnabled(true);
	}
	
	private void setupJoinGameUI() {
		setupHostGameUI();
		fileChooserLbl.setVisible(false);
		fileNameLbl.setVisible(false);
		teamPromptLbl.setVisible(false);
		avgRatingLbl.setVisible(false);
		teamSelectSlider.setVisible(false);
		chooseFileBtn.setVisible(false);
		ipArea.setVisible(true);
	}
	
	public void updateWaitingLabel(int playersWaiting) {
		waitingLbl.setText("Waiting for " + playersWaiting + " other players to join...");
	}
	
	private void startClientAndWait() {
		if (hostGameRadio.isSelected()) {
//			gameData.createGameServer(Integer.parseInt(portArea.getText()), teamSelectSlider.getValue(), gameData);
//			gameData.startGameServer("localhost", Integer.parseInt(portArea.getText()));
			gs = new GameServer(Integer.parseInt(portArea.getText()), teamSelectSlider.getValue(), gameData, this);
//			gs.start();
//			updateWaitingLabel(teamSelectSlider.getValue()-1);
			new ServerRunning().start();
			gc = new GameClient("localhost", Integer.parseInt(portArea.getText()), loggedInUser, this);
			if(!gc.start()) return;			
		}
		else if (joinGameRadio.isSelected()) {
			gc = new GameClient(ipArea.getText(), Integer.parseInt(portArea.getText()), loggedInUser, this);
			if(!gc.start()) return;
//			else startGame(gc.gd);
			
//			gameData.startGameServer(ipArea.getText(), Integer.parseInt(portArea.getText()));
		}
//		while (gameData.getPlayersWaiting() > 0) {
//			int pw = gameData.getPlayersWaiting();
//			waitingLbl.setText("Waiting for " + pw + " to join...");
//		}
	}
	
	public void startGame(GameData gd) {
		gameData = gd;
		gameData.InitGame();
		// Check for Quick Play
		gameData.setNumberOfQuestions(quickPlay.isSelected());
		new GameBoardUI(gameData, loggedInUser).setVisible(true);
		
		dispose();
	}
	
	private void GenerateTeams(int numTeams) {
		for (int i = 1; i <= numTeams; ++i) {
			String teamName = teamTxtBoxes[i-1].getText().trim();
			if (teamName.isEmpty())
				teamName = "Team " + i;
			gameData.addTeam(teamName);
		}
	}
	
	class ServerRunning extends Thread {		
		public void run() {
			System.out.println("TEST");
//			updateWaitingLabel(123);
			gs.start(); // run server
		}
	}
}
