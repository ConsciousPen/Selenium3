package aaa.modules.common;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import aaa.common.enums.SearchEnum;
import aaa.common.metadata.SearchMetaData;
import aaa.common.pages.SearchPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.CommonTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class SearchTests extends CommonTest {

	@Test
	@TestInfo(component = "Common.Search")
	//TODO-dchubkov: test javadoc
	public void searchQuoteByNumberPositiveTest() {
		mainApp().open();
		//String quoteNumber = getCopiedQuote();
		String quoteNumber = "QCAH3926252606";
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		PolicySummaryPage.labelPolicyNumber.verify.value(quoteNumber);
	}

	@Test
	@TestInfo(component = "Common.Search")
	//TODO-dchubkov: test javadoc
	public void searchResultWarningsChecks() {
		final int minAccNumberLength = 9;
		final int cityLength = 30;
		final int stateLength = 2;
		final int zipCodeLength = 10;
		final int phoneNumberLength = 10;
		final int ssnLength = 9;
		final int maxLength = 255;

		final String accNumberWarning = String.format("Account number invalid - Should enter %d or more numeric digits or leave as blank", minAccNumberLength);
		final String cityFormatWarning = String.format("City searches require less or equal %d characters", cityLength);
		final String stateFormatWarning = String.format("State abbreviation. State search requires %d characters", stateLength);
		final String zipFormatWarning = String.format("Zip code searches require less or equal %d characters", zipCodeLength);
		final String phoneFormatWarning = String.format("Wrong Phone# format, allowed formats are: %d digit number or number in format (999) 999-9999", phoneNumberLength);
		final String maxLengthWarningTemplate = "Maximum criteria length for '%s' is " + maxLength;
		final String ssnFormatWarning = "Social Security Number must be numeric";
		final String ssnLengthWarning = String.format("Social Security Number searches require %d digits", ssnLength);
		final String emptyCriteriaWarning = "Search criteria must be entered";
		final String notFoundWarningTemplate = "%s not found";

		TestData wrongSearchData = DataProviderFactory.emptyData();
		TestData td = DataProviderFactory.emptyData();

		mainApp().open();
		//TODO-dchubkov: Search by random non existent account number
		//TODO-dchubkov: Search by random non existent customer number
		//TODO-dchubkov: Search by random non existent billing number
		//TODO-dchubkov: Search by random non existent policy number

		// Search by random non existent quote number
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, "QCAH" + RandomStringUtils.randomNumeric(10));
		CustomAssert.assertTrue(SearchPage.warningsExist("Quote not found"));

		// Check less than minimum allowable length warning messages
		List<String> searchForValues = Stream.of(SearchEnum.SearchFor.values()).map(SearchEnum.SearchFor::get).collect(Collectors.toList());
		wrongSearchData.adjust(SearchMetaData.Search.SEARCH_FOR.getLabel(), searchForValues.get(new Random().nextInt(searchForValues.size())));
		wrongSearchData.adjust(SearchMetaData.Search.POLICY_QUOTE.getLabel(), RandomStringUtils.randomAlphabetic(4) + RandomStringUtils.randomAlphabetic(10));
		wrongSearchData.adjust(SearchMetaData.Search.ACCOUNT.getLabel(), RandomStringUtils.randomNumeric(minAccNumberLength - 1));
		wrongSearchData.adjust(SearchMetaData.Search.PHONE.getLabel(), RandomStringUtils.randomNumeric(phoneNumberLength - 1));
		wrongSearchData.adjust(SearchMetaData.Search.SSN.getLabel(), RandomStringUtils.randomNumeric(ssnLength - 1));
		td.adjust(SearchPage.assetListSearch.getName(), wrongSearchData);
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(accNumberWarning, phoneFormatWarning, ssnLengthWarning);
		SearchPage.verifyWarningsExist(false, String.format(notFoundWarningTemplate, wrongSearchData.getValue(SearchMetaData.Search.SEARCH_FOR.getLabel())));

		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(minAccNumberLength));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(phoneNumberLength));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(ssnLength));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(false, accNumberWarning, phoneFormatWarning, ssnLengthWarning);
		SearchPage.verifyWarningsExist(String.format(notFoundWarningTemplate, wrongSearchData.getValue(SearchMetaData.Search.SEARCH_FOR.getLabel())));

		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(phoneNumberLength + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(ssnLength + 1));
		SearchPage.verifyWarningsExist(phoneFormatWarning, ssnLengthWarning);


		// Check more than maximum allowable length warning messages
		//TODO-dchubkov: over max length messages: Policy/Quote #, First Name, 'Last Name, Account #

		// Check format warning messages
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomAlphabetic(9));

		//to be continued...
	}
}
