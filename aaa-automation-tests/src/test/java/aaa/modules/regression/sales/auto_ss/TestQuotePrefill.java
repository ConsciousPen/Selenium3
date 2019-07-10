/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.AutoSSMetaData.DriverTab;
import aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.NamedInsuredInformation;
import aaa.main.metadata.policy.AutoSSMetaData.VehicleTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

/**
 * @author Jelena Dembovska
 * <b> Test Prefill tab </b>
 * <p> Steps:
 *
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

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(true);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("First Name")).hasValue(expectedFN);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Last Name")).hasValue(expectedLN);
			softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Date of Birth")).hasValue(expectedBirthDay);
		});

		prefillTab.submitTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			//check GeneralTab
			GeneralTab generalTab = new GeneralTab();

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
		});

		prefillTab.submitTab();

		//check GeneralTab
		GeneralTab generalTab = new GeneralTab();

		assertThat(generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.FIRST_NAMED_INSURED)).hasValue(expectedNI_1);

		assertThat(GeneralTab.tableInsuredList).hasRows(2);

		//fill General tab
		generalTab.fillTab(getTestSpecificTD("TestData_Fill_Insured1").resolveLinks());
		generalTab.viewInsured(2);
		generalTab.fillTab(getTestSpecificTD("TestData_Fill_Insured2").resolveLinks());


		//check Driver tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab();

		assertThat(driverTab.getAssetList().getAsset(DriverTab.NAMED_INSURED)).hasValue(expectedNI_1);
		driverTab.fillTab(getTestSpecificTD("TestData_Fill_Insured1").resolveLinks());

		aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab.tableDriverList.selectRow(2);
		assertThat(driverTab.getAssetList().getAsset(DriverTab.NAMED_INSURED)).hasValue(expectedNI_2);
		driverTab.fillTab(getTestSpecificTD("TestData_Fill_Insured2").resolveLinks());
		driverTab.submitTab();

		RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
		ratingDetailReportsTab.fillTab(getTestSpecificTD("TestData_Fill_Insured1").resolveLinks());

		//check Vehicle tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

		aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab vehicleTab = new aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab();
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN)).hasValue(VIN_1);
		vehicleTab.getAssetList().getAsset(VehicleTab.USAGE).setValue("Pleasure");

		aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab.tableVehicleList.selectRow(2);
		assertThat(vehicleTab.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN)).hasValue(VIN_2);
		vehicleTab.getAssetList().getAsset(VehicleTab.USAGE).setValue("Pleasure");
		vehicleTab.submitTab();

		//Tab.buttonSaveAndExit.click();
		policy.getDefaultView().fill(getTestSpecificTD("TestData_Bind"));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

}
