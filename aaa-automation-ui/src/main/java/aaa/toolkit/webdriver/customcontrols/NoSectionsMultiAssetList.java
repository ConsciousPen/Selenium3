package aaa.toolkit.webdriver.customcontrols;

import org.openqa.selenium.By;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class NoSectionsMultiAssetList extends MultiAssetList {

	public NoSectionsMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}

	public NoSectionsMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		//ignore
	}

	@Override
	protected void selectSection(int index) {
		//ignore
	}

	@Override
	protected void setSectionValue(int index, TestData value) {
		super.setSectionValue(index, value);
	}
}
