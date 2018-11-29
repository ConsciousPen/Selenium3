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
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

@StateList(states = Constants.States.CA)
public class TestClueClaimsSurcharging extends HomeCaDP3BaseTest {

	private ApplicantTab applicantTab = new ApplicantTab();
	private ReportsTab reportsTab = new ReportsTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();

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
	public void pas18337_testClueClaimsSurchargingNB(@Optional("CA") String state) {

		// Testdata for Customer
		TestData tdCustomer = getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "BruceDP")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "Kohli")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), "132 Test street")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_TYPE.getLabel()), "Residence")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.CITY.getLabel()), "BELL GARDENS")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), "90201");

		// TestData For Policy
		TestData tdPolicy = getPolicyTD()
				.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
						HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), "90201")
				.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
						HomeCaMetaData.ApplicantTab.DwellingAddress.CITY.getLabel()), "BELL GARDENS")
				.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
						HomeCaMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), "132 Test street");

		// Open App Initiate Policy
		mainApp().open();
		createCustomerIndividual(tdCustomer);
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdPolicy, PropertyInfoTab.class, true);

		checkClaimChargedPoints();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Claims points on VRD page for CA DP3 policies during NB Applicant/Applicant And Property
	 * @scenario
	 * 1. Create customer
	 * 2. Create CA DP3 policy
	 * 3. Initiate Renewal Add new Applicant
	 * 4. Validate claims display on VRD page with points for Applicant and Property Claims
	 * 5. Change Claim To Applicant
	 * 6. Validate claims do not display on VRD page with points for Applicant
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.HOME_CA_DP3, testCaseId = "PAS-18337")
	public void pas18337_testClueClaimsSurchargingRenewal(@Optional("CA") String state) {

		// Open App Create Policy
		openAppAndCreatePolicy();

		// Initiate renewal and add a named insured that returns additional Clue claims
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());

		// Create TestData for new Applicant
     	TestData tdNewApplicant = DataProviderFactory.dataOf(
		HomeCaMetaData.ApplicantTab.NamedInsured.BTN_ADD_INSURED.getLabel(), "Click",
				HomeCaMetaData.ApplicantTab.NamedInsured.CUSTOMER_SEARCH.getLabel(), DataProviderFactory.emptyData(),
				HomeCaMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), "BruceDP",
				HomeCaMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), "Kohli",
				HomeCaMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), "Parent",
				HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "12/12/1985",
				HomeCaMetaData.ApplicantTab.NamedInsured.OCCUPATION.getLabel(), "Other");

		TestData tdApplicantTab = DataProviderFactory.dataOf(applicantTab.getClass().getSimpleName(),
				DataProviderFactory.dataOf(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), tdNewApplicant));

     	// Add new Applicant reorder reports
		applicantTab.fillTab(tdApplicantTab).submitTab();
		reportsTab.getAssetList().getAsset(HomeCaMetaData.ReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
		reportsTab.getAssetList().getAsset(HomeCaMetaData.ReportsTab.CLUE_REPORT).getTable().getRow(1).getCell("Report").controls.links.getFirst().click();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());

		checkClaimChargedPoints();
		}

	private void checkClaimChargedPoints(){
		// Calculate Premium and Check open VRD
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM).setValue("Yes");
		premiumsAndCoveragesQuoteTab.calculatePremium();
		PropertyQuoteTab.RatingDetailsView.open();

		TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys()).isEmpty();
		// Assert that Applicant and Property Claim2 is giving points
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isNotEmpty();
		PropertyQuoteTab.RatingDetailsView.close();

		// Change Claim to Applicant Calculate Premium and Open App
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, ClaimConstants.CauseOfLoss.WATER)
				.getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM).setValue("Yes");
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.LOSS_FOR).setValue(ClaimConstants.LossFor.APPLICANT);

		premiumsAndCoveragesQuoteTab.calculatePremium();
		PremiumsAndCoveragesQuoteTab.linkViewRatingDetails.click();

		TestData claimsVRD1 = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD1.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD1.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys()).isEmpty();
		// Assert that Applicant Claim2 is not giving points
		assertThat(claimsVRD1.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
	}

}