package aaa.modules.policy.cea;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.CaliforniaEarthquakeBaseTest;
import toolkit.utils.TestInfo;

public class AAATestPolicyCreation extends CaliforniaEarthquakeBaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines.CEA")
	public void testQuoteCreation() {
		mainApp().open();
		//createCustomerIndividual();
		// createPolicy(tdPolicy.getTestData("DataGather", "TestData"));
		createPolicy();
		createPolicyFromCa();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.BOUND);

	}

}
