package aaa.modules.financials.template;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.toolkit.webdriver.customcontrols.AdvancedAllocationsRepeatAssetList;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.RefundActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import aaa.modules.financials.FinancialsSQL;
import com.google.common.collect.ImmutableMap;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

    private OtherTransactionsActionTab otherTransactionsActionTab = new OtherTransactionsActionTab();
    private AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
    private BillingAccount billingAccount = new BillingAccount();
    private TestData tdBilling = testDataManager.billingAccount;
    private Dollar zeroDollars = new Dollar(0.00);

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

		// Create policy WITHOUT employee benefit, effective date three weeks from today
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime effDate = today.plusWeeks(3);
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
        JobUtils.executeJob(BatchJob.ledgerStatusUpdateJob);
        mainApp().open();
        SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_PENDING);

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
        performReinstatementWithLapse(effDate, policyNumber);
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
        LocalDateTime effDate = PolicySummaryPage.getEffectiveDate();
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
        Dollar cxPremAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION);

        //validate CNL-02
        validateCancellationTx(cxPremAmount, policyNumber, zeroDollars);

        // Refund amount due back to customer manually
        Dollar cancelRefund = generateManualRefund();

        // Validate PMT-06
        assertSoftly(softly -> {
            softly.assertThat(cancelRefund).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MANUAL_REFUND, "1044"));
            softly.assertThat(cancelRefund).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MANUAL_REFUND, "1060"));
        });

        // Void refund
        voidRefundPayment(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_REFUND);

        // Validate PMT-07
        assertSoftly(softly -> {
            softly.assertThat(cancelRefund).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REFUND_PAYMENT_VOIDED, "1044"));
            softly.assertThat(cancelRefund).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.REFUND_PAYMENT_VOIDED, "1060"));
        });

        // Reinstate policy without lapse
        performReinstatement(policyNumber);

        //validate RST-03
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber, zeroDollars);

        // Overpay and generate refund automatically
        LocalDateTime refundDate = getTimePoints().getRefundDate(effDate);
        if (!BillingSummaryPage.getTotalDue().isNegative()) {
            billingAccount.acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Check"), BillingSummaryPage.getTotalDue().abs().add(new Dollar(100.00)));
        }
        Dollar refundAmt = generateAutomaticRefund(policyNumber, refundDate);

        // Validate PMT-19
        assertSoftly(softly -> {
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.AUTOMATED_REFUND, "1044"));
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.AUTOMATED_REFUND, "1060"));
        });

        // Advance time and run escheatment job
        LocalDateTime escheatmentDate = refundDate.plusMonths(13).withDayOfMonth(1);
        TimeSetterUtil.getInstance().nextPhase(escheatmentDate);
        JobUtils.executeJob(BatchJob.aaaEscheatmentProcessAsyncJob);

        // Validate PMT-14 and PMT-15
        assertSoftly(softly -> {
            // PMT-14
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(escheatmentDate, policyNumber, FinancialsSQL.TxType.REFUND_PAYMENT_VOIDED, "1044"));
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(escheatmentDate, policyNumber, FinancialsSQL.TxType.REFUND_PAYMENT_VOIDED, "1060"));
            // PMT-15
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ESCHEATMENT, "1041"));
            softly.assertThat(refundAmt).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.ESCHEATMENT, "1044"));
        });

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

        // Perform RP endorsement and generate refund
        Dollar reducedPrem = performRPEndorsement(policyNumber, effDate);
        SearchPage.openBilling(policyNumber);
        Dollar endorsementRefund = generateManualRefund();

        // Validate PMT-05
        assertSoftly(softly -> {
            softly.assertThat(endorsementRefund).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MANUAL_REFUND, "1065"));
            softly.assertThat(endorsementRefund).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MANUAL_REFUND, "1060"));
        });

        //Advance time to policy effective date and run ledgerStatusUpdateJob to update the ledger
        TimeSetterUtil.getInstance().nextPhase(effDate);
        JobUtils.executeJob(BatchJob.ledgerStatusUpdateJob);
        mainApp().open();
        SearchPage.openBilling(policyNumber);

        //END-04 validations
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
        validateNewBusinessTxAtEffDAte(premTotal, zeroDollars, policyNumber, effDate);

        //Cancel policy
        cancelPolicy(policyNumber);
        LocalDateTime cxEffDate = getCancellationEffectiveDate();
        Dollar cxRefundAmt = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION);

        //CNL-04 validations
        validateCancellationTx(cxRefundAmt, policyNumber, zeroDollars);

        //Advance time and reinstate policy with lapse
        performReinstatementWithLapse(effDate, policyNumber);
        Dollar rstPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT);

        //RST-04 validations
        validateReinstatementTx(getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT), policyNumber, zeroDollars);

        // Validate RST-08 and RST-10 (only applicable for property)
        validateChangeAndRemoveReinstatementLapse(policyNumber, cxEffDate, rstPrem, zeroDollars);

    }

    /**
     * @scenario
     * 1. Create new policy for NJ/NY/WV with effective date today + 3 weeks
     * 2. For NJ policy use driver that returns a DUI and has SR22 filing
     * 3. For NJ, validate PLIGA fee and SR22 fee entries
     * 4. For NY, validate MLVE fee
     * 5. For WV, pay total amount due with future-dated check and then decline the payment
     * @details FEE-02, FEE-03, FEE-16, PMT-18
     */
    protected void testNewBusinessScenario_5() {

        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime effDate = today.plusWeeks(3);

        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(getAutoSSFeesTd(effDate));

        if (getState().equals(Constants.States.NJ)) {
            Dollar pligaFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PLIGA_FEE);
            Dollar sr22Fee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SR22_FEE);

            // FEE-02 and FEE-16 validations
            assertSoftly(softly -> {
                softly.assertThat(pligaFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.PLIGA_FEE, "1034"));
                softly.assertThat(pligaFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.PLIGA_FEE, "1040"));
                softly.assertThat(sr22Fee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.SR22_FEE, "1034"));
                softly.assertThat(sr22Fee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.SR22_FEE, "1040"));
            });

        }

        if (getState().equals(Constants.States.NY)) {
            Dollar mvleFee = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MVLE_FEE);

            // FEE-03 validations
            assertSoftly(softly -> {
                softly.assertThat(mvleFee).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MVLE_FEE, "1034"));
                softly.assertThat(mvleFee).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.MVLE_FEE, "1040"));
            });

        }

        if (getState().equals(Constants.States.WV)) {
            Dollar totalTaxes = getTaxAmountsForPolicy(policyNumber).get(TOTAL);
            Dollar remainingTaxes = totalTaxes.subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.STATE_TAX_WV, "1071"));
            Dollar pmtAmt = payTotalAmountDueWithDatedCheck(effDate);
            BillingHelper.declinePayment(today);

            // PMT-18 validations
            assertSoftly(softly -> {
                softly.assertThat(pmtAmt).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.PAYMENT_DECLINED, "1001"));
                softly.assertThat(pmtAmt.subtract(remainingTaxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.PAYMENT_DECLINED, "1065"));
                softly.assertThat(remainingTaxes).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.STATE_TAX_WV, "1071"));
            });

        }

    }

    /**
     * @scenario
     * 1. Create policy with monthly payment plan
     * 2. Advance time, generate and pay first installment bill
     * 3. Waive Fee
     * 4. Validate Reallocation Adjustment ledger entries
	 * 5. Generate and pay second installment bill - Future dated
	 * 6. Waive Fee
	 * 7. Validate Reallocation Adjustment ledger entries
	 * 8. Cancel policy - future dated
	 * 9. Validate ledger entries - cancellation
     * @details OPR-01, OPR-02, CNL-05
     */
    protected void testNewBusinessScenario_6() {
        // Create policy WITHOUT employee benefit, monthly payment plan
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
        LocalDateTime dueDate = PolicySummaryPage.getEffectiveDate().plusMonths(1);

        //OPR-01
        //Advance time, generate and pay first installment bill
        LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(dueDate);
        LocalDateTime billDueDate = getTimePoints().getBillDueDate(dueDate);
        TimeSetterUtil.getInstance().nextPhase(billGenDate);
        JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
        TimeSetterUtil.getInstance().nextPhase(billDueDate);

        mainApp().open();
        SearchPage.openBilling(policyNumber);
        payMinAmountDue(METHOD_CASH);

        waiveFeeByDateAndType(billGenDate, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getColumn(BillingConstants.BillingPaymentsAndOtherTransactionsTable
                .SUBTYPE_REASON).getValue()).contains(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT);

        Dollar reallocationAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT);
        Dollar totalFeeNB = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);
        Map<String, Dollar> adjustmentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT, billDueDate);
        Map<String, Dollar> paymentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT, billDueDate);

        //OPR-01 validation
        assertSoftly(softly -> {
            softly.assertThat(reallocationAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1001"));
            softly.assertThat(reallocationAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1001"));
            softly.assertThat(paymentAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1044"));
            softly.assertThat(adjustmentAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1044"));
            softly.assertThat(totalFeeNB).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1034"));
            softly.assertThat(BillingHelper.DZERO).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1034"));
        });
        // Tax Validations
        if (isTaxState()) {
            assertSoftly(softly -> {
                softly.assertThat(adjustmentAllocations.get("Taxes")).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1053"));
                softly.assertThat(paymentAllocations.get("Taxes")).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1053"));
            });
        }
        //OPR-02
        // Advance time 1 month, generate and pay installment bill
        LocalDateTime secondBillGenDate = getTimePoints().getBillGenerationDate(dueDate.plusMonths(1));
        TimeSetterUtil.getInstance().nextPhase(secondBillGenDate);
        JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

        mainApp().open();
        SearchPage.openBilling(policyNumber);
        payMinAmountDue(METHOD_CASH);

        waiveFeeByDateAndType(secondBillGenDate, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getColumn(BillingConstants.BillingPaymentsAndOtherTransactionsTable
                .SUBTYPE_REASON).getValue()).contains(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT);

        Dollar secondReallocationAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT);
        Dollar secondTalFeeNB = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.FEE,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE);
        Map<String, Dollar> secondAdjustmentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT, secondBillGenDate);
        Map<String, Dollar> secondPaymentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT, secondBillGenDate);

        //OPR-02 validation
        assertSoftly(softly -> {
            softly.assertThat(reallocationAmount.add(secondReallocationAmount)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1001"));
            softly.assertThat(reallocationAmount.add(secondReallocationAmount)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1001"));
            softly.assertThat(paymentAllocations.get("Net Premium").add(secondPaymentAllocations.get("Net Premium"))).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1044"));
            softly.assertThat(adjustmentAllocations.get("Net Premium").add(secondAdjustmentAllocations.get("Net Premium"))).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1044"));
            softly.assertThat(secondTalFeeNB).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1034"));
            softly.assertThat(BillingHelper.DZERO).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(billDueDate, policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1034"));
        });
        // Tax Validations
        if (isTaxState()) {
            assertSoftly(softly -> {
                softly.assertThat(adjustmentAllocations.get("Taxes").add(secondAdjustmentAllocations.get("Taxes"))).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1053"));
                softly.assertThat(paymentAllocations.get("Taxes").add(secondPaymentAllocations.get("Taxes"))).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.OVERPAYMENT_REALLOCATION_ADJUSTMENT, "1053"));
            });
        }
        // CNL - 05 Cancel policy
        cancelPolicy(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().plusDays(20));

        // taxes only applies to WV and KY and value needs added to premium amount for correct validation below
        Dollar totalTaxes = FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1053");
        Dollar cxPremAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION);
        //CNL-05 validation
        validateCancellation(cxPremAmount, policyNumber, totalTaxes);
    }

    /**
     * @scenario
     * 1. Create policy
     * 2. Create Other Transaction - adjustment - Write off with positive & negative values
     * 3. Validate ledger entries - Write-off
     * 4. Create Other Transaction - adjustment - Write off, Advanced allocations
     * 5. Validate ledger entries - Write-off, Advanced allocations
     * 6. Create Other Transaction - adjustment - Write off, Advanced allocations negate
     * 7. Validate ledger entries - Write-off, Advanced allocations negate
     * @details ADJ-03
     */
    protected void testNewBusinessScenario_7() {
        TestData writeOffTD = tdBilling.getTestData("Transaction", "TestData_WriteOff");
        //1. Create policy
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy();
        String effectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue();

        //2. Create Other Transaction - adjustment - Write off
        BillingSummaryPage.open();
        billingAccount.otherTransactions().perform(writeOffTD, new Dollar(100));
        Dollar writeOffPositiveAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.WRITE_OFF);
        billingAccount.otherTransactions().perform(writeOffTD, new Dollar(100).negate());
        Dollar writeOffNegativeAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.WRITE_OFF);

        //3. ADJ-03 Validate ledger entries - Write-off
        assertSoftly(softly -> {
            softly.assertThat(writeOffPositiveAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.WRITEOFF, "1044"));
            softly.assertThat(writeOffPositiveAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.WRITEOFF, "1041"));
            softly.assertThat(writeOffNegativeAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.WRITEOFF, "1041"));
            softly.assertThat(writeOffNegativeAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.WRITEOFF, "1044"));
        });

        //4. Create Other Transaction - adjustment - Write off, Advanced allocations
        BillingSummaryPage.linkOtherTransactions.click();
        otherTransactionsActionTab.fillTab(writeOffTD.adjust(TestData.makeKeyPath(OtherTransactionsActionTab.class.getSimpleName(), BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel()), new Dollar(100).toString()));
        AdvancedAllocationsActionTab.linkAdvancedAllocation.click();

        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
                .getAsset(AdvancedAllocationsRepeatAssetList.NET_PREMIUM, TextBox.class, 0).setValue("70");
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
                .getAsset(AdvancedAllocationsRepeatAssetList.POLICY_FEE, TextBox.class, 0).setValue("20");
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
                .getAsset(AdvancedAllocationsRepeatAssetList.PLIGA_FEE, TextBox.class, 0).setValue("10");
        advancedAllocationsActionTab.submitTab();

        //5. ADJ-03 Validate ledger entries - Write-off, Advanced allocations
        String accountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        List<String> transactionIds = FinancialsSQL.getTransactionIdsForAccount(accountNumber);
        int index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.WRITE_OFF);
        String paymentTransferAdjId = transactionIds.get(index);

        Map<String, Dollar> paymentTransferAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.WRITE_OFF, TimeSetterUtil.getInstance().getCurrentTime());

        assertSoftly(softly -> {
            softly.assertThat(paymentTransferAllocations.get(AdvancedAllocationsRepeatAssetList.NET_PREMIUM + effectiveDate)).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(paymentTransferAdjId, FinancialsSQL.TxType.WRITEOFF, "1044")).isEqualTo(new Dollar(70));
            softly.assertThat(paymentTransferAllocations.get(AdvancedAllocationsRepeatAssetList.POLICY_FEE + effectiveDate)).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(paymentTransferAdjId, FinancialsSQL.TxType.POLICY_FEE, "1034")).isEqualTo(new Dollar(20));
            softly.assertThat(paymentTransferAllocations.get(AdvancedAllocationsRepeatAssetList.PLIGA_FEE + effectiveDate)).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(paymentTransferAdjId, FinancialsSQL.TxType.PLIGA_FEE, "1034")).isEqualTo(new Dollar(10));
            softly.assertThat(writeOffPositiveAmount).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(paymentTransferAdjId, FinancialsSQL.TxType.WRITEOFF, "1041"));
        });

        //6. Create Other Transaction - adjustment - Write off, Advanced allocations negate
        BillingSummaryPage.linkOtherTransactions.click();
        otherTransactionsActionTab.fillTab(writeOffTD.adjust(TestData.makeKeyPath(OtherTransactionsActionTab.class.getSimpleName(), BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel()), new Dollar(100).negate().toString()));
        AdvancedAllocationsActionTab.linkAdvancedAllocation.click();

        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
                .getAsset(AdvancedAllocationsRepeatAssetList.NET_PREMIUM, TextBox.class, 0).setValue("-70");
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
                .getAsset(AdvancedAllocationsRepeatAssetList.POLICY_FEE, TextBox.class, 0).setValue("-20");
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.ADVANCED_ALLOCATIONS)
                .getAsset(AdvancedAllocationsRepeatAssetList.PLIGA_FEE, TextBox.class, 0).setValue("-10");
        advancedAllocationsActionTab.submitTab();

        //7. ADJ-03 Validate ledger entries - Write-off, Advanced allocations negate
        transactionIds = FinancialsSQL.getTransactionIdsForAccount(accountNumber);
        index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.WRITE_OFF);
        String paymentTransferAdjIdNegate = transactionIds.get(index);

        Map<String, Dollar> paymentTransferAllocationsNegate = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.WRITE_OFF, TimeSetterUtil.getInstance().getCurrentTime());

        assertSoftly(softly -> {
            softly.assertThat(paymentTransferAllocationsNegate.get(AdvancedAllocationsRepeatAssetList.NET_PREMIUM + effectiveDate).negate()).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(paymentTransferAdjIdNegate, FinancialsSQL.TxType.WRITEOFF, "1044")).isEqualTo(new Dollar(70));
            softly.assertThat(paymentTransferAllocationsNegate.get(AdvancedAllocationsRepeatAssetList.POLICY_FEE + effectiveDate).negate()).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(paymentTransferAdjIdNegate, FinancialsSQL.TxType.POLICY_FEE, "1034")).isEqualTo(new Dollar(20));
            softly.assertThat(paymentTransferAllocationsNegate.get(AdvancedAllocationsRepeatAssetList.PLIGA_FEE + effectiveDate).negate()).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(paymentTransferAdjIdNegate, FinancialsSQL.TxType.PLIGA_FEE, "1034")).isEqualTo(new Dollar(10));
            softly.assertThat(writeOffNegativeAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(paymentTransferAdjIdNegate, FinancialsSQL.TxType.WRITEOFF, "1041"));
        });
    }

    /**
     * @scenario
     * 1. Create new policy monthly
     * 2. generate 1st bill at DD-20, generate 2nd bill at DD2-20
     * 3. DD+5 Add Cancel Notice
     * 4. Cancel Policy
     * 5. Run earned premium job
     * 6. Make payment (write off)
     * @details ADJ-09, ADJ-10
     */
    protected void testNewBusinessScenario_8() {
        //1. Create new policy monthly
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createFinancialPolicy(adjustTdMonthlyPaymentPlan(getPolicyTD()));
        LocalDateTime renewalEffDate = PolicySummaryPage.getExpirationDate();
        BillingSummaryPage.open();
        LocalDateTime dueDate = BillingSummaryPage.getInstallmentDueDate(2);
        LocalDateTime dueDate2 = BillingSummaryPage.getInstallmentDueDate(3);

        //2. generate 1st bill at DD-20, generate 2nd bill at DD2-20
        //Move time to R-20
        if (!getState().equals(Constants.States.CA)) {
            TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate));//-20 days
            //Generate bill
            JobUtils.executeJob(BatchJob.billingInvoiceGenerationJob);

            TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dueDate2));//-20 days
            //Generate bill
            JobUtils.executeJob(BatchJob.billingInvoiceGenerationJob);
        }

        //3. DD+5 Add Cancel Notice
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(dueDate2));//+5 days
        JobUtils.executeJob(BatchJob.aaaCancellationNoticeAsyncJob);
        searchForPolicy(policyNumber);

        assertThat(PolicySummaryPage.labelCancelNotice).isPresent();

        //4. Cancel Policy
        log.info("Policy Cancellation Started...");
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(21).with(DateTimeUtils.nextWorkingDay));
        JobUtils.executeJob(BatchJob.aaaCancellationConfirmationAsyncJob);
        searchForPolicy(policyNumber);

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        //5. Run earned premium job
        BillingSummaryPage.open();
        LocalDateTime transactionCancellationDate = getTransactionDate(ImmutableMap.of(BillingConstants.BillingPaymentsAndOtherTransactionsTable.POLICY, policyNumber,
                BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM));

        processEarnedPremiumJob(transactionCancellationDate, policyNumber);

        //6. Make payment (write off)
        Dollar earnedPremiumWriteOffAmt = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()).negate();

        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), earnedPremiumWriteOffAmt);

        //get payment values
        String accountNumber = BillingSummaryPage.labelBillingAccountNumber.getValue();
        List<String> transactionIds = FinancialsSQL.getTransactionIdsForAccount(accountNumber);
        int index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF_REVERSED);
        String paymentEarnedPremiumWriteOffReversedId = transactionIds.get(index);

        index = getTransactionIndexByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT);
        Dollar manualPaymentAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT);
        String manualPaymentId = transactionIds.get(index);

        Map<String, Dollar> paymentTransferAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF_REVERSED, TimeSetterUtil.getInstance().getCurrentTime());
        Map<String, Dollar> manualPaymentAllocations = getAllocationsFromTransaction(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT,
                BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT, TimeSetterUtil.getInstance().getCurrentTime());

        //ADJ-09, ADJ-10 validation
        assertSoftly(softly -> {
            //Earned Premium Write-off Reversed
            softly.assertThat(paymentTransferAllocations.get("Net Premium").add(paymentTransferAllocations.getOrDefault("Taxes", BillingHelper.DZERO))).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(paymentEarnedPremiumWriteOffReversedId, FinancialsSQL.TxType.EARNED_PREMIUM_WRITE_OFF, "1037"));
            softly.assertThat(paymentTransferAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(paymentEarnedPremiumWriteOffReversedId, FinancialsSQL.TxType.EARNED_PREMIUM_WRITE_OFF, "1044"));
            softly.assertThat(paymentTransferAllocations.get("Fees")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(paymentEarnedPremiumWriteOffReversedId, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1046"));
            softly.assertThat(paymentTransferAllocations.get("Fees")).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(paymentEarnedPremiumWriteOffReversedId, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));

            //Manual Payment
            softly.assertThat(manualPaymentAmount).isEqualTo(FinancialsSQL.getDebitsForAccountByTransaction(manualPaymentId, FinancialsSQL.TxType.MANUAL_PAYMENT, "1066"));
            softly.assertThat(manualPaymentAllocations.get("Net Premium")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(manualPaymentId, FinancialsSQL.TxType.MANUAL_PAYMENT, "1044"));
            softly.assertThat(manualPaymentAllocations.get("Fees")).isEqualTo(FinancialsSQL.getCreditsForAccountByTransaction(manualPaymentId, FinancialsSQL.TxType.NON_EFT_INSTALLMENT_FEE, "1034"));
        });

        // Tax Validations for ADJ-09, ADJ-10
        if (isTaxState()) {
            assertSoftly(softly -> {
                softly.assertThat(manualPaymentAllocations.get("Taxes")).isEqualTo(getTotalTaxesFromDb(manualPaymentId, "1053", true));
                softly.assertThat(paymentTransferAllocations.get("Taxes")).isEqualTo(getTotalTaxesFromDb(paymentEarnedPremiumWriteOffReversedId, "1053", false));
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

    private LocalDateTime getTransactionDate(Map<String, String> values) {
        return TimeSetterUtil.getInstance().parse(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(values).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
    }

    private void processEarnedPremiumJob(LocalDateTime expirationDate, String policyNumber) {
        //move time to R+60 and run earnedpremiumWriteoffprocessingjob
        LocalDateTime earnedPremiumWriteOff = getTimePoints().getEarnedPremiumWriteOff(expirationDate);
        TimeSetterUtil.getInstance().nextPhase(earnedPremiumWriteOff);
        JobUtils.executeJob(BatchJob.earnedPremiumWriteoffProcessingJob);
        mainApp().reopen();
        SearchPage.openBilling(policyNumber);

        HashMap<String, String> writeOffTransaction = new HashMap<>();
        writeOffTransaction.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
        writeOffTransaction.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT);
        writeOffTransaction.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF);

        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(writeOffTransaction)).isPresent();
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

    private void performReinstatementWithLapse(LocalDateTime effDate, String policyNumber) {
        TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay));
        JobUtils.executeJob(BatchJob.changeCancellationPendingPoliciesStatusJob);
        TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(20));
        mainApp().open();
        performReinstatement(policyNumber);

    }

    private Dollar generateManualRefund() {
        AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
        billingAccount.refund().start();
        Dollar amount = new Dollar(RefundActionTab.tblAllocations.getRow(1).getCell("Balance Due").getValue()).abs();
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue("Check");
        acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).setValue(amount.toString());
        acceptPaymentActionTab.submitTab();
        if (BillingSummaryPage.tablePendingTransactions.getRowsCount() > 0) {
            billingAccount.approveRefund().perform(1);
        }
        return amount;
    }

    private Dollar generateAutomaticRefund(String policyNumber, LocalDateTime refundDate) {
        TimeSetterUtil.getInstance().nextPhase(refundDate);
        try {
            JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob);
        } catch (IstfException e) {
            // Getting intermittent errors, catching error for now
            log.error(BatchJob.aaaRefundGenerationAsyncJob.getJobName() + " failed, continuing with test...");
        }
        try {
            JobUtils.executeJob(BatchJob.aaaRefundDisbursementAsyncJob);
        } catch (IstfException e) {
            // Getting intermittent errors, catching error for now
            log.error(BatchJob.aaaRefundDisbursementAsyncJob.getJobName() + " failed, continuing with test...");
        }
        mainApp().open();
        SearchPage.openBilling(policyNumber);
        Dollar amount;
        if (BillingSummaryPage.tablePendingTransactions.getRowsCount() > 0) {
            amount = new Dollar(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(BillingConstants.BillingPendingTransactionsTable.AMOUNT).getValue());
            billingAccount.approveRefund().perform(1);
        } else {
            amount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.REFUND,  BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND);
        }
        Map<String, String> query = new HashMap<>();
        query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.REFUND);
        query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND);
        if (BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query)
                .getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(BillingConstants.PaymentsAndOtherTransactionAction.ISSUE).isPresent()) {
            billingAccount.issueRefund().perform(1);
        }
        return amount;
    }

	private void validateCancellation(Dollar cancellationAmt, String policyNumber, Dollar taxes) {
		assertSoftly(softly -> {
			softly.assertThat(cancellationAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1044"));
			softly.assertThat(cancellationAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1015")
					.subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1015")));
			softly.assertThat(cancellationAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1021")
					.subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1021")));
			softly.assertThat(cancellationAmt.subtract(taxes)).isEqualTo(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1022")
					.subtract(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1022")));
		});
		// Tax Validations CNL-05
		if (isTaxState()) {
			assertSoftly(softly -> {
				softly.assertThat(taxes).isEqualTo(FinancialsSQL.getCreditsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1053")
						.subtract(FinancialsSQL.getDebitsForAccountByPolicy(policyNumber, FinancialsSQL.TxType.CANCELLATION, "1053")));
			});
		}
	}

}
