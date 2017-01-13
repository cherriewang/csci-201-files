package client;

public class RequestThread extends Thread{
	
	private FactoryClientListener fcl;
	private FactoryManager fm;
	public RequestThread(FactoryClientListener fcl, FactoryManager fm){
		this.fcl = fcl;
		this.fm = fm;
		
		start();
	}
	public void run(){
		while (true){
			try{
				sleep(2000);
				fcl.sendMessage("rrequest");
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	
	}
	

}
