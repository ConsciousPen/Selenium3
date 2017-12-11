package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.SoftAssertions;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
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
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class Scenario15 extends ScenarioBaseTest { 	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate; 	
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 5; 
	
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
			softly.assertThat(installmentDueDates.size()).as("Billing Installments count for Five Pay payment plan and Semi-annual term policy").isEqualTo(installmentsCount);
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
	
	protected void removeAutoPay() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
		billingAccount.update().start();
		new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(false);
		Tab.buttonCancel.click();
	}
	
	protected void generateThirdBill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	protected void payThirdBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(3));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(3), BillingBillsAndStatmentsTable.MINIMUM_DUE)); 
		minDue = minDue.subtract(new Dollar("10.01"));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}
	
	protected void generateCancelNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(3));
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.CANCEL_NOTICE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE).verifyRowWithDueDate(
			getTimePoints().getCancellationTransactionDate(installmentDueDates.get(3)));
	}
	
	protected void generateCancellation() {
		LocalDateTime cDate = getTimePoints().getCancellationDate(installmentDueDates.get(3));
		TimeSetterUtil.getInstance().nextPhase(cDate);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
	}
	
	public void renewalImageGeneration() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
		//PolicyHelper.verifyAutomatedRenewalNotGenerated(renewDateImage);
		//PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
	}

	public void renewalPreviewNotGenerated() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
	}

	public void renewalOfferNotGenerated() {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_CANCELLED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(policyExpirationDate).setSubtypeReason(
			BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent(false);
	}

}