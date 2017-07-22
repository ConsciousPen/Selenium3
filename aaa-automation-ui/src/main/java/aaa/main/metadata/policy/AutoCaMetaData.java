/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata.policy;

import org.openqa.selenium.By;

import aaa.main.metadata.DialogsMetaData;
import aaa.toolkit.webdriver.customcontrols.ComboBoxFixed;
import aaa.toolkit.webdriver.customcontrols.DetailedVehicleCoveragesRepeatAssetList;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import aaa.toolkit.webdriver.customcontrols.VehicleMultiInstanceBeforeAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.AssetListConfirmationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import aaa.toolkit.webdriver.customcontrols.endorsements.AutoCAForms;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
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
		public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
		public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
		public static final AttributeDescriptor ADRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddressButton"));
		public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAPrefillAddressValidation_container']"));
		public static final AttributeDescriptor ORDER_PREFILL = declare("Order Prefill", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:orderPrefillButton"));
	}

	public static final class GeneralTab extends MetaData {

		public static final AttributeDescriptor AAA_PRODUCT_OWNED = declare("AAAProductOwned", AssetList.class, AAAProductOwned.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_ExistingPolicies']"));
		public static final AttributeDescriptor CURRENT_CARRIER_INFORMATION = declare("CurrentCarrierInformation", AssetList.class, CurrentCarrierInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_OtherOrPriorPolicy']"));
		public static final AttributeDescriptor POLICY_INFORMATION = declare("PolicyInformation", AssetList.class, PolicyInformation.class);

		public static final AttributeDescriptor NAMED_INSURED_INFORMATION = declare("NamedInsuredInformation", MultiInstanceAfterAssetList.class, NamedInsuredInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_InsuredInformationMVO']"));
		public static final AttributeDescriptor FIRST_NAMED_INSURED = declare("First Named Insured", ComboBox.class);

		public static final AttributeDescriptor THIRD_PARTY_DESIGNEE_INFORMATION = declare("ThirdPartyDesigneeInformation", AssetList.class, ThirdPartyDesigneeInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAAutoThirdPartyDesignee']"));

		public static final class NamedInsuredInformation extends MetaData {
			public static final AttributeDescriptor ADD_INSURED = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addInsured"));
			public static final AttributeDescriptor INSURED_SEARCH_DIALOG = declare("InsuredSearchDialog", SingleSelectSearchDialog.class, DialogsMetaData.DialogSearch.class, false, By.id("customerSearchPanel_container"));

			public static final AttributeDescriptor PREFIX = declare("Prefix", ComboBox.class);
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
			public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
			public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
			public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
			//public static final AttributeDescriptor SOCIAL_SECURITY_NUMBER = declare("Social Security Number", TextBox.class);
			public static final AttributeDescriptor BASE_DATE = declare("Base Date", TextBox.class);
			public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", ComboBox.class);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class);

			public static final AttributeDescriptor HAS_LIVED_LESS_THAN_3_YEARS = declare("Has lived here for less than three years?", RadioGroup.class);
			public static final AttributeDescriptor PRIOR_MOVE_IN_DATE = declare("Move-In Date", TextBox.class);
			public static final AttributeDescriptor PRIOR_ADDRESS_TYPE = declare("Prior Address Type", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_usageTypeCd"));
			public static final AttributeDescriptor PRIOR_ZIP_CODE = declare("Prior Zip Code", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_postalCode"));
			public static final AttributeDescriptor PRIOR_ADDRESS_LINE_1 = declare("Prior Address Line 1", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_addressLine1"));
			public static final AttributeDescriptor PRIOR_ADDRESS_LINE_2 = declare("Prior Address Line 2", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_addressLine2"));
			public static final AttributeDescriptor PRIOR_CITY = declare("Prior City", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_city"));
			public static final AttributeDescriptor PRIOR_STATE = declare("Prior State", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_stateProvCd"));

			public static final AttributeDescriptor IS_RESIDENTAL_DIFFERENF_FROM_MAILING = declare("Is residential address different from mailing address?", RadioGroup.class);
			public static final AttributeDescriptor MAILING_ADDRESS_TYPE = declare("Mailing Address Type", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_usageTypeCd"));
			public static final AttributeDescriptor MAILING_ZIP_CODE = declare("Mailing Zip Code", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_postalCode"));
			public static final AttributeDescriptor MAILING_ADDRESS_LINE_1 = declare("Mailing Address Line 1", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_addressLine1"));
			public static final AttributeDescriptor MAILING_ADDRESS_LINE_2 = declare("Mailing Address Line 2", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_addressLine2"));
			public static final AttributeDescriptor MAILING_CITY = declare("Mailing City", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_city"));
			public static final AttributeDescriptor MAILING_STATE = declare("Mailing State", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_stateProvCd"));

			public static final AttributeDescriptor HOME_PHONE_NUMBER = declare("Home Phone #", TextBox.class);
			public static final AttributeDescriptor WORK_PHONE_NUMBER = declare("Work Phone #", TextBox.class);
			public static final AttributeDescriptor MOBILE_PHONE_NUMBER = declare("Mobile Phone #", TextBox.class);
			//public static final AttributeDescriptor PREFERED_PHONE_NUMBER = declare("Preferred Phone #", ComboBox.class);
			public static final AttributeDescriptor EMAIL = declare("Email", TextBox.class);
			public static final AttributeDescriptor RESIDENCE = declare("Residence", ComboBox.class);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidation.class, By.id(".//*[@id='addressValidationPopupAAAInsuredAddressValidation_container']"));

			public static final class AddressValidation extends MetaData {
				public static final AttributeDescriptor ADDRESS_IS_PO_BOX = declare("Address is PO Box", CheckBox.class, Waiters.AJAX);

				public static final AttributeDescriptor STREET_NUMBER = declare("Street number", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor STREET_NAME = declare("Street Name", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor UNIT_NUMBER = declare("Unit number", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor RADIOGROUP_SELECT = declare("Select Address", RadioGroup.class, Waiters.AJAX);

				public static final AttributeDescriptor MAILING_STREET_NUMBER = declare("Mailing Street number", TextBox.class, Waiters.AJAX, false, By.id("addressValidationFormAAAInsuredAddressValidation:secondaryStreetNumberInput"));
				public static final AttributeDescriptor MAILING_STREET_NAME = declare("Mailing Street Name", TextBox.class, Waiters.AJAX, false, By.id("addressValidationFormAAAInsuredAddressValidation:secondaryStreetNameInput"));
				public static final AttributeDescriptor MAILING_UNIT_NUMBER = declare("Mailing Unit number", TextBox.class, Waiters.AJAX, false, By.id("addressValidationFormAAAInsuredAddressValidation:secondaryUnitNumberInput"));
				public static final AttributeDescriptor MAILING_RADIOGROUP_SELECT = declare("Mailing Select Address", RadioGroup.class, Waiters.AJAX);

				public static final AttributeDescriptor BTN_OK = declare("Ok", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='OK']"));
				public static final AttributeDescriptor BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Cancel']"));
			}
		}

		public static final class AAAProductOwned extends MetaData {
			public static final AttributeDescriptor CURRENT_AAA_MEMBER = declare("Current AAA Member", RadioGroup.class);
			public static final AttributeDescriptor MEMBERSHIP_NUMBER = declare("Membership Number", TextBox.class);
			public static final AttributeDescriptor MEMBER_LAST_NAME = declare("Member Last Name", TextBox.class);
			public static final AttributeDescriptor MOTORCYCLE = declare("Motorcycle", RadioGroup.class);
			public static final AttributeDescriptor MOTORCYCLE_POLICY_NUM = declare("Motorcycle Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_motorPolicyNumber"));
			public static final AttributeDescriptor LIFE = declare("Life", RadioGroup.class);
			public static final AttributeDescriptor LIFE_POLICY_NUM = declare("Life Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_lifePolicyNumber"));
			public static final AttributeDescriptor HOME = declare("Home", RadioGroup.class);
			public static final AttributeDescriptor HOME_POLICY_NUM = declare("Home Motorcycle Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_homePolicyNumber"));
			public static final AttributeDescriptor RENTERS = declare("Renters", RadioGroup.class);
			public static final AttributeDescriptor RENTERS_POLICY_NUM = declare("Renters Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_rentersPolicyNumber"));
			public static final AttributeDescriptor CONDO = declare("Condo", RadioGroup.class);
			public static final AttributeDescriptor CONDO_POLICY_NUM = declare("Condo Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_condoPolicyNumber"));
			public static final AttributeDescriptor PUP = declare("PUP", RadioGroup.class);
			public static final AttributeDescriptor PUP_POLICY_NUM = declare("PUP Motorcycle Policy #", TextBox.class, By.id("policyDataGatherForm:sedit_ExistingPolicies_pupPolicyNumber"));
			public static final AttributeDescriptor ATTRIBUTE_FOR_RULES = declare("Attribute for rules", TextBox.class);
		}

		public static final class CurrentCarrierInformation extends MetaData {
			public static final AttributeDescriptor CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class);
			public static final AttributeDescriptor OTHER_CARRIER = declare("Other Carrier", TextBox.class);
			public static final AttributeDescriptor POLICY_NUMBER = declare("Policy Number", TextBox.class);
			public static final AttributeDescriptor INCEPTION_DATE = declare("Inception Date", TextBox.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
			public static final AttributeDescriptor MONTHS_WITH_CARRIER = declare("Months with Carrier", TextBox.class);
			public static final AttributeDescriptor DAYS_LAPSED = declare("Days Lapsed", TextBox.class);
			public static final AttributeDescriptor BI_LIMITS = declare("BI Limits", ComboBox.class);
			public static final AttributeDescriptor OVERRIDE_CURRENT_CARRIER = declare("Override Prefilled Current Carrier?", RadioGroup.class);
			public static final AttributeDescriptor AGENT_ENTERED_CURRENT_PRIOR_CARRIER = declare("Agent Entered Current/Prior Carrier", ComboBox.class);
			public static final AttributeDescriptor AGENT_ENTERED_INCEPTION_DATE = declare("Agent Entered Inception Date", TextBox.class);
			public static final AttributeDescriptor AGENT_ENTERED_EXPIRATION_DATE = declare("Agent Entered Expiration Date", TextBox.class);
			public static final AttributeDescriptor AGENT_ENTERED_POLICY_NUMBER = declare("Agent Entered Policy Number", TextBox.class);
			public static final AttributeDescriptor AGENT_ENTERED_DAYS_LAPSED = declare("Agent Entered Days Lapsed", TextBox.class, By.id("policyDataGatherForm:currentCarrierInformation_enteredDaysLapsed"));
			public static final AttributeDescriptor AGENT_ENTERED_MONTHS_WITH_CARRIER = declare("Agent Entered Months with Carrier", TextBox.class, By.id("policyDataGatherForm:currentCarrierInformation_enteredMonthsWithInsurer"));
			public static final AttributeDescriptor AGENT_ENTERED_BI_LIMITS = declare("Agent Entered BI Limits", TextBox.class);
		}

		public static final class PolicyInformation extends MetaData {
			public static final AttributeDescriptor SOURCE_OF_BUSINESS = declare("Source of Business", ComboBox.class);
			public static final AttributeDescriptor POLICY_STATE = declare("Policy State", ComboBox.class);
			public static final AttributeDescriptor POLICY_TYPE = declare("Policy Type", ComboBox.class);
			public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
			//public static final AttributeDescriptor POLICY_TERM = declare("Policy Term", ComboBox.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
			public static final AttributeDescriptor CHANNEL_TYPE = declare("Channel Type", ComboBox.class);
			public static final AttributeDescriptor AGENCY = declare("Agency", ComboBox.class);
			public static final AttributeDescriptor AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class);
			public static final AttributeDescriptor AGENCY_LOCATION = declare("Agency Location", ComboBox.class);
			public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
			public static final AttributeDescriptor AGENT = declare("Agent", ComboBox.class);
			public static final AttributeDescriptor AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class);
			public static final AttributeDescriptor AGENT_NUMBER = declare("Agent Number", TextBox.class);
			public static final AttributeDescriptor COMISSION_TYPE = declare("Commission Type", ComboBox.class);
			public static final AttributeDescriptor AUTHORIZED_BY = declare("Authorized by", TextBox.class);
			public static final AttributeDescriptor TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class);
			public static final AttributeDescriptor LANGUAGE_PREFERENCE = declare("Language Preference", RadioGroup.class);
			public static final AttributeDescriptor SUPPRESS_PRINT = declare("Suppress Print", TextBox.class);
		}

		public static final class ThirdPartyDesigneeInformation extends MetaData {
			public static final AttributeDescriptor IS_THERE_ANY_THIRD_PATRY_DESIGNEE = declare("Is there any Third Party Designee?", RadioGroup.class);
			public static final AttributeDescriptor NAME = declare("Name", TextBox.class);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class);

		}
	}

	public static final class DriverTab extends MetaData {

		public static final AttributeDescriptor ADD_DRIVER = declare("Add Driver", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addDriver"));
		public static final AttributeDescriptor DRIVER_SEARCH_DIALOG = declare("DriverSearchDialog", SingleSelectSearchDialog.class, DialogsMetaData.DialogSearch.class, false, By.id("customerSearchPanel_container"));

		public static final AttributeDescriptor NAMED_INSURED = declare("Named Insured", ComboBox.class);
		public static final AttributeDescriptor DRIVER_TYPE = declare("Driver Type", ComboBox.class);
		public static final AttributeDescriptor REL_TO_FIRST_NAMED_INSURED = declare("Rel. to First Named Insured", ComboBox.class);
		public static final AttributeDescriptor TITLE = declare("Title", TextBox.class);
		public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
		public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
		public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
		public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AttributeDescriptor AGE = declare("Age", TextBox.class);
		public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class);
		public static final AttributeDescriptor MARITAL_STATUS = declare("Marital Status", ComboBox.class);
		public static final AttributeDescriptor OCCUPATION = declare("Occupation", ComboBox.class);
		public static final AttributeDescriptor BASE_DATE = declare("Base Date", TextBox.class);
		public static final AttributeDescriptor AGE_FIRST_LICENSED = declare("Age First Licensed", TextBox.class);
		public static final AttributeDescriptor PERMIT_BEFORE_LICENSE = declare("Permit Before License", RadioGroup.class);
		public static final AttributeDescriptor TOTAL_YEAR_DRIVING_EXPERIENCE = declare("Total Years Driving Experience", TextBox.class);
		public static final AttributeDescriptor LICENSED_IN_US_CANADA_FOR_18_OR_MORE_MONTHS = declare("Licensed in US/Canada for 18 or More Months?", RadioGroup.class);
		public static final AttributeDescriptor LICENSE_STATE = declare("License State", ComboBox.class);
		public static final AttributeDescriptor LICENSE_NUMBER = declare("License #", TextBox.class);

		public static final AttributeDescriptor EMPLOYEE_BENEFIT_TYPE = declare("Employee Benefit Type", ComboBox.class);
		public static final AttributeDescriptor ADB_COVERAGE = declare("ADB Coverage", RadioGroup.class);
		public static final AttributeDescriptor MOST_RECENT_GPA = declare("Most Recent GPA", ComboBox.class);
		public static final AttributeDescriptor GOOD_DRIVER_DISCOUNT = declare("Good Driver Discount", RadioGroup.class);
		public static final AttributeDescriptor NEW_DRIVER_COURSE_COMPLETED = declare("New Driver Course Completed", RadioGroup.class);
		public static final AttributeDescriptor FINANCIAL_RESPONSIBILITY_FILING_NEEDED = declare("Financial Responsibility Filing Needed", RadioGroup.class);
		public static final AttributeDescriptor SMOKER_CIGARETTES_OR_PIPES = declare("Smoker: Cigarettes, cigars or pipes", RadioGroup.class);
		public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);

		public static final AttributeDescriptor ACTIVITY_INFORMATION = declare("ActivityInformation", MultiInstanceBeforeAssetList.class, ActivityInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_DrivingRecord']"));

		public static final class ActivityInformation extends MetaData {
			public static final AttributeDescriptor ADD_ACTIVITY = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addDrivingRecord"));
			public static final AttributeDescriptor ORIGINAL_CONVICTION_DATE = declare("Original Conviction Date", TextBox.class);
			public static final AttributeDescriptor ACTIVITY_SOURCE = declare("Activity Source", ComboBox.class);
			public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
			public static final AttributeDescriptor OCCURENCE_DATE = declare("Occurrence Date", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", ComboBox.class);
			public static final AttributeDescriptor CLAIM_NUMBER = declare("Claim Number", TextBox.class);
			public static final AttributeDescriptor INCLUDE_IN_RATING = declare("Include in Rating?", RadioGroup.class);
			public static final AttributeDescriptor NOT_INCLUDED_IN_RATING_REASON = declare("Not Included in Rating Reasons", ComboBox.class);
			public static final AttributeDescriptor ACTIVITY_REMOVE_CONFIRMATION = declare("Activity remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_container"));
		}

	}

	public static final class MembershipTab extends MetaData {
		public static final AttributeDescriptor ORDER_REPORT = declare("Order Report", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:submitReports"));
		public static final AttributeDescriptor AAA_MEMBERSHIP_REPORT = declare("AAAMembershipReport", FillableTable.class, AaaMembershipReportRow.class, By.xpath("//table[@id='policyDataGatherForm:membershipReports']"));

		public static final class AaaMembershipReportRow extends MetaData {
			public static final AttributeDescriptor SELECT = declare("Select", RadioGroup.class);
			public static final AttributeDescriptor LAST_NAME = declare("Last Name", StaticElement.class);
			public static final AttributeDescriptor MEMBERSHIP_NO = declare("Membership No.", StaticElement.class);
			public static final AttributeDescriptor MEMBER_SINCE_DATE = declare("Member Since Date", StaticElement.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AttributeDescriptor STATUS = declare("Status", StaticElement.class);
			public static final AttributeDescriptor ACTION = declare("Action", Link.class);
		}
	}

	public static final class VehicleTab extends MetaData {
		public static final AttributeDescriptor ADD_VEHICLE = declare("Add Vehicle", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addVehicle"));

		public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
		public static final AttributeDescriptor VIN = declare("VIN", TextBox.class);
		public static final AttributeDescriptor CHOOSE_VIN = declare("Choose VIN", ComboBox.class);
		public static final AttributeDescriptor VIN_MATCHED = declare("VIN Matched", StaticElement.class);
		public static final AttributeDescriptor YEAR = declare("Year", TextBox.class);
		public static final AttributeDescriptor MAKE = declare("Make", ComboBox.class);
		public static final AttributeDescriptor MODEL = declare("Model", ComboBox.class);
		public static final AttributeDescriptor SERIES = declare("Series", ComboBox.class);
		public static final AttributeDescriptor BODY_STYLE = declare("Body Style", ComboBox.class);
		public static final AttributeDescriptor PRIMARY_USE = declare("Primary Use", ComboBox.class);
		public static final AttributeDescriptor OTHER_MAKE = declare("Other Make", TextBox.class);
		public static final AttributeDescriptor OTHER_MODEL = declare("Other Model", TextBox.class);
		public static final AttributeDescriptor OTHER_SERIES = declare("Other Series", TextBox.class);
		public static final AttributeDescriptor OTHER_BODY_STYLE = declare("Other Body Style", TextBox.class);

		public static final AttributeDescriptor STAT_CODE = declare("Stat Code", ComboBox.class);
		public static final AttributeDescriptor VALUE = declare("Value($)", TextBox.class);
		public static final AttributeDescriptor COBP_COLL_SYMBOL = declare("Comp/Coll Symbol", TextBox.class);
		public static final AttributeDescriptor AIR_BAGS = declare("Air Bags", ComboBox.class);
		public static final AttributeDescriptor ANTI_THEFT = declare("Anti-theft", ComboBox.class);
		public static final AttributeDescriptor ALTERNATIVE_FUEL_VEHICLE = declare("Alternative Fuel Vehicle", RadioGroup.class);
		public static final AttributeDescriptor ANTI_THEFT_RECOVERY_DEVICE = declare("Anti-theft Recovery Device", ComboBox.class);
		public static final AttributeDescriptor ANTI_LOCK_BRAKES = declare("Anti-Lock Brakes", ComboBox.class);
		public static final AttributeDescriptor EXISTING_DAMEGE = declare("Existing Damage", ComboBox.class);
		public static final AttributeDescriptor SALVAGED = declare("Salvaged?", RadioGroup.class);
		public static final AttributeDescriptor MILES_ONE_WAY_TO_WORK_OR_SCHOOL = declare("Miles One-way to Work or School", TextBox.class);
		public static final AttributeDescriptor ODOMETER_READING = declare("Odometer Reading", TextBox.class);
		public static final AttributeDescriptor ODOMETER_READING_DATE = declare("Odometer Reading Date", TextBox.class);
		public static final AttributeDescriptor CUSTOMER_DECLARED_ANNUAL_MILES = declare("Customer Declared Annual Miles", TextBox.class);
		public static final AttributeDescriptor SOURCE = declare("Source", TextBox.class);
		public static final AttributeDescriptor CALCULATE_MILEAGE = declare("Calculate Mileage", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:calculateMileage"));

		/*public static final AttributeDescriptor ENROLL_IN_USAGE_BASED_INSURANCE = declare("Enroll in Usage Based Insurance?", RadioGroup.class);
		public static final AttributeDescriptor GET_VEHICLE_DETAILS = declare("Get Vehicle Details", Button.class, By.id("policyDataGatherForm:vehicleUBIDetaiilsButton_AAATelematicDeviceInfo"));
		public static final AttributeDescriptor VEHICLE_ELIGIBILITY_RESPONCE = declare("Vehicle Eligibility Response", ComboBox.class);
		public static final AttributeDescriptor AAA_UBI_DEVICE_STATUS = declare("AAA UBI Device Status", ComboBox.class);
		public static final AttributeDescriptor AAA_UBI_DEVICE_STATUS_DATE = declare("AAA UBI Device Status Date", TextBox.class);
		public static final AttributeDescriptor DEVICE_VOUCHER_NUMBER = declare("Device Voucher Number", TextBox.class);
		public static final AttributeDescriptor SAFETY_SCORE = declare("Safety Score", TextBox.class);
		public static final AttributeDescriptor SAFETY_SCORE_DATE = declare("Safety Score Date", TextBox.class);
		public static final AttributeDescriptor GRANT_PATRITIPATION_DISCOUNT = declare("Grant Participation Discount", Link.class, By.id("policyDataGatherForm:grantParticipationDiscountLink"));*/

		public static final AttributeDescriptor IS_GARAGING_DIFFERENT_FROM_RESIDENTAL = declare("Is Garaging different from Residential?", RadioGroup.class);
		public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE = declare("State", ComboBox.class);
		public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateGaragingAddressButton"));
		public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAGaragingAddressValidation_container']"));
		public static final AttributeDescriptor OWNERSHIP = declare("Ownership", AssetList.class, Ownership.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAVehicleOwnership']"));
		public static final AttributeDescriptor ARE_THERE_ANY_ADDITIONAL_INTERESTS = declare("Are there any additional interest(s)?", RadioGroup.class);
		public static final AttributeDescriptor ADDITIONAL_INTEREST_INFORMATION = declare("AdditionalInterestInformation", MultiInstanceAfterAssetList.class, AdditionalInterestInformation.class,
				By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAAdditionalInterest']"));

		public static final class Ownership extends MetaData {
			public static final AttributeDescriptor OWNERSHIP_TYPE = declare("Ownership Type", ComboBox.class);
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", ComboBox.class);
			public static final AttributeDescriptor SECOND_NAME = declare("Second Name", TextBox.class);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", RadioGroup.class);
			public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateOwnershipAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressOwnershipValidationPopup_container']"));
		}

		public static final class AdditionalInterestInformation extends MetaData {
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
			public static final AttributeDescriptor SECOND_NAME = declare("Second Name", TextBox.class);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", RadioGroup.class);
			public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddlnInterestAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressAddlnInterestValidationPopup_container']"));
			public static final AttributeDescriptor ADD_ADDITIONAL_INTEREST = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAAdditionalInterest"));
		}

	}

	public static final class AssignmentTab extends MetaData {
		public static final AttributeDescriptor DRIVER_VEHICLE_RELATIONSHIP = declare("DriverVehicleRelationshipTable", FillableTable.class, DriverVehicleRelationshipTableRow.class, By.xpath("//table[@id='policyDataGatherForm:driverVehicleRelationshipSummary']"));

		public static final class DriverVehicleRelationshipTableRow extends MetaData {
			public static final AttributeDescriptor VEHICLE = declare("Vehicle", StaticElement.class, Waiters.NONE);
			public static final AttributeDescriptor PRIMARY_DRIVER = declare("Primary Driver", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SYSTEM_RATED_DRIVER = declare("System Rated Driver", StaticElement.class, Waiters.NONE);
			public static final AttributeDescriptor MANUALLY_RATED_DRIVER = declare("Manually Rated Driver", ComboBox.class, Waiters.AJAX);
		}
	}

	public static final class FormsTab extends MetaData {
		
		public static final AttributeDescriptor POLICY_FORMS = declare("Policy Forms", AutoCAForms.AutoCAPolicyFormsController.class, PolicyForms.class);
		public static final AttributeDescriptor VEHICLE_FORMS = declare("Vehicle Forms", AutoCAForms.AutoCAVehicleFormsController.class, VehicleForms.class);
		public static final AttributeDescriptor DRIVER_FORMS = declare("Driver Forms", AutoCAForms.AutoCADriverFormsController.class, DriverForms.class);
		
		public static final class PolicyForms extends MetaData {
			public static final AttributeDescriptor AA53CA = declare("AA53CA", AssetList.class, CommonFormMeta.class, false, By.id("addComponentPopup_PolicyEndorsementFormsManager_container"));
			public static final AttributeDescriptor APPAE = declare("APPAE", AssetList.class, CommonFormMeta.class, false, By.id("addComponentPopup_PolicyEndorsementFormsManager_container"));
		}
		
		public static final class VehicleForms extends MetaData {
//			formAssetLocator = By.id("addComponentPopup_VehicleEndorsementFormsManager_container");
		}
		
		public static final class DriverForms extends MetaData {
//			formAssetLocator = By.id("addComponentPopup_DriverEndorsementFormsManager_container");
		}
		
		public static final class CommonFormMeta extends MetaData {
			public static final AttributeDescriptor FORM_CODE = declare("Form Code", TextBox.class);
			public static final AttributeDescriptor FORM_DESCRIPTION = declare("Form Description", TextBox.class);
		}
	}

	public static final class PremiumAndCoveragesTab extends MetaData {
		public static final AttributeDescriptor PRODUCT = declare("Product", ComboBox.class);
		public static final AttributeDescriptor PAYMENT_PLAN = declare("Payment Plan", ComboBox.class);
		public static final AttributeDescriptor POLICY_TERM = declare("Policy Term", ComboBox.class);
		public static final AttributeDescriptor BODILY_INJURY_LIABILITY = declare("Bodily Injury Liability", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policyCoverageDetail']//span[normalize-space(.)='Bodily Injury Liability']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor PROPERTY_DAMAGE_LIABILITY = declare("Property Damage Liability", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policyCoverageDetail']//span[normalize-space(.)='Property Damage Liability']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor UNINSURED_MOTORISTS_BODILY_INJURY =
				declare("Uninsured Motorists Bodily Injury", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policyCoverageDetail']//span[normalize-space(.)='Uninsured Motorists Bodily Injury']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor UNDERINSURED_MOTORISTS_BODILY_INJURY =
				declare("Underinsured Motorists Bodily Injury", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policyCoverageDetail']//span[normalize-space(.)='Underinsured Motorists Bodily Injury']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor MEDICAL_PAYMENTS = declare("Medical Payments", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policyCoverageDetail']//span[normalize-space(.)='Medical Payments']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor POLICY_LEVEL_LIABILITY_COVERAGES =
				declare("Policy Level Liability Coverages", StaticElement.class, By.xpath(".//table[@id='policyDataGatherForm:policyTableTotalVehiclePremium']//td[normalize-space(.)='Policy Level Liability Coverages']/ancestor::tr[1]//td[3]"));
		public static final AttributeDescriptor DETAILED_VEHICLE_COVERAGES = declare("DetailedVehicleCoverages", DetailedVehicleCoveragesRepeatAssetList.class, DetailedVehicleCoverages.class, false);
		public static final AttributeDescriptor CALCULATE_PREMIUM = declare("Calculate Premium", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:premiumRecalc"));
		public static final AttributeDescriptor OVERRIDE_PREMIUM = declare("Override Premium", Link.class, By.id("policyDataGatherForm:overridePremiumLink"));
		public static final AttributeDescriptor RATE_DETAILS = declare("Rate Details", Button.class, Waiters.AJAX, By.xpath("//button[text()='Rate Details']"));
		public static final AttributeDescriptor VIEW_AUTO_QUOTE = declare("View Auto Quote", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:viewAutoQuote_Link"));
		public static final AttributeDescriptor VIEW_RATING_DETAILS = declare("View Rating Details", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:viewRatingDetails_Link"));
		public static final AttributeDescriptor VIEW_OVERRIDE_RULES = declare("View Override Rules", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:viewRules_Overrides"));
		public static final AttributeDescriptor ADDITIONAL_SAVINGS_OPTIONS = declare("Additional Savings Options", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:visibleRadio']"));
		public static final AttributeDescriptor HOME = declare("Home", CheckBox.class, By.xpath("//td[text()='Home']//input"));
		public static final AttributeDescriptor RENTERS = declare("Renters", CheckBox.class, By.xpath("//td[text()='Renters']//input"));
		public static final AttributeDescriptor CONDO = declare("Condo", CheckBox.class, By.xpath("//td[text()='Condo']//input"));
		public static final AttributeDescriptor MOTORCYCLE = declare("Motorcycle", CheckBox.class, By.xpath("//td[text()='Motorcycle']//input"));
		public static final AttributeDescriptor LIFE = declare("Life", CheckBox.class, By.xpath("//td[text()='Life']//input"));
		public static final AttributeDescriptor MULTI_CAR = declare("Multi-car", CheckBox.class, By.xpath("//td[text()='Multi-car']//input"));
		public static final AttributeDescriptor VIEW_QUOTE_WITH_DISCOUNTS = declare("View Quote with Discounts", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:applySelectedDiscounts"));
		public static final AttributeDescriptor DRIVER_DISCOUNTS_TABLE = declare("Driver Discounts", FillableTable.class, DriverDiscountsRow.class, By.id("policyDataGatherForm:j_id_1_1x_56_1_s_1_2_k_1_1_c_3"));
		//UNKNOWN CONTROLS below, should we remove them?
		public static final AttributeDescriptor UNINSURED_MOTORIST_PROPERTY_DAMEGE =
				declare("Uninsured Motorist Property Damage", ComboBoxFixed.class, By.xpath("./*//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Uninsured Motorist Property Damage']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor COLLISION_DEDUCTIBLE_WAIVER = declare("Collision Deductible Waiver", ComboBoxFixed.class, By.xpath("./*//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Collision Deductible Waiver']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor SPECIAL_EQUIPMENT_DESCRIPTION = declare("Special Equipment Description", TextBox.class, By.xpath("./*//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Special Equipment Description']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor SPECIAL_EQUIPMENT_COVERAGE = declare("Special Equipment Coverage", TextBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Special Equipment Coverage']/ancestor::tr[1]//input"));
		public static final AttributeDescriptor RENTAL_REIMBURSEMENT = declare("Rental Reimbursement", ComboBoxFixed.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Rental Reimbursement']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor TOWING_AND_LABOR_COVERAGE = declare("Towing and Labor Coverage", ComboBoxFixed.class, By.xpath("./[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Towing and Labor Coverage']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor UNACCEPTABLE_RISK_SURCHARGE = declare("Unacceptable Risk Surcharge", CheckBox.class, Waiters.AJAX, By.id("policyDataGatherForm:unacceptableRiskSurchargeCheckbox"));
		public static final AttributeDescriptor REASON = declare("Reason", TextBox.class, Waiters.AJAX, By.id("policyDataGatherForm:unacceptableRiskSurchargeReason"));
		public static final AttributeDescriptor UNVERIFIABLE_DRIVING_RECORD_SURCHARGE = declare("Unverifiable DrivingRecord Surcharge", CheckBox.class, Waiters.AJAX);
		public static StaticElement labelProductInquiry = new StaticElement(By.xpath("//span[@id='policyDataGatherForm:sedit_AAAProductOverride_policyFormCd']")); // TODO-dchubkov: should be removed

		public static final class DetailedVehicleCoverages extends MetaData {
			public static final AttributeDescriptor COMPREGENSIVE_DEDUCTIBLE = declare("Comprehensive Deductible", ComboBoxFixed.class);
			public static final AttributeDescriptor FULL_SAFETY_GLASS = declare("Full Safety Glass", ComboBoxFixed.class);
			public static final AttributeDescriptor COLLISION_DEDUCTIBLE = declare("Collision Deductible", ComboBoxFixed.class);
			public static final AttributeDescriptor ENHANCED_TRASPORTATION_EXPENCE = declare("Enhanced Transportation Expense", ComboBoxFixed.class);
			public static final AttributeDescriptor ALL_RISK = declare("All Risk", ComboBoxFixed.class);
			public static final AttributeDescriptor ORIGINAL_EQUIPMENT_MANUFACTURER_PARTS = declare("Original Equipment Manufacturer Parts", ComboBoxFixed.class);
			public static final AttributeDescriptor RIDESHARING_COVERAGE = declare("Ridesharing Coverage", ComboBoxFixed.class);
			// *** DO NOT DECLARE "Waive Liability" and "Vehicle Coverage" controls in this MetaData. They are added within DetailedVehicleCoveragesRepeatAssetList.class ***
		}

		public static final class DriverDiscountsRow extends MetaData {
			public static final AttributeDescriptor DRIVER = declare("Driver", StaticElement.class);
			public static final AttributeDescriptor GOOD_STUDENT = declare("Good Student", CheckBox.class);
			public static final AttributeDescriptor MATURE_DRIVER = declare("Mature Driver", CheckBox.class);
			public static final AttributeDescriptor SMART_DRIVER = declare("Smart Driver", CheckBox.class);
		}
	}

	public static final class DriverActivityReportsTab extends MetaData {
		public static final AttributeDescriptor HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY = declare("Has the customer expressed interest in purchasing the policy?", RadioGroup.class);
		public static final AttributeDescriptor SALES_AGENT_AGREEMENT = declare("Sales Agent Agreement", RadioGroup.class, By.xpath(".//table[@id='policyDataGatherForm:FfcraPanel_5']"));
		public static final AttributeDescriptor VALIDATE_DRIVING_HISTORY = declare("Validate Driving History", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:submitReports"));

		public static final AttributeDescriptor ORDER_CLUE_REPORT = declare("OrderCLUEReport", FillableTable.class, OrderClueRow.class, By.xpath("//table[@id='policyDataGatherForm:clueReports']"));
		public static final AttributeDescriptor ORDER_MVR_REPORT = declare("OrderMVRReport", FillableTable.class, OrderMVRRow.class, By.xpath("//table[@id='policyDataGatherForm:mvrReportsDataTable']"));
		public static final AttributeDescriptor ORDER_INTERNAL_CLAIMS_REPORT = declare("OrderInternalClaimsReport", FillableTable.class, OrderInternalClaimsRow.class, By.xpath("//table[@id='policyDataGatherForm:claimsReports']"));

		public static final class OrderClueRow extends MetaData {
			public static final AttributeDescriptor SELECT = declare("Select", RadioGroup.class);
			public static final AttributeDescriptor RESIDENTIAL_ADDRESS = declare("Residential Address", StaticElement.class);
			public static final AttributeDescriptor REPORT = declare("Report", Link.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AttributeDescriptor RESPONSE = declare("Response", StaticElement.class);
			public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", StaticElement.class);
		}

		public static final class OrderMVRRow extends MetaData {
			public static final AttributeDescriptor SELECT = declare("Select", RadioGroup.class);
			public static final AttributeDescriptor FORCE_ORDER = declare("Force Order", RadioGroup.class);
			public static final AttributeDescriptor NAME_ON_LICENSE = declare("Name on License", StaticElement.class);
			public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", StaticElement.class);
			public static final AttributeDescriptor STATE = declare("State", StaticElement.class);
			public static final AttributeDescriptor LICENSE_NO = declare("License #", StaticElement.class);
			public static final AttributeDescriptor LICENSE_STATUS = declare("License Status", StaticElement.class);
			public static final AttributeDescriptor REPORT = declare("Report", Link.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AttributeDescriptor RESPONSE = declare("Response", StaticElement.class);
		}

		public static final class OrderInternalClaimsRow extends MetaData {
			public static final AttributeDescriptor SELECT = declare("Select", RadioGroup.class);
			public static final AttributeDescriptor NAME_ON_LICENSE = declare("Name on License", StaticElement.class);
			public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", StaticElement.class);
			public static final AttributeDescriptor STATE = declare("State", StaticElement.class);
			public static final AttributeDescriptor LICENSE_NO = declare("License #", StaticElement.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
		}
	}

	public static final class DocumentsAndBindTab extends MetaData {
		public static final AttributeDescriptor DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAASSAdHocPrintDocs']"));
		public static final AttributeDescriptor REQUIRED_TO_BIND = declare("RequiredToBind", AssetList.class, RequiredToBind.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_OptionalSupportingDocuments']"));
		public static final AttributeDescriptor REQUIRED_TO_ISSUE = declare("RequiredToIssue", AssetList.class, RequiredToIssue.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_MandatorySupportingDocuments']"));
		public static final AttributeDescriptor VEHICLE_INFORMATION = declare("VehicleInformation", VehicleMultiInstanceBeforeAssetList.class, VehicleInformation.class);

		//public static final AttributeDescriptor AGREEMENT = declare("Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:AAADocAgreement_agreement']"));

		public static final AttributeDescriptor WORK_PHONE_NUM = declare("Work Phone #", TextBox.class);
		public static final AttributeDescriptor MOBILE_PHONE_NUM = declare("Mobile Phone #", TextBox.class);

		public static final AttributeDescriptor CASE_NUMBER = declare("Case Number", TextBox.class);

		public static final class DocumentsForPrinting extends MetaData {

			public static final AttributeDescriptor AUTO_BILLING_PLAN_EXPLANATION = declare("Auto Billing Plan Explanation", RadioGroup.class);
			public static final AttributeDescriptor AUTO_QUOTE = declare("Auto Quote", RadioGroup.class);
			public static final AttributeDescriptor AUTOMATIC_PAYMENT_AUTHORIZATION = declare("Automatic Payment Authorization", RadioGroup.class);
			public static final AttributeDescriptor CALIFORNIA_CAR_POLICY_APPLICATION = declare("California Car Policy Application", RadioGroup.class);
			public static final AttributeDescriptor CAMPER_PHYSICAL_DAMAGE_COVERAGE_WAIVER = declare("Camper Physical Damage Coverage Waiver", RadioGroup.class);
			public static final AttributeDescriptor DECLARATION_UNDER_PENALTY_OF_PERJURY = declare("Declaration Under Penalty of Perjury", RadioGroup.class);
			public static final AttributeDescriptor OPERATOR_EXCLUSION_ENDORSEMENT_AND_UNINSURED_MOTORIST_COVERAGE = declare("Operator Exclusion Endorsement and Uninsured Motorist Coverage Deletion Endorsement", RadioGroup.class);
			public static final AttributeDescriptor UNINSURED_MOTORIST_COVERAGE_DELETION_OR_SELECTION_OF_LIMITS = declare("Uninsured Motorist Coverage Deletion or Selection of Limits Agreement", RadioGroup.class);
			public static final AttributeDescriptor SUBSCRIBER_AGREEMENT = declare("Subscriber Agreement", RadioGroup.class);
			public static final AttributeDescriptor AGREEMENT_DELETING_UNINSURED_MOTORIST_PROPERTY_DAMAGE_COVERAGE = declare("Agreement Deleting Uninsured Motorist Property Damage Coverage", RadioGroup.class);
			public static final AttributeDescriptor AGREEMENT_DELETING_UNINSURED_UNDERINSURED_MoTORIST_BODILY_INJUSRY_COVERAGE = declare("Agreement Deleting Uninsured/ Underinsured Motorist Bodily Injury Coverage", RadioGroup.class);
			public static final AttributeDescriptor AGREEMENT_REDUCING_UM_UIM_COVERAGE = declare("Agreement Reducing UM/UIM Coverage", RadioGroup.class);
			public static final AttributeDescriptor APPLICATION_FOR_AUTO_INSURANCE = declare("Application for Auto Insurance", RadioGroup.class);
			public static final AttributeDescriptor AUTO_INSURANCE_QUOTE = declare("Auto Insurance Quote", RadioGroup.class);
			public static final AttributeDescriptor AUTOPAY_AUTHORIZATION_FORM = declare("AutoPay Authorization Form", RadioGroup.class);
			public static final AttributeDescriptor FAX_MEMORANDUM = declare("Fax Memorandum", RadioGroup.class);
			public static final AttributeDescriptor NON_OWNER_AUTOMOBILIST_ENDORSEMENT = declare("Non-Owner Automobile Endorsement", RadioGroup.class);
		}

		public static final class RequiredToBind extends MetaData {
			public static final AttributeDescriptor CALIFORNIA_CAR_POLICY_APPLICATION = declare("California Car Policy Application", RadioGroup.class);
			public static final AttributeDescriptor SUBSCRIBER_AGGREEMENT = declare("Subscriber Agreement", RadioGroup.class);
			public static final AttributeDescriptor PERSONAL_AUTO_APPLICATION = declare("Personal Auto Application", RadioGroup.class);
			public static final AttributeDescriptor DELETING_UNINSURED_MOTORIST_PROPERTY_DAMAGE_COVERAGE = declare("Deleting Uninsured Motorist Property Damage Coverage", RadioGroup.class);
		}

		public static final class RequiredToIssue extends MetaData {
			public static final AttributeDescriptor POLICY_APPLICATION = declare("Policy Application", RadioGroup.class);
			public static final AttributeDescriptor AUTO_BILLING_PLAN_EXPLANATION = declare("Auto Billing Plan Explanation", RadioGroup.class);
		}

		public static final class VehicleInformation extends MetaData {
			public static final AttributeDescriptor ARE_THERE_ANY_ADDITIONAL_INTERESTS = declare("Are there any additional interest(s)?", RadioGroup.class);

			public static final AttributeDescriptor SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
			public static final AttributeDescriptor ISSUE_DATE = declare("Issue Date", TextBox.class);
			public static final AttributeDescriptor METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
			public static final AttributeDescriptor SEND_TO = declare("Send To", ComboBox.class);
			public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
			public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip/Postal Code", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", TextBox.class);
			public static final AttributeDescriptor NOTES = declare("Notes", TextBox.class);
		}

	}

	//TODO done till this row
	public static final class GenerateProposalActionTab extends MetaData {
		public static final AttributeDescriptor NOTES = declare("Notes", TextBox.class);
	}

	public static final class DoNotRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class PolicyDocGenActionTab extends MetaData {}

	public static final class NonPremiumBearingEndorsementActionTab extends MetaData {
		public static final AttributeDescriptor PRINCIPALROLECD = declare("principalRoleCd", TextBox.class);
		public static final AttributeDescriptor PRIMARY_INSURED = declare("Primary Insured?", RadioGroup.class);
		public static final AttributeDescriptor NAME_TYPE = declare("Name Type", ComboBox.class);
		public static final AttributeDescriptor NAME = declare("Name", TextBox.class);
		public static final AttributeDescriptor NAME_PREFIX = declare("Name Prefix", ComboBox.class);
		public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
		public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
		public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
		public static final AttributeDescriptor ADDITIONAL_NAME = declare("Additional Name", RadioGroup.class);
		public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
		public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AttributeDescriptor COUNTY = declare("County", TextBox.class);
		public static final AttributeDescriptor PHONE_TYPE = declare("Phone Type", TextBox.class);
		public static final AttributeDescriptor PHONE = declare("Phone #", TextBox.class);
		public static final AttributeDescriptor EMAIL_TYPE = declare("Email Type", TextBox.class);
		public static final AttributeDescriptor EMAIL = declare("Email", TextBox.class);
		public static final AttributeDescriptor SECOND_NAME = declare("Second Name", TextBox.class);
		public static final AttributeDescriptor EMAIL_ADDRESS = declare("Email Address", TextBox.class);
		public static final AttributeDescriptor TYPE_OF_INTEREST = declare("Type of Interest", ComboBox.class);
		public static final AttributeDescriptor LOAN = declare("Loan #", TextBox.class);
		public static final AttributeDescriptor MORTGAGE_AMOUNT = declare("Mortgage Amount", TextBox.class);
		public static final AttributeDescriptor ADDRESS = declare("Address", TextBox.class);
	}

	public static final class CopyPolicyActionTab extends MetaData {
		public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class DeletePendedTransactionActionTab extends MetaData {}

	public static final class ChangePendedEndorsementActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
		public static final AttributeDescriptor ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
	}

	public static final class DeclineActionTab extends MetaData {
		public static final AttributeDescriptor DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AttributeDescriptor DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class SpinActionTab extends MetaData {
		public static final AttributeDescriptor TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AttributeDescriptor REASON_FOR_SPIN = declare("Reason for Spin", ComboBox.class);
		public static final AttributeDescriptor INSUREDS_APPROVE_REMOVAL_OF_DRIVER_AND_VEHICLES =
				declare("Insureds approve removal of driver and vehicles?", RadioGroup.class);
	}

	public static final class QuoteTransferActionTab extends MetaData {
		public static final AttributeDescriptor CUSTOMER_NUMBER = declare("Customer Number", TextBox.class);
		public static final AttributeDescriptor CUSTOMER_NAME = declare("Customer Name", TextBox.class);
	}

	public static final class RewriteActionTab extends MetaData {
		public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
	}

	public static final class CopyQuoteActionTab extends MetaData {
		public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class RescindCancellationActionTab extends MetaData {
		public static final AttributeDescriptor RESCIND_CANCELLATION_DATE = declare("Rescind Cancellation Date", TextBox.class);
	}

	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	public static final class CancellationActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancel Date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		public static final AttributeDescriptor AUTHORIZED_BY = declare("Authorized By", TextBox.class);
	}

	public static final class IssueSummaryActionTab extends MetaData {
		public static final AttributeDescriptor ISSUE_DATE = declare("Issue Date", TextBox.class);
		public static final AttributeDescriptor METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
		public static final AttributeDescriptor SEND_TO = declare("Send To", ComboBox.class);
		public static final AttributeDescriptor BROKER_EMAIL = declare("Broker Email", TextBox.class);
		public static final AttributeDescriptor INSURED_EMAIL = declare("Insured Email", TextBox.class);
		public static final AttributeDescriptor ADDRESS = declare("Address", TextBox.class);
		public static final AttributeDescriptor NOTES = declare("Notes", TextBox.class);
	}

	public static final class DeleteCancelNoticeActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	public static final class RemoveManualRenewFlagActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
	}

	public static final class CancelNoticeActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.NONE);
		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class, Waiters.NONE);
		public static final AttributeDescriptor DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.NONE);
	}

	public static final class ChangeBrokerActionTab extends MetaData {
		public static final AttributeDescriptor TRANSFER_ID = declare("Transfer ID", TextBox.class);
		public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class);
		public static final AttributeDescriptor TRANSFER_TYPE = declare("Transfer type", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor OTHER_REASON = declare("Other Reason", TextBox.class);
		public static final AttributeDescriptor TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", TextBox.class);
		public static final AttributeDescriptor TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
		public static final AttributeDescriptor COMMISSION_IMPACT = declare("Commission impact", RadioGroup.class);
		public static final AttributeDescriptor SOURCE_CHANNEL = declare("Source Channel", TextBox.class);
		public static final AttributeDescriptor SOURCE_LOCATION_TYPE = declare("Source Location Type", TextBox.class);
		public static final AttributeDescriptor SOURCE_LOCATION_NAME = declare("Source Location Name", TextBox.class);
		public static final AttributeDescriptor SOURCE_INSURANCE_AGENT = declare("Source Insurance Agent", TextBox.class);
		public static final AttributeDescriptor TARGET_CHANNEL = declare("Target channel", TextBox.class);
		public static final AttributeDescriptor TARGET_LOCATION_TYPE = declare("Target Location Type", TextBox.class);
		public static final AttributeDescriptor TARGET_LOCATION_NAME = declare("Target Location Name", TextBox.class);
		public static final AttributeDescriptor TARGET_INSURANCE_AGENT = declare("Target Insurance agent", ComboBox.class);
	}

	public static final class AddManualRenewFlagActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
	}

	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	public static final class RemoveDoNotRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", TextBox.class);
		public static final AttributeDescriptor DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", TextBox.class);
	}

	public static final class ReinstatementActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.NONE);
		public static final AttributeDescriptor REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor AUTHORIZED_BY = declare("Authorized By", TextBox.class);
	}

	public static final class SplitActionTab extends MetaData {
		public static final AttributeDescriptor TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AttributeDescriptor REASON_FOR_SPLIT = declare("Reason for Split", ComboBox.class);
	}

	public static final class EndorsementActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor OTHER = declare("Other", TextBox.class);
	}

	public static final class RenewActionTab extends MetaData {
		public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
		public static final AttributeDescriptor RENEWAL_DATE = declare("Renewal Date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor REASON_FOR_RENEWAL_WITH_LAPSE = declare("Reason for Renewal with Lapse", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor OTHER = declare("Other", TextBox.class, Waiters.NONE);
	}
}
