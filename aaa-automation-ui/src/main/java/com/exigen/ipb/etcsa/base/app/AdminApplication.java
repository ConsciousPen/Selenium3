package com.exigen.ipb.etcsa.base.app;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.Link;

public class AdminApplication extends Application {
	private Application.AppType type;

	public AdminApplication(Application.AppType appType) {
		super(appType.name(), formatURL(appType));
		type = appType;
	}

	@Override
	protected final void switchPanel() {
		Link linkSwitch = AppType.ADMIN.equals(type) ? new Link(By.id("logoutForm:switchToAdmin")) : new Link(By.id("logoutForm:switchToApp"));
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
