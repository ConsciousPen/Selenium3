package aaa.modules.financials.template;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import aaa.modules.financials.FinancialsSQL;
import toolkit.utils.datetime.DateTimeUtils;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

    /**
     * @scenario
     * 1. Create new policy bound on or after effective date WITHOUT employee benefit
     * 2. Advance time one week
     * 3. Perform endorsement resulting in additional premium
     * 4. Advance time one week
     * 5. Cancel policy
     * 6. Reinstate policy with no lapse (reinstatement eff. date same as cancellation date)
     * @details NBZ-01, PMT-01, END-01, CNL-01, RST-01, PMT-06, PMT-19 (Auto CA only: FEE-01, FEE-15, FEE-06, CA HO only: FEE-04)
     */
	protected void testNewBusinessScenario_1() {

		// Create policy WITHOUT employee benefit
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy();
		LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();
		Dollar premTotal = getTotalTermPremium();

        // NB validations
        assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicyNB(policyNumber, "1044"));
        assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicyNB(policyNumber, "1022")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicyNB(policyNumber, "1022")));

        Dollar addedPrem = performAPEndorsement(effDate, policyNumber);

        // AP endorsement validations
        assertThat(addedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicyEndorsement(policyNumber, "1001"));
        assertThat(addedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicyEndorsement(policyNumber, "1044"));
        assertThat(addedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicyEndorsement(policyNumber, "1044"));
        assertThat(addedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicyEndorsement(policyNumber, "1022")
                .subtract(FinancialsSQL.getCreditsForAccountByPolicyEndorsement(policyNumber, "1022")));

//		// Cancel policy
		policy.cancel().perform(getCancellationTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		// TODO implement DB validation

		// Reinstate policy without lapse
		policy.reinstate().perform(getReinstatementTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        // TODO implement DB validation
	}

    /**
     * @scenario
     * 1. Create new policy with effective date three weeks from now, WITHOUT employee benefit
     * 2. Advance time 3 days
     * 3. Perform endorsement resulting in reduction (return) of premium (add vehicle for for CA Auto)
     * 4. Advance time 4 days
     * 5. Cancel policy
     * 6. Advance time one week
     * 6. Reinstate policy with lapse
     * 7. Remove reinstatement lapse
     * @details NBZ-03, PMT-04, END-02, CNL-06, PMT-05, RST-02, RST-07, RST-09
     */
	protected void testNewBusinessScenario_2() {

		// Create policy WITHOUT employee benefit, effective date three weeks from today
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime effDate = today.plusWeeks(3);
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy(adjustTdPolicyEffDate(getPolicyTD(), effDate));

        performRPEndorsement(today, policyNumber);
        // TODO implement DB validation

		// Cancel policy
		policy.cancel().perform(getCancellationTD(effDate));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

		// Advance time and reinstate policy with lapse
        performReinstatementWithLapse(effDate, policyNumber);
        // TODO implement DB validation

		//TODO need to change the reinstatement lapse RST-07, then remove the lapse RST-09

	}

    /**
     * @scenario
     * 1. Create new policy bound on or after effective date WITH employee benefit
     * 2. Advance time one week
     * 3. Perform endorsement resulting in additional premium
     * 4. Advance time one week
     * 5. Cancel policy
     * 6. Reinstate policy with no lapse (reinstatement eff. date same as cancellation date)
     * @details NBZ-02, PMT-01, END-03, CNL-02, RST-03, PMT-06, PMT-19
     */
	protected void testNewBusinessScenario_3() {

        // Create policy WITH employee benefit
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdWithEmpBenefit(getPolicyTD()));
        LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();
        Dollar premTotal;
        if (!getPolicyType().isAutoPolicy()) {
            premTotal = PolicySummaryPage.getTotalPremiumSummaryForProperty();
        } else {
            premTotal = new Dollar(PolicySummaryPage.getAutoCoveragesSummaryTestData().getValue("Total Actual Premium"));
        }

        // NB validations
        //TODO implement

        performAPEndorsement(effDate, policyNumber);
        // TODO implement DB validation

        // Cancel policy
        policy.cancel().perform(getCancellationTD());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
        // TODO implement DB validation

        // Reinstate policy without lapse
        policy.reinstate().perform(getReinstatementTD());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        // TODO implement DB validation

    }

    /**
     * @scenario
     * 1. Create new policy with effective date three weeks from now, WITH employee benefit
     * 2. Advance time 3 days
     * 3. Perform endorsement resulting in reduction (return) of premium (add vehicle for for CA Auto)
     * 4. Advance time 4 days
     * 5. Cancel policy
     * 6. Advance time one week
     * 6. Reinstate policy with lapse
     * 7. Remove reinstatement lapse
     * @details NBZ-04, PMT-04, END-04, CNL-07, PMT-05, RST-04, RST-08, RST-10
     */
    protected void testNewBusinessScenario_4() {

        // Create policy WITH employee benefit, effective date three weeks from today
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime effDate = today.plusWeeks(3);
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdWithEmpBenefit(adjustTdPolicyEffDate(getPolicyTD(), effDate)));

        performRPEndorsement(today, policyNumber);
        // TODO implement DB validation

        // Cancel policy
        policy.cancel().perform(getCancellationTD(effDate));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

        // Advance time and reinstate policy with lapse
        performReinstatementWithLapse(effDate, policyNumber);
        // TODO implement DB validation

        //TODO need to change the reinstatement lapse RST-08, then remove the lapse RST-10
    }

    private Dollar performAPEndorsement(LocalDateTime effDate, String policyNumber) {
        // Advance time one week and perform premium-bearing endorsement (additional premium)
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(effDate.plusWeeks(1));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getEndorsementTD());
        policy.getDefaultView().fill(getAddPremiumTD());

        // Pay additional premium
        Dollar addedPrem = payAmountDue();

        // Advance time another week and open policy
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(effDate.plusWeeks(2));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        return addedPrem;
    }

    private void performRPEndorsement(LocalDateTime today, String policyNumber) {
        // Advance time 3 days and perform premium-reducing endorsement (return premium)
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(today.plusDays(3));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getEndorsementTD(today.plusWeeks(3)));
        policy.getDefaultView().fill(getReducePremiumTD());

        // Advance time another week and open policy
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(today.plusWeeks(1));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
    }

    private void performReinstatementWithLapse(LocalDateTime effDate, String policyNumber) {
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay));
        JobUtils.executeJob(Jobs.changeCancellationPendingPoliciesStatus);
        TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(20));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.reinstate().perform(getReinstatementTD());
        if (Page.dialogConfirmation.buttonYes.isPresent()) {
            Page.dialogConfirmation.buttonYes.click();
        }
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    private Dollar getTotalTermPremium() {
        if (!getPolicyType().isAutoPolicy()) {
            return PolicySummaryPage.getTotalPremiumSummaryForProperty();
        } else if (isStateCA()){
            return new Dollar(PolicySummaryPage.tableCoveragePremiumSummaryCA.getRow(2).getCell(2).getValue());
        } else {
            return new Dollar(PolicySummaryPage.getAutoCoveragesSummaryTestData().getValue("Total Term Premium"));
        }
    }

}
