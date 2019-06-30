package aaa.soap.batchJobService;

import static aaa.admin.modules.IAdmin.log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;
import aaa.soap.AAAMarshaller;
import aaa.soap.batchJobService.endpoint.BatchJobPort;
import aaa.soap.batchJobService.endpoint.BatchJobPortImplService;
import aaa.soap.batchJobService.endpoint.JobGroupStartRequest;
import aaa.soap.batchJobService.endpoint.JobGroupStartResponse;
import toolkit.exceptions.IstfException;

public class BatchJobPortImplServiceClient {
	private static final Logger LOG = LoggerFactory.getLogger(BatchJobPortImplServiceClient.class);
	private static final String EMPTY_STRING = "";

	private BatchJobPortImplService service;
	private BatchJobPort batchJobPort;

	public BatchJobPortImplServiceClient() {
		service = new BatchJobPortImplService();
		batchJobPort = service.getBatchJobPortImplPort();
	}

	public void startJob(JobGroup jobGroup) {
		JobGroupStartRequest request = new JobGroupStartRequest();
		request.setJobGroupName(jobGroup.getGroupName());
		log.info("SOAP: REQUEST");
		log.info(AAAMarshaller.modelToXml(request));

		JobGroupStartResponse response = null;
		try {
			response = batchJobPort.startJobGroup(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("SOAP: RESPONSE");
		log.info(AAAMarshaller.modelToXml(response));

		SoapJobActions soapJobActions = new SoapJobActions();
		soapJobActions.waitForIdleState(jobGroup);

		String jobLastExecutionState = soapJobActions.getJobLastExecutionState(jobGroup);
		if (!jobLastExecutionState.equals("Success")) {
			throw new IstfException(String.format("Execution state of job group %s is not equals to \"Idle\". Actual value is \"%s\"", jobGroup, jobLastExecutionState));
		}
	}

}
