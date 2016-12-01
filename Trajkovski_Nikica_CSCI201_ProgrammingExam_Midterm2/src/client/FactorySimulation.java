package client;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;

import resource.Factory;
import resource.Resource;

public class FactorySimulation {
	
	Factory mFactory;
	private boolean isDone = false;
	
	private ArrayList<FactoryObject> mFObjects;
	private ArrayList<FactoryWorker> mFWorkers;
	private FactoryNode mFNodes[][];
	protected Map<String, FactoryNode> mFNodeMap;
	private FactoryTaskBoard mTaskBoard;
	
	FactoryClientListener factoryClientListener;
	
	//instance constructor
	{
		mFObjects = new ArrayList<FactoryObject>();
		mFWorkers = new ArrayList<FactoryWorker>();
		mFNodeMap = new HashMap<String, FactoryNode>();
	}
	
	FactorySimulation(Factory inFactory, JTable inTable) {
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
		  FactoryResource fr = FactoryResourceFactory.get().makeFactoryResource(resource);
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
		
		/*//Create the walls
		
		FactoryWall factoryWall = new FactoryWall(new Rectangle(7,8,1,1));
		mFObjects.add(factoryWall);
		mFNodes[factoryWall.getX()][factoryWall.getY()].setObject(factoryWall);
		*/
		
		//Make lotsa walls
		for(int i = 0; i < 10; i++)
		{
			FactoryObject fw = new FactoryWall(new Rectangle(7,i,1,1));
			mFObjects.add(fw);
			mFNodes[fw.getX()][fw.getY()].setObject(fw);			
		}
		for(int i = 0; i < 6; i++)
		{
			FactoryObject fw = new FactoryWall2(new Rectangle(i,9,1,1));
			mFObjects.add(fw);
			mFNodes[fw.getX()][fw.getY()].setObject(fw);	
		}
	}

	public boolean isDone() {
	  return isDone;
	}
	
	public void update(double deltaTime) {
		//Update all the objects in the factor that need updating each tick
		if(isDone)
		{
			return;
		}
		for(FactoryObject object : mFWorkers) object.update(deltaTime);
		
		if(mTaskBoard.isDone())
		{
			isDone = true;
			for(FactoryObject object : mFObjects)
			{
				if(object instanceof FactoryReporter) {
					((FactoryReporter)object).report();
				}
			}
			
//      if (this.factorySim != null) {
//        if (this.factorySim.isDone()) {
          System.out.println("FACTORY DONE");
          FactoryNode cs = getNode("Coffee Shop");
//          for ( String order : ((CoffeeShop)cs.getObject()).getAllOrder() ) {
//            sendMessage(mSocket.getLocalPort() + order);
            
//            factoryClientListener.sendMessage(order);
//          }
          
//        }
//       }
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
	
	public void setListener(FactoryClientListener fcl) {
	  factoryClientListener = fcl;
	}
	
}
