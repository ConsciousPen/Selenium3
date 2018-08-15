package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import aaa.common.pages.Page;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiter;

public class InquiryAssetList extends AssetList {
	public InquiryAssetList(By locator) {
		super(locator);
	}

	public InquiryAssetList(Class<? extends MetaData> metaDataClass) {
		super(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass);
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

	public StaticElement getStaticElement(AssetDescriptor assetDesc) {
		return StaticElement.class.cast(getAsset(assetDesc.getLabel()));
	}

	/**
	 * Use this method only for getting inner asset lists, use getStaticElement for controls
	 */
	public <C extends BaseElement<?, ?>> C getAsset(AssetDescriptor<C> assetDesc) {
		return assetDesc.getControlClass().cast(getAsset(assetDesc.getLabel()));
	}
}


