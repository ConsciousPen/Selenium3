package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.CA;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.verification.CustomAssertions;

public class BillGenerationTest extends BackwardCompatibilityBaseTest {

	/**
	 * @author Deloite
	 * @name Realtime Payments - Accept Direct Payment
	 * @scenario
	 * 1. Pick an active CA policy with atleast 1 bill need to be generated.
	 * 2. Navigate to billing page and select the option generate the future statement from the drop dowm.
	 * 3. A pop up is generated Validate by  Clicking of No , page remains on Billing.
	 * 4. Validate by  Clicking of Yes ,  Bill should be generated.
	 * Check:
	 * 1. Future bill need to be generated
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_097_GenerateBill(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openBilling(policyNumber);

		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
		Dollar initialMinDue = BillingSummaryPage.getMinimumDue();
		int initialBillsCount = BillingSummaryPage.tableBillsStatements.getAllRowsCount();

		billingAccount.generateFutureStatement().perform();

		BillingSummaryPage.getMinimumDue().verify.moreThan(initialMinDue);
		CustomAssertions.assertThat(BillingSummaryPage.tableBillsStatements).as("New bill is shown in table").hasRows(initialBillsCount+1);
	}
}
