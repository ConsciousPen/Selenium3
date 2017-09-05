package aaa.modules.docgen.home_ss.ho4;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.helpers.constants.Groups;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO4BaseTest;

public class TestHO4DocgenScenarios extends HomeSSHO4BaseTest{
	private String quoteNumber;
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })

	public void TC01_Quote_Documents(){
		mainApp().open();
		createCustomerIndividual();
		TestData tdPoicy=getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
		createQuote(tdPoicy);
		quoteNumber=PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Original Quote #" + quoteNumber);
	}

}
