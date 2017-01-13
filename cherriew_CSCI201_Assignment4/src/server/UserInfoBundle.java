package server;

import java.io.Serializable;

public class UserInfoBundle implements Serializable  {

	public String username;
	public String password;
	public boolean signup;
	
	public UserInfoBundle(String user, String pass, boolean su) {
		username = user;
		password = pass;
		signup = su;
	}
	
	
	
	
}
