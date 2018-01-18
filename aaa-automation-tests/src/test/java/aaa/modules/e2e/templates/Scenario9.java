package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.BillsAndStatementsType;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class Scenario9 extends ScenarioBaseTest {
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;
	
	protected Dollar currentTermDueAmount;
	protected Dollar totalDue;
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 11;
	
	protected String[] endorsementReasonDataKeys;
	protected String policyTerm;
	protected Integer totalVehiclesNumber;
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();
		
		mainApp().open();
		
		createCustomerIndividual();	
		policyNum = createPolicy(policyCreationTD); 
		
		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);
		
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Monthly (Eleven Pay) payment plan", installmentsCount, installmentDueDates.size()); 
		
		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}
	
	protected void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}
	
	protected void payFirstBill() {
		payAndCheckBill(installmentDueDates.get(1));
	}
	
	protected void generateSecondBill() {
		generateAndCheckBill(installmentDueDates.get(2));
	}
	
	protected void paySecondBill() {
		payAndCheckBill(installmentDueDates.get(2));
	}
	
	protected void payNextSevenInstallments() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		Dollar totalDue = BillingSummaryPage.getTotalDue(); 
		Dollar lastInstallmentDue = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(10));
		Dollar amount = new Dollar(totalDue.subtract(lastInstallmentDue));
		
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), amount);
		
		BillingSummaryPage.getTotalDue().verify.equals(lastInstallmentDue);
		
		for (int i = 3; i < installmentsCount-1; i++) {
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(i)).verify.equals(new Dollar(0));
		}		
	}
	
	protected void verifyThirdBillNotGenerated() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(3));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(3)).verifyPresent(false);
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.FEE).setTransactionDate(billGenDate).verifyPresent(false);
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT).verify(1);
	}
	
	protected void verifyPaymentNotGenerated() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(3));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum); 
		
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.PAYMENT).setTransactionDate(billDueDate).verifyPresent(false);
	}
	
	protected void generateLastBill() {
		generateAndCheckBill(installmentDueDates.get(10)); 
	}
	 
	protected void payLastBill() {
		payAndCheckBill(installmentDueDates.get(10));
	}
	
	protected void removeAutoPay() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
		billingAccount.update().start();
		new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(false);
		Tab.buttonCancel.click();
	}

	protected boolean isBillGenDateAfterRenewImageGenDate() {
		LocalDateTime lastBillGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(10)); 
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate); 
		if (lastBillGenDate.isAfter(renewImageGenDate)) 
			return true;
		else 
			return false;
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
		/*
		if (getState().equals("MD") && DateTimeUtils.getCurrentDateTime().isAfter(renewPreviewGenDate)) {
			renewPreviewGenDate = DateTimeUtils.getCurrentDateTime();
		}
		*/
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}
	
	protected void renewalOfferGeneration() {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		verifyRenewOfferGenerated(installmentDueDates);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
			.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();
		
		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount);
		}
		
		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}
		
	}	

	protected void endorsementOnCurrentTerm(){
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		TestData endorsementTD = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData")); 
		policy.endorse().performAndFill(endorsementTD);
		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		PolicyHelper.verifyEndorsementIsCreated(); 
		
		currentTermDueAmount = PolicySummaryPage.TransactionHistory.getTranPremium(); 
		if (getState().equals(States.NJ)) {
			Dollar pligaFeeEndorse = BillingHelper.calculatePligaFee(transactionDate, currentTermDueAmount); 
			currentTermDueAmount = currentTermDueAmount.add(pligaFeeEndorse);
		}
		if (getState().equals(States.NY)) {
			currentTermDueAmount = currentTermDueAmount.add(10);
		}
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setTotalDue(currentTermDueAmount).verifyPresent();
		
		// Endorsement transaction displayed on billing in Payments & Other transactions section
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate)
			.setPolicy(policyNum)
			.setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason).verifyPresent();
	}
	
	protected void generateRenewalBill() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		
		Dollar minDueRenewTerm = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getColumn("Min. Due").getCell(1).getValue());
		//log.info("Min Due of current term is: " + minDueRenewTerm);
		//log.info("Min Due of previous term is: " + currentTermDueAmount); 
		
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL)
			.setDueDate(policyExpirationDate)
			.setMinDue(minDueRenewTerm.add(currentTermDueAmount)).verifyPresent();
	
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	
	}
	
	protected void dontPayRenewalBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setType(PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).verifyPresent(false);
	}

	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}
	
	protected void customerDeclineRenewal() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
	}
		
	protected void generateEarnedPremiumWriteOff() {
		//LocalDateTime earnedPremiumWriteOffDate = getTimePoints().getEarnedPremiumWriteOff(policyExpirationDate); 
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(60).with(DateTimeUtils.closestFutureWorkingDay)); 
		JobUtils.executeJob(Jobs.earnedPremiumWriteoffProcessingJob); 
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		HashMap<String, String> writeOffTransaction = new HashMap<>();
		writeOffTransaction.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
		writeOffTransaction.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.ADJUSTMENT);
		writeOffTransaction.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF); 
		writeOffTransaction.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, "(" + currentTermDueAmount.toString() + ")");
		
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(writeOffTransaction).verify.present();
	}
	
}
