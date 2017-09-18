/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test order reports
 * @scenario

 * @details
 */
public class TestPolicyOrderReports extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyOrderReports(@Optional("CA") String state) {

		TestData class_td = getTestSpecificTD("TestData");
		MultiInstanceBeforeAssetList aiAssetList = new DriverTab().getActivityInformationAssetList();
		
		mainApp().open();

		createCustomerIndividual();

		log.info("Order reports test started...");

		policy.initiate();
		policy.getDefaultView().fillUpTo(class_td, DriverActivityReportsTab.class, true);
			
		
		DriverActivityReportsTab.tableCLUEReports.getRow(1).getCell("Response").verify.value("processing complete, with results information");
		DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("Response").verify.value("Hit - Activity Found");
		

		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		 DriverTab.tableActivityInformationList.verify.rowsCount(5);
		
		//check 1 incident
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).verify.value("CLUE");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE).verify.value("Accident");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).verify.value("5000");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACCIDENT_POINTS).verify.value("0");
		
		//check 2 incident
		DriverTab.tableActivityInformationList.selectRow(2);
		
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).verify.value("CLUE");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE).verify.value("Accident");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).verify.value("649");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACCIDENT_POINTS).verify.value("0");
		
		//check 3 incident
		DriverTab.tableActivityInformationList.selectRow(3);
		
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).verify.value("CLUE");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE).verify.value("Accident");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).verify.value("419");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACCIDENT_POINTS).verify.value("0");
		
		//check 4 incident
		DriverTab.tableActivityInformationList.selectRow(4);
		
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).verify.value("CLUE");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE).verify.value("Accident");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).verify.value("650");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACCIDENT_POINTS).verify.value("0");
		
		DriverTab.tableActivityInformationList.selectRow(5);
			
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).verify.value("MVR");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE).verify.value("Minor Violation");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.DESCRIPTION).verify.value("Speeding");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).verify.value("09/10/2009");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.SVC_DESCRIPTION).verify.value("SPEEDING, GENERALLY");
		aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.CONVICTION_POINTS).verify.value("1");
		
		
	}

}
