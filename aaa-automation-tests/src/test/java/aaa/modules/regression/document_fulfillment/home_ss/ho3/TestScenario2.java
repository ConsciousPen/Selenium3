package aaa.modules.regression.document_fulfillment.home_ss.ho3;

import java.time.LocalDateTime;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;

/**
 * Check HSRMXX
 * @author qyu
 *
 */
public class TestScenario2 extends HomeSSHO3BaseTest {
	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(String state) throws Exception {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
//	, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_RenewImageGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
//	, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_RenewPreviewGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
//	, dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_RenewOfferGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
//	, dependsOnMethods = "TC01_CreatePolicy")
	public void TC05_RenewaBillGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		// TODO 581579	PROD : REG:HSRMXX Form not getting generated at R-20.
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.HSRMXX);
	}
}
