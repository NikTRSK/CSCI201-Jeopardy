package trajkovs_CSCI201L_Assignment1;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

import javax.swing.BoxLayout;
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
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


// Ref: https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
public class GameBoardUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	static private CardLayout cl = new CardLayout();
	static private JPanel mainPanel;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem restartGame, chooseNewGameFile, exitGame;
	JLabel titleLbl;
	JLabel progressTitle;
	JTextArea teamPrompt;
	JScrollPane promptPane;

	// Question Panel
	JPanel answerQuestionPanel, finalJeopardyPanel;
	JLabel qTeamLbl, qCatLbl, qPtValueLbl, qErrorLbl;
	JTextArea qQuestionArea;
	JTextArea qAnswerArea;
	JButton qSubmitBtn;
	
	JLabel [] catLbls = new JLabel [5];
	List<Pair<JLabel, JLabel>> teamLbl;
	JButton [][] qBtns = new JButton[5][5];
	
	
	// Final Jeopardy components
	JPanel [] finalPtChooser = new JPanel[4];
	JLabel [] teamPtLbl = new JLabel[4];
	JSlider [] ptSelectSlider = new JSlider[4];
	JLabel [] teamPtValLbl = new JLabel[4];
	JButton [] setBetBtn = new JButton[4];
	JTextPane FJQArea;
	JTextField [] FJAArea = new JTextField[4];
	JButton [] FJABtn = new JButton[4];
	
	public GameBoardUI () {
		super("Play Jeopardy");
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
			catLbls[i] = new JLabel(GamePlay.Categories[i], SwingConstants.CENTER);
			Helpers.styleComponent(catLbls[i], new Color(9, 204, 185), new Color(0,150,136), 20);
		}

		// Create Question buttons
		for (int pt = 0; pt < 5; ++pt) {
			for (int cat = 0; cat < 5; ++cat) {
				qBtns[cat][pt] = (new JButton("$" + GamePlay.Points[pt]));
				Helpers.styleComponent(qBtns[cat][pt], new Color(56,57,49), new Color(0,150,136), 22);
				qBtns[cat][pt].setForeground(Color.WHITE);
				qBtns[cat][pt].setPreferredSize(new Dimension(100, 100));
			}
		}
		
		// fill out an empty array of pairs for teams
		teamLbl = new ArrayList<Pair<JLabel, JLabel> >() ;
		for (int i = 0; i < 4; ++i)
			teamLbl.add(new Pair<JLabel, JLabel>(new JLabel("", SwingConstants.CENTER), new JLabel("", SwingConstants.CENTER)));
		
		// Generate all teams and values to Score Panel
		for (int i = 0; i < GamePlay.Teams.size(); i++) {
			teamLbl.get(i).getItem1().setText(GamePlay.Teams.get(i).getName()); 
			teamLbl.get(i).getItem1().setFont(new Font("Cambria", Font.BOLD, 20));
			teamLbl.get(i).getItem1().setForeground(Color.WHITE);
			teamLbl.get(i).getItem2().setText("$" + GamePlay.Teams.get(i).getPoints());
			teamLbl.get(i).getItem2().setFont(new Font("Cambria", Font.BOLD, 20));
			teamLbl.get(i).getItem2().setForeground(Color.WHITE);
		}
		
		// Progress panel components
		progressTitle = new JLabel("Game Progress", SwingConstants.CENTER);
		progressTitle.setAlignmentX(CENTER_ALIGNMENT); progressTitle.setAlignmentY(TOP_ALIGNMENT);
		progressTitle.setPreferredSize(new Dimension(400, 50));
		progressTitle.setForeground(Color.BLACK);
		progressTitle.setFont(new Font("Cambria", Font.BOLD, 25));
		
		teamPrompt = new JTextArea("Welcome to Jeopardy!\nThe team to go first is " + GamePlay.Teams.get(GamePlay.currTeam).getName() + "\n");
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
		qSubmitBtn.setPreferredSize(new Dimension(150, 60));
		qSubmitBtn.setFont(new Font("Cambria", Font.BOLD, 18));
		
		qErrorLbl = new JLabel("\n", SwingConstants.CENTER);
		qErrorLbl.setVerticalAlignment(SwingConstants.CENTER);
		qErrorLbl.setForeground(Color.LIGHT_GRAY);
		qErrorLbl.setFont(new Font("Cambria", Font.BOLD, 22));
	}
	
	private void createGUI() {
		// Initial setup
		setSize(1500, 825);
		setResizable(false);
		setLocation(100, 30);
		add(menuBar, BorderLayout.NORTH);
		JPanel screen = new JPanel();
		screen.setLayout(new BoxLayout(screen, BoxLayout.X_AXIS));
		screen.setPreferredSize(new Dimension(1030,825));
		add(screen, BorderLayout.WEST);
		
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(1030,825));
		mainPanel.setLayout(cl);
		mainPanel.setPreferredSize(new Dimension(200, 200));
		screen.add(mainPanel);
		
		// Create the main Panel
		JPanel questionListPanel = new JPanel();
		questionListPanel.setLayout(new BoxLayout(questionListPanel, BoxLayout.Y_AXIS));
		questionListPanel.setPreferredSize(new Dimension(1030,825));
	  Border mainBorder = new EmptyBorder(10, 20, 50, 20);
		questionListPanel.setBorder(mainBorder);
		mainPanel.add(questionListPanel, "questionListPanel");
				
		JPanel titlePanel = new JPanel(); titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER)); 
		titlePanel.setPreferredSize(new Dimension(1030, 10));
		titlePanel.setBackground(new Color(0,150,136));
		titlePanel.add(titleLbl);
		questionListPanel.add(titlePanel);
		
		// Create Category Panel
		JPanel questionLabelsPanel = new JPanel(); questionLabelsPanel.setLayout(new GridLayout(1, 5));
		questionListPanel.add(questionLabelsPanel);
		
		// add category labels to grid
		for (int i = 0; i < catLbls.length; ++i)
		questionLabelsPanel.add(catLbls[i]);
		
		// Create whole board
		JPanel questionPanel = new JPanel(); questionPanel.setLayout(new GridLayout(5, 5));
		questionListPanel.add(questionPanel);
		for (int pt = 0; pt < 5; ++pt) {
			for (int cat = 0; cat < 5; ++cat) {
				questionPanel.add(qBtns[cat][pt]);
			}
		}
		
		// Side panel
		JPanel sidePanel = new JPanel(new GridLayout(2, 1));
		sidePanel.setPreferredSize(new Dimension(screen.getWidth()/6, screen.getHeight()));
		add(sidePanel, BorderLayout.CENTER);
		
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
				GamePlay.InitGame();
				if (FileChooser.quickPlay.isSelected())
					GamePlay.qsAnswered = 20;
				
				// Create Question buttons
				for (int pt = 0; pt < 5; ++pt) {
					for (int cat = 0; cat < 5; ++cat) {
						qBtns[cat][pt].setEnabled(true);
						qBtns[cat][pt].setBackground(new Color(56,57,49));
					}
				}
				// Reset points in Point side panel
				for (int i = 0; i < GamePlay.Teams.size(); i++)
					teamLbl.get(i).getItem2().setText("$" + GamePlay.Teams.get(i).getPoints());
				teamPrompt.setText("Game Restarted!\nWelcome to Jeopardy!\nThe team to go first is " + GamePlay.Teams.get(GamePlay.currTeam).getName() + "\n");
			}
		});
		
		chooseNewGameFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Jeopardy.fileChooser = null;
				Jeopardy.fileChooser = new FileChooser();
				Jeopardy.GameBoard.setVisible(false);
				Jeopardy.GameBoard = null;
				GamePlay.resetVariables();
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
				GamePlay.currQuestion = GamePlay.getQuestion(GamePlay.Categories[cat],  GamePlay.Points[ptValue]);
				// Update info on questionPanel
				qTeamLbl.setText(GamePlay.Teams.get(GamePlay.currTeam).getName());
				qCatLbl.setText(Helpers.capitalize(GamePlay.currQuestion.getCategory()));
				qPtValueLbl.setText("$" + GamePlay.currQuestion.getPointValue());
				qQuestionArea.setText(GamePlay.currQuestion.getQuestion());
				qErrorLbl.setText("\n");
				qAnswerArea.setText("");
				showPanel("answerQuestionPanel"); // show question panel
				teamPrompt.append(GamePlay.Teams.get(GamePlay.currTeam).getName() + " chose the question in " + Helpers.capitalize(GamePlay.currQuestion.getCategory()) + " worth $" + GamePlay.currQuestion.getPointValue() + "\n");
				((JButton)e.getSource()).setEnabled(false);
				((JButton)e.getSource()).setBackground(new Color(113, 115, 98));
				qAnswerArea.requestFocusInWindow();
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
		
		for (int i = 0; i < GamePlay.Teams.size(); ++i) {
			ptSelectSlider[i].addChangeListener(new updateSelectedPtLabelListener(ptSelectSlider[i], teamPtValLbl[i]));
		}
		
		// Answer Button
		qSubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String answer = qAnswerArea.getText().trim();
				String [] ansBeginning = answer.split("\\s+");
				// Check if the user's answer begins with the proper format
				if ( Helpers.elementExists(GamePlay.Answers[0], ansBeginning[0]) && Helpers.elementExists(GamePlay.Answers[1], ansBeginning[1]) ) {
					if (GamePlay.checkAnswer(ansBeginning, GamePlay.currQuestion.getAnswer())) { // if the answer is correct add points to the user
						GamePlay.currQuestion.setAnswered();
						teamPrompt.append(GamePlay.Teams.get(GamePlay.currTeam).getName() + ", you got the answer right! " + printPts(GamePlay.currQuestion.getPointValue()) + " will be added to your score.\n");
						GamePlay.Teams.get(GamePlay.currTeam).addPoints(GamePlay.currQuestion.getPointValue());
						teamLbl.get(GamePlay.currTeam).getItem2().setText("$" + GamePlay.Teams.get(GamePlay.currTeam).getPoints());
						GamePlay.updateCurrentTeam();
					} else { // if not correct subtract points
						GamePlay.currQuestion.setAnswered();
						teamPrompt.append(GamePlay.Teams.get(GamePlay.currTeam).getName() + ", that is the wrong answer! " + printPts(GamePlay.currQuestion.getPointValue()) + " will be subtracted from your score.\n");
						GamePlay.Teams.get(GamePlay.currTeam).subPoints(GamePlay.currQuestion.getPointValue());
						teamLbl.get(GamePlay.currTeam).getItem2().setText(printPts(GamePlay.Teams.get(GamePlay.currTeam).getPoints()));
						GamePlay.updateCurrentTeam();
					}
					if (GamePlay.qsAnswered == 25)
						createFinalJeopardy();
					GamePlay.qsAnswered++;
					if (GamePlay.qsAnswered < 25) {
						showPanel("questionListPanel");
						teamPrompt.append("Now it's " + GamePlay.Teams.get(GamePlay.currTeam).getName() + "'s turn! Please Choose a question.\n");
					}
					GamePlay.numTries = 0;
				} else { // If it doesn't give the user a second chance
					qErrorLbl.setText("The answer needs to be posed as a question. Try again!");
					qAnswerArea.setText("");
					++GamePlay.numTries;
				}
				// check if number of tries exceeded
				if (GamePlay.numTries == 2) {
					teamPrompt.append(GamePlay.Teams.get(GamePlay.currTeam).getName() + ", that is the wrong answer! " + printPts(GamePlay.currQuestion.getPointValue()) + " will be subtracted from your score.\n");
					GamePlay.Teams.get(GamePlay.currTeam).subPoints(GamePlay.currQuestion.getPointValue());
					if (GamePlay.qsAnswered == 25)
						createFinalJeopardy();
					else showPanel("questionListPanel");
					GamePlay.qsAnswered++;
					teamLbl.get(GamePlay.currTeam).getItem2().setText(printPts(GamePlay.Teams.get(GamePlay.currTeam).getPoints()));
					GamePlay.updateCurrentTeam();
					teamPrompt.append("Now it's " + GamePlay.Teams.get(GamePlay.currTeam).getName() + "'s Please Choose a question.\n");
					GamePlay.numTries = 0;
				}
				if (GamePlay.qsAnswered == 25)
					createFinalJeopardy();
			}
			
			private void createFinalJeopardy() {
				if (GamePlay.teamsAllNegative()) {
					
					JDialog noJeopardyDialog = new JDialog();
					noJeopardyDialog.setPreferredSize(new Dimension(400, 250));
					noJeopardyDialog.setTitle("Thank you for playing");
					
					JPanel Panel = new JPanel(); Panel.setLayout(new BoxLayout(Panel, BoxLayout.Y_AXIS));
					Panel.setBackground(new Color(0,150,136));
					
					// Create message
					SimpleAttributeSet paneStyle = new SimpleAttributeSet();
					StyleConstants.setAlignment(paneStyle, StyleConstants.ALIGN_CENTER);
					StyleConstants.setFontFamily(paneStyle, "Cambria BOLD");
					StyleConstants.setFontSize(paneStyle, 18);

					JTextPane msg = new JTextPane();
					msg.setEditable(false);
					msg.setText("There are no teams eligible for Final Jeopardy.\nThere are no winners");
					msg.setBackground(new Color(0,150,136));
					msg.setForeground(Color.WHITE);
					StyledDocument doc = msg.getStyledDocument();
					doc.setParagraphAttributes(0, 104, paneStyle, false);
					Panel.add(msg);
					noJeopardyDialog.add(Panel);
					
					JPanel buttonPanel = new JPanel(); buttonPanel.setBackground(new Color(0,150,136));
					JButton OKBtn = new JButton("Okay");
					OKBtn.setBorder(new MatteBorder(13,16,13,16, Color.WHITE));
					OKBtn.setBackground(Color.WHITE);
					OKBtn.setForeground(new Color(0,150,136));
					OKBtn.setFont(new Font("Cambria", Font.BOLD, 25));
					// style button
					buttonPanel.add(OKBtn);
					noJeopardyDialog.setAlwaysOnTop(true);
					noJeopardyDialog.setLocationRelativeTo(mainPanel);
					noJeopardyDialog.setVisible(true);
					OKBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent ae) {
							noJeopardyDialog.setVisible(false);
							Jeopardy.GameBoard.setVisible(false);
							Jeopardy.fileChooser = null;
							Jeopardy.fileChooser = new FileChooser();
							Jeopardy.GameBoard = null;
							GamePlay.resetVariables();
						}
					});
					noJeopardyDialog.add(buttonPanel, BorderLayout.SOUTH);
					noJeopardyDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					noJeopardyDialog.pack();
				} else {
					teamPrompt.append("------Welcome to Final Jeopardy!------\n");
					// check who can answer the question
					for (int i = 0; i < GamePlay.Teams.size(); ++i) {
						if (GamePlay.Teams.get(i).getPoints() <= 0) { 
							setBetBtn[i].setEnabled(false);
							ptSelectSlider[i].setEnabled(false);
							teamPrompt.append(GamePlay.Teams.get(i).getName() + " doesn't have enough points to bet.\n");
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
	      ptSelectSlider[i].setMaximum(GamePlay.Teams.get(i).getPoints());
	      ptSelectSlider[i].setMinimum(0);
	      ptSelectSlider[i].setValue(1);

	      int spacing = 0;
	      if (GamePlay.Teams.get(i).getPoints() <= 100) spacing = 10;
	      else if (GamePlay.Teams.get(i).getPoints() <= 1000) spacing = 100;
	      else if (GamePlay.Teams.get(i).getPoints() <= 2000) spacing = 200;
	      else if (GamePlay.Teams.get(i).getPoints() <= 3000) spacing = 300;
	      else if (GamePlay.Teams.get(i).getPoints() <= 4000) spacing = 400;
	      else if (GamePlay.Teams.get(i).getPoints() <= 5000) spacing = 500;
	      else if (GamePlay.Teams.get(i).getPoints() <= 5000) spacing = 600;
	      else spacing = 1000;
	      ptSelectSlider[i].setMajorTickSpacing(spacing);
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
				GamePlay.FJBets[teamID] = ptSelectSlider[teamID].getValue();
				((JButton)e.getSource()).setEnabled(false);
				teamPrompt.append(GamePlay.Teams.get(teamID).getName() + " bet $" + GamePlay.FJBets[teamID] + "\n");
				
				checkAllBets();
			}
			
			// enables final jeopardy
			private void checkAllBets() {
				for (int i = 0; i < GamePlay.Teams.size(); ++i) {
					if (setBetBtn[i].isEnabled())
						return;
				}
				for (int i = 0; i < GamePlay.Teams.size(); ++i) {
					if (GamePlay.Teams.get(i).getPoints() > 0) {
						FJABtn[i].setEnabled(true);
						FJAArea[i].setEnabled(true);
						FJQArea.setText(GamePlay.FJQuestion.getQuestion());
						teamPrompt.append("Here is the Final Jeopardy question:\n" + GamePlay.FJQuestion.getQuestion() + "\n");
					}
				}
			}
		}
		
		for (int i = 0; i < GamePlay.Teams.size(); ++i)
			setBetBtn[i].addActionListener(new setBetListener(i));
		
		// When clicked clear text box
		for (int i = 0; i < GamePlay.Teams.size(); ++i) {
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
				GamePlay.FJAnswers[teamID] = FJAArea[teamID].getText().trim();
				((JButton)e.getSource()).setEnabled(false);
				
				// if all answers inputed check answers
				if (allAnswersEntered()) {
					for (int i = 0; i < GamePlay.Teams.size(); ++i) {
						checkFJAnswer(i);
					}
					// display winner
					showWinner();
				}
			}
			
			private boolean allAnswersEntered() {
				for (int i = 0; i < GamePlay.Teams.size(); ++i) {
					if (FJABtn[i].isEnabled())
						return false;
				}
				return true;
			}
			
			private void checkFJAnswer(int id) {
				// check if the answer is null (team not eligible to answer)
				if (GamePlay.FJAnswers[id] == null) return;
				
				String [] ansBeginning = GamePlay.FJAnswers[id].split("\\s+");
				// Check if the answer is in a question form
				if (GamePlay.checkAnswer(ansBeginning, GamePlay.FJQuestion.getAnswer())) {
					GamePlay.Teams.get(id).addPoints(GamePlay.FJBets[id]);
					teamPrompt.append(GamePlay.Teams.get(id).getName() + " got the answer right. " + printPts(GamePlay.FJBets[id]) + " will be added to their total.\n");
				} else {	// no second change in Final Jeopardy
					GamePlay.Teams.get(id).subPoints(GamePlay.FJBets[id]);
					teamPrompt.append(GamePlay.Teams.get(id).getName() + " got the answer wrong. " + printPts(GamePlay.FJBets[id]) + " will be subtracted from their total.\n");
				}
				teamLbl.get(id).getItem2().setText("$" + GamePlay.Teams.get(id).getPoints());
			}
			
			private void showWinner() {
				FJQArea.setText("Answer: " + GamePlay.FJQuestion.getAnswer() + "\n");
				teamPrompt.append("The answer is: " + GamePlay.FJQuestion.getAnswer() + "\n");
				
				JDialog winnerDialog = new JDialog();
				winnerDialog.setPreferredSize(new Dimension(400, 250));
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
				lbl.setText("And the winner(s) is/are\n");
				lbl.setBackground(new Color(0,150,136));
				lbl.setForeground(Color.WHITE);
				StyledDocument doc = lbl.getStyledDocument();
				doc.setParagraphAttributes(0, 104, paneStyle, false);
				winnerPanel.add(lbl);
				ArrayList<Integer> winner = GamePlay.findWinner();
				for (Integer teamID : winner) {
					lbl.setText(lbl.getText() + GamePlay.Teams.get(teamID).getName() + "\n");
				}
				
				winnerDialog.add(winnerPanel);
				
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
						winnerDialog.setVisible(false);
						Jeopardy.GameBoard.setVisible(false);
						Jeopardy.fileChooser = null;
						Jeopardy.fileChooser = new FileChooser();
						Jeopardy.GameBoard = null;
						GamePlay.resetVariables();
					}
					
				});
				winnerDialog.add(buttonPanel, BorderLayout.SOUTH);
				winnerDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				winnerDialog.pack();
				setVisible(true);
			}
		}
		
		for (int i = 0; i < GamePlay.Teams.size(); ++i) {
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
	  answerPanel.setPreferredSize(new Dimension(answerQuestionPanel.getWidth(), 100));
	  answerQuestionPanel.add(answerPanel);
	  gbc.insets = new Insets(0,0,0,10);
	  answerPanel.add(qAnswerArea, gbc);
	  answerPanel.add(qSubmitBtn);
		
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
	  for (int i = 0; i < GamePlay.Teams.size(); ++i) {
	  	teamPtLbl[i] = new JLabel(GamePlay.Teams.get(i).getName() + "'s bet:", SwingConstants.CENTER);
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
	    if (GamePlay.Teams.get(i).getPoints() <= 0)
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
	  
	  for (int i = 0; i < GamePlay.Teams.size(); ++i) {
	  	FJAArea[i] = new JTextField();
	  	Helpers.styleComponentFlat(FJAArea[i], Color.BLACK, Color.WHITE, Color.WHITE, 17, true);
	  	FJAArea[i].setText(GamePlay.Teams.get(i).getName() + ", enter your answer.");
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
	
	public static void showPanel(String panelName) {
		if (panelName.equals("questionListPanel") || panelName.equals("answerQuestionPanel") || panelName.equals("finalJeopardyPanel"))
			cl.show(mainPanel, panelName);
	}

	private String printPts(int pts) {
		if (pts < 0)
			return ("-$" + Math.abs(pts));
		else
			return ("$" + pts);
	}
}
