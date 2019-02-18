package com.exigen.ipb.eisa.base.app;

import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		if (!isApplicationOpened) {
			openSession();
			getLogin().login();
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

	public void close() {
		if (isApplicationOpened) {
			setApplicationOpened(false);
			try {
				getLogin().logout();
			} catch (Exception e) {
				log.info("Cannot close application: " + e);
			}
			closeSession();
		}
	}

	public void open(String url) {
		if (!isApplicationOpened) {
			openSession();
		}
	}

	public String formatUrl() {
		builder.setScheme(getProtocol()).setHost(getHost()).setPort(getPort()).setPath(getPath());
		return builder.toString();
	}

	protected abstract void switchPanel();

	private void openSession() {
		openSession(url);
	}

	private void openSession(String url) {
		CSAAApplicationFactory.get().closeAllApps();
		BrowserController.initBrowser();
		log.info("Opening URL: " + url);
		BrowserController.get().open(url);
		if (BrowserController.getBrowserName().equals("chrome")) {
			BrowserController.get().driver().manage().window().setSize(new Dimension(1920, 1080));
		} else {
			BrowserController.get().driver().manage().window().maximize();
		}
		setApplicationOpened(true);
	}

	private void closeSession() {
		WebDriver driver = BrowserController.get().driver();
		driver.getWindowHandles().forEach(handle -> driver.switchTo().window(handle).close());
		if (BrowserController.getBrowserName().equals("chrome")) {
			driver.quit();
		} else {
			try {
				BrowserController.get().quit();
			} catch (Exception e) {
				log.info("Unexpected exception during quit non chrome browser {}", e);
			}
		}

	}

	private void setApplicationOpened(boolean status) {
		isApplicationOpened = status;
	}

}