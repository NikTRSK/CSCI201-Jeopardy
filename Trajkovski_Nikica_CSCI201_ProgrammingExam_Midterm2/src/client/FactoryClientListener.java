package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import resource.Factory;
import utilities.Util;

public class FactoryClientListener extends Thread {

	private Socket mSocket;
	private ObjectInputStream ois;
	private PrintWriter pw;
	private FactoryManager mFManager;
	private FactoryClientGUI mFClientGUI;
	//
	private FactorySimulation factorySim;
	
	public FactoryClientListener(FactoryManager inFManager, FactoryClientGUI inFClientGUI, Socket inSocket) {
		mSocket = inSocket;
		mFManager = inFManager;
		mFClientGUI = inFClientGUI;
		boolean socketReady = initializeVariables();
		if (socketReady) {
			start();
		}
//		this.factorySim = inFManager.mFactorySimulation;
		
	}

	public void setClientListener () {
	  mFManager.mFactorySimulation.setListener(this);
	}
	
	private boolean initializeVariables() {
		try {
			ois = new ObjectInputStream(mSocket.getInputStream());
			pw = new PrintWriter(mSocket.getOutputStream());
		} catch (IOException ioe) {
			Util.printExceptionToCommand(ioe);
			Util.printMessageToCommand(Constants.unableToGetStreams);
			return false;
		}
		return true;
	}
	
	public void sendMessage(String msg) {
		pw.println(msg);
		pw.flush();
	}
	
	public void run() {
		try {
			mFClientGUI.addMessage(Constants.waitingForFactoryConfigMessage);
			Factory factory;
			while(true) {
				// in case the server sends another factory to us
				factory = (Factory)ois.readObject();
				mFManager.loadFactory(factory, mFClientGUI.getTable());
				mFClientGUI.addMessage(Constants.factoryReceived);
				mFClientGUI.addMessage(factory.toString());
//				if (this.factorySim != null) {
				  if (mFManager.mFactorySimulation.getTaskBoard().isDone()) {
				    System.out.println("FACTORY DONE ----");
				    FactoryNode cs = this.factorySim.getNode("Coffee Shop");
				    System.out.println( ((CoffeeShop)cs.getObject()).getAllOrder().size() );
				    for ( String order : ((CoffeeShop)cs.getObject()).getAllOrder() )
				      sendMessage(mSocket.getLocalPort() + order);
//				    
//				  }
				 }
			}
		} catch (IOException ioe) {
			mFClientGUI.addMessage(Constants.serverCommunicationFailed);
		} catch (ClassNotFoundException cnfe) {
			Util.printExceptionToCommand(cnfe);
		}
	}
}
