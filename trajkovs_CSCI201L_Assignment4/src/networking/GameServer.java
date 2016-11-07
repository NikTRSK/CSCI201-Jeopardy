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
//  private FileChooser fc;
  // start stop server
  private boolean listenForConnections;
  // used to check if all clients have connected
  private boolean allPlayersConnected;
  //used to check the amount of teams playing
  int numTeams;
//  private String logoutUser;
  
  public GameServer(int port, int numTeams, GameData gd, FileChooser fc) {
    this.port = port;
    playerThreads = new Vector<ServerThread>();
    this.numTeams = numTeams;
    this.gd = gd;
//    this.fc = fc;
//    this.logoutUser = null;
  }
  
  // starts the server
  public void start() {
    listenForConnections = true;
    allPlayersConnected = false;
    
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      System.out.println("Game Server waiting for connections on port " + port + ".");
      // keep listening for connections
      while (listenForConnections) {      	
        Socket socket = serverSocket.accept();  // accept connections
//        checkPlayers();
        // if the server is stopped
        if (!listenForConnections) {
          break;
        }
        // wait for all the clients to connect
          ServerThread pt = new ServerThread(socket, this); // make it a thread
          playerThreads.add(pt);  // add it to the list
          for (ServerThread p : playerThreads) {
            p.sendTeamsWaitingInQueue(numTeams - playerThreads.size());
          }
        if (playerThreads.size() == numTeams)
          allPlayersConnected = true;
        // after all clients have connected start the threads
        if (allPlayersConnected) {
          for (ServerThread p : playerThreads) {
            p.start();
            gd.addTeam(p.getTeamName());
          }
          gd.InitGame();
          sendToAllClients();
//          for (ServerThread p : playerThreads) {
//            p.sendGameData(this.gd);
//          }
        }
//        logoutUser();
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
  public void stop() {
    listenForConnections = false;
    // connect to self to close
    try {
      new Socket("localhost", port);
    } catch (Exception e) { }
  }
  
  public void checkPlayers() {
    System.out.println("----++++Verifying clients connected");
    for (ServerThread t: playerThreads)
    	System.out.println("Player " + t.getTeamName() + " is connected: " + t.isConnected());
  }
  
  // remove logged out user
  public synchronized void removeThread(String teamName) {
    for (int i = 0; i < playerThreads.size(); ++i) {
      if (playerThreads.get(i).getTeamName().equals(teamName)) {
      	System.out.println(i);
        playerThreads.removeElementAt(i);
        break;
      }
    }
    for (ServerThread p : playerThreads) {
      p.sendTeamsWaitingInQueue(numTeams - playerThreads.size());
    }
  }
  
  protected synchronized void broadcastGameData(GameData gd) {
  if (gd != null) {
  	for (int i = playerThreads.size(); i > 0; --i) {
  		try {
  			playerThreads.get(i).sendGameData(gd);
  		} catch (IOException ioe) {
  			playerThreads.remove(i);
  			continue;
  		}
    }
  }
}
  
//  public void logoutUser(String username) {
//    this.logoutUser = username;
//  }
  
  private void sendToAllClients() {
  	for (int i = playerThreads.size() -1; i > 0; --i) {
  		try {
  			playerThreads.get(i).sendGameData(this.gd);
  		} catch (IOException ioe) {
  			playerThreads.remove(i);
  			System.out.println(i + " disconnected");
  			
  			for (ServerThread p : playerThreads) {
  				p.sendTeamsWaitingInQueue(numTeams - playerThreads.size());
  			}
  			
  			continue;
  		}
    }
  }
  
  protected synchronized void broadcastGameData(String s) {
    if (s != null) {
    	for (int i = playerThreads.size(); i > 0; --i) {
    		try {
    			playerThreads.get(i).sendGameData(s);
    		} catch (IOException ioe) {
    			playerThreads.remove(i);
    			continue;
    		}
      }
    }
  }
}
