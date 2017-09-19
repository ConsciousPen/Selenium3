package aaa.modules.bct.billing_and_payments;

import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.verification.CustomAssert;

public class BillGenerationTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_097_GenerateBill(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_097_GenerateBill", "SelectPolicy").get(0);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
		Dollar initialMinDue = BillingSummaryPage.getMinimumDue();
		int initialBillsCount = BillingSummaryPage.tableBillsStatements.getRowsCount();

		new BillingAccount().generateFutureStatement().perform();

		BillingSummaryPage.getMinimumDue().verify.moreThan(initialMinDue);
		CustomAssert.assertEquals("New bill is shown in table", initialBillsCount+1, BillingSummaryPage.tableBillsStatements.getRowsCount());
	}
}
