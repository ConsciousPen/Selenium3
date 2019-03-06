package aaa.modules.regression.document_fulfillment.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.DocGenEnum;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Tatsiana Saltsevich
 * @name Test Earned Premium Write off Billing functionality ("HO_PTL_TC_211_Bind_Policy")
 * @scenario
 * 1. (NB -New Buisness) Precondition: Agent with authority to create a Property policy exists.
 * Agent initiates a Property quote <<<Product>>>. Agent creates a quote such that Single PPC is returned.
 * 2. Agent enters all mandatory details in General page,  enters valid Dwelling address details in Applicant page and validates it,
 * other mandatory details and navigates to next page.
 * 3. Agent validates whether the "Order report" link is enabled under ISO PPC report section in Reports page.
 * 4. Agent clicks on "Order report" link under ISO PPC report section in Reports page.
 * 5. Order all the reports and click Continue. Agent enters all mandatory details in Property info and clicks "Continue".
 * 8. Agent enters all mandatory details required for bind in pages and clicks "Purchase" in bind page.
 * In purchase screen Minimum Required Down Payment should be changed to $0.00.
 * 9. (NB+50) The above created active Policy is cancelled due to Insured Request. System displays the status of the Policy as Policy cancelled.
 *Total due in billing account is > $0.00
 * 10. (C+45) Run aaaCollectionCancelDebtBatchJob. Run aaaDocGenBatchJob.
 * Verify that collection notice document is generated (CANCELLDEBETNOTICE_45 event is generated in AAADocGenEntity, look for form 55 6103)
 * 11. (C+60) Run collectionFeedBatch_earnedPremiumWriteOff (contains collectionFeedBatchOrderJob and earnedPremiumWriteOffProcessingJob)
 * System reflects the current term policy values as follows:
 * Min Due = $0.00
 * Past Due = $0.00
 * Total Due = $0.00
 * In billing tab there is earned premium write off transaction created.
 * System triggers to assign the Policy to the Outbound Feed sent to the Collection Agency
 *
 */

public class TestEarnedPremiumWriteOff extends HomeSSHO3BaseTest {
	private String policyNumber;
	private LocalDateTime policyCancellationDate;
	private LocalDateTime effDate;

	@Parameters({"state"})
	@StateList(states = Constants.States.UT)
	@Test(groups = {Groups.TIMEPOINT, Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_HO3)
	public void testEarnedPremiumWriteOff(@Optional("UT") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData")));
		effDate = PolicySummaryPage.getEffectiveDate();
		policyCancellationDate = getTimePoints().getCancellationDate(effDate);

		TimeSetterUtil.getInstance().nextPhase(policyCancellationDate);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getEarnedPremiumBillThirdManualCancellation(policyCancellationDate));
		JobUtils.executeJob(BatchJob.aaaCollectionCancellDebtBatchAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents._55_6103);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getEarnedPremiumWriteOffManualCancellation(policyCancellationDate));
		JobUtils.executeJob(BatchJob.collectionFeedBatch_earnedPremiumWriteOff);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		BillingSummaryPage.getMinimumDue().verify.equals(new Dollar(0));
		BillingSummaryPage.getPastDue().verify.equals(new Dollar(0));
		BillingSummaryPage.getTotalDue().verify.equals(new Dollar(0));
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isPresent();
	}
}
