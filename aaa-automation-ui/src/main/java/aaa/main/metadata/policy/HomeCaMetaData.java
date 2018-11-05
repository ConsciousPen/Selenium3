/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata.policy;

import org.openqa.selenium.By;
import aaa.common.pages.Page;
import aaa.main.enums.DocGenConstants;
import aaa.main.metadata.DialogsMetaData;
import aaa.toolkit.webdriver.customcontrols.*;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.AssetListConfirmationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import aaa.toolkit.webdriver.customcontrols.endorsements.HomeCAEndorsementsMultiAssetList;
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
public final class HomeCaMetaData {

	// --------- General Tab --------
	public static final class GeneralTab extends MetaData {

		public static final AssetDescriptor<AssetList> POLICY_INFO = declare("PolicyInfo", AssetList.class, PolicyInfo.class);
		public static final AssetDescriptor<AssetList> CURRENT_CARRIER = declare("CurrentCarrier", AssetList.class, CurrentCarrier.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOPolicyPriorCarrier']"));

		// PolicyInfo
		public static final class PolicyInfo extends MetaData {
			public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SOURCE_POLICY_NUM = declare("Source Policy #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> APPLICATION_TYPE = declare("Application Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PRIOR_BASE_YEAR = declare("Prior base year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COMMISSION_TYPE = declare("Commission Type", ComboBox.class);
			public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
			public static final AssetDescriptor<ComboBox> LEAD_SOURCE = declare("Lead source", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> CALVET = declare("CalVet", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> ADVERSELY_IMPACTED = declare("Adversely Impacted", ComboBox.class);
		}

		// Current Carrier
		public static final class CurrentCarrier extends MetaData {
			public static final AssetDescriptor<ComboBox> CARRIER_NAME = declare("Carrier name", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class);
			public static final AssetDescriptor<TextBox> CONTINUOUS_YEARS_WITH_HO_INSURANCE = declare("Continuous years with HO insurance", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> BASE_DATE_WITH_AAA = declare("Base date with AAA", TextBox.class, Waiters.AJAX);
		}
	}

	// --------- Applicant Tab --------
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
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
			public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class);
			public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date Of Birth", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER = declare("Social security number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> BASE_DATE = declare("Base date", TextBox.class);
			public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> AAA_EMPLOYEE = declare("AAA employee", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> TRUSTEE_LLC_OWNER = declare("Trustee/LLC Owner", RadioGroup.class);
			// public static final AssetDescriptor<Button> BTN_ADD_INSURED = declare("Add", Button.class, Waiters.AJAX,
			// false, By.id("policyDataGatherForm:addAAAHONamedInsured"));
		}

		public static final class CustomerSearch extends MetaData {
			public static final AssetDescriptor<Button> BTN_CANCEL = declare("Cancel", Button.class, Waiters.AJAX, false, By.id("customerSearchFrom:cancelSearch"));
		}

		// AAA Membership
		public static final class AAAMembership extends MetaData {
			public static final AssetDescriptor<ComboBox> CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MEMBERSHIP_NUMBER = declare("Membership number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> OVERRIDE_TYPE = declare("Override Type", ComboBox.class, Waiters.AJAX);
		}

		// Dwelling Address
		public static final class DwellingAddress extends MetaData {
			public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By
					.id("policyDataGatherForm:validateHODwellAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAHODwellAddressValidationComp_container']"));
		}

		// Previous Dwelling Address
		public static final class PreviousDwellingAddress extends MetaData {
			public static final AssetDescriptor<RadioGroup> HAS_PREVIOUS_DWELLING_ADDRESS = declare("Has there been another dwelling address within the last 3 consecutive years?", RadioGroup.class,
					Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:sedit_AAAHOPreDwellingAddrExists_yesNoAnswer']"));
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOPreAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationPopupAAAHOPrevAddressValidationComp_container"));
		}

		// Mailing address
		public static final class MailingAddress extends MetaData {
			public static final AssetDescriptor<RadioGroup> IS_DIFFERENT_MAILING_ADDRESS = declare("Is the mailing address different from the dwelling address?", RadioGroup.class, Waiters.AJAX,
					false, By.xpath(".//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAHOMailingAddressComponent']"));
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By
					.id("policyDataGatherForm:validateHOMailingAddressButtonUS"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationPopupAAAHOMailingAddressValidation_container"));
		}

		// Named insured contact information
		public static final class NamedInsuredInformation extends MetaData {
			public static final AssetDescriptor<TextBox> HOME_PHONE_NUMBER = declare("Home Phone Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> WORK_PHONE_NUMBER = declare("Work Phone Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MOBILE_PHONE_NUMBER = declare("Mobile Phone Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PREFFERRED_PHONE_NUMBER = declare("Preferred Phone #", ComboBox.class);
			// public static final AssetDescriptor<TextBox> PREFERRED_PHONE = declare("Preferred Phone", TextBox.class,
			// Waiters.AJAX,
			// By.id("policyDataGatherForm:sedit_AAAHOCommunicationInfo_preferredContact_label"));
			public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
		}

		// Other active AAA policies
		public static final class OtherActiveAAAPolicies extends MetaData {
			public static final AssetDescriptor<RadioGroup> OTHER_ACTIVE_AAA_POLICIES = declare("Other active AAA policies", RadioGroup.class, Waiters.AJAX, false, By
					.xpath("//table[@id='policyDataGatherForm:formGrid_AAAHOOtherPolicyViewOnly']"));
			public static final AssetDescriptor<Button> ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOOtherOrPriorPolicyComponent"));
			public static final AssetDescriptor<SingleSelectSearchDialog> ACTIVE_UNDERLYING_POLICIES_SEARCH = declare("ActiveUnderlyingPoliciesSearch", SingleSelectSearchDialog.class,
					OtherActiveAAAPoliciesSearch.class, By.xpath(".//form[@id='policySearchForm_AAAHOOtherOrPriorActivePolicySearch']"));
			public static final AssetDescriptor<Button> REMOVE_BTN = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAAHOOtherOrPriorPolicyComponent"));
			public static final AssetDescriptor<AssetList> ACTIVE_UNDERLYING_POLICIES_MANUAL = declare("ActiveUnderlyingPoliciesManual", AssetList.class, OtherActiveAAAPoliciesManual.class);

			public static final class OtherActiveAAAPoliciesSearch extends MetaData {
				public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy number", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<Button> CANCEL_BTN = declare("Cancel", Button.class, Waiters.AJAX, By
						.id("policySearchForm_AAAHOOtherOrPriorActivePolicySearch:cancelSearch_AAAHOOtherOrPriorActivePolicySearch"));
			}

			public static final class OtherActiveAAAPoliciesManual extends MetaData {
				public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<RadioGroup> COMPANION_AUTO_PENDING_WITH_DISCOUNT = declare("Companion Auto pending with discount?", RadioGroup.class, Waiters.AJAX);
				public static final AssetDescriptor<RadioGroup> COMPANION_PUP_DP3_PENDING_WITH_DISCOUNT = declare("Companion PUP/DP3 Pending with Discount?", RadioGroup.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy number", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> POLICY_BASE_YEAR = declare("Policy base year", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> POLICY_TIER = declare("Policy tier", ComboBox.class, Waiters.AJAX);
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

		// Agent and Agency information
		public static final class AgentInfo extends MetaData {
			public static final AssetDescriptor<ComboBox> CHANNEL_TYPE = declare("Channel Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENCY = declare("Agency", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class);
			public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENCY_LOCATION = declare("Agency Location", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LOCATION = declare("Location", ComboBox.class);
			public static final AssetDescriptor<ComboBox> AGENT = declare("Agent", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class);
			public static final AssetDescriptor<TextBox> AGENT_NUMBER = declare("Agent Number", TextBox.class);
			public static final AssetDescriptor<TextBox> TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class, Waiters.AJAX);
		}
	}

	// --------- Reports Tab --------
	public static final class ReportsTab extends MetaData {
		/*
		 * public static final AssetDescriptor<RadioGroup> CUSTOMER_AGREEMENT = declare( "Customer Agreement",
		 * RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:customerRadio']"));
		 */
		public static final AssetDescriptor<RadioGroup> SALES_AGENT_AGREEMENT = declare("Sales Agent Agreement", RadioGroup.class, Waiters.AJAX, false, By
				.xpath("//table[@id='policyDataGatherForm:sedit_AAAHOCommonDisclosureMessageComponent_agentAgrees']"));

		public static final AssetDescriptor<FillableTable> AAA_MEMBERSHIP_REPORT = declare("AAAMembershipReport", FillableTable.class, AaaMembershipReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:membershipReports']"));
		public static final AssetDescriptor<FillableTable> FIRELINE_REPORT = declare("FirelineReport", FillableTable.class, FirelineReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:firelineReportTable']"));
		public static final AssetDescriptor<FillableTable> PUBLIC_PROTECTION_CLASS = declare("PublicProtectionClass", FillableTable.class, PublicProtectionClassRow.class, By
				.xpath("//table[@id='policyDataGatherForm:ppcReportTable']"));
		public static final AssetDescriptor<FillableTable> CLUE_REPORT = declare("CLUEreport", FillableTable.class, CLUEreportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:orderClueReports']"));
		public static final AssetDescriptor<FillableTable> ISO360_REPORT = declare("ISO360Report", FillableTable.class, ISO360ReportRow.class, By
				.xpath("//table[@id='policyDataGatherForm:iso360ReportTable']"));

		public enum AAAMembershipReportsTblHeaders {

			MEMBERSHIP_NO("Membership No."), ORDER_DATE("Order Date"), RECEIPT_DATE("Receipt Date"), STATUS("Status"), REPORT(
					"Report");

			private String value;

			AAAMembershipReportsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum FirelineReportTblHeaders {

			DWELLING_ADDRESS("Dwelling Address"), WILDFIRE_SCORE("Wildfire Score"), ORDER_DATE("Order Date"), EXPIRATION_DATE("Expiration Date"), STATUS("Status"), REPORT("Report");

			private String value;

			FirelineReportTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}

		}

		public enum PublicProtectionClassTblHeaders {

			NAME("Name"), DWELLING_ADDRESS("Dwelling Address"), PPC_VALUE("PPC Value"), ORDER_DATE("Order Date"), EXPIRATION_DATE("Expiration Date"), STATUS("Status"), REPORT("Report");

			private String value;

			PublicProtectionClassTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum CLUEReportTblHeaders {

			DWELLING_ADDRESS("Dwelling Address"), NO_OF_CLAIMS("No. of Claims"), ORDER_DATE("Order Date"), EXPIRATION_DATE("Expiration Date"), STATUS("Status"), REPORT("Report");

			private String value;

			CLUEReportTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum ISO360reportTblHeaders {

			DWELLING_ADDRESS("Dwelling Address"), REPLACEMENT_COST("Replacement Cost"), ORDER_DATE("Order Date"), EXPIRATION_DATE("Expiration Date"), STATUS("Status"), REPORT("Report");

			private String value;

			ISO360reportTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public static final class AaaMembershipReportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> MEMBERSHIP_NO = declare("Membership No.", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
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
		}

		public static final class CLUEreportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AssetDescriptor<StaticElement> NO_OF_CLAIMS = declare("No. of Claims", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
		}

		public static final class ISO360ReportRow extends MetaData {
			public static final AssetDescriptor<StaticElement> DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AssetDescriptor<StaticElement> REPLACEMENT_COST = declare("Replacement Cost", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
			public static final AssetDescriptor<Link> REPORT = declare("Report", Link.class);
		}

		public static final class HssOverrideInsuranceScore extends MetaData {
			public static final AssetDescriptor<TextBox> SCORE_AFTER_OVERRIDE = declare("Score after override", TextBox.class);
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class);
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

	// --------- Property Info Tab --------
	public static final class PropertyInfoTab extends MetaData {

		public static final AssetDescriptor<AssetList> DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class, By
				.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOAddiDwellAddrMVO']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_ADDRESS = declare("AdditionalAddress", MultiInstanceAfterAssetList.class, AdditionalAddress.class, By
				.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOAdditionalAddress']"));
		public static final AssetDescriptor<AssetList> PUBLIC_PROTECTION_CLASS = declare("PublicProtectionClass", AssetList.class, PublicProtectionClass.class);
		// ,By.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAPpcDetailsMVO']"));
		public static final AssetDescriptor<AssetList> FIRE_REPORT = declare("FireReport", AssetList.class, FireReport.class);
		// ,By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAFirelineDetailsMVO']"));
		public static final AssetDescriptor<AssetList> PROPERTY_VALUE = declare("PropertyValue", AssetList.class, PropertyValue.class);
		public static final AssetDescriptor<AssetList> CONSTRUCTION = declare("Construction", AssetList.class, Construction.class);
		public static final AssetDescriptor<AssetList> ADDITIONAL_QUESTIONS = declare("AdditionalQuestions", AssetList.class, AdditionalQuestions.class);
		public static final AssetDescriptor<AssetList> INTERIOR = declare("Interior", AssetList.class, Interior.class);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> DETACHED_STRUCTURES = declare("DetachedStructures", MultiInstanceAfterAssetList.class, DetachedStructures.class);
		public static final AssetDescriptor<AssetList> CEA_INFORMATION = declare("CeaInformation", AssetList.class, CeaInformation.class);
		public static final AssetDescriptor<AssetList> FIRE_PROTECTIVE_DD = declare("FireProtectiveDD", AssetList.class, FireProtectiveDD.class);
		public static final AssetDescriptor<AssetList> THEFT_PROTECTIVE_DD = declare("TheftProtectiveTPDD", AssetList.class, TheftProtectiveTPDD.class);
		public static final AssetDescriptor<AssetList> HOME_RENOVATION = declare("HomeRenovation", AssetList.class, HomeRenovation.class);
		public static final AssetDescriptor<AssetList> PETS_OR_ANIMALS = declare("PetsOrAnimals", AssetList.class, PetsOrAnimals.class);
		public static final AssetDescriptor<AssetList> STOVES = declare("Stoves", AssetList.class, Stoves.class);
		public static final AssetDescriptor<AssetList> RECREATIONAL_EQUIPMENT = declare("RecreationalEquipment", AssetList.class, RecreationalEquipment.class);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> CLAIM_HISTORY = declare("ClaimHistory", MultiInstanceAfterAssetList.class, ClaimHistory.class);
		public static final AssetDescriptor<AssetList> RENTAL_INFORMATION = declare("RentalInformation", AssetList.class, RentalInformation.class);

		public enum AdditionalAddressTblHeaders {

			STREET_ADDRESS1("Street address 1"), STREET_ADDRESS2("Street address 2"), CITY("City"), STATE("State"), ZIP_CODE("Zip Code");

			private String value;

			AdditionalAddressTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum DetachedStructuresTblHeaders {

			RENTED("Rented"), DECRIPTION("Description"), LIMIT_OF_LIABILITY("Limit Of Liability"), FAMILY_UNITS("Family Units"), OCCUPANTS("Occupants");

			private String value;

			DetachedStructuresTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum PetsOrAnimalsTblHeaders {

			ANIMAL_TYPE("Animal Type"), OTHER_SPECIFY("Other - Specify"), Count("Count");

			private String value;

			PetsOrAnimalsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum ClaimsTblHeaders {

			DATE_OF_LOSS("Date of loss"), CAUSE_OF_LOSS("Cause of loss"), AMOUNT_OF_LOSS("Amount of loss"), CLAIM_STATUS("Claim Status"), CHARGEABLE("Chargeable"), SOURCE("Source");

			private String value;

			ClaimsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public static final class DwellingAddress extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<ComboBox> STATE = declare("State / Province", ComboBox.class);
			public static final AssetDescriptor<ComboBox> NUMBER_OF_FAMILY_UNITS = declare("Number of family units", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SECTION_II_TERRITORY = declare("Section II territory", ComboBox.class);
		}

		public static final class AdditionalAddress extends MetaData {
			public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_ADDITIONAL_ADDRESSES = declare("Are there any additional addresses?", RadioGroup.class, Waiters.AJAX, false, By
					.xpath("//table[@id='policyDataGatherForm:addOptionalQuestion_AAAHOAdditionalAddress']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.id("confirmOptionalNoSelected_AAAHOAdditionalAddress_Dialog_container"));
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOAddAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS = declare("Validate Address Dialog", AddressValidationDialog.class,
					DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationFormAAAHOAdditionalDwelAddressValidation"));
			public static final AssetDescriptor<Button> BTN_ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOAdditionalAddress"));
			public static final AssetDescriptor<Button> BTN_REMOVE = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAAHOAdditionalAddress"));
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
			public static final AssetDescriptor<ComboBox> HAIL_RESISTANCE_RATING = declare("Hail-resistance rating", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> FOUNDATION_TYPE = declare("Foundation Type", ComboBox.class, Waiters.AJAX);
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
			public static final AssetDescriptor<ComboBox> NUMBER_OF_STORIES_INCLUDING_BASEMENT = declare("Number of stories, including basement", ComboBox.class, Waiters.AJAX);
		}

		public static final class DetachedStructures extends MetaData {
			public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY = declare("Are there any detached structures on the property?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHODetachedStructuresInfoComponent"));
			public static final AssetDescriptor<Button> REMOVE_BTN = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAAHODetachedStructuresInfoComponent"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.id("confirmOptionalNoSelected_AAAHODetachedStructuresInfoComponent_Dialog_container"));
			public static final AssetDescriptor<RadioGroup> RENTED_TO_OTHERS = declare("Rented to others", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> NUMBER_OF_FAMILY_UNITS = declare("Number of family units", ComboBox.class, Waiters.AJAX, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHODetachedStructuresInfoComponent_aaaNumberOfFamilyunits']"));
			public static final AssetDescriptor<ComboBox> NUMBER_OF_OCCUPANTS = declare("Number of occupants", ComboBox.class, Waiters.AJAX);
		}

		public static final class CeaInformation extends MetaData {
			public static final AssetDescriptor<ComboBox> FOUNDATION_TYPE = declare("Foundation type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ARE_THE_WATER_HEATERS_SECURED = declare("Are the water heaters secured to building frame?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWOOD = declare("Are cripple walls braced with plywood or equivalent?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_DWELLING_SECURED_TO_FOUNDATION = declare("Is the dwelling secured to foundation?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DOES_DWELLING_HAS_AN_ATTACHED_GARAGE = declare("Does dwelling has an attached garage?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_DWELLING_ANCHORED = declare(
					"Is the dwelling anchored to foundation using approved anchor bolts in accordance with California building code?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_WATER_HEATER_SECURED_FOR_EARTHQUAKE = declare(
					"Is the water heater secured to the building frame in accordance with guidelines for earthquake bracing of residential water heaters?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DOES_DWELLING_HAVE_CRIPLE_WALS = declare("Does the dwelling have cripple walls?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWODD = declare(
					"Are cripple walls braced with plywood or its equivalent and installed in accordance with California building code?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DOES_DWELLING_HAVE_POST_AND_PIER_FOUNDATION = declare("Does dwelling have post-and-pier or post-and-beam foundation?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_DWELLING_HAD_BEEN_MODIFIED_ACCORDING_BUILDING_CODE = declare(
					"Is dwelling with post-and-pier or post-and-beam foundation, has been modified in accordance with California building code?", RadioGroup.class, Waiters.AJAX);
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
			public static final AssetDescriptor<CheckBox> GATED_COMMUNITY = declare("Gated community", CheckBox.class, Waiters.NONE, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOAntiTheftDevicesInfoProxy_gatedCommInd']"));
		}

		public static final class HomeRenovation extends MetaData {
			public static final AssetDescriptor<ComboBox> PLUMBING_RENOVATION = declare("Plumbing renovation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PLUMBING_PERCENT_COMPLETE = declare("Plumbing % complete", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingPercentComplete']"));
			public static final AssetDescriptor<ComboBox> PLUMBING_MONTH_OF_COMPLECTION = declare("Plumbing Month of completion", ComboBox.class, Waiters.AJAX, false, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingRenovationMonth']"));
			public static final AssetDescriptor<TextBox> PLUMBING_YEAR_OF_COMPLECTION = declare("Plumbing Year of completion", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingRenovationYear']"));

			public static final AssetDescriptor<ComboBox> ELECTRICAL_RENOVATION = declare("Electrical renovation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ELECTRICAL_PERCENT_COMPLETE = declare("Electrical % complete", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalPercentComplete']"));
			public static final AssetDescriptor<ComboBox> ELECTRICAL_MONTH_OF_COMPLECTION = declare("Electrical Month of completion", ComboBox.class, Waiters.AJAX, false, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalRenovationMonth']"));
			public static final AssetDescriptor<TextBox> ELECTRICAL_YEAR_OF_COMPLECTION = declare("Electrical Year of completion", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalRenovationYear']"));

			public static final AssetDescriptor<ComboBox> ROOF_RENOVATION = declare("Roof renovation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ROOF_PERCENT_COMPLETE = declare("Roof % complete", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofPercentComplete']"));
			public static final AssetDescriptor<ComboBox> ROOFG_MONTH_OF_COMPLECTION = declare("Roof Month of completion", ComboBox.class, Waiters.AJAX, false, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofRenovationMonth']"));
			public static final AssetDescriptor<TextBox> ROOF_YEAR_OF_COMPLECTION = declare("Roof Year of completion", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofRenovationYear']"));

			public static final AssetDescriptor<ComboBox> HEATING_COOLING_RENOVATION = declare("Heating/cooling renovation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HEATING_COOLING_PERCENT_COMPLETE = declare("Heating/cooling % complete", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingPercentComplete']"));
			public static final AssetDescriptor<ComboBox> HEATING_COOLING_MONTH_OF_COMPLECTION = declare("Heating/cooling Month of completion", ComboBox.class, Waiters.AJAX, false, By
					.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingRenovationMonth']"));
			public static final AssetDescriptor<TextBox> HEATING_COOLING_YEAR_OF_COMPLECTION = declare("Heating/cooling Year of completion", TextBox.class, Waiters.AJAX, false, By
					.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingRenovationYear']"));

			// public static final AssetDescriptor<RadioGroup> GREEN_HOME_DISCOUNT = declare("Green Home discount",
			// RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> STORM_SHUTTER_DISCOUNT = declare("Storm Shutter discount", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> WINDSTORM_MITIGATION_DISCOUNT = declare("Windstorm Mitigation discount", RadioGroup.class, Waiters.AJAX);
		}

		public static final class PetsOrAnimals extends MetaData {
			public static final AssetDescriptor<RadioGroup> ARE_ANY_PETS_OR_ANIMALS_KEPT_ON_THE_PROPERTY = declare("Are any insured-owned pets or animals kept on the property?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<Button> BTN_ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAADwellAnimalInfoComponent"));
			public static final AssetDescriptor<Button> BTN_REMOVE = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAADwellAnimalInfoComponent"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.id("confirmOptionalNoSelected_AAADwellAnimalInfoComponent_Dialog_container"));
			public static final AssetDescriptor<ComboBox> ANIMAL_TYPE = declare("Animal type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class);
			public static final AssetDescriptor<TextBox> AGE = declare("Age", TextBox.class);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class);
			public static final AssetDescriptor<TextBox> OTHER_SPECIFY = declare("Other - specify", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ANIMAL_COUNT = declare("Animal count", TextBox.class, Waiters.AJAX);
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
			public static final AssetDescriptor<StaticElement> LABEL_CLAIM_HISTORY = declare("Claim History", StaticElement.class, By.id("policyDataGatherForm:componentViewPanelHeaderLabel_AAAHOLossInfo"));
			public static final AssetDescriptor<Button> BTN_ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOLossInfo"));
			public static final AssetDescriptor<Button> BTN_REMOVE = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminate"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.id("confirmOptionalNoSelected_AAAHOLossInfo_Dialog_container"));
			public static final AssetDescriptor<ComboBox> SOURCE = declare("Source", ComboBox.class);
			public static final AssetDescriptor<TextBox> ZIP = declare("Zip", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class, Waiters.AJAX, false, By.id("policyDataGatherForm:sedit_AAAHOLossInfo_aaaAddressEntity_address_addressLine1"));
			public static final AssetDescriptor<TextBox> DATE_OF_LOSS = declare("Date of loss", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<AdvancedComboBox> CAUSE_OF_LOSS = declare("Cause of loss", AdvancedComboBox.class);
			public static final AssetDescriptor<TextBox> AMOUNT_OF_LOSS = declare("Amount of loss", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<AdvancedComboBox> CLAIM_STATUS = declare("Claim status", AdvancedComboBox.class);
			public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> RENTAL_CLAIM = declare("Rental Claim", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> AAA_CLAIM = declare("AAA Claim", RadioGroup.class);
			public static final AssetDescriptor<RadioGroup> CATASTROPHE_LOSS = declare("Catastrophe loss", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CATASTROPHE_LOSS_CODE_REMARKS = declare("Catastrophe loss code/remarks", TextBox.class);
			public static final AssetDescriptor<ComboBox> LOSS_FOR = declare("Loss for", ComboBox.class);
			public static final AssetDescriptor<RadioGroup> CHARGEABLE = declare("Chargeable", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_CLAIM_IS_NOT_CHARGEABLE = declare("Reason claim is not chargeable", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<AssetListConfirmationDialog> ACTIVITY_REMOVE_CONFIRMATION =
					declare("Activity remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_container"));
		}

		public static final class RentalInformation extends MetaData {
			public static final AssetDescriptor<TextBox> NUMBER_OF_CONSECUTIVE_YEARS_INSURED_HAS_OWNED_ANY_RENTAL_PROPERTIES = declare(
					"Number of consecutive years insured has owned any rental properties", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR_FIRST_RENTED = declare("Year first rented", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PROPERTY_MANAGER = declare("Property manager", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_ADDITIONAL_RENTAL_DWELLINGS = declare("Are there any additional rental dwellings ?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DOES_THE_TENANT_HAVE_AN_UNDERLYING_HO4_POLICY = declare("Does the tenant have an underlying HO4 policy?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class OilPropaneStorageTank extends MetaData {
			public static final AssetDescriptor<ComboBox> OIL_FUEL_OR_PROPANE_STORAGE_TANK = declare("Oil Fuel or Propane Storage Tank", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ADD_FUEL_SYSTEM_STORAGE_TANK_COVERAGE = declare("Add fuel system storage tank coverage?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> AGE_OF_OIL_OR_PROPANE_FUEL_STORAGE_TANK = declare("Age of oil or propane fuel storage tank", TextBox.class, Waiters.AJAX);
		}
	}

	// --------- Premium and Coverage - Endorsement Tab --------
	public static final class EndorsementTab extends MetaData {
		public static final AssetDescriptor<TextBox> OPTIONAL_ENDORSEMENTS_FILTER = declare("Optional Endorsements Filter", TextBox.class, Waiters.AJAX, false, By
				.id("policyDataGatherForm:filterCriteria_AAAHoPolicyEndorsementFormManager"));
		public static final AssetDescriptor<Button> BTN_FILTER =
				declare("Filter", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:filterFormsButton_AAAHoPolicyEndorsementFormManager"));
		public static final AssetDescriptor<Button> BTN_VIEW_ALL =
				declare("View All", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:viewAllButton_AAAHoPolicyEndorsementFormManager"));

		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DL_01_04 = declare("DL 01 04",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementDL0104.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DL_24_02 = declare("DL 24 02",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementDL2402.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DL_24_11 = declare("DL 24 11",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementDL2411.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DL_24_33 = declare("DL 24 33",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementDL2433.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DL_24_82 = declare("DL 24 82", HomeCAEndorsementsMultiAssetList.class, EndorsementDL2482.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DP_04_18 = declare("DP 04 18", HomeCAEndorsementsMultiAssetList.class, EndorsementDP0418.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DP_04_22 = declare("DP 04 22", HomeCAEndorsementsMultiAssetList.class, EndorsementDP0422.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DP_04_71 = declare("DP 04 71", HomeCAEndorsementsMultiAssetList.class, EndorsementDP0471.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DP_04_73 = declare("DP 04 73", HomeCAEndorsementsMultiAssetList.class, EndorsementDP0473.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DP_04_75 = declare("DP 04 75", HomeCAEndorsementsMultiAssetList.class, EndorsementDP0475.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DP_04_95 = declare("DP 04 95", HomeCAEndorsementsMultiAssetList.class, EndorsementDP0495.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DW_04_20 = declare("DW 04 20", HomeCAEndorsementsMultiAssetList.class, EndorsementDW0420.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DW_04_21 = declare("DW 04 21", HomeCAEndorsementsMultiAssetList.class, EndorsementDW0421.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DW_04_41 = declare("DW 04 41",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementDW0441.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DW_04_63 = declare("DW 04 63", HomeCAEndorsementsMultiAssetList.class, EndorsementDW0463.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> DW_09_25 = declare("DW 09 25", HomeCAEndorsementsMultiAssetList.class, EndorsementDW0925.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> F1759C = declare("F1759C", HomeCAEndorsementsMultiAssetList.class, EndorsementF1759C.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HARI = declare("HARI", HomeCAEndorsementsMultiAssetList.class, EndorsementHARI.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_01_04 = declare("HO 01 04",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO0104.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_04_10 = declare("HO 04 10", HomeCAEndorsementsMultiAssetList.class, EndorsementHO0410.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_04_55 = declare("HO 04 55", HomeCAEndorsementsMultiAssetList.class, EndorsementHO0455.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_04_96 = declare("HO 04 96",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO0496.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_09_15 = declare("HO 09 15",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO0915.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_1004 = declare("HO-1004",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO1004.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_164 = declare("HO-164", HomeCAEndorsementsMultiAssetList.class, EndorsementHO164.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_164C = declare("HO-164C", HomeCAEndorsementsMultiAssetList.class, EndorsementHO164C.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_177 = declare("HO-177",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO177.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_17_31 = declare("HO 17 31",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO1731.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_17_32 = declare("HO 17 32", HomeCAEndorsementsMultiAssetList.class, EndorsementHO1732.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_17_33 = declare("HO 17 33", HomeCAEndorsementsMultiAssetList.class, EndorsementHO1733.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_210 = declare("HO-210", HomeCAEndorsementsMultiAssetList.class, EndorsementHO210.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_210C = declare("HO-210C", HomeCAEndorsementsMultiAssetList.class, EndorsementHO210C.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_28 = declare("HO-28", HomeCAEndorsementsMultiAssetList.class, EndorsementHO28.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_29 = declare("HO-29", HomeCAEndorsementsMultiAssetList.class, EndorsementHO29.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_300 = declare("HO-300", HomeCAEndorsementsMultiAssetList.class, EndorsementHO300.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_40 = declare("HO-40", HomeCAEndorsementsMultiAssetList.class, EndorsementHO40.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_41 = declare("HO-41", HomeCAEndorsementsMultiAssetList.class, EndorsementHO41.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_42 = declare("HO-42", HomeCAEndorsementsMultiAssetList.class, EndorsementHO42.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_42C = declare("HO-42C", HomeCAEndorsementsMultiAssetList.class, EndorsementHO42C.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_43 = declare("HO-43", HomeCAEndorsementsMultiAssetList.class, EndorsementHO43.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_43C = declare("HO-43C", HomeCAEndorsementsMultiAssetList.class, EndorsementHO43C.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_44 = declare("HO-44", HomeCAEndorsementsMultiAssetList.class, EndorsementHO44.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_48 = declare("HO-48", HomeCAEndorsementsMultiAssetList.class, EndorsementHO48.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_51 = declare("HO-51", HomeCAEndorsementsMultiAssetList.class, EndorsementHO51.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_57 = declare("HO-57", HomeCAEndorsementsMultiAssetList.class, EndorsementHO57.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_58 = declare("HO-58", HomeCAEndorsementsMultiAssetList.class, EndorsementHO58.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_58C = declare("HO-58C",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO58C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_59 = declare("HO-59", HomeCAEndorsementsMultiAssetList.class, EndorsementHO59.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_59C = declare("HO-59C",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO59C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_60 = declare("HO-60", HomeCAEndorsementsMultiAssetList.class, EndorsementHO60.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_60C = declare("HO-60C",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO60C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_61 = declare("HO-61", HomeCAEndorsementsMultiAssetList.class, EndorsementHO61.class, 
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_61C = declare("HO-61C", HomeCAEndorsementsMultiAssetList.class, EndorsementHO61C.class, 
				By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_61_1004 = declare("61 1004",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO611004.class,
		// By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_62_6501 = declare("HO 62 6501", HomeCAEndorsementsMultiAssetList.class, EndorsementHO626501.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_70 = declare("HO-70", HomeCAEndorsementsMultiAssetList.class, EndorsementHO70.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_71 = declare("HO-71", HomeCAEndorsementsMultiAssetList.class, EndorsementHO71.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_75 = declare("HO-75", HomeCAEndorsementsMultiAssetList.class, EndorsementHO75.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_76 = declare("HO-76", HomeCAEndorsementsMultiAssetList.class, EndorsementHO76.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_76C = declare("HO-76C",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHO76C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_77 = declare("HO-77", HomeCAEndorsementsMultiAssetList.class, EndorsementHO77.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_78 = declare("HO-78", HomeCAEndorsementsMultiAssetList.class, EndorsementHO78.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_79 = declare("HO-79", HomeCAEndorsementsMultiAssetList.class, EndorsementHO79.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_80 = declare("HO-80", HomeCAEndorsementsMultiAssetList.class, EndorsementHO80.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_81 = declare("HO-81", HomeCAEndorsementsMultiAssetList.class, EndorsementHO81.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_82 = declare("HO-82", HomeCAEndorsementsMultiAssetList.class, EndorsementHO82.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_90 = declare("HO-90", HomeCAEndorsementsMultiAssetList.class, EndorsementHO90.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HO_177 = declare("HO-177", HomeCAEndorsementsMultiAssetList.class, EndorsementHO177.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		//
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_00_08 = declare("HW 00 08", HomeCAEndorsementsMultiAssetList.class, EndorsementHW0008.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_04_35 = declare("HW 04 35", HomeCAEndorsementsMultiAssetList.class, EndorsementHW0435.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_04_61 = declare("HW 04 61", HomeCAEndorsementsMultiAssetList.class, EndorsementHW0461.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_04_95 = declare("HW 04 95", HomeCAEndorsementsMultiAssetList.class, EndorsementHW0495.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_05_28 = declare("HW 05 28", HomeCAEndorsementsMultiAssetList.class, EndorsementHW0528.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_09_06 = declare("HW 09 06", HomeCAEndorsementsMultiAssetList.class, EndorsementHW0906.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_09_34 = declare("HW 09 34", HomeCAEndorsementsMultiAssetList.class, EndorsementHW0934.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> FPCECA = declare("FPCECA", HomeCAEndorsementsMultiAssetList.class, EndorsementFPCECA.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> FPCECADP = declare("FPCECADP", HomeCAEndorsementsMultiAssetList.class, EndorsementFPCECADP.class, By
				.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_24_82 = declare("HW 24 82",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHW2482.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_28 = declare("HW-28",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHW28.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		// public static final AssetDescriptor<HomeCAEndorsementsMultiAssetList> HW_A6_00 = declare("HW A6 00",
		// HomeCAEndorsementsMultiAssetList.class, EndorsementHWA600.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

		public enum IncludedAndSelectedEndorsementsTblHeaders {

			FORM_ID("Form ID"), NAME("Name"), CATEGORY("Category"), NUMBER_OF_FORMS("Number of Forms"), DESCRIPTION("Description"), CONTROLS("");

			private String value;

			IncludedAndSelectedEndorsementsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum OptionalEndorsementsTblHeaders {

			FORM_ID("Form ID"), NAME("Name"), CATEGORY("Category"), DESCRIPTION("Description"), CONTROLS("");

			private String value;

			OptionalEndorsementsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public static final class EndorsementF1759C extends MetaData {
		}

		public static final class EndorsementHARI extends MetaData {
			public static final AssetDescriptor<ComboBox> NUMBER_OF_FAMILY_UNITS = declare("Number of family units", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SECTION_II_TERRITORY = declare("Section II territory", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO0410 extends MetaData {}

		public static final class EndorsementHO0455 extends MetaData {}
		
		public static final class EndorsementHO164 extends MetaData {
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HORSEPOWER = declare("Horsepower", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO210 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO42C extends MetaData {
			public static final AssetDescriptor<ComboBox> OFFICE_TYPE = declare("Office type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_BUSINESS_EQUIPMENT = declare("Description of business / equipment", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_EQUIPMENT_OVER_50_000 = declare("Business equipment over $50,000?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FOOT_TRAFFIC_EXCEEDING_2_CUSTOMERS_PER_WEEK = declare("Foot traffic exceeding 2 customers per week?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EMPLOYEES_WORKING_ON_THE_PREMISES = declare("Employees working on the premises?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_INVOLVING_HAZARDOUS_SITUATIONS_OR_MATERIALS = declare("Business involving hazardous situations or materials?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_INVOLVING_THE_MANUFACTURING_OR_REPAIRING_OF_GOODS_OR_PRODUCTS = declare(
					"Business involving the manufacturing or repairing of goods or products?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADD_A_COMMENT_IF_YOU_ANSWERED_YES_TO_ANY_OF_THE_ABOVE_QUESTIONS = declare("Add a comment if you answered yes to any of the above questions",
					TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO43C extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_BUSINESS_EQUIPMENT = declare("Description of business / equipment", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SECTION_II_TERRITORY = declare("Section II territory", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_EQUIPMENT_OVER_50_000 = declare("Business equipment over $50,000?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FOOT_TRAFFIC_EXCEEDING_2_CUSTOMERS_PER_WEEK = declare("Foot traffic exceeding 2 customers per week?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EMPLOYEES_WORKING_ON_THE_PREMISES = declare("Employees working on the premises?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_INVOLVING_HAZARDOUS_SITUATIONS_OR_MATERIALS = declare("Business involving hazardous situations or materials?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_INVOLVING_THE_MANUFACTURING_OR_REPAIRING_OF_GOODS_OR_PRODUCTS = declare(
					"Business involving the manufacturing or repairing of goods or products?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADD_A_COMMENT_IF_YOU_ANSWERED_YES_TO_ANY_OF_THE_ABOVE_QUESTIONS = declare("Add a comment if you answered yes to any of the above questions",
					TextBox.class, Waiters.AJAX);
		}
		
		public static final class EndorsementHO44 extends MetaData {}

		public static final class EndorsementHO70 extends MetaData {
			public static final AssetDescriptor<ComboBox> NUMBER_OF_FAMILY_UNITS = declare("Number of family units", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street Address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street Address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SECTION_II_TERRITORY = declare("Section II territory", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO71 extends MetaData {
			public static final AssetDescriptor<TextBox> NAME_OF_BUSINESS = declare("Name of business", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CLASSIFICATION_OCCUPATION = declare("Classification/occupation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CO_APPLICANT_CLASSIFICATION_OCCUPATION = declare("Co-applicant Classification/occupation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> TEACHERS_EXTENDED_LIABILITY = declare("Teachers extended liability", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_BUSINESS = declare("Description of Business", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THE_INSURED_SELF_EMPLOYED_A_PARTNER_IN_THE_BUSINESS = declare(
					"Is the insured self-employed, a partner in the business, or maintain any financial control in this business?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class EndorsementHO626501 extends MetaData {
			public static final AssetDescriptor<TextBox> NAME_OF_EVENT = declare("Name of Event", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> START_DATE = declare("Start Date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> END_DATE = declare("End Date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_EVENT = declare("Description of event", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO28 extends MetaData {
			public static final AssetDescriptor<TextBox> AMOUNT_OF_INSURANCE = declare("Amount of insurance", TextBox.class, Waiters.AJAX);
		}
		
		public static final class EndorsementHO29 extends MetaData {}

		public static final class EndorsementHW0906 extends MetaData {
			public static final AssetDescriptor<TextBox> MORTGAGE_COVERAGE_LIMIT_PER_MONTH_MAXIMUM = declare("Mortgage coverage limit (per month / maximum)", TextBox.class, Waiters.AJAX);
		}
		
		public static final class EndorsementHW0934 extends MetaData {}

		public static final class EndorsementHW0528 extends MetaData {
			public static final AssetDescriptor<ComboBox> LIMIT_OF_LIABILITY = declare("Limit of liability", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DEDUCTIBLE = declare("Deductible", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE_OR_MODEL = declare("Make or model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SERIAL_NUMBER = declare("Serial number", TextBox.class, Waiters.AJAX);
		}

		public static final class Endorsement611004 extends MetaData {
			public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO210C extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO164C extends MetaData {
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HORSEPOWER = declare("Horsepower", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDW0420 extends MetaData {
			public static final AssetDescriptor<ComboBox> ADDITIONAL_COVERAGE_LIMIT = declare("Additional coverage limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO300 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_EXCLUDED_STRUCTURES = declare("Description of excluded structures", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO40 extends MetaData {
		}

		public static final class EndorsementHO41 extends MetaData {
		}

		public static final class EndorsementDW0925 extends MetaData {
			public static final AssetDescriptor<TextBox> REASON_FOR_VACANCY = declare("Reason for vacancy", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LENGTH_OF_VACANCY = declare("Length of vacancy", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHW0495 extends MetaData {
			public static final AssetDescriptor<TextBox> COVERAGE_LIMIT = declare("Coverage Limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DEDUCTIBLE = declare("Deductible", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHW0435 extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LOCATION_TYPE = declare("Location Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DESCRIPTION_OF_STRUCTURE = declare("Description of structure", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class, Waiters.AJAX);
		}
		
		public static final class EndorsementHW0008 extends MetaData {}
		
		public static final class EndorsementHW0461 extends MetaData {}

		public static final class EndorsementHO48 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COVERAGE_LIMIT = declare("Coverage Limit", TextBox.class, Waiters.AJAX);
		}
		
		public static final class EndorsementHO51 extends MetaData {
			public static final AssetDescriptor<TextBox> COVERAGE_LIMIT = declare("Coverage limit", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO43 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_BUSINESS_EQUIPMENT = declare("Description of business/equipment", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SECTION_II_TERRITORY = declare("Section II territory", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSSINESS_EQUIPMENT_OVER_$5000 = declare("Business equipment over $50,000?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FOOT_TRAFIC_EXCEEDING_2_CUSTOMERS = declare("Foot traffic exceeding 2 customers per week?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EMPLOYEES_WORKING_ON_PREMISES = declare("Employees working on the premises?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSSINESS_INVOLVING_HAZZARDOUS_SITUATIONS =
					declare("Business involving hazardous situations or materials?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSSINESS_INVOLVING_THE_MANUFACTORING = declare("Business involving the manufacturing or repairing of goods or products?",
					RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_BUSINESS_CONDUCTED = declare("Is business conducted or is there equipment stored in a detached structure?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADD_COMMENT_IF_YOU_ANSWERED_YES = declare("Add a comment if you answered yes to any of the above questions", TextBox.class, Waiters.AJAX);

		}

		public static final class EndorsementHO42 extends MetaData {
			public static final AssetDescriptor<ComboBox> OFFICE_TYPE = declare("Office type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_BUSINESS_EQUIPMENT = declare("Description of business / equipment", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_EQUIPMENT_OVER_50_000 = declare("Business equipment over $50,000?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FOOT_TRAFFIC_EXCEEDING_2_CUSTOMERS_PER_WEEK = declare("Foot traffic exceeding 2 customers per week?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EMPLOYEES_WORKING_ON_THE_PREMISES = declare("Employees working on the premises?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_INVOLVING_HAZARDOUS_SITUATIONS_OR_MATERIALS = declare("Business involving hazardous situations or materials?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> BUSINESS_INVOLVING_THE_MANUFACTURING_OR_REPAIRING_OF_GOODS_OR_PRODUCTS = declare(
					"Business involving the manufacturing or repairing of goods or products?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_BUSINESS_CONDUCTED_OR_IS_THERE_EQUIPMENT_STORED = declare("Is business conducted or is there equipment stored in a detached structure?",
					RadioGroup.class, Waiters.AJAX);
		}

		public static final class EndorsementDP0422 extends MetaData {
			public static final AssetDescriptor<TextBox> COVERAGE_LIMIT = declare("Coverage limit", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDP0471 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage Limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDP0473 extends MetaData {}
		public static final class EndorsementDP0475 extends MetaData {}

		public static final class EndorsementDW0463 extends MetaData {
			public static final AssetDescriptor<ComboBox> COVERAGE_LIMIT = declare("Coverage limit", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDP0418 extends MetaData {
			public static final AssetDescriptor<TextBox> COVERAGE_LIMIT = declare("Structure coverage limit", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDW0421 extends MetaData {
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_STRUCTURE = declare("Description Of Structure", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit Of Liability", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementDP0495 extends MetaData {
			public static final AssetDescriptor<TextBox> COVERAGE_LIMIT = declare("Coverage limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DEDUCTIBLE = declare("Deductible", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO75 extends MetaData {
			public static final AssetDescriptor<ComboBox> BOAT_TYPE = declare("Boat type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HIN = declare("HIN#", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_ENGINE_MOTOR = declare("Description of engine/motor", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HORSEPOWER = declare("Horsepower", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LENGTH = declare("Length", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAXIMUM_SPEED = declare("Maximum speed", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementHO90 extends MetaData {}
		public static final class EndorsementHO57 extends MetaData {} 
		public static final class EndorsementHO58 extends MetaData {} 
		public static final class EndorsementHO59 extends MetaData {}		
		public static final class EndorsementHO60 extends MetaData {}
		public static final class EndorsementHO76 extends MetaData {}
		public static final class EndorsementHO77 extends MetaData {}
		public static final class EndorsementHO78 extends MetaData {}
		public static final class EndorsementHO79 extends MetaData {}
		public static final class EndorsementHO80 extends MetaData {}
		public static final class EndorsementHO81 extends MetaData {}
		public static final class EndorsementHO82 extends MetaData {}
		public static final class EndorsementHO177 extends MetaData {}
		
		public static final class EndorsementHO61C extends MetaData {}		
		public static final class EndorsementHO61 extends MetaData {}
		
		public static final class EndorsementHO1732 extends MetaData {}
		
		public static final class EndorsementHO1733 extends MetaData {}

		public static final class EndorsementFPCECA extends MetaData {
			public static final AssetDescriptor<AssetListConfirmationDialog> CONFIRM_OBJECT_ADDITION = declare("Confirm Object Addition", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.xpath("//div[@id='aaaEndorsementPopUpForm:fpcecaNotificationPopup_container']"));
		}

		public static final class EndorsementFPCECADP extends MetaData {
			public static final AssetDescriptor<AssetListConfirmationDialog> CONFIRM_OBJECT_ADDITION = declare("Confirm Object Addition", AssetListConfirmationDialog.class, Waiters.AJAX, false, By
					.xpath("//div[@id='aaaEndorsementPopUpForm:fpcecaNotificationPopup_container']"));
		}

		public static final class EndorsementDL2482 extends MetaData {}
	}

	public static final class PersonalPropertyTab extends MetaData {
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> BOATS = declare("Boats", PersonalPropertyMultiAssetList.class, Boats.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyBoatsItem'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> CAMERAS = declare("Cameras", PersonalPropertyMultiAssetList.class, Cameras.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyCamerasItem' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertyCamerasItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> COINS = declare("Coins", PersonalPropertyMultiAssetList.class, Coins.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyCoinsItem' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertyCoinsItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> FINE_ARTS = declare("Fine arts", PersonalPropertyMultiAssetList.class, FineArts.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyFineArtsItem' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertyFineArtsItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> FIREARMS = declare("Firearms", PersonalPropertyMultiAssetList.class, Firearms.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyFirearmsItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> FURS = declare("Furs", PersonalPropertyMultiAssetList.class, Furs.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyFursItem' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertyFursItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> GOLF_EQUIPMENT = declare("Golf equipment", PersonalPropertyMultiAssetList.class, GolfEquipment.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyGolfItem' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertyGolfItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> JEWELRY = declare("Jewelry", PersonalPropertyMultiAssetList.class, Jewelry.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyJewelryItemHO3HO4' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertyJewelryItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> MUSICAL_INSTRUMENTS = declare("Musical instruments", PersonalPropertyMultiAssetList.class, MusicalInstruments.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyMusicalItem' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertyMusicalItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> POSTAGE_STAMPS = declare("Postage stamps", PersonalPropertyMultiAssetList.class, PostageStamps.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyPostageItem' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertyPostageItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> SILVERWARE = declare("Silverware", PersonalPropertyMultiAssetList.class, Silverware.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertySilverwareItem' "
						+ "or @id='policyDataGatherForm:componentRegion_ScheduledPropertySilverwareItemHO6'][node()]"));
		public static final AssetDescriptor<PersonalPropertyMultiAssetList> TRADING_CARDS_OR_COMICS = declare("Trading cards or comics", PersonalPropertyMultiAssetList.class, TradingCardsOrComics.class, By
				.xpath("//span[@id='policyDataGatherForm:componentRegion_ScheduledPropertyTradingCardsItemHO6'][node()]"));

		public static final class Boats extends MetaData {
			public static final AssetDescriptor<ComboBox> BOAT_TYPE = declare("Boat type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HIN_NO = declare("HIN#", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HORSEPOWER = declare("Horsepower", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LENGTH_INCHES = declare("Length(inches)", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DEDUCTIBLE = declare("Deductible", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> AMOUNT_OF_INSURANCE = declare("Amount of insurance", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> LOSS_PAYEE = declare("Loss payee", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
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

		public static final class Furs extends MetaData {
			public static final AssetDescriptor<TextBox> LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}
		
		public static final class Firearms extends MetaData {
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
			public static final AssetDescriptor<RadioGroup> CERTIFICATE_OF_AUTHENTICITY_RECEIIVED = declare("Certificate of authenticity received", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		}

		// TODO-dchubkov: old metadata, should we remove it?
		/*
		 * public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class); public
		 * static final AssetDescriptor<RadioGroup> SCHEDULED_PROPERTY_COVERAGE = declare("Scheduled Property Coverage",
		 * RadioGroup.class); public static final AssetDescriptor<TextBox> LIMIT_AMOUNT = declare("Limit Amount",
		 * TextBox.class); public static final AssetDescriptor<TextBox> PAYMENT_BASIS = declare("Payment Basis",
		 * TextBox.class); public static final AssetDescriptor<TextBox> DEDUCTIBLE_AMOUNT = declare("Deductible Amount",
		 * TextBox.class); public static final AssetDescriptor<TextBox> TAXES = declare("Taxes", TextBox.class); public
		 * static final AssetDescriptor<TextBox> FEES = declare("Fees", TextBox.class); public static final
		 * AssetDescriptor<TextBox> TERM_PREMIUM = declare("Term Premium", TextBox.class); public static final
		 * AssetDescriptor<TextBox> BILLABLE_PREMIUM = declare("Billable Premium", TextBox.class);
		 *
		 * public static final class JewelryTab extends MetaData { public static final AssetDescriptor<TextBox> JEWELRY
		 * = declare("Jewelry", TextBox.class); public static final AssetDescriptor<TextBox> ITEM_NUMBER =
		 * declare("Item Number", TextBox.class); public static final AssetDescriptor<TextBox> AMOUNT =
		 * declare("Amount", TextBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION =
		 * declare("Description", TextBox.class); public static final AssetDescriptor<TextBox> OF_PICTURE =
		 * declare("# of Picture", TextBox.class); }
		 *
		 * public static final class FineArtsTab extends MetaData { public static final AssetDescriptor<TextBox>
		 * FINE_ARTS = declare("Fine Arts", TextBox.class); public static final AssetDescriptor<TextBox> ITEM_NUMBER =
		 * declare("Item Number", TextBox.class); public static final AssetDescriptor<TextBox> AMOUNT =
		 * declare("Amount", TextBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION =
		 * declare("Description", TextBox.class); public static final AssetDescriptor<TextBox> CREATOR_AUTHOR =
		 * declare("Creator(Author)", TextBox.class); public static final AssetDescriptor<TextBox> TITLE =
		 * declare("Title", TextBox.class); public static final AssetDescriptor<TextBox> DIMENSION =
		 * declare("Dimension", TextBox.class); public static final AssetDescriptor<TextBox> YEAR = declare("Year",
		 * TextBox.class); public static final AssetDescriptor<TextBox> TECHNIQUE = declare("Technique", TextBox.class);
		 * }
		 *
		 * public static final class PortableElectronicEquipmentTab extends MetaData { public static final
		 * AssetDescriptor<TextBox> PORTABLEELECTRONICEQUIPMENT = declare("PortableElectronicEquipment", TextBox.class);
		 * public static final AssetDescriptor<TextBox> ITEM_NUMBER = declare("Item Number", TextBox.class); public
		 * static final AssetDescriptor<TextBox> AMOUNT = declare("Amount", TextBox.class); public static final
		 * AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class); }
		 *
		 * public static final class SportsEquipmentTab extends MetaData { public static final AssetDescriptor<TextBox>
		 * SPORTSEQUIPMENT = declare("SportsEquipment", TextBox.class); public static final AssetDescriptor<TextBox>
		 * ITEM_NUMBER = declare("Item Number", TextBox.class); public static final AssetDescriptor<TextBox> AMOUNT =
		 * declare("Amount", TextBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION =
		 * declare("Description", TextBox.class); }
		 *
		 * public static final class MoneyTab extends MetaData { public static final AssetDescriptor<TextBox>
		 * CLASS_LIMIT = declare("Class Limit", TextBox.class); public static final AssetDescriptor<TextBox> ITEM_NUMBER
		 * = declare("Item Number", TextBox.class); public static final AssetDescriptor<TextBox> AMOUNT =
		 * declare("Amount", TextBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION =
		 * declare("Description", TextBox.class); }
		 *
		 * public static final class SecuritiesTab extends MetaData { public static final AssetDescriptor<TextBox>
		 * SECURITIES = declare("Securities", TextBox.class); public static final AssetDescriptor<TextBox> ITEM_NUMBER =
		 * declare("Item Number", TextBox.class); public static final AssetDescriptor<TextBox> AMOUNT =
		 * declare("Amount", TextBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION =
		 * declare("Description", TextBox.class); }
		 *
		 * public static final class FursTab extends MetaData { public static final AssetDescriptor<TextBox> FURS =
		 * declare("Furs", TextBox.class); public static final AssetDescriptor<TextBox> ITEM_NUMBER =
		 * declare("Item Number", TextBox.class); public static final AssetDescriptor<TextBox> AMOUNT =
		 * declare("Amount", TextBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION =
		 * declare("Description", TextBox.class); public static final AssetDescriptor<TextBox> NUMBER_OF_PICTURE =
		 * declare("# of Picture", TextBox.class); }
		 *
		 * public static final class FirearmsTab extends MetaData { public static final AssetDescriptor<TextBox>
		 * CLASS_LIMIT = declare("Class Limit", TextBox.class); public static final AssetDescriptor<TextBox> ITEM_NUMBER
		 * = declare("Item Number", TextBox.class); public static final AssetDescriptor<TextBox> AMOUNT =
		 * declare("Amount", TextBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION =
		 * declare("Description", TextBox.class); }
		 *
		 * public static final class SilverwareTab extends MetaData { public static final AssetDescriptor<TextBox>
		 * SILVERWARE = declare("Silverware", TextBox.class); public static final AssetDescriptor<TextBox> ITEM_NUMBER =
		 * declare("Item Number", TextBox.class); public static final AssetDescriptor<TextBox> AMOUNT =
		 * declare("Amount", TextBox.class); public static final AssetDescriptor<TextBox> DESCRIPTION =
		 * declare("Description", TextBox.class); }
		 */
	}

	// --------- Premium & Coverages - Quote Tab --------
	public static final class PremiumsAndCoveragesQuoteTab extends MetaData {
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN_AT_RENEWAL = declare("Payment plan at renewal", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> RECURRING_PAYMENT = declare("Recurring Payment", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> BILL_TO_AT_RENEWAL = declare("Bill to at renewal", ComboBox.class, Waiters.AJAX);

		public static final AssetDescriptor<StaticElement> COVERAGE_A = declare(HomeCaCoverages.COVERAGE_A.get(), StaticElement.class, Waiters.AJAX, false, By.xpath(String.format(
				"//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//td[3]", HomeCaCoverages.COVERAGE_A.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_B = declare(HomeCaCoverages.COVERAGE_B.get(), ComboBox.class, Waiters.AJAX, false, By.xpath(String.format(
				"//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//td[3]", HomeCaCoverages.COVERAGE_B.get())));
		public static final AssetDescriptor<TextBox> COVERAGE_C = declare(HomeCaCoverages.COVERAGE_C.get(), TextBox.class, Waiters.AJAX, false, By.xpath(String.format(
				"//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//input", HomeCaCoverages.COVERAGE_C.get())));
		public static final AssetDescriptor<TextBox> COVERAGE_D = declare(HomeCaCoverages.COVERAGE_D.get(), TextBox.class, Waiters.AJAX, false, By.xpath(String.format(
				"//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//input", HomeCaCoverages.COVERAGE_D.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_E = declare(HomeCaCoverages.COVERAGE_E.get(), ComboBox.class, Waiters.AJAX, false, By.xpath(String.format(
				"//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCoverages.COVERAGE_E.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_F = declare(HomeCaCoverages.COVERAGE_F.get(), ComboBox.class, Waiters.AJAX, false, By.xpath(String.format(
				"//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCoverages.COVERAGE_F.get())));
		public static final AssetDescriptor<ComboBox> DEDUCTIBLE = declare(HomeCaCoverages.DEDUCTIBLE.get(), ComboBox.class, Waiters.AJAX, false, By.xpath(String.format(
				"//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCoverages.DEDUCTIBLE.get())));

		/*
		 * public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX); public
		 * static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX); public static
		 * final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare( "Street address 1", TextBox.class, Waiters.AJAX);
		 * public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class,
		 * Waiters.AJAX); public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
		 * public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		 */
		public static final AssetDescriptor<RadioGroup> OPTIONAL_COVERAGE_MASONRY_VENEER = declare("Optional coverage - Masonry veneer", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> OPTIONAL_COVERAGE_BREAKAGE_OF_PERSONAL_PROPERTY = declare("Optional coverage - Breakage of personal property", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<JavaScriptButton> CALCULATE_PREMIUM_BUTTON =
				declare("Calculate Premium", JavaScriptButton.class, Waiters.AJAX, false, By.id("policyDataGatherForm:premiumRecalcCov"));
		public static final AssetDescriptor<RadioGroup> ACCEPT_CEA_OFFER = declare("Accept CEA offer", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> APPLY_CEA_DISCOUNT = declare("Apply CEA Discount", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CEA_PRODUCT = declare("CEA Product", ComboBox.class, Waiters.AJAX);

		public static final AssetDescriptor<FillableTable> INSTALLMENT_FEES_DETAILS_TABLE = declare("InstallemntFeesDetails",  FillableTable.class, ListOfFeeDetailsRow.class, By.id("policyDataGatherForm:installmentFeeDetailsTable"));

		public static final class ListOfFeeDetailsRow extends MetaData {
			public static final AssetDescriptor<StaticElement> PAYMENT_METHOD = declare("Payment Method", StaticElement.class);
			public static final AssetDescriptor<StaticElement> ENROLLED_IN_AUTO_PAY = declare("Enrolled in Auto Pay", StaticElement.class);
			public static final AssetDescriptor<StaticElement> INSTALLMENT_FEE = declare("Installment Fee", StaticElement.class);
		}

		public enum HomeCaCoverages {
			COVERAGE_A("Coverage A - Dwelling limit"), COVERAGE_B("Coverage B - Other Structures limit"), COVERAGE_C("Coverage C - Personal Property limit"), COVERAGE_D(
					"Coverage D - Loss of Use limit"), COVERAGE_E("Coverage E - Personal Liability Each Occurrence"), COVERAGE_F("Coverage F - Medical Payment to Others"), DEDUCTIBLE("Deductible"),;

			private String value;

			HomeCaCoverages(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum HomeCaCeaCoverages {
			COVERAGE_A_DWELLING_LIMIT("Coverage A - Dwelling limit"), COVERAGE_A_DEDUCTIBLE("Coverage A - Deductible"), COVERAGE_C("Coverage C - Personal Property limit"), COVERAGE_D(
					"Coverage D - Loss of Use limit"), ADDITIONAL("Additional Limited Building Code Upgrade");

			private String value;

			HomeCaCeaCoverages(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public static final class CeaCoverages extends MetaData {
			public static final AssetDescriptor<StaticElement> COVERAGE_A_DWELLING_LIMIT = declare(HomeCaCeaCoverages.COVERAGE_A_DWELLING_LIMIT.get(), StaticElement.class, Waiters.AJAX, true, By
					.xpath(String.format("//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//td[3]", HomeCaCeaCoverages.COVERAGE_A_DWELLING_LIMIT.get())));
			public static final AssetDescriptor<ComboBox> COVERAGE_A_DEDUCTIBLE = declare(HomeCaCeaCoverages.COVERAGE_A_DEDUCTIBLE.get(), ComboBox.class, Waiters.AJAX, true, By.xpath(String.format(
					"//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_A_DEDUCTIBLE.get())));
			public static final AssetDescriptor<ComboBox> COVERAGE_C = declare(HomeCaCeaCoverages.COVERAGE_C.get(), ComboBox.class, Waiters.AJAX, true, By.xpath(String.format(
					"//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_C.get())));
			public static final AssetDescriptor<ComboBox> COVERAGE_D = declare(HomeCaCeaCoverages.COVERAGE_D.get(), ComboBox.class, Waiters.AJAX, true, By.xpath(String.format(
					"//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_D.get())));
			public static final AssetDescriptor<ComboBox> ADDITIONAL = declare(HomeCaCeaCoverages.ADDITIONAL.get(), ComboBox.class, Waiters.AJAX, true, By.xpath(String.format(
					"//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.ADDITIONAL.get())));
		}
	}

	public static final class MortgageesTab extends MetaData {

		public static final AssetDescriptor<RadioGroup> MORTGAGEE = declare("Mortgagee", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> MORTGAGEE_INFORMATION = declare("MortgageeInformation", MultiInstanceAfterAssetList.class, MortgageeInformation.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOMortgageeInfo']"));
		public static final AssetDescriptor<RadioGroup> USE_LEGAL_NAMED_INSURED = declare("Use legal named insured", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> LEGAL_NAMED_INSURED = declare("Legal named insured", TextBox.class, Waiters.AJAX, false, By
				.xpath("//textarea[@id='policyDataGatherForm:aaaLegalName']"));
		public static final AssetDescriptor<RadioGroup> USE_LEGAL_PROPERTY_ADDRESS = declare("Use legal property address", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetList> LEGAL_PROPERTY_ADDRESS = declare("LegalPropetyAddress", AssetList.class, LegalPropetyAddress.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOLegalPropAddressComp']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ADDITIONA_INSURED = declare("Is there an additional insured?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_INSURED = declare("AdditionalInsured", MultiInstanceAfterAssetList.class, AdditionalInsured.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOAdditionalInsured']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ADDITIONA_INTEREST = declare("Is there an additional interest?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_INTEREST = declare("AdditionalInterest", MultiInstanceAfterAssetList.class, AdditionalInterest.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOAdditionalInterest']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ANY_THIRD_PARTY_DESIGNEE = declare("Is there any third party designee?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetList> THIRD_PARTY_DESIGNEE = declare("ThirdPartyDesignee", AssetList.class, ThirdPartyDesignee.class, By
				.xpath("//div[@id='policyDataGatherForm:componentView_AAAThirdPartyDesignee']"));

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
			public static final AssetDescriptor<RadioGroup> SAME_AS_INSURED_S_MAILING_ADDRESS =
					declare("Same as insured mailing address?", RadioGroup.class, Waiters.AJAX, By.id("policyDataGatherForm:sedit_AAAHOAdditionalInsured_differentAddressInd"));
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
	}

	public static final class UnderwritingAndApprovalTab extends MetaData {
		public static final AssetDescriptor<ComboBox> UNDERWRITER_SELECTED_INSPECTION_TYPE = declare("Underwriter-selected inspection type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> INTERIOR_INSPECTION = declare("Interior inspection", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> EXTERIOR_INSPECTION = declare("Exterior inspection", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> HIGH_VALUE_INTERIOR_INSPECTION = declare("High Value Interior inspection", CheckBox.class, Waiters.AJAX);

		public static final AssetDescriptor<RadioGroup> HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_CREATURE = declare(
				"Have any of the applicant(s)’ current pets injured, intentionally or unintentionally, another creature or person?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED_IN_THE_PAST_3_YEARS = declare(
				"Have any applicants had a prior insurance policy cancelled, refused, or non-renewed in the past 3 years?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> HAS_THE_PROPERTY_BEEN_IN_FORECLOSURE_PROCEEDINGS_WITHIN_THE_PAST_18_MONTHS = declare(
				"Has the property been in foreclosure proceedings within the past 18 months?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_GAS_WATER_HEATER_LOCATED_IN_A_GARAGE = declare("Is any gas water heater located in a garage?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ARE_ALL_WATER_HEATERS_STRAPPED_TO_THE_WALL = declare(
				"Are all water heaters strapped to the wall, or, if located in the garage, raised at least 18 inches from the floor?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DO_EMPLOYEES_OF_ANY_RESIDENT_OR_APPLICANT_RESIDE_IN_THE_DWELLING = declare("Do employees of any resident or applicant reside in the dwelling?",
				RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> TOTAL_NUMBER_OF_PART_TIME_AND_FULL_TIME_RESIDENT_EMPLOYEES = declare("Total number of part time and full time resident employees", TextBox.class,
				Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare(
				"Is any business, home day care or farming activity conducted on the premises?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_THERE_A_BUSINESS_ON_PREMISES = declare("Is there a business on premises?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DAY_CARE = declare("Day Care?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_THE_DWELLING_LOCATED_WITHIN_500_FEET_OF_BAY_OR_COASTAL_WATERS = declare("Is the dwelling located within 500 feet of bay or coastal waters?",
				RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DO_YOU_HAVE_A_LICENSE = declare("Do you have a license?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> FARMING_RANCHING = declare("Farming/Ranching?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_IT_A_FOR_PROFIT_BUSINESS = declare("Is it a for-profit business?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> INCIDENTAL_BUSINESS_OCCUPANCY = declare("Incidental Business Occupancy?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> HOW_MANY_CUSTOMERS_VISIT_THE_RESIDENCE_PREMISES_PER_WEEK = declare("How many customers visit the residence premises per week? ", TextBox.class,
				Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> OTHERS = declare("Others?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> HAVE_ANY_APPLICANTS_HAD_A_FLAT_ROOF_RELATED_CLAIM_IN_THE_PAST_3_YEARS = declare(
				"Have any applicants had a flat roof related claim in the past 3 years?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> REMARKS = declare("Remarks", TextBox.class, Waiters.AJAX);

		public static final AssetDescriptor<TextBox> REMARK_PRIOR_INSURANCE = declare("Remark Prior Insurance", TextBox.class, Waiters.AJAX, false, By
				.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaPriorPolicyStatusRemark']"));
		public static final AssetDescriptor<TextBox> REMARK_FIRE_HAZARD = declare("Remark Fire Hazard", TextBox.class, Waiters.AJAX, false, By
				.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaFireSafeDistRemark']"));
		public static final AssetDescriptor<TextBox> REMARK_DWELL_SLOPE = declare("Remark Dwell Slope", TextBox.class, Waiters.AJAX, false, By
				.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaDwellSlopeRemark']"));
		public static final AssetDescriptor<TextBox> REMARK_FORECLOSURE = declare("Remark Foreclosure", TextBox.class, Waiters.AJAX, false, By
				.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaForeClosureProceedingRemark']"));
		public static final AssetDescriptor<TextBox> REMARK_RENOVATION = declare("Remark Renovation", TextBox.class, Waiters.AJAX, false, By
				.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaDwellRenovationRemark']"));
		public static final AssetDescriptor<TextBox> REMARK_RESIDENT_EMPLOYEES = declare("Remark Resident Employees", TextBox.class, Waiters.AJAX, false, By
				.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaEmployeesGradeRemark']"));
	}

	public static final class DocumentsTab extends MetaData {
		public static final AssetDescriptor<AssetList> DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODocGenPrint']"));
		public static final AssetDescriptor<AssetList> DOCUMENTS_TO_BIND = declare("DocumentsToBind", AssetList.class, DocumentsToBind.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOBindDocuments']"));
		public static final AssetDescriptor<AssetList> DOCUMENTS_TO_ISSUE = declare("DocumentsToIssue", AssetList.class, DocumentsToIssue.class, By
				.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOIssueDocuments']"));

		public static final class DocumentsForPrinting extends MetaData {
			public static final AssetDescriptor<RadioGroup> CONSUMER_INFORMATION_NOTICE = declare("Consumer Information Notice", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FAX_MEMORANDUM = declare("Fax Memorandum", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> HOMEOWNERS_INSURANCE_QUOTE_PAGE = declare("Homeowners Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CALIFORNIA_APPLICATION_FOR_UNIT_OWNERS = declare("California Application for Unit-Owners Insurance", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> OFFER_OF_EARTHQUAKE_COVERAGE = declare("Offer of Earthquake Coverage Condominium Basic Earthquake Policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PRIVACY_INFORMATION_NOTICE = declare("Privacy Information Notice", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CONDOMINIUM_OWNERS_INSURANCE_QUOTE_PAGE = declare("Condominium Owners Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> APPLICATION_FOR_HOMEOWNERS_INSURANCE = declare("Application for Homeowners Insurance", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> APPRAISALS_RECEIPTS_FOR_SCHEDULED_PERSONAL_PROPERTY = declare("Appraisals/sales receipts for scheduled personal property items",
					RadioGroup.class, Waiters.AJAX);
		}

		public static final class DocumentsToBind extends MetaData {
			public static final AssetDescriptor<RadioGroup> PROOF_OF_UNDERLYING_INSURANCE_POLICY = declare("Proof of Underlying Insurance policy for the tenant(s) received?", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_CENTRAL_FIRE_ALARM = declare("Proof of central fire alarm", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_CENTRAL_THEFT_ALARM = declare("Proof of central theft alarm", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_OCCUPATION = declare("Proof of occupation", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT = declare("Proof of subscription to fire department", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION = declare("Proof of home renovations for the Home Modernization discount", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_ENERGY_STAR_APPLIANCES = declare("Proof of ENERGY STAR appliances or green home features", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_COMPANION_DP3_POLICY = declare("Proof of companion DP3 policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_COMPANION_PUP_POLICY = declare("Proof of companion PUP policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_ACTIVE_AAA_AUTO_POLICY = declare("Proof of active AAA auto policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CERTIFICATE_CONFIRMING_NO_CONTAMINATION_CAUSED_BY_STORAGE_TANK = declare(
					"Certificate confirming no contamination caused by inactive underground oil storage tank", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_PLUMBING_AND_OTHERS_RENOVATIONS = declare("Proof of plumbing, electrical, heating/cooling system and roof renovations",
					RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_AN_UNDERLYING_RENTERS_INSURANCE = declare("Proof of an underlying renters insurance policy for your tenant(s)", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ACTUAL_CASH_VALUE_LOSS_TO_ROOF_SURFACING = declare("Actual Cash Value Loss Settlement Windstorm Or Hail Losses To Roof Surfacing",
					RadioGroup.class, Waiters.AJAX);
		}

		public static final class DocumentsToIssue extends MetaData {
			public static final AssetDescriptor<RadioGroup> CALIFORNIA_RESIDENTIAL_PROPERTY_INSURANCE_DISCLOSURE = declare("California Residential Property Insurance Disclosure", RadioGroup.class,
					Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FAIR_PLAN_COMPANION_ENDORSEMENT_CALIFORNIA = declare("FAIR Plan Companion Endorsement - California", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNED_POLICY_APPLICATION = declare("Signed policy application", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AUTOPAY_AUTHORIZATION_FORM = declare("AutoPay Authorization Form", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FPCECA = declare("FAIR Plan Companion Endorsement - California", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FPCECADP = declare("FAIR Plan Companion Endorsement - California", RadioGroup.class, Waiters.AJAX);
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

	// policy Actions
	public static final class ExtensionRenewalActionTab extends MetaData {
	}

	public static final class CancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.AJAX);
	}

	public static final class RescindCancellationActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> RESCIND_CANCELLATION_DATE = declare("Rescind Cancellation Date", TextBox.class);
	}

	public static final class ChangeReinstatementLapsePeriodActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> REINSTATEMENT_LAPSE_DATE = declare("Reinstatement Lapse Date", TextBox.class);
		public static final AssetDescriptor<TextBox> REVISED_REINSTATEMENT_DATE = declare("Revised Reinstatement Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> LAPSE_CHANGE_REASON = declare("Lapse Change Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class);
	}

	public static final class ChangeBrokerActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TRANSFER_ID = declare("Transfer ID", TextBox.class);
		public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class);
		public static final AssetDescriptor<TextBox> TRANSFER_TYPE = declare("Transfer type", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER_REASON = declare("Other Reason", TextBox.class);
		public static final AssetDescriptor<TextBox> TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", TextBox.class);
		public static final AssetDescriptor<TextBox> POLICY_EFFECTIVE_DATE = declare("Policy Effective Date", TextBox.class);
		public static final AssetDescriptor<RadioGroup> COMMISSION_IMPACT = declare("Commission impact", RadioGroup.class);
		public static final AssetDescriptor<TextBox> SOURCE_CHANNEL = declare("Source Channel", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_LOCATION_TYPE = declare("Source Location Type", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_LOCATION_NAME = declare("Source Location Name", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_INSURANCE_AGENT = declare("Source Insurance Agent", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_CHANNEL = declare("Target channel", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_LOCATION_TYPE = declare("Target location type", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_LOCATION_NAME = declare("Target Location Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> TARGET_INSURANCE_AGENT = declare("Target Insurance agent", ComboBox.class);
		public static final AssetDescriptor<TextBox> TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
	}

	public static final class ReinstatementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> REINSTATE_DATE = declare("Reinstate date", TextBox.class, Waiters.AJAX);
	}

	public static final class DeclineActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class GenerateProposalActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
	}

	public static final class ChangeRenewalLapseActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		public static final AssetDescriptor<TextBox> RENEWAL_LAPSE_DATE = declare("Renewal Lapse Date", TextBox.class);
		public static final AssetDescriptor<TextBox> REVISED_RENEWAL_DATE = declare("Revised Renewal Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> LAPSE_CHANGE_REASON = declare("Lapse Change Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class);
	}

	public static final class AddManualRenewFlagActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	public static final class DoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> SUPPORTING_DATA = declare("Supporting Data", TextBox.class);
	}

	public static final class CancelActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
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
	}

	public static final class CopyPolicyActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class DeleteCancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	public static final class CopyQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class Inspection2ndTabWizardStyleActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE_ORDERED = declare("Date Ordered", TextBox.class);
		public static final AssetDescriptor<ComboBox> REPORT_TYPE = declare("Report Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> INSPECTION_REASON = declare("Inspection Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> PRIMARY_CONTACT_NAME = declare("Primary Contact Name", TextBox.class);
		public static final AssetDescriptor<TextBox> PRIMARY_CONTACT_PHONE = declare("Primary Contact Phone", TextBox.class);
		public static final AssetDescriptor<TextBox> SECONDARY_CONTACT_NAME = declare("Secondary Contact Name", TextBox.class);
		public static final AssetDescriptor<TextBox> SECONDARY_CONTACT_PHONE = declare("Secondary Contact Phone", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENT_CONTACT_NAME = declare("Agent Contact Name", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENT_CONTACT_PHONE = declare("Agent Contact Phone", TextBox.class);
		public static final AssetDescriptor<TextBox> AGENT_CONTACT_E_MAIL = declare("Agent Contact E-mail", TextBox.class);
		public static final AssetDescriptor<TextBox> UNDERWRITER_CONTACT_NAME = declare("Underwriter Contact Name", TextBox.class);
		public static final AssetDescriptor<TextBox> UNDERWRITER_CONTACT_PHONE = declare("Underwriter Contact Phone", TextBox.class);
		public static final AssetDescriptor<TextBox> UNDERWRITER_CONTACT_E_MAIL = declare("Underwriter Contact E-mail", TextBox.class);
		public static final AssetDescriptor<TextBox> SPECIAL_INSTRUCTIONS = declare("Special Instructions", TextBox.class);
		public static final AssetDescriptor<TextBox> INSPECTION_STATUS = declare("Inspection Status", TextBox.class);
	}

	public static final class IssueSummary3rdTabWizardStyleActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ISSUE_DATE = declare("Issue Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
		public static final AssetDescriptor<ComboBox> SEND_TO = declare("Send To", ComboBox.class);
		public static final AssetDescriptor<TextBox> BROKER_EMAIL = declare("Broker Email", TextBox.class);
		public static final AssetDescriptor<TextBox> INSURED_EMAIL = declare("Insured Email", TextBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip/Postal Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
	}

	public static final class DeletePendedTransactionActionTab extends MetaData {
	}

	public static final class RewriteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
	}

	public static final class RemoveDoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class RemoveManualRenewFlagActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	public static final class PolicyDocGenActionTab extends MetaData {
		public static final AssetDescriptor<FillableDocumentsTable> ON_DEMAND_DOCUMENTS =
				declare("OnDemandDocuments", FillableDocumentsTable.class, DocumentRow.class, By.xpath("(//div[@id='policyDataGatherForm:componentView_AAAHODocGen']//table)[1]"));
		public static final AssetDescriptor<AdvancedRadioGroup> DELIVERY_METHOD =
				declare("Delivery Method", AdvancedRadioGroup.class, Waiters.AJAX, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHODocGen_body']/table"));
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class, Waiters.AJAX);

		public static final class DocumentRow extends MetaData {
			public static final AssetDescriptor<CheckBox> SELECT = declare(DocGenConstants.OnDemandDocumentsTable.SELECT, CheckBox.class);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NUMBER = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, StaticElement.class);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NAME = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, StaticElement.class);

			// 60 5005
			public static final AssetDescriptor<ListBox> REASON_605005 =
					declare("Reason605005", ListBox.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:returnPaymentGrid_605005']//select"));

			// Fields of HSU03XX
			public static final AssetDescriptor<CheckBox> DECISION_BASED_ON_CLUE_HSU03 =
					declare("Decision based on CLUE HSU03", CheckBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:hsu03CIN:0']"));

			// Fields of HSU05XX
			public static final AssetDescriptor<TextBox> FIRST_NAME_HSU05 = declare("First name", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:firstName_HSU05']"));
			public static final AssetDescriptor<TextBox> LAST_NAME_HSU05 = declare("Last name", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:lastName_HSU05']"));
			public static final AssetDescriptor<TextBox> ZIP_CODE_HSU05 = declare("Zip code", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:zipCode_HSU05']"));
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1_HSU05 =
					declare("Street address 1", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:address1_HSU05']"));
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2_HSU05 =
					declare("Street address 2", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:address2Show_HSU05']"));
			public static final AssetDescriptor<TextBox> City_HSU05 = declare("City", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:city_HSU05']"));
			public static final AssetDescriptor<ComboBox> STATE_HSU05 = declare("State", ComboBox.class, Waiters.AJAX, false, By.xpath("//span[@id='policyDataGatherForm:stateShow_HSU05']/select"));
			public static final AssetDescriptor<TextBox> DESCRIPTION_HSU05 = declare("Description", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:uwMsg_HSU05']"));

			// Fields of HSU07XX
			public static final AssetDescriptor<CheckBox> DECISION_BASED_ON_CLUE_HSU07 =
					declare("Decision based on CLUE HSU07", CheckBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:hsu07CIN:0']"));

			// Fields of HSU09XX
			public static final AssetDescriptor<CheckBox> DECISION_BASED_ON_CLUE_HSU09 =
					declare("Decision based on CLUE HSU09", CheckBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:hsu09CIN:0']"));

		}
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
