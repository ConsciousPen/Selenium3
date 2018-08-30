package aaa.modules.bct.batch;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.jobs.GroupJobs;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
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
		Job job = GroupJobs.groupaaaBatchMarkerJob;
		JobUtils.executeJob(job);
	}

	@Parameters({"state"})
	@Test(description = "batchJob02.xml")
	public void BCT_BTCH_POLICY_STATUS_UPDATE(@Optional("") String state) {
		Job job = GroupJobs.groupPolicyStatusUpdateJob;
		JobUtils.executeJob(job);
	}

	/*
	 * Validate the policies for which renewal image should be generated
	 */
	@Parameters({"state"})
	@Test(description = "batchJob03.xml")
	public void BCT_BTCH_Automated_Renewal_Image_Data_Gather_001(@Optional("") String state) {
		Job job = GroupJobs.grouppolicyAutomatedRenewalAsyncTaskGenerationJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate the policies for which MVR report has to be ordered prior to renewals
	 */
	@Parameters({"state"})
	@Test(description = "batchJob04.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_004(@Optional("") String state) {
		Job job = GroupJobs.groupaaaMvrRenewBatchOrderAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate the policies for which membership report has to be order prior to renewals
	 */
	@Parameters({"state"})
	@Test(description = "batchJob05.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_003(@Optional("") String state) {
		Job job = GroupJobs.groupmembershipRenewalBatchOrderJob;
		executeBatchTest(getMethodName(), job);
	}

	@Parameters({"state"})
	@Test(description = "batchJob06.xml")
	public void BCT_BTCH_RENEWAL_VALIDATION(@Optional("") String state) {
		Job job = GroupJobs.grouprenewalValidationAsyncTaskJob;
		JobUtils.executeJob(job);
	}

	/*
	 * Validate if the premiums are calculated for all eligible renewal images and updates pending renewal images with status ‘dataGather’ to pending renewal previews with status ‘rated’.
	 */
	@Parameters({"state"})
	@Test(description = "batchJob07.xml")
	public void BCT_BTCH_Automated_Renewal_Premium_Calculation_007(@Optional("") String state) {
		Job job = GroupJobs.grouprenewalImageRatingAsyncTaskJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate that 35 days before the policy expires, renewal rules are applied to all eligible policies and  status is updated as "Proposed" if none of the renewal rules fail
	 */
	@Parameters({"state"})
	@Test(description = "batchJob08.xml")
	public void BCT_BTCH_Automated_Renewal_Proposed_Offer_009(@Optional("") String state) {
		Job job = GroupJobs.grouprenewalOfferAsyncTaskJob;
		executeBatchTest(getMethodName(), job);
	}

	@Parameters({"state"})
	@Test(description = "batchJob08.xml")
	public void BCT_BTCH_Automated_Renewal_Generate_Offer_Document_009(@Optional("") String state) {
		Job job = GroupJobs.grouprenewalOfferAsyncTaskJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if pre-conversion renewal notice is generated--Docgen validation
	 */
	@Parameters({"state"})
	@Test(description = "batchJob09.xml")
	public void BCT_BTCH_Automated_Renewal_Preconversion_Renewal_Notice_022(@Optional("") String state) {
		Job job = GroupJobs.groupaaaPreRenewalNoticeAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if Non-Offer alerts has been placed on all policies for which do not renew action was performed on the policy by underwriter/agent) prior to the policy expiration.
	 */
	@Parameters({"state"})
	@Test(description = "batchJob10.xml")
	public void BCT_BTCH_Automated_Renewal_DoNotRenew_030(@Optional("") String state) {
		Job job = GroupJobs.groupPolicyDoNotRenewAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify that Mortgagee Bill reminder notice is generated for Renewal Policies set for Mortgagee Bill Payment Plan • ({Minimum Renewal payment has not been received 10 days after expiry (HSRRXX)}
	 */
	@Parameters({"state"})
	@Test(description = "batchJob11.xml")
	public void BCT_BTCH_Automated_Renewal_Generate_Mortgagee_Bill_Reminder_Notice_018(@Optional("") String state) {
		Job job = GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify that Mortgagee Bill expiration notice is generated for Renewal Policies set for
	 * Mortgagee Bill Payment Plan • Minimum Renewal payment has not been received 20 days prior to the 2nd Monthly Anniversary Date(HSRR2XX) after policy expiration date)
	 */
	@Parameters({"state"})
	@Test(description = "batchJob11.xml")
	public void BCT_BTCH_Automated_Renewal_Mortgagee_Bill_Expiration_Notice_019(@Optional("") String state) {
		Job job = GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate that the companion policy information is updated in the renewal policy summary
	 */
	@Parameters({"state"})
	@Test(description = "batchJob12.xml")
	public void BCT_BTCH_Automated_Renewal_Image_Data_Gather_002(@Optional("") String state) {
		Job job = GroupJobs.groupaaaRenewalDataRefreshAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if CLAIM report is ordered for policies which are about to expire (-63 to -57 days until policy expiration).
	 */
	@Parameters({"state"})
	@Test(description = "batchJob13.xml")
	public void BCT_BTCH_Automated_Renewal_Reports_Ordering_005(@Optional("") String state) {
		Job job = GroupJobs.grouprenewalClaimOrderAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if the renewal bill is generated at given timeline
	 */
	@Parameters({"state"})
	@Test(description = "batchJob14.xml")
	public void BCT_BTCH_Automated_Renewal_Generate_Renewal_Bill_010(@Optional("") String state) {
		Job job = GroupJobs.groupaaaRenewalNoticeBillAsyncJob;
		executeBatchTest(getMethodName(), job);
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
	public void BCT_BTCH_Automated_Renewal_General_Renewal_Reminder_Notices_020(@Optional("") String state) {
		Job job = GroupJobs.groupaaaRenewalReminderGenerationAsyncJob;
		executeBatchTest(getMethodName(), job);
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
	public void BCT_BTCH_Automated_Renewal_General_Renewal_Expiration_Notices_021(@Optional("") String state) {
		Job job = GroupJobs.groupaaaRenewalReminderGenerationAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify if the payments made between 9PM and 12AM PCT have the status as pending
	 */
	@Parameters({"state"})
	@Test(description = "batchJob16.xml")
	public void BCT_BTCH_Manage_Payment_Exceptions_Payment_033(@Optional("") String state) {
		Job job = GroupJobs.groupapplyPendingTransactionsAsyncJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify if the recurring payment has been posted
	 */
	@Parameters({"state"})
	@Test(description = "batchJob17.xml")
	public void BCT_BTCH_Process_AcceptPayments_RecurringPayments_034(@Optional("") String state) {
		Job job = GroupJobs.groupaaaRecurringPaymentsProcessingJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate that Cancel Notice is sent for the policy that is in  billing account due date and min due exceeds the allowed tolerence level
	 */
	@Parameters({"state"})
	@Test(description = "batchJob18.xml")
	public void BCT_BTCH_Cancellation_Cancellation_Notice_Bill_023(@Optional("") String state) {
		Job job = GroupJobs.groupaaacancellationNoticeGenerationJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate policies with non-payment mentare cancelled, debt processing is performed and statement for cancellation/legal notice is generated.
	 */
	@Parameters({"state"})
	@Test(description = "batchJob19.xml")
	public void BCT_BTCH_Cancellation_Legal_Notice_028(@Optional("") String state) {
		Job job = GroupJobs.groupaaacancellationConfirmationGenerationJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if the policies that were cancelled with future date are updated with correct status
	 */
	@Parameters({"state"})
	@Test(description = "batchJob20.xml")
	public void BCT_BTCH_Cancellation_Future_Effective_Cancellation_024(@Optional("") String state) {
		Job job = GroupJobs.groupchangeCancellationPendingPoliciesStatusJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if the payment due can be written off or not for policies cancelled before 15 days and if the payment due cannot be written off, Earned Premium Notice 1  is generated
	 */
	@Parameters({"state"})
	@Test(description = "batchJob21.xml")
	public void BCT_BTCH_Cancellation_Earned_Premium_025(@Optional("") String state) {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if the payment due can be written off or not for policies cancelled before 30 days and if the payment due cannot be written off, Earned Premium Notice 2  is generated
	 */
	@Parameters({"state"})
	@Test(description = "batchJob21.xml")
	public void BCT_BTCH_Cancellation_Earned_Premium_026(@Optional("") String state) {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if the payment due can be written off or not for policies cancelled before 45 days and if the payment due cannot be written off, Earned Premium Notice 3  is generated
	 */
	@Parameters({"state"})
	@Test(description = "batchJob21.xml")
	public void BCT_BTCH_Cancellation_Earned_Premium_027(@Optional("") String state) {
		Job job = GroupJobs.groupaaaCollectionCancellDebtBatchJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if installment bills are getting generated
	 */
	@Parameters({"state"})
	@Test(description = "batchJob22.xml")
	public void BCT_BTCH_Generate_Bill_Generate_Installment_Bill_029(@Optional("") String state) {
		Job job = GroupJobs.groupaaaBillingInvoiceGenerationAsyncTaskJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify if refund is generated for policies that are eligible for refund
	 */
	@Parameters({"state"})
	@Test(description = "batchJob23.xml")
	public void BCT_BTCH_Manage_OffcycleBill_031(@Optional("") String state) {
		Job job = GroupJobs.groupoffCycleInvoiceGenerationJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verfiy if an offcycle bill has been generated for policies on which EP endorsement has been done on DDX-20.
	 * Endorsement would be updating the BI limit to max for auto policy and Coverage A limit to max for property policy.
	 */
	@Parameters({"state"})
	@Test(description = "batchJob24.xml")
	public void BCT_BTCH_Process_Refunds_Process_Refunds_032(@Optional("") String state) {
		Job job = GroupJobs.grouprefundGenerationJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate if renewal is issued for all proposed renewals for which Customer has paid the downpayment for the renewal term before expiry
	 */
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Bind_Issue_Renewal_Proposed_Issued_012(@Optional("") String state) {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Validate that renewal is not issued for all proposed renewals for which Customer has not paid the downpayment for the renewal term before expiry and that renewal is lapsed.
	 * Status remains Proposed
	 */
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Bind_Issue_Renewal_Proposed_Lapsed_013(@Optional("") String state) {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify that for lapsed renewals ,if the payment comes during the first 10 days of the renewal term,the renewal is automatically issued
	 */
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Bind_Issue_Renewal_Lapsed_Issued_014(@Optional("") String state) {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify that for lapsed renewals ,if the payment does not come during the first 10 days of the renewal term,the renewal status is updated as 'Customer Declined'
	 */
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Expiration_Lapsed_Customer_Declined_015(@Optional("") String state) {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify that for customer declined renewals ,if the payment comes between 10-30 days of the renewal term,the renewal is flagged for Manual renewal
	 */
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Manual_Renewal_Customer_Declined_Manual_016(@Optional("") String state) {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(getMethodName(), job);
	}

	/*
	 * Verify that for customer declined renewals ,if the payment comes after 30 days of the renewal term,system geenrates task ' Attempt to secure Rewrite',but renewal cannot e re-instated
	 */
	@Parameters({"state"})
	@Test(description = "batchJob25.xml")
	public void BCT_BTCH_Automated_NonRenewal_CustomerDeclined_Rewrite_017(@Optional("") String state) {
		Job job = GroupJobs.grouplapsedRenewalProcessJob;
		executeBatchTest(getMethodName(), job);
	}

	public String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
}
