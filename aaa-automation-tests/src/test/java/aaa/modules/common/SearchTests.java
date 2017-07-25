package aaa.modules.common;

import java.util.Arrays;
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
import toolkit.webdriver.controls.AbstractStringElement;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class SearchTests extends CommonTest {
	private static final int MIN_ACC_NUMBER_LENGTH = 9;
	private static final int CITY_LENGTH = 30;
	private static final int STATE_LENGTH = 2;
	private static final int ZIP_CODE_LENGTH = 10;
	private static final int PHONE_NUMBER_LENGTH = 10;
	private static final int SSN_LENGTH = 9;
	private static final int COMMON_MAX_LENGTH = 255;
	private Random random = new Random();


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

	/**
	 * @author Dmitry Chubkov
	 * @name Fill and clear all search criteria fields check
	 *
	 * @scenario
	 * 1. Open search page and choose any "Search For" criteria except "Policy"
	 * 2. Fill all search by criteria fields with any random values and press "Search" button
	 * 3. Verify that all search by criteria fields have same filled values from step 2
	 * 4. Press "Clear" button
	 * 5. Verify that all search by criteria fields have empty values except "Search For" which resets to default "Policy" value
	 * @details
	 */
	@Test
	@TestInfo(component = "Common.Search")
	public void fillAndClearAllSearchCriteriaCheck() {
		final String defaultSearchForCriteria = SearchEnum.SearchFor.POLICY.get();

		TestData searchData = DataProviderFactory.emptyData()
				.adjust(SearchMetaData.Search.SEARCH_FOR.getLabel(), getRandomSearchForCriteria(defaultSearchForCriteria))
				.adjust(SearchMetaData.Search.PRODUCT_ID.getLabel(), "Auto");

		for (String searchByField : SearchPage.assetListSearch.getAssetNames()) {
			if (SearchPage.assetListSearch.getControl(searchByField, BaseElement.class) instanceof TextBox) {
				searchData.adjust(searchByField, RandomStringUtils.randomAlphanumeric(20));
			}
		}

		//Steps #1-2
		mainApp().open();
		SearchPage.search(DataProviderFactory.emptyData().adjust(SearchPage.assetListSearch.getName(), searchData));

		//Step #3
		((AssetList) SearchPage.assetListSearch).verify.someValues(searchData);

		//Steps #4-5
		SearchPage.clear();
		for (String searchByField : SearchPage.assetListSearch.getAssetNames()) {
			BaseElement<?, ?> searchByControl = SearchPage.assetListSearch.getControl(searchByField, BaseElement.class);
			if (searchByField.equals(SearchMetaData.Search.SEARCH_FOR.getLabel())) {
				((RadioGroup) searchByControl).verify.value(defaultSearchForCriteria);
			} else {
				((AbstractStringElement<?>) searchByControl).verify.value("");
			}
		}
	}

	/**
	 * @author Dmitry Chubkov
	 * @name Search criteria length and format warnings checks
	 *
	 * @scenario
	 * 1. Open search page and choose any "Search For" criteria
	 * 2. Fill all search by fields with less than minimum allowable criteria length and press "Search" button
	 * 3. Check that appropriate criteria length warning messages appear
	 * 4. Fill all search by fields with minimum allowable criteria length and press "Search" button
	 * 5. Check that appropriate criteria length warning messages disappear
	 * 6. Fill all search by fields with more than maximum allowable criteria length and press "Search" button
	 * 7. Check that appropriate criteria length warning messages appear
	 * 8. Fill all search by fields with maximum allowable criteria length and press "Search" button
	 * 9. Check that appropriate criteria length warning messages disappear
	 * 10. Fill all search by fields with criteria in wrong format (e.g. letters in phone or account number) and press "Search" button
	 * 11. Check that appropriate criteria format warning messages appear
	 * 12. Press "Clear" button and then "Search" button
	 * 13. Check that warning message about not entered criteria appears
	 * @details
	 */
	@Test
	@TestInfo(component = "Common.Search")
	public void searchCriteriaLengthAndFormatWarningsChecks() {
		final String accNumberLengthWarning = String.format("Account number invalid - Should enter %d or more numeric digits or leave as blank", MIN_ACC_NUMBER_LENGTH);
		final String cityLengthWarning = String.format("City searches require less or equal %d characters", CITY_LENGTH);
		final String stateLengthAndFormatWarning = String.format("State abbreviation. State search requires %d characters", STATE_LENGTH);
		final String zipFormatWarning = String.format("Zip code searches require less or equal %d characters", ZIP_CODE_LENGTH);
		final String phoneFormatWarning = String.format("Wrong Phone# format, allowed formats are: %d digit number or number in format (999) 999-9999", PHONE_NUMBER_LENGTH);
		final String ssnLengthWarning = String.format("Social Security Number searches require %d digits", SSN_LENGTH);
		final String ssnFormatWarning = "Social Security Number must be numeric";
		final String maxLengthWarningTemplate = "Maximum criteria length for '%s' is " + COMMON_MAX_LENGTH;
		final String emptySearchCriteriaWarning = "Search criteria must be entered";
		final String notFoundWarningTemplate = "%s not found";

		TestData wrongSearchData = DataProviderFactory.emptyData();
		TestData td = DataProviderFactory.emptyData();

		mainApp().open();
		wrongSearchData.adjust(SearchMetaData.Search.SEARCH_FOR.getLabel(), getRandomSearchForCriteria());
		wrongSearchData.adjust(SearchMetaData.Search.POLICY_QUOTE.getLabel(), RandomStringUtils.randomAlphabetic(4) + RandomStringUtils.randomNumeric(10));

		// Check less than minimum allowable values length warning messages
		wrongSearchData.adjust(SearchMetaData.Search.ACCOUNT.getLabel(), RandomStringUtils.randomNumeric(MIN_ACC_NUMBER_LENGTH - 1));
		wrongSearchData.adjust(SearchMetaData.Search.PHONE.getLabel(), RandomStringUtils.randomNumeric(PHONE_NUMBER_LENGTH - 1));
		wrongSearchData.adjust(SearchMetaData.Search.SSN.getLabel(), RandomStringUtils.randomNumeric(SSN_LENGTH - 1));
		td.adjust(SearchPage.assetListSearch.getName(), wrongSearchData);
		SearchPage.search(td);

		CustomAssert.enableSoftMode();
		SearchPage.verifyWarningsExist(accNumberLengthWarning, phoneFormatWarning, ssnLengthWarning);
		SearchPage.verifyWarningsExist(false, String.format(notFoundWarningTemplate, wrongSearchData.getValue(SearchMetaData.Search.SEARCH_FOR.getLabel())));

		// Check with minimum allowable values length
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(MIN_ACC_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(PHONE_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(SSN_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(false, accNumberLengthWarning, phoneFormatWarning, ssnLengthWarning);
		SearchPage.verifyWarningsExist(String.format(notFoundWarningTemplate, wrongSearchData.getValue(SearchMetaData.Search.SEARCH_FOR.getLabel())));

		// Check more than maximum allowable values length warning messages, 1st part (not all warning messages fits on page)
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.POLICY_QUOTE.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.BILLING_ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.FIRST_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.LAST_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.CITY.getLabel()), RandomStringUtils.randomAlphabetic(CITY_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.STATE.getLabel()), RandomStringUtils.randomAlphabetic(STATE_LENGTH + 1));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(stateLengthAndFormatWarning, cityLengthWarning,
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.POLICY_QUOTE.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.FIRST_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.LAST_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.ACCOUNT.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.BILLING_ACCOUNT.getLabel()));
		// Check with maximum allowable values length, 1st part
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.POLICY_QUOTE.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.BILLING_ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.FIRST_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.LAST_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.CITY.getLabel()), RandomStringUtils.randomAlphabetic(CITY_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.STATE.getLabel()), RandomStringUtils.randomAlphabetic(STATE_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(false, stateLengthAndFormatWarning, cityLengthWarning,
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.POLICY_QUOTE.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.FIRST_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.LAST_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.ACCOUNT.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.BILLING_ACCOUNT.getLabel()));

		// Check more than maximum allowable values length warning messages, 2nd part
		SearchPage.clear();
		td.adjust(SearchPage.assetListSearch.getName(), td.getTestData(SearchPage.assetListSearch.getName()).purgeAdjustments());
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ZIP_CODE.getLabel()), RandomStringUtils.randomNumeric(ZIP_CODE_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(PHONE_NUMBER_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.CUSTOMER.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.AGENCY_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.AGENCY.getLabel()), RandomStringUtils.randomAlphanumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.UNDERWRITING_COMPANY.getLabel()), RandomStringUtils.randomAlphanumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(SSN_LENGTH + 1));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(zipFormatWarning, phoneFormatWarning, ssnLengthWarning,
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.CUSTOMER.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.AGENCY_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.AGENCY.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.UNDERWRITING_COMPANY.getLabel()));
		// Check with maximum allowable values length, 2nd part
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ZIP_CODE.getLabel()), RandomStringUtils.randomNumeric(ZIP_CODE_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(PHONE_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.CUSTOMER.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.AGENCY_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.AGENCY.getLabel()), RandomStringUtils.randomAlphanumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.UNDERWRITING_COMPANY.getLabel()), RandomStringUtils.randomAlphanumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(SSN_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(false, zipFormatWarning, phoneFormatWarning, ssnLengthWarning,
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.CUSTOMER.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.AGENCY_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.AGENCY.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.UNDERWRITING_COMPANY.getLabel()));

		// Check wrong value format warning messages
		SearchPage.clear();
		td.adjust(SearchPage.assetListSearch.getName(), td.getTestData(SearchPage.assetListSearch.getName()).purgeAdjustments());
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomAlphanumeric(MIN_ACC_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomAlphanumeric(PHONE_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomAlphanumeric(SSN_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(accNumberLengthWarning, phoneFormatWarning, ssnFormatWarning);
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH - 1));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()),
				String.format("(%1$s) %2$s-%3$s", RandomStringUtils.randomNumeric(3), RandomStringUtils.randomNumeric(3), RandomStringUtils.randomNumeric(4)));
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(SSN_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(false, accNumberLengthWarning, phoneFormatWarning, ssnFormatWarning);
		td.adjust(TestData.makeKeyPath(SearchPage.assetListSearch.getName(), SearchMetaData.Search.PHONE.getLabel()),
				String.format("(%1$s) %2$s-%3$s", RandomStringUtils.randomNumeric(3), RandomStringUtils.randomNumeric(4), RandomStringUtils.randomNumeric(4)));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(phoneFormatWarning);

		// Check empty search criteria warning message
		SearchPage.clear();
		SearchPage.buttonSearch.click();
		SearchPage.verifyWarningsExist(emptySearchCriteriaWarning);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private String getRandomSearchForCriteria() {
		return getRandomSearchForCriteria(new String[0]);
	}

	private String getRandomSearchForCriteria(String... except) {
		List<String> searchForValues = Stream.of(SearchEnum.SearchFor.values()).map(SearchEnum.SearchFor::get).collect(Collectors.toList());
		searchForValues.removeAll(Arrays.asList(except));
		return searchForValues.get(random.nextInt(searchForValues.size()));
	}
}

