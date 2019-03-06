package aaa.modules.regression.document_fulfillment.auto_ca.select;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;

/**
 * Validate if 55 5100 is generated
 * @author qyu
 *
 */
public class TestScenario5 extends AutoCaSelectBaseTest {
	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	private LocalDateTime dd6;
	
	@Parameters({ "state" })
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData")));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		BillingSummaryPage.open();
		dd6 = BillingSummaryPage.getInstallmentDueDate(2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd6));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(10));
		JobUtils.executeJob(BatchJob.preRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._55_5100);
	}

}
