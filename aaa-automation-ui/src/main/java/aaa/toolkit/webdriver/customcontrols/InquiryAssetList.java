package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiter;

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
}


