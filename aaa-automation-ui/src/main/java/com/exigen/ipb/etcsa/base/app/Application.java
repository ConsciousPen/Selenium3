package com.exigen.ipb.etcsa.base.app;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.base.config.CustomTestProperties;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;

public abstract class Application {

	protected static Logger log = LoggerFactory.getLogger(Application.class);

	protected boolean isApplicationOpened;

	protected String url = "";
	protected String name = "";

	protected ILogin login;

	public enum AppType {
		ADMIN,
		OPERATIONAL_REPORT,
		EU,
		SWAGGER
	}

	protected Application(String name, String url) {
		this.name = name;
		this.url = url;
	}

	protected void setLogin(ILogin login) {
		this.login = login;
	}

	public ILogin getLogin() {
		return login;
	}

	protected abstract void switchPanel();

	public void open() {
		if (!isApplicationOpened) {
			openSession();
			getLogin().login();
		}
		switchPanel();
	}

	public void open(String username, String password) {
		if (!isApplicationOpened) {
			openSession();
			getLogin().login(username, password);
		}
		switchPanel();
	}

	public void open(TestData td) {
		if (!isApplicationOpened) {
			openSession();
			getLogin().login(td);
		}
		switchPanel();
	}

	public void reopen() {
		close();
		open();
	}

	public void reopen(String username, String password) {
		close();
		open(username, password);
	}

	public void close() {
		if (isApplicationOpened) {
			setApplicationOpened(false);
			try {
				Button buttonSaveAndExit = new Button(By.id("topSaveAndExitLink"));
				if (buttonSaveAndExit.isPresent() && buttonSaveAndExit.isVisible()) {
					buttonSaveAndExit.click();
				}
				getLogin().logout();
			} catch (Exception e) {
				log.info("Cannot close application: " + e);
			}
			if (!TimeShiftTestUtil.isContextAvailable()) {
				closeSession();
			}
		}
	}

	/**
	 * Waiter
	 *
	 * @param seconds - secods amount to wait.
	 */
	public static void wait(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			log.warn("", e);
		}
	}

	private void openSession() {
		CSAAApplicationFactory.get().adminApp(getLogin()).close();
		CSAAApplicationFactory.get().mainApp(getLogin()).close();
		CSAAApplicationFactory.get().opReportApp(getLogin()).close();
		if (TimeShiftTestUtil.isContextAvailable()) {
			if (TimeShiftTestUtil.getContext().getPhaseUrls().length == 0) {
				TimeShiftTestUtil.getContext().setPhaseStartUrls(url);
			}
			BrowserController.initBrowser(TimeShiftTestUtil.getContext().getBrowser(0).getWebDriver());
		} else {
			BrowserController.initBrowser();
		}

		BrowserController.get().open(url);
		BrowserController.get().maximize();
		setApplicationOpened(true);
	}

	private void closeSession() {
		BrowserController.get().driver().quit();
	}

	private void setApplicationOpened(boolean status) {
		isApplicationOpened = status;
	}

	public static String getURL(AppType type) {
		String result = "http://" + getHost(type);
		switch (type) {
			case ADMIN:
				result += PropertyProvider.getProperty(TestProperties.AD_URL_TEMPLATE).split("/login.xhtml")[0];
				result += "/admin";
				break;
			case EU:
				result += PropertyProvider.getProperty(TestProperties.EU_URL_TEMPLATE).split("/login.xhtml")[0];
				result += "/";
			case OPERATIONAL_REPORT:
				result += PropertyProvider.getProperty(CustomTestProperties.OR_URL_TEMPLATE);
				break;
			default:
				break;
		}

		return result;
	}

	protected static String formatURL(AppType type) {
		String result = "http://" + getHost(type);

		switch (type) {
			case ADMIN:
				result += PropertyProvider.getProperty(TestProperties.AD_URL_TEMPLATE);
				break;
			case EU:
				result += PropertyProvider.getProperty(TestProperties.EU_URL_TEMPLATE);
				break;
			case OPERATIONAL_REPORT:
				result += PropertyProvider.getProperty(CustomTestProperties.OR_URL_TEMPLATE);
				break;
			default:
				break;
		}
		return result;
	}

	private static String getHost(AppType type) {
		String host = PropertyProvider.getProperty(TestProperties.APP_HOST);
		if ((AppType.EU.equals(type) || AppType.ADMIN.equals(type)) && !PropertyProvider.getProperty(TestProperties.APP_EU_HOST).isEmpty()) {
			host = PropertyProvider.getProperty(TestProperties.APP_EU_HOST);
		}
		return host;
	}

	public static void open(String url) {
		BrowserController.get().open(url);
	}
}