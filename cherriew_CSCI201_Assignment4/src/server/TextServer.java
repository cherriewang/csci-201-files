package server;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;



public class TextServer extends Thread {
	private ServerSocket ss;
	private Vector<ServerThread> serverThreads;
	public TextServerGUI tsg;
	
	public static final long serialVersionUID = 1;

	private String fileName = "config.txt";
	private int portHere;
	
	public TextServer(TextServerGUI abc) throws IOException {
		
		//ServerSocket ss = null;
		ss = null;
		serverThreads = new Vector<ServerThread>();
		getPortNum();
		tsg = abc;
	}
	
	private void getPortNum() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
		    String line = br.readLine();
		    portHere = Integer.parseInt(line);
		} finally {
		    br.close();
		}
	}
	
	
	
	public void run() {
		try {

			ss = new ServerSocket(portHere);
			System.out.println(portHere);
			while (true) {
				
				System.out.println("waiting for connection...");
				Socket s = ss.accept();
				System.out.println("connection from " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		} finally {
			
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("ioe closing ss: " + ioe.getMessage());
				}
			}
		}
	}

	
	public void end() throws IOException {
		ss.close();
	}
	

}
