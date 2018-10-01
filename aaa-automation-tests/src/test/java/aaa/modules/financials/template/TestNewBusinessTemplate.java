package aaa.modules.financials.template;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import toolkit.utils.datetime.DateTimeUtils;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

	protected void testNewBusinessScenario_1() {

		// Create policy WITHOUT employee benefit
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy();
		LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();

		// Advance time one week and perform premium-bearing endorsement (additional premium)
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(effDate.plusWeeks(1));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().perform(getEndorsementTD());
		policy.getDefaultView().fill(getTestSpecificTD("TestData_AddPremium"));

		// Pay additional premium
		payAmountDue();

		// Advance time another week and open policy
		mainApp().close();
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

	protected void testNewBusinessScenario_2() {

		// Create policy WITHOUT employee benefit, effective date three weeks from today
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime effDate = today.plusWeeks(3);
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy(adjustTdPolicyEffDate(getPolicyTD(), effDate));

		// Advance time 3 days and perform premium-reducing endorsement (return premium)
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(today.plusDays(3));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getTestSpecificTD("TestData_ReducePremium"));

		// Advance time another week and open policy
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(today.plusWeeks(1));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		// Cancel policy
		policy.cancel().perform(getCancellationTD(effDate));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

		// Advance time and reinstate policy with lapse
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay));
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(effDate).plusDays(3));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.reinstate().perform(getReinstatementTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

}
