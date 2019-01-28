package aaa.modules.e2e.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.BillingAccountPoliciesTable;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;

public class Scenario1 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;

	protected Dollar totalDue;
	protected List<LocalDateTime> installmentDueDates;
	protected Dollar installmentAmount;

	protected Dollar firstBillAmount;
	protected String[] endorsementReasonDataKeys;
	protected int installmentsCount = 4;

	protected String policyTerm;
	protected Integer totalVehiclesNumber;

	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();

		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);

		policyNum = createPolicy(policyCreationTD);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);

		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		totalDue = BillingSummaryPage.getTotalDue();
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		assertThat(installmentDueDates.size()).as("Billing Installments count for Quarterly payment plan").isEqualTo(installmentsCount);
		installmentAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1));

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	protected void generateFirstBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(1), softly);
		firstBillAmount = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingBillsAndStatmentsTable.MINIMUM_DUE));
	}

	protected void endorsePolicy() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		TestData endorsementTD = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		policy.endorse().performAndFill(endorsementTD);
		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		PolicyHelper.verifyEndorsementIsCreated();

		// Endorsement transaction displayed on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(transactionDate)
			.setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason).verifyPresent();

		// AP endorsement didn't increase Bill Amount (bill generated at TC2)
		Dollar bill = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingBillsAndStatmentsTable.MINIMUM_DUE));
		List<Dollar> installmentDues = BillingHelper.getInstallmentDues();

		// The installment schedule is recalculated starting with the Installment which doesn't yet have a bill
		bill.verify.equals(firstBillAmount);
		installmentAmount.verify.equals(installmentDues.get(1));
		installmentDues.get(2).verify.moreThan(installmentAmount);
		installmentDues.get(3).verify.moreThan(installmentAmount);

		Dollar totalDue1 = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		Dollar totalDue2 = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingBillsAndStatmentsTable.TOTAL_DUE));

		// "Total Due" field is updated to reflect AP amount 
		totalDue1.verify.moreThan(totalDue);
		totalDue2.verify.moreThan(totalDue);

		int vehiclesNumber = getVehiclesNumber(endorsementTD);
		if (verifyPligaOrMvleFee(transactionDate, policyTerm, vehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = transactionDate;
			totalVehiclesNumber += vehiclesNumber;
		}
	}

	protected void payFirstBill() {
		payAndCheckBill(installmentDueDates.get(1));
	}

	protected void generateSecondBill(ETCSCoreSoftAssertions softly) {
		Dollar getPligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, 0);
		generateAndCheckBill(installmentDueDates.get(2), policyEffectiveDate, getPligaOrMvleFee, softly);
	}

	protected void paySecondBill() {
		payAndCheckBill(installmentDueDates.get(2));
	}

	protected void generateThirdBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(3), softly);
	}

	protected void payThirdBill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	// TC09_Renewal_R_74
	protected void earlyRenewNotGenerated() {
		LocalDateTime beforeRenewDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate).minusDays(1);
		if ((getState().equals(Constants.States.MD) || getState().equals(Constants.States.NY))
			&& beforeRenewDate.isBefore(DateTimeUtils.getCurrentDateTime())) {
			log.info(String.format("Skipping Test. State is %s and Timepoint is before the current date", getState()));
		} else {
			TimeSetterUtil.getInstance().nextPhase(beforeRenewDate);
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
			HttpStub.executeAllBatches();
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicyHelper.verifyAutomatedRenewalNotGenerated(beforeRenewDate);
			PolicyHelper.verifyAutomatedRenewalNotGenerated(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		}
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewImageGenDate);
	}

	protected void renewalPreviewGeneration() {
		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		// TODO: investigate why whether it is a bug that status is "Gathering Info" but not "Premium Calculated"
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	// TC12_Renewal_R_35
	protected void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(installmentDueDates, softly);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
			.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount, softly);
		}

		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}
	}

	// Skip this step for CA
	protected void renewalPremiumNotice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);

		// TODO Renew premium verification was excluded, due to unexpected installment calculations
		// if (!getState().equals(Constants.States.KY) && !getState().equals(Constants.States.WV)) {
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billDate, pligaOrMvleFee, installmentsCount);
		// }
		verifyRenewPremiumNotice(policyExpirationDate, billDate, pligaOrMvleFee);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void payRenewalBill() {
		payAndCheckBill(policyExpirationDate);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		// TODO Renew premium verification was excluded, due to unexpected installment calculations
		// if (!getState().equals(Constants.States.KY) && !getState().equals(Constants.States.WV)) {
		// Dollar renewalAmount = BillingHelper.getPolicyRenewalProposalSum(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		Dollar renewalAmount = BillingHelper.getPolicyRenewalProposalSum(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), policyNum);
		Dollar firstInstallment = BillingHelper.calculateFirstInstallmentAmount(renewalAmount, installmentsCount);
		Dollar lastInstallment = BillingHelper.calculateLastInstallmentAmount(renewalAmount, installmentsCount);
		BillingHelper.getInstallmentDueByDueDate(policyExpirationDate).verify.equals(firstInstallment);
		BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1).plusYears(1)).verify.equals(lastInstallment);
		BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(2).plusYears(1)).verify.equals(lastInstallment);
		BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(3).plusYears(1)).verify.equals(lastInstallment);
		// }
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
}
