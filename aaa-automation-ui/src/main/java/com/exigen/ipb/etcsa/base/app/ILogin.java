package com.exigen.ipb.etcsa.base.app;

import toolkit.datax.TestData;

public interface ILogin {
	void login();

	void login(Boolean loginViaURL);

	void login(String username, String password);

	void login(String username, String password, Boolean loginViaURL);

	void login(TestData td);

	void login(TestData td, Boolean loginViaURL);

	default void reLogin() {
		logout();
		login();
	}

	default void reLogin(Boolean loginViaURL) {
		logout();
		login(loginViaURL);
	}

	default void reLogin(String username, String password) {
		logout();
		login(username, password);
	}

	default void reLogin(String username, String password, Boolean loginViaURL) {
		logout();
		login(username, password, loginViaURL);
	}

	void logout();
}
