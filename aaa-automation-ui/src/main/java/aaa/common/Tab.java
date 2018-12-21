/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common;

import aaa.common.components.Dialog;
import aaa.common.pages.Page;
import aaa.main.metadata.DialogsMetaData;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstract tab class.
 *
 * @category Static
 */
public abstract class Tab {
	public static final String COVERAGES_CONFIGURATION_NAME = "CoveragesAndPremium";

	public static Button buttonOk = new Button(By.xpath("//input[@id='genericForm:ok' or (@value = 'OK' or @value = 'Ok') and contains (@id, '_footer')"
			+ "and not(@class = 'hidden') and not(contains(@class, 'secondaryButton')) and not(contains(@style, 'none')) "
			+ "and not(contains(@id, 'Dialog')) and not(contains(@id, 'serviceCallButtonOk')) and not(ancestor::*[@class='popupButtonsPanel'])]"));
	public static Button buttonCancel = new Button(By.xpath("//*[((@value = 'Cancel') or (text() = 'Cancel')) and (contains(@id, '_footer') or @id='errorsForm:back') "
			+ "and not(@class = 'hidden') and not(contains(@style, 'none')) "
			+ "and not(contains(@id, 'Dialog')) and not(contains(@id, 'serviceCallButtonCancel')) and not(ancestor::*[@class='popupButtonsPanel'])]|//button[contains(., 'Cancel') "
			+ "and not(@class = 'hidden') and not(contains(@style, 'none'))]"));
	public static Button buttonFinish = new Button(By.xpath("//input[@value = 'Finish' and not(@class = 'hidden') and not(contains(@style,'none')) and contains(@id,'_footer')]"));
	public static Button buttonNext = new Button(By
			.xpath("//input[(@id='policyDataGatherForm:next_footer' or @id='crmForm:nextBtn_footer' or @id='policyDataGatherForm:next' or @id='policyDataGatherForm:nextButton_footer' or @id='policyDataGatherForm:nextInquiry_footer' or @id='policyDataGatherForm:nextInquiryButton_footer') and @onclick != '']"));
	public static Button buttonSave = new Button(By.xpath("//input[@value = 'Save' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Button buttonBack = new Button(By.xpath("//input[(@value = 'Back' or @value = 'BACK') and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Button buttonTopSave = new Button(By.id("topSaveLink"));
	public static Button buttonTopCancel = new Button(By.id("topCancelLink"));
	public static Button buttonContinue = new Button(By.xpath("//input[@value = 'Continue' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Button buttonDone = new Button(By.xpath("//input[@value = 'Done' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Button buttonSaveAndFinalize = new Button(By.xpath("//input[@value='Save and Finalize']"));
	public static Button buttonSaveAndExit = new Button(By.id("topSaveAndExitLink"));
	public static Button buttonYes = new Button(By.xpath("//input[(@value = 'Yes' or @value = 'YES') and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Button buttonGo = new Button(By.xpath("//input[(@value = 'Go' or @value = 'GO') and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Button buttonCreateVersion = new Button(By.id("topCreateQuoteVersionLink"));
	public static Button buttonSubmit = new Button(By.xpath("//input[@value = 'Submit' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
	public static Dialog dialogCancelAction = new Dialog(By.id("cancelConfirmDialogDialog_container"));

	public static StaticElement labelStatus =
			new StaticElement(By.xpath("//span[@id = 'policyDataGatherForm:dataGatherHeaderSectionInfo']//td[contains(text(), 'Status') or contains(text(), 'Status')]//span"));
	public static StaticElement labelPolicyNumber =
			new StaticElement(By.xpath("//span[@id = 'policyDataGatherForm:dataGatherHeaderSectionInfo']//td[contains(text(), 'Policy #') or contains(text(), 'Quote #')]//span"));
	public static StaticElement labelEffDate =
			new StaticElement(By.xpath("//span[@id = 'policyDataGatherForm:dataGatherHeaderSectionInfo']//td[contains(text(), 'Eff. Date') or contains(text(), 'Eff. Date')]//span"));
	public static StaticElement labelForConversionPolicy = new StaticElement(By.xpath("//span[@id = 'policyDataGatherForm:dataGatherHeaderSectionInfo']//td[3]//span"));

	public static StaticElement labelLoggedUser = new StaticElement(By.id("logoutForm:userDetails"));

	public static TextBox createVersionDescription = new TextBox(By.xpath("//input[@id='quoteVersionCreationPopupForm:quoteVersionDescription']"));
	public DialogAssetList moratoriumOverrideDialog = new DialogAssetList(By.id("policyDataGatherForm:actionValidationPopup_container"), DialogsMetaData.MoratoriumOverrideDialog.class);
	protected AbstractContainer<?, ?> assetList;
	protected InquiryAssetList inquiryAssetList;

	protected Class<? extends MetaData> metaDataClass;

	protected Tab(Class<? extends MetaData> mdClass) {
		metaDataClass = mdClass;
		assetList = new AssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
		inquiryAssetList = new InquiryAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
	}

	/**
	 * Get name of metadata associated with this tab
	 *
	 * @return metadata name
	 */
	public String getMetaKey() {
		return assetList.getName();
	}

	/**
	 * Get asset list of this tab in Inquiry Mode
	 *
	 * @return inquiry asset list
	 */
	public InquiryAssetList getInquiryAssetList() {
		return inquiryAssetList;
	}

	/**
	 * Get default asset list of this tab
	 *
	 * @return asset list
	 */
	public AbstractContainer<?, ?> getAssetList() {
		return assetList;
	}

	public StaticElement getBottomWarning() {
		return new StaticElement(By.xpath("//div[@id='contentWrapper']//span[@class='error_message']"));
	}

	/**
	 * Returns either a Quote or Policy Number from the tab header.
	 * @return
	 */
	public String getPolicyNumber() {
		return labelPolicyNumber.getValue();
	}

	/**
	 * @return String Quote/Policy status from tab header
	 */
	public String getPolicyStatus() {
		return labelStatus.getValue();
	}

	/**
	 * Return the effective date from the tab header
	 * @return LocalDateTime effective date
	 */
	public LocalDateTime getEffectiveDate() {
		return TimeSetterUtil.getInstance().parse(labelEffDate.getValue(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

	public String getPolicyNumberForConversion() {
		return labelForConversionPolicy.getValue();
	}

	/**
	 * Fill this tab. Override if tab is filled in non-standard manner (e.g.
	 * there are several asset lists, extra buttons have to be clicked etc.)
	 *
	 * @param td
	 *            TestData object which may contain another TestData by key
	 *            returned by {@link #getMetaKey()} and possibly other keys.
	 *            Note: normally the same TestData is passed here as in
	 *            {@link Workspace#fill(TestData)}.
	 */

	public Tab fillTab(TestData td) {
		hideHeader();
		assetList.fill(td);
		showHeader();
		return this;
	}

	/**
	 * Finish filling the tab. By default is a NOOP. For multi-tab workspaces it
	 * should click "Next" button, but sometimes a different button is used or
	 * none at all. Override if some button has to be clicked to finalize
	 * filling the tab.
	 */
	public Tab submitTab() {
		return this;
	}

	public Tab cancel() {
		return cancel(false);
	}

	public Tab cancel(boolean confirmDialog) {
		buttonCancel.click();
		if (confirmDialog && Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
			Page.dialogConfirmation.confirm();
		}
		return this;
	}

	public Tab saveAndExit() {
		buttonSaveAndExit.click();
		if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
			new TextBox(By.xpath("//textarea[@id='policyDataGatherForm:newbusinessnotes']")).setValue("save as incomplete");
			Page.dialogConfirmation.confirm();
		}
		return this;
	}

	public Tab back() {
		buttonBack.click();
		return this;
	}

	public Tab save() {
		buttonSave.click();
		return this;
	}

	public Tab createVersion() {
		buttonCreateVersion.click();
		if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
			createVersionDescription.setValue("new version");
			Page.dialogConfirmation.confirm();
		}
		return this;
	}

	protected Tab hideHeader() {
		BrowserController.get().executeScript("$(\'#headerForm\').hide();");
		return this;
	}

	protected Tab showHeader() {
		BrowserController.get().executeScript("$(\'#headerForm\').show();");
		return this;
	}

	/**
	 * Verifies is section label presence in AssetList (form).
	 * Note: Search by text will be performed in Asset's forms only.
	 *
	 * @param sectionName - section to check (can be @id->sectionName)
	 */
	public boolean isSectionPresent(String sectionName) {
		String xPath = String.format("//span[@class='componentViewPanelHeader']//label[text()='%s']", sectionName);
		String xPath2 = String.format("//span[text()='%s']", sectionName);
		String xPath3 = String.format("//span[text()=\"%s\"]", sectionName); //for ' in the text
		boolean present = isElementPresent(xPath) || isElementPresent(xPath2) || isElementPresent(xPath3);
		if (sectionName.contains("->")) {
			String[] query = sectionName.split("\\->");
			xPath = String.format("//*[@id='%1$s']", query[0]) + String.format("//label[text()='%s']", query[1]);
			present = isElementPresent(xPath) || isElementPresent(xPath2) || isElementPresent(xPath3);
		}
		return present;
	}

	private static boolean isElementPresent(String xPath) {
		try {
			return BrowserController.get().driver().findElement(By.xpath(xPath)).isDisplayed();
		} catch (RuntimeException ignored) {
			return false;
		}
	}

	public boolean isFieldThatIsNotInAssetListIsPresent(String label) {
		return WebDriverHelper.getControlsWithText(label).size() > 0;
	}
}
