package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.DocGenEnum;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;

public class TestScenario3 extends AutoCaChoiceBaseTest {
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testAH61XX_AH62XX(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy();
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData_SubstantialIncrease"));


		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH61XX);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		policy.reinstate().perform(getTestSpecificTD("TestData_Reinstate"));

		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH62XX);
	}
}
