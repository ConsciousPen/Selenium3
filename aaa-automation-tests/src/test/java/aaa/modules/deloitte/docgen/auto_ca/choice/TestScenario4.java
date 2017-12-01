package aaa.modules.deloitte.docgen.auto_ca.choice;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.modules.policy.AutoCaChoiceBaseTest;

public class TestScenario4 extends AutoCaChoiceBaseTest {
	private String policyNum;
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_Cancellation(String state) throws Exception {
		mainApp().open();
		policyNum = getCopiedPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
	}
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC02_checkAH63XX(String state) throws Exception {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(33));
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob, true);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, Documents.AH63XX);
	}
}
