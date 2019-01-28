/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.metadata.product;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class MoratoriumMetaData {

	public static final class AddMoratoriumTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> MORATORIUM_TYPE = declare("Moratorium Type", RadioGroup.class);
		public static final AssetDescriptor<TextBox> MORATORIUM_NAME = declare("Moratorium Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MORATORIUM_REASON = declare("Moratorium Reason", TextBox.class);
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		public static final AssetDescriptor<DialogAssetList> SELECT_CATASTROPHE = declare("Select Catastrophe", DialogAssetList.class, SelectCatastrophe.class);
		public static final AssetDescriptor<AdvancedSelector> COUNTRIES = declare("Countries", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> STATES = declare("States", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> CITIES = declare("Cities", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> ZIP_CODES = declare("Zip Codes", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> AGENCIES = declare("Agencies", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> AGENTS = declare("Agents", AdvancedSelector.class);
		public static final AssetDescriptor<ComboBox> LOB = declare("Lob", ComboBox.class);
		public static final AssetDescriptor<AdvancedSelector> PRODUCTS = declare("Products", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> BRANDS = declare("Brands", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> POLICY_FORM = declare("Policy Form", AdvancedSelector.class);
		public static final AssetDescriptor<AssetList> ADD_RULE_SECTION = declare("AddRuleSection", AssetList.class, AddRuleSection.class);

		public static final class AddRuleSection extends MetaData {
			public static final AssetDescriptor<AssetList> ADD_ADDITIONAL_EXPOSURE_CAP_SECTION = declare("AddAdditionalExposureCapSection", AssetList.class, AddAdditionalExposureCapSection.class);
			public static final AssetDescriptor<ComboBox> NEW_BUSINESS_INITIATION = declare("New Business Initiation", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:0:initiationActionType"));
			public static final AssetDescriptor<ComboBox> NEW_BUSINESS_DATA_GATHERING = declare("New Business Data Gathering", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:0:dataGatherActionType"));
			public static final AssetDescriptor<ComboBox> NEW_BUSINESS_DATA_GATHERING_LEVEL = declare("New Business Data Gathering Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:1:dataGatherUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> NEW_BUSINESS_PREMIUM_CALCULATION = declare("New Business Premium Calculation", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:0:premiumCalculationActionType"));
			public static final AssetDescriptor<ComboBox> NEW_BUSINESS_BIND = declare("New Business Bind", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:0:bindActionType"));
			public static final AssetDescriptor<ComboBox> NEW_BUSINESS_BIND_LEVEL = declare("New Business Bind Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:0:bindUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> NEW_BUSINESS_ISSUE = declare("New Business Issue", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:0:issueActionType"));
			public static final AssetDescriptor<ComboBox> NEW_BUSINESS_ISSUE_LEVEL = declare("New Business Issue Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:0:issueUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> ENDORSEMENT_INITIATION = declare("Endorsement Initiation", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:1:initiationActionType"));
			public static final AssetDescriptor<ComboBox> ENDORSEMENT_DATA_GATHERING = declare("Endorsement Data Gathering", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:1:dataGatherActionType"));
			public static final AssetDescriptor<ComboBox> ENDORSEMENT_PREMIUM_CALCULATION = declare("Endorsement Premium Calculation", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:1:premiumCalculationActionType"));
			public static final AssetDescriptor<ComboBox> ENDORSEMENT_BIND = declare("Endorsement Bind", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:1:bindActionType"));
			public static final AssetDescriptor<ComboBox> ENDORSEMENT_BIND_LEVEL = declare("Endorsement Bind Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:1:bindUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> ENDORSEMENT_ISSUE = declare("Endorsement Issue", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:1:issueActionType"));
			public static final AssetDescriptor<ComboBox> ENDORSEMENT_ISSUE_LEVEL = declare("Endorsement Issue Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:1:issueUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> RENEWAL_INITIATION = declare("Renewal Initiation", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:2:initiationActionType"));
			public static final AssetDescriptor<ComboBox> RENEWAL_DATA_GATHERING = declare("Renewal Data Gathering", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:2:dataGatherActionType"));
			public static final AssetDescriptor<ComboBox> RENEWAL_PREMIUM_CALCULATION = declare("Renewal Premium Calculation", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:2:premiumCalculationActionType"));
			public static final AssetDescriptor<ComboBox> RENEWAL_BIND = declare("Renewal Bind", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:2:bindActionType"));
			public static final AssetDescriptor<ComboBox> RENEWAL_BIND_LEVEL = declare("Renewal Bind Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:2:bindUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> RENEWAL_ISSUE = declare("Renewal Issue", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:2:issueActionType"));
			public static final AssetDescriptor<ComboBox> RENEWAL_ISSUE_LEVEL = declare("Renewal Issue Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:2:issueUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> REWRITE_INITIATION = declare("Rewrite Initiation", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:3:initiationActionType"));
			public static final AssetDescriptor<ComboBox> REWRITE_DATA_GATHERING = declare("Rewrite Data Gathering", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:3:dataGatherActionType"));
			public static final AssetDescriptor<ComboBox> REWRITE_DATA_GATHERING_LEVEL = declare("Rewrite Data Gathering Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:3:dataGatherUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> REWRITE_PREMIUM_CALCULATION = declare("Rewrite Premium Calculation", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:3:premiumCalculationActionType"));
			public static final AssetDescriptor<ComboBox> REWRITE_BIND = declare("Rewrite Bind", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:3:bindActionType"));
			public static final AssetDescriptor<ComboBox> REWRITE_ISSUE = declare("Rewrite Issue", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:3:issueActionType"));
			public static final AssetDescriptor<ComboBox> REWRITE_ISSUE_LEVEL = declare("Rewrite Issue Level", ComboBox.class, By.id("moratoriumRegistryForm:ruleQuoteEditTable_-1:3:issueUwAuthorityLevel"));
			public static final AssetDescriptor<ComboBox> REINSTATE = declare("Reinstate", ComboBox.class, By.id("moratoriumRegistryForm:rulePolicyEditTable_-1:0:actionTypeReinstatement"));
			public static final AssetDescriptor<ComboBox> ROLL_BACK_ENDORSEMENT = declare("Roll Back Endorsement", ComboBox.class, By.id("moratoriumRegistryForm:rulePolicyEditTable_-1:0:actionTypeRollBackEndrosement"));
			public static final AssetDescriptor<ComboBox> ROLL_BACK_ENDORSEMENT_LEVEL = declare("Roll Back Endorsement Level", ComboBox.class, By.id("moratoriumRegistryForm:rulePolicyEditTable_-1:0:rollBackEndorsementUwAuthorityLevel"));
			public static final AssetDescriptor<TextBox> DISPLAY_MESSAGE = declare("Display Message", TextBox.class);

			public static final class AddAdditionalExposureCapSection extends MetaData {
				public static final AssetDescriptor<ComboBox> CURRENCY_TYPE = declare("Currency Type", ComboBox.class, By.id("moratoriumRegistryForm:exposureCapTable:0:currencyTypeSelection"));
				public static final AssetDescriptor<TextBox> EXPOSURE_CAP_AMOUNT = declare("Exposure Cap Amount", TextBox.class, By.id("moratoriumRegistryForm:exposureCapTable:0:amount"));
			}
		}

		public static class SelectCatastrophe extends MetaData {
			public static final AssetDescriptor<Button> SEARCH = declare("Search", Button.class, By.id("searchForm_catatropheSingleSelect:executeSearch_catatropheSingleSelect"));
			public static final AssetDescriptor<Link> SELECT_CATASTROPHE = declare("Select Catatrophe", Link.class, By.xpath("//select[@id='searchForm_catatropheSingleSelect:available_item_catatropheSingleSelect']/option[not(contains(text(), 'update'))]"));

			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By.id("moratoriumRegistryForm:selectCatastrophies"));
			public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false, By.id("searchForm_catatropheSingleSelect:save_single_catatropheSingleSelect"));
		}
	}

	public static final class SearchMetaData extends MetaData {
		public static final AssetDescriptor<TextBox> MORATORIUM_NAME = declare("Moratorium Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> LOB = declare("LOB", ComboBox.class);
		public static final AssetDescriptor<AdvancedSelector> PRODUCT = declare("Product", AdvancedSelector.class);
		public static final AssetDescriptor<ComboBox> BRAND = declare("Brand", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MORATORIUM_TYPE = declare("Moratorium Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
		public static final AssetDescriptor<ComboBox> CITY = declare("City", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
	}
}
