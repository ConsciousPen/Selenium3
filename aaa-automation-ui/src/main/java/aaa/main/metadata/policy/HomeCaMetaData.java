/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.metadata.policy;

import org.openqa.selenium.By;
import aaa.main.metadata.DialogsMetaData;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
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
public final class HomeCaMetaData {

	// --------- General Tab -------- 
	public static final class GeneralTab extends MetaData {

		public static final AttributeDescriptor POLICY_INFO = declare("PolicyInfo", AssetList.class, PolicyInfo.class);
		public static final AttributeDescriptor CURRENT_CARRIER = declare("CurrentCarrier", AssetList.class, CurrentCarrier.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOPolicyPriorCarrier']"));

		// PolicyInfo
		public static final class PolicyInfo extends MetaData {
			public static final AttributeDescriptor STATE = declare("State", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SOURCE_POLICY_NUM = declare("Source Policy #", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor APPLICATION_TYPE = declare("Application Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PRIOR_BASE_YEAR = declare("Prior base year", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COMMISSION_TYPE = declare("Commission Type", ComboBox.class);
			public static final AttributeDescriptor SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
			public static final AttributeDescriptor LEAD_SOURCE = declare("Lead source", ComboBox.class);
			public static final AttributeDescriptor CALVET = declare("CalVet", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ADVERSELY_IMPACTED = declare("Adversely Impacted", ComboBox.class);
		}

		// Current Carrier
		public static final class CurrentCarrier extends MetaData {
			public static final AttributeDescriptor CARRIER_NAME = declare("Carrier name", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor POLICY_TYPE = declare("Policy type", ComboBox.class);
			public static final AttributeDescriptor CONTINUOUS_YEARS_WITH_HO_INSURANCE = declare("Continuous years with HO insurance", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor BASE_DATE_WITH_AAA = declare("Base date with AAA", TextBox.class, Waiters.AJAX);
		}
	}

	// --------- Applicant Tab -------- 
	public static final class ApplicantTab extends MetaData {

		public static final AttributeDescriptor NAMED_INSURED = declare("NamedInsured", MultiInstanceAfterAssetList.class, NamedInsured.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHONamedInsured']"));
		public static final AttributeDescriptor AAA_MEMBERSHIP = declare("AAAMembership", AssetList.class, AAAMembership.class, By.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOMembership']"));
		public static final AttributeDescriptor DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODwellAddress']"));
		public static final AttributeDescriptor PREVIOUS_DWELLING_ADDRESS = declare("PreviousDwellingAddress", AssetList.class, PreviousDwellingAddress.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOPreviousAddressComp']"));
		public static final AttributeDescriptor MAILING_ADDRESS = declare("MailingAddress", AssetList.class, MailingAddress.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOMailingAddressComponent']"));
		public static final AttributeDescriptor NAMED_INSURED_INFORMATION = declare("NamedInsuredInformation", AssetList.class, NamedInsuredInformation.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOCommunicationInfo']"));
		public static final AttributeDescriptor OTHER_ACTIVE_AAA_POLICIES = declare("OtherActiveAAAPolicies", MultiInstanceAfterAssetList.class, OtherActiveAAAPolicies.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOOtherOrPriorPolicyComponent']"));
		public static final AttributeDescriptor AGENT_INFORMATION = declare("AgentInfo", AssetList.class, AgentInfo.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAProducerInfo']"));

		// Named Insured
		public static final class NamedInsured extends MetaData {
			public static final AttributeDescriptor PREFIX = declare("Prefix", ComboBox.class);
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
			public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
			public static final AttributeDescriptor RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class);
			public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date Of Birth", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SOCIAL_SECURITY_NUMBER = declare("Social security number", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor BASE_DATE = declare("Base date", TextBox.class);
			public static final AttributeDescriptor OCCUPATION = declare("Occupation", ComboBox.class);
			public static final AttributeDescriptor AAA_EMPLOYEE = declare("AAA employee", RadioGroup.class);
			public static final AttributeDescriptor TRUSTEE_LLC_OWNER = declare("Trustee/LLC Owner", RadioGroup.class);
			public static final AttributeDescriptor BTN_ADD_INSURED = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHONamedInsured"));
		}

		// AAA Membership
		public static final class AAAMembership extends MetaData {
			public static final AttributeDescriptor CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor MEMBERSHIP_NUMBER = declare("Membership number", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor LAST_NAME = declare("Last name", TextBox.class, Waiters.AJAX);
		}

		// Dwelling Address
		public static final class DwellingAddress extends MetaData {
			public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHODwellAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//*[@id='addressValidationPopupAAAHODwellAddressValidationComp_container']"));
		}

		// Previous Dwelling Address
		public static final class PreviousDwellingAddress extends MetaData {
			public static final AttributeDescriptor HAS_PREVIOUS_DWELLING_ADDRESS = declare("Has there been another dwelling address within the last 3 consecutive years?", RadioGroup.class, Waiters.AJAX, false,
					By.xpath("//table[@id='policyDataGatherForm:sedit_AAAHOPreDwellingAddrExists_yesNoAnswer']"));
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AttributeDescriptor BTN_VALIDATE_ADDRESS = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOPreAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS = declare("Validate Address", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationPopupAAAHOPrevAddressValidationComp_container"));
		}

		// Mailing address
		public static final class MailingAddress extends MetaData {
			public static final AttributeDescriptor IS_DIFFERENT_MAILING_ADDRESS = declare("Is the mailing address different from the dwelling address?", RadioGroup.class, Waiters.AJAX, false,
					By.id("policyDataGatherForm:addOptionalQuestionFormGrid_AAAHOMailingAddressComponent"));
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COUTRY = declare("Country", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AttributeDescriptor BTN_VALIDATE_ADDRESS = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOMailingAddressButtonUS"));
			public static final AttributeDescriptor VALIDATE_ADDRESS = declare("Validate Address", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationPopupAAAHOMailingAddressValidation_container"));
		}

		// Named insured contact information
		public static final class NamedInsuredInformation extends MetaData {
			public static final AttributeDescriptor HOME_PHONE_NUMBER = declare("Home Phone Number", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor WORK_PHONE_NUMBER = declare("Work Phone Number", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor MOBILE_PHONE_NUMBER = declare("Mobile Phone Number", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
		}

		// Other active AAA policies
		public static final class OtherActiveAAAPolicies extends MetaData {
			public static final AttributeDescriptor OTHER_ACTIVE_AAA_POLICIES = declare("Other active AAA policies", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:formGrid_AAAHOOtherPolicyViewOnly']"));
			public static final AttributeDescriptor ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOOtherOrPriorPolicyComponent"));
			public static final AttributeDescriptor ACTIVE_UNDERLYING_POLICIES_SEARCH = declare("ActiveUnderlyingPoliciesSearch", SingleSelectSearchDialog.class, OtherActiveAAAPoliciesSearch.class,
					By.xpath(".//form[@id='policySearchForm_AAAHOOtherOrPriorActivePolicySearch']"));
			public static final AttributeDescriptor REMOVE_BTN = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAAHOOtherOrPriorPolicyComponent"));
			public static final AttributeDescriptor ACTIVE_UNDERLYING_POLICIES_MANUAL = declare("ActiveUnderlyingPoliciesManual", AssetList.class, OtherActiveAAAPoliciesManual.class);

			public static final class OtherActiveAAAPoliciesSearch extends MetaData {
				public static final AttributeDescriptor POLICY_TYPE = declare("Policy Type", ComboBox.class, Waiters.NONE);
				public static final AttributeDescriptor POLICY_NUMBER = declare("Policy Number", TextBox.class, Waiters.NONE);
			}

			public static final class OtherActiveAAAPoliciesManual extends MetaData {
				public static final AttributeDescriptor POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
				public static final AttributeDescriptor COMPANION_AUTO_PENDING_WITH_DISCOUNT = declare("Companion Auto Pending with Discount?", RadioGroup.class, Waiters.AJAX);
				public static final AttributeDescriptor COMPANION_PUP_DP3_PENDING_WITH_DISCOUNT = declare("Companion PUP/DP3 Pending with Discount?", RadioGroup.class, Waiters.AJAX);
				public static final AttributeDescriptor POLICY_NUMBER = declare("Policy number", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor POLICY_BASE_YEAR = declare("Policy base year", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor POLICY_TIER = declare("Policy tier", ComboBox.class, Waiters.AJAX);
				public static final AttributeDescriptor AUTO_POLICY_BI_LIMIT = declare("Auto policy BI limit", ComboBox.class, Waiters.AJAX);
				public static final AttributeDescriptor AUTO_POLICY_STATE = declare("Auto policy state", ComboBox.class, Waiters.AJAX);
				public static final AttributeDescriptor AUTO_INSURANCE_PERSISTENCY = declare("Auto Insurance Persistency", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor IS_THE_POLICY_PENDING = declare("Is the policy pending?", RadioGroup.class, Waiters.AJAX);
				public static final AttributeDescriptor COVERAGE_E = declare("Coverage E", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor DEDUCTIBLE = declare("Deductible", TextBox.class, Waiters.AJAX);
				public static final AttributeDescriptor DWELLING_USAGE = declare("Dwelling usage", ComboBox.class, Waiters.AJAX);
				public static final AttributeDescriptor OCCUPANCY_TYPE = declare("Occupancy type", ComboBox.class, Waiters.AJAX);
			}
		}

		// Agent and Agency information
		public static final class AgentInfo extends MetaData {
			public static final AttributeDescriptor CHANNEL_TYPE = declare("Channel Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor AGENCY = declare("Agency", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class);
			public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor AGENCY_LOCATION = declare("Agency Location", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor LOCATION = declare("Location", ComboBox.class);
			public static final AttributeDescriptor AGENT = declare("Agent", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class);
			public static final AttributeDescriptor AGENT_NUMBER = declare("Agent Number", TextBox.class);
			public static final AttributeDescriptor TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class, Waiters.AJAX);
		}
	}

	// --------- Reports Tab --------
	public static final class ReportsTab extends MetaData {
		/*
		 * public static final AttributeDescriptor CUSTOMER_AGREEMENT = declare(
		 * "Customer Agreement", RadioGroup.class, Waiters.AJAX, false,
		 * By.xpath("//table[@id='policyDataGatherForm:customerRadio']"));
		 */
		public static final AttributeDescriptor SALES_AGENT_AGREEMENT = declare("Sales Agent Agreement", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:sedit_AAAHOCommonDisclosureMessageComponent_agentAgrees']"));

		public static final AttributeDescriptor AAA_MEMBERSHIP_REPORT = declare("AAAMembershipReport", FillableTable.class, AaaMembershipReportRow.class, By.xpath("//table[@id='policyDataGatherForm:membershipReports']"));
		public static final AttributeDescriptor FIRELINE_REPORT = declare("FirelineReport", FillableTable.class, FirelineReportRow.class, By.xpath("//table[@id='policyDataGatherForm:firelineReportTable']"));
		public static final AttributeDescriptor PUBLIC_PROTECTION_CLASS = declare("PublicProtectionClass", FillableTable.class, PublicProtectionClassRow.class, By.xpath("//table[@id='policyDataGatherForm:ppcReportTable']"));
		public static final AttributeDescriptor CLUE_REPORT = declare("CLUEreport", FillableTable.class, CLUEreportRow.class, By.xpath("//table[@id='policyDataGatherForm:orderClueReports']"));
		public static final AttributeDescriptor ISO360_REPORT = declare("ISO360Report", FillableTable.class, ISO360ReportRow.class, By.xpath("//table[@id='policyDataGatherForm:iso360ReportTable']"));

		public static final class AaaMembershipReportRow extends MetaData {
			public static final AttributeDescriptor LAST_NAME = declare("Last Name", StaticElement.class);
			public static final AttributeDescriptor MEMBERSHIP_NO = declare("Membership No.", StaticElement.class);
			public static final AttributeDescriptor MEMBER_SINCE_DATE = declare("Member Since Date", StaticElement.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor RECEIPT_DATE = declare("Receipt Date", StaticElement.class);
			public static final AttributeDescriptor STATUS = declare("Status", StaticElement.class);
			public static final AttributeDescriptor REPORT = declare("Report", Link.class);
		}

		public static final class FirelineReportRow extends MetaData {
			public static final AttributeDescriptor DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AttributeDescriptor WILDFIRE_SCORE = declare("Wildfire Score", StaticElement.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AttributeDescriptor STATUS = declare("Status", StaticElement.class);
			public static final AttributeDescriptor REPORT = declare("Report", Link.class);
		}

		public static final class PublicProtectionClassRow extends MetaData {
			public static final AttributeDescriptor NAME = declare("Name", StaticElement.class);
			public static final AttributeDescriptor DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AttributeDescriptor PPC_VALUE = declare("PPC Value", StaticElement.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AttributeDescriptor STATUS = declare("Status", StaticElement.class);
			public static final AttributeDescriptor REPORT = declare("Report", Link.class);
		}

		public static final class CLUEreportRow extends MetaData {
			public static final AttributeDescriptor DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AttributeDescriptor NO_OF_CLAIMS = declare("No. of Claims", StaticElement.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AttributeDescriptor STATUS = declare("Status", StaticElement.class);
			public static final AttributeDescriptor REPORT = declare("Report", Link.class);
		}

		public static final class ISO360ReportRow extends MetaData {
			public static final AttributeDescriptor DWELLING_ADDRESS = declare("Dwelling Address", StaticElement.class);
			public static final AttributeDescriptor REPLACEMENT_COST = declare("Replacement Cost", StaticElement.class);
			public static final AttributeDescriptor ORDER_DATE = declare("Order Date", StaticElement.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AttributeDescriptor STATUS = declare("Status", StaticElement.class);
			public static final AttributeDescriptor REPORT = declare("Report", Link.class);
		}

		public static final class HssOverrideInsuranceScore extends MetaData {
			public static final AttributeDescriptor SCORE_AFTER_OVERRIDE = declare("Score after override", TextBox.class);
			public static final AttributeDescriptor REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class);
		}

		public static final class HssPPCDialog extends MetaData {
			public static final AttributeDescriptor FIRE_DEPARTMENT_TYPE = declare("Fire department type", StaticElement.class, Waiters.NONE);
			public static final AttributeDescriptor PUBLIC_PROTECTION_CLASS = declare("Public protection class", StaticElement.class, Waiters.NONE);
			public static final AttributeDescriptor DISTANCE_TO_FIRE_HYDRANT = declare("Distance to fire hydrant", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor FIRE_PROTECTION_AREA = declare("Fire protection area", StaticElement.class, Waiters.NONE);
			public static final AttributeDescriptor SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION = declare("Subscription to fire department/station", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor SYSTEM_PUBLIC_PROTECTION_CLASS = declare("System public protection class", StaticElement.class, Waiters.NONE);
		}

		public enum AAAMembershipReportsTblHeaders {

			LAST_NAME("Last Name"), MEMBERSHIP_NO("Membership No."), MEMBERSHIP_SINCE_DATE("Member Since Date"), ORDER_DATE("Order Date"), RECEIPT_DATE("Receipt Date"), STATUS("Status"), REPORT("Report");

			private String value;

			private AAAMembershipReportsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum FirelineReportTblHeaders {

			DWELLING_ADDRESS("Dwelling Address"), WILDFIRE_SCORE("Wildfire Score"), ORDER_DATE("Order Date"), EXPIRATION_DATE("Expiration Date"), STATUS("Status"), REPORT("Report");

			private String value;

			private FirelineReportTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}

		}

		public enum PublicProtectionClassTblHeaders {

			NAME("Name"), DWELLING_ADDRESS("Dwelling Address"), PPC_VALUE("PPC Value"), ORDER_DATE("Order Date"), EXPIRATION_DATE("Expiration Date"), STATUS("Status"), REPORT("Report");

			private String value;

			private PublicProtectionClassTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum CLUEReportTblHeaders {

			DWELLING_ADDRESS("Dwelling Address"), NO_OF_CLAIMS("No. of Claims"), ORDER_DATE("Order Date"), EXPIRATION_DATE("Expiration Date"), STATUS("Status"), REPORT("Report");

			private String value;

			private CLUEReportTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum ISO360reportTblHeaders {

			DWELLING_ADDRESS("Dwelling Address"), REPLACEMENT_COST("Replacement Cost"), ORDER_DATE("Order Date"), EXPIRATION_DATE("Expiration Date"), STATUS("Status"), REPORT("Report");

			private String value;

			private ISO360reportTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}
	}

	// --------- Property Info Tab --------
	public static final class PropertyInfoTab extends MetaData {

		public static final AttributeDescriptor DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class, By.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOAddiDwellAddrMVO']"));
		public static final AttributeDescriptor ADDITIONAL_ADDRESS = declare("AdditionalAddress", MultiInstanceAfterAssetList.class, AdditionalAddress.class, By.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOAdditionalAddress']"));
		public static final AttributeDescriptor PUBLIC_PROTECTION_CLASS = declare("PublicProtectionClass", AssetList.class, PublicProtectionClass.class);
		// ,By.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAPpcDetailsMVO']"));
		public static final AttributeDescriptor FIRE_REPORT = declare("FireReport", AssetList.class, FireReport.class);
		// ,By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAFirelineDetailsMVO']"));
		public static final AttributeDescriptor PROPERTY_VALUE = declare("PropertyValue", AssetList.class, PropertyValue.class);
		public static final AttributeDescriptor CONSTRUCTION = declare("Construction", AssetList.class, Construction.class);
		public static final AttributeDescriptor ADDITIONAL_QUESTIONS = declare("AdditionalQuestions", AssetList.class, AdditionalQuestions.class);
		public static final AttributeDescriptor INTERIOR = declare("Interior", AssetList.class, Interior.class);
		public static final AttributeDescriptor DETACHED_STRUCTURES = declare("DetachedStructures", MultiInstanceAfterAssetList.class, DetachedStructures.class);
		public static final AttributeDescriptor CEA_INFORMATION = declare("CeaInformation", AssetList.class, CeaInformation.class);
		public static final AttributeDescriptor FIRE_PROTECTIVE_DD = declare("FireProtectiveDD", AssetList.class, FireProtectiveDD.class);
		public static final AttributeDescriptor THEFT_PROTECTIVE_DD = declare("TheftProtectiveTPDD", AssetList.class, TheftProtectiveTPDD.class);
		public static final AttributeDescriptor HOME_RENOVATION = declare("HomeRenovation", AssetList.class, HomeRenovation.class);
		public static final AttributeDescriptor PETS_OR_ANIMALS = declare("PetsOrAnimals", AssetList.class, PetsOrAnimals.class);
		public static final AttributeDescriptor STOVES = declare("Stoves", AssetList.class, Stoves.class);
		public static final AttributeDescriptor RECREATIONAL_EQUIPMENT = declare("RecreationalEquipment", AssetList.class, RecreationalEquipment.class);
		public static final AttributeDescriptor CLAIM_HISTORY = declare("ClaimHistory", AssetList.class, ClaimHistory.class);
		public static final AttributeDescriptor RENTAL_INFORMATION = declare("RentalInformation", AssetList.class, RentalInformation.class);

		public static final class DwellingAddress extends MetaData {
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE = declare("State / Province", ComboBox.class);
			public static final AttributeDescriptor NUMBER_OF_FAMILY_UNITS = declare("Number of family units", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SECTION_II_TERRITORY = declare("Section II territory", ComboBox.class);
		}

		public static final class AdditionalAddress extends MetaData {
			public static final AttributeDescriptor ARE_THERE_ANY_ADDITIONAL_ADDRESSES = declare("Are there any additional addresses?", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestion_AAAHOAdditionalAddress']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAHOAdditionalAddress_Dialog_container"));
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AttributeDescriptor BTN_VALIDATE_ADDRESS = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOAddAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS = declare("Validate Address", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id("addressValidationFormAAAHOAdditionalDwelAddressValidation"));
			public static final AttributeDescriptor BTN_ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOAdditionalAddress"));
			public static final AttributeDescriptor BTN_REMOVE = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAAHOAdditionalAddress"));
		}

		public static final class PublicProtectionClass extends MetaData {
			public static final AttributeDescriptor FIRE_DEPARTMENT_TYPE = declare("Fire department type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION = declare("Subscription to fire department/station", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PUBLIC_PROTECTION_CLASS = declare("Public protection class", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor DISTANCE_TO_FIRE_HYDRANT = declare("Distance to fire hydrant", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SYSTEM_PUBLIC_PROTECTION_CLASS = declare("System public protection class", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor FIRE_PROTECTION_AREA = declare("Fire protection area", TextBox.class, Waiters.AJAX);
		}

		public static final class FireReport extends MetaData {
			public static final AttributeDescriptor PLACEMENT_OR_MATCH_TYPE = declare("Placement or match type", TextBox.class);
			public static final AttributeDescriptor WILDFIRE_SCORE = declare("Wildfire score", TextBox.class);
		}

		public static final class PropertyValue extends MetaData {
			public static final AttributeDescriptor COVERAGE_A_DWELLING_LIMIT = declare("Coverage A - Dwelling limit", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ISO_REPLACEMENT_COST = declare("ISO replacement cost", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE = declare("Reason replacement cost differs from the tool value", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PERSONAL_PROPERTY_VALUE = declare("Personal property value", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PURCHASE_DATE_OF_HOME = declare("Purchase date of home", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NEW_LOAN = declare("New loan", CheckBox.class, Waiters.AJAX);
			public static final AttributeDescriptor OTHER_SPECIFY = declare("Other - specify", TextBox.class, Waiters.AJAX);
		}

		public static final class Construction extends MetaData {
			public static final AttributeDescriptor YEAR_BUILT = declare("Year built", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SQUARE_FOOTAGE = declare("Square footage", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ROOF_TYPE = declare("Roof type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ROOF_SHAPE = declare("Roof shape", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor T_LOCK_SHINGLES = declare("T-Lock shingles", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor CONSTRUCTION_TYPE = declare("Construction type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR = declare("Is this a log home assembled by a licensed building contractor?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor MASONRY_VENEER = declare("Masonry Veneer", RadioGroup.class);
			public static final AttributeDescriptor HAIL_RESISTANCE_RATING = declare("Hail-resistance rating", ComboBox.class, Waiters.AJAX);
		}

		public static final class AdditionalQuestions extends MetaData {
			public static final AttributeDescriptor IS_THE_LENGTH_OF_DRIVEWAY_LESS_THAN_500_FEET = declare("Is the length of driveway less than 500 feet?", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor IS_THE_ROAD_TO_THE_HOME_AND_DRIVEWAY_PAVED = declare("Is the road to the home and driveway paved?", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor IS_THERE_A_CREDITABLE_ALTERNATIVE_WATER_SOURCE_WITHIN_1_000_FEET_OF_THE_PROPERTY = declare("Is there a creditable alternative water source within 1,000 feet of the property?", RadioGroup.class, Waiters.NONE);
		}

		public static final class Interior extends MetaData {
			public static final AttributeDescriptor DWELLING_USAGE = declare("Dwelling usage", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor OCCUPANCY_TYPE = declare("Occupancy type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor DWELLING_PERCENTAGE_USAGE = declare("Dwelling Percentage Usage", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NUMBER_OF_RESIDENTS = declare("Number of residents", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NUMBER_OF_STORIES_INCLUDING_BASEMENT = declare("Number of stories, including basement", ComboBox.class, Waiters.AJAX);
		}

		public static final class DetachedStructures extends MetaData {
			public static final AttributeDescriptor ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY = declare("Are there any detached structures on the property?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHODetachedStructuresInfoComponent"));
			public static final AttributeDescriptor REMOVE_BTN = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAAHODetachedStructuresInfoComponent"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAHODetachedStructuresInfoComponent_Dialog_container"));
			public static final AttributeDescriptor RENTED_TO_OTHERS = declare("Rented to others", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor LIMIT_OF_LIABILITY = declare("Limit of liability", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NUMBER_OF_FAMILY_UNITS = declare("Number of family units", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NUMBER_OF_OCCUPANTS = declare("Number of occupants", ComboBox.class, Waiters.AJAX);
		}

		public static final class CeaInformation extends MetaData {
			public static final AttributeDescriptor FOUNDATION_TYPE = declare("Foundation type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ARE_THE_WATER_HEATERS_SECURED = declare("Are the water heaters secured to building frame?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWOOD = declare("Are cripple walls braced with plywood or equivalent?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor IS_THE_DWELLING_SECURED_TO_FOUNDATION = declare("Is the dwelling secured to foundation?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DOES_DWELLING_HAS_AN_ATTACHED_GARAGE = declare("Does dwelling has an attached garage?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor IS_THE_DWELLING_ANCHORED = declare("Is the dwelling anchored to foundation using approved anchor bolts in accordance with California building code?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor IS_THE_WATER_HEATER_SECURED_FOR_EARTHQUAKE = declare("Is the water heater secured to the building frame in accordance with guidelines for earthquake bracing of residential water heaters?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DOES_DWELLING_HAVE_CRIPLE_WALS = declare("Does the dwelling have cripple walls?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWODD = declare("Are cripple walls braced with plywood or its equivalent and installed in accordance with California building code?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DOES_DWELLING_HAVE_POST_AND_PIER_FOUNDATION = declare("Does dwelling have post-and-pier or post-and-beam foundation?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor IS_DWELLING_HAD_BEEN_MODIFIED_ACCORDING_BUILDING_CODE = declare("Is dwelling with post-and-pier or post-and-beam foundation, has been modified in accordance with California building code?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class FireProtectiveDD extends MetaData {
			public static final AttributeDescriptor LOCAL_FIRE_ALARM = declare("Local fire alarm", CheckBox.class);
			public static final AttributeDescriptor CENTRAL_FIRE_ALARM = declare("Central fire alarm", CheckBox.class, Waiters.AJAX);
			public static final AttributeDescriptor FULL_RESIDENTIAL_SPRINKLERS = declare("Full residential sprinklers", CheckBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PARTIAL_RESIDENTIAL_SPRINKLERS = declare("Partial residential sprinklers", CheckBox.class, Waiters.AJAX);
		}

		public static final class TheftProtectiveTPDD extends MetaData {
			public static final AttributeDescriptor LOCAL_THEFT_ALARM = declare("Local theft alarm", CheckBox.class);
			public static final AttributeDescriptor CENTRAL_THEFT_ALARM = declare("Central theft alarm", CheckBox.class);
			public static final AttributeDescriptor GATED_COMMUNITY = declare("Gated community", CheckBox.class);
		}

		public static final class HomeRenovation extends MetaData {
			public static final AttributeDescriptor PLUMBING_RENOVATION = declare("Plumbing renovation", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PLUMBING_PERCENT_COMPLETE = declare("Plumbing % complete", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingPercentComplete']"));
			public static final AttributeDescriptor PLUMBING_MONTH_OF_COMPLECTION = declare("Plumbing Month of completion", ComboBox.class, Waiters.AJAX, false, By.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingRenovationMonth']"));
			public static final AttributeDescriptor PLUMBING_YEAR_OF_COMPLECTION = declare("Plumbing Year of completion", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_plumbingRenovationYear']"));

			public static final AttributeDescriptor ELECTRICAL_RENOVATION = declare("Electrical renovation", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ELECTRICAL_PERCENT_COMPLETE = declare("Electrical % complete", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalPercentComplete']"));
			public static final AttributeDescriptor ELECTRICAL_MONTH_OF_COMPLECTION = declare("Electrical Month of completion", ComboBox.class, Waiters.AJAX, false, By.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalRenovationMonth']"));
			public static final AttributeDescriptor ELECTRICAL_YEAR_OF_COMPLECTION = declare("Electrical Year of completion", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_electricalRenovationYear']"));

			public static final AttributeDescriptor ROOF_RENOVATION = declare("Roof renovation", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ROOF_PERCENT_COMPLETE = declare("Roof % complete", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofPercentComplete']"));
			public static final AttributeDescriptor ROOFG_MONTH_OF_COMPLECTION = declare("Roof Month of completion", ComboBox.class, Waiters.AJAX, false, By.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofRenovationMonth']"));
			public static final AttributeDescriptor ROOF_YEAR_OF_COMPLECTION = declare("Roof Year of completion", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_roofRenovationYear']"));

			public static final AttributeDescriptor HEATING_COOLING_RENOVATION = declare("Heating/cooling renovation", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor HEATING_COOLING_PERCENT_COMPLETE = declare("Heating/cooling % complete", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingPercentComplete']"));
			public static final AttributeDescriptor HEATING_COOLING_MONTH_OF_COMPLECTION = declare("Heating/cooling Month of completion", ComboBox.class, Waiters.AJAX, false, By.xpath("//select[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingRenovationMonth']"));
			public static final AttributeDescriptor HEATING_COOLING_YEAR_OF_COMPLECTION = declare("Heating/cooling Year of completion", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:sedit_AAAHOBuildingImprovementsInfo_heatingOrCoolingRenovationYear']"));

			// public static final AttributeDescriptor GREEN_HOME_DISCOUNT = declare("Green Home discount", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor STORM_SHUTTER_DISCOUNT = declare("Storm Shutter discount", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor WINDSTORM_MITIGATION_DISCOUNT = declare("Windstorm Mitigation discount", RadioGroup.class, Waiters.AJAX);
		}

		public static final class PetsOrAnimals extends MetaData {
			public static final AttributeDescriptor ARE_ANY_PETS_OR_ANIMALS_KEPT_ON_THE_PROPERTY = declare("Are any pets or animals kept on the property?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor BTN_ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAADwellAnimalInfoComponent"));
			public static final AttributeDescriptor BTN_REMOVE = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminateAAADwellAnimalInfoComponent"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAADwellAnimalInfoComponent_Dialog_container"));
			public static final AttributeDescriptor ANIMAL_TYPE = declare("Animal type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor NAME = declare("Name", TextBox.class);
			public static final AttributeDescriptor AGE = declare("Age", TextBox.class);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class);
			public static final AttributeDescriptor OTHER_SPECIFY = declare("Other - specify", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ANIMAL_COUNT = declare("Animal count", TextBox.class, Waiters.AJAX);
		}

		public static final class Stoves extends MetaData {
			public static final AttributeDescriptor DOES_THE_PROPERTY_HAVE_A_WOOD_BURNING_STOVE = declare("Does the property have a wood-burning stove?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT = declare("Is the stove the sole source of heat?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR = declare("Was the stove installed by a licensed contractor?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY = declare("Does the dwelling have at least one smoke detector per story?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class RecreationalEquipment extends MetaData {
			public static final AttributeDescriptor SWIMMING_POOL = declare("Swimming pool", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SPA_HOT_TUB = declare("Spa/hot tub", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor TRAMPOLINE = declare("Trampoline", ComboBox.class, Waiters.AJAX);
		}

		public static final class ClaimHistory extends MetaData {
			public static final AttributeDescriptor ADD_A_CLAIM = declare("Add a claim", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAHOLossInfo']"));
			public static final AttributeDescriptor BTN_ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOLossInfo"));
			public static final AttributeDescriptor BTN_REMOVE = declare("Remove", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:eliminate"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAHOLossInfo_Dialog_container"));
			public static final AttributeDescriptor SOURCE = declare("Source", ComboBox.class);
			public static final AttributeDescriptor DATE_OF_LOSS = declare("Date of loss", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CAUSE_OF_LOSS = declare("Cause of loss", ComboBox.class);
			public static final AttributeDescriptor AMOUNT_OF_LOSS = declare("Amount of loss", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CLAIM_STATUS = declare("Claim status", ComboBox.class);
			public static final AttributeDescriptor AAA_CLAIM = declare("AAA Claim", RadioGroup.class);
			public static final AttributeDescriptor CATASTROPHE_LOSS = declare("Catastrophe loss", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor CATASTROPHE_LOSS_CODE_REMARKS = declare("Catastrophe loss code/remarks", TextBox.class);
			public static final AttributeDescriptor LOSS_FOR = declare("Loss for", ComboBox.class);
			public static final AttributeDescriptor CHARGEABLE = declare("Chargeable", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_CLAIM_IS_NOT_CHARGEABLE = declare("Reason claim is not chargeable", TextBox.class, Waiters.NONE);
		}

		public static final class RentalInformation extends MetaData {
			public static final AttributeDescriptor NUMBER_OF_CONSECUTIVE_YEARS_INSURED_HAS_OWNED_ANY_RENTAL_PROPERTIES = declare("Number of consecutive years insured has owned any rental properties", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PROPERTY_MANAGER = declare("Property manager", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor DOES_THE_TENANT_HAVE_AN_UNDERLYING_HO4_POLICY = declare("Does the tenant have an underlying HO4 policy?", RadioGroup.class, Waiters.AJAX);
		}

		public static final class OilPropaneStorageTank extends MetaData {
			public static final AttributeDescriptor OIL_FUEL_OR_PROPANE_STORAGE_TANK = declare("Oil Fuel or Propane Storage Tank", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD_FUEL_SYSTEM_STORAGE_TANK_COVERAGE = declare("Add fuel system storage tank coverage?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor AGE_OF_OIL_OR_PROPANE_FUEL_STORAGE_TANK = declare("Age of oil or propane fuel storage tank", TextBox.class, Waiters.NONE);
		}

		public enum AdditionalAddressTblHeaders {

			STREET_ADDRESS1("Street address 1"), STREET_ADDRESS2("Street address 2"), CITY("City"), STATE("State"), ZIP_CODE("Zip Code");

			private String value;

			private AdditionalAddressTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum DetachedStructuresTblHeaders {

			RENTED("Rented"), DECRIPTION("Description"), LIMIT_OF_LIABILITY("Limit Of Liability"), FAMILY_UNITS("Family Units"), OCCUPANTS("Occupants");

			private String value;

			private DetachedStructuresTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum PetsOrAnimalsTblHeaders {

			ANIMAL_TYPE("Animal Type"), OTHER_SPECIFY("Other - Specify"), Count("Count");

			private String value;

			private PetsOrAnimalsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum ClaimsTblHeaders {

			DATE_OF_LOSS("Date of loss"), CAUSE_OF_LOSS("Cause of loss"), AMOUNT_OF_LOSS("Amount of loss"), CLAIM_STATUS("Claim Status"), CHARGEABLE("Chargeable"), SOURCE("Source");

			private String value;

			private ClaimsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}
	}

	// --------- Premium and Coverage - Endorsement Tab --------
	public static final class EndorsementTab extends MetaData {
		public static final AttributeDescriptor OPTIONAL_ENDORSEMENTS_FILTER = declare("Optional Endorsements Filter", TextBox.class, Waiters.NONE, false, By.id("policyDataGatherForm:filterCriteria_AAAHoPolicyEndorsementFormManager"));
		public static final AttributeDescriptor BTN_FILTER = declare("Filter", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:filterFormsButton_AAAHoPolicyEndorsementFormManager"));
		public static final AttributeDescriptor BTN_VIEW_ALL = declare("View All", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:viewAllButton_AAAHoPolicyEndorsementFormManager"));

		public enum IncludedAndSelectedEndorsementsTblHeaders {

			FORM_ID("Form ID"), NAME("Name"), CATEGORY("Category"), NUMBER_OF_FORMS("Number of Forms"), DESCRIPTION("Description"), CONTROLS("");

			private String value;

			private IncludedAndSelectedEndorsementsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum OptionalEndorsementsTblHeaders {

			FORM_ID("Form ID"), NAME("Name"), CATEGORY("Category"), DESCRIPTION("Description"), CONTROLS("");

			private String value;

			private OptionalEndorsementsTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}
	}

	//TODO Update
	public static final class PersonalPropertyTab extends MetaData {
		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		public static final AttributeDescriptor SCHEDULED_PROPERTY_COVERAGE = declare("Scheduled Property Coverage", RadioGroup.class);
		public static final AttributeDescriptor LIMIT_AMOUNT = declare("Limit Amount", TextBox.class);
		public static final AttributeDescriptor PAYMENT_BASIS = declare("Payment Basis", TextBox.class);
		public static final AttributeDescriptor DEDUCTIBLE_AMOUNT = declare("Deductible Amount", TextBox.class);
		public static final AttributeDescriptor TAXES = declare("Taxes", TextBox.class);
		public static final AttributeDescriptor FEES = declare("Fees", TextBox.class);
		public static final AttributeDescriptor TERM_PREMIUM = declare("Term Premium", TextBox.class);
		public static final AttributeDescriptor BILLABLE_PREMIUM = declare("Billable Premium", TextBox.class);

		public static final class JewelryTab extends MetaData {
			public static final AttributeDescriptor JEWELRY = declare("Jewelry", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
			public static final AttributeDescriptor OF_PICTURE = declare("# of Picture", TextBox.class);
		}

		public static final class FineArtsTab extends MetaData {
			public static final AttributeDescriptor FINE_ARTS = declare("Fine Arts", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
			public static final AttributeDescriptor CREATOR_AUTHOR = declare("Creator(Author)", TextBox.class);
			public static final AttributeDescriptor TITLE = declare("Title", TextBox.class);
			public static final AttributeDescriptor DIMENSION = declare("Dimension", TextBox.class);
			public static final AttributeDescriptor YEAR = declare("Year", TextBox.class);
			public static final AttributeDescriptor TECHNIQUE = declare("Technique", TextBox.class);
		}

		public static final class PortableElectronicEquipmentTab extends MetaData {
			public static final AttributeDescriptor PORTABLEELECTRONICEQUIPMENT = declare("PortableElectronicEquipment", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		}

		public static final class SportsEquipmentTab extends MetaData {
			public static final AttributeDescriptor SPORTSEQUIPMENT = declare("SportsEquipment", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		}

		public static final class MoneyTab extends MetaData {
			public static final AttributeDescriptor CLASS_LIMIT = declare("Class Limit", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		}

		public static final class SecuritiesTab extends MetaData {
			public static final AttributeDescriptor SECURITIES = declare("Securities", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		}

		public static final class FursTab extends MetaData {
			public static final AttributeDescriptor FURS = declare("Furs", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
			public static final AttributeDescriptor NUMBER_OF_PICTURE = declare("# of Picture", TextBox.class);
		}

		public static final class FirearmsTab extends MetaData {
			public static final AttributeDescriptor CLASS_LIMIT = declare("Class Limit", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		}

		public static final class SilverwareTab extends MetaData {
			public static final AttributeDescriptor SILVERWARE = declare("Silverware", TextBox.class);
			public static final AttributeDescriptor ITEM_NUMBER = declare("Item Number", TextBox.class);
			public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		}
	}

	// --------- Premium & Coverages - Quote Tab --------
	public static final class PremiumsAndCoveragesQuoteTab extends MetaData {
		public static final AttributeDescriptor PAYMENT_PLAN = declare("Payment Plan", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor PAYMENT_PLAN_AT_RENEWAL = declare("Payment plan at renewal", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor RECURRING_PAYMENT = declare("Recurring Payment", CheckBox.class, Waiters.AJAX);
		public static final AttributeDescriptor BILL_TO_AT_RENEWAL = declare("Bill to at renewal", ComboBox.class, Waiters.AJAX);

		public static final AttributeDescriptor COVERAGE_A = declare(HomeCaCoverages.COVERAGE_A.get(), StaticElement.class, Waiters.AJAX, false,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//td[3]", HomeCaCoverages.COVERAGE_A.get())));
		public static final AttributeDescriptor COVERAGE_B = declare(HomeCaCoverages.COVERAGE_B.get(), ComboBox.class, Waiters.AJAX, false,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//td[3]", HomeCaCoverages.COVERAGE_B.get())));
		public static final AttributeDescriptor COVERAGE_C = declare(HomeCaCoverages.COVERAGE_C.get(), TextBox.class, Waiters.AJAX, false,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//input", HomeCaCoverages.COVERAGE_C.get())));
		public static final AttributeDescriptor COVERAGE_D = declare(HomeCaCoverages.COVERAGE_D.get(), TextBox.class, Waiters.AJAX, false,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//input", HomeCaCoverages.COVERAGE_D.get())));
		public static final AttributeDescriptor COVERAGE_E = declare(HomeCaCoverages.COVERAGE_E.get(), ComboBox.class, Waiters.AJAX, false,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCoverages.COVERAGE_E.get())));
		public static final AttributeDescriptor COVERAGE_F = declare(HomeCaCoverages.COVERAGE_F.get(), StaticElement.class, Waiters.AJAX, false,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//td[3]", HomeCaCoverages.COVERAGE_F.get())));
		public static final AttributeDescriptor DEDUCTIBLE = declare(HomeCaCoverages.DEDUCTIBLE.get(), ComboBox.class, Waiters.AJAX, false,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCoverages.DEDUCTIBLE.get())));

		/*
		 * public static final AttributeDescriptor NAME = declare("Name",
		 * TextBox.class, Waiters.AJAX); public static final AttributeDescriptor
		 * ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX); public
		 * static final AttributeDescriptor STREET_ADDRESS_1 = declare(
		 * "Street address 1", TextBox.class, Waiters.AJAX); public static final
		 * AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2",
		 * TextBox.class, Waiters.AJAX); public static final AttributeDescriptor
		 * CITY = declare("City", TextBox.class, Waiters.AJAX); public static
		 * final AttributeDescriptor STATE = declare("State", ComboBox.class,
		 * Waiters.AJAX);
		 */
		public static final AttributeDescriptor OPTIONAL_COVERAGE_MASONRY_VENEER = declare("Optional coverage - Masonry veneer", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor OPTIONAL_COVERAGE_BREAKAGE_OF_PERSONAL_PROPERTY = declare("Optional coverage - Breakage of personal property", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor CALCULATE_PREMIUM_BUTTON = declare("Calculate Premium", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:premiumRecalcCov"));
		public static final AttributeDescriptor ACCEPT_CEA_OFFER = declare("Accept CEA offer", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor CEA_PRODUCT = declare("CEA Product", ComboBox.class, Waiters.AJAX);

		public static final class CeaCoverages extends MetaData {
			public static final AttributeDescriptor COVERAGE_A_DWELLING_LIMIT = declare(HomeCaCeaCoverages.COVERAGE_A_DWELLING_LIMIT.get(), StaticElement.class, Waiters.AJAX, true,
					By.xpath(String.format("//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//td[3]", HomeCaCeaCoverages.COVERAGE_A_DWELLING_LIMIT.get())));
			public static final AttributeDescriptor COVERAGE_A_DEDUCTIBLE = declare(HomeCaCeaCoverages.COVERAGE_A_DEDUCTIBLE.get(), ComboBox.class, Waiters.AJAX, true,
					By.xpath(String.format("//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_A_DEDUCTIBLE.get())));
			public static final AttributeDescriptor COVERAGE_C = declare(HomeCaCeaCoverages.COVERAGE_C.get(), ComboBox.class, Waiters.AJAX, true,
					By.xpath(String.format("//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_C.get())));
			public static final AttributeDescriptor COVERAGE_D = declare(HomeCaCeaCoverages.COVERAGE_D.get(), ComboBox.class, Waiters.AJAX, true,
					By.xpath(String.format("//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_D.get())));
			public static final AttributeDescriptor ADDITIONAL = declare(HomeCaCeaCoverages.ADDITIONAL.get(), ComboBox.class, Waiters.AJAX, true,
					By.xpath(String.format("//table[@id='policyDataGatherForm:ceaCoverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.ADDITIONAL.get())));
		}

		public enum HomeCaCoverages {
			COVERAGE_A("Coverage A - Dwelling limit"), COVERAGE_B("Coverage B - Other Structures limit"), COVERAGE_C("Coverage C - Personal Property limit"), COVERAGE_D("Coverage D - Loss of Use limit"), COVERAGE_E(
					"Coverage E - Personal Liability Each Occurrence"), COVERAGE_F("Coverage F - Medical Payments to Others"), DEDUCTIBLE("Deductible"),;

			private String value;

			private HomeCaCoverages(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}

		public enum HomeCaCeaCoverages {
			COVERAGE_A_DWELLING_LIMIT("Coverage A - Dwelling limit"), COVERAGE_A_DEDUCTIBLE("Coverage A - Deductible"), COVERAGE_C("Coverage C - Personal Property limit"), COVERAGE_D("Coverage D - Loss of Use limit"), ADDITIONAL(
					"Additional Limited Building Code Upgrade");

			private String value;

			private HomeCaCeaCoverages(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}
	}

	public static final class MortgageesTab extends MetaData {

		public static final AttributeDescriptor MORTGAGEE = declare("Mortgagee", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor MORTGAGEE_INFORMATION = declare("MortgageeInformation", MultiInstanceAfterAssetList.class, MortgageeInformation.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOMortgageeInfo']"));
		public static final AttributeDescriptor USE_LEGAL_NAMED_INSURED = declare("Use legal named insured", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor LEGAL_NAMED_INSURED = declare("Legal named insured", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:aaaLegalName']"));
		public static final AttributeDescriptor USE_LEGAL_PROPERTY_ADDRESS = declare("Use legal property address", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor LEGAL_PROPERTY_ADDRESS = declare("LegalPropetyAddress", AssetList.class, LegalPropetyAddress.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOLegalPropAddressComp']"));
		public static final AttributeDescriptor IS_THERE_ADDITIONA_INSURED = declare("Is there an additional insured?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ADDITIONAL_INSURED = declare("AdditionalInsured", MultiInstanceAfterAssetList.class, AdditionalInsured.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOAdditionalInsured']"));
		public static final AttributeDescriptor IS_THERE_ADDITIONA_INTEREST = declare("Is there an additional interest?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ADDITIONAL_INTEREST = declare("AdditionalInterest", MultiInstanceAfterAssetList.class, AdditionalInterest.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOAdditionalInterest']"));
		public static final AttributeDescriptor IS_THERE_ANY_THIRD_PARTY_DESIGNEE = declare("Is there any third party designee?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor THIRD_PARTY_DESIGNEE = declare("ThirdPartyDesignee", AssetList.class, ThirdPartyDesignee.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAThirdPartyDesignee']"));

		public static final class MortgageeInformation extends MetaData {
			public static final AttributeDescriptor NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address validated", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOMortAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//div[@id='addressValidationPopupAAAHOMortgageeAddressValidation_container']"));
			public static final AttributeDescriptor LOAN_NUMBER = declare("Loan number", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor USE_LEGAL_MORTGAGEE_FOR_EVIDENCE_OF_INSURANCE = declare("Use legal mortgagee for evidence of insurance", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor LEGAL_MORTGAGEE_NAME = declare("Legal mortgagee name", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOMortgageeInfo"));
		}

		public static final class LegalPropetyAddress extends MetaData {
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address validated", TextBox.class);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOLegAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class, By.id(".//div[@id='addressValidationPopupAAAHOLegalPropAddressValidationComp_container']"));
		}

		public static final class AdditionalInsured extends MetaData {
			public static final AttributeDescriptor INTEREST = declare("Interest", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor NAME = declare("Name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor SAME_AS_INSURED_S_MAILING_ADDRESS = declare("Same as insured's mailing address?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOAdditionalInsured"));
		}

		public static final class AdditionalInterest extends MetaData {
			public static final AttributeDescriptor NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor DESCRIPTION_OF_INTEREST = declare("Description of interest", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOAdditionalInterest"));
		}

		public static final class ThirdPartyDesignee extends MetaData {
			public static final AttributeDescriptor NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}
	}

	public static final class UnderwritingAndApprovalTab extends MetaData {
		public static final AttributeDescriptor UNDERWRITER_SELECTED_INSPECTION_TYPE = declare("Underwriter-selected inspection type", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor INTERIOR_INSPECTION = declare("Interior inspection", CheckBox.class, Waiters.AJAX);
		public static final AttributeDescriptor EXTERIOR_INSPECTION = declare("Exterior inspection", CheckBox.class, Waiters.AJAX);
		public static final AttributeDescriptor HIGH_VALUE_INTERIOR_INSPECTION = declare("High Value Interior inspection", CheckBox.class, Waiters.AJAX);

		public static final AttributeDescriptor HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_CREATURE = declare("Have any of the applicant(s)’ current pets injured, intentionally or unintentionally, another creature or person?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED_IN_THE_PAST_3_YEARS = declare("Have any applicants had a prior insurance policy cancelled, refused, or non-renewed in the past 3 years?", RadioGroup.class,
				Waiters.AJAX);
		public static final AttributeDescriptor HAS_THE_PROPERTY_BEEN_IN_FORECLOSURE_PROCEEDINGS_WITHIN_THE_PAST_18_MONTHS = declare("Has the property been in foreclosure proceedings within the past 18 months?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_ANY_GAS_WATER_HEATER_LOCATED_IN_A_GARAGE = declare("Is any gas water heater located in a garage?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ARE_ALL_WATER_HEATERS_STRAPPED_TO_THE_WALL = declare("Are all water heaters strapped to the wall, or, if located in the garage, raised at least 18 inches from the floor?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DO_EMPLOYEES_OF_ANY_RESIDENT_OR_APPLICANT_RESIDE_IN_THE_DWELLING = declare("Do employees of any resident or applicant reside in the dwelling?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor TOTAL_NUMBER_OF_PART_TIME_AND_FULL_TIME_RESIDENT_EMPLOYEES = declare("Total number of part time and full time resident employees", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare("Is any business, home day care or farming activity conducted on the premises?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_THERE_A_BUSINESS_ON_PREMISES = declare("Is there a business on premises?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DAY_CARE = declare("Day Care?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_THE_DWELLING_LOCATED_WITHIN_500_FEET_OF_BAY_OR_COASTAL_WATERS = declare("Is the dwelling located within 500 feet of bay or coastal waters?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DO_YOU_HAVE_A_LICENSE = declare("Do you have a license?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor FARMING_RANCHING = declare("Farming/Ranching?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_IT_A_FOR_PROFIT_BUSINESS = declare("Is it a for-profit business?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor INCIDENTAL_BUSINESS_OCCUPANCY = declare("Incidental Business Occupancy?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor HOW_MANY_CUSTOMERS_VISIT_THE_RESIDENCE_PREMISES_PER_WEEK = declare("How many customers visit the residence premises per week? ", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor OTHERS = declare("Others?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor HAVE_ANY_APPLICANTS_HAD_A_FLAT_ROOF_RELATED_CLAIM_IN_THE_PAST_3_YEARS = declare("Have any applicants had a flat roof related claim in the past 3 years?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor REMARKS = declare("Remarks", TextBox.class, Waiters.AJAX);

		public static final AttributeDescriptor REMARK_PRIOR_INSURANCE = declare("Remark Prior Insurance", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaPriorPolicyStatusRemark']"));
		public static final AttributeDescriptor REMARK_FIRE_HAZARD = declare("Remark Fire Hazard", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaFireSafeDistRemark']"));
		public static final AttributeDescriptor REMARK_DWELL_SLOPE = declare("Remark Dwell Slope", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaDwellSlopeRemark']"));
		public static final AttributeDescriptor REMARK_FORECLOSURE = declare("Remark Foreclosure", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaForeClosureProceedingRemark']"));
		public static final AttributeDescriptor REMARK_RENOVATION = declare("Remark Renovation", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaDwellRenovationRemark']"));
		public static final AttributeDescriptor REMARK_RESIDENT_EMPLOYEES = declare("Remark Resident Employees", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:sedit_AAAUnderwritingQuestionaireComponent_aaaEmployeesGradeRemark']"));
	}

	public static final class DocumentsTab extends MetaData {
		public static final AttributeDescriptor DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODocGenPrint']"));
		public static final AttributeDescriptor DOCUMENTS_TO_BIND = declare("DocumentsToBind", AssetList.class, DocumentsToBind.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOBindDocuments']"));
		public static final AttributeDescriptor DOCUMENTS_TO_ISSUE = declare("DocumentsToIssue", AssetList.class, DocumentsToIssue.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOIssueDocuments']"));

		public static final class DocumentsForPrinting extends MetaData {
			public static final AttributeDescriptor CONSUMER_INFORMATION_NOTICE = declare("Consumer Information Notice", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor FAX_MEMORANDUM = declare("Fax Memorandum", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor HOMEOWNERS_INSURANCE_QUOTE_PAGE = declare("Homeowners Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor CALIFORNIA_APPLICATION_FOR_UNIT_OWNERS = declare("California Application for Unit-Owners Insurance", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor OFFER_OF_EARTHQUAKE_COVERAGE = declare("Offer of Earthquake Coverage Condominium Basic Earthquake Policy", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PRIVACY_INFORMATION_NOTICE = declare("Privacy Information Notice", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor CONDOMINIUM_OWNERS_INSURANCE_QUOTE_PAGE = declare("Condominium Owners Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor APPLICATION_FOR_HOMEOWNERS_INSURANCE = declare("Application for Homeowners Insurance", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor APPRAISALS_RECEIPTS_FOR_SCHEDULED_PERSONAL_PROPERTY = declare("Appraisals/sales receipts for scheduled personal property items", RadioGroup.class, Waiters.NONE);
		}

		public static final class DocumentsToBind extends MetaData {
			public static final AttributeDescriptor PROOF_OF_UNDERLYING_INSURANCE_POLICY = declare("Proof of Underlying Insurance policy for the tenant(s) received?", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PROOF_OF_CENTRAL_FIRE_ALARM = declare("Proof of central fire alarm", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PROOF_OF_CENTRAL_THEFT_ALARM = declare("Proof of central theft alarm", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PROOF_OF_OCCUPATION = declare("Proof of occupation", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT = declare("Proof of subscription to fire department", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION = declare("Proof of home renovations for the Home Modernization discount", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PROOF_OF_ENERGY_STAR_APPLIANCES = declare("Proof of ENERGY STAR appliances or green home features", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PROOF_OF_COMPANION_DP3_POLICY = declare("Proof of companion DP3 policy", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PROOF_OF_COMPANION_PUP_POLICY = declare("Proof of companion PUP policy", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PROOF_OF_ACTIVE_AAA_AUTO_POLICY = declare("Proof of active AAA auto policy", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor CERTIFICATE_CONFIRMING_NO_CONTAMINATION_CAUSED_BY_STORAGE_TANK = declare("Certificate confirming no contamination caused by inactive underground oil storage tank", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PROOF_OF_PLUMBING_AND_OTHERS_RENOVATIONS = declare("Proof of plumbing, electrical, heating/cooling system and roof renovations", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PROOF_OF_AN_UNDERLYING_RENTERS_INSURANCE = declare("Proof of an underlying renters insurance policy for your tenant(s)", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ACTUAL_CASH_VALUE_LOSS_TO_ROOF_SURFACING = declare("Actual Cash Value Loss Settlement Windstorm Or Hail Losses To Roof Surfacing", RadioGroup.class, Waiters.NONE);
		}

		public static final class DocumentsToIssue extends MetaData {
			public static final AttributeDescriptor CEA_OFFER_DECLINATION = declare("CEA Offer Declination", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor CALIFORNIA_RESIDENTIAL_PROPERTY_INSURANCE_DISCLOSURE = declare("California Residential Property Insurance Disclosure", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor SIGNED_POLICY_APPLICATION = declare("Signed policy application", RadioGroup.class, Waiters.NONE);
		}
	}

	public static final class BindTab extends MetaData {
	}

	// policy Actions
	public static final class ExtensionRenewalActionTab extends MetaData {
	}

	public static final class CancelNoticeActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.NONE);
		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class, Waiters.NONE);
		public static final AttributeDescriptor DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.NONE);
	}

	public static final class RescindCancellationActionTab extends MetaData {
		public static final AttributeDescriptor RESCIND_CANCELLATION_DATE = declare("Rescind Cancellation Date", TextBox.class);
	}

	public static final class ChangeReinstatementLapsePeriodActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class);
		public static final AttributeDescriptor REINSTATEMENT_LAPSE_DATE = declare("Reinstatement Lapse Date", TextBox.class);
		public static final AttributeDescriptor REVISED_REINSTATEMENT_DATE = declare("Revised Reinstatement Date", TextBox.class);
		public static final AttributeDescriptor LAPSE_CHANGE_REASON = declare("Lapse Change Reason", ComboBox.class);
		public static final AttributeDescriptor OTHER = declare("Other", TextBox.class);
	}

	public static final class ChangeBrokerActionTab extends MetaData {
		public static final AttributeDescriptor TRANSFER_ID = declare("Transfer ID", TextBox.class);
		public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class);
		public static final AttributeDescriptor TRANSFER_TYPE = declare("Transfer type", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor OTHER_REASON = declare("Other Reason", TextBox.class);
		public static final AttributeDescriptor TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", TextBox.class);
		public static final AttributeDescriptor POLICY_EFFECTIVE_DATE = declare("Policy Effective Date", TextBox.class);
		public static final AttributeDescriptor COMMISSION_IMPACT = declare("Commission impact", RadioGroup.class);
		public static final AttributeDescriptor SOURCE_CHANNEL = declare("Source Channel", TextBox.class);
		public static final AttributeDescriptor SOURCE_LOCATION_TYPE = declare("Source Location Type", TextBox.class);
		public static final AttributeDescriptor SOURCE_LOCATION_NAME = declare("Source Location Name", TextBox.class);
		public static final AttributeDescriptor SOURCE_INSURANCE_AGENT = declare("Source Insurance Agent", TextBox.class);
		public static final AttributeDescriptor TARGET_CHANNEL = declare("Target channel", TextBox.class);
		public static final AttributeDescriptor TARGET_LOCATION_TYPE = declare("Target location type", TextBox.class);
		public static final AttributeDescriptor TARGET_LOCATION_NAME = declare("Target Location Name", TextBox.class);
		public static final AttributeDescriptor TARGET_INSURANCE_AGENT = declare("Target Insurance agent", ComboBox.class);
		public static final AttributeDescriptor TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
	}

	public static final class ReinstatementActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.NONE);
		public static final AttributeDescriptor REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
	}

	public static final class DeclineActionTab extends MetaData {
		public static final AttributeDescriptor DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AttributeDescriptor DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class GenerateProposalActionTab extends MetaData {
		public static final AttributeDescriptor NOTES = declare("Notes", TextBox.class);
	}

	public static final class ChangeRenewalLapseActionTab extends MetaData {
		public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		public static final AttributeDescriptor RENEWAL_LAPSE_DATE = declare("Renewal Lapse Date", TextBox.class);
		public static final AttributeDescriptor REVISED_RENEWAL_DATE = declare("Revised Renewal Date", TextBox.class);
		public static final AttributeDescriptor LAPSE_CHANGE_REASON = declare("Lapse Change Reason", ComboBox.class);
		public static final AttributeDescriptor OTHER = declare("Other", TextBox.class);
	}

	public static final class AddManualRenewFlagActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
	}

	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	public static final class DoNotRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class CancelActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
	}

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
	}

	public static final class CopyPolicyActionTab extends MetaData {
		public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class DeleteCancelNoticeActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	public static final class CopyQuoteActionTab extends MetaData {
		public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class Inspection2ndTabWizardStyleActionTab extends MetaData {
		public static final AttributeDescriptor DATE_ORDERED = declare("Date Ordered", TextBox.class);
		public static final AttributeDescriptor REPORT_TYPE = declare("Report Type", ComboBox.class);
		public static final AttributeDescriptor INSPECTION_REASON = declare("Inspection Reason", ComboBox.class);
		public static final AttributeDescriptor PRIMARY_CONTACT_NAME = declare("Primary Contact Name", TextBox.class);
		public static final AttributeDescriptor PRIMARY_CONTACT_PHONE = declare("Primary Contact Phone", TextBox.class);
		public static final AttributeDescriptor SECONDARY_CONTACT_NAME = declare("Secondary Contact Name", TextBox.class);
		public static final AttributeDescriptor SECONDARY_CONTACT_PHONE = declare("Secondary Contact Phone", TextBox.class);
		public static final AttributeDescriptor AGENT_CONTACT_NAME = declare("Agent Contact Name", TextBox.class);
		public static final AttributeDescriptor AGENT_CONTACT_PHONE = declare("Agent Contact Phone", TextBox.class);
		public static final AttributeDescriptor AGENT_CONTACT_E_MAIL = declare("Agent Contact E-mail", TextBox.class);
		public static final AttributeDescriptor UNDERWRITER_CONTACT_NAME = declare("Underwriter Contact Name", TextBox.class);
		public static final AttributeDescriptor UNDERWRITER_CONTACT_PHONE = declare("Underwriter Contact Phone", TextBox.class);
		public static final AttributeDescriptor UNDERWRITER_CONTACT_E_MAIL = declare("Underwriter Contact E-mail", TextBox.class);
		public static final AttributeDescriptor SPECIAL_INSTRUCTIONS = declare("Special Instructions", TextBox.class);
		public static final AttributeDescriptor INSPECTION_STATUS = declare("Inspection Status", TextBox.class);
	}

	public static final class IssueSummary3rdTabWizardStyleActionTab extends MetaData {
		public static final AttributeDescriptor ISSUE_DATE = declare("Issue Date", TextBox.class);
		public static final AttributeDescriptor METHOD_OF_DELIVERY = declare("Method Of Delivery", ComboBox.class);
		public static final AttributeDescriptor SEND_TO = declare("Send To", ComboBox.class);
		public static final AttributeDescriptor BROKER_EMAIL = declare("Broker Email", TextBox.class);
		public static final AttributeDescriptor INSURED_EMAIL = declare("Insured Email", TextBox.class);
		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
		public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip/Postal Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AttributeDescriptor NOTES = declare("Notes", TextBox.class);
	}

	public static final class DeletePendedTransactionActionTab extends MetaData {
	}

	public static final class RewriteActionTab extends MetaData {
		public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
	}

	public static final class RemoveDoNotRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class RemoveManualRenewFlagActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
	}

	public static final class PolicyDocGenActionTab extends MetaData {
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
