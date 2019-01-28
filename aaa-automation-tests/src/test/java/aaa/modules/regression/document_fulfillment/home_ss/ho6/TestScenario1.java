package aaa.modules.regression.document_fulfillment.home_ss.ho6;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.utils.StateList;

/**
 * Check AH64XX
 * @author qyu
 *
 */
public class TestScenario1 extends HomeSSHO6BaseTest {
	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
	}
	
	@Parameters({ "state" })
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_RenewImageGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}
	
	@Parameters({ "state" })
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_RenewaOfferBillGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}
	
	@Parameters({ "state" })
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_UpdatePolicyStatus(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
	}
	
	@Parameters({ "state" })
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_CancellationNotice(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		JobUtils.executeJob(Jobs.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH64XX);
	}
}
