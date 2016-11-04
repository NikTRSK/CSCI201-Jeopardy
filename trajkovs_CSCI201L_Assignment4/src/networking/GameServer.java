package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import GUI.FileChooser;
import GUI.GameData;

public class GameServer {
	// Vector holding all the clients
	private Vector<ServerThread> playerThreads;
	// port to listen connections on
	private int port;
	// The game data that we send
	private GameData gd = null;
	// holds the gui
	private FileChooser fc;
	// start stop server
	private boolean listenForConnections;
	// used to check if all clients have connected
	private boolean allPlayersConnected;
	//used to check the amount of teams playing
	int numTeams;
	
	public GameServer(int port, int numTeams, GameData gd, FileChooser fc) {
		this.port = port;
		playerThreads = new Vector<ServerThread>();
		this.numTeams = numTeams;
		this.gd = gd;
		this.fc = fc;
	}
	
	// starts the server
	public void start() {
		listenForConnections = true;
		allPlayersConnected = false;
		// create socket server and wait for connection requests
		// server socket
//		ServerSocket serverSocket = null;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
//			new ServerRunning().start();
			// keep listening for connections
			while (listenForConnections) {
				System.out.println("Game Server waiting for connections on port " + port + ".");
//				System.out.println("LP: " + serverSocket.getLocalPort() + "is closed?: " + serverSocket.isClosed());
				Socket socket = serverSocket.accept();	// accept connections
				// wait for all the clients to connect
//				while (!allPlayersConnected) {
					ServerThread pt = new ServerThread(socket/*, this*/);	// make it a thread
					playerThreads.add(pt);	// add it to the list
					System.out.println("Thread added " + playerThreads.size());
					pt.sendTeamsWaitingInQueue(numTeams - playerThreads.size());
//				}
				if (playerThreads.size() == numTeams)
					allPlayersConnected = true;
//				System.out.println("ha: " + serverSocket != null);
				// stop server if requested
//				if(!listenForConnections)
//					break;
				// after all clients have connected start the threads
				if (allPlayersConnected) {
					for (ServerThread p : playerThreads) {
						p.start();
						gd.addTeam(p.getTeamName());
						System.out.println("Size after add: " + gd.getAllTeams().size());
//						p.sendGameData(this.gd);
//						System.out.println("=== TEAM_NAME: " + p.getTeamName());
					}
					for (ServerThread p : playerThreads) {
						p.sendGameData(this.gd);
						System.out.println("=== TEAM_NAME: " + p.getTeamName());
					}
				}
			}
			// If stop requested
			try {
				serverSocket.close();
				for (ServerThread pt : playerThreads) {
					try {
						pt.ois.close();
						pt.oos.close();
						pt.s.close();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
			catch (Exception e) {
				System.out.println("Exception closing the server and clients: " + e.getMessage());
			}
		}
		catch (IOException e) {
			System.out.println("Exception in server: " + e.getMessage());
		}
	}
		
	// stop the server
	protected void stop() {
		listenForConnections = false;
		// connect to self to close
		try {
			new Socket("localhost", port);
		} catch (Exception e) { }
	}
	
	protected synchronized void broadcastGameData(GameData gd/*GameData gd*/) {
//	fc.updateWaitingLabel(numTeams - playerThreads.size());
//	for (ServerThread pt : playerThreads) {
////		pt.sendGameData(gd);
//		pt.sendGameData(s);
//	}
	if (gd != null) {
		synchronized(playerThreads) {
			for (ServerThread pt : playerThreads)
				pt.sendGameData(gd);
		}
	}
}
	
	protected synchronized void broadcastGameData(String s/*GameData gd*/) {
//		fc.updateWaitingLabel(numTeams - playerThreads.size());
//		for (ServerThread pt : playerThreads) {
////			pt.sendGameData(gd);
//			pt.sendGameData(s);
//		}
		if (s != null) {
			synchronized(playerThreads) {
				for (ServerThread pt : playerThreads)
					pt.sendGameData(s);
			}
		}
	}
	
	
	class ServerRunning extends Thread {
		public void run() {
			System.out.println("TEST");
//			updateWaitingLabel(123);
//			gs.start(); // run server
		}
	}
/*	public void sendGameDataToAllClients() {
		if (gd != null && gameStarted) {
			synchronized(playerThreads) {
				for (ServerThread pt : playerThreads) {
					pt.sendGameData(gd);
				}
			}
		}
	}*/
}
