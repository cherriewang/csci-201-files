package client;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import resource.Factory;
import resource.Resource;

public class FactorySimulation {
	
	Factory mFactory;
	
	private ArrayList<FactoryObject> mFObjects;
	private ArrayList<FactoryWorker> mFWorkers;
	private FactoryNode mFNodes[][];
	private Map<String, FactoryNode> mFNodeMap;
	private FactoryTaskBoard mTaskBoard;
	private FactoryMailbox mMailBox;
	private FactoryProductBin mProductBin;
	
	private FactoryClientListener fcl;
	
	private boolean isDone = false;
	
	private double totalTime = 0.0;
	
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
		
		//Create a wall
		Scanner reader = null;
		try {
			reader = new Scanner(new File("walls"));
			while(reader.hasNext()) {
				int x = reader.nextInt();
				int y = reader.nextInt();
				String file = reader.next();
				FactoryWall fw = new FactoryWall(new Rectangle(x,y,1,1),file);
				mFObjects.add(fw);
				mFNodes[fw.getX()][fw.getY()].setObject(fw);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			if(reader != null) reader.close();
		}
		
		// create green walls
		for(int i = 0; i < 3; i++){
			FactoryObject fw = new FactoryWall2(new Rectangle(i,9,1,1));
			mFObjects.add(fw);
			mFNodes[fw.getX()][fw.getY()].setObject(fw);
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
		
		//create the mailbox
		mMailBox = new FactoryMailbox(); //Mailbox that can stock the factory resources
		mFObjects.add(mMailBox); //Add this object to be rendered
		mFNodes[0][0].setObject(mMailBox); //Link this object to node 0,0
		mFNodeMap.put("MailBox", mFNodes[0][0]); //Make it easy to find the mailbox node
		
		// product bin
		mProductBin = new FactoryProductBin();
		mFObjects.add(mProductBin);
		mFNodes[0][3].setObject(mProductBin);
		mFNodeMap.put("ProductBin", mFNodes[0][3]);
		
		//create some stockpersons
		for(int i = 0; i < 3; ++i) {
			FactoryShipper sp = new FactoryShipper(i,mFNodes[0][0],this);
			mFObjects.add(sp);
			mFWorkers.add(sp);
		}
		
		// adding a workbench
//		FactoryWorkbench fw = new FactoryWorkbench(new Rectangle(0,mFactory.getHeight()-1,1,1));
//		mFNodes[0][mFactory.getHeight()-1].setObject(fw);
//		mFNodeMap.put("Workbench",mFNodes[0][mFactory.getHeight()-1]);
//		mFObjects.add(fw);
		
		
		// clump of workbenches
		Vector<FactoryWorkbench> workbenches = new Vector<FactoryWorkbench>();
		for(int i = 11; i <13; ++i) {
			for(int j = 11; j <13; ++j){
				FactoryWorkbench wb = new FactoryWorkbench(new Rectangle(i,j,1,1));
				mFNodes[i][j].setObject(wb);
				mFObjects.add(wb);
				workbenches.add(wb);
			}
		}
		
		// adding a door
		FactoryWorkroomDoor door = new FactoryWorkroomDoor(new Rectangle(9,12,1,1), workbenches);
		mFNodes[9][12].setObject(door);
		mFNodeMap.put("Workroom", mFNodes[9][12]);
		mFObjects.add(door);
		
		ResourceRoomDoor door2 = new ResourceRoomDoor(new Rectangle(5,8,1,1));
		mFNodes[5][8].setObject(door2);
		mFNodeMap.put("Resourceroom", mFNodes[5][8]);
		mFObjects.add(door2);
			
		// adding a washroom
		FactoryWashroom wr = new FactoryWashroom(new Rectangle(6,14,1,1));
		mFNodes[6][14].setObject(wr);
		mFNodeMap.put("Washroom",mFNodes[6][14]);
		mFObjects.add(wr);
		
		// make factory robot bin
		FactoryRobotBin robotBin = new FactoryRobotBin(new Rectangle(3,9,1,1));
		mFNodes[3][9].setObject(robotBin);
		mFNodeMap.put("RobotBin", mFNodes[3][9]);
		mFObjects.add(robotBin);
		// add robots
		for(int i = 0; i < 20; i++){
			FactoryRobot fr = new FactoryRobot(i, this.getNode("RobotBin"),this);
			mFObjects.add(fr);
			mFWorkers.add(fr);
		}
		for(FactoryWorker fw : mFWorkers) fw.getThread().start();
		
	}
	
	public FactoryProductBin getProductBin(){
		return mProductBin;
	}
	
	public void update(double deltaTime) {
		if(isDone) return;
		
		totalTime += deltaTime;
		
		//Update all the objects in the factor that need updating each tick
		for(FactoryObject object : mFWorkers) object.update(deltaTime);
		if(mTaskBoard.isDone()) {
			isDone = true;
			
			DecimalFormat threePlaces = new DecimalFormat(".###");
			
			JOptionPane.showMessageDialog(null,
									"Total time: "+threePlaces.format(totalTime)+"s",
									"Simulation Over!",
									JOptionPane.INFORMATION_MESSAGE);
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String outFileName = "reports/"+timestamp;
			outFileName = outFileName.replaceAll("[-:. ]", "_");
			File file = null;
			FileWriter fw = null;
			try{
				file = new File(outFileName);
				file.createNewFile();
				fw = new FileWriter(outFileName,true);
				for(FactoryObject object : mFObjects) {
					if(object instanceof FactoryReporter) {
						((FactoryReporter) object).report(fw);
					}
				}
			} catch(IOException e) {
				System.out.println("Error occured during file write:"+outFileName);
				if(file != null) file.delete();
			} finally {
				if(fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					System.out.println("Error: failed to close FileWriter!");
				}
			}
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
	
	public FactoryMailbox getMailBox() {
		return mMailBox;
	}
	public void loadListener(FactoryClientListener fcl){
		this.fcl = fcl;
	}
	
}
