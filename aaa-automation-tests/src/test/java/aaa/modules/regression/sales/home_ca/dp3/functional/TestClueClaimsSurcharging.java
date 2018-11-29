package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ClaimConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

@StateList(states = Constants.States.CA)
public class TestClueClaimsSurcharging extends HomeCaDP3BaseTest {

	/**
 	* @author Dominykas Razgunas
 	* @name Test Claims points on VRD page for CA DP3 policies during NB Applicant/Applicant And Property
 	* @scenario
 	* 1. Create customer
 	* 2. Initiate CA DP3 quote
 	* 3. Validate claims display on VRD page with points for Applicant and Property Claims
 	* 4. Change Claim To Applicant
 	* 5. Validate claims do not display on VRD page with points for Applicant
 	*/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.HOME_CA_DP3, testCaseId = "PAS-18337")
	public void pas18337_testClueClaimsSurcharging(@Optional("CA") String state) {

		TestData tdCustomer = getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "BruceDP")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "Kohli")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), "132 Test street")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_TYPE.getLabel()), "Residence")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.CITY.getLabel()), "BELL GARDENS")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), "90201");

		TestData tdPolicy = getPolicyTD()
				.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
						HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), "90201")
				.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
						HomeCaMetaData.ApplicantTab.DwellingAddress.CITY.getLabel()), "BELL GARDENS")
				.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
						HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), "132 Test street");

		mainApp().open();
		createCustomerIndividual(tdCustomer);
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdPolicy, PropertyInfoTab.class, true);

		new PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM).setValue("Yes");
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		PropertyQuoteTab.RatingDetailsView.open();


		TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys()).isEmpty();
		// Assert that Applicant and Property Claim2 is giving points
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isNotEmpty();


		PropertyQuoteTab.RatingDetailsView.close();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		new PropertyInfoTab().tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, ClaimConstants.CauseOfLoss.WATER)
				.getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		new PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM).setValue("Yes");
		new PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.LOSS_FOR).setValue(ClaimConstants.LossFor.APPLICANT);

		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		PremiumsAndCoveragesQuoteTab.linkViewRatingDetails.click();

		TestData claimsVRD1 = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD1.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD1.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys()).isEmpty();
		// Assert that Applicant Claim2 is not giving points
		assertThat(claimsVRD1.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
	}
}