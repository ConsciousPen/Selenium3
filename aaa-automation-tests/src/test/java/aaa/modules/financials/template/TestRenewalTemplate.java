package aaa.modules.financials.template;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import aaa.modules.financials.FinancialsSQL;
import aaa.toolkit.webdriver.customcontrols.AdvancedAllocationsRepeatAssetList;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.TextBox;

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
        if (isTaxState()) {
            taxesNB = getTaxAmountsForPolicy(policyNumber);
        }

        // Advance time 1 month, generate and pay first installment bill
        LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(dueDate);
        LocalDateTime billDueDate = getTimePoints().getBillDueDate(dueDate);
        TimeSetterUtil.getInstance().nextPhase(billGenDate);
        JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
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
        if (isTaxState()) {
            taxes = getTaxAmountsForPolicy(policyNumber).get(TOTAL).subtract(taxesNB.get(TOTAL));
        }

        // END-07 Validations
        Dollar totalTaxesEnd = taxes;
        validateAPEndorsementTx(policyNumber, addedPrem, totalTaxesEnd);

        // Roll back endorsement and pay total amount due
        Dollar rollBackAmount = rollBackEndorsement(policyNumber);
        payTotalAmountDue();

        // END-05 Validations
        validateAPRollBack(policyNumber, rollBackAmount, totalTaxesEnd);

        // Move to renewal time point and propose renewal image
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        payMinAmountDue(METHOD_CASH);
        Dollar renewalPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);
        openPolicyRenewal(policyNumber);

        taxes = new Dollar(0.00);
        if (isTaxState()) {
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
        if (isTaxState()) {
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

        Map<String, Dollar> taxesNB = new HashMap<>();
        if (isTaxState()) {
            taxesNB = getTaxAmountsForPolicy(policyNumber);
        }

        // Advance time 1 month, generate first installment bill
        LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(dueDate);
        LocalDateTime billDueDate = getTimePoints().getBillDueDate(dueDate);
        TimeSetterUtil.getInstance().nextPhase(billGenDate);
        JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
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
            // PMT-03 validations
            softly.assertThat(installmentAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.PAYMENT_DECLINED, "1001"));
            softly.assertThat(netPayment).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.PAYMENT_DECLINED, "1044"));
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

        Dollar taxes = new Dollar(0.00);
        if (isTaxState()) {
            taxes = taxesNB.get(TOTAL).subtract(getTaxAmountsForPolicy(policyNumber).get(TOTAL));
        }

        //Validate END-07
        Dollar reducedPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT);
        Dollar totalTaxesEnd = taxes;
        validateRPEndorsementTx(policyNumber, reducedPrem, totalTaxesEnd);

        // Roll back endorsement
        Dollar rollBackAmount = rollBackEndorsement(policyNumber);
        validateRPRollBack(policyNumber, rollBackAmount, totalTaxesEnd);

        // Pay off remaining balance on policy
        payTotalAmountDue();

        // Move to renewal offer time point and create renewal image
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        Dollar renewalPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);
        payTotalAmountDue();

        //Capture the credit towards the account after paying total amount due (Not applicable to CA Products and PUP). Subtract this from the renewalAmt.
        Dollar renewCredit = new Dollar(0.00);
        if (!getPolicyType().isCaProduct() && !getPolicyType().equals(PolicyType.PUP)) {
            renewCredit = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);
        }

        openPolicyRenewal(policyNumber);

        taxes = new Dollar(0.00);
        if (getState().equals(Constants.States.WV) || getState().equals(Constants.States.KY)) {
            taxes = getTaxAmountsForPolicy(policyNumber).get(TOTAL);
        }
        Dollar renewalTermTaxes = taxes;
        Dollar renewalOffsetAmt = renewCredit.add(taxes);

        // Validate RNW-03
        validateRenewalBoundBeforeEffDateAtTxDate(policyNumber, renewalPrem.subtract(renewalOffsetAmt), renewalTermTaxes);

        //Advance time to policy effective date and run ledgerStatusUpdateJob to update the ledger
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        JobUtils.executeJob(BatchJob.ledgerStatusUpdateJob);

        // RNW-03 Validations recorded at effective date
        validateRenewalBoundBeforeEffDateAtEffDate(policyNumber, renewalPrem.subtract(renewalOffsetAmt), renewalTermTaxes);

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

        // Validate END-08
        validateAPEndorsementTx(policyNumber, addedPrem, new Dollar(0.00));

        // Roll back endorsement and pay total amount due
        Dollar rollBackAmount = rollBackEndorsement(policyNumber);

        // Validate END-06
        validateAPRollBack(policyNumber, rollBackAmount, new Dollar(0.00));

        // Move to renewal effective date and propose renewal image
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        payMinAmountDue(METHOD_CASH);
        Dollar renewalPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);

        // Validate RNW-02
        validateRenewalBoundOnEffDate(policyNumber, renewalPrem, new Dollar(0.00));

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

        // Validate END-08
        validateRPEndorsementTx(policyNumber, reducedPrem, new Dollar(0.00));

        // Roll back endorsement
        Dollar rollBackAmount = rollBackEndorsement(policyNumber);

        // Validate END-06
        validateRPRollBack(policyNumber, rollBackAmount, new Dollar(0.00));

        // Move to renewal offer time point and create renewal image
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());
        Dollar renewalPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);
        payTotalAmountDue();
        openPolicyRenewal(policyNumber);

        // Validate RNW-04 recorded at transaction date
        validateRenewalBoundBeforeEffDateAtTxDate(policyNumber, renewalPrem, new Dollar(0.00));

        //Advance time to policy effective date and run ledgerStatusUpdateJob to update the ledger
        TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        JobUtils.executeJob(BatchJob.ledgerStatusUpdateJob);

        // RNW-04 validations recorded at effective date
        validateRenewalBoundBeforeEffDateAtEffDate(policyNumber, renewalPrem, new Dollar(0.00));

    }

    /**
     * @scenario
     * 1.  Create policy WITHOUT employee benefit with monthly payment plan
     * 2.  Cancel policy for reason = non-payment
     * 3.  Advance time 1 month
     * 4.  Reinstate policy w/ lapse
     * 5.  Validate reinstatement fee entries
     * 6.  Waive reinstatement fee
     * 7.  Validate entries for waiver of reinstatement fee
     * 8.  Advance time to renewal offer generation date
     * 9.  Create renewal image
     * 10. Add lapse to renewal offer
     * 11. Waive renewal offer lapse fee
     * 12. Validate ledger entries
     * @details FEE-11, FEE-12, FEE-13, FEE-14
     */
    protected void testRenewalScenario_5() {

        // Create policy WITHOUT employee benefit, monthly payment plan
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
        LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();
        LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();

        // cancel policy for non-payment of premium
        policy.cancel().perform(getCancellationNonPaymentTd(effDate));

        // Advance time one month, reinstate policy with lapse, waive reinstatement fee
        TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1));
        mainApp().open();
        SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
        performReinstatement(policyNumber);

        // Capture reinstatement fee and waive
        SearchPage.openBilling(policyNumber);
        Dollar reinstatementFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_FEE);
        waiveFeeByDateAndType(effDate.plusMonths(1), BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_FEE);

        // FEE-11 & FEE-12 validations
        assertSoftly(softly -> {
            // FEE-12
            softly.assertThat(reinstatementFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT_FEE, "1034"));
            softly.assertThat(reinstatementFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT_FEE, "1040"));
            // FEE-11
            softly.assertThat(reinstatementFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT_FEE, "1034"));
            softly.assertThat(reinstatementFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT_FEE, "1040"));
        });

        // Advance time to renewal time point and create renewal image
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());

        // Add lapse for renewal term
        PolicySummaryPage.buttonRenewals.click();
        policy.manualRenewalWithOrWithoutLapse().perform(getChangeRenewalLapseTd(renewalEffDate.plusMonths(1)));

        // Capture renewal offer with lapse fee and waive
        Dollar renewalLapseFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_FEE_RENEWAL);
        waiveFeeByDateAndType(getTimePoints().getRenewOfferGenerationDate(renewalEffDate), BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_FEE_RENEWAL);

        // FEE-13 & FEE-14 validations
        assertSoftly(softly -> {
            // FEE-13
            softly.assertThat(renewalLapseFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL_LAPSE_FEE, "1034"));
            softly.assertThat(renewalLapseFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL_LAPSE_FEE, "1040"));
            // FEE-14
            softly.assertThat(renewalLapseFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL_LAPSE_FEE, "1034"));
            softly.assertThat(renewalLapseFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL_LAPSE_FEE, "1040"));
        });

    }

    /**
     * @scenario
     * 1.  Create policy WITHOUT employee benefit with monthly payment plan
	 * 2.  Cancel policy for reason = non-payment
	 * 3.  Advance time 1 month
	 * 4.  Reinstate policy w/ lapse
	 * 5.  Validate reinstatement
     * 6.  Advance time to renewal offer generation date
     * 7.  Create renewal image
     * 8.  Add lapse to renewal offer
     * 9.  Validate ledger entries
     * @details RNW-05, RST-05
     */
    protected void testRenewalScenario_6() {
        // Create policy WITHOUT employee benefit, monthly payment plan
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
		LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();

		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_ACTIVE);
		// cancel policy for non-payment of premium
		LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();
		policy.cancel().perform(getCancellationNonPaymentTd(effDate));

        // Advance time one month, reinstate policy with lapse, waive reinstatement fee
        TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1));
        mainApp().open();
        SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
        performReinstatement(policyNumber);

		Dollar premiumAfterReinstatement = PolicySummaryPage.TransactionHistory.getEndingPremium();
		// taxes only applies to WV and KY and value needs added to premium amount for correct validation below
		Dollar totalTaxesNB = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053");

		//RST-05 validation
		validateReinstatement(premiumAfterReinstatement, policyNumber, totalTaxesNB);

        // Advance time to renewal time point and create renewal image
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.renew().performAndFill(getRenewalFillTd());

        // Add lapse for renewal term
        PolicySummaryPage.buttonRenewals.click();
        policy.manualRenewalWithOrWithoutLapse().perform(getChangeRenewalLapseTd(renewalEffDate.plusMonths(1)));

		TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
        JobUtils.executeJob(BatchJob.ledgerStatusUpdateJob);
        JobUtils.executeJob(BatchJob.policyStatusUpdateJob);

        mainApp().open();
        SearchPage.openPolicy(policyNumber);

		Dollar renewalPrem = PolicySummaryPage.TransactionHistory.getEndingPremium();
		// taxes only applies to WV and KY and value needs added to premium amount for correct validation below
		Dollar totalTaxesRenewal = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1053");

		//RNW-05 validation
		validateRenewalBoundOnEffDate(policyNumber, renewalPrem, totalTaxesRenewal);
    }

	/**
	 * @scenario
	 * 1.  Create policy WITHOUT employee benefit with monthly payment plan
	 * 2.  Cancel policy for reason = non-payment
	 * 3.  Advance time 1 month
	 * 4.  Reinstate policy w/ lapse
	 * 5.  Validate reinstatement
	 * 6.  Advance time to renewal offer generation date
	 * 7.  Create renewal image
	 * 8.  Add lapse to renewal offer
	 * 9.  Validate ledger entries
	 * @details RNW-06, RST-06
	 */
	protected void testRenewalScenario_7() {
		// Create policy WITH employee benefit
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy(adjustTdWithEmpBenefit(getPolicyTD()));
		LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();

		// cancel policy for non-payment of premium
		policy.cancel().perform(getCancellationNonPaymentTd(effDate));

		// Advance time one month, reinstate policy with lapse, waive reinstatement fee
		TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1));
		mainApp().open();
		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		performReinstatement(policyNumber);

		Dollar premiumAfterReinstatement = PolicySummaryPage.TransactionHistory.getEndingPremium();
		// taxes only applies to WV and KY and value needs added to premium amount for correct validation below
		Dollar totalTaxesNB = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053");

		//RST-06 validation
		validateReinstatement(premiumAfterReinstatement, policyNumber, totalTaxesNB);

		// Advance time to renewal time point and create renewal image
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.renew().performAndFill(getRenewalFillTd());

		// Add lapse for renewal term
		PolicySummaryPage.buttonRenewals.click();
		policy.manualRenewalWithOrWithoutLapse().perform(getChangeRenewalLapseTd(renewalEffDate.plusMonths(1)));

		TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
        JobUtils.executeJob(BatchJob.ledgerStatusUpdateJob);

		Dollar renewalPrem = PolicySummaryPage.TransactionHistory.getEndingPremium();
		// taxes only applies to WV and KY and value needs added to premium amount for correct validation below
		Dollar totalTaxesRenewal = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1053");

		//RNW-06 validation
		validateRenewalBoundOnEffDate(policyNumber, renewalPrem, totalTaxesRenewal);
	}

    /**
     * @scenario
     * 1. Create policy with monthly payment plan
	 * 2. Move time DD1-20 to generate 1st installment Bill
	 * 3. Accept payment: TotalDue - Fee amount
	 * 4. Move time: DD1 + 1day and run aaaRefundGenerationAsyncJob
	 * 5. Waive Fee and Reallocation transactions should be created
	 * 6. Validate ledger entries: Waived Fee
	 * 7. Create Renewal
	 * 8. Move time DD1-20 to generate 1st installment Bill
	 * 9. Accept payment: TotalDue - more that fee amount (example: 10)
	 * 10. Move time: DD1 + 1day and run aaaRefundGenerationAsyncJob
	 * 11. Small Balance write Off transaction should be created
	 * 12. Validate ledger entries
	 * 13. Accept payment
	 * 14. Transfer payment and allocate amounts
	 * 15. Validate ledger transactions
	 * @details ADJ-11, ADJ-13, PMT-13
     */
    protected void testRenewalScenario_8() {
        // Create policy WITHOUT employee benefit, monthly payment plan
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
		LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();
        SearchPage.openBilling(policyNumber);

        //ADJ-11
        /// Advance time 1 month, generate and pay first installment bill
        List<LocalDateTime> installmentDueDates = BillingHelper.getInstallmentDueDates();
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

        Dollar feeNB = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);
        Dollar totalPayment = BillingSummaryPage.getTotalDue().subtract(feeNB);

        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalPayment);
        TimeSetterUtil.getInstance().nextPhase(installmentDueDates.get(1).plusDays(1));
		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);

		String accountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
		List<String> transactionIds = FinancialsSQL.getTransactionIdsForAccount(accountNumber);
		int index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE_WAIVED);
		Dollar feeWaiveAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE_WAIVED);
		String feeWaivedTransactionId = transactionIds.get(index);
		//Adjustment - Reallocated Payment
		int indexAdjReallocation = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT);
		Dollar reallocationAdjAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT);
		String reallocationAdjTransactionId = transactionIds.get(indexAdjReallocation);
		//	Payment	- Reallocate Payment
		int indexPaymentReallocation = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT);
		Dollar reallocationPaymentAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT);
		String reallocationPaymentTransactionId = transactionIds.get(indexPaymentReallocation);
		Map<String, Dollar> adjustmentReallocation= getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT, TimeSetterUtil.getInstance().getCurrentTime());
		Map<String, Dollar> paymentReallocation = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT, TimeSetterUtil.getInstance().getCurrentTime());

		 //ADJ-11 validation
        assertSoftly(softly -> {
            softly.assertThat(feeWaiveAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(feeWaivedTransactionId, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));
            softly.assertThat(feeWaiveAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(feeWaivedTransactionId, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1040"));
            //Reallocation validation
			softly.assertThat(reallocationAdjAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(reallocationAdjTransactionId, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1001"));
			softly.assertThat(adjustmentReallocation.get("Net Premium")).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(reallocationAdjTransactionId, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1044"));
			softly.assertThat(adjustmentReallocation.get("Fees")).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(reallocationAdjTransactionId, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1034"));
			softly.assertThat(reallocationPaymentAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(reallocationPaymentTransactionId, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1001"));
			softly.assertThat(paymentReallocation.get("Net Premium")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(reallocationPaymentTransactionId, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1044"));
        });

		//ADJ-13
        // Move to renewal offer time point and create renewal
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
		policy.renew().performAndFill(getRenewalFillTd());
		TimeSetterUtil.getInstance().nextPhase(renewalEffDate);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar minDue = BillingSummaryPage.getMinimumDue();
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		//Advance time, generate first installment bill for Renewal
		List<LocalDateTime> installmentDueDatesRenewal = BillingHelper.getInstallmentDueDates();
		LocalDateTime billGenDateRenewal = getTimePoints().getBillGenerationDate(installmentDueDatesRenewal.get(1));
		TimeSetterUtil.getInstance().nextPhase(billGenDateRenewal);
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		Dollar renewalTotalPayment = BillingSummaryPage.getTotalDue().subtract(new Dollar(10));

		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), renewalTotalPayment);
		TimeSetterUtil.getInstance().nextPhase(installmentDueDatesRenewal.get(1).plusDays(1));
		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		transactionIds = FinancialsSQL.getTransactionIdsForAccount(accountNumber);
		index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SMALL_BALANCE_WRITE_OFF);
		String smallBalanceWOTransactionId = transactionIds.get(index);
		Dollar smallBalanceWOAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SMALL_BALANCE_WRITE_OFF);

		//ADJ-13 validation
		assertSoftly(softly -> {
			softly.assertThat(smallBalanceWOAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(smallBalanceWOTransactionId, FinancialsSQL.TxType.SMALL_BALANCE_WRITEOFF, "1041"));
			softly.assertThat(smallBalanceWOAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(smallBalanceWOTransactionId, FinancialsSQL.TxType.SMALL_BALANCE_WRITEOFF, "1044"));
		});

		Dollar transferPayment = new Dollar(100);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), transferPayment);
		new BillingAccount().transferPayment().performWithoutSubmit(testDataManager.billingAccount.getTestData("TransferPayment", "TestData"), policyNumber, Arrays.asList("40","60"));

		//PMT-13
		AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
		advancedAllocationsActionTab.linkAdvancedAllocation.click();

		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
				.getAsset(AdvancedAllocationsRepeatAssetList.NET_PREMIUM, TextBox.class, 0).setValue("30");
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
				.getAsset(AdvancedAllocationsRepeatAssetList.POLICY_FEE, TextBox.class, 0).setValue("10");
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
				.getAsset(AdvancedAllocationsRepeatAssetList.NET_PREMIUM, TextBox.class, 1).setValue("40");
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
				.getAsset(AdvancedAllocationsRepeatAssetList.POLICY_FEE, TextBox.class, 1).setValue("20");
		advancedAllocationsActionTab.buttonOk.click();

		transactionIds = FinancialsSQL.getTransactionIdsForAccount(accountNumber);
		index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_TRANSFERRED);
		Dollar paymentTransferAdjAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_TRANSFERRED);
		String paymentTransferAdjId = transactionIds.get(index);
		index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT);
		Dollar manualTransferAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT);
		String manualTransferId = transactionIds.get(index);

		Map<String, Dollar> paymentTransferAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_TRANSFERRED, TimeSetterUtil.getInstance().getCurrentTime());
		Map<String, Dollar> manualPaymentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT, TimeSetterUtil.getInstance().getCurrentTime());

		//PMT-13 validation
		assertSoftly(softly -> {
			softly.assertThat(paymentTransferAdjAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(paymentTransferAdjId, FinancialsSQL.TxType.ACCOUNT_MONEY_TRANSFER, "1001"));
			softly.assertThat(paymentTransferAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(paymentTransferAdjId, FinancialsSQL.TxType.ACCOUNT_MONEY_TRANSFER, "1044"));

			softly.assertThat(manualTransferAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(manualTransferId, FinancialsSQL.TxType.MANUAL_PAYMENT, "1001"));
			softly.assertThat(manualPaymentAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(manualTransferId, FinancialsSQL.TxType.MANUAL_PAYMENT, "1044"));
			softly.assertThat(manualPaymentAllocations.get("Fees")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(manualTransferId, FinancialsSQL.TxType.POLICY_FEE, "1034"));

		});
    }

	/**
	 * @scenario
	 * 1. Create policy
	 * 2. Create renewal proposal
	 * 3. Perform RP Endorsement
	 * 4. Validate ledger entries: Cross Policy Transfer
	 * @details CPT-01
	 */
	protected void testRenewalScenario_9() {
		// Create policy
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy(getPolicyTD());
		LocalDateTime policyEffDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();

		// Move to renewal offer time point and create renewal
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.renew().performAndFill(getRenewalFillTd());
		if (!getState().equals(Constants.States.CA)) {
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalEffDate));
			JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		}
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		performRPEndorsement(policyNumber, getTimePoints().getBillGenerationDate(renewalEffDate));
		SearchPage.openBilling(policyNumber);

		String accountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
		List<String> transactionIds = FinancialsSQL.getTransactionIdsForAccount(accountNumber);
		int index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CROSS_POLICY_TRANSFER);
		Dollar crossPolicyTransferAdjAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CROSS_POLICY_TRANSFER);
		String crossPolicyTransferAdjId = transactionIds.get(index);
		index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CROSS_POLICY_TRANSFER);
		Dollar crossPolicyTransferPaymentAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CROSS_POLICY_TRANSFER);
		String crossPolicyTransferPaymentId = transactionIds.get(index);
		Map<String, Dollar> adjustmentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CROSS_POLICY_TRANSFER, TimeSetterUtil.getInstance().getCurrentTime());
		Map<String, Dollar> paymentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CROSS_POLICY_TRANSFER, TimeSetterUtil.getInstance().getCurrentTime());

		//CPT-01 validation
		assertSoftly(softly -> {
			softly.assertThat(crossPolicyTransferAdjAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CROSS_POLICY_TRANSFER, "1001"));
			softly.assertThat(adjustmentAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CROSS_POLICY_TRANSFER, "1044"));

			softly.assertThat(crossPolicyTransferPaymentAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CROSS_POLICY_TRANSFER, "1001"));
			softly.assertThat(paymentAllocations.get("Net Premium" + policyEffDate.format(DateTimeUtils.MM_DD_YYYY))).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CROSS_POLICY_TRANSFER, "1044"));
			softly.assertThat(paymentAllocations.get("Net Premium" + renewalEffDate.format(DateTimeUtils.MM_DD_YYYY))).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CROSS_POLICY_TRANSFER, "1065"));
		});
		// Tax Validations for CPT-01
		if (isTaxState()) {
			assertSoftly(softly -> {
				softly.assertThat(adjustmentAllocations.get("Taxes")).isEqualTo(getTotalTaxesFromDb(crossPolicyTransferAdjId, "1053", false));

				softly.assertThat(paymentAllocations.get("Taxes" + policyEffDate.format(DateTimeUtils.MM_DD_YYYY))).isEqualTo(getTotalTaxesFromDb(crossPolicyTransferPaymentId, "1053", true));
				softly.assertThat(paymentAllocations.get("Taxes" + renewalEffDate.format(DateTimeUtils.MM_DD_YYYY))).isEqualTo(getTotalTaxesFromDb(crossPolicyTransferPaymentId, "1071", true));
			});
		}
		// Fee Validations for CPT-01
		if (getState().equals(Constants.States.NJ)) {
			assertSoftly(softly -> {
				softly.assertThat(adjustmentAllocations.get("Fees")).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(crossPolicyTransferAdjId, FinancialsSQL.TxType.PLIGA_FEE, "1034"));
				softly.assertThat(paymentAllocations.get("Fees")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(crossPolicyTransferPaymentId, FinancialsSQL.TxType.PLIGA_FEE, "1034"));
			});
		}
	}

	/**
	 * @scenario
	 * 1. Create policy with monthly payment plan
	 * 2. Create renewal, pay min due
	 * 3. Accept another payment
	 * 4. Transfer and allocate payment
	 * 5. Validate ledger entries
	 * @details CPT-02 - Future dated payment
	 */
	protected void testRenewalScenario_10() {
		// Create policy with monthly payment plan
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
		LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();

		// Move to renewal offer time point and create renewal
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalEffDate));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.renew().performAndFill(getRenewalFillTd());
		if (!getState().equals(Constants.States.CA)) {
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalEffDate));
			JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		}
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar minDue = BillingSummaryPage.getMinimumDue();
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		Dollar transferPayment = new Dollar(100);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), transferPayment);
		new BillingAccount().transferPayment().performWithoutSubmit(testDataManager.billingAccount.getTestData("TransferPayment", "TestData"), policyNumber, Arrays.asList("0","100"));
		AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
		advancedAllocationsActionTab.linkAdvancedAllocation.click();

		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
				.getAsset(AdvancedAllocationsRepeatAssetList.POLICY_FEE, TextBox.class, 1).setValue("20");
		if (getState().equals(Constants.States.KY)) {
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
					.getAsset(AdvancedAllocationsRepeatAssetList.NET_PREMIUM, TextBox.class, 1).setValue("65");
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
					.getAsset(AdvancedAllocationsRepeatAssetList.PRMS_KY, TextBox.class, 1).setValue("5");
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
					.getAsset(AdvancedAllocationsRepeatAssetList.PREMT_COUNTY, TextBox.class, 1).setValue("5");
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
					.getAsset(AdvancedAllocationsRepeatAssetList.PREMT_CITY, TextBox.class, 1).setValue("5");
		} else if (getState().equals(Constants.States.WV)) {
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
					.getAsset(AdvancedAllocationsRepeatAssetList.NET_PREMIUM, TextBox.class, 1).setValue("65");
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
					.getAsset(AdvancedAllocationsRepeatAssetList.PRMS_WV, TextBox.class, 1).setValue("15");
		} else {
			advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
					.getAsset(AdvancedAllocationsRepeatAssetList.NET_PREMIUM, TextBox.class, 1).setValue("80");
		}

		advancedAllocationsActionTab.buttonOk.click();

		String accountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
		List<String> transactionIds = FinancialsSQL.getTransactionIdsForAccount(accountNumber);
		int index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_TRANSFERRED);
		Dollar paymentTransferAdjAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_TRANSFERRED);
		String paymentTransferAdjId = transactionIds.get(index);
		index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT);
		Dollar manualTransferAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT);
		String manualTransferId = transactionIds.get(index);

		Map<String, Dollar> paymentTransferAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_TRANSFERRED, TimeSetterUtil.getInstance().getCurrentTime());
		Map<String, Dollar> manualPaymentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT, TimeSetterUtil.getInstance().getCurrentTime());
		//CPT-02 validation
		assertSoftly(softly -> {
			softly.assertThat(paymentTransferAdjAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(paymentTransferAdjId, FinancialsSQL.TxType.ACCOUNT_MONEY_TRANSFER, "1001"));
			softly.assertThat(paymentTransferAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(paymentTransferAdjId, FinancialsSQL.TxType.ACCOUNT_MONEY_TRANSFER, "1065"));

			softly.assertThat(manualTransferAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(manualTransferId, FinancialsSQL.TxType.MANUAL_PAYMENT, "1001"));
			softly.assertThat(manualPaymentAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(manualTransferId, FinancialsSQL.TxType.MANUAL_PAYMENT, "1065"));
			softly.assertThat(manualPaymentAllocations.get("Fees")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(manualTransferId, FinancialsSQL.TxType.POLICY_FEE, "1034"));
		});
		// Tax Validations for CPT-01
		if (isTaxState()) {
			assertSoftly(softly -> {
				softly.assertThat(manualPaymentAllocations.get("Taxes" + renewalEffDate.format(DateTimeUtils.MM_DD_YYYY))).isEqualTo(getTotalTaxesFromDb(manualTransferId, "1071", true));
				softly.assertThat(paymentTransferAllocations.get("Taxes" + renewalEffDate.format(DateTimeUtils.MM_DD_YYYY))).isEqualTo(getTotalTaxesFromDb(paymentTransferAdjId, "1071", false));
			});
		}
	}

	private Dollar getTotalTaxesFromDb(String transactionId, String ledgerAccount, boolean isCredit) {
		Dollar totalTaxes = new Dollar();
		if (isCredit) {
			if (getState().equals(Constants.States.KY)) {
				totalTaxes = FinancialsSQL.getCreditsForAccountByTransaction(transactionId, FinancialsSQL.TxType.STATE_TAX_KY, ledgerAccount)
						.add(FinancialsSQL.getCreditsForAccountByTransaction(transactionId, FinancialsSQL.TxType.CITY_TAX_KY, ledgerAccount))
						.add(FinancialsSQL.getCreditsForAccountByTransaction(transactionId, FinancialsSQL.TxType.COUNTY_TAX_KY, ledgerAccount));
			} else if (getState().equals(Constants.States.WV)) {
				totalTaxes = FinancialsSQL.getCreditsForAccountByTransaction(transactionId, FinancialsSQL.TxType.STATE_TAX_WV, ledgerAccount);
			}
		} else {
			if (getState().equals(Constants.States.KY)) {
				totalTaxes = FinancialsSQL.getDebitsForAccountByTransaction(transactionId, FinancialsSQL.TxType.STATE_TAX_KY, ledgerAccount)
						.add(FinancialsSQL.getDebitsForAccountByTransaction(transactionId, FinancialsSQL.TxType.CITY_TAX_KY, ledgerAccount))
						.add(FinancialsSQL.getDebitsForAccountByTransaction(transactionId, FinancialsSQL.TxType.COUNTY_TAX_KY, ledgerAccount));
			} else if (getState().equals(Constants.States.WV)) {
				totalTaxes = FinancialsSQL.getDebitsForAccountByTransaction(transactionId, FinancialsSQL.TxType.STATE_TAX_WV, ledgerAccount);
			}
		}
		return totalTaxes;
	}

	private void openPolicyRenewal(String policyNumber) {
        SearchPage.openPolicy(policyNumber);
        if (PolicySummaryPage.buttonRenewals.isEnabled()) {
            PolicySummaryPage.buttonRenewals.click();
        }
    }

    private void validateAPEndorsementTx(String policyNumber, Dollar addedPrem, Dollar totalTaxesEnd) {
        // END-07 and END-08 validations
        assertSoftly(softly -> {    // TODO remove the 'as' descriptor once PAS-29024 is fixed
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).as("caused by PAS-29024").isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).as("caused by PAS-29024").isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).as("caused by PAS-29024").isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).as("caused by PAS-29024").isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
        });

        // Validations for taxes (WV/KY only)
        if (isTaxState()) {
            assertSoftly(softly -> {
                softly.assertThat(totalTaxesEnd).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                        .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")));
                softly.assertThat(totalTaxesEnd).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")
                        .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")));
            });
        }
    }

    private void validateRPEndorsementTx(String policyNumber, Dollar reducedPrem, Dollar totalTaxesEnd) {
        // END-07 and END-08 validations
        assertSoftly(softly -> {    // TODO remove the 'as' descriptor once PAS-29024 is fixed
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).as("caused by PAS-29024").isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).as("caused by PAS-29024").isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).as("caused by PAS-29024").isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).as("caused by PAS-29024").isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
        });

        // END-07 validations for taxes (WV/KY only)
        if (isTaxState()) {
            assertSoftly(softly -> {
                softly.assertThat(totalTaxesEnd).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                        .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")));
                softly.assertThat(totalTaxesEnd).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")
                        .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")));
            });
        }
    }

    private void validateAPRollBack(String policyNumber, Dollar rollBackAmount, Dollar totalTaxesEnd) {
        // END-05 and END-06 validations
        assertSoftly(softly -> {
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1015")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1021")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1022")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1044"));
        });
    }

    private void validateRPRollBack(String policyNumber, Dollar rollBackAmount, Dollar totalTaxesEnd) {
        // END-05 and END-06 validations
        assertSoftly(softly -> {
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1015")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1021")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1022")));
            softly.assertThat(rollBackAmount.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ROLL_BACK_ENDORSEMENT, "1044"));
        });
    }

    private void validateRenewalBoundOnEffDate(String policyNumber, Dollar renewalPrem, Dollar totalTaxesRenewal) {
        // Validations for RNW-01 and RNW-02
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
        if (isTaxState()) {
            assertSoftly(softly -> {
                softly.assertThat(totalTaxesRenewal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1053"));
                softly.assertThat(totalTaxesRenewal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1054"));
            });
        }
    }

	private void validateReinstatement(Dollar premAmt, String policyNumber, Dollar taxes) {
		assertSoftly(softly -> {
			softly.assertThat(premAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1044"));
			softly.assertThat(premAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")
					.subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")));
			softly.assertThat(premAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")
					.subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")));
			softly.assertThat(premAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")
					.subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")));
		});

		if (isTaxState()) {
			assertSoftly(softly -> {
				softly.assertThat(taxes).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053")
						.subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053")));
				softly.assertThat(taxes).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1054")
						.subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1054")));
			});
		}
	}

    private void validateRenewalBoundBeforeEffDateAtTxDate(String policyNumber, Dollar renewalPrem, Dollar renewalTermTaxes) {
        // Validate RNW-03 and RNW-04
        assertSoftly(softly -> {
            softly.assertThat(renewalPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1042")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1042")));
            softly.assertThat(renewalPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1043")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1043")));
        });

        // Tax Validations for RNW-03
        if (isTaxState()) {
            assertSoftly(softly -> {
                softly.assertThat(renewalTermTaxes).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1071"));
                softly.assertThat(renewalTermTaxes).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1072"));
            });
        }
    }

    private void validateRenewalBoundBeforeEffDateAtEffDate(String policyNumber, Dollar renewalPrem, Dollar renewalTermTaxes) {
        // RNW-03 and RNW-04 Validations recorded at effective date
        assertSoftly(softly -> {
            softly.assertThat(renewalPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1044")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1044")));
            softly.assertThat(renewalPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1022")));
            softly.assertThat(renewalPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1021")));
            softly.assertThat(renewalPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1015")));
        });

        // Tax Validations for RNW-03 recorded at effective date
        if (isTaxState()) {
            assertSoftly(softly -> {
                softly.assertThat(renewalTermTaxes).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1053"));
                softly.assertThat(renewalTermTaxes).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1054"));
                softly.assertThat(renewalTermTaxes).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1072"));
                softly.assertThat(renewalTermTaxes).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.RENEWAL, "1071"));
            });
        }
    }
}
