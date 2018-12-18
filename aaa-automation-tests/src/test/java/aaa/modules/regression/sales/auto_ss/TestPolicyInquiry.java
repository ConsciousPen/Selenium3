package aaa.modules.regression.sales.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
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
public class TestPolicyInquiry extends AutoSSBaseTest {
	
	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyInquiry(@Optional("") String state) {
		if (getUserGroup().equals(UserGroups.B31.get())) {
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
			policy.policyInquiry().start();
			verifyPolicyInInquiryMode();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);	
		}
		else {
			mainApp().open();
			getCopiedPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			
			policy.policyInquiry().start();
			verifyPolicyInInquiryMode();			
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		}
	}
	
	private void verifyPolicyInInquiryMode() {
		NavigationPage.toViewTab(AutoSSTab.RATING_DETAIL_REPORTS.get());
		assertThat(new RatingDetailReportsTab().getAssetList().getAsset(AutoSSMetaData.RatingDetailReportsTab.ORDER_REPORT)).isDisabled();
		
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM)).isDisabled();
		assertThat(PremiumAndCoveragesTab.buttonViewRatingDetails).isEnabled();
		
		NavigationPage.toViewTab(AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		assertThat(new DriverActivityReportsTab().getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY)).isDisabled();
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		assertThat(DocumentsAndBindTab.btnPurchase).isDisabled();
		Tab.buttonCancel.click();	
	}
}
