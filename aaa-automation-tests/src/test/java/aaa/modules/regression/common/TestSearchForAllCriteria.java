package aaa.modules.regression.common;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.metadata.SearchMetaData;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.EntitiesHolder;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestSearchForAllCriteria extends AutoSSBaseTest {
	private Map<String, TestData> fullSearchData = new HashMap<>(2);

	//TODO-dchubkov: write test with search by all valid fields but for different entities
	//TODO-dchubkov: write test with search by wrong criteria
	//TODO-dchubkov: write test with search by first part values
	//TODO-dchubkov: write test for search table (e.g. click on link, check values in table, etc.)
	//TODO-dchubkov: write test for search for Quote by billing account from existent policy

	/**
	 * @author Dmitry Chubkov
	 * @name Search for Account by its number and by all other criteria simultaneously
	 *
	 * @scenario
	 * 1. Open search page and choose "Account" as "Search For" criteria
	 * 2. Fill existing "Account #" and press "Search" button
	 * 3. Verify that search is successful: Account page opens for searched account number
	 * 4. Open search page and choose "Account" as "Search For" criteria
	 * 5. Fill all search by fields with valid values of same existed policy/customer, press "Search" button
	 * 6. Verify that search is successful: Account page opens for searched criteria
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Common.SEARCH )
	public void testSearchForAccount(@Optional("") String state) {
		TestData td = getFullSearchData(SearchEnum.SearchFor.ACCOUNT);
		//BUG: Search by valid "Agency Name" or "Agency #" fails with error "base00003 -  Operation has failed due to illegal arguments".
		//TODO-dchubkov: create a defect for this
		td = DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), td.getTestData(SearchPage.assetListSearch.getName()).mask(SearchMetaData.Search.AGENCY_NAME.getLabel(), SearchMetaData.Search.AGENCY.getLabel()));
		String expectedAccountNumber = td.getTestData(SearchPage.assetListSearch.getName()).getValue(SearchMetaData.Search.ACCOUNT.getLabel());

		mainApp().open();
		CustomAssert.enableSoftMode();

		//Search for Account by its number
		SearchPage.search(SearchEnum.SearchFor.ACCOUNT, SearchEnum.SearchBy.ACCOUNT, expectedAccountNumber);
		//BUG: Search by valid "Account #" shows one result in table instead of straightly open it".
		//TODO-dchubkov: create a defect for this
		if (SearchPage.tableSearchResults.isPresent() && SearchPage.tableSearchResults.isVisible()) {
			SearchPage.tableSearchResults.getRow(SearchMetaData.Search.ACCOUNT.getLabel(), expectedAccountNumber).getCell(SearchMetaData.Search.ACCOUNT.getLabel()).controls.links.getFirst().click();
		}
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.CustomerSummaryTab.ACCOUNT.get())).isTrue();
		CustomerSummaryPage.labelAccountNumber.verify.value(expectedAccountNumber);

		//Search for Account by all search by criteria simultaneously
		SearchPage.search(td);
		if (SearchPage.tableSearchResults.isPresent() && SearchPage.tableSearchResults.isVisible()) {
			SearchPage.tableSearchResults.getRow(SearchMetaData.Search.ACCOUNT.getLabel(), expectedAccountNumber).getCell(SearchMetaData.Search.ACCOUNT.getLabel()).controls.links.getFirst().click();
		}
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.CustomerSummaryTab.ACCOUNT.get())).isTrue();
		CustomerSummaryPage.labelAccountNumber.verify.value(expectedAccountNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name Search for Customer by its number and by all other criteria simultaneously
	 *
	 * @scenario
	 * 1. Open search page and choose "Customer" as "Search For" criteria
	 * 2. Fill existing "Customer #" and press "Search" button
	 * 3. Verify that search is successful: Customer page opens for searched customer number
	 * 4. Open search page and choose "Customer" as "Search For" criteria
	 * 5. Fill all search by fields with valid values of same existed policy/customer, press "Search" button
	 * 6. Verify that search is successful: Customer page opens for searched criteria
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Common.SEARCH )
	public void testSearchForCustomer(@Optional("") String state) {
		TestData td = getFullSearchData(SearchEnum.SearchFor.CUSTOMER);
		//BUG: Search by valid "Phone #" fails with error "base00003 -  Operation has failed due to illegal arguments".
		//TODO-dchubkov: create a defect for this
		td = DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), td.getTestData(SearchPage.assetListSearch.getName()).mask(SearchMetaData.Search.PHONE.getLabel()));
		String expectedCustomerNumber = td.getTestData(SearchPage.assetListSearch.getName()).getValue(SearchMetaData.Search.CUSTOMER.getLabel());

		mainApp().open();
		CustomAssert.enableSoftMode();

		//Search for Customer by its number
		SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, expectedCustomerNumber);
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())).isTrue();
		CustomerSummaryPage.labelCustomerNumber.verify.value(expectedCustomerNumber);

		//Search for Customer by all search by criteria simultaneously
		SearchPage.search(td);
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())).isTrue();
		CustomerSummaryPage.labelCustomerNumber.verify.value(expectedCustomerNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name Search for Billing Account by its number and by all other criteria simultaneously
	 *
	 * @scenario
	 * 1. Open search page and choose "Billing" as "Search For" criteria
	 * 2. Fill existing "Billing Account #" and press "Search" button
	 * 3. Verify that search is successful: Billing page opens for searched billing account number
	 * 4. Open search page and choose "Billing" as "Search For" criteria
	 * 5. Fill all search by fields with valid values of same existed policy/customer, press "Search" button
	 * 6. Verify that search is successful: Billing page opens for searched criteria
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Common.SEARCH )
	public void testSearchForBillingAccount(@Optional("") String state) {
		TestData td = getFullSearchData(SearchEnum.SearchFor.BILLING);
		//BUG: Search result for Billing by valid "Agency Name" or "Agency #" is empty
		//TODO-dchubkov: create a defect for this
		td = DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), td.getTestData(SearchPage.assetListSearch.getName()).mask(SearchMetaData.Search.AGENCY_NAME.getLabel(), SearchMetaData.Search.AGENCY.getLabel()));
		String expectedBaNumber = td.getTestData(SearchPage.assetListSearch.getName()).getValue(SearchMetaData.Search.BILLING_ACCOUNT.getLabel());

		mainApp().open();
		CustomAssert.enableSoftMode();

		//Search for Billing Account by its number
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.BILLING_ACCOUNT, expectedBaNumber);
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.BILLING.get())).isTrue();
		assertThat(BillingSummaryPage.tableBillingGeneralInformation.getRow("ID", expectedBaNumber).getCell("ID")).hasValue(expectedBaNumber);

		//Search for Billing Account by all search by criteria simultaneously
		SearchPage.search(td);
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.BILLING.get())).isTrue();
		assertThat(BillingSummaryPage.tableBillingGeneralInformation.getRow("ID", expectedBaNumber).getCell("ID")).hasValue(expectedBaNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name Search for Policy by its number and by all other criteria simultaneously
	 *
	 * @scenario
	 * 1. Open search page and choose "Policy" as "Search For" criteria
	 * 2. Fill "Policy/Quote #" of existing policy and press "Search" button
	 * 3. Verify that search is successful: Policy page opens for searched policy number
	 * 4. Open search page and choose "Policy" as "Search For" criteria
	 * 5. Fill all search by fields with valid values of same existed policy/customer, press "Search" button
	 * 6. Verify that search is successful: Policy page opens for searched criteria
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Common.SEARCH )
	public void testSearchForPolicy(@Optional("") String state) {
		TestData td = getFullSearchData(SearchEnum.SearchFor.POLICY);
		//BUG: Search result for Policy by valid "Phone #" or "Agency Name" or "Agency #" is empty
		//TODO-dchubkov: create a defect for this
		td = DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), td.getTestData(SearchPage.assetListSearch.getName())
				.mask(SearchMetaData.Search.AGENCY_NAME.getLabel(), SearchMetaData.Search.AGENCY.getLabel(), SearchMetaData.Search.PHONE.getLabel()));
		String expectedPolicyNumber = td.getTestData(SearchPage.assetListSearch.getName()).getValue(SearchMetaData.Search.POLICY_QUOTE.getLabel());

		mainApp().open();
		CustomAssert.enableSoftMode();

		//Search for Policy by its number
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, expectedPolicyNumber);
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get())).isTrue();
		PolicySummaryPage.labelPolicyNumber.verify.value(expectedPolicyNumber);

		//Search for Policy by all search by criteria simultaneously
		SearchPage.search(td);
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get())).isTrue();
		PolicySummaryPage.labelPolicyNumber.verify.value(expectedPolicyNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Dmitry Chubkov
	 * @name Search for Quote by its number and by all other criteria simultaneously
	 *
	 * @scenario
	 * 1. Open search page and choose "Quote" as "Search For" criteria
	 * 2. Fill "Policy/Quote #" of existing quote and press "Search" button
	 * 3. Verify that search is successful: Quote page opens for searched quote number
	 * 4. Open search page and choose "Quote" as "Search For" criteria
	 * 5. Fill all search by fields with valid values of same existed quote/customer, press "Search" button
	 * 6. Verify that search is successful: Quote page opens for searched criteria
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Common.SEARCH )
	public void testSearchForQuote(@Optional("") String state) {
		TestData td = getFullSearchData(SearchEnum.SearchFor.QUOTE);
		//BUG: Search result for Quote by valid "Phone #" or "Agency Name" or "Agency #" is empty
		//TODO-dchubkov: create a defect for this
		td = DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), td.getTestData(SearchPage.assetListSearch.getName())
				.mask(SearchMetaData.Search.AGENCY_NAME.getLabel(), SearchMetaData.Search.AGENCY.getLabel(), SearchMetaData.Search.PHONE.getLabel()));
		String expectedQuoteNumber = td.getTestData(SearchPage.assetListSearch.getName()).getValue(SearchMetaData.Search.POLICY_QUOTE.getLabel());

		mainApp().open();
		CustomAssert.enableSoftMode();

		//Search for Quote by its number
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, expectedQuoteNumber);
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.QUOTE.get())).isTrue();
		PolicySummaryPage.labelPolicyNumber.verify.value(expectedQuoteNumber);

		//Search for Quote by all search by criteria simultaneously
		SearchPage.search(td);
		assertThat(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.QUOTE.get())).isTrue();
		PolicySummaryPage.labelPolicyNumber.verify.value(expectedQuoteNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private synchronized TestData getFullSearchData(SearchEnum.SearchFor searchFor) {
		final String keyPattern = searchFor.equals(SearchEnum.SearchFor.QUOTE) ? "_QuoteFullDataSearchTest" : "_OtherFullDataSearchTest";
		String customerKey = EntitiesHolder.makeCustomerKey(CustomerType.INDIVIDUAL, getState()) + keyPattern;
		String policyKey = EntitiesHolder.makeDefaultPolicyKey(getPolicyType(), getState()) + keyPattern;

		if (!EntitiesHolder.isEntityPresent(customerKey) || !EntitiesHolder.isEntityPresent(policyKey)) {
			mainApp().open();
			String customerNumber = createCustomerIndividual(getTestSpecificTD("CustomerCreation"));
			EntitiesHolder.addNewEntity(customerKey, customerNumber);
			String policyNumber = searchFor.equals(SearchEnum.SearchFor.QUOTE) ? createQuote() : createPolicy();
			EntitiesHolder.addNewEntity(policyKey, policyNumber);
		}

		if (!fullSearchData.containsKey(policyKey)) {
			TestData customerGeneralTabData = getTestSpecificTD("CustomerCreation").getTestData(customer.getDefaultView().getTab(GeneralTab.class).getClass().getSimpleName());
			TestData agencyData0 = customerGeneralTabData.getTestDataList(CustomerMetaData.GeneralTab.AGENCY_ASSIGNMENT.getLabel()).get(0);

			TestData searchData = DataProviderFactory.dataOf(
					SearchMetaData.Search.POLICY_QUOTE.getLabel(), EntitiesHolder.getEntity(policyKey),
					SearchMetaData.Search.PRODUCT_ID.getLabel(), "Auto",
					SearchMetaData.Search.CUSTOMER.getLabel(), EntitiesHolder.getEntity(customerKey),
					//values doesn't work after gold dump is used
					//SearchMetaData.Search.AGENT_OF_RECORD.getLabel(), "AAA NCNU - 500001005",
					//SearchMetaData.Search.AGENT.getLabel(), "400018581",
					SearchMetaData.Search.CITY.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.CITY.getLabel()),
					SearchMetaData.Search.STATE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.STATE.getLabel()),
					SearchMetaData.Search.ZIP_CODE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()),
					SearchMetaData.Search.PHONE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.PHONE_NUMBER.getLabel()),
					SearchMetaData.Search.AGENCY_NAME.getLabel(), agencyData0.getValue(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_NAME.getLabel()),
					SearchMetaData.Search.AGENCY.getLabel(), agencyData0.getValue(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_CODE.getLabel()),
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

			fullSearchData.put(policyKey, DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), searchData));
		}

		TestData adjustedData = fullSearchData.get(policyKey).getTestData(SearchPage.assetListSearch.getName()).adjust(SearchMetaData.Search.SEARCH_FOR.getLabel(), searchFor.get());
		return DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), adjustedData);
	}
}
