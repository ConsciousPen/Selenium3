package com.exigen.ipb.etcsa.base.app;

import java.util.HashMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;
import toolkit.webdriver.OptionsModifier;

public class CSAACapabilityModifier extends OptionsModifier {

	@Override
	public ChromeOptions chrome(ChromeOptions options) {
		String downloadPath;
		if (StringUtils.isNotEmpty(PropertyProvider.getProperty(CsaaTestProperties.REMOTE_DOWNLOAD_FOLDER_PROP, StringUtils.EMPTY))) {
			downloadPath = PropertyProvider.getProperty(CsaaTestProperties.REMOTE_DOWNLOAD_FOLDER_PROP);
		} else {
			downloadPath = System.getProperty(CsaaTestProperties.USER_DIR_PROP) + FilenameUtils.separatorsToSystem(PropertyProvider.getProperty(CsaaTestProperties.LOCAL_DOWNLOAD_FOLDER_PROP));
		}
		options.setHeadless(PropertyProvider.getProperty(CsaaTestProperties.BROWSER_SET_HEADLESS, false));
		options.addArguments("disable-infobars", "--no-sandbox");
		options.setExperimentalOption("useAutomationExtension", false);
		HashMap<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.prompt_for_download", "false");
		chromePrefs.put("download.default_directory", downloadPath);
		options.setExperimentalOption("prefs", chromePrefs);
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		return allBrowsers(options);
	}

	@Override
	public FirefoxOptions firefox(FirefoxOptions options) {
		String downloadPath;
		if (StringUtils.isNotEmpty(PropertyProvider.getProperty(CsaaTestProperties.REMOTE_DOWNLOAD_FOLDER_PROP, StringUtils.EMPTY))) {
			downloadPath = PropertyProvider.getProperty(CsaaTestProperties.REMOTE_DOWNLOAD_FOLDER_PROP);
		} else {
			downloadPath = System.getProperty(CsaaTestProperties.USER_DIR_PROP) + FilenameUtils.separatorsToSystem(PropertyProvider.getProperty(CsaaTestProperties.LOCAL_DOWNLOAD_FOLDER_PROP));
		}
		options.setHeadless(PropertyProvider.getProperty(CsaaTestProperties.BROWSER_SET_HEADLESS, false));
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.dir", downloadPath);
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/ms-excel");
		options.setCapability(FirefoxDriver.PROFILE, profile);
		return allBrowsers(options);
	}
}
