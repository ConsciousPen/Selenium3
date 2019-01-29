package aaa.modules.regression.document_fulfillment.auto_ss;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenarioCO extends AutoSSBaseTest {
	private LocalDateTime policyExpirationDate;
	private String policyNumber;

	@Parameters({ "state" })
	@StateList(states = States.CO)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA02CO, Documents.AARFIXX, Documents.AA52COA);

		/* Endorse */
		TestData td = getPolicyTD("Endorsement", "TestData").adjust(getTestSpecificTD("TestData_Endorsement").resolveLinks());
		policy.createEndorsement(td);
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA02CO, Documents.AA52COB);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.CO)
	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_RenewalImageGeneration(@Optional("") String state) {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		policy.getDefaultView().fill(getTestSpecificTD("TestData_AddRenewal"));
	}

	@Parameters({ "state" })
	@StateList(states = States.CO)
	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_RenewaOfferGeneration(@Optional("") String state) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AARNXX, Documents.AA02CO, Documents.AA10XX);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.CO)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_RenewaOfferBillGeneration(@Optional("") String state) {
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AA71COA);
	}
}
