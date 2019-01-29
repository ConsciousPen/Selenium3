package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.DocGenEnum;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;

public class TestScenario3 extends AutoCaChoiceBaseTest {
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testAH61XX(@Optional("CA") String state) {
		mainApp().open();
		String policyNum = getCopiedPolicy();
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData_SubstantialIncrease"));

		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH61XX);
	}

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testAH62XX(@Optional("CA") String state) {
		mainApp().open();
		String policyNum = getCopiedPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		policy.reinstate().perform(getTestSpecificTD("TestData_Reinstate"));
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH62XX);
	}
}
