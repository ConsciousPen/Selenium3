package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.BillingPendingTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsReason;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsStatus;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsSubtype;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsType;
import aaa.main.enums.BillingConstants.BillsAndStatementsType;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class Scenario11 extends ScenarioBaseTest { 
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	protected String[] endorsementReasonDataKeys;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 1;
	
	protected Dollar endorsementAmount; 
	
	protected LocalDateTime offCycleBillDueDate; 		//DD2 

	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();		
		mainApp().open();
		
		//createCustomerIndividual();	
		//policyNum = createPolicy(policyCreationTD); 
		policyNum = "CAAS933622643";
		SearchPage.openPolicy(policyNum);
		
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Quarterly payment plan", installmentsCount, installmentDueDates.size()); 
		
		offCycleBillDueDate = policyEffectiveDate.plusMonths(2);		
	}
	
	protected void endorsePolicy() {
		//DD1
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(1).with(DateTimeUtils.closestFutureWorkingDay)); 
		
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement").adjust(endorsementTD));
		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		PolicyHelper.verifyEndorsementIsCreated();
		
		endorsementAmount = PolicySummaryPage.TransactionHistory.getTranPremium(); 

		// Endorsement transaction displaing on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setTotalDue(endorsementAmount).verifyPresent();
		
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate).setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(reason).verifyPresent();
	}
	
	protected void generateOffCycleBill() {
		//DD2-20 
		LocalDateTime dueDate = getTimePoints().getOffcycleBillGenerationDate(offCycleBillDueDate);
		TimeSetterUtil.getInstance().nextPhase(dueDate);
		JobUtils.executeJob(Jobs.offCycleBillingInvoiceAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		//endorsementDue = BillingSummaryPage.getTotalDue();
		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).setMinDue(endorsementAmount)
				.setPastDueZero().verifyRowWithDueDate(offCycleBillDueDate);
	}
	
	protected void payOffCycleBill() {
		//DD2
		TimeSetterUtil.getInstance().nextPhase(offCycleBillDueDate);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar dueAmount = new Dollar(endorsementAmount); 
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), dueAmount);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), dueAmount.negate());		
	}
	
	protected void renewalImageGeneration() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	protected void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration() {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
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
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDateOffer)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();
		/*
		if (verifyPligaOrMvleFee(renewDateOffer, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewDateOffer;
		}
		*/
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

	protected void payRenewalOfferNotInFullAmount(Dollar toleranceAmount) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
		
		String billType = getState().equals(Constants.States.CA) ? BillsAndStatementsType.OFFER : BillsAndStatementsType.BILL;
		Dollar offerAmount = BillingHelper.getBillMinDueAmount(policyExpirationDate, billType);
		
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), offerAmount.subtract(toleranceAmount));
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
	}
	
	protected void payRenewalOfferInFullAmount(Dollar toleranceAmount) {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(20));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), toleranceAmount); 
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);		
	}

	protected void makeOverpayment() {
		//2DD1
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusMonths(1));
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		//Dollar dueAmount = new Dollar(endorsementAmount).add(100);
		Dollar dueAmount = new Dollar(100);
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), dueAmount);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), dueAmount.negate());
	}
	
	protected void cancellationPolicy() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	protected void refundGeneration() {		
		//2DD1+10 
		LocalDateTime refundDueDate = policyExpirationDate.plusMonths(1).plusDays(10);
		TimeSetterUtil.getInstance().nextPhase(refundDueDate);		
		JobUtils.executeJob(Jobs.refundGenerationJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		HashMap<String, String> query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, policyExpirationDate.plusMonths(1).format(DateTimeUtils.MM_DD_YYYY));
		query.put(BillingPaymentsAndOtherTransactionsTable.POLICY, policyNum);
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PREMIUM); 
		query.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM);

		String cancelAmount = 
				BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue().toString();
		log.info("cancelAmount is: "+cancelAmount);
		Dollar refundAmount = new Dollar(cancelAmount.substring(1, cancelAmount.length()-1));
		log.info("refundAmount is: "+refundAmount);
		
		new BillingPendingTransactionsVerifier().setTransactionDate(refundDueDate)
			.setAmount(refundAmount.add(100))
			.setType(BillingPendingTransactionsType.REFUND)
			.setSubtypeReason(BillingPendingTransactionsSubtype.AUTOMATED_REFUND)
			.setReason(BillingPendingTransactionsReason.OVERPAYMENT)
			.setStatus(BillingPendingTransactionsStatus.PENDING).verifyPresent();
		
		BillingHelper.approvePendingTransaction(refundDueDate, BillingPendingTransactionsType.REFUND); 
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refundDueDate)
			.setAmount(refundAmount.add(100))
			.setType(PaymentsAndOtherTransactionType.REFUND)
			.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
			.setReason(PaymentsAndOtherTransactionReason.OVERPAYMENT)
			.setStatus(PaymentsAndOtherTransactionStatus.APPROVED).verifyPresent(); 
		
	}
	
}
