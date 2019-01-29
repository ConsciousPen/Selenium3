package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;

public class TestScenario5 extends AutoCaChoiceBaseTest {
	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	
	@Parameters({ "state" })
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
	}
	
	@Parameters({ "state" })
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_RenewaOfferGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AHPNCAA);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_UpdatePolicyStatus(@Optional("") String state) {
		LocalDateTime updatePolicyStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updatePolicyStatusDate);
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_InsuranceRenewalReminder(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getInsuranceRenewalReminderDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH64XX);
	}
}
