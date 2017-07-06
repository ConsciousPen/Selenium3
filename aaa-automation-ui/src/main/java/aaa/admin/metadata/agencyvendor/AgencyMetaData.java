/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.agencyvendor;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.AdvancedSelector;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;

import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class AgencyMetaData {

    public static final class AgencyInfoTab extends MetaData {
        public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
        public static final AttributeDescriptor LOCATION_TYPE = declare("Location Type", ComboBox.class);
        public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
        public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
        public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
        public static final AttributeDescriptor AGENCY_NAME_ALTERNATE = declare("Agency Name - Alternate", TextBox.class);
        public static final AttributeDescriptor AGENCY_EFFECTIVE_DATE = declare("Agency Effective Date", TextBox.class);
        public static final AttributeDescriptor AGENCY_EXPIRATION_DATE = declare("Agency Expiration Date", TextBox.class);
        public static final AttributeDescriptor TAX_ID = declare("Tax ID", TextBox.class);
        public static final AttributeDescriptor EXTERNAL_REFERENCE_ID = declare("External Reference ID", TextBox.class);
        public static final AttributeDescriptor PRIMARY_AGENCY = declare("Primary Agency", RadioGroup.class);
        public static final AttributeDescriptor RELATED_PRIMARY_AGENCY = declare("Related Primary Agency", DialogSingleSelector.class, AddRelatedPrimaryAgency.class, false,
                By.id("searchPopupselectPrimaryAgency_container"));
        public static final AttributeDescriptor REGION = declare("Region", DialogSingleSelector.class, AddRegionMetaData.class, false, By.id("regionsSearch_container"));
        public static final AttributeDescriptor ZONE = declare("Zone", TextBox.class);
        public static final AttributeDescriptor OWNER_NAME = declare("Owner Name", TextBox.class);
        public static final AttributeDescriptor COMPANY_REGISTRATION = declare("Company Registration #", TextBox.class);
        public static final AttributeDescriptor FSP_NUMBER = declare("FSP Number", TextBox.class);
        public static final AttributeDescriptor VAT_VENDOR = declare("VAT Vendor", RadioGroup.class);
        public static final AttributeDescriptor VAT_NUMBER = declare("VAT Number", TextBox.class);
        public static final AttributeDescriptor LEVEL_AT_WHICH_FEES_ARE_DISBURSED = declare("Level at which Fees are disbursed", ComboBox.class);
        public static final AttributeDescriptor TIME_ZONE = declare("Time Zone", ComboBox.class);
        public static final AttributeDescriptor HOLIDAYS_CALENDAR = declare("Holidays Calendar", ComboBox.class);
        public static final AttributeDescriptor WORK_HOURS_CALENDAR = declare("Work Hours Calendar", ComboBox.class);
        public static final AttributeDescriptor BRANDS = declare("Brands", AdvancedSelector.class,
                By.xpath("//table[@class='pfForm pfSimpleForm' and .//a[@id='brokerAgencyInfoForm:BrandsSelect']]"));
        public static final AttributeDescriptor APPLICABLE_FOR_COMMISSIONS_EXTRACT = declare("Applicable for commissions Extract?", RadioGroup.class, Waiters.AJAX(60000).then(Waiters.SLEEP(5000)));
        public static final AttributeDescriptor COMMISSION_PAYMENT_SENT_TO = declare("Commission Payment Sent To", ComboBox.class);
        public static final AttributeDescriptor STATEMENT_DELIVERY_METHOD = declare("Statement Delivery Method", ComboBox.class);
        public static final AttributeDescriptor COMMISSION_PAYMENT_STATUS = declare("Commission Payment Status", ComboBox.class);
        public static final AttributeDescriptor COMMISSION_GROUPS = declare("Commission Groups", AdvancedSelector.class, By.id("brokerAgencyInfoForm:commissionGroupsSelect"));

        public static final AttributeDescriptor ELIGIBLE_FOR_AGENCY_BILLING = declare("Eligible for Agency Billing?", RadioGroup.class);
        public static final AttributeDescriptor AGENCY_BILL_DUE_DAY = declare("Agency Bill Due Day", TextBox.class);
        public static final AttributeDescriptor EXCLUDE_COMMISSIONS_FROM_BILLING = declare("Exclude commissions from billing", RadioGroup.class);
        public static final AttributeDescriptor RETAIN_COMMISSION_FROM_PAY = declare("Retain Commission From Pay", RadioGroup.class);

        public static final class AddRegionMetaData extends MetaData {
            public static final AttributeDescriptor NAME = declare("Name", TextBox.class, By.id("searchForm_regionsSearch:searchTemplate_regionsSearch"));

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerAgencyInfoForm:regionButton"));
            public static final AttributeDescriptor BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("searchForm_regionsSearch:save_single_regionsSearch"));
        }

        public static final class AddRelatedPrimaryAgency extends MetaData {
            public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
            public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerAgencyInfoForm:changePrimaryAgencyCd"));
        }
    }

    public static final class ContactInfoTab extends MetaData {
    	public static final AttributeDescriptor COUNTRY = declare("Country", StaticElement.class);
        public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
        public static final AttributeDescriptor IS_MAILING_ADDRESS_THE_SAME_AS_PHYSICAL_ADDRESS =
                declare("Is Mailing Address the same as Physical Address?", RadioGroup.class);
        public static final AttributeDescriptor COPY_INSURED_CORRESPONDENCE_REQUIRED = declare("Copy insured correspondence required?", RadioGroup.class);
        public static final AttributeDescriptor PHONE = declare("Phone #", TextBox.class);
        public static final AttributeDescriptor EXTENSION = declare("Extension #", TextBox.class);
        public static final AttributeDescriptor FAX = declare("Fax #", TextBox.class);
        public static final AttributeDescriptor ADD_CONTACT = declare("Add Contact", DialogAssetList.class, AddContactMetaData.class);

        public static final class AddContactMetaData extends MetaData {
            public static final AttributeDescriptor SALUTATION = declare("Salutation", ComboBox.class);
            public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
            public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
            public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
            public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
            public static final AttributeDescriptor CONTACT_TYPE = declare("Contact Type", ComboBox.class);

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerAgencyInfoForm:addPersonalContactBtn"));
            public static final AttributeDescriptor BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("addPersonalContactForm:savePersonalContact"));
        }
        
        public static final AttributeDescriptor MAILING_ADDRESS_LINE_1 = declare("Mailing Address Line 1", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_addressLine1"));
        public static final AttributeDescriptor MAILING_ZIP_POSTAL_CODE = declare("Mailing Zip / Postal Code", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_postalCode"));
        public static final AttributeDescriptor MAILING_CITY = declare("Mailing City", TextBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_city"));
        public static final AttributeDescriptor MAILING_STATE_PROVINCE = declare("Mailing State / Province", ComboBox.class, By.id("brokerAgencyInfoForm:mailingAddress_address_stateProvCd"));
        
    }

    public static final class BankingDetailsTab extends MetaData {
        public static final AttributeDescriptor SETTLEMENT_METHOD = declare("Settlement Method", ComboBox.class);
        public static final AttributeDescriptor BANK_DETAILS_STATUS = declare("Bank Details Status", ComboBox.class);
        public static final AttributeDescriptor ACCOUNT_HOLDER_NAME = declare("Account Holder Name", TextBox.class);
        public static final AttributeDescriptor ACCOUNT_TYPE = declare("Account Type", ComboBox.class);
        public static final AttributeDescriptor ACCOUNT = declare("Account #", TextBox.class);
        public static final AttributeDescriptor EFT_PROTOCAL_FORM_RECEIVED = declare("EFT Protocol Form Received", RadioGroup.class);
        public static final AttributeDescriptor ABA_TRANSIT = declare("ABA Transit #", TextBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AttributeDescriptor INTERNATIONAL_ACH_FORMATTING = declare("International ACH Formatting", RadioGroup.class);
        public static final AttributeDescriptor BANK_NAME = declare("Bank Name", ComboBox.class);
        public static final AttributeDescriptor BANK_BRANCH_CODE = declare("Bank Branch Code", ComboBox.class);
        public static final AttributeDescriptor BANK_ACCOUNT_TYPE = declare("Bank Account Type", ComboBox.class);
    }

    public static final class SupportTeamTab extends MetaData {
        public static final AttributeDescriptor ADD_SUPPORT_TEAM = declare("Added Support Team", DialogSingleSelector.class, AddSupportTeamMetaData.class);

        public static final class AddSupportTeamMetaData extends MetaData {
            public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
            public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
            public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
            public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerUnderwritersForm:addUnderwriter"));
            public static final AttributeDescriptor BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("underwriterSearch:addBtn"));
        }
    }
    
    public static final class ChildrenTab extends MetaData {
        public static final AttributeDescriptor ADD_CHILD_AGENCY = declare("Add Child Agency", DialogSingleSelector.class, AddChildAgencyMetaData.class);

        public static final class AddChildAgencyMetaData extends MetaData {
        	public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
            public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
            public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
            public static final AttributeDescriptor CITY = declare("City", TextBox.class);
            public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", TextBox.class);


            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("brokerChildrenForm:addUnderwriter"));
            public static final AttributeDescriptor BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("childAgencySearch:addBtn"));
        }
    }

    public static final class SearchByField extends MetaData {
        public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
        public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE = declare("State", TextBox.class);
        public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
        public static final AttributeDescriptor TAX_ID = declare("Tax Id", TextBox.class);
        public static final AttributeDescriptor PRIMARY_AGENCY = declare("Primary Agency", ComboBox.class);
        public static final AttributeDescriptor STATUS = declare("Status", ComboBox.class);
        
        public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
        public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", TextBox.class);
    }
}
