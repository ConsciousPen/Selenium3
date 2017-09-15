package aaa.modules.regression.sales.auto_ss;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.*;
import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.*;

public class TestQuoteCustomerSearch extends AutoSSBaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteCustomerSearch(String state) {

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
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.STATE).setValue(getState());
		insuredSearchDialog.clear();
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.STATE).verify.value("");

		//Verify that if no search criteria were entered, No search results will be returned, error message will be displayed
		insuredSearchDialog.search();
		insuredSearchDialog.labelErrorMessage.verify.value("At least 3 search criteria must be supplied.");

		//Verify that error message is displayed if result exceed the display limit
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.FIRST_NAME).setValue("Lisa");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.LAST_NAME).setValue("Adams");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.DATE_OF_BIRTH).setValue("05/06/1970");
		insuredSearchDialog.search();
		insuredSearchDialog.labelErrorMessage.verify.value("Returned results exceed the display limit; please refine your search criteria");

		//Verify case if multiple customers are returned in search result
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.FIRST_NAME).setValue("Roland");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.LAST_NAME).setValue("Lee");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.DATE_OF_BIRTH).clear();
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.STATE).setValue("CA");
		insuredSearchDialog.search();
		insuredSearchDialog.tableSearchResults.verify.rowsCount(4);

		//Validation of the case if Search returns no results
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.FIRST_NAME).setValue("John");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.LAST_NAME).setValue("Bamboo");
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.POSTAL_CODE).setValue("85207"); //input incorrect value for zip code
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.STATE).setValue("");
		insuredSearchDialog.search();
		insuredSearchDialog.tableSearchResults.verify.rowsCount(0);
		insuredSearchDialog.labelErrorMessage.verify.value("No Results");

		//Validation that Search returns correct result (One customer)
		insuredSearchDialog.getAsset(DialogsMetaData.DialogSearch.POSTAL_CODE).setValue("85206");
		insuredSearchDialog.search();
		insuredSearchDialog.tableSearchResults.verify.rowsCount(1);
		insuredSearchDialog.labelErrorMessage.verify.present(false);

		insuredSearchDialog.tableSearchResults.getRow(1).getCell("Customer Name").controls.links.getFirst().click();

		//Return to "general" tab, just added customer should be opened for editing, validate address (5210 East Hampton), //PAS13 ER Fix: As per defect 42775
		generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.FIRST_NAME).verify.value("John");
		generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.LAST_NAME).verify.value("Bamboo");
		generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.ADDRESS_LINE_1).verify.value("5210 East Hampton");

		//Fill all mandatory fields and go to the Drivers Tab
		generalTab.fillTab(getTestSpecificTD("GeneralTabData"));
		generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS).setValue("No");
		generalTab.submitTab();

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
		driverTab.getAssetList().getAsset(FIRST_NAME).verify.value("John");
		driverTab.getAssetList().getAsset(LAST_NAME).verify.value("Bamboo");

		//Fill all mandatory info and go ahead
		driverTab.fillTab(getTestSpecificTD("DriverTabData"));
		driverTab.submitTab();
		new RatingDetailReportsTab().fillTab(td);
		RatingDetailReportsTab.buttonSaveAndExit.click();

		//Validation that 2nd NI and Driver is added and displayed on QuoteSummaryScreen
		PolicySummaryPage.tableInsuredInformation.verify.rowsCount(2);
		PolicySummaryPage.tableInsuredInformation.getRow(2).getCell("Name").verify.value("John I Bamboo");
		PolicySummaryPage.tablePolicyDrivers.verify.rowsCount(2);
		PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Name").verify.value("John I Bamboo");

		log.info("QuoteCustomerSearch test is passed for auto_ss. Quote #" + PolicySummaryPage.labelPolicyNumber.getValue() + "is created");
	}
}
