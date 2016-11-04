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
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import GameLogic.GameClient;
import GameLogic.Question;
import GameLogic.Team;
import other.GameConstants;
import other.Helpers;
import other.Pair;


// Ref: https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
public class GameBoardUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	static private CardLayout cl = new CardLayout();
	static private JPanel mainPanel;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem restartGame, chooseNewGameFile, logoutGame, exitGame;
	JLabel titleLbl;
	JLabel progressTitle;
	JTextArea teamPrompt;
	JScrollPane promptPane;

	// Question Panel
	JPanel answerQuestionPanel, finalJeopardyPanel;
	JLabel qTeamLbl, qCatLbl, qPtValueLbl, qErrorLbl;
	JTextArea qQuestionArea;
	JTextArea qAnswerArea;
	JButton qSubmitBtn, qPassBtn;
	
	JLabel [] catLbls = new JLabel [5];
	List<Pair<JLabel, JLabel>> teamLbl;
	JButton [][] qBtns = new JButton[5][5];
	ImageIcon qBtnsEnabled, qBtnsDisabled;
	
	
	// Final Jeopardy components
	JPanel [] finalPtChooser = new JPanel[4];
	JLabel [] teamPtLbl = new JLabel[4];
	JSlider [] ptSelectSlider = new JSlider[4];
	JLabel [] teamPtValLbl = new JLabel[4];
	JButton [] setBetBtn = new JButton[4];
	JTextPane FJQArea;
	JTextField [] FJAArea = new JTextField[4];
	JButton [] FJABtn = new JButton[4];
	
	String loggedInUser; // current user
	GameData gameData;
	ArrayList<Team> Teams; // teams playing
	String [] Categories;
	int [] Points;
	
	Question currQuestion = null;
	int qsAnswered, numTries;
	
	GameClient gameClient = null;
	
	public GameBoardUI (GameData gameData, String loggedInUser) {
		super("Play Jeopardy");
		this.loggedInUser = loggedInUser;
		this.gameData = gameData;
		this.qsAnswered = gameData.getQsAnswered();
		this.numTries = 0;
		Teams = gameData.getAllTeams();
		Categories = gameData.getAllCategories();
		Points = gameData.getAllPoints();
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeComponents() {

		// Create the menu bar
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menuBar.add(menu);
		
		// Create Menu items
		restartGame = new JMenuItem("Restart This Game");
		menu.add(restartGame);
		
		chooseNewGameFile = new JMenuItem("Choose New Game File");
		menu.add(chooseNewGameFile);
		
		logoutGame = new JMenuItem("Logout");
		menu.add(logoutGame);
		logoutGame.addActionListener((ActionEvent event) -> {
      new LoginScreen().setVisible(true);
      dispose();
		});

		exitGame= new JMenuItem("Exit Game");
		exitGame.addActionListener((ActionEvent event) -> {
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
		for (int i = 0; i < 4; ++i)
			teamLbl.add(new Pair<JLabel, JLabel>(new JLabel("", SwingConstants.CENTER), new JLabel("", SwingConstants.CENTER)));
		
		// Generate all teams and values to Score Panel
		for (int i = 0; i < Teams.size(); i++) {
			teamLbl.get(i).getItem1().setText(Teams.get(i).getName()); 
			teamLbl.get(i).getItem1().setFont(new Font("Cambria", Font.BOLD, 20));
			teamLbl.get(i).getItem1().setForeground(Color.WHITE);
			teamLbl.get(i).getItem2().setText("$" + Teams.get(i).getPoints());
			teamLbl.get(i).getItem2().setFont(new Font("Cambria", Font.BOLD, 20));
			teamLbl.get(i).getItem2().setForeground(Color.WHITE);
		}
		
		// Progress panel components
		progressTitle = new JLabel("Game Progress", SwingConstants.CENTER);
		progressTitle.setAlignmentX(CENTER_ALIGNMENT); progressTitle.setAlignmentY(TOP_ALIGNMENT);
//		progressTitle.setPreferredSize(new Dimension(400, 50));
		progressTitle.setForeground(Color.BLACK);
		progressTitle.setFont(new Font("Cambria", Font.BOLD, 25));
		
		teamPrompt = new JTextArea("Welcome to Jeopardy!\nThe team to go first is " + Teams.get(gameData.getNextTeam()).getName() + "\n");
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
		
		JPanel scorePanel = new JPanel(new GridLayout(4, 2));
		scorePanel.setBackground(new Color(56,57,49));
		sidePanel.add(scorePanel);
		
		// add all teams to Score Panel
		for (int i = 0; i < teamLbl.size(); i++) {
			scorePanel.add(teamLbl.get(i).getItem1());
			scorePanel.add(teamLbl.get(i).getItem2());
		}
		
		JPanel gameProgressPanel = new JPanel();
		gameProgressPanel.setLayout(new BoxLayout(gameProgressPanel, BoxLayout.Y_AXIS));
		gameProgressPanel.setBackground(new Color(0,137,123));
		sidePanel.add(gameProgressPanel);
		
		// Create the Game Progress panel		

		gameProgressPanel.add(progressTitle);
		
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
					System.exit(0);
				}
			}
		});
		
		////////////////////
		// Menu Listeners //
		////////////////////
		restartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Reset Game Board
				gameData.InitGame();
//				if (FileChooser.quickPlay.isSelected())
//					GamePlay.qsAnswered = 20;
				
				// Create Question buttons
				for (int pt = 0; pt < 5; ++pt) {
					for (int cat = 0; cat < 5; ++cat) {
						qBtns[cat][pt].setEnabled(true);
						qBtns[cat][pt].setIcon(qBtnsEnabled);
//						qBtns[cat][pt].setBackground(new Color(56,57,49));
					}
				}
				// Reset points in Point side panel
				for (int i = 0; i < Teams.size(); i++)
					teamLbl.get(i).getItem2().setText("$" + Teams.get(i).getPoints());
				teamPrompt.setText("Game Restarted!\nWelcome to Jeopardy!\nThe team to go first is " + Teams.get(gameData.getNextTeam()).getName() + "\n");
			}
		});
		
		chooseNewGameFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameData.resetVariables();
				new FileChooser(loggedInUser).setVisible(true);
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
				currQuestion = gameData.getQuestion(Categories[cat], Points[ptValue]);
				// Update info on questionPanel
				qTeamLbl.setText(Teams.get(gameData.getNextTeam()).getName());
				qCatLbl.setText(Helpers.capitalize(currQuestion.getCategory()));
				qPtValueLbl.setText("$" + currQuestion.getPointValue());
				qQuestionArea.setText(currQuestion.getQuestion());
				qErrorLbl.setText("\n");
				qAnswerArea.setText("");
				showPanel("answerQuestionPanel"); // show question panel
				teamPrompt.append(Teams.get(gameData.getNextTeam()).getName() + " chose the question in " + Helpers.capitalize(currQuestion.getCategory()) + " worth $" + currQuestion.getPointValue() + "\n");
				((JButton)e.getSource()).setEnabled(false);
				((JButton)e.getSource()).setBackground(new Color(113, 115, 98));
				qAnswerArea.requestFocusInWindow();
				qPassBtn.setVisible(false);
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
		
		for (int i = 0; i < Teams.size(); ++i) {
			ptSelectSlider[i].addChangeListener(new updateSelectedPtLabelListener(ptSelectSlider[i], teamPtValLbl[i]));
		}
		
		// Answer Button
		qSubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Get the beginning of question to check for proper formating
				String answer = qAnswerArea.getText().trim();
				String [] ansBeginning = answer.split("\\s+");
				// Check if the user's answer begins with the proper format
				if ( ansBeginning.length > 2 && GameConstants.checkValidBeginningOfQuestion(ansBeginning[0], ansBeginning[1]) ) {
					if (gameData.checkAnswer(ansBeginning, currQuestion.getAnswer())) { // if the answer is correct add points to the user
						correctAnswer();
					} else { // if not correct subtract points
						wrongAnswer();
					}
					if (qsAnswered == 25)
						createFinalJeopardy();
				} else { // If it doesn't give the user a second chance
					qErrorLbl.setText("The answer needs to be posed as a question. Try again!");
					qAnswerArea.setText("");
					++numTries;
				}
				// check if number of tries exceeded
				if (numTries == 2) {
					wrongAnswer();
				}
				if (qsAnswered == 25)
					createFinalJeopardy();
			}
			
			private void wrongAnswer() {
				teamPrompt.append(Teams.get(gameData.getNextTeam()).getName() + ", that is the wrong answer! " + printPts(currQuestion.getPointValue()) + " will be subtracted from your score.\n");
				Teams.get(gameData.getNextTeam()).subPoints(currQuestion.getPointValue());
				teamLbl.get(gameData.getNextTeam()).getItem2().setText(printPts(Teams.get(gameData.getNextTeam()).getPoints()));
				// Update current team
				gameData.updateNextTeam();
				qPassBtn.setVisible(true);
				qPassBtn.setEnabled(true);
				// if there are teams to play
				if (gameData.getCurrentTeam() != gameData.getNextTeam()) {
					teamPrompt.append("It's " + Teams.get(gameData.getNextTeam()).getName() + "'s turn to try to answer the quesiton.\n\n");
					qAnswerArea.setText("");
					numTries = 0;
					qErrorLbl.setText("\n");
				} else {
					
					// check if it's final jeopardy
					if (qsAnswered == 25)
						createFinalJeopardy();
					else {
						gameData.updateCurrentTeam();
						gameData.setNextTeam(gameData.getCurrentTeam());
						qsAnswered++;
						currQuestion.setAnswered();
						teamLbl.get(gameData.getNextTeam()).getItem2().setText(printPts(Teams.get(gameData.getNextTeam()).getPoints()));
//						GamePlay.updateCurrentTeam();
						teamPrompt.append("Now it's " + Teams.get(gameData.getNextTeam()).getName() + "'s Please Choose a question.\n");
						numTries = 0;
						showPanel("questionListPanel");
						qPassBtn.setVisible(false);
					}
				}	
			}
			
			private void correctAnswer() {
				currQuestion.setAnswered();
				teamPrompt.append(Teams.get(gameData.getNextTeam()).getName() + ", you got the answer right! " + printPts(currQuestion.getPointValue()) + " will be added to your score.\n");
				Teams.get(gameData.getNextTeam()).addPoints(currQuestion.getPointValue());
				teamLbl.get(gameData.getNextTeam()).getItem2().setText("$" + Teams.get(gameData.getNextTeam()).getPoints());

				qPassBtn.setEnabled(true);
				if (qsAnswered < 25/* && (gameData.getCurrentTeam() != gameData.getNextTeam())*/) {
					qsAnswered++;
					currQuestion.setAnswered();
					showPanel("questionListPanel");
					qPassBtn.setVisible(false);
					qPassBtn.setEnabled(true);
					teamPrompt.append("Now it's " + Teams.get(gameData.getNextTeam()).getName() + "'s turn! Please Choose a question.\n");
				}
				numTries = 0;
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
      gameData.updateNextTeam();
      if (gameData.getCurrentTeam() != gameData.getNextTeam()) {
				teamPrompt.append("It's " + Teams.get(gameData.getNextTeam()).getName() + "'s turn to try to answer the quesiton.\n\n");
				qAnswerArea.setText("");
				numTries = 0;
				qErrorLbl.setText("\n");
      } else {
				numTries = 0;
				gameData.updateCurrentTeam();
				gameData.setNextTeam(gameData.getCurrentTeam());
				teamPrompt.append("Now it's " + Teams.get(gameData.getNextTeam()).getName() + "'s Please Choose a question.\n");
				qsAnswered++;
				if (qsAnswered == 25)
					createFinalJeopardy();
				else {
					showPanel("questionListPanel");
					qPassBtn.setVisible(false);
					qPassBtn.setEnabled(true);
				}
      }
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
				gameData.setBetForTeam(teamID, betValue);
				((JButton)e.getSource()).setEnabled(false);
//				((JButton)e.getSource()).setIcon(qBtnsDisabled);
				teamPrompt.append(Teams.get(teamID).getName() + " bet $" + betValue + "\n");
				
				checkAllBets();
			}
			
			// enables final jeopardy
			private void checkAllBets() {
				for (int i = 0; i < Teams.size(); ++i) {
					if (setBetBtn[i].isEnabled())
						return;
				}
				for (int i = 0; i < Teams.size(); ++i) {
					if (Teams.get(i).getPoints() > 0) {
						FJABtn[i].setEnabled(true);
						FJAArea[i].setEnabled(true);
						FJQArea.setText(gameData.getFJQuestion());
						teamPrompt.append("Here is the Final Jeopardy question:\n" + gameData.getFJQuestion() + "\n");
					}
				}
			}
		}
		
		for (int i = 0; i < Teams.size(); ++i)
			setBetBtn[i].addActionListener(new setBetListener(i));
		
		// When clicked clear text box
		for (int i = 0; i < Teams.size(); ++i) {
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
				gameData.setFJAnswerForTeam(teamID, FJAArea[teamID].getText().trim());
				((JButton)e.getSource()).setEnabled(false);
				
				// if all answers inputed check answers
				if (allAnswersEntered()) {
					for (int i = 0; i < Teams.size(); ++i) {
						checkFJAnswer(i);
					}
					// display winner
					showWinner();
				}
			}
			
			private boolean allAnswersEntered() {
				for (int i = 0; i < Teams.size(); ++i) {
					if (FJABtn[i].isEnabled())
						return false;
				}
				return true;
			}
			
			private void checkFJAnswer(int id) {
				String [] FJAnswers = gameData.getAllFJAnswers();
				int [] FJBets = gameData.getAllFJBets();
				// check if the answer is null (team not eligible to answer)
				if (FJAnswers[id] == null) return;
				
				String [] ansBeginning = FJAnswers[id].split("\\s+");
				// Check if the answer is in a question form
				if (ansBeginning.length > 2 && gameData.checkAnswer(ansBeginning, gameData.getFJAnswer())) {
					Teams.get(id).addPoints(FJBets[id]);
					teamPrompt.append(Teams.get(id).getName() + " got the answer right. " + printPts(FJBets[id]) + " will be added to their total.\n");
				} else {	// no second change in Final Jeopardy
					Teams.get(id).subPoints(FJBets[id]);
					teamPrompt.append(Teams.get(id).getName() + " got the answer wrong. " + printPts(FJBets[id]) + " will be subtracted from their total.\n");
				}
				teamLbl.get(id).getItem2().setText("$" + Teams.get(id).getPoints());
			}
			
			private void showWinner() {
				FJQArea.setText("Answer: " + gameData.getFJAnswer() + "\n");
				teamPrompt.append("The answer is: " + gameData.getFJAnswer() + "\n");

				createRatingsPanel(true);
			}
		}
		
		for (int i = 0; i < Teams.size(); ++i) {
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
	  JPanel BetsPanel = new JPanel(new GridLayout(4,0));
	  BetsPanel.setBorder(new MatteBorder(0,0,0,50, new Color(56,57,49)));
//	   add all slider panels regardless of # of teams
	  
	  for (int i = 0; i < 4; ++i) {
	  	finalPtChooser[i] = new JPanel();
	  	finalPtChooser[i].setLayout(new BoxLayout(finalPtChooser[i], BoxLayout.X_AXIS));
	  	finalPtChooser[i].setBackground(new Color(56,57,49));
	  	BetsPanel.add(finalPtChooser[i]);
	  }
	  for (int i = 0; i < Teams.size(); ++i) {
	  	teamPtLbl[i] = new JLabel(Teams.get(i).getName() + "'s bet:", SwingConstants.CENTER);
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
	    if (Teams.get(i).getPoints() <= 0)
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

	  JPanel [] FJanswerPanel = new JPanel[4]; 
	  // add all panels to the layout
	  JPanel FJAPanel = new JPanel(new GridLayout(2,2));
	  FJAPanel.setBackground(new Color(56,57,49));
	  FJAPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
	  for (int i = 0; i < 4; ++i) {
	  	FJanswerPanel[i] = new JPanel(); FJanswerPanel[i].setLayout(new BoxLayout(FJanswerPanel[i], BoxLayout.X_AXIS));
	  	FJanswerPanel[i].setBackground(new Color(56,57,49));
	  	FJanswerPanel[i].setBorder(new MatteBorder(0, 20, 0, 20, new Color(56,57,49)));
	  	FJAPanel.add(FJanswerPanel[i]);
	  }
	  
	  for (int i = 0; i < Teams.size(); ++i) {
	  	FJAArea[i] = new JTextField();
	  	Helpers.styleComponentFlat(FJAArea[i], Color.BLACK, Color.WHITE, Color.WHITE, 17, true);
	  	FJAArea[i].setText(Teams.get(i).getName() + ", enter your answer.");
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
	}
	
	private void createFinalJeopardy() {
		if (gameData.teamsAllNegative()) {
			createRatingsPanel(false);
		} else {
			teamPrompt.append("------Welcome to Final Jeopardy!------\n");
			// check who can answer the question
			for (int i = 0; i < Teams.size(); ++i) {
				if (Teams.get(i).getPoints() <= 0) { 
					setBetBtn[i].setEnabled(false);
					ptSelectSlider[i].setEnabled(false);
					teamPrompt.append(Teams.get(i).getName() + " doesn't have enough points to bet.\n");
				}
				else {
					setupSlider(i);
					setBetBtn[i].setEnabled(true);
				}
				FJABtn[i].setEnabled(false);
				FJAArea[i].setEnabled(false);
				
			}
			showPanel("finalJeopardyPanel");
		}
	}
	
	private void setupSlider(int i) {
    ptSelectSlider[i].setMaximum(Teams.get(i).getPoints());
    ptSelectSlider[i].setMinimum(0);
    ptSelectSlider[i].setValue(1);

    int spacing = 0;
    if (Teams.get(i).getPoints() <= 100) spacing = 10;
    else if (Teams.get(i).getPoints() <= 1000) spacing = 100;
    else if (Teams.get(i).getPoints() <= 2000) spacing = 200;
    else if (Teams.get(i).getPoints() <= 3000) spacing = 300;
    else if (Teams.get(i).getPoints() <= 4000) spacing = 400;
    else if (Teams.get(i).getPoints() <= 5000) spacing = 500;
    else if (Teams.get(i).getPoints() <= 5000) spacing = 600;
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
				lbl.setText(lbl.getText() + Teams.get(teamID).getName() + "\n");
			}
		} else {
			lbl.setText("There are no teams eligible for Final Jeopardy.\nThere are no winners");
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
				Helpers.saveFile(gameData.getGameFile(), gameData.getFileRankingItems());
				
				gameData.resetVariables();
				new FileChooser(loggedInUser).setVisible(true);;
				
				dispose();
			}
			
		});
		winnerDialog.add(buttonPanel, BorderLayout.SOUTH);
		winnerDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		winnerDialog.pack();
		setVisible(true);
	}
	
	private String printPts(int pts) {
		if (pts < 0)
			return ("-$" + Math.abs(pts));
		else
			return ("$" + pts);
	}
	
	private void sendGameData() {
		gameClient.sendGameData(gameData);
	}
}
