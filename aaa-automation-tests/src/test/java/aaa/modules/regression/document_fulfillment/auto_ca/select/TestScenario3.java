package aaa.modules.regression.document_fulfillment.auto_ca.select;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.datetime.DateTimeUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;

/**
 * Validate if AH34XX and AHTPCCA are generated
 * @author qyu
 *
 */
public class TestScenario3 extends AutoCaSelectBaseTest {
	private String policyNumber;
	private LocalDateTime dd1;
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusYears(1));
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		BillingSummaryPage.open();
		dd1 = BillingSummaryPage.getInstallmentDueDate(2);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_BillGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd1));
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_CancellationNotice(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(dd1));
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH34XX, Documents.AHTPCCA);
	}
}
