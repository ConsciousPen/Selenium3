package com.exigen.ipb.etcsa.base.app;

import java.util.function.Function;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CSAACapabilityModifier implements Function<DesiredCapabilities, DesiredCapabilities> {

	@Override
	public DesiredCapabilities apply(DesiredCapabilities desiredCapabilities) {
		if (desiredCapabilities.getBrowserName().equalsIgnoreCase("chrome")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--no-sandbox");
			options.setExperimentalOption("useAutomationExtension", false);
			desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
		}
		return desiredCapabilities;
	}
}
