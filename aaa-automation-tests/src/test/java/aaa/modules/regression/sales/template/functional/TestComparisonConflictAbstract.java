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

	protected abstract ArrayListMultimap<String, String> getPredefinedExpectedValues();

	protected abstract Tab getGeneralTab();

	protected abstract Tab getDocumentsAndBindTab();

	protected abstract void navigateToGeneralTab();

	protected abstract TestData getTdPolicy();


	//Comparison functionality

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

	public void processPlus20DaysEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Plus20Days"));
		policy.endorse().performAndFill(endorsementTD);
	}

	public void processPlus25DaysEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Plus25Days"));
		policy.endorse().performAndFill(endorsementTD);
	}

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
					String expectedValueFromTD = expectedAttributesValues.get(attributes.get(attrPos)).get(0); //TODO expecteedAV -> Map
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

	private String buildAttributePath(String componentName, String value) {
		return componentName + COMPONENT_ATTRIBUTE_SEPARATOR + value;
	}


	//Conflict functionality

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


	public void processPlus10DaysOOSEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Plus10Days"));
		policy.endorse().performAndFill(endorsementTD);
	}

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

	public void processMinus1MonthEndorsement(TestData td) {
		TestData endorsementTD = td.adjust(getTestSpecificTD("TestData_Minus1Month"));
		policy.endorse().performAndFill(endorsementTD);
	}

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

/*
	protected abstract void calculatePremium();

	protected abstract void navigateToDocumentsAndBindTab();

	protected abstract void navigateToPremiumAndCoverageTab();

	public void issuedEndorsementComparison(String state, TestData tdVersion2) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Quote"));
		processPlus25DaysEndorsement(tdVersion2);

		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ISSUE);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompareVersions.click();

	}

	public void issuedRenewalComparison(String state, TestData tdVersion2) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Quote"));
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalGenerationJob(expirationDate);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		policy.getDefaultView().fill(tdVersion2);

		PolicySummaryPage.buttonRenewalQuoteVersion.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonComparePolicy.click();

	}

	public void dataGatherComparisonNoChanges(String state) {
		mainApp().open();
		createCustomerIndividual();
		createQuote(getTestSpecificTD("TestData_NB_Quote"));
		policy.dataGather().start();
		getGeneralTab().createVersion();
		navigateToPremiumAndCoverageTab();
		calculatePremium();
		navigateToDocumentsAndBindTab();
		getDocumentsAndBindTab().saveAndExit();
		PolicySummaryPage.buttonQuoteVersionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.QUOTE);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.QUOTE);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompare.click();

	}

	public void endorsementsComparisonNoChanges(String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
		TestData endorsementTD = getTestSpecificTD("TestData_Plus1Month");
		policy.endorse().perform(endorsementTD);
		navigateToPremiumAndCoverageTab();
		calculatePremium();
		navigateToDocumentsAndBindTab();
		getDocumentsAndBindTab().submitTab();
		endorsementTD = getTestSpecificTD("TestData_Plus2Month");
		policy.endorse().perform(endorsementTD);
		navigateToPremiumAndCoverageTab();
		calculatePremium();
		navigateToDocumentsAndBindTab();
		getDocumentsAndBindTab().saveAndExit();

		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(3).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ISSUE);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompareVersions.click();

	}

	public void renewalComparisonNoChanges(String state) {
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
		navigateToPremiumAndCoverageTab();
		calculatePremium();
		navigateToDocumentsAndBindTab();
		getDocumentsAndBindTab().saveAndExit();
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		navigateToPremiumAndCoverageTab();
		calculatePremium();
		navigateToDocumentsAndBindTab();
		getDocumentsAndBindTab().saveAndExit();
		PolicySummaryPage.buttonRenewalQuoteVersion.click();

		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(3).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompare.click();

	}

	public void issuedEndorsementComparisonNoChanges(String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Quote"));
		TestData endorsementTD = getTestSpecificTD("TestData_Plus1Month");
		policy.endorse().perform(endorsementTD);
		navigateToPremiumAndCoverageTab();
		calculatePremium();
		navigateToDocumentsAndBindTab();
		getDocumentsAndBindTab().saveAndExit();

		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ENDORSEMENT);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ISSUE);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonCompareVersions.click();

	}

	public void issuedRenewalComparisonNoChanges(String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Quote"));
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		processRenewalGenerationJob(expirationDate);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		navigateToPremiumAndCoverageTab();
		calculatePremium();
		navigateToDocumentsAndBindTab();
		getDocumentsAndBindTab().saveAndExit();

		PolicySummaryPage.buttonRenewalQuoteVersion.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.RENEWAL);
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).controls.checkBoxes.get(1).setValue(true);
		PolicySummaryPage.buttonComparePolicy.click();

	}


*//*

}*//*
	//TODO delete method
	public void createForComparison(String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getTestSpecificTD("TestData_NB_Policy"));
	}*/

}