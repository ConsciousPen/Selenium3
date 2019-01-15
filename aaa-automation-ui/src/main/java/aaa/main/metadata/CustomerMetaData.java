/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata;

import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import aaa.toolkit.webdriver.customcontrols.AgencyAssignmentMultiAssetList;
import com.exigen.ipb.etcsa.controls.ComboList;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;
import com.exigen.ipb.etcsa.controls.productfactory.DatePicker;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class CustomerMetaData {

	public static final class CustomerType extends MetaData {
		public static final AssetDescriptor<RadioGroup> CUSTOMER_TYPE = declare("Customer Type", RadioGroup.class);
	}

	public static final class GeneralTab extends MetaData {
		public static final AssetDescriptor<ComboBox> SALUTATION = declare("Salutation", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
		public static final AssetDescriptor<TextBox> NICKNAME = declare("Nickname", TextBox.class);
		public static final AssetDescriptor<ComboBox> LEAD_STATUS = declare("Lead Status", ComboBox.class);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class, Waiters.AJAX.then(Waiters.AJAX));
		public static final AssetDescriptor<TextBox> SSN = declare("SSN", TextBox.class);
		public static final AssetDescriptor<TextBox> TAX_IDENTIFICATION = declare("Tax Identification", TextBox.class);

		public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);

		public static final AssetDescriptor<AssetList> ADDITIONAL_NAME_DETAILS_IND_SECTION =
				declare("AdditionalNameDetailsSection", AssetList.class, AdditionalNameDetailsSection.class, false, By.xpath("//div[@id='contentWrapper']"));
		public static final AssetDescriptor<DialogSingleSelector> GROUP_SEARCH = declare("Group Search", DialogSingleSelector.class, AddGroup.class);
		public static final AssetDescriptor<RadioGroup> CONTACT_DETAILS_TYPE = declare("Contact Details Type", RadioGroup.class);

		public static final AssetDescriptor<TextBox> NAME_DBA = declare("Name - DBA", TextBox.class);
		public static final AssetDescriptor<TextBox> SSN_TAX_IDENTIFICATION = declare("SSN/Tax Identification", TextBox.class);
		public static final AssetDescriptor<ComboBox> LEAD_SOURCE = declare("Lead Source", ComboBox.class);
		public static final AssetDescriptor<ComboBox> RATING = declare("Rating", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("Zip/Post Code", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State/Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AssetDescriptor<ComboBox> NON_INDIVIDUAL_TYPE = declare("Non-Individual Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> NAME_LEGAL = declare("Name - Legal", TextBox.class);
		public static final AssetDescriptor<AgencyAssignmentMultiAssetList> AGENCY_ASSIGNMENT = declare("Agency Assignment", AgencyAssignmentMultiAssetList.class, AddAgencyMetaData.class);
		public static final AssetDescriptor<TextBox> EIN = declare("EIN", TextBox.class);
		public static final AssetDescriptor<TextBox> DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class);
		public static final AssetDescriptor<ComboBox> DIVISION = declare("Division", ComboBox.class);
		public static final AssetDescriptor<ComboBox> INDUSTRY = declare("Industry", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SIC_DESCRIPTION = declare("SIC Description", ComboBox.class);
		public static final AssetDescriptor<TextBox> SIC_CODE = declare("SIC Code", TextBox.class);
		public static final AssetDescriptor<ComboBox> PHONE_TYPE = declare("Phone Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class);
		public static final AssetDescriptor<ComboBox> EMAIL_TYPE = declare("Email Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class);
		public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
		public static final AssetDescriptor<ComboBox> CONSENT_STATUS = declare("Consent Status", ComboBox.class);
		public static final AssetDescriptor<TextBox> CHAT_ID = declare("Chat ID", TextBox.class);
		public static final AssetDescriptor<ComboBox> CHAT_TYPE = declare("Chat Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> WEB_URL_TYPE = declare("Web URL Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> WEB_URL = declare("Web URL", TextBox.class);
		public static final AssetDescriptor<CheckBox> GROUP_SPONSOR = declare("Group Sponsor", CheckBox.class);
		public static final AssetDescriptor<RadioGroup> MAKE_PREFFERED = declare("Make Preferred?", RadioGroup.class);
		public static final AssetDescriptor<CheckBox> ASSOCIATE_DIVISIONS = declare("Associate Divisions", CheckBox.class);
		public static final AssetDescriptor<CheckBox> ASSOCIATE_BUSINESS_ENTITIES = declare("Associate Business Entities", CheckBox.class);
		public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> CARRIER_NAME = declare("Carrier Name", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SOCIAL_NET_TYPE = declare("Social Net Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> SOCIAL_NET_ID = declare("Social Net ID", TextBox.class);
		public static final AssetDescriptor<ComboBox> MEMBERSHIP_STATUS = declare("Membership Status", ComboBox.class);
		public static final AssetDescriptor<TextBox> MEMBERSHIP_START_DATE = declare("Membership Start Date", TextBox.class);
		public static final AssetDescriptor<TextBox> STUDENT_START_DATE = declare("Student Start Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> STUDENT_STATUS = declare("Student Status", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> ACCOUNT_TYPE = declare("Account Type", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> ACCOUNT_DESIGNATION_TYPE = declare("Account Designation Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> MAJOR_LARGE_ACCOUNT_ID = declare("Major/Large Account ID", TextBox.class);

		public static final class AddGroup extends MetaData {
			public static final AssetDescriptor<TextBox> GROUP_ID = declare("Group ID", TextBox.class);
			public static final AssetDescriptor<TextBox> GROUP_NAME = declare("Group Name", TextBox.class);
			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
					By.id("crmForm:showGroupSearchPopup_0"));
		}

		public static final class AdditionalNameDetailsSection extends MetaData {
			public static final AssetDescriptor<ComboBox> SALUTATION = declare("Salutation", ComboBox.class, Waiters.AJAX,
					By.id("crmForm:additionalNameInfo_0_salutation"));
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class, Waiters.AJAX,
					By.id("crmForm:additionalNameInfo_0_firstName"));
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class, Waiters.AJAX,
					By.id("crmForm:additionalNameInfo_0_middleName"));
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class, Waiters.AJAX,
					By.id("crmForm:additionalNameInfo_0_lastName"));
			public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class, Waiters.AJAX,
					By.id("crmForm:additionalNameInfo_0_suffix"));
			public static final AssetDescriptor<ComboBox> DESIGNATION = declare("Designation", ComboBox.class, Waiters.AJAX,
					By.id("crmForm:additionalNameInfo_0_designationCd"));
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX,
					By.id("crmForm:additionalNameInfo_0_designationDescription"));
			public static final AssetDescriptor<TextBox> NAME_DBA = declare("Name-DBA", TextBox.class, Waiters.AJAX,
					By.id("crmForm:additionalNameInfo_0_nameDba"));
			public static final AssetDescriptor<Button> BUTTON_ADD_ALL = declare("Add All", Button.class, Waiters.DEFAULT, false,
					By.id("crmForm:additionalNamesActionsAddButton"));
		}

		public static final class AddAgencyMetaData extends MetaData {
			public static final AssetDescriptor<ComboBox> AGENCY_CHANNEL =
					declare("Channel", ComboBox.class, Waiters.AJAX, false, By.id("brokerSearchFromcrmCustomerBrokerCd:brokerSearchCriteria_channelCd"));
			public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class, Waiters.AJAX, false);
			public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class, Waiters.AJAX, false);
			public static final AssetDescriptor<TextBox> AGENCY_ZIP_CODE =
					declare("Zip Code", TextBox.class, Waiters.AJAX, false, By.id("brokerSearchFromcrmCustomerBrokerCd:brokerSearchCriteria_postalCode"));
			public static final AssetDescriptor<TextBox> AGENCY_CITY = declare("City", TextBox.class, Waiters.AJAX, false, By.id("brokerSearchFromcrmCustomerBrokerCd:brokerSearchCriteria_city"));
			public static final AssetDescriptor<TextBox> AGENCY_STATE = declare("State", TextBox.class, Waiters.AJAX, false);
		}
	}

	public static final class OpportunityActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
		public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
		public static final AssetDescriptor<ComboBox> LIKELIHOOD = declare("Likelihood", ComboBox.class);
		public static final AssetDescriptor<TextBox> POTENTIAL = declare("Potential", TextBox.class);
	}

	public static final class BusinessEntityTab extends MetaData {
		public static final AssetDescriptor<ComboBox> NON_INDIVIDUAL_TYPE = declare("Non-Individual Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> NAME_LEGAL = declare("Name - Legal", TextBox.class);
		public static final AssetDescriptor<TextBox> NAME_DBA = declare("Name - DBA", TextBox.class);
		public static final AssetDescriptor<TextBox> EIN = declare("EIN", TextBox.class);
		public static final AssetDescriptor<TextBox> DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class);
		public static final AssetDescriptor<TextBox> NUMBER_OF_CONTINUOUS_YEARS_IN_THE_FIELDS = declare("Number of continuous years in the field", TextBox.class);
		public static final AssetDescriptor<ComboList> ENTITY_TYPE = declare("Entity Type", ComboList.class);
		public static final AssetDescriptor<CheckBox> TAX_ATTEMPT = declare("Tax Exempt", CheckBox.class);
		public static final AssetDescriptor<ComboBox> DIVISION = declare("Division", ComboBox.class);
		public static final AssetDescriptor<ComboBox> INDUSTRY = declare("Industry", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SIC_DESCRIPTION = declare("SIC Description", ComboBox.class);
		public static final AssetDescriptor<TextBox> SIC_CODE = declare("SIC Code", TextBox.class);
		public static final AssetDescriptor<ComboBox> SECTOR = declare("Sector", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SUB_SECTOR = declare("Sub Sector", ComboBox.class);
		public static final AssetDescriptor<ComboBox> INDUSTRY_GROUP = declare("Industry Group", ComboBox.class);
		public static final AssetDescriptor<ComboBox> NAICS_DESCRIPTION = declare("NAICS Description", ComboBox.class);
		public static final AssetDescriptor<TextBox> NAICS_CODE = declare("NAICS Code", TextBox.class);
	}

	public static final class RelationshipTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> TYPE = declare("Type", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> TITLE = declare("Title", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
		public static final AssetDescriptor<TextBox> SSN_TAX_IDENTIFICATION = declare("SSN/Tax Identification", TextBox.class);
		public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_CUSTOMER = declare("Relationship to Customer", ComboBox.class);
		public static final AssetDescriptor<Button> SEARCH_PARTY_RELATIONSHIP = declare("Search Party Relationship", Button.class);
		public static final AssetDescriptor<TextBox> NAME_LEGAL = declare("Name - Legal", TextBox.class);
		public static final AssetDescriptor<TextBox> NAME_DBA = declare("Name - DBA", TextBox.class);
		public static final AssetDescriptor<ComboBox> CHAT_TYPE = declare("Chat Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> CHAT_ID = declare("Chat ID", TextBox.class);
		public static final AssetDescriptor<ComboBox> WEB_URL_TYPE = declare("Web URL Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> WEB_URL = declare("Web URL", TextBox.class);
		public static final AssetDescriptor<ComboBox> PHONE_TYPE = declare("Phone Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class);
		public static final AssetDescriptor<ComboBox> EMAIL_TYPE = declare("Email Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class);
		public static final AssetDescriptor<ComboBox> SOCIAL_NET_TYPE = declare("Social Net Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> SOCIAL_NET_ID = declare("Social Net ID", TextBox.class);
		public static final AssetDescriptor<RadioGroup> ASSIGN_SERVICE_ROLE = declare("Assign Service Role?", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> AUTHORIZATION_OPTION = declare("Authorization Option", ComboBox.class);
		public static final AssetDescriptor<ComboBox> CHALLENGE_QUESTION = declare("Challenge Question", ComboBox.class);
		public static final AssetDescriptor<TextBox> PASSWORD = declare("Password", TextBox.class);
		public static final AssetDescriptor<TextBox> PASSWORD_REMINDER = declare("Password Reminder", TextBox.class);
		public static final AssetDescriptor<TextBox> ANSWER = declare("Answer", TextBox.class);
		public static final AssetDescriptor<TextBox> COMMENT = declare("Comment", TextBox.class);
		public static final AssetDescriptor<ComboList> SERVICE_ROLE = declare("Service Role", ComboList.class);
		public static final AssetDescriptor<ComboBox> CONTACT_DETAILS_TYPE = declare("Contact Details Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("Zip/Post Code", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State/Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AssetDescriptor<TextBox> DATE_BUSINESS_STARTED = declare("Date business started", TextBox.class);
	}

	public static final class DivisionsTab extends MetaData {
		public static final AssetDescriptor<TextBox> DIVISION_NUMBER = declare("Division Number", TextBox.class);
		public static final AssetDescriptor<TextBox> DIVISION_NAME = declare("Division Name", TextBox.class);
		public static final AssetDescriptor<TextBox> NUMBER_OF_INSUREDS = declare("Number of Insureds", TextBox.class);
		public static final AssetDescriptor<ComboBox> BILLING_METHOD = declare("Billing Method", ComboBox.class);
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class);
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
	}

	public static final class CommunicationActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> COMMUNICATION_CHANNEL = declare("Communication Channel", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ENTITY_TYPE = declare("Entity Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COMMUNICATION_DIRECTION = declare("Communication Direction", ComboBox.class);
		public static final AssetDescriptor<TextBox> ENTITY_REFERENCE_ID = declare("Entity Reference ID", TextBox.class);
	}

	public static final class ScheduledUpdateActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> UPDATE_EFFECTIVE_DATE = declare("Update Effective Date", TextBox.class);
	}

	public static final class ViewHistoryActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> VERSION_DATE = declare("Version Date", TextBox.class);
	}

	public static final class RelationshipContactActionTab extends MetaData {
	}

	public static final class DeletePendingUpdatesActionTab extends MetaData {
	}

	public static final class AddAgencyActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
		public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
	}

	public static final class RemoveAgencyActionTab extends MetaData {
	}

	public static final class SelectGroupRelationshipTypeActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> RELATIONSHIP_TYPE = declare("Relationship Type", ComboBox.class);
	}

	public static final class EmployeeInfoTab extends MetaData {
		public static final AssetDescriptor<TextBox> JOB_TITLE = declare("Job Title", TextBox.class);
		public static final AssetDescriptor<ComboBox> JOB_CODE = declare("Job Code", ComboBox.class);
		public static final AssetDescriptor<TextBox> ORIGINAL_HIRE_DATE = declare("Original Hire Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> EMPLOYMENT_STATUS = declare("Employment Status", ComboBox.class);
	}

	public static final class SponsorParticipantRelationshipAssociationRemovalActionTab extends MetaData {
		public static final AssetDescriptor<CheckBox> KEEP_RELATIONSHIP_HISTORY = declare("Keep Relationship history", CheckBox.class);
	}

	public static final class AssociateExistingCustomerSearchActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of birth", TextBox.class);
		public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE = declare("Address Line", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<TextBox> STATE_PROVINCE = declare("State/Province", TextBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("ZIP/Post Code", TextBox.class);
		public static final AssetDescriptor<TextBox> COUNTRY = declare("Country", TextBox.class);
	}

	public static final class QualifyActionTab extends MetaData {
	}

	public static final class MakeInvalidActionTab extends MetaData {
	}

	public static final class UndoInvalidActionTab extends MetaData {
	}

	public static final class MergeSearchMetaData extends MetaData {
		public static final AssetDescriptor<DialogSingleSelector> MERGE_SEARCH_DIALOG = declare("Search Customer", DialogSingleSelector.class, SearchMergeCustomerMetaData.class);

		public static final class SearchMergeCustomerMetaData extends MetaData {
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
			public static final AssetDescriptor<TextBox> CUSTOMER_NUMBER = declare("Customer Number", TextBox.class);
			public static final AssetDescriptor<DatePicker> DATE_OF_BIRTH = declare("Date of Birth", DatePicker.class);
			public static final AssetDescriptor<ComboBox> STATUS = declare("Status", ComboBox.class);
			public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE = declare("Address Line", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<TextBox> STATE_PROVINCE = declare("State/Province", TextBox.class);
			public static final AssetDescriptor<TextBox> ZIP_POST_CODE = declare("ZIP/Post Code", TextBox.class);
			public static final AssetDescriptor<TextBox> COUNTRY = declare("Country", TextBox.class);
		}
	}

	public static final class InitiateRenewalEntryActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> PRODUCT_NAME = declare("Product Name", ComboBox.class);
		public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy Type",
				ComboBox.class);
		public static final AssetDescriptor<TextBox> PREVIOUS_POLICY_NUMBER = declare("Previous Policy Number", TextBox.class);
		public static final AssetDescriptor<ComboBox> PREVIOUS_SOURCE_SYSTEM = declare("Previous Source System", ComboBox.class);
		public static final AssetDescriptor<ComboBox> RISK_STATE = declare("Risk State", ComboBox.class);
		public static final AssetDescriptor<AdvancedComboBox> UNDERWRITING_COMPANY = declare("Underwriting Company", AdvancedComboBox.class);
		public static final AssetDescriptor<TextBox> RENEWAL_EFFECTIVE_DATE = declare("Renewal Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> INCEPTION_DATE = declare("Inception Date", TextBox.class);
		public static final AssetDescriptor<TextBox> RENEWAL_POLICY_PREMIUM = declare("Renewal Policy Premium", TextBox.class);
		public static final AssetDescriptor<ComboBox> POLICY_TERM = declare("Policy Term", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PROGRAM_CODE = declare("Program Code", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> ENROLLED_IN_AUTOPAY = declare("Enrolled in Autopay", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> LEGACY_POLICY_HAD_MULTI_POLICY_DISCOUNT = declare("Legacy policy had Multi-Policy discount", RadioGroup.class);
		public static final AssetDescriptor<TextBox> LEGACY_TIER = declare("Legacy Tier", TextBox.class);
	}
}
