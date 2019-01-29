package aaa.helpers.jobs;

import java.time.LocalDateTime;
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
import com.exigen.ipb.eisa.utils.batchjob.SoapJobActions;
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

	public static void createJob(JobGroup job) {
		SoapJobActions soapJob = new SoapJobActions();
		if (!soapJob.isJobExist(job)) {
			soapJob.createJob(job);
		}
	}

	public static void createJob(Job job) {
		createJob(new JobGroup(job));
	}

	private static void executeJobImpl(JobGroup jobName) {
		log.info(getFullName() + " is running job: " + jobName);
		try {
			new SoapJobActions().startJob(jobName);
		} catch (Exception ie) {
			throw new IstfException(String.format("SOAP Job '%s' run failed:\n", jobName), ie);
		}
	}

	private static void executeJobLocally(JobGroup job) {
		clearFolders(job);
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
					log.warn(String.format("IstfException was caught (job='%s',testName='%s',testThreadId=%d,curThreadId=%d)", job.getGroupName(), testName, testThreadId, curThreadId), e);
					jobException.set(e);
					return Boolean.FALSE;

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
