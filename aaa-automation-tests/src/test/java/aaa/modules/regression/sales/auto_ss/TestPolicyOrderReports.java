/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jelena Dembovska
 * @name Test order reports
 * @scenario

 * @details
 */
public class TestPolicyOrderReports extends AutoSSBaseTest {

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyOrderReports(@Optional("") String state) {

		TestData class_td = getTestSpecificTD("TestData");
		MultiInstanceBeforeAssetList aiAssetList = new DriverTab().getActivityInformationAssetList();
		
		mainApp().open();

		createCustomerIndividual();

		log.info("Order reports test started...");

		policy.initiate();
		policy.getDefaultView().fillUpTo(class_td, DriverActivityReportsTab.class, true);
		
		DriverActivityReportsTab driverActivityReportTab = new DriverActivityReportsTab();
		if (driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).isPresent()) {
			driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			PremiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
			driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
		}

		
		DriverActivityReportsTab.tableCLUEReports.getRow(1).getCell("Response").verify.value("processing complete, with results information");
		
		if (!getState().equals("OK")){
		 DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("Response").verify.value("Hit - Activity Found");
		}
		
		
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		if (!getState().equals("OK")){
		  DriverTab.tableActivityInformationList.verify.rowsCount(5);
		}
	
		if (!getState().equals("OK")){
			//check MVR incident
			DriverTab.tableActivityInformationList.getRow("Source", "MVR").getCell(DriverTab.tableActivityInformationList.getColumnsCount()).controls.links.get("View/Edit").click();
			
			aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).verify.value("MVR");
			aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).verify.value("Alcohol-Related Violation");
			aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).verify.value("Driving Under the Influence of Alcohol");
			aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).verify.value("09/10/2010");
			aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.SVC_DESCRIPTION).verify.value("DUI, GENERALLY");
			aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.VIOLATION_POINTS).verify.value("0");
		}
		
		//CLUE
		Map<String, String> CLUE = new HashMap<>(); 
		CLUE.put("Source", "CLUE"); 
		
		DriverTab.tableActivityInformationList.getRows(CLUE);
		
		//check 1 CLUE incident
		DriverTab.tableActivityInformationList.selectRow(1);
		
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).verify.value("CLUE");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).verify.value("At-Fault Accident");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).verify.value("Accident (Property Damage Only)");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).verify.value("09/10/2010");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).verify.value("10000");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_POINTS).verify.value("0");
		
		//check 2 CLUE incident
		DriverTab.tableActivityInformationList.selectRow(2);
		
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE).verify.value("CLUE");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).verify.value("At-Fault Accident");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).verify.value("Accident (Property Damage Only)");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).verify.value("09/11/2010");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).verify.value("1298");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_POINTS).verify.value("0");
		
	
		//add incident manually
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("At-Fault Accident");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Accident (Property Damage Only)");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue(DateTimeUtils.getCurrentDateTime().minusDays(30).format(DateTimeUtils.MM_DD_YYYY));
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).setValue("1600");
		
		//030-144-19CT Driver can not be charged for the first Property Damage Claim > $1000
		//030-144-15NY Points are applied for Property Damage accidents with amount > 2001
		if (!(getState().equals("NY") || getState().equals("CT"))){
		   aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_POINTS).verify.value("6");
		}
		
	}

}
