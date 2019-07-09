/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.metadata.taxesfees;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.AdvancedSelector;
import aaa.admin.modules.taxesfees.strategy.tax.defaulttabs.AddTaxStrategyTab.TaxesFeesAddTaxStrategyRulesGroupAssetList;
import aaa.common.pages.Page;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class TaxesFeesMetaData {

	public static final class AddFeeTaxRegistryTab extends MetaData {
		public static final AssetDescriptor<TextBox> CODE = declare("Code", TextBox.class);
		public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SUBTYPE = declare("Subtype", ComboBox.class);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATUS = declare("Group Type", ComboBox.class);
	}

	public static final class AddFeeStrategyTab extends MetaData {

	}

	public static final class AddFeeTaxStrategyTab extends MetaData {
		public static final AssetDescriptor<AssetList> PRODUCT_GROUP = declare("Product Group", AssetList.class, ProductGroup.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<TaxesFeesAddTaxStrategyRulesGroupAssetList> RULES_GROUP = declare("Rules Group", TaxesFeesAddTaxStrategyRulesGroupAssetList.class, RulesGroup.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

		public static final class ProductGroup extends MetaData {
			public static final AssetDescriptor<AdvancedSelector> PRODUCT = declare("Product", AdvancedSelector.class);
			public static final AssetDescriptor<ComboBox> LOB = declare("LOB", ComboBox.class);
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
			public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		}

		public static final class RulesGroup extends MetaData {
			public static final AssetDescriptor<AssetList> GROUP_DETAILS = declare("Group Details", AssetList.class, RulesGroupDetails.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

			public static final class RulesGroupDetails extends MetaData {
				public static final AssetDescriptor<ComboBox> REGISTRY_TYPE_DESCRIPTION = declare("Registry Type-Description", ComboBox.class);
				public static final AssetDescriptor<TextBox> ACCOUNTING_CODE = declare("Accounting Code", TextBox.class);
				public static final AssetDescriptor<TextBox> REFERENCE_CODE = declare("Reference Code", TextBox.class);
				public static final AssetDescriptor<ComboBox> APPLIES_TO_EIS_ENTITY = declare("Applies to EIS Entity", ComboBox.class);
				public static final AssetDescriptor<ComboBox> APPLIES_TO_LEVEL = declare("Applies to Level", ComboBox.class);
				public static final AssetDescriptor<ComboBox> FREQUENCY = declare("Frequency", ComboBox.class);
				public static final AssetDescriptor<Button> APPLIES_TO_LEVEL_SELECT = declare("Applies to Level Select", Button.class);
				public static final AssetDescriptor<RadioGroup> REFUNDABLE = declare("Refundable?", RadioGroup.class);
				public static final AssetDescriptor<RadioGroup> REFUND_NON_REFUNDABLE_ON_FLAT_CANCEL = declare("Refund non-refundable on Flat Cancel?", RadioGroup.class);
				public static final AssetDescriptor<AdvancedSelector> COUNTRY = declare("Country", AdvancedSelector.class);
				public static final AssetDescriptor<AdvancedSelector> STATE = declare("State", AdvancedSelector.class);
				public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
				public static final AssetDescriptor<TextBox> AMOUNT_DOLLAR = declare("Amount $", TextBox.class, By.id("taxStrategyForm:amountPlain"));
				public static final AssetDescriptor<TextBox> AMOUNT_PERCENTAGE = declare("Amount %", TextBox.class, By.id("taxStrategyForm:amountPercent"));
				public static final AssetDescriptor<TextBox> CAPPING_THRESHOLD_DOLLAR = declare("Capping Threshold $", TextBox.class, By.id("taxStrategyForm:ruleInfo_cappingThreshold"));
			}
		}
	}

	public static final class AddFeeGroupTab extends MetaData {
		public static final AssetDescriptor<TextBox> FEE_GROUP_NAME = declare("Fee Group Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> FEE_GROUP_TYPE = declare("Fee Group Type", ComboBox.class);
		public static final AssetDescriptor<AdvancedSelector> PARTNERS = declare("Partners", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> AGENCIES = declare("Agencies", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> INDIVIDUALS = declare("Individuals", AdvancedSelector.class);
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
	}

	public static final class SearchByField extends MetaData {
		public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class);
		public static final AssetDescriptor<ComboBox> LINE_OF_BUSINESS = declare("Line of Business", ComboBox.class);
		public static final AssetDescriptor<TextBox> ACCOUNTING_CODE = declare("Accounting Code", TextBox.class);
		public static final AssetDescriptor<TextBox> REFERENCE_CODE = declare("Reference Code", TextBox.class);
		public static final AssetDescriptor<TextBox> RULE_ID = declare("Rule ID", TextBox.class);
		public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
		public static final AssetDescriptor<TextBox> CODE = declare("Code", TextBox.class);
		public static final AssetDescriptor<ComboBox> SUBTYPE = declare("Subtype", ComboBox.class);
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
	}
}
