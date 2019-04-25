package aaa.modules.batch;

import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.jobs.Job;

public class BackendJobNames {
	private static Logger log = LoggerFactory.getLogger(BackendJobNames.class);

	/**
	 * These names are used in the database and at the Job's logs page (Scheduler -> Job details) .
	 * @param  -> Job job
	 * @return String jobName
	 */
	public static String getBackendJobNames(Job job) {
		String backendJobName = null;
		for (BackendJobNamesEnum item : BackendJobNamesEnum.values()) {
			if(item.getGroupJobsName().equals(job) || item.getJobName().equals(job)){
				backendJobName = item.getBackendJobName();
				break;
			}else{
				backendJobName = "";
			}
		}

		if(backendJobName==null || backendJobName.isEmpty()){
			throw new NoSuchElementException("Backend jobname doesn't exist for : " + job.getJobName());
		}
		return backendJobName;
	}
}

