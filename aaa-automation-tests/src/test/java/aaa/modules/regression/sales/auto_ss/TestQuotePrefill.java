/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.AutoSSMetaData.DriverTab;
import aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.NamedInsuredInformation;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

/**
 * @author Jelena Dembovska
 * @name Test Prefill tab
 * @scenario
 * @details
 */
public class TestQuotePrefill extends AutoSSBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyPrefill1() {

		mainApp().open();

		createCustomerIndividual();

		log.info("Prefill test Started...");

		policy.initiate();

		TestData td = getTestSpecificTD("TestData1");

		//get expected values from test data
		String expectedNI = td.getTestData("VerificationData").getValue(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel());
		String expectedFN = td.getTestData("VerificationData").getValue(NamedInsuredInformation.FIRST_NAME.getLabel());
		String expectedLN = td.getTestData("VerificationData").getValue(NamedInsuredInformation.LAST_NAME.getLabel());
		String expectedZip = td.getTestData("VerificationData").getValue(NamedInsuredInformation.ZIP_CODE.getLabel());
		String expectedAddress = td.getTestData("VerificationData").getValue(NamedInsuredInformation.ADDRESS_LINE_1.getLabel());
		String expectedBirthDay = td.getTestData("VerificationData").getValue(DriverTab.DATE_OF_BIRTH.getLabel());
		String expectedLicense = td.getTestData("VerificationData").getValue(DriverTab.LICENSE_NUMBER.getLabel());

		//order prefill and get response
		PrefillTab prefillTab = new PrefillTab();

		prefillTab.fillTab(td);

		CustomAssert.enableSoftMode();

		PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst().verify.value(true);
		PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst().verify.value(true);
		PrefillTab.tableDrivers.getRow(1).getCell("First Name").verify.value(expectedFN);
		PrefillTab.tableDrivers.getRow(1).getCell("Last Name").verify.value(expectedLN);
		PrefillTab.tableDrivers.getRow(1).getCell("Date of Birth").verify.value(expectedBirthDay);

		if (!getState().equals("IN")) //additional vehicle is returned from stub for IN
		CustomAssert.assertEquals("No vehicles should be returned from stub", 0, PrefillTab.tableVehicles.getRowsCount());

		prefillTab.submitTab();

		//check GeneralTab
		aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab();

		MultiAssetList namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();
		generalTab.verifyFieldHasValue(namedInsuredInfo, NamedInsuredInformation.FIRST_NAME.getLabel(), expectedFN);
		generalTab.verifyFieldHasValue(namedInsuredInfo, NamedInsuredInformation.LAST_NAME.getLabel(), expectedLN);
		generalTab.verifyFieldHasValue(namedInsuredInfo, NamedInsuredInformation.ZIP_CODE.getLabel(), expectedZip);
		generalTab.verifyFieldHasValue(namedInsuredInfo, NamedInsuredInformation.ADDRESS_LINE_1.getLabel(), expectedAddress);
		generalTab.verifyFieldHasValue(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel(), expectedNI);

		//check Driver tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab();

		driverTab.verifyFieldHasValue(DriverTab.NAMED_INSURED.getLabel(), expectedNI);
		driverTab.verifyFieldHasValue(DriverTab.FIRST_NAME.getLabel(), expectedFN);
		driverTab.verifyFieldHasValue(DriverTab.LAST_NAME.getLabel(), expectedLN);
		driverTab.verifyFieldHasValue(DriverTab.DATE_OF_BIRTH.getLabel(), expectedBirthDay);
		driverTab.verifyFieldHasValue(DriverTab.LICENSE_NUMBER.getLabel(), expectedLicense);

		Tab.buttonSaveAndExit.click();

		CustomAssert.assertAll();

	}

	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyPrefill2() {

		mainApp().open();

		createCustomerIndividual();

		log.info("Prefill test Started...");

		policy.initiate();

		TestData td = getTestSpecificTD("TestData2");

		//get expected values from test data
		String expectedNI_1 = td.getTestDataList("VerificationData").get(0).getValue(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel());
		String VIN_1 = td.getTestDataList("VerificationData").get(0).getValue(AutoSSMetaData.VehicleTab.VIN.getLabel());

		String expectedNI_2 = td.getTestDataList("VerificationData").get(1).getValue(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel());
		String VIN_2 = td.getTestDataList("VerificationData").get(1).getValue(AutoSSMetaData.VehicleTab.VIN.getLabel());

		//order prefill and get response
		PrefillTab prefillTab = new PrefillTab();

		prefillTab.fillTab(td);

		CustomAssert.enableSoftMode();

		PrefillTab.tableVehicles.getRow(1).getCell(1).controls.checkBoxes.getFirst().verify.value(false);
		PrefillTab.tableVehicles.getRow(2).getCell(1).controls.checkBoxes.getFirst().verify.value(false);
		PrefillTab.tableVehicles.getRow(1).getCell("VIN").verify.value(VIN_1);
		PrefillTab.tableVehicles.getRow(2).getCell("VIN").verify.value(VIN_2);

		PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst().verify.value(true);
		PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst().verify.value(true);

		PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst().verify.value(false);
		PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst().verify.value(false);

		PrefillTab.tableVehicles.getRow(1).getCell(1).controls.checkBoxes.getFirst().setValue(true);
		PrefillTab.tableVehicles.getRow(2).getCell(1).controls.checkBoxes.getFirst().setValue(true);
		PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst().setValue(true);
		PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst().verify.value(true);

		prefillTab.submitTab();

		//check GeneralTab
		aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab();

		generalTab.verifyFieldHasValue(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel(), expectedNI_1);

		CustomAssert.assertEquals(aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab.tblInsuredList.getRowsCount(), 2);

		//check Driver tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab();

		driverTab.verifyFieldHasValue(DriverTab.NAMED_INSURED.getLabel(), expectedNI_1);

		aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab.tableDriverList.selectRow(2);
		driverTab.verifyFieldHasValue(DriverTab.NAMED_INSURED.getLabel(), expectedNI_2);

		//check Vehicle tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab vehicleTab = new aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab();
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN.getLabel(), VIN_1);

		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN.getLabel(), VIN_2);

		Tab.buttonSaveAndExit.click();

		CustomAssert.assertAll();

	}

}
