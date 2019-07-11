package aaa.soap.batchJobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.base.application.Application;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import aaa.soap.AAAMarshaller;
import aaa.soap.batchJobService.endpoint.*;
import toolkit.config.PropertyProvider;
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
		LOG.debug("SOAP: startJobGroup REQUEST");
		LOG.debug(AAAMarshaller.modelToXml(request));

		JobGroupStartResponse response = null;
		try {
			response = batchJobPort.startJobGroup(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.debug("SOAP: startJobGroup RESPONSE");
		LOG.debug(AAAMarshaller.modelToXml(response));

		String jobLastExecutionState = waitForNAState(jobGroup);

	}


	public String waitForNAState(JobGroup jobGroup) {
		int delay = 2;
		if (getJobLastExecutionState(jobGroup).isEmpty()) {
			Application.wait(delay);
		}
		int maxWaitTime = Integer.parseInt(PropertyProvider.getProperty("test.batchjob.timeout", "1200000"));
		int currentWaitTime = 0;
		String currentStatus = getJobStatus(jobGroup);

		while (!"N/A".equalsIgnoreCase(currentStatus)) {
			Application.wait(delay);
			LOG.debug("Waiting for N/A state... Current wait time is {} ms.", currentWaitTime);
			if (currentWaitTime > maxWaitTime) {
				throw new IstfException(String.format("Job status : %s after %s ms waiting.", currentStatus, maxWaitTime));
			}
			currentWaitTime += delay;
			currentStatus = getJobStatus(jobGroup);
		}
		return currentStatus;
	}

	public String getJobLastExecutionState(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();
		String result;
		try {
			result = getJobStatusResponse(jobGroup).getStatus();
		} catch (Exception e) {
			throw new IstfException("Unable to get Job's status", e);
		}
		return result != null ? result : EMPTY_STRING;
	}

	protected void checkExecutionServiceIsAlive() {
		if (batchJobPort == null) {
			throw new IstfException("Batch Job execution service client was not initialized");
		}
	}

	public String getJobStatus(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();
		String result = getJobStatusResponse(jobGroup).getStatus();
		return result != null ? result : EMPTY_STRING;
	}

	public JobGroupStatusResponse getJobStatusResponse(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();
		JobGroupStatusResponse result = null;
		JobGroupStatusRequest request = new JobGroupStatusRequest();
		request.setJobGroupName(jobGroup.getGroupName());
		try {
			result = batchJobPort.getJobGroupStatus(request);
			LOG.debug("SOAP: getJobStatusResponse REQUEST");
			LOG.debug(AAAMarshaller.modelToXml(result));
		} catch (Exception e) {
			LOG.warn("Exception", e);
		}
		return result;
	}
}
