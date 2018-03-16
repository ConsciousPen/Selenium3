package toolkit.webdriver.controls;

import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.utils.meters.WaitMeters;
import toolkit.webdriver.ElementHighlighter;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Base class for clickable controls such as buttons or links (but not checkboxes!)
 */
public abstract class AbstractClickableStringElement extends AbstractNonEditableStringElement {
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
