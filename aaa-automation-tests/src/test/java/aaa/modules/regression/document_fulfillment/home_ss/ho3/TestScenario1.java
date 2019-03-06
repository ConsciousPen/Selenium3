package aaa.modules.regression.document_fulfillment.home_ss.ho3;

import java.time.LocalDateTime;
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
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;

/**
 * Check AH62XX
 * @author qyu
 *
 */
public class TestScenario1 extends HomeSSHO3BaseTest {
	private String policyNumber;
	private LocalDateTime policyEffectiveDate;
	
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = { Groups.DOCGEN, Groups.REGRESSION, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData")));
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.HS_04_59);

		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(3));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(5));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		//TODO aperapecha: DocGen - remove shift after upgrade
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH62XX);
	}


}
