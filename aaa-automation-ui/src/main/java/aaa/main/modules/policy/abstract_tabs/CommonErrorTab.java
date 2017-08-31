package aaa.main.modules.policy.abstract_tabs;

import aaa.common.Tab;
import aaa.main.enums.ErrorEnum;
import aaa.toolkit.webdriver.customcontrols.FillableErrorTable;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lkazarnovskiy on 8/8/2017.
 */
public abstract class CommonErrorTab extends Tab {
	public Button buttonOverride = new Button(By.id("errorsForm:overrideRules"));
	public Button buttonApproval = new Button(By.id("errorsForm:referForApproval"));
	public Verify verify = new Verify();

	protected CommonErrorTab(Class<? extends MetaData> mdClass) {
		super(mdClass);
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

	public class Verify {

		public void errorsPresent(String... errorsMessages) {
			errorsPresent(true, errorsMessages);
		}

		public void errorsPresent(boolean expectedValue, String... errorsMessages) {
			final int maxMessageLengthInTableWithoutDots = 77;

			List<String> actualMessagesList = getErrorsControl().getTable().getColumn(ErrorEnum.ErrorsColumn.MESSAGE.get()).getValue();
			actualMessagesList.replaceAll(actualMessage -> StringUtils.removeEnd(actualMessage, "...").trim());

			for (String expectedMessage : errorsMessages) {
				String assertionMessage = String.format("Error message \"%1$s\" is not %2$s as expected.", expectedMessage, expectedValue ? "present" : "absent");
				final String expectedTruncatedMessage = StringUtils.removeEnd(expectedMessage, "...").trim();

				// check error exists
				if (expectedValue) {
					if (expectedTruncatedMessage.length() > maxMessageLengthInTableWithoutDots) {
						CustomAssert.assertTrue(assertionMessage, actualMessagesList.stream().anyMatch(expectedTruncatedMessage::startsWith));

						//check with full hint message
						String messageInRow = actualMessagesList.stream().filter(actualMessage -> expectedTruncatedMessage.equals(actualMessage) || expectedTruncatedMessage.startsWith(actualMessage)).findFirst().get();
						Row errorRow = getErrorsControl().getTable().getRow(actualMessagesList.indexOf(messageInRow) + 1);
						WebElement actualFullMessageElement = BrowserController.get().driver().findElement(new ByChained(getErrorsControl().getTable().getLocator(), errorRow.getLocator(), By.xpath(".//div[contains(@id, 'content')]")));
						CustomAssert.assertEquals(assertionMessage, expectedMessage, actualFullMessageElement.getAttribute("innerText"));
					} else {
						CustomAssert.assertTrue(assertionMessage, actualMessagesList.stream().anyMatch(expectedTruncatedMessage::equals));
					}
				// check error does not exist
				} else {
					if (expectedTruncatedMessage.length() > maxMessageLengthInTableWithoutDots) {
						CustomAssert.assertTrue(assertionMessage, actualMessagesList.stream().noneMatch(expectedTruncatedMessage::startsWith));
					} else {
						CustomAssert.assertTrue(assertionMessage, actualMessagesList.stream().noneMatch(expectedTruncatedMessage::equals));
					}
				}
			}
		}

		public void errorsPresent(ErrorEnum.Errors... errors) {
			errorsPresent(true, errors);
		}

		public void errorsPresent(boolean expectedValue, ErrorEnum.Errors... errors) {
			List<String> actualErrorCodesList = getErrorsControl().getTable().getColumn(ErrorEnum.ErrorsColumn.CODE.get()).getValue();

			for (ErrorEnum.Errors error : errors) {
				if (expectedValue) {
					CustomAssert.assertTrue(error + " is not present as expected.", actualErrorCodesList.contains(error.getCode()));
				} else {
					CustomAssert.assertFalse(error + " is not absent as expected.", actualErrorCodesList.contains(error.getCode()));
				}
			}

			List<String> errorMessagesList = new ArrayList<>(errors.length);
			Arrays.stream(errors).forEach(e -> errorMessagesList.add(e.getMessage()));
			errorsPresent(expectedValue, errorMessagesList.toArray(new String[errorMessagesList.size()]));
		}
	}
}

