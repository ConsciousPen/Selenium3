package aaa.modules.batch;

import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.jobs.Job;
import aaa.modules.bct.batch.BackendJobNamesEnum;

public class BackendJobNames {
	private static Logger log = LoggerFactory.getLogger(BackendJobNames.class);

	/**
	 * This names are used in the database and at the Job's logs page (Scheduler -> Job details) .
	 * @param  -> Job job
	 * @return String jobName
	 */
	public static String getBackEndJobNames(Job job) {
		String backendJobName = null;
		for (BackendJobNamesEnum item : BackendJobNamesEnum.values()) {
			backendJobName = item.getGroupJobsName().equals(job) || item.getJobName().equals(job) ? item.getBackendJobName() : "";
			log.info("UI jobname {} : Backend jobname: {}", job, backendJobName);
		}

		if(backendJobName==null || backendJobName.isEmpty()){
			throw new NoSuchElementException("Backend jobname doesn't exist for : " + job.getJobName());
		}
		return backendJobName;
	}
}

