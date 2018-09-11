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

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_097_GenerateBill(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
		Dollar initialMinDue = BillingSummaryPage.getMinimumDue();
		int initialBillsCount = BillingSummaryPage.tableBillsStatements.getAllRowsCount();

		billingAccount.generateFutureStatement().perform();

		BillingSummaryPage.getMinimumDue().verify.moreThan(initialMinDue);
		CustomAssertions.assertThat(BillingSummaryPage.tableBillsStatements).as("New bill is shown in table").hasRows(initialBillsCount+1);
	}
}
