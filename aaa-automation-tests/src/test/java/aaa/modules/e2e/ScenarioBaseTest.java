package aaa.modules.e2e;

import java.time.LocalDateTime;
import java.util.List;

import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingInstallmentsScheduleVerifier;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

public class ScenarioBaseTest extends BaseTest {

	protected String policyNum;

	protected void generateAndCheckBill(LocalDateTime installmentDate) {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDate, billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.verifyPresent();
	}

	protected void payAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
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
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
	}

	protected void verifyRenewOfferGenerated(LocalDateTime policyExpDate, List<LocalDateTime> installmentDates) {
		BillingSummaryPage.showPriorTerms();

		CustomAssert.enableSoftMode();
		for (int i = 1; i < installmentDates.size(); i++) { // Do not include
															// Deposit bill
			new BillingInstallmentsScheduleVerifier().setDescription(BillingConstants.InstallmentDescription.INSTALLMENT)
					.setInstallmentDueDate(installmentDates.get(i).plusYears(1)).verifyPresent();
		}
		if (!getState().equals(Constants.States.CA)) {
			new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.OFFER).verifyPresent(false);
		}
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @param expirationDate
	 *            - original policy expiration date
	 * @param renewOfferDate
	 *            - Renew generate offer date
	 * @param billGenDate
	 *            - Bill generation date
	 * @param installmentsCount
	 *            : MONTHLY_STANDARD or ELEVEN_PAY: 11 installments QUARTERLY: 4
	 *            installments SEMI_ANNUAL: 2 installments PAY_IN_FULL or
	 *            ANNUAL: 1 installment
	 */
	protected void verifyRenewalOfferPaymentAmount(LocalDateTime expirationDate, LocalDateTime renewOfferDate, LocalDateTime billGenDate,
			Integer installmentsCount) {
		BillingSummaryPage.showPriorTerms();
		Dollar fullAmount = BillingHelper.getPolicyRenewalProposalSum(renewOfferDate);
		Dollar fee = BillingHelper.getFeesValue(billGenDate);

		Dollar expOffer = BillingHelper.calculateFirstInstallmentAmount(fullAmount, installmentsCount).add(fee);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setDueDate(expirationDate).setMinDue(expOffer)
				.verifyPresent();
	}

	/**
	 * Same as
	 * {@link #verifyRenewalOfferPaymentAmount(java.time.LocalDateTime, java.time.LocalDateTime, java.time.LocalDateTime, java.lang.Integer)}
	 */
	protected void verifyCaRenewalOfferPaymentAmount(LocalDateTime expirationDate, LocalDateTime renewOfferDate, Integer installmentsCount) {
		BillingSummaryPage.showPriorTerms();
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.verifyPresent();

		Dollar fullAmount = BillingHelper.getPolicyRenewalProposalSum(renewOfferDate);
		Dollar fee = BillingHelper.getFeesValue(renewOfferDate);

		Dollar expOffer = BillingHelper.calculateFirstInstallmentAmount(fullAmount, installmentsCount).add(fee);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.OFFER).setDueDate(expirationDate).setMinDue(expOffer)
				.verifyPresent();
	}

	protected void verifyRenewPremiumNotice(LocalDateTime renewDate, LocalDateTime billGenerationDate) {
		BillingSummaryPage.showPriorTerms();
		Dollar billAmount = BillingHelper.getInstallmentDueByDueDate(renewDate).add(BillingHelper.getFeesValue(billGenerationDate));
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(renewDate);
		// TODO Check whu there are no verifications for KY and WV
		// if (!BaseTest.getState().equals(Constants.States.KY) &&
		// !BaseTest.getState().equals(Constants.States.WV)) {
		new BillingBillsAndStatementsVerifier().setMinDue(billAmount).verifyRowWithDueDate(renewDate);
		// }
	}
}
