package aaa.helpers;

/*import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.exceptions.IstfException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Add htmlcleaner to pom before usage
 *
 * 	 <!-- https://mvnrepository.com/artifact/net.sourceforge.htmlcleaner/htmlcleaner -->
	 <dependency>
	 <groupId>net.sourceforge.htmlcleaner</groupId>
	 <artifactId>htmlcleaner</artifactId>
	 <version>2.21</version>
	 </dependency>
 */
public class TestAnalyticsHelper {
	private final static String ANALYTICS_URL = "http://aws2aaaanalytics01.corevelocity.csaa.cloud:9001";
	private static Logger log = LoggerFactory.getLogger(TestAnalyticsHelper.class);

	public static void main(String[] args) {
		TestAnalyticsHelper testAnalyticsHelper = new TestAnalyticsHelper();
String[] testRunIds = {
				"6dc0887a-c51c-11e7-8656-97f7850cff6a",
				"aeaf8809-c51a-11e7-8be7-1106724f7cc3",
				"6b506232-c38b-11e7-9be5-f5abe8c3f277",
				"ad919655-c357-11e7-b50f-3b2cf94890a4",
				"94aa764b-c2c3-11e7-a736-5914ccc7391a",
				"f632b284-c296-11e7-916e-335d64f495d9",
				"c5530d39-c217-11e7-a237-5b225a8408a6",
				"60636702-c03b-11e7-8d17-ad98f0f5e40a",
				"c897165e-c02f-11e7-9c4d-2dd9ff556c49",
				"c25da477-bfb2-11e7-82de-7ba511d3b3d4",
				"4668cb80-bf6e-11e7-99b8-f358905490dc",
				"027d8063-bf69-11e7-842a-a35dff11e296",
				"a0d3fd51-bea8-11e7-8d41-5dc41725c639",
				"c544f098-bdd6-11e7-842c-215a56ed95d5",
				"128deab4-bd16-11e7-8bb8-711413541281",
				"57910ab8-b9e2-11e7-a748-556902f78041",
				"5e82023c-b920-11e7-a2e1-6938cf04180d"};


		testAnalyticsHelper.setFailureReasonForNotAnalyzedTests(FailureReason.ENVIRONMENT, "02b630de-c5bb-11e7-bc74-9505fe61df93");
	}

	public void setFailureReasonForNotAnalyzedTests(FailureReason setReason, String... testRunIds) {
		List<String> testRunUrls = Arrays.stream(testRunIds).map(testRunId -> String.format("%1$s/runs/%2$s?exclude=PASSED", ANALYTICS_URL, testRunId)).collect(Collectors.toList());
		Client client = ClientBuilder.newBuilder().build();
		/*HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);*/

		log.info(String.format("Updating process for %s test runs has been started", testRunIds.length));
		for (String testRunUrl : testRunUrls) {
			List<String> notAnalyzedTestIds = new ArrayList<>();
			List<String> failedToSetReasonTestIds = new ArrayList<>();

			log.info("=======================================================================================");
			log.info("Getting not analyzed test IDs from analytics URL: " + testRunUrl);
			/*try {
				URL url = new URL(testRunUrl);
				URLConnection conn = url.openConnection();
				TagNode node = cleaner.clean(new InputStreamReader(conn.getInputStream()));
				Object[] notAnalyzedNodes = node.evaluateXPath("//div[@class='progress']/span[@class='progress-box reason-unknown']");
				for (Object notAnalyzedNode : notAnalyzedNodes) {
					notAnalyzedTestIds.add(((TagNode) notAnalyzedNode).getAttributeByName("entry-id"));
				}
			} catch (IOException | XPatherException e) {
				throw new IstfException("Unable to get not analyzed test IDs from analytics URL: " + testRunUrl, e);
			}*/

			log.info(String.format("Found %s tests with not analyzed reason", notAnalyzedTestIds.size()));
			if (notAnalyzedTestIds.isEmpty()) {
				log.info("All tests are already analyzed in " + testRunUrl);
				continue;
			}

			log.info(String.format("Setting \"%s\" reason to all found not analyzed tests...", setReason.get()));
			//Client client = ClientBuilder.newBuilder().build();
			for (String notAnalyzedTestId : notAnalyzedTestIds) {
				Response response = client
						.target(String.format("%1$s/api/tests/%2$s/analyze", ANALYTICS_URL, notAnalyzedTestId))
						.queryParam("reason", setReason.get())
						.request()
						.post(null);
				if (!"SUCCESSFUL".equals(response.getStatusInfo().getFamily().name())) {
					log.warn("Unable to set state AUTO to test with id: " + notAnalyzedTestId);
					failedToSetReasonTestIds.add(notAnalyzedTestId);
				}
			}

			if (failedToSetReasonTestIds.isEmpty()) {
				log.info("All not analyzed tests are successfully updated with reason: " + setReason.get());
			} else {
				log.warn(String.format("Failed to set reason to %s tests IDs. Update is not successful!", failedToSetReasonTestIds.size()));
				failedToSetReasonTestIds.clear();
			}

			notAnalyzedTestIds.clear();
		}
		log.info("Updating process for test run(s) has been completed!");
	}

	public enum FailureReason {
		ENVIRONMENT("ENV"),
		AUTOMATION("AUTO"),
		PRODUCT("PROD"),
		PERFORMANCE("PERF"),
		NOT_ANALYZED("NONE");

		String id;

		FailureReason(String id) {
			this.id = id;
		}

		public String get() {
			return id;
		}
	}
}
