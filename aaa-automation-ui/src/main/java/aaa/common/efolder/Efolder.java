/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.efolder;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.utils.RetryService;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import aaa.common.Workspace;
import aaa.common.efolder.views.DefaultView;
import aaa.common.enums.EfolderConstants;
import aaa.utils.browser.DownloadsHelper;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;

public class Efolder {
	private static final Logger LOGGER = LoggerFactory.getLogger(Efolder.class);
	public static Link linkOpenEFolder = new Link(By.id("slide_panel_open_ctrl"));
	public static Link linkCloseEFolder = new Link(By.id("slide_panel_hide_ctrl"));
	private Workspace defaultView = new DefaultView();

	public static boolean isNotOpened() {
		return !(linkCloseEFolder.isPresent() && linkCloseEFolder.isVisible());
	}

	public Workspace getDefaultView() {
		return defaultView;
	}

	public static boolean isDocumentExist(String path, String documentName) {
		if (!isTreeExpanded(path)) {
			expandFolder(path);
		}
		return new StaticElement(By.xpath(String.format("//form[@id='efForm']//span[@class='rf-trn-lbl']/span[.='%s']", documentName))).isPresent();
	}

	public static boolean isDocumentExist(String documentName) {
		return new StaticElement(By.xpath(String.format("//form[@id='efForm']//span[@class='rf-trn-lbl']/span[.='%s']", documentName))).isPresent();
	}

	public static String getFileName(String path, String partialName) {
		expandFolder(path);
		StaticElement document = new StaticElement(By.xpath(String.format("//span[contains(text(), '%s')]", partialName)));
		RetryService.run(predicate -> document.isPresent(), () -> {
			BrowserController.get().driver().navigate().refresh();
			return null;
		}, StopStrategies.stopAfterAttempt(15), WaitStrategies.fixedWait(15, TimeUnit.SECONDS));
		return document.getValue();
	}

	public static File downLoadFileByDescription(String path, String description) {
		if (!isTreeExpanded(path)) {
			expandFolder(path);
		}
		StaticElement document = new StaticElement(By.xpath(String.format("//dt[contains(text(), '%s')]//ancestor::span[@class='rf-trn-lbl']/span", description)));
		RetryService.run(predicate -> document.isPresent(), () -> {
			BrowserController.get().driver().navigate().refresh();
			return null;
		}, StopStrategies.stopAfterAttempt(15), WaitStrategies.fixedWait(15, TimeUnit.SECONDS));

		String fileName = document.getValue();
		new Link(By.xpath(String.format("//form[@id='efForm']//span[@class='rf-trn-lbl']/span[.='%s']", fileName))).doubleClick();

		AtomicReference<File> report = new AtomicReference<>();
		RetryService.run(fileExist -> !fileExist.equals(false), () -> report.getAndSet(DownloadsHelper.getFile(fileName).get()).exists(),
				StopStrategies.stopAfterAttempt(5), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));
		return report.get();
	}

	public static void expandFolder(String path) {
		if (isNotOpened()) {
			linkOpenEFolder.click();
		}
		Arrays.asList(path.split("/")).stream().forEach(p ->
				new Link(By.xpath(String.format("//form[@id='efForm']//div[span/span/span[contains(@id, 'efForm:efTree') and .='%s']]/span[1]", p))).click());
	}

	public static StaticElement getLabel(String label) {
		if (isNotOpened()) {
			linkOpenEFolder.click();
		}
		return new StaticElement(By.xpath(String.format("//div[@id='efForm:efTree']//span[@class='rf-trn-lbl']/span[.='%s']", label)));
	}

	public static String makeKeyPath(String... keys) {
		return StringUtils.join(keys, "/");
	}

	private static void executeContextMenu(String path, String operation) {
		if (!isTreeExpanded(path)) {
			expandFolder(path);
		}
		String[] pathParts = path.split("/");
		new Actions(BrowserController.get().driver()).contextClick(getLabel(pathParts[pathParts.length - 1]).getWebElement()).perform();
		BrowserController.get().executeScript("$('.rf-tt.ef-tree-node-tooltip').hide();");
		new Link(By.xpath(String.format("//div[@id='jqContextMenu']//li[.='%s']", operation))).click();
	}

	//TODO vskatulo:  functionality is not full, verify only one layer
	private static boolean isTreeExpanded(String path) {
		String[] pathParts = path.split("/");
		StaticElement lastFolder = new StaticElement(By.xpath(String.format("//form[@id='efForm']//div[span/span/span[contains(@id, 'efForm:efTree') and .='%s']]/span[1]",
				pathParts[pathParts.length - 1])));
		StaticElement expandButton = new StaticElement(By.xpath(String.format("//form[@id='efForm']//div[span/span/span[contains(@id, 'efForm:efTree') and .='%s']]/span[@class='rf-trn-hnd-colps rf-trn-hnd']",
				pathParts[pathParts.length - 1])));
		return lastFolder.isPresent() && !expandButton.isPresent();
	}

	public void addDocument(TestData testData, String path) {
		executeContextMenu(path, EfolderConstants.DocumentOparetions.ADD_DOCUMENT);
		getDefaultView().fill(testData);
	}

	public void addExtDocument(TestData testData, String path) {
		executeContextMenu(path, EfolderConstants.DocumentOparetions.ADD_EXT_DOCUMENT);
		getDefaultView().fill(testData);
	}

	public void renameFile(TestData testData, String path) {
		executeContextMenu(path, EfolderConstants.DocumentOparetions.RENAME);
		getDefaultView().fill(testData);
	}

	public void reindexFile(TestData testData, String path) {
		executeContextMenu(path, EfolderConstants.DocumentOparetions.REINDEX);
		getDefaultView().fill(testData);
	}

	public void retrieveFile(String path) {
		executeContextMenu(path, EfolderConstants.DocumentOparetions.RETRIEVE);
	}
}
