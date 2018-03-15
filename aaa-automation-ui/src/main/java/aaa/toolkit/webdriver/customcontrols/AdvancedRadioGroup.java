package aaa.toolkit.webdriver.customcontrols;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.regex.Pattern;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.waiters.Waiter;

/**
 * Extended RadioGroup with extra features like setting value by starts, contains, regex, etc.
 */
public class AdvancedRadioGroup extends RadioGroup {
	public AdvancedRadioGroup(By locator) {
		super(locator);
	}

	public AdvancedRadioGroup(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public AdvancedRadioGroup(BaseElement<?, ?> parent, By locator) {
		super(parent, locator);
	}

	public AdvancedRadioGroup(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	/**
	 * Select value by index in radio group
	 * @param index in radio group to be selected
	 */
	public void setValueByIndex(int index) {
		//TODO-dchubkov: implement setting value by index
		throw new NotImplementedException("Setting radio group value by index is not implemented yet");
	}

	/**
	 * Select value that contains provided substring
	 * @param partOfValue part of value to select
	 */
	public void setValueContains(String partOfValue) {
		setValueByRegex(".*" + Pattern.quote(partOfValue) + ".*");
	}

	/**
	 * Select value that starts with provided substring
	 * @param partOfValue part of value to select
	 */
	public void setValueStarts(String partOfValue) {
		setValueByRegex("^" + Pattern.quote(partOfValue) + ".*");
	}

	/**
	 * Select value that matches provided regular expression
	 * @param regex regex to match desired value
	 */
	public void setValueByRegex(String regex) {
		if (!getValue().matches(regex)) {
			for (String value : getAllValues()) {
				if (value.matches(regex)) {
					setValue(value);
					return;
				}
			}
			throw new IstfException(String.format("Radio group %1$s does not contain value that matches %2$s", this, regex));
		}
	}

	@Override
	protected void setRawValue(String value) {
		//TODO-dchubkov: add selection of random value (with/without current of list of excluded values) similar as it was implemented in AdvancedComboBox

		String[] parsedValue = value.split("=");
		assertThat(parsedValue.length).as("Cannot parse value %1$s for radio group %2$s", value, this).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(2);

		if (parsedValue.length == 1) {
			super.setRawValue(value);
		} else {
			switch (parsedValue[0].toLowerCase()) {
				case "index":
					setValueByIndex(Integer.parseInt(parsedValue[1]));
					break;
				case "contains":
					setValueContains(parsedValue[1]);
					break;
				case "starts":
					setValueStarts(parsedValue[1]);
					break;
				case "regex":
					setValueByRegex(parsedValue[1]);
					break;
				default:
					super.setRawValue(value);
			}
		}
	}
}
