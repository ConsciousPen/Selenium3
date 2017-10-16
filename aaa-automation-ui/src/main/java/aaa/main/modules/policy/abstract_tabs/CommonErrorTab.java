package aaa.main.modules.policy.abstract_tabs;

import aaa.common.Tab;
import aaa.main.enums.ErrorEnum;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.toolkit.webdriver.customcontrols.FillableErrorTable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Row;

import java.util.*;

/**
 * Created by lkazarnovskiy on 8/8/2017.
 */
public abstract class CommonErrorTab extends Tab {
	public Button buttonOverride = new Button(By.id("errorsForm:overrideRules"));
	public Button buttonApproval = new Button(By.id("errorsForm:referForApproval"));
	public Verify verify = new Verify();

	protected static Logger log = LoggerFactory.getLogger(CommonErrorTab.class);

	protected CommonErrorTab(Class<? extends MetaData> mdClass) {
		super(mdClass);
	}

	public boolean isVisible() {
		if (buttonOverride.isVisible())
			return true;
		else
			return false;
	}

	public Tab cancel() {
		buttonCancel.click();
		return this;
	}

	public void overrideAllErrors() {
		overrideAllErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER);
	}

	public void overrideAllErrors(ErrorEnum.Duration duration, ErrorEnum.ReasonForOverride reason) {
		for (Row row : getErrorsControl().getTable().getRows()) {
			CheckBox checkBoxOverride = row.getCell("Override").controls.checkBoxes.getFirst();
			if (checkBoxOverride.isPresent() && checkBoxOverride.isEnabled()) {
				checkBoxOverride.setValue(true);
				row.getCell("Duration").controls.radioGroups.getFirst().setValue(duration.get());
				row.getCell("Reason for override").controls.comboBoxes.getFirst().setValue(reason.get());
			}
		}

		buttonOverride.click();
	}

	public void overrideErrors(ErrorEnum.Errors... errors) {
		overrideErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER, errors);
	}

	public void overrideErrors(ErrorEnum.Duration duration, ErrorEnum.ReasonForOverride reason, ErrorEnum.Errors... errors) {
		TestData td = DataProviderFactory.dataOf(
				"Override", "true",
				"Duration", duration.get(),
				"Reason for override", reason.get());

		for (ErrorEnum.Errors error : errors) {
			getErrorsControl().fillRow(td.adjust("Code", error.getCode()));
		}

		buttonOverride.click();
	}

	public void referForApprovals(ErrorEnum.Errors... errors) {
		referForApprovals(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER, errors);
	}

	public void referForApprovals(ErrorEnum.Duration duration, ErrorEnum.ReasonForOverride reason, ErrorEnum.Errors... errors) {
		TestData td = DataProviderFactory.dataOf(
				"Approval", "true",
				"Duration", duration.get(),
				"Reason for override", reason.get());

		for (ErrorEnum.Errors error : errors) {
			getErrorsControl().fillRow(td.adjust("Code", error.getCode()));
		}
		buttonApproval.click();
	}

	public abstract FillableErrorTable getErrorsControl();

	public List<String> getErrorCodesList() {
		return getErrorsControl().getTable().getColumn(ErrorEnum.ErrorsColumn.CODE.get()).getValue();
	}


	public List<String> getErrorMessagesList() {
		return getErrorMessagesList(false);
	}

	public List<String> getHintErrorMessagesList() {
		return getErrorMessagesList(true);
	}

	public Map<String, String> getErrorsMap() {
		return getErrorsMap(false);
	}

	public Map<String, String> getErrorsWithHintMessagesMap() {
		return getErrorsMap(true);
	}

	private List<String> getErrorMessagesList(boolean getHintMessages) {
		List<String> actualMessagesList = new ArrayList<>();
		if (getHintMessages) {
			for (Row row : getErrorsControl().getTable().getRows()) {
				ByChained hintLocator = new ByChained(getErrorsControl().getTable().getLocator(), row.getLocator(), By.xpath(".//div[contains(@id, 'content')]"));
				String hintMessage = WebDriverHelper.getInnerText(hintLocator);
				if (StringUtils.isBlank(hintMessage)) {
					log.warn(String.format("Unable to get hint message for error message with locator %s. Looks like you are using outdated browser version.", hintLocator));
					hintMessage = "";
				}
				actualMessagesList.add(hintMessage.trim());
			}
		} else {
			actualMessagesList = getErrorsControl().getTable().getColumn(ErrorEnum.ErrorsColumn.MESSAGE.get()).getValue();
		}
		return actualMessagesList;
	}

	private Map<String, String> getErrorsMap(boolean getHintMessages) {
		List<String> actualCodesList = getErrorCodesList();
		List<String> actualMessagesList = getErrorMessagesList(getHintMessages);
		Map<String, String> actualErrors = new LinkedHashMap<>(actualCodesList.size());
		for (int i = 0; i < actualCodesList.size(); i++) {
			actualErrors.put(actualCodesList.get(i), actualMessagesList.get(i));
		}
		return actualErrors;
	}

	private Map<String, Pair<String, String>> getErrorCodesAndMessagePairsMap(boolean returnHintSameAsInTable) {
		List<String> actualCodesList = getErrorCodesList();
		List<Pair<String, String>> tableAndHintErrorMessagePairs = getTableAndHintErrorMessagePairs(returnHintSameAsInTable);
		Map<String, Pair<String, String>> actualErrorCodesAndMessagePairsMap = new LinkedHashMap<>(actualCodesList.size());
		for (int i = 0; i < actualCodesList.size(); i++) {
			actualErrorCodesAndMessagePairsMap.put(actualCodesList.get(i), tableAndHintErrorMessagePairs.get(i));
		}
		return actualErrorCodesAndMessagePairsMap;
	}

	private List<Pair<String, String>> getTableAndHintErrorMessagePairs(boolean returnHintSameAsInTable) {
		List<String> actualMessagesList = getErrorMessagesList();
		List<String> actualHintMessagesList;
		if (returnHintSameAsInTable) {
			actualHintMessagesList = new ArrayList<>(actualMessagesList);
		} else {
			actualHintMessagesList = getHintErrorMessagesList();
		}

		List<Pair<String, String>> tableAndHintErrorMessagePairs = new ArrayList<>(actualMessagesList.size());
		for (int i = 0; i < actualMessagesList.size(); i++) {
			tableAndHintErrorMessagePairs.add(Pair.of(actualMessagesList.get(i), actualHintMessagesList.get(i)));
		}
		return tableAndHintErrorMessagePairs;
	}

	private boolean isMessagePresentInTableAndHintPopup(Pair<String, String> actualTableAndHintErrorMessagePairs, String expectedMessage) {
		return isMessagePresentInTableAndHintPopup(Collections.singletonList(actualTableAndHintErrorMessagePairs), expectedMessage);
	}

	private boolean isMessagePresentInTableAndHintPopup(List<Pair<String, String>> actualTableAndHintErrorMessagePairs, String expectedMessage) {
		final int maxMessageLengthInTableWithoutDots = 77;
		if (expectedMessage.length() > maxMessageLengthInTableWithoutDots) {
			String expectedTruncatedMessage = StringUtils.removeEnd(expectedMessage, "...").trim();
			List<Pair<String, String>> actualTruncatedTableAndHintErrorMessagePairs = new ArrayList<>(actualTableAndHintErrorMessagePairs.size());
			actualTableAndHintErrorMessagePairs.forEach(actualMessagePair -> actualTruncatedTableAndHintErrorMessagePairs.add(
					Pair.of(StringUtils.removeEnd(actualMessagePair.getKey(), "...").trim(), StringUtils.removeEnd(actualMessagePair.getValue(), "...").trim())));

			return actualTruncatedTableAndHintErrorMessagePairs.stream().anyMatch(actualMessagePair ->
					(expectedTruncatedMessage.equals(actualMessagePair.getKey()) || expectedTruncatedMessage.startsWith(actualMessagePair.getKey())) && actualMessagePair.getValue().startsWith(expectedTruncatedMessage));
		}

		return actualTableAndHintErrorMessagePairs.stream().anyMatch(actualMessagePair -> actualMessagePair.getKey().equals(expectedMessage) && actualMessagePair.getValue().equals(expectedMessage));
	}

	public class Verify {

		public void errorsPresent(String... errorsMessages) {
			errorsPresent(true, errorsMessages);
		}

		public void errorsPresent(boolean expectedValue, String... errorsMessages) {
			List<Pair<String, String>> tableAndHintErrorMessagePairs = getTableAndHintErrorMessagePairs(!expectedValue);
			for (String expectedMessage : errorsMessages) {
				String assertionMessage = String.format("Error message \"%1$s\" is not %2$s as expected.", expectedMessage, expectedValue ? "present" : "absent");
				CustomAssert.assertTrue(assertionMessage, isMessagePresentInTableAndHintPopup(tableAndHintErrorMessagePairs, expectedMessage) == expectedValue);
			}
		}

		public void errorsPresent(ErrorEnum.Errors... errors) {
			errorsPresent(true, errors);
		}

		public void errorsPresent(boolean expectedValue, ErrorEnum.Errors... errors) {
			Map<String, Pair<String, String>> actualErrorCodesAndMessagePairsMap = getErrorCodesAndMessagePairsMap(!expectedValue);
			for (ErrorEnum.Errors error : errors) {
				CustomAssert.assertTrue(String.format("%s is %s.", error, expectedValue ? "absent" : "present"),
						(actualErrorCodesAndMessagePairsMap.containsKey(error.getCode())
								&& isMessagePresentInTableAndHintPopup(actualErrorCodesAndMessagePairsMap.get(error.getCode()), error.getMessage())) == expectedValue);
			}
		}
	}
}

