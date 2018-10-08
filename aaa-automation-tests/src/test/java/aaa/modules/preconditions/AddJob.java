package aaa.modules.preconditions;

import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;
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
	}
}
