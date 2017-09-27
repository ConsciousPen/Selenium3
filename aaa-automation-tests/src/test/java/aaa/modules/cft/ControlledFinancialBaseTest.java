/* Copyright ?? 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.cft;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeSuite;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ControlledFinancialBaseTest extends PolicyBaseTest {

	protected static final String DEFAULT_TEST_DATA_KEY = "TestData";
	protected static final String STATE_PARAM = "state";

	private LocalDateTime startTime;
	private ThreadLocal<List<LocalDateTime>> installments = new ThreadLocal<>();
	private ThreadLocal<String> policyNumber = ThreadLocal.withInitial(() -> StringUtils.EMPTY);

	@BeforeSuite(alwaysRun = true)
	public void runCFTJob() {
		//runCFTJobs();
		startTime = TimeSetterUtil.getInstance().getCurrentTime();
	}

	/**
	 * Creating of the policy for test
	 * DD0 - means today time - start time point
	 */
	protected void testCFTScenario1CreateQuote() {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		td.adjust("PremiumAndCoveragesTab", getTestSpecificTD("PremiumAndCoveragesTab_DataGather"));
		td.adjust("PurchaseTab", getTestSpecificTD("PurchaseTab_DataGather"));
		policyNumber.set(createPolicy(td.resolveLinks()));
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installments.set(BillingHelper.getInstallmentDueDates());
	}

	/**
	 * Endorsement of the policy
	 * X_2 - today(suite start time) + 2 day
	 */
	protected void testCFTScenario1Endorsement() {
		TimeSetterUtil.getInstance().nextPhase(startTime.plusDays(2));
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber.get());
		policy.endorse().performAndFill(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Endorsement effective %1$s for Policy %2$s", TimeSetterUtil.getInstance().getCurrentTime().plusDays(2).format(DateTimeUtils.MM_DD_YYYY), policyNumber.get()));
	}

	/**
	 * Endorsement of the policy
	 * DD1_20 - 1st Due Day - 20 days
	 */
	protected void testCFTScenario1FirstInstallmentBillGeneration() {
		LocalDateTime billDueDate = getTimePoints().getBillGenerationDate(installments.get().get(0));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber.get());
		BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).verify
				.value(BillingConstants.BillsAndStatementsType.BILL);
		BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE).verify
				.value(installments.get().get(0).format(DateTimeUtils.MM_DD_YYYY));
		BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).verify
				.value(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue());
		BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.PAST_DUE).verify
				.value(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAST_DUE).getValue());
		BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE).verify
				.value(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
	}

	/**
	 * Endorsement of the policy
	 * Auto AZ DD1+8+8
	 * Auto MD, Property - UT, DE, OK, MD, KS, OH DD1+5+13
	 * Property NJ - DD1+5+15
	 */
	protected void testCFTScenario1AutomaticCancellationNotice() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(installments.get().get(0));
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(installments.get().get(0));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber.get());
		BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE)
				.verify.value(cancellationDate.format(DateTimeUtils.MM_DD_YYYY));
		BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE)
				.verify.value(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE);
	}

	/**
	 * Endorsement of the policy
	 * Auto AZ DD1+8+8
	 * Auto MD, Property - UT, DE, OK, MD, KS, OH DD1+5+13
	 * Property NJ - DD1+5+15
	 */
	protected void testCFTScenario1AutomaticCancellation() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(installments.get().get(0));
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(cancellationNoticeDate);
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber.get());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	/**
	 *
	 */
	protected void testCFTScenario1WriteOff() {
		LocalDateTime writeOffDate = getTimePoints().getEarnedPremiumWriteOff(installments.get().get(0));
		TimeSetterUtil.getInstance().nextPhase(writeOffDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber.get());
		// check write off was generated
		log.info("");
	}

	private void runCFTJobs() {
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		JobUtils.executeJob(Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		JobUtils.executeJob(Jobs.policyTransactionLedgerJob);
	}

}
