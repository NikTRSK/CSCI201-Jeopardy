package GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import GameLogic.GameClient;
import GameLogic.Question;
import GameLogic.Team;
import networking.GameServer;
import other.GameConstants;
import other.Helpers;
import other.Pair;
import other.Timer;
import other.TimerAnimation;


// Ref: https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
public class GameBoardUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	static private CardLayout cl = new CardLayout();
	static private JPanel mainPanel;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem restartGame, chooseNewGameFile, logoutGame, exitGame;
	JLabel titleLbl;
	JLabel progressTitle, currUserLbl;
	JTextArea teamPrompt;
	JScrollPane promptPane;

	// Question Panel
	JPanel answerQuestionPanel, finalJeopardyPanel;
	JLabel qTeamLbl, qCatLbl, qPtValueLbl, qErrorLbl, qBuzzInTimerLbl;
	JTextArea qQuestionArea;
	JTextArea qAnswerArea;
	JButton qSubmitBtn, qPassBtn;
	
	JLabel [] catLbls = new JLabel [5];
	List<Pair<JLabel, JLabel>> teamLbl;
//	List<ImageIcon> waitTimerIcon;
	ArrayList<JLabel> waitTimerImage;
	JButton [][] qBtns = new JButton[5][5];
	ImageIcon qBtnsEnabled, qBtnsDisabled;
	
	
	// Final Jeopardy components
	JPanel [] finalPtChooser = new JPanel[4];
	JLabel [] teamPtLbl = new JLabel[4];
	JSlider [] ptSelectSlider = new JSlider[4];
	JLabel [] teamPtValLbl = new JLabel[4];
	JButton [] setBetBtn = new JButton[4];
	JTextPane FJQArea;
	JPanel [] FJanswerPanel;
	JTextField [] FJAArea = new JTextField[4];
	JButton [] FJABtn = new JButton[4];
	JTextPane FJwaitingArea, FJBetsArea; 
	JPanel FJAPanel, BetsPanel, FJInfoPanel;
	JLabel FJnotifyLbl;
	StyledDocument docbets, docwaiting;
	
	String myTeamName; // current user
	int myTeamID;
	GameData gameData;
	String [] Categories;
	int [] Points;
	
	Question currQuestion = null;
	int qsAnswered, numTries;;
	private boolean notNetworkedGame;
	private boolean myTurn;
	private boolean ratingsOpen;
//	boolean [] teamsAnswered;
	GameClient gameClient = null;
	GameServer gameServer = null;
	//Assignment 5 additions
	public Timer timer;
	TimerAnimation timerAnimation;
	
	public GameBoardUI (GameData gameData, String myTeamName, GameClient gameClient, GameServer gameServer) {
		super("Play Jeopardy");
		this.myTeamName = myTeamName;
		this.gameData = gameData;
		this.numTries = 0;
//		Teams = gameData.getAllTeams();
		Categories = gameData.getAllCategories();
		Points = gameData.getAllPoints();
		// init client
		if (gameClient != null) {
			this.gameClient = gameClient;
			this.gameClient.gameBoard = this;
		}
		// init server
		if (gameServer != null)
			this.gameServer = gameServer;
		
		initializeComponents();
		createGUI();
		addEvents();
		
		// check for a networked game
		if (gameClient == null && gameServer == null)
			notNetworkedGame = true;
		if (!notNetworkedGame)
			myTeamID = gameData.findTeamID(myTeamName);
		teamPrompt.append("Welcome to Jeopardy!\nThe team to go first is " + gameData.getTeam(gameData.getNextTeam()).getName() + "\n");
		currUserLbl.setText(gameData.getTeam(gameData.getNextTeam()).getName());
		if (gameData.getNextTeam() != myTeamID)
			myTurn = false;
		else
			myTurn = true;
		ratingsOpen = false;
		
		timer = new Timer(titleLbl, waitTimerImage, this);
		// Start timer
//		timer.restart(gameData.getNextTeam());
		timer.start(gameData.getNextTeam());
	}
	
	private void initializeComponents() {

		// Create the menu bar
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menuBar.add(menu);
		
		// Create Menu items
		restartGame = new JMenuItem("Restart This Game");
		// only add the option if you are the server
		if (this.gameServer != null)
			menu.add(restartGame);
		
		chooseNewGameFile = new JMenuItem("Choose New Game File");
		menu.add(chooseNewGameFile);
		
		logoutGame = new JMenuItem("Logout");
		menu.add(logoutGame);
		logoutGame.addActionListener((ActionEvent event) -> {
			gameData.gameTerminatedBy(myTeamName);
			gameClient.sendUpdateToServer(gameData);
      new LoginScreen().setVisible(true);
      dispose();
		});

		exitGame= new JMenuItem("Exit Game");
		exitGame.addActionListener((ActionEvent event) -> {
			gameData.gameTerminatedBy(myTeamName);
			gameClient.sendUpdateToServer(gameData);
      System.exit(0);
		});
		menu.add(exitGame);
		
		//////////////////////////
		// Question Panel Items //
		//////////////////////////
		// Create title Label
		titleLbl = new JLabel("Jeopardy");
		titleLbl.setFont(new Font("Cambria", Font.BOLD, 30));
		titleLbl.setForeground(Color.WHITE);
		titleLbl.setBorder(new MatteBorder(10,0,0,0, new Color(0,150,136)));
		
		// Create Category Labels
		for (int i = 0; i < catLbls.length; ++i) {
			catLbls[i] = new JLabel(Categories[i], SwingConstants.CENTER) {
				private static final long serialVersionUID = 1L;
				public void paintComponent(Graphics g) {
					ImageIcon lblImg = new ImageIcon(gameData.getCategoryPath());
					g.drawImage(lblImg.getImage(), 0, 0, null);
					super.paintComponent(g);
				}
			};
			catLbls[i].setForeground(Color.WHITE);
			catLbls[i].setFont(new Font("Cambria", Font.BOLD, 22));
			catLbls[i].setBorder(new LineBorder(new Color(0,150,136)));
		}

		qBtnsEnabled = new ImageIcon(gameData.getqBtnEnabledPath());
		qBtnsDisabled = new ImageIcon(gameData.getqBtnDisabledPath());
		// Create Question buttons
		for (int pt = 0; pt < 5; ++pt) {
			for (int cat = 0; cat < 5; ++cat) {
				qBtns[cat][pt] = (new JButton("$" + Points[pt]));
				Helpers.styleComponent(qBtns[cat][pt], new Color(56,57,49), new Color(0,150,136), 22);
				qBtns[cat][pt].setForeground(Color.WHITE);
				qBtns[cat][pt].setPreferredSize(new Dimension(100, 100));
				
				// new additions
				qBtns[cat][pt].setIcon(qBtnsEnabled);
				qBtns[cat][pt].setDisabledIcon(qBtnsDisabled);
				qBtns[cat][pt].setHorizontalTextPosition(SwingConstants.CENTER);
			}
		}
		
		// fill out an empty array of pairs for teams
		teamLbl = new ArrayList<Pair<JLabel, JLabel> >() ;
//		waitTimerIcon = new ArrayList<ImageIcon>();
		waitTimerImage = new ArrayList<JLabel>();
		
		for (int i = 0; i < 4; ++i) {
			teamLbl.add(new Pair<JLabel, JLabel>(new JLabel("", SwingConstants.CENTER), new JLabel("", SwingConstants.CENTER)));
			waitTimerImage.add(new JLabel());
		}
		
		// Generate all teams and values to Score Panel
		for (int i = 0; i < gameData.getNumTeams(); i++) {
			teamLbl.get(i).getItem1().setText(gameData.getTeam(i).getName()); 
			teamLbl.get(i).getItem1().setFont(new Font("Cambria", Font.BOLD, 20));
			teamLbl.get(i).getItem1().setForeground(Color.WHITE);
			teamLbl.get(i).getItem2().setText("$" + gameData.getTeam(i).getPoints());
			teamLbl.get(i).getItem2().setFont(new Font("Cambria", Font.BOLD, 20));
			teamLbl.get(i).getItem2().setForeground(Color.WHITE);
		}
		
		// Progress panel components
		progressTitle = new JLabel("Game Progress", SwingConstants.CENTER);
		progressTitle.setAlignmentX(CENTER_ALIGNMENT); progressTitle.setAlignmentY(TOP_ALIGNMENT);
		progressTitle.setForeground(Color.BLACK);
		progressTitle.setFont(new Font("Cambria", Font.BOLD, 25));
		
		currUserLbl = new JLabel();
		currUserLbl.setFont(new Font("Cambria", Font.PLAIN, 23));
		
		teamPrompt = new JTextArea("");
		promptPane = new JScrollPane(teamPrompt);
		promptPane.getViewport().setBackground(new Color(0,137,123));
		promptPane.setBorder(new LineBorder(new Color(0,137,123)));
		teamPrompt.setWrapStyleWord(true);
		teamPrompt.setLineWrap(true);
		teamPrompt.setForeground(Color.BLACK);
		teamPrompt.setOpaque(false);
		teamPrompt.setFont(new Font("Cambria", Font.BOLD, 17));
		teamPrompt.setEditable(false);
		teamPrompt.setDisabledTextColor(Color.BLACK);
		teamPrompt.setEnabled(false);
		DefaultCaret caret = (DefaultCaret)teamPrompt.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		qTeamLbl = new JLabel("Team 201", SwingConstants.CENTER);
		qTeamLbl.setVerticalAlignment(SwingConstants.CENTER);
		qTeamLbl.setBackground(new Color(0,150,136));
		qTeamLbl.setOpaque(true);
		qTeamLbl.setBorder(new LineBorder(new Color(0,150,136)));
		qTeamLbl.setForeground(Color.LIGHT_GRAY);
		qTeamLbl.setFont(new Font("Cambria", Font.BOLD, 30));
		
		qCatLbl = new JLabel("Category 2", SwingConstants.CENTER);
		qCatLbl.setVerticalAlignment(SwingConstants.CENTER);
		qCatLbl.setBackground(new Color(0,150,136));
		qCatLbl.setOpaque(true);
		qCatLbl.setBorder(new LineBorder(new Color(0,150,136)));
		qCatLbl.setForeground(Color.LIGHT_GRAY);
		qCatLbl.setFont(new Font("Cambria", Font.BOLD, 30));
		qCatLbl.setPreferredSize(new Dimension(20, 20));
		
		qBuzzInTimerLbl = new JLabel();
		qBuzzInTimerLbl.setVerticalAlignment(SwingConstants.CENTER);
		qBuzzInTimerLbl.setBackground(new Color(0,150,136));
		qBuzzInTimerLbl.setOpaque(true);
		
		qPtValueLbl = new JLabel("$100", SwingConstants.CENTER);
		qPtValueLbl.setVerticalAlignment(SwingConstants.CENTER);
		qPtValueLbl.setBackground(new Color(0,150,136));
		qPtValueLbl.setOpaque(true);
		qPtValueLbl.setBorder(new LineBorder(new Color(0,150,136)));
		qPtValueLbl.setForeground(Color.LIGHT_GRAY);
		qPtValueLbl.setFont(new Font("Cambria", Font.BOLD, 30));
		
		qQuestionArea = new JTextArea();
		qQuestionArea.setFont(new Font("Cambria", Font.BOLD, 28));
		qQuestionArea.setWrapStyleWord(true);
		qQuestionArea.setLineWrap(true);
		qQuestionArea.setFocusable(false);
		qQuestionArea.setCursor(null);
		qQuestionArea.setPreferredSize(new Dimension(850, 500));
	  qQuestionArea.setEditable(false);
		
		qAnswerArea = new JTextArea();
		qAnswerArea.setPreferredSize(new Dimension(500, 60));
		qAnswerArea.setLineWrap(true);
		qAnswerArea.setWrapStyleWord(true);
		qAnswerArea.setBackground(Color.WHITE);
		qAnswerArea.setForeground(Color.LIGHT_GRAY);
		qAnswerArea.setFont(new Font("Cambria", Font.BOLD, 22));
		
		qSubmitBtn = new JButton("Submit Answer");
    // Make it look flat
		qSubmitBtn.setForeground(Color.BLACK);
		qSubmitBtn.setBackground(Color.WHITE);
	  Border line = new LineBorder(Color.WHITE);
	  Border margin = new EmptyBorder(5, 15, 5, 15);
	  Border compound = new CompoundBorder(line, margin);
		qSubmitBtn.setBorder(compound);
		qSubmitBtn.setPreferredSize(new Dimension(160, 60));
		qSubmitBtn.setFont(new Font("Cambria", Font.BOLD, 18));
		
		qPassBtn = new JButton("Pass");
		if (!notNetworkedGame)
			qPassBtn.setText("Buzz in");
    // Make it look flat
		qPassBtn.setForeground(Color.BLACK);
		qPassBtn.setBackground(Color.WHITE);
	  qPassBtn.setBorder(compound);
	  qPassBtn.setPreferredSize(new Dimension(80, 40));
	  qPassBtn.setFont(new Font("Cambria", Font.BOLD, 18));
		
		qErrorLbl = new JLabel("\n", SwingConstants.CENTER);
		qErrorLbl.setVerticalAlignment(SwingConstants.CENTER);
		qErrorLbl.setForeground(Color.LIGHT_GRAY);
		qErrorLbl.setFont(new Font("Cambria", Font.BOLD, 22));
	}
	
	private void createGUI() {
		// Initial setup
		setSize(1500, 825);
		setLocation(100, 30);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		setJMenuBar(menuBar);
		JPanel screen = new JPanel();
		screen.setLayout(new BoxLayout(screen, BoxLayout.X_AXIS));
		screen.setPreferredSize(new Dimension((int)(3.5*this.getSize().getWidth()/6),(int)(this.getSize().getHeight())));
//		add(screen, BorderLayout.WEST);
		add(screen);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(cl);
		screen.add(mainPanel);
		
		// Create the main Panel
		JPanel questionListPanel = new JPanel();
		questionListPanel.setLayout(new BoxLayout(questionListPanel, BoxLayout.Y_AXIS));
	  Border mainBorder = new EmptyBorder(10, 20, 50, 20);
		questionListPanel.setBorder(mainBorder);
		mainPanel.add(questionListPanel, "questionListPanel");
				
		JPanel titlePanel = new JPanel(); titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER)); 
		titlePanel.setPreferredSize(new Dimension(1030, 10));
		titlePanel.setBackground(new Color(0,150,136));
		
		titlePanel.add(titleLbl);
		questionListPanel.add(titlePanel);
//		questionListPanel.add(Box.createVerticalStrut(5));
		
		// Create Category Panel
		JPanel questionLabelsPanel = new JPanel(); questionLabelsPanel.setLayout(new GridLayout(1, 5));
		questionListPanel.add(questionLabelsPanel);
		
		// add category labels to grid
		for (int i = 0; i < catLbls.length; ++i)
			questionLabelsPanel.add(catLbls[i]);
		
		// Create whole board
		JPanel questionPanel = new JPanel(); questionPanel.setLayout(new GridLayout(5, 5));
//		JPanel questionPanel = new JPanel(); questionPanel.setLayout(new GridLayout(6, 6));
		questionListPanel.add(questionPanel);
//		for (int i = 0; i < catLbls.length; ++i)
//			questionPanel.add(catLbls[i]);
		for (int pt = 0; pt < 5; ++pt) {
			for (int cat = 0; cat < 5; ++cat) {
				questionPanel.add(qBtns[cat][pt]);
			}
		}
		titlePanel.setPreferredSize(new Dimension(questionListPanel.getWidth(),questionPanel.getHeight()/15));
		// Side panel
		JPanel sidePanel = new JPanel(new GridLayout(2, 1));
//		sidePanel.setPreferredSize(new Dimension(screen.getWidth()/6, screen.getHeight()));
		sidePanel.setPreferredSize(new Dimension((int)(1.5*this.getSize().getWidth()/6),(int)(this.getSize().getHeight())));
//		add(sidePanel, BorderLayout.CENTER);
		add(Box.createGlue());
		add(sidePanel);
		
		JPanel scorePanel = new JPanel(new GridLayout(4, 3));
		scorePanel.setBackground(new Color(56,57,49));
		sidePanel.add(scorePanel);
		
		// add all teams to Score Panel
		for (int i = 0; i < teamLbl.size(); i++) {
			scorePanel.add(teamLbl.get(i).getItem1());
			scorePanel.add(waitTimerImage.get(i));
			scorePanel.add(teamLbl.get(i).getItem2());
		}
		
		JPanel gameProgressPanel = new JPanel();
		gameProgressPanel.setLayout(new BoxLayout(gameProgressPanel, BoxLayout.Y_AXIS));
		gameProgressPanel.setBackground(new Color(0,137,123));
		sidePanel.add(gameProgressPanel);
		
		// Create the Game Progress panel		

//		JPanel progressTitlePanel = new JPanel();
//		progressTitlePanel.setSize(new Dimension(progressTitle.getPreferredSize()));
//		progressTitlePanel.setOpaque(false);
		gameProgressPanel.add(progressTitle);
		gameProgressPanel.add(currUserLbl);
//		gameProgressPanel.add(progressTitlePanel);
//		gameProgressPanel.add(progressTitle);
//		progressTitle
		
		// Create the team prompt
		gameProgressPanel.add(promptPane);
		
		createQuestionPanel();
		
		// Create question panel
	mainPanel.add(answerQuestionPanel, "answerQuestionPanel");
	mainPanel.add(finalJeopardyPanel, "finalJeopardyPanel");

	}
	
	private void addEvents() {
		// Listener for Window close
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				class closeDialog extends JOptionPane {
					private static final long serialVersionUID = 1L;
					public closeDialog() {
						UIManager.put("OptionPane.background", new Color(0,150,136));
						UIManager.put("Panel.background", new Color(0,150,136));
						UIManager.put("Panel.foreground", Color.WHITE);
						UIManager.put("Button.background", new Color(39,40,34));
						UIManager.put("Button.foreground", Color.WHITE);
					}
				}
				
				String Btns[] = {"Yes", "No"};
				new closeDialog();
				int result = closeDialog.showOptionDialog(null, "Are you sure you want to quit?", "Quit Program", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, Btns, Btns[1]);
				if (result == JOptionPane.YES_OPTION) {
					gameData.gameTerminatedBy(myTeamName);
					gameClient.sendUpdateToServer(gameData);
		      System.exit(0);
				}
			}
		});
		
		////////////////////
		// Menu Listeners //
		////////////////////
		restartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameData.restartGame(true);
				// Reset Game Data
				gameData.InitGame();
				gameClient.sendUpdateToServer(gameData);
			}
		});
		
		chooseNewGameFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameData.resetVariables();
				gameData.gameTerminatedBy(myTeamName);
				gameClient.sendUpdateToServer(gameData);
				new FileChooser(myTeamName).setVisible(true);
				dispose();
			}
		});
		
		///////////////////////////////
		// Select Question Listeners //
		///////////////////////////////
		class questionSelectedListener implements ActionListener {
			int cat, ptValue;
			
			public questionSelectedListener(int cat, int ptValue) {
				this.cat = cat;
				this.ptValue = ptValue;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				timer.stopTimer();
				gameData.timerStopped(true);
				if (myTurn || notNetworkedGame) {
					gameData.setSelectedQuestion(cat, ptValue);
					gameData.updateSwitchingLogic(true, "answerQuestionPanel");
					((JButton)e.getSource()).setEnabled(false);
					((JButton)e.getSource()).setBackground(new Color(113, 115, 98));
					
					sendGameData();
					System.out.println("Action listener clicked");
				}
			}
		}
		// Create Question buttons
		for (int pt = 0; pt < 5; ++pt) {
			for (int cat= 0; cat < 5; ++cat) {
				qBtns[cat][pt].addActionListener(new questionSelectedListener(cat, pt));
			}
		}
		
		// Final Jeopardy Sliders
		// Custom Listener that changes the label based on the slider value
		class updateSelectedPtLabelListener implements ChangeListener {
			JSlider slider; JLabel label;
			
			public updateSelectedPtLabelListener(JSlider slider, JLabel label) {
				this.slider = slider;
				this.label = label;
			}

			@Override
			public void stateChanged(ChangeEvent e) {
				// bets of less than 0 don't make sense so force minumum of 1
				if (slider.getValue() == 0)
					slider.setValue(1);
				label.setText("$"+slider.getValue());
			}
		}
		
		for (int i = 0; i < gameData.getNumTeams(); ++i) {
			ptSelectSlider[i].addChangeListener(new updateSelectedPtLabelListener(ptSelectSlider[i], teamPtValLbl[i]));
		}
		
		// Answer Button
		qSubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameData.getTeam(gameData.getNextTeam()).setAnsweredThisRound();
				// Get the beginning of question to check for proper formating
				String answer = qAnswerArea.getText().trim();
				String [] ansBeginning = answer.split("\\s+");
				// Check if the user's answer begins with the proper format
				if ( ansBeginning.length > 2 && GameConstants.checkValidBeginningOfQuestion(ansBeginning[0], ansBeginning[1]) ) {
					if (gameData.checkAnswer(ansBeginning, currQuestion.getAnswer())) { // if the answer is correct add points to the user
						if (!notNetworkedGame) {
							gameData.setCorrectAnswer(true);
							sendGameData();
						} else
							correctAnswer();
					} else { // if not correct subtract points
						gameData.setWrongAnswer(true);
						sendGameData();
					}
				} else { // If it doesn't give the user a second chance
					qErrorLbl.setText("The answer needs to be posed as a question. Try again!");
					qAnswerArea.setText("");
					++numTries;
				}
				// check if number of tries exceeded
				if (numTries == 2) {
					gameData.setWrongAnswer(true);
					sendGameData();
				}
			}
		});
		
		qAnswerArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				if (!qAnswerArea.getText().trim().equals("")) {
					qPassBtn.setEnabled(false);
				}
			}

			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				if (!qAnswerArea.getText().trim().equals("")) {
					qPassBtn.setEnabled(false);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {

			}
		});
		
		qPassBtn.addActionListener((ActionEvent event) -> {
			gameData.buzzInTeam(myTeamName);
			gameData.setNextTeam(myTeamID);
//			gameData.setNextTeam(gameData.getCurrentTeam());
			gameClient.sendUpdateToServer(gameData);
		});
		///////////////////////////////
		// Final Jeoppardy Listeners //
		///////////////////////////////
		class setBetListener implements ActionListener {
			int teamID;
			
			public setBetListener(int teamID) {
				this.teamID = teamID;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int betValue = ptSelectSlider[teamID].getValue();
				ptSelectSlider[teamID].setEnabled(false);
				gameData.setBetForTeam(teamID, betValue);
				if (!notNetworkedGame) {
					gameData.setTeamJustBet(teamID);
					gameData.teamBet(true);
					gameClient.sendUpdateToServer(gameData);
				}
				((JButton)e.getSource()).setEnabled(false);
			}
		}
		
		for (int i = 0; i < gameData.getNumTeams(); ++i)
			setBetBtn[i].addActionListener(new setBetListener(i));
		
		// When clicked clear text box
		for (int i = 0; i < gameData.getNumTeams(); ++i) {
			FJAArea[i].addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					((JTextField)e.getSource()).setText("");
				}

				public void focusLost(FocusEvent e) {
					// Nothing to do
				}
			});
		}
		
		//////////////////////////////
		// Answer Question Listener //
		//////////////////////////////
		class answerJeopardyListener implements ActionListener {
			int teamID;
			
			public answerJeopardyListener(int teamID) {
				this.teamID = teamID;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FJAArea[teamID].setEnabled(false);
				gameData.getTeam(teamID).setFinalJeopardyAnswer(FJAArea[teamID].getText().trim());
				gameClient.sendUpdateToServer(gameData);
				((JButton)e.getSource()).setEnabled(false);
			}
		}
		
		for (int i = 0; i < gameData.getNumTeams(); ++i) {
			FJABtn[i].addActionListener(new answerJeopardyListener(i));
		}
		
		promptPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
	}
	
	private void createQuestionPanel() {		
		answerQuestionPanel = new JPanel();
		answerQuestionPanel.setLayout(new BoxLayout(answerQuestionPanel, BoxLayout.Y_AXIS));
		answerQuestionPanel.setBackground(new Color(56,57,49));
		
		JPanel qTitlePanel = new JPanel(new GridLayout(1, 3));
		qTitlePanel.setPreferredSize(new Dimension(answerQuestionPanel.getWidth(), 100));
		qTitlePanel.setBackground(new Color(0,150,136));
		qTitlePanel.add(qTeamLbl);
		qTitlePanel.add(qBuzzInTimerLbl);
		qTitlePanel.add(qCatLbl);
		qTitlePanel.add(qPtValueLbl);
		answerQuestionPanel.add(qTitlePanel);
		
		JPanel errorPanel = new JPanel(new GridBagLayout());
		errorPanel.setPreferredSize(new Dimension(answerQuestionPanel.getWidth(), 150));
		errorPanel.setBackground(new Color(56,57,49));
		errorPanel.add(qErrorLbl);
		answerQuestionPanel.add(errorPanel);
		
		JPanel qPanel = new JPanel();
	  Border qBorder = new EmptyBorder(10, 20, 50, 20);
	  qPanel.setBorder(qBorder);
	  answerQuestionPanel.add(qPanel);
	  qPanel.add(qQuestionArea);
		
	  JPanel answerPanel = new JPanel(new GridBagLayout());
	  GridBagConstraints gbc = new GridBagConstraints();
	  answerPanel.setBackground(new Color(56,57,49));
	  answerPanel.setPreferredSize(new Dimension(answerQuestionPanel.getWidth(), 130));
	  answerQuestionPanel.add(answerPanel);
	  gbc.gridx = 0; gbc.gridy = 0;
	  gbc.insets = new Insets(0,0,0,10);
	  answerPanel.add(qAnswerArea, gbc);
	  gbc.gridx = 1; gbc.gridy = 0;
	  answerPanel.add(qSubmitBtn, gbc);
	  gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
	  gbc.insets = new Insets(5,0,0,0);
	  answerPanel.add(qPassBtn, gbc);
		
	  ////////////////////////////////////////////////////////
	  // Final Jeopardy Panel //
	  ////////////////////////////////////////////////////////
	  finalJeopardyPanel = new JPanel();
	  finalJeopardyPanel.setLayout(new BoxLayout(finalJeopardyPanel, BoxLayout.Y_AXIS));
	  finalJeopardyPanel.setBackground(new Color(56,57,49));
	  
		JPanel FJTitlePanel = new JPanel();
		FJTitlePanel.setBorder(new EmptyBorder(10, 20, 20, 20));
		FJTitlePanel.setBackground(new Color(56,57,49));
		JLabel FJTitle = new JLabel("Final Jeopardy Round", SwingConstants.CENTER);
		FJTitle.setPreferredSize(new Dimension(950, 60));
		Helpers.styleComponent(FJTitle, new Color(0,150,136), new Color(0,150,136), 40);
		FJTitle.setForeground(Color.LIGHT_GRAY);
	  FJTitlePanel.add(FJTitle);
	  
	  finalJeopardyPanel.add(FJTitlePanel);
//	  
	  BetsPanel = new JPanel(new GridLayout(4,0));
	  BetsPanel.setBorder(new MatteBorder(0,0,0,50, new Color(56,57,49)));
	  BetsPanel.setBackground(new Color(56,57,49));
//	   add all slider panels regardless of # of teams
	  
	  for (int i = 0; i < 4; ++i) {
	  	finalPtChooser[i] = new JPanel();
	  	finalPtChooser[i].setLayout(new BoxLayout(finalPtChooser[i], BoxLayout.X_AXIS));
	  	finalPtChooser[i].setBackground(new Color(56,57,49));
	  	BetsPanel.add(finalPtChooser[i]);
	  }
	  for (int i = 0; i < gameData.getNumTeams(); ++i) {
	  	teamPtLbl[i] = new JLabel(gameData.getTeam(i).getName() + "'s bet:", SwingConstants.CENTER);
	  	Helpers.styleComponent(teamPtLbl[i], new Color(56,57,49), new Color(56,57,49), 15);
	  	teamPtLbl[i].setForeground(Color.LIGHT_GRAY);
	  	teamPtLbl[i].setPreferredSize(new Dimension(170, 50));
	  	finalPtChooser[i].add(teamPtLbl[i]);
	  	
	  	ptSelectSlider[i] = new JSlider();
	  	ptSelectSlider[i].setValue(0);
	  	ptSelectSlider[i].setMaximum(0);
			ptSelectSlider[i].setPaintLabels(true);
			ptSelectSlider[i].setPaintTicks(true);
			ptSelectSlider[i].setFont(new Font("Cambria", Font.BOLD, 15));
			ptSelectSlider[i].setBackground(new Color(56,57,49));
			ptSelectSlider[i].setForeground(Color.WHITE);
			ptSelectSlider[i].setPreferredSize(new Dimension(650, 70));
			// Create Border
		  Border line = new LineBorder(new Color(56,57,49));
		  Border margin = new EmptyBorder(0, 15, 0, 15);
		  Border compound = new CompoundBorder(line, margin);
			ptSelectSlider[i].setBorder(compound);
			finalPtChooser[i].add(ptSelectSlider[i]);
			
			teamPtValLbl[i] = new JLabel("$" + Integer.toString(ptSelectSlider[i].getValue()), SwingConstants.CENTER);
			Helpers.styleComponent(teamPtValLbl[i], new Color(56,57,49), new Color(56,57,49), 15);
			teamPtValLbl[i].setForeground(Color.WHITE);
			teamPtValLbl[i].setPreferredSize(new Dimension(50, 50));
			finalPtChooser[i].add(teamPtValLbl[i]);
			
			setBetBtn[i] = new JButton("Set Bet");
			setBetBtn[i].setForeground(Color.BLACK);
			setBetBtn[i].setBackground(Color.WHITE);
	    setBetBtn[i].setBorder(compound);
	    setBetBtn[i].setBorder(new MatteBorder(5, 0, 5, 0, Color.WHITE));
	    if (gameData.getTeam(i).getPoints() <= 0)
	    	setBetBtn[i].setEnabled(false);
			finalPtChooser[i].add(setBetBtn[i]);
			finalPtChooser[i].setBorder(new EmptyBorder(30, 0, 30, 0));
	  }
	  finalJeopardyPanel.add(BetsPanel);

	  FJQArea = new JTextPane();
		SimpleAttributeSet paneStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(paneStyle, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(paneStyle, "Cambria BOLD");
		StyleConstants.setFontSize(paneStyle, 22);

		FJQArea.setEditable(false);
		FJQArea.setText("And the quesiton is...\n");
		FJQArea.setBackground(new Color(0,150,136));
	  FJQArea.setForeground(Color.WHITE);
		StyledDocument doc = FJQArea.getStyledDocument();
		doc.setParagraphAttributes(0, 104, paneStyle, false);
	  FJQArea.setPreferredSize(new Dimension(950, 60));
	  
	  JPanel FJQAreaPanel = new JPanel();
	  FJQAreaPanel.setBackground(new Color(56,57,49));
	  FJQAreaPanel.setBorder(new EmptyBorder(0, 20, 30, 20));
	  FJQAreaPanel.add(FJQArea);	  
	  finalJeopardyPanel.add(FJQAreaPanel);

	  FJanswerPanel = new JPanel[4]; 
	  // add all panels to the layout
	  FJAPanel = new JPanel(new GridLayout(2,2));
	  FJAPanel.setBackground(new Color(56,57,49));
	  FJAPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
	  for (int i = 0; i < 4; ++i) {
	  	FJanswerPanel[i] = new JPanel(); FJanswerPanel[i].setLayout(new BoxLayout(FJanswerPanel[i], BoxLayout.X_AXIS));
	  	FJanswerPanel[i].setBackground(new Color(56,57,49));
	  	FJanswerPanel[i].setBorder(new MatteBorder(0, 20, 0, 20, new Color(56,57,49)));
	  	FJAPanel.add(FJanswerPanel[i]);
	  }
	  
	  for (int i = 0; i < gameData.getNumTeams(); ++i) {
	  	FJAArea[i] = new JTextField();
	  	Helpers.styleComponentFlat(FJAArea[i], Color.BLACK, Color.WHITE, Color.WHITE, 17, true);
	  	FJAArea[i].setText(gameData.getTeam(i).getName() + ", enter your answer.");
	  	FJanswerPanel[i].add(FJAArea[i]);
	  	
	  	JLabel pad = new JLabel("   ");
	  	FJanswerPanel[i].add(pad);
	  	
	  	FJABtn[i] = new JButton("Submit Answer");
	  	Helpers.styleComponentFlat(FJABtn[i], Color.BLACK, Color.WHITE, Color.WHITE, 17, true);
	  	FJABtn[i].setBorder(new MatteBorder(13, 12, 13, 12, Color.WHITE));
	  	FJanswerPanel[i].add(FJABtn[i]);
	  	
	  	
	  	FJanswerPanel[i].setBorder(new EmptyBorder(15, 30, 15, 30));
	  	FJanswerPanel[i].setBackground(new Color(56,57,49));
	  	
	  }
	  finalJeopardyPanel.add(FJAPanel);
	  
	  FJwaitingArea = new JTextPane();
	  FJwaitingArea.setBackground(new Color(56,57,49));
		StyleConstants.setFontSize(paneStyle, 17);
	  FJwaitingArea.setForeground(Color.WHITE);
		docwaiting = FJwaitingArea.getStyledDocument();
		docwaiting.setParagraphAttributes(0, 104, paneStyle, false);

	  FJBetsArea = new JTextPane();
	  FJBetsArea.setBackground(new Color(56,57,49));
	  FJBetsArea.setForeground(Color.WHITE);
		docbets = FJwaitingArea.getStyledDocument();
		docbets.setParagraphAttributes(0, 104, paneStyle, false);
	  
	  FJnotifyLbl = new JLabel("\n", SwingConstants.CENTER);
	  FJnotifyLbl.setBackground(new Color(56,57,49));
	  FJnotifyLbl.setFont(new Font("Cambria", Font.BOLD, 17));
	  FJnotifyLbl.setForeground(Color.WHITE);
	  
	  FJInfoPanel = new JPanel();
	  FJInfoPanel.setLayout(new BoxLayout(FJInfoPanel, BoxLayout.Y_AXIS));
	  FJInfoPanel.setBackground(new Color(56,57,49));
	  FJInfoPanel.add(FJwaitingArea);
	  FJwaitingArea.setAlignmentX(CENTER_ALIGNMENT);
	  FJInfoPanel.add(FJBetsArea);
	  FJBetsArea.setAlignmentX(CENTER_ALIGNMENT);
	}
	
	private void createNetworkedFinalJeopardy() {
		if (gameData.teamsAllNegative()) {
			createRatingsPanel(false);
		} else {
//			int myTeamID = gameData.findTeamID(myTeamName);
			// remove all teams from pane
			for (int i = 0; i < 4; i++)
				BetsPanel.remove(finalPtChooser[i]);
			BetsPanel.add(finalPtChooser[myTeamID]);
			teamPrompt.append("------Welcome to Final Jeopardy!------\n");
			// setup window if user eligible
			if (gameData.checkFJEligible(myTeamName)) {
				FJAPanel.setLayout(new GridLayout(1, 2));
				FJAPanel.removeAll();
				FJAPanel.add(FJanswerPanel[myTeamID]);
				FJQArea.setText("Wait for it...");
				teamPtLbl[myTeamID].setText(myTeamName + "'s bet:");
				FJAArea[myTeamID].setVisible(true);
				FJAArea[myTeamID].setEnabled(false);
				FJAArea[myTeamID].setEditable(false);
				FJAArea[myTeamID].setText(myTeamName + ", enter your answer");
				FJABtn[myTeamID].setEnabled(false);
				FJABtn[myTeamID].setVisible(true);
				setBetBtn[myTeamID].setEnabled(true);
				setupSlider(myTeamID);
				finalPtChooser[myTeamID].setVisible(true);
				for (int i = 0; i < gameData.getNumTeams(); ++i) {
					if (i != myTeamID) {
						finalPtChooser[i].setVisible(false);
						FJAArea[i].setVisible(false);
						FJABtn[i].setVisible(false);
						FJAPanel.remove(FJanswerPanel[i]);
					}
				}
				FJwaitingArea.setVisible(true);
				FJBetsArea.setVisible(true);
				
				BetsPanel.add(FJInfoPanel);
				FJInfoPanel.setVisible(true);
//				BetsPanel.add(FJnotifyLbl);
				finalJeopardyPanel.add(FJnotifyLbl);
				FJnotifyLbl.setVisible(true);
				FJnotifyLbl.setText("\n");
				showPanel("finalJeopardyPanel");
			} else {
				qErrorLbl.setText("You are not eligible for Final Jeopardy. Wait for other players.");
			}
		}
	}
	
	private void setupSlider(int i) {
    ptSelectSlider[i].setMaximum(gameData.getTeam(i).getPoints());
    ptSelectSlider[i].setMinimum(0);
    ptSelectSlider[i].setValue(1);

    int spacing = 0;
    if (gameData.getTeam(i).getPoints() <= 100) spacing = 10;
    else if (gameData.getTeam(i).getPoints() <= 1000) spacing = 100;
    else if (gameData.getTeam(i).getPoints() <= 2000) spacing = 200;
    else if (gameData.getTeam(i).getPoints() <= 3000) spacing = 300;
    else if (gameData.getTeam(i).getPoints() <= 4000) spacing = 400;
    else if (gameData.getTeam(i).getPoints() <= 5000) spacing = 500;
    else if (gameData.getTeam(i).getPoints() <= 5000) spacing = 600;
    else spacing = 1000;
    ptSelectSlider[i].setMajorTickSpacing(spacing);
	}
	
	public static void showPanel(String panelName) {
		if (panelName.equals("questionListPanel") || panelName.equals("answerQuestionPanel") || panelName.equals("finalJeopardyPanel"))
			cl.show(mainPanel, panelName);
	}

	private void createRatingsPanel(boolean hasWinner) {
		// 0 = no winners
		// 1 = has winners
		
		JDialog winnerDialog = new JDialog();
		winnerDialog.setPreferredSize(new Dimension(600, 600));
		winnerDialog.setTitle("Thank you for playing");
		
		JPanel winnerPanel = new JPanel(); winnerPanel.setLayout(new BoxLayout(winnerPanel, BoxLayout.Y_AXIS));
		winnerPanel.setBackground(new Color(0,150,136));
		
		// Create message
		SimpleAttributeSet paneStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(paneStyle, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(paneStyle, "Cambria BOLD");
		StyleConstants.setFontSize(paneStyle, 18);

		JTextPane lbl = new JTextPane();
		lbl.setEditable(false);
		lbl.setBackground(new Color(0,150,136));
		lbl.setForeground(Color.WHITE);
		StyledDocument doc = lbl.getStyledDocument();
		doc.setParagraphAttributes(0, 104, paneStyle, false);
		winnerPanel.add(lbl, BorderLayout.NORTH);
		if (hasWinner) {
			lbl.setText("And the winner(s) is/are\n");
			ArrayList<Integer> winner = gameData.findWinner();
			for (Integer teamID : winner) {
				lbl.setText(lbl.getText() + gameData.getTeam(teamID).getName() + "\n");
			}
		} else {
			if (notNetworkedGame)
				lbl.setText("There are no teams eligible for Final Jeopardy.\nThere are no winners");
			else
				lbl.setText("Sorry, you are not eligible for Final Jeopardy.\nPlease rate the game");
		}
		
		winnerDialog.add(winnerPanel);
		
		// add rating panel
		
  	JSlider ratingSlider = new JSlider();
  	ratingSlider.setValue(1);
  	ratingSlider.setMaximum(5);
  	ratingSlider.setValue(3);
  	ratingSlider.setPaintLabels(true);
  	ratingSlider.setPaintTicks(true);
  	ratingSlider.setMajorTickSpacing(1);
  	ratingSlider.setFont(new Font("Cambria", Font.BOLD, 15));
  	ratingSlider.setBackground(new Color(0,150,136));
  	ratingSlider.setForeground(Color.WHITE);
  	ratingSlider.setPreferredSize(new Dimension(500, 70));
  	
  	JLabel selectedRatingLbl = new JLabel(""+ratingSlider.getValue(), SwingConstants.CENTER);
  	selectedRatingLbl.setPreferredSize(new Dimension(30, 70));
  	selectedRatingLbl.setFont(new Font("Cambria", Font.BOLD, 15));
  	selectedRatingLbl.setForeground(Color.WHITE);
  	JPanel selectedRatingPanel = new JPanel();
  	selectedRatingPanel.setPreferredSize(new Dimension(30,70)); selectedRatingPanel.setBackground(Color.DARK_GRAY);
  	selectedRatingPanel.add(selectedRatingLbl);
  	
  	JLabel currentAvgRatingLbl = new JLabel("current average rating: " + gameData.getFileRanking() + "/5", SwingConstants.CENTER); 
  	currentAvgRatingLbl.setFont(new Font("Cambria", Font.BOLD, 20));
  	
  	ratingSlider.addChangeListener((ChangeEvent ce) -> {
  		selectedRatingLbl.setText(""+ratingSlider.getValue());
  	});

  	
  	// Selected rating
  	GridBagConstraints gbc = new GridBagConstraints();
  	JPanel ratingsPanel = new JPanel(new GridBagLayout());
  	ratingsPanel.setBackground(new Color(0,150,136));
  	gbc.gridx = 0; gbc.gridy = 0;
  	ratingsPanel.add(ratingSlider, gbc);
  	gbc.gridx = 1; gbc.gridy = 0; gbc.insets = new Insets(0,10,0,0);
  	ratingsPanel.add(selectedRatingPanel, gbc);
  	gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.insets = new Insets(0,0,0,0);
  	ratingsPanel.add(currentAvgRatingLbl, gbc);
  	
  	ratingsPanel.setBorder(new MatteBorder(0,0,50,0, new Color(0,150,136)));
  	
		winnerPanel.add(ratingsPanel);
		
		JPanel buttonPanel = new JPanel(); buttonPanel.setBackground(new Color(0,150,136));
		JButton OKBtn = new JButton("Okay");
		OKBtn.setBorder(new MatteBorder(13,16,13,16, Color.WHITE));
		OKBtn.setBackground(Color.WHITE);
		OKBtn.setForeground(new Color(0,150,136));
		OKBtn.setFont(new Font("Cambria", Font.BOLD, 25));
		// style button
		buttonPanel.add(OKBtn);
		winnerDialog.setAlwaysOnTop(true);
		winnerDialog.setLocationRelativeTo(mainPanel);
		winnerDialog.setVisible(true);
		OKBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				// get values and increase # of people rated
				gameData.updateFileRanking(ratingSlider.getValue());
				gameClient.sendUpdateToServer(gameData);
				
//				if (gameServer == null) {
					gameData.resetVariables();
					new FileChooser(myTeamName).setVisible(true);;
//					dispose();
//				}
				winnerDialog.dispose();
				dispose();
			}
		});
		winnerDialog.add(buttonPanel, BorderLayout.SOUTH);
		winnerDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		winnerDialog.pack();
		if (!notNetworkedGame)
			setVisible(true);
		else
			this.setVisible(false);
	}
	
	private String printPts(int pts) {
		if (pts < 0)
			return ("-$" + Math.abs(pts));
		else
			return ("$" + pts);
	}
	
	public void displayAnswerPanel() {
		currQuestion = gameData.getQuestion(Categories[gameData.getSelectedQuestionCat()], Points[gameData.getSelectedQuestionPtValue()]);
		// Update info on questionPanel
		if(gameData.getTeam(gameData.getNextTeam()).getName().equals(myTeamName))
			qTeamLbl.setText(gameData.getTeam(gameData.getNextTeam()).getName());
		else
			qTeamLbl.setText("\n");
		qCatLbl.setText(Helpers.capitalize(currQuestion.getCategory()));
		qPtValueLbl.setText("$" + currQuestion.getPointValue());
		qQuestionArea.setText(currQuestion.getQuestion());
		qErrorLbl.setText("\n");
		qAnswerArea.setText("");
		// if timer expires no need to change the panel
//		if (!gameData.timerExpired())
		showPanel(gameData.getCurrPanel());
		teamPrompt.append(gameData.getTeam(gameData.getNextTeam()).getName() + " chose the question in " + Helpers.capitalize(currQuestion.getCategory()) + " worth $" + currQuestion.getPointValue() + "\n");
		qAnswerArea.requestFocusInWindow();
		qPassBtn.setVisible(false);
		
		timer.setupAnswerPane(qTeamLbl);
		// added
//		if (!gameData.changePanel())
//			timer.start(gameData.getNextTeam());
//			timer.restart(gameData.getNextTeam());
		qErrorLbl.setText("Answer within 20 seconds!");
	}
	
	private void sendGameData() {
		System.out.println("!!!!!!___Client is sending data.");
		gameClient.sendUpdateToServer(gameData);
	}
	
	public void updateClientData(GameData gd) {
		gameData = gd;
	}
	
	private void correctAnswer() {
		currQuestion.setAnswered();
		teamPrompt.append(gameData.getTeam(gameData.getNextTeam()).getName() + ", you got the answer right! " + printPts(currQuestion.getPointValue()) + " will be added to your score.\n");
		gameData.getTeam(gameData.getNextTeam()).addPoints(currQuestion.getPointValue());
		teamLbl.get(gameData.getNextTeam()).getItem2().setText("$" + gameData.getTeam(gameData.getNextTeam()).getPoints());

		qPassBtn.setEnabled(true);
		if (gameData.getQsAnswered() < 25 && allTeamsAnswered()) {
			currQuestion.setAnswered();
			showPanel("questionListPanel");
			qPassBtn.setVisible(false);
			qPassBtn.setEnabled(true);
			if (gameData.getQsAnswered() != 25)
				teamPrompt.append("Now it's " + gameData.getTeam(gameData.getNextTeam()).getName() + "'s turn! Please Choose a question.\n");
			currUserLbl.setText(gameData.getTeam(gameData.getNextTeam()).getName());
		}
		numTries = 0;
	}
	
	private boolean allTeamsAnswered() {
		for (Team t : gameData.getAllTeams()) {
			if (!t.hasAnswered())
				return false;
		}
		return true;
	}
	
	private void wrongAnswerNetworked() {
		teamPrompt.append(gameData.getTeam(gameData.getNextTeam()).getName() + ", that is the wrong answer! " + printPts(currQuestion.getPointValue()) + " will be subtracted from the score.\n");
		gameData.getTeam(gameData.getNextTeam()).subPoints(currQuestion.getPointValue());
		teamLbl.get(gameData.getNextTeam()).getItem2().setText(printPts(gameData.getTeam(gameData.getNextTeam()).getPoints()));
		for (Team t : gameData.getAllTeams()) {
			if (t.getName().equals(myTeamName)) {
				if (!t.hasAnswered()) {
					qPassBtn.setVisible(true);
					qPassBtn.setEnabled(true);
					qErrorLbl.setText("Buzz in to answer!");
				} else {
					qPassBtn.setVisible(false);
					qPassBtn.setEnabled(false);
					qErrorLbl.setText("You cannot buzz in. You already answered");
				}
			}
			qSubmitBtn.setEnabled(false);
			qAnswerArea.setEditable(false);
		}
		// took this out
		if (allTeamsAnswered() && gameData.getQsAnswered() != 25) {
			numTries = 0;
			gameData.updateCurrentTeam();
			gameData.setNextTeam(gameData.getCurrentTeam());
		}
	  else
			teamPrompt.append("Another team can buzz in.\n");
	}
	
	public void setupQuestionListPanel() {
		if (!gameData.timerExpired() || timer.inBuzzInTime()) {
			System.out.print("Marking q as answered");
			gameData.qsAnsweredIncrement();
			currQuestion.setAnswered();
		}
		
		teamLbl.get(gameData.getNextTeam()).getItem2().setText(printPts(gameData.getTeam(gameData.getNextTeam()).getPoints()));
		if (gameData.getNextTeam() != myTeamID)
			myTurn = false;
		else
			myTurn = true;
		if (gameData.getQsAnswered() != 25)
			teamPrompt.append("Now it's " + gameData.getTeam(gameData.getNextTeam()).getName() + "'s Please Choose a question.\n");
		numTries = 0;
		if(gameData.getQsAnswered() != 25)
			showPanel("questionListPanel");
		qPassBtn.setVisible(false);
		if (!gameData.timerExpired() || timer.inBuzzInTime()) {
			qBtns[gameData.getSelectedQuestionCat()][gameData.getSelectedQuestionPtValue()].setEnabled(false);
			System.out.print("Marking q as answered 2");
		}
		
		// start timer
		currUserLbl.setText(gameData.getTeam(gameData.getNextTeam()).getName());
		timer.restart(gameData.getCurrentTeam());
	}
	
	private void checkAllBetsNetworked() {
		boolean allBetsSet = true;
		FJwaitingArea.setText("");
		for (Team t : gameData.getAllTeams()) {
			// if player is eligible check if they set their bets
			if (t.isFJEligible()) {
				try {
					if (!t.hasSetBet()) {
						if (!t.getName().equals(myTeamName))
							docbets.insertString(docbets.getLength(), "Waiting for " + t.getName() + " to set their bet.\n", null);
						allBetsSet = false;
					}
					else
						docwaiting.insertString(docwaiting.getLength(), t.getName() + " bet $" + t.getBet() + "\n", null);
					} catch (BadLocationException e) { }
			}
		}
		
		if (allBetsSet) {
			FJABtn[myTeamID].setEnabled(true);
			FJAArea[myTeamID].setEnabled(true);
			FJAArea[myTeamID].setEditable(true);
			FJQArea.setText(gameData.getFJQuestion());
			teamPrompt.append("Here is the Final Jeopardy question:\n" + gameData.getFJQuestion() + "\n");
			FJwaitingArea.setText("\n");
			FJBetsArea.setText("\n");
		}
	}
 
	private boolean allFJAnswersEntered() {
		// if we are still not in final jeopardy then return false
		if (!gameData.inFinalJeopardy())
			return false;
		for (Team t : gameData.getAllTeams()) {
			if (t.isFJEligible() && !t.answeredFinalJeopardy())
				return false;
		}
		return true;
	}
	
	private void checkFJAnswersNetworked() {
		for (Team t : gameData.getAllTeams()) {
			if (t.isFJEligible() && t.hasSetBet()) {
				String [] ansBeginning = t.getFinalJeopardyAnswer().split("\\s+");
				if (ansBeginning.length > 2 && gameData.checkAnswer(ansBeginning, gameData.getFJAnswer())) {
					t.addPoints(t.getBet());;
					teamPrompt.append(t.getName() + " got the answer right. $" + t.getBet() + " will be added to their total.\n");
				} else {	// no second change in Final Jeopardy
					t.subPoints(t.getBet());
					teamPrompt.append(t.getName() + " got the answer wrong. $" + t.getBet() + " will be subtracted from their total.\n");
				}
			}
			teamLbl.get(gameData.findTeamID(t.getName())).getItem2().setText("$" + t.getPoints());
			if (t.getName().equals(myTeamName))
				createRatingsPanel(true);
		}
	}
	
	public void noTeamBuzzedIn() {
		System.out.println("inBuzzInTime");
		teamPrompt.append("Nobody buzzed in. \n");
		gameData.updateCurrentTeam();
		gameData.setNextTeam(gameData.getCurrentTeam());
		setupQuestionListPanel();
		//TODO flipped this
		timer.setupQuestionListPane(titleLbl, waitTimerImage);
	}
	
	public void restartGame() {
		// Create Question buttons
		for (int pt = 0; pt < 5; ++pt) {
			for (int cat = 0; cat < 5; ++cat) {
				qBtns[cat][pt].setEnabled(true);
				qBtns[cat][pt].setIcon(qBtnsEnabled);
			}
		}
		// Reset points in Point side panel
		for (int i = 0; i < gameData.getNumTeams(); i++)
			teamLbl.get(i).getItem2().setText("$" + gameData.getTeam(i).getPoints());
		teamPrompt.setText("Game Restarted!\nWelcome to Jeopardy!\nThe team to go first is " + gameData.getTeam(gameData.getNextTeam()).getName() + "\n");
		
		// reset restart flag
		gameData.restartGame(false);
	}
	
	public void timeExpired() {
		System.out.println("RUNNING TIMER EXPIRED");
		if (timer.inQuestionListPane())
			System.out.println("INQLIST - TRUE");
		if (timer.inAnswerPane())
			System.out.println("INANSPANE - TRUE");
		if (timer.inBuzzInTime())
			System.out.println("INBUZZTIME - TRUE");
		
		gameData.timerExpired(true);
		gameData.getTeam(gameData.getNextTeam()).hasAnswered();
		if (timer.inQuestionListPane()) {
			System.out.println("IN QLISTPANE");
//			timer.restart(teamID);
		gameClient.sendUpdateToServer(gameData);
		System.out.println("Timer Expired");
		}
		else if (timer.inAnswerPane()) {
			gameData.getTeam(gameData.getNextTeam()).setAnsweredThisRound();
			System.out.println("inAnswerPane");
			updateInAnswerPane();
		}
		else if (timer.inBuzzInTime()) {
			timer.stopTimer();
			gameData.timerStopped(true);
			gameClient.sendUpdateToServer(gameData);
//			System.out.println("inBuzzInTime");
//			teamPrompt.append("Nobody buzzed in. \n");
//			gameData.updateCurrentTeam();
//			gameData.setNextTeam(gameData.getCurrentTeam());
//			setupQuestionListPanel();
//			//TODO flipped this
//			timer.setupQuestionListPane(titleLbl, waitTimerImage);
		}
	}
	
	private void updateExpired() {
		if (gameData.timerExpired() && !timer.stopped()) {
			System.out.println("in updateExpired");
			teamPrompt.append("Timer expired for " + gameData.getTeam(gameData.getNextTeam()).getName() + "\n");
			gameData.updateCurrentTeam();
			gameData.setNextTeam(gameData.getCurrentTeam());
			setupQuestionListPanel();
			gameData.timerExpired(false);
		}		
	}
	
	// called to setup buzzin timers
	private void updateInAnswerPane() {
		System.out.println("updateInAnswerPane");
//		if (gameData.timerExpired() && !timer.stopped()) {
//		if (inBuzzInPanel)
			teamPrompt.append(gameData.getTeam(gameData.getNextTeam()).getName() + " got the answer wrong!" + printPts(currQuestion.getPointValue()) + " will be deducted from their total.\n");
			teamPrompt.append("Another team can buzz in within the next 20 seconds to answer.\n");
			gameData.getTeam(gameData.getCurrentTeam()).subPoints(currQuestion.getPointValue());
			for (Team t : gameData.getAllTeams()) {
				if (t.getName().equals(myTeamName)) {
					if (!t.hasAnswered()) {
						qPassBtn.setVisible(true);
						qPassBtn.setEnabled(true);
						qErrorLbl.setText("Buzz in to answer!");
					} else {
						qPassBtn.setVisible(false);
						qPassBtn.setEnabled(false);
						qErrorLbl.setText("You cannot buzz in. You already answered");
					}
				}
				qSubmitBtn.setEnabled(false);
				qAnswerArea.setEditable(false);
			}
			gameData.timerExpired(false);
			timer.setupBuzzInTimer(qBuzzInTimerLbl);
//		}		
	}
	
	public void updateClientGUI() {
		// if the server Restarts the Game
		if (gameData.restartGame())
			restartGame();
		// if the game is terminated show a message to the clients
		if (gameData.gameTerminatedBy() != null) {
			if (!gameData.gameTerminatedBy().equals(myTeamName)) {
				String Btns[] = {"OK"};
				new dialogBox();
				int result = dialogBox.showOptionDialog(null, "Game terminated", "One of the players exited the game.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, Btns, Btns[0]);
				if (result == JOptionPane.OK_OPTION) {
					new FileChooser(myTeamName).setVisible(true);
					dispose();
				}
			}
			if (gameServer != null)
				gameServer.stop();
			return;
		}
		// handle cases of the timer expired
		if (gameData.timerExpired()) {
			System.out.println("UPDATE EXPIRED");
			if (!gameData.timerStopped()) {
				if (!timer.inBuzzInTime())
					System.out.println("InBuzzInTime FALSE");
//				if (timer.inBuzzInTime())
//					noTeamBuzzedIn();
//				else
					updateExpired();
//			if (timer.inQuestionListPane())
//				noTeamBuzzedIn();
			}
				
		}
		if (gameData.changePanel() && timer.stopped()) {
			// TODO: Fix this bug
			System.out.println("CHANGING PANEL");
//			timer.stopTimer();
			displayAnswerPanel(); // doesn't have SEND DATA
			
			if (!gameData.getTeam(gameData.getNextTeam()).getName().equals(myTeamName)) {
//				qTeamLbl.setVisible(true);
				qAnswerArea.setEditable(false);
				qSubmitBtn.setEnabled(false);
			} else {
//				qTeamLbl.setVisible(true);
				qAnswerArea.setEditable(true);
				qSubmitBtn.setEnabled(true);
			}
			gameData.updateSwitchingLogic(false);
			// Setup timer for anwer panel
//			timer.stopTimer();
			timer.setupAnswerPane(qTeamLbl);
//			timer.restart(gameData.getNextTeam());
		}
		// check if question answered
		if (gameData.correctAnswer()) {
			System.out.println("CORRECT ANSWER");
			correctAnswer();
			for (Team t : gameData.getAllTeams())
				t.setAnsweredThisRound();
			gameData.setCorrectAnswer(false);
		}
		// in case the answer is wrong
		if (gameData.wrongAnswer()) {
			System.out.println("WRONG ANSWER");
			wrongAnswerNetworked();
			qTeamLbl.setText("\n");
			gameData.setWrongAnswer(false);
		}
		// if someone buzzed in
		if (gameData.buzzedInTeam() != null) {
			if (gameData.buzzedInTeam().equals(myTeamName)) {
				qErrorLbl.setText("It's your turn to answer.");
				qSubmitBtn.setEnabled(true);
				qAnswerArea.setEditable(true);
				qPassBtn.setVisible(false);
				qPassBtn.setEnabled(false);
			} else {
				qPassBtn.setVisible(false);
			}
			qTeamLbl.setText(gameData.buzzedInTeam());
			teamPrompt.append(gameData.buzzedInTeam() + " buzzed in.\n");	
			gameData.buzzInTeam(null); // reset the buzzed in team after label
		}
		if (allTeamsAnswered() && gameData.getQsAnswered() != 25) {
			setupQuestionListPanel();
			gameData.resetTeamAnsweredFlag();
		}
//		// check if it's final jeopardy
		if (gameData.getQsAnswered()== 25 && !gameData.inFinalJeopardy()) {
			createNetworkedFinalJeopardy();
			gameData.inFinalJeopardy(true);
		}
		// check if FJ got pdate (a team places bet)
		if (gameData.teamBet()) {
			if (gameData.getTeamJustBet() != -1) {
				teamPrompt.append(gameData.getTeam(gameData.getTeamJustBet()).getName() + " bet $" + gameData.getTeam(gameData.getTeamJustBet()).getBet() + ".\n");
				FJnotifyLbl.setText(gameData.getTeam(gameData.getTeamJustBet()).getName() + " bet $" + gameData.getTeam(gameData.getTeamJustBet()).getBet() + ".\n");
				gameData.setTeamJustBet(-1);
			}
			checkAllBetsNetworked();
			gameData.teamBet(false);
		}
		// check if all users have entered their answers
		if (allFJAnswersEntered() && !ratingsOpen) {
			checkFJAnswersNetworked();
			FJnotifyLbl.setText("\n");
			ratingsOpen = true;
		} else
			FJnotifyLbl.setText("Waiting for the rest of the players to answer...");
		if (gameData.allTeamsRatedFile()) {
			if (gameServer != null) {
				Helpers.saveFile(gameData.getGameFile(), gameData.getFileRankingItems());
				gameData.resetVariables();
				new FileChooser(myTeamName).setVisible(true);
				dispose();
			}
		}
	}
}