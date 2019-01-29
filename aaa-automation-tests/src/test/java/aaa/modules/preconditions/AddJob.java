package aaa.modules.preconditions;

import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.SoapJobActions;
import aaa.helpers.jobs.BatchJob;

public class AddJob {

	@Test
	public void addJob() {
		SoapJobActions service = new SoapJobActions();
		if (!service.isJobExist(JobGroup.fromSingleJob(BatchJob.aaaRefundDisbursementAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(BatchJob.aaaRefundDisbursementAsyncJob.getJobName()));
		}
		if (!service.isJobExist(JobGroup.fromSingleJob(BatchJob.aaaRefundsDisbursementRejectionsAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(BatchJob.aaaRefundsDisbursementRejectionsAsyncJob.getJobName()));
		}
		if (!service.isJobExist(JobGroup.fromSingleJob(BatchJob.aaaRefundGenerationAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(BatchJob.aaaRefundGenerationAsyncJob.getJobName()));
		}

		if (!service.isJobExist(JobGroup.fromSingleJob(BatchJob.aaaRefundsDisbursementReceiveInfoAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(BatchJob.aaaRefundsDisbursementReceiveInfoAsyncJob.getJobName()));
		}

		if (!service.isJobExist(JobGroup.fromSingleJob(BatchJob.aaaRefundCancellationAsyncJob.getJobName()))) {
			service.createJob(JobGroup.fromSingleJob(BatchJob.aaaRefundCancellationAsyncJob.getJobName()));
		}
	}
}