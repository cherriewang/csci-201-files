package client;

import java.sql.Timestamp;

import libraries.ImageLibrary;
import resource.Product;

public class FactoryShipper extends FactoryWorker{
	
	Product finished;
	
	private Timestamp finishTime;
	
	FactoryShipper(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(inNumber, startNode, inFactorySimulation);
		mLabel = "Shipper "+inNumber;
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_empty" + Constants.png);
	}
	
	public void run(){
		mLock.lock();
		try {
			while(true) {
				if(finished == null) {
					mDestinationNode = mFactorySimulation.getNode("ProductBin");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					while(!mDestinationNode.aquireNode())Thread.sleep(1);
					finished = mFactorySimulation.getProductBin().removeProduct();
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_box" + Constants.png);
					Thread.sleep(1000);
					mDestinationNode.releaseNode();
				} else {
					mDestinationNode = mFactorySimulation.getNode("MailBox");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryMailbox fmb = (FactoryMailbox)mDestinationNode.getObject();
					fmb.addProduct(finished);
					/*
					FactoryResource toGive = (FactoryResource)mDestinationNode.getObject();
					toGive.giveResource(mProductToStock.getQuantity());
					*/
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_empty" + Constants.png);
					
					
					// go to the taskboard
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					
					FactoryTaskBoard ftb = (FactoryTaskBoard)mDestinationNode.getObject();
					ftb.productShipped(finished);
					finishTime = new Timestamp(System.currentTimeMillis());
					finished = null;

				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
		
	}
}
