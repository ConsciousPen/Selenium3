package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.SoftAssertions;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.Tab;
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
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.BillingConstants.PolicyFlag;
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

public class Scenario13 extends ScenarioBaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	protected String[] endorsementReasonDataKeys;
	
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 11; 
	
	protected List<LocalDateTime> installmentDueDatesAfterEndorsement;
	protected int installmentsCountAfterEndorsement = 7; 
	
	List<LocalDateTime> installmentDueDatesForRenewal;
	protected int installmentsCountForRenewal = 2; 
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();		
		mainApp().open();
		
		createCustomerIndividual();	
		policyNum = createPolicy(policyCreationTD); 
		
		//PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(PolicyStatus.POLICY_ACTIVE);
		});

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Semi-Annual payment plan", installmentsCount, installmentDueDates.size()); 	
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(installmentDueDates.size()).as("Billing Installments count for Monthly (Eleven Pay) payment plan").isEqualTo(installmentsCount);
		});
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

	protected void generateThirdBill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	protected void payThirdBill() {
		payAndCheckBill(installmentDueDates.get(3));
	}
	
	protected void generateFourthBill() {
		generateAndCheckBill(installmentDueDates.get(4));
	}

	protected void payFourthBill() {
		payAndCheckBill(installmentDueDates.get(4));
	}
	
	protected void generateFifthBill() {
		generateAndCheckBill(installmentDueDates.get(5));
	}
	
	protected void removeAutoPay() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
		billingAccount.update().start();
		new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(false);
		Tab.buttonCancel.click();
	}

	protected void payFifthBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(5));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
		
		
		log.info("Bill Due Date is: "+billDueDate);
		log.info("Installment Due Date is: "+installmentDueDates.get(5));
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(5), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		//verify recurring payment is not generated
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(DateTimeUtils.getCurrentDateTime())
			.setAmount(minDue.negate())
			.setType(PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).verifyPresent(false);
		
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());	
	}
	
	//DD6-21
	protected void changePaymentPlanDuringEndorsement() {
		LocalDateTime endorseDueDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(6)).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(endorseDueDate);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);		
		Dollar totalDueBeforeEndorsement =  new Dollar(BillingSummaryPage.getTotalDue());
		
		BillingSummaryPage.openPolicy(policyEffectiveDate);				
		TestData endorsementTD = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		policy.endorse().performAndFill(endorsementTD);
		Dollar endorseAmount = new Dollar(PolicySummaryPage.TransactionHistory.getTranPremium()); 
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get()); 
		Dollar totalDueAfterEndorsement;
		if (endorseAmount.isNegative()) {
			totalDueAfterEndorsement = new Dollar(totalDueBeforeEndorsement.add(endorseAmount));
		}
		else {
			totalDueAfterEndorsement = new Dollar(totalDueBeforeEndorsement.subtract(endorseAmount));
		}
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE)
			.setPaymentPlan("Semi-Annual")
			.setTotalDue(totalDueAfterEndorsement).verifyPresent();

		installmentDueDatesAfterEndorsement = BillingHelper.getInstallmentDueDates();	
		List<Dollar> installmentDues = BillingHelper.getInstallmentDues();
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(installmentDueDatesAfterEndorsement.size()).as("Billing Installments count after payment plan changed to Semi-Annual")
				.isEqualTo(installmentsCountAfterEndorsement);
			softly.assertThat(installmentDues.get(6)).as("Last installment amount is incorrect").isEqualTo(totalDueAfterEndorsement);
		});		
		
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(endorseDueDate)
			.setPolicy(policyNum)
			.setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason)
			.setAmount(endorseAmount).verifyPresent();
		
	}
	
	protected void generateSixthBill() {
		generateAndCheckBill(installmentDueDates.get(6));
	}

	protected void paySixthBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(6));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(6), BillingBillsAndStatmentsTable.MINIMUM_DUE)); 
		minDue = minDue.subtract(new Dollar(4.99));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}
	
	protected void refundGeneration() {
		LocalDateTime refundDate = getTimePoints().getRefundDate(installmentDueDates.get(6)); 
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		JobUtils.executeJob(Jobs.refundGenerationJob);
		
		Dollar amount = new Dollar(4.99);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refundDate)
			.setAmount(amount.negate())
			.setType(PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.SMALL_BALANCE_WRITE_OFF)
			.setStatus(PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.getTotalDue()).as("Total Due is not $0.00").isEqualTo(new Dollar(0));
			softly.assertThat(BillingSummaryPage.getMinimumDue()).as("Min Due is not $0.00").isEqualTo(new Dollar(0));
		});	
		
	}
	
	protected void cancelNoticeNotGenerated() {
		LocalDateTime cancelNoticeDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(6));
		TimeSetterUtil.getInstance().nextPhase(cancelNoticeDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(PolicyFlag.DEFAULT).verifyRowWithEffectiveDate(policyEffectiveDate);
	}
	
	protected void cancellationNotGenerated() {
		LocalDateTime cancelDate = getTimePoints().getCancellationDate(installmentDueDates.get(6)); 
		TimeSetterUtil.getInstance().nextPhase(cancelDate);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
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
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE)
				.setPaymentPlan("Semi-Annual").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED)
				.setPaymentPlan("Semi-Annual (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);
		BillingSummaryPage.buttonHidePriorTerms.click();
		
		installmentDueDatesForRenewal = BillingHelper.getInstallmentDueDates();	
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(installmentDueDatesForRenewal.size()).as("Billing Installments count for renewal is incorrect")
				.isEqualTo(installmentsCountForRenewal);
		});
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCountForRenewal );
		}		
		/*
		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
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

	/*
	protected void createRenewalVersion2() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar renewalBillAmount = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingBillsAndStatmentsTable.MINIMUM_DUE)); 
		
		BillingSummaryPage.openPolicy(policyEffectiveDate);	
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		policy.policyInquiry().start();
	}
	*/
	
	protected void payRenewalBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingBillsAndStatmentsTable.MINIMUM_DUE)); 
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}
	
	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
}