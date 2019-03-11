package aaa.toolkit.webdriver.customcontrols;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.waiters.Waiter;

/**
 * Extended ComboBox with extra features like setting random values, etc.
 */
public class AdvancedComboBox extends ComboBox {
	public static final String RANDOM_MARK = "/random";
	public static final String SELECTED_MARK = "/selected";
	public static final String RANDOM_EXCEPT_MARK = RANDOM_MARK + "_except";
	public static final String RANDOM_EXCEPT_CONTAINS_MARK = RANDOM_EXCEPT_MARK + "_contains";
	public static final String RANDOM_EXCEPT_EMPTY = RANDOM_EXCEPT_MARK + "=|";
	private Random random = new Random();

	public AdvancedComboBox(By locator) {
		super(locator);
	}

	public AdvancedComboBox(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public AdvancedComboBox(BaseElement<?, ?> parent, By locator) {
		super(parent, locator);
	}

	public AdvancedComboBox(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	public void setAnyValueExcept(String... exceptValues) {
		setAnyValueExcept(true, exceptValues);
	}

	@Override
	protected void setRawValue(String value) {
		if (value.startsWith(RANDOM_EXCEPT_CONTAINS_MARK)) {
			setRawValueExcept(value, true);
		} else if (value.startsWith(RANDOM_EXCEPT_MARK)) {
			setRawValueExcept(value, false);
		} else if (value.equals(RANDOM_MARK)) {
			setAnyValue();
		} else {
			super.setRawValue(value);
		}
	}

	/**
	 * Set random option from list of existing options excluding selected one
	 *
	 */
	public void setAnyValue() {
		setAnyValueExcept(true);
	}

	public void setAnyValueExcept(boolean exceptCurrentValue, String... exceptValues) {
		setAnyValueExcept(exceptCurrentValue, false, exceptValues);
	}

	/**
	 * Set random option from list of existing options except selected one (if exceptCurrentValue = true) and except other values from exceptValues array
	 *
	 * @param exceptCurrentValue if true then exclude selected option from random selection
	 * @param exceptValuesByContains if true then excludes from list of possible values Strings which contains substings from exceptValues array,
	 *                                  otherwise Strings which equals to values from exceptValues will be excluded
	 * @param exceptValues  array of values to be excluded from random selection (including existing value if exceptCurrentValue = true)
	 *                      If this array is empty then nothing will be excluded (except current value if ceptCurrentValue = true)
	 */
	public void setAnyValueExcept(boolean exceptCurrentValue, boolean exceptValuesByContains, String... exceptValues) {
		List<String> optionsList = getAllValues();
		if (1 == optionsList.size()) {
			log.warn("Combobox {} has only one option, can't change to another one", this);
		} else {
			if (exceptCurrentValue) {
				optionsList.remove(getRawValue());
			}

			if (exceptValuesByContains) {
				optionsList.removeIf(v -> Arrays.stream(exceptValues).anyMatch(v::contains));
			} else {
				optionsList.removeIf(v -> Arrays.stream(exceptValues).anyMatch(v::equals));
			}

			assertThat(optionsList).as("Can't get random option for %s combobox - all available options were excluded", this).isNotEmpty();
			String randomValue = optionsList.get(random.nextInt(optionsList.size()));
			setValue(randomValue);
		}
	}

	private void setRawValueExcept(String value, boolean exceptValuesByContains) {
		String[] parsedValue = value.split("=");
		assertThat(parsedValue.length).as("'%s' should be followed with '=' and list of options separated with '|' to be excluded from random selection.", RANDOM_MARK).isEqualTo(2);
		String[] excludedValues = parsedValue[1].split("\\|", -1);
		if (Arrays.asList(excludedValues).contains(SELECTED_MARK)) {
			excludedValues = ArrayUtils.removeElement(excludedValues, SELECTED_MARK);
			setAnyValueExcept(true, exceptValuesByContains, excludedValues);
		} else {
			setAnyValueExcept(false, exceptValuesByContains, excludedValues);
		}
	}
}
