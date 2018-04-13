package aaa.modules.regression.billing_and_payments.template.functional;

import static aaa.main.enums.BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public abstract class TestEarnedPremiumWriteOffAbstract extends PolicyBaseTest {

	ProductRenewalsVerifier productRenewalsVerifier = new ProductRenewalsVerifier();
	protected abstract TestData getTdPolicy();
	protected abstract TestData getTestSpecificTDForTestEndorsement();
	protected BillingAccount billingAccount = new BillingAccount();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();

	public void pas11697_testEarnedPremiumWriteOffLessDecline(String state) {
		String endorsementAmount;
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentLess(endorsementAmount);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Customer Declined");
	}

	public void pas11697_testEarnedPremiumWriteOffLessDeclineMortgagee(String state) {
		String endorsementAmount;
		createPolicyWithMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		String earnedPremium = processEarnedPremiumJobWithAPEndorsementMortgagee(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentLess(earnedPremium);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Customer Declined");
	}

	public void pas11697_testEarnedPremiumWriteOffLessProposed(String state) {
		String endorsementAmount;
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		changeStatusFromDeclineToProposed(policyNumber);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentLess(endorsementAmount);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Proposed");
	}

	public void pas11697_testEarnedPremiumWriteOffEqualDecline(String state) {
		String endorsementAmount;
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentEqual(endorsementAmount);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Customer Declined");
	}

	public void pas11697_testEarnedPremiumWriteOffEqualProposed(String state) {
		String endorsementAmount;
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		changeStatusFromDeclineToProposed(policyNumber);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentEqual(endorsementAmount);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Proposed");
	}

	public void pas11697_testEarnedPremiumWriteOffEqualProposedMortgagee(String state) {
		String endorsementAmount;
		createPolicyWithMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		changeStatusFromDeclineToProposed(policyNumber);
		String earnedPremium = processEarnedPremiumJobWithAPEndorsementMortgagee(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentEqual(earnedPremium);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Proposed");
	}

	public void pas11697_testEarnedPremiumWriteOffMoreDecline(String state) {
		String endorsementAmount;
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentMore(endorsementAmount);
	}

	public void pas11697_testEarnedPremiumWriteOffMoreDeclineMortgagee(String state) {
		String endorsementAmount;
		createPolicyWithMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		String earnedPremium = processEarnedPremiumJobWithAPEndorsementMortgagee(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentMore(earnedPremium);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Customer Declined");
	}

	public void pas11697_testEarnedPremiumWriteOffMoreProposed(String state) {
		String endorsementAmount;
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		changeStatusFromDeclineToProposed(policyNumber);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentMore(endorsementAmount);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Proposed");
	}

	public void pas11697_testEarnedPremiumWriteOffNoAP(String state){
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		processLapsedAndCollectionsJobs(expirationDate);
		processEarnedPremiumJobWithoutAPEndorsement(expirationDate, policyNumber);
		acceptManualPaymentMoreNoAP();
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(POLICY_STATUS).getValue()).isEqualTo("Customer Declined");
	}

	private void processEarnedPremiumJobWithAPEndorsement(LocalDateTime expirationDate, String policyNumber, String endorsementAmount) {
		processEarnedPremiumJob(expirationDate, policyNumber);
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isPresent();
		assertThat(BillingSummaryPage.labelAmountEarnedPremiumWriteOff.getValue()).isEqualTo(endorsementAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Adjustment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Earned Premium Write-off");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo("("+endorsementAmount+")");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Applied");
	}

	private void processEarnedPremiumJob(LocalDateTime expirationDate, String policyNumber) {
		//move time to R+60 and run earnedpremiumWriteoffprocessingjob
		LocalDateTime earnedPremiumWriteOff = getTimePoints().getEarnedPremiumWriteOff(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(earnedPremiumWriteOff);
		JobUtils.executeJob(Jobs.earnedPremiumWriteoffProcessingJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
	}

	private String processEarnedPremiumJobWithAPEndorsementMortgagee(LocalDateTime expirationDate, String policyNumber, String endorsementAmount) {
		//move time to R+60 and run earnedpremiumWriteoffprocessingjob
		processEarnedPremiumJob(expirationDate, policyNumber);
		String policyPremium = BillingSummaryPage.tablePaymentsOtherTransactions.getRow("Subtype/Reason", "Policy").getCell(AMOUNT).getValue();
		String earnedPremiumAmount = new Dollar(endorsementAmount).add(new Dollar(policyPremium)).toString();
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isPresent();
		assertThat(BillingSummaryPage.labelAmountEarnedPremiumWriteOff.getValue()).isEqualTo(earnedPremiumAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Adjustment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Earned Premium Write-off");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo("("+earnedPremiumAmount+")");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Applied");
		return earnedPremiumAmount;
	}

	private void processEarnedPremiumJobWithoutAPEndorsement(LocalDateTime expirationDate, String policyNumber) {
		//move time to R+60 and run earnedpremiumWriteoffprocessingjob
		processEarnedPremiumJob(expirationDate, policyNumber);
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isAbsent();
	}

	private void changeStatusFromDeclineToProposed(String policyNumber) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().submitTab();
	}

	private void processLapsedAndCollectionsJobs(LocalDateTime expirationDate) {
		//move time to R and run policyUpdate Status
		TimeSetterUtil.getInstance().nextPhase(expirationDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		//move time to R+10 and run policyLapsedRenewalProcessAsyncJob
		LocalDateTime policyLapsedDate = getTimePoints().getRenewCustomerDeclineDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyLapsedDate);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		//move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
		LocalDateTime earnedPremium15 = getTimePoints().getEarnedPremiumBillFirst(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(earnedPremium15);
		JobUtils.executeJob(Jobs.aaaCollectionCancelDebtBatchJob);

		LocalDateTime earnedPremium30 = getTimePoints().getEarnedPremiumBillSecond(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(earnedPremium30);
		JobUtils.executeJob(Jobs.aaaCollectionCancelDebtBatchJob);

		LocalDateTime earnedPremium45 = getTimePoints().getEarnedPremiumBillThird(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(earnedPremium45);
		JobUtils.executeJob(Jobs.aaaCollectionCancelDebtBatchJob);
	}

	private String perfomAPEndorsement(String policyNumber) {
		String endorsementAmount;//do AP endorsement, less that $100
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTD = getTestSpecificTDForTestEndorsement().adjust(getStateTestData(getTdPolicy(), "Endorsement", "TestData_Plus10Day"));
		policy.endorse().performAndFill(endorsementTD);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		endorsementAmount = BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(AMOUNT).getValue();
		return endorsementAmount;
	}

	private void processRenewalAndBillGenerationJobs(LocalDateTime expirationDate) {
		//move time to R-45 and run renewal batch job
		LocalDateTime renewImageGenDate = getTimePoints().getRenewPreviewGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//move time to R-35 and run renewal batch job
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//move time to R-20 and run aaaRenewalNotice job
		LocalDateTime renewalNoticeDate = getTimePoints().getBillGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewalNoticeDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
	}

	private void createPolicyWithoutMortgagee() {
		TestData policyCreationTD = getPolicyDefaultTD();
		mainApp().open();
		createCustomerIndividual();
		createPolicy(policyCreationTD);
	}

	private void createPolicyWithMortgagee() {
		TestData policyCreationTD = getPolicyDefaultTD();
		adjustWithMortgageeData(policyCreationTD);
		mainApp().open();
		createCustomerIndividual();
		createPolicy(policyCreationTD);
	}

	private void acceptManualPaymentLess(String endorsementAmount) {
		//pay payment by cash
		String newEndorsementAmount;
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		newEndorsementAmount = new Dollar(endorsementAmount).subtract(new Dollar(1)).toString();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(newEndorsementAmount);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_PREVIOS_TERM.getLabel()).getValue()).isEqualTo(newEndorsementAmount);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_RENEWAL_TERM.getLabel()).getValue()).isEqualTo("$0.00");
		AcceptPaymentActionTab.buttonOk.click();
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isPresent();
		assertThat(BillingSummaryPage.labelAmountEarnedPremiumWriteOff.getValue()).isEqualTo(new Dollar(endorsementAmount).subtract(new Dollar(newEndorsementAmount)).toString());
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE).getValue()).isEqualTo("Adjustment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Earned Premium Write-off Reversed");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(AMOUNT).getValue()).isEqualTo(newEndorsementAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(STATUS).getValue()).isEqualTo("Applied");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Payment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Manual Payment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo("("+ newEndorsementAmount +")");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Cleared");
	}

	private void acceptManualPaymentEqual(String endorsementAmount) {
		//pay payment by cash
		String newEndorsementAmount;
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		newEndorsementAmount = new Dollar(endorsementAmount).toString();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(newEndorsementAmount);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_PREVIOS_TERM.getLabel()).getValue()).isEqualTo(newEndorsementAmount);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_RENEWAL_TERM.getLabel()).getValue()).isEqualTo("$0.00");
		AcceptPaymentActionTab.buttonOk.click();
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isAbsent();
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE).getValue()).isEqualTo("Adjustment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Earned Premium Write-off Reversed");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(AMOUNT).getValue()).isEqualTo(newEndorsementAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(STATUS).getValue()).isEqualTo("Applied");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Payment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Manual Payment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo("("+ newEndorsementAmount +")");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Cleared");
	}

	private void acceptManualPaymentMore(String endorsementAmount) {
		//pay payment by cash
		String newEndorsementAmount;
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		newEndorsementAmount = new Dollar(endorsementAmount).multiply(2).subtract(new Dollar(1)).toString();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(newEndorsementAmount);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_PREVIOS_TERM.getLabel()).getValue()).isEqualTo(endorsementAmount);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_RENEWAL_TERM.getLabel()).getValue()).isEqualTo(new Dollar(newEndorsementAmount).subtract(new Dollar(endorsementAmount)).toString());
		AcceptPaymentActionTab.buttonOk.click();
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isAbsent();
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE).getValue()).isEqualTo("Adjustment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Earned Premium Write-off Reversed");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(AMOUNT).getValue()).isEqualTo(endorsementAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(STATUS).getValue()).isEqualTo("Applied");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Payment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Manual Payment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo("("+ newEndorsementAmount +")");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Cleared");
	}

	private void acceptManualPaymentMoreNoAP() {
		//pay payment by cash
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		String paymentAmount = new Dollar(30).toString();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(paymentAmount);
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_PREVIOS_TERM.getLabel()).getValue()).isEqualTo("$0.00");
		assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_RENEWAL_TERM.getLabel()).getValue()).isEqualTo(paymentAmount);
		AcceptPaymentActionTab.buttonOk.click();
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isAbsent();
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Payment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Manual Payment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo(paymentAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Cleared");
	}

	private void adjustWithMortgageeData(TestData policyTD) {
		//adjust TestData with Mortgagee tab data
		String mortgageeTabKey = TestData.makeKeyPath(HomeSSMetaData.MortgageesTab.class.getSimpleName());
		TestData mortgageeTD = getTestSpecificTD("MortgageesTab");
		//adjust TestData with Premium and Coverage tab data
		String premiumAndCoverageTabKey = TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName());
		TestData premiumAndCoverageTD = getTestSpecificTD("PremiumsAndCoveragesQuoteTab_Mortgagee");
		policyTD.adjust(mortgageeTabKey, mortgageeTD).adjust(premiumAndCoverageTabKey, premiumAndCoverageTD);
	}
}
