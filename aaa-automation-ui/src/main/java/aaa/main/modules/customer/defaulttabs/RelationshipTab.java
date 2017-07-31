/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.customer.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.pages.summary.CustomerSummaryPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;

// TODO 1) assetlist is protected field. Why is used super.getAssetList() or getAssetlist()
public class RelationshipTab extends DefaultTab {

	public static Button buttonRemoveRelationship = new Button(By.xpath("//a[text()='Remove This Relationship']"));
	public static Button buttonAddRelationship = new Button(By.id("crmForm:addRelationshipBtn"));
	public static Button buttonSearchPartyRelationship = new Button(By.xpath("//a[text()='Search Party Relationship']"));
	public static Button buttonAddNewContactDetails = new Button(By.xpath("//a[text()='Add New Contacts Details']"));

	public static Link linkRelationshipsTogglePanel = new Link(By.xpath("//div[@id='crmForm:newRelationshipsTogglePanel_1:header']//div[@class='rf-cp-lbl-colps']"));

	public static ComboBox comboBoxSelectContactMethod = new ComboBox(By.id("crmForm:contactMethod_0:contactMethodSelect"));

	public static StaticElement labelTaxIdentification = new StaticElement(By.xpath("//div[@id='crmForm:customerRelationshipsTogglePanel']//*[@class= 'rf-cp rf-tgp contactMethodContainer'][2]//span[contains(@id, 'crmForm:taxId')]"));
	public static StaticElement labelBusinessStarted = new StaticElement(By.xpath("//div[@id='crmForm:customerRelationshipsTogglePanel']//*[@class= 'rf-cp rf-tgp contactMethodContainer'][2]//span[contains(@id, 'crmForm:dateStarted')]"));

	public static ComboBox comboBoxRelationshipRole = new ComboBox(By.id("crmForm:generalInfoRight_0_relationshipRole"));

	public RelationshipTab() {
		super(CustomerMetaData.RelationshipTab.class);
	}

	@Override
	public Tab fillTab(TestData td) {
		if (td.containsKey(getMetaKey())) {
			for (TestData testData : td.getTestDataList(getMetaKey())) {
				String keyType = CustomerMetaData.RelationshipTab.TYPE.getLabel();
				String keySearch = CustomerMetaData.RelationshipTab.SEARCH_PARTY_RELATIONSHIP.getLabel();
				String keyDetails = CustomerMetaData.RelationshipTab.CONTACT_DETAILS_TYPE.getLabel();

				if (testData.containsKey(keyType)) {
					if (!buttonRemoveRelationship.isPresent()) {
						buttonAddRelationship.click();
					}

					super.getAssetList().getAsset(keyType, RadioGroup.class).setValue(testData.getValue(keyType));
					if (Page.dialogConfirmation.isPresent()) {
						Page.dialogConfirmation.confirm();
					}
					((AssetList) super.getAssetList()).setValue(testData.mask(keyType, keySearch));

					testData.removeAdjustment(keySearch);
					if (testData.containsKey(keySearch)) {
						buttonSearchPartyRelationship.click();
						CustomerSummaryPage.tableRelationshipPartySearchResult.getRow(1).getCell(11).controls.links.get(ActionConstants.GET).click();
					}

				} else if (testData.containsKey(keyDetails)) {
					comboBoxSelectContactMethod.setValue(testData.getValue(keyDetails));
					buttonAddNewContactDetails.click();

					((AssetList) super.getAssetList()).setValue(testData.mask(keyDetails));

				} else {
					((AssetList) super.getAssetList()).setValue(testData);
				}
			}
		}

		return this;
	}

	@Override
	public Tab submitTab() {
		if (buttonDone.isPresent()) {
			buttonDone.click();
		} else {
			buttonNext.click();
		}
		return this;
	}
}
