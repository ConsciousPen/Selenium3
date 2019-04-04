package com.exigen.ipb.eisa.base.app;

import com.exigen.ipb.eisa.base.app.impl.*;

public class CSAAApplicationFactory {

	private static ThreadLocal<MainApplication> mainApp = ThreadLocal.withInitial(MainApplication::new);

	private static ThreadLocal<AdminApplication> adminApp = ThreadLocal.withInitial(AdminApplication::new);

	private static ThreadLocal<OperationalReportApplication> opReportApp = ThreadLocal.withInitial(OperationalReportApplication::new);

	private static ThreadLocal<RatingApplication> ratingApp = ThreadLocal.withInitial(RatingApplication::new);

	private static ThreadLocal<StubApplication> stubApp = ThreadLocal.withInitial(StubApplication::new);


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

	public RatingApplication ratingApp() {
		return ratingApp.get();
	}

	public StubApplication stubApp() {
		return stubApp.get();
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
