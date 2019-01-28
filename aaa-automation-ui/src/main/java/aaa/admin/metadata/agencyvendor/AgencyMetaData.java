/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.agencyvendor;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class AgencyMetaData {

    public static final class AgencyInfoTab extends MetaData {
        public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
        public static final AssetDescriptor<ComboBox> LOCATION_TYPE = declare("Location Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_NAME_ALTERNATE = declare("Agency Name - Alternate", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_EFFECTIVE_DATE = declare("Agency Effective Date", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_EXPIRATION_DATE = declare("Agency Expiration Date", TextBox.class);
        public static final AssetDescriptor<TextBox> TAX_ID = declare("Tax ID", TextBox.class);
        public static final AssetDescriptor<TextBox> EXTERNAL_REFERENCE_ID = declare("External Reference ID", TextBox.class);
        public static final AssetDescriptor<RadioGroup> PRIMARY_AGENCY = declare("Primary Agency", RadioGroup.class);
        public static final AssetDescriptor<DialogSingleSelector> RELATED_PRIMARY_AGENCY = declare("Related Primary Agency", DialogSingleSelector.class, AddRelatedPrimaryAgency.class, false,
                By.id("searchPopupselectPrimaryAgency_container"));
        public static final AssetDescriptor<DialogSingleSelector> REGION = declare("Region", DialogSingleSelector.class, AddRegionMetaData.class, false, By.id("regionsSearch_container"));
        public static final AssetDescriptor<TextBox> ZONE = declare("Zone", TextBox.class);
        public static final AssetDescriptor<TextBox> OWNER_NAME = declare("Owner Name", TextBox.class);
        public static final AssetDescriptor<TextBox> COMPANY_REGISTRATION = declare("Company Registration #", TextBox.class);
        public static final AssetDescriptor<TextBox> FSP_NUMBER = declare("FSP Number", TextBox.class);
        public static final AssetDescriptor<RadioGroup> VAT_VENDOR = declare("VAT Vendor", RadioGroup.class);
        public static final AssetDescriptor<TextBox> VAT_NUMBER = declare("VAT Number", TextBox.class);
        public static final AssetDescriptor<ComboBox> LEVEL_AT_WHICH_FEES_ARE_DISBURSED = declare("Level at which Fees are disbursed", ComboBox.class);
        public static final AssetDescriptor<ComboBox> TIME_ZONE = declare("Time Zone", ComboBox.class);
        public static final AssetDescriptor<ComboBox> HOLIDAYS_CALENDAR = declare("Holidays Calendar", ComboBox.class);
        public static final AssetDescriptor<ComboBox> WORK_HOURS_CALENDAR = declare("Work Hours Calendar", ComboBox.class);
        public static final AssetDescriptor<AdvancedSelector> BRANDS = declare("Brands", AdvancedSelector.class,
                By.xpath("//table[@class='pfForm pfSimpleForm' and .//a[@id='brokerAgencyInfoForm:BrandsSelect']]"));
        public static final AssetDescriptor<RadioGroup> APPLICABLE_FOR_COMMISSIONS_EXTRACT = declare("Applicable for commissions Extract?", RadioGroup.class, Waiters.AJAX(60000).then(Waiters.SLEEP(5000)));
        public static final AssetDescriptor<ComboBox> COMMISSION_PAYMENT_SENT_TO = declare("Commission Payment Sent To", ComboBox.class);
        public static final AssetDescriptor<ComboBox> STATEMENT_DELIVERY_METHOD = declare("Statement Delivery Method", ComboBox.class);
        public static final AssetDescriptor<ComboBox> COMMISSION_PAYMENT_STATUS = declare("Commission Payment Status", ComboBox.class);
        public static final AssetDescriptor<AdvancedSelector> COMMISSION_GROUPS = declare("Commission Groups", AdvancedSelector.class, By.id("brokerAgencyInfoForm:commissionGroupsSelect"));

        public static final AssetDescriptor<RadioGroup> ELIGIBLE_FOR_AGENCY_BILLING = declare("Eligible for Agency Billing?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> AGENCY_BILL_DUE_DAY = declare("Agency Bill Due Day", TextBox.class);
        public static final AssetDescriptor<RadioGroup> EXCLUDE_COMMISSIONS_FROM_BILLING = declare("Exclude commissions from billing", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> RETAIN_COMMISSION_FROM_PAY = declare("Retain Commission From Pay", RadioGroup.class);

        public static final class AddRegionMetaData extends MetaData {
            public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, By.id("searchForm_regionsSearch:searchTemplate_regionsSearch"));

            public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerAgencyInfoForm:regionButton"));
            public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("searchForm_regionsSearch:save_single_regionsSearch"));
        }

        public static final class AddRelatedPrimaryAgency extends MetaData {
            public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
            public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);

            public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerAgencyInfoForm:changePrimaryAgencyCd"));
        }
    }

    public static final class ContactInfoTab extends MetaData {
    	public static final AssetDescriptor<StaticElement> COUNTRY = declare("Country", StaticElement.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> IS_MAILING_ADDRESS_THE_SAME_AS_PHYSICAL_ADDRESS =
                declare("Is Mailing Address the same as Physical Address?", RadioGroup.class);
        public static final AssetDescriptor<RadioGroup> COPY_INSURED_CORRESPONDENCE_REQUIRED = declare("Copy insured correspondence required?", RadioGroup.class);
        public static final AssetDescriptor<TextBox> PHONE = declare("Phone #", TextBox.class);
        public static final AssetDescriptor<TextBox> EXTENSION = declare("Extension #", TextBox.class);
        public static final AssetDescriptor<TextBox> FAX = declare("Fax #", TextBox.class);
        public static final AssetDescriptor<DialogAssetList> ADD_CONTACT = declare("Add Contact", DialogAssetList.class, AddContactMetaData.class);

        public static final class AddContactMetaData extends MetaData {
            public static final AssetDescriptor<ComboBox> SALUTATION = declare("Salutation", ComboBox.class);
            public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
            public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
            public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
            public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
            public static final AssetDescriptor<ComboBox> CONTACT_TYPE = declare("Contact Type", ComboBox.class);

            public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerAgencyInfoForm:addPersonalContactBtn"));
            public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("addPersonalContactForm:savePersonalContact"));
        }
        
        public static final AssetDescriptor<TextBox> MAILING_ADDRESS_LINE_1 = declare("Mailing Address Line 1", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_addressLine1"));
        public static final AssetDescriptor<TextBox> MAILING_ZIP_POSTAL_CODE = declare("Mailing Zip / Postal Code", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_postalCode"));
        public static final AssetDescriptor<TextBox> MAILING_CITY = declare("Mailing City", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_city"));
        public static final AssetDescriptor<ComboBox> MAILING_STATE_PROVINCE = declare("Mailing State / Province", ComboBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_stateProvCd"));
        
    }

    public static final class BankingDetailsTab extends MetaData {
        public static final AssetDescriptor<ComboBox> SETTLEMENT_METHOD = declare("Settlement Method", ComboBox.class);
        public static final AssetDescriptor<ComboBox> BANK_DETAILS_STATUS = declare("Bank Details Status", ComboBox.class);
        public static final AssetDescriptor<TextBox> ACCOUNT_HOLDER_NAME = declare("Account Holder Name", TextBox.class);
        public static final AssetDescriptor<ComboBox> ACCOUNT_TYPE = declare("Account Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> ACCOUNT = declare("Account #", TextBox.class);
        public static final AssetDescriptor<RadioGroup> EFT_PROTOCAL_FORM_RECEIVED = declare("EFT Protocol Form Received", RadioGroup.class);
        public static final AssetDescriptor<TextBox> ABA_TRANSIT = declare("ABA Transit #", TextBox.class);
        public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AssetDescriptor<RadioGroup> INTERNATIONAL_ACH_FORMATTING = declare("International ACH Formatting", RadioGroup.class);
        public static final AssetDescriptor<ComboBox> BANK_NAME = declare("Bank Name", ComboBox.class);
        public static final AssetDescriptor<ComboBox> BANK_BRANCH_CODE = declare("Bank Branch Code", ComboBox.class);
        public static final AssetDescriptor<ComboBox> BANK_ACCOUNT_TYPE = declare("Bank Account Type", ComboBox.class);
    }

    public static final class SupportTeamTab extends MetaData {
        public static final AssetDescriptor<DialogSingleSelector> ADD_SUPPORT_TEAM = declare("Added Support Team", DialogSingleSelector.class, AddSupportTeamMetaData.class);

        public static final class AddSupportTeamMetaData extends MetaData {
            public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
            public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
            public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
            public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);

            public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerUnderwritersForm:addUnderwriter"));
            public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("underwriterSearch:addBtn"));
        }
    }
    
    public static final class ChildrenTab extends MetaData {
        public static final AssetDescriptor<DialogSingleSelector> ADD_CHILD_AGENCY = declare("Add Child Agency", DialogSingleSelector.class, AddChildAgencyMetaData.class);

        public static final class AddChildAgencyMetaData extends MetaData {
        	public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
            public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
            public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
            public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
            public static final AssetDescriptor<TextBox> STATE_PROVINCE = declare("State / Province", TextBox.class);


            public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerChildrenForm:addUnderwriter"));
            public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("childAgencySearch:addBtn"));
        }
    }

    public static final class SearchByField extends MetaData {
        public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
        public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
        public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
        public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
        public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AssetDescriptor<TextBox> TAX_ID = declare("Tax Id", TextBox.class);
        public static final AssetDescriptor<ComboBox> PRIMARY_AGENCY = declare("Primary Agency", ComboBox.class);
        public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
        
        public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
        public static final AssetDescriptor<TextBox> STATE_PROVINCE = declare("State / Province", TextBox.class);
    }
}
