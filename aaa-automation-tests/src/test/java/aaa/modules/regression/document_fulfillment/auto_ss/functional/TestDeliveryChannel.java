package aaa.modules.regression.document_fulfillment.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * @author Megha Gubbala
 * @name Test Document Generation - default to Preferred Channel
 * @scenario
 * 0. Create an eValue Quote
 * 1. Go to the document and bind page and click on Generate document
 * 2. verify channel type in DB
 * 3. Go to document and bind page and click on Generate document
 * 4. verify channel type in DB
 * 5. bind the policy and verify document type in DB
 * 6. Go to GOD page and verify any document and choose print channel central print
 * 7. generate document and verify channel type in DB
 * 8. Go to GOD page and verify any document and choose print channel local print
 * 9. generate document and verify channel type in DB
 * 10.Go to GOD page and verify any document and choose print channel email print
 * 11. generate document and verify channel type in DB
 */
public class TestDeliveryChannel extends AutoSSBaseTest {

	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-358"})
	public void pas358_documentGenerationDefaultToPreferredChannel(@Optional("VA") String state) {
		mainApp().open();
		testEValueDiscount.eValueQuoteCreation();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnGenerateDocuments.click();

		WebDriverHelper.switchToDefault();

		String getDocumentChannelFromDb = "select * from( "
				+ "select * from aaadocgenentity "
				+ "where ENTITYID in (select id from policysummary where policynumber = '%s') "
				+ "order by id desc "
				+ ")temp "
				+ "where rownum=1 "
				+ "and data like '%%%s%%' order by creationdate desc";

		assertThat(DBService.get().getValue(String.format(getDocumentChannelFromDb, quoteNumber, "LocalPrintChannel")).get()).isNotBlank();

		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS).click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
		documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.RECIPIENT_EMAIL_ADDRESS).setValue("test@email.com");
		documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.BTN_OK).click();
		Page.dialogConfirmation.buttonOk.click();
		Waiters.SLEEP(2000).go();

		assertThat(DBService.get().getValue(String.format(getDocumentChannelFromDb, quoteNumber, "ESignatureChannel")).get()).isNotBlank();
		BindTab.buttonSaveAndExit.click();

		testEValueDiscount.simplifiedQuoteIssue();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		assertThat(DBService.get().getValue(String.format(getDocumentChannelFromDb, policyNumber, "PreferredChannel")).get()).isNotBlank();

		GenerateOnDemandDocumentActionTab generateOnDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();

		policy.policyDocGen().start();
		generateOnDemandDocumentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.LOCAL_PRINT, DocGenEnum.Documents.AA10XX);
		generateOnDemandDocumentActionTab.buttonOk.click();
		Waiters.SLEEP(2000).go();
		assertThat(DBService.get().getValue(String.format(getDocumentChannelFromDb, policyNumber, "LocalPrintChannel")).get()).isNotBlank();
		generateOnDemandDocumentActionTab.cancel();
		policy.policyDocGen().start();

		generateOnDemandDocumentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.CENTRAL_PRINT, DocGenEnum.Documents.AA10XX);
		assertThat(DBService.get().getValue(String.format(getDocumentChannelFromDb, policyNumber, "CentralPrintChannel")).get()).isNotBlank();

		policy.policyDocGen().start();
		generateOnDemandDocumentActionTab.generateDocuments(DocGenEnum.DeliveryMethod.EMAIL, "aaa@aaa.com", null, null, DocGenEnum.Documents.AA10XX);
		assertThat(DBService.get().getValue(String.format(getDocumentChannelFromDb, policyNumber, "EmailChannel")).get()).isNotBlank();

	}

}
