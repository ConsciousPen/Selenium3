package aaa.modules.common;

import java.util.Random;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.SearchEnum;
import aaa.common.metadata.SearchMetaData;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestSearchPageFull extends AutoSSBaseTest {
	/*private static final int MIN_ACC_NUMBER_LENGTH = 9;
	private static final int CITY_LENGTH = 30;
	private static final int STATE_LENGTH = 2;
	private static final int ZIP_CODE_LENGTH = 10;
	private static final int PHONE_NUMBER_LENGTH = 10;
	private static final int SSN_LENGTH = 9;
	private static final int COMMON_MAX_LENGTH = 255;*/
	private Random random = new Random();

	@Test
	@TestInfo(component = "Common.Search")
	//TODO-dchubkov: test javadoc
	public void searchPolicyByFullTestData() {
		TestData customerData = tdSpecific.getTestData("CustomerCreation");
		TestData policyData = getStateTestData(tdPolicy, "DataGather", "TestData");
		TestData customerGeneralTabData = customerData.getTestData(customer.getDefaultView().getTab(GeneralTab.class).getClass().getSimpleName());

		mainApp().open();
		String customerNumber = createCustomerIndividual(customerData);
		String policyNumber = createPolicy(policyData);

		//String policyNumber = "CAAS926252998";
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		CustomAssert.assertTrue(NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get()));
		PolicySummaryPage.labelPolicyNumber.verify.value(policyNumber);

		TestData fullSearchData = DataProviderFactory.emptyData();
		TestData td = DataProviderFactory.emptyData();

		fullSearchData.adjust(SearchMetaData.Search.SEARCH_FOR.getLabel(), SearchEnum.SearchFor.POLICY.get());
		fullSearchData.adjust(SearchMetaData.Search.POLICY_QUOTE.getLabel(), policyNumber);
		//TODO-dchubkov: Product ID ?
		fullSearchData.adjust(SearchMetaData.Search.CUSTOMER.getLabel(), customerNumber);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		fullSearchData.adjust(SearchMetaData.Search.BILLING_ACCOUNT.getLabel(), BillingSummaryPage.labelBillingAccountNumber.getValue());

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.ACCOUNT.get());
		fullSearchData.adjust(SearchMetaData.Search.ACCOUNT.getLabel(), CustomerSummaryPage.labelAccountNumber.getValue());

		fullSearchData.adjust(SearchMetaData.Search.AGENT_OF_RECORD.getLabel(), "House Agent SMTestIA");
		fullSearchData.adjust(SearchMetaData.Search.AGENT.getLabel(), "400018581");

		fullSearchData.adjust(SearchMetaData.Search.FIRST_NAME.getLabel(), CustomerSummaryPage.labelCustomerName.getValue().split("\\s")[0]);
		fullSearchData.adjust(SearchMetaData.Search.LAST_NAME.getLabel(), CustomerSummaryPage.labelCustomerName.getValue().split("\\s")[1]);
		fullSearchData.adjust(SearchMetaData.Search.CITY.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.CITY.getLabel()));
		fullSearchData.adjust(SearchMetaData.Search.STATE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.STATE.getLabel()));
		fullSearchData.adjust(SearchMetaData.Search.ZIP_CODE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()));
		fullSearchData.adjust(SearchMetaData.Search.PHONE.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.PHONE_NUMBER.getLabel()));

		TestData agencyData0 = customerGeneralTabData.getTestDataList(CustomerMetaData.GeneralTab.AGENCY_ASSIGNMENT.getLabel()).get(0);
		fullSearchData.adjust(SearchMetaData.Search.AGENCY_NAME.getLabel(), agencyData0.getValue(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_NAME.getLabel()));
		fullSearchData.adjust(SearchMetaData.Search.AGENCY.getLabel(), agencyData0.getValue(CustomerMetaData.GeneralTab.AddAgencyMetaData.AGENCY_NAME.getLabel()));

		//TODO-dchubkov: Underwriting Company #
		fullSearchData.adjust(SearchMetaData.Search.SSN.getLabel(), customerGeneralTabData.getValue(CustomerMetaData.GeneralTab.SSN.getLabel()));

		td.adjust(SearchPage.assetListSearch.getName(), fullSearchData);
		SearchPage.search(td);
		PolicySummaryPage.buttonTransactionHistory.isPresent();
		PolicySummaryPage.labelPolicyNumber.verify.value(policyNumber);
	}
}

