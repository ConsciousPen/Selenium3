package aaa.modules.common;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.SearchEnum;
import aaa.common.metadata.SearchMetaData;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.EntitiesHolder;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.AbstractEditableStringElement;

public class TestSearchPageFull extends AutoSSBaseTest {
	private Map<String, TestData> fullSearchData = new HashMap<>(2);

	@Test
	@TestInfo(component = "Common.Search")
	//TODO-dchubkov: test javadoc
	public void searchForAccountByFullTestData() {
		//TestData td = getFullSearchData();
		TestData td = getFullSearchData(SearchEnum.SearchFor.ACCOUNT);
		String expectedAccountNumber = td.getTestData(SearchPage.assetListSearch.getName()).getValue(SearchMetaData.Search.ACCOUNT.getLabel());

		mainApp().open();

		CustomAssert.enableSoftMode();
		//Search for Account by each search by criteria
		for (SearchEnum.SearchBy searchBy : SearchEnum.SearchBy.values()) {
			String searchForValue = td.getTestData(SearchPage.assetListSearch.getName()).getValue(searchBy.get());
			if (searchForValue == null) {
				continue;
			}

			SearchPage.search(SearchEnum.SearchFor.ACCOUNT, searchBy, searchForValue);
			if (searchBy.equals(SearchEnum.SearchBy.PRODUCT_ID) || searchBy.equals(SearchEnum.SearchBy.AGENT_OF_RECORD) || searchBy.equals(SearchEnum.SearchBy.AGENT)) {
				SearchPage.tableSearchResults.getRow(SearchMetaData.Search.ACCOUNT.getLabel(), expectedAccountNumber).verify.present(false);
				CustomerSummaryPage.labelAccountNumber.verify.present(false);
				SearchPage.verifyWarningsExist("Result set too large, refine search criteria");
				SearchPage.assetListSearch.getAsset(searchBy.get(), AbstractEditableStringElement.class).setValue("");
			} else {
				SearchPage.tableSearchResults.getRow(SearchMetaData.Search.ACCOUNT.getLabel(), expectedAccountNumber).verify.present();
				SearchPage.tableSearchResults.getRow(SearchMetaData.Search.ACCOUNT.getLabel(), expectedAccountNumber).getCell(SearchMetaData.Search.ACCOUNT.getLabel()).controls.links.getFirst().click();
				CustomerSummaryPage.labelAccountNumber.verify.value(expectedAccountNumber);
			}
		}

		//Search for Account by all search by criteria simultaneously
		SearchPage.search(td);
		SearchPage.tableSearchResults.getRow(SearchMetaData.Search.ACCOUNT.getLabel(), expectedAccountNumber).verify.present();
		SearchPage.tableSearchResults.getRow(SearchMetaData.Search.ACCOUNT.getLabel(), expectedAccountNumber).getCell(SearchMetaData.Search.ACCOUNT.getLabel()).controls.links.getFirst().click();
		CustomerSummaryPage.labelAccountNumber.verify.value(expectedAccountNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test
	@TestInfo(component = "Common.Search")
	//TODO-dchubkov: test javadoc
	public void searchForCustomerByFullTestData() {
		//TestData td = getFullSearchData();
		TestData td = getFullSearchData(SearchEnum.SearchFor.CUSTOMER);
		String expectedCustomerNumber = td.getTestData(SearchPage.assetListSearch.getName()).getValue(SearchMetaData.Search.CUSTOMER.getLabel());

		mainApp().open();

		CustomAssert.enableSoftMode();
		//Search for Customer by each search by criteria
		for (SearchEnum.SearchBy searchBy : SearchEnum.SearchBy.values()) {
			String searchForValue = td.getTestData(SearchPage.assetListSearch.getName()).getValue(searchBy.get());
			if (searchForValue == null) {
				continue;
			}

			SearchPage.search(SearchEnum.SearchFor.CUSTOMER, searchBy, searchForValue);
			if (searchBy.equals(SearchEnum.SearchBy.PRODUCT_ID) || searchBy.equals(SearchEnum.SearchBy.AGENT_OF_RECORD) || searchBy.equals(SearchEnum.SearchBy.AGENT)) {
				SearchPage.tableSearchResults.getRow(SearchMetaData.Search.CUSTOMER.getLabel(), expectedCustomerNumber).verify.present(false);
				CustomerSummaryPage.labelCustomerNumber.verify.present(false);
				SearchPage.verifyWarningsExist("Result set too large, refine search criteria");
				SearchPage.assetListSearch.getAsset(searchBy.get(), AbstractEditableStringElement.class).setValue("");
			} else {
				CustomAssert.assertTrue(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get()));
				CustomerSummaryPage.labelAccountNumber.verify.value(expectedCustomerNumber);
			}
		}

		//Search for Customer by all search by criteria simultaneously
		SearchPage.search(td);
		CustomAssert.assertTrue(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get()));
		CustomerSummaryPage.labelCustomerNumber.verify.value(expectedCustomerNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	//....

	@Test
	@TestInfo(component = "Common.Search")
	//TODO-dchubkov: test javadoc
	public void searchForQuoteByFullTestData() {
		TestData td = getFullSearchData(SearchEnum.SearchFor.QUOTE);
		String expectedQuoteNumber = td.getTestData(SearchPage.assetListSearch.getName()).getValue(SearchMetaData.Search.POLICY_QUOTE.getLabel());

		mainApp().open();

		CustomAssert.enableSoftMode();
		//Search for Quote by each search by criteria
		for (SearchEnum.SearchBy searchBy : SearchEnum.SearchBy.values()) {
			String searchForValue = td.getTestData(SearchPage.assetListSearch.getName()).getValue(searchBy.get());
			if (searchForValue == null) {
				continue;
			}

			SearchPage.search(SearchEnum.SearchFor.QUOTE, searchBy, searchForValue);
			if (searchBy.equals(SearchEnum.SearchBy.AGENT_OF_RECORD) || searchBy.equals(SearchEnum.SearchBy.AGENT)) {
				SearchPage.tableSearchResults.getRow(SearchMetaData.Search.POLICY_QUOTE.getLabel(), expectedQuoteNumber).verify.present(false);
				CustomerSummaryPage.labelCustomerNumber.verify.present(false);
				SearchPage.verifyWarningsExist("Result set too large, refine search criteria");
				SearchPage.assetListSearch.getAsset(searchBy.get(), AbstractEditableStringElement.class).setValue("");
			} else if (searchBy.equals(SearchEnum.SearchBy.PRODUCT_ID)) {
				if (SearchPage.tableSearchResults.isPresent()) {
					SearchPage.tableSearchResults.getRow("Quote #", expectedQuoteNumber).getCell(1).controls.links.getFirst().click();
					PolicySummaryPage.labelPolicyNumber.verify.value(expectedQuoteNumber);
				} /*else {
					SearchPage.verifyWarningsExist("Result set too large, refine search criteria");
					SearchPage.assetListSearch.getAsset(searchBy.get(), AbstractEditableStringElement.class).setValue("");
				}*/
			} else {
				CustomAssert.assertTrue(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.QUOTE.get()));
				PolicySummaryPage.labelPolicyNumber.verify.value(expectedQuoteNumber);
			}
		}

		//Search for Quote by all search by criteria simultaneously
		SearchPage.search(td);
		CustomAssert.assertTrue(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.QUOTE.get()));
		CustomerSummaryPage.labelCustomerNumber.verify.value(expectedQuoteNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private synchronized TestData getFullSearchData(SearchEnum.SearchFor searchFor) {
		final String keyPattern = searchFor.equals(SearchEnum.SearchFor.QUOTE) ? "_QuoteFullDataSearchTest" : "_OtherFullDataSearchTest";
		String customerKey = EntitiesHolder.makeCustomerKey(CustomerType.INDIVIDUAL, getState()) + keyPattern;
		String policyKey = EntitiesHolder.makeDefaultPolicyKey(getPolicyType(), getState()) + keyPattern;

		if (!EntitiesHolder.isEntityPresent(customerKey) || !EntitiesHolder.isEntityPresent(policyKey)) {
			mainApp().open();
			String customerNumber = createCustomerIndividual(tdSpecific.getTestData("CustomerCreation"));
			EntitiesHolder.addNewEntity(customerKey, customerNumber);
			String policyNumber = searchFor.equals(SearchEnum.SearchFor.QUOTE) ? createQuote() : createPolicy();
			EntitiesHolder.addNewEntity(policyKey, policyNumber);
		}

		if (!fullSearchData.containsKey(policyKey)) {
			TestData customerGeneralTabData = tdSpecific.getTestData("CustomerCreation").getTestData(customer.getDefaultView().getTab(GeneralTab.class).getClass().getSimpleName());
			//TestData agencyData0 = customerGeneralTabData.getTestDataList(CustomerMetaData.GeneralTab.AGENCY_ASSIGNMENT.getLabel()).get(0);

			TestData searchData = DataProviderFactory.dataOf(
					SearchMetaData.Search.POLICY_QUOTE.getLabel(), EntitiesHolder.getEntity(policyKey),
					SearchMetaData.Search.PRODUCT_ID.getLabel(), "Auto",
					SearchMetaData.Search.CUSTOMER.getLabel(), EntitiesHolder.getEntity(customerKey),
					SearchMetaData.Search.AGENT_OF_RECORD.getLabel(), "House Agent SMTestIA",
					SearchMetaData.Search.AGENT.getLabel(), "400018581",
					SearchMetaData.Search.CITY.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.CITY.getLabel()),
					SearchMetaData.Search.STATE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.STATE.getLabel()),
					SearchMetaData.Search.ZIP_CODE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()),
					SearchMetaData.Search.PHONE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.PHONE_NUMBER.getLabel()),
					//BUG: Search by valid "Agency Name" or "Agency #" fails with error "base00003 -  Operation has failed due to illegal arguments".
					//TODO-dchubkov: create a defect for this
					/*SearchMetaData.Search.AGENCY_NAME.getLabel(), agencyData0.getValue(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_NAME.getLabel()),
					SearchMetaData.Search.AGENCY.getLabel(), agencyData0.getValue(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_NAME.getLabel()),*/
					//TODO-dchubkov: add data for "Underwriting Company #"
					SearchMetaData.Search.SSN.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.SSN.getLabel()));

			//TODO-dchubkov: get values from DB instead of search in UI?
			mainApp().open();
			SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.POLICY_QUOTE, EntitiesHolder.getEntity(policyKey));
			searchData.adjust(SearchMetaData.Search.FIRST_NAME.getLabel(), CustomerSummaryPage.labelCustomerName.getValue().split("\\s")[0]);
			searchData.adjust(SearchMetaData.Search.LAST_NAME.getLabel(), CustomerSummaryPage.labelCustomerName.getValue().split("\\s")[1]);
			searchData.adjust(SearchMetaData.Search.BILLING_ACCOUNT.getLabel(), CustomerSummaryPage.BillingSection.getTable().getRow(1).getCell("Billing Account").getValue());
			NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.ACCOUNT.get());
			searchData.adjust(SearchMetaData.Search.ACCOUNT.getLabel(), CustomerSummaryPage.labelAccountNumber.getValue());

			fullSearchData.put(policyKey, searchData);
		}

		//CustomAssert.assertTrue(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get()));
		//PolicySummaryPage.labelPolicyNumber.verify.value(policyNumber);

		/*TestData td = fullSearchData.get(policyKey).getTestData(SearchPage.assetListSearch.getName());
		TestData returnData = DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.SEARCH_FOR.getLabel()), searchFor.get()));*/

		return DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), fullSearchData.get(policyKey).adjust(SearchMetaData.Search.SEARCH_FOR.getLabel(), searchFor.get()));
	}
}

