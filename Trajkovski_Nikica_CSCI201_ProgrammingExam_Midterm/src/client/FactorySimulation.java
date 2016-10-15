package client;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import resource.Factory;
import resource.Resource;

public class FactorySimulation {
	
	Factory mFactory;
	private boolean isDone = false;
	
	private ArrayList<FactoryObject> mFObjects;
	private ArrayList<FactoryWorker> mFWorkers;
	private FactoryNode mFNodes[][];
	private Map<String, FactoryNode> mFNodeMap;
	private FactoryTaskBoard mTaskBoard;

	//Code for timestamp filename making
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	String outFileName = "reports/" + timestamp;
	
	//Code for final time measurement
	private double totalTime = 0.0;
	
	
	//instance constructor
	{
		mFObjects = new ArrayList<FactoryObject>();
		mFWorkers = new ArrayList<FactoryWorker>();
		mFNodeMap = new HashMap<String, FactoryNode>();
	}
	
	FactorySimulation(Factory inFactory, JTable inTable) {
		outFileName = outFileName.replaceAll("[-:. ]", "_");
		
		mFactory = inFactory;
		mFNodes = new FactoryNode[mFactory.getWidth()][mFactory.getHeight()];
		
		//Create the nodes of the factory
		for(int height = 0; height < mFactory.getHeight(); height++) {
			for(int width = 0; width < mFactory.getWidth(); width++) {
				mFNodes[width][height] = new FactoryNode(width,height);
				mFObjects.add(mFNodes[width][height]);
			}
		}
		
		//Link all of the nodes together
		for(FactoryNode[] nodes: mFNodes) {
			for(FactoryNode node : nodes) {
				int x = node.getX();
				int y = node.getY();
				if(x != 0) node.addNeighbor(mFNodes[x-1][y]);
				if(x != mFactory.getWidth()-1) node.addNeighbor(mFNodes[x+1][y]);
				if(y != 0) node.addNeighbor(mFNodes[x][y-1]);
				if(y != mFactory.getHeight()-1) node.addNeighbor(mFNodes[x][y+1]);
			}
		}
		
		//Create the resources
		for(Resource resource : mFactory.getResources()) {
			FactoryResource fr = new FactoryResource(resource);
			mFObjects.add(fr);
			mFNodes[fr.getX()][fr.getY()].setObject(fr);
			mFNodeMap.put(fr.getName(), mFNodes[fr.getX()][fr.getY()]);
		}
		
		//Create the task board
		Point taskBoardLocation = mFactory.getTaskBoardLocation();
		mTaskBoard = new FactoryTaskBoard(inTable,inFactory.getProducts(),taskBoardLocation.x,taskBoardLocation.y);
		mFObjects.add(mTaskBoard);
		mFNodes[taskBoardLocation.x][taskBoardLocation.y].setObject(mTaskBoard);
		mFNodeMap.put("Task Board", mFNodes[taskBoardLocation.x][taskBoardLocation.y]);
		
		//Create the workers, set their first task to look at the task board
		for(int i = 0; i < mFactory.getNumberOfWorkers(); i++) {
			//Start each worker at the first node (upper left corner)
			//Note, may change this, but all factories have an upper left corner(0,0) so it makes sense
			FactoryWorker fw = new FactoryWorker(i, mFNodes[0][0], this);
			mFObjects.add(fw);
			mFWorkers.add(fw);
		}
		//File scanner
				Scanner reader = null;
				try{
					reader = new Scanner(new File("walls"));
					while(reader.hasNext()) { //Remember, the format for reading this file is <Xcoord> <Ycoord> <URL>
						int x = reader.nextInt();
						int y = reader.nextInt();
						String file = reader.next();
						FactoryWall fw = new FactoryWall(new Rectangle(x,y,1,1), file);
						mFObjects.add(fw);
						mFNodes[fw.getX()][fw.getY()].setObject(fw);
					}
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				} finally {
					if(reader != null) {
						reader.close();
					}
				}
		/*//Create the walls
		
		FactoryWall factoryWall = new FactoryWall(new Rectangle(7,8,1,1));
		mFObjects.add(factoryWall);
		mFNodes[factoryWall.getX()][factoryWall.getY()].setObject(factoryWall);
		*/
		
		//Make lotsa walls
		/*for(int i = 0; i < 10; i++)
		{
			FactoryObject fw = new FactoryWall(new Rectangle(7,i,1,1));
			mFObjects.add(fw);
			mFNodes[fw.getX()][fw.getY()].setObject(fw);			
		}*/
		for(int i = 0; i < 6; i++)
		{
			FactoryObject fw = new FactoryWall2(new Rectangle(i,9,1,1));
			mFObjects.add(fw);
			mFNodes[fw.getX()][fw.getY()].setObject(fw);	
		}
		
		new FactoryHierarchy(inFactory);
	}
	
	public void update(double deltaTime) {
		//Update all the objects in the factor that need updating each tick
		if(isDone)
		{
			return;
		}
		for(FactoryObject object : mFWorkers) object.update(deltaTime);
		
		//increment the total time here
		totalTime += deltaTime;
		
		if(mTaskBoard.isDone())
		{
			isDone = true;
			//saveResourceFile();
			//saveCompletedItemsFile();
			//Create popup menu here
			DecimalFormat threePlaces = new DecimalFormat(".###");
			JOptionPane.showMessageDialog(null,
										"Total Time: " + threePlaces.format(totalTime) + "s",
										"Simulation Over!",
										JOptionPane.INFORMATION_MESSAGE);
			//Do file save prompt here
			FileSaverPrompt fsp = new FileSaverPrompt();
			fsp.setVisible(true);
		}
	}
	
	public ArrayList<FactoryObject> getObjects() {
		return mFObjects;
	}
	
	public ArrayList<FactoryWorker> getWorkers() {
		return mFWorkers;
	}
	
	public FactoryNode[][] getNodes() {
		return mFNodes;
	}

	public String getName() {
		return mFactory.getName();
	}

	public double getWidth() {
		return mFactory.getWidth();
	}
	
	public double getHeight() {
		return mFactory.getHeight();
	}

	public FactoryNode getNode(String key) {
		return mFNodeMap.get(key);
	}

	public FactoryTaskBoard getTaskBoard() {
		return mTaskBoard;
	}
	
	//New method to save the time stamp resource file
	private void saveResourceFile()
	{	
		File file = null;
		FileWriter fw = null;
		//File complete = null;//New file for the completed list
		//FileWriter fw2 = null;//Can't use the same filewriter to do both the end task and the complted products.
		try{
			file = new File(outFileName);
			file.createNewFile();
			fw = new FileWriter(outFileName,true);
			for(FactoryObject object : mFObjects)
			{
				if(object instanceof FactoryReporter) {
					((FactoryReporter)object).report(fw);
				}
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			if(file != null) {
				file.delete();
			}
		} finally {
			if(fw != null) {
				try{
					fw.close();
				} catch (IOException e) {
					System.out.println("Failed to close the filewriter!");
				}
			}
		}
	}
	
	//New method to save the Completed Items file
	private void saveCompletedItemsFile()
	{
		File complete = null;//New file for the completed list
		FileWriter fw2 = null;//Can't use the same filewriter to do both the end task and the complted products.
		try{
			String completeFileName = "reports/" + "CompletedItems.txt";
			complete = new File(completeFileName); //Create a new text file called CompletedItems.txt
			complete.createNewFile(); // Make the file
			fw2 = new FileWriter(completeFileName, true); //Initialize the second filewriter
			
			ArrayList<String> toWrite = mTaskBoard.getCompletedProducts(); //get the strings to write
			for(int i = 0; i < toWrite.size(); i++) //For all the elements in toWrite
			{
				fw2.write(toWrite.get(i)+"\n"); //Write it into the file
				//fw2.write("\n");
			}
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			if(complete != null) {
				complete.delete();
			}
		} finally {
			if(fw2 != null) {
				try{
					fw2.close();
				} catch (IOException e) {
					System.out.println("Failed to close the filewriter2!");
				}
			}
		}
	}
	
	
	//New inner class for Lab 4 expansion
	class FileSaverPrompt extends JFrame{
		private static final long serialVersionUID = 43912804982148L;
		
		JPanel buttonBox;
		
		
		public FileSaverPrompt()
		{
			super("Save The Files?"); //Super call for JFrame
			setSize(320,240); //Set the size of the frame
			JButton resources = new JButton("Resource Only"); // New button for saving resources
			JButton completed = new JButton("Items Only"); //New button for savving the items
			JButton both = new JButton("Both"); //Both please with a side of fries
			
			JPanel textPanel = new JPanel();//New JPanel to save the text lines
			JLabel text = new JLabel("Would you like to save the Resources used,"); //text 1
			JLabel text2 = new JLabel("the completed items list, or both?"); //text 2
			textPanel.setBorder(new BevelBorder(1)); //Make it look nice
			textPanel.add(text); //add the first line
			textPanel.add(text2); //add the second line
			
			add(textPanel,BorderLayout.CENTER); //add the entire text panel to the center
			
			buttonBox = new JPanel(); //Make another JPanel for the buttonbox
			
			buttonBox.setLayout(new GridBagLayout()); //The layout will be gridbaglayout
			GridBagConstraints gbc = new GridBagConstraints(); //Make some new gbc constraints
			gbc.fill = GridBagConstraints.HORIZONTAL; //"Yo, these buttons are gonna be standing next each other"
			gbc.weightx = 1; //Extrude the buttons so they will fill the entire box horizontally, not vertically
			gbc.ipadx = 1; //Pad it a bit
			gbc.gridx = 1; //Position is on left
			buttonBox.add(resources,gbc); //add it using the gbc
			
			gbc.gridx = 2; //position is middle
			buttonBox.add(completed,gbc); //add it
			
			gbc.gridx = 3; //position is middle
			buttonBox.add(both ,gbc); //add it
			
			//Put some actionListeners for the buttons
			resources.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					saveResourceFile();
					setVisible(false); //Close the window
				}
			});
			
			completed.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					saveCompletedItemsFile();
					setVisible(false); //Close the window
				}
			});
			
			both.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					saveResourceFile();
					saveCompletedItemsFile();
					setVisible(false); //Close the window
				}
			});
			
			add(buttonBox,BorderLayout.SOUTH); //add the button box
			setVisible(true); //Is this frame visible at the start? Nope
			setLocationRelativeTo(null); //sets the location of the JFrame with respect to another Component, passing null to this centers the JFrame on the screen.
		}
	}
}
