/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ipb.etcsa.utils.batchjob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.base.app.Application;
import com.exigen.ipb.etcsa.utils.batchjob.listener.DefaultJobListener;
import com.exigen.ipb.etcsa.utils.batchjob.listener.JobListener;
import com.exigen.ipb.etcsa.utils.batchjob.ws.BatchJobExecutorService;
import com.exigen.ipb.etcsa.utils.batchjob.ws.BatchJobTrigger;
import com.exigen.ipb.etcsa.utils.batchjob.ws.model.*;
import toolkit.config.ClassConfigurator;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

/**
 * JobAction implementation for running batch jobs via BatchJobService soap web-service.<br/>
 * There is possibility to register you own {@link JobListener} implementation <br/>
 * via {@link ClassConfigurator}.<br/>
 * <p/>
 * {@code waitForResponse} is responsible for getting response <br/>
 * from BatchTriggerRequest only after job processing is finished. <br/>
 */
public class SoapJobActions implements JobActions {
	private static final Logger LOG = LoggerFactory.getLogger(SoapJobActions.class);
	private static final String EMPTY_STRING = "";
	@ClassConfigurator.Configurable
	private static long jobCreationWaitTime = 20000;
	@ClassConfigurator.Configurable
	private static boolean waitForResponse = true;
	@ClassConfigurator.Configurable(byClassName = true)
	private static JobListener listener = new DefaultJobListener();
	private BatchJobExecutorService service;
	private BatchJobTrigger jobTrigger;

	static {
		ClassConfigurator configurator = new ClassConfigurator(SoapJobActions.class);
		configurator.applyConfiguration();
	}

	public SoapJobActions() {
		try {
			service = new BatchJobExecutorService();
			jobTrigger = service.getBatchJobExecutorPort();
		} catch (Exception e) {
			//ignore
		}
	}

	@Override
	public void createJob(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();
		JobGroupCreateRequest request = new JobGroupCreateRequest();
		request.setGroupName(jobGroup.getGroupName());
		for (Job job : jobGroup.getJobs()) {
			request.getJobs().add(convertJobToModel(job));
		}
		JobGroupCreateResponse response = jobTrigger.createJobGroup(request);
		String status = response.getServiceResponse().getResponseCode().value();
		waitForJobIsCreated(jobGroup);
		LOG.info("Group {} with job(s) {} has been successfully created", jobGroup.getGroupName(), jobGroup.getJobs());
	}

	@Override
	public String getJobState(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();
		String result = getJobStatusResponse(jobGroup).getCurrentState();
		return result != null ? result : EMPTY_STRING;
	}

	@Override
	public void startJob(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();

		BatchTriggerRequest request = new BatchTriggerRequest();
		request.setGroupName(jobGroup.getGroupName());
		request.setWaitForResponse(waitForResponse);
		jobGroup.getJobs().forEach(job -> request.getJobs().add(convertJobToModel(job)));

		LOG.info("Job {} execution started. Wait for response is set to {}.", jobGroup.getGroupName(), waitForResponse);
		BatchTriggerResponse response = jobTrigger.startBatchJob(request);

		if (!waitForResponse) {
			waitForIdleState(jobGroup);
		}
		String currentState = getJobLastExecutionState(jobGroup);

		if (!currentState.equals("Success")) {
			throw new IstfException(String.format("Execution state of job group %s is not equals to \"Idle\". Actual value is \"%s\"", jobGroup, currentState));
		}

		LOG.info("Job {} execution completed successfully", jobGroup.getGroupName());
	}

	@Override
	public void stopJob(JobGroup jobGroup) {
		BatchTriggerStopRequest request = new BatchTriggerStopRequest();
		request.setJobGroupName(jobGroup.getGroupName());
		jobTrigger.stopBatchJob(request);
	}

	@Override
	public boolean isJobExist(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();
		boolean result;
		try {
			result = !getJobStatusResponse(jobGroup).getServiceResponse().getResponseCode().equals(ServiceResponseCode.JOB_GROUP_DOES_NOT_EXIST);
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	protected String waitForIdleState(JobGroup jobGroup) {
		int delay = 2;
		if (getJobLastExecutionState(jobGroup).isEmpty()) {
			Application.wait(delay);
		}
		int maxWaitTime = Integer.parseInt(PropertyProvider.getProperty("test.batchjob.timeout", "1200000"));
		int currentWaitTime = 0;
		String currentState = getJobState(jobGroup);

		while (!currentState.equals("Idle")) {
			//while (jobStatus.equals("Running") || jobResult.equals("N/A") || executeCountAfter <= executeCountBefore) {
			Application.wait(delay);
			LOG.debug("Waiting for Idle state... Current wait time is {} ms.", currentWaitTime);
			if (currentWaitTime > maxWaitTime) {
				stopJob(jobGroup);
				throw new IstfException(String.format("Job status : %s after %s ms waiting. Terminated.", currentState, maxWaitTime));
			}
			currentWaitTime += delay;
			currentState = getJobState(jobGroup);
		}
		return currentState;
	}

	protected BatchStatusResponse getJobStatusResponse(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();
		BatchStatusResponse result = null;
		BatchStatusRequest request = new BatchStatusRequest();
		request.setJobGroupName(jobGroup.getGroupName());
		try {
			result = jobTrigger.getBatchJobStatus(request);
		} catch (Exception e) {
			LOG.warn("Exception", e);
		}
		return result;
	}

	protected void checkExecutionServiceIsAlive() {
		if (jobTrigger == null) {
			throw new IstfException("Batch Job execution service client was not initialized");
		}
	}

	protected String getJobLastExecutionState(JobGroup jobGroup) {
		checkExecutionServiceIsAlive();
		String result;
		try {
			result = getJobStatusResponse(jobGroup).getBatchSummary().getLastExecutionResult();
		} catch (Exception e) {
			throw new IstfException("Unable to get Job's status", e);
		}
		return result != null ? result : EMPTY_STRING;
	}

	/**
	 *
	 * @param response
	 * @return job state after job execution with waitForResponse = true
	 */
	protected String getJobLastExecutionState(BatchTriggerResponse response) {
		checkExecutionServiceIsAlive();
		String result;
		try {
			result = response.getBatchSummary().getLastExecutionResult();
		} catch (Exception e) {
			throw new IstfException("Unable to get Job's status", e);
		}
		return result != null ? result : EMPTY_STRING;
	}

	private void waitForJobIsCreated(JobGroup jobGroup) {
		boolean isJobExist = isJobExist(jobGroup);
		int waitTime = 0;
		while (!isJobExist && waitTime < jobCreationWaitTime) {
			Application.wait(waitTime += 2000);
			LOG.debug("JobGroup {} is still not ready for use. Total wait time {}.", jobGroup.getGroupName(), waitTime);
			isJobExist = isJobExist(jobGroup);
		}
	}

	private com.exigen.ipb.etcsa.utils.batchjob.ws.model.Job convertJobToModel(Job job) {
		com.exigen.ipb.etcsa.utils.batchjob.ws.model.Job modelJob = new com.exigen.ipb.etcsa.utils.batchjob.ws.model.Job(job.getJobName());

		if (job.getJobParameters() != null) {
			com.exigen.ipb.etcsa.utils.batchjob.ws.model.Job.JobParameters modelJobParameters = new com.exigen.ipb.etcsa.utils.batchjob.ws.model.Job.JobParameters();

			for (String key : job.getJobParameters().keySet()) {
				com.exigen.ipb.etcsa.utils.batchjob.ws.model.Job.JobParameters.Parameter modelParameter = new com.exigen.ipb.etcsa.utils.batchjob.ws.model.Job.JobParameters.Parameter();
				modelParameter.setKey(key);
				modelParameter.setValue(job.getJobParameters().get(key));

				modelJobParameters.getParameter().add(modelParameter);
			}
			if (modelJobParameters.getParameter().size() > 0) {
				modelJob.setJobParameters(modelJobParameters);
			}
		}
		return modelJob;
	}
}
