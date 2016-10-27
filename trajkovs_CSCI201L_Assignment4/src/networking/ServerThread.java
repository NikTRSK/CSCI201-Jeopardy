package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import GUI.GameData;

public class ServerThread extends Thread {
	private Socket s;
	private BufferedReader br;
	private PrintWriter pw;
	private GameServer gs;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public ServerThread(Socket s, GameServer gs) {
		this.gs = gs;
		this.s = s;
		
		try {
//			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			pw = new PrintWriter(s.getOutputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in Server Thread(): " + ioe.getMessage());
		}
	}
	
	public void sendGameData(GameData gd) {
		try {
			oos.writeObject(gd);
			oos.flush();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void run() {
		try {
			while (true) {
				GameData gd = (GameData)ois.readObject();
				gs.sendGameDataToAllClients(gd);
			}
		} catch (IOException ioe) {
			System.out.println("ioe in run(): " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run(): " + cnfe.getMessage());
		}
		finally {
			try {
				if (pw != null) pw.close();
				if (br != null) br.close();
				if (s != null) s.close();
			} catch (IOException ioe) {}
		}
	}
}
