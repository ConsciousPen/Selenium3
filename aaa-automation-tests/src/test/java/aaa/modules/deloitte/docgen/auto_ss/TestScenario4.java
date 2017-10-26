package aaa.modules.deloitte.docgen.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.verification.CustomAssert;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoSSBaseTest;

public class TestScenario4 extends AutoSSBaseTest {
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();
		
		// Create quote
		createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_QuoteCreation").resolveLinks()));
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
