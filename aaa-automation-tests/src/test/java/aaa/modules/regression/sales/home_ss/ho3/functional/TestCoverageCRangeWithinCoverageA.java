package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

@StateList(statesExcept = Constants.States.CA)
public class TestCoverageCRangeWithinCoverageA extends HomeSSHO3BaseTest {

	private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();

	/**
	 * @author Dominykas Razgunas
	 * @name Test Coverage C limit 70% within of Coverage A
	 * @scenario
	 * 1. Create Individual Customer
	 * 2. Create Property Policy
	 * 3. Initiate Renewal
	 * 4. Navigate To Property Info Tab
	 * 5. Change Cov A to 271215 so that Cov C is calculated by the system to be 189851
	 * 6. Navigate to P&C
	 * 7. Calculate Premium Issue Policy
	 * 8. Renew Policy
	 * 9. Endorse Policy and Calculate Premium
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Coverage C limit 70% within of Coverage A")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-20367")
	protected void pas20367_TestCoverageCRangeWithinCoverageATemplate(@Optional("") String state){

		String policyNumber = openAppAndCreatePolicy();
		TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getExpirationDate());

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.renew().perform();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getPropertyValueAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT).setValue("271215");
		propertyInfoTab.getPropertyValueAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST).setValue("271000");
		premiumsAndCoveragesQuoteTab.calculatePremium();
		// Check that Premium is calculated with no Error page
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).isPresent();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();
		payTotalAmtDue(policyNumber);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		premiumsAndCoveragesQuoteTab.calculatePremium();
		// Check that Premium is calculated with no Error page
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).isPresent();
	}
}