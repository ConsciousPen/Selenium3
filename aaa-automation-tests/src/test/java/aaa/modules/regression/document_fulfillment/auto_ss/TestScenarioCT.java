package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

public class TestScenarioCT extends AutoSSBaseTest {
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);

	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();

		createCustomerIndividual();
		String quoteNumber = createQuote();

		policy.quoteDocGen().start();
		docgenActionTab.generateDocuments(Documents.AHCAAG);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AHCAAG);
		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		policy.dataGather().getView().fillFromTo(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase").resolveLinks()), DocumentsAndBindTab.class, PurchaseTab.class, true);
		policy.dataGather().getView().getTab(PurchaseTab.class).submitTab();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		//DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AARFIXX);		
		
		JobUtils.executeJob(Jobs.aaaCCardExpiryNoticeJob, true);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._60_5006);
	}
}
