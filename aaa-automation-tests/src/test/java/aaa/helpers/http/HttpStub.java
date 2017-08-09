package aaa.helpers.http;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import aaa.helpers.http.impl.HttpConstants;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import toolkit.exceptions.IstfException;
import aaa.helpers.http.impl.HttpAAARequestor;
import aaa.helpers.http.impl.HttpHelper;
import aaa.helpers.http.impl.HttpQueryBuilder;

public class HttpStub {

	private static final String PARAMS_FILENAME = "stub.txt";
	private final static String PORT = ":9083";
	private static Logger log = LoggerFactory.getLogger(HttpStub.class);
	private final static String URL_POSTFIX = "/aaa-external-stub-services-app/";
	private static String host = HttpHelper.getHost();
	private static String urlTemplate = HttpConstants.URL_PROTOCOL + host + PORT + URL_POSTFIX;
	private static final String RESULT_REGEXP = "<b>Result:</b> <span style=\"color:red\">([^<]+)";
	private static HashMap<HttpStubBatch, StubState> stubs = new HashMap<>();
	private static LocalDateTime currentPhase;

	private enum StubState {
		TRUE, FALSE
	}

	public static void executeAllBatches() {

		for (HttpStubBatch value : HttpStubBatch.values()) {
			executeSingleBatch(value);
		}
	}

	public static void executeSingleBatch(HttpStubBatch stubName) {
		if (TimeSetterUtil.getInstance().isPEF()) {
			LocalDateTime phaseTime = TimeSetterUtil.getInstance().getPhaseStartTime();

			if (!phaseTime.equals(currentPhase)) {
				stubs = new HashMap<HttpStubBatch, StubState>();
				currentPhase = phaseTime;
			}
		} else
			stubs = new HashMap<HttpStubBatch, StubState>();
		synchronized (stubs) {
			switch (getStubState(stubName)) {
			case FALSE:
				try {
					executeOfflineBatch(stubName);
					setStubState(stubName, StubState.TRUE);
				} catch (IstfException ie) {
					setStubState(stubName, StubState.FALSE);
					throw ie;
				}
				break;
			case TRUE:
				//log.info("HTTP Stub: '" + stubName.getName() + "' has been already executed.");
				break;

			}
		}
	}

	private static void executeOfflineBatch(HttpStubBatch batchName) {
		// System.setProperty("http.proxyHost", "localhost");
		// System.setProperty("http.proxyPort", "8888");
		HttpAAARequestor httpRequestor = new HttpAAARequestor();
		httpRequestor.setDomain(host + PORT);

		try {
			HttpQueryBuilder queryBuilder = new HttpQueryBuilder();
			queryBuilder.readParamsFile(PARAMS_FILENAME);

			httpRequestor.sendPostRequest(urlTemplate + batchName.getRequest(), queryBuilder.buildQueryString(0, null));

			String result = HttpHelper.find(httpRequestor.getResponse(), RESULT_REGEXP);
			log.info("HTTP Stub: '" + batchName.getName() + "' execution finished with result: " + result);
		} catch (IOException e) {
			throw new IstfException("HTTP Stub ERROR: '" + batchName.getName() + "' execution failed. \n", e);
		}

	}

	public enum HttpStubBatch {

		OFFLINE_MVR_BATCH("Offline ChoicePoint (MVR) batch", "offlineProcessServlet"),
		OFFLINE_AAA_CLAIMS_BATCH("Offline AAA claims batch", "claimsOfflineProcessServlet"),
		OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH("Offline AAA membership summary batch", "membershipProcessServlet"),
		OFFLINE_AAA_CREDIT_SCORE_BATCH("Offline AAA credit score batch", "creditScoreProcessServlet"),
		//OFFLINE_AAA_EARS_BATCH("Offline AAA E.A.R.S. batch", "earsOfflineProcessServlet"), 
		;
		private String name;
		private String value;

		private HttpStubBatch(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getRequest() {
			return value;
		}

		public String getName() {
			return name;
		}
	}

	private static StubState getStubState(HttpStubBatch stubName) {
		synchronized (stubs) {
			StubState s = stubs.get(stubName);
			if (s == null) {
				s = StubState.FALSE;
				stubs.put(stubName, StubState.FALSE);
			}
			return s;
		}
	}

	private static void setStubState(HttpStubBatch stubName, StubState stubState) {
		synchronized (stubs) {
			stubs.put(stubName, stubState);
		}
	}

	@Test
	public void testMVR() throws IOException {
		executeAllBatches();
	}
}