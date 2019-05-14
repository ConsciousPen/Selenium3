package com.exigen.ipb.etcsa.base.app;

import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;

public abstract class Application {

	protected static Logger log = LoggerFactory.getLogger(Application.class);
	private static URIBuilder builder = new URIBuilder();

	protected String protocol;
	protected String host;
	protected String path;
	protected String name;
	protected int port;
	protected ILogin login;
	protected String url;

	protected boolean isApplicationOpened;

	protected Application() {
	}

	public ILogin getLogin() {
		return login;
	}

	protected void setLogin(ILogin login) {
		this.login = login;
	}

	public String getHost() {
		return host;
	}

	public String getPath() {
		return path;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getUrl() {
		builder.setScheme(getProtocol()).setHost(getHost()).setPort(getPort()).setPath(getPath());
		return builder.toString().replace("/login.xhtml", "");
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

	public void open() {
		if (isApplicationOpened) {
			close();
		}
		openSession();
		getLogin().login();
		switchPanel();
	}

	public void open(TestData td) {
		if (isApplicationOpened) {
			close();
		}
		openSession();
		getLogin().login(td);
		switchPanel();
	}

	public void reopen() {
		close();
		open();
	}

	public void close() {
		if (isApplicationOpened) {
			setApplicationOpened(false);
			try {
				getLogin().logout();
			} catch (Exception e) {
				log.info("Cannot close application: " + e);
			}
			if (!TimeShiftTestUtil.isContextAvailable()) {
				closeSession();
			}
		}
	}

	public void open(String url) {
		BrowserController.get().open(url);
	}

	public String formatUrl() {
		builder.setScheme(getProtocol()).setHost(getHost()).setPort(getPort()).setPath(getPath());
		return builder.toString();
	}

	protected abstract void switchPanel();

	private void openSession() {
		CSAAApplicationFactory.get().adminApp().close();
		CSAAApplicationFactory.get().mainApp().close();
		CSAAApplicationFactory.get().opReportApp().close();
		if (TimeShiftTestUtil.isContextAvailable()) {
			if (TimeShiftTestUtil.getContext().getPhaseUrls().length == 0) {
				log.info("Opening URL: " + url);
				TimeShiftTestUtil.getContext().setPhaseStartUrls(url);
			}
			BrowserController.initBrowser(TimeShiftTestUtil.getContext().getBrowser(0).getWebDriver());
		} else {
			BrowserController.initBrowser();
		}

		BrowserController.get().open(url);
		BrowserController.get().driver().manage().window().setSize(new Dimension(1920, 1080));
		setApplicationOpened(true);
	}

	private void closeSession() {
		WebDriver driver = BrowserController.get().driver();
		driver.getWindowHandles().forEach(handle -> driver.switchTo().window(handle).close());
		driver.quit();
	}

	private void setApplicationOpened(boolean status) {
		isApplicationOpened = status;
	}

}