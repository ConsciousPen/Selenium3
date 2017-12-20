package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.modules.policy.AutoCaChoiceBaseTest;

public class TestScenario8 extends AutoCaChoiceBaseTest {
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_PolicyCreation(String state) throws Exception {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA57CA);
	}
}
