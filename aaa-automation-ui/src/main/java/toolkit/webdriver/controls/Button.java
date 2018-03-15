package toolkit.webdriver.controls;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import toolkit.utils.meters.WaitMeters;
import toolkit.webdriver.ElementHighlighter;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Standard button implementation.
 */
public class Button extends AbstractClickableStringElement implements HighlightableElement {
	public Button(By locator) {
		super(locator, Waiters.DEFAULT);
	}

	public Button(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public Button(BaseElement<?, ?> parent, By locator) {
		super(parent, locator, Waiters.DEFAULT);
	}

	public Button(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	@Override
	protected String getRawValue() {
		WebElement el = getWebElement();
		String v = el.getText();
		if (v.isEmpty()) {
			v = el.getAttribute("value");
		}
		return v;
	}

	@Override
	public boolean isEnabled() {
		boolean enabled = super.isEnabled();
		if (enabled && !getWebElement().getTagName().equals("input")) {
			enabled = getWebElement().getAttribute("onclick") != null;
		}
		return enabled;
	}

	@Override
	public void click() {
		log.debug("Clicking control " + this);
		ElementHighlighter.highlight(this);
		ensureVisible();
		Waiters.SLEEP(500).go();
		getWebElement().click();
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waitForPageUpdate();
	}

	@Override
	public void click(Waiter waiter) {
		log.debug("Clicking control " + this);
		ElementHighlighter.highlight(this);
		ensureVisible();
		Waiters.SLEEP(500).go();
		getWebElement().click();
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waiter.go();
	}
}
