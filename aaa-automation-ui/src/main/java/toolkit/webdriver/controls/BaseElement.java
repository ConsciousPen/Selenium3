package toolkit.webdriver.controls;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.meters.WaitMeters;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.ElementHighlighter;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Abstract implementation of basic UI element
 * @param <I> class of the value this element consumes as input
 * @param <O> class of the value this element produces as output
 */
public abstract class BaseElement<I, O> implements Named {
	protected static final Logger log = LoggerFactory.getLogger(BaseElement.class);
	protected static final int WAIT_TIMEOUT = PropertyProvider.getProperty(TestProperties.WEBDRIVER_TIMEOUT, 10000);
	private static final Boolean IS_ENSURE_VISIBLE = PropertyProvider.getProperty(TestProperties.WEBDRIVER_ELEMENT_ENSURE_VISIBLE, true);
	private static final boolean RENDER_LOCATORS = PropertyProvider.getProperty(TestProperties.WEBDRIVER_ELEMENT_RENDER_LOCATORS, true);
	@Deprecated
	public final Verify verify = this.new Verify();
	protected BaseElement<?, ?> parent;
	protected By locator;
	protected Waiter waiter;
	protected String name;
	/*	needed to delegate certain methods to parent assetlist. usually but not necessarily (in case of logical containers) equal to parent element */
	protected Optional<AbstractContainer<?, ?>> container = Optional.empty();

	/**
	 * Create element without a parent
	 * @param locator element locator
	 * @param waiter object describing wait mode and timeout
	 */
	protected BaseElement(By locator, Waiter waiter) {
		this(null, locator, waiter);
	}

	/**
	 * Create element with a parent
	 * @param parent parent of the current element
	 * @param locator element locator
	 * @param waiter object describing wait mode and timeout
	 */
	protected BaseElement(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		this.parent = parent;
		this.locator = locator;
		this.waiter = waiter;
	}

	/**
	 * Get underlying WebElement instance
	 * @return WebElement instance
	 */
	public WebElement getWebElement() {
		return BrowserController.get().getElement(this);
	}

	/**
	 * Get locator of the current element
	 * @return locator
	 */
	public By getLocator() {
		return locator;
	}

	/**
	 * Get parent of the current element
	 * @return parent element or null if there is no parent
	 */
	public BaseElement<?, ?> getParent() {
		return parent;
	}

	/**
	 * Check if element is present and visible
	 * @return true if visible, false if invisible or not present
	 */
	public boolean isPresent() {
		try {
			return getWebElement().isDisplayed();
		} catch (WebDriverException e) {
			return false;
		}
	}

	/**
	 * Check if element is visible. Same as {@link #isPresent()} but throws exception if element is not present
	 * @return true if visible, false if invisible
	 */
	public boolean isVisible() {
		return getWebElement().isDisplayed();
	}

	/**
	 * Check if element is enabled
	 * @return element's enabled status
	 */
	public boolean isEnabled() {
		return getWebElement().isEnabled();
	}

	/**
	 * Get element's value and handle errors
	 * @return value element value
	 * @throws IstfException with standardized message
	 */
	public O getValue() {
		ElementHighlighter.highlight(this);
		try {
			return getRawValue();
		} catch (Exception e) {
			throw new IstfException(String.format("Cannot get value of %1$s %2$s",
					this.getClass().getSimpleName(), this), e);
		}
	}

	/**
	 * Set element's value and handle errors
	 * @param value new value
	 * @throws IstfException with standardized message
	 */
	public void setValue(I value) {
		log.debug("Setting value '" + value + "' in the control " + this);
		ElementHighlighter.highlight(this);
		try {
			setRawValue(value);
		} catch (Exception e) {
			throw new IstfException(buildSetValueErrorMessage(value), e);
		}
	}

	/**
	 * Check if asset is required (always false for non-asset elements)
	 * @return true if required
	 */
	public boolean isRequired() {
		return container.map(c -> c.isAssetRequired(this.getName())).orElse(false);
	}

	/**
	 * Get asset's warning (always empty for non-asset elements)
	 * @return optional of asset's warning
	 */
	public Optional<String> getWarning() {
		return container.flatMap(c -> {
			BaseElement<?, String> we = c.getWarning(this.getName());
			try {
				return Optional.ofNullable(we.getValue());
			} catch (Exception e) {
				return Optional.empty();
			}
		});
	}

	/**
	 * Set element's container (if it is an asset in an asset list)
	 * @param container asset list
	 */
	public void setContainer(AbstractContainer<?, ?> container) {
		this.container = Optional.ofNullable(container);
	}

	/**
	 * Get element's value (without error handling)
	 * @return value element value
	 */
	protected abstract O getRawValue();

	/**
	 * Set element's value (without error handling)
	 * @param value new value
	 */
	protected abstract void setRawValue(I value);

	@Override
	public String toString() {
		String locStr = locator.toString();
		String nm;
		if (RENDER_LOCATORS) {
			nm = name == null ? "<unnamed>" : name;
			return "{" + nm + ": " + (parent == null ? locStr : parent + " -> " + locStr) + "}";
		} else {
			nm = name == null ? locStr : name;
			return "{" + nm + "}";
		}
	}

	/**
	 * Get element's name
	 * @return element's name or null if there is none
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set element's name (to be used to get values from test data)
	 * @param name element's name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Wait for page update using current element's wait mode and timeout
	 */
	public void waitForPageUpdate() {
		waiter.go();
	}

	/**
	 * Wait for default timeout for predicate to evaluate to true
	 * @param predicate predicate to wait for
	 */
	public void waitFor(Predicate<? super BaseElement<?, ?>> predicate) {
		waitFor(WAIT_TIMEOUT, predicate);
	}

	/**
	 * Wait for specified timeout (in ms) for predicate to evaluate to true
	 * @param timeout wait timeout
	 * @param predicate predicate predicate to wait for
	 */
	public void waitFor(int timeout, Predicate<? super BaseElement<?, ?>> predicate) {
		new FluentWait<BaseElement<?, ?>>(this)
				.withTimeout(timeout, TimeUnit.MILLISECONDS)
				.pollingEvery(1, TimeUnit.SECONDS)
				.ignoring(NotFoundException.class)
				.until(be -> predicate.test(be));
	}

	/**
	 * Wait for this element to become present and visible
	 * @param timeout  wait timeout
	 */
	public void waitForAccessible(int timeout) {
		waitForAccessible(timeout, true);
	}

	/**
	 * Wait for this element's presence and visibility status to become as specified
	 * @param timeout  wait timeout
	 * @param status true for present/visible, false for absent/invisible
	 */
	public void waitForAccessible(int timeout, boolean status) {
		waitFor(timeout, e -> e.isPresent() == status);
	}

	/**
	 * Get element's output test data type
	 * @return test data type
	 */
	public abstract TestData.Type testDataType();

	/**
	 * Set element's value with overridden wait mode
	 * @param value new value
	 * @param waiter waiter to use after setting value
	 */
	public void setValue(I value, Waiter waiter) {
		//	TODO make thread-safe
		Waiter saveWaiter = this.waiter;
		this.waiter = waiter;
		setValue(value);
		this.waiter = saveWaiter;
	}

	/**
	 * Get element's partial value according to template. Should return full value for non-composite controls.
	 * This method is used internally by the framework and normally should not be called by the client code.
	 * @param template value template
	 * @return element's partial value
	 */
	public O getPartialValue(Object template) {
		return getValue();
	}

	/**
	 * Fill element using element's name as a key from provided TestData
	 * @param td TestData object that should contain appropriate value of type &lt;T&gt; under the appropriate key
	 */
	public abstract void fill(TestData td);

	/**
	 * Get element's attribute
	 * @param attrName attribute name
	 * @return attribute value
	 */
	public String getAttribute(String attrName) {
		return getWebElement().getAttribute(attrName);
	}

	/**
	 * Hover mouse cursor over the element
	 */
	public void mouseOver() {
		Actions action = new Actions(BrowserController.get().driver());
		action.moveToElement(getWebElement()).build().perform();
	}

	/**
	 * Clear element
	 */
	public void clear() {
		getWebElement().clear();
	}

	/**
	 * Method to transform raw value (usually received from test data) to element's output value.
	 * @param rawValue raw value
	 * @return transformed value
	 */
	protected abstract O normalize(Object rawValue);

	/**
	 * Build error message for setValue() method. Can be overridden in subclasses to include more information (e.g. available values).
	 * @param value element value for which error has occurred
	 * @return error message to be used in the thrown exception
	 */
	protected String buildSetValueErrorMessage(I value) {
		return String.format("Cannot set value of %1$s %2$s to '%3$s'", this.getClass().getSimpleName(), this, value);
	}

	/**
	 * Ensure the element is visible prior to interaction
	 */
	protected void ensureVisible() {
		if (IS_ENSURE_VISIBLE) {
			log.info("Element location: " + getWebElement().getLocation());
			BrowserController.get().executeScript("arguments[0].scrollIntoView();", getWebElement());
		}
	}

	/**
	 * Click the element
	 */
	protected void click() {
		log.debug("Clicking control " + this);
		ensureVisible();
		Waiters.SLEEP(300).go();
		ElementHighlighter.highlight(this);
		getWebElement().click();
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waitForPageUpdate();
	}

	/**
	 * Click the element with overridden wait mode
	 * @param waiter waiter to use after click
	 */
	protected void click(Waiter waiter) {
		log.debug("Clicking control " + this);
		ElementHighlighter.highlight(this);
		ensureVisible();
		getWebElement().click();
		WaitMeters.capture(WaitMeters.PAGE_LOAD);
		waiter.go();
	}

	/**
	 * Verifier class for BaseElement
	 */
	@Deprecated
	public class Verify {
		private int sleepTimeout = Integer.parseInt(PropertyProvider.getProperty(TestProperties.WEBDRIVER_SLEEP_TIMEOUT, "5000"));

		/**
		 * Verify that element's value is as expected
		 * @param expectedValue expected element's value
		 */
		public void value(O expectedValue) {
			CustomAssert.assertEquals("Value in control " + BaseElement.this, expectedValue, getValue());
		}

		/**
		 * Verify that element's value is as expected
		 * @param message custom message that will be logged in case of verification fail
		 * @param expectedValue value to be expected as correct
		 */
		public void value(String message, O expectedValue) {
			CustomAssert.assertEquals(message, expectedValue, getValue());
		}

		/**
		 * Verify that element is present
		 */
		public void present() {
			present(true);
		}

		/**
		 * Verify that element is present
		 * @param message custom message that will be logged in case of verification fail
		 */
		public void present(String message) {
			present(message, true);
		}

		/**
		 * Verify that element is present or not based on <b>expectedValue</b> parameter
		 * @param expectedValue true in case we going to check presence, false - otherwise
		 */
		public void present(boolean expectedValue) {
			String message = String.format("Element %1$s is not %2$s as expected.", BaseElement.this, expectedValue ? "present" : "absent");
			present(message, expectedValue);
		}

		/**
		 * Verify that element is present or not based on <b>expectedValue</b> parameter
		 * @param expectedValue true in case we going to check presence, false - otherwise
		 * @param message custom message that will be logged in case of verification fail
		 */
		public void present(String message, boolean expectedValue) {
			try {
				BaseElement.this.waitFor(sleepTimeout, e -> e.isPresent() == expectedValue);
			} catch (TimeoutException te) {
				CustomAssert.assertTrue(message, false);
			}
		}

		/**
		 * Verify that element is enabled
		 * @param message custom message that will be logged in case of verification fail
		 */
		public void enabled(String message) {
			enabled(message, true);
		}

		/**
		 * Verify that element is enabled
		 */
		public void enabled() {
			enabled(true);
		}

		/**
		 * Verify that element is enabled or not
		 * @param expectedValue object state (enable/disable) to check
		 */
		public void enabled(boolean expectedValue) {
			String message = String.format("Element %1$s is not %2$s as expected.", BaseElement.this, expectedValue ? "enabled" : "disabled");
			enabled(message, expectedValue);
		}

		/**
		 * Verify that element is enabled or not
		 * @param message custom message to log
		 * @param expectedValue object state (enable/disable) to check
		 */
		public void enabled(String message, boolean expectedValue) {
			try {
				BaseElement.this.waitFor(sleepTimeout, e -> e.isEnabled() == expectedValue);
			} catch (TimeoutException te) {
				CustomAssert.assertTrue(message, false);
			}
		}
	}
}
