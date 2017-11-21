package aaa.helpers.rating;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.utils.openl.model.OpenLPolicy;
import aaa.utils.openl.parser.OpenLFileParser;
import toolkit.datax.TestData;

public class OpenLTestsHolder<T extends OpenLFileParser<P, ?>, P extends OpenLPolicy> {
	private List<OpenLTest> openLTests;

	public OpenLTestsHolder(String pathToOpenLfile, T openLFileParser) {
		openLFileParser.parse(new File(pathToOpenLfile));
		List<P> openLPolicies = openLFileParser.getPolicies();
		openLTests = new ArrayList<>(openLPolicies.size());

		for (P oPolicy : openLPolicies) {
			TestData ratingData = openLFileParser.generateTestData(oPolicy);
			Dollar expectedPremium = openLFileParser.getFinalPremium(oPolicy);
			openLTests.add(new OpenLTest(ratingData, expectedPremium));
		}
	}

	public List<OpenLTest> getTests() {
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
