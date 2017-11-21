package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

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
import aaa.main.enums.ProductConstants;
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
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import com.exigen.ipb.etcsa.utils.Dollar;

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
	
	protected Dollar endorseAmount1; 
	protected Dollar endorseAmount2;
	
	protected LocalDateTime offCycleBillDueDate1; 		 
	protected LocalDateTime offCycleBillDueDate2;		
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();		
		mainApp().open();
		
		createCustomerIndividual();	
		policyNum = createPolicy(policyCreationTD); 
		
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Annual (Pay In Full) payment plan", installmentsCount, installmentDueDates.size()); 
		
		offCycleBillDueDate1 = policyEffectiveDate.plusMonths(1);
		offCycleBillDueDate2 = policyEffectiveDate.plusMonths(2);		
	}
	
	protected void makeFistEndorsement() {		
		//DD0+5
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(5));
		
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement1").adjust(endorsementTD));
		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		PolicyHelper.verifyEndorsementIsCreated();
		
		endorseAmount1 = PolicySummaryPage.TransactionHistory.getTranPremium(); 

		// Endorsement transaction displaing on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setTotalDue(endorseAmount1).verifyPresent();
		
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate).setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(reason).verifyPresent();
	}
	
	protected void generateFirstOffCycleBill() {
		//DD1-20 
		LocalDateTime dueDate = getTimePoints().getOffcycleBillGenerationDate(offCycleBillDueDate1);
		TimeSetterUtil.getInstance().nextPhase(dueDate);
		JobUtils.executeJob(Jobs.offCycleBillingInvoiceAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).setMinDue(endorseAmount1)
				.setPastDueZero().verifyRowWithDueDate(offCycleBillDueDate1);
	}
	
	protected void payFirstOffCycleBill() {
		//DD1
		TimeSetterUtil.getInstance().nextPhase(offCycleBillDueDate1);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar dueAmount = new Dollar(endorseAmount1); 
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), dueAmount);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), dueAmount.negate());		
	}
	
	protected void makeSecondEndorsement() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement2").adjust(endorsementTD));
		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		PolicyHelper.verifyEndorsementIsCreated();
		
		endorseAmount2 = PolicySummaryPage.TransactionHistory.getTranPremium(); 

		// Endorsement transaction displaing on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setTotalDue(endorseAmount2).verifyPresent();
		
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate).setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(reason).verifyPresent();		
	}
	
	protected void generateSecondOffCycleBill() {
		//DD2-20 
		LocalDateTime dueDate = getTimePoints().getOffcycleBillGenerationDate(offCycleBillDueDate2);
		TimeSetterUtil.getInstance().nextPhase(dueDate);
		JobUtils.executeJob(Jobs.offCycleBillingInvoiceAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).setMinDue(endorseAmount2)
				.setPastDueZero().verifyRowWithDueDate(offCycleBillDueDate2);
	}
	
	protected void paySecondOffCycleBill() {
		//DD2
		TimeSetterUtil.getInstance().nextPhase(offCycleBillDueDate2);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar dueAmount = new Dollar(endorseAmount2); 
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
	
	//Skip this step for CA
	protected void generateRenewalBill() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		//Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewPremiumNotice(policyExpirationDate, billDate);
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

	//For AutoSS, HomeSS
	protected void payRenewalBillNotInFullAmount(Dollar toleranceAmount) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate)); 
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		
		Dollar offerAmount = BillingHelper.getBillMinDueAmount(policyExpirationDate, BillsAndStatementsType.BILL);
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), 
				getAmountToPaidOfferNotInFull(offerAmount, toleranceAmount)); 
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
	
	//For AutoCA 
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

		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), 
				getAmountToPaidOfferNotInFull(offerAmount, toleranceAmount));
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate); 
	}
	
	protected void payRenewalOfferInFullAmount(Dollar toleranceAmount) {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(20));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), 
				getRestAmountToPaidOfferInFull(toleranceAmount)); 
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);		
	}

	protected void makeOverpayment() {
		//2DD1
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusMonths(1));
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
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
		query.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.CANCELLATION);

		String cancelAmount = 
				BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue().toString();
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

	private Dollar getAmountToPaidOfferNotInFull(Dollar offerAmount, Dollar toleranceAmount) {
		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			Dollar caFraudAssessmentFee = new Dollar(1.76);
			offerAmount = offerAmount.subtract(caFraudAssessmentFee.multiply(2)); 
			toleranceAmount = toleranceAmount.add(0.01);			
		}
		return offerAmount.subtract(toleranceAmount);
	}
	
	private Dollar getRestAmountToPaidOfferInFull(Dollar toleranceAmount) {
		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			Dollar caFraudAssessmentFee = new Dollar(1.76); 
			toleranceAmount = toleranceAmount.add(caFraudAssessmentFee.multiply(2));
			toleranceAmount = toleranceAmount.add(0.01);
		}
		return toleranceAmount;
	}
	
}
