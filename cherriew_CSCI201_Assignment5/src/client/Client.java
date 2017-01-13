package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import server.Commands;
import server.Commands.Command;
import textdocument.TextDocumentManager;
import textdocument.TextDocumentPanel;

final public class Client implements Runnable {
	
	private static Client mInstance;
	
	public static Client get() {
		return mInstance;
	}
	
	public ArrayList<String> inArr = new ArrayList<String>();
	private static String CONFIG_FILE = "client.config";
	private static String host = null;
	private static int port = -1;
	private static Socket mSocket;
	private static BufferedReader mReader;
	private static BufferedWriter mWriter;
	private static String mUsername;
	
	public Semaphore yay = new Semaphore(0);
	
	
	static {
		mInstance = new Client();
	}
	
	{
		loadConfig();
		connect();
	}
	
	public void loadConfig(){
		Scanner s = null;
		try {
			s = new Scanner(new File(CONFIG_FILE));
			while(s.hasNext()) {
				String id = s.next();
				switch(id) {
				case "Port":
					port = Integer.valueOf(s.next());
					break;
				case "Host":
					host = s.next();
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Error in configuration File!");
		} finally {
			if(s != null) s.close();
		}
	}
	
	
	@Override
	public void run(){
		
		try {
			while(true){
		
				String myline = mReader.readLine();
				String[] mySplitted = myline.split(" ");
				if(Command.valueOf(mySplitted[0]) == Commands.Command.getThings){
					if(TextDocumentManager.sharedManager != null){
						for(TextDocumentPanel p : TextDocumentManager.sharedManager.allPanelsOpen){
							if(p.isOnline == true){
								String curr = p.mTextPane.getText();
								String old = p.olderText;
								
								curr = curr.replace("\n", "%");
									
								curr = curr.replace(" ", "$");
									
								old = old.replace("\n", "%");
								old = old.replace(" ", "$");
								try {
									writeln(Commands.buildCommand(Commands.Command.giveIt, p.mFile.getName(), curr, old));
									flush();
								} catch (Exception e){
									
								}
	
							}
						}
						// more commands
						try{
							// hey we sent it all
							writeln(Commands.buildCommand(Commands.Command.weFinished));
							flush();
						} catch (Exception e){
							
						}
							
						
					}
						
				} else if (Command.valueOf(mySplitted[0]) == Commands.Command.weFinished){
					
					// checking to make sure we done 
					if(TextDocumentManager.sharedManager != null){
						for(TextDocumentPanel p : TextDocumentManager.sharedManager.allPanelsOpen){
							if(p.isOnline == true && p.mFile.getName().equals(mySplitted[1])){
								
								String curr = mySplitted[2];
								String old = p.olderText;
								
								curr = curr.replace("%", "\n");
									
								curr = curr.replace("$", " ");
								
								int cursor = p.mTextPane.getCaretPosition();
								
								p.mTextPane.setText(curr);
								
								if (cursor > p.mTextPane.getText().length())
								{
									p.mTextPane.setCaretPosition(p.mTextPane.getText().length());
								}
								else
								{
									p.mTextPane.setCaretPosition(cursor);
								}
								
								p.olderText = curr;
								
								
								
							}
						}
					}
					
				} else {
					//TODO:
					inArr.add(myline);
					yay.release();
				}
				
			}
		
			
		} catch (Exception e){
			
		}
		
		
		
	}
	
	
	public void connect() {
		if(mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			mSocket = new Socket(host, port);
			mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
			new Thread(this).start();
		} catch (IOException e) {
			System.out.println("Unable to connect to:" + host + port);
		}
	}
	
	public void write(String str) throws IOException {
		mWriter.write(str);
	}
	
	public void writeln(String str) throws IOException {
		mWriter.write(str);
		mWriter.newLine();
	}
	
	public void writeFile(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		int x = 0;
		while((x=fis.read()) != -1) {
			mWriter.write(x);
		}
		mWriter.write(0);
		mWriter.flush();
		fis.close();
	}
	
	public void flush() throws IOException {
		mWriter.flush();
	}
	
	public String read() throws IOException {
		try {
			this.yay.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return inArr.remove(0);
		
		//return mReader.readLine();
	}
	
	public void readFile(File f) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		int x = 0;
		while((x=mReader.read()) != 0) fos.write(x);
		fos.close();
	}

	public boolean isOnline() {
		try {
			writeln(Command.Heartbeat.toString());
			flush();
		} catch (Exception e) { return false; }
		return true;
	}

	public void setUser(String inUsername) {
		mUsername = inUsername;
	}
	
	public String getUser(){
		return mUsername;
	}
	
	public boolean isAuthentic() {
		return mUsername != null;
	}
	
}
