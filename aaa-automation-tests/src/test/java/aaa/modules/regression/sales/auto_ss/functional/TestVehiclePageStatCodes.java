/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Chris Johns
 * <p>
 * PAS-6166
 * @name Verify STAT Code Symbol Pre-Ffx is Removed from STAT Code Selections on vehicle page
 * @scenario Review the STAT Code Drop Down Values on the vehicle page and verify the two letter symbol prefix is removed from all available choices
 * 1. Create a new Customer, initiate a new ss auto quote, Fill up to the vehicle page
 * 2. Enter in a VIN that will not return a VIN match
 * 3. Enter 'Other' in the Make field to get the STAT Code dropdown to appear on the page
 * 4. Compare the expected dropdown values with the actual values; values should not contain the STAT Code prefix values.
 * 5. Bind the quote and then initiate an endorsement on the resulting policy.
 * 6. Navigate to vehicle page and verify
 * @details
 */

public class TestVehiclePageStatCodes extends AutoSSBaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.LOW})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6166")
	public void pas6166_vehPageStatCodes(@Optional("") String state) {

		//Adjust default test data to use a junk vin on the vehicle tab (junk vin will not have vin match) and enter other vehicle info fields
		TestData testData = getPolicyTD();

		//Create new customer
		mainApp().open();
		createCustomerIndividual();

		//Initiate a new ss auto quote, enter all data until the Vehicle Tab, Use Junk Vin Mentioned in above test data
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, VehicleTab.class, true);

		//Create a new list of Current Stat Codes available for selection on the Vehicle Page
		List<String> availStatCodes = new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.STAT_CODE).getAllValues();

		//Create a lit of Values that should be available in the STAT Code dropdown
		List<String> correctStatSymbols = Arrays.asList("",
				"Small SUV",
				"Midsize High Exposure Vehicle",
				"Large SUV",
				"Midsize car",
				"Large High Exposure Vehicle",
				"Midsize SUV",
				"Small car",
				"Standard pickup or Utility Truck",
				"Large car",
				"Small pickup or Utility Truck",
				"Passenger Van",
				"Small High Exposure Vehicle",
				"Crossover/Station Wagon");

		//Create a lit of Values that should NOT be available in the STAT Code dropdown
		List<String> oldStatSymbols = Arrays.asList(
				"AC - Small SUV",
				"AD - Midsize High Exposure Vehicle",
				"AE - Large SUV",
				"AI - Midsize car",
				"AJ - Large High Exposure Vehicle",
				"AK - Midsize SUV",
				"AN - Small car",
				"AO - Standard pickup or Utility Truck",
				"AQ - Large car",
				"AR - Small pickup or Utility Truck",
				"AX - Passenger Van",
				"AY - Small High Exposure Vehicle",
				"AZ - Crossover/Station Wagon"
		);

		//Verify the stat symbols were removed from the list of stat codes by comparing the Expected List of Values with the Actual Dropdown Values
		assertThat(availStatCodes).doesNotContainAnyElementsOf(oldStatSymbols);
		assertThat(availStatCodes).containsAll(correctStatSymbols);
		log.info("SUCCESS: No STAT Code Symbols Present in STAT Code Dropdown during quote creation!");

		//continue to bind: fill data until the
		policy.getDefaultView().fillFromTo(testData, VehicleTab.class, DocumentsAndBindTab.class, true);
		//Save and Exit so that the simplifiedQuoteCreation method can be called - it starts from the consolidated page
		//NOTE: Refactor may be needed to include 'simplifiedQuoteIssue' into the base tests//I steal it's use here as to not re-invent the wheel
		new DocumentsAndBindTab().saveAndExit();
		new TestEValueDiscount().simplifiedQuoteIssue();

		//Endorse the policy
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		//Verify the stat symbols were removed from the list of stat codes by comparing the Expected List of Values with the Actual Dropdown Values
		assertThat(availStatCodes).doesNotContainAnyElementsOf(oldStatSymbols);
		assertThat(availStatCodes).containsAll(correctStatSymbols);
		log.info("SUCCESS: No STAT Code Symbols Present in STAT Code Dropdown during endorsement!");

	}
}
