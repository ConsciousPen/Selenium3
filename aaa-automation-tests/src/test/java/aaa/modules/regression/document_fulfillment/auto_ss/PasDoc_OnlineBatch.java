package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class PasDoc_OnlineBatch extends AutoSSBaseTest {
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testPolicyCreationFull(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_full = getPolicyTD().adjust(getTestSpecificTD("TestData_Full").resolveLinks());
		createPolicy(td_full);
		log.info("TEST: Standard Full: Created policy# " + PolicySummaryPage.getPolicyNumber());
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testPolicyCreationNano(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_nano = getPolicyTD().adjust(getTestSpecificTD("TestData_Nano").resolveLinks());
		createPolicy(td_nano);
		log.info("TEST: Non-owner: Created policy# " + PolicySummaryPage.getPolicyNumber());
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testQuoteCreation(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_quote = getPolicyTD().adjust(getTestSpecificTD("TestData_Quote").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_quote, DocumentsAndBindTab.class, true);
		new DocumentsAndBindTab().saveAndExit();
		log.info("TEST: Standard Quote: Created quote# " + PolicySummaryPage.getPolicyNumber());
	}
	
}
