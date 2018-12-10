package aaa.modules.regression.sales.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author oreva
 * @name Test Policy Inquiry
 * @scenario
 * 1. Find customer or create new. 
 * 2. Create a new Auto SS policy.
 * 3. Select 'Inquiry' in Take Action drop down. 
 * 4. Verify that policy is opened in Inquiry mode.
 */
public class TestPolicyInquiry extends HomeSSHO3BaseTest{
	
	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testPolicyInquiry(@Optional("") String state) {
		mainApp().open();
		//getCopiedPolicy();
		createCustomerIndividual();
		createPolicy();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		String totalPremium = PolicySummaryPage.tableTotalPremiumSummaryProperty.getRow(1).getCell(2).getValue();
		
		policy.policyInquiry().start();
		
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());		
		assertThat(PremiumsAndCoveragesQuoteTab.btnCalculatePremium).isDisabled();
		assertThat(PremiumsAndCoveragesQuoteTab.linkViewRatingDetails).isEnabled();
		assertThat(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue()).isEqualTo(totalPremium);
		
		NavigationPage.toViewTab(HomeSSTab.BIND.get());		
		assertThat(new BindTab().btnPurchase).isDisabled();
		
		Tab.buttonCancel.click();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

}
