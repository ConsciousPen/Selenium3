/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestRfiNano extends AutoSSBaseTest {

	private final InquiryAssetList inquiryAssetList = new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), AutoSSMetaData.GeneralTab.class);
	private final ErrorTab errorTab = new ErrorTab();
	private final GeneralTab generalTab = new GeneralTab();
	private final DriverTab driverTab = new DriverTab();
	private final DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final PurchaseTab purchaseTab = new PurchaseTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	/**
	 * @author Oleg Stasyuk
	 * @name RFI
	 * @scenario
	 * 1.Initiate quote creation.
	 * Insured1
	 * Proof of Prior Insurance (including original inception date of policy and prior BI limits)	Prior BI overridden by agent
	 *
	 *
	 * Driver1
	 * Proof of Good Student
	 *
	 * Driver2
	 * DL - Foreign
	 * Smart Driver Course Completed?
	 *
	 * Driver3 - Not Available for Rating, insured with other carrier
	 *    Proof of Current Insurance for all "Not Available for Rating" drivers
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-349")
	public void pas349_rfiNano(@Optional("VA") String state) {
		createQuoteWithCustomData();

		CustomAssert.enableSoftMode();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();
		rfiDocumentContentCheck(policyNumber);

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
		documentsAndBindTab.saveAndExit();
	}

	private static void rfiDocumentContentCheck(String policyNum) {
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNum, "AARFIXX", "POLICY_ISSUE");
		DocGenHelper.getDocumentDataSectionsByName("CoverageDetails", DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements();
		rfiTagCheck(query, "PsnlAutoApplYN");
	}

	private static void rfiTagCheck(String query, String tag) {
		CustomAssert.assertEquals(tag + "has a problem.", DocGenHelper.getDocumentDataElemByName(tag, DocGenEnum.Documents.AARFIXX, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField(), "Y");
	}
}
