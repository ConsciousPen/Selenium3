package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.service.helper.HelperWireMockPaperlessPreferences;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.util.LinkedList;
import java.util.List;

public class TestDocumentDeliverySection extends AutoSSBaseTest{
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private static List<String> requestIdList = new LinkedList<>();

	/**
	 * @author Jovita Pukenaite
	 * @name Test Document Delivery section when paperless preferences OPT_OUT/ OPT_IN during endorsement.
	 * @scenario
	 * 1. Create AutoSS policy.
	 * 2. Set policy paperless preferences to opt_out.
	 * 3. Start endorsement.
	 * 4. Don't change paperless preferences and go to the bind tab.
	 * 5. Check document delivery section. Should be displaying.
	 * 6. Set policy paperless preferences to opt_in.
	 * 7. Open endorsement data gather mode.
	 * 8. Check document delivery section. Should not be displaying.
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS,testCaseId = "PAS-12458")

	public void pas12458_documentDeliverySectionDuringEndorsement (@Optional("VA") String state) {

		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_OUT.get());
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("No");
		documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(true);
		documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).setValue("Email");
		documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(true);
		documentsAndBindTab.saveAndExit();
		deleteSinglePaperlessPreferenceRequest(requestId);

		String requestId2 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN.get());
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");
		documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId2);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Test Document Delivery section when paperless preferences OPT_IN_PENDING/IN/OUT
	 * @scenario
	 * 1. Create AutoSS quote.
	 * 2. Set policy preferences opt_in_pending.
	 * 3. Go to data gather mode, bind tab.
	 * 4. Check document delivery section. Should not be displaying.
	 * 5. Set policy preferences opt_in.
	 * 6. Go to data gather mode, bind tab.
	 * 7. Check document delivery section. Should not be displaying.
	 * 8. Set policy preferences opt_out.
	 * 9. Go to data gather mode, bind tab.
	 * 10. Check document delivery section. Should not be displaying.
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS,testCaseId = "PAS-12458")

	public void pas12458_documentDeliverySectionDataGatherMode (@Optional("VA") String state) {

		mainApp().open();
		createCustomerIndividual();
		String quoteNumber = createQuote();

		String requestId = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Pending");
		documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId);
		documentsAndBindTab.cancel(true);

		String requestId2 = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");
		documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId2);
		documentsAndBindTab.cancel(true);

		String requestId3 = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_OUT.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("No");
		documentsAndBindTab.getDocumentPrintingDetailsAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId3);
	}

	@AfterSuite()
	public void deleteAllPaperlessPreferencesRequests() {
		deleteMultiplePaperlessPreferencesRequests();
		printToLog("ALL REQUEST DELETION WAS EXECUTED");
	}

	private String createPaperlessPreferencesRequestId(String policyNumber, String scenarioJsonFile) {
		String requestId = HelperWireMockPaperlessPreferences.setPaperlessPreferencesToValue(policyNumber, scenarioJsonFile);
		requestIdList.add(requestId);
		printToLog("THE REQUEST ID WAS CREATED " + requestId);
		return requestId;
	}

	private void deleteMultiplePaperlessPreferencesRequests() {
		for (Object requestId : requestIdList) {
			HelperWireMockPaperlessPreferences.deleteProcessedRequestFromStub(requestId.toString());
			printToLog("MULTIPLE REQUEST DELETION WAS EXECUTED for " + requestId);
		}
		requestIdList.clear();
	}

	private void deleteSinglePaperlessPreferenceRequest(String requestId) {
		HelperWireMockPaperlessPreferences.deleteProcessedRequestFromStub(requestId);
		requestIdList.remove(requestId);
		printToLog("DELETE SINGLE REQUEST WAS EXECUTED for " + requestId);
	}
}
