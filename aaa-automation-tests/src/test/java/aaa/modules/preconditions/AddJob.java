package aaa.modules.preconditions;

import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;

public class AddJob {

	@Test
	public void addJob() {
		SoapJobActions service = new SoapJobActions();
		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.aaaRefundDisbursementAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.aaaRefundDisbursementAsyncJob.getJobName()));
		}
		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.aaaRefundsDisbursementRejectionsAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.aaaRefundsDisbursementRejectionsAsyncJob.getJobName()));
		}
		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.aaaRefundGenerationAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.aaaRefundGenerationAsyncJob.getJobName()));
		}

		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.aaaRefundDisbursementRecieveInfoJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.aaaRefundDisbursementRecieveInfoJob.getJobName()));
		}

		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.aaaRefundCancellationAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.aaaRefundCancellationAsyncJob.getJobName()));
		}
		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.renewalClaimOrderAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.renewalClaimOrderAsyncJob.getJobName()));
		}
		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.aaaRecurringPaymentsProcessingJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.aaaRecurringPaymentsProcessingJob.getJobName()));
		}
		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.aaaCollectionCancelDebtBatchAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.aaaCollectionCancelDebtBatchAsyncJob.getJobName()));
		}
		if (!service.isJobExist(JobGroup.fromSingleJob(Jobs.ledgerStatusUpdateJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(Jobs.ledgerStatusUpdateJob.getJobName()));
		}
		JobUtils.executeJob(Jobs.ledgerStatusUpdateJob);
	}
}