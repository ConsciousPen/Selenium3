package com.exigen.ipb.eisa.base.app;

import java.util.Arrays;
import java.util.HashMap;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;
import toolkit.metrics.ReportingContext;
import toolkit.webdriver.OptionsModifier;

public class CSAACapabilityModifier extends OptionsModifier {

	@Override
	public ChromeOptions chrome(ChromeOptions options) {
		options.setHeadless(PropertyProvider.getProperty(CsaaTestProperties.BROWSER_SET_HEADLESS, false));
		options.addArguments("disable-infobars", "--no-sandbox");
		options.setExperimentalOption("useAutomationExtension", false);
		HashMap<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("safebrowsing.enabled", true);
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.prompt_for_download", "false");
		chromePrefs.put("plugins.always_open_pdf_externally", true);
		options.setExperimentalOption("prefs", chromePrefs);
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		options.setCapability("name", ReportingContext.get().getCurrentTestName());
		options.setCapability("enableVNC", true);
		return allBrowsers(options);
	}

	@Override
	public FirefoxOptions firefox(FirefoxOptions options) {
		options.setHeadless(PropertyProvider.getProperty(CsaaTestProperties.BROWSER_SET_HEADLESS, false));
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/ms-excel");
		options.setCapability(FirefoxDriver.PROFILE, profile);
		options.setCapability("name", ReportingContext.get().getCurrentTestName());
		options.setCapability("enableVNC", true);
		return allBrowsers(options);
	}

	@Override
	public InternetExplorerOptions iexplore(InternetExplorerOptions options) {
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		options.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
		options.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);
		options.setCapability("name", ReportingContext.get().getCurrentTestName());
		options.setCapability("enableVNC", true);
		options.usePerProcessProxy();
		options.setCapability("requireWindowFocus", false);
		options.setCapability("ie.ensureCleanSession", true);
		options.setCapability("ie.browserCommandLineSwitches", Arrays.asList("-private"));
		options.takeFullPageScreenshot();
		options.ignoreZoomSettings();
		options.introduceFlakinessByIgnoringSecurityDomains();
		return allBrowsers(options);
	}
}
