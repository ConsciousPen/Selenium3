/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.components;

import org.mortbay.log.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.pagefactory.ByChained;
import aaa.common.metadata.SearchMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.collection.Controls;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class Dialog {

	public Button buttonYes;
	public Button buttonNo;
	public Button buttonOk;
	public Button buttonCancel;
	public Button buttonNext;
	public Button buttonProceed;
	public StaticElement labelHeader;
	public StaticElement labelMessage;
	public Controls controls;
	public Button buttonDeleteEndorsement;
	public Button buttonCloseWithCross;
	public Button buttonRequestApproval;
	private By locator;

	public Dialog(String dialogLocator) {
		this(By.xpath(dialogLocator));
	}

	public Dialog(By dialogLocator) {
		locator = dialogLocator;
		controls = new Controls(dialogLocator);

		buttonYes = new Button(new ByChained(locator, By.xpath(".//*[@value='Yes' or @value='YES' or text()='Yes' or text()='YES']")));
		buttonNo = new Button(new ByChained(locator, By.xpath(".//*[@value='No' or @value='NO' or text()='No' or text()='NO']")));
		buttonOk = new Button(new ByChained(locator, By.xpath(".//*[@value='Ok' or @value='OK' or text()='Ok' or text()='OK']")));
		buttonCancel = new Button(new ByChained(locator, By.xpath(".//*[@value='Cancel' or @value='CANCEL' or text()='Cancel' or text()='CANCEL']")));
		buttonNext = new Button(new ByChained(locator, By.xpath(".//*[@value='Next' or text()='Next']")));
		buttonProceed = new Button(new ByChained(locator, By.xpath(".//*[@value='Proceed' or text()='Proceed']")));
		buttonDeleteEndorsement = new Button(new ByChained(locator, By.xpath(".//*[@value='Delete Endorsement']")));
		buttonCloseWithCross = new Button(new ByChained(locator, By.xpath(".//*[@id='policyDataGatherForm:installmentFeeDetailsPopup_header_controls' or @id='purchaseForm:installmentFeeDetailsPopup_header_controls']")));
		buttonRequestApproval = new Button(new ByChained(locator, By.xpath(".//*[@value='Request Approval']")));

		labelHeader = new StaticElement(new ByChained(locator, By.xpath(".//div[contains(@id, '_header_content')]")));
		labelMessage = new StaticElement(new ByChained(locator, By.xpath(".//div[contains(@id, '_content_scroller') or contains(@class,'content')]"
			+ "//*[contains(@id, 'Message') or contains(@class, 'textBold') or contains(@class, 'message')]")));

	}

	public boolean isPresent() {
		return new StaticElement(locator).isPresent();
	}

	public boolean isVisible() {
		try{ return new StaticElement(locator).isVisible();}
		catch(NoSuchElementException ex){
			Log.warn("DIALOG.ISVISIBLE() !! NoSuchElementException !!");
			return false;
		}
	}

	public By getLocator() {
		return locator;
	}

	public void confirm() {
		new Button(new ByChained(locator, By.xpath(".//*[text()='Yes' or text()='YES' or text()='Ok' or text()='OK' or text()='Confirm' or text()='Proceed' or text()='PROCEED' or @text()='Continue'"
			+ "or @value='Yes' or @value='YES' or @value='Ok' or @value='OK' or @value='Confirm' or @value='CONFIRM' or @value='Proceed' or @value='PROCEED' or @value='Accept & continue' or @value='Agree' or @value='Continue']"))).click();
	}

	public void reject() {
		new Button(new ByChained(locator, By.xpath(".//*[@value='Cancel' or @value='CANCEL' or @value='No' or @value='NO' or text()='Cancel' or text()='CANCEL' or text()='No' or text()='NO']")))
			.click();
	}

	public void fillAssetList(TestData testData, String nameOfAssetTypeRange) {
		new AssetList(locator, SearchMetaData.DialogSearch.class).setValue(testData.getTestData(nameOfAssetTypeRange));
	}
}
