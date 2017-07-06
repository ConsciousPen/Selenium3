package com.exigen.ipb.etcsa.base.app;

import toolkit.datax.TestData;

public interface ILogin {
	public void login();

	public void login(Boolean loginViaURL);

	public void login(String username, String password);

	public void login(String username, String password, Boolean loginViaURL);
	
	public void login(TestData td);
	
	public void login(TestData td, Boolean loginViaURL);

	default public void reLogin() {
		logout();
		login();
	}

	default public void reLogin(Boolean loginViaURL) {
		logout();
		login(loginViaURL);
	}

	default public void reLogin(String username, String password) {
		logout();
		login(username, password);
	}

	default public void reLogin(String username, String password, Boolean loginViaURL) {
		logout();
		login(username, password, loginViaURL);
	}

	public void logout();
}
