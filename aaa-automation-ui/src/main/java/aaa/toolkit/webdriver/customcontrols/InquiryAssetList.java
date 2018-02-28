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

public class InquiryAssetList extends AssetList{
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
	 *
	 * @param label {@link String} - text to find By (can be @id->label)
	 * @throws AssertionError in case of field absence
	 */
	public void assetFieldPresence(String label) {
		assetFieldPresence(label, true);
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

	public void assetFieldPresence(String label, boolean isPresent) {
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
	public void assetFieldsAbsence(List<String> absentFields) {
		for (String label : absentFields) {
			assetFieldPresence(label, false);
		}
	}

	public void assetFieldsAbsence(String... presentFields) {
		assetFieldsAbsence(Arrays.asList(presentFields));
	}

	/**
	 * Verifies are elements(fields) present in AssetList (form).
	 * Note: Search by text label will be performed in Asset's forms only.
	 * Sensitive to {@link CustomAssert} softMode.
	 * <br/>NOTE: Not works with Additional Interest assets on Vehicle tab as well as with duplicated Labels within a page
	 *
	 * @param presentFields - fields labels to check
	 * @throws AssertionError
	 */
	public void assetFieldsPresence(List<String> presentFields) {
		for (String label : presentFields) {
			assetFieldPresence(label, true);
		}
	}

	public void assetFieldsPresence(String... presentFields) {
		assetFieldsPresence(Arrays.asList(presentFields));
	}

	/**
	 * Verifies is Control from assetList mandatory.
	 * Sensitive to {@link CustomAssert} softMode.
	 *
	 * @param assetLabel (can be @id->assetLabel)
	 * @param -          condition   check for mandatory
	 * @throws AssertionError in case if asset field is not mandatory (has attribute "required")
	 */
	public void assetFieldMandatory(String assetLabel) {
		assetFieldMandatory(assetLabel, true);
	}

	public void assetFieldsMandatory(String... assetLabels) {
		for (String assetLabel : assetLabels) {
			assetFieldMandatory(assetLabel, true);
		}

	}

	public void assetFieldsOptional(String... assetLabels) {
		for (String assetLabel : assetLabels) {
			assetFieldMandatory(assetLabel, false);
		}
	}

	/**
	 * Verifies is Control from assetList mandatory.
	 * Sensitive to {@link CustomAssert} softMode.
	 *
	 * @param assetLabel  - assert field to be verified
	 * @param isMandatory - if true - verify that field is mandatory, if false - otherwise
	 * @throws AssertionError
	 */
	public void assetFieldMandatory(String assetLabel, boolean isMandatory) {
		assetFieldMandatory(assetLabel, isMandatory, getActualXpathValue(assetLabel, X_PATH_1, X_PATH_2, isMandatory));
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
	public void assetFieldUnionCheck(String label, boolean isPresent, boolean isEnabled, boolean isMandatory) {
		try {
			getControl(label).verify.present(isPresent);
			if (isPresent) {
				getControl(label).verify.enabled(isEnabled);
				this.assetFieldMandatory(label, isMandatory);
			}
		} catch (RuntimeException e) {
			if (isElementPresent(String.format(X_PATH_1, label))) {
				TextBox field = new TextBox(By.xpath(String.format(X_PATH_1, label)), Waiters.AJAX);
				if (isPresent) {
					field.verify.enabled(isEnabled);
					this.assetFieldMandatory(label, isMandatory);
				}
			}
		}
		if (label.contains("->")) {
			String[] query = label.split("\\->");
			TextBox field = new TextBox(By.xpath(String.format(getQueryXPath(X_PATH_2, query), query[0])), Waiters.AJAX);
			if (isPresent) {
				field.verify.enabled(isEnabled);
				this.assetFieldMandatory(label, isMandatory);
			}
		}
	}


	private void assetFieldMandatory(String assetLabel, boolean isMandatory, String actualXpath) {
		if (isMandatory) {
			CustomAssert.assertTrue(String.format("Asset field %1$s is not mandatory but should be", assetLabel), actualXpath.contains("required"));
		} else {
			CustomAssert.assertFalse(String.format("\"%1$s\" set as %2$s instead of not \"required\"", assetLabel, actualXpath), actualXpath.contains("required"));
		}
	}

	//TODO review
	private String getActualXpathValue(String assetLabel, String xPath1, String xPath2, boolean isMandatory) {
		try {
			String classValue = getAsset(assetLabel).getAttribute("class");
			if (classValue.contains("required")) {
				return classValue;
			}
			List<WebElement> parents = getAsset(assetLabel).getWebElement().findElements(By.xpath("ancestor::tr[contains(@class,'Row') and not(contains(@class,'hidden'))][1]"));
			String parentClass = !parents.isEmpty() ? parents.get(0).getAttribute("class") : "";
			if (parentClass.contains("required")) {
				log.info("Required class has been found in check #1");
				return parentClass;
			}
			String prepareXpath = getParentElementXpath(assetLabel);
			if (isWrappedElement(prepareXpath)) {
				log.info("Required class has been found in check #2");
				classValue = getAttribute(prepareXpath).trim();
			}
			if (isMandatory && classValue.isEmpty() || classValue.contains("validatedData") || !isWrappedElement(prepareXpath)) {
				log.info("!!!!!!!!!!! can't find Class attribute for " + assetLabel + ". Trying to find in parent ...");
				//TODO take a look possible refactor
				classValue = getAttribute(prepareXpath).trim();
				if (!classValue.isEmpty() && classValue.contains("required")) {
					log.info("Required class has been found in check #3");
				}
			}
			return classValue;
		} catch (RuntimeException ignored) {
			if (isElementPresent(String.format(xPath1, assetLabel))) {
				log.info("Required class was not found, I am in catch block");
				return getAttribute(String.format(xPath1, assetLabel) + "@class").trim();
			}
		}

		if (assetLabel.contains("->")) {
			String[] query = assetLabel.split("\\->");
			xPath2 = String.format("//*[@id='%1$s']", query[0]) + String.format(xPath2, query[1]);
			return getAttribute(xPath2 + "@class").trim();
		}
		return "";
	}

	private boolean isWrappedElement(String prepareXpath) {
		return getAttribute(prepareXpath).toLowerCase().trim().contains("wrapper");
	}

	private String getParentElementXpath(String assetLabel) {
		String prepareXpath = getControl(assetLabel).getLocator() + "/..@class";
		prepareXpath = prepareXpath.substring(prepareXpath.indexOf("/"));
		return prepareXpath;
	}

}


