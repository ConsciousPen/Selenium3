/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata.policy;

import aaa.main.enums.DocGenConstants;
import aaa.main.metadata.DialogsMetaData;
import aaa.toolkit.webdriver.customcontrols.*;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.AssetListConfirmationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import aaa.toolkit.webdriver.customcontrols.endorsements.AutoCAForms;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Metadata definitions for products.
 * For single-root products the root class contains metadata classes as direct children.
 * For multi-root products the root class contains a set of entity classes under which metadata classes are grouped.
 * Modify this class if metadata needs to be altered.
 * @category Generated
 */
public final class AutoCaMetaData {

	public static final class PrefillTab extends MetaData {
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> ADRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddressButton"));
		public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class,
				DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAPrefillAddressValidation_container']"));
		public static final AssetDescriptor<Button> ORDER_PREFILL = declare("Order Prefill", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:orderPrefillButton"));
	}

	public static final class GeneralTab extends MetaData {

		public static final AssetDescriptor<AssetList> AAA_PRODUCT_OWNED = declare("AAAProductOwned", AssetList.class, AAAProductOwned.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_ExistingPolicies']"));
		public static final AssetDescriptor<AssetList> CURRENT_CARRIER_INFORMATION = declare("CurrentCarrierInformation", AssetList.class, CurrentCarrierInformation.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_OtherOrPriorPolicy']"));
		public static final AssetDescriptor<AgencyAutoCaAssetList> POLICY_INFORMATION = declare("PolicyInformation", AgencyAutoCaAssetList.class, PolicyInformation.class);
		public static final AssetDescriptor<AssetList> CONTACT_INFORMATION = declare("ContactInformation", AssetList.class, ContactInformation.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAContactInformationMVO']"));

		public static final AssetDescriptor<MultiInstanceAfterAssetList> NAMED_INSURED_INFORMATION = declare("NamedInsuredInformation", MultiInstanceAfterAssetList.class,
				NamedInsuredInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_InsuredInformationMVO']"));
		public static final AssetDescriptor<ComboBox> FIRST_NAMED_INSURED = declare("First Named Insured", ComboBox.class);

		public static final AssetDescriptor<RadioGroup> IS_THERE_ANY_THIRD_PATRY_DESIGNEE = declare("Is there any Third Party Designee?", RadioGroup.class);
		public static final AssetDescriptor<AssetList> THIRD_PARTY_DESIGNEE_INFORMATION = declare("ThirdPartyDesigneeInformation", AssetList.class, ThirdPartyDesigneeInformation.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAAutoThirdPartyDesignee']"));

		public static final class NamedInsuredInformation extends MetaData {
			public static final AssetDescriptor<JavaScriptButton> ADD_INSURED = declare("Add", JavaScriptButton.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addInsured"));
			public static final AssetDescriptor<SingleSelectSearchDialog> INSURED_SEARCH_DIALOG = declare("InsuredSearchDialog", SingleSelectSearchDialog.class, DialogsMetaData.DialogSearch.class,
					false, By.id("customerSearchPanel_container"));

			public static final AssetDescriptor<ComboBox> TITLE = declare("Title", ComboBox.class);
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
			public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
			// public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER = declare("Social Security Number", TextBox.class);
			public static final AssetDescriptor<TextBox> BASE_DATE = declare("Base Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);

			public static final AssetDescriptor<RadioGroup> HAS_LIVED_LESS_THAN_3_YEARS = declare("Has lived here for less than three years?", RadioGroup.class);
			public static final AssetDescriptor<TextBox> PRIOR_MOVE_IN_DATE = declare("Move-In Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> PRIOR_ADDRESS_TYPE = declare("Prior Address Type", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_usageTypeCd"));
			public static final AssetDescriptor<TextBox> PRIOR_ZIP_CODE = declare("Prior Zip Code", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_postalCode"));
			public static final AssetDescriptor<TextBox> PRIOR_ADDRESS_LINE_1 = declare("Prior Address Line 1", TextBox.class, By
					.id("policyDataGatherForm:insuredInformation_priorAddress_address_addressLine1"));
			public static final AssetDescriptor<TextBox> PRIOR_ADDRESS_LINE_2 = declare("Prior Address Line 2", TextBox.class, By
					.id("policyDataGatherForm:insuredInformation_priorAddress_address_addressLine2"));
			public static final AssetDescriptor<TextBox> PRIOR_CITY = declare("Prior City", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_city"));
			public static final AssetDescriptor<ComboBox> PRIOR_STATE = declare("Prior State", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_stateProvCd"));

			public static final AssetDescriptor<RadioGroup> IS_RESIDENTAL_DIFFERENF_FROM_MAILING = declare("Is residential address different from mailing address?", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> MAILING_ADDRESS_TYPE = declare("Mailing Address Type", ComboBox.class, By
					.id("policyDataGatherForm:insuredInformation_secondaryAddress_usageTypeCd"));
			public static final AssetDescriptor<TextBox> MAILING_ZIP_CODE = declare("Mailing Zip Code", TextBox.class, By
					.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_postalCode"));
			public static final AssetDescriptor<TextBox> MAILING_ADDRESS_LINE_1 = declare("Mailing Address Line 1", TextBox.class, By
					.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_addressLine1"));
			public static final AssetDescriptor<TextBox> MAILING_ADDRESS_LINE_2 = declare("Mailing Address Line 2", TextBox.class, By
					.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_addressLine2"));
			public static final AssetDescriptor<TextBox> MAILING_CITY = declare("Mailing City", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_city"));
			public static final AssetDescriptor<ComboBox> MAILING_STATE = declare("Mailing State", ComboBox.class, By
					.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_stateProvCd"));

			// The below fields do not exist anymore on Master, were removed in sprint 15.
			// public static final AssetDescriptor<TextBox> HOME_PHONE_NUMBER = declare("Home Phone #", TextBox.class);
			// public static final AssetDescriptor<TextBox> WORK_PHONE_NUMBER = declare("Work Phone #", TextBox.class);
			// public static final AssetDescriptor<TextBox> MOBILE_PHONE_NUMBER = declare("Mobile Phone #", TextBox.class);
			// public static final AssetDescriptor<ComboBox> PREFERED_PHONE_NUMBER = declare("Preferred Phone #", ComboBox.class);
			// public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class);
			public static final AssetDescriptor<ComboBox> RESIDENCE = declare("Residence", ComboBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidation.class, By
					.id(".//*[@id='addressValidationPopupAAAInsuredAddressValidation_container']"));

			public static final class AddressValidation extends MetaData {
				public static final AssetDescriptor<CheckBox> ADDRESS_IS_PO_BOX = declare("Address is PO Box", CheckBox.class, Waiters.AJAX);
				public static final AssetDescriptor<StaticElement> YOU_ENTERED = declare("You entered", StaticElement.class, By.xpath(".//span[contains(@id, ':notSamePrimartAddressLabel')]"));
				public static final AssetDescriptor<TextBox> STREET_NUMBER = declare("Street number", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> STREET_NAME = declare("Street Name", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> UNIT_NUMBER = declare("Unit number", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<RadioGroup> RADIOGROUP_SELECT = declare("Select Address", RadioGroup.class, Waiters.AJAX);

				public static final AssetDescriptor<TextBox> MAILING_STREET_NUMBER = declare("Mailing Street number", TextBox.class, Waiters.AJAX, false, By
						.id("addressValidationFormAAAInsuredAddressValidation:secondaryStreetNumberInput"));
				public static final AssetDescriptor<TextBox> MAILING_STREET_NAME = declare("Mailing Street Name", TextBox.class, Waiters.AJAX, false, By
						.id("addressValidationFormAAAInsuredAddressValidation:secondaryStreetNameInput"));
				public static final AssetDescriptor<TextBox> MAILING_UNIT_NUMBER = declare("Mailing Unit number", TextBox.class, Waiters.AJAX, false, By
						.id("addressValidationFormAAAInsuredAddressValidation:secondaryUnitNumberInput"));
				public static final AssetDescriptor<RadioGroup> MAILING_RADIOGROUP_SELECT = declare("Mailing Select Address", RadioGroup.class, Waiters.AJAX);

				public static final AssetDescriptor<Button> BTN_OK = declare("Ok", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='OK']"));
				public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Cancel']"));
			}
		}

		public static final class AAAProductOwned extends MetaData {
			public static final AssetDescriptor<ComboBox> CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class);
			public static final AssetDescriptor<ComboBox> OVERRIDE_TYPE = declare("Override Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> MEMBERSHIP_NUMBER = declare("Membership Number", TextBox.class);
			//public static final AssetDescriptor<TextBox> MEMBER_LAST_NAME = declare("Member Last Name", TextBox.class);
			public static final AssetDescriptor<RadioGroup> MOTORCYCLE = declare("Motorcycle", RadioGroup.class);
			public static final AssetDescriptor<TextBox> MOTORCYCLE_POLICY_NUM = declare("Motorcycle Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_motorPolicyNumber"));
			public static final AssetDescriptor<RadioGroup> LIFE = declare("Life", RadioGroup.class);
			public static final AssetDescriptor<TextBox> LIFE_POLICY_NUM = declare("Life Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_lifePolicyNumber"));
			public static final AssetDescriptor<RadioGroup> HOME = declare("Home", RadioGroup.class);
			public static final AssetDescriptor<TextBox> HOME_POLICY_NUM = declare("Home Motorcycle Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_homePolicyNumber"));
			public static final AssetDescriptor<RadioGroup> RENTERS = declare("Renters", RadioGroup.class);
			public static final AssetDescriptor<TextBox> RENTERS_POLICY_NUM = declare("Renters Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_rentersPolicyNumber"));
			public static final AssetDescriptor<RadioGroup> CONDO = declare("Condo", RadioGroup.class);
			public static final AssetDescriptor<TextBox> CONDO_POLICY_NUM = declare("Condo Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_condoPolicyNumber"));
			public static final AssetDescriptor<RadioGroup> PUP = declare("PUP", RadioGroup.class);
			public static final AssetDescriptor<TextBox> PUP_POLICY_NUM = declare("PUP Motorcycle Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_pupPolicyNumber"));
			public static final AssetDescriptor<TextBox> ATTRIBUTE_FOR_RULES = declare("Attribute for rules", TextBox.class);
		}

		public static final class ContactInformation extends MetaData {
			public static final AssetDescriptor<TextBox> HOME_PHONE_NUMBER = declare("Home Phone Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> WORK_PHONE_NUMBER = declare("Work Phone Number", TextBox.class);
			public static final AssetDescriptor<TextBox> MOBILE_PHONE_NUMBER = declare("Mobile Phone Number", TextBox.class);
			public static final AssetDescriptor<ComboBox> PREFERED_PHONE_NUMBER = declare("Preferred Phone #", ComboBox.class);
			public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
		}

		public static final class CurrentCarrierInformation extends MetaData {
			public static final AssetDescriptor<ComboBox> CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class);
			public static final AssetDescriptor<TextBox> OTHER_CARRIER = declare("Other Carrier", TextBox.class);
			public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy Number", TextBox.class);
			public static final AssetDescriptor<TextBox> INCEPTION_DATE = declare("Inception Date", TextBox.class);
			public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
			public static final AssetDescriptor<TextBox> MONTHS_WITH_CARRIER = declare("Months with Carrier", TextBox.class);
			public static final AssetDescriptor<TextBox> DAYS_LAPSED = declare("Days Lapsed", TextBox.class);
			public static final AssetDescriptor<ComboBox> BI_LIMITS = declare("BI Limits", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> OVERRIDE_CURRENT_CARRIER = declare("Override Prefilled Current Carrier?", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> AGENT_ENTERED_CURRENT_PRIOR_CARRIER = declare("Agent Entered Current/Prior Carrier", ComboBox.class);
			public static final AssetDescriptor<TextBox> OTHER_AGENT_ENTERED_CURRENT_PRIOR_CARRIER = declare("Other Agent Entered Current/Prior Carrier", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_INCEPTION_DATE = declare("Agent Entered Inception Date", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_EXPIRATION_DATE = declare("Agent Entered Expiration Date", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_POLICY_NUMBER = declare("Agent Entered Policy Number", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_DAYS_LAPSED = declare("Days Lapsed", TextBox.class, By
					.id("policyDataGatherForm:currentCarrierInformation_enteredDaysLapsed"));
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_MONTHS_WITH_CARRIER = declare("Months with Carrier", TextBox.class, By
					.id("policyDataGatherForm:currentCarrierInformation_enteredMonthsWithInsurer"));
			public static final AssetDescriptor<ComboBox> AGENT_ENTERED_BI_LIMITS = declare("Agent Entered BI Limits", ComboBox.class);
		}

		public static final class PolicyInformation extends MetaData {
			public static final AssetDescriptor<ComboBox> SOURCE_OF_BUSINESS = declare("Source of Business", ComboBox.class);
			public static final AssetDescriptor<TextBox> SOURCE_POLICY_NUMBER = declare("Source Policy #", TextBox.class);
			public static final AssetDescriptor<ComboBox> POLICY_STATE = declare("Policy State", ComboBox.class);
			public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
			// public static final AssetDescriptor<ComboBox> POLICY_TERM = declare("Policy Term", ComboBox.class);
			public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> CHANNEL_TYPE = declare("Channel Type", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENCY = declare("Agency", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENCY_LOCATION = declare("Agency Location", ComboBox.class);
			public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENT = declare("Agent", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class);
			public static final AssetDescriptor<TextBox> AGENT_NUMBER = declare("Agent Number", TextBox.class);
			public static final AssetDescriptor<ComboBox> COMISSION_TYPE = declare("Commission Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized by", TextBox.class);
			public static final AssetDescriptor<TextBox> TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class);
			public static final AssetDescriptor<RadioGroup> LANGUAGE_PREFERENCE = declare("Language Preference", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
		}

		public static final class ThirdPartyDesigneeInformation extends MetaData {
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);

		}
	}

	public static final class DriverTab extends MetaData {

		public static final AssetDescriptor<JavaScriptButton> ADD_DRIVER = declare("Add Driver", JavaScriptButton.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addDriver"));
		public static final AssetDescriptor<SingleSelectSearchDialog> DRIVER_SEARCH_DIALOG = declare("DriverSearchDialog", SingleSelectSearchDialog.class, DialogsMetaData.DialogSearch.class, false,
				By.id("customerSearchPanel_container"));

		public static final AssetDescriptor<ComboBox> NAMED_INSURED = declare("Named Insured", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DRIVER_TYPE = declare("Driver Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<AdvancedComboBox> REL_TO_FIRST_NAMED_INSURED = declare("Rel. to First Named Insured", AdvancedComboBox.class);
		public static final AssetDescriptor<ComboBox> TITLE = declare("Title", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AssetDescriptor<TextBox> AGE = declare("Age", TextBox.class);
		public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
		public static final AssetDescriptor<AdvancedComboBox> OCCUPATION = declare("Occupation", AdvancedComboBox.class);
		public static final AssetDescriptor<TextBox> OCCUPATION_OTHER = declare("(provide Occupation if Other is selected)", TextBox.class);
		public static final AssetDescriptor<TextBox> BASE_DATE = declare("Base Date", TextBox.class);
		public static final AssetDescriptor<TextBox> AGE_FIRST_LICENSED = declare("Age First Licensed", TextBox.class);
		public static final AssetDescriptor<RadioGroup> PERMIT_BEFORE_LICENSE = declare("Permit Before License", RadioGroup.class);
		public static final AssetDescriptor<TextBox> TOTAL_YEAR_DRIVING_EXPERIENCE = declare("Total Years Driving Experience", TextBox.class);
		public static final AssetDescriptor<RadioGroup> LICENSED_IN_US_CANADA_FOR_18_OR_MORE_MONTHS = declare("Licensed in US/Canada for 18 or More Months?", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> LICENSE_TYPE = declare("License Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_US_CANADA_LICENSE_DATE = declare("First US/Canada License Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> LICENSE_STATE = declare("License State", ComboBox.class);
		public static final AssetDescriptor<TextBox> LICENSE_NUMBER = declare("License #", TextBox.class);
		public static final AssetDescriptor<RadioGroup> FINANCIAL_RESPONSIBILITY_FILLING_NEEDED = declare("Financial Responsibility Filling Needed", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> FILLING_TYPE = declare("Filling Type", ComboBox.class);

		public static final AssetDescriptor<ComboBox> EMPLOYEE_BENEFIT_TYPE = declare("Employee Benefit Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> EMPLOYEE_ID = declare("Employee ID", TextBox.class);
		public static final AssetDescriptor<RadioGroup> ADB_COVERAGE = declare("ADB Coverage", RadioGroup.class);
		public static final AssetDescriptor<AdvancedComboBox> MOST_RECENT_GPA = declare("Most Recent GPA", AdvancedComboBox.class);
		public static final AssetDescriptor<RadioGroup> GOOD_DRIVER_DISCOUNT = declare("Good Driver Discount", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> NEW_DRIVER_COURSE_COMPLETED = declare("New Driver Course Completed", RadioGroup.class);
		public static final AssetDescriptor<TextBox> NEW_DRIVER_COURSE_COMPLETION_DATE = declare("New Driver Course Completion Date", TextBox.class);
		public static final AssetDescriptor<TextBox> NEW_DRIVER_CERTIFICATION_NUMBER = declare("New Driver Certification Number", TextBox.class);
		public static final AssetDescriptor<RadioGroup> MATURE_DRIVER_COURSE_COMPLETED_WITHIN_36_MONTHS = declare("Mature driver course completed within 36 months", RadioGroup.class);
		public static final AssetDescriptor<TextBox> MATURE_DRIVER_COURSE_COMPLETION_DATE = declare("Mature Driver Course Completion Date", TextBox.class);
		public static final AssetDescriptor<RadioGroup> FINANCIAL_RESPONSIBILITY_FILING_NEEDED = declare("Financial Responsibility Filing Needed", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> SMOKER_CIGARETTES_OR_PIPES = declare("Smoker: Cigarettes, cigars or pipes", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> DRIVER_RIDESHARING_COVERAGE = declare("Ridesharing Coverage", RadioGroup.class);
		public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
		public static final AssetDescriptor<ActivityInformationMultiAssetList> ACTIVITY_INFORMATION = declare("ActivityInformation", ActivityInformationMultiAssetList.class, ActivityInformation.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_DrivingRecord']"));
		public static final AssetDescriptor<AssetList> REMOVE_DRIVER_DIALOG = declare("Please Confirm", AssetList.class, RemoveDriverDialog.class);
		public static final AssetDescriptor<RadioGroup> RIDESHARING_COVERAGE = declare("Ridesharing Coverage", RadioGroup.class);
		public static final class RemoveDriverDialog extends MetaData {
			public static final AssetDescriptor<Button> BTN_OK = declare("Yes", Button.class, Waiters.AJAX, By.xpath("//*[@id='confirmEliminateInstance_Dialog_form:buttonYes']"));
		}


		public static final class ActivityInformation extends MetaData {
			public static final AssetDescriptor<RadioGroup> OVERRIDE_ACTIVITY_DETAILS = declare("Override Activity Details?", RadioGroup.class);
			public static final AssetDescriptor<Button> ADD_ACTIVITY = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addDrivingRecord"));
			public static final AssetDescriptor<TextBox> ORIGINAL_CONVICTION_DATE = declare("Original Conviction Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> ACTIVITY_SOURCE = declare("Activity Source", ComboBox.class);
			public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> OCCURENCE_DATE = declare("Occurrence Date", TextBox.class);
			public static final AssetDescriptor<AdvancedComboBox> DESCRIPTION = declare("Description", AdvancedComboBox.class);
			public static final AssetDescriptor<TextBox> SUSPENSION_DATE = declare("Suspension Date", TextBox.class);
			public static final AssetDescriptor<TextBox> LOSS_PAYMENT_AMOUNT = declare("Loss Payment Amount", TextBox.class);
			public static final AssetDescriptor<TextBox> ACCIDENT_POINTS = declare("Accident Points", TextBox.class);
			public static final AssetDescriptor<TextBox> CONVICTION_POINTS = declare("Conviction Points", TextBox.class);
			public static final AssetDescriptor<TextBox> SVC_DESCRIPTION = declare("SVC Description", TextBox.class);
			public static final AssetDescriptor<TextBox> CLAIM_NUMBER = declare("Claim Number", TextBox.class);
			public static final AssetDescriptor<TextBox> CONVICTION_DATE = declare("Conviction Date", TextBox.class);
			public static final AssetDescriptor<RadioGroup> PERMISSIVE_USE_LOSS = declare("Permissive Use Loss?", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> INCLUDE_IN_RATING = declare("Include in Rating?", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> NOT_INCLUDED_IN_RATING_REASON = declare("Not Included in Rating Reasons", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> INCLUDE_IN_POINTS_AND_OR_YAF = declare("Include in Points and/or YAF?", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> NOT_INCLUDED_IN_POINTS_AND_OR_YAF_REASON_CODES = declare("Not Included in Points and/or YAF - Reason Codes", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> WAS_THE_MINOR_MOVING_VIOLATION_OBTAINED_DURING_THE_HOURS_OF_EMPLOYMENT = declare(
					"Was the minor moving violation obtained during the hours of employment?", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> WAS_THE_DRIVER_OPERATING_THEIR_EMPLOYERS_VEHICLE_FOR_COMPENSATION = declare(
					"Was the driver operating their employer's vehicle for compensation?", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> DID_THE_DRIVERS_SPECIFIC_DUTIES = declare(
					"Did the driver's specific duties include operating as a Public Utility Commission-authorized highway carrier and is the driver a registered owner/lessor of the vehicle?",
					RadioGroup.class);
			public static final AssetDescriptor<ComboBox> LIABILITY_CODE = declare("Liability Code", ComboBox.class);
			public static final AssetDescriptor<AssetListConfirmationDialog> ACTIVITY_REMOVE_CONFIRMATION = declare("Activity remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX,
					false, By.id("confirmEliminateInstance_Dialog_container"));
			public static final AssetDescriptor<AssetList> SELECT_DRIVER_DIALOG = declare("Select Driver", AssetList.class, SelectDriverDialog.class);
			}

		public static final class SelectDriverDialog extends MetaData {
		    public static final AssetDescriptor<ComboBox> ASSIGN_TO = declare("Assign To", ComboBox.class, Waiters.AJAX, By.xpath("//*[starts-with(@id,'policyDataGatherForm:driverDropDown')]"));
			public static final AssetDescriptor<Button> BTN_OK = declare("Yes", Button.class, Waiters.AJAX, By.xpath("//*[@id='policyDataGatherForm:okBtn1']"));
		}
	}

	public static final class MembershipTab extends MetaData {
		public static final AssetDescriptor<FillableTable> AAA_MEMBERSHIP_REPORT = declare("AAAMembershipReport", FillableTable.class, AaaMembershipReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:membershipReports']"));
		public static final AssetDescriptor<StaticElement> WARNING_MESSAGE_BOX = declare("Warning Message Box", StaticElement.class, By
				.xpath("//span[@id='policyDataGatherForm:componentContextHolder']/ul/li"));

		public static final class AaaMembershipReportRow extends MetaData {
			public static final AssetDescriptor<RadioGroup> SELECT = declare("Select", RadioGroup.class);
			//public static final AssetDescriptor<StaticElement> LAST_NAME = declare("Last Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MEMBERSHIP_NO = declare("Membership No.", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION = declare("Action", Link.class, Waiters.AJAX);

			public static final AssetDescriptor<AssetList> ADD_MEMBER_SINCE_DIALOG = declare("AddMemberSinceDialog", AssetList.class, AddMemberSinceDialog.class);
		}

		public static final class AddMemberSinceDialog extends MetaData {
			public static final AssetDescriptor<TextBox> MEMBER_SINCE = declare("Member Since", TextBox.class, Waiters.AJAX, By
					.xpath("//input[@id='memberSinceDateFrom:popupMemberSinceDateInputDate']"));
			public static final AssetDescriptor<Button> BTN_OK = declare("OK", Button.class, Waiters.AJAX, By.xpath("//*[@id='memberSinceDateFrom:addMemberSinceDateButton']"));
			public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, By.xpath("//*[@id='memberSinceDateFrom:cancelMemberSinceDateButton']"));
		}
		public static final AssetDescriptor<Button> ORDER_REPORT = declare("Order Report", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:submitReports"));
	}

	public static final class VehicleTab extends MetaData {
		public static final AssetDescriptor<FillableTable> LIST_OF_VEHICLE = declare("List of Vehicle", FillableTable.class, ListOfVehicleRow.class, By
				.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListVehicle']/div/table"));
		public static final AssetDescriptor<Button> ADD_VEHICLE = declare("Add Vehicle", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addVehicle"));

		public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class);
		public static final AssetDescriptor<ComboBox> CHOOSE_VIN = declare("Choose VIN", ComboBox.class);
		public static final AssetDescriptor<StaticElement> VIN_MATCHED = declare("VIN Matched", StaticElement.class);
		public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class);
		public static final AssetDescriptor<AdvancedComboBox> MAKE = declare("Make", AdvancedComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_MAKE = declare("Other Make", TextBox.class);
		public static final AssetDescriptor<AdvancedComboBox> MODEL = declare("Model", AdvancedComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_MODEL = declare("Other Model", TextBox.class);
		public static final AssetDescriptor<AdvancedComboBox> SERIES = declare("Series", AdvancedComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_SERIES = declare("Other Series", TextBox.class);
		public static final AssetDescriptor<AdvancedComboBox> BODY_STYLE = declare("Body Style", AdvancedComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_BODY_STYLE = declare("Other Body Style", TextBox.class);
		public static final AssetDescriptor<AdvancedComboBox> STAT_CODE = declare("Stat Code", AdvancedComboBox.class);
		public static final AssetDescriptor<TextBox> VALUE = declare("Value($)", TextBox.class);
		public static final AssetDescriptor<ComboBox> PRIMARY_USE = declare("Primary Use", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> SPECIAL_EQUIPMENT = declare("Special Equipment", RadioGroup.class);
		public static final AssetDescriptor<TextBox> SPECIAL_EQUIPMENT_VALUE = declare("Special Equipment Value", TextBox.class);
		public static final AssetDescriptor<TextBox> SPECIAL_EQUIPMENT_DESCRIPTION = declare("Special Equipment Description", TextBox.class);
		public static final AssetDescriptor<RadioGroup> VIEW_KIT_CAR_QUESTIONNAIRE = declare("View Kit Car questionnaire?", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> VIEW_ANTIQUE_VEHICLE_QUESTIONNAIRE = declare("View Antique vehicle questionnaire?", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> RESTORED_TO_ORGINAL_STOCK_CONDITION_WITH_NO_ALTERATIONS = declare("Restored to orginal stock condition with no alterations?", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> USED_SOLEY_IN_EXHIBITIONS_CLUB_ACTIVITY_PARADES_AND_OTHER_FUN_FUNCTIONS_OF_PUBLIC_INTEREST =
				declare("Used soley in exhibitions, club activity parades and other fun functions of public interest?", RadioGroup.class);
		public static final AssetDescriptor<TextBox> COMP_COLL_SYMBOL = declare("Comp/Coll Symbol", TextBox.class);
		public static final AssetDescriptor<ComboBox> AIR_BAGS = declare("Air Bags", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ANTI_THEFT = declare("Anti-theft", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> ALTERNATIVE_FUEL_VEHICLE = declare("Alternative Fuel Vehicle", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> ANTI_THEFT_RECOVERY_DEVICE = declare("Anti-theft Recovery Device", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ANTI_LOCK_BRAKES = declare("Anti-Lock Brakes", ComboBox.class);
		public static final AssetDescriptor<ComboBox> EXISTING_DAMEGE = declare("Existing Damage", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> SALVAGED = declare("Salvaged?", RadioGroup.class);
		public static final AssetDescriptor<TextBox> MILES_ONE_WAY_TO_WORK_OR_SCHOOL = declare("Miles One-way to Work or School", TextBox.class);
		public static final AssetDescriptor<TextBox> ODOMETER_READING = declare("Odometer Reading", TextBox.class);
		public static final AssetDescriptor<TextBox> ODOMETER_READING_DATE = declare("Odometer Reading Date", TextBox.class);
		public static final AssetDescriptor<TextBox> CUSTOMER_DECLARED_ANNUAL_MILES = declare("Customer Declared Annual Miles", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE = declare("Source", TextBox.class);
		public static final AssetDescriptor<RadioGroup> IS_THE_VEHICLE_USED_IN_ANY_COMMERCIAL_BUSINESS_OPERATIONS = declare("Is the vehicle used in any commercial business operations?",
				RadioGroup.class);
		public static final AssetDescriptor<Button> CALCULATE_MILEAGE = declare("Calculate Mileage", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:calculateMileage"));

		/*
		 * public static final AssetDescriptor<RadioGroup> ENROLL_IN_USAGE_BASED_INSURANCE = declare("Enroll in Usage Based Insurance?", RadioGroup.class); public static final AssetDescriptor<Button>
		 * GET_VEHICLE_DETAILS = declare("Get Vehicle Details", Button.class, By.id("policyDataGatherForm:vehicleUBIDetaiilsButton_AAATelematicDeviceInfo")); public static final
		 * AssetDescriptor<ComboBox> VEHICLE_ELIGIBILITY_RESPONCE = declare("Vehicle Eligibility Response", ComboBox.class); public static final AssetDescriptor<ComboBox> AAA_UBI_DEVICE_STATUS =
		 * declare("AAA UBI Device Status", ComboBox.class); public static final AssetDescriptor<TextBox> AAA_UBI_DEVICE_STATUS_DATE = declare("AAA UBI Device Status Date", TextBox.class); public
		 * static final AssetDescriptor<TextBox> DEVICE_VOUCHER_NUMBER = declare("Device Voucher Number", TextBox.class); public static final AssetDescriptor<TextBox> SAFETY_SCORE =
		 * declare("Safety Score", TextBox.class); public static final AssetDescriptor<TextBox> SAFETY_SCORE_DATE = declare("Safety Score Date", TextBox.class); public static final
		 * AssetDescriptor<Link> GRANT_PATRITIPATION_DISCOUNT = declare("Grant Participation Discount", Link.class, By.id("policyDataGatherForm:grantParticipationDiscountLink"));
		 */

		public static final AssetDescriptor<RadioGroup> IS_GARAGING_DIFFERENT_FROM_RESIDENTAL = declare("Is Garaging different from Residential?", RadioGroup.class);
		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
		public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateGaragingAddressButton"));
		public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class,
				DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAGaragingAddressValidation_container']"));
		public static final AssetDescriptor<AssetList> OWNERSHIP = declare("Ownership", AssetList.class, Ownership.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAVehicleOwnership']"));
		// public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_ADDITIONAL_INTERESTS =
		// declare("Are there any additional interest(s)?", RadioGroup.class, By.xpath(".//[@id='policyDataGatherForm:sedit_AAAAdditionalInterestLabel_addnlIntInd']"));
		public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_ADDITIONAL_INTERESTS = declare("Are there any additional interest(s)?", RadioGroup.class);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_INTEREST_INFORMATION = declare("AdditionalInterestInformation",
				MultiInstanceAfterAssetList.class, AdditionalInterestInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAAdditionalInterest']"));
		public static final AssetDescriptor<ComboBox> VEHICLE_USE = declare("Vehicle Use", ComboBox.class);
		public static final AssetDescriptor<TextBox> BUSINESS_USE_DESCRIPTION = declare("Business Use Description", TextBox.class);

		public static final class ListOfVehicleRow extends MetaData {
			public static final AssetDescriptor<StaticElement> NUM_COLUMN = declare("column=1", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MAKE = declare("Make", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MODEL = declare("Model", StaticElement.class);
			public static final AssetDescriptor<StaticElement> YEAR = declare("Year", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION_COLUMN = declare("column=5", Link.class);
			public static final AssetDescriptor<Button> CONFIRM_REMOVE = declare("Confirm Remove", Button.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_form:buttonYes"));
		}

		public static final class Ownership extends MetaData {
			public static final AssetDescriptor<ComboBox> OWNERSHIP_TYPE = declare("Ownership Type", ComboBox.class);
			public static final AssetDescriptor<ComboBox> FIRST_NAME = declare("First Name", ComboBox.class);
			public static final AssetDescriptor<TextBox> OWNER_NO_LABEL = declare("", TextBox.class, By.id("policyDataGatherForm:sedit_AAAVehicleOwnership_otherName"));
			public static final AssetDescriptor<TextBox> SECOND_NAME = declare("Second Name", TextBox.class);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By
					.id("policyDataGatherForm:validateOwnershipAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressOwnershipValidationPopup_container']"));
		}

		public static final class AdditionalInterestInformation extends MetaData {
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
			public static final AssetDescriptor<TextBox> SECOND_NAME = declare("Second Name", TextBox.class);
			public static final AssetDescriptor<RadioGroup> ZIP_CODE = declare("Zip Code", RadioGroup.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By
					.id("policyDataGatherForm:validateAddlnInterestAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressAddlnInterestValidationPopup_container']"));
			public static final AssetDescriptor<Button> ADD_ADDITIONAL_INTEREST = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAAdditionalInterest"));
		}

	}

	public static final class AssignmentTab extends MetaData {
		public static final AssetDescriptor<FillableTable> DRIVER_VEHICLE_RELATIONSHIP = declare("DriverVehicleRelationshipTable", FillableTable.class, DriverVehicleRelationshipTableRow.class,
				By.xpath("//table[@id='policyDataGatherForm:driverVehicleRelationshipSummary']"));

		public static final class DriverVehicleRelationshipTableRow extends MetaData {
			public static final AssetDescriptor<StaticElement> VEHICLE = declare("Vehicle", StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PRIMARY_DRIVER = declare("Primary Driver", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> SYSTEM_RATED_DRIVER = declare("System Rated Driver", StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<AdvancedComboBox> MANUALLY_RATED_DRIVER = declare("Manually Rated Driver", AdvancedComboBox.class, Waiters.AJAX);
		}
	}

	public static final class FormsTab extends MetaData {

		public static final AssetDescriptor<AutoCAForms.AutoCAPolicyFormsController> POLICY_FORMS = declare("Policy Forms", AutoCAForms.AutoCAPolicyFormsController.class, PolicyForms.class);
		public static final AssetDescriptor<AutoCAForms.AutoCAVehicleFormsController> VEHICLE_FORMS = declare("Vehicle Forms", AutoCAForms.AutoCAVehicleFormsController.class, VehicleForms.class);
		public static final AssetDescriptor<AutoCAForms.AutoCADriverFormsController> DRIVER_FORMS = declare("Driver Forms", AutoCAForms.AutoCADriverFormsController.class, DriverForms.class);

		public static final class PolicyForms extends MetaData {
			public static final AssetDescriptor<AssetList> AA53CA = declare("AA53CA", AssetList.class, CommonFormMeta.class, false, By.id("addComponentPopup_PolicyEndorsementFormsManager_container"));
			public static final AssetDescriptor<AssetList> APPAE = declare("APPAE", AssetList.class, CommonFormMeta.class, false, By.id("addComponentPopup_PolicyEndorsementFormsManager_container"));
		}

		public static final class VehicleForms extends MetaData {
			// formAssetLocator = By.id("addComponentPopup_VehicleEndorsementFormsManager_container");
			public static final AssetDescriptor<AssetList> LSOPCE = declare("LSOPCE", AssetList.class, LSOPCEMeta.class, false, By.id("addComponentPopup_VehicleEndorsementFormsManager_container"));
			public static final AssetDescriptor<AssetList> GEE = declare("GEE", AssetList.class, GEEMeta.class, false, By.id("addComponentPopup_VehicleEndorsementFormsManager_container"));
			public static final AssetDescriptor<AssetList> CRCE = declare("CRCE", AssetList.class, CommonFormMeta.class, false, By.id("addComponentPopup_VehicleEndorsementFormsManager_container"));
		}

		public static final class DriverForms extends MetaData {
			// formAssetLocator = By.id("addComponentPopup_DriverEndorsementFormsManager_container");
			public static final AssetDescriptor<AssetList> CIPCSR22 = declare("CIPCSR22", AssetList.class, CIPCSR22FormMeta.class, false, By
					.id("addComponentPopup_DriverEndorsementFormsManager_container"));
		}

		public static final class CommonFormMeta extends MetaData {
			public static final AssetDescriptor<TextBox> FORM_CODE = declare("Form Code", TextBox.class);
			public static final AssetDescriptor<TextBox> FORM_DESCRIPTION = declare("Form Description", TextBox.class);
		}

		public static final class CIPCSR22FormMeta extends MetaData {
			public static final AssetDescriptor<TextBox> CASE_NUMBER = declare("Case Number", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
		}

		public static final class LSOPCEMeta extends MetaData {
			public static final AssetDescriptor<ComboBox> NAME = declare("Name", ComboBox.class);
		}

		public static final class GEEMeta extends MetaData {
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
			public static final AssetDescriptor<TextBox> SECOND_NAME = declare("Second Name", TextBox.class);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
		}
	}

	public static final class PremiumAndCoveragesTab extends MetaData {
		public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> POLICY_TERM = declare("Policy Term", ComboBox.class);
		public static final AssetDescriptor<ComboBox> BODILY_INJURY_LIABILITY = declare("Bodily Injury Liability", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PROPERTY_DAMAGE_LIABILITY = declare("Property Damage Liability", ComboBox.class);
		public static final AssetDescriptor<ComboBox> UNINSURED_MOTORISTS_BODILY_INJURY = declare("Uninsured Motorists Bodily Injury", ComboBox.class);
		public static final AssetDescriptor<ComboBox> UNDERINSURED_MOTORISTS_BODILY_INJURY = declare("Underinsured Motorists Bodily Injury", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MEDICAL_PAYMENTS = declare("Medical Payments", ComboBox.class);
		public static final AssetDescriptor<StaticElement> POLICY_LEVEL_LIABILITY_COVERAGES = declare("Policy Level Liability Coverages", StaticElement.class);
		public static final AssetDescriptor<DetailedVehicleCoveragesRepeatAssetList> DETAILED_VEHICLE_COVERAGES = declare("DetailedVehicleCoverages",
				DetailedVehicleCoveragesRepeatAssetList.class, DetailedVehicleCoverages.class, false);
		//public static final AssetDescriptor<JavaScriptButton> CALCULATE_PREMIUM = declare("Calculate Premium", JavaScriptButton.class, Waiters.AJAX, By.id("policyDataGatherForm:actionButton_AAAAutoRateAction"));
		public static final AssetDescriptor<JavaScriptButton> CALCULATE_PREMIUM = declare("Calculate Premium", JavaScriptButton.class, Waiters.AJAX, By.xpath("//input[@id='policyDataGatherForm:actionButton_AAAAutoRateAction' or @id='policyDataGatherForm:calculatePremium_AAAAutoRateAction']"));
		
		public static final AssetDescriptor<DialogAssetList> OVERRRIDE_PREMIUM_DIALOG = declare("Override Premium",
				DialogAssetList.class, OverridePremiumDialog.class, By.xpath("//div[@id='premiumOverridePopup_container']//div[@id='premiumOverridePopup_content']"));
		public static final AssetDescriptor<Button> RATE_DETAILS = declare("Rate Details", Button.class, Waiters.AJAX, By.xpath("//button[text()='Rate Details']"));
		public static final AssetDescriptor<Button> VIEW_AUTO_QUOTE = declare("View Auto Quote", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:viewAutoQuote_Link"));
		public static final AssetDescriptor<Button> VIEW_RATING_DETAILS = declare("View Rating Details", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:viewRatingDetails_Link"));
		public static final AssetDescriptor<Button> VIEW_OVERRIDE_RULES = declare("View Override Rules", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:viewRules_Overrides"));
		public static final AssetDescriptor<RadioGroup> ADDITIONAL_SAVINGS_OPTIONS = declare("Additional Savings Options", RadioGroup.class, Waiters.AJAX, false,
				By.xpath("//table[@id='policyDataGatherForm:visibleRadio']"));
		public static final AssetDescriptor<CheckBox> HOME = declare("Home", CheckBox.class, By.xpath("//td[text()='Home']//input"));
		public static final AssetDescriptor<CheckBox> RENTERS = declare("Renters", CheckBox.class, By.xpath("//td[text()='Renters']//input"));
		public static final AssetDescriptor<CheckBox> CONDO = declare("Condo", CheckBox.class, By.xpath("//td[text()='Condo']//input"));
		public static final AssetDescriptor<CheckBox> MOTORCYCLE = declare("Motorcycle", CheckBox.class, By.xpath("//td[text()='Motorcycle']//input"));
		public static final AssetDescriptor<CheckBox> LIFE = declare("Life", CheckBox.class, By.xpath("//td[text()='Life']//input"));
		public static final AssetDescriptor<CheckBox> MULTI_CAR = declare("Multi-car", CheckBox.class, By.xpath("//td[text()='Multi-car']//input"));
		public static final AssetDescriptor<Button> VIEW_QUOTE_WITH_DISCOUNTS = declare("View Quote with Discounts", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:applySelectedDiscounts"));
		public static final AssetDescriptor<FillableTable> DRIVER_DISCOUNTS_TABLE = declare("Driver Discounts", FillableTable.class, DriverDiscountsRow.class, By.xpath("//label[text()='Driver Discounts']/ancestor::div[2]//table"));
		public static final AssetDescriptor<CheckBox> UNACCEPTABLE_RISK_SURCHARGE = declare("Unacceptable Risk Surcharge", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> REASON = declare("Reason", TextBox.class, Waiters.AJAX, By.id("policyDataGatherForm:unacceptableRiskSurchargeReason"));
		public static final AssetDescriptor<CheckBox> UNVERIFIABLE_DRIVING_RECORD_SURCHARGE = declare("Unverifiable DrivingRecord Surcharge", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<FillableTable> INSTALLMENT_FEES_DETAILS_TABLE = declare("InstallemntFeesDetails",  FillableTable.class, ListOfFeeDetailsRow.class, By.id("policyDataGatherForm:installmentFeeDetailsTable"));
		public static final AssetDescriptor<ComboBox> RIDESHARE_COVERAGE = declare("Ridesharing Coverage", ComboBox.class);
		public static final class ListOfFeeDetailsRow extends MetaData {
			public static final AssetDescriptor<StaticElement> PAYMENT_METHOD = declare("Payment Method", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ENROLLED_IN_AUTO_PAY = declare("Enrolled in Auto Pay", StaticElement.class);
			public static final AssetDescriptor<StaticElement> INSTALLMENT_FEE = declare("Installment Fee", StaticElement.class);
		}

		public static final class OverridePremiumDialog extends MetaData {
			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:overridePremiumLink"));
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for Override", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OTHER_REASON = declare("Other Reason", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ORIGINAL_BODILY_INJURY_OVERRIDE_PREMIUM_BY_FLAT_AMOUNT = declare("Bodily Injury Liability By Flat Amount", TextBox.class, Waiters.AJAX,
					By.xpath("//div[@id='premiumOverridePopup_content']//td[contains(.,'Bodily Injury Liability') and contains(.,'Original')]//following-sibling::td//input[contains(@id,'premiumOverrideInfoForm:deltaPremiumAmt')]"));
			public static final AssetDescriptor<TextBox> ORIGINAL_BODILY_INJURY_OVERRIDE_PREMIUM_BY_PERCENTAGE = declare("Bodily Injury Liability By Percentage", TextBox.class, Waiters.AJAX,
					By.xpath("//div[@id='premiumOverridePopup_content']//td[contains(.,'Bodily Injury Liability') and contains(.,'Original')]//following-sibling::td//input[contains(@id,'premiumOverrideInfoForm:percentageAmt')]"));
			public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.AJAX, false,
					By.id("premiumOverrideInfoForm:premiumOverrideSaveBtn"));
			public static final AssetDescriptor<Button> BUTTON_CANCEL_POPUP = declare(AbstractDialog.DEFAULT_POPUP_CLOSER_NAME, Button.class, Waiters.DEFAULT, false,
					By.id("premiumOverrideInfoForm:premiumOverrideCancelBtn"));
		}

		public static final class DetailedVehicleCoverages extends MetaData {
			public static final AssetDescriptor<ComboBox> COMPREGENSIVE_DEDUCTIBLE = declare("Comprehensive Deductible", ComboBox.class);
			public static final AssetDescriptor<AdvancedComboBox> FULL_SAFETY_GLASS = declare("Full Safety Glass", AdvancedComboBox.class);
			public static final AssetDescriptor<ComboBox> COLLISION_DEDUCTIBLE = declare("Collision Deductible", ComboBox.class);
			public static final AssetDescriptor<ComboBox> UNINSURED_MOTORIST_PROPERTY_DAMEGE = declare("Uninsured Motorist Property Damage", ComboBox.class);
			public static final AssetDescriptor<ComboBox> COLLISION_DEDUCTIBLE_WAIVER = declare("Collision Deductible Waiver", ComboBox.class);
			public static final AssetDescriptor<ComboBox> RENTAL_REIMBURSEMENT = declare("Rental Reimbursement", ComboBox.class);
			public static final AssetDescriptor<ComboBox> TOWING_AND_LABOR_COVERAGE = declare("Towing and Labor Coverage", ComboBox.class);
			public static final AssetDescriptor<TextBox> SPECIAL_EQUIPMENT_COVERAGE = declare("Special Equipment Coverage", TextBox.class);
			public static final AssetDescriptor<TextBox> SPECIAL_EQUIPMENT_DESCRIPTION = declare("Special Equipment Description", TextBox.class);

			public static final AssetDescriptor<AdvancedComboBox> ENHANCED_TRASPORTATION_EXPENCE = declare("Enhanced Transportation Expense", AdvancedComboBox.class);
			public static final AssetDescriptor<ComboBox> ALL_RISK = declare("All Risk", ComboBox.class);
			public static final AssetDescriptor<ComboBox> NEW_CAR_ADDED_PROTECTION = declare("New Car Added Protection", ComboBox.class);
			public static final AssetDescriptor<TextBox> PURCHASE_DATE = declare("Purchase Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> VEHICLE_LOAN_OR_LEASE_PROTECTION = declare("Vehicle Loan/Lease Protection", ComboBox.class);
			public static final AssetDescriptor<ComboBox> ORIGINAL_EQUIPMENT_MANUFACTURER_PARTS = declare("Original Equipment Manufacturer Parts", ComboBox.class);
			public static final AssetDescriptor<ComboBox> RIDESHARING_COVERAGE = declare("Ridesharing Coverage", ComboBox.class);
			// *** DO NOT DECLARE "Waive Liability" and "Vehicle Coverage" controls in this MetaData. They are added within DetailedVehicleCoveragesRepeatAssetList.class ***
		}

		public static final class DriverDiscountsRow extends MetaData {
			public static final AssetDescriptor<StaticElement> DRIVER = declare("Driver", StaticElement.class);
			public static final AssetDescriptor<CheckBox> GOOD_STUDENT = declare("Good Student", CheckBox.class);
			public static final AssetDescriptor<CheckBox> MATURE_DRIVER = declare("Mature Driver", CheckBox.class);
			public static final AssetDescriptor<CheckBox> SMART_DRIVER = declare("Smart Driver", CheckBox.class);
		}
	}

	public static final class DriverActivityReportsTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY = declare("Has the customer expressed interest in purchasing the policy?",
				RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> SALES_AGENT_AGREEMENT = declare("Sales Agent Agreement", RadioGroup.class, By.xpath(".//table[@id='policyDataGatherForm:FfcraPanel_5']"));
		public static final AssetDescriptor<RadioGroup> SALES_AGENT_AGREEMENT_DMV = declare("Sales Agent Agreement DMV", RadioGroup.class, By.xpath(".//table[@id='policyDataGatherForm:sedit_AAACSADriverReportDisclosureComponent_disclosureAgrees']"));
		public static final AssetDescriptor<Button> VALIDATE_DRIVING_HISTORY = declare("Validate Driving History", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:submitReports"));

		public static final AssetDescriptor<FillableTable> ORDER_CLUE_REPORT = declare("OrderCLUEReport", FillableTable.class, OrderClueRow.class, By
				.xpath("//table[@id='policyDataGatherForm:clueReports']"));
		public static final AssetDescriptor<FillableTable> ORDER_MVR_REPORT = declare("OrderMVRReport", FillableTable.class, OrderMVRRow.class, By
				.xpath("//table[@id='policyDataGatherForm:mvrReportsDataTable']"));
		public static final AssetDescriptor<FillableTable> ORDER_INTERNAL_CLAIMS_REPORT = declare("OrderInternalClaimsReport", FillableTable.class, OrderInternalClaimsRow.class, By
				.xpath("//table[@id='policyDataGatherForm:claimsReports']"));

		public static final class OrderClueRow extends MetaData {
			public static final AssetDescriptor<RadioGroup> SELECT = declare("Select", RadioGroup.class);
			public static final AssetDescriptor<StaticElement> RESIDENTIAL_ADDRESS = declare("Residential Address", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RESPONSE = declare("Response", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ADDRESS_TYPE = declare("Address Type", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_TYPE = declare("Order Type", StaticElement.class);
		}

		public static final class OrderMVRRow extends MetaData {
			public static final AssetDescriptor<RadioGroup> SELECT = declare("Select", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> FORCE_ORDER = declare("Force Order", RadioGroup.class);
			public static final AssetDescriptor<StaticElement> NAME_ON_LICENSE = declare("Name on License", StaticElement.class);
			public static final AssetDescriptor<StaticElement> DATE_OF_BIRTH = declare("Date of Birth", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATE = declare("State", StaticElement.class);
			public static final AssetDescriptor<StaticElement> LICENSE_NO = declare("License #", StaticElement.class);
			public static final AssetDescriptor<StaticElement> LICENSE_STATUS = declare("License Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RESPONSE = declare("Response", StaticElement.class);
		}

		public static final class OrderInternalClaimsRow extends MetaData {
			public static final AssetDescriptor<RadioGroup> SELECT = declare("Select", RadioGroup.class);
			public static final AssetDescriptor<StaticElement> NAME_ON_LICENSE = declare("Name on License", StaticElement.class);
			public static final AssetDescriptor<StaticElement> DATE_OF_BIRTH = declare("Date of Birth", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATE = declare("State", StaticElement.class);
			public static final AssetDescriptor<StaticElement> LICENSE_NO = declare("License #", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
		}
	}

	public static final class DocumentsAndBindTab extends MetaData {
		public static final AssetDescriptor<AssetList> DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAASSAdHocPrintDocs' or @id='policyDataGatherForm:componentView_AAAPasdocAdHocPrintDocs']"));
		public static final AssetDescriptor<AssetList> REQUIRED_TO_BIND = declare("RequiredToBind", AssetList.class, RequiredToBind.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_OptionalSupportingDocuments']"));
		public static final AssetDescriptor<AssetList> REQUIRED_TO_ISSUE = declare("RequiredToIssue", AssetList.class, RequiredToIssue.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_MandatorySupportingDocuments']"));
		public static final AssetDescriptor<VehicleMultiInstanceBeforeAssetList> VEHICLE_INFORMATION = declare("VehicleInformation", VehicleMultiInstanceBeforeAssetList.class,
				VehicleInformation.class);
		public static final AssetDescriptor<AssetList> PAPERLESS_PREFERENCES =
				declare("PaperlessPreferences", AssetList.class, AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAPaperlessPreferences']"));
		public static final AssetDescriptor<AssetList> DOCUMENT_PRINTING_DETAILS =
				declare("DocumentPrintingDetails", AssetList.class, AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PurchaseAction']"));

		// public static final AssetDescriptor<RadioGroup> AGREEMENT = declare("Agreement", RadioGroup.class, Waiters.AJAX, false,
		// By.xpath("//table[@id='policyDataGatherForm:AAADocAgreement_agreement']"));

		public static final AssetDescriptor<RadioGroup> MOTORCYCLE = declare("Motorcycle", RadioGroup.class);
		public static final AssetDescriptor<TextBox> MOTORCYCLE_POLICY_NUM = declare("Motorcycle Policy #", TextBox.class, By
				.id("policyDataGatherForm:sedit_AAAInsuredBindInformation_aaaExistingPoliciesDto_motorPolicyNumber"));
		public static final AssetDescriptor<RadioGroup> LIFE = declare("Life", RadioGroup.class);
		public static final AssetDescriptor<TextBox> LIFE_POLICY_NUM = declare("Life Policy #", TextBox.class, By
				.id("policyDataGatherForm:sedit_AAAInsuredBindInformation_aaaExistingPoliciesDto_lifePolicyNumber"));
		public static final AssetDescriptor<RadioGroup> HOME = declare("Home", RadioGroup.class);
		public static final AssetDescriptor<TextBox> HOME_POLICY_NUM = declare("Home Motorcycle Policy #", TextBox.class, By
				.id("policyDataGatherForm:sedit_AAAInsuredBindInformation_aaaExistingPoliciesDto_homePolicyNumber"));
		public static final AssetDescriptor<RadioGroup> RENTERS = declare("Renters", RadioGroup.class);
		public static final AssetDescriptor<TextBox> RENTERS_POLICY_NUM = declare("Renters Policy #", TextBox.class, By
				.id("policyDataGatherForm:sedit_AAAInsuredBindInformation_aaaExistingPoliciesDto_rentersPolicyNumber"));
		public static final AssetDescriptor<RadioGroup> CONDO = declare("Condo", RadioGroup.class);
		public static final AssetDescriptor<TextBox> CONDO_POLICY_NUM = declare("Condo Policy #", TextBox.class, By
				.id("policyDataGatherForm:sedit_AAAInsuredBindInformation_aaaExistingPoliciesDto_condoPolicyNumber"));
		public static final AssetDescriptor<RadioGroup> PUP = declare("PUP", RadioGroup.class);
		public static final AssetDescriptor<TextBox> PUP_POLICY_NUM = declare("PUP Motorcycle Policy #", TextBox.class, By
				.id("policyDataGatherForm:sedit_AAAInsuredBindInformation_aaaExistingPoliciesDto_pupPolicyNumber"));

		public static final AssetDescriptor<TextBox> WORK_PHONE_NUM = declare("Work Phone #", TextBox.class);
		public static final AssetDescriptor<TextBox> MOBILE_PHONE_NUM = declare("Mobile Phone #", TextBox.class);

		public static final AssetDescriptor<TextBox> CASE_NUMBER = declare("Case Number", TextBox.class);
		public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
		public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
		public static final AssetDescriptor<ComboBox> INCLUDE_WITH_EMAIL = declare("Include with Email", ComboBox.class);
		public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized By", TextBox.class, By.id("policyDataGatherForm:sedit_AAAInsuredBindInformation_authorizedBy"));

		public static final class DocumentsForPrinting extends MetaData {

			public static final AssetDescriptor<RadioGroup> AUTO_BILLING_PLAN_EXPLANATION = declare("Auto Billing Plan Explanation", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AUTO_QUOTE = declare("Auto Quote", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AUTOMATIC_PAYMENT_AUTHORIZATION = declare("Automatic Payment Authorization", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> CALIFORNIA_CAR_POLICY_APPLICATION = declare("California Car Policy Application", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> CAMPER_PHYSICAL_DAMAGE_COVERAGE_WAIVER = declare("Camper Physical Damage Coverage Waiver", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> DECLARATION_UNDER_PENALTY_OF_PERJURY = declare("Declaration Under Penalty of Perjury", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> OPERATOR_EXCLUSION_ENDORSEMENT_AND_UNINSURED_MOTORIST_COVERAGE = declare(
					"Operator Exclusion Endorsement and Uninsured Motorist Coverage Deletion Endorsement", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> UNINSURED_MOTORIST_COVERAGE_DELETION_OR_SELECTION_OF_LIMITS = declare(
					"Uninsured Motorist Coverage Deletion or Selection of Limits Agreement", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> SUBSCRIBER_AGREEMENT = declare("Subscriber Agreement", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AGREEMENT_DELETING_UNINSURED_MOTORIST_PROPERTY_DAMAGE_COVERAGE = declare("Agreement Deleting Uninsured Motorist Property Damage Coverage",
					RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AGREEMENT_DELETING_UNINSURED_UNDERINSURED_MoTORIST_BODILY_INJUSRY_COVERAGE = declare(
					"Agreement Deleting Uninsured/ Underinsured Motorist Bodily Injury Coverage", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AGREEMENT_REDUCING_UM_UIM_COVERAGE = declare("Agreement Reducing UM/UIM Coverage", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> APPLICATION_FOR_AUTO_INSURANCE = declare("Application for Auto Insurance", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AUTO_INSURANCE_QUOTE = declare("Auto Insurance Quote", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AUTOPAY_AUTHORIZATION_FORM = declare("AutoPay Authorization Form", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> FAX_MEMORANDUM = declare("Fax Memorandum", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> NON_OWNER_AUTOMOBILIST_ENDORSEMENT = declare("Non-Owner Automobile Endorsement", RadioGroup.class);
		}

		public static final class RequiredToBind extends MetaData {
			public static final AssetDescriptor<RadioGroup> CALIFORNIA_CAR_POLICY_APPLICATION = declare("California Car Policy Application", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> SUBSCRIBER_AGGREEMENT = declare("Subscriber Agreement", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> PERSONAL_AUTO_APPLICATION = declare("Personal Auto Application", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> DELETING_UNINSURED_MOTORIST_PROPERTY_DAMAGE_COVERAGE = declare("Deleting Uninsured Motorist Property Damage Coverage", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> REDUCING_UNINSURED_UNDERINSURED_MOTORIST_COVERAGE = declare("Reducing Uninsured/Underinsured Motorist Coverage", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> NON_OWNER_AUTOMOBILE_ENDORSEMENT = declare("Non-Owner Automobile Endorsement", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> NAMED_DRIVER_EXCLUSION = declare("Named Driver(s) Exclusion", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> OPERATOR_EXCLUSION_ENDORSEMENT_AND_UNINSURED_MOTORIST_COVERAGE_DELETION_ENDORSEMENT = declare(
					"Operator Exclusion Endorsement and Uninsured Motorist Coverage Deletion Endorsement", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> UNINSURED_MOTORIST_COVERAGE_DELETION_OR_SELECTION_OF_LIMITS_AGREEMENT = declare(
					"Uninsured Motorist Coverage Deletion or Selection of Limits Agreement", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> DECLARATION_UNDER_PENALTY_OF_PERJURY = declare("Declaration Under Penalty of Perjury", RadioGroup.class);

		}

		public static final class RequiredToIssue extends MetaData {
			public static final AssetDescriptor<RadioGroup> POLICY_APPLICATION = declare("Policy Application", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AUTO_BILLING_PLAN_EXPLANATION = declare("Auto Billing Plan Explanation", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_GOOD_STUDENT_DISCOUNT = declare("Proof of Good Student Discount", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_NEW_DRIVER_COURSE_COMPLETION = declare("Proof of New Driver Course Completion", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> CANADIAN_MVR_FOR_DRIVER = declare("Canadian MVR for (driver)", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_ANTI_THEFT_RECOVERY_DEVICE = declare("Proof of Anti-Theft Recovery Device", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> PHOTOS_SHOWING_ALL_4_SIDES_OF_SALVAGED_VEHICLES = declare("Photos showing all 4 sides of Salvaged Vehicle(s)", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_MATURE_DRIVER_COURSE_COMPLETION = declare("Proof of Mature Driver Course Completion", RadioGroup.class);
		}

		public static final class PaperlessPreferences extends MetaData {
			public static final AssetDescriptor<TextBox> ENROLLED_IN_PAPERLESS = declare("Enrolled in Paperless?", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> BTN_MANAGE_PAPERLESS_PREFERENCES = declare("Manage Paperless Preferences", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:paperlessPreferenceButton_AAAPaperlessPreferences"));
			public static final AssetDescriptor<Button> EDIT_PAPERLESS_PREFERENCES_BTN_DONE = declare("Done", Button.class, By.xpath("//input[@id='policyDataGatherForm:preferencesPopupCancel']"));
		}

		public static final class DocumentPrintingDetails extends MetaData {
			public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> INCLUDE_WITH_EMAIL = declare("Include with Email", ComboBox.class, Waiters.AJAX);
		}

		public static final class VehicleInformation extends MetaData {
			public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_ADDITIONAL_INTERESTS = declare("Are there any additional interest(s)?", RadioGroup.class, Waiters.AJAX);

			public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
			public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
			public static final AssetDescriptor<ComboBox> SEND_TO = declare("Send To", ComboBox.class);
			public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
			public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip/Postal Code", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<TextBox> STATE_PROVINCE = declare("State / Province", TextBox.class);
			public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
		}

	}

	// TODO done till this row
	public static final class GenerateProposalActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
	}

	public static final class DoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class PolicyDocGenActionTab extends MetaData {
		public static final AssetDescriptor<FillableDocumentsTable> ON_DEMAND_DOCUMENTS = declare("OnDemandDocuments", FillableDocumentsTable.class, DocumentsRow.class, By
				.xpath("(//div[@id='policyDataGatherForm:componentView_AAAAdHocOnDemandDocs' or @id='policyDataGatherForm:componentView_AAAPasdocAdHocOnDemandDocs']//table)[1]"));
		public static final AssetDescriptor<AdvancedRadioGroup> DELIVERY_METHOD = declare("Delivery Method", AdvancedRadioGroup.class, Waiters.AJAX, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAAdHocOnDemandDocs_body']/table"));
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class, Waiters.AJAX);

		public static final class DocumentsRow extends MetaData {
			public static final AssetDescriptor<CheckBox> SELECT = declare(DocGenConstants.OnDemandDocumentsTable.SELECT, CheckBox.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NUMBER = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NAME = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FREE_FORM_TEXT = declare("Free Form Text", TextBox.class, Waiters.AJAX, false, By.id("policyDataGatherForm:uwLetterMsg_AU03"));

			// CAU08
			public static final AssetDescriptor<CalendarBox> CAU08_NON_RENEWAL_DATE = declare("CAU08 Non-Renewal Date", CalendarBox.class, Waiters.AJAX, false,
					By.xpath("//input[@id='policyDataGatherForm:uwLetterCancDt_CAU08InputDate']"));
			public static final AssetDescriptor<TextBox> CAU08_FREE_FORM_TEXT = declare("CAU08 Free Form Text", TextBox.class, Waiters.AJAX, false,
					By.xpath("//textarea[@id='policyDataGatherForm:uwLetterMsg_CAU08']"));
		}
	}

	public static final class NonPremiumBearingEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> PRINCIPALROLECD = declare("principalRoleCd", TextBox.class);
		public static final AssetDescriptor<RadioGroup> PRIMARY_INSURED = declare("Primary Insured?", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> NAME_TYPE = declare("Name Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> NAME_PREFIX = declare("Name Prefix", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> ADDITIONAL_NAME = declare("Additional Name", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
		public static final AssetDescriptor<TextBox> PHONE_TYPE = declare("Phone Type", TextBox.class);
		public static final AssetDescriptor<TextBox> PHONE = declare("Phone #", TextBox.class);
		public static final AssetDescriptor<TextBox> EMAIL_TYPE = declare("Email Type", TextBox.class);
		public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class);
		public static final AssetDescriptor<TextBox> SECOND_NAME = declare("Second Name", TextBox.class);
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class);
		public static final AssetDescriptor<ComboBox> TYPE_OF_INTEREST = declare("Type of Interest", ComboBox.class);
		public static final AssetDescriptor<TextBox> LOAN = declare("Loan #", TextBox.class);
		public static final AssetDescriptor<TextBox> MORTGAGE_AMOUNT = declare("Mortgage Amount", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS = declare("Address", TextBox.class);
	}

	public static final class CopyPolicyActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class DeletePendedTransactionActionTab extends MetaData {
	}

	public static final class ChangePendedEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
	}

	public static final class DeclineActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class SpinActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON_FOR_SPIN = declare("Reason for Spin", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> INSUREDS_APPROVE_REMOVAL_OF_DRIVER_AND_VEHICLES = declare("Insureds approve removal of drivers and vehicles", RadioGroup.class);
	}

	public static final class QuoteTransferActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CUSTOMER_NUMBER = declare("Customer Number", TextBox.class);
		public static final AssetDescriptor<TextBox> CUSTOMER_NAME = declare("Customer Name", TextBox.class);
	}

	public static final class RewriteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
	}

	public static final class CopyQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class RescindCancellationActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> RESCIND_CANCELLATION_DATE = declare("Rescind Cancellation Date", TextBox.class);
	}

	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	public static final class GenerateOnDemandDocumentActionTab extends MetaData {
		public static final AssetDescriptor<FillableDocumentsTable> ON_DEMAND_DOCUMENTS =
				declare("OnDemandDocuments", FillableDocumentsTable.class, DocumentsRow.class, By.xpath("(//div[@id='policyDataGatherForm:componentView_AAAAdHocOnDemandDocs' or @id='policyDataGatherForm:componentView_AAAPasdocAdHocOnDemandDocs']//table)[1]"));
		public static final AssetDescriptor<AdvancedRadioGroup> DELIVERY_METHOD =
				declare("Delivery Method", AdvancedRadioGroup.class, Waiters.AJAX, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAAdHocOnDemandDocs_body']/table"));
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class, Waiters.AJAX);

		public static final class DocumentsRow extends MetaData {
			public static final AssetDescriptor<CheckBox> SELECT = declare(DocGenConstants.OnDemandDocumentsTable.SELECT, CheckBox.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NUMBER = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NAME = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FREE_FORM_TEXT = declare("Free Form Text", TextBox.class, Waiters.AJAX, false, By.id("policyDataGatherForm:uwLetterMsg_AU03"));
		}
	}

	public static final class CancellationActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancel Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> OTHER_REASON = declare("Other reason", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
		public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized By", TextBox.class);
	}

	public static final class IssueSummaryActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SEND_TO = declare("Send To", ComboBox.class);
		public static final AssetDescriptor<TextBox> BROKER_EMAIL = declare("Broker Email", TextBox.class);
		public static final AssetDescriptor<TextBox> INSURED_EMAIL = declare("Insured Email", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS = declare("Address", TextBox.class);
		public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
	}

	public static final class DeleteCancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	public static final class RemoveManualRenewFlagActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	public static final class CancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
        public static final AssetDescriptor<TextBox> DAYS_OF_NOTICE = declare("Days Of Notice", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> SUPPORTING_DATA = declare("Supporting Data", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> PRINT_NOTICE = declare("Print Notice", RadioGroup.class);
	}

	public static final class ChangeBrokerActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TRANSFER_ID = declare("Transfer ID", TextBox.class);
		public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class);
		public static final AssetDescriptor<TextBox> TRANSFER_TYPE = declare("Transfer type", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_REASON = declare("Other Reason", TextBox.class);
		public static final AssetDescriptor<TextBox> TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", TextBox.class);
		public static final AssetDescriptor<TextBox> TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
		public static final AssetDescriptor<RadioGroup> COMMISSION_IMPACT = declare("Commission impact", RadioGroup.class);
		public static final AssetDescriptor<TextBox> SOURCE_CHANNEL = declare("Source Channel", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_LOCATION_TYPE = declare("Source Location Type", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_LOCATION_NAME = declare("Source Location Name", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_INSURANCE_AGENT = declare("Source Insurance Agent", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_CHANNEL = declare("Target channel", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_LOCATION_TYPE = declare("Target Location Type", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_LOCATION_NAME = declare("Target Location Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> TARGET_INSURANCE_AGENT = declare("Target Insurance agent", ComboBox.class);
	}

	public static final class AddManualRenewFlagActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	public static final class RemoveDoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<TextBox> REASON = declare("Reason", TextBox.class);
		public static final AssetDescriptor<TextBox> DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", TextBox.class);
	}

	public static final class ReinstatementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized By", TextBox.class);
	}

	public static final class SplitActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON_FOR_SPLIT = declare("Reason for Split", ComboBox.class);
	}

	public static final class EndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class);
	}

	public static final class RenewActionTab extends MetaData {
		public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
		public static final AssetDescriptor<TextBox> RENEWAL_DATE = declare("Renewal Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REASON_FOR_RENEWAL_WITH_LAPSE = declare("Reason for Renewal with Lapse", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class, Waiters.AJAX);
	}

	public static final class UpdateRulesOverrideActionTab extends MetaData {
		public static final AssetDescriptor<FillableTable> UPDATE_RULES_OVERRIDE = declare("UpdateRulesOverride", FillableTable.class, RuleRow.class, By.id("errorsForm:msgList"));

		public static final class RuleRow extends MetaData {
			public static final AssetDescriptor<CheckBox> UPDATE = declare("Update All", CheckBox.class);
			public static final AssetDescriptor<CheckBox> AUTHORIZE = declare("Authorize/Delete All", CheckBox.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<RadioGroup> DURATION = declare("Duration", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class);
			public static final AssetDescriptor<StaticElement> RULE_NAME = declare("Rule name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MESSAGE = declare("Message", StaticElement.class);
		}
	}

	public static final class ErrorTab extends MetaData {
		public static final AssetDescriptor<FillableErrorTable> ERROR_OVERRIDE = declare("ErrorsOverride", FillableErrorTable.class, ErrorsOverride.class, By.id("errorsForm:msgList"));

		public static final class ErrorsOverride extends MetaData {
			public static final AssetDescriptor<CheckBox> OVERRIDE = declare("Override", CheckBox.class);
			public static final AssetDescriptor<CheckBox> APPROVAL = declare("Approval", CheckBox.class);
			public static final AssetDescriptor<Link> CODE = declare("Code", Link.class);
			public static final AssetDescriptor<StaticElement> SEVERITY = declare("Severity", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MESSAGE = declare("Message", StaticElement.class);
			public static final AssetDescriptor<RadioGroup> DURATION = declare("Duration", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class);
		}
	}

	public static final class RollOnChangesActionTab extends MetaData {
	}

	public static final class DifferencesActionTab extends MetaData {
	}

	public static final class CreateQuoteVersionTab extends MetaData {
		public static final AssetDescriptor<StaticElement> VERSION_NUM = declare("Version #", StaticElement.class);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
	}

}
