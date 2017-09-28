/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.enums.SearchEnum;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Jelena Dembovska
 * @name Test policy spin action
 * @scenario
 * 0. Create customer and auto SS policy with 2NI/2Drivers/2Vehicles
 * 1. initiate spin action
 * 2. open activities section, check spin has been executed, store spun quote number 
 * A. Steps for checking pended endorsement for current policy
 *    3. Open pended endorsement, fill all mandatory fields to bind policy
 *    4. Check policy is bind and now has 1NI/1Driver/1Vehicle
 * B. Steps to checking new quote which has been spun from initial policy
 *    5. Search spun quote
 *    6. Open quote for data gather and fill all mandatory fields required to bind and purchase
 *    7. Check policy is bind and now has 1NI/1Driver/1Vehicle
 * @details
 */
public class TestPolicySpin extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicySpin(@Optional("CA") String state) {


    	new TestPolicyCreationBig().testPolicyCreationBig(state);
    	
		//Read and store zip code from UI, will need it to fill values for spun quote
		//String zip_code = PolicySummaryPage.tablePolicyVehicles.getRow(1).getCell("Garaging Zip").getValue();

		CustomAssert.enableSoftMode();

		//1. initiate spin action
		policy.policySpin().perform(getTestSpecificTD("SpinTestData"));

		//as a result on Spin action two items are created: 
		//A) pended endorsement for current policy
		//B) new quote with one driver and one vehicle which has been spun from current policy 
		//mainApp().open();
		//SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, "CAAS950542807");
		//2. open activities section, check spin has been executed, store spun quote number 
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String description = NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "has been spun to a new quote").getCell("Description").getValue();
		CustomAssert.assertTrue("Spin action is missing in Activities and User Notes", description.contains("has been spun to a new quote"));
		String quoteNumber = description.substring(description.indexOf("QCAAS"));

		//A. Steps for checking pended endorsement for current policy
		//3. Open pended endorsement, fill all mandatory fields to bind policy
		PolicySummaryPage.buttonPendedEndorsement.click();
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Action").controls.comboBoxes.getFirst().setValue("Data Gathering");
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Action").controls.buttons.getFirst().click();

		policy.dataGather().getView().fill(getTestSpecificTD("TestData_endorsement"));

		//4. Check policy is bind and now has 1NI/1Driver/1Vehicle
		PolicySummaryPage.tablePolicyDrivers.verify.rowsCount(1);
		PolicySummaryPage.tablePolicyVehicles.verify.rowsCount(1);
		PolicySummaryPage.tableInsuredInformation.verify.rowsCount(1);

		//B. Steps to checking new quote which has been spun from initial policy
		//5. Search spun quote
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		//SearchPage.search(SearchFor.QUOTE, SearchBy.POLICY_QUOTE, "QUTSS927278692");

		//modify zip code corresponding to state
		TestData quoteTestData = getTestSpecificTD("TestData");

		//TestData namedInsured = quoteTestData.getTestData("GeneralTab").getTestDataList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()).get(0)
		//		.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE.getLabel()), zip_code);

		//List<TestData> namedInsuredList = new ArrayList<>();
		//namedInsuredList.add(namedInsured);

		//6. Open quote for data gather and fill all mandatory fields required to bind and purchase
		policy.dataGather().start();
		policy.getDefaultView().fill(quoteTestData);

		//7. Check policy is bind and now has 1NI/1Driver/1Vehicle
		PolicySummaryPage.tablePolicyDrivers.verify.rowsCount(1);
		PolicySummaryPage.tablePolicyVehicles.verify.rowsCount(1);
		PolicySummaryPage.tableInsuredInformation.verify.rowsCount(1);

		CustomAssert.assertAll();
	}

}

