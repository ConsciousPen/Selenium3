package aaa.toolkit.webdriver.customcontrols;

import aaa.common.pages.Page;
import org.openqa.selenium.By;

import toolkit.datax.TestData;
import toolkit.webdriver.controls.BaseElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class MultiInstanceAfterAssetList extends MultiAssetList {

	public MultiInstanceAfterAssetList(By locator, Class<? extends MetaData> metaDataClass) {
		super(locator, metaDataClass);
	}
	
	public MultiInstanceAfterAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
		super(parent, locator, metaDataClass);
	}

	@Override
	protected void addSection(int index, int size) {
		if (index > 0)
			((Button) getAssetCollection().get("Add")).click();
		if(Page.dialogConfirmation.isPresent()){
			Page.dialogConfirmation.reject();
		}
	}

	@Override
	protected void selectSection(int index) {
		log.info("");
	}

	@Override
	protected void setSectionValue(int index, TestData value) {
		selectSection(index);
		super.setSectionValue(index, value);
	}

}
