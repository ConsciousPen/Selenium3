package aaa.modules.regression.sales.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import aaa.utils.StateList;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.*;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.*;

public class TestQuoteCustomerSearch extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(states = { States.AZ, States.UT })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteCustomerSearch(@Optional("") String state) {

		TestData td = getPolicyTD();
		GeneralTab generalTab = new GeneralTab();
		DriverTab driverTab = new DriverTab();
		SingleSelectSearchDialog insuredSearchDialog = generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.INSURED_SEARCH_DIALOG);

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, GeneralTab.class, true);

		//Open Search Customer Dialog
		generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.ADD_INSURED).click();

		//Verify that clear button deletes inserted values
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.FIRST_NAME).setValue("Test name");
		insuredSearchDialog.clear();
		assertThat(insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.FIRST_NAME)).hasValue("");

		//Verify that if no search criteria were entered, No search results will be returned, error message will be displayed
		insuredSearchDialog.search();
		assertThat(insuredSearchDialog.labelErrorMessage).hasValue("At least 3 search criteria must be supplied.");

		//Verify that error message is displayed if result exceed the display limit
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.FIRST_NAME).setValue("Lisa");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.LAST_NAME).setValue("Adams");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.DATE_OF_BIRTH).setValue("05/06/1970");
		insuredSearchDialog.search();
		assertThat(insuredSearchDialog.labelErrorMessage).hasValue("Returned results exceed the display limit; please refine your search criteria");

		//Verify case if multiple customers are returned in search result
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.FIRST_NAME).setValue("Roland");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.LAST_NAME).setValue("Lee");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.DATE_OF_BIRTH).clear();
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.STATE).setValue("CA");
		insuredSearchDialog.search();
		assertThat(insuredSearchDialog.tableSearchResults).hasRows(4);

		//Validation of the case if Search returns no results
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.FIRST_NAME).setValue("John");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.LAST_NAME).setValue("Bamboo");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.POSTAL_CODE).setValue("85207"); //input incorrect value for zip code
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.STATE).setValue("");
		insuredSearchDialog.search();
		assertThat(insuredSearchDialog.tableSearchResults).hasRows(0);
		assertThat(insuredSearchDialog.labelErrorMessage).hasValue("No Results");

		//Validation that Search returns correct result (One customer)
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.POSTAL_CODE).setValue("85206");
		insuredSearchDialog.search();
		assertThat(insuredSearchDialog.tableSearchResults).hasRows(1);
		assertThat(insuredSearchDialog.labelErrorMessage).isPresent(false);

		insuredSearchDialog.tableSearchResults.getRow(1).getCell("Customer Name").controls.links.getFirst().click();

		//Return to "general" tab, just added customer should be opened for editing, validate address (5210 East Hampton), //PAS13 ER Fix: As per defect 42775
		assertThat(generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.FIRST_NAME)).hasValue("John");
		assertThat(generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.LAST_NAME)).hasValue("Bamboo");
		assertThat(generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.ADDRESS_LINE_1)).hasValue("5210 East Hampton");

		//Fill all mandatory fields and go to the Drivers Tab
		generalTab.fillTab(getTestSpecificTD("GeneralTabData"));
		generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS).setValue("No");
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		//Fill all mandatory fields for Drivers Tab
		driverTab.fillTab(td);
		//Add new Driver and verify the search results
		driverTab.getAssetList().getAsset(ADD_DRIVER).click();
		driverTab.getAssetList().getAsset(DRIVER_SEARCH_DIALOG).getAsset(DialogsMetaData.DialogSearch.FIRST_NAME).setValue("John");
		driverTab.getAssetList().getAsset(DRIVER_SEARCH_DIALOG).getAsset(DialogsMetaData.DialogSearch.LAST_NAME).setValue("Bamboo");
		driverTab.getAssetList().getAsset(DRIVER_SEARCH_DIALOG).getAsset(DialogsMetaData.DialogSearch.POSTAL_CODE).setValue("85206");
		driverTab.getAssetList().getAsset(DRIVER_SEARCH_DIALOG).search();
		driverTab.getAssetList().getAsset(DRIVER_SEARCH_DIALOG).tableSearchResults.getRow(1).getCell("Customer Name").controls.links.getFirst().click();

		//Verify that Driver's info is filled with found driver's details
		assertThat(driverTab.getAssetList().getAsset(FIRST_NAME)).hasValue("John");
		assertThat(driverTab.getAssetList().getAsset(LAST_NAME)).hasValue("Bamboo");

		//Fill all mandatory info and go ahead
		driverTab.fillTab(getTestSpecificTD("DriverTabData"));
		driverTab.submitTab();
		new RatingDetailReportsTab().fillTab(td);
		RatingDetailReportsTab.buttonSaveAndExit.click();

		//Validation that 2nd NI and Driver is added and displayed on QuoteSummaryScreen
		assertThat(PolicySummaryPage.tableInsuredInformation).hasRows(2);
		assertThat(PolicySummaryPage.tableInsuredInformation.getRow(2).getCell("Name")).hasValue("John I Bamboo");
		assertThat(PolicySummaryPage.tablePolicyDrivers).hasRows(2);
		assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Name")).hasValue("John I Bamboo");

		log.info("QuoteCustomerSearch test is passed for auto_ss. Quote #" + PolicySummaryPage.labelPolicyNumber.getValue() + "is created");
	}
}
