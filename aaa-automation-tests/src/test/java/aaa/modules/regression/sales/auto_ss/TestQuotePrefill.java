/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
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
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

/**
 * @author Jelena Dembovska
 * @name Test Prefill tab
 * @scenario
 * @details
 */
public class TestQuotePrefill extends AutoSSBaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyPrefill1(@Optional("") String state) {

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

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(true);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("First Name")).hasValue(expectedFN);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Last Name")).hasValue(expectedLN);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Date of Birth")).hasValue(expectedBirthDay);

			if (!getState().equals("IN")) //additional vehicle is returned from stub for IN
				softly.assertThat(PrefillTab.tableVehicles).as("No vehicles should be returned from stub").hasRows(0);

			prefillTab.submitTab();

			//check GeneralTab
			aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab();

			MultiAssetList namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();
			softly.assertThat(namedInsuredInfo.getAsset(NamedInsuredInformation.FIRST_NAME)).hasValue(expectedFN);
			softly.assertThat(namedInsuredInfo.getAsset(NamedInsuredInformation.LAST_NAME)).hasValue(expectedLN);
			softly.assertThat(namedInsuredInfo.getAsset(NamedInsuredInformation.ZIP_CODE)).hasValue(expectedZip);
			softly.assertThat(namedInsuredInfo.getAsset(NamedInsuredInformation.ADDRESS_LINE_1)).hasValue(expectedAddress);
			softly.assertThat(generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED)).hasValue(expectedNI);

			//check Driver tab
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

			aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab();
			softly.assertThat(driverTab.getAssetList().getAsset(DriverTab.NAMED_INSURED)).hasValue(expectedNI);
			softly.assertThat(driverTab.getAssetList().getAsset(DriverTab.FIRST_NAME)).hasValue(expectedFN);
			softly.assertThat(driverTab.getAssetList().getAsset(DriverTab.LAST_NAME)).hasValue(expectedLN);
			softly.assertThat(driverTab.getAssetList().getAsset(DriverTab.DATE_OF_BIRTH)).hasValue(expectedBirthDay);
			softly.assertThat(driverTab.getAssetList().getAsset(DriverTab.LICENSE_NUMBER)).hasValue(expectedLicense);

			Tab.buttonSaveAndExit.click();
		});
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyPrefill2(@Optional("") String state) {

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

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PrefillTab.tableVehicles.getRow(1).getCell(1).controls.checkBoxes.getFirst()).hasValue(false);
			softly.assertThat(PrefillTab.tableVehicles.getRow(2).getCell(1).controls.checkBoxes.getFirst()).hasValue(false);
			softly.assertThat(PrefillTab.tableVehicles.getRow(1).getCell("VIN")).hasValue(VIN_1);
			softly.assertThat(PrefillTab.tableVehicles.getRow(2).getCell("VIN")).hasValue(VIN_2);

			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(true);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);

			softly.assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(false);
			softly.assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(false);

			PrefillTab.tableVehicles.getRow(1).getCell(1).controls.checkBoxes.getFirst().setValue(true);
			PrefillTab.tableVehicles.getRow(2).getCell(1).controls.checkBoxes.getFirst().setValue(true);
			PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst().setValue(true);
			softly.assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);

			prefillTab.submitTab();

			//check GeneralTab
			GeneralTab generalTab = new GeneralTab();

			softly.assertThat(generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED)).hasValue(expectedNI_1);

			softly.assertThat(GeneralTab.tblInsuredList).hasRows(2);

			//check Driver tab
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

			aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab();

			softly.assertThat(driverTab.getAssetList().getAsset(DriverTab.NAMED_INSURED)).hasValue(expectedNI_1);

			aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab.tableDriverList.selectRow(2);
			softly.assertThat(driverTab.getAssetList().getAsset(DriverTab.NAMED_INSURED)).hasValue(expectedNI_2);

			//check Vehicle tab
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

			VehicleTab vehicleTab = new VehicleTab();
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN)).hasValue(VIN_1);

			VehicleTab.tableVehicleList.selectRow(2);
			softly.assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN)).hasValue(VIN_2);

			Tab.buttonSaveAndExit.click();
		});
	}

}
