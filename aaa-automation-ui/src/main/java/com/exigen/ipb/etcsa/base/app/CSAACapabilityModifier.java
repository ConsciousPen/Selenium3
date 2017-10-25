package com.exigen.ipb.etcsa.base.app;

import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import toolkit.config.PropertyProvider;

import java.util.HashMap;
import java.util.function.Function;

public class CSAACapabilityModifier implements Function<DesiredCapabilities, DesiredCapabilities> {

	@Override
	public DesiredCapabilities apply(DesiredCapabilities desiredCapabilities) {
		if (desiredCapabilities.getBrowserName().equalsIgnoreCase("chrome")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--no-sandbox");
			options.setExperimentalOption("useAutomationExtension", false);
			HashMap<String, Object> chromePrefs = new HashMap<>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", System.getProperty("user.dir") + FilenameUtils.separatorsToSystem(PropertyProvider.getProperty("test.downloadfiles.location")));
			options.setExperimentalOption("prefs", chromePrefs);

			desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
		}
		return desiredCapabilities;
	}
}
