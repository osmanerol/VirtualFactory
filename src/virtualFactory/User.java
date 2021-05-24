package virtualFactory;

public class User {
	public String id, username, password;
	public boolean isLoggedIn;
	
	User(String id, String username, String password, Boolean isLoggedIn){
		this.id = id;
		this.username = username;
		this.password = password;
		this.isLoggedIn = this.isLoggedIn;
	}
	
}