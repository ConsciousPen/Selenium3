package aaa.helpers.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;

public class HttpStub {

	private static Logger log = LoggerFactory.getLogger(HttpStub.class);
	private static final String RESULT_REGEXP = "<b>Result:</b> <span style=\"color:red\">([^<]+)";
	private static Object lock = new Object();

	private enum StubState {
		TRUE, FALSE
	}

	public static void executeAllBatches() {
		Arrays.stream(HttpStubBatch.values()).forEach(HttpStub::executeSingleBatch);
	}

	public static void executeSingleBatch(HttpStubBatch batchName) {
		Content content = null;
		String result;
		try {
			content = Request.Post(CSAAApplicationFactory.get().stubApp().formatUrl() + batchName.getRequest()).bodyForm(Form.form().add("button1", "Start").build()).execute().returnContent();
		} catch (IOException e) {
			log.info("Unable to execute stub Batch. /n", e);
		}
		if (content != null) {
			Pattern r = Pattern.compile(RESULT_REGEXP);
			Matcher m = r.matcher(content.asString());

			if (m.find()) {
				result = m.group(1);
				log.info("Stub Batch '{}' has been executed with result: {}", batchName.getName(), result);
			} else {
				log.info("Stub Batch '{}' has been executed with unknown result: {}", batchName.getName());
			}
		}
	}

	public enum HttpStubBatch {

		OFFLINE_MVR_BATCH("Offline ChoicePoint (MVR) batch", "/offlineProcessServlet"),
		OFFLINE_CLUE_BATCH("Offline ChoicePoint (CLUE) batch", "/offlineClueBatchProcessServlet"),
		OFFLINE_AAA_CLAIMS_BATCH("Offline AAA claims batch", "/claimsOfflineProcessServlet"),
		OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH("Offline AAA membership summary batch", "/membershipProcessServlet"),
		OFFLINE_AAA_CREDIT_SCORE_BATCH("Offline AAA credit score batch", "/creditScoreProcessServlet"),
		//OFFLINE_AAA_EARS_BATCH("Offline AAA E.A.R.S. batch", "earsOfflineProcessServlet"),
		OFFLINE_ISO_BATCH("Offline ISO batch", "/isoRenewalBatchReceiveServlet");
		private String name;
		private String value;

		HttpStubBatch(String name, String value) {
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
}
