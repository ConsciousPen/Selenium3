package aaa.modules.bct.batch;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.jobs.GroupJobs;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.modules.bct.BctType;

public class BatchTest extends BackwardCompatibilityBaseTest {

	@Override
	protected BctType getBctType() {
		return BctType.BATCH_TEST;
	}

	@Parameters({"state"})
	@Test(description = "batchJob01.xml")
	public void BCT_BTCH_AAA_BATCH_MARKER(@Optional("") String state) {
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob02.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_005(@Optional("") String state){
		executeBatchTest(GroupJobs.grouprenewalClaimOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob03.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_004(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaMvrRenewBatchOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob04.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_003(@Optional("") String state){
		executeBatchTest(GroupJobs.groupmembershipRenewalBatchOrderJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob05.xml")
	public void BCT_BTCH_POLICY_STATUS_UPDATE(@Optional("") String state) {
		executeBatchTest(GroupJobs.groupPolicyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob06.xml")
	public void BCT_BTCH_Process_AcceptPayments_RecurringPayments_034(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaRecurringPaymentsProcessingJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob07.xml")
	public void BCT_BTCH_Manage_Payment_Exceptions_Payment_033(@Optional("") String state){
		executeBatchTest(GroupJobs.groupapplyPendingTransactionsAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob8.xml")
	public void BCT_BTCH_Cancellation_Cancellation_Notice_Bill_023(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaacancellationNoticeGenerationJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob9.xml")
	public void BCT_BTCH_Cancellation_Legal_Notice_028(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaacancellationConfirmationGenerationJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob10.xml")
	public void BCT_BTCH_Cancellation_Earned_Premium_025(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaCollectionCancellDebtBatchJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob11.xml")
	public void BCT_BTCH_Process_Refunds_Process_Refunds_032(@Optional("") String state){
		executeBatchTest(GroupJobs.grouprefundGenerationJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob12.xml")
	public void BCT_BTCH_Manage_OffcycleBill_031(@Optional("") String state){
		executeBatchTest(GroupJobs.groupoffCycleInvoiceGenerationJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob13.xml")
	public void BCT_BTCH_Generate_Bill_Generate_Installment_Bill_029(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaBillingInvoiceGenerationAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob14.xml")
	public void BCT_BTCH_Cancellation_Future_Effective_Cancellation_024(@Optional("") String state){
		executeBatchTest(GroupJobs.groupchangeCancellationPendingPoliciesStatusJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob15.xml")
	public void BCT_BTCH_Automated_Renewal_DoNotRenew_030(@Optional("") String state){
		executeBatchTest(GroupJobs.groupPolicyDoNotRenewAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob16.xml")
	public void BCT_BTCH_Automated_Renewal_Image_Data_Gather_001(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaapolicyAutomatedRenewalAsyncTaskGenerationJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob17.xml")
	public void BCT_BTCH_Automated_Renewal_Image_Data_Gather_002(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaRenewalDataRefreshAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob18.xml")
	public void BCT_BTCH_RENEWAL_VALIDATION(@Optional("") String state) {
		executeBatchTest(GroupJobs.grouprenewalValidationAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob19.xml")
	public void BCT_BTCH_Automated_Renewal_Premium_Calculation_007(@Optional("") String state){
		executeBatchTest(GroupJobs.grouprenewalImageRatingAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob20.xml")
	public void BCT_BTCH_Automated_Renewal_Proposed_Offer_009(@Optional("") String state){
		executeBatchTest(GroupJobs.grouprenewalOfferAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob21.xml")
	public void BCT_BTCH_Automated_NonRenewal_CustomerDeclined_Rewrite_017(@Optional("") String state){
		executeBatchTest(GroupJobs.grouplapsedRenewalProcessJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob22.xml")
	public void BCT_BTCH_Automated_Renewal_General_Renewal_Reminder_Notices_020(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob23.xml")
	public void BCT_BTCH_Automated_Renewal_Generate_Renewal_Bill_010(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaRenewalNoticeBillAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob24.xml")
	public void BCT_BTCH_Automated_Renewal_Preconversion_Renewal_Notice_022(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaPreRenewalNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_aaaAutomatedProcessingInitiationJob(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaAutomatedProcessingInitiationJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob26.xml")
	public void BCT_BTCH_automatedProcessingRatingJob(@Optional("") String state){
		executeBatchTest(GroupJobs.groupautomatedProcessingRatingJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob27.xml")
	public void BCT_BTCH_Automated_Renewal_Generate_Mortgagee_Bill_Reminder_Notice_018(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob28.xml")
	public void BCT_BTCH_aaaRefundDisbursementAsyncJob(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaRefundDisbursementAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob29.xml")
	public void BCT_BTCH_aaaScheatmentProcessAsyncJob(@Optional("") String state){
		executeBatchTest(GroupJobs.groupAAAEscheatmentProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test(description = "batchJob30.xml")
	public void BCT_BTCH_aaaGenerateEscheatmentReportJob(@Optional("") String state){
		executeBatchTest(GroupJobs.groupaaaGenerateEscheatmentReportJob);
	}

}