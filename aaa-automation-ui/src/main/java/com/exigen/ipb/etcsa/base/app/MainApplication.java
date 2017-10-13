package com.exigen.ipb.etcsa.base.app;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.Link;

public class MainApplication extends Application {
	private Application.AppType type;

	public MainApplication(Application.AppType appType) {
		super(appType.name(), formatURL(appType));
		type = appType;
	}

	@Override
	public void switchPanel() {
		Link linkSwitch = Application.AppType.EU.equals(type) ? new Link(By.id("logoutForm:switchToApp")) : new Link(By.id("logoutForm:switchToAdmin"));
		if (linkSwitch.isPresent() && linkSwitch.isVisible()) {
			linkSwitch.click();
		}
	}

	public Application.AppType getType() {
		return type;
	}

	public void setType(Application.AppType type) {
		this.type = type;
	}
}
