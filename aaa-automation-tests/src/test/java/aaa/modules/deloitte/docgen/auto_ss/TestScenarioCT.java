package aaa.modules.deloitte.docgen.auto_ss;

import java.time.LocalDateTime;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

public class TestScenarioCT extends AutoSSBaseTest {
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	private LocalDateTime policyExpirationDate;
	private String policyNumber;

	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		
		createCustomerIndividual();
		String quoteNumber = createQuote();
		
		policy.quoteDocGen().start();
		docgenActionTab.generateDocuments(Documents.AHCAAG);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AHCAAG);
		
		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		policy.dataGather().getView().fillFromTo(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase").resolveLinks()), DocumentsAndBindTab.class, PurchaseTab.class, true);
		policy.dataGather().getView().getTab(PurchaseTab.class).submitTab();
		policyNumber = PolicySummaryPage.getPolicyNumber();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AARFIXX);
		
		policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
	}
	
	@Parameters({ "state" })
//	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_RenewalImageGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1,true);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2,true);
	}

	@Parameters({ "state" })
//	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_RenewPreviewGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2,true);
	}
	
	@Parameters({ "state" })
//	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_RenewaOfferBillGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.doNotRenewJob,true);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob,true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH65XX);
	}
}
