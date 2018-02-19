package com.exigen.ipb.etcsa.base.app;

import java.util.HashMap;
import java.util.function.Function;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.mortbay.log.Log;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import toolkit.config.PropertyProvider;

public class CSAACapabilityModifier implements Function<DesiredCapabilities, DesiredCapabilities> {

	private static final String REMOTE_DOWNLOAD_FOLDER_PROP = "test.remotefile.location";
	private static final String USER_DIR_PROP = "user.dir";
	private static final String LOCAL_DOWNLOAD_FOLDER_PROP = "test.downloadfiles.location";
	private static final String CHROME = "chrome";
	private static final String FIREFOX = "firefox";

	@Override
	public DesiredCapabilities apply(DesiredCapabilities desiredCapabilities) {
		String downloadPath;
		if (StringUtils.isNotEmpty(PropertyProvider.getProperty(REMOTE_DOWNLOAD_FOLDER_PROP, StringUtils.EMPTY))) {
			downloadPath = PropertyProvider.getProperty(REMOTE_DOWNLOAD_FOLDER_PROP);
		} else {
			downloadPath = System.getProperty(USER_DIR_PROP) + FilenameUtils.separatorsToSystem(PropertyProvider.getProperty(LOCAL_DOWNLOAD_FOLDER_PROP));
		}
		Log.info("DownloadPath: {}", downloadPath);
		switch (desiredCapabilities.getBrowserName()) {

			case CHROME : {
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--no-sandbox");
				options.setExperimentalOption("useAutomationExtension", false);
				HashMap<String, Object> chromePrefs = new HashMap<>();
				chromePrefs.put("profile.default_content_settings.popups", 0);
				chromePrefs.put("download.default_directory", downloadPath);
				options.setExperimentalOption("prefs", chromePrefs);

				desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
				break;
			}
			case FIREFOX : {
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("browser.download.dir", downloadPath);
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.manager.showWhenStarting", false);
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/ms-excel");
				desiredCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
			}
		}
		return desiredCapabilities;
	}
}
