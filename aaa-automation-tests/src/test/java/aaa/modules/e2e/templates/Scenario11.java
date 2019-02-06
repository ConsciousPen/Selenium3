package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.*;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.*;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

public class Scenario11 extends ScenarioBaseTest { 
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	protected String[] endorsementReasonDataKeys;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 1;
	
	protected String policyTerm;
	protected Integer totalVehiclesNumber;
	
	protected Dollar endorseAmount1; 
	protected Dollar endorseAmount2;
	
	protected LocalDateTime offCycleBillDueDate1; 		 
	protected LocalDateTime offCycleBillDueDate2;		
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();		
		mainApp().open();		
		createCustomerIndividual();	
		
		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD); 
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);
		//assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Annual (Pay In Full) payment plan", installmentsCount, installmentDueDates.size());
		CustomAssertions.assertThat(installmentDueDates.size()).as("Billing Installments count for Annual (Pay In Full) payment plan").isEqualTo(installmentsCount);
		
		offCycleBillDueDate1 = policyEffectiveDate.plusMonths(1);
		offCycleBillDueDate2 = policyEffectiveDate.plusMonths(2);
		
		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}
	
	protected void makeFirstEndorsement() {		
		//DD0+5
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(5));
		
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement1").adjust(endorsementTD));
		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		PolicyHelper.verifyEndorsementIsCreated();
		totalVehiclesNumber = totalVehiclesNumber + 1;
		
		endorseAmount1 = PolicySummaryPage.TransactionHistory.getTranPremium(); 
		
		// Endorsement transaction displaying on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
				
		Dollar pligaOrMvleFee = new Dollar(0);
		if (getState().equals(Constants.States.NJ)) {
			//pligaOrMvleFee = new Dollar(BillingHelper.calculatePligaFee(transactionDate, endorseAmount1)); 
			pligaOrMvleFee = new Dollar(BillingHelper.calculatePligaFee(transactionDate)); 
			endorseAmount1 = endorseAmount1.add(pligaOrMvleFee);
		}
		if (getState().equals(Constants.States.NY)) {
			pligaOrMvleFee = new Dollar(10); 
			endorseAmount1 = endorseAmount1.add(pligaOrMvleFee);
		}
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setTotalDue(endorseAmount1).verifyPresent();
		
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate)
			.setPolicy(policyNum)
			.setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason)
			.setAmount(endorseAmount1.subtract(pligaOrMvleFee)).verifyPresent();
		
		if (getState().equals(Constants.States.NJ)) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate)
				.setType(PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason("PLIGA Fee")
				.setAmount(pligaOrMvleFee).verifyPresent();
		}
		
		if (getState().equals(Constants.States.NY)) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate)
				.setType(PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason("MVLE Fee")
				.setAmount(pligaOrMvleFee).verifyPresent();
		}
	}
	
	protected void generateFirstOffCycleBill() {
		//DD1-20 
		LocalDateTime dueDate = getTimePoints().getOffcycleBillGenerationDate(offCycleBillDueDate1);
		TimeSetterUtil.getInstance().nextPhase(dueDate);
		JobUtils.executeJob(BatchJob.offCycleBillingInvoiceAsyncJob);

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
		
		// Endorsement transaction displaying on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		
		Dollar pligaFee = new Dollar(0);
		if (getState().equals(Constants.States.NJ)) {
		//	pligaFee = new Dollar(BillingHelper.calculatePligaFee(transactionDate, endorseAmount2));
			pligaFee = new Dollar(BillingHelper.calculatePligaFee(transactionDate));
			endorseAmount2 = endorseAmount2.add(pligaFee);
		}
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setTotalDue(endorseAmount2).verifyPresent();
		
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate)
			.setPolicy(policyNum)
			.setType(PaymentsAndOtherTransactionType.PREMIUM)				
			.setSubtypeReason(reason)
			.setAmount(endorseAmount2.subtract(pligaFee)).verifyPresent();	
		
		if (getState().equals(Constants.States.NJ)) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate)
				.setType(PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason("PLIGA Fee")
				.setAmount(pligaFee).verifyPresent();
		}
	}
	
	protected void generateSecondOffCycleBill() {
		//DD2-20 
		LocalDateTime dueDate = getTimePoints().getOffcycleBillGenerationDate(offCycleBillDueDate2);
		TimeSetterUtil.getInstance().nextPhase(dueDate);
		JobUtils.executeJob(BatchJob.offCycleBillingInvoiceAsyncJob);

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
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);

		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	protected void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.buttonRenewals.verify.enabled();
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.buttonRenewals.verify.enabled();
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(installmentDueDates, softly);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDateOffer)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();
		
		if (verifyPligaOrMvleFee(renewDateOffer, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewDateOffer;
		}
		
	}
	
	//Skip this step for CA
	protected void generateRenewalBill() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billGenDate, pligaOrMvleFee, installmentsCount);

		verifyRenewPremiumNotice(policyExpirationDate, billGenDate, pligaOrMvleFee);
	}

	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		
	}

	//For AutoSS, HomeSS, PUP
	protected void payRenewalBillNotInFullAmount(Dollar toleranceAmount) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate).minusHours(1)); 
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		
		//Dollar offerAmount = BillingHelper.getBillMinDueAmount(policyExpirationDate, BillsAndStatementsType.BILL);
		String billType = getState().equals(Constants.States.CA) ? BillsAndStatementsType.OFFER : BillsAndStatementsType.BILL;
		Dollar offerAmount = BillingHelper.getBillMinDueAmount(policyExpirationDate, billType);
		
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), 
				getAmountToPaidOfferNotInFull(offerAmount, toleranceAmount)); 
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
	
	//For AutoCA, HomeCA
	protected void payRenewalOfferNotInFullAmount(Dollar toleranceAmount) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate)); //.plusHours(1));
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);
		
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
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate).plusDays(5));

		//TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.plusDays(20));
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);
		
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
		//assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_CANCELLED);
	}

	protected void refundGeneration() {		
		//2DD1+10 
		LocalDateTime refundDueDate = policyExpirationDate.plusMonths(1).plusDays(10);
		TimeSetterUtil.getInstance().nextPhase(refundDueDate);
		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum); 
		
		Dollar refundAmount = getRefundAmount(); 
		
		if (refundAmount.moreThan(new Dollar(1000))) {
			new BillingPendingTransactionsVerifier().setTransactionDate(refundDueDate)
				.setAmount(refundAmount)
				.setType(BillingPendingTransactionsType.REFUND)
				.setSubtypeReason(BillingPendingTransactionsSubtype.AUTOMATED_REFUND)
				.setReason(BillingPendingTransactionsReason.OVERPAYMENT)
				.setStatus(BillingPendingTransactionsStatus.PENDING).verifyPresent();
		
			BillingHelper.approvePendingTransaction(refundDueDate, BillingPendingTransactionsType.REFUND);
		}
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refundDueDate)
			.setAmount(refundAmount)
			.setType(PaymentsAndOtherTransactionType.REFUND)
			.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
			.setReason(PaymentsAndOtherTransactionReason.OVERPAYMENT)
			.setStatus(PaymentsAndOtherTransactionStatus.APPROVED).verifyPresent(); 		
	}

	private Dollar getRefundAmount(){
		HashMap<String, String> query_cancel = new HashMap<>();
		query_cancel.put(BillingPaymentsAndOtherTransactionsTable.POLICY, policyNum);
		query_cancel.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PREMIUM); 
		query_cancel.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.CANCELLATION);
		
		String cancelAmount = 
				BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query_cancel).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue();
		Dollar refundAmount = new Dollar(cancelAmount.substring(1, cancelAmount.length()-1)); 
		refundAmount = refundAmount.add(100); 
		
		if (getPolicyType().equals(PolicyType.HOME_CA_HO3)) {
			HashMap<String, String> query_renew = new HashMap<>();
			query_renew.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PREMIUM); 
			query_renew.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.RENEWAL);
			
			String premiumRenewal = 
					BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query_renew).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue();
			Dollar premiumAmount = new Dollar(premiumRenewal.substring(1, premiumRenewal.length()-1)); 
			refundAmount = refundAmount.add(premiumAmount); 
			//refundAmount = refundAmount.subtract(new Dollar(20)); commented according to PASBB-492: Reinstatement fee 20$ should not be applied
		}		
		return refundAmount;		
	}
	
	private Dollar getAmountToPaidOfferNotInFull(Dollar offerAmount, Dollar toleranceAmount) {
		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			Dollar caFraudAssessmentFee = new Dollar(1.76);
			offerAmount = offerAmount.subtract(caFraudAssessmentFee.multiply(2)); 
			toleranceAmount = toleranceAmount.add(0.01);			
		}
		else if (getPolicyType().equals(PolicyType.HOME_CA_HO3)) {
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
		else if (getPolicyType().equals(PolicyType.HOME_CA_HO3)) {
			toleranceAmount = toleranceAmount.add(0.01);
		}
		return toleranceAmount;
	}
	
}
