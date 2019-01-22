package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.enums.NavigationEnum.HomeCaTab;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
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
public class TestPolicyInquiry extends HomeCaHO3BaseTest {
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
	public void testPolicyInquiry(@Optional("CA") String state) {
		String totalPremium;
		
		if(getUserGroup().equals(UserGroups.B31.get())) {
			mainApp().open(getLoginTD(UserGroups.QA));
			createCustomerIndividual();
			createPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			mainApp().close();
			
			//Login with B31 user
			mainApp().open(getLoginTD(UserGroups.B31));
			MainPage.QuickSearch.buttonSearchPlus.click();
			SearchPage.openPolicy(policyNumber);
			totalPremium = PolicySummaryPage.tableTotalPremiumSummaryProperty.getRow(1).getCell(2).getValue();
			policy.policyInquiry().start();		
			verifyPolicyInInquiryMode(totalPremium);
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		}
		else {
			mainApp().open();
			getCopiedPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);		
			totalPremium = PolicySummaryPage.tableTotalPremiumSummaryProperty.getRow(1).getCell(2).getValue();
			
			policy.policyInquiry().start();		
			verifyPolicyInInquiryMode(totalPremium);
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		}
	}
	
	public void verifyPolicyInInquiryMode(String totalPremium) {
		NavigationPage.toViewTab(HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		assertThat(PremiumsAndCoveragesQuoteTab.btnCalculatePremium).isDisabled();
		assertThat(PremiumsAndCoveragesQuoteTab.linkViewRatingDetails).isEnabled(); 
		assertThat(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue()).isEqualTo(totalPremium);
		
		NavigationPage.toViewTab(HomeCaTab.BIND.get());
		assertThat(new BindTab().btnPurchase).isDisabled();
		
		Tab.buttonCancel.click();
	}

}
