package aaa.modules.docgen.auto_ss;

import org.testng.annotations.Test;

import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

/**
 * 
 * @author Xiaolan Ge
 *
 */

@Test
public class TestAZScenario3 extends AutoSSBaseTest {
	private String policyNumber;

	@Test
	public void testPolicyCreation() {

		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(
				getTestSpecificTD("TestData").resolveLinks()));
		PolicySummaryPage.labelPolicyStatus.verify
				.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info(getState() + " Policy AZ AutoSS is created: " + policyNumber);
		DocGenHelper.getDocumentRequest(policyNumber).getProductName().equals("AAAA");

	}
}

