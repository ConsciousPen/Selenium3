/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata.policy;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.controls.PartySearchTextBox;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;
import aaa.main.enums.DocGenConstants;
import aaa.main.metadata.DialogsMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import aaa.toolkit.webdriver.customcontrols.DetailedVehicleCoveragesRepeatAssetList;
import aaa.toolkit.webdriver.customcontrols.FillableDocumentsTable;
import aaa.toolkit.webdriver.customcontrols.FillableErrorTable;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.AssetListConfirmationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import aaa.toolkit.webdriver.customcontrols.endorsements.AutoSSForms;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
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
		public static final AssetDescriptor<ComboBox> POLICY_STATE = declare("Policy State", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY_TOWNSHIP = declare("County / Township", ComboBox.class);
		public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX.then(Waiters.SLEEP(2000)), false, By.id("policyDataGatherForm:validateAddressButton"));
		public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAPrefillAddressValidation_container']"));
		public static final AssetDescriptor<Button> ORDER_PREFILL = declare("Order Prefill", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:orderPrefillButton"));
		public static final AssetDescriptor<ComboBox> COUNTY_TOWNSHIP = declare("County / Township", ComboBox.class);
	}

	public static final class GeneralTab extends MetaData {
		public static final AssetDescriptor<FillableTable> LIST_OF_INSURED_PRINCIPAL = declare("List of Insured Principal", FillableTable.class, ListOfInsuredPrincipalRow.class, By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListInsured']/div/table"));
		public static final AssetDescriptor<AssetList> CONTACT_INFORMATION = declare("ContactInformation", AssetList.class, ContactInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAContactInformationMVO']"));
		public static final AssetDescriptor<AssetList> CURRENT_CARRIER_INFORMATION = declare("CurrentCarrierInformation", AssetList.class, CurrentCarrierInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_OtherOrPriorPolicy']"));
		public static final AssetDescriptor<AssetList> AAA_PRODUCT_OWNED = declare("AAAProductOwned", AssetList.class, AAAProductOwned.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_ExistingPolicies']"));
		public static final AssetDescriptor<AssetList> POLICY_INFORMATION = declare("PolicyInformation", AssetList.class, PolicyInformation.class);

		public static final AssetDescriptor<MultiInstanceAfterAssetList> NAMED_INSURED_INFORMATION = declare("NamedInsuredInformation", MultiInstanceAfterAssetList.class, NamedInsuredInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_InsuredInformationMVO']"));
		public static final AssetDescriptor<ComboBox> FIRST_NAMED_INSURED = declare("First Named Insured", ComboBox.class);

		public static final class ListOfInsuredPrincipalRow extends MetaData {
			public static final AssetDescriptor<StaticElement> NUM_COLUMN = declare("column=1", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MAKE = declare("First Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MODEL = declare("Last Name", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION_COLUMN = declare("column=4", Link.class);
			public static final AssetDescriptor<Button> CONFIRM_REMOVE = declare("Confirm Remove", Button.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_form:buttonYes"));
		}

		// By.xpath(".//div[@id='policyDataGatherForm:componentView_ExistingPolicies']"));
		public static final class NamedInsuredInformation extends MetaData {
			public static final AssetDescriptor<Button> ADD_INSURED = declare("Add", Button.class, Waiters.AJAX.then(Waiters.AJAX), false, By.id("policyDataGatherForm:addInsured"));
			public static final AssetDescriptor<SingleSelectSearchDialog> INSURED_SEARCH_DIALOG = declare("InsuredSearchDialog", SingleSelectSearchDialog.class, DialogsMetaData.DialogSearch.class, false, By.id("customerSearchPanel_container"));
			public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class);
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
			public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
			public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER = declare("Social Security Number", TextBox.class);
			public static final AssetDescriptor<TextBox> BASE_DATE = declare("Base Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> COUNTY_TOWNSHIP = declare("County / Township", ComboBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);

			public static final AssetDescriptor<RadioGroup> HAS_LIVED_LESS_THAN_3_YEARS = declare("Has lived here for less than three years?", RadioGroup.class);
			public static final AssetDescriptor<TextBox> PRIOR_MOVE_IN_DATE = declare("Move-In Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> PRIOR_ADDRESS_TYPE = declare("Prior Address Type", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_usageTypeCd"));
			public static final AssetDescriptor<TextBox> PRIOR_ZIP_CODE = declare("Prior Zip Code", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_postalCode"));
			public static final AssetDescriptor<TextBox> PRIOR_ADDRESS_LINE_1 = declare("Prior Address Line 1", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_addressLine1"));
			public static final AssetDescriptor<TextBox> PRIOR_ADDRESS_LINE_2 = declare("Prior Address Line 2", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_addressLine2"));
			public static final AssetDescriptor<TextBox> PRIOR_CITY = declare("Prior City", TextBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_city"));
			public static final AssetDescriptor<ComboBox> PRIOR_STATE = declare("Prior State", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_priorAddress_address_stateProvCd"));

			public static final AssetDescriptor<RadioGroup> IS_RESIDENTAL_DIFFERENF_FROM_MAILING = declare("Is residential address different from mailing address?", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> MAILING_ADDRESS_TYPE = declare("Mailing Address Type", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_usageTypeCd"));
			public static final AssetDescriptor<TextBox> MAILING_ZIP_CODE = declare("Mailing Zip Code", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_postalCode"));
			public static final AssetDescriptor<TextBox> MAILING_ADDRESS_LINE_1 = declare("Mailing Address Line 1", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_addressLine1"));
			public static final AssetDescriptor<TextBox> MAILING_ADDRESS_LINE_2 = declare("Mailing Address Line 2", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_addressLine2"));
			public static final AssetDescriptor<TextBox> MAILING_CITY = declare("Mailing City", TextBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_city"));
			public static final AssetDescriptor<ComboBox> MAILING_STATE = declare("Mailing State", ComboBox.class, By.id("policyDataGatherForm:insuredInformation_secondaryAddress_address_stateProvCd"));

			public static final AssetDescriptor<ComboBox> RESIDENCE = declare("Residence", ComboBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidation.class, By.id(".//*[@id='addressValidationPopupAAAInsuredAddressValidation_container']"));

			public static final class AddressValidation extends MetaData {
				public static final AssetDescriptor<StaticElement> YOU_ENTERED = declare("You entered", StaticElement.class, By.xpath(".//span[contains(@id, ':notSamePrimartAddressLabel')]"));
				public static final AssetDescriptor<CheckBox> ADDRESS_IS_PO_BOX = declare("Address is PO Box", CheckBox.class, Waiters.AJAX);

				public static final AssetDescriptor<TextBox> STREET_NUMBER = declare("Street number", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryStreetNumberInput')]"));
				public static final AssetDescriptor<TextBox> STREET_NAME = declare("Street Name", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryStreetNameInput')]"));
				public static final AssetDescriptor<TextBox> UNIT_NUMBER = declare("Unit number", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, ':primaryUnitNumberInput')]"));
				public static final AssetDescriptor<RadioGroup> RADIOGROUP_SELECT = declare("Select Address", RadioGroup.class, Waiters.AJAX);

				public static final AssetDescriptor<TextBox> MAILING_STREET_NUMBER = declare("Mailing Street number", TextBox.class, Waiters.AJAX, false, By.id("addressValidationFormAAAInsuredAddressValidation:secondaryStreetNumberInput"));
				public static final AssetDescriptor<TextBox> MAILING_STREET_NAME = declare("Mailing Street Name", TextBox.class, Waiters.AJAX, false, By.id("addressValidationFormAAAInsuredAddressValidation:secondaryStreetNameInput"));
				public static final AssetDescriptor<TextBox> MAILING_UNIT_NUMBER = declare("Mailing Unit number", TextBox.class, Waiters.AJAX, false, By.id("addressValidationFormAAAInsuredAddressValidation:secondaryUnitNumberInput"));
				public static final AssetDescriptor<RadioGroup> MAILING_RADIOGROUP_SELECT = declare("Mailing Select Address", RadioGroup.class, Waiters.AJAX);

				public static final AssetDescriptor<Button> BTN_OK = declare("Ok", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='OK']"));
				public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Cancel']"));
			}
		}

		public static final class ContactInformation extends MetaData {
			public static final AssetDescriptor<TextBox> HOME_PHONE_NUMBER = declare("Home Phone Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> WORK_PHONE_NUMBER = declare("Work Phone Number", TextBox.class);
			public static final AssetDescriptor<TextBox> MOBILE_PHONE_NUMBER = declare("Mobile Phone Number", TextBox.class);
			public static final AssetDescriptor<ComboBox> PREFERED_PHONE_NUMBER = declare("Preferred Phone #", ComboBox.class);
			public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
		}
		
		public static final class AAAProductOwned extends MetaData {
			public static final AssetDescriptor<ComboBox> CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class);
			public static final AssetDescriptor<StaticElement> EXISTING_MEMBERSHIP_NO_NJ_DE_WARNING_BLOCK = declare("ExistingMembershipNo_NJ_DE_WarningBlock", StaticElement.class, By.xpath("//*[@id='policyDataGatherForm:existingMembershipNo_NJ_DE_WarningBlock' or @id='policyDataGatherForm:existingMembershipPending_NJ_DE_WarningBlock']"));
			public static final AssetDescriptor<TextBox> MEMBERSHIP_NUMBER = declare("Membership Number", TextBox.class);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last name", TextBox.class);
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
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_INCEPTION_DATE = declare("Agent Entered Inception Date", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_EXPIRATION_DATE = declare("Agent Entered Expiration Date", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_POLICY_NUMBER = declare("Agent Entered Policy Number", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_DAYS_LAPSED = declare("Agent Entered Days Lapsed", TextBox.class, By.id("policyDataGatherForm:currentCarrierInformation_enteredDaysLapsed"));
			public static final AssetDescriptor<TextBox> AGENT_ENTERED_MONTHS_WITH_CARRIER = declare("Agent Entered Months with Carrier", TextBox.class, By.id("policyDataGatherForm:currentCarrierInformation_enteredMonthsWithInsurer"));
			public static final AssetDescriptor<ComboBox> AGENT_ENTERED_BI_LIMITS = declare("Agent Entered BI Limits", ComboBox.class);
			public static final AssetDescriptor<StaticElement> CURRENT_CARRIER_INFORMATION_WARNING_MESSAGE = declare("Current Carrier Information Warning Message", StaticElement.class, By.xpath("//*[@id='policyDataGatherForm:enteredPriorBILimitsInfoLbl' or @id='policyDataGatherForm:overridePrefilledCarrierInfoLbl']"));
		}

		public static final class PolicyInformation extends MetaData {
			public static final AssetDescriptor<ComboBox> SOURCE_OF_BUSINESS = declare("Source of Business", ComboBox.class);
			public static final AssetDescriptor<TextBox> RENEWAL_TERM_PREMIUM_OLD_RATER = declare("Renewal Term Premium - Old Rater", TextBox.class);
			public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> POLICY_TERM = declare("Policy Term", ComboBox.class);
			public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
			public static final AssetDescriptor<ComboBox> EXTRAORDINARY_LIFE_CIRCUMSTANCE = declare("Extraordinary Life Circumstance", ComboBox.class);
			public static final AssetDescriptor<AdvancedComboBox> ADVERSELY_IMPACTED = declare("Adversely Impacted", AdvancedComboBox.class);
			public static final AssetDescriptor<RadioGroup> OVERRIDE_ASD_LEVEL = declare("Override ASD Level", RadioGroup.class);
			public static final AssetDescriptor<TextBox> ADVANCED_SHOPPING_DISCOUNTS = declare("Advance Shopping Discount", TextBox.class);
			public static final AssetDescriptor<ComboBox> CHANNEL_TYPE = declare("Channel Type", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENCY = declare("Agency", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class);
			public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENCY_LOCATION = declare("Agency Location", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENT = declare("Agent", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class);
			public static final AssetDescriptor<TextBox> AGENT_NUMBER = declare("Agent Number", TextBox.class);
			public static final AssetDescriptor<RadioGroup> HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE = declare("Has the insured ever been enrolled in eValue?", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> COMMISSION_TYPE = declare("Commission Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized by", TextBox.class);
			public static final AssetDescriptor<TextBox> TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class);
			public static final AssetDescriptor<ComboBox> LEAD_SOURCE = declare("Lead Source", ComboBox.class);
			public static final AssetDescriptor<TextBox> SUPPRESS_PRINT = declare("Suppress Print", TextBox.class);
			public static final AssetDescriptor<AssetListConfirmationDialog> CHANGE_POLICY_TYPE_CONFIRMATION = declare("Change Policy Type Confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.xpath(".//div[@id='policyDataGatherForm:switchToNanoConfirmDialog_container']"));

		}
	}

	public static final class DriverTab extends MetaData {
		public static final AssetDescriptor<FillableTable> LIST_OF_DRIVER = declare("List of Driver", FillableTable.class, ListOfDriver.class, By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListDriver']/div/table"));
		
		public static final AssetDescriptor<Button> ADD_DRIVER = declare("Add Driver", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addDriver"));
		public static final AssetDescriptor<SingleSelectSearchDialog> DRIVER_SEARCH_DIALOG = declare("DriverSearchDialog", SingleSelectSearchDialog.class, DialogsMetaData.DialogSearch.class, false, By.id("customerSearchPanel_container"));

		public static final AssetDescriptor<ComboBox> NAMED_INSURED = declare("Named Insured", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DRIVER_TYPE = declare("Driver Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class, By.id("policyDataGatherForm:driverMVOInformation_driver_nonDriverReason"));
		public static final AssetDescriptor<ComboBox> CARRIER = declare("Carrier", ComboBox.class, By.id("policyDataGatherForm:driverMVOInformation_driver_carrierName"));
		public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy Number", TextBox.class);
		public static final AssetDescriptor<ComboBox> REL_TO_FIRST_NAMED_INSURED = declare("Rel. to First Named Insured", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AssetDescriptor<TextBox> AGE = declare("Age", TextBox.class);
		public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
		public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class);
		public static final AssetDescriptor<TextBox> BASE_DATE = declare("Base Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> LICENSE_TYPE = declare("License Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> LICENSE_STATE = declare("License State", ComboBox.class);
		public static final AssetDescriptor<TextBox> LICENSE_NUMBER = declare("License Number", TextBox.class);
		public static final AssetDescriptor<TextBox> AGE_FIRST_LICENSED = declare("Age First Licensed", TextBox.class);
		public static final AssetDescriptor<TextBox> TOTAL_YEAR_DRIVING_EXPERIENCE = declare("Total Years Driving Experience", TextBox.class);
		public static final AssetDescriptor<ComboBox> AFFINITY_GROUP = declare("Affinity Group", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MOST_RECENT_GPA = declare("Most Recent GPA", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DEFENSIVE_DRIVER_COURSE_COMPLETED = declare("Defensive Driver Course Completed?", ComboBox.class);
		public static final AssetDescriptor<TextBox> DEFENSIVE_DRIVER_COURSE_COMPLETION_DATE = declare("Defensive Driver Course Completion Date", TextBox.class);
		public static final AssetDescriptor<TextBox> DEFENSIVE_DRIVER_COURSE_CERTIFICATE_NUMBER = declare("Defensive Driver Course Certificate Number", TextBox.class);
		public static final AssetDescriptor<RadioGroup> ADB_COVERAGE = declare("ADB Coverage", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> FINANCIAL_RESPONSIBILITY_FILING_NEEDED = declare("Financial Responsibility Filing Needed", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> FILING_STATE = declare("Filing State", ComboBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);

		public static final AssetDescriptor<MultiInstanceBeforeAssetList> ACTIVITY_INFORMATION = declare("ActivityInformation", MultiInstanceBeforeAssetList.class, ActivityInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_DrivingRecord']"));
		
		public static final class ListOfDriver extends MetaData {
			public static final AssetDescriptor<StaticElement> NUM_COLUMN = declare("column=1", StaticElement.class);
			public static final AssetDescriptor<StaticElement> FIRST_NAME = declare("First Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> LAST_NAME = declare("Last Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> BIRTH_DATE = declare("Birth Date", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION_COLUMN = declare("column=5", Link.class);
			public static final AssetDescriptor<Button> CONFIRM_REMOVE = declare("Confirm Remove", Button.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_form:buttonYes"));
		}
		
		public static final class ActivityInformation extends MetaData {
			public static final AssetDescriptor<Button> ADD_ACTIVITY = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addDrivingRecord"));
			public static final AssetDescriptor<ComboBox> ACTIVITY_SOURCE = declare("Activity Source", ComboBox.class);
			public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
			public static final AssetDescriptor<ComboBox> DESCRIPTION = declare("Description", ComboBox.class);
			public static final AssetDescriptor<TextBox> SVC_DESCRIPTION = declare("SVC Description", TextBox.class);
			public static final AssetDescriptor<TextBox> CLAIM_NUMBER = declare("Claim Number", TextBox.class);
			public static final AssetDescriptor<TextBox> OCCURENCE_DATE = declare("Occurrence Date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CONVICTION_DATE = declare("Conviction Date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIOLATION_POINTS = declare("Violation points", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<TextBox> LOSS_PAYMENT_AMOUNT = declare("Loss Payment Amount", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CLAIM_POINTS = declare("Claim Points", TextBox.class);
			public static final AssetDescriptor<RadioGroup> INCLUDE_IN_POINTS_AND_OR_TIER = declare("Include in Points and/or Tier?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> NOT_INCLUDED_IN_POINTS_AND_OR_TIER_REASON_CODES = declare("Not Included in Points and/or Tier - Reason Codes", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> INCLUDE_IN_RATING = declare("Include in Rating?", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> NOT_INCLUDED_IN_RATING_REASON = declare("Not Included in Rating Reasons", ComboBox.class);
			public static final AssetDescriptor<AssetListConfirmationDialog> ACTIVITY_REMOVE_CONFIRMATION = declare("Activity remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_container"));
		}

	}

	public static final class RatingDetailReportsTab extends MetaData {
		public static final AssetDescriptor<FillableTable> ORDER_INSURANCE_SCORE_REPORT = declare("OrderInsuranceScoreReport", FillableTable.class, OrderInsuranceScoreReportRow.class, By.xpath("//table[@id='policyDataGatherForm:creditReports']"));
		
		public static final AssetDescriptor<RadioGroup> CUSTOMER_AGREEMENT = declare("Customer Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:sedit_AAASSDriverReportFCRAComponent_customerAgrees']"));
		public static final AssetDescriptor<RadioGroup> SALES_AGENT_AGREEMENT = declare("Sales Agent Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:sedit_AAASSDriverReportFFCRAComponent_agentAgrees']"));
		public static final AssetDescriptor<Button> ORDER_REPORT = declare("Order Report", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:submitReports"));

		public static final AssetDescriptor<FillableTable> AAA_MEMBERSHIP_REPORT = declare("AAAMembershipReport", FillableTable.class, AaaMembershipReportRow.class, By.xpath("//table[@id='policyDataGatherForm:membershipReports']"));
		public static final AssetDescriptor<FillableTable> INSURANCE_SCORE_OVERRIDE = declare("InsuranceScoreOverride", FillableTable.class, InsuranceScoreOverrideRow.class, By.xpath("//table[@id='policyDataGatherForm:creditScoreOverride']"));

		public static final class AaaMembershipReportRow extends MetaData {
			public static final AssetDescriptor<RadioGroup> SELECT = declare("Select", RadioGroup.class);
			public static final AssetDescriptor<StaticElement> LAST_NAME = declare("Last Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MEMBERSHIP_NO = declare("Membership No.", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MEMBER_SINCE_DATE = declare("Member Since Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION = declare("Action", Link.class);

			public static final AssetDescriptor<AssetList> ADD_MEMBER_SINCE_DIALOG = declare("AddMemberSinceDialog", AssetList.class, AddMemberSinceDialog.class);
		}

		public static final class AddMemberSinceDialog extends MetaData {
			public static final AssetDescriptor<TextBox> MEMBER_SINCE = declare("Member Since", TextBox.class, By.xpath("//input[@id='memberSinceDateFrom:popupMemberSinceDateInputDate']"));
			public static final AssetDescriptor<Button> BTN_OK = declare("OK", Button.class, By.xpath("//input[@id='memberSinceDateFrom:addMemberSinceDateButton']"));
			public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, By.xpath("//input[@id='memberSinceDateFrom:cancelMemberSinceDateButton']"));
		}

		public static final class OrderInsuranceScoreReportRow extends MetaData {
			public static final AssetDescriptor<RadioGroup> SELECT = declare("Select", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> CUSTOMER_REQUEST_REORDER = declare("Customer Request Re-order", RadioGroup.class);
			public static final AssetDescriptor<StaticElement> NAME = declare("Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RESIDENTIAL_ADDRESS = declare("Residential Address", StaticElement.class);
			public static final AssetDescriptor<StaticElement> SSN_ENTERED = declare("SSN Entered", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RESPONSE = declare("Response", StaticElement.class);
			public static final AssetDescriptor<StaticElement> BAND_NUMBER = declare("Band Number", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ADDRESS_TYPE = declare("Address Type", StaticElement.class);
		}

		public static final class InsuranceScoreOverrideRow extends MetaData {
			public static final AssetDescriptor<StaticElement> NAMED_INSURED = declare("Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> INSURANCE_SCORE = declare("Score", StaticElement.class);
			public static final AssetDescriptor<StaticElement> OVERRIDE_DATE = declare("Override Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> REASON_FOR_OVERRIDE = declare("Reason for Override", StaticElement.class);
			public static final AssetDescriptor<StaticElement> OVERRIDDEN_BY = declare("User", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION = declare("Action", Link.class);
			public static final AssetDescriptor<AssetList> EDIT_INSURANCE_SCORE = declare("EditInsuranceScoreDialog", AssetList.class, EditInsuranceScoreDialog.class);
		}

		public static final class EditInsuranceScoreDialog extends MetaData {
			public static final AssetDescriptor<TextBox> NEW_SCORE = declare("New Score", TextBox.class, By.xpath("//input[@id='editInsuranceScoreFrom:popupInsuranceScore_newScore']"));
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for Override", ComboBox.class, By.xpath("//select[@id='editInsuranceScoreFrom:popupInsuranceScore_reasonForOverride']"));
			public static final AssetDescriptor<Button> BTN_SAVE = declare("Save", Button.class, By.xpath("//input[@id='editInsuranceScoreFrom:saveButton']"));
			public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, By.xpath("//input[@id='editInsuranceScoreFrom:cancelEditButton']"));
		}

	}

	public static final class VehicleTab extends MetaData {
		public static final AssetDescriptor<FillableTable> LIST_OF_VEHICLE = declare("List of Vehicle", FillableTable.class, ListOfVehicleRow.class, By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListVehicle']/div/table"));
		public static final AssetDescriptor<Button> ADD_VEHICLE = declare("Add Vehicle", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addVehicle"));		
		public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
		public static final AssetDescriptor<AssetListConfirmationDialog> CHANGE_VEHICLE_CONFIRMATION = declare("Change Vehicle Confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.xpath(".//div[@id='confirmVehicleTypeChangePopup_container']"));
		public static final AssetDescriptor<ComboBox> USAGE = declare("Usage", ComboBox.class);
		public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class);
		public static final AssetDescriptor<ComboBox> CHOOSE_VIN = declare("Choose VIN", ComboBox.class);
		public static final AssetDescriptor<StaticElement> VIN_MATCHED = declare("VIN Matched", StaticElement.class);
		public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class);
		public static final AssetDescriptor<ComboBox> MAKE = declare("Make", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MODEL = declare("Model", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SERIES = declare("Series", ComboBox.class);
		public static final AssetDescriptor<ComboBox> BODY_STYLE = declare("Body Style", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_MAKE = declare("Other Make", TextBox.class);
		public static final AssetDescriptor<TextBox> OTHER_MODEL = declare("Other Model", TextBox.class);
		public static final AssetDescriptor<TextBox> OTHER_SERIES = declare("Other Series", TextBox.class);
		public static final AssetDescriptor<ComboBox> OTHER_BODY_STYLE = declare("Other Body Style", ComboBox.class);
		public static final AssetDescriptor<ComboBox> STAT_CODE = declare("Stat Code", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> STATED_AMOUNT = declare("Stated Amount", TextBox.class,By.xpath(".//input[@id='policyDataGatherForm:vehicleInformation_vehicleRatingInfo_costPurchase']"));
		public static final AssetDescriptor<ComboBox> EXISTING_DAMAGE = declare("Existing Damage", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> SALVAGED = declare("Salvaged?", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> AIR_BAGS = declare("Air Bags", ComboBox.class);
		public static final AssetDescriptor<ComboBox> ANTI_THEFT = declare("Anti-theft", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> ALTERNATIVE_FUEL_VEHICLE = declare("Alternative Fuel Vehicle", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> LESS_THAN_3000_MILES = declare("Less Than 3,000 Miles", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> IS_THIS_A_REPLACEMENT_VEHICLE = declare("Is this a Replacement Vehicle?", RadioGroup.class);
		public static final AssetDescriptor<TextBox> MILES_ONE_WAY_TO_WORK_OR_SCHOOL = declare("Miles One-way to Work or School", TextBox.class);
		public static final AssetDescriptor<TextBox> VALUE = declare("Value($)", TextBox.class);

		public static final AssetDescriptor<RadioGroup> ENROLL_IN_USAGE_BASED_INSURANCE = declare("Enroll in Usage Based Insurance?", RadioGroup.class);
		public static final AssetDescriptor<Button> GET_VEHICLE_DETAILS = declare("Get Vehicle Details", Button.class, By.id("policyDataGatherForm:vehicleUBIDetaiilsButton_AAATelematicDeviceInfo"));
		public static final AssetDescriptor<ComboBox> VEHICLE_ELIGIBILITY_RESPONCE = declare("Vehicle Eligibility Response", ComboBox.class);
		public static final AssetDescriptor<ComboBox> AAA_UBI_DEVICE_STATUS = declare("AAA UBI Device Status", ComboBox.class);
		public static final AssetDescriptor<TextBox> AAA_UBI_DEVICE_STATUS_DATE = declare("AAA UBI Device Status Date", TextBox.class);
		public static final AssetDescriptor<TextBox> DEVICE_VOUCHER_NUMBER = declare("Device Voucher Number", TextBox.class);
		public static final AssetDescriptor<TextBox> SAFETY_SCORE = declare("Safety Score", TextBox.class);
		public static final AssetDescriptor<TextBox> SAFETY_SCORE_DATE = declare("Safety Score Date", TextBox.class);
		public static final AssetDescriptor<Link> GRANT_PATRITIPATION_DISCOUNT = declare("Grant Participation Discount", Link.class, By.id("policyDataGatherForm:grantParticipationDiscountLink"));

		public static final AssetDescriptor<RadioGroup> IS_GARAGING_DIFFERENT_FROM_RESIDENTAL = declare("Is Garaging different from Residential?", RadioGroup.class);
		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
		public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateGaragingAddressButton"));
		public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAGaragingAddressValidation_container']"));
		public static final AssetDescriptor<AssetList> OWNERSHIP = declare("Ownership", AssetList.class, Ownership.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAVehicleOwnership']"));
		public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_ADDITIONAL_INTERESTS = declare("Are there any additional interest(s)?", RadioGroup.class);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_INTEREST_INFORMATION = declare("AdditionalInterestInformation", MultiInstanceAfterAssetList.class, AdditionalInterestInformation.class,
				By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAAdditionalInterest']"));

		public static final class ListOfVehicleRow extends MetaData {
			public static final AssetDescriptor<StaticElement> NUM_COLUMN = declare("column=1", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MAKE = declare("Make", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MODEL = declare("Model", StaticElement.class);
			public static final AssetDescriptor<StaticElement> YEAR = declare("Year", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION_COLUMN = declare("column=5", Link.class);
			public static final AssetDescriptor<Button> CONFIRM_REMOVE = declare("Confirm Remove", Button.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_form:buttonYes"));
		}

		public static final class Ownership extends MetaData {
			public static final AssetDescriptor<RadioGroup> IS_REGISTERED_OWNER_DIFFERENT_THAN_NAMED_INSURED = declare("Is Registered Owner Different than Named Insured", RadioGroup.class);
            public static final AssetDescriptor<TextBox> PRIMARY_OWNER_FIRST_LAST_NAME = declare("Primary Owner First, Last Name", TextBox.class);
            public static final AssetDescriptor<TextBox> ADDITIONAL_OWNER_FIRST_LAST_NAME = declare("Additional Owner First, Last Name", TextBox.class);

			public static final AssetDescriptor<ComboBox> OWNERSHIP_TYPE = declare("Ownership Type", ComboBox.class);
			public static final AssetDescriptor<ComboBox> FIRST_NAME = declare("First Name", ComboBox.class);
			public static final AssetDescriptor<TextBox> SECOND_NAME = declare("Second Name", TextBox.class);
			public static final AssetDescriptor<RadioGroup> ZIP_CODE = declare("Zip Code", RadioGroup.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateOwnershipAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressOwnershipValidationPopup_container']"));
		}

		public static final class AdditionalInterestInformation extends MetaData {
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
			public static final AssetDescriptor<TextBox> SECOND_NAME = declare("Second Name", TextBox.class);
			public static final AssetDescriptor<RadioGroup> ZIP_CODE = declare("Zip Code", RadioGroup.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAddlnInterestAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressAddlnInterestValidationPopup_container']"));
			public static final AssetDescriptor<Button> ADD_ADDITIONAL_INTEREST = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAAdditionalInterest"));
		}

	}

	public static final class FormsTab extends MetaData {
		public static final AssetDescriptor<AutoSSForms.AutoSSPolicyFormsController> POLICY_FORMS = declare("Policy Forms", AutoSSForms.AutoSSPolicyFormsController.class, PolicyForms.class);
		public static final AssetDescriptor<AutoSSForms.AutoSSVehicleFormsController> VEHICLE_FORMS = declare("Vehicle Forms", AutoSSForms.AutoSSVehicleFormsController.class, VehicleForms.class);
		public static final AssetDescriptor<AutoSSForms.AutoSSDriverFormsController> DRIVER_FORMS = declare("Driver Forms", AutoSSForms.AutoSSDriverFormsController.class, DriverForms.class);

		public static final class PolicyForms extends MetaData {
			// formAssetLocator =
			// By.id("addComponentPopup_PolicyEndorsementFormsManager_container");
		}

		public static final class VehicleForms extends MetaData {
			public static final AssetDescriptor<AssetList> AALP = declare("AALP", AssetList.class, CommonFormMeta.class, false, By.id("addComponentPopup_VehicleEndorsementFormsManager_container"));
			public static final AssetDescriptor<AssetList> AA09 = declare("AA09", AssetList.class, CommonFormMeta.class, false, By.id("addComponentPopup_VehicleEndorsementFormsManager_container"));
		}

		public static final class DriverForms extends MetaData {
			public static final AssetDescriptor<AssetList> ADBE = declare("ADBE", AssetList.class, CommonFormMeta.class, false, By.id("addComponentPopup_DriverEndorsementFormsManager_container"));
			public static final AssetDescriptor<AssetList> SR22 = declare("SR22", AssetList.class, SR22FormMeta.class, false, By.id("addComponentPopup_DriverEndorsementFormsManager_container"));
		}

		public static final class CommonFormMeta extends MetaData {
			public static final AssetDescriptor<TextBox> FORM_CODE = declare("Form Code", TextBox.class);
			public static final AssetDescriptor<TextBox> FORM_DESCRIPTION = declare("Form Description", TextBox.class);
		}

		public static final class SR22FormMeta extends MetaData {
			public static final AssetDescriptor<TextBox> FINANCIAL_RESPONSIBILITY_TYPE = declare("Financial Responsibility Type", TextBox.class);
			public static final AssetDescriptor<TextBox> FILLING_DATE = declare("Filing Date", TextBox.class);
			public static final AssetDescriptor<TextBox> CASE_NUMBER = declare("Case Number", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
			public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		}
	}

	public static final class AssignmentTab extends MetaData {
		public static final AssetDescriptor<FillableTable> DRIVER_VEHICLE_RELATIONSHIP = declare("DriverVehicleRelationshipTable", FillableTable.class, DriverVehicleRelationshipTableRow.class, By.xpath("//table[contains(@id, 'riverAssignmentTable')]"));
		public static final AssetDescriptor<Button> ASSIGN = declare("Assign", Button.class, Waiters.AJAX, By.xpath("//input[@value='Assign']"));
		public static final AssetDescriptor<FillableTable> EXCESS_VEHICLES_TABLE = declare("ExcessVehiclesTable", FillableTable.class, ExcessVehiclesRow.class, By.xpath("//table[contains(@id, 'excessVehiclesTable')]"));

		public static final class DriverVehicleRelationshipTableRow extends MetaData {
			public static final AssetDescriptor<StaticElement> DRIVER = declare("Driver", StaticElement.class, Waiters.NONE);
			public static final AssetDescriptor<ComboBox> SELECT_VEHICLE = declare("Select Vehicle", ComboBox.class, Waiters.AJAX);
		}

		public static final class ExcessVehiclesRow extends MetaData {
			public static final AssetDescriptor<StaticElement> EXCESS_VEHICLES = declare("Excess Vehicle(s)", StaticElement.class, Waiters.NONE);
			public static final AssetDescriptor<ComboBox> SELECT_DRIVER = declare("Select Driver", ComboBox.class, Waiters.AJAX);
		}
	}

	public static final class PremiumAndCoveragesTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> APPLY_EVALUE_DISCOUNT = declare("Apply eValue Discount", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan", ComboBox.class);
		public static final AssetDescriptor<ComboBox> POLICY_TERM = declare("Policy Term", ComboBox.class);
		public static final AssetDescriptor<DetailedVehicleCoveragesRepeatAssetList> DETAILED_VEHICLE_COVERAGES = declare("DetailedVehicleCoverages", DetailedVehicleCoveragesRepeatAssetList.class, DetailedVehicleCoverages.class, false);

		public static final AssetDescriptor<ComboBox> BODILY_INJURY_LIABILITY = declare("Bodily Injury Liability", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Bodily Injury Liability']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> PROPERTY_DAMAGE_LIABILITY = declare("Property Damage Liability", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Property Damage Liability']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> UNINSURED_MOTORISTS_BODILY_INJURY = declare("Uninsured Motorists Bodily Injury", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Uninsured Motorists Bodily Injury']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> UNINSURED_MOTORIST_STACKED_UNSTACKED = declare("Uninsured Motorist Stacked/Unstacked", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Uninsured Motorist Stacked/Unstacked']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> UNDERINSURED_MOTORISTS_BODILY_INJURY = declare("Underinsured Motorists Bodily Injury", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Underinsured Motorists Bodily Injury']/ancestor::tr[1]//select"));	
		public static final AssetDescriptor<ComboBox> UNDERINSURED_MOTORIST_STACKED_UNSTACKED = declare("Underinsured Motorist Stacked/Unstacked", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Underinsured Motorist Stacked/Unstacked']/ancestor::tr[1]//select"));	
		public static final AssetDescriptor<ComboBox> FIRST_PARTY_BENEFITS = declare("First Party Benefits", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='First Party Benefits']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> MEDICAL_EXPENSES = declare("Medical Expenses", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Medical Expenses']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> FUNERAL_BENEFITS = declare("Funeral Benefits", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Funeral Benefits']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<RadioGroup> EXTRAORDINARY_MEDICAL_EXPENSE_BENEFITS = declare("Extraordinary Medical Expense Benefits", RadioGroup.class, Waiters.AJAX, false, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']/tbody/tr[13]/td[2]"));
		public static final AssetDescriptor<ComboBox> UNINSURED_MOTORIST_PROPERTY_DAMAGE_LIMIT = declare("Uninsured Motorist Property Damage Limit", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Uninsured Motorist Property Damage Limit']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY =
				declare("Uninsured/Underinsured Motorist Bodily Injury", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Uninsured/Underinsured Motorist Bodily Injury']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<RadioGroup> UNDERINSURED_MOTORIST_CONVERSION_COVERAGE =
				declare("Underinsured Motorist Conversion Coverage", RadioGroup.class, Waiters.AJAX, false,
						By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Underinsured Motorist Conversion Coverage']/ancestor::tr[1]//table[contains(@id,'policyCoverageLimitSelectEMB')]"));
		public static final AssetDescriptor<ComboBox> MEDICAL_PAYMENTS = declare("Medical Payments", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Medical Payments']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> PERSONAL_INJURY_PROTECTION = declare("Personal Injury Protection", ComboBox.class, By.xpath(".//table[@id='policyDataGatherForm:policy_vehicle_detail_coverage']//span[normalize-space(.)='Personal Injury Protection']/ancestor::tr[1]//select"));
		// public static final AssetDescriptor<StaticElement>
		// POLICY_LEVEL_LIABILITY_COVERAGES = declare("Policy Level Liability
		// Coverages", StaticElement.class,
		// By.id("policyDataGatherForm:policyTableTotalVehiclePremium:0:j_id_1_1x_56_1_a_1_2_k_1_1_3z"));

		public static final AssetDescriptor<ComboBox> COMPREGENSIVE_DEDUCTIBLE = declare("Comprehensive Deductible", ComboBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Comprehensive Deductible']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> COLLISION_DEDUCTIBLE = declare("Collision Deductible", ComboBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Collision Deductible']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> UNINSURED_MOTORIST_PROPERTY_DAMAGE = declare("Uninsured Motorist Property Damage", ComboBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Uninsured Motorist Property Damage']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<TextBox> SPECIAL_EQUIPMENT_COVERAGE = declare("Special Equipment Coverage", TextBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Special Equipment Coverage']/ancestor::tr[1]//input"));
		public static final AssetDescriptor<TextBox> SPECIAL_EQUIPMENT_MD = declare("Special Equipment", TextBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Special Equipment']/ancestor::tr[1]//input"));
		public static final AssetDescriptor<ComboBox> FULL_SAFETY_GLASS = declare("Full Safety Glass", ComboBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Full Safety Glass']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> RENTAL_REIMBURSEMENT = declare("Rental Reimbursement", ComboBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Rental Reimbursement']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> TOWING_AND_LABOR_COVERAGE = declare("Towing and Labor Coverage", ComboBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Towing and Labor Coverage']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<ComboBox> NEW_CAR_ADDED_PROTECTION = declare("New Car Added Protection", ComboBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='New Car Added Protection']/ancestor::tr[1]//select"));
		public static final AssetDescriptor<TextBox> PURCHASE_DATE = declare("Purchase Date", TextBox.class, By.xpath(".//*[@id='policyDataGatherForm:premiumCoverageDetailPanel']//span[normalize-space(.)='Purchase Date']/ancestor::tr[1]//input"));

		public static final AssetDescriptor<CheckBox> UNACCEPTABLE_RISK_SURCHARGE = declare("Unacceptable Risk Surcharge", CheckBox.class, Waiters.AJAX, By.id("policyDataGatherForm:unacceptableRiskSurchargeCheckbox"));
		public static final AssetDescriptor<TextBox> REASON = declare("Reason", TextBox.class, Waiters.AJAX, By.id("policyDataGatherForm:unacceptableRiskSurchargeReason"));
		// public static final AssetDescriptor<CheckBox>
		// UNVERIFIABLE_DRIVING_RECORD_SURCHARGE = declare("Unverifiable Driving
		// Record Surcharge", CheckBox.class, Waiters.AJAX);

		public static final AssetDescriptor<Button> CALCULATE_PREMIUM = declare("Calculate Premium", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:premiumRecalc"));
		public static final AssetDescriptor<RadioGroup> ADDITIONAL_SAVINGS_OPTIONS = declare("Additional Savings Options", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:visibleRadio']"));
		public static final AssetDescriptor<CheckBox> MEMBERSHIP = declare("Membership", CheckBox.class, By.xpath("//td[text()='Membership']//input"));
		public static final AssetDescriptor<CheckBox> MULTI_CAR = declare("Multi-car", CheckBox.class, By.xpath("//td[text()='Multi-car']//input"));
		public static final AssetDescriptor<CheckBox> LIFE = declare("Life", CheckBox.class, By.xpath("//td[text()='Life']//input"));
		public static final AssetDescriptor<CheckBox> HOME = declare("Home", CheckBox.class, By.xpath("//td[text()='Home']//input"));
		public static final AssetDescriptor<CheckBox> RENTERS = declare("Renters", CheckBox.class, By.xpath("//td[text()='Renters']//input"));
		public static final AssetDescriptor<CheckBox> CONDO = declare("Condo", CheckBox.class, By.xpath("//td[text()='Condo']//input"));
		public static final AssetDescriptor<CheckBox> MOTORCYCLE = declare("Motorcycle", CheckBox.class, By.xpath("//td[text()='Motorcycle']//input"));

		public static final AssetDescriptor<DialogAssetList> OVERRRIDE_PREMIUM_DIALOG = declare("Override Premium", DialogAssetList.class, OverridePremiumDialog.class,
				By.xpath("//div[@id='premiumOverridePopup_container']//div[@id='premiumOverridePopup_content']"));

		public static final class OverridePremiumDialog extends MetaData {
			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.AJAX, false,
					By.id("policyDataGatherForm:overridePremiumLink"));

			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for Override", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OTHER_REASON = declare("Other Reason", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ORIGINAL_BODILY_INJURY_OVERRIDE_PREMIUM_BY_FLAT_AMOUNT = declare("Bodily Injury Liability By Flat Amount", TextBox.class, Waiters.AJAX, By.xpath("//div[@id='premiumOverridePopup_content']//td[contains(.,'Bodily Injury Liability') and contains(.,'Original')]//following-sibling::td//input[contains(@id,'premiumOverrideInfoForm:deltaPremiumAmt')]"));
			public static final AssetDescriptor<TextBox> ORIGINAL_BODILY_INJURY_OVERRIDE_PREMIUM_BY_PERCENTAGE = declare("Bodily Injury Liability By Percentage", TextBox.class, Waiters.AJAX, By.xpath("//div[@id='premiumOverridePopup_content']//td[contains(.,'Bodily Injury Liability') and contains(.,'Original')]//following-sibling::td//input[contains(@id,'premiumOverrideInfoForm:percentageAmt')]"));

			public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.AJAX, false,
					By.id("premiumOverrideInfoForm:premiumOverrideSaveBtn"));
			public static final AssetDescriptor<Button> BUTTON_CANCEL_POPUP = declare(AbstractDialog.DEFAULT_POPUP_CLOSER_NAME, Button.class, Waiters.DEFAULT, false,
					By.id("premiumOverrideInfoForm:premiumOverrideCancelBtn"));

		}
		
		public static final class DetailedVehicleCoverages extends MetaData {
			public static final AssetDescriptor<ComboBox> COMPREGENSIVE_DEDUCTIBLE = declare("Comprehensive Deductible", ComboBox.class, By.xpath(".//span[normalize-space(.)='Comprehensive Deductible']/ancestor::tr[1]//select"));
			public static final AssetDescriptor<ComboBox> FULL_SAFETY_GLASS = declare("Full Safety Glass", ComboBox.class, By.xpath(".//span[normalize-space(.)='Full Safety Glass']/ancestor::tr[1]//select"));
			public static final AssetDescriptor<TextBox> EXCESS_ELECTRONIC_EQUIPMENT = declare("Excess Electronic Equipment", TextBox.class, By.xpath(".//span[normalize-space(.)='Excess Electronic Equipment']/ancestor::tr[1]//input"));
			public static final AssetDescriptor<ComboBox> COLLISION_DEDUCTIBLE = declare("Collision Deductible", ComboBox.class, By.xpath(".//span[normalize-space(.)='Collision Deductible']/ancestor::tr[1]//select"));
			public static final AssetDescriptor<ComboBox> TRANSPORTATION_EXPENSE = declare("Transportation Expense", ComboBox.class, By.xpath(".//span[normalize-space(.)='Transportation Expense']/ancestor::tr[1]//select"));
			public static final AssetDescriptor<ComboBox> TOWING_AND_LABOR_COVERAGE = declare("Towing and Labor Coverage", ComboBox.class, By.xpath(".//span[normalize-space(.)='Towing and Labor Coverage']/ancestor::tr[1]//select"));
			public static final AssetDescriptor<ComboBox> NEW_CAR_ADDED_PROTECTION = declare("New Car Added Protection", ComboBox.class, By.xpath(".//span[normalize-space(.)='New Car Added Protection']/ancestor::tr[1]//select"));
			public static final AssetDescriptor<TextBox> PURCHASE_DATE = declare("Purchase Date", TextBox.class, By.xpath(".//span[normalize-space(.)='Purchase Date']/ancestor::tr[1]//input"));
			// *** DO NOT DECLARE "Waive Liability" and "Vehicle Coverage" controls in this MetaData. They are added within DetailedVehicleCoveragesRepeatAssetList.class ***
		}
	}

	public static final class DriverActivityReportsTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE = declare("Has the customer expressed interest in purchasing the quote?", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> SALES_AGENT_AGREEMENT = declare("Sales Agent Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:agentAgreesAAASSMvrOrClueReportDisclaimerComponent']"));
		public static final AssetDescriptor<Button> VALIDATE_DRIVING_HISTORY = declare("Validate Driving History", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:submitReports"));

		public static final AssetDescriptor<FillableTable> ORDER_CLUE_REPORT = declare("OrderCLUEReport", FillableTable.class, OrderCLUEReportRow.class, By.xpath("//table[@id='policyDataGatherForm:clueReports']"));
		public static final AssetDescriptor<FillableTable> ORDER_MVR = declare("OrderMVRReport", FillableTable.class, OrderMVRReportRow.class, By.xpath("//table[@id='policyDataGatherForm:mvrReportsDataTable']"));
		public static final AssetDescriptor<FillableTable> ORDER_INTERNAL_CLAIMS_REPORT = declare("OrderInternalClaimsReport", FillableTable.class, OrderInternalClaimsReportRow.class, By.xpath("//table[@id='policyDataGatherForm:claimsReports']"));

		public static final class OrderCLUEReportRow extends MetaData {
			public static final AssetDescriptor<RadioGroup> SELECT = declare("Select", RadioGroup.class);
			public static final AssetDescriptor<StaticElement> RESIDENTIAL_ADDRESS = declare("Residential Address", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RESPONSE = declare("Response", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ADDRESS_TYPE = declare("Address Type", StaticElement.class);
		}

		public static final class OrderMVRReportRow extends MetaData {
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

		public static final class OrderInternalClaimsReportRow extends MetaData {
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
		public static final AssetDescriptor<AssetList> DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAASSAdHocPrintDocs']"));
		public static final AssetDescriptor<AssetList> REQUIRED_TO_BIND = declare("RequiredToBind", AssetList.class, RequiredToBind.class, By.xpath("//div[@id='policyDataGatherForm:componentView_OptionalSupportingDocuments']"));
		public static final AssetDescriptor<AssetList> REQUIRED_TO_ISSUE = declare("RequiredToIssue", AssetList.class, RequiredToIssue.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_MandatorySupportingDocuments']"));
		public static final AssetDescriptor<AssetList> GENERAL_INFORMATION = declare("GeneralInformation", AssetList.class, GeneralInformation.class, By.id("policyDataGatherForm:componentView_AAAInsuredBindInformation"));
		public static final AssetDescriptor<AssetList> PAPERLESS_PREFERENCES = declare("PaperlessPreferences", AssetList.class, PaperlessPreferences.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAPaperlessPreferences']"));
        public static final AssetDescriptor<AssetList> DOCUMENT_PRINTING_DETAILS = declare("DocumentPrintingDetails", AssetList.class, DocumentPrintingDetails.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PurchaseAction']"));


		public static final AssetDescriptor<RadioGroup> AGREEMENT = declare("Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:AAADocAgreement_agreement']"));
		public static final AssetDescriptor<TextBox> CASE_NUMBER = declare("Case Number", TextBox.class);
		public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
		public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
		public static final AssetDescriptor<ComboBox> INCLUDE_WITH_EMAIL = declare("Include with Email", ComboBox.class);
		
		public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized By", TextBox.class);

		public static final class DocumentsForPrinting extends MetaData {
			public static final AssetDescriptor<RadioGroup> AAA_USAGE_BASED_INSURANCE_PROGRAM_TERMS_AND_CONDITIONS = declare("AAA Usage Based Insurance Program Terms and Conditions", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AAA_WITH_SMARTTRECK_ACKNOWLEDGEMENT = declare("AAA with SMARTtrek Acknowledgement of T&Cs and Privacy Policies", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ACP_SMARTTRECK_SUBSCRIPTION_TERMS = declare("ACP SMARTtrek Subscription Terms and Conditions", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AUTO_INSURANCE_APPLICATION = declare("Auto Insurance Application", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AUTO_INSURANCE_QUOTE = declare("Auto Insurance Quote", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AUTOPAY_AUTHORIZATION_FORM = declare("AutoPay Authorization Form", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CONSUMER_INFORMATION_NOTICE = declare("Consumer Information Notice", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> MEDICAL_PAYMENTS_REJECTION_OF_COVERAGE = declare("Medical Payments Rejection of Coverage", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION = declare("Uninsured and Underinsured Motorist Coverage Selection", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> NAMED_DRIVER_EXCLUSION = declare("Named Driver Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CRITICAL_INFORMATION_FOR_TEENAGE_DRIVERS_AND_THEIR_PARENTS = declare("Critical Information for Teenage Drivers and Their Parents", RadioGroup.class, Waiters.AJAX);
			//VA
			public static final AssetDescriptor<RadioGroup> EVALUE_ACKNOWLEDGEMENT = declare("eValue Acknowledgement", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> ADVERSE_ACTION_UNDERWRITING_DECISION_NOTICE = declare("Adverse Action Underwriting Decision Notice", RadioGroup.class, Waiters.AJAX);

			public static final AssetDescriptor<Link> BTN_GENERATE_DOCUMENTS = declare("Generate Documents", Link.class, Waiters.NONE, By.xpath("//a[@id='policyDataGatherForm:generate_link']"));
			public static final AssetDescriptor<Link> BTN_GENERATE_ESIGNATURE_DOCUMENTS = declare("Generate eSignature Documents", Link.class, Waiters.NONE, By.xpath("//a[@id='policyDataGatherForm:generate_link_esign']"));
			public static final AssetDescriptor<AssetList> ENTER_RECIPIENT_EMAIL_ADDRESS_DIALOG = declare("EnterRecipientEmailAddress", AssetList.class, EnterRecipientEmailAddressDialog.class, By.xpath("//div[@id='errorDialog1_content_scroller']"));

			public static final class EnterRecipientEmailAddressDialog extends MetaData {
				public static final AssetDescriptor<TextBox> RECIPIENT_EMAIL_ADDRESS = declare("Recipient Email Address", TextBox.class, Waiters.NONE, By.id("recipientEmailAddressForm:recpEmail"));
				public static final AssetDescriptor<Button> BTN_OK = declare("OK", Button.class, Waiters.AJAX, By.id("recipientEmailAddressForm:okButton"));

				public static final AssetDescriptor<Link> LINK_CANCEL = declare("Cancel", Link.class, Waiters.AJAX, By.id("recipientEmailAddressForm:cancelBtn1"));
			}

		}

		public static final class EnterRecipientEmailAddressDialog extends MetaData {
			public static final AssetDescriptor<TextBox> RECIPIENT_EMAIL_ADDRESS = declare("Recipient Email Address", TextBox.class, Waiters.NONE, By.id("recipientEmailAddressForm:recpEmail"));
			public static final AssetDescriptor<Button> BTN_OK = declare("OK", Button.class, Waiters.AJAX, By.id("recipientEmailAddressForm:okButton"));

			public static final AssetDescriptor<Link> LINK_CANCEL = declare("Cancel", Link.class, Waiters.AJAX, By.id("recipientEmailAddressForm:cancelBtn1"));
		}

		public static final class RequiredToBind extends MetaData {
			public static final AssetDescriptor<RadioGroup> AUTO_INSURANCE_APPLICATION = declare("Auto Insurance Application", RadioGroup.class, Waiters.NONE,false, By.xpath("//table[@id='policyDataGatherForm:document_PAA']"));
			public static final AssetDescriptor<RadioGroup> MEDICAL_PAYMENTS_REJECTION_OF_COVERAGE = declare("Medical Payments Rejection of Coverage", RadioGroup.class, Waiters.NONE, false, By.xpath("//table[@id='policyDataGatherForm:document_RMPC']"));
			public static final AssetDescriptor<RadioGroup> PERSONAL_AUTO_APPLICATION = declare("Personal Auto Application", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS = declare("AAA Insurance with SMARTtrek Acknowledgement of Terms and Conditions and Privacy Policies", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> NAMED_DRIVER_EXCLUSION_ELECTION = declare("Named Driver Exclusion Election", RadioGroup.class);
			//DC
			public static final AssetDescriptor<RadioGroup> INFORMED_CONSENT_FORM_UNINSURED_MOTORIST_COVERAGE = declare("Informed Consent Form - Uninsured Motorist Coverage", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> DISTRICT_OF_COLUMBIA_COVERAGE_SELECTION_REJECTION_FORM = declare("District of Columbia Coverage Selection/Rejection Form", RadioGroup.class);
			//DE
			public static final AssetDescriptor<RadioGroup> DELAWARE_MOTORISTS_PROTECTION_ACT = declare("Delaware Motorists Protection Act", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> IMPORTANT_INFORMATION_PERSONAL_INJURY_PROTECTION = declare("Important Information Personal Injury Protection (PIP) - Delaware Understanding (PIP) Deductibles", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> ACKNOLEDGEMENT_OF_DEDUCTIBLE_SELECTIONS = declare("Acknowledgement of Deductible Selections", RadioGroup.class);
			//MD
			public static final AssetDescriptor<RadioGroup> NOTICE_AND_WAIVER_OF_INCREASED_LIMITS_OF_UMC = declare("Notice And Waiver Of Increased Limits Of Uninsured Motorists Coverage", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> MARYLAND_NOTICE_AND_WAIVER_OF_PIP_COVERAGE = declare("Maryland Notice And Waiver Of Personal Injury Protection (PIP) Coverage", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> MARYLAND_AUTO_INSURANCE_APPLICATION = declare("Maryland Auto Insurance Application", RadioGroup.class);
			//NJ
			public static final AssetDescriptor<RadioGroup> NJ_AUTO_STANDARD_POLICY_COVERAGE_SELECTION_FORM = declare("NJ Auto Standard Policy Coverage Selection Form", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> ACNOWLEDGEMENT_OF_REQUIREMENT_FOR_INSURANCE_INSPECTION = declare("Acknowledgement of Requirement for Insurance Inspection", RadioGroup.class);
			//NY
			public static final AssetDescriptor<RadioGroup> ACNOWLEDGEMENT_OF_REQUIREMENT_FOR_PHIOTO_INSPECTION = declare("Acknowledgement of Requirement for Photo Inspection", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> NEW_YORK_AUTO_INSURANCE_APPLICATION = declare("New York Auto Insurance Application", RadioGroup.class);
			//PA
			public static final AssetDescriptor<RadioGroup> UNUNSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION = declare("Uninsured Motorists Coverage Selection/Rejection", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> UNDERINSURED_MOTORISTS_COVERAGE_SELECTION_REJECTION = declare("Underinsured Motorists Coverage Selection/Rejection", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> FIRST_PARTY_BENEFITS_COVERAGE_AND_LIMITS_SELECTION_FORM = declare("First Party Benefits Coverage and Limits Selection Form", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> PENNSYLVANIA_IMPORTANT_NOTICE = declare("Pennsylvania Important Notice", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> PENNSYLVANIA_NOTICE_TO_NAMED_INSURED_REGARDING_TORT_OPTIONS = declare("Pennsylvania Notice to Named Insured Regarding Tort Options", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> INVOICE_MINIMUM_COVERAGES = declare("Invoice-Minimum Coverages", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> NAMED_DRIVER_EXCLUSION_ENDORSEMENT = declare("Named Driver Exclusion Endorsement", RadioGroup.class, Waiters.NONE);
			//WV
			public static final AssetDescriptor<RadioGroup> UNINSURED_UNDERINSURED_MOTORISTS_COVERAGE_OFFER = declare("Uninsured/Underinsured Motorists Coverage Offer", RadioGroup.class, Waiters.NONE);
			//Nano
			public static final AssetDescriptor<RadioGroup> NON_OWNER_AUTOMOBILE_ENDORSEMENT = declare("Non-Owner Automobile Endorsement", RadioGroup.class, Waiters.NONE, By.id("policyDataGatherForm:supportingDocsContent_OptionalSupportingDocuments_NONOE"));
            //IN	
			public static final AssetDescriptor<RadioGroup> REJECTION_OF_UMPD_COVERAGE = declare("Rejection of UMPD Coverage", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> UNINSURED_UNDERINSURED_COVERAGE_REJECTION_OR_ELECTION_OF_LOWER_LIMITS = declare("Uninsured/Underinsured Coverage - Rejection or Election of Lower Limits", RadioGroup.class, Waiters.NONE);
            //VA
			public static final AssetDescriptor<RadioGroup> EVALUE_ACKNOWLEDGEMENT = declare("eValue Acknowledgement", RadioGroup.class, Waiters.NONE);			//OK
			public static final AssetDescriptor<RadioGroup> UNINSURED_UNDERINSURED_LIMIT_SELECTION_FORM = declare("Uninsured/Underinsured Limit Selection Form", RadioGroup.class, Waiters.NONE);
			//AZ
			public static final AssetDescriptor<RadioGroup> UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION = declare("Uninsured and Underinsured Motorist Coverage Selection", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> NAMED_DRIVER_EXCLUSION = declare("Named Driver Exclusion", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> COVERAGE_ACCEPTANCE_STATEMENT = declare("Coverage Acceptance Statement", RadioGroup.class);
 		}

		public static final class RequiredToIssue extends MetaData {
			public static final AssetDescriptor<RadioGroup> PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE = declare("Photos for salvage vehicle with physical damage coverage", RadioGroup.class, Waiters.NONE);
			//NJ
			public static final AssetDescriptor<RadioGroup> PROOF_OF_ANTI_THEFT_RECOVERY_DEVICE = declare("Proof of Anti-Theft Recovery Device", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> CARGO_VEHICLE_INSPECTION_COMPLETED = declare("CARCO Vehicle Inspection completed or Prior Physical Damage Coverage Inspection Waiver", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> SEPARATE_VEHICLE_1 = declare("Separate Vehicle 1", RadioGroup.class, By.xpath("//table[@id='policyDataGatherForm:driverName_0']"));
			public static final AssetDescriptor<RadioGroup> SEPARATE_VEHICLE_2 = declare("Separate Vehicle 2", RadioGroup.class, By.xpath("//table[@id='policyDataGatherForm:driverName_1']"));
			//OK
			public static final AssetDescriptor<RadioGroup> COVERAGE_SELECTION_FORM = declare("Coverage Selection Form", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_PRIOR_INSURANCE = declare("Proof of Prior Insurance", RadioGroup.class, Waiters.NONE);
            //PA
			public static final AssetDescriptor<RadioGroup> PROOF_OF_CURRENT_INSURANCE_FOR = declare("Proof of Current Insurance for", RadioGroup.class, Waiters.NONE);
		
			public static final AssetDescriptor<RadioGroup> PROOF_OF_DEFENSIVE_DRIVER_COURSE_COMPLETION = declare("Proof of Defensive Driver course completion", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_GOOD_STUDENT_DISCOUNT = declare("Proof of Good Student Discount", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_SMART_DRIVER_COURSE_COMPLETION = declare("Proof of Smart Driver Course Completion", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES = declare("Proof of purchase date (bill of sale) for new vehicle(s)", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES = declare("Proof of equivalent new car added protection with prior carrier for new vehicle(s)", RadioGroup.class, Waiters.NONE);
			public static final AssetDescriptor<RadioGroup> CANADIAN_MVR_FOR_DRIVER = declare("Canadian MVR for (driver)", RadioGroup.class, Waiters.NONE);
		}

		public static final class GeneralInformation extends MetaData {
			public static final AssetDescriptor<TextBox> EXISTING_AAA_LIFE_POLICY_NUMBER = declare("Existing AAA Life Policy #", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<TextBox> EXISTING_AAA_HOME_POLICY_NUMBER = declare("Existing AAA Home Policy #", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<TextBox> EXISTING_AAA_RENTERS_POLICY_NUMBER = declare("Existing AAA Renters Policy #", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<TextBox> EXISTING_AAA_CONDO_POLICY_NUMBER = declare("Existing AAA Condo Policy #", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<TextBox> WORK_PHONE_NUMBER = declare("Work Phone #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MOBILE_PHONE_NUMBER = declare("Mobile Phone #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized By", TextBox.class, Waiters.AJAX);

		}

		public static final class PaperlessPreferences extends MetaData {
			public static final AssetDescriptor<TextBox> ENROLLED_IN_PAPERLESS = declare("Enrolled in Paperless?", TextBox.class, Waiters.NONE);

			public static final AssetDescriptor<Button> BTN_MANAGE_PAPERLESS_PREFERENCES = declare("Manage Paperless Preferences", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:paperlessPreferenceButton_AAAPaperlessPreferences"));
            public static final AssetDescriptor<Button> EDIT_PAPERLESS_PREFERENCES_BTN_DONE = declare("Done", Button.class, By.xpath("//input[@id='policyDataGatherForm:preferencesPopupCancel']"));
        }

        public static final class DocumentPrintingDetails extends MetaData {
            public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class, Waiters.NONE);
            public static final AssetDescriptor<ComboBox> METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class, Waiters.NONE);
            public static final AssetDescriptor<ComboBox> INCLUDE_WITH_EMAIL = declare("Include with Email", ComboBox.class, Waiters.NONE);
        }
	}

	//TODO done till this row
	public static final class ChangePendedEndEffDateActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
	}

	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	public static final class GenerateOnDemandDocumentActionTab extends MetaData {
		public static final AssetDescriptor<FillableDocumentsTable> ON_DEMAND_DOCUMENTS = declare("OnDemandDocuments", FillableDocumentsTable.class, DocumentsRow.class, By.xpath("(//div[@id='policyDataGatherForm:componentView_AAAAdHocOnDemandDocs']//table)[1]"));
		public static final AssetDescriptor<RadioGroup> DELIVERY_METHOD = declare("Delivery Method", RadioGroup.class, Waiters.AJAX, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAAdHocOnDemandDocs_body']/table"));
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class, Waiters.AJAX);

		public static final class DocumentsRow extends MetaData {
			public static final AssetDescriptor<CheckBox> SELECT = declare(DocGenConstants.OnDemandDocumentsTable.SELECT, CheckBox.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NUMBER = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NAME = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FREE_FORM_TEXT = declare("Free Form Text", TextBox.class, Waiters.AJAX, false, By.id("policyDataGatherForm:uwLetterMsg_AU03"));
		}
	}

	public static final class StartNonPremiumBearingEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_DATE = declare("Endorsement date", TextBox.class);
	}

	public static final class NonPremiumBearingEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> INSURED_PARTY_SELECTION = declare("Insured Party Selection", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> PRIMARY_INSURED = declare("Primary Insured?", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> TITLE = declare("Title", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AssetDescriptor<PartySearchTextBox> LAST_NAME = declare("Last Name", PartySearchTextBox.class);
		public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AssetDescriptor<TextBox> AGE = declare("Age", TextBox.class);
		public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital Status", ComboBox.class);
		public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
		public static final AssetDescriptor<ComboBox> ADDRESS_TYPE = declare("Address Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
		public static final AssetDescriptor<ComboBox> BUSINESS_TYPE = declare("Business Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> USAGE = declare("Usage", ComboBox.class);
		public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class);
		public static final AssetDescriptor<TextBox> VIN_MATCHED = declare("Vin Matched", TextBox.class);
		public static final AssetDescriptor<ComboBox> CHOOSE_VIN = declare("Choose Vin", ComboBox.class);
		public static final AssetDescriptor<ComboBox> NO_VIN_REASON = declare("No Vin Reason", ComboBox.class);
		public static final AssetDescriptor<ComboBox> MODEL_YEAR = declare("Model Year", ComboBox.class);
		public static final AssetDescriptor<TextBox> MANUFACTURE_YEAR = declare("Manufacture Year", TextBox.class);
		public static final AssetDescriptor<ComboBox> MAKE = declare("Make", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_MAKE = declare("Other Make", TextBox.class);
		public static final AssetDescriptor<ComboBox> MODEL = declare("Model", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_MODEL = declare("Other Model", TextBox.class);
		public static final AssetDescriptor<ComboBox> SERIES = declare("Series", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_SERIES = declare("Other Series", TextBox.class);
		public static final AssetDescriptor<ComboBox> BODY_STYLE = declare("Body Style", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_BODY_STYLE = declare("Other Body Style", TextBox.class);
		public static final AssetDescriptor<ComboBox> PERFORMANCE = declare("Performance", ComboBox.class);
		public static final AssetDescriptor<ComboBox> REGISTERED_STATE = declare("Registered State", ComboBox.class);
		public static final AssetDescriptor<TextBox> PLATE_NUMBER = declare("Plate Number", TextBox.class);
		public static final AssetDescriptor<TextBox> VALUE = declare("Value", TextBox.class);
		public static final AssetDescriptor<TextBox> ADJUSTMENT_TO_VALUE = declare("Adjustment to Value", TextBox.class);
		public static final AssetDescriptor<TextBox> ADJUSTED_VALUE = declare("Adjusted Value", TextBox.class);
		public static final AssetDescriptor<TextBox> COST_NEW = declare("Cost New ($)", TextBox.class);
		public static final AssetDescriptor<TextBox> STATED_AMOUNT = declare("Stated Amount ($)", TextBox.class);
		public static final AssetDescriptor<TextBox> SYMBOL = declare("Symbol", TextBox.class);
		public static final AssetDescriptor<TextBox> COMP_SYMBOL = declare("Comp Symbol", TextBox.class);
		public static final AssetDescriptor<TextBox> COLL_SYMBOL = declare("Coll Symbol", TextBox.class);
		public static final AssetDescriptor<TextBox> BI_SYMBOL = declare("BI Symbol", TextBox.class);
		public static final AssetDescriptor<TextBox> PD_SYMBOL = declare("PD Symbol", TextBox.class);
		public static final AssetDescriptor<TextBox> PIP_MED_SYMBOL = declare("PIP/MED Symbol", TextBox.class);
		public static final AssetDescriptor<TextBox> LIABILITY_SYMBOL = declare("Liability Symbol", TextBox.class);
		public static final AssetDescriptor<RadioGroup> USAGE_BASED_RATING = declare("Usage Based Rating?", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> INSURED_AGREES_TO_UBI_TERMS_CONDITIONS = declare("Insured agrees to UBI Terms Conditions?", RadioGroup.class);
		public static final AssetDescriptor<TextBox> RANK = declare("Rank", TextBox.class);
		public static final AssetDescriptor<ComboBox> INTEREST_TYPE = declare("Interest Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> SECOND_NAME = declare("Second Name", TextBox.class);
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class);
		public static final AssetDescriptor<TextBox> LOAN = declare("Loan #", TextBox.class);
		public static final AssetDescriptor<TextBox> MORTGAGE_AMOUNT = declare("Mortgage Amount", TextBox.class);
		public static final AssetDescriptor<TextBox> LOSS_PAYEE_LEASE_EXPIRATION_DATE = declare("Loss Payee / Lease Expiration Date", TextBox.class);
		public static final AssetDescriptor<RadioGroup> ADD_AS_ADDITIONAL_INSURED = declare("Add as Additional Insured", RadioGroup.class);
	}

	public static final class AddFromCancelActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
		public static final AssetDescriptor<RadioGroup> USE_ORIGINAL_POLICY_NUMBER = declare("Use Original Policy Number?", RadioGroup.class);
	}

	public static final class DeclineByCompanyActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AssetDescriptor<TextBox> DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class SplitActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON_FOR_SPLIT = declare("Reason for Split", ComboBox.class);
	}

	public static final class DoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
		public static final AssetDescriptor<TextBox> SUPPORTING_DATA = declare("Supporting Data", TextBox.class);
	}

	public static final class DeclineByCustomerActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AssetDescriptor<TextBox> DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class CopyQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class RemoveCancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	public static final class ExtensionRenewalActionTab extends MetaData {
	}

	public static final class CancellationActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancel Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> OTHER_REASON = declare("Other reason", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized By", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
	}

	public static final class RemoveDoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	public static final class ChangeBrokerActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TRANSFER_ID = declare("Transfer ID", TextBox.class);
		public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class);
		public static final AssetDescriptor<RadioGroup> TRANSFER_TYPE = declare("Transfer Type", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", RadioGroup.class);
		public static final AssetDescriptor<TextBox> TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
		public static final AssetDescriptor<RadioGroup> COMMISSION_IMPACT = declare("Commission Impact", RadioGroup.class);
		public static final AssetDescriptor<TextBox> SOURCE_CHANNEL = declare("Source Channel", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_LOCATION_TYPE = declare("Source Location Type", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_LOCATION_NAME = declare("Source Location Name", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_INSURANCE_AGENT = declare("Source Insurance Agent", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_CHANNEL = declare("Target channel", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_LOCATION_TYPE = declare("Target location type", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_LOCATION_NAME = declare("Target location Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> TARGET_INSURANCE_AGENT = declare("Target Insurance agent", ComboBox.class);
		public static final AssetDescriptor<DialogSingleSelector> LOCATION_NAME = declare("Location Name", DialogSingleSelector.class, ChangeLocationMetaData.class);
		public static final AssetDescriptor<ComboBox> INSURANCE_AGENT = declare("Insurance Agent", ComboBox.class);

		public static final class ChangeLocationMetaData extends MetaData {
			public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
			public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);

			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By.id("policyDataGatherForm:changeTargetProducerCd"));
		}
	}

	public static final class CancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.NONE);
		public static final AssetDescriptor<TextBox> DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.NONE);
		public static final AssetDescriptor<TextBox> SUPPORTING_DATA = declare("Supporting Data", TextBox.class, Waiters.NONE);
		public static final AssetDescriptor<RadioGroup> PRINT_NOTICE = declare("Print Notice", RadioGroup.class);
	}

	public static final class SpinActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON_FOR_SPIN = declare("Reason for Spin", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> INSUREDS_APPROVE_REMOVAL_OF_DRIVER_AND_VEHICLES = declare("Insureds approve removal of drivers and vehicles", RadioGroup.class);
	}

	public static final class ReinstatementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> AUTHORIZED_BY = declare("Authorized By", TextBox.class, Waiters.AJAX);
	}

	public static final class RewriteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
	}

	public static final class BindActionTab extends MetaData {
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
		public static final AssetDescriptor<FillableErrorTable> ERROR_OVERRIDE = declare("ErrorsOverride", FillableErrorTable.class, RuleRow.class, By.id("errorsForm:msgList"));

		public static final class RuleRow extends MetaData {
			public static final AssetDescriptor<CheckBox> OVERRIDE = declare("Override", CheckBox.class);
			public static final AssetDescriptor<CheckBox> APPROVAL = declare("Approval", CheckBox.class);
			public static final AssetDescriptor<Link> CODE = declare("Code", Link.class);
			public static final AssetDescriptor<StaticElement> SEVERITY = declare("Severity", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MESSAGE = declare("Message", StaticElement.class);
			public static final AssetDescriptor<RadioGroup> DURATION = declare("Duration", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class);
		}
	}

	public static final class ErrorTabCalculatePremium extends MetaData {
		public static final AssetDescriptor<FillableErrorTable> ERROR_OVERRIDE = declare("ErrorsOverride", FillableErrorTable.class, RuleRow.class, By.id("errorsForm:msgList"));

		public static final class RuleRow extends MetaData {
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
	
	public static final class RollOnChangesErrorTab extends MetaData {
		public static final AssetDescriptor<Button> PROCEED = declare("Proceed", Button.class, Waiters.AJAX, false, By.id("errorsForm:proceed"));
	}

	public static final class CopyFromPolicyActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class DeletePendedTransactionActionTab extends MetaData {
	}

	public static final class RemoveManualRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	public static final class ProposeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
	}

	public static final class ManualRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	public static final class RescindCancellationActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> RESCIND_CANCELLATION_DATE = declare("Rescind Cancellation Date", TextBox.class);
	}

	public static final class IssueActionTab extends MetaData {
	}

	public static final class AuthorityActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> AUTHORIZED_PERSON_REQUESTING_CHANGE = declare("Authorized Person Requesting Change", ComboBox.class);
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
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class, Waiters.NONE);
	}

	public static final class CreateQuoteVersionTab extends MetaData {
		public static final AssetDescriptor<StaticElement> VERSION_NUM = declare("Version #", StaticElement.class);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
	}
}
