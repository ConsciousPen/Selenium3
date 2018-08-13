/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.AutoSSMetaData.DriverTab;
import aaa.main.metadata.policy.AutoSSMetaData.VehicleTab;
import aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.NamedInsuredInformation;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

/**
 * @author Jelena Dembovska
 * @name Test Prefill tab
 * @scenario
 * @details
 */
public class TestQuotePrefill extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
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
		
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst().getValue()).isEqualTo(true);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst().getValue()).isEqualTo(true);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("First Name").getValue()).isEqualTo(expectedFN);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Last Name").getValue()).isEqualTo(expectedLN);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Date of Birth").getValue()).isEqualTo(expectedBirthDay);

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

	}

	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
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

		assertThat(PrefillTab.tableVehicles.getRow(1).getCell(1).controls.checkBoxes.getFirst().getValue()).isEqualTo(false);
		assertThat(PrefillTab.tableVehicles.getRow(2).getCell(1).controls.checkBoxes.getFirst().getValue()).isEqualTo(false);
		assertThat(PrefillTab.tableVehicles.getRow(1).getCell("VIN").getValue()).isEqualTo(VIN_1);
		assertThat(PrefillTab.tableVehicles.getRow(2).getCell("VIN").getValue()).isEqualTo(VIN_2);

		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst().getValue()).isEqualTo(true);
		assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst().getValue()).isEqualTo(true);
		
		assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst().getValue()).isEqualTo(false);
		assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst().getValue()).isEqualTo(false);

		PrefillTab.tableVehicles.getRow(1).getCell(1).controls.checkBoxes.getFirst().setValue(true);
		PrefillTab.tableVehicles.getRow(2).getCell(1).controls.checkBoxes.getFirst().setValue(true);
		PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst().setValue(true);
		assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst().getValue()).isEqualTo(true);

		prefillTab.submitTab();

		//check GeneralTab
		aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab();

		generalTab.verifyFieldHasValue(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel(), expectedNI_1);

		assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab.tableInsuredList.getRowsCount()).isEqualTo(2);
		
		//fill General tab
		generalTab.fillTab(getTestSpecificTD("TestData_Fill_Insured1").resolveLinks());
		generalTab.viewInsured(2);
		generalTab.fillTab(getTestSpecificTD("TestData_Fill_Insured2").resolveLinks());
		
		
		//check Driver tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab();

		driverTab.verifyFieldHasValue(DriverTab.NAMED_INSURED.getLabel(), expectedNI_1);
		driverTab.fillTab(getTestSpecificTD("TestData_Fill_Insured1").resolveLinks());

		aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab.tableDriverList.selectRow(2);
		driverTab.verifyFieldHasValue(DriverTab.NAMED_INSURED.getLabel(), expectedNI_2);
		driverTab.fillTab(getTestSpecificTD("TestData_Fill_Insured2").resolveLinks());
		driverTab.submitTab();
		
		aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab ratingDetailReportsTab = new aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab();
		ratingDetailReportsTab.fillTab(getTestSpecificTD("TestData_Fill_Insured1").resolveLinks());
		
		//check Vehicle tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab vehicleTab = new aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab();
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN.getLabel(), VIN_1);
		vehicleTab.getAssetList().getAsset(VehicleTab.USAGE).setValue("Pleasure");
		
		aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.verifyFieldHasValue(AutoSSMetaData.VehicleTab.VIN.getLabel(), VIN_2);
		vehicleTab.getAssetList().getAsset(VehicleTab.USAGE).setValue("Pleasure");
		vehicleTab.submitTab();
		
		
		//Tab.buttonSaveAndExit.click();
		policy.getDefaultView().fill(getTestSpecificTD("TestData_Bind"));
		
		
        assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);




	}

}
