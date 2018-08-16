package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Lina Li
 * @name Test renew decline by customer
 * @scenario
 * 1. Create Customer
 * 2. Create a Policy 
 * 3. Renew Policy
 * 4. Decline by customer
 * 5. Verify Policy status is 'Customer Declined'
 * @details
 */

public class PolicyRenewDeclineByCompany extends PolicyBaseTest {
	public void testPolicyRenewDeclineByCompany() {
		mainApp().open();

		getCopiedPolicy();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		log.info("TEST: Decline By Company Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

		policy.renew().performAndExit();

		PolicySummaryPage.buttonRenewals.click();

		policy.declineByCompanyQuote().perform(getPolicyTD("DeclineByCompany", "TestData_Plus1Year"));
		PolicySummaryPage.buttonRenewals.click();

		assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell(4)).hasValue(ProductConstants.PolicyStatus.COMPANY_DECLINED);
	}

}
