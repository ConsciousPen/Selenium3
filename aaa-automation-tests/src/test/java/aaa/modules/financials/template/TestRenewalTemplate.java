package aaa.modules.financials.template;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import aaa.modules.financials.FinancialsSQL;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestRenewalTemplate extends FinancialsBaseTest {

    /**
     * @scenario
     * 1. Create policy WITHOUT employee benefit with monthly payment plan.
     * 2. Advance time 1 month and pay installment amount (and full amount due)
     * 3. Advance policy through renewal cycle
     * 4. Pay amount due and verify renewal is active
     * 5. Create NPB endorsement with eff. date today + 2 days
     * 6. Create AP endorsement with eff. date today + 1 day (OOS)
     * 7. Roll back endorsement
     * 8. Move time point and bind renewal ON or AFTER the renewal effective date
     * @details PMT-02, TAX-03, TAX-06, RNW-01, END-05, END-07, FEE-10
     */
    protected void testRenewalScenario_1() {

        // Create policy WITHOUT employee benefit with monthly payment plan
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
        LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime dueDate = PolicySummaryPage.getEffectiveDate().plusMonths(1);

        Map<String, Dollar> taxesNB = new HashMap<>();
        if (getState().equals(Constants.States.WV) || getState().equals(Constants.States.KY)) {
            taxesNB = getTaxAmountsForPolicy(policyNumber);
        }

        // Advance time 1 month, generate and pay first installment bill
        LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(dueDate);
        LocalDateTime billDueDate = getTimePoints().getBillDueDate(dueDate);
        TimeSetterUtil.getInstance().nextPhase(billGenDate);
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
        TimeSetterUtil.getInstance().nextPhase(billDueDate);

        mainApp().open();
        SearchPage.openBilling(policyNumber);
        Dollar nonEftFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);
        Dollar installmentAmt = payMinAmountDue(METHOD_CASH);

        // Used to validate TAX-03 and TAX-06 below
        Dollar installmentTaxes = new Dollar();
        if (getState().equals(Constants.States.KY)) {
            installmentTaxes = FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.STATE_TAX_KY, "1053")
                    .add(FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.CITY_TAX_KY, "1053"))
                    .add(FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.COUNTY_TAX_KY, "1053"));
        } else if (getState().equals(Constants.States.WV)) {
            installmentTaxes = FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.STATE_TAX_WV, "1053");
        }
        Dollar netPayment = installmentAmt.subtract(nonEftFee).subtract(installmentTaxes);

        assertSoftly(softly -> {
            // FEE-10 validations
            softly.assertThat(nonEftFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(billGenDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));
            softly.assertThat(nonEftFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(billGenDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1040"));

            // PMT-02 validations
            softly.assertThat(installmentAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MANUAL_PAYMENT, "1001"));
            softly.assertThat(nonEftFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));

            // TAX-03 (KY), TAX-06 (WV) validations (validates using netPayment from above)
            softly.assertThat(netPayment).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MANUAL_PAYMENT, "1044"));
        });

        // Perform Endorsement effective today+6days and AP OOS Endorsement effective today+5day
        SearchPage.openPolicy(policyNumber);
        performNonPremBearingEndorsement(policyNumber, dueDate.plusDays(6));
        performAPEndorsement(policyNumber, dueDate.plusDays(5));
        policy.rollOn().perform(false, true);
        Dollar addedPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT, dueDate.plusDays(5));

        Dollar taxes = new Dollar(0.00);
        if (getState().equals(Constants.States.WV) || getState().equals(Constants.States.KY)) {
            taxes = getTaxAmountsForPolicy(policyNumber).get(TOTAL).subtract(taxesNB.get(TOTAL));
        }

        // END-07 Validations
        Dollar totalTaxesEnd = taxes;
        assertSoftly(softly -> {
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
        });

        // END-07 validations for taxes (WV/KY only)
        if (getState().equals(Constants.States.WV) || getState().equals(Constants.States.KY)) {
            assertSoftly(softly -> {
                softly.assertThat(totalTaxesEnd).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                        .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")));
                softly.assertThat(totalTaxesEnd).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")
                        .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")));
            });
        }

        // Roll back endorsement and pay total amount due
        Dollar rollBackAmount = rollBackEndorsement(policyNumber);
        payTotalAmountDue();

        // END-05 Validations
        assertSoftly(softly -> {
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1015")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1021")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1022")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1044"));
        });

        // Move to renewal time point and propose renewal image
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        payMinAmountDue(METHOD_CASH);
        Dollar renewalPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);

        SearchPage.openPolicy(policyNumber);
        if (PolicySummaryPage.buttonRenewals.isEnabled()) {
            PolicySummaryPage.buttonRenewals.click();
            assertThat(renewalEffDate).isEqualToIgnoringHours(PolicySummaryPage.getEffectiveDate());
        }

        taxes = new Dollar(0.00);
        if (getState().equals(Constants.States.WV) || getState().equals(Constants.States.KY)) {
            taxes = getTaxAmountsForPolicy(policyNumber).get(TOTAL);
        }
        Dollar totalTaxesRenewal = taxes;

        // Validations for RNW-01
        assertSoftly(softly -> {
            softly.assertThat(renewalPrem.subtract(totalTaxesRenewal)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1044"));
            softly.assertThat(renewalPrem.subtract(totalTaxesRenewal)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1015")));
            softly.assertThat(renewalPrem.subtract(totalTaxesRenewal)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1021")));
            softly.assertThat(renewalPrem.subtract(totalTaxesRenewal)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1022")));
        });

        // Tax Validations for RNW-01
        if (getState().equals(Constants.States.WV) || getState().equals(Constants.States.KY)) {
            assertSoftly(softly -> {
                softly.assertThat(totalTaxesRenewal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1053"));
                softly.assertThat(totalTaxesRenewal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1054"));
            });
        }

    }

    /**
     * @scenario
     * 1. Create policy WITHOUT employee benefit with monthly payment plan.
     * 2. Advance time 1 month and pay installment amount (and full amount due)
     * 3. Decline payment for reason = NSF
     * 4. Create NPB endorsement with eff. date today + 2 days
     * 5. Create RP endorsement with eff. date today + 1 day (OOS)
     * 6. Roll back endorsement
     * 7. Move time point and bind renewal BEFORE the renewal effective date
     * @details PMT-03, FEE-07, FEE-08, FEE-09, FEE-17, FEE-18, RNW-03, END-05, END-07
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

        // Pay installment amount by check, decline payment with fees + no restriction, waive NSF & installment fees
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        Dollar installmentAmt = payMinAmountDue(METHOD_CHECK);
        Dollar nonEftFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);
        BillingHelper.declinePayment(billDueDate);
        Dollar nsfFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NSF_FEE__WITHOUT_RESTRICTION);
        waiveFeeByDateAndType(billDueDate, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NSF_FEE__WITHOUT_RESTRICTION);
        waiveFeeByDateAndType(billGenDate, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);

        assertSoftly(softly -> {
            // PMT-03 validations
            softly.assertThat(installmentAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.PAYMENT_DECLINED, "1001"));
            softly.assertThat(installmentAmt.subtract(nonEftFee)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.PAYMENT_DECLINED, "1044"));
            softly.assertThat(nonEftFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));

            // FEE-07 validations
            softly.assertThat(nsfFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NSF_FEE, "1034"));
            softly.assertThat(nsfFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NSF_FEE, "1040"));

            // FEE-08 validations, (for credits, validating there are now 2 nsf fee values since entry is identical to PMT-02 entry)
            softly.assertThat(nonEftFee.multiply(2)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));
            softly.assertThat(nonEftFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1040"));

            // FEE-09 validations
            softly.assertThat(nsfFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NSF_FEE_WAIVED, "1034"));
            softly.assertThat(nsfFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NSF_FEE_WAIVED, "1040"));
        });

        // Perform Endorsement effective today+6days and RP OOS Endorsement effective today+5day (can't use performRPEndorsement method here)
        SearchPage.openPolicy(policyNumber);
        performNonPremBearingEndorsement(policyNumber, dueDate.plusDays(6));
        policy.endorse().perform(getEndorsementTD(dueDate.plusDays(5)));
        policy.getDefaultView().fill(getReducePremiumTD());
        policy.rollOn().perform(false, true);
        Dollar reducedPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT);

        // TODO Validate END-07

        // Roll back endorsement
        Dollar rollBackAmount = rollBackEndorsement(policyNumber);

        // TODO Validate END-05

        // Move to renewal offer time point and create renewal image
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        Dollar renewalAmt = payTotalAmountDue();

        // TODO Validate RNW-03

    }

    /**
     * @scenario
     * 1. Create policy WITH employee benefit with monthly payment plan.
     * 2. Advance time 1 month and pay installment amount (and full amount due)
     * 3. Advance policy through renewal cycle
     * 4. Pay amount due and verify renewal is active
     * 5. Create NPB endorsement with eff. date today + 2 days
     * 6. Create AP endorsement with eff. date today + 1 day (OOS)
     * 7. Roll back endorsement
     * 8. Move time point and bind renewal ON or AFTER the renewal effective date
     * @details RNW-02, END-06, END-08
     */
    protected void testRenewalScenario_3() {

        // Create policy WITH employee benefit
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdWithEmpBenefit(getPolicyTD()));
        LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();
        LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();

        // Perform Endorsement effective today+6days and AP OOS Endorsement effective today+5day
        performNonPremBearingEndorsement(policyNumber, effDate.plusDays(6));
        performAPEndorsement(policyNumber, effDate.plusDays(5));
        policy.rollOn().perform(false, true);
        Dollar addedPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT, effDate.plusDays(5));

        // TODO END-08 validations

        // Roll back endorsement and pay total amount due
        Dollar rollBackAmount = rollBackEndorsement(policyNumber);
        payTotalAmountDue();

        // TODO END-06 Validations

        // Move to renewal effective date and propose renewal image
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        payMinAmountDue(METHOD_CASH);
        Dollar renewalPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);

        // TODO RNW-02 Validations

    }

    /**
     * @scenario
     * 1. Create policy WITH employee benefit with monthly payment plan.
     * 2. Advance time 1 month and pay installment amount (and full amount due)
     * 3. Advance policy through renewal cycle
     * 4. Pay amount due and verify renewal is active
     * 5. Create NPB endorsement with eff. date today + 2 days
     * 6. Create RP endorsement with eff. date today + 1 day (OOS)
     * 7. Roll back endorsement
     * 8. Move time point and bind renewal BEFORE the renewal effective date
     * @details RNW-04, END-06, END-08
     */
    protected void testRenewalScenario_4() {

        // Create policy WITH employee benefit
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdWithEmpBenefit(getPolicyTD()));
        LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();
        LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();

        // Perform Endorsement effective today+6days and RP OOS Endorsement effective today+5day (can't use performRPEndorsement method here)
        SearchPage.openPolicy(policyNumber);
        performNonPremBearingEndorsement(policyNumber, effDate.plusDays(6));
        policy.endorse().perform(getEndorsementTD(effDate.plusDays(5)));
        policy.getDefaultView().fill(getReducePremiumTD());
        policy.rollOn().perform(false, true);
        Dollar reducedPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT);

        // TODO END-08 validations

        // Roll back endorsement
        Dollar rollBackAmount = rollBackEndorsement(policyNumber);

        // TODO END-06 Validations

        // Move to renewal offer time point and create renewal image
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        Dollar renewalAmt = payTotalAmountDue();

        // TODO RNW-04 Validations

    }

}
