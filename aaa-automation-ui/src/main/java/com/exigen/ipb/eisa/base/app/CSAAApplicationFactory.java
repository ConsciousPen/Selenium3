package com.exigen.ipb.eisa.base.app;

import com.exigen.ipb.eisa.base.app.impl.AdminApplication;
import com.exigen.ipb.eisa.base.app.impl.MainApplication;
import com.exigen.ipb.eisa.base.app.impl.OperationalReportApplication;

public class CSAAApplicationFactory {
	static CSAAApplicationFactory appFactory;

	private static ThreadLocal<MainApplication> mainApp = ThreadLocal.withInitial(MainApplication::new);

	private static ThreadLocal<AdminApplication> adminApp = ThreadLocal.withInitial(AdminApplication::new);

	private static ThreadLocal<OperationalReportApplication> opReportApp = ThreadLocal.withInitial(OperationalReportApplication::new);

	public static synchronized CSAAApplicationFactory get() {
		return InstanceHolder.HOLDER_INSTANCE;
	}

	public MainApplication mainApp() {
		return mainApp.get();
	}

	public AdminApplication adminApp() {
		return adminApp.get();
	}

	public OperationalReportApplication opReportApp() {
		return opReportApp.get();
	}

	private static class InstanceHolder {
		public static final CSAAApplicationFactory HOLDER_INSTANCE = new CSAAApplicationFactory();
	}

	public void closeAllApps() {
		mainApp().close();
		adminApp().close();
		opReportApp().close();
	}

}
