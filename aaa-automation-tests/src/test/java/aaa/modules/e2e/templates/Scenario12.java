package aaa.modules.e2e.templates;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.*;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.MyWorkConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

//import toolkit.verification.CustomAssert;

public class Scenario12 extends ScenarioBaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime paymentDate;	
	protected Dollar dueAmount = new Dollar(0);
	
	protected LocalDateTime policyExpirationDate_FirstRenewal; 
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 2; 
	protected List<LocalDateTime> installmentDueDates_FirstRenewal;
	protected int installmentsCount_FirstRenewal = 4; 
	protected List<LocalDateTime> installmentDueDates_SecondRenewal;
	protected int installmentsCount_SecondRenewal = 2; 
	
	protected String policyTerm;
	protected Integer totalVehiclesNumber;
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();		
		mainApp().open();		
		createCustomerIndividual();	
		
		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		//PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Semi-Annual payment plan", installmentsCount, installmentDueDates.size());
		CustomAssertions.assertThat(installmentDueDates.size()).as("Billing Installments count for Semi-Annual payment plan").isEqualTo(installmentsCount);

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	protected void generateFirstBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(1), softly);
	}

	protected void payFirstBill() {
		payCashAndCheckBill(installmentDueDates.get(1));
	}

	protected void generateCancelNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.CANCEL_NOTICE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE).verifyRowWithDueDate(
			getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)));
	}

	protected void generateCancellation() {
		LocalDateTime cDate = getTimePoints().getCancellationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cDate);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	protected void createRemittanceFile() {
		LocalDateTime date = getTimePoints().getCancellationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(date.plusDays(41).with(DateTimeUtils.closestFutureWorkingDay));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar cnAmount = BillingHelper.getBillDueAmount(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)),
				BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE);
		File remitanceFile = RemittancePaymentsHelper.createRemittanceFile(getState(), policyNum, cnAmount, BillingConstants.ExternalPaymentSystem.REGLKBX);
		RemittancePaymentsHelper.copyRemittanceFileToServer(remitanceFile);
	}

	protected void payCancellationNoticeByRemittance() {
		paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		JobUtils.executeJob(Jobs.aaaRemittanceFeedAsyncBatchReceiveJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar cnAmount = BillingHelper.getBillDueAmount(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)),
				BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setAmount(cnAmount.negate())
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REGULUS_LOCKBOX)
				.verifyPresent();

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT)
				.verifyPresent(false);

		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_CANCELLED).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	protected void verifyTaskCreated() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonTasks.click();
		//MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_NAME, "Payment received for cancelled policy for possible rewrite.").verify.present();
		CustomAssertions.assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME,
				"Payment received for cancelled policy for possible rewrite.")).isPresent();
		MyWorkSummaryPage.buttonCancel.click();
	}

	protected void policyReinstatement() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData_ReinstateWithLapse"));
		//PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(TimeSetterUtil.getInstance().getCurrentTime())
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT).verifyPresent();

		if (!getState().equals(Constants.States.CA)) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(TimeSetterUtil.getInstance().getCurrentTime())
					.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_FEE)
			.setAmount(new Dollar(20)).verifyPresent();
		}

		String totalDue = BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingConstants.BillingGeneralInformationTable.TOTAL_DUE).getValue();
		log.info("Total Due is : "+ totalDue);
		if (totalDue.equals("$0.00")) {
			dueAmount = new Dollar(0);
		}
		else {
			dueAmount = new Dollar(totalDue.substring(1, totalDue.length() - 1));
		}
	}

	protected void generateRefund() {
		LocalDateTime refundDate = getTimePoints().getRefundDate(paymentDate);
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		if (dueAmount.equals(new Dollar(0))) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refundDate)
					.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND).verifyPresent(false);
		}
		else {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refundDate)
				.setAmount(dueAmount)
					.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
					.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED)
					.setReason(BillingConstants.PaymentsAndOtherTransactionReason.OVERPAYMENT).verifyPresent();
		}
	}
	
	protected boolean isRefundAfterImageGeneration() {
		LocalDateTime refundDate = getTimePoints().getRefundDate(paymentDate);
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		return refundDate.isAfter(renewDateImage);
	}

	protected void renewalImageGeneration() {
		renewalImageGeneration(policyExpirationDate);
	}

	protected void renewalPreviewGeneration() {
		renewalPreviewGeneration(policyExpirationDate);
	}

	protected void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.buttonRenewals.verify.enabled();
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Semi-Annual (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(installmentDueDates, softly);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDateOffer)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (verifyPligaOrMvleFee(renewDateOffer, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewDateOffer;
		}
	}

	//Skip this step for CA
	protected void generateRenewalBill() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Semi-Annual (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billGenDate, pligaOrMvleFee, installmentsCount);

		verifyRenewPremiumNotice(policyExpirationDate, billGenDate, pligaOrMvleFee);
	}

	protected void changePaymentPlan() {
		LocalDateTime dueDate = getTimePoints().getBillGenerationDate(policyExpirationDate).plusHours(1);
		TimeSetterUtil.getInstance().nextPhase(dueDate);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		TestData renewalTD = getTestSpecificTD("TestData_FirstRenewal");
		changePaymentPlan(renewalTD);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Semi-Annual").verifyPresent();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)").verifyPresent();

		BillingSummaryPage.buttonHidePriorTerms.click();
		installmentDueDates_FirstRenewal = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Quarterly (Renewal) payment plan", installmentsCount_FirstRenewal, installmentDueDates_FirstRenewal.size());
		CustomAssertions.assertThat(installmentDueDates_FirstRenewal.size()).as("Billing Installments count for Quarterly (Renewal) payment plan").isEqualTo(installmentsCount_FirstRenewal);

	}

	protected void changePaymentPlanForCA() {
		LocalDateTime renewOfferDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate).plusHours(1);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Semi-Annual (Renewal)").verifyPresent();

		billingAccount.changePaymentPlan().perform(tdBilling.getTestData("ChangePaymentPlan", "TestData_ChangePaymentPlanToQuarterly"));

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Semi-Annual").verifyPresent();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)").verifyPresent();
		BillingSummaryPage.buttonHidePriorTerms.click();

		installmentDueDates_FirstRenewal = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Quarterly (Renewal) payment plan", installmentsCount_FirstRenewal, installmentDueDates_FirstRenewal.size());
		CustomAssertions.assertThat(installmentDueDates_FirstRenewal.size()).as("Billing Installments count for Quarterly (Renewal) payment plan").isEqualTo(installmentsCount_FirstRenewal);

		new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.OFFER).verifyPresent();
		//new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER).verifyPresent();

		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(renewOfferDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason("Non EFT Installment Fee").verifyPresent();

		//new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).setSubtypeReason("Non EFT Installment Fee Waived").verifyPresent();
	}

	protected void enableAutoPay() {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_EnableAutopay"));
		billingAccount.update().start();
		//new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(true);
		CustomAssertions.assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(true);
		Tab.buttonCancel.click();

		//verify payment plans are not changed
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Semi-Annual").verifyPresent();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)").verifyPresent();
		BillingSummaryPage.buttonHidePriorTerms.click();
	}

	protected void payRenewalBill(){
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate.plusHours(1));
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		//verify recurring payment is not generated
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(DateTimeUtils.getCurrentDateTime())
			.setAmount(minDue.negate())
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).verifyPresent(false);

		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());

		//verify payment plans are not changed
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPaymentPlan("Quarterly (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);
		BillingSummaryPage.buttonHidePriorTerms.click();
	}

	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);

		SearchPage.openPolicy(policyNum, ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyExpirationDate_FirstRenewal = PolicySummaryPage.getExpirationDate();
	}
	
	protected void generateFirstBillOfFirstRenewal(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates_FirstRenewal.get(1), softly);
	}

	protected void generateSecondBillOfFirstRenewal(ETCSCoreSoftAssertions softly){
		generateAndCheckBill(installmentDueDates_FirstRenewal.get(2), softly);
	}

	protected void payFirstBillOfFirstRenewal() {
		payAndCheckBill(installmentDueDates_FirstRenewal.get(1));
	}
	
	protected void generateThirdBillOfFirstRenewal(ETCSCoreSoftAssertions softly){
		generateAndCheckBill(installmentDueDates_FirstRenewal.get(3), softly);
	}

	protected void paySecondBillOfFirstRenewal() {
		payAndCheckBill(installmentDueDates_FirstRenewal.get(2));
	}

	protected void renewalOfferGeneration_FirstRenewal(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate_FirstRenewal);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.buttonRenewals.verify.enabled();
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)")
				.verifyRowWithEffectiveDate(policyExpirationDate_FirstRenewal);
		verifyRenewOfferGenerated(installmentDueDates_FirstRenewal, softly);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDateOffer)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (verifyPligaOrMvleFee(renewDateOffer, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewDateOffer;
		}
	}

	protected void payThirdBillOfFirstRenewal() {
		payAndCheckBill(installmentDueDates_FirstRenewal.get(3));
	}

	protected void renewalImageGeneration_FirstRenewal() {
		renewalImageGeneration(policyExpirationDate_FirstRenewal);
	}

	protected void renewalPreviewGeneration_FirstRenewal() {
		renewalPreviewGeneration(policyExpirationDate_FirstRenewal);
	}

	protected void changePaymentPlan_FirstRenewal() {
		mainApp().open();
		SearchPage.openPolicy(policyNum, ProductConstants.PolicyStatus.POLICY_ACTIVE);

		TestData renewalTD = getTestSpecificTD("TestData_SecondRenewal");
		changePaymentPlan(renewalTD);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Semi-Annual (Renewal)")
				.verifyRowWithEffectiveDate(policyExpirationDate_FirstRenewal);


		BillingSummaryPage.buttonHidePriorTerms.click();
		installmentDueDates_SecondRenewal = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Semi-Annual (Renewal) payment plan", installmentsCount_SecondRenewal, installmentDueDates_SecondRenewal.size());
		CustomAssertions.assertThat(installmentDueDates_SecondRenewal.size()).as("Billing Installments count for Semi-Annual (Renewal) payment plan").isEqualTo(installmentsCount_SecondRenewal);
	}

	protected void changePaymentPlanForCA_FirstRenewal() {
		LocalDateTime renewOfferDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate_FirstRenewal);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		billingAccount.changePaymentPlan().perform(tdBilling.getTestData("ChangePaymentPlan", "TestData_ChangePaymentPlanToSemiAnnual"));

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).setPaymentPlan("Semi-Annual").verifyPresent();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly (Renewal)").verifyPresent();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Semi-Annual (Renewal)").verifyPresent();
		BillingSummaryPage.buttonHidePriorTerms.click();

		installmentDueDates_SecondRenewal = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Semi-Annual (Renewal) payment plan",	installmentsCount_SecondRenewal, installmentDueDates_SecondRenewal.size());
		CustomAssertions.assertThat(installmentDueDates_SecondRenewal.size()).as("Billing Installments count for Semi-Annual (Renewal) payment plan").isEqualTo(installmentsCount_SecondRenewal);

		//verifyRenewalOfferPaymentAmount(policyExpirationDate, renewOfferDate, billGenDate, installmentsCount_FirstRenewal);

		new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.OFFER).verifyPresent();
		//new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER).verifyPresent();

		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(renewOfferDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason("EFT Installment Fee - Credit Card").verifyPresent();

		//new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).setSubtypeReason("EFT Installment Fee - Credit Card Waived").verifyPresent();
	}

	protected void generateRenewalBill_FirstRenewal() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate_FirstRenewal);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Semi-Annual (Renewal)")
				.verifyRowWithEffectiveDate(policyExpirationDate_FirstRenewal);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);

		verifyRenewPremiumNotice(policyExpirationDate_FirstRenewal, billDate, pligaOrMvleFee);
	}

	protected void payRenewalBill_FirstRenewal() {
		payAndCheckBill(policyExpirationDate_FirstRenewal);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate_FirstRenewal);
	}

	protected void updatePolicyStatus_FirstRenewal() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate_FirstRenewal);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyExpirationDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate_FirstRenewal);

		//SearchPage.openPolicy(policyNum, PolicyStatus.POLICY_ACTIVE);
		//policyExpirationDate_SecondRenewal = PolicySummaryPage.getExpirationDate();
	}

	protected void generateFirstBillOfSecondRenewal(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates_SecondRenewal.get(1), softly);
	}

	private void changePaymentPlan(TestData td) {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		if (getPolicyType().isAutoPolicy()) {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			new PremiumAndCoveragesTab().fillTab(td);
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			new DocumentsAndBindTab().fillTab(td);
			new DocumentsAndBindTab().submitTab();
		} else if (getPolicyType().equals(PolicyType.PUP)) {
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
			new PremiumAndCoveragesQuoteTab().fillTab(td);
			new PremiumAndCoveragesQuoteTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
			new aaa.main.modules.policy.pup.defaulttabs.BindTab().submitTab();
		} else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
			new PremiumsAndCoveragesQuoteTab().fillTab(td, true);
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().submitTab();
		}
	}

	protected void payFirstBillOfSecondRenewal() {
		payAndCheckBill(installmentDueDates_SecondRenewal.get(1));
	}

	private void renewalImageGeneration(LocalDateTime policyExpirationDate) {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	private void renewalPreviewGeneration(LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.buttonRenewals.verify.enabled();
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	private void payCashAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}
}
