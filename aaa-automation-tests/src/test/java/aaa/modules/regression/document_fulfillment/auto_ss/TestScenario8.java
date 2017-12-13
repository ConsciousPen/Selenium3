package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.modules.policy.AutoSSBaseTest;

/**
 * Check AARFIXX generated for AAA Insurance with SMARTtrek Acknowledgement of
 * Terms and Conditions and Privacy Policies
 * 
 * @author qyu
 *
 */
public class TestScenario8 extends AutoSSBaseTest {

	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_UBI").resolveLinks()));
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AARFIXX);
	}
}
