/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata.policy;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.PartySearchTextBox;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;

import aaa.main.metadata.DialogsMetaData.AddressValidationMetaData;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.toolkit.webdriver.customcontrols.dialog.AddressValidationDialog;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
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
public final class CaliforniaEarthquakeMetaData {

    public static final class GeneralTab extends MetaData {
    	
    	public static final AssetDescriptor<AssetList> POLICY_INFORMATION = declare("PolicyInformation", AssetList.class, PolicyInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAACeaUnderlyingPolicy']"));
    	public static final AssetDescriptor<AssetList> CEA_POLICY_INFORMATION = declare("CeaPolicyInformation", AssetList.class, CeaPolicyInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_Policy' or @id='policyDataGatherForm:componentView_AAAPolicyPrintingInfoComponent']"));
    	public static final AssetDescriptor<AssetList> DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_DwellingAddressMVO' or @id='policyDataGatherForm:componentView_AAACeaAddressType']"));
    	public static final AssetDescriptor<AssetList> MAILING_ADDRESS = declare("MailingAddress", AssetList.class, MailingAddress.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOMailingAddressComponent']"));
    	public static final AssetDescriptor<AssetList> NAMED_INSURED = declare("NamedInsured", AssetList.class, NamedInsured.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_Insured']"));
    	public static final AssetDescriptor<AssetList> NAMED_INSURED_CONTACT_INFORMATION = declare("NamedInsuredContactInformation", AssetList.class, NamedInsuredContactInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_CommunicationInfo']"));
    	public static final AssetDescriptor<AssetList> AGENCY_INFORMATION = declare("AgencyInformation", AssetList.class, AgencyInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAProducerInfo']"));
    	
    	public static final class PolicyInformation extends MetaData {
    		public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy Number", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<Button> PREFILL = declare("Prefill", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:prefillHOPolicy"));
    		public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
    		public static final AssetDescriptor<ComboBox> POLICY_TYPE = declare("Policy Type", ComboBox.class);
    		public static final AssetDescriptor<TextBox> HOMEOWNERS_POLICY_COVERAGE_A_LIMIT = declare("Homeowners policy Coverage A limit", TextBox.class);
    		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration date", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<ComboBox> OCCUPANCY_TYPE = declare("Occupancy Type", ComboBox.class, Waiters.AJAX);
    	}
    	
    	public static final class CeaPolicyInformation extends MetaData {
    		public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
    		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration date", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<ComboBox> LEAD_SOURCE = declare("Lead source", ComboBox.class);
    		public static final AssetDescriptor<ComboBox> SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
    	}
    	
    	public static final class DwellingAddress extends MetaData {
    		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
    		public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class);
    		public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class);
    		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    		public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
    		public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);
    		public static final AssetDescriptor<RadioGroup> THIS_EARTHQUAKE_QUOTE_IS_FOR = declare("This Earthquake quote is for", RadioGroup.class, Waiters.AJAX);
    	}	
    	
    	public static final class MailingAddress extends MetaData {
    		public static final AssetDescriptor<RadioGroup> IS_DIFFERENT_MAILING_ADDRESS = declare("Is the mailing address different from the dwelling address?", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAHOMailingAddressComponent']"));
    		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
    		public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class);
    		public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class);
    		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
    		public static final AssetDescriptor<TextBox> COUNTY = declare("County", TextBox.class);
    		public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class);
    		public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
    		public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOMailingAddressButtonUS"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class,
					By.id(".//*[@id='addressValidationPopupAAAHOMailingAddressValidation_container']"));
    	}	
    	
    	public static final class NamedInsured extends MetaData {
    		public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class);
    		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First name", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle name", TextBox.class);
    		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last name", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<ComboBox> SUFFIX = declare("Suffix", ComboBox.class);
    		public static final AssetDescriptor<ComboBox> RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class);
    		public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class);
    		public static final AssetDescriptor<ComboBox> MARITAL_STATUS = declare("Marital status", ComboBox.class);
    		public static final AssetDescriptor<TextBox> DATE_OF_BIRTH = declare("Date of birth", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> SOCIAL_SECURITY_NUMBER = declare("Social security number", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> BASE_DATE = declare("Base date", TextBox.class);
    		public static final AssetDescriptor<ComboBox> OCCUPATION = declare("Occupation", ComboBox.class);
    		public static final AssetDescriptor<TextBox> EMPLOYER = declare("Employer", TextBox.class);
    		public static final AssetDescriptor<RadioGroup> AAA_EMPLOYEE = declare("AAA employee", RadioGroup.class);
    		public static final AssetDescriptor<RadioGroup> TRUSTEE_LLC_OWNER = declare("Trustee/LLC Owner", RadioGroup.class);
    		//public static final AssetDescriptor<RadioGroup> ENTERED_SSN_IS_NOT_VALID = declare("Entered SSN is not valid", RadioGroup.class);
    	}
    	
    	public static final class NamedInsuredContactInformation extends MetaData {
    		public static final AssetDescriptor<TextBox> HOME_PHONE = declare("Home phone", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> WORK_PHONE = declare("Work phone", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> MOBILE_PHONE = declare("Mobile phone", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> FAX = declare("Fax", TextBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
    	}	
    	
    	public static final class AgencyInformation extends MetaData {
    		public static final AssetDescriptor<ComboBox> CHANNEL_TYPE = declare("Channel Type", ComboBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<ComboBox> AGENCY = declare("Agency", ComboBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<ComboBox> AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<ComboBox> SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
    		public static final AssetDescriptor<ComboBox> AGENCY_LOCATION = declare("Agency Location", ComboBox.class);
    		public static final AssetDescriptor<ComboBox> AGENT = declare("Agent", ComboBox.class);
    		public static final AssetDescriptor<ComboBox> AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class, Waiters.AJAX);
    		public static final AssetDescriptor<TextBox> AGENT_NUMBER = declare("Agent Number", TextBox.class, Waiters.AJAX);
    	}	
    	
    }
    
    
	public static final class PropertyInfoTab extends MetaData {
		public static final AssetDescriptor<ComboBox> DWELLING_USAGE = declare("Dwelling Usage", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> YEAR_BUILT = declare("Year built", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> CONSTRUCTION_TYPE = declare("Construction type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> NUMBER_OF_FAMILY_UNITS = declare("Number of family units", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> FOUNDATION_TYPE = declare("Foundation type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> NUMBER_OF_STORIES_INCLUDING_BASEMENT = declare("Number of stories, including basement", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> SQUARE_FOOTAGE = declare("Square footage", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> NUMBER_OF_CHIMNEYS = declare("Number of chimneys", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> ROOF_TYPE = declare("Roof type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_THE_DWELLING_ANCHORED_TO_FOUNDATION = declare("Is the dwelling anchored to foundation using approved anchor bolts in accordance with California building code?", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> IS_THE_WATER_HEATER_SECURED_TO_THE_BUILDING = declare("Is the water heater secured to the building frame in accordance with guidelines for earthquake bracing of residential water heaters?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DOES_THE_DWELLING_HAVE_CRIPPLE_WALLS = declare("Does the dwelling have cripple walls?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWOOD_AND_INSTALLED = declare("Are cripple walls braced with plywood or its equivalent and installed in accordance with California building code?", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> DOES_DWELLING_HAVE_POST_AND_PIER = declare("Does dwelling have post-and-pier or post-and-beam foundation?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_THE_DWELLING_WITH_POST_AND_PIER = declare("Is dwelling with post-and-pier or post-and-beam foundation, has been modified in accordance with California building code?", RadioGroup.class);

		/*public static final AssetDescriptor<RadioGroup> ARE_THE_WATER_HEATERS_SECURED_TO_BUILDING_FRAME = declare("Are the water heaters secured to building frame?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWOOD_OR_EQUIVALENT = declare("Are cripple walls braced with plywood or equivalent?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_THE_DWELLING_SECURED_TO_FOUNDATION = declare("Is the dwelling secured to foundation?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWOOD = declare("Are cripple walls braced with plywood or its equivalent and installed in accordance with California Building Code?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DOES_THE_DWELLING_HAVE_POST_AND_PIER = declare("Does the dwelling have post-and-pier or post-and-beam foundation?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> IS_DWELLING_WITH_POST_AND_PIER_HAS_BEEN_MODIFIED = declare("Is dwelling with post-and-pier or post-and-beam foundation, has been modified in accordance with California Building Code?", RadioGroup.class, Waiters.AJAX);*/
		
		public static final AssetDescriptor<RadioGroup> ARE_THERE_UNREPAIRED_STRUCTURAL_EARTHQUAKE_DAMAGES_TO_THE_DWELLING = declare("Are there unrepaired structural earthquake damages to the dwelling?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> DOES_DWELLING_HAVE_AN_ATTACHED_GARAGE = declare("Does dwelling have an attached garage?", RadioGroup.class, Waiters.AJAX);

	}

	public static final class PremiumCoveragesTab extends MetaData {
		public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> BILL_TO_AT_RENEWAL = declare("Bill to at renewal", ComboBox.class);

		public static final AssetDescriptor<TextBox> COVERAGE_A_DWELLING_LIMIT = declare(HomeCaCeaCoverages.COVERAGE_A_DWELLING_LIMIT.get(), TextBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//input", HomeCaCeaCoverages.COVERAGE_A_DWELLING_LIMIT.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_A_DEDUCTIBLE = declare(HomeCaCeaCoverages.COVERAGE_A_DEDUCTIBLE.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_A_DEDUCTIBLE.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_C = declare(HomeCaCeaCoverages.COVERAGE_C.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_C.get())));
		public static final AssetDescriptor<ComboBox> COVERAGE_D = declare(HomeCaCeaCoverages.COVERAGE_D.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_D.get())));
		public static final AssetDescriptor<ComboBox> ADDITIONAL = declare(HomeCaCeaCoverages.ADDITIONAL.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.ADDITIONAL.get())));

		public static final AssetDescriptor<RadioGroup> OPTIONAL_COVERAGE_MASONRY_VENEER = declare("Optional Coverage - Masonry Veneer", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> OPTIONAL_COVERAGE_BREAKAGE_OF_PERSONAL_PROPERTY = declare("Optional Coverage - Breakage of personal property", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<RadioGroup> SIGNED_RETROFIT_VERIFICATION_FORM = declare("Signed Retrofit Verification form available?", RadioGroup.class, Waiters.AJAX);
		
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
		public static final AssetDescriptor<RadioGroup> MORTGAGEE = declare("Mortgagee", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> MORTGAGEE_INFORMATION = declare("MortgageeInformation", MultiInstanceAfterAssetList.class, MortgageeInformation.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOMortgageeInfo']"));
		public static final AssetDescriptor<RadioGroup> USE_LEGAL_NAMED_INSURED = declare("Use legal named insured", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> LEGAL_NAMED_INSURED = declare("Legal named insured", TextBox.class, Waiters.AJAX, false, By.xpath("//textarea[@id='policyDataGatherForm:aaaLegalName']"));
		public static final AssetDescriptor<RadioGroup> USE_LEGAL_PROPERTY_ADDRESS = declare("Use legal property address", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetList> LEGAL_PROPERTY_ADDRESS = declare("LegalPropetyAddress", AssetList.class, LegalPropetyAddress.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOLegalPropAddressComp']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ADDITIONA_INSURED = declare("Is there an additional insured?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_INSURED = declare("AdditionalInsured", MultiInstanceAfterAssetList.class, AdditionalInsured.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOAdditionalInsured']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ADDITIONA_INTEREST = declare("Is there an additional interest?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<MultiInstanceAfterAssetList> ADDITIONAL_INTEREST = declare("AdditionalInterest", MultiInstanceAfterAssetList.class, AdditionalInterest.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAHOAdditionalInterest']"));
		public static final AssetDescriptor<RadioGroup> IS_THERE_ANY_THIRD_PARTY_DESIGNEE = declare("Is there any third party designee?", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<AssetList> THIRD_PARTY_DESIGNEE = declare("ThirdPartyDesignee", AssetList.class, ThirdPartyDesignee.class, By.xpath("//div[@id='policyDataGatherForm:componentView_AAAThirdPartyDesignee']"));
		
		public static final class MortgageeInformation extends MetaData {
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip code", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_1 = declare("Street address 1", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> STREET_ADDRESS_2 = declare("Street address 2", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> STATE = declare("State", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<TextBox> ADDRESS_VALIDATED = declare("Address validated", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<Button> VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOMortAddressButton"));
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class, By.id(".//div[@id='addressValidationPopupAAAHOMortgageeAddressValidation_container']"));
			public static final AssetDescriptor<TextBox> LOAN_NUMBER = declare("Loan number", TextBox.class, Waiters.NONE);
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
			public static final AssetDescriptor<AddressValidationDialog> VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class, By.id(".//div[@id='addressValidationPopupAAAHOLegalPropAddressValidationComp_container']"));
		}
		
		public static final class AdditionalInsured extends MetaData {
			public static final AssetDescriptor<ComboBox> INTEREST = declare("Interest", ComboBox.class, Waiters.NONE);
			public static final AssetDescriptor<TextBox> NAME = declare("Name", TextBox.class, Waiters.NONE);
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
			public static final AssetDescriptor<TextBox> DESCRIPTION_OF_INTEREST = declare("Description of interest", TextBox.class, Waiters.NONE);
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
    
	public static final class DocumentsTab extends MetaData {
		public static final AssetDescriptor<AssetList> DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODocGenPrint']"));
		// public static final AssetDescriptor<AssetList> DOCUMENTS_TO_BIND =
		// declare("DocumentsToBind", AssetList.class, DocumentsToBind.class,
		// By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOBindDocuments']"));
		public static final AssetDescriptor<AssetList> DOCUMENTS_TO_ISSUE = declare("DocumentsToIssue", AssetList.class, DocumentsToIssue.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAACeaBindDocuments']"));

		public static final class DocumentsForPrinting extends MetaData {
			public static final AssetDescriptor<RadioGroup> EARTHQUAKE_INSURANCE_APPLICATION = declare("Earthquake Insurance Application", RadioGroup.class, Waiters.AJAX);
			public static final AssetDescriptor<RadioGroup> AAA_CALIFORNIA_EARTHQUAKE_INSURANCE_QUOTE = declare("AAA California Earthquake Insurance Quote", RadioGroup.class, Waiters.AJAX);
		}

		/*
		 * public static final class DocumentsToBind extends MetaData { }
		 */
		public static final class DocumentsToIssue extends MetaData {
			public static final AssetDescriptor<RadioGroup> EARTHQUAKE_INSURANCE_APPLICATION = declare("Earthquake Insurance Application", RadioGroup.class, Waiters.AJAX);
		}
	}
	
	public static final class BindTab extends MetaData {
	}
	
	
    //TODO done till now
    
    public static final class ChangePendedEndEffDateActionTab extends MetaData {
        public static final AssetDescriptor<TextBox> ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
        public static final AssetDescriptor<ComboBox> ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
    }

    public static final class SuspendQuoteActionTab extends MetaData {
        public static final AssetDescriptor<TextBox> DATE = declare("Date", TextBox.class);
        public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
    }

    public static final class GenerateOnDemandDocumentActionTab extends MetaData {}

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
        public static final AssetDescriptor<RadioGroup> INSURED_AGREES_TO_UBI_TERMS_CONDITIONS =
                declare("Insured agrees to UBI Terms Conditions?", RadioGroup.class);
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

    public static final class ExtensionRenewalActionTab extends MetaData {}

    public static final class CancellationActionTab extends MetaData {
        public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
        public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.AJAX);
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
        public static final AssetDescriptor<DialogSingleSelector> LOCATION_NAME = declare("Location Name", DialogSingleSelector.class,
                ChangeLocationMetaData.class);
        public static final AssetDescriptor<ComboBox> INSURANCE_AGENT = declare("Insurance Agent", ComboBox.class);

        public static final class ChangeLocationMetaData extends MetaData {
            public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
            public static final AssetDescriptor<TextBox> AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);
            public static final AssetDescriptor<TextBox> ZIP_CODE = declare("Zip Code", TextBox.class);
            public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
            public static final AssetDescriptor<TextBox> STATE = declare("State", TextBox.class);

            public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("policyDataGatherForm:changeTargetProducerCd"));
        }
    }

    public static final class CancelNoticeActionTab extends MetaData {
        public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation effective date", TextBox.class, Waiters.AJAX);
        public static final AssetDescriptor<ComboBox> CANCELLATION_REASON = declare("Cancellation reason", ComboBox.class, Waiters.NONE);
        public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class, Waiters.NONE);
        public static final AssetDescriptor<TextBox> DAYS_OF_NOTICE = declare("Days of notice", TextBox.class, Waiters.NONE);
    }

    public static final class SpinActionTab extends MetaData {
        public static final AssetDescriptor<TextBox> TRANSACTION_EFFECTIVE_DATE = declare("Transaction Effective Date", TextBox.class);
        public static final AssetDescriptor<ComboBox> REASON_FOR_SPIN = declare("Reason for Spin", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> INSUREDS_APPROVE_REMOVAL_OF_DRIVER_AND_VEHICLES =
                declare("Insureds approve removal of driver and vehicles?", RadioGroup.class);
    }

    public static final class ReinstatementActionTab extends MetaData {
        public static final AssetDescriptor<TextBox> CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.NONE);
        public static final AssetDescriptor<TextBox> REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
    }

    public static final class RewriteActionTab extends MetaData {
        public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
        public static final AssetDescriptor<ComboBox> REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
    }

    public static final class BindActionTab extends MetaData {}

    public static final class RollOnChangesActionTab extends MetaData {}

    public static final class CopyFromPolicyActionTab extends MetaData {
        public static final AssetDescriptor<TextBox> QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
    }

    public static final class DeletePendedTransactionActionTab extends MetaData {}

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

    public static final class IssueActionTab extends MetaData {}

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

}
