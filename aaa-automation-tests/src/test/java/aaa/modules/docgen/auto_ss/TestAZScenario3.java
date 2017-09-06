package aaa.modules.docgen.auto_ss;

import org.testng.annotations.Test;

import toolkit.verification.CustomAssert;
import aaa.helpers.docgen.DocGenHelper;
import aaa.modules.policy.AutoSSBaseTest;

/**
 * @author Ryan Yu
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
	private String policyNumber = "AZSS952118433";

	@Test
	public void testPolicyCreation() {
		CustomAssert.enableSoftMode();
//		mainApp().open();
//		createCustomerIndividual();
//		policyNumber = createPolicy(getPolicyTD().adjust(
//				getTestSpecificTD("TestData").resolveLinks()));
//		PolicySummaryPage.labelPolicyStatus.verify
//				.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
//		log.info(getState() + " Policy AZ AutoSS is created: " + policyNumber);
//		DocGenHelper.verifyDocumentsGenerated(policyNumber, AA41XX);
		DocGenHelper.verifyDocumentsMapping(false, policyNumber, getTestSpecificTD("TestData_Verification"));
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}

