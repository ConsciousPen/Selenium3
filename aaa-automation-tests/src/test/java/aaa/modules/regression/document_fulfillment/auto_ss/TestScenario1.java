package aaa.modules.regression.document_fulfillment.auto_ss;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
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

	@Parameters({"state"})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		policyNumber = createPolicy();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCheckUWRules(policyExpirationDate));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.doNotRenewJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH65XX);
	}

}
