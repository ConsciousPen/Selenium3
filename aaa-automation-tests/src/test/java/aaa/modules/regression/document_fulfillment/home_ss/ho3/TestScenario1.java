package aaa.modules.regression.document_fulfillment.home_ss.ho3;

import java.time.LocalDateTime;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

/**
 * Check AH62XX
 * @author qyu
 *
 */
public class TestScenario1 extends HomeSSHO3BaseTest {
	private String policyNumber;
	private LocalDateTime policyEffectiveDate;
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(String state) throws Exception {
		mainApp().open();
		policyNumber = getCopiedPolicy();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
	}
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_CancelPolicy(String state) throws Exception {
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(3));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
	}
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_ReinstatePolicy(String state) throws Exception {
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(5));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH62XX);
	}

}
