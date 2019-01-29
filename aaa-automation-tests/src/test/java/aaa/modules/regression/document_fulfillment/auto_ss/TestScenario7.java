package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;

/**
 * Check AH60XXA for OH
 * @author qyu
 *
 */
public class TestScenario7 extends AutoSSBaseTest {
	
	@Parameters({ "state" })
	@StateList(states = States.OH)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		policy.cancel().perform(getTestSpecificTD("TestData_Cancellation"));
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH60XXA);
	}
}
