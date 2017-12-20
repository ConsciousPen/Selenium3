package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.auto_ca.actiontabs.PolicyDocGenActionTab;
import aaa.modules.policy.AutoCaChoiceBaseTest;

public class TestScenario4 extends AutoCaChoiceBaseTest {
	private String policyNum;
	PolicyDocGenActionTab docgenTab = policy.policyDocGen().getView().getTab(PolicyDocGenActionTab.class);
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_Cancellation(String state) throws Exception {
		mainApp().open();
		policyNum = getCopiedPolicy();
		policy.policyDocGen().start();
		docgenTab.generateDocuments(getTestSpecificTD("TestData"), Documents.CAU08);
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.CAU08, Documents.F1455);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
	}
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC02_checkAH63XX(String state) throws Exception {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(33));
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, Documents.AH63XX, Documents.AH61XXA);
	}
}
