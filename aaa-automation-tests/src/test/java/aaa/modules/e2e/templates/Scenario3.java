package aaa.modules.e2e.templates;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.*;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.*;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.enums.SearchEnum.*;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scenario3 extends BaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected String policyNum;
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;

	/*
	 * Create policy
	 */
	public void createTestPolicy(TestData policyCreationTD) {
		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Semi-Annual payment plan", 2, installmentDueDates.size());
	}

	public void TC02_Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	public void TC03_Generate_Cancelation_Notice() {
		LocalDateTime cancNoticeDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cancNoticeDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelCancelNotice.verify.present();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(PolicyFlag.CANCEL_NOTICE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingBillsAndStatementsVerifier().setDueDate(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)))
				.setType(BillsAndStatementsType.CANCELLATION_NOTICE).verifyPresent();
	}

	public void TC04_Cancel_Policy() {
		LocalDateTime cDate = getTimePoints().getCancellationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cDate);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
	}

	public void TC05_Create_Remittance_File() {
		LocalDateTime date = getTimePoints().getCancellationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(date.plusDays(5).with(DateTimeUtils.closestWorkingDay));
		mainApp().open();
		SearchPage.search(SearchFor.BILLING, SearchBy.POLICY_QUOTE, policyNum);
		Dollar cnAmount = getCancellationNoticeDueAmount(installmentDueDates.get(1));
		File remitanceFile = RemittancePaymentsHelper.createRemittanceFile(getState(), policyNum, cnAmount, ExternalPaymentSystem.REGLKBX);
		RemittancePaymentsHelper.copyRemittanceFileToServer(remitanceFile);
	}

	public void TC06_Pay_Cancellation_Notice_By_Remittance() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		JobUtils.executeJob(Jobs.remittanceFeedBatchReceiveJob);
		mainApp().open();
		SearchPage.search(SearchFor.BILLING, SearchBy.POLICY_QUOTE, policyNum);
		Dollar cnAmount = getCancellationNoticeDueAmount(installmentDueDates.get(1));

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setAmount(cnAmount.negate())
				.setType(PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REGULUS_LOCKBOX)
				.verifyPresent();

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setType(PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT).verifyPresent();

		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	public void TC07_Renewal_Image_Generation() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	public void TC08_Renewal_Preview_Generation() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	public void TC09_Renewal_Offer_Generation() {
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
		BillingHelper.verifyRenewOfferGenerated(policyExpirationDate, installmentDueDates);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDateOffer)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();
	}

	public void TC10_Renewal_Premium_Notice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		BillingHelper.verifyRenewPremiumNotice(policyExpirationDate, billDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	public void TC11_Expire_Policy() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		//JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.search(SearchFor.BILLING, SearchBy.POLICY_QUOTE, policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	public void TC12_Customer_Decline_Renewal() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.search(SearchFor.BILLING, SearchBy.POLICY_QUOTE, policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);

	}

	public void TC13_Bind_Renew() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPayLapsedRenewShort(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.CUSTOMER_DECLINED).verify(1);

		policy.manualRenewalWithOrWithoutLapse().perform(getStateTestData(tdPolicy, "ManualRenewalWithOrWithoutLapse", "TestData"));

		if (PolicySummaryPage.labelPolicyStatus.getValue().equals(PolicyStatus.POLICY_EXPIRED)) {
			PolicySummaryPage.buttonRenewals.click();
		}

		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelLapseExist.verify.present();
		PolicySummaryPage.labelLapseExist.verify.value("Term includes a lapse period");

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
		//TODO Possible problems with MD state, See QC 35220 for details.
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getPayLapsedRenewShort(policyExpirationDate))
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	private Dollar getCancellationNoticeDueAmount(LocalDateTime installmentDate) {
		Map<String, String> values = new HashMap<>();
		values.put(BillingBillsAndStatmentsTable.DUE_DATE, getTimePoints().getCancellationTransactionDate(installmentDate).format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingBillsAndStatmentsTable.TYPE, BillsAndStatementsType.CANCELLATION_NOTICE);
		return new Dollar(BillingSummaryPage.tableBillsStatements.getRow(values).getCell(BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
	}

	private void generateAndCheckBill(LocalDateTime installmentDate) {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDate, billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}
}