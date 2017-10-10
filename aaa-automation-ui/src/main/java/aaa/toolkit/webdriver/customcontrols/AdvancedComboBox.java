package aaa.toolkit.webdriver.customcontrols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.mortbay.log.Log;
import org.openqa.selenium.By;
import toolkit.exceptions.IstfException;
import toolkit.utils.meters.WaitMeters;
import toolkit.verification.CustomAssert;
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

	@Override
	protected void setRawValue(String value) {
		if (value.startsWith(RANDOM_EXCEPT_MARK)) {
			String[] parsedValue = value.split("=");
			CustomAssert.assertEquals(String.format("'%s' should be followed with '=' and list of options separated with '|' to be excluded from random selection.", RANDOM_MARK), parsedValue.length, 2);
			String[] excludedValues = parsedValue[1].split("\\|");
			if (Arrays.asList(excludedValues).contains(SELECTED_MARK)) {
				excludedValues = ArrayUtils.removeElement(excludedValues, SELECTED_MARK);
				setAnyValueExcept(true, excludedValues);
			} else {
				setAnyValueExcept(false, excludedValues);
			}
			WaitMeters.capture(WaitMeters.PAGE_LOAD);
			waitForPageUpdate();
		} else if (value.startsWith(RANDOM_MARK)) {
			setAnyValue();
			WaitMeters.capture(WaitMeters.PAGE_LOAD);
			waitForPageUpdate();
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

	/**
	 * Set random option from list of existing options including selected one but excluding options from 'exceptValues' array
	 *
	 * @param exceptValues  array of values to be excluded from random selection. If this array is empty then nothing will be excluded
	 */
	public void setAnyValueExcept(String... exceptValues) {
		setAnyValueExcept(false, exceptValues);
	}

	/**
	 * Set random option from list of existing options except selected one (if exceptCurrentValue = true) and except other values from exceptValues array
	 *
	 * @param exceptCurrentValue if true then exclude selected option from random selection
	 * @param exceptValues  array of values to be excluded from random selection (including existing value if exceptCurrentValue = true).
	 *                      If this array is empty then nothing will be excluded (except current value if ceptCurrentValue = true)
	 */
	public void setAnyValueExcept(boolean exceptCurrentValue, String... exceptValues) {
		String[] excludedValues;

		if (exceptCurrentValue) {
			excludedValues = Arrays.copyOf(exceptValues, exceptValues.length + 1);
			excludedValues[excludedValues.length - 1] = getRawValue();
		} else {
			excludedValues = Arrays.copyOf(exceptValues, exceptValues.length);
		}

		List<String> optionsArray = getAllValues();
		if (1 == optionsArray.size()) {
			Log.warn("Combobox has only one option, can't change to another one");
		} else {
			List<String> optionsList = new ArrayList<>(optionsArray);
			optionsList.removeAll(Arrays.asList(excludedValues));
			CustomAssert.assertFalse("Can't get random option - all available options were excluded.", optionsList.isEmpty());
			String randomValue = optionsList.get(random.nextInt(optionsList.size()));
			setValue(randomValue);
		}
	}

	/**
	 * Sets random value to the ComboBox.
	 *
	 * List of random values selects from list of comboBox values
	 * And will not include current value if exceptCurrentValue is true
	 * Additionally excludes from list of possible values Strings which contains substings from exceptSubstrings array
	 *
	 *
	 * @param exceptCurrentValue if true then exclude selected option from random selection
	 * @param exceptSubstrings   parts of strings which should be excluded
	 */
	public void setAnyValueExceptContains(boolean exceptCurrentValue, String... exceptSubstrings) {
		List<String> allValues = getAllValues();
		if (exceptCurrentValue) allValues.remove(getRawValue());

		allValues.removeIf(v -> Arrays.stream(exceptSubstrings).anyMatch(v::contains));

		if (allValues.isEmpty()) {
			throw new IstfException(name + " " + this.getClass().getSimpleName() + " " +
					"doesn't contains values which not contains " + exceptCurrentValue);
		}

		setValue(allValues.get(random.nextInt(allValues.size())));
	}


	/**
	 * Overload {@link aaa.toolkit.webdriver.customcontrols.AdvancedComboBox#setAnyValueExceptContains(boolean, java.lang.String...)}
	 * with excluding current value from list of possible values
	 *
	 *  @param exceptSubstrings   parts of strings which should be excluded
	 */
	public void setAnyValueExceptContains(String... exceptSubstrings) {
		setAnyValueExceptContains(true,exceptSubstrings);
	}
}
