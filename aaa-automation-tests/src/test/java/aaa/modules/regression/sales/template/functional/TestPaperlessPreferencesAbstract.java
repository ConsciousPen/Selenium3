/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.HelperWireMockPaperlessPreferences;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import org.testng.annotations.AfterSuite;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.LinkedList;
import java.util.List;

public abstract class TestPaperlessPreferencesAbstract extends PolicyBaseTest {

	protected abstract String getDocumentsAndBindTab();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract InquiryAssetList getInquiryAssetList();

	protected abstract AssetDescriptor<TextBox> getEnrolledInPaperless();
	protected abstract AssetDescriptor<Button> getButtonManagePaperlessPreferences();
	protected abstract AssetDescriptor<Button> getEditPaperlessPreferencesButtonDone();
	protected abstract AssetList getPaperlessPreferencesAssetList();

	protected abstract AssetDescriptor<TextBox> getIssueDate();
	protected abstract AssetDescriptor<ComboBox> getMethodOfDelivery();
	protected abstract AssetDescriptor<ComboBox> getIncludeWithEmail();
	protected abstract AssetList getDocumentPrintingDetailsAssetList();
	protected abstract AssetDescriptor<ComboBox> getSuppressPrint();

	public abstract void pas283_paperlessPreferencesForAllStatesProducts(String state);

	private static List<String> requestIdList = new LinkedList<>();

	/**
	 * @author Oleg Stasyuk
	 * @name Test Paperless Preferences properties and Inquiry mode
	 * @scenario 1. Create new Paperless Preferences eligible quote but for the not eligible state (PA)
	 * 2. Check Enrolled in Paperless? field and Manage Paperless Preferences button are shown in Documents tab
	 * 3. Check Manage Paperless Preferences button is enabled
	 * 4. Check Documents Delivery section fields are present
	 * 5. Check Help text for Paperless option
	 * 4. Check Paperless Preferences Popup can be opened and closed
	 * @details
	 */

	protected void pas283_paperlessPreferencesForAllStatesProducts() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		//SearchPage.openPolicy("VASS952918561");

		//pas283_eValuePaperlessPreferences should be put here for all states and products
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		InquiryAssetList inquiryAssetList = new InquiryAssetList(new DocumentsAndBindTab().getAssetList().getLocator(), AutoSSMetaData.DocumentsAndBindTab.class);

		inquiryAssetList.assetSectionPresence("Paperless Preferences");
		inquiryAssetList.assetSectionPresence("Document Delivery Details", false);
		getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless()).verify.present();
		getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless()).verify.enabled(false);
		getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless()).verify.value("Yes");

		getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences()).verify.present();
		getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences()).verify.enabled();

		//left overs of previous functionality. Showing Hiding rules will change with new story
		//getDocumentsAndBindTabElement().getAssetList().getAsset(getSuppressPrint()).verify.present(); //TODO not for PUP, if uncomment, then  add "if ProductType"
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).verify.present(false);
		getDocumentPrintingDetailsAssetList().getAsset(getIncludeWithEmail()).verify.present(false);
		//left overs of previous functionality. Lookup list will change with new story

		//PAS-3097 remove the Issue Date field from Bind tab (VA state)
		inquiryAssetList.assetFieldsAbsence( "Issue Date");
		getDocumentPrintingDetailsAssetList().getAsset(getIssueDate()).verify.present(false);
		//PAS-3097 end

		inquiryAssetList.assetFieldsAbsence( "Method Of Delivery");
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).verify.present(false);
		inquiryAssetList.assetFieldsAbsence( "Include with Email");
		getDocumentPrintingDetailsAssetList().getAsset(getIncludeWithEmail()).verify.present(false);

		//PAS-266 start
		getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences()).click();
		Waiters.SLEEP(5000).go();
		getPaperlessPreferencesAssetList().getAsset(getEditPaperlessPreferencesButtonDone()).click();
		//PAS-282, PAS-268, PAS-266 end

		//PAS-287 start
		String helpMessageHT10001 = "Indicates the customer's paperless notifications enrollment status. If \"Pending\", advise the customer to accept the terms and conditions. During mid-term, you may not be able to complete the endorsement until the status has changed to \"Yes\".";
		CustomAssert.assertTrue(DocumentsAndBindTab.helpIconPaperlessPreferences.getAttribute("title").equals(helpMessageHT10001));
		//PAS-287 end

		//PAS-277 start
		inquiryAssetList.assetSectionPresence("Document Delivery Details", false);
		inquiryAssetList.assetFieldsAbsence("Send To", "Country", "Zip/Postal Code", "Address Line 1", "Address Line 2", "Address Line 3", "City", "State / Province", "Notes", "Issue Date");
		//PAS-277 end

		getDocumentsAndBindTabElement().saveAndExit();
		policy.quoteInquiry().start();

		//PAS-269 start
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		inquiryAssetList.assetSectionPresence("Paperless Preferences");
		getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences()).verify.present();
		getPaperlessPreferencesAssetList().getAsset(getButtonManagePaperlessPreferences()).verify.enabled(false);
		inquiryAssetList.getStaticElement(getEnrolledInPaperless().getLabel()).verify.present();
		inquiryAssetList.getStaticElement(getIssueDate().getLabel()).verify.present(false);
		inquiryAssetList.getStaticElement(getMethodOfDelivery().getLabel()).verify.present(false);
		inquiryAssetList.getStaticElement(getIncludeWithEmail().getLabel()).verify.present(false);//will change based on View/Hide rules
		getDocumentsAndBindTabElement().cancel();
		//PAS-269 end
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Test Document Delivery section when paperless preferences OPT_OUT/ OPT_IN during endorsement.
	 * @scenario
	 * 1. Create policy.
	 * 2. Set policy paperless preferences to opt_out.
	 * 3. Start endorsement.
	 * 4. Don't change paperless preferences and go to the bind tab.
	 * 5. Check document delivery section. Should be displaying.
	 * 6. Set policy paperless preferences to opt_in.
	 * 7. Start endorsement again.
	 * 8. Check document delivery section. Should not be displaying.
	 * @details
	 */

	protected void pas12458_documentDeliverySectionDuringEndorsement() {

		String policyNumber = PolicySummaryPage.getPolicyNumber();
		String requestId = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_OUT.get());
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());

		getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless()).verify.value("No");
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).verify.present(true);
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).setValue("Email");
		getDocumentPrintingDetailsAssetList().getAsset(getIncludeWithEmail()).verify.present(true);
		getDocumentsAndBindTabElement().saveAndExit();
		deleteSinglePaperlessPreferenceRequest(requestId);

		String requestId2 = createPaperlessPreferencesRequestId(policyNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN.get());
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless()).verify.value("Yes");
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId2);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Test Document Delivery section when paperless preferences OPT_IN_PENDING/IN/OUT
	 * @scenario
	 * 1. Create quote.
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

	protected void pas12458_documentDeliverySectionDataGatherMode() {

		String quoteNumber = PolicySummaryPage.getPolicyNumber();

		String requestId = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless()).verify.value("Pending");
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId);
		getDocumentsAndBindTabElement().cancel(true);

		String requestId2 = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless()).verify.value("Yes");
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).verify.present(false);
		deleteSinglePaperlessPreferenceRequest(requestId2);
		getDocumentsAndBindTabElement().cancel(true);

		String requestId3 = createPaperlessPreferencesRequestId(quoteNumber, HelperWireMockPaperlessPreferences.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_OUT.get());
		policy.dataGather().start();
		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		getPaperlessPreferencesAssetList().getAsset(getEnrolledInPaperless()).verify.value("No");
		getDocumentPrintingDetailsAssetList().getAsset(getMethodOfDelivery()).verify.present(false);
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