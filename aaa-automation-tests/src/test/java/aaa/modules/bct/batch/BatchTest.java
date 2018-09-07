package aaa.modules.bct.batch;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.jobs.GroupJobs;
import aaa.helpers.jobs.Job;
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
		Job job = Jobs.aaaBatchMarkerJob;
		JobUtils.executeJob(job);
	}

	@Parameters({"state"})
	@Test(description = "batchJob02.xml")
	public void BCT_BTCH_POLICY_STATUS_UPDATE(@Optional("") String state) {
		Job job = GroupJobs.groupPolicyStatusUpdateJob;
		JobUtils.executeJob(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob03.xml")
	public void BCT_BTCH_Automated_Renewal_Image_Data_Gather_001(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouppolicyAutomatedRenewalAsyncTaskGenerationJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob04.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_004(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaMvrRenewBatchOrderAsyncJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob05.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_003(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupmembershipRenewalBatchOrderJob;
		executeBatchTest(job);
	}

	@Parameters({"state"})
	@Test(description = "batchJob06.xml")
	public void BCT_BTCH_RENEWAL_VALIDATION(@Optional("") String state) {
		Job job = GroupJobs.grouprenewalValidationAsyncTaskJob;
		JobUtils.executeJob(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob07.xml")
	public void BCT_BTCH_Automated_Renewal_Premium_Calculation_007(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouprenewalImageRatingAsyncTaskJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob08.xml")
	public void BCT_BTCH_Automated_Renewal_Proposed_Offer_009(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouprenewalOfferAsyncTaskJob;
		executeBatchTest(job);
	}

	@Parameters({"state"})
	@Test(description = "batchJob08.xml")
	public void BCT_BTCH_Automated_Renewal_Generate_Offer_Document_009(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouprenewalOfferAsyncTaskJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob09.xml")
	public void BCT_BTCH_Automated_Renewal_Preconversion_Renewal_Notice_022(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaPreRenewalNoticeAsyncJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob10.xml")
	public void BCT_BTCH_Automated_Renewal_DoNotRenew_030(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupPolicyDoNotRenewAsyncJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob11.xml")
	public void BCT_BTCH_Automated_Renewal_Generate_Mortgagee_Bill_Reminder_Notice_018(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob;
		executeBatchTest(job);
	}

	/*
	 * Verify that Mortgagee Bill expiration notice is generated for Renewal Policies set for
	 * Mortgagee Bill Payment Plan • Minimum Renewal payment has not been received 20 days prior to the 2nd Monthly Anniversary Date(HSRR2XX) after policy expiration date)
	 */
	@Parameters({"state"})
	@Test(description = "batchJob11.xml")
	public void BCT_BTCH_Automated_Renewal_Mortgagee_Bill_Expiration_Notice_019(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob12.xml")
	public void BCT_BTCH_Automated_Renewal_Image_Data_Gather_002(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaRenewalDataRefreshAsyncJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob13.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_005(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouprenewalClaimOrderAsyncJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob14.xml")
	public void BCT_BTCH_Automated_Renewal_Generate_Renewal_Bill_010(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaRenewalNoticeBillAsyncJob;
		executeBatchTest(job);
	}

	/*
	* Validate that “Renewal Reminder Notices” are generated for for customer declined AAA_CSA renewal quotes,
	* Policy Transaction type is ‘renewal’
	* Policy Status is ‘customerDeclined’ and policy lapsed with batch job processing.
	* Product code is ‘AAA_CSA’
	* Policy Contract Term is before ‘R + AAALapsedRenewalParameters#lapsedRenewalFirstDay’ for any type of policies except first term ADES renewals.
	* Policy Contract Term is before ‘R + AAALapsedRenewalParameters#lapsedRenewalFirstDayADES’ for first term ADES renewals.
	* Renewal Reminder Notice have not been generated yet for policy (sentRenewalReminderNotice IS NULL OR sentRenewalReminderNotice = 0)
	*/
	@Parameters({"state"})
	@Test(description = "batchJob15.xml")
	public void BCT_BTCH_Automated_Renewal_General_Renewal_Reminder_Notices_020(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaRenewalReminderGenerationAsyncJob;
		executeBatchTest(job);
	}

	/*
	 * Validate that Renewal Expiration Notices” are generated for proposed AAA_SS, AAA_HO_SS, AAA_PUP_SS renewal quotes.
	 * Policy Transaction type is ‘renewal’
	 * Policy Status is ‘proposed’
	 * Product code is ‘AAA_SS’, ‘AAA_HO_SS’, ‘AAA_PUP_SS’
	 * Minimum billing due is greater than 0
	 * Policy Contract Term is before ‘R + AAALapsedRenewalParameters#renewalExpNoticeTerm’.
	 * Expiration Notice document generation event is not yet dispatched
	 */
	@Parameters({"state"})
	@Test(description = "batchJob15.xml")
	public void BCT_BTCH_Automated_Renewal_General_Renewal_Expiration_Notices_021(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaRenewalReminderGenerationAsyncJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob16.xml")
	public void BCT_BTCH_Manage_Payment_Exceptions_Payment_033(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupapplyPendingTransactionsAsyncJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob17.xml")
	public void BCT_BTCH_Process_AcceptPayments_RecurringPayments_034(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaRecurringPaymentsProcessingJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob18.xml")
	public void BCT_BTCH_Cancellation_Cancellation_Notice_Bill_023(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaacancellationNoticeGenerationJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob19.xml")
	public void BCT_BTCH_Cancellation_Legal_Notice_028(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaacancellationConfirmationGenerationJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob20.xml")
	public void BCT_BTCH_Cancellation_Future_Effective_Cancellation_024(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupchangeCancellationPendingPoliciesStatusJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob21.xml")
	public void BCT_BTCH_Cancellation_Earned_Premium_025(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob21.xml")
	public void BCT_BTCH_Cancellation_Earned_Premium_026(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob21.xml")
	public void BCT_BTCH_Cancellation_Earned_Premium_027(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob22.xml")
	public void BCT_BTCH_Generate_Bill_Generate_Installment_Bill_029(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupaaaBillingInvoiceGenerationAsyncTaskJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob23.xml")
	public void BCT_BTCH_Manage_OffcycleBill_031(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.groupoffCycleInvoiceGenerationJob;
		executeBatchTest(job);
	}

	/*
	 * Verfiy if an offcycle bill has been generated for policies on which EP endorsement has been done on DDX-20.
	 * Endorsement would be updating the BI limit to max for auto policy and Coverage A limit to max for property policy.
	 */
	@Parameters({"state"})
	@Test(description = "batchJob24.xml")
	public void BCT_BTCH_Process_Refunds_Process_Refunds_032(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouprefundGenerationJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Bind_Issue_Renewal_Proposed_Issued_012(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(job);
	}

	/*
	 * Validate that renewal is not issued for all proposed renewals for which Customer has not paid the downpayment for the renewal term before expiry and that renewal is lapsed.
	 * Status remains Proposed
	 */
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Bind_Issue_Renewal_Proposed_Lapsed_013(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Bind_Issue_Renewal_Lapsed_Issued_014(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(job);
	}

	
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Expiration_Lapsed_Customer_Declined_015(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(job);
	}

	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Manual_Renewal_Customer_Declined_Manual_016(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(job);
	}

	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Automated_NonRenewal_CustomerDeclined_Rewrite_017(@Optional("") String state) throws IOException, ParseException {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(job);
	}

}
