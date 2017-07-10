package aaa.modules.policy.home_ss;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.utils.TestInfo;

public class TestPolicyReinstatement extends HomeSSBaseTest {
	
	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyReinstatement(){
		mainApp().open();
		
		createPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		String tdName = this.getClass().getSimpleName();	
		
		new HomeSSPolicyActions.Cancel().perform(tdPolicy.getTestData(tdName, "TestData_Cancellation"));
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		
		new HomeSSPolicyActions.Reinstate().perform(tdPolicy.getTestData(tdName, "TestData_Reinstatement"));
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

}
