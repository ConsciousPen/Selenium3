/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.enums.SearchEnum;
import aaa.main.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
public class TestPolicySpin extends AutoSSBaseTest {

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicySpin(@Optional("") String state) {


    	new TestPolicyCreationBig().testPolicyCreationBig(state);
    	
		//Read and store zip code from UI, will need it to fill values for spun quote
		String zip_code = PolicySummaryPage.tablePolicyVehicles.getRow(1).getCell("Garaging Zip").getValue();

		//1. initiate spin action
		policy.policySpin().perform(getTestSpecificTD("SpinTestData"));

		//as a result on Spin action two items are created: 
		//A) pended endorsement for current policy
		//B) new quote with one driver and one vehicle which has been spun from current policy 

		//2. open activities section, check spin has been executed, store spun quote number 
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		String description = NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", "has been spun to a new quote").getCell("Description").getValue();		
		
		assertSoftly(softly -> {
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(ActivitiesAndUserNotesTable.DESCRIPTION,
					"has been spun to a new quote").isPresent());
		});
		
		
		String quoteNumber = description.substring(description.indexOf("Q" + getState() + "SS"));

		//A. Steps for checking pended endorsement for current policy
		//3. Open pended endorsement, fill all mandatory fields to bind policy
		PolicySummaryPage.buttonPendedEndorsement.click();
		PolicySummaryPage.tableEndorsements.getRow(1).getCell(8).controls.buttons.getFirst().click();

		policy.dataGather().getView().fill(getTestSpecificTD("TestData_endorsement"));

		//4. Check policy is bind and now has 1NI/1Driver/1Vehicle
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PolicySummaryPage.tablePolicyDrivers.getRowsCount()).isEqualTo(1);
			softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRowsCount()).isEqualTo(1);
			softly.assertThat(PolicySummaryPage.tableInsuredInformation.getRowsCount()).isEqualTo(1);
			
		});	

		//B. Steps to checking new quote which has been spun from initial policy
		//5. Search spun quote
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		//SearchPage.search(SearchFor.QUOTE, SearchBy.POLICY_QUOTE, "QUTSS927278692");

		//modify zip code corresponding to state
		TestData quoteTestData = getTestSpecificTD("TestData");

		TestData namedInsured = quoteTestData.getTestData("GeneralTab").getTestDataList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()).get(0)
				.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE.getLabel()), zip_code);

		List<TestData> namedInsuredList = new ArrayList<>();
		namedInsuredList.add(namedInsured);

		//6. Open quote for data gather and fill all mandatory fields required to bind and purchase
		policy.dataGather().start();
		policy.getDefaultView().fill(
				quoteTestData.adjust(TestData.makeKeyPath("GeneralTab", AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()), namedInsuredList));

		//7. Check policy is bind and now has 1NI/1Driver/1Vehicle
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PolicySummaryPage.tablePolicyDrivers.getRowsCount()).isEqualTo(1);
			softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRowsCount()).isEqualTo(1);
			softly.assertThat(PolicySummaryPage.tableInsuredInformation.getRowsCount()).isEqualTo(1);
			
		});	

	}

}

