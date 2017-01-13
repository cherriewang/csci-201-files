package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import server.Commands.Command;

public class ClientConnection implements Runnable{

	private Socket mSocket;
	private BufferedReader mReader;
    private BufferedWriter mWriter;
    
    private Log mLog;
    
    private final static String DIRECTORY = "serverfiles/";
    private String user;
    private File userDirectory;
	
	public ClientConnection(Socket inSocket, Log inLog) throws IOException {
		mSocket = inSocket;
		mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
		mLog = inLog;
	}
	
	private void newLineFlush() throws IOException {
		mWriter.newLine();
		mWriter.flush();
	}
	
	@Override
	public void run() {
		boolean running = true;
		while(running) {
			try {
				String line = mReader.readLine();
				String[] split = line.split(" ");
				mLog.log(Commands.logCommand(split));
				switch(Command.valueOf(split[0])) {
				case Signup:
					if(Database.get().signup(split[1], split[2])) {
						mLog.log("Signup success User:" + split[1]);
						mWriter.write(Command.Success.toString());
						user = split[1];
						userDirectory = new File(DIRECTORY+user);
						userDirectory.mkdir();
					}
					else {
						mLog.log("Signup failure User:" + split[1]);
						mWriter.write(Command.Failure.toString());
					}
					newLineFlush();
					break;
				case giveIt:
					Server.thisServer.updateMyServer(split[1], split[3], split[2]);
					
					break;
					
				case weFinished:
					Server.thisServer.weDone();
					break;
				case Login:
					if(Database.get().login(split[1], split[2])) {
						mLog.log("Login success User:" + split[1]);
						mWriter.write(Command.Success.toString());
						user = split[1];
						userDirectory = new File(DIRECTORY+user);
					}
					else {
						mLog.log("Login failure User:" + split[1]);
						mWriter.write(Command.Failure.toString());
					}
					newLineFlush();
					break;
				case GetUserFileNames:
					StringBuilder sb = new StringBuilder();
					for(File f : userDirectory.listFiles()) if(f.isFile()) sb.append(f.getName()+" ");
					mWriter.write(sb.toString());
					newLineFlush();
					break;
				case OpenUserFile: {
					FileInputStream fis = new FileInputStream(DIRECTORY+user+"/"+split[1]);
					int x = 0;
					while((x=fis.read()) != -1) {
						mWriter.write(x);
					}
					mWriter.write(0);
					mWriter.flush();
					fis.close();
					mLog.log("File opened User:" + user + " File:" + split[1]);
				} break;
				case SaveUserFile: {
					FileOutputStream fos = new FileOutputStream(DIRECTORY+user+"/"+split[1]);
					int x = 0;
					while((x=mReader.read()) != 0) fos.write(x);
					fos.close();
					mLog.log("File saved User:" + user +" File:"+split[1]);
				} break;
				case Heartbeat: break;
				default:
					mLog.log("Command not processed: " + split[0]);
					break;
				}
				
			} catch (Exception e) {
				System.out.println("Shutting down");
				try {mSocket.close();} catch (IOException ioe) {}
				running = false;
			}
		}
	}

}
