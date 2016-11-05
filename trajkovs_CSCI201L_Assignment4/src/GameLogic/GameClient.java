package GameLogic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JPanel;

import GUI.FileChooser;
import GUI.GameBoardUI;
import GUI.GameData;

public class GameClient/* extends Thread */{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	
	private boolean gameDataChanged;
	private boolean gameStarted;
	public GameData gd = null;
	public GameBoardUI gameBoard;
	private FileChooser fc;
	
	private int waitingForPlayers;
	private int port;
	private String hostname, teamName;
	
	public GameClient (String hostname, int port, String teamName, FileChooser fc) {
		this.hostname = hostname;
		this.port = port;
		this.teamName = teamName;
		this.fc = fc;
	}
	
	public void sendUpdateToServer(GameData gameData) {
		System.out.println(teamName + ": Sending game data update...");
		try {
			oos.writeObject(gameData);
			oos.flush();
		} catch (IOException e) {
			System.out.println("IOE updating data: " + e.getMessage());
//			e.printStackTrace();
		}
	}

	// q - answer question panel; j - final jeopardy; l - list questions
	public void sendUpdateToServer(Character c) {
		System.out.println(teamName + ": Sending game data update...");
		try {
			oos.writeObject(c); // q - answer question panel; j - final jeopardy; l - list questions
			oos.flush();
		} catch (IOException e) {
			System.out.println("IOE updating data: " + e.getMessage());
//			e.printStackTrace();
		}
	}
	
	public void showAnswerPanel(JPanel answerPanel) {
		System.out.println(teamName + ": Sending game data update...");
		try {
			oos.writeObject(answerPanel);
			oos.flush();
		} catch (IOException e) {
			System.out.println("IOE updating data: " + e.getMessage());
//			e.printStackTrace();
		}
	}
	
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(hostname, port);
		} catch(Exception e) {
			System.out.println("Error Connecting to server: " + e.getMessage());
			return false;
		}
		System.out.println("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
		
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException ioe) {
			System.out.println("Exception in creation new I/O stream: " + ioe.getMessage());
		}
		
		// create the thread to listen from the server
		new ListenFromServer().start();
//		this.start();
		
		try {
//			if (gd != null) {
//				oos.writeObject(gd);
//				oos.flush();
//				gd = null;
			
			// send team name to server
			if (!gameStarted){
				System.out.println("***SENDING TEAM NAME: " + teamName);
				oos.writeObject(teamName);
				oos.flush();
				gameStarted = true;
			} else {
				oos.writeObject("testing testing");
				oos.flush();
			}
//			}
		} catch (IOException ioe) {
			System.out.println("Exception during login: " + ioe);
			return false;
		}
//		fc.startGame();
		return true;
	}
	
	class ListenFromServer extends Thread {
		private boolean startGame; // used to initialize the game
		
		public void run() {
			while (true) {
				try {
					Object input = ois.readObject();
					
//					GameData gd = (GameData)ois.readObject();
					if (input instanceof Integer) {
						Integer teamsWaiting = (Integer)input;
						if (teamsWaiting == 0) {
							startGame = true;
						}
						else
							fc.updateWaitingLabel(teamsWaiting);
					}
					else if (input instanceof String) {
						String output;
						output = (String)input;
						if (output != null)
							System.out.println("Got data from server: " + output);
					}
					else if (input instanceof GameData) {
						GameData gd = (GameData)input;
						if (startGame) {
							fc.startGame(gd);
							startGame = false;
						} else {
							if (gameBoard != null) {
								System.out.println("updating client ui... " + teamName);
								gameBoard.updateClientData(gd);
								gameBoard.updateClientGUI();
							}
						}
//						ArrayList<Team> t = gd.getAllTeams();
//						System.out.println("Size: " + t.size());
						gd = null; // reset for when we get the data again
					}
					else if (input instanceof Character) {
						Character c = (Character)(input);
					// q - answer question panel; j - final jeopardy; l - list questions
						System.out.println("changing display...");
						if (c == 'q')
							gameBoard.displayAnswerPanel(/*gameBoard.selectedQCat, gameBoard.selectedQPtVal*/);
					}
						
						/*
					else {
						output = (String)input;
					}*/

					// do stuff with data on client side
				} catch (IOException ioe) {
					System.out.println("Connection closed by server: " + ioe.getMessage()); break;
				} catch (ClassNotFoundException cnfe) {
					cnfe.printStackTrace(); break;
				}
			}
		}
	}
}
