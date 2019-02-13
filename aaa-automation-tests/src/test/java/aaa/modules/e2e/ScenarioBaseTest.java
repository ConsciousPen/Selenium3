package aaa.modules.e2e;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingInstallmentsScheduleVerifier;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingAccountPoliciesTable;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;

public class ScenarioBaseTest extends BaseTest {
	protected static Logger log = LoggerFactory.getLogger(ScenarioBaseTest.class);

	protected String policyNum;

	protected void generateAndCheckBill(LocalDateTime installmentDate, ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDate, null, softly);
	}

	protected void generateAndCheckBill(LocalDateTime installmentDate, LocalDateTime effectiveDate, ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDate, effectiveDate, BillingHelper.DZERO, softly);
	}

	protected void generateAndCheckBill(LocalDateTime installmentDate, LocalDateTime effectiveDate, Dollar pligaOrMvleFee, ETCSCoreSoftAssertions softly) {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDate, billGenDate, effectiveDate, pligaOrMvleFee);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void payAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingPaymentsAndTransactionsVerifier().verifyAutoPaymentGenerated(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}

	protected void cancelPolicy(LocalDateTime installmentDueDate) {
		LocalDateTime cDate = getTimePoints().getCancellationDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(cDate);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_CANCELLED);
	}

	protected void verifyRenewOfferGenerated(List<LocalDateTime> installmentDates, ETCSCoreSoftAssertions softly) {
		BillingSummaryPage.showPriorTerms();

		for (int i = 1; i < installmentDates.size(); i++) { // Do not include Deposit bill
			if (!installmentDates.get(i).plusYears(1).isEqual(TimeSetterUtil.getInstance().parse("02/28/2020", DateTimeUtils.MM_DD_YYYY))) { //skip verification for last days February of leap year
				new BillingInstallmentsScheduleVerifier(softly).setDescription(BillingConstants.InstallmentDescription.INSTALLMENT)
						.setInstallmentDueDate(installmentDates.get(i).plusYears(1)).verifyPresent();
			}
		}
		if (!getState().equals(Constants.States.CA)) {
			new BillingBillsAndStatementsVerifier(softly).setType(BillingConstants.BillsAndStatementsType.OFFER).verifyPresent(false);
		}
	}

	protected void verifyRenewalOfferPaymentAmount(LocalDateTime expirationDate, LocalDateTime renewOfferDate, LocalDateTime billGenDate, Integer installmentsCount) {
		verifyRenewalOfferPaymentAmount(expirationDate, renewOfferDate, billGenDate, BillingHelper.DZERO, installmentsCount);
	}

	/**
	 * @param expirationDate
	 *            - original policy expiration date
	 * @param renewOfferDate
	 *            - Renew generate offer date
	 * @param billGenDate
	 *            - Bill generation date
	 * @param pligaOrMvleFee
	 * 			  - PLIGA or MVLE Fee amount(applicable for NJ and NY states only, for other states provide new Dollar(0) or use overloaded method without this argument)
	 * @param installmentsCount
	 *            : MONTHLY_STANDARD or ELEVEN_PAY: 11 installments QUARTERLY: 4
	 *            installments SEMI_ANNUAL: 2 installments PAY_IN_FULL or
	 *            ANNUAL: 1 installment
	 */
	protected void verifyRenewalOfferPaymentAmount(LocalDateTime expirationDate, LocalDateTime renewOfferDate, LocalDateTime billGenDate, Dollar pligaOrMvleFee,
		Integer installmentsCount) {
		BillingSummaryPage.showPriorTerms();
		String policyNum = BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.POLICY_NUM).getValue();
		Dollar fullAmount = BillingHelper.getPolicyRenewalProposalSum(renewOfferDate, policyNum);
		Dollar fee = BillingHelper.getFeesValue(billGenDate);
		Dollar previousTermMinDueAmount = new Dollar(0);
		Dollar renewalTermTotalPaid = new Dollar(0);
		if (BillingSummaryPage.tableBillingAccountPolicies.getRow(2).isPresent()) {
			previousTermMinDueAmount = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(BillingAccountPoliciesTable.MIN_DUE).getValue());
			renewalTermTotalPaid = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.TOTAL_PAID).getValue());
		}
		
		Dollar expOffer = BillingHelper.calculateFirstInstallmentAmount(fullAmount, installmentsCount).add(fee).add(pligaOrMvleFee).add(previousTermMinDueAmount).subtract(renewalTermTotalPaid);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(expirationDate).setMinDue(expOffer).verifyPresent();
	}
	
	/**
	 * @param intallmentDate
	 *            - installment due date
	 * @param billGenDate
	 *            - Bill generation date
	 *
	 *      Verify Renewal Offer payment amount using Installment amount due. 
	 *      Used in separate case: current installment is less then following
	 */
	protected void verifyRenewalOfferPaymentAmountByIntallmentAmount(LocalDateTime intallmentDate, LocalDateTime billGenDate) {
		 verifyRenewalOfferPaymentAmountByIntallmentAmount(intallmentDate, billGenDate, new Dollar(0));
	}
	
	/**
	 * @param intallmentDate
	 *            - installment due date
	 * @param billGenDate
	 *            - Bill generation date
	 * @param correctionAmount
	 *            - Amount needed for correction of base amount            
	 *
	 *      Verify Renewal Offer payment amount using Installment amount due. 
	 *      Used in separate case: necessary update amount manually to simplify calculation
	 */
	protected void verifyRenewalOfferPaymentAmountByIntallmentAmount(LocalDateTime intallmentDate, LocalDateTime billGenDate, Dollar correctionAmount) {
			BillingSummaryPage.showPriorTerms();
			Dollar previousTermMinDueAmount = new Dollar(0);
			if (BillingSummaryPage.tableBillingAccountPolicies.getRow(2).isPresent()) {
				previousTermMinDueAmount = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(BillingAccountPoliciesTable.MIN_DUE).getValue());
			}
			Dollar fee = BillingHelper.getFeesValue(billGenDate);
			Dollar expOffer = BillingHelper.getInstallmentDueByDueDate(intallmentDate).add(fee).add(previousTermMinDueAmount).add(correctionAmount);
			if (getState().equals(Constants.States.CA)) {
				new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.OFFER).setDueDate(intallmentDate).setMinDue(expOffer).verifyPresent();
			} else {
				new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(intallmentDate).setMinDue(expOffer).verifyPresent();
			}
			
	}


	/**
	 * Same as
	 * {@link #verifyRenewalOfferPaymentAmount(java.time.LocalDateTime, java.time.LocalDateTime, java.time.LocalDateTime, java.lang.Integer)}
	 */
	protected void verifyCaRenewalOfferPaymentAmount(LocalDateTime expirationDate, LocalDateTime renewOfferDate, Integer installmentsCount, ETCSCoreSoftAssertions softly) {
		BillingSummaryPage.showPriorTerms();
		new BillingPaymentsAndTransactionsVerifier(softly).setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
			.verifyPresent();

		String policyNum = BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.POLICY_NUM).getValue();
		Dollar fullAmount = BillingHelper.getPolicyRenewalProposalSum(renewOfferDate, policyNum);
		Dollar fee = BillingHelper.getFeesValue(renewOfferDate);

		Dollar expOffer = BillingHelper.calculateFirstInstallmentAmount(fullAmount, installmentsCount).add(fee);
		new BillingBillsAndStatementsVerifier(softly).setType(BillingConstants.BillsAndStatementsType.OFFER).setDueDate(expirationDate).setMinDue(expOffer)
			.verifyPresent();
	}

	protected void verifyRenewPremiumNotice(LocalDateTime renewDate, LocalDateTime billGenerationDate) {
		verifyRenewPremiumNotice(renewDate, billGenerationDate, BillingHelper.DZERO);
	}

	protected void verifyRenewPremiumNotice(LocalDateTime renewDate, LocalDateTime billGenerationDate, Dollar pligaOrMvleFee) {
		BillingSummaryPage.showPriorTerms();
		Dollar billAmount = BillingHelper.getInstallmentDueByDueDate(renewDate).add(BillingHelper.getFeesValue(billGenerationDate).add(pligaOrMvleFee));
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(renewDate);
		// TODO Check whu there are no verifications for KY and WV
		// if (!BaseTest.getState().equals(Constants.States.KY) &&
		// !BaseTest.getState().equals(Constants.States.WV)) {
		new BillingBillsAndStatementsVerifier().setMinDue(billAmount).verifyRowWithDueDate(renewDate);
		// }
	}

	protected boolean verifyPligaOrMvleFee(LocalDateTime transactionDate) {
		return verifyPligaOrMvleFee(transactionDate, BillingConstants.PolicyTerm.ANNUAL, 0);
	}

	protected boolean verifyPligaOrMvleFee(LocalDateTime transactionDate, String policyTerm, int numberOfVehiclesExceptTrailers) {
		Dollar expectedFee = getPligaOrMvleFee(null, transactionDate, policyTerm, numberOfVehiclesExceptTrailers);
		boolean isFeePresent = false;

		if (!expectedFee.isZero()) {
			if (getState().equals(Constants.States.NJ)) {
				new BillingPaymentsAndTransactionsVerifier().verifyPligaFee(transactionDate, expectedFee);
				isFeePresent = true;
			} else if (isMvleFeeApplicable()) {
				new BillingPaymentsAndTransactionsVerifier().verifyMVLEFee(transactionDate, expectedFee);
				isFeePresent = true;
			}
		}

		if ((getState().equals(Constants.States.NJ) || isMvleFeeApplicable()) && expectedFee.isZero()) {
			log.warn("PLIGA or MVLE Fee is applicable but expected value is $0, verification in \"Payments & Other Transactions\" table is skipped. Adjust your test data if you don't want to skip such verification.");
		}
		return isFeePresent;
	}

	protected Dollar getPligaOrMvleFee(LocalDateTime transactionDate) {
		return getPligaOrMvleFee(null, transactionDate);
	}

	protected Dollar getPligaOrMvleFee(String policyNumber, LocalDateTime transactionDate) {
		return getPligaOrMvleFee(policyNumber, transactionDate, BillingConstants.PolicyTerm.ANNUAL, 1);
	}

	protected Dollar getPligaOrMvleFee(String policyNumber, LocalDateTime transactionDate, String policyTerm, int numberOfVehiclesExceptTrailers) {
		Dollar expectedPligaOrMvleFee = BillingHelper.DZERO;
		if (transactionDate == null) {
			log.warn("Premium transaction date is null, assume PLIGA or MVLE Fee should be $0");
			return expectedPligaOrMvleFee;
		}
		if (getState().equals(Constants.States.NJ)) {
			goToBillingPage(policyNumber);
			expectedPligaOrMvleFee = BillingHelper.calculatePligaFee(transactionDate);
		} else if (isMvleFeeApplicable()) {
			goToBillingPage(policyNumber);
			expectedPligaOrMvleFee = BillingHelper.calculateMvleFee(policyTerm, numberOfVehiclesExceptTrailers);
		}
		return expectedPligaOrMvleFee;
	}

	protected String getPolicyTerm(TestData td) {
		if (isMvleFeeApplicable()) {
			return td.getTestData(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel()).getValue(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel());
		}
		return BillingConstants.PolicyTerm.ANNUAL;
	}

	protected int getVehiclesNumber(TestData td) {
		if (isMvleFeeApplicable()) {
			// TODO-dchubkov: exclude trailers from list of vehicles
			return td.getTestDataList(new VehicleTab().getMetaKey()).size();
		}
		return 0;
	}

	protected boolean isMvleFeeApplicable() {
		return getState().equals(Constants.States.NY) && getPolicyType().equals(PolicyType.AUTO_SS);
	}

	protected void goToBillingPage(String policyNumber) {
		if (policyNumber != null && !BillingSummaryPage.isVisible()) {
			mainApp().open();
			SearchPage.openBilling(policyNumber);
		}
	}
}
