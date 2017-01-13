package client;

import libraries.ImageLibrary;
import resource.Resource;


public class FactoryStockPerson extends FactoryWorker {

	Resource mProductToStock;
	
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockPerson_empty" + Constants.png);
	}
	
	FactoryStockPerson(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(inNumber, startNode, inFactorySimulation);
		mLabel = "StockPerson "+inNumber;
	}

	@Override
	public void run() {

	}
	
}
