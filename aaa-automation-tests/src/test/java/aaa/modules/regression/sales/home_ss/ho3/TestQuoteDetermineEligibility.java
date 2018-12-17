package aaa.modules.regression.sales.home_ss.ho3;

import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

public class TestQuoteDetermineEligibility extends HomeSSHO3BaseTest {

	private String ER0906 = "Dwellings with more than 2 roof layers are ineligible.";
	private String ER0908 = "Wood burning stoves as the sole source of heat are ineligible.";
	private String ER0909 = "Wood burning stoves are ineligible unless professionally installed by a licensed contractor.";
	private String ER0522 = "Dwellings with a wood burning stove without at least one smoke detector installed per floor are ineligible.";
	private String ER0903 = "Applicants/Insureds with vicious dogs or exotic animals are ineligible.";

	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testDetermineEligibility_SC1(@Optional("") String state) {
		mainApp().open();

		TestData td_sc1_1 = getTestSpecificTD("TestData_SC1_1");
		TestData td_sc1_2 = getTestSpecificTD("TestData_SC1_2");
		TestData td_sc1_3 = getTestSpecificTD("TestData_SC1_3");

		//getCopiedQuote();
		createCustomerIndividual();
		createQuote();

		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td_sc1_1);
		propertyInfoTab.submitTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.HOME_RENOVATION).getAsset(
					HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION).getWarning().toString()).contains(ER0906);
			softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.STOVES).getAsset(
					HomeSSMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT).getWarning().toString()).contains(ER0908);
			softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.STOVES).getAsset(
					HomeSSMetaData.PropertyInfoTab.Stoves.WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR).getWarning().toString()).contains(ER0909);
			softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.STOVES).getAsset(
					HomeSSMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY).getWarning().toString()).contains(ER0522);
			if (!getState().equals("MD")) {
				softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PETS_OR_ANIMALS).getAsset(
						HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_TYPE).getWarning().toString()).contains(ER0903);
			}
		});	
		
		/*
		CustomAssert.enableSoftMode();
		CustomAssert.assertEquals(
				propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.HOME_RENOVATION).getWarning(
						HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION.getLabel()).getValue().toString(), ER0906);

		CustomAssert.assertEquals(
				propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.STOVES).getWarning(
						HomeSSMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT.getLabel()).getValue().toString(), ER0908);
		CustomAssert.assertEquals(
				propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.STOVES).getWarning(
						HomeSSMetaData.PropertyInfoTab.Stoves.WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR.getLabel()).getValue().toString(), ER0909);
		CustomAssert.assertEquals(
				propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.STOVES).getWarning(
						HomeSSMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY.getLabel()).getValue().toString(), ER0522);

		if (!getState().equals("MD")) {
			CustomAssert.assertEquals(
					propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PETS_OR_ANIMALS).getWarning(
							HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_TYPE.getLabel()).getValue().toString(), ER0903);
		}
*/
		propertyInfoTab.fillTab(td_sc1_2);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		premiumsTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		BindTab bindTab = new BindTab();
		bindTab.btnPurchase.click();

		ErrorTab errorTab = new ErrorTab();
		//ER-0679: Dwellings with more than 2 detached building structures rented to others on
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3280000_1);
		//WM-0566: Coverage B must be less than 50% of Coverage A to bind
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS4250648);
		errorTab.cancel();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(td_sc1_3);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.btnPurchase.click();

		//WM-0561: Dwellings with more than 3 detached building structures on the residence
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3281092);
		//ER-0680: Coverage B cannot exceed Coverage A
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3281224);
		errorTab.cancel();

		BindTab.buttonSaveAndExit.click();
		log.info("TEST Determine Eligibility SC1: HSS Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

	}

	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testDetermineEligibility_SC2(@Optional("") String state) {
		mainApp().open();

		TestData td_sc2_1 = getTestSpecificTD("TestData_SC2_1");

		//getCopiedQuote();
		createCustomerIndividual();
		createQuote();

		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		ApplicantTab applicantTab = new ApplicantTab();
		applicantTab.fillTab(td_sc2_1);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td_sc2_1);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		premiumsTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		BindTab bindTab = new BindTab();
		bindTab.btnPurchase.click();

		ErrorTab errorTab = new ErrorTab();

		switch (getState()) {
			case "NJ":
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS11120040);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3195184);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_WM_0523);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12023000);
				break;
			case "OR":
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS11120040);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3195184);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12141800);
				//Applicants with more than 1 paid non-CAT claim and/or more than 1 paid CAT claim in the last 3 years are ineligible
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS1020340_OR);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12023000);
				break;
			case "SD":
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS11120040);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3195184);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12141800);
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_WM_0523_SD);
				break;
			default:
				//WM-0548:Dwellings built prior to 1900 are ineligible
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS11120040);
				//WM-0550: Risks with more than 3 horses or 4 livestock are unacceptable
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3195184);
				//ER-0913: Underwriting approval required. Primary home of the applicant is not insured
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12141800);
				//WM-0523: Applicants with 2 or more paid non-CAT claims OR 2 or more paid CAT claim
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS4040546);
				//ER-1607: Applicants with any liability claims in the past 3 years are ineligible
				errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12023000);
				break;
		}
		errorTab.cancel();

		BindTab.buttonSaveAndExit.click();
		log.info("TEST Determine Eligibility SC2: HSS Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

	}

	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testDetermineEligibility_SC3(@Optional("") String state) {
		mainApp().open();

		TestData td_sc3_1 = getTestSpecificTD("TestData_SC3_1");

		//getCopiedQuote();
		createCustomerIndividual();
		createQuote();

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		premiumsTab.calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
		MortgageesTab mortgageesTab = new MortgageesTab();
		mortgageesTab.fillTab(td_sc3_1);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		BindTab bindTab = new BindTab();
		bindTab.btnPurchase.click();

		ErrorTab errorTab = new ErrorTab();

		//WM-0559: More than 2 additional Insureds require Underwriting approval
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3230162);
		//WM-0560: More than 2 additional Interests require Underwriting approval
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3230756);

		errorTab.cancel();
		BindTab.buttonSaveAndExit.click();
		log.info("TEST Determine Eligibility SC3: HSS Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

	}
	
	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testDetermineEligibility_SC4(@Optional("") String state) {
		mainApp().open();

		TestData td_sc2_1 = getTestSpecificTD("TestData_SC2_1");
		TestData td_sc2_2 = getTestSpecificTD("TestData_SC2_2");
		TestData td_sc2_3 = getTestSpecificTD("TestData_SC2_3");

		//getCopiedQuote();
		createCustomerIndividual();
		createQuote();

		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		ApplicantTab applicantTab = new ApplicantTab();
		applicantTab.fillTab(td_sc2_1);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
			
		propertyInfoTab.fillTab(td_sc2_2);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		premiumsTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		BindTab bindTab = new BindTab();
		bindTab.btnPurchase.click();

		ErrorTab errorTab = new ErrorTab();
		
		//WM-0912: Coverage A greater than 120% of replacement cost requires underwriting approval
		//errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS1160000); //rule disabled according to PPS-371
	    //WM-0549: Dwellings built prior to 1940 must have all four major systems fully renovated.
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3282256);
		//WM-0550: Risks with more than 3 horses or 4 livestock are unacceptable
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3200008);
		//ER-0913: Underwriting approval required. Primary home of the applicant is not insured
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12141800);
		//ER-1607: Applicants with any liability claims in the past 3 years are ineligible
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12023000);

		errorTab.cancel();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(td_sc2_3);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.btnPurchase.click();

		if (!getState().equals("MD")) {
			//For MD there is delta-rule AAA_HO_SS1162304_MD - Coverage A greater than $2,000,000 requires underwriting approval
			//WM-0531: Coverage A greater than $1,000,000 requires underwriting approval
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS1162304);
		}

		if (!getState().equals("OR")) {
			//For OR this rule verifying in Delta tests: it's displaying when one more claim added older than this
			//WM-0530: Applicants with any paid claims over $25,000 in the last 3 years are ineligible.
			errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12200234);
		}
		errorTab.cancel();

		BindTab.buttonSaveAndExit.click();
		log.info("TEST Determine Eligibility SC4: HSS Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

	}


}
