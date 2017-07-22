package aaa.modules.policy.auto_ca;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test futuredated policy
 * @scenario 
 * 1. Find customer or create new if customer does not exist; 
 * 2. Create CA Select Auto Policy 
 * 3. Set Quote effective date > current date 
 * 4. Verify Policy status is ' Policy Pending' 
 * @details
 */

public class TestPolicyFuturedated extends AutoCaBaseTest {
	@Test
	@TestInfo(component = "Policy.AutoCA")
	public void testpolicyfuturedate() {
		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");

		//adjust default policy data with effective date = today plus 10 days
		TestData td = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(TestData.makeKeyPath("GeneralTab",
						AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
						"/today+10d:MM/dd/yyyy");

		getPolicyType().get().createPolicy(td);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);

		PolicySummaryPage.labelPolicyEffectiveDate.verify
				.contains(td.getTestData("GeneralTab", AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel()).getValue(AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()));

	}

}
