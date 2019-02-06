package aaa.modules.e2e.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.*;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;

public class Scenario6 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;

	protected List<LocalDateTime> installmentDueDates;
	protected List<Dollar> installmentsAmounts;
	protected Dollar installmentAmount;
	protected Dollar totalDue;
	protected Dollar minDue;
	protected Dollar endorsementAmount;
	protected Dollar installmentsSum = new Dollar(0);

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

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		installmentDueDates = BillingHelper.getInstallmentDueDates();
		assertThat(installmentDueDates.size()).as("Billing Installments count for Quarterly payment plan").isEqualTo(installmentsCount);
		installmentAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1));

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	protected void generateFirstBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(1), softly);

		totalDue = BillingSummaryPage.getTotalDue();
		minDue = BillingSummaryPage.getMinimumDue();
		installmentsAmounts = BillingHelper.getInstallmentDues();
	}

	// TODO Check: Removed according to 20427:US CL GD Generate Premium Due Notice v2.0. Form has been renamed to AHIBXX
	protected void verifyFormAHIBXX() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AHIBXX);
	}

	protected void endorsePolicy() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(endorsementTD.adjust(getTestSpecificTD("TestData_EndorsementRP")));
		PolicyHelper.verifyEndorsementIsCreated();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(TimeSetterUtil.getInstance().getPhaseStartTime()).setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason).verifyPresent();
		endorsementAmount = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, reason).getCell(
			BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());

		// Min due on General info section is reduced after RP Endorse
		new Dollar(BillingSummaryPage.getMinimumDue()).verify.lessThan(minDue);

		// Min due on Billing Account Policies section is reduced after RP Endorse
		new Dollar(BillingHelper.getPolicyMinimumDueAmount(policyNum)).verify.lessThan(minDue);

		// Min due on Bills & Statements section is NOT changed after RP Endorse
		new Dollar(BillingHelper.getBillMinDueAmount(installmentDueDates.get(1), BillsAndStatementsType.BILL)).verify.equals(minDue);

		// Total due on General info section is reduced after RP Endorse
		new Dollar(BillingSummaryPage.getTotalDue()).verify.lessThan(totalDue);

		// Total due on Billing Account Policies section is reduced after RP Endorse
		new Dollar(BillingHelper.getPolicyTotalDueAmount(policyNum)).verify.lessThan(totalDue);

		// Total due on Bills & Statements section is NOT changed after RP Endorse
		new Dollar(BillingHelper.getBillTotalDueAmount(installmentDueDates.get(1), BillsAndStatementsType.BILL)).verify.equals(totalDue);

		// TODO
		// Replace with verify.less if 42369 Defect will be fixed
		for (int i = 2; i < installmentsAmounts.size(); i++) {
			if (getState().equals(States.KY)) {
				BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(i)).verify.moreThan(installmentsAmounts.get(i)); // include tax
			} else {
				BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(i)).verify.equals(installmentsAmounts.get(i));
			}
		}
	}

	protected void payFirstBill() {
		payCashAndCheckBill(installmentDueDates.get(1));
	}

	protected void generateCancellNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(BatchJob.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(PolicyFlag.DEFAULT).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	protected void generateSecondBill() {
		LocalDateTime genDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(genDate);
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar billAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(2));
		billAmount = billAmount.add(BillingHelper.getFeesValue(getTimePoints().getBillGenerationDate(installmentDueDates.get(2))));
		new BillingBillsAndStatementsVerifier().setDueDate(installmentDueDates.get(2)).setType(BillsAndStatementsType.BILL).setMinDue(billAmount).verifyPresent();
	}

	protected void paySecondBill() {
		payCashAndCheckBill(installmentDueDates.get(2));
	}

	protected void generateThirdBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(3), softly);
	}

	protected void payThirdBill() {
		payCashAndCheckBill(installmentDueDates.get(3));
	}

	protected void setDoNotRenewFlag() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		policy.doNotRenew().perform(getStateTestData(tdPolicy, "DoNotRenew", "TestData"));
		PolicySummaryPage.verifyDoNotRenewFlagPresent();
	}

	protected void renewalImageGeneration() {
		LocalDateTime policyExpirationDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateImage);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);

		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalNotGenerated(policyExpirationDateImage);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);
	}

	protected void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled(false);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);
	}

	protected void renewalOfferGeneration() {
		LocalDateTime policyExpirationDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateOffer);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled(false);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(policyExpirationDate).setType(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent(false);
	}

	protected void manualRenewPolicy(ETCSCoreSoftAssertions softly) {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		policy.manualRenew().perform(getStateTestData(tdPolicy, "ManualRenew", "TestData"));
		PolicySummaryPage.verifyManualRenewFlagPresent();

		policy.createRenewal(getStateTestData(tdPolicy, "Renew", "TestData_Today").adjust(getTestSpecificTD("TestData_ProposeRenew")));

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(installmentDueDates, softly);

		int leapDays = 0;
		for (LocalDateTime date : installmentDueDates) {
			if (date.plusYears(1).isEqual(TimeSetterUtil.getInstance().parse("02/28/2020", DateTimeUtils.MM_DD_YYYY))) {
				leapDays = policyEffectiveDate.getDayOfMonth() - 28 > 0 ? 1 : 0;
			}
			installmentsSum = installmentsSum.add(BillingHelper.getInstallmentDueByDueDate(date.plusYears(1).plusDays(leapDays)));
			leapDays = 0;
		}
		totalDue = BillingSummaryPage.getTotalDue();
	}

	protected void verifyFormAHR1XX_And_HSRNXX() {
		// DocGenHelper.verifyDocumentsGeneratedByJob(TimeSetterUtil.getInstance().getCurrentTime(), policyNum,
		// Arrays.asList(OnDemandDocuments.AHRBXX, OnDemandDocuments.HSRNXX));
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		//For CA document 61 5121 substitutes HSRNXX
		Documents document = getState().equals(States.CA) ? DocGenEnum.Documents._61_5121 : DocGenEnum.Documents.HSRNXX;
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AHRBXX, document);
	}

	protected void payRenewOffer() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(policyExpirationDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar overpayment = new Dollar(200);
		String billType = getState().equals(States.CA) ? BillsAndStatementsType.OFFER : BillsAndStatementsType.BILL;
		Dollar renewOfferAmount = BillingHelper.getBillMinDueAmount(policyExpirationDate, billType).add(overpayment);

		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), renewOfferAmount);

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);

		int leapDays = 0;
		Dollar renewInstallmentsSum = new Dollar(0);
		for (LocalDateTime date : installmentDueDates) {
			if (date.plusYears(1).isEqual(TimeSetterUtil.getInstance().parse("02/28/2020", DateTimeUtils.MM_DD_YYYY))) {
				leapDays = policyEffectiveDate.getDayOfMonth() - 28 > 0 ? 1 : 0;
			}
			renewInstallmentsSum = renewInstallmentsSum.add(BillingHelper.getInstallmentDueByDueDate(date.plusYears(1).plusDays(leapDays)));
			leapDays = 0;
		}

		installmentsSum.subtract(overpayment).verify.equals(renewInstallmentsSum);
		totalDue.subtract(renewOfferAmount).verify.equals(BillingSummaryPage.getTotalDue());
	}

	protected void updatePolicyStatus() {
		LocalDateTime date = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		if (getPolicyType().equals(PolicyType.HOME_CA_HO3) || getPolicyType().equals(PolicyType.HOME_SS_HO3))
			new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void automaticRefundNotGenerated() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRefundDate(getTimePoints().getBillDueDate(policyExpirationDate)));
		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob); //.refundGenerationJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.REFUND).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND).setReason(
			PaymentsAndOtherTransactionReason.OVERPAYMENT).verifyPresent(false);
	}

	private void payCashAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDate, BillingBillsAndStatmentsTable.MINIMUM_DUE));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}
}
