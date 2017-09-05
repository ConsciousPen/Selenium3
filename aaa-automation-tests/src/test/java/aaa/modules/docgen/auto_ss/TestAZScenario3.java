package aaa.modules.docgen.auto_ss;

import org.testng.annotations.Test;

import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import static aaa.main.enums.DocGenEnum.Documents.*;

/**
 * @author Lina Li
 * @name Test the form AA41XX
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy and the policy type =NANO
 * 3. Calculate premium and bind policy
 * 4. Verify the form AA41XX was generated
 * @details
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
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AA41XX);
	}
}

