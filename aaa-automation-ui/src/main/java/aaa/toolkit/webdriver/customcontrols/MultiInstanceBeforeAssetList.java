package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class MultiInstanceBeforeAssetList extends MultiAssetList {

	public MultiInstanceBeforeAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public MultiInstanceBeforeAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		if (getAssetCollection().containsKey("Add")) {
			getAsset("Add", Button.class).click();
		}
	}

	@Override
	protected void selectSection(int index) {
	}

	@Override
	protected void setSectionValue(int index, TestData value) {
		selectSection(index);
		super.setSectionValue(index, value);
	}
}
