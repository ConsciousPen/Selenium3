package aaa.modules.regression.sales.template;

import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author N. Belakova
 * @name Test futuredated policy
 * @scenario 
 * 1. Find customer or create new if customer does not exist; 
 * 2. Create CA Select Auto Policy 
 * 3. Set Quote effective date > current date 
 * 4. Verify Policy status is ' Policy Pending' 
 * @details
 */

public abstract class PolicyFuturedated extends PolicyBaseTest {
	
	public void testPolicyFuturedated() {
		
		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");

		//adjust default policy data with effective date = today plus 10 days
		TestData td = getPolicyTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(new GeneralTab().getMetaKey(),
						AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
						DateTimeUtils.getCurrentDateTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY));
						

		getPolicyType().get().createPolicy(td);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);

		PolicySummaryPage.labelPolicyEffectiveDate.verify
				.contains(td.getTestData(new GeneralTab().getMetaKey(), 
										AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel()).getValue(AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()));

	}

}
