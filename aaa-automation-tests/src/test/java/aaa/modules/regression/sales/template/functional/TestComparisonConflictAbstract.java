package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public abstract class TestComparisonConflictAbstract extends PolicyBaseTest {

	public static final String COMPONENT_ATTRIBUTE_SEPARATOR = ".";
	public static final int COMPONENT_NAME_ROW_INDEX = 1;
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
	 * Return attribute property from TD by path from comparison page
	 */
	protected abstract ArrayListMultimap<String, String> getAttributesToTDMapping();

	/**
	 * Return values for attributes if they are not present in Test Data
	 */
	protected abstract ArrayListMultimap<String, String> getPredefinedExpectedValues();

	protected abstract Tab getGeneralTab();

	protected abstract Tab getDocumentsAndBindTab();

	protected abstract void navigateToGeneralTab();

	protected abstract TestData getTdPolicy();


	//Comparison functionality

	/**
	 * Verification of Comparison screen (components/attributes/values) of 2 quote versions based on test data that was used during creation
	 * @param state state code
	 * @param tdVersion1 test data that is used for version1 quote creation
	 * @param tdVersion2 test data that is used for version2 quote creation
	 * @param checkedComponents list of components/attributes that should be displayed on Comparison page for section
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	public void dataGatherComparison(String state, TestData tdVersion1, TestData tdVersion2, Multimap checkedComponents, String tabName, String sectionName) {
		mainApp().open();
		createCustomerIndividual();
		createQuote(getTestSpecificTD("TestData_NB_Quote"));
		policy.dataGather().start();
		getGeneralTab().createVersion();
		navigateToGeneralTab();
		policy.getDefaultView().fillUpTo(tdVersion2, getDocumentsAndBindTab().getClass(), false);
		getDocumentsAndBindTab().saveAndExit();
		PolicySummaryPage.buttonQuoteVersionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.QUOTE);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.QUOTE);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompare.click();

		checkComparisonPage(tdVersion1, tdVersion2, checkedComponents, tabName, sectionName);
	}

	/**
	 * Verification of Comparison screen (components/attributes/values) of 2 endorsement versions based on test data that was used during creation
	 * @param state state code
	 * @param tdVersion1 test data that is used for version1 endorsement transaction
	 * @param tdVersion2 test data that is used for version2 endorsement transaction
	 * @param checkedComponents list of components/attributes that should be displayed on Comparison page for section
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	public void endorsementsComparison(String state, TestData tdVersion1, TestData tdVersion2, Multimap checkedComponents, String tabName, String sectionName) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		processPlus20DaysEndorsement(tdVersion1);
		processPlus25DaysEndorsement(tdVersion2);

		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(3).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ISSUE);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompareVersions.click();

		checkComparisonPage(tdVersion1, tdVersion2, checkedComponents, tabName, sectionName);
	}

	/**
	 * Verification of Comparison screen (components/attributes/values) of 2 renewal versions based on test data that was used during creation
	 * @param state state code
	 * @param tdVersion1 test data that is used for version1 renewal transaction
	 * @param tdVersion2 test data that is used for version2 renewal transaction
	 * @param checkedComponents list of components/attributes that should be displayed on Comparison page for section
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	public void renewalComparison(String state, TestData tdVersion1, TestData tdVersion2, Multimap checkedComponents, String tabName, String sectionName) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalGenerationJob(expirationDate);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		policy.getDefaultView().fill(tdVersion1);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		policy.getDefaultView().fill(tdVersion2);
		PolicySummaryPage.buttonRenewalQuoteVersion.click();

		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(3).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompare.click();

		checkComparisonPage(tdVersion1, tdVersion2, checkedComponents, tabName, sectionName);
	}

	/**
	 * Mid-term endorsement transaction effectice date + 20 days
	 * @param td test data that is used for endorsement transaction
	 */
	public void processPlus20DaysEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Plus20Days"));
		policy.endorse().performAndFill(endorsementTD);
	}

	/**
	 * Mid-term endorsement transaction effectice date + 25 days
	 * @param td test data that is used for endorsement transaction
	 */
	public void processPlus25DaysEndorsement(TestData td) {
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
	 * Compare actual result for components/attributes/values from comparison screen with expected result taken from Test Data + predefinedExpectedValues (fields that are not in Test Data)
	 * @param tdVersion1 test data that is used for version1 transaction (data gather, endorsement or renewal)
	 * @param tdVersion2 test data that is used for version2 transaction (data gather, endorsement or renewal)
	 * @param expectedComponentsAttributes list of components/attributes that should be displayed on Comparison page for section
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	void checkComparisonPage(TestData tdVersion1, TestData tdVersion2, Multimap expectedComponentsAttributes, String tabName, String sectionName) {
		ArrayListMultimap<String, String> expectedAttributesValuesV1 = createExpectedResultFromTD(tdVersion1, tabName, sectionName);
		ArrayListMultimap<String, String> expectedAttributesValuesV2 = createExpectedResultFromTD(tdVersion2, tabName, sectionName);

		ArrayListMultimap<String, String> actualComponentsAttributes = ArrayListMultimap.create();
		ArrayListMultimap<String, String> actualAttributesValuesV1 = ArrayListMultimap.create();
		ArrayListMultimap<String, String> actualAttributesValuesV2 = ArrayListMultimap.create();
		fillActualResults(actualComponentsAttributes, actualAttributesValuesV1, actualAttributesValuesV2);
		verificationComparisonPage(expectedComponentsAttributes, expectedAttributesValuesV1, actualComponentsAttributes, actualAttributesValuesV1, 1); //version1
		verificationComparisonPage(expectedComponentsAttributes, expectedAttributesValuesV2, actualComponentsAttributes, actualAttributesValuesV2, 0); //version2
	}

	/**
	 * Creation of expected result for section based on Test Data, tab name and section name
	 * @param td test data name based on what we tok our expected results for attributes
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 * @return list of expected attributes and according values
	 */
	ArrayListMultimap<String, String> createExpectedResultFromTD(TestData td, String tabName, String sectionName) {
		ArrayListMultimap<String, String> expectedAttributesValues = ArrayListMultimap.create();
		List<TestData> testData = td.getTestData(tabName).getTestDataList(sectionName);
		TestData attributes = testData.isEmpty() ? td.getTestData(tabName) : testData.get(0);
		for (String key : attributes.getKeys()) {
			if (!excludedTDKeys.contains(key)) {
				String value = attributes.getValue(key);
				String valueOnComparisonPage = getComparisonPageDifferentValues().getOrDefault(value, value);
				expectedAttributesValues.put(key, valueOnComparisonPage);
			}
		}
		return expectedAttributesValues;
	}

	/**
	 * Verification of expected list of components/attributes and attributes/values
	 * @param expectedComponentsAttributes expected list of components(sections name)/attributes(fields in section)
	 * @param expectedAttributesValues expected list of attributes(fields in section)/values
	 * @param actualComponentsAttributes actual list of components(sections name)/attributes(fields in section)
	 * @param actualAttributesValues actual list of attributes(fields in section)/values
	 * @param version version that is under verification (current/available)
	 */
	private void verificationComparisonPage(Multimap<String, String> expectedComponentsAttributes, ArrayListMultimap<String, String> expectedAttributesValues,
			Multimap<String, String> actualComponentsAttributes, ArrayListMultimap<String, String> actualAttributesValues, int version) {
		assertSoftly(softly -> {
			softly.assertThat(actualComponentsAttributes.keySet().size()).isEqualTo(expectedComponentsAttributes.keySet().size());
			for (String componentName : expectedComponentsAttributes.keySet()) {
				softly.assertThat(actualComponentsAttributes.get(componentName)).isNotNull();
				softly.assertThat(actualComponentsAttributes.get(componentName)).isEqualTo(expectedComponentsAttributes.get(componentName));
			}
		});
		for (String attributePath : actualAttributesValues.keySet()) {
			List<String> attributes = getAttributesToTDMapping().get(attributePath);
			if (CollectionUtils.isEmpty(attributes)) {
				List<String> expectedValues = getPredefinedExpectedValues().get(attributePath);
				assertThat(expectedValues.isEmpty()).as("Attribute path {0} not found in TestData or predefined values.", attributePath).isFalse();
				// if no attributes prefilled from testdata then look into predefined
				String expectedValue = expectedValues.get(version);
				assertThat(expectedValue).isNotNull().as("Expected values for attribute path {0} not found in TestData or predefined values.", attributePath);
				// only unique attribute key is supported for now
				assertThat(actualAttributesValues.get(attributePath).get(0)).as("Problem in " + attributePath).isEqualTo(expectedValue);
			} else {
				for (int attrPos = 0; attrPos < attributes.size(); attrPos++) {
					String expectedValueFromTD = expectedAttributesValues.get(attributes.get(attrPos)).get(0);
					String expectedValue;
					if (expectedValueFromTD == null) {
						expectedValue = getPredefinedExpectedValues().get(attributePath).get(version);
					} else {
						expectedValue = expectedValueFromTD;
					}
					assertThat(actualAttributesValues.get(attributePath).get(attrPos)).as("Problem in " + attributePath).isEqualTo(expectedValue);
				}
			}
		}
	}

	/**
	 * Creation of actual result  (components/attributes/values) from Comparison screen
	 * @param actualComponentsAttributes actual list of components/attributes
	 * @param actualAttributesValuesV1 actual list of attributes/values for version1
	 * @param actualAttributesValuesV2 actual list of attributes/values for version2
	 */
	private void fillActualResults(Multimap<String, String> actualComponentsAttributes, ArrayListMultimap<String, String> actualAttributesValuesV1,
			ArrayListMultimap<String, String> actualAttributesValuesV2) {
		for (int componentNumber = 0; ; componentNumber++) {
			StaticElement componentText = PolicySummaryPage.TransactionHistory.provideLinkTextComparisonTree(componentNumber);
			if (componentText.isPresent()) {
				String componentName = componentText.getValue();
				List<String> attributes = parseAttributesForComponent(componentNumber);
				actualComponentsAttributes.putAll(componentName, attributes);
				actualAttributesValuesV1.putAll(parseAttributesValuesForComponent(componentNumber, componentName, attributes, 3));
				actualAttributesValuesV2.putAll(parseAttributesValuesForComponent(componentNumber, componentName, attributes, 2));
			} else {
				break;
			}
		}
	}

	/**
	 * Getting actual list of values for component/attribute
	 * @param componentNumber number of component
	 * @param componentName name of component
	 * @param attributes list of attributes belong to component
	 * @param column version that is checked (current/available)
	 * @return values for component/attribute
	 */
	private Multimap<String, String> parseAttributesValuesForComponent(int componentNumber, String componentName, List<String> attributes, int column) {
		Multimap<String, String> comparisonValues = ArrayListMultimap.create();
		for (int attributeNumber = 0; attributeNumber < attributes.size(); attributeNumber++) {
			StaticElement columnValue = PolicySummaryPage.TransactionHistory.provideValueExpandComparisonTree(componentNumber, attributeNumber, column);
			if (columnValue.isPresent()) {
				comparisonValues.put(buildAttributePath(componentName, attributes.get(attributeNumber)), columnValue.getValue());
			} else {
				comparisonValues.put(buildAttributePath(componentName, attributes.get(attributeNumber)), StringUtils.EMPTY);
			}
		}
		return comparisonValues;
	}

	/**
	 * Getting actual list of attributes for a component
	 * @param componentNumber number of component
	 * @return list of attributes
	 */
	private List<String> parseAttributesForComponent(int componentNumber) {
		List attributes = new ArrayList();
		PolicySummaryPage.TransactionHistory.provideLinkExpandComparisonTree(componentNumber).click();
		for (int attributeNumber = 0; ; attributeNumber++) {
			StaticElement attributeElement = PolicySummaryPage.TransactionHistory.provideAtributeExpandComparisonTree(componentNumber, attributeNumber);
			if (attributeElement.isPresent()) {
				attributes.add(attributeElement.getValue());
			} else {
				break;
			}
		}
		return attributes;
	}

	/**
	 * Creating component.attribute for storing value
	 * @param componentName component
	 * @param value attribute name
	 * @return component.attribute
	 */
	private String buildAttributePath(String componentName, String value) {
		return componentName + COMPONENT_ATTRIBUTE_SEPARATOR + value;
	}


	//Conflict functionality

	/**
	 * Verification of comparison screen after conflict resolution for oose and rolled on transactions,  endorsement and rolled on transaction. Verification content of Conflict screen.
	 * @param state state code
	 * @param tdVersion1 test data that is used for endorsement transaction
	 * @param tdVersion2 test data that is used for oose transaction
	 * @param conflictLinks what version(current/available) we chose on conflict page
	 * @param checkedComponentsOOSE expected list of components/attributes on comparison for oose and rolled on transactions
	 * @param checkedComponentsEndorsement expected list of components/attributes on comparison for endorsement and rolled on transactions
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 * @param isAutomatic what kind of conflict we are doing automatic = true, manual = false
	 */
	public void ooseConflict(String state, TestData tdVersion1, TestData tdVersion2,  ArrayListMultimap<String, String> conflictLinks, Multimap checkedComponentsOOSE,  Multimap checkedComponentsEndorsement, String tabName, String sectionName, Boolean isAutomatic ) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		processPlus20DaysEndorsement(tdVersion1);
		processPlus10DaysOOSEndorsement(tdVersion2);
		policy.rollOn().openConflictPage(isAutomatic);
		resolveConflict(conflictLinks);
		policy.rollOn().submit();


		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ROLLED_ON_ENORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.OOS_ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(3).getCell(2).verify.value(ProductConstants.TransactionHistoryType.BACKED_OFF_ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(4).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ISSUE);

		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompareVersions.click();
		checkComparisonPage(tdVersion2, tdVersion1, checkedComponentsOOSE, tabName, sectionName);
		Tab.buttonCancel.click();

		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(false);
		PolicySummaryPage.tableTransactionHistory.getRow(3).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompareVersions.click();
		checkComparisonPage(tdVersion1, tdVersion2, checkedComponentsEndorsement, tabName, sectionName);
		Tab.buttonCancel.click();
	}

	/**
	 * OOS endorsement transaction effective date + 10 days
	 * @param td test data that is used for endorsement transaction
	 */
	public void processPlus10DaysOOSEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Plus10Days"));
		policy.endorse().performAndFill(endorsementTD);
	}

	/**
	 * Verification of comparison screen after Renewal Merge for renewal and rolled on transactions. Verification content of Conflict screen during Renewal Merge.
	 * @param state state code
	 * @param tdVersion1 test data that is used for renewal transaction
	 * @param tdVersion2 test data that is used for oose transaction
	 * @param conflictLinks what version(current/available) we chose on conflict page
	 * @param checkedComponentsRenewal expected list of components/attributes on comparison for renewal and rolled on transactions
	 * @param tabName name of tab where section is located
	 * @param sectionName section name that is under verification
	 */
	public void renewalMerge(String state, TestData tdVersion1, TestData tdVersion2,  ArrayListMultimap<String, String> conflictLinks, Multimap checkedComponentsRenewal, String tabName, String sectionName) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalGenerationJob(expirationDate);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		policy.getDefaultView().fill(tdVersion1);
		processMinus1MonthEndorsement(tdVersion2);
		//Todo solve this issue with error for effective date
		if (errorTab.isVisible()) {
			errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_200011);
			errorTab.submitTab();
		}

		resolveConflict(conflictLinks);
		policy.rollOn().submit();
		PolicySummaryPage.buttonRenewalQuoteVersion.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(3).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);

		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompare.click();
		checkComparisonPage(tdVersion2, tdVersion1, checkedComponentsRenewal, tabName, sectionName);
		Tab.buttonCancel.click();
	}

	/**
	 * OOS endorsement transaction current date date - 1 month
	 * @param td test data that is used for endorsement transaction
	 */
	public void processMinus1MonthEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Minus1Month"));
		policy.endorse().performAndFill(endorsementTD);
	}

	/**
	 * For each attribute we select current or available version
	 * @param conflictLinks what version(current/available) we chose on conflict page
	 */
	private void resolveConflict(ArrayListMultimap<String, String> conflictLinks) {
		Multiset<String> attributesPath = conflictLinks.keys();
		List<String> presentedComponentOnConflictPage = new ArrayList<>();
		for (int componentNumber = 0; ; componentNumber++) {
			StaticElement componentText = PolicySummaryPage.TransactionHistory.provideLinkTextComparisonTree(componentNumber);
			if (componentText.isPresent()) {
				String componentName = componentText.getValue();
				if (attributesPath.stream().anyMatch(attributePath -> attributePath.startsWith(componentName))) {
					selectAttributesVersioins(componentName, componentNumber, conflictLinks);
				}
				presentedComponentOnConflictPage.add(componentName);
			} else {
				break;
			}
		}
		allComponentPresentedOnConflictPage(presentedComponentOnConflictPage, conflictLinks.keySet());
	}

	/**
	 * Verify that all components are present
	 * @param presentedComponentOnConflictPage actual list of components
	 * @param attributesPaths expected list of components
	 */
	private void allComponentPresentedOnConflictPage(List<String> presentedComponentOnConflictPage, Set<String> attributesPaths) {
		assertSoftly(softly -> {
			for (String componentName : presentedComponentOnConflictPage) {
				softly.assertThat(attributesPaths.stream()
						.anyMatch(attributePath -> attributePath.startsWith(buildAttributePath(componentName, StringUtils.EMPTY))))
						.as("Component " + componentName + " not present in attributes configuration.")
						.isTrue();
			}
		});
	}

	/**
	 * Resolve all conflicts based on current or available selection
	 * @param componentName name of the component
	 * @param componentNumber number of the components on conflict page
	 * @param conflictLinks what version(current/available) we chose on conflict page
	 */
	private void selectAttributesVersioins(String componentName, int componentNumber, ArrayListMultimap<String, String> conflictLinks) {
		PolicySummaryPage.TransactionHistory.provideLinkExpandComparisonTree(componentNumber).click();
		List<String> attributesPathList = conflictLinks.keys().stream()
				.filter(attributePath -> attributePath.startsWith(componentName + COMPONENT_ATTRIBUTE_SEPARATOR))
				.collect(Collectors.toList());
		long expectedResolvedAttributeConflicts = conflictLinks.keys().stream()
				.filter(path -> path.startsWith(componentName))
				.count();
		int actualResolvedAttributeConflicts = 0;
		Table tableDifferences = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));
		if (tableDifferences.isPresent()) {
			int columnsCount = tableDifferences.getColumnsCount();

			for (int attributeNumber = 0; ; attributeNumber++) {
				StaticElement attributeElement = PolicySummaryPage.TransactionHistory.provideAtributeExpandComparisonTree(componentNumber, attributeNumber);
				if (attributeElement.isPresent()) {
					String attributePath = buildAttributePath(componentName, attributeElement.getValue());
					if (attributesPathList.contains(attributePath)) {
						List<String> versionsLinkValues = conflictLinks.get(attributePath);
						for (int attributeWithSameNameNumber = 0; attributeWithSameNameNumber < versionsLinkValues.size(); attributeWithSameNameNumber++) {
							String versionLinkValue = versionsLinkValues.get(attributeWithSameNameNumber);
							int attributePosition = attributeNumber + attributeWithSameNameNumber;
							if (pressVersionLink(tableDifferences, attributePosition, columnsCount, versionLinkValue, componentName)) {
								actualResolvedAttributeConflicts++;
							}
						}
					}
				} else {
					break;
				}
			}
		}
		assertThat(actualResolvedAttributeConflicts).as("Invalid resolved attributes number for {0}.", componentName).isEqualTo((int)expectedResolvedAttributeConflicts);

	}

	/**
	 * Select needed version (current or available)
	 * @param tableDifferences table for conflict selection
	 * @param attributeRow row for the attribute
	 * @param columnsCount last column for select version
	 * @param versionValue version value current/available
	 * @param componentName name of the component (section name)
	 * @return boolean value based on press or not link
	 */
	private boolean pressVersionLink(Table tableDifferences, int attributeRow, int columnsCount, String versionValue, String componentName) {
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
		int componentRowIndex = tableDifferences.getRow(COMPONENT_NAME_ROW_INDEX, componentName).getIndex();
		linkSetValue = tableDifferences.getRow(componentRowIndex + attributeRow + 1).getCell(columnsCount).controls.links.get(
				versionPosition);
		if (linkSetValue.isPresent() && linkSetValue.isVisible()) {
			linkSetValue.click();
			return true;
		}
		return false;
	}


}