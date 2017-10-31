package aaa.modules.docgen.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import aaa.helpers.constants.Groups;
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
public class TestScenario3 extends AutoSSBaseTest {
	private String policyNumber;
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testPolicyCreation(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info(getState() + " Policy AutoSS is created: " + policyNumber);
		TestData tdVerification = getTestSpecificTD("TestData_Verification");
		switch (getState()) {
		case "AZ":
			tdVerification.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber).adjust(TestData.makeKeyPath("AARFIXX", "form", "PlcyNum", "TextField"), policyNumber);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA41XX, Documents.AARFIXX).verify.mapping(tdVerification, policyNumber);
			break;
		case "IN":
		case "OK":
			tdVerification.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA41XX).verify.mapping(tdVerification, policyNumber);
			break;
		case "PA":
			tdVerification.adjust(TestData.makeKeyPath("AA41PA", "form", "PlcyNum", "TextField"), policyNumber).adjust(TestData.makeKeyPath("AA52UPAB", "form", "PlcyNum", "TextField"), policyNumber).adjust(TestData.makeKeyPath("AA52IPAB", "form", "PlcyNum", "TextField"), policyNumber);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA41PA, Documents.AA52UPAB, Documents.AA52IPAB).verify.mapping(tdVerification, policyNumber);	
			checkEndorseDocGen();
			break; 
		}
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void checkEndorseDocGen() {
		TestData tdVerification = getTestSpecificTD("TestData_EndorseVerification");
		tdVerification.adjust(TestData.makeKeyPath("AA52UPAC", "form", "PlcyNum", "TextField"), policyNumber).adjust(TestData.makeKeyPath("AA52IPAC", "form", "PlcyNum", "TextField"), policyNumber);
		policy.endorse().performAndFill(getTestSpecificTD("Endorsement").adjust(getPolicyTD("Endorsement", "TestData")));
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA52UPAC, Documents.AA52IPAC).verify.mapping(tdVerification, policyNumber);
	}
}
