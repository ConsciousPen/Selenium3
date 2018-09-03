package com.exigen.ipb.etcsa.base.app.impl;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.exigen.ipb.etcsa.base.app.Application;
import com.exigen.ipb.etcsa.base.app.ILogin;
import aaa.common.pages.LoginPage;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Link;

public class AdminApplication extends Application {

	public AdminApplication() {
		this.host = PropertyProvider.getProperty(TestProperties.APP_HOST);
		this.name = "ADMIN_APP";
		this.path = PropertyProvider.getProperty(CsaaTestProperties.AD_PATH, "aaa-admin/admin/login.xhtml");
		this.port = PropertyProvider.getProperty(CsaaTestProperties.AD_PORT, 0000);
		this.protocol = PropertyProvider.getProperty(TestProperties.APP_PROTOCOL, "http");
		this.login = new LoginPage(PropertyProvider.getProperty(TestProperties.APP_USER), PropertyProvider.getProperty(TestProperties.APP_PASSWORD));
		this.url = formatUrl();
	}

	@Override
	protected final void switchPanel() {
		log.debug("Switch to Admin App");
		log.debug("Session id: {}", ((RemoteWebDriver) BrowserController.get().driver()).getSessionId());
		Link linkSwitch = new Link(By.id("logoutForm:switchToAdmin"));
		if (linkSwitch.isPresent() && linkSwitch.isVisible()) {
			linkSwitch.click();
		}
	}

	@Override
	public void setLogin(ILogin login) {
		this.login = login;
	}
}
