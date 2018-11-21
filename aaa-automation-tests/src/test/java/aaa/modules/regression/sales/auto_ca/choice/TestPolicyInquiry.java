package aaa.modules.regression.sales.auto_ca.choice;

import static toolkit.verification.CustomAssertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author oreva
 * @name Test Policy Inquiry
 * @scenario
 * 1. Find customer or create new. 
 * 2. Create a new Auto CA Choice policy.
 * 3. Select 'Inquiry' in Take Action drop down. 
 * 4. Verify that policy is opened in Inquiry mode. 
 */
public class TestPolicyInquiry extends AutoCaChoiceBaseTest {
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE) 
	public void testPolicyInquiry(@Optional("CA") String state) {
		mainApp().open();
		getCopiedPolicy();		
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		String totalTermPremium = PolicySummaryPage.tableCoveragePremiumSummary.getRow("Coverage", "Total Actual Premium for Veh #1").getCell(4).getValue();
		
		policy.policyInquiry().start();
		
		NavigationPage.toViewTab(AutoCaTab.MEMBERSHIP.get());
		assertThat(new MembershipTab().getAssetList().getAsset(AutoCaMetaData.MembershipTab.ORDER_REPORT)).isDisabled();
		
		NavigationPage.toViewTab(AutoCaTab.PREMIUM_AND_COVERAGES.get());
		assertThat(PremiumAndCoveragesTab.labelProductInquiry).valueContains("CA Choice");
		assertThat(PremiumAndCoveragesTab.totalTermPremium).hasValue(totalTermPremium);		
		assertThat(new PremiumAndCoveragesTab().getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM)).isDisabled();
		
		NavigationPage.toViewTab(AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
		assertThat(new DriverActivityReportsTab().getAssetList().getAsset(AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY)).isDisabled();

		NavigationPage.toViewTab(AutoCaTab.DOCUMENTS_AND_BIND.get());
		assertThat(DocumentsAndBindTab.btnPurchase).isDisabled();		
		Tab.buttonCancel.click();	
		
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);		
	}
}
