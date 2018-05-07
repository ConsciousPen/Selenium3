/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestMiniServicesDiscounts extends AutoSSBaseTest {
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-9495"})
	public void pas9495_miniServicesDiscounts(@Optional("AZ") String state) {
		createQuoteWithCustomData(state);

		CustomAssert.enableSoftMode();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue("ACH");

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void createQuoteWithCustomData(String state) {
		TestData td = getTestSpecificTD("TestData").adjust(TestData.makeKeyPath("GeneralTab",
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), "$<today+9d:MM/dd/yyyy>");

		if(!state.equals("VA")){
			td.mask( "AssignmentTab")
			.mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(),AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT.getLabel()));
		}

		mainApp().open();
		createCustomerIndividual();
		//SearchPage.openCustomer("700032251");

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class, false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		documentsAndBindTab.saveAndExit();
	}

}
