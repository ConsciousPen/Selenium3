/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.product;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.ipb.eisa.controls.productfactory.DatePicker;
import com.exigen.ipb.eisa.controls.productfactory.MdCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.ProductFactoryCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFComboBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFTextBox;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.LocatorWaiter;
import toolkit.webdriver.controls.waiters.Waiters;

public final class ProductMetaData {

    public static final class ProductAutomatedProcessingTab extends MetaData {
        public static final AssetDescriptor<RadioGroup> STRATEGY_TYPE = declare("Strategy Type", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> RENEWAL_TYPE = declare("Renewal Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> STRATEGY_ID = declare("Strategy ID", TextBox.class);
        public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
        public static final AssetDescriptor<AdvancedSelector> LINE_OF_BUSINESS = declare("Line of Business", AdvancedSelector.class);
        public static final AssetDescriptor<AdvancedSelector> PRODUCT = declare("Product", AdvancedSelector.class);
        public static final AssetDescriptor<AdvancedSelector> POLICY_FORM = declare("Policy Form", AdvancedSelector.class);
        public static final AssetDescriptor<AdvancedSelector> RISK_STATE = declare("Risk State", AdvancedSelector.class);
        public static final AssetDescriptor<AdvancedSelector> UNDEWRITING_COMPANY = declare("Underwriting Company", AdvancedSelector.class);
        public static final AssetDescriptor<RadioGroup> APPLICABLE_FOR_TERM = declare("Applicable for Term", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> PROCESS_TYPE = declare("Process Type", RadioGroup.class);
        public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AssetDescriptor<ComboBox> TRIGGER_TYPE = declare("Trigger Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> BASIS_FOR_TIME_BASED_CALCULATION = declare("Basis for Time-based Calculation", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> PENDED_TRANSACTION_HANDLING = declare("Pended Transaction Handling", RadioGroup.class);
        public static final AssetDescriptor<TextBox> OF_DAYS_ALLOWED_FOR_RENEWAL_LAPSE = declare("# of Days allowed for Renewal Lapse", TextBox.class);
        public static final AssetDescriptor<TextBox> DO_NOT_PROCESS_OF_DAYS_BEFORE_TERM_EXPIRES = declare("Do not process # of Days before term expires", TextBox.class);
        public static final AssetDescriptor<RadioGroup> RATE_POLICY = declare("Rate Policy?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> GENERATE_DOCS = declare("Generate Docs?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> GENERATE_TASKS = declare("Generate Tasks?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> POLICIES_FLAGGED_FOR_MANUAL_RENEW = declare("Policies flagged for Manual Renew", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> POLICIES_FLAGGED_AS_DO_NOT_RENEW = declare("Policies flagged as Do Not Renew", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> POLICIES_FLAGGED_FOR_CANCEL_NOTICE = declare("Policies flagged for Cancel Notice", RadioGroup.class);
        public static final AssetDescriptor<AdvancedSelector> ADDITIONAL_POLICY_SELECTION_RULES_ACTION = declare("Additional Policy Selection Rules Action", AdvancedSelector.class);
        public static final AssetDescriptor<TextBox> INITIATE_QUOTE = declare("Initiate Quote", TextBox.class);
        public static final AssetDescriptor<TextBox> RUN_REPORTS_SERVICES = declare("Run Reports/Services", TextBox.class);
        public static final AssetDescriptor<TextBox> RATE_QUOTE = declare("Rate Quote", TextBox.class);
        public static final AssetDescriptor<TextBox> OFFER_ISSUE_QUOTE = declare("Offer/Issue Quote", TextBox.class);
    }

    public static final class SearchByField extends MetaData {
        public static final AssetDescriptor<TextBox> STRATEGY_ID = declare("Strategy ID", TextBox.class);
    }

    public static final class NewProductTab extends MetaData {
        public static final AssetDescriptor<TextBox> PRODUCT_CODE = declare("Product code", TextBox.class);
        public static final AssetDescriptor<TextBox> PRODUCT_NAME = declare("Product name", TextBox.class);
        public static final AssetDescriptor<DatePicker> APPLIES_TO_POLICY_EFFECTIVE_DATE = declare("Applies to policy effective date", DatePicker.class);
        public static final AssetDescriptor<DatePicker> APPLIES_TO_ENDORSEMENT_EFFECTIVE_DATE = declare("Applies to endorsement effective date", DatePicker.class);
        public static final AssetDescriptor<ComboBox> BROAD_LINE_OF_BUSINESS = declare("Broad line of business", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LINE_OF_BUSINESS = declare("Line of business", ComboBox.class);
        public static final AssetDescriptor<ComboBox> POLICY_TERM_TYPE = declare("Policy term type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> POLICY_CHANGES_ALLOWED_AT = declare("Policy changes allowed at", ComboBox.class);
        public static final AssetDescriptor<ComboBox> RENEWAL_TYPES_ALLOWED = declare("Renewal types allowed", ComboBox.class);
        public static final AssetDescriptor<TextBox> PRODUCT_DESCRIPTION = declare("Product description", TextBox.class);
    }

    public static final class HomeTab extends MetaData {
        public static final AssetDescriptor<PFButton> BUTTON_ACTIVATE = declare("Button Activate", PFButton.class, Waiters.DEFAULT, false, By.xpath("//button[@title='Activate']"));
        public static final AssetDescriptor<PFButton> BUTTON_DEACTIVATE =
                declare("Button Deactivate", PFButton.class, Waiters.DEFAULT, false, By.xpath("//button[@title='Deactivate']"));
    }

    public static final class PropertiesTab extends MetaData {}

    public static final class ActionsTab extends MetaData {}

    public static final class ComponentsTab extends MetaData {
        public static final AssetDescriptor<PFTextBox> REFERENCE_NAME = declare("Reference name", PFTextBox.class,
                By.id("component-reference:name"));
        public static final AssetDescriptor<ProductFactoryCheckBox> REQUIRED = declare("Required", ProductFactoryCheckBox.class,
                By.xpath("//input[@id='manage-attribute-validation:required']"));
        public static final AssetDescriptor<PFButton> BUTTON_SET_PROPERTIES = declare("Button Set Properties", PFButton.class, Waiters.SLEEP, false,
                By.id("manage-component-properties:manage"));
        public static final AssetDescriptor<PFTextBox> COVERAGE_CD = declare("CoverageCd", PFTextBox.class,
                By.xpath("//label[.='CoverageCd']//following-sibling::input"));
        public static final AssetDescriptor<PFButton> BUTTON_SAVE = declare("Button Save", PFButton.class, By.id("component-properties:save"));

        public static final class CreateAttribute extends MetaData {
            public static final AssetDescriptor<PFTextBox> ATTRIBUTE_NAME = declare("Attribute name", PFTextBox.class, By.id("attribute-create:name"));
            public static final AssetDescriptor<PFTextBox> ATTRIBUTE_LABEL = declare("Attribute label", PFTextBox.class, By.id("attribute-create:label"));
            public static final AssetDescriptor<PFComboBox> DATA_TYPE = declare("Data type", PFComboBox.class, By.id("attribute-create:type"));
        }
    }

    public static final class WorkspacesTab extends MetaData {
        public static final AssetDescriptor<PFButton> BUTTON_ADD_NEW_TAB = declare("Button Add New Tab", PFButton.class, new LocatorWaiter(By.id("tab-add:addBtn")).then(Waiters.SLEEP(1000)),
                By.id("tree-bar-right:addTab"));
        public static final AssetDescriptor<PFTextBox> TAB_NAME = declare("Tab Name", PFTextBox.class,
                By.xpath("//input[@id='tab-add:name']"));
        public static final AssetDescriptor<PFButton> BUTTON_DONE_ADD_NEW_TAB = declare("Button Done Add New Tab", PFButton.class, By.id("tab-add:addBtn"));
        public static final AssetDescriptor<PFTextBox> ASSIGN_COMPONENT_REFERENCE_NAME = declare("Assign Component Reference Name", PFTextBox.class,
                By.id("quick-search:quick-search-field"));
        public static final AssetDescriptor<PFButton> BUTTON_ASSIGN_COMPONENT = declare("Button Assign Component", PFButton.class,
                By.id("quick-search:quick-add"));
    }

    public static final class ConsolidationTab extends MetaData {}

    public static final class TemplatesTab extends MetaData {}

    public static final class DecisionTablesTab extends MetaData {}

    public static final class RulesTab extends MetaData {
        public static final class RuleSearch extends MetaData {
            public static final AssetDescriptor<PFTextBox> RULE_NAME = declare("Rule name", PFTextBox.class, By.xpath("//input[@name='name']"));
        }
        public static final class RuleProperties extends MetaData {
            public static final AssetDescriptor<PFTextBox> RULE_NAME = declare("Rule name", PFTextBox.class, By.xpath("//input[@name='name']"));
            public static final AssetDescriptor<PFTextBox> APPLIED_TO = declare("Applied to", PFTextBox.class, By.xpath("//input[@name='appliedTo']"));
            public static final AssetDescriptor<PFTextBox> DESCRIPTION = declare("Description", PFTextBox.class, By.xpath("//textarea[@name='description']"));
            public static final AssetDescriptor<PFTextBox> ERROR_MEAASGE = declare("Error Meaasge", PFTextBox.class, By.xpath("//textarea[@name='errorMessage']"));
            public static final AssetDescriptor<MdCheckBox> DISABLE_RULE = declare("Disable rule", MdCheckBox.class,
                    By.xpath("//md-checkbox[@aria-label='Disable rule']"));
            public static final AssetDescriptor<MdCheckBox> GLOBAL = declare("Global", MdCheckBox.class,
                    By.xpath("//md-checkbox[@aria-label='Global']"));
            public static final AssetDescriptor<PFTextBox> ASSERTION_EXPRESSION = declare("Assertion expression", PFTextBox.class, By.xpath("//div[@name='assertionExpression']//textarea"));
            public static final AssetDescriptor<PFTextBox> CONDITION_EXPRESSION = declare("Condition expression", PFTextBox.class, By.xpath("//div[@name='conditionExpression']//textarea"));
            public static final AssetDescriptor<PFTextBox> DEFAULT_VALUE_EXPRESSION = declare("Default value expression", PFTextBox.class,
                    By.xpath("//div[@name='defaultValueExpression']//textarea"));
        }
    }
    public static final class RuleSetsTab extends MetaData {}

    public static final class ProductProductFactorySearch extends MetaData {
        public static final AssetDescriptor<TextBox> STRATEGY_ID = declare("Strategy ID", TextBox.class);
        public static final AssetDescriptor<TextBox> PRODUCT_NAME = declare("Product Name", TextBox.class);
        public static final AssetDescriptor<TextBox> PRODUCT_CODE = declare("Product Code", TextBox.class);
        public static final AssetDescriptor<ComboBox> LINE_OF_BUSINESS = declare("Line of Business", ComboBox.class);
        public static final AssetDescriptor<ComboBox> BROAD_LINE_OF_BUSINESS = declare("Broad Line of Business", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PRODUCT_TYPE = declare("Product Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
    }

    public static final class ProductProductFactorySelectProductType extends MetaData {
        public static final AssetDescriptor<ComboBox> PRODUCT_TYPE = declare("Product Type", ComboBox.class, By.id("productTypeSelectForm:selectedNewProductType"));
        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                By.id("productTypeSelectForm:productTypeSelectFormSubmit"));
    }

    public static final class ProductProductFactoryCopy extends MetaData {
        public static final AssetDescriptor<TextBox> NEW_PRODUCT_CODE = declare("New product code", TextBox.class);
        public static final AssetDescriptor<TextBox> NEW_PRODUCT_NAME = declare("New product name", TextBox.class);
        public static final AssetDescriptor<TextBox> APPLIES_TO_PRODUCT_EFFECTIVE_DATE = declare("Applies to product effective date", TextBox.class);
        public static final AssetDescriptor<TextBox> APPLIES_TO_ENDORSEMENT_EFFECTIVE_DATE = declare("Applies to endorsement effective date", TextBox.class);

        public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                By.id("copyProductForm:copyProductOk"));
    }
}
