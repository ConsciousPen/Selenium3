package aaa.helpers.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.http.impl.*;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class HttpJob {

	private static final String ASYNC_MANAGER_REGEX = "<a([^>]+)id=\"asyncTaskSummaryForm\\:([^\"]+)\"([^>]+)>([^>]+)</a>";
	private static final String WAITING_REGEX = ".*Waiting:.*[^0-9]([0-9]+)<";
	private static final String PROCESSING_REGEX = ".*Processing:.*[^0-9]([0-9]+)<";
	private static final String FLOW_QUERY_STRING = "<form id=\"asyncTaskSummaryForm\" name=\"asyncTaskSummaryForm\" method=\"post\" action=\"([^\"]+)\"";
	private static final String JOB_PARAMS_REGEX = "\"parameters\"\\:(\\{[^\\}]+%s[^\\}]+\\})";
	private static final String JOB_STATUS_REGEX = "State\\:<span[^>]+>(\\w+)<";
	private static final String JOB_RESULT_REGEX = "Result\\:<span[^>]+>([/\\w]+)<";
	private static final String JOB_EXECUTED_REGEX = "(\\d+\\sPassed[^>]+)\n";
	private static final String JOB_EXECUTE_BUTTON_REGEX = "<a [^\\>]*id=\"(jobs:jobsTable:\\d+:start-job)\"";
	private static final String JOB_ROW_SPLITTER_REGEX = "<tr data-ri=\"\\d+\"";
	private static final int JOB_SLEEP_RERUN = 1500;
	private static final String JOB_ADD_PARAMS_FILENAME = "job_run.txt";
	private static final String STOP_ASYNC_PARAMS_FILENAME = "stop_async.txt";
	private static final long ASYNC_TIMEOUT = 300000;

	//	private static final String JOB_EXECUTED_REGEX = "Executed\\:<span[^>]+>([/\\w]+)<";
	//	private static final String JOB_ROW_SPLITTER_REGEX = "<tr id=\"jobs:jobsTable:\\d+\"";
	//  private static final String JOB_EXECUTE_BUTTON_REGEX = "<a [^\\>]*id=\"(jobs:jobsTable:\\d+:executeJob)\"";


	private static Logger log = LoggerFactory.getLogger(HttpJob.class);
	private static int JOB_TIMEOUT = Integer.parseInt(PropertyProvider.getProperty("test.batchjob.timeout", "1200000"));

	private HttpJob() {
	}

	public static void checkAsyncManager(HttpAAARequestor httpRequestor) throws Exception {
		//String refreshQuery = "/aaa-admin/admin/flow?_flowId=async-task-statistics-flow&_flowExecutionKey=e6s1&_windowId=W1522750697151";
		HttpQueryBuilder queryBuilder = new HttpQueryBuilder();
		queryBuilder.readParamsFile(JOB_ADD_PARAMS_FILENAME);

		httpRequestor.sendGetRequest("/aaa-admin/admin/flow?_flowId=async-task-statistics-flow");
		String asyncManager = HttpHelper.find(httpRequestor.getResponse(), ASYNC_MANAGER_REGEX, 4);

		if (asyncManager.equalsIgnoreCase("start manager")) {
			HttpAAARequestor httpRequestor2 = HttpLogin.loginAd();
			httpRequestor2.sendGetRequest("/aaa-admin/admin/flow?_flowId=async-task-statistics-flow");

			String flowUrl = HttpHelper.find(httpRequestor2.getResponse(), FLOW_QUERY_STRING).replace("amp;", "");
			String viewState = HttpHelper.find(httpRequestor2.getResponse(), HttpConstants.REGEX_VIEW_STATE);

			Map<String, String> mapping = new HashMap<String, String>();
			mapping.put("viewState", viewState);

			httpRequestor2.sendPostRequest(flowUrl, queryBuilder.buildQueryString(0, mapping));
			//Refresh Start
			//httpRequestor2.sendGetRequest("/aaa-admin/admin/flow?_flowId=async-task-statistics-flow");
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

		httpRequestor.sendGetRequest("/aaa-admin/admin/flow?_flowId=async-task-statistics-flow");
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

	public static synchronized void executeJob(String jobName) throws Exception {
		/*
		 * System.setProperty("http.proxyHost", "localhost");
		 * System.setProperty("http.proxyPort", "8888");
		 */
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

	private static void runSingleJob(HttpAAARequestor httpRequestor, String jobName) throws Exception {
		String request = "/aaa-admin/admin/flow?_flowId=scheduler-summary-flow";
		httpRequestor.sendGetRequest(request);
		if (httpRequestor.getResponse().contains(String.format("title=\"%s\"",jobName))) {

			String flowUrl = HtmlParser.getFlowUrl(httpRequestor.getResponse());
			String params = processParams(httpRequestor.getResponse(), jobName);
			int executeCountBefore = getExecuteCount(httpRequestor.getResponse(), jobName);
			httpRequestor.sendPostRequest(flowUrl, params);
			httpRequestor.sendGetRequest(request);
			String jobStatus = getStatus(httpRequestor.getResponse(), jobName);
			int executeCountAfter = getExecuteCount(httpRequestor.getResponse(), jobName);
			String jobResult = getResult(httpRequestor.getResponse(), jobName);
			long endTime = System.currentTimeMillis() + JOB_TIMEOUT;
			while (jobStatus.equals("Running") || jobResult.equals("N/A") || executeCountAfter <= executeCountBefore) {
				if (endTime < System.currentTimeMillis()) {
					throw new IstfException("HTTP Job ERROR: <--- Job '" + jobName + "' has timed out after " + JOB_TIMEOUT + " milliseconds");
				}
				httpRequestor.sendGetRequest(request);
				jobStatus = getStatus(httpRequestor.getResponse(), jobName);
				executeCountAfter = getExecuteCount(httpRequestor.getResponse(), jobName);
				jobResult = getResult(httpRequestor.getResponse(), jobName);
				Thread.sleep(JOB_SLEEP_RERUN);
			}
			if (!jobResult.equals("Success")) {
				throw new IstfException("HTTP Job ERROR: <--- Job '" + jobName + "' was executed with status " + jobResult);
			}
		} else {
			throw new IstfException("HTTP Job ERROR: Job '" + jobName + "' does not exist or created. Job was not executed. ");
		}
	}

	private static String processParams(String content, String jobName) throws Exception {
		String buttonId = getStartButtonId(content, jobName); // buttonId : jobs:jobsTable:120:executeJob
		String params = "jobs_SUBMIT=1&javax.faces.ViewState=" + HttpHelper.find(content, HttpConstants.REGEX_VIEW_STATE) + "&" + buttonId + "=" + buttonId;
		// Builded above
		//jobs_SUBMIT=1&javax.faces.ViewState=d6bc0b94-b53e-4a58-8cef-0570eb6122a6&jobs:jobsTable:2:start-job=jobs:jobsTable:2:start-job
		// Fiddler
		//jobs_SUBMIT=1&javax.faces.ViewState=a01831e0-55c6-4d5a-a825-86f933c325f2&javax.faces.partial.ajax=true&javax.faces.source=jobs:jobsTable:2:start-job&javax.faces.partial.execute=@all&javax.faces.partial.render=jobs+statistics:schedulerTable&jobs:jobsTable:2:start-job=jobs:jobsTable:2:start-job&jobs:j_id_28_37_7w=Anytime&jobs:j_id_28_37_7z=Name
	return params;

	}

	private static String getStatus(String content, String jobName) throws IOException {
		String status = "";
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].contains(">" + jobName + "<") && parts[i].contains("State")) {
				status = HttpHelper.find(parts[i], JOB_STATUS_REGEX);
				break;
			}
		}
		return status;
	}

	private static String getResult(String content, String jobName) throws IOException {
		String result = "";
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].contains(">" + jobName + "<") && parts[i].contains("Result")) {
				result = HttpHelper.find(parts[i], JOB_RESULT_REGEX);
				break;
			}
		}
		return result;
	}

	private static Integer getExecuteCount(String content, String jobName) throws IOException {
		Integer result = 0;
		/* Split rows */
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (int i = 0; i < parts.length; i++) {
			/* Find specified job html block */
			if (parts[i].contains(jobName)) {
				/* Get row with job statistics */
				String jobRunStatistic = HttpHelper.find(parts[i], "(\\d+\\sPassed[^>]+Interrupted[^>])");
				/* Sum Passed - Failed - Interrupted runs */
				for(String jobRun : jobRunStatistic.split("/")){
					jobRun = jobRun.replaceAll("\\D+",""); // delete everything except numbers
					result += Integer.parseInt(jobRun);
				}
				break;
			}
		}
		log.info("HTTP Job: Job executed {} times",result);
		return result;
	}

	private static String getStartButtonId(String content, String jobName) throws IOException {
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].contains(jobName) && parts[i].contains("Start")) {
				return HttpHelper.find(parts[i], JOB_EXECUTE_BUTTON_REGEX);
			}
		}
		return null;
	}

	private static void checkAsyncTask() throws IOException {
		HttpAAARequestor httpRequestor2 = HttpLogin.loginAd();
		String waiting, processing;
		String request = "/aaa-admin/admin/flow?_flowId=async-task-statistics-flow";

		httpRequestor2.sendGetRequest(request);
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

		log.info("HTTP Job: Waiting async task: " + waiting + ". Processing async task: " + processing);
	}
}
