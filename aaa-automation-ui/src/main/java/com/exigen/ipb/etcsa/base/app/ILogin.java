package com.exigen.ipb.etcsa.base.app;

import toolkit.datax.TestData;

public interface ILogin {
	void login();

	void login(TestData td);

	default void reLogin() {
		logout();
		login();
	}

	default void reLogin(TestData td) {
		logout();
		login(td);
	}

	void logout();
}
