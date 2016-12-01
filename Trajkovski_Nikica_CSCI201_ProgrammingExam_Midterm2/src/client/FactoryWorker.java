package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import resource.Product;
import resource.Resource;
import libraries.ImageLibrary;

public class FactoryWorker extends FactoryObject implements Runnable, FactoryReporter{
	
	private int mNumber;
	
	private FactorySimulation mFactorySimulation;
	private Product mProductToMake;
	
	private Lock mLock;
	private Condition atLocation;
	
	private Timestamp finished;
	
	//Nodes each worker keeps track of for path finding
	private FactoryNode mCurrentNode;
	private FactoryNode mNextNode;
	private FactoryNode mDestinationNode;
	private Stack<FactoryNode> mShortestPath;
	
	//instance constructor
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "worker" + Constants.png);
		mLock = new ReentrantLock();
		atLocation = mLock.newCondition();
	}
	
	FactoryWorker(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(new Rectangle(startNode.getX(), startNode.getY(), 1, 1));
		mNumber = inNumber;
		mCurrentNode = startNode;
		mFactorySimulation = inFactorySimulation;
		mLabel = Constants.workerString + String.valueOf(mNumber);
		new Thread(this).start();
	}
	
	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
	}
	
	@Override
	public void update(double deltaTime) {
		if(!mLock.tryLock()) return;
		//if we have somewhere to go, go there
		if(mDestinationNode != null) {
			if(moveTowards(mNextNode,deltaTime * Constants.workerSpeed)) {
				//if we arrived, save our current node
				mCurrentNode = mNextNode;
				if(!mShortestPath.isEmpty()) {
					//if we have somewhere else to go, save that location
					mNextNode = mShortestPath.pop();
				}//if we arrived at the location, signal the worker thread so they can do more actions
				if(mCurrentNode == mDestinationNode) atLocation.signal();
			}
		}
		mLock.unlock();
	}
	
	//Use a separate thread for expensive operations
	//Path finding
	//Making objects
	//Waiting
	@Override
	public void run() {
		mLock.lock();
		try {
			//Thread.sleep(1000*mNumber); //used to space out the factory workers
			while(true) {
				if(mProductToMake == null) {
					//get an assignment from the table
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					while(!mDestinationNode.aquireNode())Thread.sleep(1);
					mProductToMake = mFactorySimulation.getTaskBoard().getTask();
					Thread.sleep(1000);
					mDestinationNode.releaseNode();
					if(mProductToMake == null) break; //No more tasks, end here
				}
				
				// go to coffee shop
        mDestinationNode = mFactorySimulation.getNode("Coffee Shop");
        mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
        mNextNode = mShortestPath.pop();
        atLocation.await();
        ((CoffeeShop)mDestinationNode.getObject()).addOrder(" - " + this.mLabel + " ordered coffee at " + ZonedDateTime.now());
        Thread.sleep(500);
				
				
				// go to dining room
        DiningRoom dm;
        mDestinationNode = mFactorySimulation.getNode("Dining Room");
        dm = ((DiningRoom)mDestinationNode.getObject());
        mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
        mNextNode = mShortestPath.pop();
        atLocation.await();
        if (((DiningRoom)mDestinationNode.getObject()).takeOne()) {
          Thread.sleep(5000);
          ((DiningRoom)mDestinationNode.getObject()).leaveOne();
        }
        else {
          mDestinationNode = mFactorySimulation.getNode("Coffee Shop");
          mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
          mNextNode = mShortestPath.pop();
          atLocation.await();
          while (!dm.available)
            Thread.yield();
          mDestinationNode = mFactorySimulation.getNode("Dining Room");
          mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
          mNextNode = mShortestPath.pop();
          atLocation.await();
//          if (((DiningRoom)mDestinationNode.getObject()).takeOne()) {
//            Thread.sleep(5000);
            ((DiningRoom)mDestinationNode.getObject()).takeOne();
            Thread.sleep(5000);
//          ((CoffeeShop)mDestinationNode.getObject()).addOrder(" - " + this.mLabel + " ordered coffee at " + ZonedDateTime.now());
//          Thread.sleep(500);
//          if
        }

        
				//build the product
				for(Resource resource : mProductToMake.getResourcesNeeded()) {
					mDestinationNode = mFactorySimulation.getNode(resource.getName());
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryResource toTake = (FactoryResource)mDestinationNode.getObject();
					toTake.takeResource(resource.getQuantity());
				}
				//update table
				{
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					finished = new Timestamp(System.currentTimeMillis());
					mFactorySimulation.getTaskBoard().endTask(mProductToMake);
					mProductToMake = null;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}

	@Override
	public void report() {
		// TODO Auto-generated method stub
		System.out.println(mNumber + " finished at " + finished);
	}

}
