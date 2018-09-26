package aaa.modules.financials.template;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

	protected void testNewBusinessScenario_1() {

		// Create policy WITHOUT employee benefit
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy();
		LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();

		// Advance time one week and perform premium-bearing endorsement (additional premium)
		TimeSetterUtil.getInstance().nextPhase(effDate.plusWeeks(1));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().perform(getEndorsementTD());
		policy.getDefaultView().fill(getTestSpecificTD("TestData_AddPremium"));

		// Pay additional premium
		payAmountDue();

		// Advance time another week and open policy
		TimeSetterUtil.getInstance().nextPhase(effDate.plusWeeks(2));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		// Cancel policy
		policy.cancel().perform(getCancellationTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		// Reinstate policy without lapse
		policy.reinstate().perform(getReinstatementTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

}
