package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.CA;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.verification.CustomAssertions;

public class BillGenerationTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_097_GenerateBill(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_097_GenerateBill", "SelectPolicy").get(0);

		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
		Dollar initialMinDue = BillingSummaryPage.getMinimumDue();
		int initialBillsCount = BillingSummaryPage.tableBillsStatements.getAllRowsCount();

		new BillingAccount().generateFutureStatement().perform();

		BillingSummaryPage.getMinimumDue().verify.moreThan(initialMinDue);
		CustomAssertions.assertThat(BillingSummaryPage.tableBillsStatements).as("New bill is shown in table").hasRows(initialBillsCount+1);
	}
}
