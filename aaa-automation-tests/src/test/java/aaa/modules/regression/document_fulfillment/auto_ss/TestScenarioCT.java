package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;

public class TestScenarioCT extends AutoSSBaseTest {
	
	private String quoteNumber;	
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);

	@Parameters({ "state" })
	@StateList(states = States.CT)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01(@Optional("") String state) {
		mainApp().open();
		//String currentHandle = WebDriverHelper.getWindowHandle();

		createCustomerIndividual();
		quoteNumber = createQuote();

		policy.quoteDocGen().start();
		docgenActionTab.generateDocuments(Documents.AHCAAG);
		DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AHCAAG);

		mainApp().open();

		SearchPage.openQuote(quoteNumber);

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		policy.dataGather().getView().fillFromTo(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase").resolveLinks()), DocumentsAndBindTab.class, PurchaseTab.class, true);
		policy.dataGather().getView().getTab(PurchaseTab.class).submitTab();
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		JobUtils.executeJob(BatchJob.aaaCCardExpiryNoticeAsyncJob);
		//TODO aperapecha: DocGen - remove shift after upgrade
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._60_5006); //CCEXPIRATION_NOTICE
	}
}
