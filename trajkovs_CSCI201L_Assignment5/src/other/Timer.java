package other;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import GUI.GameBoardUI;

public class Timer extends Thread {
	private Integer timer, imageIdx, teamID;
	private Integer sleepTime;
	private GameBoardUI gameBoard;
	private JLabel editLbl;
	private ArrayList<JLabel> waitLbl;
	private ArrayList<ImageIcon> images;
	private Boolean inAnswerPane;
	private Boolean stopTimer;
	
	public Timer(JLabel editLbl, ArrayList<JLabel> waitLbl, GameBoardUI gameBoard) {
		this.editLbl = editLbl;
		this.waitLbl = waitLbl;
		timer = 15;
		imageIdx = 0;
		sleepTime = 1000;
		inAnswerPane = false;
		stopTimer = false;
		
		this.gameBoard = gameBoard;
		loadImages();
//		new Thread(this);
	}
	
	public void start(int teamID) {
		this.teamID = teamID;
		this.timer = 15;
		this.imageIdx = 0;
//		if (!isAlive())
		start();
	}
	
	private void loadImages() {
		File folder = new File("resources/clockAnimation/");
		images = new ArrayList<ImageIcon>();
		for (File file : folder.listFiles()) {
			images.add(new ImageIcon(file.getAbsolutePath()));
		}
	}
	
	public void restart(int teamID) {
		this.teamID = teamID;
		this.timer = 15;
		this.imageIdx = 0;
		run();
	}
	
	public void stopTimer() {
		timer = -1;
		stopTimer = true;
		waitLbl.get(teamID).setIcon(null);
		
	}
	
	public void setupAnswerPane(JLabel userLbl) {
		this.inAnswerPane = true;
		this.editLbl = userLbl;
		this.timer = 20;
		this.imageIdx = 0;
		run();
	}
	
	@Override
	public void run() {
		while (timer >= 0) {
//			if (inAnswerPane)
//				editLbl.setText("" + timer--);
//			else
			
			try {
				if (stopTimer) break;
				synchronized (this) {
					if (inAnswerPane)
						editLbl.setText("" + timer--);
						editLbl.setText("Jeopardy: " + timer--);
					waitLbl.get(teamID).setIcon(images.get(imageIdx++));
				}
				Thread.yield();
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
//		while (timer >= 0) {
//			if (inAnswerPane)
//				editLbl.setText("" + timer--);
//			else
//				editLbl.setText("Jeopardy: " + timer--);
//			waitLbl.get(teamID).setIcon(images.get(imageIdx++));
//			try {
//				Thread.sleep(sleepTime);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		waitLbl.get(teamID).setIcon(null);
		gameBoard.timeExpired();
	}

}
