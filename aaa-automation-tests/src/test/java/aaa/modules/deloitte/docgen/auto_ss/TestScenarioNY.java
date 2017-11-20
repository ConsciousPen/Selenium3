package aaa.modules.deloitte.docgen.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

public class TestScenarioNY extends AutoSSBaseTest {
	private DocumentsAndBindTab documentsAndBindTab = policy.getDefaultView().getTab(DocumentsAndBindTab.class);
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		
		createCustomerIndividual();
		String quoteNumber = createQuote();
		
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.BTN_GENERATE_DOCUMENTS).click();
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AAOANY);
		documentsAndBindTab.cancel();
		
		/* Purchase */
		policy.calculatePremiumAndPurchase(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase")));
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		DocGenHelper.verifyDocumentsGenerated(policyNumber, 
				Documents.AAMTNY, 
				Documents.FS20, 
				Documents.AADNNY2,
				Documents.AAACNY);
		policy.policyDocGen().start();
		docgenActionTab.verify.documentsPresent(Documents.AAIFNY2);
		docgenActionTab.cancel();
		
		/* Endorse */
		TestData td = getPolicyTD("Endorsement", "TestData").adjust(getTestSpecificTD("TestData_Endorsement").resolveLinks());
		policy.createEndorsement(td);
		policy.policyDocGen().start();
		docgenActionTab.verify.documentsPresent(Documents.AAIFNYF);		
	}
}
