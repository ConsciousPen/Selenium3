package aaa.modules.financials.template;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import aaa.modules.financials.FinancialsSQL;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

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
     * 8. Move time point and bind renewal ON or AFTER the renewal effective date
     * @details PMT-02, RNW-01, END-05, END-07, FEE-10
     */
    protected void testRenewalScenario_1() {

        // Create policy WITHOUT employee benefit with monthly payment plan
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
        LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime dueDate = PolicySummaryPage.getEffectiveDate().plusMonths(1);

        // Advance time 1 month, generate and pay first installment bill
        LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(dueDate);
        LocalDateTime billDueDate = getTimePoints().getBillDueDate(dueDate);
        TimeSetterUtil.getInstance().nextPhase(billGenDate);
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        TimeSetterUtil.getInstance().nextPhase(billDueDate);

        mainApp().open();
        SearchPage.openBilling(policyNumber);
        Dollar nonEftFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);
        Dollar installmentAmt = payMinAmountDue();

        assertSoftly(softly -> {
            // FEE-10 validations
            softly.assertThat(nonEftFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(billGenDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));
            softly.assertThat(nonEftFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(billGenDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1040"));

            // PMT-02 validations
            softly.assertThat(installmentAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MANUAL_PAYMENT, "1001"));
            softly.assertThat(installmentAmt.subtract(nonEftFee)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MANUAL_PAYMENT, "1044"));
            softly.assertThat(nonEftFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));
        });

        // Pay off remaining balance on policy
        SearchPage.openPolicy(policyNumber);
        payTotalAmountDue();

        // Perform Endorsement effective today+2days and AP OOS Endorsement effective today+1day
        SearchPage.openPolicy(policyNumber);
        performNonPremBearingEndorsement(policyNumber, dueDate.plusDays(2));
        performAPEndorsement(policyNumber, dueDate.plusDays(1));
        policy.rollOn().perform(false, true);

        // TODO Validate END-07

        // Roll back endorsement
        policy.rollBackEndorsement().perform(getPolicyTD("EndorsementRollBack", "TestData"));

        // TODO Validate END-05

        // Move to renewal timepoint and propose renewal image
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        Dollar renewalAmt = payTotalAmountDue();

        // TODO Validate RNW-01

    }

    /**
     * @scenario
     * 1. Create policy WITHOUT employee benefit with monthly payment plan.
     * 2. Advance time 1 month and pay installment amount (and full amount due)
     * 3. Decline payment for reason = NSF
     * 4. Create AP endorsement with eff. date today + 2 days
     * 5. Create AP endorsement with eff. date today + 1 day (OOS)
     * 6. Roll back endorsement
     * 7. Move time point and bind renewal BEFORE the renewal effective date
     * @details FEE-07, RNW-03, END-05, END-07
     */
    protected void testRenewalScenario_2() {

        // Create policy WITHOUT employee benefit with monthly payment plan
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
        LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime dueDate = PolicySummaryPage.getEffectiveDate().plusMonths(1);

        // Advance time 1 month, generate first installment bill
        LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(dueDate);
        LocalDateTime billDueDate = getTimePoints().getBillDueDate(dueDate);
        TimeSetterUtil.getInstance().nextPhase(billGenDate);
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        TimeSetterUtil.getInstance().nextPhase(billDueDate);

        // Pay installment amount and decline payment
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        payMinAmountDue();
        SearchPage.openBilling(policyNumber);
        BillingHelper.declinePayment(billDueDate);

        // TODO Validate FEE-07

        // Perform Endorsement effective today+2days and RP OOS Endorsement effective today+1day
        SearchPage.openPolicy(policyNumber);
        performNonPremBearingEndorsement(policyNumber, dueDate.plusDays(2));
        performRPEndorsement(policyNumber, dueDate.plusDays(1));
        policy.rollOn().perform(false, true);

        // TODO Validate END-07

        // Roll back endorsement
        policy.rollBackEndorsement().perform(getPolicyTD("EndorsementRollBack", "TestData"));

        // TODO Validate END-05

        // Move to renewal offer time point and create renewal image
        moveTimeAndRunRenewJobs(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        policy.getDefaultView().fill(getRenewalFillTd());
        Dollar renewalAmt = payTotalAmountDue();

        // TODO Validate RNW-03

    }

}
