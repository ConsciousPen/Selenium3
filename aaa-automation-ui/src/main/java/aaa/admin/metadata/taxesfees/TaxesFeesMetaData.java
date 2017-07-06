/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.metadata.taxesfees;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.AdvancedSelector;

import aaa.admin.modules.taxesfees.strategy.tax.defaulttabs.AddTaxStrategyTab.TaxesFeesAddTaxStrategyRulesGroupAssetList;
import aaa.common.pages.Page;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class TaxesFeesMetaData {

    public static final class AddFeeTaxRegistryTab extends MetaData {
        public static final AttributeDescriptor CODE = declare("Code", TextBox.class);
        public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
        public static final AttributeDescriptor SUBTYPE = declare("Subtype", ComboBox.class);
        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AttributeDescriptor STATUS = declare("Group Type", ComboBox.class);
    }

    public static final class AddFeeStrategyTab extends MetaData {

    }

    public static final class AddFeeTaxStrategyTab extends MetaData {
        public static final AttributeDescriptor PRODUCT_GROUP = declare("Product Group",
                AssetList.class, ProductGroup.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
        public static final AttributeDescriptor RULES_GROUP = declare("Rules Group",
                TaxesFeesAddTaxStrategyRulesGroupAssetList.class, RulesGroup.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

        public static final class ProductGroup extends MetaData {
            public static final AttributeDescriptor PRODUCT = declare("Product", AdvancedSelector.class);
            public static final AttributeDescriptor LOB = declare("LOB", ComboBox.class);
            public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
            public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        }

        public static final class RulesGroup extends MetaData {
            public static final AttributeDescriptor GROUP_DETAILS = declare("Group Details",
                    AssetList.class, RulesGroupDetails.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

            public static final class RulesGroupDetails extends MetaData {
                public static final AttributeDescriptor REGISTRY_TYPE_DESCRIPTION = declare("Registry Type-Description", ComboBox.class);
                public static final AttributeDescriptor ACCOUNTING_CODE = declare("Accounting Code", TextBox.class);
                public static final AttributeDescriptor REFERENCE_CODE = declare("Reference Code", TextBox.class);
                public static final AttributeDescriptor APPLIES_TO_EIS_ENTITY = declare("Applies to EIS Entity", ComboBox.class);
                public static final AttributeDescriptor APPLIES_TO_LEVEL = declare("Applies to Level", ComboBox.class);
                public static final AttributeDescriptor FREQUENCY = declare("Frequency", ComboBox.class);
                public static final AttributeDescriptor APPLIES_TO_LEVEL_SELECT = declare("Applies to Level Select", Button.class);
                public static final AttributeDescriptor REFUNDABLE = declare("Refundable?", RadioGroup.class);
                public static final AttributeDescriptor REFUND_NON_REFUNDABLE_ON_FLAT_CANCEL = declare("Refund non-refundable on Flat Cancel?", RadioGroup.class);
                public static final AttributeDescriptor COUNTRY = declare("Country", AdvancedSelector.class);
                public static final AttributeDescriptor STATE = declare("State", AdvancedSelector.class);
                public static final AttributeDescriptor CITY = declare("City", TextBox.class);
                public static final AttributeDescriptor AMOUNT_DOLLAR = declare("Amount $", TextBox.class,
                        By.id("taxStrategyForm:amountPlain"));
                public static final AttributeDescriptor AMOUNT_PERCENTAGE = declare("Amount %", TextBox.class,
                        By.id("taxStrategyForm:amountPercent"));
                public static final AttributeDescriptor CAPPING_THRESHOLD_DOLLAR = declare("Capping Threshold $", TextBox.class,
                        By.id("taxStrategyForm:ruleInfo_cappingThreshold"));
            }
        }
    }

    public static final class AddFeeGroupTab extends MetaData {
        public static final AttributeDescriptor FEE_GROUP_NAME = declare("Fee Group Name", TextBox.class);
        public static final AttributeDescriptor FEE_GROUP_TYPE = declare("Fee Group Type", ComboBox.class);
        public static final AttributeDescriptor PARTNERS = declare("Partners", AdvancedSelector.class);
        public static final AttributeDescriptor AGENCIES = declare("Agencies", AdvancedSelector.class);
        public static final AttributeDescriptor INDIVIDUALS = declare("Individuals", AdvancedSelector.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    }

    public static final class SearchByField extends MetaData {
        public static final AttributeDescriptor PRODUCT = declare("Product", ComboBox.class);
        public static final AttributeDescriptor LINE_OF_BUSINESS = declare("Line of Business", ComboBox.class);
        public static final AttributeDescriptor ACCOUNTING_CODE = declare("Accounting Code", TextBox.class);
        public static final AttributeDescriptor REFERENCE_CODE = declare("Reference Code", TextBox.class);
        public static final AttributeDescriptor RULE_ID = declare("Rule ID", TextBox.class);
        public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
        public static final AttributeDescriptor STATUS = declare("Status", ComboBox.class);
        public static final AttributeDescriptor CODE = declare("Code", TextBox.class);
        public static final AttributeDescriptor SUBTYPE = declare("Subtype", ComboBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    }
}
