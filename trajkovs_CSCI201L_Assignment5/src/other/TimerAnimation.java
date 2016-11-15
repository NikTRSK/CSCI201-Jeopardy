package other;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class TimerAnimation extends Thread {
	private Integer timer;
	private Integer sleepTime;
	private Integer teamID;
	private ArrayList<JLabel> editLbl;
//	private ArrayList<String> imageList;
	
	private ArrayList<ImageIcon> images;
	
	public TimerAnimation(ArrayList<JLabel> editLbl) {
		this.editLbl = editLbl;
		loadImages();
		sleepTime = (15*1000/images.size());
		System.out.println("sleepTime: " + sleepTime + " size: " + images.size());
	}
	
	public void restart() {
		timer = 15;
	}
	
	public void start(int teamID) {
		this.teamID = teamID;
	}
	
	private void loadImages() {
		File folder = new File("resources/clockAnimation/");
//		imageList = new String[folder.listFiles().length];
		images = new ArrayList<ImageIcon>();
		for (File file : folder.listFiles()) {
			images.add(new ImageIcon(file.getAbsolutePath()));
		}
	}
	
	@Override
	public void run() {
		for (ImageIcon image : images) {
			editLbl.get(teamID).setIcon(image);
			try {
				Thread.sleep(1000);
//				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		while (timer >= 0) {
//			editLbl.setIcon();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
}
