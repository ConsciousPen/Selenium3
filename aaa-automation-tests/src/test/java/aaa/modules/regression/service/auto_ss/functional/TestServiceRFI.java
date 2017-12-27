/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.RfiDocumentResponse;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestServiceRFI extends AutoSSBaseTest {

	private final InquiryAssetList inquiryAssetList = new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), AutoSSMetaData.GeneralTab.class);
	private final ErrorTab errorTab = new ErrorTab();
	private final GeneralTab generalTab = new GeneralTab();
	private final DriverTab driverTab = new DriverTab();
	private final DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final PurchaseTab purchaseTab = new PurchaseTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	private static void rfiDocumentContentCheck(String policyNum) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AARFIXX", "POLICY_ISSUE");
		DocGenHelper.getDocumentDataSectionsByName("CoverageDetails", DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements();

		rfiTagCheck(query, "PrevInsDiscYN", "Y");
		rfiTagCheck(query, "GoodStuDiscYN", "Y");
		rfiTagCheck(query, "VehNwAddPrtcYN", "Y");
		rfiTagCheck(query, "CurInsDrvrYN", "Y");
		rfiTagCheck(query, "SmrtDrvrCrseCertYN", "Y");
		rfiTagCheck(query, "VehNwAddPrtcPrevCrirYN", "Y");
		rfiTagCheck(query, "SalvVehYN", "Y");
		rfiTagCheck(query, "PsnlAutoApplYN", "Y");
		rfiTagCheck(query, "CanMVRYN", "Y");
		//TODO UBITrmCndtnYN is N, but the RFI contains it. Kinda illogical
		rfiTagCheck(query, "UBITrmCndtnYN", "N");
	}

	private static void rfiDocumentContentCheckNano(String policyNum) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AARFIXX", "POLICY_ISSUE");
		DocGenHelper.getDocumentDataSectionsByName("CoverageDetails", DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements();
		rfiTagCheck(query, "PsnlAutoApplYN", "Y");
		//Non-Owner Automobile Endorsement tag
		rfiTagCheck(query, "VehTyp", "NO");
	}

	private static void rfiTagCheck(String query, String tag, String tagValue) {
		CustomAssert.assertEquals(
				tag + "has a problem.", DocGenHelper.getDocumentDataElemByName(tag, DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice()
						.getTextField(), tagValue);
	}

	private void createQuoteWithCustomDataNano(String state) {
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testDataManager.getDefault(TestPolicyNano.class).getTestData("TestData_" + state), DocumentsAndBindTab.class, false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).verify.value("Not Signed");
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT).verify.value("Not Signed");
		documentsAndBindTab.saveAndExit();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name RFI
	 * @scenario
	 * 1.Initiate quote creation.
	 * Insured1
	 * Proof of Prior Insurance (including original inception date of policy and prior BI limits)	Prior BI overridden by agent
	 *
	 * Driver1
	 * Proof of Good Student
	 *
	 * Driver2
	 * DL - Foreign
	 * Smart Driver Course Completed?
	 *
	 * Driver3 - Not Available for Rating, insured with other carrier
	 * Proof of Current Insurance for all "Not Available for Rating" drivers
	 *
	 * Driver4 - Not Available for Rating
	 *
	 *
	 * Vehicle1 -
	 * Photos showing all 4 sides of salvaged vehicles	select salvaged
	 * Proof of purchase date (bill of sale) for new vehicle(s) - less than 30 days
	 *
	 * Vehicle2 -
	 * Proof of equivalent new car added protection coverage with prior carrier for new vehicle(s)	new car added protection; date is more than 30 days ago
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-349", "PAS-341"})
	public void pas349_rfiAuto(@Optional("VA") String state) {
		createQuoteWithCustomData();

		CustomAssert.enableSoftMode();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
		rfiDocumentContentCheck(policyNumber);

		//PAS-341 Start
		RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_CURRENT_INSURANCE_FOR.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER.getLabel());
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE.getLabel());
		//PAS-341 End

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name RFI
	 * @scenario
	 * 1.Initiate quote creation.
	 * Auto Insurance Application
	 * Non-Owner Automobile Endorsement
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-349", "PAS-341"})
	public void pas349_rfiNano(@Optional("NV") String state) {
		createQuoteWithCustomDataNano(state);

		//CustomAssert.enableSoftMode();
		//TODO Question to Karen: IN has Uninsured Property Damage = No COverage, disabled, but required to have at least 25k
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
		rfiDocumentContentCheckNano(policyNumber);

		//PAS-341 Start
		RfiDocumentResponse[] result = HelperCommon.executeRequestRfi(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel());
		//TODO Question to Karen: NV returns "Non-Owner Automobile Endorsement Form" instead of "Non-Owner Automobile Endorsement"
		policyServiceRfiStatusCheck(result, AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT.getLabel());
		//PAS-341 End

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void createQuoteWithCustomData() {
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData"), DocumentsAndBindTab.class, false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION).verify.value("Not Signed");
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS).verify.value("Not Signed");

		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_CURRENT_INSURANCE_FOR).verify.value("No");
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT).verify.value("No");
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION).verify.value("No");
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE).verify.value("No");
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PURCHASE_DATE_BILL_OF_SALE_FOR_NEW_VEHICLES).verify.value("No");
		documentsAndBindTab.getRequiredToIssueAssetList()
				.getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES).verify.value("No");
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER).verify.value("No");
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PHOTOS_FOR_SALVATAGE_VEHICLE_WITH_PHYSICAL_DAMAGE_COVERAGE).verify.value("No");

		documentsAndBindTab.saveAndExit();
	}

	private void policyServiceRfiStatusCheck(RfiDocumentResponse[] result, String rfiName) {
		RfiDocumentResponse allDocuments = Arrays.stream(result).filter(doc -> rfiName.equals(doc.documentName)).findFirst().orElse(null);
		CustomAssert.assertTrue(allDocuments != null);
		CustomAssert.assertTrue("NS".equals(allDocuments.status));
	}

}
