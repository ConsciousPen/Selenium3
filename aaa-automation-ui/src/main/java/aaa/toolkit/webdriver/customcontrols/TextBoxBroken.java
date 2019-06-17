package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import toolkit.utils.meters.WaitMeters;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.waiters.Waiter;

public class TextBoxBroken extends TextBox {
	public TextBoxBroken(By locator) {
		super(locator);
	}

	public TextBoxBroken(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public TextBoxBroken(BaseElement<?, ?> parent, By locator) {
		super(parent, locator);
	}

	public TextBoxBroken(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	@Override
	protected void setRawValue(String value) {
		CharSequence seq = value.isEmpty() ? Keys.DELETE : value;
		getWebElement().sendKeys(Keys.chord(Keys.CONTROL, "a"));
		getWebElement().sendKeys(seq);
		getWebElement().click();
		getWebElement().sendKeys(Keys.TAB);
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waitForPageUpdate();
	}
}
