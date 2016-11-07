package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import GUI.GameData;

public class ServerThread extends Thread {
  Socket s;
  private GameServer gs;
  private String teamName;
  
  ObjectInputStream ois;
  ObjectOutputStream oos;
  
  public ServerThread(Socket s, GameServer gs) {
    this.gs = gs;
    this.s = s;
//    this.teamName = teamName;
    
    try {
      oos = new ObjectOutputStream(s.getOutputStream());
      ois = new ObjectInputStream(s.getInputStream());
      
      System.out.println("Client Connected");
      String str = (String)ois.readObject();
      if (str != null) {
        System.out.println("GOT name: " + str);
        this.teamName = str; 
      }
//      else
//        System.out.println("Team name is null");
    } catch (IOException | ClassNotFoundException ioe) {
      System.out.println("ioe in Server Thread(): " + ioe.getMessage());
    }
  }

  public void sendGameData(GameData gd) {
    try {
      oos.writeObject(gd);
      oos.flush();
//      System.out.println("_____made it through");
    } catch (IOException e) {e.printStackTrace();}
  }
  
  public void sendGameData(String s) {
    try {
      oos.writeObject(s);
      oos.flush();
    } catch (IOException e) {e.printStackTrace();}
  }
  
  public String getTeamName() {
    return teamName;
  }
  
  public void sendTeamsWaitingInQueue(Integer teamsWaiting) {
    try {
      oos.writeObject(teamsWaiting);
      oos.flush();
    } catch (IOException e) { e.printStackTrace(); }
  }
  
  public void run() {
    boolean listenForConnections = true;
    while (listenForConnections) {
      try {
        Object input = ois.readObject();
        String teamName;
        if (input instanceof String) {
          teamName = (String)input;
          if (teamName.contains("LOGOUT:")) {
            String logoutUser = teamName.replace("LOGOUT:", "");
            System.out.println("THREAD DEBUG: LOGOUT " + logoutUser);
//            gs.removeThread(logoutUser);
//            gs.logoutUser(logoutUser);
          }
          if (teamName != null)
            this.teamName = teamName;
        } else if (input instanceof GameData) {
          GameData gd = (GameData)input;
          if (gd != null)
            gs.broadcastGameData(gd);
        }
      } catch (IOException ioe) {
        System.out.println("ioe in run(): " + ioe.getMessage()); break;
      } catch (ClassNotFoundException cnfe) {
        System.out.println("cnfe in run(): " + cnfe.getMessage()); break;
      }
/*      finally {
        try {
          if (pw != null) pw.close();
          if (br != null) br.close();
          if (s != null) s.close();
        } catch (IOException ioe) {}
      }*/
    }
  }
}
