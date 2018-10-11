/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
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
import aaa.helpers.db.DbAwaitHelper;
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
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-349", "PAS-341", "PAS-20333"})
	public void pas349_rfiHO3_1(@Optional("AZ") String state) {
		String today = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		TestData adjustedTd = rfiTestData(state, "TestData1", "1939");

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(adjustedTd, DocumentsTab.class, true);

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
		assertThat(documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION)).hasValue("Not Signed");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES)).hasValue("No");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM)).hasValue("No");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM)).hasValue("No");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS)).hasValue("No");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT)).hasValue("No");
		documentsTab.saveAndExit();

		CustomSoftAssertions.assertSoftly(softly -> {
			String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

			//BUG PAS-10779 HSRFIXX is not generated on Issue anymore
			policy.quoteDocGen().start();
			goddTab.generateDocuments(DocGenEnum.Documents.HSRFIXX);

			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "HSRFIXX", "ADHOC_DOC_ON_DEMAND_GENERATE");
			assertThat(DbAwaitHelper.waitForQueryResult(query, 10)).isTrue();//XML doesn't appear in DB at once
			DocGenHelper.getDocumentDataSectionsByName("FormData", DocGenEnum.Documents.HSRFIXX, query).get(0).getDocumentDataElements();
			rfiTagCheck(HSRFIXX, query, "AtBndFlg", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "OldHoModrnDiscYN", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "GrnHoDiscYN", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "RtCntrlAlrmForThft", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "RtCntrlAlrmForFire", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "SubFireDepYN", "Y", softly);

			//PAS-341 Start
			RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), "1POSOA", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "8POGHA", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), "1POCFA", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "2POCTA", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS.getLabel(), "9POPRR", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT.getLabel(), "12FDEPT", "NS", softly);
			//PAS-341 End

			uploadDocuments(policyNumber, softly);

			SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
			endorseRateDocuments();
			softly.assertThat(documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION)).hasValue("Pending Review (Uploaded " + today + ")");
			softly.assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM)).hasValue("Pending Review (Uploaded " + today + ")");
			softly.assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM))
					.hasValue("Pending Review (Uploaded " + today + ")");

			//check Upload Pending is present after value is changed
			documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION).setValue("Physically Signed");
			documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM).setValue("Yes");
			NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
			bindTab.submitTab();

			endorseRateDocuments();
			softly.assertThat(documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION)).hasValue("Physically Signed");
			softly.assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM))
					.hasValue("Pending Review (Uploaded " + today + ")");
			softly.assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM)).hasValue("Yes");
		});
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-349", "PAS-341", "PAS-20333"})
	public void pas349_rfiHO3_2(@Optional("VA") String state) {
		TestData adjustedTd = rfiTestData(state, "TestData2", "1941");

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(adjustedTd, DocumentsTab.class, true);

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
		assertThat(documentsTab.getRequiredToIssueAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION)).hasValue("Not Signed");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES)).hasValue("No");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM)).hasValue("No");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM)).hasValue("No");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION)).hasValue("No");
		assertThat(documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT)).hasValue("No");
		documentsTab.saveAndExit();

		CustomSoftAssertions.assertSoftly(softly -> {
			String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

			//BUG PAS-10779 /HSRFIXX is not generated on Issue anymore
			policy.quoteDocGen().start();
			goddTab.generateDocuments(DocGenEnum.Documents.HSRFIXX);

			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "HSRFIXX", "ADHOC_DOC_ON_DEMAND_GENERATE");
			assertThat(DbAwaitHelper.waitForQueryResult(query, 10)).isTrue();//XML doesn't appear in DB at once
			DocGenHelper.getDocumentDataSectionsByName("FormData", DocGenEnum.Documents.HSRFIXX, query).get(0).getDocumentDataElements();
			rfiTagCheck(HSRFIXX, query, "AtBndFlg", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "NewHoModrnDiscYN", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "GrnHoDiscYN", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "RtCntrlAlrmForThft", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "RtCntrlAlrmForFire", "Y", softly);
			rfiTagCheck(HSRFIXX, query, "SubFireDepYN", "Y", softly);

			//PAS-341 Start
			RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), "1POSOA", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "8POGHA", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), "1POCFA", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "2POCTA", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION.getLabel(), "10PORHM", "NS", softly);
			HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT.getLabel(), "12FDEPT", "NS", softly);
			//PAS-341 End
		});
	}

	private TestData rfiTestData(String state, String testSpecificTestDataName, String year) {
		String yearBuilt =
				TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), HomeSSMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel());
		String hailResistanceRating =
				TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING
						.getLabel());
		TestData adjustedTd;
		if ("CO, IN, KS, KY, OH, OK, SD, WV".contains(state)) {
			 adjustedTd = getPolicyTD().adjust(getTestSpecificTD(testSpecificTestDataName).adjust(yearBuilt, year).adjust(hailResistanceRating, "index=1").resolveLinks()).resolveLinks();
		} else {
			 adjustedTd = getPolicyTD().adjust(getTestSpecificTD(testSpecificTestDataName).adjust(yearBuilt, year).resolveLinks()).resolveLinks();
		}
		return adjustedTd;
	}

	private static void rfiTagCheck(DocGenEnum.Documents document, String query, String tag, String tagValue, ETCSCoreSoftAssertions softly) {
		softly.assertThat(DocGenHelper.getDocumentDataElemByName(tag, document, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField())
				.as(tag + "has a problem.").isEqualTo(tagValue);
	}

	private void uploadDocuments(String policyNumber, ETCSCoreSoftAssertions softly) {
		LocalDateTime uploadDate = DateTimeUtils.getCurrentDateTime();
		String formattedDate = uploadDate.format(DateTimeUtils.MM_DD_YYYY);
		DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), policyNumber));
		DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), policyNumber));
		DBService.get()
				.executeUpdate(String.format(HelperRfi.UPDATE_DOCUMENT_STATUS, formattedDate, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), policyNumber));

		String currentVersion = DBService.get().getValue(String.format(HelperRfi.GET_POLICY_SUMMARY_FIELD, "version", policyNumber)).get();
		String latestPolicySummaryId = DBService.get().getValue(String.format(HelperRfi.GET_POLICY_SUMMARY_FIELD, "id", policyNumber)).get();
		DBService.get().executeUpdate(String.format(HelperRfi.UPDATE_POLICY_VERSION, String.valueOf(Integer.valueOf(currentVersion) + 1), latestPolicySummaryId));

		RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToIssue.SIGNED_POLICY_APPLICATION.getLabel(), "1POSOA", "PS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "8POGHA", "NS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), "1POCFA", "PS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "2POCTA", "PS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_PLUMBING_AND_OTHER_RENOVATIONS.getLabel(), "9POPRR", "NS", softly);
		HelperRfi.policyServiceRfiValuesCheck(result, HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_SUBSCRIPTION_TO_FIRE_DEPARTMENT.getLabel(), "12FDEPT", "NS", softly);
	}

	private void endorseRateDocuments() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
	}
}
