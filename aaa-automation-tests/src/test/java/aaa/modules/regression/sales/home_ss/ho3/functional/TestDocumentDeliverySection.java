package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.service.helper.HelperWireMockPaperlessPreferences;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import java.util.LinkedList;
import java.util.List;

public class TestDocumentDeliverySection extends HomeSSHO3BaseTest {

	private BindTab bindTab = new BindTab();
	private static List<String> requestIdList = new LinkedList<>();
	/**
	 * @author Jovita Pukenaite
	 * @name Test Document Delivery section when paperless preferences OPT_OUT/ OPT_IN during endorsement.
	 * @scenario
	 * 1. Create HomeSS HO3 policy.
	 * 2. Set policy paperless preferences to opt_out.
	 * 3. Start endorsement.
	 * 4. Dont change paperless preferences and go to the bind tab.
	 * 5. Check document delivery section. Should be displaying.
	 * 6. Set policy paperless preferences to opt_in.
	 * 7. Start endorsement again.
	 * 8. Check document delivery section. Should not be displaying.
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3,testCaseId = "PAS-12458")

	public void pas12458_documentDeliverySectionDuringEndorsement (@Optional("VA") String state) {

		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_OUT.get());
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.getPaperlessPreferencesAssetList().getAsset(HomeSSMetaData.BindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("No");
		bindTab.getDocumentPrintingDetailsAssetList().getAsset(HomeSSMetaData.BindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(true);
		bindTab.getDocumentPrintingDetailsAssetList().getAsset(HomeSSMetaData.BindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).setValue("Email");
		bindTab.getDocumentPrintingDetailsAssetList().getAsset(HomeSSMetaData.BindTab.DocumentPrintingDetails.INCLUDE_WITH_EMAIL).verify.present(true);
		bindTab.cancel(true);
		deleteSinglePaperlessPreferenceRequest(requestId);

		String requestId2 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN.get());
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.getPaperlessPreferencesAssetList().getAsset(HomeSSMetaData.BindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");
		bindTab.getDocumentPrintingDetailsAssetList().getAsset(HomeSSMetaData.BindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId2);
		bindTab.cancel(true);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Test Document Delivery section when paperless preferences OPT_IN_PENDING/IN/OUT
	 * @scenario
	 * 1. Create HomeSS HO3 quote.
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
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3,testCaseId = "PAS-12458")

	public void pas12458_documentDeliverySectionDataGatherMode (@Optional("VA") String state) {

		mainApp().open();
		createCustomerIndividual();
		String quoteNumber = createQuote();

		String requestId = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.getPaperlessPreferencesAssetList().getAsset(HomeSSMetaData.BindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Pending");
		bindTab.getDocumentPrintingDetailsAssetList().getAsset(HomeSSMetaData.BindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId);
		bindTab.cancel(true);

		String requestId2 = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.getPaperlessPreferencesAssetList().getAsset(HomeSSMetaData.BindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");
		bindTab.getDocumentPrintingDetailsAssetList().getAsset(HomeSSMetaData.BindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId2);
		bindTab.cancel(true);

		String requestId3 = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_OUT.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.getPaperlessPreferencesAssetList().getAsset(HomeSSMetaData.BindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("No");
		bindTab.getDocumentPrintingDetailsAssetList().getAsset(HomeSSMetaData.BindTab.DocumentPrintingDetails.METHOD_OF_DELIVERY).verify.present(false);
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
