package aaa.http;

/*import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import toolkit.exceptions.IstfException;
import toolkit.utils.http.HttpExecutor;
import toolkit.utils.http.HttpQueryBuilder;
import toolkit.utils.http.HttpRequest;
import toolkit.utils.http.HttpRequest.HttpRequestHeader;
import toolkit.utils.http.HttpResponse.HttpResponseHeader;
import toolkit.utils.http.HttpResponse;*/


public class HttpJob {

	/*private static Logger log = LoggerFactory.getLogger(HttpJob.class);
	private static final String ASYNC_MANAGER_REGEX = "<a([^>]+)id=\"asyncTaskSummaryForm\\:([^\"]+)\"([^>]+)>([^>]+)</a>";
	private static final String WAITING_REGEX = ".*Waiting:.*[^0-9]([0-9]+)<";
	private static final String PROCESSING_REGEX = ".*Processing:.*[^0-9]([0-9]+)<";
	private static final String FLOW_QUERY_STRING = "<form id=\"asyncTaskSummaryForm\" name=\"asyncTaskSummaryForm\" method=\"post\" action=\"([^\"]+)\"";
	private static final String JOB_EXECUTE_BUTTON_REGEX = "<a [^\\>]*id=\"(jobs:jobsTable:\\d+:executeJob)\"";
	private static final String JOB_PARAMS_REGEX = "\"parameters\"\\:(\\{[^\\}]+%s[^\\}]+\\})";
	private static final String JOB_STATUS_REGEX = "State\\:<span[^>]+>(\\w+)<";
	private static final String JOB_RESULT_REGEX = "Result\\:<span[^>]+>([/\\w]+)<";
	private static final String JOB_EXECUTED_REGEX = "Executed\\:<span[^>]+>([/\\w]+)<";
	private static final String JOB_ROW_SPLITTER_REGEX = "<tr id=\"jobs:jobsTable:\\d+\"";
	private static final int JOB_SLEEP_RERUN = 1500;
	private static final long JOB_TIMEOUT = 1500000;

	private static final String JOB_ADD_PARAMS_FILENAME = "job_run.txt";
	private static final String STOP_ASYNC_PARAMS_FILENAME = "stop_async.txt";
	private static final long ASYNC_TIMEOUT = 300000;*/

	private HttpJob() {
	}

	/*public static void checkAsyncManager(HttpRequest httpRequestor) throws Exception {
		HttpQueryBuilder queryBuilder = new HttpQueryBuilder();
		queryBuilder.readParamsFile(JOB_ADD_PARAMS_FILENAME);
		
		httpRequestor.setUrl(new URL(httpRequestor.getUrl(), "/aaa-admin/admin/flow?_flowId=async-task-statistics-flow"));
		HttpResponse response = HttpExecutor.sendRequest(httpRequestor);
		response.getHeader(HttpResponseHeader.SET_COOKIE);
		
		
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

			asyncManager = HttpHelper.find(httpRequestor2.getResponse(), ASYNC_MANAGER_REGEX, 4);
			if (!asyncManager.equalsIgnoreCase("stop manager")) {
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
		
		 * System.setProperty("http.proxyHost", "localhost");
		 * System.setProperty("http.proxyPort", "8888");
		 
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
		if (jobName.equals("aaaRecurringPaymentsProcessingJob") || jobName.equals("remittanceFeedBatchReceiveJob") || jobName.equals("policyStatusUpdateJob")
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
		if (httpRequestor.getResponse().contains(jobName + "<")) {

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
		} else
			throw new IstfException("HTTP Job ERROR: Job '" + jobName + "' does not exist or created. Job was not executed. ");
	}

	private static String processParams(String content, String jobName) throws Exception {
		String buttonId = getExecuteButtonId(content, jobName);
		String additionalParams = "jobs=jobs&jobs_SUBMIT=1&javax.faces.ViewState=" + HttpHelper.find(content, HttpConstants.REGEX_VIEW_STATE) + "&" + buttonId + "=" + buttonId;

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(HttpHelper.find(StringEscapeUtils.unescapeHtml(content), String.format(JOB_PARAMS_REGEX, jobName)));

		String params = "";
		for (Object key : json.keySet()) {
			params += key + "=" + json.get(key) + "&";
		}
		return params + "&" + additionalParams;
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
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].contains(">" + jobName + "<") && parts[i].contains("Executed")) {
				result = Integer.parseInt(HttpHelper.find(parts[i], JOB_EXECUTED_REGEX));
				break;
			}
		}
		return result;
	}

	private static String getExecuteButtonId(String content, String jobName) throws IOException {
		String[] parts = content.split(JOB_ROW_SPLITTER_REGEX);
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].contains(jobName) && parts[i].contains("Execute")) {
				return HttpHelper.find(parts[i], JOB_EXECUTE_BUTTON_REGEX);
			}
		}
		return null;
	}

	private static void checkAsyncTask() throws IOException, InterruptedException {
		HttpAAARequestor httpRequestor2 = HttpLogin.loginAd();
		String waiting, processing;
		String request = "/aaa-admin/admin/flow?_flowId=async-task-statistics-flow";

		httpRequestor2.sendGetRequest(request);
		waiting = HttpHelper.find(httpRequestor2.getResponse(), WAITING_REGEX);
		processing = HttpHelper.find(httpRequestor2.getResponse(), PROCESSING_REGEX);

		long endTime = System.currentTimeMillis() + ASYNC_TIMEOUT;

		while (!processing.equals("0") || !waiting.equals("0")) {
			if (endTime < System.currentTimeMillis()) {
				throw new IstfException("Async tasks process is timed out after " + ASYNC_TIMEOUT + " milliseconds. Waiting async task = " + waiting + ". Processing async task = " + processing);
			}
			log.info("HTTP Job: Waiting async task: " + waiting + ". Processing async task: " + processing);

			Thread.sleep(5000);
			httpRequestor2.sendGetRequest(request);
			waiting = HttpHelper.find(httpRequestor2.getResponse(), WAITING_REGEX);
			processing = HttpHelper.find(httpRequestor2.getResponse(), PROCESSING_REGEX);
		}

		log.info("HTTP Job: Waiting async task: " + waiting + ". Processing async task: " + processing);

	}*/
}
