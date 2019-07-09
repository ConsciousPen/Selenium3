package com.exigen.ipb.eisa.base.app.impl;

import com.exigen.ipb.eisa.base.app.Application;
import com.exigen.ipb.eisa.base.app.ILogin;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;

public class OperationalReportApplication extends Application {

	public OperationalReportApplication() {
		this.host = PropertyProvider.getProperty(CsaaTestProperties.OR_HOST, PropertyProvider.getProperty(TestProperties.APP_HOST));
		this.name = "OR_APP";
		this.path = PropertyProvider.getProperty(CsaaTestProperties.OR_PATH, "operational-reports-app/login.xhtml");
		this.port = PropertyProvider.getProperty(CsaaTestProperties.OR_PORT, 9084);
		this.protocol = PropertyProvider.getProperty(TestProperties.APP_PROTOCOL, "http");
		this.url = formatUrl();
	}

	@Override
	public void switchPanel() {
	}

	@Override
	public void setLogin(ILogin login) {
		this.login = login;
	}

}
