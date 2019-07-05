package aaa.helpers.jobs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.ws.model.WSJobSummary;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import aaa.helpers.ssh.RemoteHelper;
import aaa.modules.BaseTest;
import toolkit.exceptions.IstfException;

public class JobUtils {

	private static Logger log = LoggerFactory.getLogger(JobUtils.class);
	private static LocalDateTime currentPhase;

	public static void executeJob(JobGroup job, Boolean forceExecution) {
		CSAAApplicationFactory.get().closeAllApps();
		if (TimeShiftTestUtil.isPEFAvailable()) {
			executeJobByPEF(job, forceExecution);
		} else {
			executeJobLocally(job);
		}
	}

	public static void executeJob(JobGroup job) {
		executeJob(job, false);
	}

	public static void executeJob(Job job) {
		executeJob(JobGroup.fromSingleJob(job), false);
	}

	public static void executeJob(Job job, Boolean forceExecution) {
		executeJob(JobGroup.fromSingleJob(job), forceExecution);
	}

	public static void createJob(JobGroup job) {
		CsaaSoapJobService soapJob = new CsaaSoapJobService();
		if (!soapJob.isJobExist(job)) {
			soapJob.createJob(job);
		}
	}

	public static void createJob(Job job) {
		createJob(new JobGroup(job));
	}

	public static Boolean isJobExist(JobGroup job) {
		return new CsaaSoapJobService().isJobExist(job);
	}

	public static Boolean isJobExist(Job job) {
		return isJobExist(new JobGroup(job));
	}

	/**
	 * This methods grabs all job execution and select the latest run, according to todays date.
	 * @param jobGroup
	 * @return latest today's job run
	 */

	public static WSJobSummary getLatestJobRun(JobGroup jobGroup) {
		return getLatestJobRun(jobGroup, TimeSetterUtil.getInstance().getCurrentTime());
	}

	public static WSJobSummary getLatestJobRun(JobGroup jobGroup, LocalDateTime localDateTime) {
		CsaaSoapJobService soapJobActions = new CsaaSoapJobService();
		// Get list of all job runs
		List<WSJobSummary> jobSummaries = soapJobActions.getStatusResponse(jobGroup).getBatchSummary().getJobSummary();
		// store all jobSummaries ended today
		List<WSJobSummary> mostRecentJobSummaries = jobSummaries.stream().filter(wsJobSummary -> wsJobSummary.getEndTime().contains(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).collect(Collectors.toList());

		if (mostRecentJobSummaries.isEmpty()) {
			throw new IstfException("Job summary is not present for this date " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}
		// Select jobSummary with biggest/latest EndTime
		return mostRecentJobSummaries.stream().max(Comparator.comparing(WSJobSummary::getEndTime)).get();
	}

	private static void executeJobImpl(JobGroup jobName) {
		log.info(getFullName() + " is running job: " + jobName);
		try {
			CsaaSoapJobService soapJob = new CsaaSoapJobService();
			if (!soapJob.isJobExist(jobName)) {
				soapJob.createJob(jobName);
			}
			soapJob.startJob(jobName);
		} catch (Exception ie) {
			throw new IstfException(String.format("SOAP Job '%s' run failed:\n", jobName), ie);
		}
	}

	private static void executeJobLocally(JobGroup job) {
		//clearFolders(job);
		executeJobImpl(job);
	}

	private static void executeJobByPEF(JobGroup job, boolean forceExecution) {
		String testName = TimeSetterUtil.getInstance().getContext().getName();
		long testThreadId = Thread.currentThread().getId();
		AtomicReference<Exception> jobException = new AtomicReference<Exception>();

		Callable<Boolean> jobCallback = new Callable<Boolean>() {
			@Override
			public Boolean call() {
				long curThreadId = Thread.currentThread().getId();
				try {
					log.info("Attempt to execute job '{}' in callback (testName='{}',testThreadId={},curThreadId={})", job.getGroupName(), testName, testThreadId, curThreadId);
					executeJobLocally(job);
					log.info("Job '{}' callback was executed successfully (testName='{}')", job.getGroupName(), testName);
					return Boolean.TRUE;

				} catch (IstfException e) {
					log.error(String.format("IstfException was caught (job='%s',testName='%s',testThreadId=%d,curThreadId=%d)", job.getGroupName(), testName, testThreadId, curThreadId), e);
					jobException.set(e);
					return Boolean.TRUE;

				} catch (RuntimeException e) {
					log.error(String.format("Exception was caught (job='%s',testName='%s',testThreadId=%d,curThreadId=%d)", job.getGroupName(), testName, testThreadId, curThreadId), e);
					jobException.set(e);
					return Boolean.FALSE;

				}
			}
		};
		boolean isSuccess = TimeShiftTestUtil.getContext().executeJob(job.getGroupName(), jobCallback, forceExecution);

		if (!isSuccess) {
			throw new IstfException(String.format("Job '%s' execution failed: \n", job.getGroupName()), jobException.get());
		}
	}

	private static String getFullName() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String fullName = "";
		for (StackTraceElement element : stackTraceElements) {
			if (element.getClassName().startsWith("sun.reflect.") || element.getClassName().contains("java.lang.Class")) {
				break;
			}
			fullName = element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber() + " (" + BaseTest.getState() + ")";
		}

		return fullName;
	}

	private static void clearFolders(JobGroup job) {
		List<Job> jobsWithFolders = job.getJobs().stream().filter(jobChild -> !jobChild.getJobFolders().isEmpty()).collect(Collectors.toList());
		if (!jobsWithFolders.isEmpty()) {
			jobsWithFolders.stream().forEach(job1 -> RemoteHelper.get().clearFolder(job1.getJobFolders()));
		}
	}
}
