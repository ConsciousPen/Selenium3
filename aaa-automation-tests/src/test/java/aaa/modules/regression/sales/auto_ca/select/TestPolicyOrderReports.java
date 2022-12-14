/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * <b> Test order reports </b>
 * <p> Steps:
 *
 */
public class TestPolicyOrderReports extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyOrderReports(@Optional("CA") String state) {

		TestData class_td = getTestSpecificTD("TestData");
		ActivityInformationMultiAssetList aiAssetList = new DriverTab().getActivityInformationAssetList();
		
		mainApp().open();

		createCustomerIndividual();

		log.info("Order reports test started...");

		policy.initiate();
		policy.getDefaultView().fillUpTo(class_td, DriverActivityReportsTab.class, true);
			
		
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1).getCell("Response")).hasValue("processing complete, with results information");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("Response")).hasValue("Hit - Activity Found");
		

		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		//workaround for PAS-10786
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		 assertThat(DriverTab.tableActivityInformationList).hasRows(5);
		
		//check 1 incident
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE)).hasValue("Accident");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("5000");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACCIDENT_POINTS)).hasValue("0");
		
		//check 2 incident
		DriverTab.tableActivityInformationList.selectRow(2);
		
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE)).hasValue("Accident");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("649");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACCIDENT_POINTS)).hasValue("0");
		
		//check 3 incident
		DriverTab.tableActivityInformationList.selectRow(3);
		
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE)).hasValue("Accident");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("419");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACCIDENT_POINTS)).hasValue("0");
		
		//check 4 incident
		DriverTab.tableActivityInformationList.selectRow(4);
		
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE)).hasValue("Accident");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("650");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACCIDENT_POINTS)).hasValue("0");
		
		DriverTab.tableActivityInformationList.selectRow(5);
			
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("MVR");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.TYPE)).hasValue("Minor Violation");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.DESCRIPTION)).hasValue("Speeding");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE)).hasValue("09/10/2009");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.SVC_DESCRIPTION)).hasValue("SPEEDING, GENERALLY");
		assertThat(aiAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.CONVICTION_POINTS)).hasValue("1");
		
		
	}

}
