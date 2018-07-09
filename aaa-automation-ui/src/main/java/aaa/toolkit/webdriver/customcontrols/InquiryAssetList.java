package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiter;
import toolkit.webdriver.controls.waiters.Waiters;

import java.util.Arrays;
import java.util.List;

public class InquiryAssetList extends AssetList {
	public InquiryAssetList(By locator) {
		super(locator);
	}

	public InquiryAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public InquiryAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	protected void registerAsset(String assetName, Class<? extends BaseElement<?, ?>> controlClass, Waiter waiter, Class<? extends MetaData> metaClass, boolean hasParent, By assetLocator) {
		if (AbstractContainer.class.isAssignableFrom(controlClass)) {
			super.registerAsset(assetName, InquiryAssetList.class, waiter, metaClass, hasParent, assetLocator);
		} else {
			super.registerAsset(assetName, StaticElement.class, waiter, metaClass, hasParent, assetLocator);
		}
	}

	/**
	 * To check value of fields in inquiry mode. works with all fields when value to verify <>""
	 * Calendar controls cant be checked with this method.
	 * @deprecated Use getAsset() instead
	 * @param elementName - field label
	 */
	@Deprecated
	public StaticElement getStaticElement(String elementName) {
		String xpath1 = locator.toString().replace("By.xpath: ", "") +
				String.format("//*[text()='%s']/parent::td/following-sibling::td[1]", elementName);
		String postfix = "//span[string-length(text()) > 0 and not(contains(@style,'none')) and not(ancestor::span[contains(@style,'none')])]";
		try {
			getWebElement().findElement(By.xpath(xpath1 + postfix)).getText();
		} catch (Exception e) {
			return new StaticElement(By.xpath(xpath1 + "//*"));
		}
		return new StaticElement(By.xpath(xpath1 + postfix));
	}

	/**
	 * Verifies section label is present in AssetList (form).
	 * Note: Search by text will be performed in Asset's forms only.
	 * Sensitive to {@link CustomAssert} softMode.
	 *
	 * @param sectionName - section to check (can be @id->sectionName)
	 * @throws AssertionError
	 */
	public void assetSectionPresence(String sectionName) {
		assetSectionPresence(sectionName, true);
	}

	/**
	 * Verifies is section label presence in AssetList (form).
	 * Note: Search by text will be performed in Asset's forms only.
	 * Sensitive to {@link CustomAssert} softMode.
	 *
	 * @param sectionName - section to check (can be @id->sectionName)
	 * @param isPresent   - condition in case of true - check for presence, otherwise - check for absence
	 * @throws AssertionError
	 */
	public void assetSectionPresence(String sectionName, boolean isPresent) {
		String xPath = String.format("//span[@class='componentViewPanelHeader']//label[text()='%s']", sectionName);
		String xPath2 = String.format("//span[text()='%s']", sectionName);
		String xPath3 = String.format("//span[text()=\"%s\"]", sectionName); //for ' in the text
		boolean present = isElementPresent(xPath) || isElementPresent(xPath2) || isElementPresent(xPath3);
		if (sectionName.contains("->")) {
			String[] query = sectionName.split("\\->");
			xPath = String.format("//*[@id='%1$s']", query[0]) + String.format("//label[text()='%s']", query[1]);
			present = isElementPresent(xPath) || isElementPresent(xPath2) || isElementPresent(xPath3);
		}
		CustomAssert.assertEquals(String.format("Expected %1$s element \"is present\" = %2$s, but found \"is present\" = %3$s", sectionName, isPresent, present), isPresent, present);
	}

	private static boolean isElementPresent(String xPath) {
		try {
			return BrowserController.get().driver().findElement(By.xpath(xPath)).isDisplayed();
		} catch (RuntimeException ignored) {
			return false;
		}
	}

	/**
	 * Verifies is element(field) presence in AssetList (form).
	 * Note: Search by text label will be performed in Asset's forms only.
	 * Sensitive to {@link CustomAssert} softMode.
	 * <br/>NOTE: Not works with Additional Interest assets on Vehicle tab as well as with duplicated Labels within a page
	 * <br/> Workaround for duplicate controls check use following format of the label String :
	 * <br/> formId->labelName
	 *
	 * @param label     {@link String} - text to find By (can be @id->label)
	 * @param isPresent condition, in case of true - check for presence, otherwise - check for absence
	 * @throws AssertionError
	 */
	private static final String X_PATH_1 = "//div[@id='contentWrapper']//table/tbody/tr[td[normalize-space(.)='%s'] and (not(@class) or @class!='hidden' or @class!='hidden oddRow')]";
	private static final String X_PATH_2 = "//table/tbody/tr[td[normalize-space(.)='%s'] and (not(@class) or @class!='hidden' or @class!='hidden oddRow')]";

	private String getQueryXPath(String xPath, String... params) {
		return String.format("//*[@id='%1$s']", params[0]) + String.format(xPath, params[1]);
	}

	private void assetFieldPresence(String label, boolean isPresent) {
		boolean present;
		try {
			present = getControl(label).isPresent();
		} catch (IstfException e) {
			//Term:Enable Recurring Payments
			present = isElementPresent(String.format(X_PATH_1, label));
		}
		// workaround for duplicated fields paymentMethodForm->Enable Recurring Payment
		if (label.contains("->")) {
			String[] query = label.split("\\->");
			present = isElementPresent(getQueryXPath(X_PATH_2, query[0], query[1]));
		}
		CustomAssert.assertEquals(String.format("Expected \"%1$s\" element \"is present\" = %2$s, but found \"is present\" = %3$s", label, isPresent, present), isPresent, present);
	}

	/**
	 * Verifies are elements(fields) present in AssetList (form).
	 * Note: Search by text label will be performed in Asset's forms only.
	 * Sensitive to {@link CustomAssert} softMode.
	 * <br/>NOTE: Not works with Additional Interest assets on Vehicle tab as well as with duplicated Labels within a page
	 *
	 * @param absentFields - fields labels to check
	 * @throws AssertionError
	 */
	private void assetFieldsAbsence(List<String> absentFields) {
		for (String label : absentFields) {
			assetFieldPresence(label, false);
		}
	}

	@Deprecated
	public void assetFieldsAbsence(String... presentFields) {
		assetFieldsAbsence(Arrays.asList(presentFields));
	}

	/**
	 * Verifies is Control from assetList present, enabled and mandatory.
	 * Sensitive to {@link CustomAssert} softMode.
	 *
	 * @param label       text to find By or "@id->labelName"
	 * @param isPresent   is element Present
	 * @param isEnabled   is element Enabled
	 * @param isMandatory is element Mandatory
	 * @throws AssertionError
	 */
	@Deprecated
	public void assetFieldUnionCheck(String label, boolean isPresent, boolean isEnabled, boolean isMandatory) {
		try {
			getControl(label).verify.present(isPresent);
			if (isPresent) {
				getControl(label).verify.enabled(isEnabled);
			}
		} catch (RuntimeException e) {
			if (isElementPresent(String.format(X_PATH_1, label))) {
				TextBox field = new TextBox(By.xpath(String.format(X_PATH_1, label)), Waiters.AJAX);
				if (isPresent) {
					field.verify.enabled(isEnabled);
				}
			}
		}
		if (label.contains("->")) {
			String[] query = label.split("\\->");
			TextBox field = new TextBox(By.xpath(String.format(getQueryXPath(X_PATH_2, query), query[0])), Waiters.AJAX);
			if (isPresent) {
				field.verify.enabled(isEnabled);
			}
		}
	}
}


