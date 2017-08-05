package aaa.toolkit.webdriver;

public interface Login {

	/**
	 * Logins to the application
	 */
	public void doLogin();
	
	/**
	 * Logins to the application
	 */
	public void doLogin(String user, String password, String state);
	
	/**
	 * Logout from the application
	 */
	public void doLogout();
}
