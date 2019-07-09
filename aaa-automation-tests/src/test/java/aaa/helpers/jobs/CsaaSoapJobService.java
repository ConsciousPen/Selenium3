package aaa.helpers.jobs;

import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.SoapJobActions;
import com.exigen.ipb.eisa.utils.batchjob.ws.model.BatchStatusResponse;

public class CsaaSoapJobService extends SoapJobActions {

	public BatchStatusResponse getStatusResponse(JobGroup jobGroup) {
		return getJobStatusResponse(jobGroup);
	}
}
