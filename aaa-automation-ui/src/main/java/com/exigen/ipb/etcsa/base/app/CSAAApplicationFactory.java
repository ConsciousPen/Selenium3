package com.exigen.ipb.etcsa.base.app;

public class CSAAApplicationFactory {
	static CSAAApplicationFactory appFactory = null;

	private static ThreadLocal<Application.AppType> appType = new ThreadLocal<Application.AppType>() {
		@Override
		public Application.AppType initialValue() {
			return Application.AppType.EU;
		}
	};

	private static ThreadLocal<MainApplication> mainApp = new ThreadLocal<MainApplication>() {
		@Override
		public MainApplication initialValue() {
			return new MainApplication(Application.AppType.EU);
		}
	};

	private static ThreadLocal<AdminApplication> adminApp = new ThreadLocal<AdminApplication>() {
		@Override
		public AdminApplication initialValue() {
			return new AdminApplication(Application.AppType.ADMIN);
		}
	};

	private static ThreadLocal<OperationalReportApplication> opReportApp = new ThreadLocal<OperationalReportApplication>() {
		@Override
		public OperationalReportApplication initialValue() {
			return new OperationalReportApplication(Application.AppType.OPERATIONAL_REPORT);
		}
	};

	public static synchronized CSAAApplicationFactory get() {
		if (appFactory == null) {
			appFactory = new CSAAApplicationFactory();
		}
		return appFactory;
	}

	public MainApplication mainApp(ILogin login) {
		appType.set(Application.AppType.EU);
		mainApp.get().setType(Application.AppType.EU);
		mainApp.get().setLogin(login);
		return mainApp.get();
	}

	public AdminApplication adminApp(ILogin login) {
		appType.set(Application.AppType.ADMIN);
		adminApp.get().setType(Application.AppType.ADMIN);
		adminApp.get().setLogin(login);
		return adminApp.get();
	}

	public OperationalReportApplication opReportApp(ILogin login) {
		appType.set(Application.AppType.OPERATIONAL_REPORT);
		opReportApp.get().setType(Application.AppType.OPERATIONAL_REPORT);
		opReportApp.get().setLogin(login);
		return opReportApp.get();
	}

	public Application.AppType getAppType() {
		return appType.get();
	}
}
