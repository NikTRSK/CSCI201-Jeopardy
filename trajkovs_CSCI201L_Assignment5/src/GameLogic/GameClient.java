package GameLogic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JPanel;

import GUI.FileChooser;
import GUI.GameBoardUI;
import GUI.GameData;

public class GameClient {
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	private boolean gameStarted;
	public GameData gd = null;
	public GameBoardUI gameBoard;
	private FileChooser fc;
	private int port;
	private String hostname, teamName;
	
	public GameClient (String hostname, int port, String teamName, FileChooser fc) {
		this.hostname = hostname;
		this.port = port;
		this.teamName = teamName;
		this.fc = fc;
	}
	
	public void sendUpdateToServer(GameData gameData) {
		System.out.println("CLIENT LISTENER IS SENDING DATA");
		try {
			oos.writeObject(gameData);
			oos.flush();
		} catch (IOException e) {
			System.out.println("IOE updating data: " + e.getMessage());
		}
	}
	
	public void showAnswerPanel(JPanel answerPanel) {
		try {
			oos.writeObject(answerPanel);
			oos.flush();
		} catch (IOException e) {
			System.out.println("IOE updating data: " + e.getMessage());
		}
	}
	
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(hostname, port);
		} catch(Exception e) {
			System.out.println("Error Connecting to server: " + e.getMessage());
			System.out.println("here");
//			if (fc != null)
//			fc.serverLoggedOut();
			fc.cantConnect();;
			return false;
		}
		
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException ioe) {
			System.out.println("Exception in creation new I/O stream: " + ioe.getMessage());
		}
		// create the thread to listen from the server
		new ListenFromServer().start();
		
		try {
			// send team name to server
			if (!gameStarted){
				oos.writeObject(teamName);
				oos.flush();
				gameStarted = true;
			}
		} catch (IOException ioe) {
			System.out.println("Exception during login: " + ioe);
			return false;
		}
		return true;
	}
	
	public void disconnect() {
		String logoutClient = "LOGOUT:" + teamName;
		try {
			oos.writeObject(logoutClient);
			oos.flush();
		} catch (IOException e1) {}
	}
	
	class ListenFromServer extends Thread {
		private boolean startGame; // used to initialize the game
		
		public void run() {
			while (true) {
				try {
					Object input = ois.readObject();
					if (input instanceof Integer) {
						Integer teamsWaiting = (Integer)input;
						if (teamsWaiting == 0) {
							startGame = true;
						}
						else
							fc.updateWaitingLabel(teamsWaiting);
					}
					if (input instanceof GameData) {
						GameData gd = (GameData)input;
						if (startGame) {
							fc.startGame(gd);
							startGame = false;
						} else {
							if (gameBoard != null) {
								if (gd.timerStopped())
									gameBoard.timer.stopTimer();
								gameBoard.updateClientData(gd);
								gd = null; // reset for when we get the data again
								System.out.println("Updating client...");
//								if (gd.sto)
								gameBoard.updateClientGUI();
							}
						}
//						gd = null; // reset for when we get the data again
					}
					input = null;
					// do stuff with data on client side
				} catch (IOException ioe) {
					System.out.println("Connection closed by server: " + ioe.getMessage());
					fc.serverLoggedOut();
					break;
				} catch (ClassNotFoundException cnfe) {
					cnfe.printStackTrace(); break;
				}
			}
		}
	}
}
