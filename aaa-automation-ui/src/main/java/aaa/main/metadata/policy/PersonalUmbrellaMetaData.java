/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata.policy;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;
import aaa.common.pages.Page;
import aaa.main.enums.DocGenConstants;
import aaa.main.metadata.DialogsMetaData;
import aaa.toolkit.webdriver.customcontrols.*;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.AssetListConfirmationDialog;
import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import aaa.toolkit.webdriver.customcontrols.endorsements.PupEndorsementsMultiAssetList;
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
public final class PersonalUmbrellaMetaData {

	public static final class PrefillTab extends MetaData {

		public static final AssetDescriptor<MultiInstanceAfterAssetList> NAMED_INSURED = declare("NamedInsured", MultiInstanceAfterAssetList.class, NamedInsured.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAInsured']"));
		public static final AssetDescriptor<AdditionalPoliciesMultiAssetList> ACTIVE_UNDERLYING_PRIMARY_POLICY = declare("ActiveUnderlyingPrimaryPolicy", AdditionalPoliciesMultiAssetList.class, ActiveUnderlyingPolicies.class);
		public static final AssetDescriptor<AdditionalPoliciesMultiAssetList> ACTIVE_UNDERLYING_POLICIES = declare("ActiveUnderlyingPolicies", AdditionalPoliciesMultiAssetList.class, ActiveUnderlyingPolicies.class);
		
		public static final class NamedInsured extends MetaData {
			public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital status", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of birth", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER = declare("Social security number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> D_O_OFFICERS_PRESIDENT_VP_SECRETARY_TREASURER =
					declare("D&O Officers - President, VP, Secretary, Treasurer", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> D_O_OTHER_THAN_OFFICERS_BOARD_MEMBERS = declare("D&O other than Officers - Board Members", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EMPLOYER = declare("Employer", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AAA_EMPLOYEE = declare("AAA employee", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> LLC = declare("LLC", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> TRUSTEE = declare("Trustee", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAInsured"));
		}

		public static final class ActiveUnderlyingPolicies extends MetaData {
			public static final AssetDescriptor<Button> ADD_BTN = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupPrefill"));
			public static final AssetDescriptor<SingleSelectSearchDialog> ACTIVE_UNDERLYING_POLICIES_SEARCH = declare("ActiveUnderlyingPoliciesSearch", SingleSelectSearchDialog.class, ActiveUnderlyingPoliciesSearch.class, By.xpath(".//div[@id='pupPolicySearchPopup_container']"));
			public static final AssetDescriptor<AssetList> ACTIVE_UNDERLYING_POLICIES_MANUAL = declare("ActiveUnderlyingPoliciesManual", AssetList.class, ActiveUnderlyingPoliciesManual.class);
			public static final AssetDescriptor<Button> SAVE_BTN = declare("Save", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addManualPrefill"));

			public static final class ActiveUnderlyingPoliciesSearch extends MetaData {
				public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy Type", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy Number", TextBox.class, Waiters.AJAX);
			}

			public static final class ActiveUnderlyingPoliciesManual extends MetaData {
				public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy number", TextBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> DWELLING_USAGE = declare("Dwelling usage", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> OCCUPANCY_TYPE = declare("Occupancy type", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/Data source", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<RadioGroup> PRIMARY_POLICY = declare("Primary policy", RadioGroup.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class, Waiters.AJAX);
				public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			}
		}

		public enum ActiveUnderlyingPoliciesTblHeaders {
			OCCUPANCY_TYPE("Occupancy Type"), DWELLING_USAGE("Dwelling Usage"), SOURCE("Source"), PRIMARY("Primary"), STATUS("Status"), POLICY_TYPE("Policy Type"), POLICY_NUMBER("Policy Number");

			private final String value;

			ActiveUnderlyingPoliciesTblHeaders(String value) {
				this.value = value;
			}

			public String get() {
				return value;
			}
		}
	}

	public static final class GeneralTab extends MetaData {
		public static final AssetDescriptor<AssetList> POLICY_INFO = declare("PolicyInfo", AssetList.class, PolicyInfo.class);
		public static final AssetDescriptor<AssetList> AAA_MEMBERSHIP = declare("AAAMembership", AssetList.class, AAAMembership.class);
		public static final AssetDescriptor<AssetList> DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class);
		public static final AssetDescriptor<AssetList> MAILING_ADDRESS = declare("MailingAddress", AssetList.class, MailingAddress.class, By.xpath(".//table[@id='policyDataGatherForm:formGrid_AAAHOMailingAddressComponent']"));
		public static final AssetDescriptor<AssetList> THIRD_PARTY_DESIGNEE = declare("ThirdPartyDesignee", AssetList.class, ThirdPartyDesignee.class);
		public static final AssetDescriptor<AssetList> NAMED_INSURED_CONTACT_INFORMATION = declare("NamedInsuredContactInformation", AssetList.class, NamedInsuredContactInformation.class);
		public static final AssetDescriptor<TextBox> CONVERSION_DATE = declare("Conversion Date", TextBox.class, Waiters.AJAX);


		public static final class PolicyInfo extends MetaData {
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> APPLICATION_TYPE = declare("Application type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> UNDERWRITING_COMPANY = declare("Underwriting Company", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CHANNEL_TYPE = declare("Channel Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LOCATION = declare("Location", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENCY = declare("Agency", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> AGENT_NUMBER = declare("Agent Number", StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENCY_LOCATION = declare("Agency Location", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AGENT = declare("Agent", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COMMISSION_TYPE = declare("Commission Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LEAD_SOURCE = declare("Lead source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY_INSURANCE_SCORE = declare("Policy insurance score", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> OVERRIDE_POLICY_INSURANCE_SCORE = declare("Override policy insurance score", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OVERRIDE_SCORE = declare("Override score", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OVERRIDEN_BY = declare("Overriden by", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> TOLLFREE_NUMBER = declare("TollFree Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY_NUM = declare("Policy #", TextBox.class);

		}

		public static final class AAAMembership extends MetaData {
			public static final AssetDescriptor<ComboBox> CURRENT_AAA_MEMBER = declare("Current AAA Member", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MEMBERSHIP_NUMBER = declare("Membership number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MEMBER_SINCE_DATE = declare("Member Since Date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MEMBERSHIP_EXPIRATION_DATE = declare("Membership Expiration date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MEMBERSHIP_STATUS = declare("Membership Status", TextBox.class, Waiters.AJAX);
		}

		public static final class DwellingAddress extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_DWELLING_ADDRESS = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAdditionalDwellingAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG_DWELLING_ADDRESS = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class,
					By.id(" .//form[@id='addressValidationFormAAAHODwellAddressValidationComp']"));
		}

		public static final class MailingAddress extends MetaData {
			public static final AssetDescriptor<RadioGroup> IS_DIFFERENT_MAILING_ADDRESS_RBTN = declare("Is the mailing address different from the dwelling address?", RadioGroup.class, Waiters.AJAX, false,
					By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestion_AAAHOMailingAddressComponent']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAHOMailingAddressComponent_Dialog_container"));
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated?", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOMailingAddressButtonUS"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class,
					By.id(".//form[@id='addressValidationFormAAAHOMailingAddressValidation']"));
		}

		public static final class ThirdPartyDesignee extends MetaData {
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class NamedInsuredContactInformation extends MetaData {
			public static final AssetDescriptor<TextBox> HOME_PHONE = declare("Home phone", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> WORK_PHONE = declare("Work phone", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MOBILE_PHONE = declare("Mobile phone", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FAX = declare("Fax", TextBox.class, Waiters.AJAX);
		}
	}

	public static final class UnderlyingRisksPropertyTab extends MetaData {
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_RESIDENCIES = declare("AdditionalResidencies", MultiInstanceAfterAssetList.class, AdditionalResidencies.class, By.xpath(".//table[@id='policyDataGatherForm:formGrid_PupAdditionalDwelling']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> BUSINESS_OR_FARMING_COVERAGE = declare("BusinessOrFarmingCoverage", MultiInstanceAfterAssetList.class, BusinessOrFarmingCoverage.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupUnderlyingFormsMVO']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> PETS_OR_ANIMAL_INFORMATION = declare("PetsOrAnimalsInformation", MultiInstanceAfterAssetList.class, PetsOrAnimalsInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAADwellAnimalInfoComponent']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> RECREATIONAL_EQUIPMENT_INFORMATION = declare("RecreationalEquipmentInformation", MultiInstanceAfterAssetList.class, RecreationalEquipmentInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupRecreationalEquipmentComponent']"));

		public static final class AdditionalResidencies extends MetaData {
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupAdditionalDwelling"));
			public static final AssetDescriptor<RadioGroup> ADD_RESIDENCE = declare("Add residence", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupAdditionalDwelling']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupAdditionalDwelling_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_ADDITIONAL_RESIDENCES_ON_HARI = declare("Number of additional residences on HARI", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PERSONAL_INJURY_ENDORSEMENT_DS_24_82 = declare("Personal Injury endorsement DS 24 82", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street Address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street Address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PRIMARY_POLICY = declare("Primary policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration date", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> OCCUPANCY_TYPE = declare("Occupancy type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_UNITS_ACRES = declare("Number of units/acres", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LIMIT_OF_LIABILITY = declare("Limit of liability", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DEDUCTIBLE = declare("Deductible", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY_TIER = declare("Policy tier", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PUBLIC_PROTECTION_CLASS = declare("Public protection class", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateAdditionalDwellingAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, DialogsMetaData.AddressValidationMetaData.class,
					By.id(".//div[@id='addressValidationPopupAAAPupAdditionalDwellAddressValidation_container']"));
			
		}	
		
		public static final class BusinessOrFarmingCoverage extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_BUSINESS_OR_FARMING_COVERAGES = declare("Business or farming coverages on underlying home policies", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAUnderlyingForm']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAUnderlyingForm_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> ENDORSEMENT = declare("Endorsement", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PROPERTY_POLICY_NUMBER = declare("Property policy number", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupUnderlyingForm"));
		}	
		
		public static final class PetsOrAnimalsInformation extends MetaData {
			public static final AssetDescriptor<RadioGroup> ARE_ANY_INSURED_OWNED_PETS = declare("Are any insured-owned pets or animals kept on the property", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAADwellAnimalInfoComponent']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAADwellAnimalInfoComponent_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PROPERTY_POLICY_NUMBER = declare("Property policy number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> ANIMAL_TYPE = declare("Animal type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OTHER_SPECIFY = declare("Other - specify", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ANIMAL_COUNT = declare("Animal count", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> AGE = declare("Age", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAADwellAnimalInfoComponent"));
		}
		
		public static final class RecreationalEquipmentInformation extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_RECREATIONAL_EQUIPMENT = declare("Add recreational equipment", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupRecreationalEquipmentComponent']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupRecreationalEquipmentComponent_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PROPERTY_POLICY_NUMBER = declare("Property policy number", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> RECREATIONAL_EQUIPMENT = declare("Recreational equipment", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DESCRIPTION = declare("Description", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> FREQUENCY_OF_POOL_PARTIES_WITH_10_OR_MORE_GUEST = declare("Frequency of pool parties with 10 or more guest", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupRecreationalEquipmentComponent"));
		}
		
		
		/*public static final class PupFirearms extends MetaData {
			public static final AssetDescriptor<TextBox> NUMBER_OF_HAND_GUNS = declare("Number of hand guns", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_SHOT_GUNS = declare("Number of shot guns", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NUMBER_OF_OTHER_GUNS = declare("Number of other guns", TextBox.class, Waiters.AJAX);
		}
		
		public static final class PupIncidentalOO extends MetaData {
			public static final AssetDescriptor<RadioGroup> IS_THERE_AN_OFFICE_ON_PREMISES = declare("Is there an office on premises", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FOOT_TRAFFIC_PER_WEEK = declare("Foot traffic per week", TextBox.class, Waiters.AJAX);
		}
		
		public static final class PupSwimmingPool extends MetaData {
			public static final AssetDescriptor<ComboBox> PROPERTY_POLICY_NUMBER = declare("Property policy number", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> RECREATIONAL_EQUIPMENT = declare("Recreational equipment", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> DESCRIPTION = declare("Description", ComboBox.class, Waiters.AJAX);
		}*/
	}
	
	public static final class UnderlyingRisksAutoTab extends MetaData {
		public static final AssetDescriptor<MultiInstanceAfterAssetList> DRIVERS = declare("Drivers", MultiInstanceAfterAssetList.class, Drivers.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupDriverMVO']"));
		public static final AssetDescriptor<FillableTable> LIST_OF_AUTOMOBILES = declare("List of Automobiles", FillableTable.class, ListOfAutomobilesRow.class, By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPupAutomobile']/div/table"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> AUTOMOBILES = declare("Automobiles", MultiInstanceAfterAssetList.class, Automobiles.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupAutomobileMVO']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> MOTORCYCLES = declare("Motorcycles", MultiInstanceAfterAssetList.class, Motorcycles.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupMotorcycle']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> MOTOR_HOMES = declare("MotorHomes", MultiInstanceAfterAssetList.class, MotorHomes.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupMotorhome']"));

		public static final class Drivers extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_DRIVERS = declare("Add drivers", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_Driver']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_Driver_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PHONE_NUMBER = declare("Phone Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> AGE_FIRST_LICENSED = declare("Age First Licensed", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> TOTAL_YEARS_OF_DRIVING_EXPERIENCE = declare("Total Years of Driving Experience", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LICENSE_NUMBER = declare("License Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LICENSE_STATUS = declare("License Status", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LICENSE_SUSPENDED_DATE = declare("License Suspended Date", TextBox.class, Waiters.AJAX);
			/*public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);*/
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupDriver"));
		}

		public static final class ListOfAutomobilesRow extends MetaData {
			public static final AssetDescriptor<StaticElement> NUM_COLUMN = declare("column=1", StaticElement.class);
			public static final AssetDescriptor<StaticElement> YEAR = declare("Year", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MAKE = declare("Make", StaticElement.class);
			public static final AssetDescriptor<StaticElement> MODEL = declare("Model", StaticElement.class);
			public static final AssetDescriptor<StaticElement> TYPE = declare("Type", StaticElement.class);
			public static final AssetDescriptor<StaticElement> CURRENT_CARRIER = declare("Current Carrier", StaticElement.class);
			public static final AssetDescriptor<StaticElement> POLICY_NUM = declare("Policy #", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXCLUDED = declare("Excluded", StaticElement.class);
			public static final AssetDescriptor<Link> ACTION_COLUMN = declare("column=9", Link.class);
			public static final AssetDescriptor<Button> CONFIRM_REMOVE = declare("Confirm Remove", Button.class, Waiters.AJAX, false, By.id("confirmEliminateInstance_Dialog_form:buttonYes"));
		}
		
		public static final class Automobiles extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_AUTOMOBILE = declare("Add automobile", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupAutomobile']"));
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Add']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupAutomobile_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CAR_TYPE = declare("Car Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> MAKE = declare("Make", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> MODEL = declare("Model", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY_NUM = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PRIMARY_AUTO_POLICY = declare("Primary Auto Policy", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> IS_THIS_A_SIGNATURE_SERIES_AUTO_POLICY = declare("Is this a signature series auto policy?", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> AUTO_TIER = declare("Auto Tier", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_TYPE = declare("Coverage Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<DoubleTextBox> BI_LIMITS = declare("BI Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PD_LIMITS = declare("PD Limits", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> USAGE = declare("Usage", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
			//public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, true, By.xpath(".//input[@value='Add']"));
		}
		
		public static final class Motorcycles extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_MOTORCYCLE = declare("Add motorcycle", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupMotorcycle']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupMotorcycle_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> GUEST_PASSENGER_LIABILITY_COVERAGE = declare("Guest Passenger Liability Coverage", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_TYPE = declare("Coverage Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<DoubleTextBox> BI_LIMITS = declare("BI Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PD_LIMITS = declare("PD Limits", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> USAGE = declare("Usage", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupMotorcycle"));
		}
		
		public static final class MotorHomes extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_MOTORE_HOME = declare("Add motor home", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupMotorhome']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupMotorhome_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_TYPE = declare("Coverage Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<DoubleTextBox> BI_LIMITS = declare("BI Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PD_LIMITS = declare("PD Limits", TextBox.class, Waiters.AJAX);
			//public static final AssetDescriptor<DoubleTextBox> UM_UIM_LIMITS = declare("UM/UIM Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupMotorhome"));
		}
	}
	
	
	public static final class UnderlyingRisksOtherVehiclesTab extends MetaData {
		public static final AssetDescriptor<MultiInstanceAfterAssetList> WATERCRAFT = declare("Watercraft", MultiInstanceAfterAssetList.class, Watercraft.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupWatercraft']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> RECREATIONAL_VEHICLE = declare("RecreationalVehicle", MultiInstanceAfterAssetList.class, RecreationalVehicle.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupOffroad']"));
		
		
		public static final class Watercraft extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_WATERCRAFT = declare("Add watercraft", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupWatercraft']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupWatercraft_Dialog_container"));
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HIN = declare("HIN", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> LENGTH = declare("Length", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> HORSEPOWER = declare("Horsepower", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupWatercraft"));
		}	
		
		public static final class RecreationalVehicle extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_RECREATIONAL_VEHICLE = declare("Add recreational vehicle (off-road)", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupOffroad']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupOffroad_Dialog_container"));
			public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> EXCLUDED = declare("Excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CURRENT_CARRIER = declare("Current Carrier", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> COVERAGE_TYPE = declare("Coverage Type", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> COMBINED_SINGLE_LIMIT = declare("Combined Single Limit", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<DoubleTextBox> BI_LIMITS = declare("BI Limits", DoubleTextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PD_LIMITS = declare("PD Limits", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> USAGE = declare("Usage", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addPupOffroad"));
		}
	
	}
	
	public static final class UnderlyingRisksAllResidentsTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> ADD_OTHER_RESIDENTS = declare("Add other residents", RadioGroup.class);
		public static final AssetDescriptor<Button> CONFIRM_NO_RESIDENTS = declare("Confirm no residents", Button.class, Waiters.AJAX, false, By.id("dataGatherViewConfirm_Dialog_form:buttonYes"));
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First name", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle name", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last name", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of birth", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> LICENSE_NUMBER = declare("License Number", TextBox.class, Waiters.AJAX);
	}
	
	public static final class ClaimsTab extends MetaData {
		public static final AssetDescriptor<AddAutoViolationsClaimsMultiAssetList> AUTO_VIOLATIONS_CLAIMS =
				declare("AutoViolationsClaims", AddAutoViolationsClaimsMultiAssetList.class, AutoViolationsClaims.class, By
						.xpath(".//div[@id='policyDataGatherForm:componentView_PupDrivingRecord' or @id='policyDataGatherForm:componentView_PupDrivingRecordMVO']"));
		public static final AssetDescriptor<MultiInstanceAfterAssetList> PROPERTY_CLAIMS = declare("PropertyClaims", MultiInstanceAfterAssetList.class, PropertyClaims.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOLossInfo']"));
		
		public static final class AutoViolationsClaims extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_AUTO_VIOLATION_CLAIM = declare("Add auto violations/claims", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_PupDrivingRecord']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_PupDrivingRecord_Dialog_container"));
			public static final AssetDescriptor<ComboBox> SOURCE = declare("Source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> SELECT_DRIVER = declare("Select Driver", ComboBox.class, Waiters.AJAX, false, By.xpath("//span[@id='policyDataGatherForm:driverListLabel']/../../td[2]/select"));
			public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class, Waiters.AJAX, false, By.id("policyDataGatherForm:drivingRecordMVO_drivingRecord_activityType"));
			public static final AssetDescriptor<ComboBox> DESCRIPTION = declare("Description", ComboBox.class, Waiters.AJAX, false, By.id("policyDataGatherForm:drivingRecordMVO_drivingRecord_incidentDescription"));
			public static final AssetDescriptor<TextBox> OCCURRENCE_DATE = declare("Occurrence Date", TextBox.class, Waiters.AJAX, false, By.id("policyDataGatherForm:drivingRecordMVO_drivingRecord_accidentViolationDtInputDate"));
			public static final AssetDescriptor<TextBox> LOSS_PAYMENT_AMOUNT =
					declare("Loss Payment Amount", TextBox.class, Waiters.AJAX, false, By.id("policyDataGatherForm:drivingRecordMVO_drivingRecord_lossPaymentAmt"));
			public static final AssetDescriptor<ComboBox> LIABILITY_CODE = declare("Liability Code", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD_AUTO_VIOLATION_CLAIM_INFORMATION = declare("Add Violation/Claim Information", Button.class, Waiters.AJAX, true, By.xpath("//input[@value='Add Violation/Claim Information']"));
		}
		
		public static final class PropertyClaims extends MetaData {
			public static final AssetDescriptor<RadioGroup> ADD_LIABILITY_RELATED_PROPERTY_CLAIMS = declare("Add liability related property claims", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAHOLossInfo']"));
			public static final AssetDescriptor<AssetListConfirmationDialog> REMOVE_CONFIRMATION = declare("Remove confirmation", AssetListConfirmationDialog.class, Waiters.AJAX, false, By.id("confirmOptionalNoSelected_AAAHOLossInfo_Dialog_container"));
			public static final AssetDescriptor<ComboBox> SOURCE = declare("Source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> PREFILL_DATA_SOURCE = declare("Prefill/data source", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> PROPERTY_POLICY_NUMBER = declare("Property Policy Number", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DATE_OF_LOSS = declare("Date of Loss", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CAUSE_OF_LOSS = declare("Cause of loss", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> AMOUNT_OF_LOSS = declare("Amount of loss", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CLAIM_CARRIER = declare("Claim carrier", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> CLAIM_STATUS = declare("Claim status", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AAA_CLAIM = declare("AAA Claim", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CATASTROPHE_LOSS = declare("Catastrophe Loss", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> ADD = declare("Add", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:addAAAHOLossInfo"));
		}
	}
	
	public static final class EndorsementsTab extends MetaData {

		public static final AssetDescriptor<PupEndorsementsMultiAssetList> F1752C = declare("F1752C", PupEndorsementsMultiAssetList.class, EndorsementF1752C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> F1755C = declare("F1755C", PupEndorsementsMultiAssetList.class, EndorsementF1755C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> F1757C = declare("F1757C", PupEndorsementsMultiAssetList.class, EndorsementF1757C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> F1758C = declare("F1758C", PupEndorsementsMultiAssetList.class, EndorsementF1758C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> F1759C = declare("F1759C", PupEndorsementsMultiAssetList.class, EndorsementF1759C.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> PS_09_22 = declare("PS 09 22", PupEndorsementsMultiAssetList.class, EndorsementPS0922.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		//		public static final AssetDescriptor<PupEndorsementsMultiAssetList> PS_09_23 = declare("PS 09 23", PupEndorsementsMultiAssetList.class, EndorsementPS0923.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> PS_98_11 = declare("PS 98 11", PupEndorsementsMultiAssetList.class, EndorsementPS9811.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> PS_98_13 = declare("PS 98 13", PupEndorsementsMultiAssetList.class, EndorsementPS9813.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> PS_98_14 = declare("PS 98 14", PupEndorsementsMultiAssetList.class, EndorsementPS9814.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> PS_98_15 = declare("PS 98 15", PupEndorsementsMultiAssetList.class, EndorsementPS9815.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AssetDescriptor<PupEndorsementsMultiAssetList> _58_1027_CA = declare("58 1027 CA", PupEndorsementsMultiAssetList.class, Endorsement581027CA.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

		public static final class EndorsementPS9813 extends MetaData {
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementPS9814 extends MetaData {
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementPS9815 extends MetaData {
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> HIN = declare("HIN", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementPS9811 extends MetaData {
			public static final AssetDescriptor<TextBox> REASON_FOR_EXCLUSION = declare("Reason for exclusion", TextBox.class, Waiters.AJAX);
		}

		public static final class Endorsement581027CA extends MetaData {
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementPS0922 extends MetaData {
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LICENSE_NUMBER = declare("License Number", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementF1758C extends MetaData {
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street Address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street Address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
		}

		public static final class EndorsementF1757C extends MetaData {
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementF1759C extends MetaData {
			public static final AssetDescriptor<TextBox> ANIMAL_TYPE = declare("Animal Type", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> AGE = declare("Age", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementF1752C extends MetaData {
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of Birth", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> LICENCE_NUMBER = declare("License Number", TextBox.class, Waiters.AJAX);
		}

		public static final class EndorsementF1755C extends MetaData {
			public static final AssetDescriptor<TextBox> YEAR = declare("Year", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MAKE = declare("Make", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> MODEL = declare("Model", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> VIN = declare("VIN", TextBox.class, Waiters.AJAX);
		}
	}
	
	public static final class PremiumAndCoveragesQuoteTab extends MetaData {
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<CheckBox> RECURRING_PAYMENT = declare("Recurring Payment", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> BILL_TO_AT_RENEWAL = declare("Bill to at renewal", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN_AT_RENEWAL = declare("Payment plan at renewal", ComboBox.class, Waiters.AJAX);
		
		public static final AssetDescriptor<ComboBox> PERSONAL_UMBRELLA = declare("Personal Umbrella", ComboBox.class, Waiters.AJAX, true, By.id("policyDataGatherForm:pupCoverageDetail:0:pupTableCoverageLimitSelect"));
		//public static final AssetDescriptor<JavaScriptButton> CALCULATE_PREMIUM = declare("Calculate Premium", JavaScriptButton.class, Waiters.AJAX, By.id("policyDataGatherForm:actionButton_AAAPUPRateAction")); 
		public static final AssetDescriptor<JavaScriptButton> CALCULATE_PREMIUM = declare("Calculate Premium", JavaScriptButton.class, Waiters.AJAX, By.xpath("//input[@id='policyDataGatherForm:actionButton_AAAPUPRateAction'or @id='policyDataGatherForm:calculatePremium_AAAPUPRateAction']"));
		public static final AssetDescriptor<DialogAssetList> OVERRRIDE_PREMIUM_DIALOG = declare("OverridePremium", DialogAssetList.class, OverridePremiumDialog.class,
				By.xpath("//form[@id='premiumOverrideInfoFormAAAPUPPremiumOverride']"));
		public static final class OverridePremiumDialog extends MetaData {
			public static final AssetDescriptor<ComboBox> REASON_FOR_OVERRIDE = declare("Reason for override", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> REMARKS = declare("Remarks", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> OVERRIDE_PREMIUM_BY_FLAT_AMOUNT = declare("Override Premium By Flat Amount", TextBox.class, Waiters.AJAX, By.id("premiumOverrideInfoFormAAAPUPPremiumOverride:deltaPremiumAmt"));
			public static final AssetDescriptor<TextBox> OVERRIDE_PERCENTAGE = declare("Percentage", TextBox.class, Waiters.AJAX, By.id("premiumOverrideInfoFormAAAPUPPremiumOverride:percentageAmt"));
	     }
	}
	
	public static final class UnderwritingAndApprovalTab extends MetaData {
		
		public static final AssetDescriptor<RadioGroup> HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED = declare("Have any applicants had a prior insurance policy cancelled, refused or non-renewed in the past 3 years?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> HAS_THE_APPLICANT_BEEN_SUED_FOR_LIBEL_OR_SLANDER = declare("Has the applicant been sued for libel or slander?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DOES_APPLICANT_OWN_ANY_PROPERTY_OUTSIDE_OF_THE_US = declare("Does applicant own any property outside of the U.S. or reside outside of the U.S. for more than 180 days per year (excluding Canada)?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_THERE_A_BUSINESS_ON_PREMISES = declare("Is there a business on premises?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DAY_CARE = declare("Day Care?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DO_YOU_HAVE_A_LICENSE = declare("Do you have a license?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> FARMING_RANCHING = declare("Farming/Ranching", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_IT_A_FOR_PROFIT_BUSINESS = declare("Is it a for-profit business?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> OTHERS = declare("Others", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS_HOME_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare("Is any business, home day care or farming activity conducted on the premises for which an endorsement is not already attached to the policy?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_ANOTHER_CREATURE_OR_PERSON = declare("Have any of the applicant(s)' current pets injured, intentionally or unintentionally, another creature or person?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_OWNED_LEASED_WATERCRAFT_USED_FOR_ANYTHING_OTHER_THAN_PERSONAL_PLEASURE_USE = declare("Are there any owned, leased or rented watercraft, personal watercraft, recreational vehicles, motorcycles or automobiles used for anything other than personal/pleasure use?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ARE_THERE_ANY_OWNED_LEASED_WATERCRAFT_WITHOUT_LIABILITY_COVERAGE = declare("Are there any owned, leased or rented watercraft, recreational vehicles, motorcycles or automobiles without liability coverage?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DO_ANY_APPLICANTS_USE_THEIR_PERSONAL_VEHICLES_FOR_WHOLESALE = declare("Do any applicants or drivers use their personal vehicles for wholesale or retail delivery of cargo or persons?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DO_ANY_APPLICANTS_OPERATE_A_COMMERCIAL_VEHICLE = declare("Do any applicants or drivers operate a commercial vehicle or a vehicle furnished by an employer?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DO_EMPLOYEES_OF_ANY_RESIDENT_RESIDE_IN_THE_DWELLING = declare("Do employees of any resident or applicant reside in the dwelling?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ANY_RESIDENT_SELF_EMPLOYED_OR_APPOINTED_PUBLIC_OFFICES = declare("Any resident self-employed, public lecturer, broadcaster, telecaster, newspaper reporter, editor, publisher, professional actor or entertainer, author, professional athlete or similar, or holder of any elected or appointed public offices?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> TOTAL_NUMBER_OF_PART_TIME_AND_FULL_TIME_RESIDENT_EMPLOYEES = declare("Total number of part time and full time resident employees", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ARE_ANY_APPLICANTS_OR_INSUREDS_A_CELEBRITY_OR_A_PUBLIC_FIGURE = declare("Are any applicants or insureds a celebrity or a public figure?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> APPLICANTS_WHO_HAVE_BEEN_CANCELLED = declare("Applicant(s), who have been cancelled, refused insurance or non-renewed in the past 3 years are ineligible if based on any of the following reasons: Fraud or Material Misrepresentation, Substantial Increase in Hazard, or Claims.", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare("Is any business or farming activity conducted on the premises?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES = declare("Is any business, adult day care, pet day care or farming activity conducted on the premises?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> REMARKS = declare("Remarks", TextBox.class, Waiters.AJAX);
		
		public static final AssetDescriptor<TextBox> REMARK_CANCELLED_POLICY = declare("Remark Cancelled Policy", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionPolicyCancelled_remarks']"));
		public static final AssetDescriptor<TextBox> REMARK_CANCELLED_POLICY_EXTN = declare("Remark Cancelled Policy Extn", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionPolicyCancelledExtn_remarks']"));
		public static final AssetDescriptor<TextBox> REMARK_PROPERTY_OUTSUDE_US = declare("Remark Property Outside US", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionForeignProperty_remarks']"));
		public static final AssetDescriptor<TextBox> REMARK_NOT_PLEASURE_VEHICLE = declare("Remark Vehicles not for personal/pleasure use", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionVehicleForUnauthorizedUse_remarks']"));
		public static final AssetDescriptor<TextBox> REMARK_COMMERCIAL_VEHICLE = declare("Remark Commercial Vehicle", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionCommercialVehicle_remarks']"));
		public static final AssetDescriptor<TextBox> REMARK_RESIDENT_EMPLOYEES = declare("Remark Resident Employees", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionResidentEmployees_remarks']"));
		public static final AssetDescriptor<TextBox> REMARK_CELEBRITY = declare("Remark Celebrity", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:underwritingQuestion_UWQuestionCelebrityPublic_remarks']"));
	}
	
	
	public static final class DocumentsTab extends MetaData {
		
		public static final AssetDescriptor<AssetList> DOCUMENTS_FOR_PRINTING = declare("DocumentsAvailableForPrinting", AssetList.class, DocumentsAvailableForPrinting.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODocGenPrint']"));
		public static final AssetDescriptor<AssetList> REQUIRED_TO_BIND = declare("RequiredToBind", AssetList.class, RequiredToBind.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupOptionalSupportingDocuments']"));
		public static final AssetDescriptor<AssetList> REQUIRED_TO_ISSUE = declare("RequiredToIssue", AssetList.class, RequiredToIssue.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_PupMandatorySupportingDocuments']"));
		
		public static final class DocumentsAvailableForPrinting extends MetaData {
			public static final AssetDescriptor<RadioGroup> NAMED_DRIVER_EXCLUSION = declare("Named Driver Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> FAX_MEMORANDUM = declare("Fax Memorandum", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PERSONAL_UMBRELLA_LIABILITY_QUOTE_PAGE = declare("Personal Umbrella Liability Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> APPLICATION_FOR_PERSONAL_UMBRELLA_LIABILITY = declare("Application for Personal Umbrella Liability Insurance", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> CONSUMER_INFORMATION_NOTICE = declare("Consumer Information Notice", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DESIGNATED_MOTOR_VEHICLE_ENDORSEMENT = declare("Designated Recreational Motor Vehicle Exclusion Endorsement", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_FOR_VEHICLE_NOT_INSURED_WITH_AAA_AMIG =
					declare("Proof of underlying insurance for each vehicle/watercraft not insured with AAA/AMIG", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PUP_UM_UIM_DISCLOUSURE_SIGNATURE = declare("PUP UM/UIM Disclosure", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> THIRD_PARTY_DESIGNEE_FORM = declare("Third Party Designee Form", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> COVERAGE_ACCEPTANCE_STATEMENT = declare("Coverage Acceptance Statement", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PERSONAL_UMBRELLA_POLICY_APP = declare("Personal Umbrella Policy Application", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PUP_INSURANCE_QUOTE_PAGE = declare("PUP Insurance Quote Page", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DESIGNATED_WATERCRAFT_EXCLUSION = declare("Designated Watercraft Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DESIGNATED_AUTOMOBILE_EXCLUSION = declare("Designated Automobile Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> ALL_HAZARD_EXCLUSION_ENDORSEMENT = declare("All Hazards Exclusion Endorsement", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DESIGNATED_INDIVIDUAL_EXCLUSION = declare("Designated Individual Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> DESIGNATED_ANIMAL_EXCLUSION = declare("Designated Animal Exclusion", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_ON_FILE_FOR_DESIGNATED_RECREATIONAL_MOTOR_VEHICLE = declare("Signature on file for Designated Recreational Motor Vehicle", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_ON_FILE_FOR_DESIGNATED_AUTO = declare("Signature on file for Designated Auto", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_ON_FILE_FOR_DESIGNATED_WATERCRAFT = declare("Signature on file for Designated Watercraft", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PROOF_OF_UNDERLYING_INSURANCE =
					declare("Proof of underlying insurance for each vehicle/watercraft not insured with AAA/AMIG", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_OF_INSURED_FOR_EXCESS_UNINSURED_OFFER =
					declare("Signature of insured for Excess Uninsured Motorists Offer", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_OF_INSURED_FOR_EXCESS_UNDERINSURED_OFFER =
					declare("Signature of insured for Excess Underinsured Motorists Offer", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AUTOPAY_AUTHORIZATION_FORM = declare("AutoPay Authorization Form", RadioGroup.class, Waiters.AJAX);
		}
		
		public static final class RequiredToBind extends MetaData {
			public static final AssetDescriptor<RadioGroup> AAA_PROOF_OF_UNDERLYING_INSURANCE =
					declare("Proof of underlying insurance for each vehicle/watercraft not insured with AAA/AMIG", RadioGroup.class, Waiters.AJAX);
			
		}

		public static final class RequiredToIssue extends MetaData {
			public static final AssetDescriptor<RadioGroup> APPLICANT_STATEMENT_SIGNATURE = declare("Applicant's statement signature", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_FOR_INDIVIDUAL_EXCLUSION_ON_FILE =
					declare("Signature of named insured for Individual exclusion on file", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_ON_FILE_IF_AUTO_LIABILITY_IS_EXCLUDED =
					declare("Signature on file if Auto Liability is excluded", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_FOR_AUTOMOBILE_EXCLUSION_ON_FILE =
					declare("Signature of named insured for Automobile exclusion on file", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_ON_FILE_FOR_ALL_HAZZARDS = declare("Signature on file for All Hazards", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_FOR_ALL_HAZARDS_EXCLUSION_ON_FILE =
					declare("Signature of named insured for all hazards exclusion on file", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_FOR_ANIMAL_EXCLUSION_ON_FILE = declare("Signature of named insured for animal exclusion on file", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_FOR_WATERCRAFT_EXCLUSION_ON_FILE =
					declare("Signature of named insured for watercraft exclusion on file", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_FOR_MOTOR_VEHICLE_EXCLUSION_ON_FILE =
					declare("Signature of named insured for recreational motor vehicle exclusion on file", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_OF_EXCLUDED_NAMED_DRIVER_ON_FILE = declare("Signature of excluded named driver on file", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> PUP_UM_UIM_DISCLOSURE = declare("PUP UM/UIM Disclosure", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_OF_INSURED_FOR_EXCESS_UNINSURED_MOTORISTS_OFFER =
					declare("Signature of insured for Excess Uninsured Motorists Offer", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> SIGNATURE_OF_INSURED_FOR_EXCESS_UNDERINSURED_MOTORISTS_OFFER =
					declare("Signature of insured for Excess Underinsured Motorists Offer", RadioGroup.class, Waiters.AJAX);
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

	///
	

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

	public static final class DeclineByCustomerActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AssetDescriptor<TextBox> DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class ManualRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	public static final class DeclineByCompanyActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DECLINE_TYPE = declare("Decline Type", TextBox.class);
		public static final AssetDescriptor<TextBox> DECLINE_DATE = declare("Decline Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
	}

	public static final class DeleteCancelNoticeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_NOTICE_DATE = declare("Cancellation Notice Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation Reason", ComboBox.class);
	}

	public static final class ExtensionRenewalActionTab extends MetaData {
	}

	public static final class CopyQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class RemoveDoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
	}

	public static final class CopyFromPolicyActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
	}

	public static final class RollBackEndorsementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> ENDORSEMENT_ROLL_BACK_DATE = declare("Endorsement Roll Back Date", TextBox.class);
	}

	public static final class DeletePendedTransactionActionTab extends MetaData {
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
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
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
		public static final AssetDescriptor<RadioGroup> WILL_THIS_MORTGAGEE_COMPANY_BE_PAYING_THE_POLICY_PREMIUM = declare("Will this Mortgagee Company be Paying the Policy Premium?", RadioGroup.class);
	}

	public static final class RollOnChangesActionTab extends MetaData {
	}

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

	public static final class DoNotRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<ComboBox> DO_NOT_RENEW_STATUS = declare("Do Not Renew Status", ComboBox.class);
		public static final AssetDescriptor<TextBox> SUPPORTING_DATA = declare("Supporting Data", TextBox.class);
	}

	public static final class ReinstatementActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.AJAX);
        public static final AssetDescriptor<TextBox> REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
	}

	public static final class ProposeActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> NOTES = declare("Notes", TextBox.class);
	}

	public static final class SuspendQuoteActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
	}

	public static final class CancelNoticeActionTab extends MetaData {
	    public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.AJAX);
	}

	public static final class RemoveManualRenewActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
	}

	public static final class ChangeBrokerActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TRANSFER_ID = declare("Transfer ID", TextBox.class);
		public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class);
		public static final AssetDescriptor<RadioGroup> TRANSFER_TYPE = declare("Transfer Type", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> TRANSFER_EFFECTIVE_UPON = declare("Transfer Effective Upon", RadioGroup.class);
		public static final AssetDescriptor<TextBox> TRANSFER_EFFECTIVE_DATE = declare("Transfer Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
		public static final AssetDescriptor<RadioGroup> COMMISSION_IMPACT = declare("Commission Impact", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> COMMISSION_OVERRIDE = declare("Commission Override", RadioGroup.class);
		public static final AssetDescriptor<TextBox> SOURCE_CHANNEL = declare("Source Channel", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_LOCATION_TYPE = declare("Source Location Type", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_LOCATION_NAME = declare("Source Location Name", TextBox.class);
		public static final AssetDescriptor<TextBox> SOURCE_INSURANCE_AGENT = declare("Source Insurance Agent", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_CHANNEL = declare("Target channel", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_LOCATION_TYPE = declare("Target location type", TextBox.class);
		public static final AssetDescriptor<TextBox> TARGET_LOCATION_NAME = declare("Target location Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> TARGET_INSURANCE_AGENT = declare("Target Insurance agent", ComboBox.class);
		public static final AssetDescriptor<DialogSingleSelector> LOCATION_NAME = declare("Location Name", DialogSingleSelector.class, ChangeLocationMetaData.class);
		public static final AssetDescriptor<DialogSingleSelector> NEW_BROKER = declare("New Broker", DialogSingleSelector.class, ChangeBrokerMetaData.class);
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

		public static final class ChangeBrokerMetaData extends MetaData {
			public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
			public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
			public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);

			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false, By.id("policyDataGatherForm:changeBrokerLink_BrokerChangeAction"));
		}
	}

	public static final class BindActionTab extends MetaData {
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
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class, Waiters.AJAX);
	}

	public static final class GenerateOnDemandDocumentActionTab extends MetaData {
		public static final AssetDescriptor<FillableDocumentsTable> ON_DEMAND_DOCUMENTS = declare("OnDemandDocuments", FillableDocumentsTable.class, DocumentsRow.class, By.xpath("(//div[@id='policyDataGatherForm:componentView_AAAHODocGen']//table)[1]"));
		public static final AssetDescriptor<AdvancedRadioGroup> DELIVERY_METHOD =
				declare("Delivery Method", AdvancedRadioGroup.class, Waiters.AJAX.then(Waiters.AJAX), By.xpath("//span[@id='policyDataGatherForm:delveryMethodSectionPanel']/table"));
		public static final AssetDescriptor<TextBox> EMAIL_ADDRESS = declare("Email Address", TextBox.class, Waiters.AJAX);

		public static final class DocumentsRow extends MetaData {
			public static final AssetDescriptor<CheckBox> SELECT = declare(DocGenConstants.OnDemandDocumentsTable.SELECT, CheckBox.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NUMBER = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NUM, StaticElement.class, Waiters.AJAX);
			public static final AssetDescriptor<StaticElement> DOCUMENT_NAME = declare(DocGenConstants.OnDemandDocumentsTable.DOCUMENT_NAME, StaticElement.class, Waiters.AJAX);
			
	     // Fields of HSU03XX
			public static final AssetDescriptor<CheckBox> DECISION_BASED_ON_CLUE_HSU03 = declare("Decision based on CLUE HSU03", CheckBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:hsu03CIN:0']"));
						
	     // Fields of HSU05XX
			public static final AssetDescriptor<TextBox> FIRST_NAME_HSU05 = declare("First name", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:firstName_HSU05']"));
			public static final AssetDescriptor<TextBox> LAST_NAME_HSU05 = declare("Last name", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:lastName_HSU05']"));
			public static final AssetDescriptor<TextBox> ZIP_CODE_HSU05 = declare("Zip code", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:zipCode_HSU05']"));
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1_HSU05 = declare("Street address 1", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:address1_HSU05']"));
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2_HSU05 = declare("Street address 2", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:address2Show_HSU05']"));
			public static final AssetDescriptor<TextBox> City_HSU05 = declare("City", TextBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:city_HSU05']"));
			public static final AssetDescriptor<ComboBox> STATE_HSU05 = declare("State", ComboBox.class, Waiters.AJAX, false, By.xpath("//span[@id='policyDataGatherForm:stateShow_HSU05']/select"));
			public static final AssetDescriptor<TextBox> DESCRIPTION_HSU05 = declare("Description", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:uwMsg_HSU05']"));
					
		// Fields of HSU07XX
			public static final AssetDescriptor<CheckBox> DECISION_BASED_ON_CLUE_HSU07 = declare("Decision based on CLUE HSU07", CheckBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:hsu07CIN:0']"));
		// Fields of HSU09XX
			public static final AssetDescriptor<CheckBox> DECISION_BASED_ON_CLUE_HSU09 = declare("Decision based on CLUE HSU09", CheckBox.class, Waiters.AJAX, false, By.xpath("//input[@id='policyDataGatherForm:hsu09CIN:0']"));
		}
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
