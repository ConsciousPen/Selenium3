package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

/**
 * Created for Vehicle Information section in Documents & Bind tab for Auto Quote
 */

public class VehicleMultiInstanceBeforeAssetList extends MultiAssetList {

	public VehicleMultiInstanceBeforeAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public VehicleMultiInstanceBeforeAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		if (index > 0) {
			new Link(By.xpath(String.format("//ul[@id='vehicleSubTabs']/li[%1s]/a/span", (size-index)))).click();
		}
	}

	@Override
	protected void selectSection(int index) {

	}

}
