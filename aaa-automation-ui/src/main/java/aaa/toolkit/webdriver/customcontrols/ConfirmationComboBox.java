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
}
