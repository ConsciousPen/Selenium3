package com.exigen.ipb.etcsa.base.app;

import com.exigen.ipb.etcsa.base.app.impl.AdminApplication;
import com.exigen.ipb.etcsa.base.app.impl.MainApplication;
import com.exigen.ipb.etcsa.base.app.impl.OperationalReportApplication;

public class CSAAApplicationFactory {
	static CSAAApplicationFactory appFactory;

	private static ThreadLocal<MainApplication> mainApp = new ThreadLocal<MainApplication>() {
		@Override
		public MainApplication initialValue() {
			return new MainApplication();
		}
	};

	private static ThreadLocal<AdminApplication> adminApp = new ThreadLocal<AdminApplication>() {
		@Override
		public AdminApplication initialValue() {
			return new AdminApplication();
		}
	};

	private static ThreadLocal<OperationalReportApplication> opReportApp = new ThreadLocal<OperationalReportApplication>() {
		@Override
		public OperationalReportApplication initialValue() {
			return new OperationalReportApplication();
		}
	};

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

}
