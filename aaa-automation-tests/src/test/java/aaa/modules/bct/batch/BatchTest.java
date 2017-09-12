package aaa.modules.bct.batch;

import aaa.helpers.jobs.GroupJobs;
import aaa.helpers.jobs.Job;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.modules.bct.BctType;
import org.testng.annotations.Test;

public class BatchTest extends BackwardCompatibilityBaseTest {

	@Override
	protected BctType getBctType() {
		return BctType.BATCH_TEST;
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Premium_Calculation_007() {
		Job job = GroupJobs.grouprenewalImageRatingAsyncTaskJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Premium_Calculation_007", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Proposed_Offer_009() {
		Job job = GroupJobs.grouprenewalOfferAsyncTaskJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Proposed_Offer_009", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Generate_Offer_Document_009() {
		Job job = GroupJobs.grouprenewalOfferAsyncTaskJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Generate_Offer_Document_009", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Image_Data_Gather_002() {
		Job job = GroupJobs.groupaaaRenewalDataRefreshAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Image_Data_Gather_002", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_003() {
		Job job = GroupJobs.groupmembershipRenewalBatchOrderJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Reports_Ordering_003", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_004() {
		Job job = GroupJobs.groupaaaMvrRenewBatchOrderAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Reports_Ordering_004", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_005() {
		Job job = GroupJobs.grouprenewalClaimOrderAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Reports_Ordering_005", job);
	}

	@Test
	public void BCT_BTCH_Expiration_Lapsed_Customer_Declined_015() {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		super.executeBatchTest("BCT_BTCH_Expiration_Lapsed_Customer_Declined_015", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Preconversion_Renewal_Notice_022() {
		Job job = GroupJobs.groupaaaPreRenewalNoticeAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Preconversion_Renewal_Notice_022", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_DoNotRenew_030() {
		Job job = GroupJobs.groupPolicyDoNotRenewAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_DoNotRenew_030", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Generate_Mortgagee_Bill_Reminder_Notice_018() {
		Job job = GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Generate_Mortgagee_Bill_Reminder_Notice_018", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Mortgagee_Bill_Expiration_Notice_019() {
		Job job = GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Mortgagee_Bill_Expiration_Notice_019", job);
	}

	@Test
	public void BCT_BTCH_Bind_Issue_Renewal_Lapsed_Issued_014() {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		super.executeBatchTest("BCT_BTCH_Bind_Issue_Renewal_Lapsed_Issued_014", job);
	}

	@Test
	public void BCT_BTCH_Manual_Renewal_Customer_Declined_Manual_016() {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		super.executeBatchTest("BCT_BTCH_Manual_Renewal_Customer_Declined_Manual_016", job);
	}

	@Test
	public void BCT_BTCH_Cancellation_Future_Effective_Cancellation_024() {
		Job job = GroupJobs.groupchangeCancellationPendingPoliciesStatusJob;
		super.executeBatchTest("BCT_BTCH_Cancellation_Future_Effective_Cancellation_024", job);
	}

	@Test
	public void BCT_BTCH_Cancellation_Earned_Premium_025() {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		super.executeBatchTest("BCT_BTCH_Cancellation_Earned_Premium_025", job);
	}

	@Test
	public void BCT_BTCH_Cancellation_Earned_Premium_026() {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		super.executeBatchTest("BCT_BTCH_Cancellation_Earned_Premium_026", job);
	}

	@Test
	public void BCT_BTCH_Cancellation_Earned_Premium_027() {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		super.executeBatchTest("BCT_BTCH_Cancellation_Earned_Premium_027", job);
	}

	@Test
	public void BCT_BTCH_Generate_Bill_Generate_Installment_Bill_029() {
		Job job = GroupJobs.groupaaaBillingInvoiceGenerationAsyncTaskJob;
		super.executeBatchTest("BCT_BTCH_Generate_Bill_Generate_Installment_Bill_029", job);
	}

	@Test
	public void BCT_BTCH_Process_Refunds_Process_Refunds_032() {
		Job job = GroupJobs.grouprefundGenerationJob;
		super.executeBatchTest("BCT_BTCH_Process_Refunds_Process_Refunds_032", job);
	}

	@Test
	public void BCT_BTCH_Process_AcceptPayments_RecurringPayments_034() {
		Job job = GroupJobs.groupaaaRecurringPaymentsProcessingJob;
		super.executeBatchTest("BCT_BTCH_Process_AcceptPayments_RecurringPayments_034", job);
	}

	@Test
	public void BCT_BTCH_Manage_OffcycleBill_031() {
		Job job = GroupJobs.groupoffCycleInvoiceGenerationJob;
		super.executeBatchTest("BCT_BTCH_Manage_OffcycleBill_031", job);
	}

	@Test
	public void BCT_BTCH_Manage_Payment_Exceptions_Payment_033() {
		Job job = GroupJobs.groupapplyPendingTransactionsAsyncJob;
		super.executeBatchTest("BCT_BTCH_Manage_Payment_Exceptions_Payment_033", job);
	}

	@Test
	public void BCT_BTCH_Cancellation_Cancellation_Notice_Bill_023() {
		Job job = GroupJobs.groupaaacancellationNoticeGenerationJob;
		super.executeBatchTest("BCT_BTCH_Cancellation_Cancellation_Notice_Bill_023", job);
	}

	@Test
	public void BCT_BTCH_Bind_Issue_Renewal_Proposed_Issued_012() {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		super.executeBatchTest("BCT_BTCH_Bind_Issue_Renewal_Proposed_Issued_012", job);
	}

	@Test
	public void BCT_BTCH_Bind_Issue_Renewal_Proposed_Lapsed_013() {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		super.executeBatchTest("BCT_BTCH_Bind_Issue_Renewal_Proposed_Lapsed_013", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Generate_Renewal_Bill_010() {
		Job job = GroupJobs.groupaaaRenewalNoticeBillAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Generate_Renewal_Bill_010", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_General_Renewal_Expiration_Notices_021() {
		Job job = GroupJobs.groupaaaRenewalReminderGenerationAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_General_Renewal_Expiration_Notices_021", job);
	}

	@Test
	public void BCT_BTCH_Automated_NonRenewal_CustomerDeclined_Rewrite_017() {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		super.executeBatchTest("BCT_BTCH_Automated_NonRenewal_CustomerDeclined_Rewrite_017", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_Image_Data_Gather_001() {
		Job job = GroupJobs.grouppolicyAutomatedRenewalAsyncTaskGenerationJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_Image_Data_Gather_001", job);
	}

	@Test
	public void BCT_BTCH_Automated_Renewal_General_Renewal_Reminder_Notices_020() {
		Job job = GroupJobs.groupaaaRenewalReminderGenerationAsyncJob;
		super.executeBatchTest("BCT_BTCH_Automated_Renewal_General_Renewal_Reminder_Notices_020", job);
	}

	@Test
	public void BCT_BTCH_Cancellation_Legal_Notice_028() {
		Job job = GroupJobs.groupaaacancellationConfirmationGenerationJob;
		super.executeBatchTest("BCT_BTCH_Cancellation_Legal_Notice_028", job);
	}
}
