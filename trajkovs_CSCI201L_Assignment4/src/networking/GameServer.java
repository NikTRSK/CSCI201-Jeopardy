package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import GUI.GameData;

public class GameServer {
	private Vector<ServerThread> playerThreads;
	
	public GameServer(int port) {
		ServerSocket ss = null;
		playerThreads = new Vector<ServerThread>();
		try {
			ss = new ServerSocket(port);
			while (true){
				System.out.println("Server started on port " + port + "\nWaiting for connections...");
				Socket s = ss.accept();
				System.out.println("connection from " + s.getInetAddress() + ":" + s.getPort());
				
				ServerThread st = new ServerThread(s, this);
				playerThreads.addElement(st);
			}
		} catch (IOException ioe) { System.out.println("ioe: " + ioe.getMessage()); }
	finally {
			try {
				if (ss != null) {
					ss.close();
				}
			} catch (IOException ioe) {}
		}
	}
	
	public void sendGameDataToAllClients(GameData gd) {
//		if (gd != null && cm.getMessage() != null) {
//			synchronized(serverThreads) {
//				for (ServerThread st : serverThreads) {
//					st.sendMessage(cm);
//				}
//			}
//		}
	}
}
