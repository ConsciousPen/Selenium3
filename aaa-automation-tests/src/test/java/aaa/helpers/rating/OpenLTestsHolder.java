package aaa.helpers.rating;

import aaa.main.modules.policy.PolicyType;
import aaa.utils.rating.OpenLFileParser;
import aaa.utils.rating.openl_objects.OpenLPolicy;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.datax.TestData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpenLTestsHolder {
	private List<OpenLTest> openLTests;

	public OpenLTestsHolder(String pathToOpenLfile, PolicyType policyType) {
		OpenLFileParser openLFileParser = policyType.getOpenLFileParser().parse(new File(pathToOpenLfile));
		openLTests = initializeTests(openLFileParser);
	}

	public List<OpenLTest> getTests() {
		return openLTests;
	}

	private List<OpenLTest> initializeTests(OpenLFileParser openLFileParser) {
		List<OpenLTest> openLTests = new ArrayList<>(openLFileParser.getPolicies().size());
		for (OpenLPolicy openLPolicy : openLFileParser.getPolicies()) {
			TestData ratingData = openLFileParser.generateTestData(openLPolicy);
			Dollar expectedPremium = openLFileParser.getFinalPremium(openLPolicy.getNumber());
			openLTests.add(new OpenLTest(ratingData, expectedPremium));
		}
		return openLTests;
	}

	public class OpenLTest {
		private TestData testData;
		private Dollar expectedPremium;

		public OpenLTest(TestData testData, Dollar expectedPremium) {
			this.testData = testData;
			this.expectedPremium = expectedPremium;
		}

		public TestData getTestData() {
			return testData;
		}

		public Dollar getExpectedPremium() {
			return expectedPremium;
		}

		@Override
		public String toString() {
			return "OpenLtest{" +
					"testData=" + testData +
					", expectedPremium=" + expectedPremium +
					'}';
		}
	}
}
