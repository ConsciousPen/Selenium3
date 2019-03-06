/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata.policy;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.PartySearchTextBox;
import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import aaa.common.pages.Page;
import aaa.main.enums.DocGenConstants;
import aaa.main.metadata.DialogsMetaData;
import aaa.toolkit.webdriver.customcontrols.*;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.AssetListConfirmationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import aaa.toolkit.webdriver.customcontrols.endorsements.HomeSSEndorsementsMultiAssetList;
import toolkit.webdriver.controls.*;
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
public final class HomeSSMetaData {

	public static final class GeneralTab extends MetaData {
		// PolicyInfo
		public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> CONVERSION_DATE = declare("Conversion Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> APPLICATION_TYPE = declare("Application type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> SOURCE_POLICY = declare("Source Policy #", TextBox.class);
		public static final AssetDescriptor<TextBox> PRIOR_BASE_YEAR = declare("Prior base year", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> COMMISSION_TYPE = declare("Commission Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
		public static final AssetDescriptor<ComboBox> LEAD_SOURCE = declare("Lead source", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> CALVET = declare("CalVet", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> ADVERSELY_IMPACTED = declare("Adversely Impacted", ComboBox.class);
		public static final AssetDescriptor<ComboBox> EXTRAORDINARY_LIFE_CIRCUMSTANCE = declare("Extraordinary Life Circumstance", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> SOURCE_POLICY_NUMBER = declare("Source Policy #", TextBox.class);

		// Current Carrier
		public static final AssetDescriptor<ComboBox> IMMEDIATE_PRIOR_CARRIER = declare("Immediate prior carrier", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> OTHER_CARRIER_NAME = declare("Other carrier name", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER = declare("Continuous years with immediate prior carrier", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG = declare("Property insurance base date with CSAA IG", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> BASE_DATE_WITH_AAA = declare("Base date with AAA", TextBox.class, Waiters.AJAX);
	}

	public static final class ApplicantTab extends MetaData {

		public static final AssetDescriptor<MultiInstanceAfterAssetList> NAMED_INSURED = declare("NamedInsured", MultiInstanceAfterAssetList.class, NamedInsured.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHONamedInsured']"));
		public static final AssetDescriptor<AssetList> AAA_MEMBERSHIP = declare("AAAMembership", AssetList.class, AAAMembership.class, By
				.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOMembership']"));
		public static final AssetDescriptor<AssetList> DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODwellAddress']"));
		public static final AssetDescriptor<AssetList> PREVIOUS_DWELLING_ADDRESS = declare("PreviousDwellingAddress", AssetList.class, PreviousDwellingAddress.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOPreviousAddressComp']"));
		public static final AssetDescriptor<AssetList> MAILING_ADDRESS = declare("MailingAddress", AssetList.class, MailingAddress.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOMailingAddressComponent']"));
		public static final AssetDescriptor<AssetList> NAMED_INSURED_INFORMATION = declare("NamedInsuredInformation", AssetList.class, NamedInsuredInformation.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOCommunicationInfo']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> OTHER_ACTIVE_AAA_POLICIES = declare("OtherActiveAAAPolicies", MultiInstanceAfterAssetList.class, OtherActiveAAAPolicies.class,
				By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOOtherOrPriorPolicyComponent']"));
		public static final AssetDescriptor<AssetList> AGENT_INFORMATION = declare("AgentInfo", AssetList.class, AgentInfo.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAProducerInfo']"));

		// Named Insured
		public static final class NamedInsured extends MetaData {
			public static final AssetDescriptor<Button> BTN_ADD_INSURED = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHONamedInsured"));
			public static final AssetDescriptor<SingleSelectSearchDialog> CUSTOMER_SEARCH = declare("CustomerSearchDialog", SingleSelectSearchDialog.class, CustomerSearch.class, By
					.xpath(".//form[@id='customerSearchFrom']"));
			public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class);
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle name", TextBox.class);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
			public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class);
			public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital status", ComboBox.class);
			public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of birth", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER = declare("Social security number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> BASE_DATE = declare("Base date", TextBox.class);
			public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> AAA_EMPLOYEE = declare("AAA employee", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> TRUSTEE_LLC_OWNER = declare("Trustee/LLC Owner", RadioGroup.class);
			public static final AssetDescriptor<ComboBox> NJ_CHAMBER_OF_COMMERCE = declare("NJ Chamber of Commerce", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ENTERED_SSN_IS_NOT_VALID = declare("Entered SSN is not valid", RadioGroup.class);
			public static final AssetDescriptor<Button> BTN_REMOVE_INSURED = declare("Remove Insured", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAAHONamedInsured"));
		}

		public static final class CustomerSearch extends MetaData {
			public static final AssetDescriptor<Button> BTN_CUSTOMER_SEARCH = declare("Customer Search", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:dataGatherView_ListAAAHONamedInsured:0:dataGatherView_List_CustomerSearch"));
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of birth", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, false, By.id("customerSearchFrom:cancelSearch"));
		}

		public static final class AAAMembership extends MetaData {
			public static final AssetDescriptor<ComboBox> CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MEMBERSHIP_NUMBER = declare("Membership number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MEMBER_SINCE_DATE = declare("Member Since Date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> OVERRIDE_TYPE = declare("Override Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MEM_SINCE_DATE = declare("Member Since Date", TextBox.class, Waiters.AJAX);
		}

		// Dwelling Address
		public static final class DwellingAddress extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> RETIREMENT_COMMUNITY = declare("Retirement Community", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By
					.id("policyDataGatherForm:validateHODwellAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAHODwellAddressValidationComp_content']"));
		}

		public static final class MailingAddress extends MetaData {
			public static final AssetDescriptor<RadioGroup> IS_DIFFERENT_MAILING_ADDRESS = declare("Is the mailing address different from the dwelling address?", RadioGroup.class, Waiters.AJAX,
					false,
					By.xpath(".//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAHOMailingAddressComponent']"));
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COUTRY = declare("Country", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AssetDescriptor<Button> BTN_VALIDATE_ADDRESS = declare("Validate Address", Button.class, Waiters.AJAX, false, By
					.id("policyDataGatherForm:validateHOMailingAddressButtonUS"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationPopupAAAHOMailingAddressValidation_container"));
		}

		public static final class PreviousDwellingAddress extends MetaData {
			public static final AssetDescriptor<RadioGroup> HAS_PREVIOUS_DWELLING_ADDRESS = declare("Has there been another dwelling address within the last 3 consecutive years?", RadioGroup.class,
					Waiters.AJAX, false,
					By.xpath("//table[@id='policyDataGatherForm:sedit_AAAHOPreDwellingAddrExists_yesNoAnswer']"));
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AssetDescriptor<Button> BTN_VALIDATE_ADDRESS = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOPreAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationPopupAAAHOPrevAddressValidationComp_container"));
		}

		public static final class AgentInfo extends MetaData {
			public static final AssetDescriptor<ComboBox> CHANNEL_TYPE = declare("Channel Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENCY = declare("Agency", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class);
			public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENCY_LOCATION = declare("Agency Location", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENT = declare("Agent", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class);
			public static final AssetDescriptor<TextBox> AGENT_NUMBER = declare("Agent Number", TextBox.class);
			public static final AssetDescriptor<TextBox> TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class, Waiters.AJAX);
		}

		public static final class NamedInsuredInformation extends MetaData {
			public static final AssetDescriptor<TextBox> HOME_PHONE_NUMBER = declare("Home Phone Number", TextBox.class, Waiters.AJAX.then(Waiters.AJAX));
			public static final AssetDescriptor<TextBox> WORK_PHONE_NUMBER = declare("Work Phone Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MOBILE_PHONE_NUMBER = declare("Mobile Phone Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PREFFERRED_PHONE_NUMBER = declare("Preferred Phone #", ComboBox.class);
			public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
		}

		public static final class OtherActiveAAAPolicies extends MetaData {
			public static final AssetDescriptor<RadioGroup> OTHER_ACTIVE_AAA_POLICIES = declare("Other active AAA policies", RadioGroup.class, Waiters.AJAX, false, By
					.xpath("//table[@id='policyDataGatherForm:formGrid_AAAHOOtherPolicyViewOnly']"));
			public static final AssetDescriptor<Button> ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOOtherOrPriorPolicyComponent"));
			public static final AssetDescriptor<SingleSelectSearchDialog> ACTIVE_UNDERLYING_POLICIES_SEARCH = declare("ActiveUnderlyingPoliciesSearch", SingleSelectSearchDialog.class,
					OtherActiveAAAPoliciesSearch.class,
					By.xpath(".//form[@id='policySearchForm_AAAHOOtherOrPriorActivePolicySearch']"));
			public static final AssetDescriptor<AssetList> ACTIVE_UNDERLYING_POLICIES_MANUAL = declare("ActiveUnderlyingPoliciesManual", AssetList.class, OtherActiveAAAPoliciesManual.class);

			public static final class OtherActiveAAAPoliciesSearch extends MetaData {
				public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy number", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, false, By.id("policySearchForm_AAAHOOtherOrPriorActivePolicySearch:cancelSearch_AAAHOOtherOrPriorActivePolicySearch"));
			}

			public static final class OtherActiveAAAPoliciesManual extends MetaData {
				public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<RadioGroup> COMPANION_AUTO_PENDING_WITH_DISCOUNT = declare("Companion Auto Pending with Discount?", RadioGroup.class, Waiters.AJAX);
				public static final AssetDescriptor<RadioGroup> COMPANION_PUP_DP3_PENDING_WITH_DISCOUNT = declare("Companion PUP/DP3 Pending with Discount?", RadioGroup.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy number", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> POLICY_BASE_YEAR = declare("Policy base year", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> POLICY_TIER = declare("Policy tier", ComboBox.class, Waiters.AJAX,
						By.id("policyDataGatherForm:sedit_AAAHOOtherOrPriorPolicyComponent_autoPolicyTier"));
				public static final AssetDescriptor<ComboBox> AUTO_POLICY_BI_LIMIT = declare("Auto policy BI limit", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> AUTO_POLICY_STATE = declare("Auto policy state", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> AUTO_INSURANCE_PERSISTENCY = declare("Auto Insurance Persistency", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<RadioGroup> IS_THE_POLICY_PENDING = declare("Is the policy pending?", RadioGroup.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> COVERAGE_E = declare("Coverage E", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> DEDUCTIBLE = declare("Deductible", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> DWELLING_USAGE = declare("Dwelling usage", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> OCCUPANCY_TYPE = declare("Occupancy type", ComboBox.class, Waiters.AJAX);
			}
		}
	}

	public static final class ReportsTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> SALES_AGENT_AGREEMENT = declare("Sales Agent Agreement", RadioGroup.class, Waiters.AJAX, false, By
				.xpath("//table[@id='policyDataGatherForm:sedit_AAAHOCommonDisclosureMessageComponent_agentAgrees']"));

		public static final AssetDescriptor<FillableTable> AAA_MEMBERSHIP_REPORT = declare("AAAMembershipReport", FillableTable.class, AaaMembershipReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:membershipReports']"));
		public static final AssetDescriptor<FillableTable> INSURANCE_SCORE_REPORT = declare("InsuranceScoreReport", FillableTable.class, InsuranceScoreReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:creditReports']"));
		public static final AssetDescriptor<FillableTable> INSURANCE_SCORE_OVERRIDE = declare("InsuranceScoreOverride", FillableTable.class, InsuranceScoreOverrideRow.class, By
				.xpath("//table[@id='policyDataGatherForm:creditScoreOverride']"));
		public static final AssetDescriptor<FillableTable> FIRELINE_REPORT = declare("FirelineReport", FillableTable.class, FirelineReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:firelineReportTable']"));
		public static final AssetDescriptor<FillableTable> RISK_METER_REPORT = declare("RiskMeterReport", FillableTable.class, RiskMeterReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:riskMeterReportTable']"));
		public static final AssetDescriptor<FillableTable> PUBLIC_PROTECTION_CLASS = declare("PublicProtectionClass", FillableTable.class, PublicProtectionClassRow.class, By
				.xpath("//table[@id='policyDataGatherForm:ppcReportTable']"));
		public static final AssetDescriptor<FillableTable> CLUE_REPORT = declare("CLUEreport", FillableTable.class, CLUEreportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:orderClueReports']"));
		public static final AssetDescriptor<FillableTable> ORDER_INTERNAL_CLAIMS = declare("OrderInternalClaims", FillableTable.class, OrderInternalClaimsRow.class, By
				.xpath("//table[@id='policyDataGatherForm:orderInternalClaimsReports']"));
		public static final AssetDescriptor<FillableTable> ISO360_REPORT = declare("ISO360Report", FillableTable.class, ISO360ReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:iso360ReportTable']"));
		public static final AssetDescriptor<StaticElement> WARNING_MESSAGE_BOX = declare("Warning Message Box", StaticElement.class, By
				.xpath("//span[@id='policyDataGatherForm:componentContextHolder']/ul/li"));
		// public static final AssetDescriptor<StaticElement> ADVERSELY_IMPACTED_APPLIED_MESSAGE = declare("Adversely Impacted was applied to the policy effective", StaticElement.class,
		// By.xpath("//span[@id='policyDataGatherForm:warningMessage']"));

		public static final class RiskMeterReportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AssetDescriptor<StaticElement> DISTANCE_TO_COAST = declare("Distance To Coast", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ELEVATION = declare("Elevation", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
		}

		public static final class AaaMembershipReportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> MEMBERSHIP_NO = declare("Membership No.", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MEMBER_SINCE_DATE = declare("Member Since Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
			public static final AssetDescriptor<Link> ADD_MEMBER_SINCE = declare("Add Member since", Link.class);
			public static final AssetDescriptor<AssetList> ADD_MEMBER_SINCE_DIALOG = declare("AddMemberSinceDialog", AssetList.class, AddMemberSinceDialog.class);
		}

		public static final class AddMemberSinceDialog extends MetaData {
			public static final AssetDescriptor<TextBox> MEMBER_SINCE = declare("Member since date", TextBox.class, By.xpath("//input[@id='memberSinceDateFrom:popupMemberSinceDateInputDate']"));
			public static final AssetDescriptor<TextBox> MEMBERSHIP_EXPIRATION_DATE =
					declare("Membership expiration date", TextBox.class, By.xpath("//input[@id='memberSinceDateFrom:popupMemberExpirationDateInputDate']"));
			public static final AssetDescriptor<Button> BTN_OK = declare("OK", Button.class, By.xpath("//input[@id='memberSinceDateFrom:addMemberSinceDateButton']"));
			public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, By.xpath("//input[@id='memberSinceDateFrom:cancelMemberSinceDateButton']"));
		}

		public static final class InsuranceScoreReportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> NAMED_INSURED = declare("Named Insured", StaticElement.class);
			public static final AssetDescriptor<RadioGroup> ORDER_INSURANCE_SCORE = declare("Order Insurance Score", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> REORDER_AT_RENEWAL = declare("Reorder at renewal", RadioGroup.class);
			public static final AssetDescriptor<StaticElement> SSN_ENTERED = declare("SSN Entered", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<StaticElement> BAND_NUMBER = declare("Band Number", StaticElement.class);
			public static final AssetDescriptor<RadioGroup> CUSTOMER_AGREEMENT = declare("Customer Agreement", RadioGroup.class, Waiters.AJAX, false, By
					.xpath("//table[@id='policyDataGatherForm:customerRadio']"));
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
		}

		public static final class InsuranceScoreOverrideRow extends MetaData {
			public static final AssetDescriptor<StaticElement> NAMED_INSURED = declare("Named Insured", StaticElement.class);
			public static final AssetDescriptor<StaticElement> INSURANCE_SCORE = declare("Insurance Score", StaticElement.class);
			public static final AssetDescriptor<StaticElement> OVERRIDE_DATE = declare("Override Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> REASON_FOR_OVERRIDE = declare("Reason for Override", StaticElement.class);
			public static final AssetDescriptor<StaticElement> OVERRIDDEN_BY = declare("Overridden by", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION = declare("Action", Link.class);
			public static final AssetDescriptor<AssetList> EDIT_INSURANCE_SCORE = declare("EditInsuranceScoreDialog", AssetList.class, EditInsuranceScoreDialog.class);
		}

		public static final class FirelineReportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AssetDescriptor<StaticElement> WILDFIRE_SCORE = declare("Wildfire Score", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
		}

		public static final class PublicProtectionClassRow extends MetaData {
			public static final AssetDescriptor<StaticElement> NAME = declare("Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AssetDescriptor<StaticElement> PPC_VALUE = declare("PPC Value", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
			public static final AssetDescriptor<DialogAssetList> PPC_REPORT_DIALOG = declare("PPCReportDialog", DialogAssetList.class, PPCReportDialog.class, false, By.xpath("//div[@id='ppcPercentagePopup_container']"));
		}

		public static final class PPCReportDialog extends MetaData {
			public static final AssetDescriptor<RadioGroup> SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION = declare("Subscription to fire department/station", RadioGroup.class, Waiters.AJAX,By.id("ppcPercentageForm:subsriptionIndicator"));
			public static final AssetDescriptor<Button> BTN_OK = declare("OK", Button.class, By.id("ppcPercentageForm:cmdBtnPublicProtectionClass"));
		}

		public static final class CLUEreportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AssetDescriptor<StaticElement> NO_OF_CLAIMS = declare("No. of Claims", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
		}

		public static final class OrderInternalClaimsRow extends MetaData {
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION = declare("Action", Link.class);
		}

		public static final class ISO360ReportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AssetDescriptor<StaticElement> REPLACEMENT_COST = declare("Replacement Cost", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
		}

		public static final class EditInsuranceScoreDialog extends MetaData {
			public static final AssetDescriptor<TextBox> SCORE_AFTER_OVERRIDE = declare("Score after override", TextBox.class, Waiters.NONE, By.xpath("//input[@id='editInsuranceScoreFrom:newScore']"));
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class, Waiters.NONE, By.xpath("//select[@id='editInsuranceScoreFrom:billingType_billing']"));
			public static final AssetDescriptor<Button> BTN_SAVE = declare("Save", Button.class, By.xpath("//input[@id='editInsuranceScoreFrom:saveButton']"));
		}

		public static final class HssPPCDialog extends MetaData {
			public static final AssetDescriptor<StaticElement> FIRE_DEPARTMENT_TYPE = declare("Fire department type", StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> PUBLIC_PROTECTION_CLASS = declare("Public protection class", StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DISTANCE_TO_FIRE_HYDRANT = declare("Distance to fire hydrant", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> FIRE_PROTECTION_AREA = declare("Fire protection area", StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION = declare("Subscription to fire department/station", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> SYSTEM_PUBLIC_PROTECTION_CLASS = declare("System public protection class", StaticElement.class, Waiters.AJAX);
		}
	}

	public static final class PropertyInfoTab extends MetaData {

		public static final AssetDescriptor<AssetList> DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class, By
				.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOAddiDwellAddrMVO']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_ADDRESS = declare("AdditionalAddress", MultiInstanceAfterAssetList.class, AdditionalAddress.class, By
				.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOAdditionalAddress']"));
		public static final AssetDescriptor<AssetList> PUBLIC_PROTECTION_CLASS = declare("PublicProtectionClass", AssetList.class, PublicProtectionClass.class);
		// ,By.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAPpcDetailsMVO']"));
		public static final AssetDescriptor<AssetList> FIRE_REPORT = declare("FireReport", AssetList.class, FireReport.class);
		// ,By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAFirelineDetailsMVO']"));
		public static final AssetDescriptor<AssetList> RISKMETER = declare("Riskmeter", AssetList.class, Riskmeter.class);
		public static final AssetDescriptor<AssetList> PROPERTY_VALUE = declare("PropertyValue", AssetList.class, PropertyValue.class);
		public static final AssetDescriptor<AssetList> CONSTRUCTION = declare("Construction", AssetList.class, Construction.class);
		public static final AssetDescriptor<AssetList> ADDITIONAL_QUESTIONS = declare("AdditionalQuestions", AssetList.class, AdditionalQuestions.class);
		public static final AssetDescriptor<AssetList> INTERIOR = declare("Interior", AssetList.class, Interior.class);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> DETACHED_STRUCTURES = declare("DetachedStructures", MultiInstanceAfterAssetList.class, DetachedStructures.class);
		public static final AssetDescriptor<AssetList> FIRE_PROTECTIVE_DD = declare("FireProtectiveDD", AssetList.class, FireProtectiveDD.class);
		public static final AssetDescriptor<AssetList> THEFT_PROTECTIVE_DD = declare("TheftProtectiveTPDD", AssetList.class, TheftProtectiveTPDD.class);
		public static final AssetDescriptor<AssetList> HOME_RENOVATION = declare("HomeRenovation", AssetList.class, HomeRenovation.class);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> PETS_OR_ANIMALS = declare("PetsOrAnimals", MultiInstanceAfterAssetList.class, PetsOrAnimals.class);
		public static final AssetDescriptor<AssetList> STOVES = declare("Stoves", AssetList.class, Stoves.class);
		public static final AssetDescriptor<AssetList> RECREATIONAL_EQUIPMENT = declare("RecreationalEquipment", AssetList.class, RecreationalEquipment.class);
		public static final AssetDescriptor<AssetList> OIL_FUEL_OR_PROPANE_STORAGE_TANK = declare("OilFuelOrPropaneStorageTank", AssetList.class, OilPropaneStorageTank.class);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> CLAIM_HISTORY = declare("ClaimHistory", MultiInstanceAfterAssetList.class, ClaimHistory.class);
		public static final AssetDescriptor<AssetList> RENTAL_INFORMATION = declare("RentalInformation", AssetList.class, RentalInformation.class);
		public static final AssetDescriptor<TextBox> COVERAGE_A_DWELLING_LIMIT = declare("Coverage A - Dwelling limit", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> PLUMBING_RENOVATION = declare("Plumbing renovation", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> ELECTRICAL_RENOVATION = declare("Electrical renovation", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> ROOF_RENOVATION = declare("Roof renovation", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> HEATING_COOLING_RENOVATION = declare("Heating/cooling renovation", ComboBox.class, Waiters.AJAX);

		public static final class DwellingAddress extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State / Province", ComboBox.class);
			public static final AssetDescriptor<ComboBox> NUMBER_OF_FAMILY_UNITS = declare("Number of family units", ComboBox.class, Waiters.AJAX);
		}

		public static final class AdditionalAddress extends MetaData {
			public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_ADDITIONAL_ADDRESSES = declare("Are there any additional address", RadioGroup.class, Waiters.AJAX, false, By
					.xpath("//table[@id='policyDataGatherForm:sedit_AAAHOAddiDwellAddrMVO_proxiedComponents_PreconfigDwell_addAddressInd']"));
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AssetDescriptor<Button> BTN_VALIDATE_ADDRESS = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOAddAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationFormAAAHOAdditionalDwelAddressValidation"));
			public static final AssetDescriptor<Button> BTN_ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOAdditionalAddress"));
		}

		public static final class PublicProtectionClass extends MetaData {
			public static final AssetDescriptor<ComboBox> FIRE_DEPARTMENT_TYPE = declare("Fire department type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION = declare("Subscription to fire department/station", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PUBLIC_PROTECTION_CLASS = declare("Public protection class", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DISTANCE_TO_FIRE_HYDRANT = declare("Distance to fire hydrant", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SYSTEM_PUBLIC_PROTECTION_CLASS = declare("System public protection class", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FIRE_PROTECTION_AREA = declare("Fire protection area", TextBox.class, Waiters.AJAX);
		}

		public static final class FireReport extends MetaData {
			public static final AssetDescriptor<TextBox> PLACEMENT_OR_MATCH_TYPE = declare("Placement or match type", TextBox.class);
			public static final AssetDescriptor<TextBox> WILDFIRE_SCORE = declare("Wildfire score", TextBox.class);
		}

		public static final class PropertyValue extends MetaData {
			public static final AssetDescriptor<TextBox> COVERAGE_A_DWELLING_LIMIT = declare("Coverage A - Dwelling limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ISO_REPLACEMENT_COST = declare("ISO replacement cost", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE = declare("Reason replacement cost differs from the tool value", ComboBox.class,
					Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PERSONAL_PROPERTY_VALUE = declare("Personal property value", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PURCHASE_DATE_OF_HOME = declare("Purchase date of home", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<CheckBox> NEW_LOAN = declare("New loan", CheckBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OTHER_SPECIFY = declare("Other - specify", TextBox.class, Waiters.AJAX);
		}

		public static final class Construction extends MetaData {
			public static final AssetDescriptor<TextBox> YEAR_BUILT = declare("Year built", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SQUARE_FOOTAGE = declare("Square footage", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> ROOF_TYPE = declare("Roof type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> ROOF_SHAPE = declare("Roof shape", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> T_LOCK_SHINGLES = declare("T-Lock shingles", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CONSTRUCTION_TYPE = declare("Construction type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR = declare("Is this a log home assembled by a licensed building contractor?",
					RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> MASONRY_VENEER = declare("Masonry Veneer", RadioGroup.class);
			public static final AssetDescriptor<AdvancedComboBox> HAIL_RESISTANCE_RATING = declare("Hail-resistance rating", AdvancedComboBox.class, Waiters.AJAX);
		}

		public static final class AdditionalQuestions extends MetaData {
			public static final AssetDescriptor<RadioGroup> IS_THE_LENGTH_OF_DRIVEWAY_LESS_THAN_500_FEET = declare("Is the length of driveway less than 500 feet?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_ROAD_TO_THE_HOME_AND_DRIVEWAY_PAVED = declare("Is the road to the home and driveway paved?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THERE_A_CREDITABLE_ALTERNATIVE_WATER_SOURCE_WITHIN_1_000_FEET_OF_THE_PROPERTY = declare(
					"Is there a creditable alternative water source within 1,000 feet of the property?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class Interior extends MetaData {
			public static final AssetDescriptor<ComboBox> DWELLING_USAGE = declare("Dwelling usage", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> OCCUPANCY_TYPE = declare("Occupancy type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DWELLING_PERCENTAGE_USAGE = declare("Dwelling Percentage Usage", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_RESIDENTS = declare("Number of residents", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> NUMBER_OF_STORIES = declare("Number of stories", ComboBox.class, Waiters.AJAX);
		}

		public static final class DetachedStructures extends MetaData {
			public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY = declare("Are there any detached structures on the property?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.id("confirmOptionalNoSelected_AAAHODetachedStructuresInfoComponent_Dialog_container"));
			public static final AssetDescriptor<Button> BTN_ADD_STRUCTURE = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHODetachedStructuresInfoComponent"));
			public static final AssetDescriptor<RadioGroup> RENTED_TO_OTHERS = declare("Rented to others", RadioGroup.class, Waiters.AJAX, false, By
					.xpath("//table[@id='policyDataGatherForm:sedit_AAAHODetachedStructuresInfoComponent_aaaRentedToOther']"));
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> NUMBER_OF_FAMILY_UNITS = declare("Number of family units", ComboBox.class, Waiters.AJAX, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHODetachedStructuresInfoComponent_aaaNumberOfFamilyunits']"));
			public static final AssetDescriptor<ComboBox> NUMBER_OF_OCCUPANTS = declare("Number of occupants", ComboBox.class, Waiters.AJAX, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHODetachedStructuresInfoComponent_aaaNumberOfOccupants']"));
		}

		public static final class FireProtectiveDD extends MetaData {
			public static final AssetDescriptor<CheckBox> LOCAL_FIRE_ALARM = declare("Local fire alarm", CheckBox.class);
			public static final AssetDescriptor<CheckBox> CENTRAL_FIRE_ALARM = declare("Central fire alarm", CheckBox.class, Waiters.AJAX);
			public static final AssetDescriptor<CheckBox> FULL_RESIDENTIAL_SPRINKLERS = declare("Full residential sprinklers", CheckBox.class, Waiters.AJAX);
			public static final AssetDescriptor<CheckBox> PARTIAL_RESIDENTIAL_SPRINKLERS = declare("Partial residential sprinklers", CheckBox.class, Waiters.AJAX);
		}

		public static final class TheftProtectiveTPDD extends MetaData {
			public static final AssetDescriptor<CheckBox> LOCAL_THEFT_ALARM = declare("Local theft alarm", CheckBox.class);
			public static final AssetDescriptor<CheckBox> CENTRAL_THEFT_ALARM = declare("Central theft alarm", CheckBox.class);
			public static final AssetDescriptor<CheckBox> GATED_COMMUNITY = declare("Gated community", CheckBox.class, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOAntiTheftDevicesInfoProxy_gatedCommInd']"));
		}

		public static final class HomeRenovation extends MetaData {
			public static final AssetDescriptor<ComboBox> PLUMBING_RENOVATION = declare("Plumbing renovation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PLUMBING_PERCENT_COMPLETE = declare("Plumbing % complete", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingPercentComplete']"));
			public static final AssetDescriptor<ComboBox> PLUMBING_MONTH_OF_COMPLECTION = declare("Plumbing Month of completion", ComboBox.class, Waiters.AJAX, false,
					By.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingRenovationMonth']"));
			public static final AssetDescriptor<TextBox> PLUMBING_YEAR_OF_COMPLECTION = declare("Plumbing Year of completion", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingRenovationYear']"));

			public static final AssetDescriptor<ComboBox> ELECTRICAL_RENOVATION = declare("Electrical renovation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ELECTRICAL_PERCENT_COMPLETE = declare("Electrical % complete", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalPercentComplete']"));
			public static final AssetDescriptor<ComboBox> ELECTRICAL_MONTH_OF_COMPLECTION = declare("Electrical Month of completion", ComboBox.class, Waiters.AJAX, false,
					By.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalRenovationMonth']"));
			public static final AssetDescriptor<TextBox> ELECTRICAL_YEAR_OF_COMPLECTION = declare("Electrical Year of completion", TextBox.class, Waiters.AJAX, false,
					By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalRenovationYear']"));

			public static final AssetDescriptor<ComboBox> ROOF_RENOVATION = declare("Roof renovation", ComboBox.class, Waiters.AJAX, false, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofRenovationType']"));
			public static final AssetDescriptor<TextBox> ROOF_PERCENT_COMPLETE = declare("Roof % complete", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofPercentComplete']"));
			public static final AssetDescriptor<ComboBox> ROOFG_MONTH_OF_COMPLECTION = declare("Roof Month of completion", ComboBox.class, Waiters.AJAX, false, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofRenovationMonth']"));
			public static final AssetDescriptor<TextBox> ROOF_YEAR_OF_COMPLECTION = declare("Roof Year of completion", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofRenovationYear']"));

			public static final AssetDescriptor<ComboBox> HEATING_COOLING_RENOVATION = declare("Heating/cooling renovation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HEATING_COOLING_PERCENT_COMPLETE = declare("Heating/cooling % complete", TextBox.class, Waiters.AJAX, false,
					By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingPercentComplete']"));
			public static final AssetDescriptor<ComboBox> HEATING_COOLING_MONTH_OF_COMPLECTION = declare("Heating/cooling Month of completion", ComboBox.class, Waiters.AJAX, false,
					By.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingRenovationMonth']"));
			public static final AssetDescriptor<TextBox> HEATING_COOLING_YEAR_OF_COMPLECTION = declare("Heating/cooling Year of completion", TextBox.class, Waiters.AJAX, false,
					By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingRenovationYear']"));

			public static final AssetDescriptor<RadioGroup> GREEN_HOME_DISCOUNT = declare("Green Home discount", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> STORM_SHUTTER_DISCOUNT = declare("Storm Shutter discount", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WINDSTORM_MITIGATION_DISCOUNT = declare("Windstorm Mitigation discount", RadioGroup.class, Waiters.AJAX);
		}

		public static final class PetsOrAnimals extends MetaData {
			public static final AssetDescriptor<RadioGroup> ARE_ANY_PETS_OR_ANIMALS_KEPT_ON_THE_PROPERTY = declare("Are any insured-owned pets or animals kept on the property?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.id("confirmOptionalNoSelected_AAADwellAnimalInfoComponent_Dialog_container"));
			public static final AssetDescriptor<ComboBox> ANIMAL_TYPE = declare("Animal type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OTHER_SPECIFY = declare("Other - specify", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ANIMAL_COUNT = declare("Animal count", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> BTN_ADD = declare("Add", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:addAAADwellAnimalInfoComponent"));
		}

		public static final class Stoves extends MetaData {
			public static final AssetDescriptor<RadioGroup> DOES_THE_PROPERTY_HAVE_A_WOOD_BURNING_STOVE = declare("Does the property have a wood-burning stove?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT = declare("Is the stove the sole source of heat?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR = declare("Was the stove installed by a licensed contractor?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY = declare("Does the dwelling have at least one smoke detector per story?",
					RadioGroup.class, Waiters.AJAX);
		}

		public static final class RecreationalEquipment extends MetaData {
			public static final AssetDescriptor<ComboBox> SWIMMING_POOL = declare("Swimming pool", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SPA_HOT_TUB = declare("Spa/hot tub", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> TRAMPOLINE = declare("Trampoline", ComboBox.class, Waiters.AJAX);
		}

		public static final class ClaimHistory extends MetaData {
			//public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
			//		.id("confirmOptionalNoSelected_AAAHOLossInfo_Dialog_container"));
			public static final AssetDescriptor<StaticElement> LABEL_CLAIM_HISTORY = declare("Claim History", StaticElement.class, By.id("policyDataGatherForm:componentViewPanelHeaderLabel_AAAHOLossInfo"));
			public static final AssetDescriptor<Button> BTN_ADD = declare("Add", Button.class, Waiters.AJAX, By.id("policyDataGatherForm:addAAAHOLossInfo"));
			public static final AssetDescriptor<Button> BTN_REMOVE = declare("Remove", Button.class, Waiters.NONE, By.id("policyDataGatherForm:eliminate"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.id("confirmEliminateInstance_Dialog_container"));
			public static final AssetDescriptor<ComboBox> SOURCE = declare("Source", ComboBox.class);
			public static final AssetDescriptor<TextBox> DATE_OF_LOSS = declare("Date of loss", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<AdvancedComboBox> CAUSE_OF_LOSS = declare("Cause of loss", AdvancedComboBox.class);
			public static final AssetDescriptor<TextBox> AMOUNT_OF_LOSS = declare("Amount of loss", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CLAIM_STATUS = declare("Claim status", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> AAA_CLAIM = declare("AAA Claim", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> CATASTROPHE_LOSS = declare("Catastrophe loss", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CATASTROPHE_LOSS_CODE_REMARKS = declare("Catastrophe loss code/remarks", TextBox.class);
			public static final AssetDescriptor<ComboBox> LOSS_FOR = declare("Loss for", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> INCLUDED_IN_RATING_AND_ELIGIBILITY = declare("Include in Rating and Eligibility", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_CLAIM_IS_NOT_CHARGEABLE = declare("Reason claim is not chargeable", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<AssetListConfirmationDialog> ACTIVITY_REMOVE_CONFIRMATION =
					declare("Activity remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_container"));
		}

		public static final class RentalInformation extends MetaData {
			public static final AssetDescriptor<TextBox> NUMBER_OF_CONSECUTIVE_YEARS_INSURED_HAS_OWNED_ANY_RENTAL_PROPERTIES = declare(
					"Number of consecutive years insured has owned any rental properties", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PROPERTY_MANAGER = declare("Property manager", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DOES_THE_TENANT_HAVE_AN_UNDERLYING_HO4_POLICY = declare("Does the tenant have an underlying HO4 policy?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class Riskmeter extends MetaData {
			public static final AssetDescriptor<TextBox> DISTANCE_TO_COAST_MILES = declare("Distance to Coast(miles)", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ELEVATION_FEET = declare("Elevation(feet)", TextBox.class, Waiters.AJAX);
		}

		public static final class OilPropaneStorageTank extends MetaData {
			public static final AssetDescriptor<ComboBox> OIL_FUEL_OR_PROPANE_STORAGE_TANK = declare("Oil Fuel or Propane Storage Tank", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ADD_FUEL_SYSTEM_STORAGE_TANK_COVERAGE = declare("Add fuel system storage tank coverage?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> AGE_OF_OIL_OR_PROPANE_FUEL_STORAGE_TANK = declare("Age of oil or propane fuel storage tank", TextBox.class, Waiters.AJAX);
		}
	}

	public static final class ProductOfferingTab extends MetaData {
		// TODO-dchubkov: didn't find such controls, should we remove them?
		/*
		 * public static final AssetDescriptor<TextBox> SEQUENCE = declare("Sequence #", TextBox.class); public static final AssetDescriptor<ComboBox> INCIDENT_SOURCE = declare("Incident Source",
		 * ComboBox.class); public static final AssetDescriptor<RadioGroup> INCLUDE_IN_RATING = declare("Include in Rating", RadioGroup.class); public static final AssetDescriptor<ComboBox> REASON =
		 * declare("Reason", ComboBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class); public static final AssetDescriptor<TextBox> CLAIM =
		 * declare("Claim #", TextBox.class); public static final AssetDescriptor<ComboBox> CLAIM_TYPE = declare("Claim Type", ComboBox.class); public static final AssetDescriptor<TextBox>
		 * DATE_OF_LOSS = declare("Date of Loss", TextBox.class); public static final AssetDescriptor<TextBox> CLAIM_AMOUNT = declare("Claim Amount", TextBox.class); public static final
		 * AssetDescriptor<TextBox> DESCRIPTION_OF_LOSS = declare("Description of Loss", TextBox.class);
		 */

		public static final AssetDescriptor<ProductOfferingVariationControl> HERITAGE = declare("Heritage", ProductOfferingVariationControl.class, VariationControls.class, By
				.xpath("//span[text()='Heritage']//ancestor::div[contains(@id, 'policyDataGatherForm:QuoteVariation')]"));
		public static final AssetDescriptor<ProductOfferingVariationControl> LEGACY = declare("Legacy", ProductOfferingVariationControl.class, VariationControls.class, By
				.xpath("//span[text()='Legacy']//ancestor::div[contains(@id, 'policyDataGatherForm:QuoteVariation')]"));
		public static final AssetDescriptor<ProductOfferingVariationControl> PRESTIGE = declare("Prestige", ProductOfferingVariationControl.class, VariationControls.class, By
				.xpath("//span[text()='Prestige']//ancestor::div[contains(@id, 'policyDataGatherForm:QuoteVariation')]"));
		public static final AssetDescriptor<CheckBox> CAPPING_LOCK = declare("Capping Lock", CheckBox.class, Waiters.AJAX, By.id("policyDataGatherForm:cappingIndicatorLocked"));

		public static final class VariationControls extends MetaData {
			public static final AssetDescriptor<TextBox> COVERAGE_A = declare("Coverage A", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, 'AAACoverageA_limitAmount_limitAmount')]"));
			public static final AssetDescriptor<ComboBox> COVERAGE_B_PERCENT = declare("Coverage B Percent", ComboBox.class, Waiters.AJAX, By
					.xpath(".//select[contains(@id, 'AAACoverageB_additionalLimitAmount_additionalLimitAmount')]"));
			public static final AssetDescriptor<TextBox> COVERAGE_B_LIMIT = declare("Coverage B Limit", TextBox.class, Waiters.AJAX, By
					.xpath(".//input[contains(@id, 'AAACoverageB_limitAmount_limitAmount')]"));
			public static final AssetDescriptor<TextBox> COVERAGE_C = declare("Coverage C", TextBox.class, Waiters.AJAX, By.xpath(".//input[contains(@id, 'AAACoverageC_limitAmount_limitAmount')]"));
			public static final AssetDescriptor<ComboBox> COVERAGE_D_PERCENT = declare("Coverage D Percent", ComboBox.class, Waiters.AJAX, By
					.xpath(".//select[contains(@id, 'AAACoverageD_additionalLimitAmount_additionalLimitAmount')]"));
			public static final AssetDescriptor<TextBox> COVERAGE_D_LIMIT = declare("Coverage D Limit", TextBox.class, Waiters.AJAX, By
					.xpath(".//input[contains(@id, 'AAACoverageD_limitAmount_limitAmount')]"));
			public static final AssetDescriptor<ComboBox> COVERAGE_E =
					declare("Coverage E", ComboBox.class, Waiters.AJAX, By.xpath(".//select[contains(@id, 'AAACoverageE_limitAmount_limitAmount')]"));
			public static final AssetDescriptor<ComboBox> COVERAGE_F =
					declare("Coverage F", ComboBox.class, Waiters.AJAX, By.xpath(".//select[contains(@id, 'AAACoverageF_limitAmount_limitAmount')]"));
			public static final AssetDescriptor<ComboBox> DEDUCTIBLE = declare("Deductible", ComboBox.class, Waiters.AJAX, By
					.xpath(".//select[contains(@id, 'AAAPropertyDeductible_limitAmount_limitAmount')]"));
			public static final AssetDescriptor<ComboBox> HURRICANE_DEDUCTIBLE = declare("Hurricane Deductible", ComboBox.class, Waiters.AJAX, By
					.xpath(".//select[contains(@id, 'AAAPropertHurricaneNew_additionalLimitAmount_deductibleAmount')]"));
			public static final AssetDescriptor<Button> SELECT_VARIATION = declare("Select variation", Button.class, Waiters.AJAX, By.xpath(".//input[@value='Select variation']"));
			public static final AssetDescriptor<Button> REMOVE_VARIATION = declare("Remove variation", Button.class, Waiters.AJAX, By.xpath(".//input[@value='Remove variation']"));
			public static final AssetDescriptor<Button> RESTORE_DEFAULTS = declare("Restore defaults", Button.class, Waiters.AJAX, By.xpath(".//input[@value='Restore defaults']"));
		}
	}

	public static final class EndorsementTab extends MetaData {

		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_03_12 = declare("DS 03 12", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0312.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_03_30 = declare("DS 03 30", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0330.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_03_30_08 = declare("DS 03 30 08", HomeSSEndorsementsMultiAssetList.class, EndorsementDS033008.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_03_30_08 = declare("DS 03 30 08", HomeSSEndorsementsMultiAssetList.class, EndorsementDS033008.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_03_30_08_12_VA = declare("DS 03 30 08 12 VA", HomeSSEndorsementsMultiAssetList.class, EndorsementDS03300812VA.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_10 = declare("DS 04 10", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0410.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_20 = declare("DS 04 20", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0420.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_41 = declare("DS 04 41", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0441.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_63 = declare("DS 04 63", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0463.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_69 = declare("DS 04 69", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0469.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_68 = declare("DS 04 68", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0468.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_71 = declare("DS 04 71", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0471.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_73 = declare("DS 04 73", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0473.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_75 = declare("DS 04 75", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0475.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_77 = declare("DS 04 77", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0477.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_80 = declare("DS 04 80", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0480.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_95 = declare("DS 04 95", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0495.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_99 = declare("DS 04 99", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0499.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_04_99_08_12 = declare("DS 04 99 08 12", HomeSSEndorsementsMultiAssetList.class, EndorsementDS04990812.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_05_78 = declare("DS 05 78", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0578.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_09_26 = declare("DS 09 26", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0926.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_09_29 = declare("DS 09 29", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0929.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_09_34 = declare("DS 09 34", HomeSSEndorsementsMultiAssetList.class, EndorsementDS0934.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_24_82 = declare("DS 24 82", HomeSSEndorsementsMultiAssetList.class, EndorsementDS2482.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_24_94 = declare("DS 24 94", HomeSSEndorsementsMultiAssetList.class, EndorsementDS2494.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> DS_MS_I2 = declare("DS MS I2", HomeSSEndorsementsMultiAssetList.class, EndorsementDSMSI2.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_03_12 = declare("HS 03 12", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0312.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_03_30 = declare("HS 03 30", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0330.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_03_30_08_12_VA = declare("HS 03 30 08 12 VA", HomeSSEndorsementsMultiAssetList.class, EndorsementHS03300812VA.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_10 = declare("HS 04 10", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0410.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_12 = declare("HS 04 12", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0412.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_20 = declare("HS 04 20", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0420.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_20_CO = declare("HS 04 20", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0420CO.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_20_WV = declare("HS 04 20", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0420WV.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_35 = declare("HS 04 35", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0435.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_40 = declare("HS 04 40", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0440.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_41 = declare("HS 04 41", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0441.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_42 = declare("HS 04 42", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0442.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_43 = declare("HS 04 43", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0443.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_50 = declare("HS 04 50", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0450.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_53 = declare("HS 04 53", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0453.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_54 = declare("HS 04 54", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0454.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_36 = declare("HS 04 36", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0436.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_55 = declare("HS 04 55", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0455.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_59 = declare("HS 04 59", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0459.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_61 = declare("HS 04 61", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0461.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_65 = declare("HS 04 65", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0465.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_77 = declare("HS 04 77", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0477.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_90 = declare("HS 04 90", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0490.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_92 = declare("HS 04 92", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0492.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_93 = declare("HS 04 93", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0493.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_95 = declare("HS 04 95", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0495.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_99 = declare("HS 04 99", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0499.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_99_08_12 = declare("HS 04 99 08 12", HomeSSEndorsementsMultiAssetList.class, EndorsementHS04990812.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_05_24 = declare("HS 05 24", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0524.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_05_46 = declare("HS 05 46", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0546.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_05_78 = declare("HS 05 78", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0578.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_06_14 = declare("HS 06 14", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0614.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_04 = declare("HS 09 04", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0904.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_06 = declare("HS 09 06", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0906.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_26 = declare("HS 09 26", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0926.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_27 = declare("HS 09 27", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0927.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_29 = declare("HS 09 29", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0929.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_30 = declare("HS 09 30", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0930.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_31 = declare("HS 09 31", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0931.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_32 = declare("HS 09 32", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0932.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_34 = declare("HS 09 34", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0934.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_37 = declare("HS 09 37", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0937.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_65 = declare("HS 09 65", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0965.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_09_88 = declare("HS 09 88", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0988.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_17_31 = declare("HS 17 31", HomeSSEndorsementsMultiAssetList.class, EndorsementHS1731.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_17_33 = declare("HS 17 33", HomeSSEndorsementsMultiAssetList.class, EndorsementHS1733.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_23_38 = declare("HS 23 38", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2338.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_23_83 = declare("HS 23 83", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2383.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_23_84 = declare("HS 23 84", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2384.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_24_43 = declare("HS 24 43", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2443.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_24_52 = declare("HS 24 52", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2452.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_24_64 = declare("HS 24 64", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2464.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_24_71 = declare("HS 24 71", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2471.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_24_72 = declare("HS 24 72", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2472.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_24_73 = declare("HS 24 73", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2473.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_04_52 = declare("HS 04 52", HomeSSEndorsementsMultiAssetList.class, EndorsementHS0452.class,
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_24_94 = declare("HS 24 94", HomeSSEndorsementsMultiAssetList.class, EndorsementHS2494.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeSSEndorsementsMultiAssetList> HS_MS_I2 = declare("HS MS I2", HomeSSEndorsementsMultiAssetList.class, EndorsementHSMSI2.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

		public static final class EndorsementDS0312 extends MetaData {
			public static final AssetDescriptor<ComboBox> WINDSTORM_OR_HAIL_DEDUCTIBLE = declare("Windstorm or Hail Deductible", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0420 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AMOUNT_OF_INSURANCE = declare("Amount of insurance", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0463 extends MetaData {
			public static final AssetDescriptor<ComboBox> LOCATION_TYPE = declare("Location type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DESCRIPTION_OF_STRUCTURE = declare("Description of structure", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0468 extends MetaData {
			public static final AssetDescriptor<ComboBox> LOCATION_TYPE = declare("Location type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DESCRIPTION_OF_STRUCTURE = declare("Description of structure", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CONSTRUCTION_TYPE = declare("Construction type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class);
		}

		public static final class EndorsementDS0469 extends MetaData {
			public static final AssetDescriptor<ComboBox> DEDUCTIBLE = declare("Deductible", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> INCLUDE_COVERAGE_FOR_EARTHQUAKE_LOSS_TO_EXTERIOR_MASONRY_VENEER = declare(
					"Include coverage for earthquake loss to exterior masonry veneer?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0471 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0473 extends MetaData {
		}

		public static final class EndorsementDS0475 extends MetaData {
		}

		public static final class EndorsementDS0480 extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY_OTHER_STRUCTURES = declare("Limit of liability - Other Structures", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0495 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0499 extends MetaData {}

		public static final class EndorsementDS0926 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0312 extends MetaData {
			public static final AssetDescriptor<ComboBox> WINDSTORM_OR_HAIL_DEDUCTIBLE = declare("Windstorm or Hail Deductible", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0412 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_BUSINESS_CONDUCTED_ON_THE_RESIDENCE_PREMISES = declare("Is the business conducted on the residence premises?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_BUSINESS_PROPERTY_FOR_SAMPLE_SALE_OR_DELIVERY_AFTER_SALE = declare(
					"Is the business property for sample, sale or delivery after sale?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0420 extends MetaData {
			public static final AssetDescriptor<ComboBox> AMOUNT_OF_INSURANCE = declare("Amount of insurance", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0435 extends MetaData {
			public static final AssetDescriptor<ComboBox> LOCATION_TYPE = declare("Location type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DESCRIPTION_OF_STRUCTURE = declare("Description of structure", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class, Waiters.AJAX);
		}

		// Don't move, should be before HS0436
		public static final class EndorsementHS0454 extends MetaData {
			public static final AssetDescriptor<ComboBox> DEDUCTIBLE = declare("Deductible", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> INCLUDE_COVERAGE_FOR_EARTHQUAKE_LOSS_TO_EXTERIOR_MASONRY_VENEER = declare(
					"Include coverage for earthquake loss to exterior masonry veneer?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REGION = declare("Region", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0436 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class);
			public static final AssetDescriptor<ComboBox> LOCATION_TYPE = declare("Location type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DESCRIPTION_OF_STRUCTURE = declare("Description of structure", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CONSTRUCTION_TYPE = declare("Construction type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0442 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_BUSINESS = declare("Description of business", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LOCATION_OF_BUSINESS = declare("Location of business", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_OTHER_STRUCTURE = declare("Description of other structure", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SECTION_I_COVERAGE = declare("Section I coverage", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FOOT_TRAFFIC_EXCEEDING_2_CUSTOMERS_PER_WEEK = declare("Foot traffic exceeding 2 customers per week", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_EQUIPMENT_OVER_50_000 = declare("Business equipment over $50,000", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EMPLOYEES_WORKING_ON_THE_PREMISES = declare("Employees working on the premises", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_INVOLVING_HAZARDOUS_SITUATIONS_OR_MATERIALS = declare("Business involving hazardous situations or materials", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_INVOLVING_THE_MANUFACTURING_OR_REPAIRING_OF_GOODS_OR_PRODUCTS = declare(
					"Business involving the manufacturing or repairing of goods or products", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROFESSIONAL_LIABILITY_EXPOSURE_THAT_IS_UNINSURED = declare("Professional liability exposure that is uninsured", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SUNTANNING_OR_PROFESSIONAL_EXERCISE_EQUIPMENT = declare("Suntanning or professional exercise equipment", RadioGroup.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0443 extends MetaData {}

		public static final class EndorsementHS0450 extends MetaData {
			public static final AssetDescriptor<TextBox> COVERAGE_LIMIT = declare("Coverage limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_RESIDENCE = declare("Description of residence", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0453 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class);
		}

		public static final class EndorsementHS0455 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class);
		}

		public static final class EndorsementHS0459 extends MetaData {
			public static final AssetDescriptor<TextBox> NAME_OF_RELATIVE = declare("Name of relative", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DOES_THE_FACILITY_PROVIDE_LIVING_SERVICES_SUCH_AS_DINING_THERAPY_MEDICAL_SUPERVISION_HOUSEKEEPING_AND_SOCIAL_ACTIVITIES =
					declare("Does the facility provide living services such as dining, therapy, medical supervision, housekeeping and social activities?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NAME_OF_FACILITY = declare("Name of facility", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COVERAGE_C_LIMIT = declare("Coverage C limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_E_LIMIT = declare("Coverage E limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0461 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class);
		}

		public static final class EndorsementHS0465 extends MetaData {
			public static final AssetDescriptor<ComboBox> MONEY_AND_BANK_NOTES = declare("Money and bank notes", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SECURITIES = declare("Securities", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> WATERCRAFT = declare("Watercraft", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> TRAILERS = declare("Trailers", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LOSS_OF_FIREARMS = declare("Loss of firearms", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LOSS_OF_SILVERWARE = declare("Loss of silverware", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PORTABLE_ELECTRONIC_EQUIPMENT = declare("Portable electronic equipment", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0477 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0490 extends MetaData {
		}

		public static final class EndorsementHS0492 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_STRUCTURE = declare("Description of Structure", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_STRUCTURE_USED_AS_A_DWELLING_OR_CAPABLE_OF_BEING_USED_AS_A_DWELLING = declare(
					"Is the structure used as a dwelling or capable of being used as a dwelling?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_STRUCTURE_USED_TO_CONDUCT_ANY_BUSINESS_OR_TO_STORE_ANY_BUSINESS_PROPERTY = declare(
					"Is the structure used to conduct any business or to store any business property?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_STRUCTURE_RENTED_OR_HELD_FOR_RENTAL_TO_ANY_PERSON_WHO_IS_NOT_A_RESIDENT_OF_THE_HOUSEHOLD = declare(
					"Is the structure rented or held for rental to any person who is not a resident of the household?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> FIRE_DEPARTMENT_TYPE = declare("Fire department type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION = declare("Subscription to fire department/station", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PUBLIC_PROTECTION_CLASS = declare("Public protection class", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DISTANCE_TO_FIRE_HYDRANT = declare("Distance to fire hydrant", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FIRE_PROTECTION_AREA = declare("Fire protection area", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0493 extends MetaData {}

		public static final class EndorsementHS0495 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0499 extends MetaData {}

		public static final class EndorsementHS0524 extends MetaData {}

		public static final class EndorsementHS0546 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_RENTED_UNIT = declare("Description of rented unit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_AMOUNT = declare("Coverage amount", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0578 extends MetaData {}

		public static final class EndorsementHS0614 extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COVERAGE_LIMIT = declare("Coverage limit", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0904 extends MetaData {
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class);
			public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration date", TextBox.class);
			public static final AssetDescriptor<RadioGroup> IS_THIS_AN_EXTENSION_OF_A_PRIOR_STRUCTURAL_ALTERATION_COVERAGE_ENDORSEMENT = declare(
					"Is this an extension of a prior Structural Alteration Coverage endorsement?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXTENSION = declare("Reason for extension", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THIS_FOR_AN_EXISTING_HOME_THAT_WILL_BE_UNDERGOING_RENOVATION_OR_REMODEL = declare(
					"Is this for an existing home that will be undergoing renovation or remodel?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THIS_FOR_A_NEW_HOME_THAT_IS_BEING_RENOVATED_FOR_SPECULATION = declare("Is this for a new home that is being renovated for speculation?",
					RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_CONSTRUCTION_BE_COMPLETED_BY_A_LICENSED_CONTRACTOR = declare("Will construction be completed by a licensed contractor?",
					RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_THE_HOME_REMAIN_OCCUPIED_DURING_CONSTRUCTION = declare("Will the home remain occupied during construction?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<TextBox> WHEN_WILL_CONSTRUCTION_BEGIN = declare("When will construction begin?", TextBox.class);
			public static final AssetDescriptor<RadioGroup> WILL_THE_CONSTRUCTION_BE_COMPLETED_WITHIN_180_DAYS = declare("Will the construction be completed within 180 days?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> A_COPY_OF_A_CERTIFICATE_OF_INSURANCE_FROM_THE_GENERAL_CONTRACTOR_IS_REQUIRED_HAS_THIS_BEEN_COLLECTED_AND_SENT_TO_FASTLANE =
					declare("A copy of a certificate of insurance from the general contractor is required. Has this been collected and sent to Fastlane?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0906 extends MetaData {
		}

		public static final class EndorsementHS0926 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class);
		}

		public static final class EndorsementHS0930 extends MetaData {
			public static final AssetDescriptor<ComboBox> PROPERTY_COVERAGE_LIMIT = declare("Property Coverage Limit", ComboBox.class);
			public static final AssetDescriptor<ComboBox> LIABILITY_COVERAGE_LIMIT = declare("Liability Coverage Limit", ComboBox.class);
		}

		public static final class EndorsementHS0934 extends MetaData {
		}

		public static final class EndorsementHS0937 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_STRUCTURE = declare("Description of structure", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LOCATION_OF_STRUCTURE = declare("Location of structure", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0965 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0988 extends MetaData {
			public static final AssetDescriptor<TextBox> NAME_OF_PERSON_OR_ORGANIZATION = declare("Name of person or organization", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_INSURABLE_INTEREST = declare("Description of insurable interest", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_EVENT = declare("Description of event", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EVENT_ZIP_CODE = declare("Event zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EVENT_STREET_ADDRESS_1 = declare("Event street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EVENT_STREET_ADDRESS_2 = declare("Event street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EVENT_CITY = declare("Event city", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> EVENT_STATE = declare("Event state", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_E_LIMIT = declare("Coverage E limit", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDITIONAL_COMMENTS_ABOUT_THE_EVENT = declare("Additional comments about the event", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_THERE_BE_ALCOHOLIC_BEVERAGES_AT_THE_EVENT = declare("Will there be alcoholic beverages at the event?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_THERE_BE_A_SWIMMING_POOL_BOUNCE_HOUSE_OR_TRAMPOLINE_AT_THE_EVENT_LOCATION = declare(
					"Will there be a swimming pool, bounce house, or trampoline at the event location?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_THERE_BE_ATHLETIC_ACTIVITIES_AT_THE_EVENT = declare("Will there be athletic activities at the event?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_THERE_BE_DANGEROUS_OR_EXOTIC_ANIMALS_INCLUDING_VICIOUS_DOGS_AT_THE_EVENT = declare(
					"Will there be dangerous or exotic animals, including vicious dogs at the event?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_THERE_BE_HIRED_OR_VOLUNTEER_STAFF_AT_THE_EVENT = declare("Will there be hired or volunteer staff at the event?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ESTIMATED_TOTAL_NUMBER_OF_GUESTS_18_AND_OLDER = declare("Estimated total number of guests 18 and older", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ESTIMATED_TOTAL_NUMBER_OF_GUESTS_UNDER_AGE_18 = declare("Estimated total number of guests under age 18", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> EVENT_LOCATION = declare("Event location", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> ADDITIONAL_INSURED_TYPE = declare("Additional insured type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_EVENT_VENUE_SELF_INSURED = declare("Is the event venue self-insured?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NAME_OF_THE_INSURANCE_CARRIER = declare("Name of the insurance carrier", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> IF_A_CONTRACT_APPLIES_DOES_THE_CONTRACT_INCLUDE_A_HOLD_HARMLESS_CLAUSE = declare(
					"If a contract applies, does the contract include a hold harmless clause?", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_A_PERMIT_OR_LICENSE_REQUIRED_FOR_THE_EVENT = declare("Is a permit or license required for the event?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup>
					IS_THE_EVENT_BEING_HELD_BY_THE_NAMED_INSURED_AND_IF_A_CONTRACT_APPLIES_IS_THE_CONTRACT_AGREEMENT_IN_THE_NAME_OF_AT_LEAST_ONE_OF_THE_NAMED_INSUREDS_ON_THIS_POLICY =
					declare("Is the event being held by the named insured and, if a contract applies, is the contract agreement in the name of at least one of the named insureds on this policy?",
							RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_EVENT_BEING_HELD_FOR_BUSINESS_PURPOSES = declare("Is the event being held for business purposes?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_EVENT_BEING_HELD_FOR_COMPENSATION_OR_TO_RAISE_DONATIONS_OR_MONEY_CHARITY_EVENTS_POLITICAL_FUNDRAISERS_ETC =
					declare("Is the event being held for compensation or to raise donations or money? (charity events, political fundraisers, etc)", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_THE_EVENT_BE_OPEN_TO_THE_PUBLIC = declare("Will the event be open to the public?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WILL_THE_EVENT_BE_HELD_ON_LAND = declare("Will the event be held on land?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class EndorsementHS1731 extends MetaData {}
		public static final class EndorsementHS1733 extends MetaData {}

		public static final class EndorsementHS2383 extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY_OTHER_STRUCTURES = declare("Limit of liability - Other Structures", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS2443 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_BUSINESS = declare("Description of business", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS2452 extends MetaData {}

		public static final class EndorsementHS2464 extends MetaData {
			public static final AssetDescriptor<TextBox> MAKE_OR_MODEL = declare("Make or Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SERIAL_OR_MOTOR_NUMBER = declare("Serial or Motor number", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS2471 extends MetaData {
			public static final AssetDescriptor<TextBox> NAME_OF_BUSINESS = declare("Name of business", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_BUSINESS = declare("Description of Business", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CLASSIFICATION_OCCUPATION = declare("Classification/occupation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_INSURED_SELF_EMPLOYED_A_PARTNER_IN_THE_BUSINESS_OR_MAINTAIN_ANY_FINANCIAL_CONTROL_IN_THIS_BUSINESS =
					declare("Is the insured self-employed, a partner in the business, or maintain any financial control in this business?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class EndorsementHS2472 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_THE_NATURE_OF_THE_FARMING = declare("Description of the nature of the farming", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_FARMING_LOCATED_AT_THE_RESIDENCE_PREMISES = declare("Is the farming located at the residence premises?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_INCOME_DERIVED_FROM_THE_FARMING_A_PRIMARY_SOURCE_OF_INCOME = declare(
					"Is the income derived from the farming a primary source of income?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_FARMING_LOCATION_USED_FOR_RACING_PURPOSES = declare("Is the farming location used for racing purposes?", RadioGroup.class,
					Waiters.AJAX);
		}

		public static final class EndorsementHS2473 extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street Address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street Address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> TOTAL_ACREAGE = declare("Total acreage", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_BUILDING_PRESENT_ON_THE_FARM_PREMISES = declare("Is the building present on the farm premises?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> TOTAL_NUMBER_OF_PERSON_DAYS_WORKED_FOR_ALL_PART_TIME_EMPLOYEES_WHO_WORK_40_DAYS_OR_LESS_PER_YEAR = declare(
					"Total number of person days worked for all part-time employees who work 40 days or less per year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_PART_TIME_EMPLOYEES_41_179_DAYS_PER_YEAR = declare("Number of part-time employees (41-179 days per year)", TextBox.class,
					Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_FULL_TIME_EMPLOYEES_180_OR_MORE_DAYS_PER_YEAR = declare("Number of full-time employees (180 or more days per year)", TextBox.class,
					Waiters.AJAX);
			public static final AssetDescriptor<TextBox> TOTAL_ANNUAL_PAYROLL = declare("Total annual payroll", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> OWNERSHIP_AND_OPERATION_OF_FARM = declare("Ownership and operation of farm", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup>
					IS_PRINCIPAL_PURPOSE_OF_THE_FARM_IS_TO_SUPPLY_COMMODITIES_FOR_MANUFACTURING_OR_PROCESSING_BY_THE_INSURED_FOR_SALE_TO_OTHERS_SUCH_AS_CREAMERIES_AND_DAIRIES =
					declare("Is principal purpose of the farm is to supply commodities for manufacturing or processing by the insured for sale to others, such as creameries and dairies?",
							RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_FARMING_LOCATION_USED_FOR_RACING_PURPOSES = declare("Is the farming location used for racing purposes?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DOES_THE_FARM_OPERATE_FREEZING_OR_DEHYDRATING_PLANTS_OR_POULTRY_FACTORIES = declare(
					"Does the farm operate freezing or dehydrating plants or poultry factories?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_FARM_INCORPORATED = declare("Is the farm incorporated?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_ANY_OTHER_BUSINESS_ACTIVITY_CONDUCTED_AT_THE_FARM_LOCATION = declare("Is any other business activity conducted at the farm location?",
					RadioGroup.class, Waiters.AJAX);
		}

		// Don't move, should be after HS2472/HS2473
		public static final class EndorsementHS0452 extends MetaData {
			public static final AssetDescriptor<TextBox> TOTAL_NUMBER_OF_LIVESTOCK = declare("Total number of livestock", TextBox.class);
		}

		public static final class EndorsementHS2494 extends MetaData {
			public static final AssetDescriptor<RadioGroup> IS_THE_EMPLOYEE_A_PRIVATE_RESIDENCE_OR_ESTATE_FULL_TIME_INSERVANT = declare(
					"Is the employee a Private Residence or Estate - Full Time Inservant?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_EMPLOYEE_A_PRIVATE_RESIDENCE_FULL_TIME_OUTSERVANT_INCLUDING_DRIVERS = declare(
					"Is the employee a Private Residence - Full Time Outservant including drivers?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_EMPLOYEE_A_PRIVATE_ESTATE_FULL_TIME_OUTSERVANT_INCLUDING_DRIVERS = declare(
					"Is the employee a Private Estate - Full Time Outservant including drivers?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LIABILITY_COVERAGE_LIMIT = declare("Liability coverage limit", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_EMPLOYEES_VALUE1 = declare("Number of Employees Value1", TextBox.class, Waiters.AJAX, By.id("policyDataGatherForm:sedit_AAAHoPolicyEndorsementFormManager_HS2494EndorsementForm_value1"));
			public static final AssetDescriptor<TextBox> NUMBER_OF_EMPLOYEES_VALUE2 = declare("Number of Employees Value2", TextBox.class, Waiters.AJAX, By.id("policyDataGatherForm:sedit_AAAHoPolicyEndorsementFormManager_HS2494EndorsementForm_value2"));
			public static final AssetDescriptor<TextBox> NUMBER_OF_EMPLOYEES_VALUE3 = declare("Number of Employees Value3", TextBox.class, Waiters.AJAX, By.id("policyDataGatherForm:sedit_AAAHoPolicyEndorsementFormManager_HS2494EndorsementForm_value3"));
		}

		public static final class EndorsementDS2494 extends MetaData {
			public static final AssetDescriptor<RadioGroup> IS_THE_EMPLOYEE_A_PRIVATE_RESIDENCE_OR_ESTATE_FULL_TIME_INSERVANT = declare(
					"Is the employee a Private Residence or Estate - Full Time Inservant?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_EMPLOYEE_A_PRIVATE_RESIDENCE_FULL_TIME_OUTSERVANT_INCLUDING_DRIVERS = declare(
					"Is the employee a Private Residence - Full Time Outservant including drivers?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_EMPLOYEE_A_PRIVATE_ESTATE_FULL_TIME_OUTSERVANT_INCLUDING_DRIVERS = declare(
					"Is the employee a Private Estate - Full Time Outservant including drivers?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LIABILITY_COVERAGE_LIMIT = declare("Liability coverage limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0929 extends MetaData {
			public static final AssetDescriptor<ComboBox> PROPERTY_COVERAGE_LIMIT = declare("Property Coverage Limit", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LIABILITY_COVERAGE_LIMIT = declare("Liability Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS2338 extends MetaData {
			public static final AssetDescriptor<TextBox> NUMBER_OF_PERSONS_RECEIVING_DAY_CARE_SERVICES = declare("Number of persons receiving day care services", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LOCATION_OF_BUSINESS = declare("Location of business", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SECTION_I_LIMIT_OF_LIABILITY = declare("Section I limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SECTION_I_DESCRIPTION_OF_STRUCTURE = declare("Section I description of structure", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SECTION_II_COVERAGES_E_AND_F_COMBINED = declare("Section II coverages E and F combined", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHS0931 extends MetaData {}

		public static final class EndorsementHS0932 extends MetaData {
			public static final AssetDescriptor<ComboBox> PROPERTY_COVERAGE_LIMIT = declare("Property Coverage Limit", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LIABILITY_COVERAGE_LIMIT = declare("Liability Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0929 extends MetaData {
			public static final AssetDescriptor<ComboBox> PROPERTY_COVERAGE_LIMIT = declare("Property Coverage Limit", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LIABILITY_COVERAGE_LIMIT = declare("Liability Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDS0934 extends MetaData {
		}

		public static final class EndorsementDS2482 extends MetaData {
		}
	}

	// (add endorsement HS 04 61 to show this tab)
	public static final class PersonalPropertyTab extends MetaData {
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> BICYCLES = declare("Bicycles", PersonalPropertyMultiAssetList.class, Bicycles.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyBicyclesItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> CAMERAS = declare("Cameras", PersonalPropertyMultiAssetList.class, Cameras.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyCamerasItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> COINS = declare("Coins", PersonalPropertyMultiAssetList.class, Coins.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyCoinsItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> FINE_ARTS = declare("Fine arts", PersonalPropertyMultiAssetList.class, FineArts.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyFineArtsItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> FIREARMS = declare("Firearms", PersonalPropertyMultiAssetList.class, Firearms.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyFirearmsItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> FURS = declare("Furs", PersonalPropertyMultiAssetList.class, Furs.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyFursItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> GOLF_EQUIPMENT = declare("Golf equipment", PersonalPropertyMultiAssetList.class, GolfEquipment.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyGolfItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> JEWELRY = declare("Jewelry", PersonalPropertyMultiAssetList.class, Jewelry.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyJewelryItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> MEDICAL_DEVICES = declare("Medical devices", PersonalPropertyMultiAssetList.class, MedicalDevices.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyMedicalItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> MUSICAL_INSTRUMENTS = declare("Musical instruments", PersonalPropertyMultiAssetList.class, MusicalInstruments.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyMusicalItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> POSTAGE_STAMPS = declare("Postage stamps", PersonalPropertyMultiAssetList.class, PostageStamps.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyPostageItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> SILVERWARE = declare("Silverware", PersonalPropertyMultiAssetList.class, Silverware.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertySilverwareItem']"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> TRADING_CARDS_OR_COMICS = declare("Trading cards or comics", PersonalPropertyMultiAssetList.class, TradingCardsOrComics.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyTradingCardsItem']"));

		public static final class Bicycles extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class Cameras extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class Coins extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_ARTICLES = declare("Number of articles", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class FineArts extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> FORM_OF_ART = declare("Form of art", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class Firearms extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class Furs extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class GolfEquipment extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LEFT_OR_RIGHT_HANDED_CLUB = declare("Left- or right-handed club", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class Jewelry extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> JEWELRY_CATEGORY = declare("Jewelry category", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class MedicalDevices extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> TYPE_OF_DEVICE = declare("Type of device", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class MusicalInstruments extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class PostageStamps extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_STAMPS = declare("Number of stamps", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class Silverware extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SET_OR_INDIVIDUAL_PIECE = declare("Set or individual piece", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		public static final class TradingCardsOrComics extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_COMIC_BOOKS_OR_CARDS = declare("Number of comic books or cards", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CERTIFICATE_OF_AUTHENTICITY_RECEIVED = declare("Certificate of authenticity received", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}
	}

	public static final class PremiumsAndCoveragesQuoteTab extends MetaData {
		public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> RECURRING_PAYMENT = declare("Recurring payment", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> BILL_TO_AT_RENEWAL = declare("Bill to at renewal", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment plan", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN_AT_RENEWAL = declare("Payment plan at renewal", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);

		public static final AssetDescriptor<StaticElement> COVERAGE_A = declare(HomeSSCoverages.COVERAGE_A.get(), StaticElement.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//span", HomeSSCoverages.COVERAGE_A.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_B = declare(HomeSSCoverages.COVERAGE_B.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeSSCoverages.COVERAGE_B.get())));
		public static final AssetDescriptor<TextBox> COVERAGE_C = declare(HomeSSCoverages.COVERAGE_C.get(), TextBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//input", HomeSSCoverages.COVERAGE_C.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_D = declare(HomeSSCoverages.COVERAGE_D.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeSSCoverages.COVERAGE_D.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_E = declare(HomeSSCoverages.COVERAGE_E.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeSSCoverages.COVERAGE_E.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_F = declare(HomeSSCoverages.COVERAGE_F.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeSSCoverages.COVERAGE_F.get())));
		public static final AssetDescriptor<ComboBox> DEDUCTIBLE = declare(HomeSSCoverages.DEDUCTIBLE.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeSSCoverages.DEDUCTIBLE.get())));
		public static final AssetDescriptor<ComboBox> HURRICANE_DEDUCTIBLE = declare(HomeSSCoverages.HURRICANE_DEDUCTIBLE.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeSSCoverages.HURRICANE_DEDUCTIBLE.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_C_BUILDING = declare(HomeSSCoverages.COVERAGE_C_BUILDING.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeSSCoverages.COVERAGE_C_BUILDING.get())));
		//public static final AssetDescriptor<JavaScriptButton> CALCULATE_PREMIUM = declare("Calculate Premium", JavaScriptButton.class, Waiters.AJAX, By.id("policyDataGatherForm:actionButton_AAAHORateAction"));
		public static final AssetDescriptor<JavaScriptButton> CALCULATE_PREMIUM =
				declare("Calculate Premium", JavaScriptButton.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:actionButton_AAAHORateAction'or @id='policyDataGatherForm:calculatePremium_AAAHORateAction']"));

		public static final AssetDescriptor<DialogAssetList> OVERRRIDE_PREMIUM_DIALOG = declare("Override Premium", DialogAssetList.class, OverridePremiumDialog.class,
				By.xpath("//form[@id='premiumOverrideInfoFormAAAHOPremiumOverride']"));

		public static final AssetDescriptor<FillableTable> INSTALLMENT_FEES_DETAILS_TABLE = declare("InstallemntFeesDetails", FillableTable.class, ListOfFeeDetailsRow.class, By.id("policyDataGatherForm:installmentFeeDetailsTable"));

		public static final class ListOfFeeDetailsRow extends MetaData {
			public static final AssetDescriptor<StaticElement> PAYMENT_METHOD = declare("Payment Method", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ENROLLED_IN_AUTO_PAY = declare("Enrolled in Auto Pay", StaticElement.class);
			public static final AssetDescriptor<StaticElement> INSTALLMENT_FEE = declare("Installment Fee", StaticElement.class);
		}

		public enum HomeSSCoverages {
			COVERAGE_A("Coverage A - Dwelling limit"),
			COVERAGE_B("Coverage B - Other Structures limit"),
			COVERAGE_C("Coverage C - Personal Property limit"),
			COVERAGE_C_BUILDING("Coverage C - Building Additions & Alterations"),
			COVERAGE_D(
					"Coverage D - Loss of Use limit"),
			COVERAGE_E("Coverage E - Personal Liability Each Occurrence"),
			COVERAGE_F("Coverage F - Medical Payments to Others"),
			DEDUCTIBLE(
					"Deductible"),
			HURRICANE_DEDUCTIBLE("Hurricane Deductible"),
			COVERAGE_A_DED("Coverage A Deductible"),
			COVERAGE_C_DED("Coverage C Deductible"),
			ADDITIONAL("Additional Limited Building Code Upgrade"),;

			private String value;

			HomeSSCoverages(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public static final class OverridePremiumDialog extends MetaData {
			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.AJAX, false,
					By.id("policyDataGatherForm:overridePremiumLinkHo"));
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REMARKS = declare("Remarks", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OVERRIDE_PREMIUM_BY_FLAT_AMOUNT = declare("Override Premium By Flat Amount", TextBox.class, Waiters.AJAX, By
					.id("premiumOverrideInfoFormAAAHOPremiumOverride:deltaPremiumAmt"));
			public static final AssetDescriptor<TextBox> OVERRIDE_PERCENTAGE = declare("Percentage", TextBox.class, Waiters.AJAX, By.id("premiumOverrideInfoFormAAAHOPremiumOverride:percentageAmt"));
			public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.AJAX, false,
					By.id("premiumOverrideInfoFormAAAHOPremiumOverride:premiumOverrideSaveBtn"));
			public static final AssetDescriptor<Button> BUTTON_CANCEL_POPUP = declare(AbstractDialog.DEFAULT_POPUP_CLOSER_NAME, Button.class, Waiters.DEFAULT, false,
					By.id("premiumOverrideInfoFormAAAHOPremiumOverride:premiumOverrideCancelBtn"));
			public static final AssetDescriptor<DialogAssetList> ADDITIONAL_POPUP_SUBMIT = declare("Additional Popup Submit", DialogAssetList.class, AdditionalOverridePremiumDialog.class,
					By.id("overrideModalConfirmationDialog_container"));
		}

		public static final class AdditionalOverridePremiumDialog extends MetaData {
			public static final AssetDescriptor<Button> BUTTON_SUBMIT_POPUP = declare(AbstractDialog.DEFAULT_POPUP_SUBMITTER_NAME, Button.class, Waiters.AJAX, false,
					By.id("overrideModalConfirmationDialogForm:okBtn"));
			public static final AssetDescriptor<Button> BUTTON_CANCEL_POPUP = declare(AbstractDialog.DEFAULT_POPUP_CLOSER_NAME, Button.class, Waiters.DEFAULT, false,
					By.id("overrideModalConfirmationDialogForm:cancelBtn"));
		}

		public static final AssetDescriptor<DialogAssetList> VIEW_CAPPING_DETAILS_DIALOG = declare("View Capping Details", DialogAssetList.class, PremiumsAndCoveragesQuoteTab.ViewCappingDetailsDialog.class,
				By.xpath("//form[@id='cappingDetailsPopupPanel']"));

		public static final class ViewCappingDetailsDialog extends MetaData {
			public static final AssetDescriptor<Link> VIEW_CAPPING_DETAILS = declare("View Capping Details", Link.class, By.id("policyDataGatherForm:cappingHODetailsPopup"));
			public static final AssetDescriptor<TextBox> MANUAL_CAPPING_FACTOR = declare("Manual Capping Factor (%)", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CAPPING_OVERRIDE_REASON = declare("Capping Override Reason", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> BUTTON_TO_PREMIUM_AND_COVERAGES = declare("Return to Premium & Coverages", Button.class, Waiters.AJAX, false, By.id("cappingDetailsPopupPanel:cappingReturnTo"));
			public static final AssetDescriptor<Button> BUTTON_CALCULATE = declare("Calculate", Button.class, Waiters.AJAX, false, By.id("cappingDetailsPopupPanel:cappingCalculate"));
			public static final AssetDescriptor<Button> BUTTON_SAVE_AND_RETURN_TO_PREMIUM_AND_COVERAGES = declare("Save and Return to Premium & Coverages", Button.class, Waiters.AJAX,
					false, By.id("cappingDetailsPopupPanel:cappingSave"));
		}

		public static final AssetDescriptor<Button> VIEW_RATING_DETAILS = declare("View Rating Details", Button.class,
				By.id("policyDataGatherForm:ratingHODetailsPopup"));

		public static final class ViewRatingDetailsWindow extends MetaData {
			public static final AssetDescriptor<TextBox> MEMBERSHIP_INDICATOR = declare("Membership current AAA Member indicator", TextBox.class, By.id("horatingDetailsPopupForm_6:ratingDetailsTable:5:j_id_1_27_5b_j_2_10_7"));
		}
	}

	public static final class MortgageesTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> MORTGAGEE = declare("Mortgagee", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetListConfirmationDialog> MORTGAGEE_REMOVE_CONFIRMATION = declare("Mortgagee remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX,
				false,
				By.xpath("//div[@id='confirmOptionalNoSelected_AAAHOMortgageeInfo_dialogContent' or @id='confirmOptionalNoSelected_AAAHOMortgageeInfo_DialogCDiv']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> MORTGAGEE_INFORMATION = declare("MortgageeInformation", MultiInstanceAfterAssetList.class, MortgageeInformation.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOMortgageeInfo']"));
		public static final AssetDescriptor<RadioGroup> USE_LEGAL_NAMED_INSURED = declare("Use legal named insured", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> LEGAL_NAMED_INSURED = declare("Legal named insured", TextBox.class, Waiters.AJAX, false, By
				.xpath("//textarea[@id='policyDataGatherForm:aaaLegalName']"));
		public static final AssetDescriptor<RadioGroup> USE_LEGAL_PROPERTY_ADDRESS = declare("Use legal property address", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetListConfirmationDialog> LEGAL_PROPERTY_ADDRESS_REMOVE_CONFIRMATION = declare("Legal property address remove confirmation",
				AssetListConfirmationDialog.class, Waiters.AJAX, false,
				By.xpath("//div[@id='confirmOptionalNoSelected_AAAHOLegalPropAddressComp_Dialog_container' or @id='confirmOptionalNoSelected_AAAHOLegalPropAddressComp_DialogCDiv']"));
		public static final AssetDescriptor<AssetList> LEGAL_PROPERTY_ADDRESS = declare("LegalPropetyAddress", AssetList.class, LegalPropetyAddress.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOLegalPropAddressComp']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ADDITIONAL_INSURED = declare("Is there an additional insured?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetListConfirmationDialog> ADDITIONAL_INSURED_REMOVE_CONFIRMATION = declare("Additional Insured remove confirmation", AssetListConfirmationDialog.class,
				Waiters.AJAX, false,
				By.xpath("//div[@id='confirmOptionalNoSelected_AAAHOAdditionalInsured_DialogCDiv' or @id='confirmOptionalNoSelected_AAAHOAdditionalInsured_Dialog_container']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_INSURED = declare("AdditionalInsured", MultiInstanceAfterAssetList.class, AdditionalInsured.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOAdditionalInsured']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ADDITIONA_INTEREST = declare("Is there an additional interest?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetListConfirmationDialog> ADDITIONAL_INTEREST_REMOVE_CONFIRMATION = declare("Additional Insured remove confirmation", AssetListConfirmationDialog.class,
				Waiters.AJAX, false,
				By.xpath("//div[@id='confirmOptionalNoSelected_AAAHOAdditionalInterest_Dialog_content' or @id='confirmOptionalNoSelected_AAAHOAdditionalInterest_DialogCDiv']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_INTEREST = declare("AdditionalInterest", MultiInstanceAfterAssetList.class, AdditionalInterest.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOAdditionalInterest']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ANY_THIRD_PARTY_DESIGNEE = declare("Is there any third party designee?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetList> THIRD_PARTY_DESIGNEE = declare("ThirdPartyDesignee", AssetList.class, ThirdPartyDesignee.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAThirdPartyDesignee']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_A_CERTIFICATE_HOLDER = declare("Is there a certificate holder?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetList> CERTIFICATE_HOLDERS = declare("CertificateHolders", AssetList.class, CertificateHolders.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOCertificateHolders']"));

		public static final class MortgageeInformation extends MetaData {
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN =
					declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOMortAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id(".//div[@id='addressValidationPopupAAAHOMortgageeAddressValidation_container']"));
			public static final AssetDescriptor<TextBox> LOAN_NUMBER = declare("Loan number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> USE_LEGAL_MORTGAGEE_FOR_EVIDENCE_OF_INSURANCE = declare("Use legal mortgagee for evidence of insurance", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LEGAL_MORTGAGEE_NAME = declare("Legal mortgagee name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOMortgageeInfo"));
		}

		public static final class LegalPropetyAddress extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOLegAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id(".//div[@id='addressValidationPopupAAAHOLegalPropAddressValidationComp_container']"));
		}

		public static final class AdditionalInsured extends MetaData {
			public static final AssetDescriptor<ComboBox> INTEREST = declare("Interest", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SAME_AS_INSURED_S_MAILING_ADDRESS = declare("Same as insured's mailing address?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOAdditionalInsured"));
		}

		public static final class AdditionalInterest extends MetaData {
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_INTEREST = declare("Description of interest", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOAdditionalInterest"));
		}

		public static final class ThirdPartyDesignee extends MetaData {
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class CertificateHolders extends MetaData {
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street Address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street Address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}
	}

	public static final class UnderwritingAndApprovalTab extends MetaData {
		public static final AssetDescriptor<ComboBox> UNDERWRITER_SELECTED_INSPECTION_TYPE = declare("Underwriter-selected inspection type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> INTERIOR_INSPECTION = declare("Interior inspection", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> EXTERIOR_INSPECTION = declare("Exterior inspection", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> HIGH_VALUE_INTERIOR_INSPECTION = declare("High Value Interior inspection", CheckBox.class, Waiters.AJAX);

		public static final AssetDescriptor<RadioGroup> HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED_IN_THE_PAST_3_YEARS = declare(
				"Have any applicants had a prior insurance policy cancelled, refused, or non-renewed in the past 3 years?", RadioGroup.class,
				Waiters.AJAX);

		public static final AssetDescriptor<RadioGroup> HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_PERSON = declare(
				"Have any of the applicant(s)’ current pets injured, intentionally or unintentionally, another creature or person?", RadioGroup.class, Waiters.AJAX);
		// public static final AssetDescriptor<RadioGroup> HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_PERSON =
		// declare("Have any of the applicant(s)' current pets injured, intentionally or unintentionally, another creature or person?", RadioGroup.class, Waiters.AJAX);

		// public static final AssetDescriptor<RadioGroup> HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED_IN_THE_PAST_3_YEARS =
		// declare("Have any applicants had a prior insurance policy cancelled, refused, or non-renewed in the past 3 years?", RadioGroup.class,Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> HAS_THE_PROPERTY_BEEN_IN_FORECLOSURE_PROCEEDINGS_WITHIN_THE_PAST_18_MONTHS = declare(
				"Has the property been in foreclosure proceedings within the past 18 months?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DO_EMPLOYEES_OF_ANY_RESIDENT_RESIDE = declare("Do employees of any resident or applicant reside in the dwelling?", RadioGroup.class,
				Waiters.AJAX);
		public static final AssetDescriptor<TextBox> TOTAL_NUMBER_OF_RESIDENT_EMPLOYEES = declare("Total number of part time and full time resident employees", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS_HOME_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare(
				"Is any business, home day care or farming activity conducted on the premises?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS_CONDUCTED_ON_THE_PREMISES_FOR_WHICH_AN_ENDORSEMENT_IS_NOT_ATTACHED_TO_THE_POLICY = declare(
				"Is any business, home day care, or farming activity conducted on the premises for which an endorsement is not already attached to the policy?", RadioGroup.class, Waiters.AJAX,
				By.xpath("//table[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaPremisesEndrosInd']"));
		public static final AssetDescriptor<RadioGroup> HAVE_ANY_APPLICANTS_HAD_A_FLAT_ROOF_RELATED_CLAIM = declare("Have any applicants had a flat roof related claim in the past 3 years?",
				RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> APPLICANTS_WHO_HAVE_BEEN_CANCELLED_REFUSED_INSURANCE_OR_NONRENEWED = declare(
				"Applicant(s), who have been cancelled, refused insurance or non-renewed in the past 3 years are ineligible if based on any of the following reasons: Fraud or Material Misrepresentation, Substantial Increase in Hazard, or Claims.",
				RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare("Is any business or farming activity conducted on the premises?",
				RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare(
				"Is any business, adult day care, pet day care or farming activity conducted on the premises?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES_FOR_WHICH_AN_ENDORSEMENT_IS_NOT_ALREADY_ATTACHED_TO_THE_POLICY = declare(
				"Is any business or farming activity conducted on the premises for which an endorsement is not already attached to the policy?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ARE_ALL_WATER_HEATERS_RAISED_FROM_THE_FLOOR = declare(
				"Are all water heaters (except tankless and electric heaters) strapped to the wall with at least one strap and if located in the garage, raised at least 18 inches from the floor?",
				RadioGroup.class, Waiters.AJAX);

		public static final AssetDescriptor<TextBox> REMARK_PRIOR_INSURANCE = declare("Remark Prior Insurance", TextBox.class, Waiters.AJAX, false, By
				.id("policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaPriorPolicyStatusRemark"));
		public static final AssetDescriptor<TextBox> REMARK_PRIOR_INSURANCE_MD = declare("Remark Prior Insurance MD", TextBox.class, Waiters.AJAX, false, By
				.id("policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaPriorPolicyStatusWithReasonsRemark"));
		public static final AssetDescriptor<TextBox> REMARK_FIRE_HAZARD = declare("Remark Fire Hazard", TextBox.class, Waiters.AJAX, false, By
				.id("policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaFireSafeDistRemark"));
		public static final AssetDescriptor<TextBox> REMARK_DWELL_SLOPE = declare("Remark Dwell Slope", TextBox.class, Waiters.AJAX, false, By
				.id("policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaDwellSlopeRemark"));
		public static final AssetDescriptor<TextBox> REMARK_FORECLOSURE = declare("Remark Foreclosure", TextBox.class, Waiters.AJAX, false, By
				.id("policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaForeClosureProceedingRemark"));
		public static final AssetDescriptor<TextBox> REMARK_RENOVATION = declare("Remark Renovation", TextBox.class, Waiters.AJAX, false, By
				.id("policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaDwellRenovationRemark"));
		public static final AssetDescriptor<TextBox> REMARK_RESIDENT_EMPLOYEES = declare("Remark Resident Employees", TextBox.class, Waiters.AJAX, false, By
				.id("policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaEmployeesGradeRemark"));

	}

	public static final class DocumentsTab extends MetaData {
		public static final AssetDescriptor<AssetList> DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODocGenPrint']"));
		public static final AssetDescriptor<AssetList> DOCUMENTS_TO_BIND = declare("DocumentsToBind", AssetList.class, DocumentsToBind.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOBindDocuments']"));
		public static final AssetDescriptor<AssetList> DOCUMENTS_TO_ISSUE = declare("DocumentsToIssue", AssetList.class, DocumentsToIssue.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOIssueDocuments']"));

		public static final class DocumentsForPrinting extends MetaData {

		}

		public static final class DocumentsToBind extends MetaData {
			public static final AssetDescriptor<RadioGroup> CONSUMER_INFORMATION_NOTICE = declare("Consumer Information Notice", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FAX_MEMORANDUM = declare("Fax Memorandum", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> HOMEOWNERS_INSURANCE_QUOTE_PAGE = declare("Homeowners Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> APPLICATION_FOR_HOMEOWNERS_INSURANCE = declare("Application for Homeowners Insurance", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_UNDERLYING_INSURANCE_POLICY = declare("Proof of Underlying Insurance policy for the tenant(s) received?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_CENTRAL_FIRE_ALARM = declare("Proof of central fire alarm", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_CENTRAL_THEFT_ALARM = declare("Proof of central theft alarm", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT = declare("Proof of subscription to fire department", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION = declare("Proof of home renovations for the Home Modernization discount", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_ENERGY_STAR_APPLIANCES = declare("Proof of ENERGY STAR appliances or green home features", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_COMPANION_DP3_POLICY = declare("Proof of companion DP3 policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_COMPANION_PUP_POLICY = declare("Proof of companion PUP policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_ACTIVE_AAA_AUTO_POLICY = declare("Proof of active AAA auto policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CERTIFICATE_NO_CONTAMINATION_CAUSED_BY_OIL_STORAGE_TANK = declare(
					"Certificate confirming no contamination caused by inactive underground oil storage tank", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS = declare("Proof of plumbing, electrical, heating/cooling system and roof renovations",
					RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ACTUAL_CASH_VALUE_LOSS_SETTLEMENT_LOSSES_TO_ROOF_SURFACING = declare(
					"Actual Cash Value Loss Settlement Windstorm Or Hail Losses To Roof Surfacing", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> APPRAISALS_SALES_RECEIPTS_FOR_SCHEDULED_PROPERTY = declare("Appraisals/sales receipts for scheduled personal property items",
					RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> COVERAGE_ACCEPTANCE_STATEMENT = declare("Coverage Acceptance Statement", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> THIRD_PARTY_DESIGNEE_FORM = declare("Third Party Designee Form", RadioGroup.class, Waiters.AJAX);

			public static final AssetDescriptor<RadioGroup> CALIFORNIA_RESIDENTIAL_PROPERTY_INSURANCE = declare("California Residential Property Insurance Disclosure", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> OHIO_MINE_SUBSIDENCE_INSURANCE_UNDERWRITING_ASSOSIATION_APPLICATION = declare(
					"Ohio Mine Subsidence Insurance Underwriting Association Application", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_LEAD_LIABILITY_AFFECTED_PROPERTY_REGISTERED_DOE = declare(
					"Proof lead liability \"affected property\" is registered with the Department of Environment and meets their requirements", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> KENTUCKY_MINE_SUBSIDENCE_INSURANCE_FUND_WAIVER_FORM = declare("Kentucky Mine Subsidence Insurance Fund Waiver Form", RadioGroup.class,
					Waiters.AJAX);
		}

		public static final class DocumentsToIssue extends MetaData {
			public static final AssetDescriptor<RadioGroup> KENTUCKY_MINE_SUBSIDENCE_INSURANCE_FUND_WAIVER_FORM = declare("Kentucky Mine Subsidence Insurance Fund Waiver Form", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNED_POLICY_APPLICATION = declare("Signed policy application", RadioGroup.class, Waiters.AJAX);
		}
	}

	public static final class BindTab extends MetaData {
		public static final AssetDescriptor<AssetList> PAPERLESS_PREFERENCES =
				declare("PaperlessPreferences", AssetList.class, PersonalUmbrellaMetaData.BindTab.PaperlessPreferences.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAPaperlessPreferences']"));
		public static final AssetDescriptor<AssetList> DOCUMENT_PRINTING_DETAILS =
				declare("DocumentPrintingDetails", AssetList.class, PersonalUmbrellaMetaData.BindTab.DocumentPrintingDetails.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PurchaseAction']"));

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

		public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class, Waiters.AJAX);
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

	// Policy Actions

	// updated everywhere
	public static final class CancellationActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
	}

	public static final class ChangeReinstatementLapseActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> REINSTATEMENT_LAPSE_DATE = declare("Reinstatement Lapse Date", TextBox.class);
		public static final AssetDescriptor<TextBox> REVISED_REINSTATEMENT_DATE = declare("Revised Reinstatement Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> LAPSE_CHANGE_REASON = declare("Lapse Change Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class);
	}

	// updated
	public static final class DeclineByCustomerActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AssetDescriptor<TextBox> DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	// updated
	public static final class ManualRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	// updated
	public static final class DeclineByCompanyActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AssetDescriptor<TextBox> DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	// updated
	public static final class DeleteCancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	// updated
	public static final class CopyQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	// updated
	public static final class RemoveDoNotRenewActionTab extends MetaData {
	}

	// updated
	public static final class CopyFromPolicyActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	// updated
	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	// updated
	public static final class DeletePendedTransactionActionTab extends MetaData {
	}

	public static final class RollOnChangesActionTab extends MetaData {
	}

	// updated everywhere
	public static final class RewriteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
	}

	public static final class ChangePendedEndEffDateActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
	}

	public static final class IssueActionTab extends MetaData {
	}

	// updated
	public static final class DoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> SUPPORTING_DATA = declare("Supporting Data", TextBox.class);
	}

	// updated everywhere
	public static final class ReinstatementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
	}

	// updated
	public static final class ProposeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
	}

	// updated
	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	// updated everywhere
	public static final class CancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.AJAX);
	}

	// updated
	public static final class RemoveManualRenewActionTab extends MetaData {
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

			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By
					.id("policyDataGatherForm:changeTargetProducerCd"));
		}
	}

	public static final class BindActionTab extends MetaData {
	}

	// updated everywhere
	public static final class EndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class);
	}

	// updated everywhere
	public static final class RenewActionTab extends MetaData {
		public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
		public static final AssetDescriptor<TextBox> RENEWAL_DATE = declare("Renewal Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REASON_FOR_RENEWAL_WITH_LAPSE = declare("Reason for Renewal with Lapse", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class, Waiters.AJAX);
	}

	public static final class GenerateOnDemandDocumentActionTab extends MetaData {
		public static final AssetDescriptor<FillableDocumentsTable> ON_DEMAND_DOCUMENTS = declare("OnDemandDocuments", FillableDocumentsTable.class, DocumentRow.class, By
				.xpath("(//div[@id='policyDataGatherForm:componentView_AAAHODocGen']//table)[1]"));
		public static final AssetDescriptor<AdvancedRadioGroup> DELIVERY_METHOD = declare("Delivery Method", AdvancedRadioGroup.class, Waiters.AJAX, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHODocGen_body']/table"));
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<DialogAssetList> E_SIGNATURE_EMAIL_DIALOG = declare("ESignatureEmailDialog", DialogAssetList.class, ESignatureEmailDialog.class, By.xpath("//div[@id='emailAddrPopup_container']"));

		public static final class ESignatureEmailDialog extends MetaData {
			public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class, By.id("recipientEmailAddressForm:recpEmailPopUp"));
			public static final AssetDescriptor<Button> GENERATE_DOCUMENTS =  declare("Submit Popup", Button.class, By.id("recipientEmailAddressForm:generateESignatureButton"));
			public static final AssetDescriptor<Link> CANCEL =  declare("Close Popup", Link.class, By.id("recipientEmailAddressForm:cancelESignatureButton"));
		}

		public static final class DocumentRow extends MetaData {
			public static final AssetDescriptor<CheckBox> SELECT = declare(DocGenConstants.OnDemandDocumentsTable.SELECT, CheckBox.class);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NUMBER = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, StaticElement.class);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NAME = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, StaticElement.class);

			// Fields of HSU03XX
			public static final AssetDescriptor<CheckBox> DECISION_BASED_ON_CLUE_HSU03 = declare("Decision based on CLUE HSU03", CheckBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:hsu03CIN:0']"));

			// Fields of HSU05XX
			public static final AssetDescriptor<TextBox> FIRST_NAME_HSU05 = declare("First name", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:firstName_HSU05']"));
			public static final AssetDescriptor<TextBox> LAST_NAME_HSU05 = declare("Last name", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:lastName_HSU05']"));
			public static final AssetDescriptor<TextBox> ZIP_CODE_HSU05 = declare("Zip code", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:zipCode_HSU05']"));
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1_HSU05 = declare("Street address 1", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:address1_HSU05']"));
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2_HSU05 = declare("Street address 2", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:address2Show_HSU05']"));
			public static final AssetDescriptor<TextBox> City_HSU05 = declare("City", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:city_HSU05']"));
			public static final AssetDescriptor<ComboBox> STATE_HSU05 = declare("State", ComboBox.class, Waiters.AJAX, false, By.xpath("//span[@id='policyDataGatherForm:stateShow_HSU05']/select"));
			public static final AssetDescriptor<TextBox> DESCRIPTION_HSU05 = declare("Description", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:uwMsg_HSU05']"));

			// Fields of HSU09XX
			public static final AssetDescriptor<CheckBox> DECISION_BASED_ON_CLUE_HSU09 = declare("Decision based on CLUE HSU09", CheckBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:hsu09CIN:0']"));
		}
	}

	// TODO Delete next meta if these actions not used in AAA
	public static final class ChangeRenewalLapseActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		public static final AssetDescriptor<TextBox> RENEWAL_LAPSE_DATE = declare("Renewal Lapse Date", TextBox.class);
		public static final AssetDescriptor<TextBox> REVISED_RENEWAL_DATE = declare("Revised Renewal Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> LAPSE_CHANGE_REASON = declare("Lapse Change Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class);
	}

	public static final class RescindCancellationActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> RESCIND_CANCELLATION_DATE = declare("Rescind Cancellation Date", TextBox.class);
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
		public static final AssetDescriptor<ComboBox> BUSINESS_TYPE = declare("Business Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
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
		public static final AssetDescriptor<ComboBox> SELECT_ADDITIONAL_INTEREST_PARTY = declare("Select 'Additional Interest' Party", ComboBox.class);
		public static final AssetDescriptor<TextBox> RANK = declare("Rank", TextBox.class);
		public static final AssetDescriptor<ComboBox> INTEREST_TYPE = declare("Interest Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> LOAN = declare("Loan #", TextBox.class);
		public static final AssetDescriptor<TextBox> LOAN_AMOUNT = declare("Loan Amount", TextBox.class);
		public static final AssetDescriptor<RadioGroup> ADD_AS_ADDITIONAL_INSURED = declare("Add as Additional Insured", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> WILL_THIS_MORTGAGEE_COMPANY_BE_PAYING_THE_POLICY_PREMIUM = declare("Will this Mortgagee Company be Paying the Policy Premium?",
				RadioGroup.class);
	}

	public static final class ExtensionRenewalActionTab extends MetaData {
	}

	public static final class AuthorityActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> AUTHORIZED_PERSON_REQUESTING_CHANGE = declare("Authorized Person Requesting Change", ComboBox.class);
	}

	public static final class ManualRenewalWithOrWithoutLapseActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		public static final AssetDescriptor<TextBox> RENEWAL_LAPSE_DATE = declare("Renewal Lapse Date", TextBox.class);
		public static final AssetDescriptor<TextBox> REVISED_RENEWAL_DATE = declare("Revised Renewal Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> LAPSE_CHANGE_REASON = declare("Lapse Change Reason", ComboBox.class, Waiters.AJAX);
	}

	public static final class CreateQuoteVersionTab extends MetaData {
		public static final AssetDescriptor<StaticElement> VERSION_NUM = declare("Version #", StaticElement.class);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
	}
}
