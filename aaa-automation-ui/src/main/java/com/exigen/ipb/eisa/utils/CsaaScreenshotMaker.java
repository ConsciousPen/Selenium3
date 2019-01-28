/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ipb.eisa.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.exceptions.IstfException;
import toolkit.utils.screenshots.impl.BasicScreenshotMaker;
import toolkit.webdriver.BrowserController;

/**
 * @author ikisly
 */
public class CsaaScreenshotMaker extends BasicScreenshotMaker {
	private static final Logger LOGGER = LoggerFactory.getLogger(CsaaScreenshotMaker.class);

	@Override
	public boolean capture(File file) throws IOException {
		boolean result;
		try {
			hideFooter();
			BrowserController.get().executeScript("document.body.scrollTop = document.documentElement.scrollTop = 0;");
			result = isProfileChrome() ? getChromeFullScreenShot(file) : super.capture(file);
			showFooter();
		} catch (IstfException ie) {
			LOGGER.info("Unable to make Screenshot: {}", ie.getMessage());
			return false;
		} catch (Exception e) {
			LOGGER.info("Unable to make Screenshot: {}", e.getMessage());
			e.printStackTrace();
			return false;
		}
		return result;
	}

	protected void hideFooter() {
		try {
			BrowserController.get().executeScript("$('#headerForm').hide();");
			BrowserController.get().executeScript("try{document.styleSheets[0].insertRule('body:after,footer:after{display:none!important}',0);}catch(e){}");
		} catch (Exception e) {
			LOGGER.debug("Error execute script for hide footer: ", e);
		}
	}

	protected void showFooter() {
		try {
			BrowserController.get().executeScript("$('#headerForm').show();");
		} catch (Exception e) {
			LOGGER.debug("Error execute script for show footer: ", e);
		}
	}

	private boolean isProfileChrome() {
		String actualProfile = PropertyProvider.getProperty(TestProperties.WEBDRIVER_PROFILE);
		return actualProfile.equals("chrome") || actualProfile.equals("googlechrome");
	}

	private boolean getChromeFullScreenShot(File file) throws IOException {
		if (BrowserController.isInitialized()) {
			BufferedImage screenshot = Shutterbug.shootPage(BrowserController.get().driver(), ScrollStrategy.BOTH_DIRECTIONS).getImage();
			ImageIO.write(screenshot, "png", file);
			return true;
		}
		return false;
	}
}
