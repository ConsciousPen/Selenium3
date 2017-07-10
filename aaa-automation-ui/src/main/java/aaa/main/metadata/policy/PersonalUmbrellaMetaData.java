/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata.policy;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;

import aaa.main.metadata.DialogsMetaData.AddressValidationMetaData;
import aaa.toolkit.webdriver.customcontrols.AdditionalPoliciesMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.AssetListConfirmationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.DoubleTextBox;
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
public final class PersonalUmbrellaMetaData {

	public final static class PrefillTab extends MetaData {

		public static final AttributeDescriptor NAMED_INSURED = declare("NamedInsured", MultiInstanceAfterAssetList.class, NamedInsured.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAInsured']"));
		public static final AttributeDescriptor ACTIVE_UNDERLYING_PRIMARY_POLICY = declare("ActiveUnderlyingPrimaryPolicy", AdditionalPoliciesMultiAssetList.class, ActiveUnderlyingPolicies.class);
		public static final AttributeDescriptor ACTIVE_UNDERLYING_POLICIES = declare("ActiveUnderlyingPolicies", AdditionalPoliciesMultiAssetList.class, ActiveUnderlyingPolicies.class);
		
		public static final class NamedInsured extends MetaData {
			public static final AttributeDescriptor PREFIX = declare("Prefix", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor FIRST_NAME = declare("First name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MIDDLE_NAME = declare("Middle name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor LAST_NAME = declare("Last name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor MARITAL_STATUS = declare("Marital status", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of birth", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SOCIAL_SECURITY_NUMBER = declare("Social security number", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor OCCUPATION = declare("Occupation", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor D_O_OFFICERS_PRESIDENT_VP_SECRETARY_TREASURER = declare("D&O Officers - President, VP, Secretary, Treasurer", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor D_O_OTHER_THAN_OFFICERS_BOARD_MEMBERS = declare("D&O other than Officers - Board Members", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor EMPLOYER = declare("Employer", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor AAA_EMPLOYEE = declare("AAA employee", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor LLC = declare("LLC", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor TRUSTEE = declare("Trustee", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAInsured"));
		}

		public static final class ActiveUnderlyingPolicies extends MetaData {
			public static final AttributeDescriptor ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupPrefill"));
			public static final AttributeDescriptor ACTIVE_UNDERLYING_POLICIES_SEARCH = declare("ActiveUnderlyingPoliciesSearch", SingleSelectSearchDialog.class, ActiveUnderlyingPoliciesSearch.class, By.xpath(".//div[@id='pupPolicySearchPopup_container']"));
			public static final AttributeDescriptor ACTIVE_UNDERLYING_POLICIES_MANUAL = declare("ActiveUnderlyingPoliciesManual", AssetList.class, ActiveUnderlyingPoliciesManual.class);
			public static final AttributeDescriptor SAVE_BTN = declare("Save", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addManualPrefill"));

			public static final class ActiveUnderlyingPoliciesSearch extends MetaData {
				public static final AttributeDescriptor POLICY_TYPE = declare("Policy Type", ComboBox.class, Waiters.NONE);
				public static final AttributeDescriptor POLICY_NUMBER = declare("Policy Number", TextBox.class, Waiters.NONE);
			}

			public static final class ActiveUnderlyingPoliciesManual extends MetaData {
				public static final AttributeDescriptor POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
				public static final AttributeDescriptor POLICY_NUMBER = declare("Policy number", TextBox.class, Waiters.NONE);
				public static final AttributeDescriptor DWELLING_USAGE = declare("Dwelling usage", ComboBox.class, Waiters.NONE);
				public static final AttributeDescriptor OCCUPANCY_TYPE = declare("Occupancy type", ComboBox.class, Waiters.AJAX);
				public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/Data source", ComboBox.class, Waiters.NONE);
				public static final AttributeDescriptor PRIMARY_POLICY = declare("Primary policy", RadioGroup.class, Waiters.AJAX);
				public static final AttributeDescriptor RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class, Waiters.NONE);
			}
		}

		public enum ActiveUnderlyingPoliciesTblHeaders {
			OCCUPANCY_TYPE("Occupancy Type"), DWELLING_USAGE("Dwelling Usage"), SOURCE("Source"), PRIMARY("Primary"), STATUS("Status"), POLICY_TYPE("Policy Type"), POLICY_NUMBER("Policy Number");

			private String value;

			private ActiveUnderlyingPoliciesTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}
	}

	public static final class GeneralTab extends MetaData {
		public static final AttributeDescriptor POLICY_INFO = declare("PolicyInfo", AssetList.class, PolicyInfo.class);
		public static final AttributeDescriptor AAA_MEMBERSHIP = declare("AAAMembership", AssetList.class, AAAMembership.class);
		public static final AttributeDescriptor DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class);
		public static final AttributeDescriptor MAILING_ADDRESS = declare("MailingAddress", AssetList.class, MailingAddress.class, By.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOMailingAddressComponent']"));
		public static final AttributeDescriptor THIRD_PARTY_DESIGNEE = declare("ThirdPartyDesignee", AssetList.class, ThirdPartyDesignee.class);
		public static final AttributeDescriptor NAMED_INSURED_CONTACT_INFORMATION = declare("NamedInsuredContactInformation", AssetList.class, NamedInsuredContactInformation.class);
		
		public static final class PolicyInfo extends MetaData {
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor APPLICATION_TYPE = declare("Application type", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor UNDERWRITING_COMPANY = declare("Underwriting Company", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor CHANNEL_TYPE = declare("Channel Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor LOCATION = declare("Location", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor AGENCY = declare("Agency", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor AGENT_NUMBER = declare("Agent Number", StaticElement.class, Waiters.NONE);
			public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor AGENCY_LOCATION = declare("Agency Location", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor AGENT = declare("Agent", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COMMISSION_TYPE = declare("Commission Type", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor LEAD_SOURCE = declare("Lead source", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY_INSURANCE_SCORE = declare("Policy insurance score", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor OVERRIDE_POLICY_INSURANCE_SCORE = declare("Override policy insurance score", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor OVERRIDE_SCORE = declare("Override score", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor OVERRIDEN_BY = declare("Overriden by", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class, Waiters.NONE);
		}

		public static final class AAAMembership extends MetaData {
			public static final AttributeDescriptor CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor MEMBERSHIP_NUMBER = declare("Membership number", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor LAST_NAME = declare("Last name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MEMBER_SINCE_DATE = declare("Member Since Date", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MEMBERSHIP_EXPIRATION_DATE = declare("Membership Expiration date", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MEMBERSHIP_STATUS = declare("Membership Status", TextBox.class, Waiters.NONE);
		}

		public static final class DwellingAddress extends MetaData {
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.NONE);
		}

		public static final class MailingAddress extends MetaData {
			public static final AttributeDescriptor IS_DIFFERENT_MAILING_ADDRESS_RBTN = declare("Is the mailing address different from the dwelling address?", RadioGroup.class, Waiters.AJAX, false,
					By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestion_AAAHOMailingAddressComponent']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAHOMailingAddressComponent_Dialog_container"));
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COUNTY = declare("County", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address validated?", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOMailingAddressButtonUS"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class,
					By.id(".//form[@id='addressValidationFormAAAHOMailingAddressValidation']"));
		}

		public static final class ThirdPartyDesignee extends MetaData {
			public static final AttributeDescriptor NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class NamedInsuredContactInformation extends MetaData {
			public static final AttributeDescriptor HOME_PHONE = declare("Home phone", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor WORK_PHONE = declare("Work phone", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor MOBILE_PHONE = declare("Mobile phone", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor FAX = declare("Fax", TextBox.class, Waiters.AJAX);
		}
	}
	
	public static final class UnderlyingRisksPropertyTab extends MetaData {
		public static final AttributeDescriptor ADDITIONAL_RESIDENCIES = declare("AdditionalResidencies", MultiInstanceAfterAssetList.class, AdditionalResidencies.class, By.xpath(".//table[@id='policyDataGatherForm:formGrid_PupAdditionalDwelling']"));
		public static final AttributeDescriptor BUSINESS_OR_FARMING_COVERAGE = declare("BusinessOrFarmingCoverage", MultiInstanceAfterAssetList.class, BusinessOrFarmingCoverage.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupUnderlyingFormsMVO']"));
		public static final AttributeDescriptor PETS_OR_ANIMAL_INFORMATION = declare("PetsOrAnimalsInformation", MultiInstanceAfterAssetList.class, PetsOrAnimalsInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAADwellAnimalInfoComponent']"));
		public static final AttributeDescriptor RECREATIONAL_EQUIPMENT_INFORMATION = declare("RecreationalEquipmentInformation", MultiInstanceAfterAssetList.class, RecreationalEquipmentInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupRecreationalEquipmentComponent']"));

		public static final class AdditionalResidencies extends MetaData {
			public static final AttributeDescriptor ADD_RESIDENCE = declare("Add residence", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupAdditionalDwelling']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupAdditionalDwelling_Dialog_container"));
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NUMBER_OF_ADDITIONAL_RESIDENCES_ON_HARI = declare("Number of additional residences on HARI", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor PERSONAL_INJURY_ENDORSEMENT_DS_24_82 = declare("Personal Injury endorsement DS 24 82", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street Address 1", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street Address 2", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor COUNTY = declare("County", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor PRIMARY_POLICY = declare("Primary policy", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY_NUMBER = declare("Policy Number", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration date", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor OCCUPANCY_TYPE = declare("Occupancy type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NUMBER_OF_UNITS_ACRES = declare("Number of units/acres", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor LIMIT_OF_LIABILITY = declare("Limit of liability", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor DEDUCTIBLE = declare("Deductible", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY_TIER = declare("Policy tier", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor PUBLIC_PROTECTION_CLASS = declare("Public protection class", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAdditionalDwellingAddressButton"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class,
					By.id(".//div[@id='addressValidationPopupAAAPupAdditionalDwellAddressValidation_container']"));
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupAdditionalDwelling"));
			
		}	
		
		public static final class BusinessOrFarmingCoverage extends MetaData {
			public static final AttributeDescriptor ADD_BUSINESS_OR_FARMING_COVERAGES = declare("Business or farming coverages on underlying home policies", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAUnderlyingForm']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAUnderlyingForm_Dialog_container"));
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ENDORSEMENT = declare("Endorsement", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor PROPERTY_POLICY_NUMBER = declare("Property policy number", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupUnderlyingForm"));
		}	
		
		public static final class PetsOrAnimalsInformation extends MetaData {
			public static final AttributeDescriptor ARE_ANY_INSURED_OWNED_PETS = declare("Are any insured-owned pets or animals kept on the property", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAADwellAnimalInfoComponent']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAADwellAnimalInfoComponent_Dialog_container"));
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PROPERTY_POLICY_NUMBER = declare("Property policy number", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor ANIMAL_TYPE = declare("Animal type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor NAME = declare("Name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor OTHER_SPECIFY = declare("Other - specify", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor ANIMAL_COUNT = declare("Animal count", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor AGE = declare("Age", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAADwellAnimalInfoComponent"));
		}
		
		public static final class RecreationalEquipmentInformation extends MetaData {
			public static final AttributeDescriptor ADD_RECREATIONAL_EQUIPMENT = declare("Add recreational equipment", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupRecreationalEquipmentComponent']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupRecreationalEquipmentComponent_Dialog_container"));
			public static final AttributeDescriptor PROPERTY_POLICY_NUMBER = declare("Property policy number", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor RECREATIONAL_EQUIPMENT = declare("Recreational equipment", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor FREQUENCY_OF_POOL_PARTIES_WITH_10_OR_MORE_GUEST = declare("Frequency of pool parties with 10 or more guest", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupRecreationalEquipmentComponent"));
		}
		
		
		/*public static final class PupFirearms extends MetaData {
			public static final AttributeDescriptor NUMBER_OF_HAND_GUNS = declare("Number of hand guns", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NUMBER_OF_SHOT_GUNS = declare("Number of shot guns", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor NUMBER_OF_OTHER_GUNS = declare("Number of other guns", TextBox.class, Waiters.AJAX);
		}
		
		public static final class PupIncidentalOO extends MetaData {
			public static final AttributeDescriptor IS_THERE_AN_OFFICE_ON_PREMISES = declare("Is there an office on premises", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor FOOT_TRAFFIC_PER_WEEK = declare("Foot traffic per week", TextBox.class, Waiters.AJAX);
		}
		
		public static final class PupSwimmingPool extends MetaData {
			public static final AttributeDescriptor PROPERTY_POLICY_NUMBER = declare("Property policy number", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor RECREATIONAL_EQUIPMENT = declare("Recreational equipment", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", ComboBox.class, Waiters.AJAX);
		}*/
	}
	
	public static final class UnderlyingRisksAutoTab extends MetaData {
		public static final AttributeDescriptor DRIVERS = declare("Drivers", MultiInstanceAfterAssetList.class, Drivers.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupDriverMVO']"));
		public static final AttributeDescriptor AUTOMOBILES = declare("Automobiles", MultiInstanceAfterAssetList.class, Automobiles.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupAutomobileMVO']"));
		public static final AttributeDescriptor MOTORCYCLES = declare("Motorcycles", MultiInstanceAfterAssetList.class, Motorcycles.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupMotorcycle']"));
		public static final AttributeDescriptor MOTOR_HOMES = declare("MotorHomes", MultiInstanceAfterAssetList.class, MotorHomes.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupMotorhome']"));

		public static final class Drivers extends MetaData {
			public static final AttributeDescriptor ADD_DRIVERS = declare("Add drivers", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_Driver']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_Driver_Dialog_container"));
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor OCCUPATION = declare("Occupation", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor PHONE_NUMBER = declare("Phone Number", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor EMAIL = declare("Email", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor AGE_FIRST_LICENSED = declare("Age First Licensed", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor TOTAL_YEARS_OF_DRIVING_EXPERIENCE = declare("Total Years of Driving Experience", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor LICENSE_NUMBER = declare("License Number", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor LICENSE_STATUS = declare("License Status", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor LICENSE_SUSPENDED_DATE = declare("License Suspended Date", TextBox.class, Waiters.AJAX);
			/*public static final AttributeDescriptor ZIP_CODE = declare("Zip code", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);*/
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupDriver"));
		}
		
		public static final class Automobiles extends MetaData {
			public static final AttributeDescriptor ADD_AUTOMOBILE = declare("Add automobile", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupAutomobile']"));
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Add']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupAutomobile_Dialog_container"));
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor CAR_TYPE = declare("Car Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor VIN = declare("VIN", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor MAKE = declare("Make", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor MODEL = declare("Model", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor POLICY_NUM = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PRIMARY_AUTO_POLICY = declare("Primary Auto Policy", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor IS_THIS_A_SIGNATURE_SERIES_AUTO_POLICY = declare("Is this a signature series auto policy?", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor AUTO_TIER = declare("Auto Tier", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COVERAGE_TYPE = declare("Coverage Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor BI_LIMITS = declare("BI Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PD_LIMITS = declare("PD Limits", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor USAGE = declare("Usage", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.NONE);
			//public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Add']"));
		}
		
		public static final class Motorcycles extends MetaData {
			public static final AttributeDescriptor ADD_MOTORCYCLE = declare("Add motorcycle", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupMotorcycle']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupMotorcycle_Dialog_container"));
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor GUEST_PASSENGER_LIABILITY_COVERAGE = declare("Guest Passenger Liability Coverage", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor VIN = declare("VIN", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor YEAR = declare("Year", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MAKE = declare("Make", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MODEL = declare("Model", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor COVERAGE_TYPE = declare("Coverage Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor BI_LIMITS = declare("BI Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PD_LIMITS = declare("PD Limits", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor USAGE = declare("Usage", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupMotorcycle"));
		}
		
		public static final class MotorHomes extends MetaData {
			public static final AttributeDescriptor ADD_MOTORE_HOME = declare("Add motor home", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupMotorhome']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupMotorhome_Dialog_container"));
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor VIN = declare("VIN", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor YEAR = declare("Year", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MAKE = declare("Make", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MODEL = declare("Model", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COVERAGE_TYPE = declare("Coverage Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor BI_LIMITS = declare("BI Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PD_LIMITS = declare("PD Limits", TextBox.class, Waiters.AJAX);
			//public static final AttributeDescriptor UM_UIM_LIMITS = declare("UM/UIM Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupMotorhome"));
		}
	}
	
	
	public static final class UnderlyingRisksOtherVehiclesTab extends MetaData {
		public static final AttributeDescriptor WATERCRAFT = declare("Watercraft", MultiInstanceAfterAssetList.class, Watercraft.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupWatercraft']"));
		public static final AttributeDescriptor RECREATIONAL_VEHICLE = declare("RecreationalVehicle", MultiInstanceAfterAssetList.class, RecreationalVehicle.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupOffroad']"));
		
		
		public static final class Watercraft extends MetaData {
			public static final AttributeDescriptor ADD_WATERCRAFT = declare("Add watercraft", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupWatercraft']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupWatercraft_Dialog_container"));
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor HIN = declare("HIN", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor YEAR = declare("Year", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MAKE = declare("Make", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MODEL = declare("Model", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor LENGTH = declare("Length", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor HORSEPOWER = declare("Horsepower", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupWatercraft"));
		}	
		
		public static final class RecreationalVehicle extends MetaData {
			public static final AttributeDescriptor ADD_RECREATIONAL_VEHICLE = declare("Add recreational vehicle (off-road)", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupOffroad']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupOffroad_Dialog_container"));
			public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor VIN = declare("VIN", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor YEAR = declare("Year", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MAKE = declare("Make", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor MODEL = declare("Model", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor STATE = declare("State", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor COVERAGE_TYPE = declare("Coverage Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor BI_LIMITS = declare("BI Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor PD_LIMITS = declare("PD Limits", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor USAGE = declare("Usage", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupOffroad"));
		}
	
	}
	
	public static final class UnderlyingRisksAllResidentsTab extends MetaData {
	    public static final AttributeDescriptor FIRST_NAME = declare("First name", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor MIDDLE_NAME = declare("Middle name", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor LAST_NAME = declare("Last name", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of birth", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor OCCUPATION = declare("Occupation", ComboBox.class, Waiters.NONE);
        public static final AttributeDescriptor LICENSE_NUMBER = declare("License Number", TextBox.class, Waiters.NONE);
	}
	
	public static final class ClaimsTab extends MetaData {
		public static final AttributeDescriptor AUTO_VIOLATIONS_CLAIMS = declare("AutoViolationsClaims", MultiInstanceAfterAssetList.class, AutoViolationsClaims.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupDrivingRecord' or @id='policyDataGatherForm:componentView_PupDrivingRecordMVO']"));
		public static final AttributeDescriptor PROPERTY_CLAIMS = declare("PropertyClaims", MultiInstanceAfterAssetList.class, PropertyClaims.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOLossInfo']"));
		
		public static final class AutoViolationsClaims extends MetaData {
			public static final AttributeDescriptor ADD_AUTO_VIOLATION_CLAIM = declare("Add auto violations/claims", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupDrivingRecord']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupDrivingRecord_Dialog_container"));
			public static final AttributeDescriptor SOURCE = declare("Source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor SELECT_DRIVER = declare("Select Driver", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor DESCRIPTION = declare("Description", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor OCCURRENCE_DATE = declare("Occurrence Date", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor LOSS_PAYMENT_AMOUNT = declare("Loss Payment Amount", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor LIABILITY_CODE = declare("Liability Code", ComboBox.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD_AUTO_VIOLATION_CLAIM_INFORMATION = declare("Add Violation/Claim Information", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Add Violation/Claim Information']"));
		}
		
		public static final class PropertyClaims extends MetaData {
			public static final AttributeDescriptor ADD_LIABILITY_RELATED_PROPERTY_CLAIMS = declare("Add liability related property claims", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAHOLossInfo']"));
			public static final AttributeDescriptor REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAHOLossInfo_Dialog_container"));
			public static final AttributeDescriptor SOURCE = declare("Source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor PROPERTY_POLICY_NUMBER = declare("Property Policy Number", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor DATE_OF_LOSS = declare("Date of Loss", TextBox.class, Waiters.AJAX);
			public static final AttributeDescriptor CAUSE_OF_LOSS = declare("Cause of loss", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor AMOUNT_OF_LOSS = declare("Amount of loss", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor CLAIM_CARRIER = declare("Claim carrier", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor CLAIM_STATUS = declare("Claim status", ComboBox.class, Waiters.NONE);
			public static final AttributeDescriptor AAA_CLAIM = declare("AAA Claim", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor CATASTROPHE_LOSS = declare("Catastrophe Loss", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOLossInfo"));
		}
	}
	
	public static final class EndorsementsTab extends MetaData {
		
	}
	
	public static final class PremiumAndCoveragesQuoteTab extends MetaData {
		public static final AttributeDescriptor PAYMENT_PLAN = declare("Payment Plan", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor RECURRING_PAYMENT = declare("Recurring Payment", CheckBox.class, Waiters.AJAX);
		public static final AttributeDescriptor BILL_TO_AT_RENEWAL = declare("Bill to at renewal", ComboBox.class);
		public static final AttributeDescriptor PAYMENT_PLAN_AT_RENEWAL = declare("Payment plan at renewal", ComboBox.class, Waiters.AJAX);
		
		public static final AttributeDescriptor PERSONAL_UMBRELLA = declare("Personal Umbrella", ComboBox.class, Waiters.AJAX, true, By.id("policyDataGatherForm:pupCoverageDetail:0:pupTableCoverageLimitSelect"));
	}
	
	public static final class UnderwritingAndApprovalTab extends MetaData {
		
		public static final AttributeDescriptor HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED = declare("Have any applicants had a prior insurance policy cancelled, refused or non-renewed in the past 3 years?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor HAS_THE_APPLICANT_BEEN_SUED_FOR_LIBEL_OR_SLANDER = declare("Has the applicant been sued for libel or slander?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DOES_APPLICANT_OWN_ANY_PROPERTY_OUTSIDE_OF_THE_US = declare("Does applicant own any property outside of the U.S. or reside outside of the U.S. for more than 180 days per year (excluding Canada)?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_THERE_A_BUSINESS_ON_PREMISES = declare("Is there a business on premises?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DAY_CARE = declare("Day Care?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DO_YOU_HAVE_A_LICENSE = declare("Do you have a license?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor FARMING_RANCHING = declare("Farming/Ranching", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_IT_A_FOR_PROFIT_BUSINESS = declare("Is it a for-profit business?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor OTHERS = declare("Others", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_ANY_BUSINESS_HOME_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare("Is any business, home day care or farming activity conducted on the premises for which an endorsement is not already attached to the policy?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_CREATURE_OR_PERSON = declare("Have any of the applicant(s)' current pets injured, intentionally or unintentionally, another creature or person?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ARE_THERE_ANY_OWNED_LEASED_WATERCRAFT_USED_FOR_ANYTHING_OTHER_THAN_PERSONAL_PLEASURE_USE = declare("Are there any owned, leased or rented watercraft, personal watercraft, recreational vehicles, motorcycles or automobiles used for anything other than personal/pleasure use?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ARE_THERE_ANY_OWNED_LEASED_WATERCRAFT_WITHOUT_LIABILITY_COVERAGE = declare("Are there any owned, leased or rented watercraft, recreational vehicles, motorcycles or automobiles without liability coverage?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DO_ANY_APPLICANTS_USE_THEIR_PERSONAL_VEHICLES_FOR_WHOLESALE = declare("Do any applicants or drivers use their personal vehicles for wholesale or retail delivery of cargo or persons?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DO_ANY_APPLICANTS_OPERATE_A_COMMERCIAL_VEHICLE = declare("Do any applicants or drivers operate a commercial vehicle or a vehicle furnished by an employer?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DO_EMPLOYEES_OF_ANY_RESIDENT_RESIDE_IN_THE_DWELLING = declare("Do employees of any resident or applicant reside in the dwelling?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ANY_RESIDENT_SELF_EMPLOYED_OR_APPOINTED_PUBLIC_OFFICES = declare("Any resident self-employed, public lecturer, broadcaster, telecaster, newspaper reporter, editor, publisher, professional actor or entertainer, author, professional athlete or similar, or holder of any elected or appointed public offices?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor TOTAL_NUMBER_OF_PART_TIME_AND_FULL_TIME_RESIDENT_EMPLOYEES = declare("Total number of part time and full time resident employees", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ARE_ANY_APPLICANTS_OR_INSUREDS_A_CELEBRITY_OR_A_PUBLIC_FIGURE = declare("Are any applicants or insureds a celebrity or a public figure?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor APPLICANTS_WHO_HAVE_BEEN_CANCELLED = declare("Applicant(s), who have been cancelled, refused insurance or non-renewed in the past 3 years are ineligible if based on any of the following reasons: Fraud or Material Misrepresentation, Substantial Increase in Hazard, or Claims.", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare("Is any business or farming activity conducted on the premises?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare("Is any business, adult day care, pet day care or farming activity conducted on the premises?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor REMARKS = declare("Remarks", TextBox.class, Waiters.AJAX);
		
		public static final AttributeDescriptor REMARK_CANCELLED_POLICY = declare("Remark Cancelled Policy", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionPolicyCancelled_remarks']"));
		public static final AttributeDescriptor REMARK_PROPERTY_OUTSUDE_US = declare("Remark Property Outside US", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionForeignProperty_remarks']"));
		public static final AttributeDescriptor REMARK_NOT_PLEASURE_VEHICLE = declare("Remark Vehicles not for personal/pleasure use", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionVehicleForUnauthorizedUse_remarks']"));
		public static final AttributeDescriptor REMARK_COMMERCIAL_VEHICLE = declare("Remark Commercial Vehicle", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionCommercialVehicle_remarks']"));
		public static final AttributeDescriptor REMARK_RESIDENT_EMPLOYEES = declare("Remark Resident Employees", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionResidentEmployees_remarks']"));
		public static final AttributeDescriptor REMARK_CELEBRITY = declare("Remark Celebrity", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionCelebrityPublic_remarks']"));
	}
	
	
	public static final class DocumentsTab extends MetaData {
		
		public static final AttributeDescriptor DOCUMENTS_FOR_PRINTING = declare("DocumentsAvailableForPrinting", AssetList.class, DocumentsAvailableForPrinting.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODocGenPrint']"));
		public static final AttributeDescriptor REQUIRED_TO_BIND = declare("RequiredToBind", AssetList.class, RequiredToBind.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupOptionalSupportingDocuments']"));
		public static final AttributeDescriptor REQUIRED_TO_ISSUE = declare("RequiredToIssue", AssetList.class, RequiredToIssue.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupMandatorySupportingDocuments']"));
		
		public static final class DocumentsAvailableForPrinting extends MetaData {
			public static final AttributeDescriptor NAMED_DRIVER_EXCLUSION = declare("Named Driver Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor FAX_MEMORANDUM = declare("Fax Memorandum", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PERSONAL_UMBRELLA_LIABILITY_QUOTE_PAGE = declare("Personal Umbrella Liability Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor APPLICATION_FOR_PERSONAL_UMBRELLA_LIABILITY = declare("Application for Personal Umbrella Liability Insurance", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor CONSUMER_INFORMATION_NOTICE = declare("Consumer Information Notice", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DESIGNATED_MOTOR_VEHICLE_ENDORSEMENT = declare("Designated Recreational Motor Vehicle Exclusion Endorsement", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PROOF_FOR_VEHICLE_NOT_INSURED_WITH_AAA_AMIG = declare("Proof of underlying insurance for each vehicle/watercraft not insured with AAA/AMIG", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PUP_UM_UIM_DISCLOUSURE_SIGNATURE = declare("PUP UM/UIM Disclosure", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor THIRD_PARTY_DESIGNEE_FORM = declare("Third Party Designee Form", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor COVERAGE_ACCEPTANCE_STATEMENT = declare("Coverage Acceptance Statement", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PERSONAL_UMBRELLA_POLICY_APP = declare("Personal Umbrella Policy Application", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PUP_INSURANCE_QUOTE_PAGE = declare("PUP Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DESIGNATED_WATERCRAFT_EXCLUSION = declare("Designated Watercraft Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DESIGNATED_AUTOMOBILE_EXCLUSION = declare("Designated Automobile Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor ALL_HAZARD_EXCLUSION_ENDORSEMENT = declare("All Hazards Exclusion Endorsement", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DESIGNATED_INDIVIDUAL_EXCLUSION = declare("Designated Individual Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor DESIGNATED_ANIMAL_EXCLUSION = declare("Designated Animal Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor SIGNATURE_ON_FILE_FOR_DESIGNATED_RECREATIONAL_MOTOR_VEHICLE = declare("Signature on file for Designated Recreational Motor Vehicle", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor SIGNATURE_ON_FILE_FOR_DESIGNATED_AUTO = declare("Signature on file for Designated Auto", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor SIGNATURE_ON_FILE_FOR_DESIGNATED_WATERCRAFT = declare("Signature on file for Designated Watercraft", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor PROOF_OF_UNDERLYING_INSURANCE = declare("Proof of underlying insurance for each vehicle/watercraft not insured with AAA/AMIG", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_OF_INSURED_FOR_EXCESS_UNINSURED_OFFER = declare("Signature of insured for Excess Uninsured Motorists Offer", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_OF_INSURED_FOR_EXCESS_UNDERINSURED_OFFER = declare("Signature of insured for Excess Underinsured Motorists Offer", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor AUTOPAY_AUTHORIZATION_FORM = declare("AutoPay Authorization Form", RadioGroup.class, Waiters.NONE);
		}
		
		public static final class RequiredToBind extends MetaData {
			
		}

		public static final class RequiredToIssue extends MetaData {
			public static final AttributeDescriptor APPLICANT_STATEMENT_SIGNATURE = declare("Applicant's statement signature", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_FOR_INDIVIDUAL_EXCLUSION_ON_FILE = declare("Signature of named insured for Individual exclusion on file", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_ON_FILE_IF_AUTO_LIABILITY_IS_EXCLUDED = declare("Signature on file if Auto Liability is excluded", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_FOR_AUTOMOBILE_EXCLUSION_ON_FILE = declare("Signature of named insured for Automobile exclusion on file", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_ON_FILE_FOR_ALL_HAZZARDS = declare("Signature on file for All Hazards", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor SIGNATURE_FOR_ALL_HAZARDS_EXCLUSION_ON_FILE = declare("Signature of named insured for all hazards exclusion on file", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_FOR_ANIMAL_EXCLUSION_ON_FILE = declare("Signature of named insured for animal exclusion on file", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_FOR_WATERCRAFT_EXCLUSION_ON_FILE = declare("Signature of named insured for watercraft exclusion on file", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_FOR_MOTOR_VEHICLE_EXCLUSION_ON_FILE = declare("Signature of named insured for recreational motor vehicle exclusion on file", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_OF_EXCLUDED_NAMED_DRIVER_ON_FILE = declare("Signature of excluded named driver on file", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor PUP_UM_UIM_DISCLOSURE = declare("PUP UM/UIM Disclosure", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_OF_INSURED_FOR_EXCESS_UNINSURED_MOTORISTS_OFFER = declare("Signature of insured for Excess Uninsured Motorists Offer", RadioGroup.class, Waiters.NONE);
			public static final AttributeDescriptor SIGNATURE_OF_INSURED_FOR_EXCESS_UNDERINSURED_MOTORISTS_OFFER = declare("Signature of insured for Excess Underinsured Motorists Offer", RadioGroup.class, Waiters.NONE);
		}
	}
	
	public static final class BindTab extends MetaData {
		
	}

	///
	

	public static final class CancellationActionTab extends MetaData {
	    public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
        public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
	}

	public static final class ChangeReinstatementLapseActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class);
		public static final AttributeDescriptor REINSTATEMENT_LAPSE_DATE = declare("Reinstatement Lapse Date", TextBox.class);
		public static final AttributeDescriptor REVISED_REINSTATEMENT_DATE = declare("Revised Reinstatement Date", TextBox.class);
		public static final AttributeDescriptor LAPSE_CHANGE_REASON = declare("Lapse Change Reason", ComboBox.class);
		public static final AttributeDescriptor OTHER = declare("Other", TextBox.class);
	}

	public static final class DeclineByCustomerActionTab extends MetaData {
		public static final AttributeDescriptor DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AttributeDescriptor DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AttributeDescriptor DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class ManualRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
	}

	public static final class DeclineByCompanyActionTab extends MetaData {
		public static final AttributeDescriptor DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AttributeDescriptor DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AttributeDescriptor DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class DeleteCancelNoticeActionTab extends MetaData {
		public static final AttributeDescriptor CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	public static final class ExtensionRenewalActionTab extends MetaData {
	}

	public static final class CopyQuoteActionTab extends MetaData {
		public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class RemoveDoNotRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class CopyFromPolicyActionTab extends MetaData {
		public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	public static final class DeletePendedTransactionActionTab extends MetaData {
	}

	public static final class RescindCancellationActionTab extends MetaData {
		public static final AttributeDescriptor RESCIND_CANCELLATION_DATE = declare("Rescind Cancellation Date", TextBox.class);
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
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
		public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
		public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of Birth", TextBox.class);
		public static final AttributeDescriptor AGE = declare("Age", TextBox.class);
		public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class);
		public static final AttributeDescriptor MARITAL_STATUS = declare("Marital Status", ComboBox.class);
		public static final AttributeDescriptor OCCUPATION = declare("Occupation", ComboBox.class);
		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
		public static final AttributeDescriptor BUSINESS_TYPE = declare("Business Type", ComboBox.class);
		public static final AttributeDescriptor NAME = declare("Name", TextBox.class);
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
		public static final AttributeDescriptor SELECT_ADDITIONAL_INTEREST_PARTY = declare("Select 'Additional Interest' Party", ComboBox.class);
		public static final AttributeDescriptor RANK = declare("Rank", TextBox.class);
		public static final AttributeDescriptor INTEREST_TYPE = declare("Interest Type", ComboBox.class);
		public static final AttributeDescriptor LOAN = declare("Loan #", TextBox.class);
		public static final AttributeDescriptor LOAN_AMOUNT = declare("Loan Amount", TextBox.class);
		public static final AttributeDescriptor ADD_AS_ADDITIONAL_INSURED = declare("Add as Additional Insured", RadioGroup.class);
		public static final AttributeDescriptor WILL_THIS_MORTGAGEE_COMPANY_BE_PAYING_THE_POLICY_PREMIUM = declare("Will this Mortgagee Company be Paying the Policy Premium?", RadioGroup.class);
	}

	public static final class RollOnChangesActionTab extends MetaData {
	}

	public static final class RewriteActionTab extends MetaData {
	    public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
        public static final AttributeDescriptor REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
	}

	public static final class ChangePendedEndEffDateActionTab extends MetaData {
		public static final AttributeDescriptor ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
		public static final AttributeDescriptor ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
	}

	public static final class IssueActionTab extends MetaData {
	}

	public static final class DoNotRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class ReinstatementActionTab extends MetaData {
	    public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
	}

	public static final class ProposeActionTab extends MetaData {
		public static final AttributeDescriptor NOTES = declare("Notes", TextBox.class);
	}

	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	public static final class CancelNoticeActionTab extends MetaData {
	    public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
        public static final AttributeDescriptor CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.NONE);
        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.NONE);
	}

	public static final class RemoveManualRenewActionTab extends MetaData {
		public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
	}

	public static final class ChangeBrokerActionTab extends MetaData {
		public static final AttributeDescriptor TRANSFER_ID = declare("Transfer ID", TextBox.class);
		public static final AttributeDescriptor POLICY = declare("Policy #", TextBox.class);
		public static final AttributeDescriptor TRANSFER_TYPE = declare("Transfer Type", RadioGroup.class);
		public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
		public static final AttributeDescriptor TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", RadioGroup.class);
		public static final AttributeDescriptor TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
		public static final AttributeDescriptor TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AttributeDescriptor COMMISSION_IMPACT = declare("Commission Impact", RadioGroup.class);
		public static final AttributeDescriptor COMMISSION_OVERRIDE = declare("Commission Override", RadioGroup.class);
		public static final AttributeDescriptor SOURCE_CHANNEL = declare("Source Channel", TextBox.class);
		public static final AttributeDescriptor SOURCE_LOCATION_TYPE = declare("Source Location Type", TextBox.class);
		public static final AttributeDescriptor SOURCE_LOCATION_NAME = declare("Source Location Name", TextBox.class);
		public static final AttributeDescriptor SOURCE_INSURANCE_AGENT = declare("Source Insurance Agent", TextBox.class);
		public static final AttributeDescriptor TARGET_CHANNEL = declare("Target channel", TextBox.class);
		public static final AttributeDescriptor TARGET_LOCATION_TYPE = declare("Target location type", TextBox.class);
		public static final AttributeDescriptor TARGET_LOCATION_NAME = declare("Target location Name", TextBox.class);
		public static final AttributeDescriptor TARGET_INSURANCE_AGENT = declare("Target Insurance agent", ComboBox.class);
		public static final AttributeDescriptor LOCATION_NAME = declare("Location Name", DialogSingleSelector.class, ChangeLocationMetaData.class);
		public static final AttributeDescriptor NEW_BROKER = declare("New Broker", DialogSingleSelector.class, ChangeBrokerMetaData.class);
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

		public static final class ChangeBrokerMetaData extends MetaData {
			public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
			public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
			public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
			public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AttributeDescriptor CITY = declare("City", TextBox.class);
			public static final AttributeDescriptor STATE = declare("State", TextBox.class);

			public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By.id("policyDataGatherForm:changeBrokerLink_BrokerChangeAction"));
		}
	}

	public static final class BindActionTab extends MetaData {
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

	public static final class GenerateOnDemandDocumentActionTab extends MetaData {
	}
}
