package toolkit.webdriver.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import toolkit.utils.meters.WaitMeters;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Standard listbox implementation
 */
public class ListBox extends AbstractStringListElement implements SelectableElement<String>, HighlightableElement {
	public ListBox(By locator) {
		super(locator, Waiters.DEFAULT);
	}

	public ListBox(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public ListBox(BaseElement<?, ?> parent, By locator) {
		super(parent, locator, Waiters.DEFAULT);
	}

	public ListBox(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	/**
	 * Select single value
	 * @param value new value
	 */
	public void setValue(String value) {
		setValue(Arrays.asList(value));
	}

	/**
	 * Select all available values except the provided one
	 * @param excludedValue value to exclude from selection
	 */
	public void setAllValuesBut(String excludedValue) {
		setAllValuesBut(Arrays.asList(excludedValue));
	}

	/**
	 * Select all available values except the provided ones
	 * @param excludedValues values to exclude from selection
	 */
	public void setAllValuesBut(List<String> excludedValues) {
		List<String> values = getAllValues();
		values.removeAll(excludedValues);
		setValue(values);
	}

	/**
	 * Construct and return underlying Select element
	 * @return Select element
	 */
	protected Select getSelect() {
		return new Select(getWebElement());
	}

	@Override
	protected List<String> getRawValue() {
		List<String> values = new ArrayList<>();
		for (WebElement option : getSelect().getAllSelectedOptions()) {
			values.add(option.getText().trim());
		}
		return values;
	}

	/**
	 * Select all values from the provided list.
	 * List may contain either literal values or select or strings in the format "index={n}" where {n} is the index of the option to select.
	 */
	@Override
	protected void setRawValue(List<String> values) {
		for (String value : values) {
			if (value.startsWith("index=")) {
				getSelect().selectByIndex(Integer.parseInt(value.replace("index=", "")));
			} else {
				getSelect().selectByVisibleText(value);
			}
			waitForPageUpdate();
		}
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
	}

	@Override
	protected String buildSetValueErrorMessage(List<String> value, Exception exception) {
		return String.format("Cannot set value of %1$s %2$s to '%3$s'. Available values: %4$s", this.getClass().getSimpleName(), this, value, getAllValues());
	}

	/**
	 * Get all available values
	 * @return list of all options in the listbox
	 */
	@Override
	public List<String> getAllValues() {
		List<String> values = new ArrayList<>();
		for (WebElement option : getSelect().getOptions()) {
			values.add(option.getText().trim());
		}
		return values;
	}

	/**
	 * Select all available values
	 */
	public void setAllValues() {
		setAllValuesBut(new ArrayList<String>());
	}

	/**
	 * Deselect specified value
	 * @param value value to deselect
	 */
	public void unsetValue(String value) {
		unsetValues(Arrays.asList(value));
	}

	/**
	 * Deselect all values
	 */
	public void unsetAllValues() {
		getSelect().deselectAll();
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waitForPageUpdate();
	}

	/**
	 * Deselect specified values
	 * @param values values to deselect
	 */
	public void unsetValues(List<String> values) {
		for (String value : values) {
			getSelect().deselectByVisibleText(value);
		}
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waitForPageUpdate();
	}
}
