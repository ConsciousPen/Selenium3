package aaa.modules.regression.document_fulfillment.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class TestScenarioNY extends AutoSSBaseTest {
	private DocumentsAndBindTab documentsAndBindTab = policy.getDefaultView().getTab(DocumentsAndBindTab.class);
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	private LocalDateTime policyExpirationDate;
	private String policyNumber;
	
	@Parameters({ "state" })
	@StateList(states = States.NY)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();
			String currentHandle = WebDriverHelper.getWindowHandle();

			createCustomerIndividual();
			String quoteNumber = createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));

			policy.dataGather().start();
			NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.BTN_GENERATE_DOCUMENTS).click();
			WebDriverHelper.switchToWindow(currentHandle);
			DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AAOANY);
			documentsAndBindTab.cancel();

			/* Purchase */
			policy.calculatePremiumAndPurchase(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase")));
			policyNumber = PolicySummaryPage.getPolicyNumber();
			policyExpirationDate = PolicySummaryPage.getExpirationDate();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);
			DocGenHelper.verifyDocumentsGenerated(policyNumber,
					Documents.AAMTNY,
					Documents.FS20,
					Documents.AADNNY2,
					Documents.AAACNY);
			policy.policyDocGen().start();
			docgenActionTab.verify.documentsPresent(Documents.AAIFNY2, Documents.AAIFNYC);
			docgenActionTab.cancel();

			/* Endorse */
			TestData td = getPolicyTD("Endorsement", "TestData").adjust(getTestSpecificTD("TestData_Endorsement").resolveLinks());
			policy.createEndorsement(td);
			policy.policyDocGen().start();
			docgenActionTab.verify.documentsPresent(Documents.AAIFNYF);
			docgenActionTab.buttonCancel.click();
		});
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NY)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_RenewaOfferBillGeneration(@Optional("") String state) {
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AACDNYR);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NY)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_UpdatePolicyStatus(@Optional("") String state) {
		LocalDateTime updatePolicyStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updatePolicyStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NY)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_InsuranceRenewalReminder(@Optional("") String state) {
		LocalDateTime insuranceRenewalReminderDate = getTimePoints().getInsuranceRenewalReminderDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(insuranceRenewalReminderDate);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		JobUtils.executeJob(Jobs.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH64XX);
	}
}
