package client;

import java.awt.Rectangle;
import java.util.concurrent.Semaphore;
import libraries.ImageLibrary;

public class ResourceRoomDoor extends FactoryObject{
	private Semaphore workPermits;
	
	protected ResourceRoomDoor(Rectangle inDimensions) {
		super(inDimensions);
		workPermits = new Semaphore(3);
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "door2" +Constants.png);
		mLabel = "ResourceRoom Door";
	}
	
	public void accessRoom() throws InterruptedException {
		workPermits.acquire();
	}
	
	public void leaveRoom() {
		workPermits.release();
	}


}
