package GameLogic;

import java.awt.Window.Type;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import GUI.FileChooser;
import GUI.GameData;

public class GameClient/* extends Thread */{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	
	private boolean gameDataChanged;
	private boolean gameStarted;
	public GameData gd = null;
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
		public void run() {
			while (true) {
				try {
					Object input = ois.readObject();
					
//					GameData gd = (GameData)ois.readObject();
					if (input instanceof Integer) {
						Integer teamsWaiting = (Integer)input;
						System.out.println("GC: waiting " + teamsWaiting);
						if (teamsWaiting == 0) {
//							fc.startGame();
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
						fc.startGame(gd);
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
