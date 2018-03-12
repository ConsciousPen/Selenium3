/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceBeforeAssetList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

/**
 * @author Jelena Dembovska
 * @name Test order reports
 * @scenario

 * @details
 */
public class TestPolicyOrderReports extends AutoSSBaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testPolicyOrderReports(@Optional("") String state) {

		TestData classTd = getTestSpecificTD("TestData");
		MultiInstanceBeforeAssetList aiAssetList = new DriverTab().getActivityInformationAssetList();

		mainApp().open();

		createCustomerIndividual();

		log.info("Order reports test started...");

		policy.initiate();
		policy.getDefaultView().fillUpTo(classTd, DriverActivityReportsTab.class, true);

		DriverActivityReportsTab driverActivityReportTab = new DriverActivityReportsTab();
		if (driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).isPresent()) {
			driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			PremiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
			driverActivityReportTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
		}

		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1).getCell("Response")).hasValue("processing complete, with results information");

		if (!getState().equals(Constants.States.OK)) {
			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("Response")).hasValue("Hit - Activity Found");
		}

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		//workaround for PAS-10786
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		if (!getState().equals(Constants.States.OK)) {
			DriverTab.tableActivityInformationList.verify.rowsCount(5);
		}

		if (!getState().equals(Constants.States.OK)) {
			//check MVR incident
			DriverTab.tableActivityInformationList.getRow("Source", "MVR").getCell(DriverTab.tableActivityInformationList.getColumnsCount()).controls.links.get("View/Edit").click();

			assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("MVR");
			assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE)).hasValue("Alcohol-Related Violation");
			assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION)).hasValue("Driving Under the Influence of Alcohol");
			assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE)).hasValue("09/10/2010");
			assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.SVC_DESCRIPTION)).hasValue("DUI, GENERALLY");
			assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.VIOLATION_POINTS)).hasValue("0");
		}

		//CLUE
		DriverTab.tableActivityInformationList.filterBy("Source", "CLUE");

		//check 1 CLUE incident
		DriverTab.tableActivityInformationList.selectRow(1);

		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE)).hasValue("At-Fault Accident");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION)).hasValue("Accident (Property Damage Only)");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE)).hasValue("09/10/2010");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("10000");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_POINTS)).hasValue("0");

		//check 2 CLUE incident
		DriverTab.tableActivityInformationList.selectRow(2);

		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ACTIVITY_SOURCE)).hasValue("CLUE");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE)).hasValue("At-Fault Accident");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION)).hasValue("Accident (Property Damage Only)");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE)).hasValue("09/11/2010");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT)).hasValue("1298");
		assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_POINTS)).hasValue("0");

		//add incident manually
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.ADD_ACTIVITY).click();
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.TYPE).setValue("At-Fault Accident");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION).setValue("Accident (Property Damage Only)");
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE).setValue(DateTimeUtils.getCurrentDateTime().minusDays(30).format(DateTimeUtils.MM_DD_YYYY));
		aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT).setValue("1700");

		//030-144-19CT Driver can not be charged for the first Property Damage Claim > $1000
		//030-144-15NY Points are applied for Property Damage accidents with amount > 2001
		if (!(getState().equals(Constants.States.NY) || getState().equals(Constants.States.CT))) {
			assertThat(aiAssetList.getAsset(AutoSSMetaData.DriverTab.ActivityInformation.CLAIM_POINTS)).hasValue("6");
		}
	}
}
