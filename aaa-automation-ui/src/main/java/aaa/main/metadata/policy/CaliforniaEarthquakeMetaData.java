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
public final class CaliforniaEarthquakeMetaData {

    public static final class GeneralTab extends MetaData {
    	
    	public static final AttributeDescriptor POLICY_INFORMATION = declare("PolicyInformation", AssetList.class, PolicyInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAACeaUnderlyingPolicy']"));
    	public static final AttributeDescriptor CEA_POLICY_INFORMATION = declare("CeaPolicyInformation", AssetList.class, CeaPolicyInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_Policy' or @id='policyDataGatherForm:componentView_AAAPolicyPrintingInfoComponent']"));
    	public static final AttributeDescriptor DWELLING_ADDRESS = declare("DwellingAddress", AssetList.class, DwellingAddress.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_DwellingAddressMVO' or @id='policyDataGatherForm:componentView_AAACeaAddressType']"));
    	public static final AttributeDescriptor MAILING_ADDRESS = declare("MailingAddress", AssetList.class, MailingAddress.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOMailingAddressComponent']"));
    	public static final AttributeDescriptor NAMED_INSURED = declare("NamedInsured", AssetList.class, NamedInsured.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_Insured']"));
    	public static final AttributeDescriptor NAMED_INSURED_CONTACT_INFORMATION = declare("NamedInsuredContactInformation", AssetList.class, NamedInsuredContactInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_CommunicationInfo']"));
    	public static final AttributeDescriptor AGENCY_INFORMATION = declare("AgencyInformation", AssetList.class, AgencyInformation.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAProducerInfo']"));
    	
    	public static final class PolicyInformation extends MetaData {
    		public static final AttributeDescriptor POLICY_NUMBER = declare("Policy Number", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor PREFILL = declare("Prefill", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:prefillHOPolicy"));
    		public static final AttributeDescriptor STATE = declare("State", TextBox.class);
    		public static final AttributeDescriptor POLICY_TYPE = declare("Policy Type", ComboBox.class);
    		public static final AttributeDescriptor HOMEOWNERS_POLICY_COVERAGE_A_LIMIT = declare("Homeowners policy Coverage A limit", TextBox.class);
    		public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective date", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration date", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor OCCUPANCY_TYPE = declare("Occupancy Type", ComboBox.class, Waiters.AJAX);
    	}
    	
    	public static final class CeaPolicyInformation extends MetaData {
    		public static final AttributeDescriptor STATE = declare("State", TextBox.class);
    		public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration date", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor LEAD_SOURCE = declare("Lead source", ComboBox.class);
    		public static final AttributeDescriptor SUPPRESS_PRINT = declare("Suppress Print", ComboBox.class);
    	}
    	
    	public static final class DwellingAddress extends MetaData {
    		public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
    		public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class);
    		public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class);
    		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
    		public static final AttributeDescriptor COUNTY = declare("County", TextBox.class);
    		public static final AttributeDescriptor STATE = declare("State", TextBox.class);
    		public static final AttributeDescriptor THIS_EARTHQUAKE_QUOTE_IS_FOR = declare("This Earthquake quote is for", RadioGroup.class, Waiters.AJAX);
    	}	
    	
    	public static final class MailingAddress extends MetaData {
    		public static final AttributeDescriptor IS_DIFFERENT_MAILING_ADDRESS = declare("Is the mailing address different from the dwelling address?", RadioGroup.class, Waiters.AJAX, false, By.xpath("//table[@id='policyDataGatherForm:addOptionalQuestionFormGrid_AAAHOMailingAddressComponent']"));
    		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
    		public static final AttributeDescriptor STREET_ADDRESS_1 = declare("Street address 1", TextBox.class);
    		public static final AttributeDescriptor STREET_ADDRESS_2 = declare("Street address 2", TextBox.class);
    		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
    		public static final AttributeDescriptor COUNTY = declare("County", TextBox.class);
    		public static final AttributeDescriptor STATE = declare("State", ComboBox.class);
    		public static final AttributeDescriptor ADDRESS_VALIDATED = declare("Address Validated?", TextBox.class);
    		public static final AttributeDescriptor VALIDATE_ADDRESS_BTN = declare("Validate Address", Button.class, Waiters.AJAX, false, By.id("policyDataGatherForm:validateHOMailingAddressButtonUS"));
			public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class,
					By.id(".//*[@id='addressValidationPopupAAAHOMailingAddressValidation_container']"));
    	}	
    	
    	public static final class NamedInsured extends MetaData {
    		public static final AttributeDescriptor PREFIX = declare("Prefix", ComboBox.class);
    		public static final AttributeDescriptor FIRST_NAME = declare("First name", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor MIDDLE_NAME = declare("Middle name", TextBox.class);
    		public static final AttributeDescriptor LAST_NAME = declare("Last name", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor SUFFIX = declare("Suffix", ComboBox.class);
    		public static final AttributeDescriptor RELATIONSHIP_TO_PRIMARY_NAMED_INSURED = declare("Relationship to primary named insured", ComboBox.class);
    		public static final AttributeDescriptor GENDER = declare("Gender", ComboBox.class);
    		public static final AttributeDescriptor MARITAL_STATUS = declare("Marital status", ComboBox.class);
    		public static final AttributeDescriptor DATE_OF_BIRTH = declare("Date of birth", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor SOCIAL_SECURITY_NUMBER = declare("Social security number", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor BASE_DATE = declare("Base date", TextBox.class);
    		public static final AttributeDescriptor OCCUPATION = declare("Occupation", ComboBox.class);
    		public static final AttributeDescriptor EMPLOYER = declare("Employer", TextBox.class);
    		public static final AttributeDescriptor AAA_EMPLOYEE = declare("AAA employee", RadioGroup.class);
    		public static final AttributeDescriptor TRUSTEE_LLC_OWNER = declare("Trustee/LLC Owner", RadioGroup.class);
    		//public static final AttributeDescriptor ENTERED_SSN_IS_NOT_VALID = declare("Entered SSN is not valid", RadioGroup.class);
    	}
    	
    	public static final class NamedInsuredContactInformation extends MetaData {
    		public static final AttributeDescriptor HOME_PHONE = declare("Home phone", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor WORK_PHONE = declare("Work phone", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor MOBILE_PHONE = declare("Mobile phone", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor FAX = declare("Fax", TextBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor EMAIL = declare("Email", TextBox.class, Waiters.AJAX);
    	}	
    	
    	public static final class AgencyInformation extends MetaData {
    		public static final AttributeDescriptor CHANNEL_TYPE = declare("Channel Type", ComboBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor AGENCY = declare("Agency", ComboBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor AGENCY_OF_RECORD = declare("Agency of Record", ComboBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor SALES_CHANNEL = declare("Sales Channel", ComboBox.class);
    		public static final AttributeDescriptor AGENCY_LOCATION = declare("Agency Location", ComboBox.class);
    		public static final AttributeDescriptor AGENT = declare("Agent", ComboBox.class);
    		public static final AttributeDescriptor AGENT_OF_RECORD = declare("Agent of Record", ComboBox.class, Waiters.AJAX);
    		public static final AttributeDescriptor AGENT_NUMBER = declare("Agent Number", TextBox.class, Waiters.AJAX);
    	}	
    	
    }
    
    
	public static final class PropertyInfoTab extends MetaData {
		public static final AttributeDescriptor DWELLING_USAGE = declare("Dwelling Usage", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor YEAR_BUILT = declare("Year built", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor CONSTRUCTION_TYPE = declare("Construction type", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor NUMBER_OF_FAMILY_UNITS = declare("Number of family units", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor FOUNDATION_TYPE = declare("Foundation type", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor NUMBER_OF_STORIES_INCLUDING_BASEMENT = declare("Number of stories, including basement", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor SQUARE_FOOTAGE = declare("Square footage", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor NUMBER_OF_CHIMNEYS = declare("Number of chimneys", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ROOF_TYPE = declare("Roof type", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_THE_DWELLING_ANCHORED_TO_FOUNDATION = declare("Is the dwelling anchored to foundation using approved anchor bolts in accordance with California building code?", RadioGroup.class);
		public static final AttributeDescriptor IS_THE_WATER_HEATER_SECURED_TO_THE_BUILDING = declare("Is the water heater secured to the building frame in accordance with guidelines for earthquake bracing of residential water heaters?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DOES_THE_DWELLING_HAVE_CRIPPLE_WALLS = declare("Does the dwelling have cripple walls?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWOOD_AND_INSTALLED = declare("Are cripple walls braced with plywood or its equivalent and installed in accordance with California building code?", RadioGroup.class);
		public static final AttributeDescriptor DOES_DWELLING_HAVE_POST_AND_PIER = declare("Does dwelling have post-and-pier or post-and-beam foundation?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_THE_DWELLING_WITH_POST_AND_PIER = declare("Is dwelling with post-and-pier or post-and-beam foundation, has been modified in accordance with California building code?", RadioGroup.class);

		/*public static final AttributeDescriptor ARE_THE_WATER_HEATERS_SECURED_TO_BUILDING_FRAME = declare("Are the water heaters secured to building frame?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWOOD_OR_EQUIVALENT = declare("Are cripple walls braced with plywood or equivalent?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_THE_DWELLING_SECURED_TO_FOUNDATION = declare("Is the dwelling secured to foundation?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor ARE_CRIPPLE_WALLS_BRACED_WITH_PLYWOOD = declare("Are cripple walls braced with plywood or its equivalent and installed in accordance with California Building Code?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DOES_THE_DWELLING_HAVE_POST_AND_PIER = declare("Does the dwelling have post-and-pier or post-and-beam foundation?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor IS_DWELLING_WITH_POST_AND_PIER_HAS_BEEN_MODIFIED = declare("Is dwelling with post-and-pier or post-and-beam foundation, has been modified in accordance with California Building Code?", RadioGroup.class, Waiters.AJAX);*/
		
		public static final AttributeDescriptor ARE_THERE_UNREPAIRED_STRUCTURAL_EARTHQUAKE_DAMAGES_TO_THE_DWELLING = declare("Are there unrepaired structural earthquake damages to the dwelling?", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor DOES_DWELLING_HAVE_AN_ATTACHED_GARAGE = declare("Does dwelling have an attached garage?", RadioGroup.class, Waiters.AJAX);

	}

	public static final class PremiumCoveragesTab extends MetaData {
		public static final AttributeDescriptor PRODUCT = declare("Product", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor PAYMENT_PLAN = declare("Payment Plan", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor BILL_TO_AT_RENEWAL = declare("Bill to at renewal", ComboBox.class);

		public static final AttributeDescriptor COVERAGE_A_DWELLING_LIMIT = declare(HomeCaCeaCoverages.COVERAGE_A_DWELLING_LIMIT.get(), TextBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//input", HomeCaCeaCoverages.COVERAGE_A_DWELLING_LIMIT.get())));
		public static final AttributeDescriptor COVERAGE_A_DEDUCTIBLE = declare(HomeCaCeaCoverages.COVERAGE_A_DEDUCTIBLE.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_A_DEDUCTIBLE.get())));
		public static final AttributeDescriptor COVERAGE_C = declare(HomeCaCeaCoverages.COVERAGE_C.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_C.get())));
		public static final AttributeDescriptor COVERAGE_D = declare(HomeCaCeaCoverages.COVERAGE_D.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.COVERAGE_D.get())));
		public static final AttributeDescriptor ADDITIONAL = declare(HomeCaCeaCoverages.ADDITIONAL.get(), ComboBox.class, Waiters.AJAX, true,
				By.xpath(String.format("//table[@id='policyDataGatherForm:coverageSummaryTable']//tr[td[.='%s']]//select", HomeCaCeaCoverages.ADDITIONAL.get())));

		public static final AttributeDescriptor OPTIONAL_COVERAGE_MASONRY_VENEER = declare("Optional Coverage - Masonry Veneer", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor OPTIONAL_COVERAGE_BREAKAGE_OF_PERSONAL_PROPERTY = declare("Optional Coverage - Breakage of personal property", RadioGroup.class, Waiters.AJAX);
		public static final AttributeDescriptor SIGNED_RETROFIT_VERIFICATION_FORM = declare("Signed Retrofit Verification form available?", RadioGroup.class, Waiters.AJAX);
		
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
				public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class, By.id(".//div[@id='addressValidationPopupAAAHOMortgageeAddressValidation_container']"));
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
				public static final AttributeDescriptor VALIDATE_ADDRESS_DIALOG = declare("Validate Address Dialog", AddressValidationDialog.class, AddressValidationMetaData.class, By.id(".//div[@id='addressValidationPopupAAAHOLegalPropAddressValidationComp_container']"));
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
    
	public static final class DocumentsTab extends MetaData {
		public static final AttributeDescriptor DOCUMENTS_FOR_PRINTING = declare("DocumentsForPrinting", AssetList.class, DocumentsForPrinting.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHODocGenPrint']"));
		// public static final AttributeDescriptor DOCUMENTS_TO_BIND =
		// declare("DocumentsToBind", AssetList.class, DocumentsToBind.class,
		// By.xpath(".//div[@id='policyDataGatherForm:componentView_AAAHOBindDocuments']"));
		public static final AttributeDescriptor DOCUMENTS_TO_ISSUE = declare("DocumentsToIssue", AssetList.class, DocumentsToIssue.class, By.xpath(".//div[@id='policyDataGatherForm:componentView_AAACeaBindDocuments']"));

		public static final class DocumentsForPrinting extends MetaData {
			public static final AttributeDescriptor EARTHQUAKE_INSURANCE_APPLICATION = declare("Earthquake Insurance Application", RadioGroup.class, Waiters.AJAX);
			public static final AttributeDescriptor AAA_CALIFORNIA_EARTHQUAKE_INSURANCE_QUOTE = declare("AAA California Earthquake Insurance Quote", RadioGroup.class, Waiters.AJAX);
		}

		/*
		 * public static final class DocumentsToBind extends MetaData { }
		 */
		public static final class DocumentsToIssue extends MetaData {
			public static final AttributeDescriptor EARTHQUAKE_INSURANCE_APPLICATION = declare("Earthquake Insurance Application", RadioGroup.class, Waiters.AJAX);
		}
	}
	
	public static final class BindTab extends MetaData {
	}
	
	
    //TODO done till now
    
    public static final class ChangePendedEndEffDateActionTab extends MetaData {
        public static final AttributeDescriptor ENDORSEMENT_DATE = declare("Endorsement Date", TextBox.class);
        public static final AttributeDescriptor ENDORSEMENT_REASON = declare("Endorsement Reason", ComboBox.class);
    }

    public static final class SuspendQuoteActionTab extends MetaData {
        public static final AttributeDescriptor DATE = declare("Date", TextBox.class);
        public static final AttributeDescriptor REASON = declare("Reason", ComboBox.class);
        public static final AttributeDescriptor FOLLOW_UP_REQUIRED = declare("Follow-up required", RadioGroup.class);
    }

    public static final class GenerateOnDemandDocumentActionTab extends MetaData {}

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
        public static final AttributeDescriptor INSURED_AGREES_TO_UBI_TERMS_CONDITIONS =
                declare("Insured agrees to UBI Terms Conditions?", RadioGroup.class);
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

    public static final class ExtensionRenewalActionTab extends MetaData {}

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
        public static final AttributeDescriptor LOCATION_NAME = declare("Location Name", DialogSingleSelector.class,
                ChangeLocationMetaData.class);
        public static final AttributeDescriptor INSURANCE_AGENT = declare("Insurance Agent", ComboBox.class);

        public static final class ChangeLocationMetaData extends MetaData {
            public static final AttributeDescriptor CHANNEL = declare("Channel", ComboBox.class);
            public static final AttributeDescriptor AGENCY_NAME = declare("Agency Name", TextBox.class);
            public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);
            public static final AttributeDescriptor ZIP_CODE = declare("Zip Code", TextBox.class);
            public static final AttributeDescriptor CITY = declare("City", TextBox.class);
            public static final AttributeDescriptor STATE = declare("State", TextBox.class);

            public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.DEFAULT, false,
                    By.id("policyDataGatherForm:changeTargetProducerCd"));
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
        public static final AttributeDescriptor INSUREDS_APPROVE_REMOVAL_OF_DRIVER_AND_VEHICLES =
                declare("Insureds approve removal of driver and vehicles?", RadioGroup.class);
    }

    public static final class ReinstatementActionTab extends MetaData {
        public static final AttributeDescriptor CANCELLATION_EFFECTIVE_DATE = declare("Cancellation Effective Date", TextBox.class, Waiters.NONE);
        public static final AttributeDescriptor REINSTATE_DATE = declare("Reinstate Date", TextBox.class, Waiters.AJAX);
    }

    public static final class RewriteActionTab extends MetaData {
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
        public static final AttributeDescriptor REWRITE_REASON = declare("Rewrite reason", ComboBox.class);
    }

    public static final class BindActionTab extends MetaData {}

    public static final class RollOnChangesActionTab extends MetaData {}

    public static final class CopyFromPolicyActionTab extends MetaData {
        public static final AttributeDescriptor QUOTE_EFFECTIVE_DATE = declare("Quote Effective Date", TextBox.class);
    }

    public static final class DeletePendedTransactionActionTab extends MetaData {}

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

    public static final class IssueActionTab extends MetaData {}

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
