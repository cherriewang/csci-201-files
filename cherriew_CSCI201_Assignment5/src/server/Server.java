package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;

public class Server {
	private final ServerLogFrame mServerLog;
	private static final String CONFIG_FILE = "server.config";
	private int port;

	public static Server thisServer;
	
	
	public HashMap<String, MergingHere> myMap = new HashMap<>();

	private ServerSocket ss = null;
	private Thread mThread;
	private ArrayList<Socket> mConnections;
	public SavingThread savingThread;
	static int ct = 0;

	{
		thisServer = this;
		JButton toggleButton = new JButton() {
			private static final long serialVersionUID = 6990460083243951898L;
			private final static String START_TEXT = "Start";
			private final static String STOP_TEXT = "Stop";
			private boolean started = false;
			{
				setText(START_TEXT);
				addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if(!started) {
							setText(STOP_TEXT);
							mThread = new Thread(()->{run();});
							mThread.start();
						} else {
							setText(START_TEXT);
							mThread.interrupt();
							try { ss.close(); } catch (IOException e) { }
						}
						started = !started;
					}
				});
			}
		};
		mServerLog = new ServerLogFrame(toggleButton);

		Scanner s = null;
		try {
			s = new Scanner(new File(CONFIG_FILE));
			while(s.hasNext()) {
				String id = s.next();
				switch(id) {
				case "Port":
					port = Integer.valueOf(s.next());
					break;
				}
			}
		} catch (FileNotFoundException e) {
			mServerLog.log("Error in configuration File!");
		} finally {
			if(s != null) s.close();
		}
		mConnections = new ArrayList<Socket>();
		mServerLog.setVisible(true);
	}

	public static void main(String[] args) {
		new Server();
	}

	
	public void weDone() {
		ct--;
		//check for 
		if(ct == 0){
			//then lopp
			for(MergingHere v: myMap.values() ){
				String tmp = v.completedMerge();
				for(int i = 0; i< mConnections.size(); i++){
					try {
					BufferedWriter writeToMe = new BufferedWriter(new OutputStreamWriter(mConnections.get(i).getOutputStream()));
					
						writeToMe.write(Commands.buildCommand(Commands.Command.weFinished, v.fileNameJustInCase, tmp));
						writeToMe.newLine();
						writeToMe.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	
	
	public void updateMyServer(String fname, String oldStuff, String newStuff){
		MergingHere m = myMap.get(fname);
		if (m == null){
			m = new MergingHere();
			myMap.put(fname, m);
			m.oldStuff = oldStuff;
			m.fileNameJustInCase = fname;
		}
		m.contentOfFiles.add(newStuff);
	}
	
	
	
	public class MergeThread extends Thread {
		
		@Override
		public void run(){
			while(true){
				
				//TODO config
				try {
					Thread.sleep(2000);
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ct = mConnections.size();
				for(int j = 0; j < ct; j++){
					
					try {
						BufferedWriter writeToMe = new BufferedWriter(new OutputStreamWriter(mConnections.get(j).getOutputStream()));
						
						writeToMe.write(Commands.buildCommand(Commands.Command.getThings));
						writeToMe.newLine();
						writeToMe.flush();
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		}
		
	}
	

	public class SavingThread extends Thread {

		@Override
		public void run(){
			while(true){
				try {
					Thread.sleep(5000);
					// loop thru merg in my map
					for(MergingHere val : myMap.values()){
						String file = val.fileNameJustInCase;
						String ownerName = Database.get().getOwnerOfFile(file);
						String whereWeGoin = "serverfiles/"+ownerName+"/"+file;

						FileOutputStream out = new FileOutputStream(whereWeGoin);

						//TODO: wierd thigns 


						String fileStuff = val.oldStuff;
						out.write(fileStuff.getBytes(Charset.forName("UTF-8")));
						out.close();
						
					}

				} catch (InterruptedException e) {

					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	public void run() {
		try {
			ss = new ServerSocket(port);
			mServerLog.log("Server Started On Port:" + port);
			while(true) {
				Socket socket = ss.accept();
				new Thread(new ClientConnection(socket, mServerLog)).start();

				// every 5 sec baby
				if(savingThread == null){
					savingThread = new SavingThread();
					savingThread.start();
					new MergeThread().start();
				}

				mConnections.add(socket);
			}
		} catch (Exception e) {
			for(Socket s : mConnections)
				try { s.close(); } catch (IOException e1) { }
			mServerLog.log("Server stopped.");
		} finally {
			try {
				if(ss != null) ss.close();
			} catch (IOException e) {
			}
		}
	}
}
