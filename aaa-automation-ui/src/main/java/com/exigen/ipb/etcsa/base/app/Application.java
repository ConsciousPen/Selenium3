package com.exigen.ipb.etcsa.base.app;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;
import com.exigen.ipb.etcsa.base.config.CustomTestProperties;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;

public abstract class Application {

    protected static Logger log = LoggerFactory.getLogger(Application.class);

    protected boolean isApplicationOpened = false;

    protected String url = "";
    protected String name = "";

    public ILogin login;

    public enum AppType {
        ADMIN,
        OPERATIONAL_REPORT,
        EU
    }

    protected Application(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void setLogin(ILogin login) {
        this.login = login;
    }

    public ILogin getLogin() {
        return login;
    }

    public abstract void switchPanel();

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

    public static void wait(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            log.warn("", e);
        }
    }

    private void openSession() {
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

    public static String formatURL(AppType type) {
        String result = "http://";
        String host = PropertyProvider.getProperty(TestProperties.APP_HOST);
        if ((AppType.EU.equals(type) || AppType.ADMIN.equals(type)) && !PropertyProvider.getProperty(TestProperties.APP_EU_HOST).isEmpty()) {
            host = PropertyProvider.getProperty(TestProperties.APP_EU_HOST);
        }
        return result + host + addTemplate(type);
    }

	private static String addTemplate(AppType type) {
		String result = "";
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
      /*  String result = "";
        String mainServerURLendpoint = PropertyProvider.getProperty(TestProperties.EU_URL_TEMPLATE).split("/login.xhtml")[0];
        switch (type) {
            case ADMIN:
                result += PropertyProvider.getProperty(TestProperties.AD_URL_TEMPLATE);
                if (!mainServerURLendpoint.endsWith("/")) {
                    result += "/";
                }
                result += "admin";
                break;
            case EU:
                result += mainServerURLendpoint + "/";
                break;
            case OPERATIONAL_REPORT:
                if (PropertyProvider.getProperty(CustomTestProperties.OR_URL_TEMPLATE).isEmpty()) {
                    log.warn("Property '" + CustomTestProperties.OR_URL_TEMPLATE + "' is missing.");
                    result += mainServerURLendpoint;
                } else {
                    result += PropertyProvider.getProperty(CustomTestProperties.OR_URL_TEMPLATE);
                }
            default:
        }
        return result;*/
    }
}