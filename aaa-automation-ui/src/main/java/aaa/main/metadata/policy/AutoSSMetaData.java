/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata.policy;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.PartySearchTextBox;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;

import aaa.main.metadata.DialogsMetaData.AddressValidationMetaData;
import aaa.main.metadata.DialogsMetaData.DialogSearch;
import aaa.toolkit.webdriver.customcontrols.ComboBoxFixed;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.AssetListConfirmationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
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
 * Metadata definitions for products. For single-root products the root class
 * contains metadata classes as direct children. For multi-root products the
 * root class contains a set of entity classes under which metadata classes are
 * grouped. Modify this class if metadata needs to be altered.
 *
 * @category Generated
 */
public final class AutoSSMetaData {

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
		public static final AttributeDescriptor POLICY_STATE = declare("Policy State", ComboBox.class);
		public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddressButton"));
		public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAPrefillAddressValidation_container']"));
		public static final AttributeDescriptor ORDER_PREFILL = declare("Order Prefill", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:orderPrefillButton"));
	}

	public static final class GeneralTab extends MetaData {
		public static final AttributeDescriptor AAA_PRODUCT_OWNED = declare("AAAProductOwned", AssetList.class, AAAProductOwned.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_ExistingPolicies']"));
		public static final AttributeDescriptor CURRENT_CARRIER_INFORMATION = declare("CurrentCarrierInformation", AssetList.class, CurrentCarrierInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_OtherOrPriorPolicy']"));
		public static final AttributeDescriptor POLICY_INFORMATION = declare("PolicyInformation", AssetList.class, PolicyInformation.class);// ,

		public static final AttributeDescriptor NAMED_INSURED_INFORMATION = declare("NamedInsuredInformation", MultiInstanceAfterAssetList.class, NamedInsuredInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_InsuredInformationMVO']"));
		public static final AttributeDescriptor FIRST_NAMED_INSURED = declare("First Named Insured", ComboBox.class);
																																			// By.xpath(".//div[@id='policyDataGatherForm:componentView_ExistingPolicies']"));
		public static final class NamedInsuredInformation extends MetaData {
			public static final AttributeDescriptor ADD_INSURED = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addInsured"));
			public static final AttributeDescriptor INSURED_SEARCH_DIALOG = declare("InsuredSearchDialog", SingleSelectSearchDialog.class, DialogSearch.class, false, By.id("customerSearchPanel_container"));
			public static final AttributeDescriptor PREFIX = declare("Prefix", ComboBox.class);
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
			public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
			public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
			public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
			public static final AttributeDescriptor SOCIAL_SECURITY_NUMBER = declare("Social Security Number", TextBox.class);
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

			public static final AttributeDescriptor HOME_PHONE_NUMBER = declare("Home Phone Number", TextBox.class);
			public static final AttributeDescriptor WORK_PHONE_NUMBER = declare("Work Phone Number", TextBox.class);
			public static final AttributeDescriptor MOBILE_PHONE_NUMBER = declare("Mobile Phone Number", TextBox.class);
			public static final AttributeDescriptor PREFERED_PHONE_NUMBER = declare("Preferred Phone #", ComboBox.class);
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
			public static final AttributeDescriptor CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class);
			public static final AttributeDescriptor MEMBERSHIP_NUMBER = declare("Membership Number", TextBox.class);
			public static final AttributeDescriptor LAST_NAME = declare("Last name", TextBox.class);
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
			public static final AttributeDescriptor RENEWAL_TERM_PREMIUM_OLD_RATER = declare("Renewal Term Premium - Old Rater", TextBox.class);
			public static final AttributeDescriptor POLICY_TYPE = declare("Policy Type", ComboBox.class);
			public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
			public static final AttributeDescriptor POLICY_TERM = declare("Policy Term", ComboBox.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
			public static final AttributeDescriptor OVERRIDE_ASD_LEVEL = declare("Override ASD Level", RadioGroup.class);
			public static final AttributeDescriptor ADVANCED_SHOPPING_DISCOUNTS = declare("Advance Shopping Discount", TextBox.class);
			public static final AttributeDescriptor CHANNEL_TYPE = declare("Channel Type", ComboBox.class);
			public static final AttributeDescriptor AGENCY = declare("Agency", ComboBox.class);
			public static final AttributeDescriptor AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class);
			public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
			public static final AttributeDescriptor AGENCY_LOCATION = declare("Agency Location", ComboBox.class);
			public static final AttributeDescriptor AGENT = declare("Agent", ComboBox.class);
			public static final AttributeDescriptor AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class);
			public static final AttributeDescriptor AGENT_NUMBER = declare("Agent Number", TextBox.class);
			public static final AttributeDescriptor COMISSION_TYPE = declare("Commission Type", ComboBox.class);
			public static final AttributeDescriptor AUTHORIZED_BY = declare("Authorized by", TextBox.class);
			public static final AttributeDescriptor TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class);
			public static final AttributeDescriptor LEAD_SOURCE = declare("Lead Source", ComboBox.class);
			public static final AttributeDescriptor SUPPRESS_PRINT = declare("Suppress Print", TextBox.class);
		}
	}

	public static final class DriverTab extends MetaData {
		public static final AttributeDescriptor ADD_DRIVER = declare("Add Driver", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addDriver"));
		public static final AttributeDescriptor DRIVER_SEARCH_DIALOG = declare("DriverSearchDialog", SingleSelectSearchDialog.class, DialogSearch.class, false, By.id("customerSearchPanel_container"));

		public static final AttributeDescriptor NAMED_INSURED = declare("Named Insured", ComboBox.class);
		public static final AttributeDescriptor DRIVER_TYPE = declare("Driver Type", ComboBox.class);
		public static final AttributeDescriptor REL_TO_FIRST_NAMED_INSURED = declare("Rel. to First Named Insured", ComboBox.class);
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
		public static final AttributeDescriptor LICENSE_TYPE = declare("License Type", ComboBox.class);
		public static final AttributeDescriptor LICENSE_STATE = declare("License State", ComboBox.class);
		public static final AttributeDescriptor LICENSE_NUMBER = declare("License Number", TextBox.class);
		public static final AttributeDescriptor AGE_FIRST_LICENSED = declare("Age First Licensed", TextBox.class);
		public static final AttributeDescriptor TOTAL_YEAR_DRIVING_EXPERIENCE = declare("Total Years Driving Experience", TextBox.class);
		public static final AttributeDescriptor AFFINITY_GROUP = declare("Affinity Group", ComboBox.class);
		public static final AttributeDescriptor ADB_COVERAGE = declare("ADB Coverage", RadioGroup.class);
		public static final AttributeDescriptor FINANCIAL_RESPONSIBILITY_FILING_NEEDED = declare("Financial Responsibility Filing Needed", RadioGroup.class);
		public static final AttributeDescriptor FILING_STATE = declare("Filing State", ComboBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AttributeDescriptor COUNTY = declare("County", TextBox.class);
		public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
		
		public static final AttributeDescriptor ACTIVITY_INFORMATION = declare("ActivityInformation", MultiInstanceBeforeAssetList.class, ActivityInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_DrivingRecord']"));
		
		public static final class ActivityInformation extends MetaData {
			public static final AttributeDescriptor ADD_ACTIVITY = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addDrivingRecord"));
			public static final AttributeDescriptor ACTIVITY_SOURCE = declare("Activity Source", ComboBox.class);
			public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", ComboBox.class);
			public static final AttributeDescriptor SVC_DESCRIPTION = declare("SVC Description", TextBox.class);
			public static final AttributeDescriptor CLAIM_NUMBER = declare("Claim Number", TextBox.class);
			public static final AttributeDescriptor OCCURENCE_DATE = declare("Occurrence Date", TextBox.class);
			public static final AttributeDescriptor INCLUDE_IN_RATING = declare("Include in Rating?", RadioGroup.class);
			public static final AttributeDescriptor NOT_INCLUDED_IN_RATING_REASON = declare("Not Included in Rating Reasons", ComboBox.class);
			public static final AttributeDescriptor ACTIVITY_REMOVE_CONFIRMATION = declare("Activity remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_container"));
		}

	}

	public static final class RatingDetailReportsTab extends MetaData {
		public static final AttributeDescriptor CUSTOMER_AGREEMENT = declare("Customer Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:sedit_AAASSDriverReportFCRAComponent_customerAgrees']"));
		public static final AttributeDescriptor SALES_AGENT_AGREEMENT = declare("Sales Agent Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:sedit_AAASSDriverReportFFCRAComponent_agentAgrees']"));
		public static final AttributeDescriptor ORDER_REPORT = declare("Order Report", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:submitReports"));
	}

	public static final class VehicleTab extends MetaData {
		public static final AttributeDescriptor ADD_VEHICLE = declare("Add Vehicle", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addVehicle"));

		public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
		public static final AttributeDescriptor USAGE = declare("Usage", ComboBox.class);
		public static final AttributeDescriptor VIN = declare("VIN", TextBox.class);
		public static final AttributeDescriptor CHOOSE_VIN = declare("Choose VIN", ComboBox.class);
		public static final AttributeDescriptor VIN_MATCHED = declare("VIN Matched", StaticElement.class);
		public static final AttributeDescriptor YEAR = declare("Year", TextBox.class);
		public static final AttributeDescriptor MAKE = declare("Make", ComboBox.class);
		public static final AttributeDescriptor MODEL = declare("Model", ComboBox.class);
		public static final AttributeDescriptor SERIES = declare("Series", ComboBox.class);
		public static final AttributeDescriptor BODY_STYLE = declare("Body Style", ComboBox.class);
		public static final AttributeDescriptor OTHER_MAKE = declare("Other Make", TextBox.class);
		public static final AttributeDescriptor OTHER_MODEL = declare("Other Model", TextBox.class);
		public static final AttributeDescriptor OTHER_SERIES = declare("Other Series", TextBox.class);
		public static final AttributeDescriptor OTHER_BODY_STYLE = declare("Other Body Style", TextBox.class);
		public static final AttributeDescriptor EXISTING_DAMAGE = declare("Existing Damage", ComboBox.class);
		public static final AttributeDescriptor SALVAGED = declare("Salvaged?", RadioGroup.class);
		public static final AttributeDescriptor AIR_BAGS = declare("Air Bags", ComboBox.class);
		public static final AttributeDescriptor ANTI_THEFT = declare("Anti-theft", ComboBox.class);
		public static final AttributeDescriptor ALTERNATIVE_FUEL_VEHICLE = declare("Alternative Fuel Vehicle", RadioGroup.class);
		public static final AttributeDescriptor LESS_THAN_3000_MILES = declare("Less Than 3,000 Miles", RadioGroup.class);

		public static final AttributeDescriptor ENROLL_IN_USAGE_BASED_INSURANCE = declare("Enroll in Usage Based Insurance?", RadioGroup.class);
		public static final AttributeDescriptor GET_VEHICLE_DETAILS = declare("Get Vehicle Details", Button.class, By.id("policyDataGatherForm:vehicleUBIDetaiilsButton_AAATelematicDeviceInfo"));
		public static final AttributeDescriptor VEHICLE_ELIGIBILITY_RESPONCE = declare("Vehicle Eligibility Response", ComboBox.class);
		public static final AttributeDescriptor AAA_UBI_DEVICE_STATUS = declare("AAA UBI Device Status", ComboBox.class);
		public static final AttributeDescriptor AAA_UBI_DEVICE_STATUS_DATE = declare("AAA UBI Device Status Date", TextBox.class);
		public static final AttributeDescriptor DEVICE_VOUCHER_NUMBER = declare("Device Voucher Number", TextBox.class);
		public static final AttributeDescriptor SAFETY_SCORE = declare("Safety Score", TextBox.class);
		public static final AttributeDescriptor SAFETY_SCORE_DATE = declare("Safety Score Date", TextBox.class);
		public static final AttributeDescriptor GRANT_PATRITIPATION_DISCOUNT = declare("Grant Participation Discount", Link.class, By.id("policyDataGatherForm:grantParticipationDiscountLink"));

		public static final AttributeDescriptor IS_GARAGING_DIFFERENT_FROM_RESIDENTAL = declare("Is Garaging different from Residential?", RadioGroup.class);
		public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE = declare("State", ComboBox.class);
		public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateGaragingAddressButton"));
		public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAGaragingAddressValidation_container']"));
		public static final AttributeDescriptor OWNERSHIP = declare("Ownership", AssetList.class, Ownership.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAVehicleOwnership']"));

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
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class, By.id(".//*[@id='addressOwnershipValidationPopup_container']"));
		}

		public static final AttributeDescriptor ARE_THERE_ANY_ADDITIONAL_INTERESTS = declare("Are there any additional interest(s)?", RadioGroup.class);
		public static final AttributeDescriptor ADDITIONAL_INTEREST_INFORMATION = declare("AdditionalInterestInformation", MultiInstanceAfterAssetList.class, AdditionalInterestInformation.class,
				By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAAdditionalInterest']"));

		public static final class AdditionalInterestInformation extends MetaData {
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
			public static final AttributeDescriptor SECOND_NAME = declare("Second Name", TextBox.class);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", RadioGroup.class);
			public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddlnInterestAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class, By.id(".//*[@id='addressAddlnInterestValidationPopup_container']"));
			public static final AttributeDescriptor ADD_ADDITIONAL_INTEREST = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAAdditionalInterest"));
		}

	}

	public static final class FormsTab extends MetaData {
	}
	
	public static final class AssignmentTab extends MetaData {
	}

	public static final class PremiumAndCoveragesTab extends MetaData {
		public static final AttributeDescriptor PAYMENT_PLAN = declare("Payment Plan", ComboBox.class);
		public static final AttributeDescriptor POLICY_TERM = declare("Policy Term", ComboBox.class);

		public static final AttributeDescriptor BODILY_INJURY_LIABILITY = declare("Bodily Injury Liability", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Bodily Injury Liability']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor PROPERTY_DAMAGE_LIABILITY = declare("Property Damage Liability", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Property Damage Liability']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor UNINSURED_MOTORISTS_BODILY_INJURY = declare("Uninsured Motorists Bodily Injury", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Uninsured Motorists Bodily Injury']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor UNDERINSURED_MOTORISTS_BODILY_INJURY = declare("Underinsured Motorists Bodily Injury", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Underinsured Motorists Bodily Injury']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor MEDICAL_PAYMENTS = declare("Medical Payments", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Medical Payments']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor PERSONAL_INJURY_PROTECTION = declare("Personal Injury Protection", ComboBoxFixed.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Personal Injury Protection']/ancestor::tr[1]//select"));
		// public static final AttributeDescriptor
		// POLICY_LEVEL_LIABILITY_COVERAGES = declare("Policy Level Liability
		// Coverages", StaticElement.class,
		// By.id("policyDataGatherForm:policyTableTotalVehiclePremium:0:j_id_1_1x_56_1_a_1_2_k_1_1_3z"));

		public static final AttributeDescriptor COMPREGENSIVE_DEDUCTIBLE = declare("Comprehensive Deductible", ComboBoxFixed.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Comprehensive Deductible']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor COLLISION_DEDUCTIBLE = declare("Collision Deductible", ComboBoxFixed.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Collision Deductible']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor SPECIAL_EQUIPMENT_COVERAGE = declare("Special Equipment Coverage", TextBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Special Equipment Coverage']/ancestor::tr[1]//input"));
		public static final AttributeDescriptor FULL_SAFETY_GLASS = declare("Full Safety Glass", ComboBoxFixed.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Full Safety Glass']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor RENTAL_REIMBURSEMENT = declare("Rental Reimbursement", ComboBoxFixed.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Rental Reimbursement']/ancestor::tr[1]//select"));
		public static final AttributeDescriptor TOWING_AND_LABOR_COVERAGE = declare("Towing and Labor Coverage", ComboBoxFixed.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Towing and Labor Coverage']/ancestor::tr[1]//select"));

		public static final AttributeDescriptor UNACCEPTABLE_RISK_SURCHARGE = declare("Unacceptable Risk Surcharge", CheckBox.class, Waiters.AJAX, By.id("policyDataGatherForm:unacceptableRiskSurchargeCheckbox"));
		public static final AttributeDescriptor REASON = declare("Reason", TextBox.class, Waiters.AJAX, By.id("policyDataGatherForm:unacceptableRiskSurchargeReason"));
		// public static final AttributeDescriptor
		// UNVERIFIABLE_DRIVING_RECORD_SURCHARGE = declare("Unverifiable Driving
		// Record Surcharge", CheckBox.class, Waiters.AJAX);

		public static final AttributeDescriptor CALCULATE_PREMIUM = declare("Calculate Premium", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:premiumRecalc"));
	}

	public static final class DriverActivityReportsTab extends MetaData {
		public static final AttributeDescriptor HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE = declare("Has the customer expressed interest in purchasing the quote?", RadioGroup.class);
		public static final AttributeDescriptor VALIDATE_DRIVING_HISTORY = declare("Validate Driving History", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:submitReports"));

	}

	public static final class DocumentsAndBindTab extends MetaData {
		public static final AttributeDescriptor DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAASSAdHocPrintDocs']"));
		public static final AttributeDescriptor REQUIRED_TO_BIND = declare("RequiredToBind", AssetList.class, RequiredToBind.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_OptionalSupportingDocuments']"));
		public static final AttributeDescriptor REQUIRED_TO_ISSUE = declare("RequiredToIssue", AssetList.class, RequiredToIssue.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_MandatorySupportingDocuments']"));

		public static final AttributeDescriptor AGREEMENT = declare("Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:AAADocAgreement_agreement']"));

		public static final AttributeDescriptor WORK_PHONE_NUM = declare("Work Phone #", TextBox.class);
		public static final AttributeDescriptor MOBILE_PHONE_NUM = declare("Mobile Phone #", TextBox.class);
		public static final AttributeDescriptor CASE_NUMBER = declare("Case Number", TextBox.class);
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

		public static final class DocumentsForPrinting extends MetaData {
			public static final AttributeDescriptor AAA_USAGE_BASED_INSURANCE_PROGRAM_TERMS_AND_CONDITIONS = declare("AAA Usage Based Insurance Program Terms and Conditions", RadioGroup.class);
			public static final AttributeDescriptor AAA_WITH_SMARTTRECK_ACKNOWLEDGEMENT = declare("AAA with SMARTtrek Acknowledgement of T&Cs and Privacy Policies", RadioGroup.class);
			public static final AttributeDescriptor ACP_SMARTTRECK_SUBSCRIPTION_TERMS = declare("ACP SMARTtrek Subscription Terms and Conditions", RadioGroup.class);
			public static final AttributeDescriptor AUTO_INSURANCE_APPLICATION = declare("Auto Insurance Application", RadioGroup.class);
			public static final AttributeDescriptor AUTO_INSURANCE_QUOTE = declare("Auto Insurance Quote", RadioGroup.class);
			public static final AttributeDescriptor AUTOPAY_AUTHORIZATION_FORM = declare("AutoPay Authorization Form", RadioGroup.class);
			public static final AttributeDescriptor CONSUMER_INFORMATION_NOTICE = declare("Consumer Information Notice", RadioGroup.class);
			public static final AttributeDescriptor UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION = declare("Uninsured and Underinsured Motorist Coverage Selection", RadioGroup.class);
		}

		public static final class RequiredToBind extends MetaData {
			public static final AttributeDescriptor AUTO_INSURANCE_APPLICATION = declare("Auto Insurance Application", RadioGroup.class);
			public static final AttributeDescriptor PERSONAL_AUTO_APPLICATION = declare("Personal Auto Application", RadioGroup.class);
			public static final AttributeDescriptor AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS = declare("AAA Insurance with SMARTtrek Acknowledgement of Terms and Conditions and Privacy Policies", RadioGroup.class);
			//DC
			public static final AttributeDescriptor INFORMED_CONSENT_FORM_UNINSURED_MOTORIST_COVERAGE = declare("Informed Consent Form - Uninsured Motorist Coverage", RadioGroup.class);
			public static final AttributeDescriptor DISTRICT_OF_COLUMBIA_COVERAGE_SELECTION_REJECTION_FORM = declare("District of Columbia Coverage Selection/Rejection Form", RadioGroup.class);
			//DE
			public static final AttributeDescriptor DELAWARE_MOTORISTS_PROTECTION_ACT= declare("Delaware Motorists Protection Act", RadioGroup.class);
			public static final AttributeDescriptor IMPORTANT_INFORMATION_PERSONAL_INJURY_PROTECTION = declare("Important Information Personal Injury Protection (PIP) - Delaware Understanding (PIP) Deductibles", RadioGroup.class);
			public static final AttributeDescriptor ACKNOLEDGEMENT_OF_DEDUCTIBLE_SELECTIONS = declare("Acknowledgement of Deductible Selections", RadioGroup.class);
			//MD
			public static final AttributeDescriptor NOTICE_AND_WAIVER_OF_INCREASED_LIMITS_OF_UMC = declare("Notice And Waiver Of Increased Limits Of Uninsured Motorists Coverage", RadioGroup.class);
			public static final AttributeDescriptor MARYLAND_NOTICE_AND_WAIVER_OF_PIP_COVERAGE = declare("Maryland Notice And Waiver Of Personal Injury Protection (PIP) Coverage", RadioGroup.class);
			public static final AttributeDescriptor MARYLAND_AUTO_INSURANCE_APPLICATION = declare("Maryland Auto Insurance Application", RadioGroup.class);
			//NJ
			public static final AttributeDescriptor NJ_AUTO_STANDARD_POLICY_COVERAGE_SELECTION_FORM = declare("NJ Auto Standard Policy Coverage Selection Form", RadioGroup.class);
			public static final AttributeDescriptor ACNOWLEDGEMENT_OF_REQUIREMENT_FOR_INSURANCE_INSPECTION = declare("Acknowledgement of Requirement for Insurance Inspection", RadioGroup.class);
			//NY
			public static final AttributeDescriptor ACNOWLEDGEMENT_OF_REQUIREMENT_FOR_PHIOTO_INSPECTION = declare("Acknowledgement of Requirement for Photo Inspection", RadioGroup.class);
			public static final AttributeDescriptor NEW_YORK_AUTO_INSURANCE_APPLICATION = declare("New York Auto Insurance Application", RadioGroup.class);
			//PA
			public static final AttributeDescriptor UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION = declare("Uninsured Motorists Coverage Selection/Rejection", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor UNDERINSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION = declare("Underinsured Motorists Coverage Selection/Rejection", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor FIRST_PARTY_BENEFITS_COVERAGE_AND_LIMITS_SELECTION_FORM = declare("First Party Benefits Coverage and Limits Selection Form", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PENNSYLVANIA_IMPORTANT_NOTICE = declare("Pennsylvania Important Notice", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PENNSYLVANIA_NOTICE_TO_NAMED_INSURED_REGARDING_TORT_OPTIONS = declare("Pennsylvania Notice to Named Insured Regarding Tort Options", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor INVOICE_MINIMUM_COVERAGES = declare("Invoice-Minimum Coverages", RadioGroup.class, Waiters.NONE);
			//WV
			public static final AttributeDescriptor UNINSURED_UNDERINSURED_MOTORISTS_COVERAGE_OFFER = declare("Uninsured/Underinsured Motorists Coverage Offer", RadioGroup.class, Waiters.NONE);
		}

		public static final class RequiredToIssue extends MetaData {
			public static final AttributeDescriptor PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE = declare("Photos for salvage vehicle with physical damage coverage", RadioGroup.class, Waiters.NONE);
			//NJ
			public static final AttributeDescriptor CARGO_VEHICLE_INSPECTION_COMPLETED = declare("CARCO Vehicle Inspection completed or Prior Physical Damage Coverage Inspection Waiver", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SEPARATE_VEHICLE_1 = declare("Separate Vehicle 1", RadioGroup.class, By.xpath("//table[@id='policyDataGatherForm:driverName_0']"));
			//OK
			public static final AttributeDescriptor COVERAGE_SELECTION_FORM = declare("Coverage Selection Form", RadioGroup.class, Waiters.NONE);
		}
	}

	//TODO done till this row
	public static final class ChangePendedEndEffDateActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
		public static final AttributeDescriptor ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
	}

	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	public static final class GenerateOnDemandDocumentActionTab extends MetaData {
	}

	public static final class StartNonPremiumBearingEndorsementActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_DATE = declare("Endorsement date", TextBox.class);
	}

	public static final class NonPremiumBearingEndorsementActionTab extends MetaData {
		public static final AttributeDescriptor INSURED_PARTY_SELECTION = declare("Insured Party Selection", ComboBox.class);
		public static final AttributeDescriptor PRIMARY_INSURED = declare("Primary Insured?", RadioGroup.class);
		public static final AttributeDescriptor TITLE = declare("Title", ComboBox.class);
		public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
		public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", PartySearchTextBox.class);
		public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
		public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AttributeDescriptor AGE = declare("Age", TextBox.class);
		public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class);
		public static final AttributeDescriptor MARITAL_STATUS = declare("Marital Status", ComboBox.class);
		public static final AttributeDescriptor OCCUPATION = declare("Occupation", ComboBox.class);
		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		public static final AttributeDescriptor ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
		public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AttributeDescriptor COUNTY = declare("County", TextBox.class);
		public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
		public static final AttributeDescriptor BUSINESS_TYPE = declare("Business Type", ComboBox.class);
		public static final AttributeDescriptor NAME = declare("Name", TextBox.class);
		public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
		public static final AttributeDescriptor USAGE = declare("Usage", ComboBox.class);
		public static final AttributeDescriptor VIN = declare("VIN", TextBox.class);
		public static final AttributeDescriptor VIN_MATCHED = declare("Vin Matched", TextBox.class);
		public static final AttributeDescriptor CHOOSE_VIN = declare("Choose Vin", ComboBox.class);
		public static final AttributeDescriptor NO_VIN_REASON = declare("No Vin Reason", ComboBox.class);
		public static final AttributeDescriptor MODEL_YEAR = declare("Model Year", ComboBox.class);
		public static final AttributeDescriptor MANUFACTURE_YEAR = declare("Manufacture Year", TextBox.class);
		public static final AttributeDescriptor MAKE = declare("Make", ComboBox.class);
		public static final AttributeDescriptor OTHER_MAKE = declare("Other Make", TextBox.class);
		public static final AttributeDescriptor MODEL = declare("Model", ComboBox.class);
		public static final AttributeDescriptor OTHER_MODEL = declare("Other Model", TextBox.class);
		public static final AttributeDescriptor SERIES = declare("Series", ComboBox.class);
		public static final AttributeDescriptor OTHER_SERIES = declare("Other Series", TextBox.class);
		public static final AttributeDescriptor BODY_STYLE = declare("Body Style", ComboBox.class);
		public static final AttributeDescriptor OTHER_BODY_STYLE = declare("Other Body Style", TextBox.class);
		public static final AttributeDescriptor PERFORMANCE = declare("Performance", ComboBox.class);
		public static final AttributeDescriptor REGISTERED_STATE = declare("Registered State", ComboBox.class);
		public static final AttributeDescriptor PLATE_NUMBER = declare("Plate Number", TextBox.class);
		public static final AttributeDescriptor VALUE = declare("Value", TextBox.class);
		public static final AttributeDescriptor ADJUSTMENT_TO_VALUE = declare("Adjustment to Value", TextBox.class);
		public static final AttributeDescriptor ADJUSTED_VALUE = declare("Adjusted Value", TextBox.class);
		public static final AttributeDescriptor COST_NEW = declare("Cost New ($)", TextBox.class);
		public static final AttributeDescriptor STATED_AMOUNT = declare("Stated Amount ($)", TextBox.class);
		public static final AttributeDescriptor SYMBOL = declare("Symbol", TextBox.class);
		public static final AttributeDescriptor COMP_SYMBOL = declare("Comp Symbol", TextBox.class);
		public static final AttributeDescriptor COLL_SYMBOL = declare("Coll Symbol", TextBox.class);
		public static final AttributeDescriptor BI_SYMBOL = declare("BI Symbol", TextBox.class);
		public static final AttributeDescriptor PD_SYMBOL = declare("PD Symbol", TextBox.class);
		public static final AttributeDescriptor PIP_MED_SYMBOL = declare("PIP/MED Symbol", TextBox.class);
		public static final AttributeDescriptor LIABILITY_SYMBOL = declare("Liability Symbol", TextBox.class);
		public static final AttributeDescriptor USAGE_BASED_RATING = declare("Usage Based Rating?", RadioGroup.class);
		public static final AttributeDescriptor INSURED_AGREES_TO_UBI_TERMS_CONDITIONS = declare("Insured agrees to UBI Terms Conditions?", RadioGroup.class);
		public static final AttributeDescriptor RANK = declare("Rank", TextBox.class);
		public static final AttributeDescriptor INTEREST_TYPE = declare("Interest Type", ComboBox.class);
		public static final AttributeDescriptor SECOND_NAME = declare("Second Name", TextBox.class);
		public static final AttributeDescriptor EMAIL_ADDRESS = declare("Email Address", TextBox.class);
		public static final AttributeDescriptor LOAN = declare("Loan #", TextBox.class);
		public static final AttributeDescriptor MORTGAGE_AMOUNT = declare("Mortgage Amount", TextBox.class);
		public static final AttributeDescriptor LOSS_PAYEE_LEASE_EXPIRATION_DATE = declare("Loss Payee / Lease Expiration Date", TextBox.class);
		public static final AttributeDescriptor ADD_AS_ADDITIONAL_INSURED = declare("Add as Additional Insured", RadioGroup.class);
	}

	public static final class AddFromCancelActionTab extends MetaData {
		public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
		public static final AttributeDescriptor USE_ORIGINAL_POLICY_NUMBER = declare("Use Original Policy Number?", RadioGroup.class);
	}

	public static final class DeclineByCompanyActionTab extends MetaData {
		public static final AttributeDescriptor DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AttributeDescriptor DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AttributeDescriptor DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class SplitActionTab extends MetaData {
		public static final AttributeDescriptor TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AttributeDescriptor REASON_FOR_SPLIT = declare("Reason for Split", ComboBox.class);
	}

	public static final class DoNotRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class DeclineByCustomerActionTab extends MetaData {
		public static final AttributeDescriptor DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AttributeDescriptor DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AttributeDescriptor DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class CopyQuoteActionTab extends MetaData {
		public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class RemoveCancelNoticeActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	public static final class ExtensionRenewalActionTab extends MetaData {
	}

	public static final class CancellationActionTab extends MetaData {
	    public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
        public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
	}

	public static final class RemoveDoNotRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	public static final class ChangeBrokerActionTab extends MetaData {
		public static final AttributeDescriptor TRANSFER_ID = declare("Transfer ID", TextBox.class);
		public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class);
		public static final AttributeDescriptor TRANSFER_TYPE = declare("Transfer Type", RadioGroup.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", RadioGroup.class);
		public static final AttributeDescriptor TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
		public static final AttributeDescriptor COMMISSION_IMPACT = declare("Commission Impact", RadioGroup.class);
		public static final AttributeDescriptor SOURCE_CHANNEL = declare("Source Channel", TextBox.class);
		public static final AttributeDescriptor SOURCE_LOCATION_TYPE = declare("Source Location Type", TextBox.class);
		public static final AttributeDescriptor SOURCE_LOCATION_NAME = declare("Source Location Name", TextBox.class);
		public static final AttributeDescriptor SOURCE_INSURANCE_AGENT = declare("Source Insurance Agent", TextBox.class);
		public static final AttributeDescriptor TARGET_CHANNEL = declare("Target channel", TextBox.class);
		public static final AttributeDescriptor TARGET_LOCATION_TYPE = declare("Target location type", TextBox.class);
		public static final AttributeDescriptor TARGET_LOCATION_NAME = declare("Target location Name", TextBox.class);
		public static final AttributeDescriptor TARGET_INSURANCE_AGENT = declare("Target Insurance agent", ComboBox.class);
		public static final AttributeDescriptor LOCATION_NAME = declare("Location Name", DialogSingleSelector.class, ChangeLocationMetaData.class);
		public static final AttributeDescriptor INSURANCE_AGENT = declare("Insurance Agent", ComboBox.class);

		public static final class ChangeLocationMetaData extends MetaData {
			public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
			public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
			public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE = declare("State", TextBox.class);

			public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By.id("policyDataGatherForm:changeTargetProducerCd"));
		}
	}

	public static final class CancelNoticeActionTab extends MetaData {
	    public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
        public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.NONE);
        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.NONE);
	}

	public static final class SpinActionTab extends MetaData {
		public static final AttributeDescriptor TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AttributeDescriptor REASON_FOR_SPIN = declare("Reason for Spin", ComboBox.class);
		public static final AttributeDescriptor INSUREDS_APPROVE_REMOVAL_OF_DRIVER_AND_VEHICLES = declare("Insureds approve removal of driver and vehicles?", RadioGroup.class);
	}

	public static final class ReinstatementActionTab extends MetaData {
	    public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
	}

	public static final class RewriteActionTab extends MetaData {
	    public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
        public static final AttributeDescriptor REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
	}

	public static final class BindActionTab extends MetaData {
	}

	public static final class RollOnChangesActionTab extends MetaData {
	}

	public static final class CopyFromPolicyActionTab extends MetaData {
		public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class DeletePendedTransactionActionTab extends MetaData {
	}

	public static final class RemoveManualRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
	}

	public static final class ProposeActionTab extends MetaData {
		public static final AttributeDescriptor NOTES = declare("Notes", TextBox.class);
	}

	public static final class ManualRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
	}

	public static final class RescindCancellationActionTab extends MetaData {
		public static final AttributeDescriptor RESCIND_CANCELLATION_DATE = declare("Rescind Cancellation Date", TextBox.class);
	}

	public static final class IssueActionTab extends MetaData {
	}

	public static final class AuthorityActionTab extends MetaData {
		public static final AttributeDescriptor AUTHORIZED_PERSON_REQUESTING_CHANGE = declare("Authorized Person Requesting Change", ComboBox.class);
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
