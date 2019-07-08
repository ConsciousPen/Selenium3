package aaa.toolkit.webdriver.customcontrols;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Quotes;
import toolkit.config.ClassConfigurator;
import toolkit.utils.meters.WaitMeters;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * ComboBox where options are shown after clicking on it.
 * {@code valueListOpenerLocator} - locator of element need to be clicked to show available options.
 * {@code valueListXpath} - locator of option.
 *
 */
public class ClickComboBoxBroken extends ComboBox {
	@ClassConfigurator.Configurable
	private static By valueListOpenerLocator = By.xpath(".//div/label[contains(@class, 'ui-selectonemenu-label')]");
	@ClassConfigurator.Configurable
	private static String valueListXpath = "//div[contains(@class, 'ui-selectonemenu-panel') and contains(@style,'display')]//ul/li";
	@ClassConfigurator.Configurable
	private static ByT valueLocatorTemplate = ByT.xpath(valueListXpath + "[normalize-space(.)=" + Quotes.escape("%s") + "]");
	@ClassConfigurator.Configurable
	private static int timeout = 1000;

	static {
		ClassConfigurator configurator = new ClassConfigurator(ClickComboBoxBroken.class);
		configurator.applyConfiguration();
	}

	public ClickComboBoxBroken(By locator) {
		super(locator, Waiters.DEFAULT);
	}

	public ClickComboBoxBroken(By locator, Waiter waitBy) {
		super(locator, waitBy);
	}

	public ClickComboBoxBroken(BaseElement<?, ?> parent, By locator) {
		super(parent, locator, Waiters.DEFAULT);
	}

	public ClickComboBoxBroken(BaseElement<?, ?> parent, By locator, Waiter waitBy) {
		super(parent, locator, waitBy);
	}

	@Override
	protected String getRawValue() {
		return new StaticElement(this, valueListOpenerLocator).getValue();
	}

	@Override
	protected void setRawValue(String value) {
		if (!getRawValue().equals(value)) {
			openValuesList();
			try {
				new Link(this, valueLocatorTemplate.format(value)).click();
				Waiters.SLEEP(timeout).go();
				WaitMeters.capture(WaitMeters.PAGE_LOAD);
				waitForPageUpdate();
				this.click();
			} catch (Exception e) {
				openValuesList();
				throw e;
			}
		}
	}

	/**
	 * Get all values (i.e. visible texts) from the ClickComboBox
	 *
	 * @return list of values
	 */
	@Override
	public List<String> getAllValues() {
		openValuesList();
		List<String> values = BrowserController.get().driver().findElements(By.xpath(valueListXpath))
				.stream().map(we -> we.getText().trim()).filter(s -> !s.isEmpty()).collect(Collectors.toList());
		openValuesList();
		return values;
	}

	/**
	 * Open options list popup. Second click collapse popup.
	 */
	private void openValuesList() {
		new Link(this, valueListOpenerLocator).click(Waiters.DEFAULT.then(Waiters.SLEEP(timeout)));
	}
}
