package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import resource.Product;
import resource.Resource;
import libraries.ImageLibrary;

public class FactoryWorker extends FactoryObject implements Runnable, FactoryReporter{

	protected int mNumber;
	
	protected FactorySimulation mFactorySimulation;
	private Product mProductToMake;
	
	protected Thread mThread;
	
	protected Lock mLock;
	protected Condition atLocation;
	
	//Nodes each worker keeps track of for path finding
	protected FactoryNode mCurrentNode;
	protected FactoryNode mNextNode;
	protected FactoryNode mDestinationNode;
	protected Stack<FactoryNode> mShortestPath;
	
	private Timestamp finished;
	
	//instance constructor
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "worker" + Constants.png);
		mLock = new ReentrantLock();
		atLocation = mLock.newCondition();
	}
	
	FactoryWorker(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(new Rectangle(startNode.getX(), startNode.getY(),1,1));
		mNumber = inNumber;
		mCurrentNode = startNode;
		mFactorySimulation = inFactorySimulation;
		mLabel = Constants.workerString + String.valueOf(mNumber);
		mThread = new Thread(this);
		new Thread(this).start();
	}
	
	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
	}
	

	public Thread getThread(){
		return mThread;
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
					mCurrentNode.unMark();
				}//if we arrived at the location, signal the worker thread so they can do more actions
				if(mCurrentNode == mDestinationNode) {
					atLocation.signal();
				}
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
					
				// goes to the semaphore thing
				mDestinationNode = mFactorySimulation.getNode("Resourceroom");
				mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
				mNextNode = mShortestPath.pop();
				atLocation.await();
				ResourceRoomDoor door2 = (ResourceRoomDoor)mDestinationNode.getObject();
				door2.accessRoom();		
				
				// lab 13 robots
				ArrayList<FactoryRobot> robots = new ArrayList<FactoryRobot>();
				ArrayList<Thread> robotThreads = new ArrayList<Thread>();
				FactoryRobotBin robotBin;
				// go to robot bin
				{	
					mDestinationNode = mFactorySimulation.getNode("RobitBin");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					robotBin = (FactoryRobotBin)(mDestinationNode.getObject());
				}
				
				
				//build the product
				for(Resource resource : mProductToMake.getResourcesNeeded()) {
//					FactoryRobot robot = robotBin.getRobot();
//					if(robot != null){
//						robots.add(robot);
//						robot.getResource(resource, mFactorySimulation.getNode("Resourceroom"));
//						robotThreads.add(robot.getThread());
//					} else{
//						// work at the same time as your robots
//						mDestinationNode = mFactorySimulation.getNode(resource.getName());
//						mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
//						mNextNode = mShortestPath.pop();
//						atLocation.await();
//						if(!mProductToMake.getResourcesNeeded().lastElement().equals(resource)){
//							mDestinationNode = mFactorySimulation.getNode("RobotBin");
//							mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
//							mNextNode = mShortestPath.pop();
//							atLocation.await();
//						}
//					}
					
					mDestinationNode = mFactorySimulation.getNode(resource.getName());
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryResource toTake = (FactoryResource)mDestinationNode.getObject();
					toTake.takeResource(resource.getQuantity());	
					
				}
				// opens up a work permit
				mDestinationNode = mFactorySimulation.getNode("Resourceroom");
				mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
				mNextNode = mShortestPath.pop();
				atLocation.await();
//				for(FactoryRobot fr : robots){
//					fr.sendBack();
//				}
//				for(Thread t : robotThreads){
//					t.join();
//				}
				door2.leaveRoom();
				
				
				//update table
				{
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					finished = new Timestamp(System.currentTimeMillis());
					mFactorySimulation.getTaskBoard().endTask(mProductToMake);
					
				}
				// assemble product
				{
					// naviaate to the work room door
					mDestinationNode = mFactorySimulation.getNode("Workroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					
					// get an available workbench, and navigate to it
					FactoryWorkroomDoor door = (FactoryWorkroomDoor)mDestinationNode.getObject();
					FactoryWorkbench workbench = door.getWorkbench();
					mDestinationNode = mFactorySimulation.getNodes()[workbench.getX()][workbench.getY()];
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					// create the product
					workbench.assemble(mProductToMake);
					// navigate back to door to exit
					mDestinationNode = mFactorySimulation.getNode("Workroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					// give up a permit since we are exiting
					door.returnWorkbench(workbench);
					
					// not sure if this is where it goes
//					FactoryNode workbenchNode = mFactorySimulation.getNode("Workbench");
//					mShortestPath = mCurrentNode.findShortestPath(workbenchNode);
//					mShortestPath.remove(workbenchNode);
//					mDestinationNode=mShortestPath.firstElement();
//					mNextNode = mShortestPath.pop();
//					atLocation.await();
//					FactoryWorkbench workbench = (FactoryWorkbench)workbenchNode.getObject();
//					workbench.lock();
//					mDestinationNode = workbenchNode;
//					mNextNode = workbenchNode;
//					atLocation.await();
//					Thread.sleep(1000); //working
//					//workbench.assemble(mProductToMake);
//					workbench.unlock();
	
				}
				
				// washroom pitstop
				{
					// not sure if this is where it goes
					FactoryNode washroomNode = mFactorySimulation.getNode("Washroom");
					mShortestPath = mCurrentNode.findShortestPath(washroomNode);
					mShortestPath.remove(washroomNode);
					mDestinationNode=mShortestPath.firstElement();
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryWashroom washroom = (FactoryWashroom)washroomNode.getObject();
					washroom.lock();
					mDestinationNode = washroomNode;
					mNextNode = washroomNode;
					atLocation.await();
					Thread.sleep(1000); //working
					//washroom.wash(mProductToMake);
					washroom.unlock();
	
				}
				
				
				
				// bring finished to product bin
				{
					mDestinationNode = mFactorySimulation.getNode("ProductBin");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					

					FactoryProductBin fpb = (FactoryProductBin)mDestinationNode.getObject();
					fpb.addProduct(mProductToMake);
					System.out.println("worker added something to the product bin");
					mProductToMake = null;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}

	@Override
	public void report(FileWriter fw) throws IOException {
		fw.write(mNumber +" finished at "+ finished +'\n');
	}

}
