/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.HelperRfi;
import aaa.modules.regression.service.helper.dtoAdmin.RfiDocumentResponse;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class TestServiceRFI extends HomeSSHO3BaseTest {

	private static final DocGenEnum.Documents HSRFIXX = DocGenEnum.Documents.HSRFIXX;
	private final InquiryAssetList inquiryAssetList = new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), AutoSSMetaData.GeneralTab.class);
	private final ErrorTab errorTab = new ErrorTab();
	private final GeneralTab generalTab = new GeneralTab();
	private final DocumentsTab documentsTab = new DocumentsTab();
	private final PurchaseTab purchaseTab = new PurchaseTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private final PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private final BindTab bindTab = new BindTab();
	private final GenerateOnDemandDocumentActionTab goddTab = new GenerateOnDemandDocumentActionTab();

	/**
	 * @author Oleg Stasyuk
	 * @name RFI
	 * @scenario
	 * Signed policy application - Not Signed
	 * Proof of ENERGY STAR appliances or green home features - No
	 * Proof of central fire alarm - No
	 * Proof of central theft alarm - No
	 * Proof of plumbing, electrical, heating/cooling system and roof renovations - No
	 * @details
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-349", "PAS-341"})
	public void pas349_rfiHO3_1(@Optional("VA") String state) {
		String today = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String yearBuilt = TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), "Year built");

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData1").adjust(yearBuilt, "1939"), DocumentsTab.class, true);

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
		documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION).verify.value("Not Signed");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES).verify.value("No");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM).verify.value("No");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM).verify.value("No");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS).verify.value("No");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT).verify.value("No");
		documentsTab.saveAndExit();

		CustomAssert.enableSoftMode();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

		//BUG PAS-10779 HSRFIXX is not generated on Issue anymore
		policy.quoteDocGen().start();
		goddTab.generateDocuments(DocGenEnum.Documents.HSRFIXX);

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "HSRFIXX", "POLICY_ISSUE");
		DocGenHelper.getDocumentDataSectionsByName("FormData", DocGenEnum.Documents.HSRFIXX, query).get(0).getDocumentDataElements();
		rfiTagCheck(HSRFIXX, query, "AtBndFlg", "Y");
		rfiTagCheck(HSRFIXX, query, "OldHoModrnDiscYN", "Y");
		rfiTagCheck(HSRFIXX, query, "GrnHoDiscYN", "Y");
		rfiTagCheck(HSRFIXX, query, "RtCntrlAlrmForThft", "Y");
		rfiTagCheck(HSRFIXX, query, "RtCntrlAlrmForFire", "Y");
		rfiTagCheck(HSRFIXX, query, "SubFireDepYN", "Y");

		//PAS-341 Start
		RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), "NBA", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "DISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), "DISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "DISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS.getLabel(), "MISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT.getLabel(), "MISC", "NS");
		//PAS-341 End

		uploadDocuments(policyNumber);

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		endorseRateDocuments();
		documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION).verify.value("Pending Review (Uploaded " + today + ")");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM).verify.value("Pending Review (Uploaded " + today + ")");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM).verify.value("Pending Review (Uploaded " + today + ")");

		//check Upload Pending is present after value is changed
		documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION).setValue("Physically Signed");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM).setValue("Yes");
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();

		endorseRateDocuments();
		documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION).verify.value("Physically Signed");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM).verify.value("Pending Review (Uploaded " + today + ")");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM).verify.value("Yes");

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name RFI
	 * @scenario
	 * 1.Initiate quote creation.
	 * Signed policy application - Not Signed
	 * Proof of ENERGY STAR appliances or green home features - No
	 * Proof of central fire alarm - No
	 * Proof of central theft alarm - No
	 * Proof of home renovations for the Home Modernization discount - No
	 * @details
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-349", "PAS-341"})
	public void pas349_rfiHO3_2(@Optional("VA") String state) {
		String yearBuilt = TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), "Year built");
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData2").adjust(yearBuilt, "1941"), DocumentsTab.class, true);

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
		documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION).verify.value("Not Signed");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES).verify.value("No");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM).verify.value("No");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM).verify.value("No");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION).verify.value("No");
		documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT).verify.value("No");
		documentsTab.saveAndExit();

		CustomAssert.enableSoftMode();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

		//BUG PAS-10779 /HSRFIXX is not generated on Issue anymore
		policy.quoteDocGen().start();
		goddTab.generateDocuments(DocGenEnum.Documents.HSRFIXX);

		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "HSRFIXX", "POLICY_ISSUE");
		DocGenHelper.getDocumentDataSectionsByName("FormData", DocGenEnum.Documents.HSRFIXX, query).get(0).getDocumentDataElements();
		rfiTagCheck(HSRFIXX, query, "AtBndFlg", "Y");
		rfiTagCheck(HSRFIXX, query, "NewHoModrnDiscYN", "Y");
		rfiTagCheck(HSRFIXX, query, "GrnHoDiscYN", "Y");
		rfiTagCheck(HSRFIXX, query, "RtCntrlAlrmForThft", "Y");
		rfiTagCheck(HSRFIXX, query, "RtCntrlAlrmForFire", "Y");
		rfiTagCheck(HSRFIXX, query, "SubFireDepYN", "Y");

		//PAS-341 Start
		RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), "NBA", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "DISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), "DISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "DISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION.getLabel(), "DISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT.getLabel(), "MISC", "NS");
		//PAS-341 End

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private static void rfiTagCheck(DocGenEnum.Documents document, String query, String tag, String tagValue) {
		CustomAssert.assertEquals(
				tag + "has a problem.", DocGenHelper.getDocumentDataElemByName(tag, document, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField(), tagValue);
	}

	private void uploadDocuments(String policyNumber) {
		LocalDateTime uploadDate = DateTimeUtils.getCurrentDateTime();
		String formattedDate = uploadDate.format(DateTimeUtils.MM_DD_YYYY);
		DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), policyNumber));
		DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), policyNumber));
		DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), policyNumber));

		String currentVersion = DBService.get().getValue(String.format(HelperRfi.GET_POLICY_SUMMARY_FIELD, "version", policyNumber)).get();
		String latestPolicySummaryId = DBService.get().getValue(String.format(HelperRfi.GET_POLICY_SUMMARY_FIELD, "id", policyNumber)).get();
		DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_POLICY_VERSION, String.valueOf(Integer.valueOf(currentVersion) + 1), latestPolicySummaryId));

		RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), "NBA", "PS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "DISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), "DISC", "PS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "DISC", "PS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS.getLabel(), "MISC", "NS");
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT.getLabel(), "MISC", "NS");
	}

	private void endorseRateDocuments() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
	}
}
