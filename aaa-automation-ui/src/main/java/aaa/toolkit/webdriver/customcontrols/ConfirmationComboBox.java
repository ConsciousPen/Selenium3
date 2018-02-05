package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import aaa.common.pages.Page;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.waiters.Waiter;

/**
 * Confirms any appeared pop-up after setting value
 */
public class ConfirmationComboBox extends AdvancedComboBox {

	public ConfirmationComboBox(By locator) {
		super(locator);
	}

	public ConfirmationComboBox(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public ConfirmationComboBox(BaseElement<?, ?> parent, By locator) {
		super(parent, locator);
	}

	public ConfirmationComboBox(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	@Override
	protected void setRawValue(String value) {
		super.setRawValue(value);
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
	}

	/**
	 * Set random option from list of existing options except selected one (if exceptCurrentValue = true) and except other values from exceptValues array.
	 * After that confirms any pop-up if it appears.
	 *
	 * @param exceptCurrentValue if true then exclude selected option from random selection
	 * @param exceptValues  array of values to be excluded from random selection (including existing value if exceptCurrentValue = true).
	 *                      If this array is empty then nothing will be excluded (except current value if ceptCurrentValue = true)
	 */
	@Override
	public void setAnyValueExcept(boolean exceptCurrentValue, String... exceptValues) {
		super.setAnyValueExcept(exceptCurrentValue, exceptValues);
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
	}

	/**
	 * Sets random value to the ComboBox and confirms any pop-up if it appears.
	 *
	 * List of random values selects from list of comboBox values
	 * And will not include current value if exceptCurrentValue is true
	 * Additionally excludes from list of possible values Strings which contains substings from exceptSubstrings array
	 *
	 *
	 * @param exceptCurrentValue if true then exclude selected option from random selection
	 * @param exceptSubstrings   parts of strings which should be excluded
	 */
	@Override
	public void setAnyValueExceptContains(boolean exceptCurrentValue, String... exceptSubstrings) {
		super.setAnyValueExcept(exceptCurrentValue, exceptSubstrings);
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
	}
}
