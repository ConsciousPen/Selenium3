/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common;

import aaa.common.components.Dialog;
import aaa.common.pages.Page;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract tab class.
 * 
 * @category Static
 */
public abstract class Tab {

	public static Button buttonOk = new Button(By.xpath("//input[(@value = 'OK' or @value = 'Ok') and contains(@id, '_footer') "
		+ "and not(@class = 'hidden') and not(contains(@class, 'secondaryButton')) and not(contains(@style, 'none')) "
		+ "and not(contains(@id, 'Dialog')) and not(contains(@id, 'serviceCallButtonOk')) and not(ancestor::*[@class='popupButtonsPanel'])]"));
	public static Button buttonCancel = new Button(By.xpath("//*[((@value = 'Cancel') or (text() = 'Cancel')) and (contains(@id, '_footer') or @id='errorsForm:back') "
		+ "and not(@class = 'hidden') and not(contains(@style, 'none')) "
		+ "and not(contains(@id, 'Dialog')) and not(contains(@id, 'serviceCallButtonCancel')) and not(ancestor::*[@class='popupButtonsPanel'])]|//button[contains(., 'Cancel') "
		+ "and not(@class = 'hidden') and not(contains(@style, 'none'))]"));
	public static Button buttonFinish = new Button(By.xpath("//input[@value = 'Finish' and not(@class = 'hidden') and not(contains(@style,'none')) and contains(@id,'_footer')]"));
	public static Button buttonNext = new Button(By
		.xpath("//input[@id='policyDataGatherForm:next_footer' or @id='crmForm:nextBtn_footer' or @id='policyDataGatherForm:next' or @id='policyDataGatherForm:nextButton_footer']"));
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

	public static Link linkAdminApp = new Link(By.id("logoutForm:switchToAdmin"));
	public static Link linkMainApp = new Link(By.id("logoutForm:switchToApp"));

	public static Dialog dialogCancelAction = new Dialog(By.id("cancelConfirmDialogDialog_container"));

	public static StaticElement labelPolicyNumber = new StaticElement(By.xpath("//span[@id = 'policyDataGatherForm:dataGatherHeaderSectionInfo']//td[2]//span"));
	public static StaticElement labelLoggedUser = new StaticElement(By.id("logoutForm:userDetails"));

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

	/**
	 * Fill this tab. Override if tab is filled in non-standard manner (e.g.
	 * there are several asset lists, extra buttons have to be clicked etc.)
	 * 
	 * @param td
	 *            TestData object which may contain another TestData by key
	 *            returned by {@link #getMetaKey()} and possibly other keys.
	 *            Note: normally the same TestData is passed here as in
	 *            {@link products.Workspace.fill()}.
	 */
	public Tab fillTab(TestData td) {
		assetList.fill(td);
		return this;
	}

	public Tab verifyTab(TestData td) {
		if (td.containsKey(assetList.getName())) {
			((AssetList) assetList).verify.someValues(td.getTestData(assetList.getName()));
		} else {
			((AssetList) assetList).verify.someValues(td);
		}
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

	public boolean isEmpty(TestData td) {
		return td.getTestData(metaDataClass.getSimpleName()).getKeys().isEmpty();
	}

	public Tab verifyFieldIsDisplayed(String label) {
		BaseElement<?, ?> control = assetList.getAsset(label);
		CustomAssert.assertTrue("Field '" + label + "' must be displayed but it is not", control.isPresent() && control.isVisible());
		return this;
	}

	public Tab verifyFieldIsNotDisplayed(String label) {
		BaseElement<?, ?> control = assetList.getAsset(label);
		if (control.isPresent()) {
			CustomAssert.assertFalse("Field '" + label + "' must not be displayed but it is", control.isVisible());
		}
		return this;
	}

	public Tab verifyFieldsAreNotDisplayed(String [] labels) {
		List<String> listOfLabels = Arrays.asList(labels);
		for (String label : listOfLabels) {
			verifyFieldIsNotDisplayed(label);
		}
		return this;
	}

	public Tab verifyFieldIsEnabled(String label) {
		CustomAssert.assertTrue("Field '" + label + "' must be enabled but it is not", assetList.getAsset(label).isEnabled());
		return this;
	}

	public Tab verifyFieldIsDisabled(String label) {
		CustomAssert.assertFalse("Field '" + label + "' must be disabled but it is not", assetList.getAsset(label).isEnabled());
		return this;
	}

	public Tab verifyFieldHasValue(String label, String expectedValue) {
		return verifyFieldHasValue(assetList, label, expectedValue);
	}

	public Tab verifyFieldHasValue(AbstractContainer<?, ?> assetList, String label, String expectedValue) {
		String actualValue = assetList.getAsset(label).getValue().toString();
		String errorMessage = String.format("'%s' field's actual value '%s' is not equal to the expected value of '%s'", label, actualValue, expectedValue);
		CustomAssert.assertEquals(errorMessage, expectedValue, actualValue);
		return this;
	}

	public Tab verifyFieldHasValue(AssetDescriptor<BaseElement<?, ?>> attributeDescriptor, String expectedValue) {
		return verifyFieldHasValue(assetList, attributeDescriptor, expectedValue);
	}

	public Tab verifyFieldHasValue(AbstractContainer<?, ?> assetList, AssetDescriptor<BaseElement<?, ?>> attributeDescriptor, String expectedValue) {
		String actualValue = assetList.getAsset(attributeDescriptor.getLabel()).getValue().toString();
		String errorMessage = String.format("'%s' field's actual value '%s' is not equal to the expected value of '%s'", attributeDescriptor.getLabel(), actualValue, expectedValue);
		CustomAssert.assertEquals(errorMessage, expectedValue, actualValue);
		return this;
	}

	public Tab verifyFieldHasNotValue(String label, String expectedValue) {
		String actualValue = assetList.getAsset(label).getValue().toString();
		String errorMessage = String.format("'%s' field's actual value '%s' is not equal to the expected value of '%s'", label, actualValue, expectedValue);
		CustomAssert.assertFalse(errorMessage, expectedValue.equals(actualValue));
		return this;
	}

	public Tab verifyFieldHasMessage(String label, String expectedValue) {
		String actualValue = assetList.getWarning(label).getValue();
		String errorMessage = String.format("'%s' field's actual warning '%s' is not equal to the expected warning of '%s'", label, actualValue, expectedValue);
		CustomAssert.assertEquals(errorMessage, expectedValue, actualValue);
		return this;
	}

	public Tab verifyTabHasBottomMessage(String errorMessage) {
		getBottomWarning().verify.contains(errorMessage);
		return this;
	}

	public StaticElement getBottomWarning() {
		return new StaticElement(By.xpath("//div[@id='contentWrapper']//span[@class='error_message']"));
	}

	public Tab cancel() {
		buttonCancel.click();
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

	public String getPolicyNumber(){
		return labelPolicyNumber.getValue();
	}
}
