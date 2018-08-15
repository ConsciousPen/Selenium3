/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoCaMetaData.DriverTab;
import aaa.main.metadata.policy.AutoCaMetaData.GeneralTab.NamedInsuredInformation;
import aaa.main.modules.policy.auto_ca.defaulttabs.PrefillTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static aaa.main.metadata.policy.AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION;
import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Jelena Dembovska
 * @name Test Prefill tab
 * @scenario
 * @details
 */
public class TestQuotePrefill extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyPrefill1(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();

		log.info("Prefill test Started...");

		policy.initiate();

		TestData td = getTestSpecificTD("TestData1");

		//get expected values from test data
		String expectedNI = td.getTestData("VerificationData").getValue(AutoCaMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel());
		String expectedFN = td.getTestData("VerificationData").getValue(NamedInsuredInformation.FIRST_NAME.getLabel());
		String expectedLN = td.getTestData("VerificationData").getValue(NamedInsuredInformation.LAST_NAME.getLabel());
		String expectedZip = td.getTestData("VerificationData").getValue(NamedInsuredInformation.ZIP_CODE.getLabel());
		String expectedAddress = td.getTestData("VerificationData").getValue(NamedInsuredInformation.ADDRESS_LINE_1.getLabel());
		String expectedBirthDay = td.getTestData("VerificationData").getValue(DriverTab.DATE_OF_BIRTH.getLabel());

		//order prefill and get response
		PrefillTab prefillTab = new PrefillTab();

		prefillTab.fillTab(td);
		
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(true);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("First Name")).hasValue(expectedFN);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Last Name")).hasValue(expectedLN);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Date of Birth")).hasValue(expectedBirthDay);

		assertThat(PrefillTab.tableVehicles).hasRows(0);

		prefillTab.submitTab();

		//check GeneralTab
		aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab();

		MultiInstanceAfterAssetList namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();
		assertThat(generalTab.getNamedInsuredInfoAssetList().getAsset(NamedInsuredInformation.FIRST_NAME)).hasValue(expectedFN);
		assertThat(generalTab.getNamedInsuredInfoAssetList().getAsset(NamedInsuredInformation.LAST_NAME)).hasValue(expectedLN);
		assertThat(generalTab.getNamedInsuredInfoAssetList().getAsset(NamedInsuredInformation.ZIP_CODE)).hasValue(expectedZip);
		assertThat(generalTab.getNamedInsuredInfoAssetList().getAsset(NamedInsuredInformation.ADDRESS_LINE_1)).hasValue(expectedAddress);
		assertThat(generalTab.getAssetList().getAsset(AutoCaMetaData.GeneralTab.FIRST_NAMED_INSURED)).hasValue(expectedNI);

		//check Driver tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab();

		assertThat(driverTab.getAssetList().getAsset(DriverTab.NAMED_INSURED)).hasValue(expectedNI);
		assertThat(driverTab.getAssetList().getAsset(DriverTab.FIRST_NAME)).hasValue(expectedFN);
		assertThat(driverTab.getAssetList().getAsset(DriverTab.LAST_NAME)).hasValue(expectedLN);
		assertThat(driverTab.getAssetList().getAsset(DriverTab.DATE_OF_BIRTH)).hasValue(expectedBirthDay);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		
		policy.getDefaultView().fill(getTestSpecificTD("TestDataFill_1_CA"));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);


	}

	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyPrefill2(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();

		log.info("Prefill test Started...");

		policy.initiate();

		TestData td = getTestSpecificTD("TestData2");

		//get expected values from test data
		String expectedNI_1 = td.getTestDataList("VerificationData").get(0).getValue(AutoCaMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel());
		String VIN_1 = td.getTestDataList("VerificationData").get(0).getValue(AutoCaMetaData.VehicleTab.VIN.getLabel());

		String expectedNI_2 = td.getTestDataList("VerificationData").get(1).getValue(AutoCaMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel());
		String VIN_2 = td.getTestDataList("VerificationData").get(1).getValue(AutoCaMetaData.VehicleTab.VIN.getLabel());

		//order prefill and get response
		PrefillTab prefillTab = new PrefillTab();

		prefillTab.fillTab(td);

		assertThat(PrefillTab.tableVehicles.getRow(1).getCell(1).controls.checkBoxes.getFirst()).hasValue(false);
		assertThat(PrefillTab.tableVehicles.getRow(2).getCell(1).controls.checkBoxes.getFirst()).hasValue(false);
		assertThat(PrefillTab.tableVehicles.getRow(1).getCell("VIN")).hasValue(VIN_1);
		assertThat(PrefillTab.tableVehicles.getRow(2).getCell("VIN")).hasValue(VIN_2);

		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(true);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);
		
		assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(false);
		assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(false);


		PrefillTab.tableVehicles.getRow(1).getCell(1).controls.checkBoxes.getFirst().setValue(true);
		PrefillTab.tableVehicles.getRow(2).getCell(1).controls.checkBoxes.getFirst().setValue(true);
		PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst().setValue(true);
		assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);

		prefillTab.submitTab();

		//check GeneralTab
		aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab();
		assertThat(generalTab.getAssetList().getAsset(AutoCaMetaData.GeneralTab.FIRST_NAMED_INSURED)).hasValue(expectedNI_1);
		assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab.tableInsuredList).hasRows(2);
		generalTab.fillTab(getTestSpecificTD("TestDataFill_1_CA"));
		
		generalTab.viewInsured(2);
		generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS).setValue("No");


		//check Driver tab
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
		aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab();
		assertThat(driverTab.getAssetList().getAsset(DriverTab.NAMED_INSURED)).hasValue(expectedNI_1);
		aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab.tableDriverList.selectRow(2);
		assertThat(driverTab.getAssetList().getAsset(DriverTab.NAMED_INSURED)).hasValue(expectedNI_2);

		aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab.viewDriver(1);
		driverTab.fillTab(getTestSpecificTD("DriverTab1"));
		
		aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab.viewDriver(2);
		driverTab.fillTab(getTestSpecificTD("DriverTab2"));
		driverTab.submitTab();
		
		aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab membershipTab = new aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab();
		membershipTab.fillTab(getTestSpecificTD("MembershipTab"));
		membershipTab.submitTab();
		
		//check Vehicle tab
		aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab vehicleTab = new aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab();
		
		VehicleTab.tableVehicleList.selectRow(1);
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN)).hasValue(VIN_1);
		
		VehicleTab.tableVehicleList.selectRow(2);
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN)).hasValue(VIN_2);
		vehicleTab.fillTab(getTestSpecificTD("VehicleTab"));
		vehicleTab.submitTab();
		
		
		policy.getDefaultView().fill(getTestSpecificTD("TestDataFill_2_CA"));
		
		
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);


	}

}
