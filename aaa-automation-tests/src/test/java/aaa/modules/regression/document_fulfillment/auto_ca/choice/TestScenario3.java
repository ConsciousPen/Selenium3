package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.modules.policy.AutoCaChoiceBaseTest;

public class TestScenario3 extends AutoCaChoiceBaseTest {
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testAH61XX(String state) throws Exception {
		mainApp().open();
		String policyNum = getCopiedPolicy();
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData_SubstantialIncrease"));
		
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, Documents.AH61XX);
	}
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testAH62XX(String state) throws Exception {
		mainApp().open();
		String policyNum = getCopiedPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		
		policy.reinstate().perform(getTestSpecificTD("TestData_Reinstate"));
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, Documents.AH62XX);
	}
}
