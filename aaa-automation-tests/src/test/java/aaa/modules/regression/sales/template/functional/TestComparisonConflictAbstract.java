package aaa.modules.regression.sales.template.functional;

import static aaa.main.enums.ProductConstants.TransactionHistoryType.*;
import static aaa.main.pages.summary.PolicySummaryPage.tableDifferences;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.*;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;

public abstract class TestComparisonConflictAbstract extends PolicyBaseTest {

	private static final String SECTION_UIFIELD_SEPARATOR = ".";
	private static final int SECTION_NAME_ROW_INDEX = 1;
	private final ErrorTab errorTab = new ErrorTab();

	/**
	 * Not used Test Data fields for creation expected result for sections
	 */
	private List<String> excludedTDKeys = ImmutableList.<String>builder()
			.add("Zip Code")
			.add("Address Line 1")
			.add("Address Line 2")
			.add("City")
			.add("State")
			.add("Has lived here for less than three years?")
			.add("Prior Zip Code")
			.add("Prior Address Line 1")
			.add("Prior Address Line 2")
			.add("Prior City")
			.add("Prior State")
			.add("Is residential address different from mailing address?")
			.add("Mailing Zip Code")
			.add("Mailing Address Line 1")
			.add("Mailing Address Line 2")
			.add("Mailing City")
			.add("Mailing State")
			.add("Validate Address")
			.add("Validate Address Dialog")
			.build();

	/**
	 * Represents ui values taken from TestData which representation on comparison page is different from TD value.
	 */
	protected abstract Map<String, String> getComparisonPageDifferentValues();

	/**
	 * Return ui field property from TD by path from comparison page
	 */
	protected abstract ArrayListMultimap<String, String> getUIFieldsToTDMapping();

	/**
	 * Return values for UI fields if they are not present in Test Data
	 */
	protected abstract ArrayListMultimap<String, String> getPredefinedExpectedValues();

	protected abstract Tab getGeneralTab();

	protected abstract Tab getDocumentsAndBindTab();

	protected abstract void navigateToGeneralTab();

	protected abstract TestData getTdPolicy();

	//Comparison functionality

	/**
	 * Verification of Comparison screen (sections/UI fields from section/values) of 2 quote versions based on test data that was used during creation
	 * @param tdVersion1 test data that is used for version1 quote creation
	 * @param tdVersion2 test data that is used for version2 quote creation
	 * @param expectedSectionsAndUIFields list of sections/UI fields from section that should be displayed on Comparison page
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	protected void dataGatherComparison(TestData tdVersion1, TestData tdVersion2, Multimap<String, String> expectedSectionsAndUIFields, String tabName, String sectionName) {
		mainApp().open();
		createCustomerIndividual();
		createQuote(getTestSpecificTD("TestData_NB_Quote"));
		policy.dataGather().start();
		getGeneralTab().createVersion();
		navigateToGeneralTab();
		policy.getDefaultView().fillUpTo(tdVersion2, getDocumentsAndBindTab().getClass(), false);
		getDocumentsAndBindTab().saveAndExit();
		PolicySummaryPage.buttonQuoteVersionHistory.click();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		verifyTransactionHistoryType(1, QUOTE, softly);
		verifyTransactionHistoryType(2, QUOTE, softly);
		selectTransactionType(1, true);
		selectTransactionType(2, true);
		PolicySummaryPage.buttonCompare.click();

		checkComparisonPage(tdVersion1, tdVersion2, expectedSectionsAndUIFields, tabName, sectionName, softly);

		softly.close();
	}

	private void selectTransactionType(int rowIndex, boolean isSelected) {
		PolicySummaryPage.tableTransactionHistory.getRow(rowIndex).getCell(1).controls.checkBoxes.get(1).setValue(isSelected);
	}

	private void verifyTransactionHistoryType(int rowIndex, String type, ETCSCoreSoftAssertions softly) {
		softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(rowIndex).getCell(2).getValue()).as("Transaction type should be {0}", type).isEqualTo(type);
	}

	/**
	 * Verification of Comparison screen (sections/UI fields from section/values) of 2 endorsement versions based on test data that was used during creation
	 * @param tdVersion1 test data that is used for version1 endorsement transaction
	 * @param tdVersion2 test data that is used for version2 endorsement transaction
	 * @param expectedSectionsAndUIFields list of sections/UI fields from section that should be displayed on Comparison page
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	protected void endorsementsComparison(TestData tdVersion1, TestData tdVersion2, Multimap<String, String> expectedSectionsAndUIFields, String tabName, String sectionName) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		processPlus20DaysEndorsement(tdVersion1);
		processPlus25DaysEndorsement(tdVersion2);

		PolicySummaryPage.buttonTransactionHistory.click();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		verifyTransactionHistoryType(1, ENDORSEMENT, softly);
		verifyTransactionHistoryType(2, ENDORSEMENT, softly);
		verifyTransactionHistoryType(3, ISSUE, softly);
		selectTransactionType(1, true);
		selectTransactionType(2, true);
		PolicySummaryPage.buttonCompareVersions.click();

		checkComparisonPage(tdVersion1, tdVersion2, expectedSectionsAndUIFields, tabName, sectionName, softly);

		softly.close();
	}

	/**
	 * Verification of Comparison screen (sections/UI fields from section/values) of 2 renewal versions based on test data that was used during creation
	 * @param tdVersion1 test data that is used for version1 renewal transaction
	 * @param tdVersion2 test data that is used for version2 renewal transaction
	 * @param expectedSectionsAndUIFields list of sections/UI fields from section that should be displayed on Comparison page
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	protected void renewalComparison(TestData tdVersion1, TestData tdVersion2, Multimap<String, String> expectedSectionsAndUIFields, String tabName, String sectionName) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalGenerationJob(expirationDate);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		renewalVersionCreation(tdVersion1);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		policy.getDefaultView().fill(tdVersion2);
		PolicySummaryPage.buttonRenewalQuoteVersion.click();

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		verifyTransactionHistoryType(1, RENEWAL, softly);
		verifyTransactionHistoryType(2, RENEWAL, softly);
		verifyTransactionHistoryType(3, RENEWAL, softly);
		selectTransactionType(1, true);
		selectTransactionType(2, true);
		PolicySummaryPage.buttonCompare.click();

		checkComparisonPage(tdVersion1, tdVersion2, expectedSectionsAndUIFields, tabName, sectionName, softly);

		softly.close();
	}

	private void renewalVersionCreation(TestData tdVersion) {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		policy.getDefaultView().fill(tdVersion);
	}

	/**
	 * Mid-term endorsement transaction effective date + 20 days
	 * @param td test data that is used for endorsement transaction
	 */
	private void processPlus20DaysEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Plus20Days"));
		policy.endorse().performAndFill(endorsementTD);
	}

	/**
	 * Mid-term endorsement transaction effective date + 25 days
	 * @param td test data that is used for endorsement transaction
	 */
	private void processPlus25DaysEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Plus25Days"));
		policy.endorse().performAndFill(endorsementTD);
	}

	/**
	 * Process renewal jobs to get policy in Proposed status
	 * @param expirationDate term expiration date
	 */
	private void processRenewalGenerationJob(LocalDateTime expirationDate) {
		//move time to R-45 and run renewal batch job
		LocalDateTime renewImageGenDate = getTimePoints().getRenewPreviewGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//move time to R-35 and run renewal batch job
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	/**
	 * Compare actual result for sections/UI fields from section/values of fields from comparison screen with expected result taken from Test Data + predefinedExpectedValues (fields that are not in Test Data)
	 * @param tdVersion1 test data that is used for version1 transaction (data gather, endorsement or renewal)
	 * @param tdVersion2 test data that is used for version2 transaction (data gather, endorsement or renewal)
	 * @param expectedSectionsAndUIFields list of sections/UI fields from section that should be displayed on Comparison page
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	private void checkComparisonPage(TestData tdVersion1, TestData tdVersion2, Multimap<String, String> expectedSectionsAndUIFields, String tabName, String sectionName, ETCSCoreSoftAssertions softly) {
		ListMultimap<String, String> expectedUIFieldsAndValuesV1 = createExpectedResultFromTD(tdVersion1, tabName, sectionName);
		ListMultimap<String, String> expectedUIFieldsAndValuesV2 = createExpectedResultFromTD(tdVersion2, tabName, sectionName);

		ArrayListMultimap<String, String> actualSectionsAndUIFields = ArrayListMultimap.create();
		ArrayListMultimap<String, String> actualUIFieldsAndValuesV1 = ArrayListMultimap.create();
		ArrayListMultimap<String, String> actualUIFieldsAndValuesV2 = ArrayListMultimap.create();
		gatherActualResults(actualSectionsAndUIFields, actualUIFieldsAndValuesV1, actualUIFieldsAndValuesV2);
		verificationComparisonPage(expectedSectionsAndUIFields, expectedUIFieldsAndValuesV1, actualSectionsAndUIFields, actualUIFieldsAndValuesV1, 1, softly); //version1
		verificationComparisonPage(expectedSectionsAndUIFields, expectedUIFieldsAndValuesV2, actualSectionsAndUIFields, actualUIFieldsAndValuesV2, 0, softly); //version2
	}

	/**
	 * Creation of expected result for section based on Test Data, tab name and section name
	 * @param td test data name based on what we took our expected results for UI fields
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 * @return list of expected UI fields and according values
	 */
	private ListMultimap<String, String> createExpectedResultFromTD(TestData td, String tabName, String sectionName) {
		ListMultimap<String, String> expectedUIFieldsAndValues = MultimapBuilder.hashKeys().arrayListValues().build();
		List<TestData> testData = td.getTestData(tabName).getTestDataList(sectionName);
		TestData uiFields = testData.isEmpty() ? td.getTestData(tabName) : testData.get(0);
		for (String key : uiFields.getKeys()) {
			if (!excludedTDKeys.contains(key)) {
				String value = uiFields.getValue(key);
				String valueOnComparisonPage = getComparisonPageDifferentValues().getOrDefault(value, value);
				expectedUIFieldsAndValues.put(key, valueOnComparisonPage);
			}
		}
		return expectedUIFieldsAndValues;
	}

	/**
	 * Verification of expected list of sections/UI fields and UI fields/values
	 * @param expectedSectionsAndUIFields expected list of sections/UI fields from section
	 * @param expectedUIFieldsAndValues expected list of UI fields from section/values
	 * @param actualSectionsAndUIFields actual list of sections/UI fields from section
	 * @param actualUIFieldsAndValues actual list of UI fields from section/values
	 * @param version version that is under verification (current/available)
	 */
	private void verificationComparisonPage(Multimap<String, String> expectedSectionsAndUIFields, ListMultimap<String, String> expectedUIFieldsAndValues,
			Multimap<String, String> actualSectionsAndUIFields, ArrayListMultimap<String, String> actualUIFieldsAndValues, int version, ETCSCoreSoftAssertions softly) {
		softly.assertThat(actualSectionsAndUIFields.keySet().size()).isEqualTo(expectedSectionsAndUIFields.keySet().size());
		for (String sectionName : expectedSectionsAndUIFields.keySet()) {
			softly.assertThat(actualSectionsAndUIFields.get(sectionName)).isNotNull();
			softly.assertThat(actualSectionsAndUIFields.get(sectionName)).isEqualTo(expectedSectionsAndUIFields.get(sectionName));
		}
		for (String uiFieldsPath : actualUIFieldsAndValues.keySet()) {
			List<String> uiFields = getUIFieldsToTDMapping().get(uiFieldsPath);
			if (CollectionUtils.isEmpty(uiFields)) {
				List<String> expectedValues = getPredefinedExpectedValues().get(uiFieldsPath);
				softly.assertThat(expectedValues.isEmpty()).as("UI field path {0} not found in TestData or predefined values.", uiFieldsPath).isFalse();
				// if no ui fields prefilled from testdata then look into predefined
				String expectedValue = expectedValues.get(version);
				softly.assertThat(expectedValue).isNotNull().as("Expected values for ui field path {0} not found in TestData or predefined values.", uiFieldsPath);
				// only unique ui field key is supported for now
				softly.assertThat(actualUIFieldsAndValues.get(uiFieldsPath).get(0)).as("Problem in " + uiFieldsPath).isEqualTo(expectedValue);
			} else {
				for (int uiFieldPos = 0; uiFieldPos < uiFields.size(); uiFieldPos++) {
					String expectedValueFromTD = expectedUIFieldsAndValues.get(uiFields.get(uiFieldPos)).get(0);
					String expectedValue;
					if (expectedValueFromTD == null) {
						expectedValue = getPredefinedExpectedValues().get(uiFieldsPath).get(version);
					} else {
						expectedValue = expectedValueFromTD;
					}
					softly.assertThat(actualUIFieldsAndValues.get(uiFieldsPath).get(uiFieldPos)).as("Problem in " + uiFieldsPath).isEqualTo(expectedValue);
				}
			}
		}
	}

	/**
	 * Creation of actual result  (sections/UI fields from section/values) from Comparison screen
	 * @param actualSectionsAndUIFields actual list of sections/UI fields from section
	 * @param actualUIFieldsAndValuesV1 actual list of UI fields from section/values for version1
	 * @param actualUIFieldsAndValuesV2 actual list of UI fields from section/values for version2
	 */
	private void gatherActualResults(Multimap<String, String> actualSectionsAndUIFields, ArrayListMultimap<String, String> actualUIFieldsAndValuesV1,
			ArrayListMultimap<String, String> actualUIFieldsAndValuesV2) {
		for (int sectionNumber = 0; ; sectionNumber++) {
			StaticElement sectionText = PolicySummaryPage.TransactionHistory.provideLinkTextComparisonTree(sectionNumber);
			if (sectionText.isPresent()) {
				String sectionName = sectionText.getValue();
				List<String> uiFields = parseUIFieldsForSection(sectionNumber);
				actualSectionsAndUIFields.putAll(sectionName, uiFields);
				actualUIFieldsAndValuesV1.putAll(parseUIFieldsAndValuesForSection(sectionNumber, sectionName, uiFields, 3));
				actualUIFieldsAndValuesV2.putAll(parseUIFieldsAndValuesForSection(sectionNumber, sectionName, uiFields, 2));
			} else {
				break;
			}
		}
	}

	/**
	 * Getting actual list of values for sections/UI fields
	 * @param sectionNumber number of section
	 * @param sectionName name of section
	 * @param uiFields list of UI fields belong to section
	 * @param column version that is checked (current/available)
	 * @return values for sections/UI fields
	 */
	private Multimap<String, String> parseUIFieldsAndValuesForSection(int sectionNumber, String sectionName, List<String> uiFields, int column) {
		Multimap<String, String> comparisonValues = ArrayListMultimap.create();
		for (int uiFieldNumber = 0; uiFieldNumber < uiFields.size(); uiFieldNumber++) {
			StaticElement columnValue = PolicySummaryPage.TransactionHistory.provideValueExpandComparisonTree(sectionNumber, uiFieldNumber, column);
			if (columnValue.isPresent()) {
				comparisonValues.put(buildUIFieldPath(sectionName, uiFields.get(uiFieldNumber)), columnValue.getValue());
			} else {
				comparisonValues.put(buildUIFieldPath(sectionName, uiFields.get(uiFieldNumber)), StringUtils.EMPTY);
			}
		}
		return comparisonValues;
	}

	/**
	 * Getting actual list of UI fields for a section
	 * @param sectionNumber number of section
	 * @return list of UI fields
	 */
	private List<String> parseUIFieldsForSection(int sectionNumber) {
		List<String> uiFields = new ArrayList<>();
		PolicySummaryPage.TransactionHistory.provideLinkExpandComparisonTree(sectionNumber).click();
		for (int uiFieldNumber = 0; ; uiFieldNumber++) {
			StaticElement uiFieldElement = PolicySummaryPage.TransactionHistory.provideAtributeExpandComparisonTree(sectionNumber, uiFieldNumber);
			if (uiFieldElement.isPresent()) {
				uiFields.add(uiFieldElement.getValue());
			} else {
				break;
			}
		}
		return uiFields;
	}

	/**
	 * Creating Section.UIField for storing value
	 * @param sectionName section
	 * @param value UI field name
	 * @return Section.UIField
	 */
	private String buildUIFieldPath(String sectionName, String value) {
		return sectionName + SECTION_UIFIELD_SEPARATOR + value;
	}

	//Conflict functionality

	/**
	 * Verification of comparison screen after conflict resolution for oose and rolled on transactions,  endorsement and rolled on transaction. Verification content of Conflict screen.
	 * @param tdVersion1 test data that is used for endorsement transaction
	 * @param tdVersion2 test data that is used for oose transaction
	 * @param conflictLinks what version(current/available) we chose on conflict page
	 * @param expectedSectionsAndUIFieldsOOSE expected list of sections/UI fields for section on comparison for oose and rolled on transactions
	 * @param expectedSectionsAndUIFieldsEndorsement expected list of sections/UI fields for section on comparison for endorsement and rolled on transactions
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 * @param isAutomatic what kind of conflict we are doing automatic = true, manual = false
	 */
	protected void ooseConflict(TestData tdVersion1, TestData tdVersion2, ArrayListMultimap<String, String> conflictLinks, Multimap<String, String> expectedSectionsAndUIFieldsOOSE,
			Multimap<String, String> expectedSectionsAndUIFieldsEndorsement, String tabName, String sectionName, Boolean isAutomatic) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		processPlus20DaysEndorsement(tdVersion1);
		processPlus10DaysOOSEndorsement(tdVersion2);
		policy.rollOn().openConflictPage(isAutomatic);
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		resolveConflict(conflictLinks, softly);
		policy.rollOn().submit();

		PolicySummaryPage.buttonTransactionHistory.click();
		verifyTransactionHistoryType(1, ROLLED_ON_ENORSEMENT, softly);
		verifyTransactionHistoryType(2, OOS_ENDORSEMENT, softly);
		verifyTransactionHistoryType(3, BACKED_OFF_ENDORSEMENT, softly);
		verifyTransactionHistoryType(4, ISSUE, softly);

		selectTransactionType(1, true);
		selectTransactionType(2, true);
		PolicySummaryPage.buttonCompareVersions.click();
		checkComparisonPage(tdVersion2, tdVersion1, expectedSectionsAndUIFieldsOOSE, tabName, sectionName, softly);
		Tab.buttonCancel.click();

		selectTransactionType(1, true);
		selectTransactionType(2, false);
		selectTransactionType(3, true);
		PolicySummaryPage.buttonCompareVersions.click();
		checkComparisonPage(tdVersion1, tdVersion2, expectedSectionsAndUIFieldsEndorsement, tabName, sectionName, softly);
		Tab.buttonCancel.click();

		softly.close();
	}

	/**
	 * OOS endorsement transaction effective date + 10 days
	 * @param td test data that is used for endorsement transaction
	 */
	private void processPlus10DaysOOSEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Plus10Days"));
		policy.endorse().performAndFill(endorsementTD);
	}

	/**
	 * Verification of comparison screen after Renewal Merge for renewal and rolled on transactions. Verification content of Conflict screen during Renewal Merge.
	 * @param tdVersion1 test data that is used for renewal transaction
	 * @param tdVersion2 test data that is used for oose transaction
	 * @param conflictLinks what version(current/available) we chose on conflict page
	 * @param expectedSectionsAndUIFieldsRenewal expected list of sections/UI fields on comparison for renewal and rolled on transactions
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	protected void renewalMerge(TestData tdVersion1, TestData tdVersion2, ArrayListMultimap<String, String> conflictLinks, Multimap<String, String> expectedSectionsAndUIFieldsRenewal, String tabName,
			String sectionName) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalGenerationJob(expirationDate);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		renewalVersionCreation(tdVersion1);
		processMinus1MonthEndorsement(tdVersion2);
		//Todo solve this issue with error for effective date
		if (errorTab.isVisible()) {
			errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200011);
			errorTab.submitTab();
		}

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		resolveConflict(conflictLinks, softly);
		policy.rollOn().submit();
		PolicySummaryPage.buttonRenewalQuoteVersion.click();
		verifyTransactionHistoryType(1, RENEWAL, softly);
		verifyTransactionHistoryType(2, RENEWAL, softly);
		verifyTransactionHistoryType(3, RENEWAL, softly);

		selectTransactionType(1, true);
		selectTransactionType(2, true);
		PolicySummaryPage.buttonCompare.click();

		checkComparisonPage(tdVersion2, tdVersion1, expectedSectionsAndUIFieldsRenewal, tabName, sectionName, softly);
		Tab.buttonCancel.click();

		softly.close();
	}

	/**
	 * OOS endorsement transaction current date date - 1 month
	 * @param td test data that is used for endorsement transaction
	 */
	private void processMinus1MonthEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Minus1Month"));
		policy.endorse().performAndFill(endorsementTD);
	}

	/**
	 * For each UI field we select current or available version
	 * @param conflictLinks what version(current/available) we chose on conflict page
	 */
	private void resolveConflict(ArrayListMultimap<String, String> conflictLinks, ETCSCoreSoftAssertions softly) {
		Multiset<String> uiFieldPath = conflictLinks.keys();
		List<String> presentedSectionOnConflictPage = new ArrayList<>();
		for (int sectionNumber = 0; ; sectionNumber++) {
			StaticElement sectionText = PolicySummaryPage.TransactionHistory.provideLinkTextComparisonTree(sectionNumber);
			if (sectionText.isPresent()) {
				String sectionName = sectionText.getValue();
				if (uiFieldPath.stream().anyMatch(uiFieldsPath -> uiFieldsPath.startsWith(sectionName))) {
					selectUIFieldsVersions(sectionName, sectionNumber, conflictLinks, softly);
				}
				presentedSectionOnConflictPage.add(sectionName);
			} else {
				break;
			}
		}
		allSectionsPresentedOnConflictPage(presentedSectionOnConflictPage, conflictLinks.keySet(), softly);
	}

	/**
	 * Verify that all sections are present
	 * @param presentedSectionOnConflictPage actual list of sections
	 * @param uiFieldsPaths expected list of sections
	 */
	private void allSectionsPresentedOnConflictPage(List<String> presentedSectionOnConflictPage, Set<String> uiFieldsPaths, ETCSCoreSoftAssertions softly) {
		for (String sectionName : presentedSectionOnConflictPage) {
			softly.assertThat(uiFieldsPaths.stream()
					.anyMatch(uiFieldPath -> uiFieldPath.startsWith(buildUIFieldPath(sectionName, StringUtils.EMPTY))))
					.as("Section " + sectionName + " not present in UI fields configuration.")
					.isTrue();
		}
	}

	/**
	 * Resolve all conflicts based on current or available selection
	 * @param sectionName name of the section
	 * @param sectionNumber number of the sections on conflict page
	 * @param conflictLinks what version(current/available) we chose on conflict page
	 */
	private void selectUIFieldsVersions(String sectionName, int sectionNumber, ArrayListMultimap<String, String> conflictLinks, ETCSCoreSoftAssertions softly) {
		PolicySummaryPage.TransactionHistory.provideLinkExpandComparisonTree(sectionNumber).click();
		List<String>uiFieldsPathList = conflictLinks.keys().stream()
				.filter(uiFieldsPath -> uiFieldsPath.startsWith(sectionName + SECTION_UIFIELD_SEPARATOR))
				.collect(Collectors.toList());
		long expectedResolvedUIFieldsConflicts = conflictLinks.keys().stream()
				.filter(path -> path.startsWith(sectionName))
				.count();
		int actualResolvedUIFieldsConflicts = 0;
		if (tableDifferences.isPresent()) {
			int columnsCount = tableDifferences.getColumnsCount();

			for (int uiFieldNumber = 0; ; uiFieldNumber++) {
				StaticElement uiFieldElement = PolicySummaryPage.TransactionHistory.provideAtributeExpandComparisonTree(sectionNumber, uiFieldNumber);
				if (uiFieldElement.isPresent()) {
					String uiFieldPath = buildUIFieldPath(sectionName, uiFieldElement.getValue());
					if (uiFieldsPathList.contains(uiFieldPath)) {
						List<String> versionsLinkValues = conflictLinks.get(uiFieldPath);
						for (int uiFieldsWithSameNameNumber = 0; uiFieldsWithSameNameNumber < versionsLinkValues.size(); uiFieldsWithSameNameNumber++) {
							String versionLinkValue = versionsLinkValues.get(uiFieldsWithSameNameNumber);
							int uiFieldPosition = uiFieldNumber + uiFieldsWithSameNameNumber;
							if (pressVersionLink(uiFieldPosition, columnsCount, versionLinkValue, sectionName)) {
								actualResolvedUIFieldsConflicts++;
							}
						}
					}
				} else {
					break;
				}
			}
		}
		softly.assertThat(actualResolvedUIFieldsConflicts).as("Invalid resolved UI field number for {0}.", sectionName).isEqualTo((int) expectedResolvedUIFieldsConflicts);

	}

	/**
	 * Select needed version (current or available)
	 * @param uiFieldRow row for the UI field
	 * @param columnCount last column for select version
	 * @param versionValue version value current/available
	 * @param sectionName name of the section
	 * @return boolean value based on press or not link
	 */
	private boolean pressVersionLink(int uiFieldRow, int columnCount, String versionValue, String sectionName) {
		Link linkSetValue;
		int versionPosition;
		switch (versionValue) {
			case "Current": {
				versionPosition = 1;
				break;
			}
			case "Available": {
				versionPosition = 2;
				break;
			}
			default:
				throw new InvalidArgumentException("Unknown conflict version");
		}
		int sectionRowIndex = tableDifferences.getRow(SECTION_NAME_ROW_INDEX, sectionName).getIndex();
		linkSetValue = tableDifferences.getRow(sectionRowIndex + uiFieldRow + 1).getCell(columnCount).controls.links.get(
				versionPosition);
		if (linkSetValue.isPresent() && linkSetValue.isVisible()) {
			linkSetValue.click();
			return true;
		}
		return false;
	}

}