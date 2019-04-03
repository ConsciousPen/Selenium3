package com.exigen.ipb.eisa.base.app.impl;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.base.app.Application;
import com.exigen.ipb.eisa.base.app.ILogin;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.webdriver.controls.Link;

public class StubApplication extends Application {

	public StubApplication() {
		this.host = PropertyProvider.getProperty(CsaaTestProperties.STUB_HOST, PropertyProvider.getProperty(TestProperties.APP_HOST));
		this.name = "STUB_APP";
		this.path = PropertyProvider.getProperty(CsaaTestProperties.STUB_PATH, "aaa-external-stub-services-app");
		this.port = PropertyProvider.getProperty(CsaaTestProperties.STUB_PORT, 0000);
		this.protocol = PropertyProvider.getProperty(TestProperties.APP_PROTOCOL, "http");
		this.url = formatUrl();
	}

	@Override
	public final void switchPanel() {
		log.debug("Switch to Admin App");
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