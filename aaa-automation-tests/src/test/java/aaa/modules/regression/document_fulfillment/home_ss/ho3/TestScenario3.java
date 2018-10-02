package aaa.modules.regression.document_fulfillment.home_ss.ho3;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.utils.TestInfo;

public class TestScenario3 extends HomeSSHO3BaseTest {
	private String policyNumber;
	private LocalDateTime policyCancellationDate;

	@Parameters({"state"})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3, testCaseId = {"HO_PTL_TC_211_Bind_Policy"})
	public void PolicyPPCReportAndCancelDebtNoticeGeneration(@Optional("UT") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData")));
		policyCancellationDate = PolicySummaryPage.getEffectiveDate().plusDays(50);

		TimeSetterUtil.getInstance().nextPhase(policyCancellationDate);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		TimeSetterUtil.getInstance().nextPhase(policyCancellationDate.plusDays(45));
		JobUtils.executeJob(Jobs.aaaCollectionCancelDebtBatchJob, true);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents._55_6103);

		TimeSetterUtil.getInstance().nextPhase(policyCancellationDate.plusDays(60));
		JobUtils.executeJob(Jobs.collectionFeedBatch_earnedPremiumWriteOff, true);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		BillingSummaryPage.getMinimumDue().verify.equals(new Dollar(0));
		BillingSummaryPage.getPastDue().verify.equals(new Dollar(0));
		BillingSummaryPage.getTotalDue().verify.equals(new Dollar(0));
	}
}
