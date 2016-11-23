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
	private JLabel buzzInWait;
	private ArrayList<ImageIcon> images;
	private Boolean inAnswerPane, inBuzzInTime, inQuestionListPane, inFJ;
	private Boolean stopTimer;
	
	public Timer(JLabel editLbl, ArrayList<JLabel> waitLbl, GameBoardUI gameBoard) {
		this.editLbl = editLbl;
		this.waitLbl = waitLbl;
		timer = 15;
		imageIdx = 0;
		sleepTime = 1000;
		stopTimer = false;
		// track panel
		this.inQuestionListPane = true;
		this.inAnswerPane = false;
		this.inBuzzInTime = false;
		// track panel end
		
		this.gameBoard = gameBoard;
		loadImages();
//		new Thread(this);
	}
	
	public void start(int teamID) {
		this.stopTimer = false;
//		this.inBuzzInTime = false;
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
		// restart timer only if it's already running
		if (!stopTimer) {
			this.teamID = teamID;
			this.timer = 15;
			this.imageIdx = 0;
			run();
		}
	}
	
	public void stopTimer() {
		timer = -1;
		stopTimer = true;
		waitLbl.get(teamID).setIcon(null);
		
	}
	
	public boolean stopped() {
		return this.stopTimer;
	}
	
	public void setupQuestionListPane(JLabel editLbl, ArrayList<JLabel> waitLbl) {
		System.out.println("Setting up questionlist timer");
		this.editLbl = editLbl;
		this.waitLbl = waitLbl;
		timer = 15;
		imageIdx = 0;
		sleepTime = 1000;
		stopTimer = false;
		// track panel
		this.inQuestionListPane = true;
		this.inAnswerPane = false;
		this.inBuzzInTime = false;
		// track panel end
	}
	
	public void setupAnswerPane(JLabel userLbl) {
		System.out.println("Setting up answerpane timer");
		// track panel
		this.inQuestionListPane = false;
		this.inAnswerPane = true;
		this.inBuzzInTime = false;
		// track panel end
		this.stopTimer = false;
		this.editLbl = userLbl;
		this.timer = 20;
		this.imageIdx = 0;
//		run();
	}
	
	public void setupBuzzInTimer(JLabel buzzInLabel) {
		System.out.println("Setting up buzzin timer");
		this.editLbl.setText("\n");
		// track panel
		this.inQuestionListPane = false;
		this.inAnswerPane = false;
		this.inBuzzInTime = true;
		// track panel end
		this.stopTimer = false;
		this.buzzInWait = buzzInLabel;
		this.timer = 20;
		this.imageIdx = 0;
		waitLbl.get(teamID).setIcon(null);
		run();
	}
	
	public boolean inAnswerPane() {
		return this.inAnswerPane;
	}
	
	public boolean inQuestionListPane() {
		return this.inQuestionListPane;
	}
	
	public boolean inBuzzInTime() {
		return this.inBuzzInTime;
	}
	
	@Override
	public void run() {
		while (timer >= 0) {
//			System.out.println("Timer in while: " + timer);
//			if (inAnswerPane)
//				editLbl.setText("" + timer--);
//			else
			
			try {
				if (stopTimer) break;
//				synchronized (this) {
					if (inAnswerPane || inBuzzInTime)
						editLbl.setText("" + timer--);
					else
						editLbl.setText("Jeopardy: " + timer--);
					if (inBuzzInTime)
						buzzInWait.setIcon(images.get(imageIdx++));
					else
						waitLbl.get(teamID).setIcon(images.get(imageIdx++));
//				}
				Thread.yield();
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waitLbl.get(teamID).setIcon(null);
		gameBoard.timeExpired();
	}

}
