package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;

/**
 * @author Ryan Yu
 *
 */
public class TestScenario8 extends AutoSSBaseTest {
	
	@Parameters({ "state" })
	@StateList(states = States.NV)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA16NV);
	}
	
}
