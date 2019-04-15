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
import toolkit.utils.datetime.DateTimeUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import java.time.LocalDateTime;
import java.util.Map;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

    private static LocalDateTime cxDateStatusJob;
    private static LocalDateTime rstDate;

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
     * @details NBZ-01, PMT-01, TAX-02, TAX-05, TAX-08, TAX-09, END-01, CNL-01, RST-01, FEE-01
     */
	protected void testNewBusinessScenario_1() {

		// Create policy WITHOUT employee benefit
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy();
		Dollar premTotal = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY);

        // taxes only applies to WV and KY and value needs added to premium amount for correct validation below
        Dollar totalTaxesNB = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1053");

        // NBZ-01 validations
        assertSoftly(softly -> {
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044"));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")));
        });

        // PMT-01 validations
        Dollar paymentAmt = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.DEPOSIT_PAYMENT);
        assertSoftly(softly -> {
            softly.assertThat(paymentAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1001"));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1044"));
        });

        // TAX-02/TAX-08/PMT-01 validations
        if (getState().equals(Constants.States.KY)) {
            Map<String, Dollar> taxes = getTaxAmountsForPolicy(policyNumber);
            assertSoftly(softly -> {
                // TAX-02 (as part of PMT-01)
                softly.assertThat(taxes.get(STATE)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.STATE_TAX_KY, "1053"));
                softly.assertThat(taxes.get(CITY)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CITY_TAX_KY, "1053"));
                softly.assertThat(taxes.get(COUNTY)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.COUNTY_TAX_KY, "1053"));
                // TAX-08 (as part of NBZ-01)
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1053"));
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1054"));
            });
        }

        // TAX-05/PMT-01 validations (WV only)
        if (getState().equals(Constants.States.WV)) {
            Map<String, Dollar> taxes = getTaxAmountsForPolicy(policyNumber);
            assertSoftly(softly -> {
                // TAX-05
                softly.assertThat(taxes.get(STATE)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.STATE_TAX_WV, "1053"));
                // PMT-01
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1053"));
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1054"));
            });
        }

        // FEE-01/PMT-01 validations (CA Choice only)
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            Dollar policyFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY_FEE);
            assertSoftly(softly -> {
                // PMT-01
                softly.assertThat(policyFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1034"));
                // FEE-01
                softly.assertThat(policyFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1034"));
                softly.assertThat(policyFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1040"));
            });
        }

        // Perform AP endorsement and pay total amount due
        performAPEndorsement(policyNumber);
        Dollar addedPrem = payTotalAmountDue();
        SearchPage.openPolicy(policyNumber);

        Dollar totalTaxesEnd = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053"));

        // END-01 validations
        assertSoftly(softly -> {
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));
        });

        // TAX-09 validations (as part of END-01)
        if (isTaxState()) {
            Dollar taxesAdded = getTaxAmountsForPolicy(policyNumber).get(TOTAL).subtract(totalTaxesNB);
            assertSoftly(softly -> {
                softly.assertThat(taxesAdded).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                        .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")));
                softly.assertThat(taxesAdded).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")
                        .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")));
            });
        }

		// Cancel policy
        cancelPolicy(policyNumber);

		// CNL-01 validations
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber, totalTaxesNB.add(totalTaxesEnd));

		// Reinstate policy without lapse
        performReinstatement(policyNumber);

		// RST-01 validations
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber, totalTaxesNB.add(totalTaxesEnd));

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
     * @details NBZ-03, END-02, CNL-03, PMT-04, TAX-01, TAX-04, TAX-07, TAX-10, RST-02, RST-07, RST-09, FEE-04, FEE-15, FEE-18, FEE-19
     */
	protected void testNewBusinessScenario_2() {

		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime effDate = today.plusWeeks(3);

        // Create policy WITHOUT employee benefit, effective date three weeks from today
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createFinancialPolicy(adjustTdPolicyEffDate(getPolicyTD(), effDate));

        // Get premium and fee amounts from billing tab (if any)
        Dollar premTotal = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY);
        Dollar totalFees = BillingHelper.getFeesValue(today);
        Dollar totalTaxesNB = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1071");

        // NBZ-03 and PMT-04 validations
        assertSoftly(softly -> {
            softly.assertThat(premTotal.add(totalFees)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1001"));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.DEPOSIT_PAYMENT, "1065"));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1042"));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")));
        });

        // TAX-01/TAX-07 validations
        if (getState().equals(Constants.States.KY)) {
            Map<String, Dollar> taxes = getTaxAmountsForPolicy(policyNumber);
            assertSoftly(softly -> {
                // TAX-01 (as part of PMT-04)
                softly.assertThat(taxes.get(STATE)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.STATE_TAX_KY, "1071"));
                softly.assertThat(taxes.get(CITY)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CITY_TAX_KY, "1071"));
                softly.assertThat(taxes.get(COUNTY)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.COUNTY_TAX_KY, "1071"));
                // TAX-07 (as part of NBZ-03)
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1071"));
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1072"));
            });
        }

        // TAX-04/PMT-04 validations (WV only)
        if (getState().equals(Constants.States.WV)) {
            Map<String, Dollar> taxes = getTaxAmountsForPolicy(policyNumber);
            assertSoftly(softly -> {
                // TAX-04
                softly.assertThat(taxes.get(STATE)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.STATE_TAX_WV, "1071"));
                // PMT-04
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1071"));
                softly.assertThat(taxes.get(TOTAL)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1072"));
            });
        }

        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            Dollar policyFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY_FEE);
            // PMT-04 (Fees, CA Choice only)
            assertSoftly(softly -> {
                softly.assertThat(policyFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.POLICY_FEE, "1034"));
            });
        }

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

        Dollar reducedPrem = performRPEndorsement(policyNumber, effDate);

        //Advance time to policy effective date and run ledgerStatusUpdateJob to update the ledger
        TimeSetterUtil.getInstance().nextPhase(effDate);
        JobUtils.executeJob(Jobs.ledgerStatusUpdateJob);
        mainApp().open();

        // Validate NBZ-03 transactions that are recorded at effective date
        validateNewBusinessTxAtEffDAte(premTotal, totalTaxesNB, policyNumber, effDate);

        Dollar totalTaxesEnd = FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053"));

        // TAX-10 validations (as part of END-02)
        if (isTaxState()) {
            Dollar taxesReduced = totalTaxesNB.subtract(getTaxAmountsForPolicy(policyNumber).get(TOTAL));
            assertSoftly(softly -> {
                softly.assertThat(taxesReduced).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                        .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")));
                softly.assertThat(taxesReduced).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")
                        .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")));
            });
        }

        // END-02 validations
        assertSoftly(softly -> {
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")));
            softly.assertThat(totalTaxesEnd).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")));
            softly.assertThat(totalTaxesEnd).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1054")));
        });

		// Cancel policy on effective date (flat cancellation)
        cancelPolicy(policyNumber);
        LocalDateTime cxEffDate = getCancellationEffectiveDate();

        // CNL-03 validations
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber,  totalTaxesNB.subtract(totalTaxesEnd));

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

		// Advance time and reinstate policy with lapse
        performReinstatementWithLapse(policyNumber, effDate);
        Dollar rstTaxes = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053");
        Dollar rstPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT);

        // RST-02 validations
        validateReinstatementTx(rstPrem, policyNumber,  rstTaxes);

        // Validate RST-07 and RST-09 (only applicable for property)
        validateChangeAndRemoveReinstatementLapse(policyNumber, cxEffDate, rstPrem, rstTaxes);

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

        // taxes only applies to WV and KY and value needs added to premium amount for correct validation below
        Dollar totalTaxesNB = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1053");

        //NBZ-02 validations
        assertSoftly(softly -> {
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044"));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")
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

        Dollar totalTaxesEnd = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053"));

        //END-03 validations
        assertSoftly(softly -> {
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044")
            .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1044")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022"))
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1041")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021"))
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1044")));
            softly.assertThat(addedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015"))
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.EMPLOYEE_BENEFIT, "1041")));
        });

        // Cancel policy
        cancelPolicy(policyNumber);

        //validate CNL-02
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber, totalTaxesNB.add(totalTaxesEnd));

        // Reinstate policy without lapse
        performReinstatement(policyNumber);

        //validate RST-03
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber, totalTaxesNB.add(totalTaxesEnd));
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
     * @details NBZ-04, PMT-05, END-04, CNL-04, RST-04, RST-08, RST-10
     */
    protected void testNewBusinessScenario_4() {

        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime effDate = today.plusWeeks(3);

        // Create policy WITH employee benefit, effective date three weeks from today
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdWithEmpBenefit(adjustTdPolicyEffDate(getPolicyTD(), effDate)));
        Dollar premTotal = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.POLICY);
        Dollar totalTaxesNB = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1071");

        //NBZ-04 validations
        assertSoftly(softly -> {
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1042"));
            softly.assertThat(premTotal.subtract(totalTaxesNB)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")
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

        Dollar totalTaxesEnd = FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053")
                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1053"));

        //END-04 and PMT-05 validations
        assertSoftly(softly -> {
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1044"));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1021")));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1015")));
            softly.assertThat(reducedPrem.subtract(totalTaxesEnd)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ENDORSEMENT, "1022")
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
        validateNewBusinessTxAtEffDAte(premTotal, totalTaxesNB, policyNumber, effDate);

        //Cancel policy
        cancelPolicy(policyNumber);
        LocalDateTime cxEffDate = getCancellationEffectiveDate();

        //CNL-04 validations
        validateCancellationTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION), policyNumber, totalTaxesNB.subtract(totalTaxesEnd));

        //Advance time and reinstate policy with lapse
        performReinstatementWithLapse(policyNumber, effDate);
        Dollar rstTaxes = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053");
        Dollar rstPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT);

        Dollar totalTaxesReinstatement = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053");

        //RST-04 validations
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber, totalTaxesReinstatement);

        // Validate RST-08 and RST-10 (only applicable for property)
        validateChangeAndRemoveReinstatementLapse(policyNumber, cxEffDate, rstPrem, rstTaxes);

    }

    private void validateCancellationTx(Dollar refundAmt, String policyNumber, Dollar taxes) {
        assertSoftly(softly -> {
            softly.assertThat(refundAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1044"));
            softly.assertThat(refundAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1015")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1015")));
            softly.assertThat(refundAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1021")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1021")));
            softly.assertThat(refundAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1022")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1022")));
            softly.assertThat(taxes).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1053")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1053")));
            softly.assertThat(taxes).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1054")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1054")));
        });
    }

    private void validateReinstatementTx(Dollar premAmt, String policyNumber, Dollar taxes) {
        assertSoftly(softly -> {
            softly.assertThat(premAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1044"));
            softly.assertThat(premAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")));
            softly.assertThat(premAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")));
            softly.assertThat(premAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")));
            softly.assertThat(taxes).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053")));
            softly.assertThat(taxes).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1054")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1054")));
        });
    }

    private void validateNewBusinessTxAtEffDAte(Dollar premAmt, Dollar taxAmt, String policyNumber, LocalDateTime effDate) {
        assertSoftly(softly -> {
            //Recorded at effective date
            softly.assertThat(premAmt.subtract(taxAmt)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1044")));
            softly.assertThat(premAmt.subtract(taxAmt)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1022")));
            softly.assertThat(premAmt.subtract(taxAmt)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1021")));
            softly.assertThat(premAmt.subtract(taxAmt)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")
                    .subtract(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1015")));
            softly.assertThat(premAmt.subtract(taxAmt)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1043")));
            softly.assertThat(premAmt.subtract(taxAmt)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1042"));
            softly.assertThat(taxAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1071"));
            softly.assertThat(taxAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1072"));
            softly.assertThat(taxAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1053"));
            softly.assertThat(taxAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(effDate, policyNumber, FinancialsSQL.TxType.NEW_BUSINESS, "1054"));
        });

    }

    private void validateChangeAndRemoveReinstatementLapse(String policyNumber, LocalDateTime cxEffDate, Dollar rstPrem, Dollar rstTaxes) {
        if (!getPolicyType().isAutoPolicy() && !getPolicyType().equals(PolicyType.PUP)) {
            // Change reinstatement lapse
            SearchPage.openPolicy(policyNumber);
            policy.changeReinstatementLapse().perform(getPolicyTD("ReinstatementChangeLapse", "TestData_Plus5Days"));

            Dollar lapseChangeTaxes = rstTaxes.subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053")));
            Dollar rstChangePrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT);

            // Validations for RST-07
            assertSoftly(softly -> {
                softly.assertThat(rstPrem.subtract(rstTaxes).subtract(rstChangePrem.subtract(lapseChangeTaxes)))
                        .isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")
                                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")));
                softly.assertThat(rstPrem.subtract(rstTaxes).subtract(rstChangePrem.subtract(lapseChangeTaxes)))
                        .isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")
                                .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")));
                softly.assertThat(rstPrem.subtract(rstTaxes).subtract(rstChangePrem.subtract(lapseChangeTaxes)))
                        .isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")
                                .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")));
                softly.assertThat(rstPrem.subtract(rstTaxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1044"));

            });

            // Remove reinstatement lapse
            SearchPage.openPolicy(policyNumber);
            policy.changeReinstatementLapse().perform(getRemoveReinstatementLapseTd(cxEffDate));

            Dollar lapseRemovalPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT);
            Dollar lapseRemovalTaxes = FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053")
                    .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1053"))
                    .subtract(rstTaxes.subtract(lapseChangeTaxes));

            // Validations for RST-09
            assertSoftly(softly -> {
                softly.assertThat(lapseRemovalPrem.subtract(lapseRemovalTaxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015")
                        .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1015"))
                        .subtract(rstPrem.subtract(rstTaxes).subtract(rstChangePrem.subtract(lapseChangeTaxes))));
                softly.assertThat(lapseRemovalPrem.subtract(lapseRemovalTaxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021")
                        .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1021"))
                        .subtract(rstPrem.subtract(rstTaxes).subtract(rstChangePrem.subtract(lapseChangeTaxes))));
                softly.assertThat(lapseRemovalPrem.subtract(lapseRemovalTaxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022")
                        .subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1022"))
                        .subtract(rstPrem.subtract(rstTaxes).subtract(rstChangePrem.subtract(lapseChangeTaxes))));
                softly.assertThat(lapseRemovalPrem.subtract(lapseRemovalTaxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1044")
                        .subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REINSTATEMENT, "1044"))
                        .subtract(rstPrem.subtract(rstTaxes).subtract(rstChangePrem.subtract(lapseChangeTaxes))));
            });
        }
    }

    private void performReinstatementWithLapse(String policyNumber, LocalDateTime effDate) {
        TimeSetterUtil.getInstance().nextPhase(getCxDateStatusJobTime(effDate));
        JobUtils.executeJob(Jobs.changeCancellationPendingPoliciesStatus);
        TimeSetterUtil.getInstance().nextPhase(getRstDate(effDate));
        mainApp().open();
        performReinstatement(policyNumber);

    }

    private synchronized LocalDateTime getCxDateStatusJobTime(LocalDateTime effDate) {
        if (cxDateStatusJob == null) {
            cxDateStatusJob = effDate.plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay);
        }
        assertThat(cxDateStatusJob).isEqualToIgnoringHours(effDate.plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay));
        return cxDateStatusJob;
    }

    private synchronized LocalDateTime getRstDate(LocalDateTime effDate) {
        if (rstDate == null) {
            rstDate = effDate.plusDays(20);
        }
        assertThat(rstDate).isEqualToIgnoringHours(effDate.plusDays(20));
        return rstDate;
    }

}
