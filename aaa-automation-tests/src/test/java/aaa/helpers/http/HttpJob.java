package aaa.helpers.http;

import static aaa.common.enums.JobResultEnum.JobStatus;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.common.enums.JobResultEnum;
import aaa.helpers.http.impl.*;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class HttpJob {
	private static final String SCHEDULER_SUMMARY_FLOW = "/aaa-admin/admin/flow?_flowId=scheduler-summary-flow";
	private static final String ASYNC_TASK_STATISTICS_FLOW = "/aaa-admin/admin/flow?_flowId=async-task-statistics-flow";

	private static final String ASYNC_MANAGER_REGEX = "<a([^>]+)id=\"asyncTaskSummaryForm\\:([^\"]+)\"([^>]+)>([^>]+)</a>";
	private static final String WAITING_REGEX = ".*Waiting:.*[^0-9]([0-9]+)<";
	private static final String PROCESSING_REGEX = ".*Processing:.*[^0-9]([0-9]+)<";
	private static final String FLOW_QUERY_STRING = "<form id=\"asyncTaskSummaryForm\" name=\"asyncTaskSummaryForm\" method=\"post\" action=\"([^\"]+)\"";

	private static final String JOB_TOTAL_RUNS_STAT = "(\\d+\\sPassed[^>]+Interrupted[^>])";
	private static final String JOB_STATUS_REGEX = "/JOB_NAME/\\s<\\/a><[^>]+>\\(([a-zA-Z]+\\))<\\/span>";
	private static final String JOB_EXECUTE_BUTTON_REGEX = "<a [^\\>]*id=\"(jobs:jobsTable:\\d+:start-job)\"";
	private static final String JOB_ROW_SPLITTER_REGEX = "<tr data-ri=\"\\d+\"";
	private static final String JOB_LATEST_RUN_STATUS = "Last Run.*\\(([a-zA-Z]+)\\)<";

	private static final int JOB_SLEEP_RERUN = 1500;
	private static final String JOB_ADD_PARAMS_FILENAME = "job_run.txt";
	private static final String STOP_ASYNC_PARAMS_FILENAME = "stop_async.txt";
	private static final long ASYNC_TIMEOUT = 300000;

	private static Logger log = LoggerFactory.getLogger(HttpJob.class);
	private static int JOB_TIMEOUT = Integer.parseInt(PropertyProvider.getProperty("test.batchjob.timeout", "1200000"));

	private HttpJob() {
	}

	public static void checkAsyncManager(HttpAAARequestor httpRequestor) throws IOException, IstfException {
		//String refreshQuery = "/aaa-admin/admin/flow?_flowId=async-task-statistics-flow&_flowExecutionKey=e6s1&_windowId=W1522750697151";
		HttpQueryBuilder queryBuilder = new HttpQueryBuilder();
		queryBuilder.readParamsFile(JOB_ADD_PARAMS_FILENAME);

		getSchedulerSummaryPage(httpRequestor, ASYNC_TASK_STATISTICS_FLOW);
		String asyncManager = HttpHelper.find(httpRequestor.getResponse(), ASYNC_MANAGER_REGEX, 4);

		if (asyncManager.equalsIgnoreCase("start manager")) {
			HttpAAARequestor httpRequestor2 = HttpLogin.loginAd();
			getSchedulerSummaryPage(httpRequestor2, ASYNC_TASK_STATISTICS_FLOW);

			String flowUrl = HttpHelper.find(httpRequestor2.getResponse(), FLOW_QUERY_STRING).replace("amp;", "");
			String viewState = HttpHelper.find(httpRequestor2.getResponse(), HttpConstants.REGEX_VIEW_STATE);

			Map<String, String> mapping = new HashMap<String, String>();
			mapping.put("viewState", viewState);

			httpRequestor2.sendPostRequest(flowUrl, queryBuilder.buildQueryString(0, mapping));
			//Refresh Start
			//httpRequestor2.sendGetRequest(ASYNC_TASK_STATISTICS_FLOW);
			httpRequestor2.sendPostRequest(flowUrl, queryBuilder.buildQueryString(1, mapping));
			//Refresh End

			asyncManager = HttpHelper.find(httpRequestor2.getResponse(), ASYNC_MANAGER_REGEX, 4);
			if (!asyncManager.equalsIgnoreCase("stop manager")) {
				HttpLogout.logoutAdmin(httpRequestor2);
				throw new IstfException("HTTP ERROR: Async Manager was not started");
			} else {
				log.info("HTTP Job: Async manager started");
				HttpLogout.logoutAdmin(httpRequestor2);
			}
		}
	}

	public static void stopAsyncManager() throws IOException {
		log.info("HTTP: Starting login");
		HttpAAARequestor httpRequestor = HttpLogin.loginAd();
		log.info("HTTP: Logged in");
		log.info("HTTP: Stopping Assync Manager");
		HttpQueryBuilder queryBuilder = new HttpQueryBuilder();
		queryBuilder.readParamsFile(STOP_ASYNC_PARAMS_FILENAME);

		getSchedulerSummaryPage(httpRequestor, ASYNC_TASK_STATISTICS_FLOW);
		String asyncManager = HttpHelper.find(httpRequestor.getResponse(), ASYNC_MANAGER_REGEX, 4);

		if (asyncManager.equalsIgnoreCase("stop manager")) {
			String flowUrl = HttpHelper.find(httpRequestor.getResponse(), FLOW_QUERY_STRING).replace("amp;", "");
			httpRequestor.sendPostRequest(flowUrl, queryBuilder.buildQueryString(0, null));
			asyncManager = HttpHelper.find(httpRequestor.getResponse(), ASYNC_MANAGER_REGEX, 4);
			if (!asyncManager.equalsIgnoreCase("start manager")) {
				throw new IstfException("HTTP ERROR: Async Manager was not stoped");
			} else {
				log.info("HTTP: Async manager stoped");
			}
		}
	}

	public static synchronized void executeJob(String jobName) throws IOException, IstfException, InterruptedException {
		/* System.setProperty("http.proxyHost", "localhost");
		 System.setProperty("http.proxyPort", "8888");*/
		log.info("HTTP Job: ---> Started Job '" + jobName + "' execution");
		log.info("HTTP: Starting login");
		HttpAAARequestor httpRequestor = HttpLogin.loginAd();
		log.info("HTTP: Logged in");
		log.info("HTTP: Starting check Assync Manager");
		checkAsyncManager(httpRequestor);
		log.info("HTTP: Assync Manager check finished");
		log.info(String.format("HTTP: Starting job '%s' execution", jobName));
		runSingleJob(httpRequestor, jobName);
		log.info(String.format("HTTP: Job '%s' execution finished", jobName));
		if (jobName.equals("aaaRecurringPaymentsProcessingJob") || jobName.equals("aaaRemittanceFeedAsyncBatchReceiveJob") || jobName.equals("policyStatusUpdateJob")
				|| jobName.equals("aaaBillingInvoiceAsyncTaskJob") || jobName.equals("Renewal_Offer_Generation_Part2")) {
			log.info("HTTP: Starting check Assync Tasks");
			checkAsyncTask();
			log.info("HTTP: Check Assync Tasks finished");
		}
		log.info("HTTP: Starting logout");
		HttpLogout.logoutAdmin(httpRequestor);
		log.info("HTTP: Logout finished");
		log.info("HTTP Job: <--- Job '" + jobName + "' was executed successfully");
	}

	private static void runSingleJob(HttpAAARequestor httpRequestor, String jobName) throws IOException, IstfException, InterruptedException {
		getSchedulerSummaryPage(httpRequestor, SCHEDULER_SUMMARY_FLOW);
		if (httpRequestor.getResponse().contains(String.format("title=\"%s\"",jobName))) {
			String flowUrl = HtmlParser.getFlowUrl(httpRequestor.getResponse());
			/* Prepare params for job start */
			String params = processParams(httpRequestor.getResponse(), jobName);
			int executeCountBefore = getTotalJobExecuteCount(httpRequestor.getResponse(), jobName);
			httpRequestor.sendPostRequest(flowUrl, params);
			getSchedulerSummaryPage(httpRequestor, SCHEDULER_SUMMARY_FLOW);
			/* Get job launch info */
			String jobStatus = getCurrentJobStatus(httpRequestor.getResponse(), jobName);
			int executeCountAfter = getTotalJobExecuteCount(httpRequestor.getResponse(), jobName);
			/* Wait till job finishes */
			long endTime = System.currentTimeMillis() + JOB_TIMEOUT;
			while (jobStatus.equals(JobStatus.RUNNING.get()) || jobStatus.equals(JobStatus.WAITING.get()) || executeCountAfter <= executeCountBefore) {
				if (endTime < System.currentTimeMillis()) {
					throw new IstfException("HTTP Job ERROR: <--- Job '" + jobName + "' has timed out after " + JOB_TIMEOUT + " milliseconds");
				}
				getSchedulerSummaryPage(httpRequestor, SCHEDULER_SUMMARY_FLOW);
				jobStatus = getCurrentJobStatus(httpRequestor.getResponse(), jobName);
				executeCountAfter = getTotalJobExecuteCount(httpRequestor.getResponse(), jobName);
				Thread.sleep(JOB_SLEEP_RERUN);
			}
			/* Gather job run info */
			String latestJobRunStatus = getLastJobRunStatus(httpRequestor.getResponse(), jobName);
			if (!latestJobRunStatus.equals(JobStatus.PASSED.get())) {
				throw new IstfException("HTTP Job ERROR: <--- Job '" + jobName + "' was executed with status " + latestJobRunStatus);
			}
		} else {
			throw new IstfException("HTTP Job ERROR: Job '" + jobName + "' does not exist or created. Job was not executed. ");
		}


	}


	private static void checkAsyncTask() throws IOException {
		HttpAAARequestor httpRequestor2 = HttpLogin.loginAd();
		String waiting, processing;

		getSchedulerSummaryPage(httpRequestor2, ASYNC_TASK_STATISTICS_FLOW);
		waiting = HttpHelper.find(httpRequestor2.getResponse(), WAITING_REGEX);
		processing = HttpHelper.find(httpRequestor2.getResponse(), PROCESSING_REGEX);

		//TODO Temporary removed Asynk tasks verification. Need to improve verification logic.
		/*long endTime = System.currentTimeMillis() + ASYNC_TIMEOUT;

		while (!processing.equals("0") || !waiting.equals("0")) {
			if (endTime < System.currentTimeMillis()) {
				throw new IstfException("Async tasks process is timed out after " + ASYNC_TIMEOUT + " milliseconds. Waiting async task = " + waiting + ". Processing async task = " + processing);
			}
			log.info("HTTP Job: Waiting async task: " + waiting + ". Processing async task: " + processing);

			Thread.sleep(5000);
			httpRequestor2.sendGetRequest(request);
			waiting = HttpHelper.find(httpRequestor2.getResponse(), WAITING_REGEX);
			processing = HttpHelper.find(httpRequestor2.getResponse(), PROCESSING_REGEX);
		}*/

		log.info("HTTP Job: Waiting async task: {}. Processing async task: {}", waiting, processing);
	}

	private static String getLastJobRunStatus(String content, String jobName) throws IOException {
		String result = "";
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (String part : parts) {
			if (part.contains(jobName)) {
				result = HttpHelper.find(part, JOB_LATEST_RUN_STATUS);
				break;
			}
		}
		log.info("HTTP: Latest {} run status {}", jobName, result);
		return result;
	}

	private static Integer getTotalJobExecuteCount(String content, String jobName) throws IOException {
		Integer result = 0;
		String jobRunStatistic = null;
		/* Split admin scheduler page rows */
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (String part : parts) {
			/* Find specified job html block */
			if (part.contains(jobName)) {
				/* Get row with job statistics */
				jobRunStatistic = HttpHelper.find(part, JOB_TOTAL_RUNS_STAT);
				/* Sum Passed - Failed - Interrupted runs */
				for (String jobRun : jobRunStatistic.split("/")) {
					jobRun = jobRun.replaceAll("\\D+", ""); // delete everything except numbers
					result += Integer.parseInt(jobRun);
				}
				break;
			}
		}
		log.info("HTTP: Job executed {} times, detailed job runs statistic: {}", result, jobRunStatistic);
		return result;
	}

	private static String getCurrentJobStatus(String content, String jobName) throws IOException {
		String status = "";
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (String part : parts) {
			if (part.contains(jobName)) {
				try {
					status = HttpHelper.find(part, JOB_STATUS_REGEX.replace("/JOB_NAME/", jobName));
				} catch (IOException ioe) {
					status = "Unknown"; // when job was executed and not in running/waiting state, span with status inside is not present at the page
				}
				break;
			}
		}
		log.info("HTTP Job: Current {} run status: {}", jobName, status);
		return status;
	}

	private static String getStartButtonId(String content, String jobName) throws IOException {
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (String part : parts) {
			if (part.contains(jobName) && part.contains("Start")) {
				return HttpHelper.find(part, JOB_EXECUTE_BUTTON_REGEX);
			}
		}
		return null;
	}

	private static String processParams(String content, String jobName) throws IOException {
		String buttonId = getStartButtonId(content, jobName); // buttonId : jobs:jobsTable:120:executeJob
		//todo remove hardcoded filters
		String jobsFilterIds = "&jobs:j_id_28_37_7w=Anytime&jobs:j_id_28_37_7z=Name";

		return "jobs_SUBMIT=1&javax.faces.ViewState=" + HttpHelper.find(content, HttpConstants.REGEX_VIEW_STATE) + "&" + buttonId + "=" + buttonId + jobsFilterIds;
	}

	private static void getSchedulerSummaryPage(HttpAAARequestor httpRequestor, String schedulerSummaryFlow) throws IOException {
		httpRequestor.sendGetRequest(schedulerSummaryFlow);
	}
}
