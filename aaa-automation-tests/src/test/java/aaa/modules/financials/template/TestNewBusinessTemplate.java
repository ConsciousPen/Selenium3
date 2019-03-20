package aaa.modules.financials.template;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.financials.FinancialsBaseTest;
import aaa.modules.financials.FinancialsSQL;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import java.time.LocalDateTime;
import java.util.Map;

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
     * @details NBZ-01, PMT-01, TAX-02, TAX-05, END-01, CNL-01, RST-01, FEE-01
     */
	protected void testNewBusinessScenario_1() {

		// Create policy WITHOUT employee benefit
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy();
		Dollar premTotal = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY);

        // NBZ-01 validations
        assertSoftly(softly -> {
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044"));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")));
        });

        // PMT-01 validations
        Dollar paymentAmt = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.DEPOSIT_PAYMENT);
        assertSoftly(softly -> {
            softly.assertThat(paymentAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1001"));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1044"));
        });

        // TAX-02/PMT-01 validations (KY only)
        if (getState().equals(Constants.States.KY)) {
            Map<String, Dollar> taxes = getTaxAmountsFromVRD(policyNumber);
            assertSoftly(softly -> {
                // TAX-02
                softly.assertThat(taxes.get(STATE)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.STATE_TAX_KY, "1053"));
                softly.assertThat(taxes.get(CITY)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CITY_TAX_KY, "1053"));
                softly.assertThat(taxes.get(COUNTY)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.COUNTY_TAX_KY, "1053"));
                // PMT-01
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1053"));
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1054"));
            });
        }

        // TAX-05/PMT-01 validations (WV only)
        if (getState().equals(Constants.States.WV)) {
            Map<String, Dollar> taxes = getTaxAmountsFromVRD(policyNumber);
            assertSoftly(softly -> {
                // TAX-05
                softly.assertThat(taxes.get(STATE)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.STATE_TAX_WV, "1053"));
                // PMT-01
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1053"));
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1054"));
            });
        }

        // FEE-01 validations (CA Choice only)
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            Dollar policyFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY_FEE);
            assertSoftly(softly -> {
                softly.assertThat(policyFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1034"));
                softly.assertThat(policyFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1034"));
                softly.assertThat(policyFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1040"));
            });
        }
        SearchPage.openPolicy(policyNumber);

        // Perform AP endorsement and pay total amount due
        performAPEndorsement(policyNumber);
        Dollar addedPrem = payTotalAmountDue();
        SearchPage.openPolicy(policyNumber);

        // END-01 validations
        assertSoftly(softly -> {
            softly.assertThat(addedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
            softly.assertThat(addedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(addedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(addedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));
        });

		// Cancel policy
        cancelPolicy(policyNumber);

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

        // Get premium and fee amounts from billing tab (if any)
        Dollar premTotal = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY);
        Dollar totalFees = BillingHelper.getFeesValue(today);

        // NBZ-03 and PMT-04 validations
        assertSoftly(softly -> {
            softly.assertThat(premTotal.add(totalFees)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1001"));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1065"));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1042"));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")));
        });

        // TAX-01 and TAX-04 validations
//        assertSoftly(softly -> {
//            softly.assertThat()
//        });

        // FEE-15 validations (CA Select only)
        if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
            Dollar fraudFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CA_FRAUD_ASSESSMENT_FEE);
            assertSoftly(softly -> {
                softly.assertThat(fraudFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1034"));
                softly.assertThat(fraudFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1034"));
                softly.assertThat(fraudFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1040"));
            });
        }

        // FEE-04 validations (CA HO products only, excluding DP3 and PUP)
        if (getPolicyType().equals(PolicyType.HOME_CA_HO3) || getPolicyType().equals(PolicyType.HOME_CA_DP3) || getPolicyType().equals(PolicyType.HOME_CA_HO6)) {
            Dollar seismicFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SEISMIC_SAFETY_FEE);
            assertSoftly(softly -> {
                softly.assertThat(seismicFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.SEISMIC_FEE, "1034"));
                softly.assertThat(seismicFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.SEISMIC_FEE, "1040"));
            });
        }

        // Advance time to policy effective date
        advanceTimeAndOpenPolicy(effDate, policyNumber);

        // Perform RP endorsement
        Dollar reducedPrem = performRPEndorsement(policyNumber, effDate);

        // END-02 validations
        assertSoftly(softly -> {
            softly.assertThat(reducedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
            softly.assertThat(reducedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(reducedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(reducedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));
        });

		// Cancel policy on effective date (flat cancellation)
        cancelPolicy(policyNumber);

        // CNL-03 validations
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber);

        // FEE-19 validations (CA Choice only)
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            Dollar cancellationFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_FEE);
            assertSoftly(softly -> {
                softly.assertThat(cancellationFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION_FEE, "1034"));
                softly.assertThat(cancellationFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION_FEE, "1040"));
            });
        }

        // FEE-18 validations
        if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
            Dollar fraudFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CA_FRAUD_ASSESSMENT_FEE);
            assertSoftly(softly -> {
                softly.assertThat(fraudFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1034"));
                softly.assertThat(fraudFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.CA_FRAUD_ASSESSMENT_FEE, "1040"));
            });
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
        Dollar premTotal = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY);

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
            Dollar employeeDiscount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EMPLOYEE_BENEFIT);
            assertSoftly(softly -> {
                softly.assertThat(employeeDiscount).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1044"));
                softly.assertThat(employeeDiscount).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1041"));
            });
        }
        SearchPage.openPolicy(policyNumber);

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
        cancelPolicy(policyNumber);

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
        Dollar premTotal = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY);

        //NBZ-04 validations
        assertSoftly(softly -> {
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1042"));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")));
        });
        //Employee Benefit discount (CA Auto Only) for NBZ-04 validations
        if(isAutoCA()){
            assertSoftly(softly -> {
                Dollar employeeDiscount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EMPLOYEE_BENEFIT);
                softly.assertThat(employeeDiscount).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1044"));
                softly.assertThat(employeeDiscount).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1041"));
            });
        }

        SearchPage.openPolicy(policyNumber);
        Dollar reducedPrem = performRPEndorsement(policyNumber, effDate);

        //Advance time to policy effective date and run ledgerStatusUpdateJob to update the ledger
        TimeSetterUtil.getInstance().nextPhase(effDate);
        JobUtils.executeJob(Jobs.ledgerStatusUpdateJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);

        //END-04 and PMT-05 validations
        assertSoftly(softly -> {
            softly.assertThat(reducedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
            softly.assertThat(reducedPrem).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(reducedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(reducedPrem).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));
        });
        //END-04 Employee Benefit validations (CA Auto Only)
        if(isAutoCA()){
            assertSoftly(softly -> {
                //Store employee benefit for END-04
                Dollar employeeDiscount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EMPLOYEE_BENEFIT);
                softly.assertThat(employeeDiscount).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1044"));
                softly.assertThat(employeeDiscount).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1041"));
            });
        }

        //Remaining NBZ-04 validations that are recorded at effective date
        assertSoftly(softly -> {
            //Recorded at effective date
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")));
            softly.assertThat(premTotal).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1042"));
        });

        //Cancel policy
        cancelPolicy(policyNumber);

        //CNL-04 validations
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber);

        //Advance time and reinstate policy with lapse
        performReinstatementWithLapse(effDate, policyNumber);

        //RST-04 validations
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber);

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
