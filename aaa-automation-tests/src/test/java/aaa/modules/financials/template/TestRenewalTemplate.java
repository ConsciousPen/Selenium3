package aaa.modules.financials.template;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;

public class TestRenewalTemplate extends FinancialsBaseTest {

    /**
     * @scenario
     * 1. Create policy WITHOUT employee benefit with monthly payment plan.
     * 2. Advance time 1 month and pay installment amount (and full amount due)
     * 3. Advance policy through renewal cycle
     * 4. Pay amount due and verify renewal is active
     * 5. Create AP endorsement with eff. date today + 2 days
     * 6. Create AP endorsement with eff. date today + 1 day (OOS)
     * 7. Roll back endorsement
     * 8. Cancel policy ON or AFTER effective date
     * @details PMT-02, RNW-01, END-05, END-07, CNL-03
     */
    protected void testRenewalScenario_1() {

        // Create policy WITHOUT employee benefit with monthly payment plan
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
        LocalDateTime dueDate = PolicySummaryPage.getEffectiveDate().plusMonths(1);

        // Advance time 1 month, generate and pay first installment bill
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate));
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dueDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        Dollar installmentAmt = payMinAmountDue();

        // TODO Validate PMT-02

        // Pay off remaining balance on policy
        SearchPage.openPolicy(policyNumber);
        payTotalAmountDue();

        // Perform Endorsement effective today+2days and AP OOS Endorsement effective today+1day
        SearchPage.openPolicy(policyNumber);
        performNonPremBearingEndorsement(policyNumber, dueDate.plusDays(2));
        Dollar endorsementOosAmt = performAPEndorsement(policyNumber, dueDate.plusDays(1));

        // TODO Validate END-05

        // TODO Implement rollback endorsement and validation for END-07

        cancelPolicy();

        // TODO validate CNL-03


    }

}
