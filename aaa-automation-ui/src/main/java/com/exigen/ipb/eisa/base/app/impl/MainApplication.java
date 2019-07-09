package com.exigen.ipb.eisa.base.app.impl;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.base.app.Application;
import com.exigen.ipb.eisa.base.app.ILogin;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.webdriver.controls.Link;

public class MainApplication extends Application {

	public MainApplication() {
		this.host = PropertyProvider.getProperty(TestProperties.APP_HOST);
		this.name = "MAIN_APP";
		this.path = PropertyProvider.getProperty(TestProperties.APP_PATH, "aaa-app/login.xhtml");
		this.port = PropertyProvider.getProperty(TestProperties.APP_PORT, 0000);
		this.protocol = PropertyProvider.getProperty(TestProperties.APP_PROTOCOL, "http");
		this.url = formatUrl();

	}

	@Override
	public void switchPanel() {
		Link linkSwitch = new Link(By.id("logoutForm:switchToApp"));
		if (linkSwitch.isPresent() && linkSwitch.isVisible()) {
			linkSwitch.click();
		}
	}

	@Override
	public void setLogin(ILogin login) {
		this.login = login;
	}
}
