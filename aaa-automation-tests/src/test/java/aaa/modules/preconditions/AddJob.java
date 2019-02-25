package aaa.modules.preconditions;

import org.testng.annotations.Test;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;

public class AddJob {

	@Test
	public void addJob() {
		JobUtils.createJob(BatchJob.aaaRefundDisbursementAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundsDisbursementRejectionsAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundGenerationAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundsDisbursementReceiveInfoAsyncJob);
		JobUtils.createJob(BatchJob.aaaRefundCancellationAsyncJob);
		JobUtils.createJob(BatchJob.renewalClaimOrderAsyncJob);
	}
}