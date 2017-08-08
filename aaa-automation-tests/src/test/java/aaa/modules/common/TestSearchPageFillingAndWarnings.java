package aaa.modules.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import aaa.main.enums.SearchEnum;
import aaa.common.metadata.SearchMetaData;
import aaa.common.pages.SearchPage;
import aaa.modules.BaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.AbstractStringElement;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestSearchPageFillingAndWarnings extends BaseTest {
	private static final int MIN_ACC_NUMBER_LENGTH = 9;
	private static final int CITY_LENGTH = 30;
	private static final int STATE_LENGTH = 2;
	private static final int ZIP_CODE_LENGTH = 10;
	private static final int PHONE_NUMBER_LENGTH = 10;
	private static final int SSN_LENGTH = 9;
	private static final int COMMON_MAX_LENGTH = 255;
	private Random random = new Random();

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
	public void testFillAndClearAllSearchCriteria() {
		final SearchEnum.SearchFor defaultSearchForCriteria = SearchEnum.SearchFor.POLICY;

		TestData searchRandomData = DataProviderFactory.dataOf(
				SearchMetaData.Search.SEARCH_FOR.getLabel(), getRandomSearchForCriteria(defaultSearchForCriteria).get(),
				SearchMetaData.Search.PRODUCT_ID.getLabel(), "Auto");

		for (String searchByField : SearchPage.assetListSearch.getAssetNames()) {
			if (SearchPage.assetListSearch.getAsset(searchByField) instanceof TextBox) {
				searchRandomData.adjust(searchByField, RandomStringUtils.randomAlphanumeric(20));
			}
		}

		//Steps #1-2
		mainApp().open();
		SearchPage.search(DataProviderFactory.dataOf(SearchPage.assetListSearch.getName(), searchRandomData));

		//Step #3
		((AssetList) SearchPage.assetListSearch).verify.someValues(searchRandomData);

		//Steps #4-5
		SearchPage.clear();
		for (String searchByField : SearchPage.assetListSearch.getAssetNames()) {
			BaseElement<?, ?> searchByControl = SearchPage.assetListSearch.getAsset(searchByField);
			if (searchByField.equals(SearchMetaData.Search.SEARCH_FOR.getLabel())) {
				((RadioGroup) searchByControl).verify.value(defaultSearchForCriteria.get());
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
	 * 2. Fill any search criteria which produces too broad search results (e.g. set "Product ID" = "Auto") and press "Search" button
	 * 3. Check that result set too large warning message appear
	 * 4. Fill all search by fields with less than minimum allowable criteria length and press "Search" button
	 * 5. Check that appropriate criteria length warning messages appear
	 * 6. Fill all search by fields with minimum allowable criteria length and press "Search" button
	 * 7. Check that appropriate criteria length warning messages disappear
	 * 8. Fill all search by fields with more than maximum allowable criteria length and press "Search" button
	 * 9. Check that appropriate criteria length warning messages appear
	 * 10. Fill all search by fields with maximum allowable criteria length and press "Search" button
	 * 11. Check that appropriate criteria length warning messages disappear
	 * 12. Fill all search by fields with criteria in wrong format (e.g. letters in phone or account number) and press "Search" button
	 * 13. Check that appropriate criteria format warning messages appear
	 * 14. Press "Clear" button and then "Search" button
	 * 15. Check that warning message about not entered criteria appears
	 * @details
	 */
	@Test
	@TestInfo(component = "Common.Search")
	public void testSearchWarnings() {
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
		final String resultSetTooLargeWarning = "Result set too large, refine search criteria";

		String searchAl = SearchPage.assetListSearch.getName();

		mainApp().open();
		// Check that result set too large warning message appear
		SearchPage.search(getRandomSearchForCriteria(), SearchEnum.SearchBy.PRODUCT_ID, "Auto");

		CustomAssert.enableSoftMode();
		SearchPage.verifyWarningsExist(resultSetTooLargeWarning);

		TestData wrongSearchData = DataProviderFactory.emptyData()
				.adjust(SearchMetaData.Search.SEARCH_FOR.getLabel(), getRandomSearchForCriteria().get())
				.adjust(SearchMetaData.Search.POLICY_QUOTE.getLabel(), RandomStringUtils.randomAlphabetic(4) + RandomStringUtils.randomNumeric(10))

				// Check less than minimum allowable values length warning messages
				.adjust(SearchMetaData.Search.ACCOUNT.getLabel(), RandomStringUtils.randomNumeric(MIN_ACC_NUMBER_LENGTH - 1))
				.adjust(SearchMetaData.Search.PHONE.getLabel(), RandomStringUtils.randomNumeric(PHONE_NUMBER_LENGTH - 1))
				.adjust(SearchMetaData.Search.SSN.getLabel(), RandomStringUtils.randomNumeric(SSN_LENGTH - 1));

		TestData td = DataProviderFactory.emptyData().adjust(searchAl, wrongSearchData);
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(accNumberLengthWarning, phoneFormatWarning, ssnLengthWarning);
		SearchPage.verifyWarningsExist(false, String.format(notFoundWarningTemplate, wrongSearchData.getValue(SearchMetaData.Search.SEARCH_FOR.getLabel())));

		// Check with minimum allowable values length
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(MIN_ACC_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(PHONE_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(SSN_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(false, accNumberLengthWarning, phoneFormatWarning, ssnLengthWarning);
		SearchPage.verifyWarningsExist(String.format(notFoundWarningTemplate, wrongSearchData.getValue(SearchMetaData.Search.SEARCH_FOR.getLabel())));

		// Check more than maximum allowable values length warning messages, 1st part (not all warning messages fits on page)
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.POLICY_QUOTE.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.BILLING_ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.FIRST_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.LAST_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.CITY.getLabel()), RandomStringUtils.randomAlphabetic(CITY_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.STATE.getLabel()), RandomStringUtils.randomAlphabetic(STATE_LENGTH + 1));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(stateLengthAndFormatWarning, cityLengthWarning,
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.POLICY_QUOTE.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.FIRST_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.LAST_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.ACCOUNT.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.BILLING_ACCOUNT.getLabel()));
		// Check with maximum allowable values length, 1st part
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.POLICY_QUOTE.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.BILLING_ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.FIRST_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.LAST_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.CITY.getLabel()), RandomStringUtils.randomAlphabetic(CITY_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.STATE.getLabel()), RandomStringUtils.randomAlphabetic(STATE_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(false, stateLengthAndFormatWarning, cityLengthWarning,
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.POLICY_QUOTE.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.FIRST_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.LAST_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.ACCOUNT.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.BILLING_ACCOUNT.getLabel()));

		// Check more than maximum allowable values length warning messages, 2nd part
		SearchPage.clear();
		td.adjust(searchAl, td.getTestData(searchAl).purgeAdjustments());
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.ZIP_CODE.getLabel()), RandomStringUtils.randomNumeric(ZIP_CODE_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(PHONE_NUMBER_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.CUSTOMER.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.AGENCY_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.AGENCY.getLabel()), RandomStringUtils.randomAlphanumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.UNDERWRITING_COMPANY.getLabel()), RandomStringUtils.randomAlphanumeric(COMMON_MAX_LENGTH + 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(SSN_LENGTH + 1));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(zipFormatWarning, phoneFormatWarning, ssnLengthWarning,
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.CUSTOMER.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.AGENCY_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.AGENCY.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.UNDERWRITING_COMPANY.getLabel()));
		// Check with maximum allowable values length, 2nd part
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.ZIP_CODE.getLabel()), RandomStringUtils.randomNumeric(ZIP_CODE_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomNumeric(PHONE_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.CUSTOMER.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.AGENCY_NAME.getLabel()), RandomStringUtils.randomAlphabetic(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.AGENCY.getLabel()), RandomStringUtils.randomAlphanumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.UNDERWRITING_COMPANY.getLabel()), RandomStringUtils.randomAlphanumeric(COMMON_MAX_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(SSN_LENGTH));
		SearchPage.search(td);
		//BUG: Maximum allowable length of 'Underwriting Company' criteria warning is incorrect on Search page"
		//TODO-dchubkov: create this defect
		SearchPage.verifyWarningsExist(false, zipFormatWarning, phoneFormatWarning, ssnLengthWarning,
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.CUSTOMER.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.AGENCY_NAME.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.AGENCY.getLabel()),
				String.format(maxLengthWarningTemplate, SearchMetaData.Search.UNDERWRITING_COMPANY.getLabel()));

		// Check wrong value format warning messages
		SearchPage.clear();
		td.adjust(searchAl, td.getTestData(searchAl).purgeAdjustments());
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomAlphanumeric(MIN_ACC_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.PHONE.getLabel()), RandomStringUtils.randomAlphanumeric(PHONE_NUMBER_LENGTH));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomAlphanumeric(SSN_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(accNumberLengthWarning, phoneFormatWarning, ssnFormatWarning);
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.ACCOUNT.getLabel()), RandomStringUtils.randomNumeric(COMMON_MAX_LENGTH - 1));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.PHONE.getLabel()),
				String.format("(%1$s) %2$s-%3$s", RandomStringUtils.randomNumeric(3), RandomStringUtils.randomNumeric(3), RandomStringUtils.randomNumeric(4)));
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.SSN.getLabel()), RandomStringUtils.randomNumeric(SSN_LENGTH));
		SearchPage.search(td);
		SearchPage.verifyWarningsExist(false, accNumberLengthWarning, phoneFormatWarning, ssnFormatWarning);
		td.adjust(TestData.makeKeyPath(searchAl, SearchMetaData.Search.PHONE.getLabel()),
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

	private SearchEnum.SearchFor getRandomSearchForCriteria() {
		return getRandomSearchForCriteria(new SearchEnum.SearchFor[0]);
	}

	private SearchEnum.SearchFor getRandomSearchForCriteria(SearchEnum.SearchFor... except) {
		List<SearchEnum.SearchFor> searchForValues = new ArrayList<>(Arrays.asList(SearchEnum.SearchFor.values()));
		searchForValues.removeAll(new ArrayList<>(Arrays.asList(except)));
		return searchForValues.get(random.nextInt(searchForValues.size()));
	}
}

