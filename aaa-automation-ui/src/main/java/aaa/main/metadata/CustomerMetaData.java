/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.ComboList;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;
import com.exigen.ipb.etcsa.controls.productfactory.DatePicker;

import aaa.toolkit.webdriver.customcontrols.AgencyAssignmentMultiAssetList;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class CustomerMetaData {

    public static final class CustomerType extends MetaData {
        public static final AttributeDescriptor CUSTOMER_TYPE = declare("Customer Type", RadioGroup.class);
    }

    public static final class GeneralTab extends MetaData {
    	public static final AttributeDescriptor SALUTATION = declare("Salutation", ComboBox.class);
		public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
		public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
		public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
		public static final AttributeDescriptor NICKNAME = declare("Nickname", TextBox.class);
        public static final AttributeDescriptor LEAD_STATUS = declare("Lead Status", ComboBox.class);
		public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class);
		public static final AttributeDescriptor MARITAL_STATUS = declare("Marital Status", ComboBox.class);
		public static final AttributeDescriptor SSN = declare("SSN", TextBox.class);
		public static final AttributeDescriptor TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);

		public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
    	
        public static final AttributeDescriptor ADDITIONAL_NAME_DETAILS_IND_SECTION =
                declare("AdditionalNameDetailsSection", AssetList.class, AdditionalNameDetailsSection.class, false, By.xpath("//div[@id='contentWrapper']"));
        public static final AttributeDescriptor GROUP_SEARCH = declare("Group Search", DialogSingleSelector.class, AddGroup.class);
        public static final AttributeDescriptor CONTACT_DETAILS_TYPE = declare("Contact Details Type", RadioGroup.class);
        
        public static final AttributeDescriptor NAME_DBA = declare("Name - DBA", TextBox.class);
        public static final AttributeDescriptor SSN_TAX_IDENTIFICATION = declare("SSN/Tax Identification", TextBox.class);
        public static final AttributeDescriptor LEAD_SOURCE = declare("Lead Source", ComboBox.class);
        public static final AttributeDescriptor RATING = declare("Rating", ComboBox.class);
        public static final AttributeDescriptor ZIP_POST_CODE = declare("Zip/Post Code", TextBox.class);
        public static final AttributeDescriptor STATE_PROVINCE = declare("State/Province", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AttributeDescriptor NON_INDIVIDUAL_TYPE = declare("Non-Individual Type", ComboBox.class);
        public static final AttributeDescriptor NAME_LEGAL = declare("Name - Legal", TextBox.class);
        public static final AttributeDescriptor AGENCY_ASSIGNMENT = declare("Agency Assignment", AgencyAssignmentMultiAssetList.class, AddAgencyMetaData.class);
        public static final AttributeDescriptor EIN = declare("EIN", TextBox.class);
        public static final AttributeDescriptor DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class);
        public static final AttributeDescriptor DIVISION = declare("Division", ComboBox.class);
        public static final AttributeDescriptor INDUSTRY = declare("Industry", ComboBox.class);
        public static final AttributeDescriptor SIC_DESCRIPTION = declare("SIC Description", ComboBox.class);
        public static final AttributeDescriptor SIC_CODE = declare("SIC Code", TextBox.class);
        public static final AttributeDescriptor PHONE_TYPE = declare("Phone Type", ComboBox.class);
        public static final AttributeDescriptor PHONE_NUMBER = declare("Phone Number", TextBox.class);
        public static final AttributeDescriptor EMAIL_TYPE = declare("Email Type", ComboBox.class);
        public static final AttributeDescriptor EMAIL_ADDRESS = declare("Email Address", TextBox.class);
        public static final AttributeDescriptor COMMENT = declare("Comment", TextBox.class);
        public static final AttributeDescriptor CONSENT_STATUS = declare("Consent Status", ComboBox.class);
        public static final AttributeDescriptor CHAT_ID = declare("Chat ID", TextBox.class);
        public static final AttributeDescriptor CHAT_TYPE = declare("Chat Type", ComboBox.class);
        public static final AttributeDescriptor WEB_URL_TYPE = declare("Web URL Type", ComboBox.class);
        public static final AttributeDescriptor WEB_URL = declare("Web URL", TextBox.class);
        public static final AttributeDescriptor GROUP_SPONSOR = declare("Group Sponsor", CheckBox.class);
        public static final AttributeDescriptor MAKE_PREFFERED = declare("Make Preferred?", RadioGroup.class);
        public static final AttributeDescriptor ASSOCIATE_DIVISIONS = declare("Associate Divisions", CheckBox.class);
        public static final AttributeDescriptor ASSOCIATE_BUSINESS_ENTITIES = declare("Associate Business Entities", CheckBox.class);
        public static final AttributeDescriptor POLICY_TYPE = declare("Policy Type", ComboBox.class);
        public static final AttributeDescriptor CARRIER_NAME = declare("Carrier Name", ComboBox.class);
        public static final AttributeDescriptor SOCIAL_NET_TYPE = declare("Social Net Type", ComboBox.class);
        public static final AttributeDescriptor SOCIAL_NET_ID = declare("Social Net ID", TextBox.class);
        public static final AttributeDescriptor MEMBERSHIP_STATUS = declare("Membership Status", ComboBox.class);
        public static final AttributeDescriptor MEMBERSHIP_START_DATE = declare("Membership Start Date", TextBox.class);
        public static final AttributeDescriptor STUDENT_START_DATE = declare("Student Start Date", TextBox.class);
        public static final AttributeDescriptor STUDENT_STATUS = declare("Student Status", ComboBox.class);
        public static final AttributeDescriptor ACCOUNT_TYPE = declare("Account Type", RadioGroup.class);
        public static final AttributeDescriptor ACCOUNT_DESIGNATION_TYPE = declare("Account Designation Type", ComboBox.class);
        public static final AttributeDescriptor MAJOR_LARGE_ACCOUNT_ID = declare("Major/Large Account ID", TextBox.class);

        public static final class AddGroup extends MetaData {
            public static final AttributeDescriptor GROUP_ID = declare("Group ID", TextBox.class);
            public static final AttributeDescriptor GROUP_NAME = declare("Group Name", TextBox.class);
            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("crmForm:showGroupSearchPopup_0"));
        }

        public static final class AdditionalNameDetailsSection extends MetaData {
            public static final AttributeDescriptor SALUTATION = declare("Salutation", ComboBox.class, Waiters.AJAX,
                    By.id("crmForm:additionalNameInfo_0_salutation"));
            public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class, Waiters.AJAX,
                    By.id("crmForm:additionalNameInfo_0_firstName"));
            public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class, Waiters.AJAX,
                    By.id("crmForm:additionalNameInfo_0_middleName"));
            public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class, Waiters.AJAX,
                    By.id("crmForm:additionalNameInfo_0_lastName"));
            public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class, Waiters.AJAX,
                    By.id("crmForm:additionalNameInfo_0_suffix"));
            public static final AttributeDescriptor DESIGNATION = declare("Designation", ComboBox.class, Waiters.AJAX,
                    By.id("crmForm:additionalNameInfo_0_designationCd"));
            public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX,
                    By.id("crmForm:additionalNameInfo_0_designationDescription"));
            public static final AttributeDescriptor NAME_DBA = declare("Name-DBA", TextBox.class, Waiters.AJAX,
                    By.id("crmForm:additionalNameInfo_0_nameDba"));
            public static final AttributeDescriptor BUTTON_ADD_ALL = declare("Add All", Button.class, Waiters.DEFAULT, false,
                    By.id("crmForm:additionalNamesActionsAddButton"));
        }

        public static final class AddAgencyMetaData extends MetaData {
            public static final AttributeDescriptor AGENCY_CHANNEL = declare("Channel", ComboBox.class, Waiters.NONE, false, By.id("brokerSearchFromcrmCustomerBrokerCd:brokerSearchCriteria_channelCd"));
            public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class, Waiters.NONE, false);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class, Waiters.NONE, false);
            public static final AttributeDescriptor AGENCY_ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.NONE, false, By.id("brokerSearchFromcrmCustomerBrokerCd:brokerSearchCriteria_postalCode"));
            public static final AttributeDescriptor AGENCY_CITY = declare("City", TextBox.class, Waiters.NONE, false, By.id("brokerSearchFromcrmCustomerBrokerCd:brokerSearchCriteria_city"));
            public static final AttributeDescriptor AGENCY_STATE = declare("State", TextBox.class, Waiters.NONE, false);
        }
    }

    public static final class OpportunityActionTab extends MetaData {
        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
        public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
        public static final AttributeDescriptor LIKELIHOOD = declare("Likelihood", ComboBox.class);
        public static final AttributeDescriptor POTENTIAL = declare("Potential", TextBox.class);
    }

    public static final class BusinessEntityTab extends MetaData {
        public static final AttributeDescriptor NON_INDIVIDUAL_TYPE = declare("Non-Individual Type", ComboBox.class);
        public static final AttributeDescriptor NAME_LEGAL = declare("Name - Legal", TextBox.class);
        public static final AttributeDescriptor NAME_DBA = declare("Name - DBA", TextBox.class);
        public static final AttributeDescriptor EIN = declare("EIN", TextBox.class);
        public static final AttributeDescriptor DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class);
        public static final AttributeDescriptor NUMBER_OF_CONTINUOUS_YEARS_IN_THE_FIELDS = declare("Number of continuous years in the field", TextBox.class);
        public static final AttributeDescriptor ENTITY_TYPE = declare("Entity Type", ComboList.class);
        public static final AttributeDescriptor TAX_ATTEMPT = declare("Tax Exempt", CheckBox.class);
        public static final AttributeDescriptor DIVISION = declare("Division", ComboBox.class);
        public static final AttributeDescriptor INDUSTRY = declare("Industry", ComboBox.class);
        public static final AttributeDescriptor SIC_DESCRIPTION = declare("SIC Description", ComboBox.class);
        public static final AttributeDescriptor SIC_CODE = declare("SIC Code", TextBox.class);
        public static final AttributeDescriptor SECTOR = declare("Sector", ComboBox.class);
        public static final AttributeDescriptor SUB_SECTOR = declare("Sub Sector", ComboBox.class);
        public static final AttributeDescriptor INDUSTRY_GROUP = declare("Industry Group", ComboBox.class);
        public static final AttributeDescriptor NAICS_DESCRIPTION = declare("NAICS Description", ComboBox.class);
        public static final AttributeDescriptor NAICS_CODE = declare("NAICS Code", TextBox.class);
    }

    public static final class RelationshipTab extends MetaData {
        public static final AttributeDescriptor TYPE = declare("Type", RadioGroup.class);
        public static final AttributeDescriptor TITLE = declare("Title", ComboBox.class);
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
        public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
        public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class);
        public static final AttributeDescriptor MARITAL_STATUS = declare("Marital Status", ComboBox.class);
        public static final AttributeDescriptor SSN_TAX_IDENTIFICATION = declare("SSN/Tax Identification", TextBox.class);
        public static final AttributeDescriptor RELATIONSHIP_TO_CUSTOMER = declare("Relationship to Customer", ComboBox.class);
        public static final AttributeDescriptor SEARCH_PARTY_RELATIONSHIP = declare("Search Party Relationship", Button.class);
        public static final AttributeDescriptor NAME_LEGAL = declare("Name - Legal", TextBox.class);
        public static final AttributeDescriptor NAME_DBA = declare("Name - DBA", TextBox.class);
        public static final AttributeDescriptor CHAT_TYPE = declare("Chat Type", ComboBox.class);
        public static final AttributeDescriptor CHAT_ID = declare("Chat ID", TextBox.class);
        public static final AttributeDescriptor WEB_URL_TYPE = declare("Web URL Type", ComboBox.class);
        public static final AttributeDescriptor WEB_URL = declare("Web URL", TextBox.class);
        public static final AttributeDescriptor PHONE_TYPE = declare("Phone Type", ComboBox.class);
        public static final AttributeDescriptor PHONE_NUMBER = declare("Phone Number", TextBox.class);
        public static final AttributeDescriptor EMAIL_TYPE = declare("Email Type", ComboBox.class);
        public static final AttributeDescriptor EMAIL_ADDRESS = declare("Email Address", TextBox.class);
        public static final AttributeDescriptor SOCIAL_NET_TYPE = declare("Social Net Type", ComboBox.class);
        public static final AttributeDescriptor SOCIAL_NET_ID = declare("Social Net ID", TextBox.class);
        public static final AttributeDescriptor ASSIGN_SERVICE_ROLE = declare("Assign Service Role?", RadioGroup.class);
        public static final AttributeDescriptor AUTHORIZATION_OPTION = declare("Authorization Option", ComboBox.class);
        public static final AttributeDescriptor CHALLENGE_QUESTION = declare("Challenge Question", ComboBox.class);
        public static final AttributeDescriptor PASSWORD = declare("Password", TextBox.class);
        public static final AttributeDescriptor PASSWORD_REMINDER = declare("Password Reminder", TextBox.class);
        public static final AttributeDescriptor ANSWER = declare("Answer", TextBox.class);
        public static final AttributeDescriptor COMMENT = declare("Comment", TextBox.class);
        public static final AttributeDescriptor SERVICE_ROLE = declare("Service Role", ComboList.class);
        public static final AttributeDescriptor CONTACT_DETAILS_TYPE = declare("Contact Details Type", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", ComboBox.class);
        public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
        public static final AttributeDescriptor ZIP_POST_CODE = declare("Zip/Post Code", TextBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE_PROVINCE = declare("State/Province", ComboBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
        public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
        public static final AttributeDescriptor DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class);
    }

    public static final class DivisionsTab extends MetaData {
        public static final AttributeDescriptor DIVISION_NUMBER = declare("Division Number", TextBox.class);
        public static final AttributeDescriptor DIVISION_NAME = declare("Division Name", TextBox.class);
        public static final AttributeDescriptor NUMBER_OF_INSUREDS = declare("Number of Insureds", TextBox.class);
        public static final AttributeDescriptor BILLING_METHOD = declare("Billing Method", ComboBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
    }

    public static final class CommunicationActionTab extends MetaData {
        public static final AttributeDescriptor COMMUNICATION_CHANNEL = declare("Communication Channel", ComboBox.class);
        public static final AttributeDescriptor ENTITY_TYPE = declare("Entity Type", ComboBox.class);
        public static final AttributeDescriptor COMMUNICATION_DIRECTION = declare("Communication Direction", ComboBox.class);
        public static final AttributeDescriptor ENTITY_REFERENCE_ID = declare("Entity Reference ID", TextBox.class);
    }

    public static final class ScheduledUpdateActionTab extends MetaData {
        public static final AttributeDescriptor UPDATE_EFFECTIVE_DATE = declare("Update Effective Date", TextBox.class);
    }

    public static final class ViewHistoryActionTab extends MetaData {
        public static final AttributeDescriptor VERSION_DATE = declare("Version Date", TextBox.class);
    }

    public static final class RelationshipContactActionTab extends MetaData {
    }

    public static final class DeletePendingUpdatesActionTab extends MetaData {
    }

    public static final class AddAgencyActionTab extends MetaData {
        public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
        public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
        public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
        public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE = declare("State", TextBox.class);
    }

    public static final class RemoveAgencyActionTab extends MetaData {
    }

    public static final class SelectGroupRelationshipTypeActionTab extends MetaData {
        public static final AttributeDescriptor RELATIONSHIP_TYPE = declare("Relationship Type", ComboBox.class);
    }

    public static final class EmployeeInfoTab extends MetaData {
        public static final AttributeDescriptor JOB_TITLE = declare("Job Title", TextBox.class);
        public static final AttributeDescriptor JOB_CODE = declare("Job Code", ComboBox.class);
        public static final AttributeDescriptor ORIGINAL_HIRE_DATE = declare("Original Hire Date", TextBox.class);
        public static final AttributeDescriptor EMPLOYMENT_STATUS = declare("Employment Status", ComboBox.class);
    }

    public static final class SponsorParticipantRelationshipAssociationRemovalActionTab extends MetaData {
        public static final AttributeDescriptor KEEP_RELATIONSHIP_HISTORY = declare("Keep Relationship history", CheckBox.class);
    }

    public static final class AssociateExistingCustomerSearchActionTab extends MetaData {
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of birth", TextBox.class);
        public static final AttributeDescriptor PHONE_NUMBER = declare("Phone Number", TextBox.class);
        public static final AttributeDescriptor ADDRESS_LINE = declare("Address Line", TextBox.class);
        public static final AttributeDescriptor CITY = declare("City", TextBox.class);
        public static final AttributeDescriptor STATE_PROVINCE = declare("State/Province", TextBox.class);
        public static final AttributeDescriptor ZIP_POST_CODE = declare("ZIP/Post Code", TextBox.class);
        public static final AttributeDescriptor COUNTRY = declare("Country", TextBox.class);
    }

    public static final class QualifyActionTab extends MetaData {
    }

    public static final class MakeInvalidActionTab extends MetaData {
    }

    public static final class UndoInvalidActionTab extends MetaData {
    }
    
    public static final class MergeSearchMetaData extends MetaData {
        public static final AttributeDescriptor MERGE_SEARCH_DIALOG = declare("Search Customer", DialogSingleSelector.class, SearchMergeCustomerMetaData.class);

        public static final class SearchMergeCustomerMetaData extends MetaData {
            public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
            public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
            public static final AttributeDescriptor CUSTOMER_NUMBER = declare("Customer Number", TextBox.class);
            public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", DatePicker.class);
            public static final AttributeDescriptor STATUS = declare("Status", ComboBox.class);
            public static final AttributeDescriptor PHONE_NUMBER = declare("Phone Number", TextBox.class);
            public static final AttributeDescriptor ADDRESS_LINE = declare("Address Line", TextBox.class);
            public static final AttributeDescriptor CITY = declare("City", TextBox.class);
            public static final AttributeDescriptor STATE_PROVINCE = declare("State/Province", TextBox.class);
            public static final AttributeDescriptor ZIP_POST_CODE = declare("ZIP/Post Code", TextBox.class);
            public static final AttributeDescriptor COUNTRY = declare("Country", TextBox.class);
        }
    }
}
