package aaa.toolkit.webdriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.BrowserController;

public class WebDriverHelper {
	protected static Logger log = LoggerFactory.getLogger(WebDriverHelper.class);

	public static String getWindowHandle() {
		return BrowserController.get().driver().getWindowHandle();
	}

	public static String getInnerText(By locator) {
		return getInnerText(locator, false);
	}

	public static String getInnerText(By locator, boolean throwException) {
		String text = null;
		try {
			WebElement webElement = BrowserController.get().driver().findElement(locator);
			text = webElement.getText();
			text = StringUtils.isEmpty(text) ? webElement.getAttribute("innerText") : text;

			if (text == null && throwException) {
				throw new IstfException("Can't get inner text for web element with locator: " + locator);
			}
		} catch (WebDriverException e) {
			if (throwException) {
				throw new IstfException("Error while getting text from web element with locator: " + locator, e);
			}
			//e.printStackTrace();
		}
		return text;
	}

	public static Integer getInnerNumber(By locator) {
		return getInnerNumber(locator, false);
	}

	public static Integer getInnerNumber(By locator, boolean throwException) {
		Integer number = null;
		String text = getInnerText(locator, throwException);
		if (NumberUtils.isCreatable(text)) {
			number = Integer.parseInt(text);
		}
		if (number == null && throwException) {
			throw new IstfException(String.format("Can't get number from text: \"%1$s\" found in web element by locator %2$s", text, locator));
		}

		return number;
	}

	public static void switchToWindow(String windowHandle) {
		BrowserController.get().driver().switchTo().window(windowHandle);
	}

	public static void switchToDefault() {
		WebDriver driver = BrowserController.get().driver();
		driver.switchTo().defaultContent();
		closeAllSecondaryWindows(driver.getWindowHandle());

	}

	public static void closeAllSecondaryWindows(String primaryHandle) {
		WebDriver driver = BrowserController.get().driver();
		Set<String> handles = driver.getWindowHandles();
		if (handles.size() > 1) {
			handles.remove(primaryHandle);
			for (String handle : handles) {
				if (!primaryHandle.equals(handle)) {
					driver.switchTo().window(handle).close();
				}
			}
		}
		driver.switchTo().window(primaryHandle);
	}

	public static List<WebElement> getControlsWithText(String text) {
		WebDriver driver = BrowserController.get().driver();
		return driver.findElements(By.xpath(String.format("//*[text()='%s' and not(self::script) and not(@class='hidden' or @class='invisible') and not(ancestor-or-self::*[contains(@style,'display')"
				+ " and contains(@style,'none')]) and not(ancestor-or-self::*[contains(@style,'visibility') and contains(@style,'hidden')])]", text)));
	}

	public static List<WebElement> getControlsContainingText(String text) {
		WebDriver driver = BrowserController.get().driver();
		return driver.findElements(By.xpath(String.format("//*[contains(text(), '%s') and not(self::script) and not(@class='hidden' or @class='invisible') and not(ancestor-or-self::*[contains(@style,'display')"
				+ " and contains(@style,'none')]) and not(ancestor-or-self::*[contains(@style,'visibility') and contains(@style,'hidden')])]", text)));
	}

	/**
	 * Gets list of open window handles and change the web driver's focus based on tab index.
	 * @param index
	 */
	public static void switchToBrowserTab(Integer index){
		Set<String> _set = BrowserController.get().driver().getWindowHandles();
		List<String> _list = new ArrayList<>(_set);
		BrowserController.get().driver().switchTo().window(_list.get(index));
	}
}
