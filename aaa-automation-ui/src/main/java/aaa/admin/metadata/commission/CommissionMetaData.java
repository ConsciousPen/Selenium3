/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.commission;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.AdvancedSelector;
import com.exigen.ipb.etcsa.controls.dialog.DialogMultiSelector;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class CommissionMetaData {

    public static final class AddCommissionGroup extends MetaData {
        public static final AttributeDescriptor GROUP_NAME = declare("Group Name", TextBox.class);
        public static final AttributeDescriptor GROUP_TYPE = declare("Group Type", RadioGroup.class);
        public static final AttributeDescriptor PARTNERS = declare("Partners", DialogMultiSelector.class, AddPartners.class);
        public static final AttributeDescriptor AGENCIES = declare("Agencies", DialogMultiSelector.class, AddAgencies.class);
        public static final AttributeDescriptor DIRECT = declare("Direct", DialogMultiSelector.class, AddDirect.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);

        public static final class AddPartners extends MetaData {
            public static final AttributeDescriptor PARTNER_NAME = declare("Partner Name", TextBox.class);
            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class,
                    Waiters.DEFAULT, false, By.id("groupEditForm:selectPartnersBtn"));
            public static final AttributeDescriptor BUTTON_SUBMIT_POPUP =
                    declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false, By.id("selectPartnersForm:popupSelectBtn"));
        }

        public static final class AddAgencies extends MetaData {
            public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
            public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
            public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class);
            public static final AttributeDescriptor CITY = declare("City", TextBox.class);
            public static final AttributeDescriptor STATE = declare("State", TextBox.class);
            public static final AttributeDescriptor INCLUDE_INACTIVE = declare("Include Inactive", CheckBox.class);
            public static final AttributeDescriptor BUTTON_OPEN_POPUP =
                    declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By.id("groupEditForm:selectAgenciesBtn"));
            public static final AttributeDescriptor BUTTON_SUBMIT_POPUP =
                    declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false, By.id("selectAgenciesForm:popupSelectBtn"));
        }

        public static final class AddDirect extends MetaData {
            public static final AttributeDescriptor USER_LOGIN = declare("User Login", ComboBox.class);
            public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
            public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
            public static final AttributeDescriptor BUTTON_OPEN_POPUP =
                    declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By.id("groupEditForm:selectIndividualsBtn"));
            public static final AttributeDescriptor BUTTON_SUBMIT_POPUP =
                    declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false, By.id("selectIndividualsForm:popupSelectBtn"));
        }
    }

    public static final class AddCommissionTemplate extends MetaData {
        public static final AttributeDescriptor TEMPLATE_NAME = declare("Template Name", TextBox.class);

        public static final class PolicyYear extends MetaData {
            public static final AttributeDescriptor LOWER_BOUNDARY = declare("Lower Boundary", TextBox.class, Waiters.DEFAULT, By.id("addTemplateDialogForm_groupBenefitsGraded:startYear"));
            public static final AttributeDescriptor UPPER_BOUNDARY = declare("Upper Boundary", TextBox.class, Waiters.DEFAULT, By.id("addTemplateDialogForm_groupBenefitsGraded:endYear"));
            public static final AttributeDescriptor END_YEAR = declare("End Year", TextBox.class, Waiters.DEFAULT, false, By.id("addTemplateDialogForm_groupBenefitsHeap:endYear"));
            public static final AttributeDescriptor COMMISSION = declare("Commission %", TextBox.class, By.xpath("//input[contains(@id, 'rate') and not(@disabled)]"));
        }
    }

    public static final class CopyPCCommissionStrategy extends MetaData {
        public static final AttributeDescriptor SELECT_STRATEGY_TO_COPY = declare("Select Strategy to Copy", ComboBox.class);
        public static final AttributeDescriptor COMMISSION_PAYMENT_TRIGGER = declare("Select Product", AdvancedSelector.class,
                By.xpath("//table[.//input[@id='bulkStrategyForm:selectProductsBtn' or @id='bulkStrategyForm:selectProductsBtn_footer']]"));
    }

    public static final class AddPCCommissionStrategy extends MetaData {
        public static final AttributeDescriptor PRODUCT_CODE_NAME = declare("Product Code-Name", ComboBox.class);
        public static final AttributeDescriptor COMMISSION_PAYMENT_TRIGGER = declare("Commission Payment Trigger", ComboBox.class);
        public static final AttributeDescriptor ROUNDING = declare("Rounding", ComboBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor DETERMINE_COMMISSION_RATE_BY = declare("Determine Commission Rate By", RadioGroup.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    }

    public static final class AddGBCommissionStrategy extends MetaData {
        public static final AttributeDescriptor PRODUCT_CODE_NAME = declare("Product Code-Name", ComboBox.class);
        public static final AttributeDescriptor ROUNDING = declare("Rounding", ComboBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AttributeDescriptor DETERMINE_COMMISSION_RATE_BY = declare("Determine Commission Rate By", RadioGroup.class);
        public static final AttributeDescriptor AVAILABLE_FOR_OVERRIDE = declare("Available for Override", CheckBox.class);
    }

    public static final class AddGBCommissionRule extends MetaData {
        public static final AttributeDescriptor COMMISSION_TYPE = declare("Commission Type", ComboBox.class);

        public static final class AddCommissionRule extends MetaData {
            public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class, Waiters.DEFAULT, false);
            public static final AttributeDescriptor GEOGRAPHY_SELECTION = declare("Geography Selection", ComboBox.class, Waiters.DEFAULT, false);
            public static final AttributeDescriptor USE_TEMPLATE = declare("Use Template", RadioGroup.class, Waiters.DEFAULT, false);
            public static final AttributeDescriptor TEMPLATE_NAME = declare("Template Name", ComboBox.class, Waiters.DEFAULT, false);

            public static final class PolicyYear extends MetaData {
                public static final AttributeDescriptor LOWER_BOUNDARY = declare("Lower Boundary", TextBox.class, Waiters.DEFAULT, false, By.id("addRuleDialogForm_groupBenefitsGraded:startYear"));
                public static final AttributeDescriptor UPPER_BOUNDARY = declare("Upper Boundary", TextBox.class, Waiters.DEFAULT, false, By.id("addRuleDialogForm_groupBenefitsGraded:endYear"));
                public static final AttributeDescriptor END_YEAR = declare("End Year", TextBox.class, Waiters.DEFAULT, false, By.id("addRuleDialogForm_groupBenefitsHeap:endYear"));
                public static final AttributeDescriptor COMMISSION = declare("Commission %", TextBox.class, Waiters.DEFAULT, false,
                        By.xpath("//input[contains(@id, 'rate') and contains(@id, 'addRuleDialogForm') and not(@disabled)]"));
            }
        }
    }

    public static final class AddPCCommissionRule extends MetaData {
        public static final AttributeDescriptor POLICY_LEVEL_COMMISSION = declare("Policy Level Commission", RadioGroup.class);
        public static final AttributeDescriptor COMMISSION_TYPE = declare("Commission Type", RadioGroup.class);
        public static final AttributeDescriptor DYNAMIC_COMMISSION_SCHEDULE = declare("Dynamic Commission Schedule", RadioGroup.class);
        public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
        public static final AttributeDescriptor COMISSION_GROUP = declare("Commission Group", DialogSingleSelector.class, AddCommissionGroup.class);
        public static final AttributeDescriptor GEOGRAPHY_SELECTION = declare("Geography Selection", ComboBox.class);
        public static final AttributeDescriptor NEW_BUSINESS_COMMISSION_RATE = declare("New Business Commission Rate", TextBox.class);
        public static final AttributeDescriptor ENDORSEMENT_COMMISSION_RATE = declare("Endorsement Commission Rate", TextBox.class);
        public static final AttributeDescriptor RENEWAL_COMMISSION_RATE = declare("Renewal Commission Rate", TextBox.class);
        public static final AttributeDescriptor REINSTATEMENT_COMMISSION_RATE = declare("Reinstatement Commission Rate", TextBox.class);
        public static final AttributeDescriptor SPIN_COMMISSION_RATE = declare("Spin Commission Rate", TextBox.class);
        public static final AttributeDescriptor SPLIT_COMMISSION_RATE = declare("Split Commission Rate", TextBox.class);

        public static final class AddCommissionGroup extends MetaData {
            public static final AttributeDescriptor GROUP_NAME = declare("Group Name", TextBox.class, By.id("selectGroupForm:groupNameTxt"));
            public static final AttributeDescriptor PERTNER = declare("Partner", TextBox.class);
            public static final AttributeDescriptor AGENCY = declare("Agency", TextBox.class);
            public static final AttributeDescriptor DIRECT = declare("Direct", TextBox.class);
            public static final AttributeDescriptor BUTTON_OPEN_POPUP =
                    declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By.id("strategyTopForm:selectGroupBtn"));
        }
    }

    public static final class AddCommissionBonus extends MetaData {
        public static final AttributeDescriptor COMMISSION_BONUS_NAME = declare("Commission Bonus Name", TextBox.class);
        public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
        public static final AttributeDescriptor DATE_FROM = declare("Date From", TextBox.class);
        public static final AttributeDescriptor DATE_TO = declare("Date To", TextBox.class);
        public static final AttributeDescriptor TRANSACTION_DATES = declare("Transaction Dates", RadioGroup.class);
        public static final AttributeDescriptor ROUNDING = declare("Rounding", ComboBox.class);

        public static final class MinimumPremiumThreshold extends MetaData {
            public static final AttributeDescriptor PRODUCT_CODE_NAME = declare("Product Code-Name", ComboBox.class);
            public static final AttributeDescriptor PREMIUM_AMOUNT = declare("Premium Amount", TextBox.class);
            public static final AttributeDescriptor LOSS_RATIO = declare("Loss Ratio", TextBox.class);
        }

        public static final class BonusPlan extends MetaData {
            public static final AttributeDescriptor TOTAL_PREMIUM_WRITTEN = declare("Total Premium Written", TextBox.class);
            public static final AttributeDescriptor PAY_BONUS = declare("Pay Bonus", TextBox.class);
            public static final AttributeDescriptor LOSS_RATIO = declare("Loss Ratio", TextBox.class);
        }
    }

    public static final class AddCommissionReferral extends MetaData {
        public static final AttributeDescriptor COMMISSION_BONUS_NAME = declare("Product Code-Name", ComboBox.class);
        public static final AttributeDescriptor SALES_CHANNEL = declare("Commission Payment Trigger", ComboBox.class);
        public static final AttributeDescriptor ROUNDING = declare("Rounding", ComboBox.class);
        public static final AttributeDescriptor TRANSACTION_DATES = declare("Payment Frequency", ComboBox.class);
        public static final AttributeDescriptor DATE_FROM = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor DATE_TO = declare("Expiration Date", TextBox.class);
    }

    public static final class AddBulkAdjustment extends MetaData {
        public static final AttributeDescriptor PRODUCT_NAME = declare("Product Name", DialogSingleSelector.class, AddProduct.class);
        public static final AttributeDescriptor PRODUCT_CODE_NAME = declare("Product Code-Name", ComboBox.class);
        public static final AttributeDescriptor ADJUSTMENT_START_DATE = declare("Adjustment Start Date", TextBox.class);
        public static final AttributeDescriptor ADJUSTMENT_END_DATE = declare("Adjustment End Date", TextBox.class);
        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
        public static final AttributeDescriptor SPECIFY_POLICIES = declare("Specify Policies", RadioGroup.class);

        public static final class AddProduct extends MetaData {
            public static final AttributeDescriptor PRODUCT_NAME = declare("Product Name", TextBox.class);
        }
    }

    public static final class AddBulkAdjustmentRule extends MetaData {
        public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
        public static final AttributeDescriptor GEOGRAPHY_SELECTION = declare("Geography Selection", ComboBox.class);
        public static final AttributeDescriptor NEW_BUSINESS_COMMISSION_RATE = declare("New Business Target Rate", TextBox.class);
        public static final AttributeDescriptor ENDORSEMENT_COMMISSION_RATE = declare("Endorsement Target Rate", TextBox.class);
        public static final AttributeDescriptor RENEWAL_COMMISSION_RATE = declare("Renewal Target Rate", TextBox.class);
        public static final AttributeDescriptor REINSTATEMENT_COMMISSION_RATE = declare("Reinstatement Target Rate", TextBox.class);
        public static final AttributeDescriptor SPIN_COMMISSION_RATE = declare("Spin Target Rate", TextBox.class);
        public static final AttributeDescriptor SPLIT_COMMISSION_RATE = declare("Split Target Rate", TextBox.class);
    }

    public static final class SearchByField extends MetaData {
        public static final AttributeDescriptor PRODUCT_NAME = declare("Product Name", TextBox.class);
        public static final AttributeDescriptor COMMISSION_STRATEGY_STATUS = declare("Commission Strategy Status", ComboBox.class);
        public static final AttributeDescriptor COMMISSION_BONUS_NAME = declare("Commission Bonus Name", TextBox.class);
        public static final AttributeDescriptor COMMISSION_BONUS_STATUS = declare("Commission Bonus Status", ComboBox.class);
        public static final AttributeDescriptor REFERAL_STATUS = declare("Referral Status", ComboBox.class);
        public static final AttributeDescriptor COMMISSION_GROUP_NAME = declare("Commission Group Name", TextBox.class);
        public static final AttributeDescriptor COMMISSION_GROUP_STATUS = declare("Commission Group Status", ComboBox.class);
        public static final AttributeDescriptor BULK_ADJUSTMENT_STATUS = declare("Bulk Adjustment Status", ComboBox.class);
        public static final AttributeDescriptor COMMISSION_TEMPLATE_NAME = declare("Template Name", TextBox.class);
        public static final AttributeDescriptor COMMISSION_TEMPLATE_TYPE = declare("Template Type", ComboBox.class);
    }
}
