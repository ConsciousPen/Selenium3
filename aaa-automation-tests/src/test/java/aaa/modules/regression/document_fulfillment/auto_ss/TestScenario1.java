package aaa.modules.regression.document_fulfillment.auto_ss;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

/**
 * Check AH65XX for CT, MD, MT, VA, WV
 * @author qyu
 *
 */
public class TestScenario1 extends AutoSSBaseTest {
	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		policyNumber = getCopiedPolicy();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_DoNotRenew(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCheckUWRules(policyExpirationDate));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
	}

	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_RenewaOfferGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.policyDoNotRenewAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH65XX);
	}
}
