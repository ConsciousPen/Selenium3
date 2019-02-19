package toolkit.webdriver.controls;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.meters.WaitMeters;
import toolkit.webdriver.ElementHighlighter;
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
		log.debug("Clicking control " + this);
		ElementHighlighter.highlight(this);
		ensureVisible();
		try {
			//If works without this sleep - Delete this class
			//Waiters.SLEEP(500).go();
			getWebElement().click();
		} catch (TimeoutException te) {
			throw new IstfException(String.format("Page failed to reload in time after click on %1$s", this), te);
		}
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waiter.go();
	}

	@Override
	public void doubleClick() {
		super.doubleClick();
	}
}
