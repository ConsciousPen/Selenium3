package aaa.modules.deloitte.docgen.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.verification.CustomAssert;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.policy.AutoSSBaseTest;

public class TestScenario4 extends AutoSSBaseTest {
	private DocumentsAndBindTab documentsAndBindTab = policy.getDefaultView().getTab(DocumentsAndBindTab.class);
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();
		
		// Create quote
		createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_QuoteCreation").resolveLinks()));
		
		// Verify: In "Documents Available for Printing" section,
		// "Consumer Information Notice" form is listed, selected and enabled
		// (radio button="Yes")
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		switch(getState()){
		case States.VA:
			documentsAndBindTab.verifyFieldHasValue(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ADVERSE_ACTION_UNDERWRITING_DECISION_NOTICE.getLabel(), "Yes");
			break;
		default: 
			documentsAndBindTab.verifyFieldHasValue(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.CONSUMER_INFORMATION_NOTICE.getLabel(), "Yes");
			break;
		}

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
