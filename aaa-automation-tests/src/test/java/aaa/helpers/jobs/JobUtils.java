package aaa.helpers.jobs;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.exec.core.TimedTestContext;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;

import aaa.helpers.http.HttpJob;
import aaa.helpers.jobs.Jobs.JobState;
import aaa.helpers.ssh.RemoteHelper;
import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class JobUtils {
	
	private static Logger log = LoggerFactory.getLogger(JobUtils.class);

	private static LocalDateTime currentPhase;

	public static void executeJob(Job job, Boolean forceExecution) {
		if (isPef()) {
			LocalDateTime phaseTime = TimeSetterUtil.getInstance().getPhaseStartTime();
			if (!phaseTime.equals(currentPhase)) {
				Jobs.clearJobsState();
				currentPhase = phaseTime;
			}
		} else {
			Jobs.clearJobsState();
		}

		synchronized (job) {
			if (forceExecution)
				Jobs.setJobState(job.getJobName(), JobState.FALSE);
			switch (Jobs.getJobState(job.getJobName())) {
			case FALSE:
				try {
					if (isPef()) {
						executeJobByPEF(job, forceExecution);
					} else {
						executeJobLocally(job);
					}
					log.info(String.format("Job '%s' was executed successfully", job.getJobName()));
				} catch (Exception e) {
					Jobs.setJobState(job.getJobName(), JobState.FAILED);
					throw e;
					//throw new CustomTestException("Job " + job.getJobName() + " execution is FAILED ", e);
				}
				break;
			case TRUE:
				log.info(String.format("Job '%s' has been already executed", job.getJobName()));
				break;
			case FAILED:
				log.error(String.format("Job " + job.getJobName() + " execution is FAILED"));
				//throw new CustomTestException("Job " + job.getJobName() + " execution is FAILED");
			default:
				break;
			}
		}
	}

	public static void executeJob(Job job) {
		executeJob(job, false);
	}

	public static void executeJob(Job job, long pauseBefore) {
		try {
			log.info("Pause before " + job.getJobName() + " job execution is called");
			Thread.sleep(pauseBefore);
		} catch (InterruptedException e) {
			log.info("Pause before job execution is failed: ", e);
		}
		executeJob(job, false);
	}

	private static void executeJob(String jobName) {
		log.info(getFullName() + " is running job: " + jobName);
		try {
			try {
				HttpJob.executeJob(jobName);
			} catch (Exception ioe) {
				// Workaround of HTTP 502 error
				if (ioe instanceof IOException || ioe.getCause() instanceof IOException) {
					log.info("Failed during run of " + jobName + ". Trying to rerun.", ioe);
					HttpJob.executeJob(jobName);
				} else {
					throw ioe;
				}
			}
		} catch (Exception ie) {
			throw new IstfException(String.format("HTTP Job '%s' run failed:\n", jobName), ie);
		}
	}

	private static void executeJobLocally(Job job) {
		try {
			Jobs.setJobState(job.getJobName(), JobState.TRUE);
			if (!job.getJobFolders().isEmpty())
				RemoteHelper.clearFolder(job.getJobFolders());
			executeJob(job.getJobName());
		} catch (IstfException e) {
			throw e;
		}
	}

	private static void executeJobByPEF(final Job job, boolean forceExecution) {
		Jobs.setJobState(job.getJobName(), JobState.TRUE);
		final String testName = TimeSetterUtil.getInstance().getContext().getName();
		final long testThreadId = Thread.currentThread().getId();
		final AtomicReference<Exception> jobException = new AtomicReference<Exception>();

		Callable<Boolean> jobCallback = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				long curThreadId = Thread.currentThread().getId();
				try {
					log.info("Attempt to execute job '{}' in callback (testName='{}',testThreadId={},curThreadId={})", job.getJobName(), testName, testThreadId, curThreadId);
					if (!job.getJobFolders().isEmpty())
						RemoteHelper.clearFolder(job.getJobFolders());
					executeJob(job.getJobName());
					log.info("Job '{}' callback was executed successfully (testName='{}')", job.getJobName(), testName);
					return Boolean.TRUE;

				} catch (IstfException e) {
					log.warn(String.format("IstfException was caught (job='%s',testName='%s',testThreadId=%d,curThreadId=%d)", job.getJobName(), testName, testThreadId, curThreadId), e);
					jobException.set(e);
					return Boolean.FALSE;

				} catch (Exception e) {
					log.error(String.format("Exception was caught (job='%s',testName='%s',testThreadId=%d,curThreadId=%d)", job.getJobName(), testName, testThreadId, curThreadId), e);
					jobException.set(e);
					return Boolean.FALSE;
				}
			}
		};

		boolean isSuccess = getContext().executeJob(job.getJobName(), jobCallback, forceExecution);

		if (!isSuccess) {
			throw new IstfException(String.format("Job '%s' execution failed: \n", job.getJobName()), jobException.get());
		}
	}

	public static Boolean isPefManager() {
		return StringUtils.isNotBlank(PropertyProvider.getProperty("timeshift.controller.class"));
	}

	private static TimedTestContext getContext() {
		return TimeShiftTestUtil.getContext();
	}

	private static Boolean isPef() {
		return TimeShiftTestUtil.isContextAvailable();
	}

	private static String getFullName() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String fullName = new String();
		for (StackTraceElement element : stackTraceElements) {
			if (element.getClassName().startsWith("sun.reflect.") || element.getClassName().contains("java.lang.Class"))
				break;
			fullName = element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber() + " (" + BaseTest.getState() + ")";
		}

		return fullName;
	}

}
