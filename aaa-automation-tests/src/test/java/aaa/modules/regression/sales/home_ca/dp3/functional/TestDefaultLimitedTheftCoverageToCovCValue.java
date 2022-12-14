
package aaa.modules.regression.sales.home_ca.dp3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.EndorsementForms;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test Default limited theft coverage value to cov c value NB
 * @scenario
 * 1. Create Customer
 * 2. Initiate Home CA DP3 quote
 * 3. FIll quote
 * 4. Navigate to Endorsement Tab
 * 5. Add DP 04 73 endorsement
 * 6. Calculate Premium
 * 7. Check that Cov C value is equal to endorsement form value
 * @details
 */
public class TestDefaultLimitedTheftCoverageToCovCValue extends HomeCaDP3BaseTest {

	private String endorsementID = EndorsementForms.HomeCAEndorsementForms.DP_04_73.getFormId();

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-25050 Test Default limited theft coverage value to cov c value NB")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-25050")
	public void pas25050_defaultLimitedTheftCoverageComparisonToCovCNB(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), EndorsementTab.class, true);

		addEndorsementAndCheckValues();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Default limited theft coverage value to cov c value Endorsement
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Home CA DP3 policy
	 * 3. Initiate endorsement
	 * 4. Navigate to Endorsement Tab
	 * 5. Add DP 04 73 endorsement
	 * 6. Calculate Premium
	 * 7. Check that Cov C value is equal to endorsement form value
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-25050 Test Default limited theft coverage value to cov c value Endorsement")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-25050")
	public void pas25050_defaultLimitedTheftCoverageComparisonToCovCEndorsement(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());

		addEndorsementAndCheckValues();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test Default limited theft coverage value to cov c value Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Home CA DP3 policy
	 * 3. Initiate Renewal
	 * 4. Navigate to Endorsement Tab
	 * 5. Add DP 04 73 endorsement
	 * 6. Calculate Premium
	 * 7. Check that Cov C value is equal to endorsement form value
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-25050 Test Default limited theft coverage value to cov c value Renewal")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-25050")
	public void pas25050_defaultLimitedTheftCoverageComparisonToCovCRenewal(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());

		addEndorsementAndCheckValues();
	}

	private void addEndorsementAndCheckValues(){
		// add endorsement form
		new EndorsementTab().getAddEndorsementLink(endorsementID).click();
		//calculate Premium and save Cov C + Endorsement form values. Assert that they are equal
		new PremiumsAndCoveragesQuoteTab().calculatePremium();

		String value1 = new PremiumsAndCoveragesQuoteTab().getAssetList()
				.getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C).getValue();

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.ENDORSEMENT.get());
		new EndorsementTab().getLinkEdit(endorsementID).click();
		String value2 = new EndorsementTab().getAssetList().getAsset(HomeCaMetaData.EndorsementTab.DP_04_73).getAsset(HomeCaMetaData.EndorsementTab.EndorsementDP0473.COVERAGE_LIMIT).getValue().replace("$", "").replaceAll(",", "");

		// assert Endorsement tab endorsement coverage limit value contains Cov C value
		assertThat(value1).contains(value2);
	}
}