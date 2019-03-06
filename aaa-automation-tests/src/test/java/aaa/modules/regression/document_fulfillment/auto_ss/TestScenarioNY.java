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
import aaa.helpers.jobs.BatchJob;
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

public class TestScenarioNY extends AutoSSBaseTest {
	private DocumentsAndBindTab documentsAndBindTab = policy.getDefaultView().getTab(DocumentsAndBindTab.class);
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	private LocalDateTime policyExpirationDate;
	private String policyNumber;

	@Parameters({"state"})
	@StateList(states = States.NY)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void TC01_CreatePolicy(@Optional("") String state) {
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

		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AACDNYR);

		LocalDateTime updatePolicyStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updatePolicyStatusDate);
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);

		LocalDateTime insuranceRenewalReminderDate = getTimePoints().getInsuranceRenewalReminderDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(insuranceRenewalReminderDate);
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);
		JobUtils.executeJob(BatchJob.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);

		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH64XX);
	}

}
