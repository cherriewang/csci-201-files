package server;

import java.io.Serializable;

public class SaveInfoBundle implements Serializable{


	public String username;
	public String password;
	public boolean signup;
	
	public SaveInfoBundle(String user, String pass, boolean su) {
		username = user;
		password = pass;
		signup = su;
	}
	
	
}
