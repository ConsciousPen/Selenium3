package aaa.modules.docgen.auto_ss;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
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
public class TestAZScenario3 extends AutoSSBaseTest {

	@Test
	public void testPolicyCreation() {
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info(getState() + " Policy AZ AutoSS is created: " + policyNumber);
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA41XX)
				.verify.mapping(getTestSpecificTD("TestData_Verification").adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber));
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}

