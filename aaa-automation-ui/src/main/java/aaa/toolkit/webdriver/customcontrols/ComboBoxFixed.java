package aaa.toolkit.webdriver.customcontrols;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

public class ComboBoxFixed extends ComboBox {

	public ComboBoxFixed(By locator) {
		super(locator, Waiters.DEFAULT);
	}

	public ComboBoxFixed(By locator, Waiter waiter) {
		super(locator, waiter);
	}

	public ComboBoxFixed(BaseElement<?, ?> parent, By locator) {
		super(parent, locator, Waiters.DEFAULT);
	}

	public ComboBoxFixed(BaseElement<?, ?> parent, By locator, Waiter waiter) {
		super(parent, locator, waiter);
	}

	@Override
	public void setValueContains(String partOfValue) {
		setValueByRegex(".*" + Pattern.quote(partOfValue) + ".*");
	}

	@Override
	public void setValueStarts(String partOfValue) {
		setValueByRegex("^" + Pattern.quote(partOfValue) + ".*");
	}

}
