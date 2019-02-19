package toolkit.webdriver.controls;

import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.waiters.Waiter;


/**
 * Base class for clickable controls such as buttons or links (but not checkboxes!)
 */
abstract public class AbstractClickableStringElement extends AbstractNonEditableStringElement {
	protected AbstractClickableStringElement(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	protected AbstractClickableStringElement(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	@Override
	protected void setRawValue(Void value) {
		click();
	}

	/**
	 * "Fill" element by clicking it if provided TestData contains corresponding key (value is ignored)
	 */
	@Override
	public void fill(TestData td) {
		if (td.containsKey(name)) {
			click();
		}
	}

	@Override
	public void click() {
		super.click();
	}

	@Override
	public void click(Waiter waiter) {
		super.click(waiter);
	}

	@Override
	public void doubleClick() {
		super.doubleClick();
	}
}
