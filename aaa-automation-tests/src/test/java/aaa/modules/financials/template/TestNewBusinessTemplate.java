package aaa.modules.financials.template;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import aaa.modules.financials.FinancialsSQL;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

    /**
     * @scenario
     * 1. Create new policy bound on or after effective date WITHOUT employee benefit
     * 2. Validate NB sub-ledger entries
     * 3. Perform endorsement resulting in additional premium
     * 4. Validate Endorsement sub-ledger entries
     * 5. Cancel policy
     * 6. Validate Cancellation sub-ledger entries
     * 7. Reinstate policy with no lapse (reinstatement eff. date same as cancellation date)
     * 8. Validate Reinstatement sub-ledger entries
     * @details NBZ-01, PMT-01, END-01, CNL-01, RST-01, FEE-01
     */
	protected void testNewBusinessScenario_1() {

		// Create policy WITHOUT employee benefit
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy();
		Dollar premTotal = getTotalTermPremium();
		LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();

        // NBZ-01 validations
        assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044"));
        assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")
                .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")));
        assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")));
        assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")));

        // PMT-01 validations
        Dollar paymentAmt = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.DEPOSIT_PAYMENT);
        assertThat(paymentAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1001"));
        assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1044"));

        // FEE-01 validations (CA Choice only)
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            Dollar policyFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY_FEE);
            assertThat(policyFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1034"));
            assertThat(policyFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1034"));
            assertThat(policyFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1040"));
        }
        SearchPage.openPolicy(policyNumber);

        // Perform AP endorsement and pay total amount due
        performAPEndorsement(policyNumber);
        Dollar addedPrem = payTotalAmountDue();
        SearchPage.openPolicy(policyNumber);

        // END-01 validations
        assertThat(addedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
        assertThat(addedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
        assertThat(addedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
        assertThat(addedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));

		// Cancel policy
		cancelPolicy();

		// CNL-01 validations
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber);
        SearchPage.openPolicy(policyNumber);

		// Reinstate policy without lapse
        performReinstatement(policyNumber);

		// RST-01 validations
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber);

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
     * @details NBZ-03, END-02, CNL-03, PMT-04, RST-02, RST-07, RST-09, FEE-04, FEE-15, FEE-18, FEE-19
     */
	protected void testNewBusinessScenario_2() {

		// Create policy WITHOUT employee benefit, effective date three weeks from today
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime effDate = today.plusWeeks(3);
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy(adjustTdPolicyEffDate(getPolicyTD(), effDate));
        Dollar premTotal = getTotalTermPremium();

        // Get Fee amount from billing tab (if any)
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        Dollar totalFees = BillingHelper.getFeesValue(today);

        // NB-03 and PMT-04 validations
        assertThat(premTotal.add(totalFees)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1001"));
        assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1065"));
        assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1042"));
        assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")));

        // FEE-15 validations (CA Select only)
        if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
            Dollar fraudFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CA_FRAUD_ASSESSMENT_FEE);
            assertThat(fraudFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1034"));
            assertThat(fraudFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1034"));
            assertThat(fraudFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1040"));
        }

        // FEE-04 validations (CA HO products only, excluding DP3 and PUP)
        if (getPolicyType().equals(PolicyType.HOME_CA_HO3) || getPolicyType().equals(PolicyType.HOME_CA_DP3) || getPolicyType().equals(PolicyType.HOME_CA_HO6)) {
            Dollar seismicFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SEISMIC_SAFETY_FEE);
            assertThat(seismicFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.SEISMIC_FEE, "1034"));
            assertThat(seismicFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.SEISMIC_FEE, "1040"));
        }

        // Advance time to policy effective date
        advanceTimeAndOpenPolicy(effDate, policyNumber);

        // Perform RP endorsement
        Dollar reducedPrem = performRPEndorsement(effDate, policyNumber);

        // END-02 validations
        assertThat(reducedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
        assertThat(reducedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
        assertThat(reducedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
        assertThat(reducedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));

		// Cancel policy on effective date (flat cancellation)
        cancelPolicy();

        // CNL-03 validations
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber);

        // FEE-19 validations (CA Choice only)
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            Dollar cancellationFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_FEE);
            assertThat(cancellationFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION_FEE, "1034"));
            assertThat(cancellationFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION_FEE, "1040"));
        }

        // FEE-18 validations
        if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
            Dollar fraudFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CA_FRAUD_ASSESSMENT_FEE);
            assertThat(fraudFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1034"));
            assertThat(fraudFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1040"));
        }
        SearchPage.openPolicy(policyNumber);

		// Advance time and reinstate policy with lapse
        performReinstatementWithLapse(effDate, policyNumber);

        // RST-02 validations
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber);

		//TODO need to change the reinstatement lapse RST-07, then remove the lapse RST-09 and validations

	}

    /**
     * @scenario
     * 1. Create new policy bound on or after effective date WITH employee benefit
     * 2. Validate NB sub-ledger entries
     * 3. Perform endorsement resulting in additional premium WITH employee benefit
     * 4. Validate Endorsement sub-ledger entries
     * 5. Cancel policy on or after effective date WITH employee benefit
     * 6. Validate Cancellation sub-ledger entries
     * 7. Reinstate policy with no lapse (reinstatement eff. date same as cancellation date)
     * 8. Validate Reinstatement sub-ledger entries
     * @details NBZ-02, END-03, CNL-02, RST-03
     */
	protected void testNewBusinessScenario_3() {

        // Create policy WITH employee benefit
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdWithEmpBenefit(getPolicyTD()));
        Dollar premTotal = getTotalTermPremium();

        //NBZ-02 validations
        assertSoftly(softly -> {
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044"));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")));
        });
        //Employee Benefit discount (CA Only) for NBZ-02 validations
        if(isAutoCA()){
            Dollar employeeDiscount = getEmployeeDiscount();
            assertSoftly(softly -> {
                softly.assertThat(employeeDiscount).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1044"));
                softly.assertThat(employeeDiscount).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1041"));
            });
        }

        // Perform AP endorsement
        performAPEndorsement(policyNumber);
        Dollar addedPrem = payTotalAmountDue();
        SearchPage.openPolicy(policyNumber);

        //END-03 validations
        assertSoftly(softly -> {
            softly.assertThat(addedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044")
            .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1044")));
            softly.assertThat(addedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022"))
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1041")));
            softly.assertThat(addedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021"))
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1044")));
            softly.assertThat(addedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015"))
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1041")));
        });

        // Cancel policy
        cancelPolicy();

        //validate CNL-02
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber);

        // Reinstate policy without lapse
        performReinstatement(policyNumber);

        //validate RST-03
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber);
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
     * @details NBZ-04, PMT-05, PMT-06, END-04, CNL-04, RST-04, RST-08, RST-10
     */
    protected void testNewBusinessScenario_4() {

        // Create policy WITH employee benefit, effective date three weeks from today
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime effDate = today.plusWeeks(3);
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdWithEmpBenefit(adjustTdPolicyEffDate(getPolicyTD(), effDate)));
        Dollar premTotal = getTotalTermPremium();

        //TODO NBZ-04 validations

        Dollar reducedPrem = performRPEndorsement(effDate, policyNumber);

        // TODO END-04 and PMT-05 validations

        // Advance time to policy effective date
        advanceTimeAndOpenPolicy(effDate, policyNumber);

        // Cancel policy
        cancelPolicy();

        // TODO CNL-04 validations

        // Advance time and reinstate policy with lapse
        performReinstatementWithLapse(effDate, policyNumber);

        // TODO RST-04 validations

        //TODO need to change the reinstatement lapse RST-08, then remove the lapse RST-10 and validations

    }

    private void validateCancellationTx(Dollar refundAmt, String policyNumber) {
        assertSoftly(softly -> {
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1044"));
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1015")));
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1021")));
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1022")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1022")));
        });
    }

    private void validateReinstatementTx(Dollar premAmt, String policyNumber) {
        assertSoftly(softly -> {
            softly.assertThat(premAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1044"));
            softly.assertThat(premAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")));
            softly.assertThat(premAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")));
            softly.assertThat(premAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")));
        });
    }

}
