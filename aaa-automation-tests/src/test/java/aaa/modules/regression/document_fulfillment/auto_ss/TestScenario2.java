package aaa.modules.regression.document_fulfillment.auto_ss;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

/**
 * @author Ryan Yu
 *
 */
public class TestScenario2 extends AutoSSBaseTest {
	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AARFIXX);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_RenewOfferGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AAPNUBI, Documents.ACPPNUBI, Documents.AADNUBI);
	}
}
