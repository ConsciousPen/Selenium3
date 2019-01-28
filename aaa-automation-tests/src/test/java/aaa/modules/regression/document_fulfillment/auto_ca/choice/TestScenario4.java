package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.auto_ca.actiontabs.PolicyDocGenActionTab;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;

public class TestScenario4 extends AutoCaChoiceBaseTest {
	private String policyNum;
	PolicyDocGenActionTab docgenTab = policy.policyDocGen().getView().getTab(PolicyDocGenActionTab.class);
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_Cancellation(@Optional("") String state) {
		mainApp().open();
		policyNum = getCopiedPolicy();
		policy.policyDocGen().start();
		docgenTab.generateDocuments(getTestSpecificTD("TestData"), DocGenEnum.Documents.CAU08);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(policyNum, DocGenEnum.Documents.CAU08, DocGenEnum.Documents.F1455);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
	}

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC02_checkAH63XX(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(33));
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH63XX, DocGenEnum.Documents.AH61XXA);
	}
}
