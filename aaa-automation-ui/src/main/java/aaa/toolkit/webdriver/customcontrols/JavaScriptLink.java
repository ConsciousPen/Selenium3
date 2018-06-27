package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.utils.meters.WaitMeters;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.ElementHighlighter;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

public class JavaScriptLink extends Link {

	public JavaScriptLink(By locator) {
		super(locator, Waiters.DEFAULT);
	}

	public JavaScriptLink(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public JavaScriptLink(BaseElement<?, ?> parent, By locator) {
		super(parent, locator, Waiters.DEFAULT);
	}

	public JavaScriptLink(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	@Override
	public void click() {
		log.debug("Clicking control " + this);
		ensureVisible();
		ElementHighlighter.highlight(this);
		BrowserController.get().executeScript("arguments[0].click();", getWebElement());
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waitForPageUpdate();
	}

	@Override
	public void click(Waiter waiter) {
		log.debug("Clicking control " + this);
		ensureVisible();
		ElementHighlighter.highlight(this);
		BrowserController.get().executeScript("arguments[0].click();", getWebElement());
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waiter.go();
	}
}
