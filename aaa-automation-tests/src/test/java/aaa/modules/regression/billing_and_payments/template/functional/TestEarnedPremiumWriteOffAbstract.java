package aaa.modules.regression.billing_and_payments.template.functional;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.table.Row;

public abstract class TestEarnedPremiumWriteOffAbstract extends PolicyBaseTest {

	protected abstract TestData getTdPolicy();
	protected abstract TestData getTestSpecificTDForTestEndorsement();
	private PremiumsAndCoveragesQuoteTab premiumAndCoverage = new PremiumsAndCoveragesQuoteTab();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private BindTab bindTab = new BindTab();

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment less that earned premium write off.
	 * 10. Verify that earned premium became less (earned premium write off reversal transaction).
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffLessDecline(String state) {
			createPolicyWithoutMortgagee();
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
			processRenewalAndBillGenerationJobs(expirationDate);
			String endorsementAmount = perfomAPEndorsement(policyNumber);
			processLapsedAndCollectionsJobs(expirationDate);
			processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
			acceptManualPaymentLess(endorsementAmount);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy with Mortgagee.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment equal to earned premium write off.
	 * 10. Verify that earned premium is fully reversed.
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffLessDeclineMortgagee(String state) {
		createPolicyWithMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		String endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		String earnedPremium = processEarnedPremiumJobWithAPEndorsementMortgagee(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentLess(earnedPremium);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Change status to Proposed.
	 * 10. Make payment less that earned premium write off.
	 * 11. Verify that earned premium became less (earned premium write off reversal transaction).
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffLessProposed(String state) {
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		String endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		changeStatusFromDeclineToProposed(policyNumber);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentLess(endorsementAmount);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment equal to earned premium write off.
	 * 10. Verify that earned premium is fully reversed.
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffEqualDecline(String state) {
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		String endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentEqual(endorsementAmount);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Change status to Proposed.
	 * 10. Make payment equal to earned premium write off.
	 * 11. Verify that earned premium is fully reversed.
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffEqualProposed(String state) {
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		String endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		changeStatusFromDeclineToProposed(policyNumber);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentEqual(endorsementAmount);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy with Mortgagee.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Change status to Proposed.
	 * 10. Make payment equal to earned premium write off.
	 * 11. Verify that earned premium is fully reversed.
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffEqualProposedMortgagee(String state) {
		createPolicyWithMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		String endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		changeStatusFromDeclineToProposed(policyNumber);
		String earnedPremium = processEarnedPremiumJobWithAPEndorsementMortgagee(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentEqual(earnedPremium);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment more than earned premium write off.
	 * 10. Verify that earned premium is fully reversed.
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffMoreDecline(String state) {
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		String endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentMore(endorsementAmount);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy with Mortgagee.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment more than earned premium write off.
	 * 10. Verify that earned premium is fully reversed.
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffMoreDeclineMortgagee(String state) {
		createPolicyWithMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		String endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		String earnedPremium = processEarnedPremiumJobWithAPEndorsementMortgagee(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentMore(earnedPremium);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Change status to Proposed.
	 * 10. Make payment more than earned premium write off.
	 * 11. Verify that earned premium is fully reversed.
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffMoreProposed(String state) {
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		String endorsementAmount = perfomAPEndorsement(policyNumber);
		processLapsedAndCollectionsJobs(expirationDate);
		changeStatusFromDeclineToProposed(policyNumber);
		processEarnedPremiumJobWithAPEndorsement(expirationDate, policyNumber, endorsementAmount);
		acceptManualPaymentMore(endorsementAmount);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Move time to R and run status update job.
	 * 5. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 6. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 7. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 8. Earned premium write off is absent on policy
	 * @details
	 */
	public void pas11697_testEarnedPremiumWriteOffNoAP(String state){
		createPolicyWithoutMortgagee();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalAndBillGenerationJobs(expirationDate);
		processLapsedAndCollectionsJobs(expirationDate);
		processEarnedPremiumJobWithoutAPEndorsement(expirationDate, policyNumber);
		acceptManualPaymentMoreNoAP();
	}

	private void processEarnedPremiumJobWithAPEndorsement(LocalDateTime expirationDate, String policyNumber, String endorsementAmount) {
		processEarnedPremiumJob(expirationDate, policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff.isPresent()).isTrue();
			softly.assertThat(BillingSummaryPage.labelAmountEarnedPremiumWriteOff).hasValue(endorsementAmount);
			//in nightly parallel run with other tests adjustment row is not latest sometimes
			Row adjustment_row = BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(TYPE, "Adjustment");
			softly.assertThat(adjustment_row.getCell(TYPE)).hasValue("Adjustment");
			softly.assertThat(adjustment_row.getCell(SUBTYPE_REASON)).hasValue("Earned Premium Write-off");
			softly.assertThat(adjustment_row.getCell(AMOUNT).getValue()).isEqualTo(toNegateAmount(endorsementAmount));
			softly.assertThat(adjustment_row.getCell(STATUS)).hasValue("Applied");		
			/*
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE)).hasValue("Adjustment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON)).hasValue("Earned Premium Write-off");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo(toNegateAmount(endorsementAmount));
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS)).hasValue("Applied");
			*/
		});
	}

	private String toNegateAmount(String amount) {
		return new Dollar(amount).negate().toString();
	}

	public void processEarnedPremiumJob(LocalDateTime expirationDate, String policyNumber) {
		//move time to R+60 and run earnedpremiumWriteoffprocessingjob
		LocalDateTime earnedPremiumWriteOff = getTimePoints().getEarnedPremiumWriteOff(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(earnedPremiumWriteOff);
		JobUtils.executeJob(Jobs.earnedPremiumWriteoffProcessingJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
	}

	public String processEarnedPremiumJobWithAPEndorsementMortgagee(LocalDateTime expirationDate, String policyNumber, String endorsementAmount) {
		//move time to R+60 and run earnedpremiumWriteoffprocessingjob
		processEarnedPremiumJob(expirationDate, policyNumber);
		String policyPremium = BillingSummaryPage.tablePaymentsOtherTransactions.getRow("Subtype/Reason", "Policy").getCell(AMOUNT).getValue();
		String earnedPremiumAmount = new Dollar(endorsementAmount).add(new Dollar(policyPremium)).toString();
		assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff.isPresent()).isTrue();
			softly.assertThat(BillingSummaryPage.labelAmountEarnedPremiumWriteOff).hasValue(earnedPremiumAmount);
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE)).hasValue("Adjustment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON)).hasValue("Earned Premium Write-off");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo(toNegateAmount(earnedPremiumAmount));
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS)).hasValue("Applied");
		});
		return earnedPremiumAmount;
	}

	private void processEarnedPremiumJobWithoutAPEndorsement(LocalDateTime expirationDate, String policyNumber) {
		//move time to R+60 and run earnedpremiumWriteoffprocessingjob
		processEarnedPremiumJob(expirationDate, policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff.isPresent()).isFalse();
		});
	}

	public void changeStatusFromDeclineToProposed(String policyNumber) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumAndCoverage.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();
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

	public String perfomAPEndorsement(String policyNumber) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTD = getTestSpecificTDForTestEndorsement().adjust(getStateTestData(getTdPolicy(), "Endorsement", "TestData_Plus10Day"));
		policy.endorse().performAndFill(endorsementTD);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Map<String,String> endorsement_row = new HashMap<>();
		endorsement_row.put(TYPE, "Premium");
		endorsement_row.put(SUBTYPE_REASON, "Endorsement");
		//return BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(AMOUNT).getValue();
		return BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(endorsement_row).getCell(AMOUNT).getValue();
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
		mainApp().open();
		createCustomerIndividual();
		TestData policyCreationTD = getPolicyDefaultTD();
		createPolicy(policyCreationTD);
	}

	private void createPolicyWithMortgagee() {
		mainApp().open();
		TestData policyCreationTD = getPolicyDefaultTD();
		adjustWithMortgageeData(policyCreationTD);
		createCustomerIndividual();
		createPolicy(policyCreationTD);
	}

	private void acceptManualPaymentLess(String endorsementAmount) {
		//pay payment by cash less that earned premium write off
		String newEndorsementAmount;
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		newEndorsementAmount = new Dollar(endorsementAmount).subtract(new Dollar(1)).toString();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(newEndorsementAmount);
		assertSoftly(softly -> {
			softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_PREVIOS_TERM)).hasValue(newEndorsementAmount);
			softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_RENEWAL_TERM)).hasValue("$0.00");
		});
		AcceptPaymentActionTab.buttonOk.click();
		assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff.isPresent()).isTrue();
			softly.assertThat(BillingSummaryPage.labelAmountEarnedPremiumWriteOff).hasValue(new Dollar(endorsementAmount).subtract(new Dollar(newEndorsementAmount)).toString());
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE)).hasValue("Adjustment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(SUBTYPE_REASON)).hasValue("Earned Premium Write-off Reversed");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(AMOUNT)).hasValue(newEndorsementAmount);
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(STATUS)).hasValue("Applied");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE)).hasValue("Payment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON)).hasValue("Manual Payment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT)).hasValue(toNegateAmount(newEndorsementAmount));
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS)).hasValue("Cleared");
		});
	}

	private void acceptManualPaymentEqual(String endorsementAmount) {
		//pay payment by cash equal to earned premium write off
		String newEndorsementAmount;
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		newEndorsementAmount = new Dollar(endorsementAmount).toString();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(newEndorsementAmount);
		assertSoftly(softly -> {
			softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_PREVIOS_TERM.getLabel()).getValue()).isEqualTo(newEndorsementAmount);
			softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_RENEWAL_TERM.getLabel()).getValue()).isEqualTo("$0.00");
		});
		AcceptPaymentActionTab.buttonOk.click();
		assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff.isPresent()).isFalse();
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE).getValue()).isEqualTo("Adjustment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Earned Premium Write-off Reversed");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(AMOUNT).getValue()).isEqualTo(newEndorsementAmount);
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(STATUS).getValue()).isEqualTo("Applied");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Payment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Manual Payment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo(toNegateAmount(newEndorsementAmount));
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Cleared");
		});
	}

	private void acceptManualPaymentMore(String endorsementAmount) {
		//pay payment by cash more than earned premium write off
		String newEndorsementAmount;
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		newEndorsementAmount = new Dollar(endorsementAmount).multiply(2).subtract(new Dollar(1)).toString();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(newEndorsementAmount);
		assertSoftly(softly -> {
			softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_PREVIOS_TERM.getLabel()).getValue()).isEqualTo(endorsementAmount);
			softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_RENEWAL_TERM.getLabel()).getValue())
					.isEqualTo(new Dollar(newEndorsementAmount).subtract(new Dollar(endorsementAmount)).toString());
		});
		AcceptPaymentActionTab.buttonOk.click();
		assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff.isPresent()).isFalse();
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(TYPE).getValue()).isEqualTo("Adjustment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Earned Premium Write-off Reversed");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(AMOUNT).getValue()).isEqualTo(endorsementAmount);
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2).getCell(STATUS).getValue()).isEqualTo("Applied");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Payment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Manual Payment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo(toNegateAmount(newEndorsementAmount));
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Cleared");
		});
	}

	private void acceptManualPaymentMoreNoAP() {
		//pay payment by cash
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("Cash");
		String paymentAmount = new Dollar(30).toString();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(paymentAmount);
		assertSoftly(softly -> {
			softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_PREVIOS_TERM.getLabel()).getValue()).isEqualTo("$0.00");
			softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT_RENEWAL_TERM.getLabel()).getValue()).isEqualTo(paymentAmount);
		});
		AcceptPaymentActionTab.buttonOk.click();
		assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff.isPresent()).isFalse();
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).getValue()).isEqualTo("Payment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON).getValue()).isEqualTo("Manual Payment");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo(toNegateAmount(paymentAmount));
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS).getValue()).isEqualTo("Cleared");
		});
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
