package aaa.modules.regression.document_fulfillment.auto_ca.select;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;

public class TestScenarioManualReinstate extends AutoCaSelectBaseTest {
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testManualReinstate(@Optional("CA") String state) {
		
		mainApp().open();

		getCopiedPolicy();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);


		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusDays(1));
		
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		
		log.info("TEST: Reinstate Policy #" + policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._55_5080);
		
		
	}
}
